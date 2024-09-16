package com.example.factoryscheduling.solver;

import com.example.factoryscheduling.domain.Process;
import com.example.factoryscheduling.domain.*;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.api.score.stream.*;
import org.springframework.util.CollectionUtils;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public class FactorySchedulingConstraintProvider implements ConstraintProvider {

    @Override
    public Constraint[] defineConstraints(ConstraintFactory constraintFactory) {
        return new Constraint[] {
                machineCapacityConstraint(constraintFactory),
                machineStatusConstraint(constraintFactory),
                orderPriorityConstraint(constraintFactory),
                processSequenceConstraint(constraintFactory),
                minimizeMakespan(constraintFactory),
                preventMaintenanceMachineAssignment(constraintFactory),
                preferIdleMachines(constraintFactory),
                preventUnnecessaryMachineAssignment(constraintFactory),
                respectActualStartTimes(constraintFactory),
                parallelProcessConstraint(constraintFactory),
                avoidMaintenanceOverlap(constraintFactory)
        };
    }

    /**
     * 机器容量约束：确保每台机器的总处理时间不超过其容量
     */
    private Constraint machineCapacityConstraint(ConstraintFactory constraintFactory) {
        return constraintFactory.forEach(Process.class)
                .filter(Process::isRequiresMachine)
                .groupBy(Process::getMachine, ConstraintCollectors.sum(Process::getProcessingTime))
                .filter((machine, totalTime) -> totalTime > machine.getCapacity())
                .penalize(HardSoftScore.ONE_HARD,
                        (machine, totalTime) -> totalTime - machine.getCapacity())
                .asConstraint("Machine capacity");
    }

    /**
     * 机器状态约束：确保机器状态与当前工序状态一致
     */
    private Constraint machineStatusConstraint(ConstraintFactory constraintFactory) {
        return constraintFactory.forEach(Process.class)
                .filter(Process::isRequiresMachine)
                .join(Machine.class, Joiners.equal(Process::getMachine, machine -> machine))
                .filter((process, machine) ->
                        (process.getStatus() == ProcessStatus.PROCESSING && machine.getStatus() != MachineStatus.PROCESSING) ||
                                (process.getStatus() != ProcessStatus.PROCESSING && machine.getStatus() == MachineStatus.PROCESSING))
                .penalize(HardSoftScore.ONE_HARD)
                .asConstraint("Machine status");
    }

    /**
     * 订单优先级约束：尽量确保高优先级的订单在低优先级订单之前开始处理
     */
    private Constraint orderPriorityConstraint(ConstraintFactory constraintFactory) {
        return constraintFactory.forEachUniquePair(Process.class,
                Joiners.equal(Process::getMachine),
                Joiners.equal(p -> p, o -> o.getOrder() != null),
                        Joiners.lessThan(p -> p.getOrder().getPriority()))
                .filter((p1, p2) -> p1.getEffectiveStartTime().isAfter(p2.getEffectiveStartTime()))
                .penalize(HardSoftScore.ONE_SOFT,
                        (p1, p2) -> (int) Duration.between(p2.getEffectiveStartTime(), p1.getEffectiveStartTime()).toMinutes())
                .asConstraint("Order priority");
    }

    /**
     * 防止维护中的机器被分配新工序
     */
    private Constraint preventMaintenanceMachineAssignment(ConstraintFactory constraintFactory) {
        return constraintFactory.forEach(Process.class)
                .filter(Process::isRequiresMachine)
                .join(Machine.class,
                        Joiners.equal(Process::getMachine, machine -> machine),
                        Joiners.equal(process -> true, machine -> machine.getStatus() == MachineStatus.MAINTENANCE))
                .penalize(HardSoftScore.ONE_HARD)
                .asConstraint("Prevent maintenance machine assignment");
    }

    /**
     * 优先使用空闲状态的机器
     */
    private Constraint preferIdleMachines(ConstraintFactory constraintFactory) {
        return constraintFactory.forEach(Process.class)
                .filter(Process::isRequiresMachine)
                .join(Machine.class,
                        Joiners.equal(Process::getMachine, machine -> machine),
                        Joiners.equal(process -> true, machine -> machine.getStatus() != MachineStatus.IDLE))
                .penalize(HardSoftScore.ONE_SOFT)
                .asConstraint("Prefer idle machines");
    }

    /**
     * 防止不需要机器的工序被分配到机器上
     */
    private Constraint preventUnnecessaryMachineAssignment(ConstraintFactory constraintFactory) {
        return constraintFactory.forEach(Process.class)
                .filter(process -> !process.isRequiresMachine() && process.getMachine() != null)
                .penalize(HardSoftScore.ONE_HARD)
                .asConstraint("Prevent unnecessary machine assignment");
    }

    /**
     * 尊重已经开始的工序的实际开始时间
     */
    private Constraint respectActualStartTimes(ConstraintFactory constraintFactory) {
        return constraintFactory.forEach(Process.class)
                .filter(Process::hasStarted)
                .filter(process -> !process.getStartTime().equals(process.getActualStartTime()))
                .penalize(HardSoftScore.ONE_HARD,
                        process -> (int) Duration.between(process.getActualStartTime(), process.getStartTime()).toMinutes())
                .asConstraint("Respect actual start times");
    }

    /**
     * 避免工序与机器维护时间重叠
     */
    private Constraint avoidMaintenanceOverlap(ConstraintFactory constraintFactory) {
        return constraintFactory.forEach(Process.class)
                .join(MachineMaintenance.class,
                        Joiners.equal(Process::getMachine, MachineMaintenance::getMachine),
                        Joiners.overlapping(
                                Process::getEffectiveStartTime,
                                Process::getEndTime,
                                MachineMaintenance::getStartTime,
                                MachineMaintenance::getEndTime))
                .penalize(HardSoftScore.ONE_HARD,
                        (process, maintenance) -> {
                            long overlap = Duration.between(
                                    process.getEffectiveStartTime().isAfter(maintenance.getStartTime()) ? process.getEffectiveStartTime() : maintenance.getStartTime(),
                                    process.getEndTime().isBefore(maintenance.getEndTime()) ? process.getEndTime() : maintenance.getEndTime()
                            ).toMinutes();
                            return (int) overlap;
                        })
                .asConstraint("Avoid maintenance overlap");
    }

    /**
     * 工序顺序约束：确保非并行工序按正确的顺序执行
     */
    private Constraint processSequenceConstraint(ConstraintFactory constraintFactory) {
        return constraintFactory.forEach(Process.class)
                .filter(process -> !CollectionUtils.isEmpty(process.getLink()))
                .penalize(HardSoftScore.ONE_HARD, process -> {
                    if (CollectionUtils.isEmpty(process.getLink()) || process.getEffectiveStartTime() == null) {
                        return 0;
                    }
                    LocalDateTime end = process.getLink().stream().map(Link::getNext)
                            .map(Process::getEndTime).max(LocalDateTime::compareTo)
                            .orElse(LocalDateTime.now());
                    LocalDateTime effect =
                            process.getEffectiveStartTime();
                    long overlap = Duration.between(end, effect).toMinutes();
                    return overlap < 0 ? (int) -overlap : 0; // 只在重叠时惩罚
                })
                .asConstraint("Process sequence");
    }

    /**
     * 并行工序约束：确保并行工序的开始时间尽可能接近
     */
    private Constraint parallelProcessConstraint(ConstraintFactory constraintFactory) {
        return constraintFactory.forEach(Process.class)
                .filter(process -> !CollectionUtils.isEmpty(process.getLink()))
                .penalize(HardSoftScore.ONE_SOFT, process -> {
                    if (CollectionUtils.isEmpty(process.getLink()) || process.getEffectiveStartTime() == null) {
                        return 0;
                    }
                    LocalDateTime next = process.getLink().stream().map(Link::getNext)
                            .map(Process::getEffectiveStartTime).max(LocalDateTime::compareTo)
                            .orElse(LocalDateTime.now());
                    LocalDateTime previous = process.getEffectiveStartTime();
                    return (int) Math.abs(
                            Duration.between(previous, next)
                                    .toMinutes());
                })
                .asConstraint("Parallel process");
    }

    /**
     * 最小化制造周期：尽量减少所有订单的总完成时间
     */
    private Constraint minimizeMakespan(ConstraintFactory constraintFactory) {
        return constraintFactory.forEach(Order.class)
                .penalize(HardSoftScore.ONE_SOFT, order -> {
                    Process lastProcesses = findLastProcesses(order.getStartProcess());
                    if (lastProcesses == null || order.getPlannedStartTime() == null) {
                        return 0; // 如果没有最后的工序或时间未设置，不进行惩罚
                    }
                    LocalDateTime latestEndTime = lastProcesses.getEndTime();
                    if (latestEndTime == null) {
                        return 0;
                    }
                    return (int) Duration.between(order.getPlannedStartTime(), latestEndTime).toMinutes();
                })
                .asConstraint("Minimize makespan");
    }

    private Process findLastProcesses(Process startProcess) {
        List<Link> lastProcesses = startProcess.getLink();
        if (CollectionUtils.isEmpty(lastProcesses)) {
            return startProcess;
        }
        Link link = lastProcesses.get(0);
        return findLastProcesses(link.getNext());

    }

}

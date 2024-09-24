package com.example.factoryscheduling.solver;

import com.example.factoryscheduling.domain.Process;
import com.example.factoryscheduling.domain.*;
import lombok.extern.slf4j.Slf4j;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.api.score.stream.*;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
public class FactorySchedulingConstraintProvider implements ConstraintProvider {

    @Override
    public Constraint[] defineConstraints(ConstraintFactory constraintFactory) {
        return new Constraint[] {
                sequentialProcesses(constraintFactory)
        };
    }


    // 约束1: 顺序工序必须按顺序进行
    Constraint sequentialProcesses(ConstraintFactory factory) {
        return factory.forEach(Process.class)
                .join(Link.class, Joiners.equal(p->p, Link::getPrevious))
                .penalize(HardSoftScore.ONE_HARD,
                        (process, link) -> {
                            int duration = (int) Duration.between(process.getEndTime(), link.getNext().getStartTime())
                                    .toMinutes();
                            if (duration < 0) {
                                link.getNext().setStartTime(process.getEndTime());
                                log.info("{}->{},start:{},duration:{}, next start:{}", process.getId(),
                                        link.getNext().getId(),
                                        process.getStartTime(), process.getDuration(), link.getNext().getStartTime());
                                return 0;
                            }
                            log.info("{}->{},start:{},duration:{}, next start:{}", process.getId(),
                                    link.getNext().getId(),
                                    process.getStartTime(), process.getDuration(), link.getNext().getStartTime());
                            return duration;
                        })
                .asConstraint("Sequential processes");
    }



    // 约束2: 并行工序可以同时进行
    Constraint parallelProcesses(ConstraintFactory factory) {
        return factory.forEach(Process.class)
                .join(Link.class)
                .filter((p, l) -> !CollectionUtils.isEmpty(p.getLink()) && p.getLink().size() > 1)
                .filter((process, link) -> process.getStartTime() != null
                        && link.getNext().getStartTime() != null
                        && !process.getStartTime().equals(link.getNext().getStartTime()))
                .penalize(HardSoftScore.ONE_SOFT,
                        (process, link) -> (int) Math.abs(Duration
                                .between(process.getStartTime(), link.getNext().getStartTime()).toMinutes()))
                .asConstraint("Parallel processes");
    }

    // 约束3: 机器冲突
    Constraint machineConflict(ConstraintFactory factory) {
        return factory.forEachUniquePair(Process.class, Joiners.equal(Process::getMachine, Process::getMachine)
                .and(Joiners.overlapping(Process::getStartTime, Process::getEndTime)))
                .penalize(HardSoftScore.ONE_HARD,
                        (p1, p2) -> (int) Duration.between(
                                p1.getStartTime().isBefore(p2.getStartTime()) ? p2.getStartTime() : p1.getStartTime(),
                                p1.getEndTime().isAfter(p2.getEndTime()) ? p2.getEndTime() : p1.getEndTime())
                                .toMinutes())
                .asConstraint("Machine conflict");
    }

    // 约束4: 尊重计划开始时间
    Constraint respectPlanStartTime(ConstraintFactory factory) {
        return factory.forEach(Process.class)
                .filter(p -> p.getStartTime() != null && p.getPlanStartTime() != null)
                .penalize(HardSoftScore.ONE_SOFT,
                        p -> (int) Math.abs(Duration.between(p.getStartTime(), p.getPlanStartTime()).toMinutes()))
                .asConstraint("Respect plan start time");
    }

    // 约束6: 鼓励提前计划开始时间
    Constraint earlierPlanStartTime(ConstraintFactory factory) {
        return factory.forEach(Process.class)
                .filter(p -> p.getStartTime() != null)
                .reward(HardSoftScore.ONE_SOFT,
                        p -> {
                            LocalDateTime now = LocalDateTime.now();
                            int duration = (int) Duration.between(now, p.getPlanStartTime()).toMinutes();
                            if (duration > 0) {
                                int hours = duration / 60;
                                p.setStartTime(p.getPlanStartTime().minusHours(hours / 2));
                            }
                            return duration;
                        })
                .asConstraint("Earlier plan start time");
    }


    /**
     * 机器容量约束：确保每台机器的总处理量不超过其容量
     */
    private Constraint machineCapacityConstraint(ConstraintFactory constraintFactory) {
        return constraintFactory.forEach(Process.class)
                .filter(Process::isRequiresMachine)
                .groupBy(Process::getMachine, ConstraintCollectors.count())
                .filter((machine, total) -> total > machine.getCapacity())
                .penalize(HardSoftScore.ONE_HARD,
                        (machine, total) -> total - machine.getCapacity())
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
                (process.getStatus() == Status.PROCESSING && machine.getStatus() != MachineStatus.PROCESSING) ||
                        (process.getStatus() != Status.PROCESSING && machine.getStatus() == MachineStatus.PROCESSING))
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
                .filter((p1, p2) -> p1.getStartTime().isAfter(p2.getStartTime()))
                .penalize(HardSoftScore.ONE_SOFT,
                        (p1, p2) -> (int) Duration.between(p2.getStartTime(), p1.getStartTime()).toMinutes())
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
                .filter(process -> !process.getPlanStartTime().equals(process.getStartTime()))
                .penalize(HardSoftScore.ONE_HARD,
                        process -> (int) Duration.between(process.getStartTime(), process.getStartTime()).toMinutes())
                .asConstraint("Respect actual start times");
    }

    /**
     * 避免工序与机器维护时间重叠
     */
    // private Constraint avoidMaintenanceOverlap(ConstraintFactory constraintFactory) {
    // return constraintFactory.forEach(Process.class)
    // .join(MachineMaintenance.class,
    // Joiners.equal(Process::getMachine, MachineMaintenance::getMachine),
    // Joiners.overlapping(
    // Process::getEffectiveStartTime,
    // Process::getEndTime,
    // MachineMaintenance::getStartTime,
    // MachineMaintenance::getEndTime))
    // .penalize(HardSoftScore.ONE_HARD,
    // (process, maintenance) -> {
    // long overlap = Duration.between(
    // process.getEffectiveStartTime().isAfter(maintenance.getStartTime()) ? process.getEffectiveStartTime() :
    // maintenance.getStartTime(),
    // process.getEndTime().isBefore(maintenance.getEndTime()) ? process.getEndTime() : maintenance.getEndTime()
    // ).toMinutes();
    // return (int) overlap;
    // })
    // .asConstraint("Avoid maintenance overlap");
    // }

    /**
     * 工序顺序约束：确保非并行工序按正确的顺序执行
     */
    private Constraint processSequenceConstraint(ConstraintFactory constraintFactory) {
        return constraintFactory.forEach(Process.class)
                .join(Link.class)
                .filter(((process, link) -> process.equals(link.getPrevious())))
                .penalize(HardSoftScore.ONE_HARD, (previous, link) -> {
                    Process next = link.getNext();
                    int duration = (int) Duration.between(previous.getEndTime(), next.getStartTime()).toMinutes();
                    if (duration < 0) {
                        next.setStartTime(next.getStartTime().plusMinutes(duration));
                    }
                    duration= (int) Duration.between(previous.getEndTime(), next.getStartTime()).toMinutes();
                    return duration;
                }).asConstraint("Process sequence");
    }

    /**
     * 并行工序约束：确保并行工序的开始时间尽可能接近
     */
    private Constraint parallelProcessConstraint(ConstraintFactory constraintFactory) {
        return constraintFactory.forEach(Process.class)
                .filter(process -> !CollectionUtils.isEmpty(process.getLink()))
                .penalize(HardSoftScore.ONE_SOFT, process -> {
                    if (CollectionUtils.isEmpty(process.getLink()) || process.getStartTime() == null) {
                        return 0;
                    }
                    LocalDateTime next = process.getLink().stream().map(Link::getNext)
                            .map(Process::getStartTime).max(LocalDateTime::compareTo)
                            .orElse(LocalDateTime.now());
                    LocalDateTime previous = process.getStartTime();
                    return (int) Math.abs(
                            Duration.between(previous, next)
                                    .toMinutes());
                })
                .asConstraint("Parallel process");
    }

    /**
     * 最小化制造周期：尽量减少所有订单的总完成时间
     */
    private Constraint minimizeMakeSpan(ConstraintFactory constraintFactory) {
        return constraintFactory.forEach(Order.class)
                .penalize(HardSoftScore.ONE_SOFT, order -> {
                    Process lastProcesses = findLastProcesses(order.getStartProcess());
                    if (ObjectUtils.isEmpty(order.getStartProcess()) || ObjectUtils.isEmpty(order.getStartProcess().getStartTime())) {
                        return 0; // 如果没有最后的工序或时间未设置，不进行惩罚
                    }
                    LocalDateTime latestEndTime = lastProcesses.getEndTime();
                    if (latestEndTime == null) {
                        return 0;
                    }
                    long duration = Duration.between(order.getStartProcess().getStartTime(), latestEndTime).toMinutes();
                    if (duration < 0) {
                        return 0;
                    }
                    return Math.toIntExact(duration);
                })
                .asConstraint("Minimize makespan");
    }

    private Process findLastProcesses(Process process) {
        List<Link> nextLinks = process.getLink();
        if (!CollectionUtils.isEmpty(nextLinks) && nextLinks.size() > 0) {
            for (Link link : nextLinks) {
                return findLastProcesses(link.getNext());
            }
        }
        return process;
    }

}

package com.example.factoryscheduling.solver;

import com.example.factoryscheduling.domain.*;
import com.example.factoryscheduling.domain.Process;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.api.score.stream.*;

import java.time.Duration;

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
                        Joiners.lessThan(p -> p.getOrder().getPriority()))
                .filter((p1, p2) -> p1.getEffectiveStartTime().isAfter(p2.getEffectiveStartTime()))
                .penalize(HardSoftScore.ONE_SOFT,
                        (p1, p2) -> (int) Duration.between(p2.getEffectiveStartTime(), p1.getEffectiveStartTime()).toMinutes())
                .asConstraint("Order priority");
    }

    /**
     * 工序顺序约束：确保同一订单中的工序按正确的顺序执行
     */
    private Constraint processSequenceConstraint(ConstraintFactory constraintFactory) {
        return constraintFactory.forEach(Process.class)
                .join(Process.class,
                        Joiners.equal(Process::getOrder),
                        Joiners.lessThan(Process::getProcessNumber))
                .filter((p1, p2) -> !p1.getEndTime().isBefore(p2.getEffectiveStartTime()))
                .penalize(HardSoftScore.ONE_HARD,
                        (p1, p2) -> (int) Duration.between(p2.getEffectiveStartTime(), p1.getEndTime()).toMinutes())
                .asConstraint("Process sequence");
    }

    /**
     * 最小化制造周期
     * 尽量减少所有订单的总完成时间
     */
    private Constraint minimizeMakespan(ConstraintFactory constraintFactory) {
        return constraintFactory.forEach(Process.class)
                .reward(HardSoftScore.ONE_SOFT,
                        p -> (int) Duration.between(p.getOrder().getPlannedStartTime(), p.getEndTime()).toMinutes())
                .asConstraint("Minimize makespan");
    }
//    private Constraint minimizeMakespan(ConstraintFactory constraintFactory) {
//        return constraintFactory.forEach(Order.class)
//                .join(Process.class, Joiners.equal(order -> order, Process::getOrder))
//                .groupBy((order, process) -> order,
//                        ConstraintCollectors.max((order, process) -> process.getEndTime()))
//                .penalize(HardSoftScore.ONE_SOFT,
//                        (order, lastEndTime) -> (int) Duration.between(order.getPlannedStartTime(), lastEndTime).toMinutes())
//                .asConstraint("Minimize makespan");
//    }

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
                                process -> process.getEffectiveStartTime(),
                                process -> process.getEndTime(),
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
}

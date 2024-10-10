package com.example.factoryscheduling.solver;

import com.example.factoryscheduling.domain.*;
import lombok.extern.slf4j.Slf4j;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.api.score.stream.Constraint;
import org.optaplanner.core.api.score.stream.ConstraintFactory;
import org.optaplanner.core.api.score.stream.ConstraintProvider;
import org.optaplanner.core.api.score.stream.Joiners;
import org.springframework.hateoas.Link;
import org.springframework.util.CollectionUtils;

import java.time.Duration;
import java.util.Objects;

@Slf4j
public class FactorySchedulingConstraintProvider implements ConstraintProvider {

    @Override
    public Constraint[] defineConstraints(ConstraintFactory constraintFactory) {
        return new Constraint[] {
                        sequentialProcesses(constraintFactory),
                        machineCapacityConstraint(constraintFactory)
        };
    }


    // 约束1: 顺序工序必须按顺序进行
    Constraint sequentialProcesses(ConstraintFactory factory) {
        return factory.forEach(Timeslot.class)
                .join(factory.forEach(Timeslot.class))
                .filter((left, right) -> {
                    Order leftOrder = left.getOrder();
                    Order rightOrder = right.getOrder();
                    Procedure leftProcedure = left.getProcedure();
                    Procedure rightProcedure = right.getProcedure();
                    return Objects.equals(leftOrder.getOrderNo(), rightOrder.getOrderNo()) &&
                            (CollectionUtils.isEmpty(leftProcedure.getNextProcedureNo())
                                    && leftProcedure.getNextProcedureNo().contains(rightProcedure.getProcedureNo()));
                })
                .penalize(HardSoftScore.ONE_HARD)
                .asConstraint("Sequential processes");
    }



    // 约束2: 并行工序可以同时进行
    Constraint parallelProcesses(ConstraintFactory factory) {
        return factory.forEach(Procedure.class)
                .join(Link.class)
                .penalize(HardSoftScore.ONE_SOFT)
                .asConstraint("Parallel processes");
    }

    // 约束3: 机器冲突
    Constraint machineConflict(ConstraintFactory factory) {
        return factory.forEachUniquePair(Procedure.class)
                .penalize(HardSoftScore.ONE_HARD)
                .asConstraint("Machine conflict");
    }

    // 约束4: 尊重计划开始时间
    Constraint respectPlanStartTime(ConstraintFactory factory) {
        return factory.forEach(Procedure.class)
                .filter(p -> p.getStartTime() != null && p.getPlanStartTime() != null)
                .penalize(HardSoftScore.ONE_SOFT,
                        p -> (int) Math.abs(Duration.between(p.getStartTime(), p.getPlanStartTime()).toMinutes()))
                .asConstraint("Respect plan start time");
    }

    // 约束6: 鼓励提前计划开始时间
    Constraint earlierPlanStartTime(ConstraintFactory factory) {
        return factory.forEach(Procedure.class)
                .filter(p -> p.getStartTime() != null)
                .reward(HardSoftScore.ONE_SOFT)
                .asConstraint("Earlier plan start time");
    }


    /**
     * 机器容量约束：确保每台机器的总处理量不超过其容量
     */
    private Constraint machineCapacityConstraint(ConstraintFactory constraintFactory) {
        return constraintFactory.forEach(MachineMaintenance.class)
                .join(Timeslot.class,
                        Joiners.equal(m -> m.getMachine().getMachineNo(), timeslot -> timeslot.getProcedure().getMachineNo()),
                        Joiners.equal(m -> m.getMachine().getMachineNo(), t -> t.getMachine().getMachineNo()))
                .penalize(HardSoftScore.ONE_HARD)
                .asConstraint("Machine capacity");
    }

    /**
     * 机器状态约束：确保机器状态与当前工序状态一致
     */
    private Constraint machineStatusConstraint(ConstraintFactory constraintFactory) {
        return constraintFactory.forEach(Procedure.class)
                .penalize(HardSoftScore.ONE_HARD)
                .asConstraint("Machine status");
    }

    /**
     * 订单优先级约束：尽量确保高优先级的订单在低优先级订单之前开始处理
     */
    private Constraint orderPriorityConstraint(ConstraintFactory constraintFactory) {
        return constraintFactory.forEachUniquePair(Procedure.class)
                .penalize(HardSoftScore.ONE_SOFT,
                        (p1, p2) -> (int) Duration.between(p2.getStartTime(), p1.getStartTime()).toMinutes())
                .asConstraint("Order priority");
    }

    /**
     * 防止维护中的机器被分配新工序
     */
    private Constraint preventMaintenanceMachineAssignment(ConstraintFactory constraintFactory) {
        return constraintFactory.forEach(Procedure.class)
                .join(Machine.class)
                .penalize(HardSoftScore.ONE_HARD)
                .asConstraint("Prevent maintenance machine assignment");
    }

    /**
     * 优先使用空闲状态的机器
     */
    private Constraint preferIdleMachines(ConstraintFactory constraintFactory) {
        return constraintFactory.forEach(Procedure.class)
                .join(Machine.class)
                .penalize(HardSoftScore.ONE_SOFT)
                .asConstraint("Prefer idle machines");
    }

    /**
     * 防止不需要机器的工序被分配到机器上
     */
    private Constraint preventUnnecessaryMachineAssignment(ConstraintFactory constraintFactory) {
        return constraintFactory.forEach(Procedure.class)
                .penalize(HardSoftScore.ONE_HARD)
                .asConstraint("Prevent unnecessary machine assignment");
    }

    /**
     * 尊重已经开始的工序的实际开始时间
     */
    private Constraint respectActualStartTimes(ConstraintFactory constraintFactory) {
        return constraintFactory.forEach(Procedure.class)
                .filter(process -> !process.getPlanStartTime().equals(process.getStartTime()))
                .penalize(HardSoftScore.ONE_HARD,
                        process -> (int) Duration.between(process.getStartTime(), process.getStartTime()).toMinutes())
                .asConstraint("Respect actual start times");
    }

    /**
     * 避免工序与机器维护时间重叠
     */
    // private Constraint avoidMaintenanceOverlap(ConstraintFactory constraintFactory) {
    // return constraintFactory.forEach(Procedure.class)
    // .join(MachineMaintenance.class,
    // Joiners.equal(Procedure::getMachine, MachineMaintenance::getMachine),
    // Joiners.overlapping(
    // Procedure::getEffectiveStartTime,
    // Procedure::getEndTime,
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
        return constraintFactory.forEach(Procedure.class)
                .join(Link.class)
                .penalize(HardSoftScore.ONE_HARD).asConstraint("Procedure sequence");
    }

    /**
     * 并行工序约束：确保并行工序的开始时间尽可能接近
     */
    private Constraint parallelProcessConstraint(ConstraintFactory constraintFactory) {
        return constraintFactory.forEach(Procedure.class)
                .penalize(HardSoftScore.ONE_SOFT)
                .asConstraint("Parallel process");
    }

    /**
     * 最小化制造周期：尽量减少所有订单的总完成时间
     */
    private Constraint minimizeMakeSpan(ConstraintFactory constraintFactory) {
        return constraintFactory.forEach(Order.class)
                .penalize(HardSoftScore.ONE_SOFT)
                .asConstraint("Minimize makespan");
    }


}

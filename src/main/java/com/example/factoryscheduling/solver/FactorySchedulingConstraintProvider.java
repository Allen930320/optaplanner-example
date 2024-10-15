package com.example.factoryscheduling.solver;

import com.example.factoryscheduling.domain.Machine;
import com.example.factoryscheduling.domain.MachineMaintenance;
import com.example.factoryscheduling.domain.Procedure;
import com.example.factoryscheduling.domain.Timeslot;
import lombok.extern.slf4j.Slf4j;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.api.score.stream.Constraint;
import org.optaplanner.core.api.score.stream.ConstraintFactory;
import org.optaplanner.core.api.score.stream.ConstraintProvider;
import org.optaplanner.core.api.score.stream.Joiners;
import org.springframework.hateoas.Link;
import org.springframework.util.CollectionUtils;

import java.time.Duration;
import java.time.LocalDate;
import java.util.Comparator;

import static org.optaplanner.core.api.score.stream.ConstraintCollectors.min;
import static org.optaplanner.core.api.score.stream.ConstraintCollectors.sum;

@Slf4j
public class FactorySchedulingConstraintProvider implements ConstraintProvider {

    @Override
    public Constraint[] defineConstraints(ConstraintFactory constraintFactory) {
        return new Constraint[] {
                        machineConflict(constraintFactory),
                        sequentialProcesses(constraintFactory),
                        machineModelMatch(constraintFactory),
                        machineCapacityConstraint(constraintFactory),
                        earlierPlanStartTime(constraintFactory),
                        // workingWithMaintenanceConflict(constraintFactory),
                        minimizeMakeSpan(constraintFactory)
        };
    }


    // 约束1: 顺序工序必须按顺序进行
    Constraint sequentialProcesses(ConstraintFactory factory) {
        return factory.forEachUniquePair(Timeslot.class, Joiners.equal(timeslot -> timeslot.getOrder().getId()),
                Joiners.filtering((timeslot, timeslot2) -> !CollectionUtils.isEmpty(timeslot.getProcedure().getNextProcedureNo()) && timeslot.getProcedure().getNextProcedureNo().contains(timeslot2.getProcedure().getProcedureNo())),
                Joiners.filtering((timeslot, timeslot2) -> timeslot.getDateTime() != null && timeslot2.getDateTime() != null))
                .filter((timeslot, timeslot2) -> timeslot.getDateTime().isAfter(timeslot2.getDateTime()))
                .penalize(HardSoftScore.ONE_HARD,
                        ((timeslot, timeslot2) ->(int) Duration.between(timeslot2.getMaintenance().getDate().atStartOfDay(),timeslot.getMaintenance().getDate().atStartOfDay()).toDays()))
                .asConstraint("Sequential processes");
    }

     Constraint workingWithMaintenanceConflict(ConstraintFactory factory) {
     return factory.forEachUniquePair(Timeslot.class,Joiners.equal(timeslot -> timeslot.getProcedure().getId()))
     .penalize(HardSoftScore.ONE_HARD).asConstraint("严格按工作日历执行工作");
     }


    // 约束2: 并行工序可以同时进行
    Constraint parallelProcesses(ConstraintFactory factory) {
        return factory.forEach(Procedure.class)
                .join(Link.class)
                .penalize(HardSoftScore.ONE_SOFT)
                .asConstraint("Parallel processes");
    }

    // 约束3: 同天同订单同工序同机器不能同时被安排两次
    Constraint machineConflict(ConstraintFactory factory) {
        return factory.forEachUniquePair(Timeslot.class, Joiners.equal(timeslot -> timeslot.getOrder().getOrderNo()),
                Joiners.equal(timeslot -> timeslot.getProcedure().getProcedureNo()))
                .filter(((timeslot, timeslot2) -> timeslot.getMaintenance().getDate()
                        .equals(timeslot2.getMaintenance().getDate())))
                .penalize(HardSoftScore.ONE_HARD)
                .asConstraint("Machine conflict");
    }

    // 约束4: 尊重计划开始时间
    Constraint respectPlanStartTime(ConstraintFactory factory) {
        return factory.forEach(Timeslot.class)
                .filter(timeslot -> timeslot.getProcedure().getProcedureNo().equals(0))
                .penalize(HardSoftScore.ONE_HARD)
                .asConstraint("Respect plan start time");
    }

    // 约束6: 鼓励提前计划开始时间
    Constraint earlierPlanStartTime(ConstraintFactory factory) {
        return factory.forEach(Timeslot.class)
                .groupBy(timeslot -> timeslot.getOrder().getOrderNo() + timeslot.getProcedure().getOrderNo(),
                        min(timeslot -> timeslot,
                                Comparator.comparing(
                                        timeslot -> timeslot.getMaintenance().getDate().atStartOfDay().getMinute())))
                .reward(HardSoftScore.ONE_SOFT, (s, timeslot) -> (int) Duration.between(LocalDate.now().atStartOfDay(),
                        timeslot.getMaintenance().getDate().atStartOfDay()).toDays())
                .asConstraint("Earlier plan start time");
    }


    /**
     * 给时间片分配机器
     *
     * @param constraintFactory
     * @return
     */
    private Constraint machineModelMatch(ConstraintFactory constraintFactory) {
        return constraintFactory.forEach(Timeslot.class)
                .filter(timeslot -> !timeslot.getMachine().getMachineNo()
                        .equals(timeslot.getMaintenance().getMachine().getMachineNo()))
                .penalize(HardSoftScore.ONE_HARD)
                .asConstraint("Machine model mismatch");
    }

    /**
     * 机器容量约束：确保每台机器的总处理量不超过其容量
     */
    private Constraint machineCapacityConstraint(ConstraintFactory constraintFactory) {
        return constraintFactory.forEach(Timeslot.class)
                .groupBy(timeslot -> timeslot.getMaintenance().getId(), sum(Timeslot::getDailyHours))
                .join(MachineMaintenance.class, Joiners.equal((id, sum) -> id, MachineMaintenance::getId))
                .filter((id, total, maintenance) -> maintenance.getCapacity() < total)
                .penalize(HardSoftScore.ONE_HARD, (id, total, maintenance) -> total - maintenance.getCapacity())
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
                .penalize(HardSoftScore.ONE_SOFT)
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
                .penalize(HardSoftScore.ONE_HARD)
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
    private Constraint procedureSequenceConstraint(ConstraintFactory constraintFactory) {
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
        return constraintFactory.forEachUniquePair(Timeslot.class)
                .filter(((timeslot, timeslot2)
                        -> !timeslot.getOrder().getOrderNo().equals(timeslot2.getOrder().getOrderNo())))
                .filter(((timeslot, timeslot2)
                        -> !timeslot.getProcedure().getProcedureNo().equals(timeslot2.getProcedure().getProcedureNo())))
                .filter(((timeslot, timeslot2) -> timeslot.getMaintenance().getDate().isAfter(timeslot2.getMaintenance().getDate())))
                .penalize(HardSoftScore.ONE_SOFT,
                        (timeslot, timeslot2) -> (int) Duration.between(timeslot2.getMaintenance().getDate().atStartOfDay(),
                                timeslot.getMaintenance().getDate().atStartOfDay()).toDays())
                .asConstraint("Minimize makespan");
    }


}

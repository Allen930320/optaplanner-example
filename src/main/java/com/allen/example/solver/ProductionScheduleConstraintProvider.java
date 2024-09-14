package com.allen.example.solver;

import com.allen.example.entity.Task;
import org.optaplanner.core.api.score.buildin.hardmediumsoft.HardMediumSoftScore;
import org.optaplanner.core.api.score.stream.Constraint;
import org.optaplanner.core.api.score.stream.ConstraintFactory;
import org.optaplanner.core.api.score.stream.ConstraintProvider;

import java.time.Duration;

/**
 * 该类定义了生产调度问题的约束条件。
 * 它使用OptaPlanner的Constraint Streams API来定义评分规则。
 */
public class ProductionScheduleConstraintProvider implements ConstraintProvider {

    @Override
    public Constraint[] defineConstraints(ConstraintFactory factory) {
        return new Constraint[] {
                machineConflict(factory),
                processOrderConflict(factory),
                machineTypeRequirement(factory),
                parallelProcesses(factory),
                orderPriority(factory),
                plannedEndDate(factory),
                plannedStartDate(factory),
                machineMaintenance(factory)
        };
    }

    /**
     * 确保同一时间不会有两个任务安排在同一台机器上。
     */
    private Constraint machineConflict(ConstraintFactory factory) {
        return factory.forEach(Task.class)
                .join(Task.class)
                .filter((task1, task2) -> task1.getId() < task2.getId()
                        && task1.getMachine() != null
                        && task1.getMachine().equals(task2.getMachine())
                        && task1.getStartTime() != null
                        && task2.getStartTime() != null
                        && !task1.getEndTime().isBefore(task2.getStartTime())
                        && !task2.getEndTime().isBefore(task1.getStartTime()))
                .penalize(HardMediumSoftScore.ONE_HARD, (task1, task2) -> 1)
                .asConstraint("机器冲突");
    }

    /**
     * 确保订单内的工序按正确顺序执行。
     */
    private Constraint processOrderConflict(ConstraintFactory factory) {
        return factory.forEach(Task.class)
                .join(Task.class)
                .filter((task1, task2) -> task1.getId() < task2.getId()
                        && task1.getOrder().equals(task2.getOrder())
                        && task1.getProcess().getNextProcessNumbers().contains(task2.getProcess().getProcessNumber())
                        && task1.getStartTime() != null
                        && task2.getStartTime() != null
                        && !task1.getEndTime().isBefore(task2.getStartTime()))
                .penalize(HardMediumSoftScore.ONE_HARD, (task1, task2) -> 1)
                .asConstraint("工序顺序冲突");
    }


    /**
     * 确保任务被分配到正确类型的机器上。
     */
    private Constraint machineTypeRequirement(ConstraintFactory factory) {
        return factory.forEach(Task.class)
                .filter(task -> task.getMachine() != null
                        && task.getMachine().getType() != task.getProcess().getRequiredMachineType())
                .penalize(HardMediumSoftScore.ONE_HARD, task -> 1)
                .asConstraint("机器类型要求");
    }


    /**
     * 鼓励可能的并行工序执行。
     */
    private Constraint parallelProcesses(ConstraintFactory factory) {
        return factory.forEach(Task.class)
                .join(Task.class)
                .filter((task1, task2) -> task1.getId() < task2.getId()
                        && task1.getOrder().equals(task2.getOrder())
                        && task1.isParallelExecutionPossible(task2)
                        && task1.getStartTime() != null
                        && task2.getStartTime() != null
                        && !task1.getStartTime().equals(task2.getStartTime()))
                .reward(HardMediumSoftScore.ONE_MEDIUM, (task1, task2) -> 1)
                .asConstraint("并行工序");
    }


    /**
     * 根据订单的优先级进行排序。
     */
    private Constraint orderPriority(ConstraintFactory factory) {
        return factory.forEachUniquePair(Task.class)
                .filter((task1, task2) -> task1.getOrder().getPriority().ordinal() < task2.getOrder().getPriority().ordinal()
                        && task1.getStartTime() != null
                        && task2.getStartTime() != null
                        && task1.getStartTime().isAfter(task2.getStartTime()))
                .penalize(HardMediumSoftScore.ONE_MEDIUM, (task1, task2) -> 1)
                .asConstraint("订单优先级");
    }

    /**
     * 对超过订单计划结束日期的任务进行惩罚。
     */
    private Constraint plannedEndDate(ConstraintFactory factory) {
        return factory.forEach(Task.class)
                .filter(task -> task.getEndTime() != null && task.getEndTime().isAfter(task.getOrder().getPlannedEndTime()))
                .penalize(HardMediumSoftScore.ONE_SOFT,
                        task -> (int) Duration.between(task.getOrder().getPlannedEndTime(), task.getEndTime()).toMinutes())
                .asConstraint("计划结束日期");
    }

    /**
     * 对早于订单计划开始日期的任务进行惩罚。
     */
    private Constraint plannedStartDate(ConstraintFactory factory) {
        return factory.forEach(Task.class)
                .filter(task -> task.getStartTime() != null && task.getStartTime().isBefore(task.getOrder().getPlannedStartTime()))
                .penalize(HardMediumSoftScore.ONE_SOFT,
                        task -> (int) Duration.between(task.getStartTime(), task.getOrder().getPlannedStartTime()).toMinutes())
                .asConstraint("计划开始日期");
    }

    /**
     * 确保在机器维护期间不安排任务。
     */
    private Constraint machineMaintenance(ConstraintFactory factory) {
        return factory.forEach(Task.class)
                .filter(task -> task.getMachine() != null
                        && task.getStartTime() != null
                        && task.getMachine().needsMaintenance(task.getStartTime()))
                .penalize(HardMediumSoftScore.ONE_HARD, task -> 1)
                .asConstraint("机器维护");
    }
}

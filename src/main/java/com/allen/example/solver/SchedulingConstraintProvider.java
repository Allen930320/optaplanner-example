package com.allen.example.solver;

import com.allen.example.domain.Order;
import com.allen.example.domain.ProcessStep;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.api.score.stream.*;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * 该类定义了生产调度问题的约束条件。
 * 它使用OptaPlanner的Constraint Streams API来定义评分规则。
 */
public class SchedulingConstraintProvider implements ConstraintProvider {

    @Override
    public Constraint[] defineConstraints(ConstraintFactory factory) {
        return new Constraint[] {
                machineConflict(factory),
                processStepDependency(factory),
                meetPlannedEndTime(factory),
                orderPriority(factory),
                parallelProcessing(factory),
                minimizeMakespan(factory)
        };
    }

    private Constraint machineConflict(ConstraintFactory factory) {
        return factory.fromUniquePair(ProcessStep.class,
                        Joiners.equal(ProcessStep::getRequiredMachine),
                        Joiners.overlapping(ProcessStep::getStartTime, ProcessStep::getEndTime))
                .penalize("Machine conflict", HardSoftScore.ONE_HARD);
    }

    private Constraint processStepDependency(ConstraintFactory factory) {
        return factory.forEach(ProcessStep.class)
                .join(ProcessStep.class,
                        Joiners.equal(ProcessStep::getOrder),
                        Joiners.equal(ProcessStep::getNextStepNumber, ProcessStep::getStepNumber))
                .filter((step1, step2) -> step1.getEndTime().isAfter(step2.getStartTime()))
                .penalize("Process step dependency", HardSoftScore.ONE_HARD,
                        (step1, step2) -> (int) Duration.between(step1.getEndTime(), step2.getStartTime()).toMinutes());
    }

    private Constraint meetPlannedEndTime(ConstraintFactory factory) {
        return factory.forEach(Order.class)
                .filter(order -> order.getProcessSteps().stream()
                        .mapToInt(step -> step.getEndTime().compareTo(order.getPlannedEndTime()))
                        .max()
                        .orElse(0) > 0)
                .penalize("Exceed planned end time", HardSoftScore.ONE_HARD,
                        order -> (int) Duration.between(order.getPlannedEndTime(),
                                order.getProcessSteps().stream()
                                        .map(ProcessStep::getEndTime)
                                        .max(LocalDateTime::compareTo)
                                        .orElse(order.getPlannedEndTime())).toMinutes());
    }

    private Constraint orderPriority(ConstraintFactory factory) {
        return factory.forEach(Order.class)
                .join(Order.class)
                .filter((order1, order2) -> order1.getPriority() > order2.getPriority())
                .join(ProcessStep.class,
                        Joiners.equal((order1, order2) -> order1, ProcessStep::getOrder))
                .join(ProcessStep.class,
                        Joiners.equal((order1, order2, step1) -> order2, ProcessStep::getOrder),
                        Joiners.lessThan((order1, order2, step1, step2) -> step1.getStartTime(), ProcessStep::getStartTime))
                .penalize("Order priority", HardSoftScore.ONE_SOFT);
    }

    private Constraint parallelProcessing(ConstraintFactory factory) {
        return factory.forEach(Order.class)
                .reward("Parallel processing", HardSoftScore.ONE_SOFT,
                        order -> (int) order.getProcessSteps().stream()
                                .filter(step -> step.getNextStepNumber() == null)
                                .count());
    }

    private Constraint minimizeMakespan(ConstraintFactory factory) {
        return factory.forEach(ProcessStep.class)
                .groupBy(ProcessStep::getOrder, ConstraintCollectors.max(ProcessStep::getEndTime))
                .penalize("Minimize makespan", HardSoftScore.ONE_SOFT,
                        (order, latestEndTime) -> (int) Duration.between(order.getPlannedStartTime(), latestEndTime).toMinutes());
    }
}

package com.allen.example.domain;

import lombok.Data;
import org.optaplanner.core.api.domain.solution.PlanningEntityCollectionProperty;
import org.optaplanner.core.api.domain.solution.PlanningScore;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.domain.solution.ProblemFactCollectionProperty;
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;

import java.time.LocalDateTime;
import java.util.List;

@PlanningSolution
@Data
public class ScheduleSolution {

    @ValueRangeProvider(id = "machineRange")
    @ProblemFactCollectionProperty
    private List<Machine> machines;

    @PlanningEntityCollectionProperty
    private List<ProcessStep> processSteps;

    @ProblemFactCollectionProperty
    private List<Order> orders;

    @PlanningScore
    private HardSoftScore score;

    @ValueRangeProvider(id = "startTimeRange")
    public List<LocalDateTime> getStartTimeRange() {
        // Implement logic to generate a range of possible start times
        // This could be based on the earliest planned start time and latest planned end time of all orders
        // ...
    }
}

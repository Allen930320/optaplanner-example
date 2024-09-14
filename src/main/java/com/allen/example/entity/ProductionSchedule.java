package com.allen.example.entity;

import lombok.Data;
import org.optaplanner.core.api.domain.solution.PlanningEntityCollectionProperty;
import org.optaplanner.core.api.domain.solution.PlanningScore;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.domain.solution.ProblemFactCollectionProperty;
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import org.optaplanner.core.api.score.buildin.hardmediumsoft.HardMediumSoftScore;

import javax.persistence.CascadeType;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@PlanningSolution
@Data
public class ProductionSchedule {

    @ProblemFactCollectionProperty
    private List<Order> orders = new ArrayList<>();

    @ProblemFactCollectionProperty
    @ValueRangeProvider(id = "machineRange")
    private List<Machine> machines = new ArrayList<>();

    @PlanningEntityCollectionProperty
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Process> processes = new ArrayList<>();

    @PlanningScore
    private HardMediumSoftScore score;

}

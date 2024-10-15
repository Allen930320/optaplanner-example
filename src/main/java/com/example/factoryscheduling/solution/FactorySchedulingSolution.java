package com.example.factoryscheduling.solution;

import com.example.factoryscheduling.domain.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.optaplanner.core.api.domain.solution.PlanningEntityCollectionProperty;
import org.optaplanner.core.api.domain.solution.PlanningScore;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.domain.solution.ProblemFactCollectionProperty;
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.api.solver.SolverStatus;

import java.util.List;

@PlanningSolution
public class FactorySchedulingSolution {


    @PlanningEntityCollectionProperty
    private List<Timeslot> timeslots;

    @ValueRangeProvider(id = "maintenanceRange")
    @ProblemFactCollectionProperty
    @JsonIgnore
    private List<MachineMaintenance> maintenanceRange;


    @PlanningScore
    private HardSoftScore score;

    private SolverStatus solverStatus;;


    public FactorySchedulingSolution() {
    }

    public FactorySchedulingSolution(List<Timeslot> timeslots, List<Order> orders, List<Machine> machines,
                                     List<Procedure> procedures, List<MachineMaintenance> maintenances) {
        this.timeslots = timeslots;
        this.maintenanceRange = maintenances;
    }

    public List<Timeslot> getTimeslots() {
        return timeslots;
    }

    public void setTimeslots(List<Timeslot> timeslots) {
        this.timeslots = timeslots;
    }


    public HardSoftScore getScore() {
        return score;
    }

    public void setScore(HardSoftScore score) {
        this.score = score;
    }

    public SolverStatus getSolverStatus() {
        return solverStatus;
    }

    public void setSolverStatus(SolverStatus solverStatus) {
        this.solverStatus = solverStatus;
    }

    public List<MachineMaintenance> getMaintenanceRange() {
        return maintenanceRange;
    }

    public void setMaintenanceRange(List<MachineMaintenance> maintenanceRange) {
        this.maintenanceRange = maintenanceRange;
    }
}

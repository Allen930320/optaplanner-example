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

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@PlanningSolution
public class FactorySchedulingSolution {


    @PlanningEntityCollectionProperty
    private List<Timeslot> timeslots;

    @ProblemFactCollectionProperty
    @ValueRangeProvider(id = "orderRange")
    @JsonIgnore
    private List<Order> orders;

    @ProblemFactCollectionProperty
    @ValueRangeProvider(id = "machineRange")
    @JsonIgnore
    private List<Machine> machines;

    @ValueRangeProvider(id = "procedureRange")
    @ProblemFactCollectionProperty
    @JsonIgnore
    private List<Procedure> procedures;

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
        this.orders = orders;
        this.machines = machines;
        this.procedures = procedures;
        this.maintenanceRange = maintenances;
    }

    public List<LocalDate> getDate(){
        return this.maintenanceRange.stream().map(MachineMaintenance::getDate).distinct().collect(Collectors.toList());
    }

    public List<Timeslot> getTimeslots() {
        return timeslots;
    }

    public void setTimeslots(List<Timeslot> timeslots) {
        this.timeslots = timeslots;
    }

    public List<Order> getOrders() {
        return orders;
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }

    public List<Machine> getMachines() {
        return machines;
    }

    public void setMachines(List<Machine> machines) {
        this.machines = machines;
    }

    public List<Procedure> getProcedures() {
        return procedures;
    }

    public void setProcedures(List<Procedure> procedures) {
        this.procedures = procedures;
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

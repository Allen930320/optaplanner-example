package com.example.factoryscheduling.solution;

import com.example.factoryscheduling.domain.Machine;
import com.example.factoryscheduling.domain.MachineMaintenance;
import com.example.factoryscheduling.domain.Order;
import com.example.factoryscheduling.domain.Procedure;
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
    private List<OrderSolution> solutions;

    @ProblemFactCollectionProperty
    @ValueRangeProvider(id = "orderRange")
    private List<Order> orders;

    @ProblemFactCollectionProperty
    @ValueRangeProvider(id = "machineRange")
    private List<Machine> machines;

    @ValueRangeProvider(id = "procedureRange")
    @ProblemFactCollectionProperty
    private List<Procedure> procedures;

    @ValueRangeProvider(id = "maintenanceRangeProvider")
    @ProblemFactCollectionProperty
    private List<MachineMaintenance> maintenanceRange;

    @PlanningScore
    private HardSoftScore score;

    private SolverStatus solverStatus;;


    public FactorySchedulingSolution() {
    }

    public FactorySchedulingSolution(List<OrderSolution> solutions, List<Order> orders, List<Machine> machines, List<Procedure> procedures, List<MachineMaintenance> maintenances) {
        this.solutions = solutions;
        this.orders = orders;
        this.machines = machines;
        this.procedures = procedures;
        this.maintenanceRange = maintenances;
    }

    public List<OrderSolution> getSolutions() {
        return solutions;
    }

    public void setSolutions(List<OrderSolution> solutions) {
        this.solutions = solutions;
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

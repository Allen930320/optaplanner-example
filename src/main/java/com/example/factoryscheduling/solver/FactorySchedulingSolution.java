package com.example.factoryscheduling.solver;

import com.example.factoryscheduling.domain.Machine;
import com.example.factoryscheduling.domain.Order;
import com.example.factoryscheduling.domain.Process;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.optaplanner.core.api.domain.solution.PlanningEntityCollectionProperty;
import org.optaplanner.core.api.domain.solution.PlanningScore;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.domain.solution.ProblemFactCollectionProperty;
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.api.solver.SolverStatus;
import org.springframework.util.ObjectUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@PlanningSolution
public class FactorySchedulingSolution {

    @ProblemFactCollectionProperty
    @ValueRangeProvider(id = "machineRange")
    private List<Machine> machines;

    @ProblemFactCollectionProperty
    private List<Order> orders;

    @ValueRangeProvider(id = "processRange")
    @PlanningEntityCollectionProperty
    private List<Process> processes;

//    @ProblemFactCollectionProperty
//    private List<MachineMaintenance> maintenances;


    @PlanningScore
    private HardSoftScore score;

    private SolverStatus solverStatus;;

    @ValueRangeProvider(id = "startTimeRange")
    public List<LocalDateTime> getStartTimeRange() {
        return processes.stream().map(p -> {
            if (!ObjectUtils.isEmpty(p.getStartTime())) {
                return p.getStartTime();
            } else {
                return p.getPlanStartTime();
            }
        }).collect(Collectors.toList());
    }
    // 无参构造函数，OptaPlanner需要
    public FactorySchedulingSolution() {
    }

    // 全参数构造函数
    public FactorySchedulingSolution(List<Order> orders, List<Process> processes, List<Machine> machines) {
        this.orders = orders;
        this.processes = processes;
        this.machines = machines;
    }

    // Getter和Setter方法

    public List<Machine> getMachines() {
        return machines;
    }

    public void setMachines(List<Machine> machines) {
        this.machines = machines;
    }

    public List<Order> getOrders() {
        return orders;
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }

    @JsonIgnore
    public List<Process> getProcesses() {
        return processes;
    }

    public void setProcesses(List<Process> processes) {
        this.processes = processes;
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

}

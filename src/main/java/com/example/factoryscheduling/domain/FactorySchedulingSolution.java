package com.example.factoryscheduling.domain;

import org.optaplanner.core.api.domain.solution.PlanningEntityCollectionProperty;
import org.optaplanner.core.api.domain.solution.PlanningScore;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.domain.solution.ProblemFactCollectionProperty;
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;

import java.util.List;

@PlanningSolution
public class FactorySchedulingSolution {

    @ProblemFactCollectionProperty
    @ValueRangeProvider(id = "machineRange")
    private List<Machine> machines;

    @ProblemFactCollectionProperty
    private List<Order> orders;

    @PlanningEntityCollectionProperty
    private List<Process> processes;

    @PlanningScore
    private HardSoftScore score;

    public FactorySchedulingSolution() {
    }

    public FactorySchedulingSolution(List<Machine> machines, List<Order> orders, List<Process> processes) {
        this.machines = machines;
        this.orders = orders;
        this.processes = processes;
    }

    public FactorySchedulingSolution(List<Machine> machines, List<Order> orders, List<Process> processes, HardSoftScore score) {
        this.machines = machines;
        this.orders = orders;
        this.processes = processes;
        this.score = score;
    }

    /**
     * 获取所有机器
     * @return 机器列表
     */
    public List<Machine> getMachines() {
        return machines;
    }

    /**
     * 设置机器列表
     * @param machines 机器列表
     */
    public void setMachines(List<Machine> machines) {
        this.machines = machines;
    }

    /**
     * 获取所有订单
     * @return 订单列表
     */
    public List<Order> getOrders() {
        return orders;
    }

    /**
     * 设置订单列表
     * @param orders 订单列表
     */
    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }

    /**
     * 获取所有工序
     * @return 工序列表
     */
    public List<Process> getProcesses() {
        return processes;
    }

    /**
     * 设置工序列表
     * @param processes 工序列表
     */
    public void setProcesses(List<Process> processes) {
        this.processes = processes;
    }

    /**
     * 获取调度解决方案的得分
     * @return 硬软得分
     */
    public HardSoftScore getScore() {
        return score;
    }

    /**
     * 设置调度解决方案的得分
     * @param score 硬软得分
     */
    public void setScore(HardSoftScore score) {
        this.score = score;
    }
}

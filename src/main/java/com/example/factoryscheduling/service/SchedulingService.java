package com.example.factoryscheduling.service;

import com.example.factoryscheduling.domain.Machine;
import com.example.factoryscheduling.domain.MachineMaintenance;
import com.example.factoryscheduling.domain.Order;
import com.example.factoryscheduling.domain.Process;
import com.example.factoryscheduling.solver.FactorySchedulingSolution;
import org.optaplanner.core.api.score.ScoreExplanation;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.api.solver.SolutionManager;
import org.optaplanner.core.api.solver.SolverManager;
import org.optaplanner.core.api.solver.SolverStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
public class SchedulingService {

    private final OrderService orderService;
    private final ProcessService processService;
    private final MachineService machineService;
    private final MachineMaintenanceService maintenanceService;
    private final SolverManager<FactorySchedulingSolution, Long> solverManager;
    private final SolutionManager<FactorySchedulingSolution, HardSoftScore> solutionManager;

    @Autowired
    public SchedulingService(OrderService orderService,
            ProcessService processService,
            MachineService machineService,
            MachineMaintenanceService maintenanceService,
            SolverManager<FactorySchedulingSolution, Long> solverManager,
            SolutionManager<FactorySchedulingSolution, HardSoftScore> solutionManager) {
        this.orderService = orderService;
        this.processService = processService;
        this.machineService = machineService;
        this.maintenanceService = maintenanceService;
        this.solverManager = solverManager;
        this.solutionManager = solutionManager;
    }

    /**
     * 开始调度过程
     * @param problemId 问题ID
     */
    public void startScheduling(Long problemId) {
        solverManager.solveAndListen(problemId, this::loadProblem, this::saveSolution);
    }



    /**
     * 停止调度过程
     * @param problemId 问题ID
     */
    public void stopScheduling(Long problemId) {
        solverManager.terminateEarly(problemId);
    }

    /**
     * 获取当前最佳解决方案
     * 
     * @param problemId 问题ID
     * @return 当前最佳解决方案
     */
    public Optional<FactorySchedulingSolution> getBestSolution(Long problemId) {
        return Optional.ofNullable(solverManager.getSolverStatus(problemId))
                .filter(status -> !status.equals(SolverStatus.NOT_SOLVING))
                .map(status -> {
                    FactorySchedulingSolution solution = loadProblem(problemId);
                    solutionManager.update(solution);
                    solution.setSolverStatus(status);
                    return solution;
                });
    }

    /**
     * 获取解决方案得分
     * @param problemId 问题ID
     * @return 解决方案得分
     */
    public Optional<HardSoftScore> getScore(Long problemId) {
        return getBestSolution(problemId).map(FactorySchedulingSolution::getScore);
    }

    /**
     * 检查求解是否正在进行
     * @param problemId 问题ID
     * @return 是否正在求解
     */
    public boolean isSolving(Long problemId) {
        return solverManager.getSolverStatus(problemId).equals(SolverStatus.SOLVING_ACTIVE);
    }

    /**
     * 加载问题数据
     * @return 工厂调度问题实例
     */
    private FactorySchedulingSolution loadProblem(Long id) {
        List<Order> orders = orderService.getAllOrders();
        List<Process> processes = processService.getAllProcesses();
        List<Machine> machines = machineService.getAllMachines();
        List<MachineMaintenance> maintenances = maintenanceService.getAllMaintenances();
        return new FactorySchedulingSolution(orders, processes, machines, maintenances);
    }

    /**
     * 保存解决方案
     * @param solution 调度解决方案
     */
    @Transactional
    public void saveSolution(FactorySchedulingSolution solution) {
        orderService.updateAll(solution.getOrders());
        processService.updateAll(solution.getProcesses());
        machineService.updateAll(solution.getMachines());
    }

    public FactorySchedulingSolution getFinalBestSolution() {
        List<Order> orders = orderService.getAllOrders();
        List<Process> processes = processService.getAllProcesses();
        List<Machine> machines = machineService.getAllMachines();
        List<MachineMaintenance> maintenances = maintenanceService.getAllMaintenances();
        return new FactorySchedulingSolution(orders, processes, machines, maintenances);
    }

    /**
     * 验证解决方案
     * @param problemId 问题ID
     * @return 验证结果
     */
    public boolean isSolutionFeasible(Long problemId) {
        return getBestSolution(problemId)
                .map(solution -> solution.getScore().isFeasible())
                .orElse(false);
    }

    /**
     * 更新问题
     * 
     * @param problemId 问题ID
     * @param updatedSolution 更新后的解决方案
     */
    public void updateProblem(Long problemId, FactorySchedulingSolution updatedSolution) {
        solutionManager.update(updatedSolution);
    }

    /**
     * 获取解决方案的解释
     * 
     * @param problemId 问题ID
     * @return 解决方案的解释
     */
    public Optional<ScoreExplanation<FactorySchedulingSolution, HardSoftScore>> explainSolution(Long problemId) {
        return getBestSolution(problemId).map(solutionManager::explain);
    }
}

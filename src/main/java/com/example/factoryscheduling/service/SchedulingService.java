package com.example.factoryscheduling.service;

import com.example.factoryscheduling.domain.FactorySchedulingSolution;
import com.example.factoryscheduling.domain.Machine;
import com.example.factoryscheduling.domain.Order;
import com.example.factoryscheduling.domain.Process;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.api.solver.SolverManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class SchedulingService {

    private  OrderService orderService;
    private  ProcessService processService;
    private  MachineService machineService;
    private  SolverManager<FactorySchedulingSolution, Long> solverManager;

    private final ConcurrentHashMap<Long, FactorySchedulingSolution> bestSolutions = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Long, Boolean> solvingStatus = new ConcurrentHashMap<>();

    @Autowired
    public void setOrderService(OrderService orderService) {
        this.orderService = orderService;
    }

    @Autowired
    public void setProcessService(ProcessService processService) {
        this.processService = processService;
    }
    @Autowired
    public void setMachineService(MachineService machineService) {
        this.machineService = machineService;
    }

    @Autowired
    public void setSolverManager(SolverManager<FactorySchedulingSolution, Long> solverManager) {
        this.solverManager = solverManager;
    }

    /**
     * 开始调度过程
     * 
     * @param problemId 问题ID
     */
    public void startScheduling(Long problemId) {
        FactorySchedulingSolution problem = loadProblem();
        solvingStatus.put(problemId, true);
        solverManager.solveAndListen(
                problemId,
                id -> problem,
                bestSolution -> {
                    bestSolutions.put(problemId, bestSolution);
                    saveSolution(bestSolution);
                },
                finalBestSolution -> {
                    bestSolutions.put(problemId, finalBestSolution);
                    saveSolution(finalBestSolution);
                },
                (problemId_, exception) -> {
                    // 处理异常
                    System.err.println("Error occurred for problem " + problemId + ": " + exception.getMessage());
                    solvingStatus.put(problemId, false);
                });
    }

    /**
     * 获取当前最佳解决方案
     * 
     * @param problemId 问题ID
     * @return 当前最佳解决方案
     */
    public Optional<FactorySchedulingSolution> getBestSolution(Long problemId) {
        return Optional.ofNullable(bestSolutions.get(problemId));
    }

    /**
     * 停止调度过程
     * 
     * @param problemId 问题ID
     */
    public void stopScheduling(Long problemId) {
        solverManager.terminateEarly(problemId);
        solvingStatus.put(problemId, false);
    }

    /**
     * 获取解决方案得分
     *
     * @param problemId 问题ID
     * @return 解决方案得分
     */
    public Optional<HardSoftScore> getScore(Long problemId) {
        return getBestSolution(problemId).map(FactorySchedulingSolution::getScore);
    }

    /**
     * 检查求解是否正在进行
     * 
     * @param problemId 问题ID
     * @return 是否正在求解
     */
    public boolean isSolving(Long problemId) {
        return solvingStatus.getOrDefault(problemId, false);
    }

    /**
     * 加载问题数据
     * 
     * @return 工厂调度问题实例
     */
    private FactorySchedulingSolution loadProblem() {
        List<Order> orders = orderService.getAllOrders();
        List<Process> processes = processService.getAllProcesses();
        List<Machine> machines = machineService.getAllMachines();
        return new FactorySchedulingSolution(machines,orders, processes);
    }

    /**
     * 保存解决方案
     * 
     * @param solution 调度解决方案
     */
    @Transactional
    public void saveSolution(FactorySchedulingSolution solution) {
        for (Process process : solution.getProcesses()) {
            processService.updateProcess(process.getId(), process);
        }
    }

    /**
     * 验证解决方案
     *
     * @param problemId 问题ID
     * @return 验证结果
     */
    public boolean isSolutionFeasible(Long problemId) {
        return getBestSolution(problemId)
                .map(solution -> solution.getScore().isFeasible())
                .orElse(false);
    }
}

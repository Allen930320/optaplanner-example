package com.example.factoryscheduling.service;

import com.example.factoryscheduling.domain.Process;
import com.example.factoryscheduling.domain.*;
import com.example.factoryscheduling.solver.FactorySchedulingSolution;
import lombok.extern.slf4j.Slf4j;
import org.optaplanner.core.api.score.ScoreExplanation;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.api.solver.SolutionManager;
import org.optaplanner.core.api.solver.SolverJob;
import org.optaplanner.core.api.solver.SolverManager;
import org.optaplanner.core.api.solver.SolverStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SchedulingService {

    private OrderService orderService;
    private ProcessService processService;
    private MachineService machineService;
    private MachineMaintenanceService maintenanceService;
    private SolverManager<FactorySchedulingSolution, Long> solverManager;
    private SolutionManager<FactorySchedulingSolution, HardSoftScore> solutionManager;

    private LinkService linkService;

    @Autowired
    public void setLinkService(LinkService linkService) {
        this.linkService = linkService;
    }

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
    public void setMaintenanceService(MachineMaintenanceService maintenanceService) {
        this.maintenanceService = maintenanceService;
    }

    @Autowired
    public void setSolverManager(SolverManager<FactorySchedulingSolution, Long> solverManager) {
        this.solverManager = solverManager;
    }

    @Autowired
    public void setSolutionManager(SolutionManager<FactorySchedulingSolution, HardSoftScore> solutionManager) {
        this.solutionManager = solutionManager;
    }

    /**
     * 开始调度过程
     * @param problemId 问题ID
     */
    public void startScheduling(Long problemId) {
        FactorySchedulingSolution problem = loadProblem(problemId);
        SolverJob<FactorySchedulingSolution, Long> solverJob = solverManager.solveAndListen(
                problemId,
                id -> problem,
                solution -> {
                    // 每次找到更好的解决方案时调用
                    System.out.println("New best solution found: " + solution.getScore());
                    // 这里可以更新UI或数据库
                    saveSolution(solution);
                },
                finalBestSolution -> {
                    // 求解完成时调用
                    System.out.println("Solving finished. Best score: " + finalBestSolution);
                    saveSolution(finalBestSolution);
                    // 这里可以保存最终结果
                },
                (id, throwable) -> {
                });
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
    public FactorySchedulingSolution getBestSolution(Long problemId) {
        FactorySchedulingSolution solution = getFinalBestSolution();
        SolverStatus solverStatus = solverManager.getSolverStatus(problemId);
        solution.setSolverStatus(solverStatus);
        return solution;
    }




    /**
     * 获取解决方案得分
     * @param problemId 问题ID
     * @return 解决方案得分
     */
    public HardSoftScore getScore(Long problemId) {
        return getBestSolution(problemId).getScore();
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
        List<Process> processes = orders.stream().map(this::findAllOrderProcess).flatMap(List::stream).distinct().collect(Collectors.toList());
        List<Machine> machines = processes.stream().map(Process::getMachine).distinct().collect(Collectors.toList());
        List<Link> linkList = processes.stream().map(Process::getLink).flatMap(List::stream).distinct().collect(Collectors.toList());
        List<MachineMaintenance> maintenances = maintenanceService.getAllMaintenances();
        FactorySchedulingSolution solution = new FactorySchedulingSolution(orders, processes, machines);
        solution.setLinks(linkList);
        solution.setLinks(linkService.findAll());
        return solution;
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
        List<Process> processes =
                orders.stream().map(this::findAllOrderProcess).flatMap(List::stream).distinct().collect(Collectors.toList());
        List<Machine> machines = processes.stream().map(Process::getMachine).collect(Collectors.toList());
        List<MachineMaintenance> maintenances = maintenanceService.getAllMaintenances();
        return new FactorySchedulingSolution(orders, processes, machines);
    }

    private List<Process> findAllOrderProcess(Order order) {
        Set<Process> processes = new HashSet<>();
        Process startProcess = order.getStartProcess();
        findAllProcesses(startProcess, processes);
        return new ArrayList<>(processes);
    }


    private void findAllProcesses(Process process, Set<Process> processes) {
        processes.add(process);
        List<Link> nextLinks = process.getLink();
        if (CollectionUtils.isEmpty(nextLinks) || nextLinks.size() == 0) {
            return;
        }
        for (Link link:nextLinks){
            findAllProcesses(link.getNext(),processes);
        }
    }

    /**
     * 验证解决方案
     * @param problemId 问题ID
     * @return 验证结果
     */
    public boolean isSolutionFeasible(Long problemId) {
        return getBestSolution(problemId)
                .getScore().isFeasible();
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
    public ScoreExplanation<FactorySchedulingSolution, HardSoftScore> explainSolution(Long problemId) {
        FactorySchedulingSolution solution = getBestSolution(problemId);
        return solutionManager.explain(solution);
    }
}

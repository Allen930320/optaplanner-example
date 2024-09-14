package com.allen.example.service;

import com.allen.example.domain.Machine;
import com.allen.example.domain.Order;
import com.allen.example.domain.ProcessStep;
import com.allen.example.domain.ScheduleSolution;
import com.allen.example.persistence.MachineRepository;
import com.allen.example.persistence.OrderRepository;
import com.allen.example.persistence.ProcessStepRepository;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.api.solver.SolutionManager;
import org.optaplanner.core.api.solver.SolverJob;
import org.optaplanner.core.api.solver.SolverManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Service
public class SchedulingService {
    @Autowired
    private SolverManager<ScheduleSolution, Long> solverManager;

    @Autowired
    private SolutionManager<ScheduleSolution, HardSoftScore> solutionManager;

    @Autowired
    private MachineRepository machineRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProcessStepRepository processStepRepository;

    public CompletableFuture<ScheduleSolution> solveAsync() {
        ScheduleSolution problem = createProblem();
        Long problemId = UUID.randomUUID().node();
        return solverManager.solveAndListen(problemId,
                id -> problem,
                solution -> {
                    saveScheduleSolution(solution);
                    return solution;
                });
    }

    public ScheduleSolution getBestSolution() {
        ScheduleSolution problem = createProblem();
        return solutionManager.findBestSolution(problem);
    }

    public HardSoftScore getScore(ScheduleSolution solution) {
        return solutionManager.explain(solution).getScore();
    }

    private ScheduleSolution createProblem() {
        ScheduleSolution problem = new ScheduleSolution();
        problem.setMachines(machineRepository.findAll());

        List<Order> orders = orderRepository.findAll();
        orders.sort(Comparator.comparingInt(Order::getPriority).reversed());
        problem.setOrders(orders);

        List<ProcessStep> processSteps = processStepRepository.findAll();
        processSteps.sort(Comparator.comparing(step -> step.getOrder().getPriority(), Comparator.reverseOrder())
                .thenComparing(ProcessStep::getStepNumber));
        problem.setProcessSteps(processSteps);

        return problem;
    }

    @Transactional
    public void saveScheduleSolution(ScheduleSolution solution) {
        for (ProcessStep step : solution.getProcessSteps()) {
            ProcessStep savedStep = processStepRepository.findById(step.getId()).orElseThrow();
            savedStep.setStartTime(step.getStartTime());
            processStepRepository.save(savedStep);
            Order order = savedStep.getOrder();
            if (order.getStatus().equals("PENDING")) {
                order.setStatus("SCHEDULED");
                orderRepository.save(order);
            }
        }
    }

    @Transactional
    public ScheduleSolution solve() throws ExecutionException, InterruptedException {
        ScheduleSolution problem = new ScheduleSolution();
        problem.setMachines(machineRepository.findAll());

        List<Order> orders = orderRepository.findAll();
        orders.sort(Comparator.comparingInt(Order::getPriority).reversed());
        problem.setOrders(orders);

        List<ProcessStep> processSteps = processStepRepository.findAll();
        processSteps.sort(Comparator.comparing(step -> step.getOrder().getPriority(), Comparator.reverseOrder())
                .thenComparing(ProcessStep::getStepNumber));
        problem.setProcessSteps(processSteps);

        UUID problemId = UUID.randomUUID();
        SolverJob<ScheduleSolution, UUID> solverJob = solverManager.solve(problemId, problem);
        ScheduleSolution solution = solverJob.getFinalBestSolution();

        saveScheduleSolution(solution);

        return solution;
    }


    @Transactional
    public Order createOrder(Order order) {
        order.setStatus("PENDING");
        return orderRepository.save(order);
    }

    @Transactional
    public ProcessStep addProcessStep(Long orderId, ProcessStep step) {
        Order order = orderRepository.findById(orderId).orElseThrow();
        step.setOrder(order);
        return processStepRepository.save(step);
    }

    @Transactional
    public Order updateOrderPriority(Long orderId, int newPriority) {
        Order order = orderRepository.findById(orderId).orElseThrow();
        order.setPriority(newPriority);
        return orderRepository.save(order);
    }

    @Transactional
    public Order updateOrderStatus(Long orderId, String newStatus) {
        Order order = orderRepository.findById(orderId).orElseThrow();
        order.setStatus(newStatus);
        return orderRepository.save(order);
    }

    @Transactional
    public Machine createMachine(Machine machine) {
        return machineRepository.save(machine);
    }

    @Transactional
    public Machine updateMachineStatus(Long machineId, String newStatus) {
        Machine machine = machineRepository.findById(machineId).orElseThrow();
        machine.setStatus(newStatus);
        return machineRepository.save(machine);
    }

    public List<Order> getOrders() {
        return orderRepository.findAll();
    }

    public List<ProcessStep> getProcessSteps() {
        return processStepRepository.findAll();
    }

    public List<Machine> getMachines() {
        return machineRepository.findAll();
    }

    public Order getOrder(Long orderId) {
        return orderRepository.findById(orderId).orElseThrow();
    }

    public ProcessStep getProcessStep(Long stepId) {
        return processStepRepository.findById(stepId).orElseThrow();
    }

    public Machine getMachine(Long machineId) {
        return machineRepository.findById(machineId).orElseThrow();
    }


}

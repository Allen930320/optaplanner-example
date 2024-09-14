package com.allen.example.service;

import com.allen.example.entity.Machine;
import com.allen.example.entity.Order;
import com.allen.example.entity.ProductionSchedule;
import com.allen.example.entity.Task;
import com.allen.example.persistence.ProductionScheduleRepository;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.api.solver.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

@Service
public class ProductionSchedulingService {

    @Autowired
    private SolverManager<ProductionSchedule, Long> solverManager;
    @Autowired
    private SolutionManager<ProductionSchedule, HardSoftScore> solutionManager;
    @Autowired
    private ProductionScheduleRepository scheduleRepository;


    @Transactional
    public ProductionSchedule solveSchedule(Long scheduleId) {
        Optional<ProductionSchedule> optionalSchedule = scheduleRepository.findById(scheduleId);
        if (optionalSchedule.isEmpty()) {
            throw new IllegalArgumentException("No schedule found with id: " + scheduleId);
        }
        ProductionSchedule schedule = optionalSchedule.get();

        SolverJob<ProductionSchedule, Long> solverJob =
                solverManager.solveAndListen(scheduleId, this::findById,
                        scheduleRepository::save);
        try {
            return solverJob.getFinalBestSolution();
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Failed to solve production schedule", e);
        }
    }


    @Transactional
    public void solveSchedule(ProductionSchedule productionSchedule) {
             scheduleRepository.save(productionSchedule);
    }


    public ProductionSchedule findById(Long scheduleId) {
        return scheduleRepository.findById(scheduleId).get();
    }

    @Transactional(readOnly = true)
    public ProductionSchedule getCurrentSchedule() {
        return scheduleRepository.findById(1L)
                .orElseThrow(() -> new IllegalStateException("No current schedule exists"));
    }

    @Transactional
    public void addNewOrder(Long scheduleId, Order order) {
        ProductionSchedule currentSchedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new IllegalArgumentException("No schedule found with id: " + scheduleId));
        currentSchedule.getOrders().add(order);
        order.getProcesses().forEach(process -> {
            Task task = new Task();
            task.setOrder(order);
            task.setProcess(process);
            currentSchedule.getTasks().add(task);
        });
        scheduleRepository.save(currentSchedule);
        solverManager.solve(scheduleId, this::findById, this::saveSchedule);
    }

    @Transactional
    public void updateMachineStatus(Long scheduleId, String machineName, Machine.MachineStatus newStatus) {
        ProductionSchedule currentSchedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new IllegalArgumentException("No schedule found with id: " + scheduleId));
        currentSchedule.getMachines().stream()
                .filter(machine -> machine.getName().equals(machineName))
                .findFirst()
                .ifPresent(machine -> machine.setStatus(newStatus));
        scheduleRepository.save(currentSchedule);
        solverManager.solve(scheduleId, this::findById, this::saveSchedule);
    }

    @Transactional
    public void scheduleMachineMaintenance(Long scheduleId, String machineName, LocalDateTime maintenanceTime) {
        ProductionSchedule currentSchedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new IllegalArgumentException("No schedule found with id: " + scheduleId));
        currentSchedule.getMachines().stream()
                .filter(machine -> machine.getName().equals(machineName))
                .findFirst()
                .ifPresent(machine -> machine.setNextMaintenanceDate(maintenanceTime));
        scheduleRepository.save(currentSchedule);
        solverManager.solve(scheduleId, this::findById, this::saveSchedule);
    }

    public SolverStatus getSolverStatus(Long scheduleId) {
        return solverManager.getSolverStatus(scheduleId);
    }

    public ProductionSchedule getScoreExplanation(Long scheduleId) {
        Optional<ProductionSchedule> optionalSchedule = scheduleRepository.findById(scheduleId);
        if (optionalSchedule.isEmpty()) {
            throw new IllegalArgumentException("No schedule found with id: " + scheduleId);
        }
        ProductionSchedule schedule = optionalSchedule.get();
        return solutionManager.explain(schedule, SolutionUpdatePolicy.NO_UPDATE).getSolution();
    }

    @Transactional
    public void saveSchedule(ProductionSchedule schedule) {
        scheduleRepository.save(schedule);
    }
}

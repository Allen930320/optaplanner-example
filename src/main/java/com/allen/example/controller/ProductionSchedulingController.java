package com.allen.example.controller;

import com.allen.example.entity.Machine;
import com.allen.example.entity.Order;
import com.allen.example.entity.ProductionSchedule;
import com.allen.example.service.ProductionSchedulingService;
import org.optaplanner.core.api.solver.SolverStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/production-scheduling")
public class ProductionSchedulingController {

    @Autowired
    private ProductionSchedulingService schedulingService;


    @PostMapping("/solve")
    public ResponseEntity<Void> solveSchedule(@RequestBody ProductionSchedule problem) {
        schedulingService.solveSchedule(problem);
        return ResponseEntity.ok().build();
    }


    @PostMapping("/solve/{scheduleId}")
    public ResponseEntity<ProductionSchedule> solveSchedule(@PathVariable Long scheduleId) {
        ProductionSchedule solution = schedulingService.solveSchedule(scheduleId);
        return ResponseEntity.ok(solution);
    }

    @GetMapping("/current-schedule")
    public ResponseEntity<ProductionSchedule> getCurrentSchedule() {
        return ResponseEntity.ok(schedulingService.getCurrentSchedule());
    }

    @PostMapping("/{scheduleId}/add-order")
    public ResponseEntity<Void> addNewOrder(@PathVariable Long scheduleId, @RequestBody Order order) {
        schedulingService.addNewOrder(scheduleId, order);
        return ResponseEntity.accepted().build();
    }

    @PutMapping("/{scheduleId}/update-machine-status")
    public ResponseEntity<Void> updateMachineStatus(
            @PathVariable Long scheduleId,
            @RequestParam String machineName,
            @RequestParam Machine.MachineStatus newStatus) {
        schedulingService.updateMachineStatus(scheduleId, machineName, newStatus);
        return ResponseEntity.accepted().build();
    }

    @PostMapping("/{scheduleId}/schedule-maintenance")
    public ResponseEntity<Void> scheduleMachineMaintenance(
            @PathVariable Long scheduleId,
            @RequestParam String machineName,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime maintenanceTime) {
        schedulingService.scheduleMachineMaintenance(scheduleId, machineName, maintenanceTime);
        return ResponseEntity.accepted().build();
    }

    @GetMapping("/{scheduleId}/solver-status")
    public ResponseEntity<SolverStatus> getSolverStatus(@PathVariable Long scheduleId) {
        SolverStatus status = schedulingService.getSolverStatus(scheduleId);
        return ResponseEntity.ok(status);
    }

    @GetMapping("/{scheduleId}/score-explanation")
    public ResponseEntity<ProductionSchedule> getScoreExplanation(@PathVariable Long scheduleId) {
        ProductionSchedule schedule = schedulingService.getScoreExplanation(scheduleId);
        return ResponseEntity.ok(schedule);
    }

}

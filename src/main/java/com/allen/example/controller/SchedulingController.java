package com.allen.example.controller;

import com.allen.example.domain.Order;
import com.allen.example.domain.ProcessStep;
import com.allen.example.domain.ScheduleSolution;
import com.allen.example.service.SchedulingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/production-scheduling")
public class SchedulingController {

    @Autowired
    private SchedulingService schedulingService;

    @PostMapping("/solve")
    public ScheduleSolution solve() throws ExecutionException, InterruptedException {
        return schedulingService.solve();
    }


    @PostMapping("/orders")
    public ResponseEntity<Order> createOrder(@RequestBody Order order) {
        return ResponseEntity.ok(schedulingService.createOrder(order));
    }

    @PostMapping("/orders/{orderId}/steps")
    public ResponseEntity<ProcessStep> addProcessStep(@PathVariable Long orderId, @RequestBody ProcessStep step) {
        return ResponseEntity.ok(schedulingService.addProcessStep(orderId, step));
    }

    @GetMapping("/orders")
    public ResponseEntity<List<Order>> getOrders() {
        return ResponseEntity.ok(schedulingService.getOrders());
    }

    @GetMapping("/steps")
    public ResponseEntity<List<ProcessStep>> getProcessSteps() {
        return ResponseEntity.ok(schedulingService.getProcessSteps());
    }

}

package com.example.factoryscheduling.controller;

import com.example.factoryscheduling.domain.Order;
import com.example.factoryscheduling.domain.Process;
import com.example.factoryscheduling.service.OrderService;
import com.example.factoryscheduling.service.ProcessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private  OrderService orderService;

    private ProcessService processService;

    @Autowired
    public void setProcessService(ProcessService processService) {
        this.processService = processService;
    }

    @Autowired
    public void setOrderService(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    public List<Order> getAllOrders() {
        return orderService.getAllOrders();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrderById(@PathVariable Long id) {
        return orderService.getOrderById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Order createOrder(@RequestBody Order order) {
        return orderService.createOrder(order);
    }


    @PostMapping("/list")
    public ResponseEntity<List<Order>> createOrders(@RequestBody List<Order> orders) {
        return ResponseEntity.ok(orderService.createOrders(orders));
    }

    @PostMapping("/processes")
    public ResponseEntity<List<Process>> createProcesses(@RequestBody List<Process> processes) {
        return ResponseEntity.ok(processService.createProcesses(processes));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Order> updateOrder(@PathVariable Long id, @RequestBody Order orderDetails) {
        Order updatedOrder = orderService.updateOrder(id, orderDetails);
        return updatedOrder != null
                ? ResponseEntity.ok(updatedOrder)
                : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
        orderService.deleteOrder(id);
        return ResponseEntity.ok().build();
    }
}

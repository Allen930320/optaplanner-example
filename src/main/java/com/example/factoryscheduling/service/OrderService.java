package com.example.factoryscheduling.service;

import com.example.factoryscheduling.domain.Order;
import com.example.factoryscheduling.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class OrderService {

    private final OrderRepository orderRepository;

    @Autowired
    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public Optional<Order> getOrderById(Long id) {
        return orderRepository.findById(id);
    }

    public Order createOrder(Order order) {
        return orderRepository.save(order);
    }

    public Order updateOrder(Long id, Order orderDetails) {
        Optional<Order> order = orderRepository.findById(id);
        if (order.isPresent()) {
            Order existingOrder = order.get();
            existingOrder.setName(orderDetails.getName());
            existingOrder.setOrderNumber(orderDetails.getOrderNumber());
            existingOrder.setPlannedStartTime(orderDetails.getPlannedStartTime());
            existingOrder.setPlannedEndTime(orderDetails.getPlannedEndTime());
            existingOrder.setPriority(orderDetails.getPriority());
            existingOrder.setStatus(orderDetails.getStatus());
            existingOrder.setProcesses(orderDetails.getProcesses());
            return orderRepository.save(existingOrder);
        }
        return null;
    }

    public void deleteOrder(Long id) {
        orderRepository.deleteById(id);
    }
    public void updateAll(List<Order> orders){
        orderRepository.saveAll(orders);
    }
}

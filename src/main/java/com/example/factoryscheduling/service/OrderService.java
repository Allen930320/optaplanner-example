package com.example.factoryscheduling.service;

import com.example.factoryscheduling.domain.Order;
import com.example.factoryscheduling.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProcedureService processService;


    @Autowired
    public OrderService(OrderRepository orderRepository, ProcedureService processService) {
        this.orderRepository = orderRepository;
        this.processService = processService;
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public Optional<Order> getOrderById(Long id) {
        return orderRepository.findById(id);
    }

    @Transactional
    public Order createOrder(Order order) {
        return orderRepository.save(order);
    }
    @Transactional
    public List<Order> createOrders(List<Order> orders) {
        return orderRepository.saveAll(orders);
    }
    @Transactional
    public void deleteOrder(Long id) {
        orderRepository.deleteById(id);
    }
}

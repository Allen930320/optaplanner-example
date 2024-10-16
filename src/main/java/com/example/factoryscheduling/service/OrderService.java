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

    private OrderRepository orderRepository;

    @Autowired
    public void setOrderRepository(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public Optional<Order> getOrderById(Long id) {
        return orderRepository.findById(id);
    }

    public Order findFirstByOrderNo(String orderNo) {
        return orderRepository.findFirstByOrderNo(orderNo);
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

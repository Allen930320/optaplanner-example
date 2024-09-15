package com.example.factoryscheduling.service;

import com.example.factoryscheduling.domain.Order;
import com.example.factoryscheduling.domain.Process;
import com.example.factoryscheduling.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProcessService processService;

    @Autowired
    public OrderService(OrderRepository orderRepository, ProcessService processService) {
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
        // 保存订单
        Order savedOrder = orderRepository.save(order);

        // 如果订单有起始工序，保存并设置
        if (order.getStartProcess() != null) {
            Process startProcess = processService.createProcess(order.getStartProcess());
            savedOrder.setStartProcess(startProcess);
            savedOrder = orderRepository.save(savedOrder);
        }

        return savedOrder;
    }

    @Transactional
    public Order updateOrder(Long id, Order orderDetails) {
        return orderRepository.findById(id)
                .map(existingOrder -> {
                    existingOrder.setName(orderDetails.getName());
                    existingOrder.setOrderNumber(orderDetails.getOrderNumber());
                    existingOrder.setPlannedStartTime(orderDetails.getPlannedStartTime());
                    existingOrder.setPlannedEndTime(orderDetails.getPlannedEndTime());
                    existingOrder.setPriority(orderDetails.getPriority());
                    existingOrder.setStatus(orderDetails.getStatus());

                    // 更新起始工序
                    if (orderDetails.getStartProcess() != null) {
                        Process updatedStartProcess = processService.updateProcess(
                                existingOrder.getStartProcess().getId(),
                                orderDetails.getStartProcess()
                        );
                        existingOrder.setStartProcess(updatedStartProcess);
                    }

                    return orderRepository.save(existingOrder);
                })
                .orElseThrow(() -> new RuntimeException("Order not found with id " + id));
    }

    @Transactional
    public void deleteOrder(Long id) {
        orderRepository.deleteById(id);
    }
    public void updateAll(List<Order> orders){
        orderRepository.saveAll(orders);
    }
}

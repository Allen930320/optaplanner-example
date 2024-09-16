package com.example.factoryscheduling.service;

import com.example.factoryscheduling.domain.Link;
import com.example.factoryscheduling.domain.Order;
import com.example.factoryscheduling.domain.Process;
import com.example.factoryscheduling.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProcessService processService;
    private LinkService linkService;

    @Autowired
    public void setLinkService(LinkService linkService) {
        this.linkService = linkService;
    }

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
        Process process = order.getStartProcess();
        Process startProcess = processService.getProcessById(process.getId()).orElse(null);
        if (startProcess == null) {
            throw new RuntimeException("start can not be null");
        }
        order.setStartProcess(startProcess);
        orderRepository.save(order);
        startProcess.setOrder(order);
        updateProcess(startProcess);
        startProcess.setLink(process.getLink());
        ergodicProcess(startProcess);
        return getOrder(getOrderById(order.getId()).orElse(new Order()));
    }

    @Transactional
    public List<Order> createOrders(List<Order> orders) {
        return orders.stream().map(this::createOrder).collect(Collectors.toList());
    }

    public Order getOrder(Order order) {
        Process startProcess = order.getStartProcess();
        ignoreStartProcess(startProcess);
        return order;
    }

    public void ignoreStartProcess(Process process) {
        if (ObjectUtils.isEmpty(process) || CollectionUtils.isEmpty(process.getLink())) {
            return;
        }
        Order order = process.getOrder();
        if (!ObjectUtils.isEmpty(order)) {
            order.setStartProcess(null);
        }
        for (Link link : process.getLink()) {
            Process current = link.getCurrent();
            if (current != null && current.getOrder() != null) {
                Process copy = copyProcess(current);
                link.setCurrent(copy);
            }
            Process next = link.getNext();
            if (next != null && next.getOrder() != null) {
                Order order2 = next.getOrder();
                order2.setStartProcess(null);
            } else {
                continue;
            }
            ignoreStartProcess(next);
        }
    }

    private Process copyProcess(Process process) {
        Process copy = new Process();
        copy.setId(process.getId());
        Order order = process.getOrder();
        order.setStartProcess(null);
        copy.setOrder(order);
        copy.setName(process.getName());
        copy.setStatus(process.getStatus());
        copy.setMachine(process.getMachine());
        copy.setStartTime(process.getStartTime());
        copy.setActualStartTime(process.getActualStartTime());
        copy.setProcessingTime(process.getProcessingTime());
        copy.setRequiresMachine(process.isRequiresMachine());
        return copy;
    }


    private void updateProcess(Process process) {
        processService.update(process);
    }

    private void ergodicProcess(Process process) {
        if (CollectionUtils.isEmpty(process.getLink())) {
            return;
        }
        for (Link link : process.getLink()) {
            if (link.getCurrent() == null) {
                continue;
            }
            Long currentId = link.getCurrent().getId();
            if (Objects.equals(currentId, process.getId())) {
                link.setCurrent(process);
            }
            if (link.getNext() == null) {
                continue;
            }
            Process tempNext = link.getNext();
            Long nextId = link.getNext().getId();
            if (nextId != null) {
                Process next = processService.getProcessById(nextId).orElse(null);
                link.setNext(next);
                linkService.createProcessLink(link);
                if (next != null) {
                    next.setOrder(process.getOrder());
                    updateProcess(next);
                    next.setOrder(process.getOrder());
                    next.setLink(tempNext.getLink());
                    ergodicProcess(next);
                }
            }

        }
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

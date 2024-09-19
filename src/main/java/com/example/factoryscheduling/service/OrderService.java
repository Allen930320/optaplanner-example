package com.example.factoryscheduling.service;

import com.example.factoryscheduling.domain.Link;
import com.example.factoryscheduling.domain.Order;
import com.example.factoryscheduling.domain.Process;
import com.example.factoryscheduling.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

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
        return  order;
    }

    @Transactional
    public List<Order> createOrders(List<Order> orders) {
        return orders.stream().map(this::createOrder).collect(Collectors.toList());
    }

    private void updateProcess(Process process) {
        processService.update(process);
    }

    private void ergodicProcess(Process process) {
        if (CollectionUtils.isEmpty(process.getLink())) {
            return;
        }
        for (Link link : process.getLink()) {
            if (link.getPrevious() == null) {
                continue;
            }
            Long currentId = link.getPrevious().getId();
            if (Objects.equals(currentId, process.getId())) {
                link.setPrevious(process);
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
    public void deleteOrder(Long id) {
        orderRepository.deleteById(id);
    }
    public void updateAll(List<Order> orders){
        orderRepository.saveAll(orders);
    }
}

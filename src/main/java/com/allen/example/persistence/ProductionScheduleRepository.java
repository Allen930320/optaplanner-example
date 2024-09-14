package com.allen.example.persistence;

import com.allen.example.entity.Machine;
import com.allen.example.entity.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ProductionScheduleRepository {

    private OrderRepository orderRepository;
    private ProcessRepository processRepository;
    private MachineRepository machineRepository;

    @Autowired
    public void setOrderRepository(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }
    @Autowired
    public void setProcessRepository(ProcessRepository processRepository) {
        this.processRepository = processRepository;
    }
    @Autowired
    public void setMachineRepository(MachineRepository machineRepository) {
        this.machineRepository = machineRepository;
    }

    public ProcessHandle findAll(){
        List<Machine> machines = machineRepository.findAll();
        List<Order> orders  = orderRepository.findAll();


    }


}

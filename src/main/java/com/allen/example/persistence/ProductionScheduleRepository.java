package com.allen.example.persistence;

import com.allen.example.domain.Machine;
import com.allen.example.domain.Order;
import com.allen.example.domain.ProcessStep;
import com.allen.example.domain.ScheduleSolution;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ProductionScheduleRepository {

    private OrderRepository orderRepository;
    private ProcessStepRepository processRepository;
    private MachineRepository machineRepository;

    @Autowired
    public void setOrderRepository(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }
    @Autowired
    public void setProcessRepository(ProcessStepRepository processRepository) {
        this.processRepository = processRepository;
    }
    @Autowired
    public void setMachineRepository(MachineRepository machineRepository) {
        this.machineRepository = machineRepository;
    }

    public ScheduleSolution findAll() {
        List<Machine> machines = machineRepository.findAll();
        List<Order> orders  = orderRepository.findAll();
        List<ProcessStep> processes = processRepository.findAll();
        return new ScheduleSolution(orders, machines, processes);
    }

    public void save(ScheduleSolution productionSchedule) {
        List<Machine> machines = productionSchedule.getMachines();
        List<Order> orders = productionSchedule.getOrders();
        List<ProcessStep> processes = productionSchedule.getProcesses();
        machineRepository.saveAll(machines);
        orderRepository.saveAll(orders);
        processRepository.saveAll(processes);
    }


}

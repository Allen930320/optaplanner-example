package com.example.factoryscheduling.service;

import com.example.factoryscheduling.domain.Machine;
import com.example.factoryscheduling.domain.Order;
import com.example.factoryscheduling.domain.Procedure;
import com.example.factoryscheduling.domain.Timeslot;
import com.example.factoryscheduling.repository.ProcedureRepository;
import com.example.factoryscheduling.repository.TimeslotRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ProcedureService {

    private ProcedureRepository processRepository;

    private TimeslotRepository timeslotRepository;

    private OrderService orderService;

    private MachineService machineService;

    @Autowired
    public void setOrderService(OrderService orderService) {
        this.orderService = orderService;
    }

    @Autowired
    public void setMachineService(MachineService machineService) {
        this.machineService = machineService;
    }

    @Autowired
    public void setTimeslotRepository(TimeslotRepository timeslotRepository) {
        this.timeslotRepository = timeslotRepository;
    }

    @Autowired
    public void setProcessRepository(ProcedureRepository processRepository) {
        this.processRepository = processRepository;
    }
    @Transactional
    public List<Procedure> createProcesses(List<Procedure> procedures) {
        String[] machines = {"01", "02", "03", "04", "05"};
        Map<String, List<Procedure>> map = procedures.stream().collect(Collectors.groupingBy(Procedure::getOrderNo));
        List<Procedure> newP = new ArrayList<>();
        map.forEach((key, value) -> {
            int size = value.size();
            for (Procedure procedure : value) {
                long id = procedure.getId();
                int procedureNo = (int) (id % 10) * 10;
                procedure.setProcedureNo(Integer.toString(procedureNo));
                if (procedureNo != size * 10) {
                    String[] next = new String[] {Integer.toString(procedureNo + 10)};
                    procedure.setNextProcedureNo(Arrays.asList(next));
                }
                int index = (int) (Math.random() * 100) % 5;
                procedure.setMachineNo(machines[index]);
                procedure.setStartTime(null);
                newP.add(procedure);
            }
        });
        procedures = processRepository.saveAll(newP);
        for (Procedure procedure : procedures) {
            Order order = orderService.findFirstByOrderNo(procedure.getOrderNo());
            if (order == null) {
                throw new IllegalArgumentException("Invalid order no");
            }
            Machine machine = machineService.findFirstByMachineNo(procedure.getMachineNo());
            if (machine == null) {
                throw new IllegalArgumentException("Invalid machine no");
            }
            createTimeslot(order, procedure, machine);
        }
        return newP;
    }



    public void createTimeslot(Order order, Procedure procedure, Machine machine) {
        int duration = procedure.getDuration();
        List<Timeslot> timeslotList = new ArrayList<>();
        for (int i = 0; i < duration / 10; i++) {
            Timeslot timeslot = new Timeslot();
            timeslot.setMachine(machine);
            timeslot.setOrder(order);
            timeslot.setProcedure(procedure);
            timeslot.setDailyHours(10);
            timeslotList.add(timeslot);
        }
        timeslotRepository.saveAll(timeslotList);
    }

    public List<Procedure> findAll() {
        return processRepository.findAll();
    }

}

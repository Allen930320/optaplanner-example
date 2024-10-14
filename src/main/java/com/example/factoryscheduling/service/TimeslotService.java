package com.example.factoryscheduling.service;

import com.example.factoryscheduling.domain.*;
import com.example.factoryscheduling.repository.TimeslotRepository;
import com.example.factoryscheduling.resquest.ProcedureRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
public class TimeslotService {

    private OrderService orderService;
    private MachineService machineService;
    private MachineMaintenanceService maintenanceService;
    private ProcedureService procedureService;
    private TimeslotRepository timeslotRepository;

    @Autowired
    public TimeslotService(OrderService orderService, MachineService machineService,
            MachineMaintenanceService maintenanceService, ProcedureService procedureService,
            TimeslotRepository timeslotRepository) {
        this.orderService = orderService;
        this.machineService = machineService;
        this.maintenanceService = maintenanceService;
        this.procedureService = procedureService;
        this.timeslotRepository = timeslotRepository;
    }

    @Transactional
    public Timeslot updateTimeslot(ProcedureRequest request) {
        Order order = orderService.findFirstByOrderNo(request.getOrderNo());
        Machine machine = machineService.findFirstByMachineNo(request.getMachineNo());
        Procedure procedure = procedureService.findFirstByOrderNoAndMachineNoAndProcedureNo(request.getOrderNo(),
                request.getMachineNo(), request.getProcedureNo());
        MachineMaintenance maintenance = maintenanceService.findFirstByMachineAndDate(machine, request.getDate());
        Timeslot timeslot = timeslotRepository.findFirstByOrderAndProcedureAndMachineAndMaintenance(order, procedure,
                machine, maintenance);
        timeslot.setManual(Boolean.TRUE);
        return timeslotRepository.save(timeslot);
    }
}

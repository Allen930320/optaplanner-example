package com.example.factoryscheduling.service;

import com.example.factoryscheduling.domain.Machine;
import com.example.factoryscheduling.domain.MachineMaintenance;
import com.example.factoryscheduling.repository.MachineMaintenanceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class MachineMaintenanceService {

    private final MachineMaintenanceRepository maintenanceRepository;
    private final MachineService machineService;

    @Autowired
    public MachineMaintenanceService(MachineMaintenanceRepository maintenanceRepository, MachineService machineService) {
        this.maintenanceRepository = maintenanceRepository;
        this.machineService = machineService;
    }

    public MachineMaintenance scheduleMaintenance(Long machineId, LocalDateTime startTime, LocalDateTime endTime, String description) {
        Machine machine = machineService.getMachineById(machineId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid machine ID"));

        MachineMaintenance maintenance = new MachineMaintenance(machine, startTime, endTime, description);
        return maintenanceRepository.save(maintenance);
    }

    public void cancelMaintenance(Long maintenanceId) {
        maintenanceRepository.deleteById(maintenanceId);
    }

    public List<MachineMaintenance> getMaintenanceSchedule(Long machineId, LocalDateTime start, LocalDateTime end) {
        return maintenanceRepository.findByMachineIdAndStartTimeBetween(machineId, start, end);
    }

    public List<MachineMaintenance> getAllMaintenances() {
        return maintenanceRepository.findAll();
    }
    public void updateAll(List<MachineMaintenance> maintenances){
        maintenanceRepository.saveAll(maintenances);
    }
}

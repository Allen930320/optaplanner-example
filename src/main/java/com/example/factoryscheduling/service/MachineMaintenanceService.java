package com.example.factoryscheduling.service;

import com.example.factoryscheduling.domain.Machine;
import com.example.factoryscheduling.domain.MachineMaintenance;
import com.example.factoryscheduling.repository.MachineMaintenanceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
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

    public MachineMaintenance scheduleMaintenance(Long machineId, LocalDate date, int duration, String description) {
        Machine machine = machineService.getMachineById(machineId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid machine ID"));

        MachineMaintenance maintenance = new MachineMaintenance(machine, date, duration, description);
        return maintenanceRepository.save(maintenance);
    }

    public void cancelMaintenance(Long maintenanceId) {
        maintenanceRepository.deleteById(maintenanceId);
    }

    public List<MachineMaintenance> getMaintenanceSchedule(Long machineId, LocalDate localDate) {
        return maintenanceRepository.findByMachineIdAndDate(machineId, localDate);
    }

    public List<MachineMaintenance> getAllMaintenances() {
        return maintenanceRepository.findAll();
    }
    public void updateAll(List<MachineMaintenance> maintenances){
        maintenanceRepository.saveAll(maintenances);
    }
}

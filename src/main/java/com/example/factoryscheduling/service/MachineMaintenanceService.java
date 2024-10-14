package com.example.factoryscheduling.service;

import com.example.factoryscheduling.domain.Machine;
import com.example.factoryscheduling.domain.MachineMaintenance;
import com.example.factoryscheduling.repository.MachineMaintenanceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class MachineMaintenanceService {

    private MachineMaintenanceRepository maintenanceRepository;
    private MachineService machineService;

    @Autowired
    public void setMaintenanceRepository(MachineMaintenanceRepository maintenanceRepository) {
        this.maintenanceRepository = maintenanceRepository;
    }

    @Autowired
    public void setMachineService(MachineService machineService) {
        this.machineService = machineService;
    }

    public MachineMaintenance scheduleMaintenance(Long machineId, LocalDate date, int duration, String description) {
        Machine machine = machineService.getMachineById(machineId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid machine ID"));
        MachineMaintenance maintenance = new MachineMaintenance(machine, date, duration, description);
        return maintenanceRepository.save(maintenance);
    }

    @Transactional
    public MachineMaintenance save(MachineMaintenance maintenance) {
        return maintenanceRepository.save(maintenance);
    }

    @Transactional
    public List<MachineMaintenance> saveAll(List<MachineMaintenance> maintenances) {
        return maintenanceRepository.saveAll(maintenances);
    }

    @Transactional
    public List<MachineMaintenance> autoCreateMaintenance(Machine machine) {
        LocalDate now = LocalDate.now();
        List<MachineMaintenance> maintenances = new ArrayList<>();
        for (int i = 1; i <= 14; i++) {
            MachineMaintenance maintenance = new MachineMaintenance(machine, now.plusDays(i), 480, null);
            maintenance.setStartTime(LocalTime.of(9, 0));
            maintenance.setEndTime(maintenance.getStartTime().plusMinutes(maintenance.getCapacity()));
            maintenances.add(maintenance);
        }
        return saveAll(maintenances);
    }

    public List<MachineMaintenance> auto() {
        List<Machine> machines = machineService.getAllMachines();
        List<MachineMaintenance> maintenances = new ArrayList<>();
        for (Machine machine : machines) {
            maintenances.addAll(autoCreateMaintenance(machine));
        }
        return maintenances;
    }

    public void cancelMaintenance(Long maintenanceId) {
        maintenanceRepository.deleteById(maintenanceId);
    }

    public List<MachineMaintenance> getMaintenanceSchedule(Machine machine, LocalDate localDate) {
        return maintenanceRepository.findByMachineAndDate(machine, localDate);
    }

    public List<MachineMaintenance> getAllMaintenances() {
        return maintenanceRepository.findAll();
    }
    public void updateAll(List<MachineMaintenance> maintenances){
        maintenanceRepository.saveAll(maintenances);
    }


    public MachineMaintenance findFirstByMachineAndDate(Machine machine,LocalDate date){
        return maintenanceRepository.findFirstByMachineAndDate(machine,date);
    }
}

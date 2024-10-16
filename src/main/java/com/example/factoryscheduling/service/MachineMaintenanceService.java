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
import java.util.Objects;
import java.util.stream.Collectors;

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
        for (int i = 1; i <= 30; i++) {
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

    public List<MachineMaintenance> getAllMaintenances() {
        return maintenanceRepository.findAll();
    }

    public List<MachineMaintenance> updateAll(List<MachineMaintenance> maintenances) {
        List<MachineMaintenance> list = maintenances.stream().map(maintenance -> {
            if (maintenance.getStartTime().isAfter(maintenance.getEndTime())) {
                throw new IllegalArgumentException("开始时间不能晚于结束时间");
            }
            MachineMaintenance machineMaintenance = maintenanceRepository.findById(maintenance.getId()).orElse(null);
            if (machineMaintenance == null) {
                return null;
            }
            if (maintenance.getStatus() != null) {
                machineMaintenance.setStatus(maintenance.getStatus());
            }
            if (maintenance.getStartTime() != null) {
                machineMaintenance.setStartTime(maintenance.getStartTime());
            }
            if (maintenance.getEndTime() != null) {
                machineMaintenance.setStartTime(maintenance.getEndTime());
            }
            machineMaintenance
                    .setCapacity(maintenance.getEndTime().getMinute() - maintenance.getStartTime().getMinute());
            return machineMaintenance;
        }).filter(Objects::nonNull).collect(Collectors.toList());
        return maintenanceRepository.saveAll(list);
    }


    public MachineMaintenance findFirstByMachineAndDate(Machine machine,LocalDate date){
        return maintenanceRepository.findFirstByMachineAndDate(machine,date);
    }
}

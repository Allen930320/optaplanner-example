package com.example.factoryscheduling.repository;

import com.example.factoryscheduling.domain.Machine;
import com.example.factoryscheduling.domain.MachineMaintenance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface MachineMaintenanceRepository extends JpaRepository<MachineMaintenance, Long> {
    List<MachineMaintenance> findByMachineAndDate(Machine machine, LocalDate date);

    MachineMaintenance findFirstByMachineAndDate(Machine machine,LocalDate date);
}

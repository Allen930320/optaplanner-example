package com.example.factoryscheduling.repository;

import com.example.factoryscheduling.domain.MachineMaintenance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MachineMaintenanceRepository extends JpaRepository<MachineMaintenance, Long> {
    List<MachineMaintenance> findByMachineIdAndStartTimeBetween(Long machineId, LocalDateTime start, LocalDateTime end);

}

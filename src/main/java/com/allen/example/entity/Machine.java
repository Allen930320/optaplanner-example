package com.allen.example.entity;


import org.optaplanner.core.api.domain.lookup.PlanningId;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "machines")
public class Machine {
    @Id
    @PlanningId
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private int capacity;

    @Enumerated(EnumType.STRING)
    private MachineStatus status;

    private int maintenanceTime;

    @Enumerated(EnumType.STRING)
    private Process.MachineType type;

    private LocalDateTime nextMaintenanceDate;

    public enum MachineStatus {
        OPERATIONAL, MAINTENANCE, BROKEN
    }

    public boolean needsMaintenance(LocalDateTime currentTime) {
        return currentTime.isAfter(nextMaintenanceDate) || currentTime.isEqual(nextMaintenanceDate);
    }

    // Getters and setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public MachineStatus getStatus() {
        return status;
    }

    public void setStatus(MachineStatus status) {
        this.status = status;
    }

    public int getMaintenanceTime() {
        return maintenanceTime;
    }

    public void setMaintenanceTime(int maintenanceTime) {
        this.maintenanceTime = maintenanceTime;
    }

    public Process.MachineType getType() {
        return type;
    }

    public void setType(Process.MachineType type) {
        this.type = type;
    }

    public LocalDateTime getNextMaintenanceDate() {
        return nextMaintenanceDate;
    }

    public void setNextMaintenanceDate(LocalDateTime nextMaintenanceDate) {
        this.nextMaintenanceDate = nextMaintenanceDate;
    }
}

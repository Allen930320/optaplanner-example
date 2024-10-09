package com.example.factoryscheduling.domain;

import org.optaplanner.core.api.domain.lookup.PlanningId;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "machine_maintenances")
public class MachineMaintenance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @PlanningId
    private Long id;

    private String machineNo;

    private LocalDate date;

    private int duration;

    private int capacity;

    @Enumerated(EnumType.STRING)
    private MachineStatus status;

    private String description;

    // Constructors, getters, and setters

    public MachineMaintenance() {}

    public MachineMaintenance(String machineNo, LocalDate date, int capacity, String description) {
        this.machineNo = machineNo;
        this.date = date;
        this.capacity = capacity;
        this.description = description;
        this.status = MachineStatus.IDLE;
    }

    // Getters and setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMachineNo() {
        return machineNo;
    }

    public void setMachineNo(String machineNo) {
        this.machineNo = machineNo;
    }


    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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
}

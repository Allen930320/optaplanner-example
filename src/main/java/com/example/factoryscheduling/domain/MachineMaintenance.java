package com.example.factoryscheduling.domain;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "machine_maintenances")
public class MachineMaintenance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "machine_id", nullable = false)
    private Machine machine;

    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String description;

    // Constructors, getters, and setters

    public MachineMaintenance() {}

    public MachineMaintenance(Machine machine, LocalDateTime startTime, LocalDateTime endTime, String description) {
        this.machine = machine;
        this.startTime = startTime;
        this.endTime = endTime;
        this.description = description;
    }

    // Getters and setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Machine getMachine() {
        return machine;
    }

    public void setMachine(Machine machine) {
        this.machine = machine;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}

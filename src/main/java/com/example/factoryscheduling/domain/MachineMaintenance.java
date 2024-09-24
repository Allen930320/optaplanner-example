package com.example.factoryscheduling.domain;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "machine_maintenances")
public class MachineMaintenance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "machine_id", nullable = false)
    @org.hibernate.annotations.ForeignKey(name = "none")
    private Machine machine;

    private LocalDate date;
    //每天工作时间
    private int duration;
    private String description;

    // Constructors, getters, and setters

    public MachineMaintenance() {}

    public MachineMaintenance(Machine machine, LocalDate date, int duration, String description) {
        this.machine = machine;
        this.date = date;
        this.duration = duration;
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
}

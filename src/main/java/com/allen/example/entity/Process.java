package com.allen.example.entity;


import org.optaplanner.core.api.domain.lookup.PlanningId;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "processes")
public class Process {
    @Id
    @PlanningId
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private int processNumber;

    @ElementCollection
    private List<Integer> previousProcessNumbers = new ArrayList<>();

    @ElementCollection
    private List<Integer> nextProcessNumbers = new ArrayList<>();

    private boolean isParallel;
    private int duration;

    @Enumerated(EnumType.STRING)
    private MachineType requiredMachineType;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;

    public enum MachineType {
        TYPE_A, TYPE_B, TYPE_C, TYPE_D, TYPE_E
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

    public int getProcessNumber() {
        return processNumber;
    }

    public void setProcessNumber(int processNumber) {
        this.processNumber = processNumber;
    }

    public List<Integer> getPreviousProcessNumbers() {
        return previousProcessNumbers;
    }

    public void setPreviousProcessNumbers(List<Integer> previousProcessNumbers) {
        this.previousProcessNumbers = previousProcessNumbers;
    }

    public List<Integer> getNextProcessNumbers() {
        return nextProcessNumbers;
    }

    public void setNextProcessNumbers(List<Integer> nextProcessNumbers) {
        this.nextProcessNumbers = nextProcessNumbers;
    }

    public boolean isParallel() {
        return isParallel;
    }

    public void setParallel(boolean parallel) {
        isParallel = parallel;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public MachineType getRequiredMachineType() {
        return requiredMachineType;
    }

    public void setRequiredMachineType(MachineType requiredMachineType) {
        this.requiredMachineType = requiredMachineType;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }
}

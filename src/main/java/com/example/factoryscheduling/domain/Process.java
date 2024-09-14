package com.example.factoryscheduling.domain;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.lookup.PlanningId;
import org.optaplanner.core.api.domain.variable.PlanningVariable;

import javax.persistence.*;
import java.time.Instant;
import java.time.LocalDateTime;

@Entity
@Table(name = "processes")
@PlanningEntity
public class Process {

    @Id
    @PlanningId
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private int processNumber;
    private int nextProcessNumber;
    private int processingTime;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;

    @ManyToOne
    @PlanningVariable(valueRangeProviderRefs = "machineRange")
    @JoinColumn(name = "machine_id")
    private Machine machine;


    @Enumerated(EnumType.STRING)
    private ProcessStatus status;

    private boolean requiresMachine;

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return startTime != null ? startTime.plusMinutes(processingTime) : null;
    }

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

    public int getNextProcessNumber() {
        return nextProcessNumber;
    }

    public void setNextProcessNumber(int nextProcessNumber) {
        this.nextProcessNumber = nextProcessNumber;
    }

    public int getProcessingTime() {
        return processingTime;
    }

    public void setProcessingTime(int processingTime) {
        this.processingTime = processingTime;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public Machine getMachine() {
        return machine;
    }

    public void setMachine(Machine machine) {
        this.machine = machine;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public ProcessStatus getStatus() {
        return status;
    }

    public void setStatus(ProcessStatus status) {
        this.status = status;
    }

    public boolean isRequiresMachine() {
        return requiresMachine;
    }

    public void setRequiresMachine(boolean requiresMachine) {
        this.requiresMachine = requiresMachine;
    }
}

package com.example.factoryscheduling.domain;

import org.hibernate.annotations.GenericGenerator;
import org.optaplanner.core.api.domain.lookup.PlanningId;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Entity
public class Procedure {

    @Id
    @PlanningId
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "processId")
    @GenericGenerator(name = "processId", strategy = "com.example.factoryscheduling.domain.InsertGenerator")
    private Long id;

    private String orderNo;

    private String machineNo;

    private String name;

    private int duration;

    private String procedureNo;

    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> nextProcedureNo;

    private LocalDate startTime;

    private LocalDate planStartTime;

    @Enumerated(EnumType.STRING)
    private Status status;

    public LocalDate getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDate startTime) {
        this.startTime = startTime;
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

    public int getDuration() {
        return duration;
    }

    public void setDuration(int processingTime) {
        this.duration = processingTime;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public LocalDate getPlanStartTime() {
        return planStartTime;
    }

    public void setPlanStartTime(LocalDate planStartTime) {
        this.planStartTime = planStartTime;
    }

    public void setProcedureNo(String procedureNo) {
        this.procedureNo = procedureNo;
    }

    public List<String> getNextProcedureNo() {
        return nextProcedureNo;
    }

    public void setNextProcedureNo(List<String> nextProcedureNo) {
        this.nextProcedureNo = nextProcedureNo;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getMachineNo() {
        return machineNo;
    }

    public void setMachineNo(String machineNo) {
        this.machineNo = machineNo;
    }

    public String getProcedureNo() {
        return procedureNo;
    }

}

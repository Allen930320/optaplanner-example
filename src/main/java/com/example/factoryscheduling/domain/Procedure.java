package com.example.factoryscheduling.domain;

import org.hibernate.annotations.GenericGenerator;
import org.optaplanner.core.api.domain.lookup.PlanningId;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
public class Procedure {

    @Id
    @PlanningId
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "procedureId")
    @GenericGenerator(name = "procedureId", strategy = "com.example.factoryscheduling.domain.InsertGenerator")
    private Long id;

    private String orderNo;

    private String machineNo;

    private String name;

    private int duration;

    private Integer procedureNo;

    @ElementCollection(fetch = FetchType.EAGER)
    private List<Integer> nextProcedureNo;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    @Enumerated(EnumType.STRING)
    private Status status;

    private boolean isParallel;

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

    public Integer getProcedureNo() {
        return procedureNo;
    }

    public void setProcedureNo(Integer procedureNo) {
        this.procedureNo = procedureNo;
    }

    public List<Integer> getNextProcedureNo() {
        return nextProcedureNo;
    }

    public void setNextProcedureNo(List<Integer> nextProcedureNo) {
        this.nextProcedureNo = nextProcedureNo;
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

    public boolean isParallel() {
        return isParallel;
    }

    public void setParallel(boolean parallel) {
        isParallel = parallel;
    }
}

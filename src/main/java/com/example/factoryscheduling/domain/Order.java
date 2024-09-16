package com.example.factoryscheduling.domain;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO,generator = "MyId")
    @GenericGenerator(name = "MyId",strategy = "com.example.factoryscheduling.domain.InsertGenerator")
    private Long id;

    private String name;
    private String orderNumber;
    private LocalDateTime plannedStartTime;
    private LocalDateTime plannedEndTime;
    private int priority;
    private String status;

    @OneToOne
    @JoinColumn(name = "start_process_id")
    private Process startProcess;

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

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public LocalDateTime getPlannedStartTime() {
        return plannedStartTime;
    }

    public void setPlannedStartTime(LocalDateTime plannedStartTime) {
        this.plannedStartTime = plannedStartTime;
    }

    public LocalDateTime getPlannedEndTime() {
        return plannedEndTime;
    }

    public void setPlannedEndTime(LocalDateTime plannedEndTime) {
        this.plannedEndTime = plannedEndTime;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Process getStartProcess() {
        return startProcess;
    }

    public void setStartProcess(Process startProcess) {
        this.startProcess = startProcess;
    }
}

package com.example.factoryscheduling.domain;

import org.hibernate.annotations.GenericGenerator;
import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.lookup.PlanningId;
import org.optaplanner.core.api.domain.variable.PlanningVariable;

import javax.persistence.*;

@Entity
@Table(name = "orders")
@PlanningEntity
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO,generator = "orderId")
    @GenericGenerator(name = "orderId",strategy = "com.example.factoryscheduling.domain.InsertGenerator")
    @PlanningId
    private Long id;
    private String name;
    private String orderNumber;
    private int priority;
    private Status status;

    @OneToOne
    @PlanningVariable(valueRangeProviderRefs = {"processRange"})
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

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Process getStartProcess() {
        return startProcess;
    }

    public void setStartProcess(Process startProcess) {
        this.startProcess = startProcess;
    }
}

package com.example.factoryscheduling.domain;

import org.hibernate.annotations.GenericGenerator;
import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.lookup.PlanningId;
import org.optaplanner.core.api.domain.variable.PlanningVariable;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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


    public List<LocalDateTime> getStartTimeRange() {
        return getStartDate(this.startProcess, new ArrayList<>());
    }

    private List<LocalDateTime> getStartDate(Process process, List<LocalDateTime> starts) {
        if (ObjectUtils.isEmpty(process)) {
            return starts;
        }
        LocalDateTime start = process.getStartTime();
        if (ObjectUtils.isEmpty(start)) {
            start = process.getPlanStartTime();
        }
        starts.add(start);
        if (!CollectionUtils.isEmpty(process.getLink())) {
            for (Link link : process.getLink()) {
                Process next = link.getNext();
                if (!ObjectUtils.isEmpty(next)) {
                    getStartDate(next, starts);
                }
            }
        }
        return starts;
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

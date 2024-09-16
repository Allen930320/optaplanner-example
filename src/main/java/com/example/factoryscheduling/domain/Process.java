package com.example.factoryscheduling.domain;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.lookup.PlanningId;
import org.optaplanner.core.api.domain.variable.PlanningVariable;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "processes")
@PlanningEntity
public class Process {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @PlanningId
    private Long id;

    private String name;

    private int processingTime;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;

    @ManyToOne
    @PlanningVariable(valueRangeProviderRefs = "machineRange")
    @JoinColumn(name = "machine_id")
    private Machine machine;

    @PlanningVariable(valueRangeProviderRefs = "startTimeRange")
    private LocalDateTime startTime;

    private LocalDateTime actualStartTime;

    @Enumerated(EnumType.STRING)
    private ProcessStatus status;

    private boolean requiresMachine;

    @OneToMany(mappedBy = "current", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<Link> link;


    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public List<Link> getLink() {
        return link;
    }

    public void setLink(List<Link> link) {
        this.link = link;
    }

    public LocalDateTime getActualStartTime() {
        return actualStartTime;
    }

    public void setActualStartTime(LocalDateTime actualStartTime) {
        this.actualStartTime = actualStartTime;
    }

    public LocalDateTime getEffectiveStartTime() {
        return actualStartTime != null ? actualStartTime : startTime;
    }

    public LocalDateTime getEndTime() {
        return getEffectiveStartTime() != null ? getEffectiveStartTime().plusMinutes(processingTime) : null;
    }

    public boolean hasStarted() {
        return actualStartTime != null;
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

package com.example.factoryscheduling.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.annotations.GenericGenerator;
import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.lookup.PlanningId;
import org.optaplanner.core.api.domain.variable.PlanningVariable;
import org.springframework.util.ObjectUtils;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "processes")
@PlanningEntity
public class Process {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "processId")
    @GenericGenerator(name = "processId", strategy = "com.example.factoryscheduling.domain.InsertGenerator")
    @PlanningId
    private Long id;

    private String name;

    private int duration;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;

    @ManyToOne
    @PlanningVariable(valueRangeProviderRefs = "machineRange")
    @JoinColumn(name = "machine_id")
    private Machine machine;

    @PlanningVariable(valueRangeProviderRefs = "startTimeRange")
    private LocalDateTime startTime;

    private LocalDateTime planStartTime;

    @Enumerated(EnumType.STRING)
    private Status status;

    private boolean requiresMachine;

    @OneToMany(mappedBy = "previous", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<Link> link;


    public LocalDateTime getStartTime() {
        if (ObjectUtils.isEmpty(this.startTime)) {
            setStartTime(getPlanStartTime());
        }
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

    public LocalDateTime getEndTime() {
        if (!ObjectUtils.isEmpty(getStartTime())) {
            return getStartTime().plusMinutes(duration);
        }
        if (!ObjectUtils.isEmpty(getPlanStartTime())) {
            return getPlanStartTime().plusMinutes(duration);
        }
        setStartTime(LocalDateTime.now());
        return getStartTime().plusMinutes(duration);
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

    @JsonIgnore
    public Order getOrder() {
        return order;
    }

    @JsonProperty
    public void setOrder(Order order) {
        this.order = order;
    }

    public Machine getMachine() {
        return machine;
    }

    public void setMachine(Machine machine) {
        this.machine = machine;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public boolean isRequiresMachine() {
        return requiresMachine;
    }

    public void setRequiresMachine(boolean requiresMachine) {
        this.requiresMachine = requiresMachine;
    }

    public LocalDateTime getPlanStartTime() {
        return planStartTime;
    }

    public void setPlanStartTime(LocalDateTime planStartTime) {
        this.planStartTime = planStartTime;
    }

    public boolean hasStarted() {
        return Objects.nonNull(getStartTime());
    }
}

package com.example.factoryscheduling.domain;

import com.example.factoryscheduling.solution.TimeslotVariableListener;
import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.lookup.PlanningId;
import org.optaplanner.core.api.domain.variable.PlanningVariable;
import org.optaplanner.core.api.domain.variable.ShadowVariable;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@PlanningEntity
public class Timeslot {

    @PlanningId
    @Id @GeneratedValue
    private Long id;

    @OneToOne(cascade = CascadeType.ALL)
    private Procedure procedure;

    @OneToOne
    private Order order;

    @OneToOne
    private Machine machine;

    @OneToOne(cascade = CascadeType.ALL)
    @PlanningVariable(valueRangeProviderRefs = "maintenanceRange")
    private MachineMaintenance maintenance;

    private int dailyHours;

    @ShadowVariable(variableListenerClass = TimeslotVariableListener.class ,sourceVariableName = "maintenance")
    private LocalDateTime dateTime;

    private boolean isManual;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Procedure getProcedure() {
        return procedure;
    }

    public void setProcedure(Procedure procedure) {
        this.procedure = procedure;
    }

    public int getDailyHours() {
        return dailyHours;
    }

    public void setDailyHours(int dailyHours) {
        this.dailyHours = dailyHours;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
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

    public MachineMaintenance getMaintenance() {
        return maintenance;
    }

    public void setMaintenance(MachineMaintenance maintenance) {
        this.maintenance = maintenance;
    }

    public boolean isManual() {
        return isManual;
    }

    public void setManual(boolean manual) {
        isManual = manual;
    }
}

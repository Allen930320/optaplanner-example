package com.example.factoryscheduling.domain;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.lookup.PlanningId;
import org.optaplanner.core.api.domain.variable.PlanningVariable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import java.time.LocalDate;

@Entity
@PlanningEntity
public class Timeslot {

    @PlanningId
    @Id @GeneratedValue
    private Long id;

    @OneToOne
    @PlanningVariable(valueRangeProviderRefs = "procedureRange")
    private Procedure procedure;

    @OneToOne
    @PlanningVariable(valueRangeProviderRefs = "orderRange")
    private Order order;

    @OneToOne
    @PlanningVariable(valueRangeProviderRefs = "machineRange")
    private Machine machine;

    @OneToOne
    @PlanningVariable(valueRangeProviderRefs = "maintenanceRange")
    private MachineMaintenance maintenance;

    private int dailyHours;

    private LocalDate date;


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

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
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
}

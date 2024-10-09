package com.example.factoryscheduling.domain;

import org.optaplanner.core.api.domain.lookup.PlanningId;

import javax.persistence.*;

@Entity
@Table(name = "machines")
public class Machine {

    @Id
    @PlanningId
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String machineNo;
    private String model;


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

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getMachineNo() {
        return machineNo;
    }

    public void setMachineNo(String machineNo) {
        this.machineNo = machineNo;
    }
}

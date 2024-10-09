package com.example.factoryscheduling.solution;

import com.example.factoryscheduling.domain.Machine;
import com.example.factoryscheduling.domain.MachineMaintenance;
import com.example.factoryscheduling.domain.Order;
import com.example.factoryscheduling.domain.Procedure;
import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.lookup.PlanningId;
import org.optaplanner.core.api.domain.variable.PlanningVariable;

import javax.persistence.*;


@PlanningEntity
@Entity
public class OrderSolution {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @PlanningId
    private Long id;

    @OneToOne
    @PlanningVariable(valueRangeProviderRefs = "orderRange")
    private Order order;

    @OneToOne
    @PlanningVariable(valueRangeProviderRefs = "procedureRange")
    private Procedure procedure;

    @ManyToOne
    @PlanningVariable(valueRangeProviderRefs = "machineRange")
    private Machine machine;

    @ManyToOne
    @PlanningVariable(valueRangeProviderRefs = "maintenanceRangeProvider")
    private MachineMaintenance maintenances ;

    public OrderSolution() {
    }

    public OrderSolution(Order order, Procedure procedure, Machine machine) {
        this.order = order;
        this.procedure = procedure;
        this.machine = machine;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public Procedure getProcedure() {
        return procedure;
    }

    public void setProcedure(Procedure procedure) {
        this.procedure = procedure;
    }

    public Machine getMachine() {
        return machine;
    }

    public void setMachine(Machine machine) {
        this.machine = machine;
    }

    public MachineMaintenance getMaintenances() {
        return maintenances;
    }

    public void setMaintenances(MachineMaintenance maintenances) {
        this.maintenances = maintenances;
    }
}

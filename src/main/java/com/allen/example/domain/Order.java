package com.allen.example.domain;

import lombok.Data;
import org.optaplanner.core.api.domain.lookup.PlanningId;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
public class Order {
    @Id
    @GeneratedValue
    private Long id;
    private String name;
    private String orderNumber;
    private LocalDateTime plannedStartTime;
    private LocalDateTime plannedEndTime;
    private int priority;
    private String status;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProcessStep> processSteps;

    // Getters and setters
    // ...

    public void addProcessStep(ProcessStep step) {
        processSteps.add(step);
        step.setOrder(this);
    }

    public void removeProcessStep(ProcessStep step) {
        processSteps.remove(step);
        step.setOrder(null);
    }
}

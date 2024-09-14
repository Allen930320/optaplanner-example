package com.allen.example.domain;


import lombok.Data;
import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.PlanningVariable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.time.LocalDateTime;

@Entity
@Data
@PlanningEntity
public class ProcessStep {
    @Id
    @GeneratedValue
    private Long id;
    private String name;
    private int stepNumber;
    private Integer nextStepNumber;
    private int processingTime;

    @ManyToOne
    private Machine requiredMachine;

    @ManyToOne
    private Order order;

    @PlanningVariable(valueRangeProviderRefs = "startTimeRange")
    private LocalDateTime startTime;

    public LocalDateTime getEndTime() {
        if (startTime == null) {
            return null;
        }
        return startTime.plusMinutes(processingTime);
    }



}

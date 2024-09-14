package com.allen.example.domain;


import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Entity
@Data
public class Machine {
    @Id
    @GeneratedValue
    private Long id;
    private String name;
    private String model;
    private int capacity;
    private String status;

    @OneToMany(mappedBy = "requiredMachine")
    private List<ProcessStep> processSteps;

}

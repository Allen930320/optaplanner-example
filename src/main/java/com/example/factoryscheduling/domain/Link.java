package com.example.factoryscheduling.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.optaplanner.core.api.domain.variable.PlanningVariable;

import javax.persistence.*;

@Entity
@Table(name = "links")
public class Link {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "previous_id")
    @PlanningVariable(valueRangeProviderRefs = {"processRange"})
    private Process previous;

    @ManyToOne
    @JoinColumn(name = "next_id")
    @PlanningVariable(valueRangeProviderRefs = {"processRange"})
    private Process next;

    private boolean isParallel;



    public Link() {
    }

    public Link(Process process, boolean isParallel) {
        this.next = process;
        this.isParallel = isParallel;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Process getNext() {
        return next;
    }

    public void setNext(Process next) {
        this.next = next;
    }

    public boolean isParallel() {
        return isParallel;
    }

    public void setParallel(boolean parallel) {
        isParallel = parallel;
    }

    @JsonIgnore
    public Process getPrevious() {
        return previous;
    }

    @JsonProperty
    public void setPrevious(Process current) {
        this.previous = current;
    }
}

package com.example.factoryscheduling.domain;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Entity
@Table(name = "links")
public class Link {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO,generator = "MyId")
    @GenericGenerator(name = "MyId",strategy = "com.example.factoryscheduling.domain.InsertGenerator")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "current_id")
    private Process current;

    @ManyToOne
    @JoinColumn(name = "next_id")
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

    public Process getCurrent() {
        return current;
    }

    public void setCurrent(Process current) {
        this.current = current;
    }
}

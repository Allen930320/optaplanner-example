package com.example.factoryscheduling.domain;

import javax.persistence.*;

@Entity
@Table(name = "process_links")
public class ProcessLink {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "current_id")
    @org.hibernate.annotations.ForeignKey(name = "none")
    private Process current;

    @ManyToOne
    @JoinColumn(name = "next_id")
    @org.hibernate.annotations.ForeignKey(name = "none")
    private Process process;

    private boolean isParallel;



    public ProcessLink() {
    }

    public ProcessLink(Process process, boolean isParallel) {
        this.process = process;
        this.isParallel = isParallel;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Process getProcess() {
        return process;
    }

    public void setProcess(Process process) {
        this.process = process;
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

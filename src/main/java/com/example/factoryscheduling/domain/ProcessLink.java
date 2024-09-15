package com.example.factoryscheduling.domain;

import javax.persistence.*;

@Entity
@Table(name = "process_links")
public class ProcessLink {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "from_process_id")
    private Process fromProcess;

    @ManyToOne
    @JoinColumn(name = "to_process_id")
    private Process toProcess;

    private boolean isParallel;



    public ProcessLink() {
    }

    public ProcessLink(Process fromProcess, Process toProcess, boolean isParallel) {
        this.fromProcess = fromProcess;
        this.toProcess = toProcess;
        this.isParallel = isParallel;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Process getFromProcess() {
        return fromProcess;
    }

    public void setFromProcess(Process fromProcess) {
        this.fromProcess = fromProcess;
    }

    public Process getToProcess() {
        return toProcess;
    }

    public void setToProcess(Process toProcess) {
        this.toProcess = toProcess;
    }

    public boolean isParallel() {
        return isParallel;
    }

    public void setParallel(boolean parallel) {
        isParallel = parallel;
    }
}

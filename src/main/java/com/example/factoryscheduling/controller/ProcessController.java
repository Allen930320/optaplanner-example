package com.example.factoryscheduling.controller;

import com.example.factoryscheduling.domain.Procedure;
import com.example.factoryscheduling.service.ProcedureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/processes")
public class ProcessController {

    private ProcedureService processService;

    @Autowired
    public void setProcessService(ProcedureService processService) {
        this.processService = processService;
    }

    @PostMapping
    public ResponseEntity<List<Procedure>> createProcesses(@RequestBody List<Procedure> processes) {
        return ResponseEntity.ok(processService.createProcesses(processes));
    }
}

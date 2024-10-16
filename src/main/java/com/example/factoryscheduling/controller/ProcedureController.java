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
@RequestMapping("/api/procedure")
public class ProcedureController {

    private ProcedureService procedureService;


    @Autowired
    public void setProcessService(ProcedureService processService) {
        this.procedureService = processService;
    }

    @PostMapping
    public ResponseEntity<List<Procedure>> createProcesses(@RequestBody List<Procedure> procedures) {
        return ResponseEntity.ok(procedureService.createProcesses(procedures));
    }


    @PostMapping("/list")
    public ResponseEntity<List<Procedure>> createProcedure(@RequestBody List<Procedure> procedures) {
        return ResponseEntity.ok(procedureService.createProcesses(procedures));
    }
}

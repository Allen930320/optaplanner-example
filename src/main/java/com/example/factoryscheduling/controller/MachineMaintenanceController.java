package com.example.factoryscheduling.controller;

import com.example.factoryscheduling.domain.MachineMaintenance;
import com.example.factoryscheduling.service.MachineMaintenanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/maintenance")
public class MachineMaintenanceController {

    private MachineMaintenanceService maintenanceService;

    @Autowired
    public void setMaintenanceService(MachineMaintenanceService maintenanceService) {
        this.maintenanceService = maintenanceService;
    }

    @PostMapping("/auto")
    public ResponseEntity<List<MachineMaintenance>> auto() {
        return ResponseEntity.ok(maintenanceService.auto());
    }
}

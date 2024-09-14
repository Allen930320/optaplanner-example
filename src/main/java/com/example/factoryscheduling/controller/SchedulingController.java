package com.example.factoryscheduling.controller;

import com.example.factoryscheduling.domain.FactorySchedulingSolution;
import com.example.factoryscheduling.service.SchedulingService;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/scheduling")
public class SchedulingController {

    private  SchedulingService schedulingService;

    @Autowired
    public void setSchedulingService(SchedulingService schedulingService) {
        this.schedulingService = schedulingService;
    }

    @PostMapping("/solve/{problemId}")
    public ResponseEntity<String> startScheduling(@PathVariable Long problemId) {
        schedulingService.startScheduling(problemId);
        return ResponseEntity.ok("Scheduling started for problem " + problemId);
    }

    @GetMapping("/solution/{problemId}")
    public ResponseEntity<FactorySchedulingSolution> getBestSolution(@PathVariable Long problemId) {
        return schedulingService.getBestSolution(problemId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/stop/{problemId}")
    public ResponseEntity<String> stopScheduling(@PathVariable Long problemId) {
        schedulingService.stopScheduling(problemId);
        return ResponseEntity.ok("Scheduling stopped for problem " + problemId);
    }

    @GetMapping("/score/{problemId}")
    public ResponseEntity<HardSoftScore> getScore(@PathVariable Long problemId) {
        return schedulingService.getScore(problemId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/status/{problemId}")
    public ResponseEntity<String> getStatus(@PathVariable Long problemId) {
        boolean isSolving = schedulingService.isSolving(problemId);
        return ResponseEntity.ok(isSolving ? "Solving" : "Not solving");
    }

    @GetMapping("/feasible/{problemId}")
    public ResponseEntity<Boolean> isSolutionFeasible(@PathVariable Long problemId) {
        boolean isFeasible = schedulingService.isSolutionFeasible(problemId);
        return ResponseEntity.ok(isFeasible);
    }
}

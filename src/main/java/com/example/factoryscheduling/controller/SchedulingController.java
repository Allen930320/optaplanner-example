package com.example.factoryscheduling.controller;

import com.example.factoryscheduling.service.SchedulingService;
import com.example.factoryscheduling.solver.FactorySchedulingSolution;
import org.optaplanner.core.api.score.ScoreExplanation;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/scheduling")
public class SchedulingController {

    private final SchedulingService schedulingService;

    @Autowired
    public SchedulingController(SchedulingService schedulingService) {
        this.schedulingService = schedulingService;
    }

    @PostMapping("/solve/{problemId}")
    public ResponseEntity<String> startScheduling(@PathVariable Long problemId) {
        schedulingService.startScheduling(problemId);
        return ResponseEntity.ok("Scheduling started for problem " + problemId);
    }

    @PostMapping("/stop/{problemId}")
    public ResponseEntity<String> stopScheduling(@PathVariable Long problemId) {
        schedulingService.stopScheduling(problemId);
        return ResponseEntity.ok("Scheduling stopped for problem " + problemId);
    }

    @GetMapping("/solution/{problemId}")
    public ResponseEntity<FactorySchedulingSolution> getBestSolution(@PathVariable Long problemId) {
        FactorySchedulingSolution solution = schedulingService.getBestSolution(problemId);
        return ResponseEntity.ok(solution);
    }

    @GetMapping("/score/{problemId}")
    public ResponseEntity<HardSoftScore> getScore(@PathVariable Long problemId) {
        HardSoftScore hardSoftScore = schedulingService.getScore(problemId);
        return ResponseEntity.ok(hardSoftScore);
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

    @PutMapping("/update/{problemId}")
    public ResponseEntity<String> updateProblem(@PathVariable Long problemId, @RequestBody FactorySchedulingSolution updatedSolution) {
        schedulingService.updateProblem(problemId, updatedSolution);
        return ResponseEntity.ok("Problem updated for " + problemId);
    }

    @GetMapping("/explain/{problemId}")
    public ResponseEntity<ScoreExplanation<FactorySchedulingSolution,HardSoftScore>> getExplanation(@PathVariable Long problemId ){
        return ResponseEntity.ok(schedulingService.explainSolution(problemId));
    }
}

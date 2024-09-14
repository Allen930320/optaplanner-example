package com.allen.example.config;

import com.allen.example.domain.ProcessStep;
import com.allen.example.domain.ScheduleSolution;
import com.allen.example.solver.SchedulingConstraintProvider;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.config.solver.SolverConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class OptaPlannerConfig {
    @Bean
    public SolverFactory<ScheduleSolution> solverFactory() {
        SolverConfig solverConfig = new SolverConfig()
                .withSolutionClass(ScheduleSolution.class)
                .withEntityClasses(ProcessStep.class)
                .withConstraintProviderClass(SchedulingConstraintProvider.class)
                .withTerminationSpentLimit(Duration.ofMinutes(5));

        return SolverFactory.create(solverConfig);
    }
}

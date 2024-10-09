package com.example.factoryscheduling.solution;

import com.example.factoryscheduling.domain.Machine;
import com.example.factoryscheduling.domain.Procedure;
import org.optaplanner.core.api.domain.variable.VariableListener;
import org.optaplanner.core.api.score.director.ScoreDirector;

import java.util.List;

public class MachineVariableListener implements VariableListener<OrderSolution, List<Machine>> {

    @Override
    public void beforeVariableChanged(ScoreDirector<OrderSolution> scoreDirector, List<Machine> machines) {

    }

    @Override
    public void afterVariableChanged(ScoreDirector<OrderSolution> scoreDirector, List<Machine> machines) {

    }

    @Override
    public void beforeEntityAdded(ScoreDirector<OrderSolution> scoreDirector, List<Machine> machines) {

    }

    @Override
    public void afterEntityAdded(ScoreDirector<OrderSolution> scoreDirector, List<Machine> machines) {

    }

    @Override
    public void beforeEntityRemoved(ScoreDirector<OrderSolution> scoreDirector, List<Machine> machines) {

    }

    @Override
    public void afterEntityRemoved(ScoreDirector<OrderSolution> scoreDirector, List<Machine> machines) {

    }
}

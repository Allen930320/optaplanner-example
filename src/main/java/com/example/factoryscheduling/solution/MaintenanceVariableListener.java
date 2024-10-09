package com.example.factoryscheduling.solution;

import org.optaplanner.core.api.domain.variable.VariableListener;
import org.optaplanner.core.api.score.director.ScoreDirector;

public class MaintenanceVariableListener implements VariableListener<OrderSolution,OrderSolution>{
    @Override
    public void beforeVariableChanged(ScoreDirector<OrderSolution> scoreDirector, OrderSolution orderSolution) {

    }

    @Override
    public void afterVariableChanged(ScoreDirector<OrderSolution> scoreDirector, OrderSolution orderSolution) {

    }

    @Override
    public void beforeEntityAdded(ScoreDirector<OrderSolution> scoreDirector, OrderSolution orderSolution) {

    }

    @Override
    public void afterEntityAdded(ScoreDirector<OrderSolution> scoreDirector, OrderSolution orderSolution) {

    }

    @Override
    public void beforeEntityRemoved(ScoreDirector<OrderSolution> scoreDirector, OrderSolution orderSolution) {

    }

    @Override
    public void afterEntityRemoved(ScoreDirector<OrderSolution> scoreDirector, OrderSolution orderSolution) {

    }
}

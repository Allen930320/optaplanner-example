package com.example.factoryscheduling.solution;

import com.example.factoryscheduling.domain.Timeslot;
import org.optaplanner.core.api.domain.variable.VariableListener;
import org.optaplanner.core.api.score.director.ScoreDirector;

public class MaintenanceVariableListener implements VariableListener<Timeslot,Timeslot>{
    @Override
    public void beforeVariableChanged(ScoreDirector<Timeslot> scoreDirector, Timeslot timeslot) {

    }

    @Override
    public void afterVariableChanged(ScoreDirector<Timeslot> scoreDirector, Timeslot timeslot) {

    }

    @Override
    public void beforeEntityAdded(ScoreDirector<Timeslot> scoreDirector, Timeslot timeslot) {

    }

    @Override
    public void afterEntityAdded(ScoreDirector<Timeslot> scoreDirector, Timeslot timeslot) {

    }

    @Override
    public void beforeEntityRemoved(ScoreDirector<Timeslot> scoreDirector, Timeslot timeslot) {

    }

    @Override
    public void afterEntityRemoved(ScoreDirector<Timeslot> scoreDirector, Timeslot timeslot) {

    }
}

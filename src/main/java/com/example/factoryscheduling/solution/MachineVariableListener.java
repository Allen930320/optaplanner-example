package com.example.factoryscheduling.solution;

import com.example.factoryscheduling.domain.Machine;
import com.example.factoryscheduling.domain.Procedure;
import com.example.factoryscheduling.domain.Timeslot;
import org.optaplanner.core.api.domain.variable.VariableListener;
import org.optaplanner.core.api.score.director.ScoreDirector;

import java.util.List;

public class MachineVariableListener implements VariableListener<Timeslot, List<Machine>> {
    @Override
    public void beforeVariableChanged(ScoreDirector<Timeslot> scoreDirector, List<Machine> machines) {

    }

    @Override
    public void afterVariableChanged(ScoreDirector<Timeslot> scoreDirector, List<Machine> machines) {

    }

    @Override
    public void beforeEntityAdded(ScoreDirector<Timeslot> scoreDirector, List<Machine> machines) {

    }

    @Override
    public void afterEntityAdded(ScoreDirector<Timeslot> scoreDirector, List<Machine> machines) {

    }

    @Override
    public void beforeEntityRemoved(ScoreDirector<Timeslot> scoreDirector, List<Machine> machines) {

    }

    @Override
    public void afterEntityRemoved(ScoreDirector<Timeslot> scoreDirector, List<Machine> machines) {

    }
}

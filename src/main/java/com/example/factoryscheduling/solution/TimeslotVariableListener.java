package com.example.factoryscheduling.solution;

import com.example.factoryscheduling.domain.MachineMaintenance;
import com.example.factoryscheduling.domain.Timeslot;
import lombok.extern.slf4j.Slf4j;
import org.optaplanner.core.api.domain.variable.VariableListener;
import org.optaplanner.core.api.score.director.ScoreDirector;

import java.time.LocalTime;

@Slf4j
public class TimeslotVariableListener implements VariableListener<FactorySchedulingSolution, Timeslot> {

    @Override
    public void beforeVariableChanged(ScoreDirector<FactorySchedulingSolution> scoreDirector, Timeslot timeslot) {
        MachineMaintenance maintenance = timeslot.getMaintenance();
        if (maintenance != null) {
            maintenance.setUsageTime(maintenance.getUsageTime() - timeslot.getDailyHours());
        }

    }

    @Override
    public void afterVariableChanged(ScoreDirector<FactorySchedulingSolution> scoreDirector, Timeslot timeslot) {
        update(scoreDirector, timeslot);
    }


    private void update(ScoreDirector<FactorySchedulingSolution> scoreDirector, Timeslot timeslot) {
        MachineMaintenance maintenance = timeslot.getMaintenance();
        if (maintenance != null) {
            LocalTime startTime = maintenance.getStartTime();
            int duration = maintenance.getUsageTime();
            duration = duration + timeslot.getDailyHours();
            scoreDirector.beforeVariableChanged(timeslot, "dateTime");
            timeslot.setDateTime(maintenance.getDate().atTime(startTime).plusMinutes(duration));
            scoreDirector.afterVariableChanged(timeslot, "dateTime");
            maintenance.setUsageTime(duration);
        } else {
            scoreDirector.beforeVariableChanged(timeslot, "dateTime");
            timeslot.setDateTime(null);
            scoreDirector.afterVariableChanged(timeslot, "dateTime");
        }
    }

    @Override
    public void beforeEntityAdded(ScoreDirector<FactorySchedulingSolution> scoreDirector, Timeslot timeslot) {

    }

    @Override
    public void afterEntityAdded(ScoreDirector<FactorySchedulingSolution> scoreDirector, Timeslot timeslot) {
        update(scoreDirector, timeslot);
    }

    @Override
    public void beforeEntityRemoved(ScoreDirector<FactorySchedulingSolution> scoreDirector, Timeslot timeslot) {

    }

    @Override
    public void afterEntityRemoved(ScoreDirector<FactorySchedulingSolution> scoreDirector, Timeslot timeslot) {

    }
}

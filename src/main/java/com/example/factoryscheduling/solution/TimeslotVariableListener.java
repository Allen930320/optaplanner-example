package com.example.factoryscheduling.solution;

import com.example.factoryscheduling.domain.MachineMaintenance;
import com.example.factoryscheduling.domain.Timeslot;
import lombok.extern.slf4j.Slf4j;
import org.optaplanner.core.api.domain.variable.VariableListener;
import org.optaplanner.core.api.score.director.ScoreDirector;
import org.springframework.util.CollectionUtils;

import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class TimeslotVariableListener implements VariableListener<FactorySchedulingSolution, Timeslot> {

    @Override
    public void beforeVariableChanged(ScoreDirector<FactorySchedulingSolution> scoreDirector, Timeslot timeslot) {
        // MachineMaintenance maintenance = timeslot.getMaintenance();
        // if (maintenance != null) {
        // maintenance.setUsageTime(maintenance.getUsageTime() - timeslot.getDailyHours());
        // }

    }

    @Override
    public void afterVariableChanged(ScoreDirector<FactorySchedulingSolution> scoreDirector, Timeslot timeslot) {
        update(scoreDirector, timeslot);
    }


    private void update(ScoreDirector<FactorySchedulingSolution> scoreDirector, Timeslot timeslot) {
        MachineMaintenance maintenance = timeslot.getMaintenance();
        if (maintenance != null) {
            List<Timeslot> timeslots = scoreDirector.getWorkingSolution().getTimeslots()
                    .stream()
                    .filter(t -> t.getMaintenance() != null && t.getMaintenance().getId().equals(maintenance.getId()))
                    .collect(Collectors.toList());
            LocalTime startTime = maintenance.getStartTime();
            if (CollectionUtils.isEmpty(timeslots) || !timeslots.stream().map(Timeslot::getMaintenance)
                    .collect(Collectors.toList()).contains(maintenance)) {
                timeslots.add(timeslot);
            }
            int duration = timeslots.stream().mapToInt(Timeslot::getDailyHours).sum();
            scoreDirector.beforeVariableChanged(timeslot, "dateTime");
            timeslot.setDateTime(
                    maintenance.getDate().atTime(startTime).plusMinutes(duration - timeslot.getDailyHours()));
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
        log.info("beforeEntityAdded");
    }

    @Override
    public void afterEntityAdded(ScoreDirector<FactorySchedulingSolution> scoreDirector, Timeslot timeslot) {
        update(scoreDirector, timeslot);
    }

    @Override
    public void beforeEntityRemoved(ScoreDirector<FactorySchedulingSolution> scoreDirector, Timeslot timeslot) {
        log.info("beforeEntityRemoved");
    }

    @Override
    public void afterEntityRemoved(ScoreDirector<FactorySchedulingSolution> scoreDirector, Timeslot timeslot) {
        log.info("beforeEntityRemoved");
    }
}

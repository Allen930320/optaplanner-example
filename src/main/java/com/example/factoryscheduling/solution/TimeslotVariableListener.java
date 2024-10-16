package com.example.factoryscheduling.solution;

import com.example.factoryscheduling.domain.MachineMaintenance;
import com.example.factoryscheduling.domain.Timeslot;
import lombok.extern.slf4j.Slf4j;
import org.optaplanner.core.api.domain.variable.VariableListener;
import org.optaplanner.core.api.score.director.ScoreDirector;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;
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
            List<Timeslot> maintenanceTimeslots = scoreDirector.getWorkingSolution().getTimeslots()
                    .stream()
                    .filter(t -> t.getMaintenance() != null && t.getMaintenance().getId().equals(maintenance.getId()))
                    .collect(Collectors.toList());
            LocalTime startTime = maintenance.getStartTime();
            if (CollectionUtils.isEmpty(maintenanceTimeslots) || !maintenanceTimeslots.stream().map(Timeslot::getMaintenance)
                    .collect(Collectors.toList()).contains(maintenance)) {
                maintenanceTimeslots.add(timeslot);
            }
            int duration = maintenanceTimeslots.stream().mapToInt(Timeslot::getDailyHours).sum();
            List<Timeslot> procedureTimeslots = scoreDirector.getWorkingSolution().getTimeslots()
                            .stream().filter(t->t.getProcedure().getId().equals(timeslot.getProcedure().getId()))
                            .collect(Collectors.toList());
            LocalDateTime start = procedureTimeslots.stream().map(Timeslot::getDateTime).filter(Objects::nonNull).min(LocalDateTime::compareTo).orElse(null);
            LocalDateTime end =
                    procedureTimeslots.stream().filter(t -> t.getDateTime() != null)
                            .map(t -> t.getDateTime().plusMinutes(t.getDailyHours())).max(LocalDateTime::compareTo)
                            .orElse(null);
            scoreDirector.beforeVariableChanged(timeslot, "dateTime");
            timeslot.setDateTime(maintenance.getDate().atTime(startTime).plusMinutes(duration - timeslot.getDailyHours()));
            scoreDirector.afterVariableChanged(timeslot, "dateTime");
            maintenance.setUsageTime(duration);
            if (start != null) {
                timeslot.getProcedure().setStartTime(start);
            }
            if (end != null) {
                timeslot.getProcedure().setEndTime(end);
            }
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

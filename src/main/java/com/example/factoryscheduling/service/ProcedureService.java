package com.example.factoryscheduling.service;

import com.example.factoryscheduling.domain.Procedure;
import com.example.factoryscheduling.repository.ProcedureRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ProcedureService {

    private ProcedureRepository processRepository;

    @Autowired
    public void setProcessRepository(ProcedureRepository processRepository) {
        this.processRepository = processRepository;
    }
    @Transactional
    public List<Procedure> createProcesses(List<Procedure> processes) {
        String[] machine = {"01", "02", "03", "04", "05"};
        Map<String, List<Procedure>> map = processes.stream().collect(Collectors.groupingBy(Procedure::getOrderNo));
        List<Procedure> newP = new ArrayList<>();
        map.forEach((key, value) -> {
            int size = value.size();
            for (Procedure procedure : value) {
                long id = procedure.getId();
                int procedureNo = (int) (id % 10) * 10;
                procedure.setProcedureNo(Integer.toString(procedureNo));
                if (procedureNo != size * 10) {
                    String[] next = new String[] {Integer.toString(procedureNo + 10)};
                    procedure.setNextProcedureNo(Arrays.asList(next));
                }
                int index = (int) (Math.random() * 100) % 5;
                procedure.setMachineNo(machine[index]);
                procedure.setStartTime(null);
                newP.add(procedure);
            }
        });
        return processRepository.saveAll(newP);
    }

    public List<Procedure> findAll() {
        return processRepository.findAll();
    }

}

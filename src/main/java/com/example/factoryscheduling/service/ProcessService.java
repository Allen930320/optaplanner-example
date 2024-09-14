package com.example.factoryscheduling.service;

import com.example.factoryscheduling.domain.Process;
import com.example.factoryscheduling.repository.ProcessRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProcessService {

    private  ProcessRepository processRepository;

    @Autowired
    public void setProcessRepository(ProcessRepository processRepository) {
        this.processRepository = processRepository;
    }

    public List<Process> getAllProcesses() {
        return processRepository.findAll();
    }

    public Optional<Process> getProcessById(Long id) {
        return processRepository.findById(id);
    }

    public Process createProcess(Process process) {
        return processRepository.save(process);
    }

    public Process updateProcess(Long id, Process processDetails) {
        Optional<Process> process = processRepository.findById(id);
        if (process.isPresent()) {
            Process existingProcess = process.get();
            existingProcess.setName(processDetails.getName());
            existingProcess.setProcessNumber(processDetails.getProcessNumber());
            existingProcess.setNextProcessNumber(processDetails.getNextProcessNumber());
            existingProcess.setProcessingTime(processDetails.getProcessingTime());
            existingProcess.setOrder(processDetails.getOrder());
            existingProcess.setMachine(processDetails.getMachine());
            return processRepository.save(existingProcess);
        }
        return null;
    }

    public void deleteProcess(Long id) {
        processRepository.deleteById(id);
    }
}

package com.example.factoryscheduling.service;

import com.example.factoryscheduling.domain.Process;
import com.example.factoryscheduling.repository.ProcessRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
public class ProcessService {

    private final ProcessRepository processRepository;
    private final ProcessLinkService processLinkService;

    @Autowired
    public ProcessService(ProcessRepository processRepository, ProcessLinkService processLinkService) {
        this.processRepository = processRepository;
        this.processLinkService = processLinkService;
    }

    public List<Process> getAllProcesses() {
        return processRepository.findAll();
    }

    public Optional<Process> getProcessById(Long id) {
        return processRepository.findById(id);
    }

    @Transactional
    public Process createProcess(Process process) {
        Process savedProcess = processRepository.save(process);

        // 创建工序链接
        if (process.getNextLinks() != null) {
            process.getNextLinks().forEach(link -> {
                link.setFromProcess(savedProcess);
                processLinkService.createProcessLink(link);
            });
        }

        return savedProcess;
    }

    @Transactional
    public Process updateProcess(Long id, Process processDetails) {
        return processRepository.findById(id)
                .map(existingProcess -> {
                    existingProcess.setName(processDetails.getName());
                    existingProcess.setProcessingTime(processDetails.getProcessingTime());
                    existingProcess.setOrder(processDetails.getOrder());
                    existingProcess.setMachine(processDetails.getMachine());
                    existingProcess.setStartTime(processDetails.getStartTime());
                    existingProcess.setActualStartTime(processDetails.getActualStartTime());
                    existingProcess.setStatus(processDetails.getStatus());
                    existingProcess.setRequiresMachine(processDetails.isRequiresMachine());

                    // 更新工序链接
                    processLinkService.updateProcessLinks(existingProcess, processDetails.getNextLinks());

                    return processRepository.save(existingProcess);
                })
                .orElseThrow(() -> new RuntimeException("Process not found with id " + id));
    }

    @Transactional
    public void deleteProcess(Long id) {
        processRepository.deleteById(id);
    }
    public void updateAll(List<Process> processes) {
        processRepository.saveAll(processes);
    }
}

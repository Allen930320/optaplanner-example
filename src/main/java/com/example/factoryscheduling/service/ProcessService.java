package com.example.factoryscheduling.service;

import com.example.factoryscheduling.domain.Process;
import com.example.factoryscheduling.domain.ProcessLink;
import com.example.factoryscheduling.repository.ProcessRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.transaction.Transactional;
import java.util.ArrayList;
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
        List<ProcessLink> links = process.getNext();

        return savedProcess;
    }

    public void saveAll(List<Process> processes) {
        processes = processRepository.saveAll(processes);
        for (Process process : processes) {
            List<ProcessLink> links = new ArrayList<>();
            if (!CollectionUtils.isEmpty(process.getNext())) {
                for (ProcessLink link : process.getNext()) {
                    if (link.getProcess() == null) {
                        continue;
                    }
                    link.setCurrent(process);
                    Process next = processRepository.getById(link.getProcess().getId());
                    link.setProcess(next);
                    processLinkService.createProcessLink(link);
                    links.add(link);
                }
            }
            process.setNext(links);
        }

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
                    processLinkService.updateProcessLinks(existingProcess, processDetails.getNext());

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

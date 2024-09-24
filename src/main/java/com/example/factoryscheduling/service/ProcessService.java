package com.example.factoryscheduling.service;

import com.example.factoryscheduling.domain.Link;
import com.example.factoryscheduling.domain.Process;
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
    private final LinkService processLinkService;

    @Autowired
    public ProcessService(ProcessRepository processRepository, LinkService processLinkService) {
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
        return savedProcess;
    }

    @Transactional
    public List<Process> createProcesses(List<Process> processes) {
        return processRepository.saveAll(processes);

    }
    public void saveAll(List<Process> processes) {
        processes = processRepository.saveAll(processes);
        for (Process process : processes) {
            List<Link> links = new ArrayList<>();
            if (!CollectionUtils.isEmpty(process.getLink())) {
                for (Link link : process.getLink()) {
                    if (link.getNext() == null) {
                        continue;
                    }
                    link.setPrevious(process);
                    Process next = processRepository.getById(link.getNext().getId());
                    link.setNext(next);
                    processLinkService.createProcessLink(link);
                    links.add(link);
                }
            }
            process.setLink(links);
        }

    }

    @Transactional
    public Process update(Process process) {
        return processRepository.save(process);
    }

    @Transactional
    public void deleteProcess(Long id) {
        processRepository.deleteById(id);
    }
    public void updateAll(List<Process> processes) {
        processRepository.saveAll(processes);
    }
}

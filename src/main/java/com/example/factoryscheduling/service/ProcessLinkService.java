package com.example.factoryscheduling.service;

import com.example.factoryscheduling.domain.Process;
import com.example.factoryscheduling.domain.ProcessLink;
import com.example.factoryscheduling.repository.ProcessLinkRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ProcessLinkService {

    private final ProcessLinkRepository processLinkRepository;

    @Autowired
    public ProcessLinkService(ProcessLinkRepository processLinkRepository) {
        this.processLinkRepository = processLinkRepository;
    }

    public List<ProcessLink> getAllProcessLinks() {
        return processLinkRepository.findAll();
    }

    public Optional<ProcessLink> getProcessLinkById(Long id) {
        return processLinkRepository.findById(id);
    }

    @Transactional
    public ProcessLink createProcessLink(ProcessLink processLink) {
        return processLinkRepository.save(processLink);
    }

    @Transactional
    public ProcessLink updateProcessLink(Long id, ProcessLink processLinkDetails) {
        return processLinkRepository.findById(id)
                .map(existingLink -> {
                    existingLink.setFromProcess(processLinkDetails.getFromProcess());
                    existingLink.setToProcess(processLinkDetails.getToProcess());
                    existingLink.setParallel(processLinkDetails.isParallel());
                    return processLinkRepository.save(existingLink);
                })
                .orElseThrow(() -> new RuntimeException("ProcessLink not found with id " + id));
    }

    @Transactional
    public void deleteProcessLink(Long id) {
        processLinkRepository.deleteById(id);
    }

    @Transactional
    public void updateProcessLinks(Process process, List<ProcessLink> newLinks) {
        // 删除旧的链接
//        processLinkRepository.deleteByFromProcess(process);
        // 创建新的链接
        if (newLinks != null) {
            newLinks.forEach(link -> {
                link.setFromProcess(process);
                createProcessLink(link);
            });
        }
    }
}

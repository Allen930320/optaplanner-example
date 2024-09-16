package com.example.factoryscheduling.service;

import com.example.factoryscheduling.domain.Link;
import com.example.factoryscheduling.domain.Process;
import com.example.factoryscheduling.repository.ProcessLinkRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class LinkService {

    private final ProcessLinkRepository processLinkRepository;

    @Autowired
    public LinkService(ProcessLinkRepository processLinkRepository) {
        this.processLinkRepository = processLinkRepository;
    }

    public List<Link> getAllProcessLinks() {
        return processLinkRepository.findAll();
    }

    public Optional<Link> getProcessLinkById(Long id) {
        return processLinkRepository.findById(id);
    }

    @Transactional
    public Link createProcessLink(Link processLink) {
        return processLinkRepository.save(processLink);
    }

    @Transactional
    public Link updateProcessLink(Long id, Link processLinkDetails) {
        return processLinkRepository.findById(id)
                .map(existingLink -> {
                    existingLink.setNext(processLinkDetails.getNext());
                    existingLink.setParallel(processLinkDetails.isParallel());
                    return processLinkRepository.save(existingLink);
                })
                .orElseThrow(() -> new RuntimeException("Link not found with id " + id));
    }

    @Transactional
    public void deleteProcessLink(Long id) {
        processLinkRepository.deleteById(id);
    }

    @Transactional
    public void updateProcessLinks(Process process, List<Link> newLinks) {
        // 删除旧的链接
//        processLinkRepository.deleteByFromProcess(process);
        // 创建新的链接
        if (newLinks != null) {
            newLinks.forEach(link -> {
                link.setNext(process);
                createProcessLink(link);
            });
        }
    }

    public void saveAll(List<Link> links){
        processLinkRepository.saveAll(links);
    }
}

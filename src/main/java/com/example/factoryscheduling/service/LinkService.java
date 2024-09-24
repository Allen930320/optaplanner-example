package com.example.factoryscheduling.service;

import com.example.factoryscheduling.domain.Link;
import com.example.factoryscheduling.repository.ProcessLinkRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class LinkService {

    private  ProcessLinkRepository processLinkRepository;

    @Autowired
    public void setProcessLinkRepository(ProcessLinkRepository processLinkRepository) {
        this.processLinkRepository = processLinkRepository;
    }

    @Transactional
    public Link createProcessLink(Link processLink) {
        return processLinkRepository.save(processLink);
    }

    public List<Link> findAll(){
        return processLinkRepository.findAll();
    }
}

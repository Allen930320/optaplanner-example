package com.example.factoryscheduling.repository;

import com.example.factoryscheduling.domain.ProcessLink;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProcessLinkRepository  extends JpaRepository<ProcessLink,Long> {
}

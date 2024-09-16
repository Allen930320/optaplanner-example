package com.example.factoryscheduling.repository;

import com.example.factoryscheduling.domain.Link;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProcessLinkRepository  extends JpaRepository<Link,Long> {
}

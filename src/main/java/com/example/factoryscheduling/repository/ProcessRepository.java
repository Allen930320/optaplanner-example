package com.example.factoryscheduling.repository;

import com.example.factoryscheduling.domain.Process;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProcessRepository extends JpaRepository<Process, Long> {

}

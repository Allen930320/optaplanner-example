package com.example.factoryscheduling.repository;

import com.example.factoryscheduling.domain.Machine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MachineRepository extends JpaRepository<Machine,Long> {
    @Override
    List<Machine> findAll();
}

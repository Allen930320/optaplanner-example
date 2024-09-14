package com.allen.example.persistence;

import com.allen.example.domain.Machine;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface MachineRepository extends PagingAndSortingRepository<Machine,Long> {
    @Override
    List<Machine> findAll();
}

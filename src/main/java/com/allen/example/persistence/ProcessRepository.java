package com.allen.example.persistence;

import com.allen.example.entity.Process;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface ProcessRepository extends PagingAndSortingRepository<Process, Long> {
    @Override
    List<Process> findAll();
}

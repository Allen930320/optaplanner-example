package com.allen.example.persistence;

import com.allen.example.domain.ProcessStep;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface ProcessStepRepository extends PagingAndSortingRepository<ProcessStep, Long> {
    @Override
    List<ProcessStep> findAll();
}

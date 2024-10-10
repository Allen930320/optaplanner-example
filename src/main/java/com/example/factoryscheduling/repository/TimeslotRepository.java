package com.example.factoryscheduling.repository;

import com.example.factoryscheduling.domain.Timeslot;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface TimeslotRepository extends PagingAndSortingRepository<Timeslot, Long> {

    @Override
    List<Timeslot> findAll();

}

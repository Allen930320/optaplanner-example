package com.allen.example.persistence;

import com.allen.example.entity.Order;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface OrderRepository extends PagingAndSortingRepository<Order, Long> {
    @Override
    List<Order> findAll();
}

package com.example.factoryscheduling.repository;

import com.example.factoryscheduling.domain.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    @Override
    List<Order> findAll();

    Order findFirstByOrderNo(String orderNo);
}

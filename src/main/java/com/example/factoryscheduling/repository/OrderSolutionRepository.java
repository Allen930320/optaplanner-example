package com.example.factoryscheduling.repository;

import com.example.factoryscheduling.solution.OrderSolution;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderSolutionRepository extends JpaRepository<OrderSolution, Long> {
}

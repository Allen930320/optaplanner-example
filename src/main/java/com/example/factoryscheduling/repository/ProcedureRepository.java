package com.example.factoryscheduling.repository;

import com.example.factoryscheduling.domain.Procedure;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProcedureRepository extends JpaRepository<Procedure, Long> {


    Procedure findFirstByOrderNoAndMachineNoAndProcedureNo(String orderNo,String machineNo,Integer procedureNo);
}

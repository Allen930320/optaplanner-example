package com.example.factoryscheduling.service;

import com.example.factoryscheduling.domain.Machine;
import com.example.factoryscheduling.repository.MachineRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MachineService {

    private  MachineRepository machineRepository;


    @Autowired
    public void setMachineRepository(MachineRepository machineRepository) {
        this.machineRepository = machineRepository;
    }

    public List<Machine> getAllMachines() {
        return machineRepository.findAll();
    }

    public Optional<Machine> getMachineById(Long id) {
        return machineRepository.findById(id);
    }

    public Machine createMachine(Machine machine) {
        return machineRepository.save(machine);
    }

    public Machine updateMachine(Long id, Machine machineDetails) {
        Optional<Machine> machine = machineRepository.findById(id);
        if (machine.isPresent()) {
            Machine existingMachine = machine.get();
            existingMachine.setName(machineDetails.getName());
            existingMachine.setModel(machineDetails.getModel());
            return machineRepository.save(existingMachine);
        }
        return null;
    }

    public void deleteMachine(Long id) {
        machineRepository.deleteById(id);
    }

    public void updateAll(List<Machine> machines){
        machineRepository.saveAll(machines);
    }

    public List<Machine> create(List<Machine> machines){
        machineRepository.saveAll(machines);
        return machines;
    }

    public Machine findFirstByMachineNo(String machineNo){
        return machineRepository.findFirstByMachineNo(machineNo);
    }
}

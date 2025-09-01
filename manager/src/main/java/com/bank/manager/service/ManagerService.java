package com.bank.manager.service;

import com.bank.manager.model.Manager;
import com.bank.manager.model.ManagerType;
import com.bank.manager.repository.ManagerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ManagerService {

    @Autowired
    private ManagerRepository managerRepository;

    public Manager save(Manager manager) {
        // add in the future business logics before saving.
        if (managerRepository.findByEmail(manager.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email already in use.");
        }
        return managerRepository.save(manager);
    }

    public Optional<Manager> findByCpf(String cpf) {
        return managerRepository.findById(cpf);
    }

    public List<Manager> findAll() {
        return managerRepository.findAll();
    }

    public void deleteByCpf(String cpf) {
        managerRepository.deleteById(cpf);
    }

    public List<Manager> findByType(ManagerType type) {
        return managerRepository.findByType(type);
    }
}
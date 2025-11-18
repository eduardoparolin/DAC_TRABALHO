package com.bank.manager.repository;

import com.bank.manager.model.Manager;
import com.bank.manager.model.ManagerType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface ManagerRepository extends JpaRepository<Manager, Long> {
    Optional<Manager> findByCpf(String cpf);
    Optional<Manager> findByEmail(String email);
    List<Manager> findByIdIn(List<Long> managerIds);

    // Get all manager IDs (to be used with bank-account service for counting)
    @org.springframework.data.jpa.repository.Query("SELECT m.id FROM Manager m")
    List<Long> findAllManagerIds();
}
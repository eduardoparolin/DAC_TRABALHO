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
    List<Manager> findByIdIn(List<Long> managerIds);
}
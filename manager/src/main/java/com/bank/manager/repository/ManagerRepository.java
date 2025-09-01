package com.bank.manager.repository;

import com.bank.manager.model.Manager;
import com.bank.manager.model.ManagerType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface ManagerRepository extends JpaRepository<Manager, String> {

    // save(), findAll(), findById(), and deleteById() included in Jpa.

    // A custom query method to find a manager by their email.
    Optional<Manager> findByEmail(String email);

    // A custom query method to find all managers of a specific type.
    List<Manager> findByType(ManagerType type);
}
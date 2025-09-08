package com.bank.manager.repository;

import com.bank.manager.entity.Manager;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ManagerRepository extends JpaRepository<Manager, String> {
    // JpaRepository already provides findById, findAll, deleteById.
}
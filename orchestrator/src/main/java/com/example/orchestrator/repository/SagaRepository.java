package com.example.orchestrator.repository;

import com.example.orchestrator.model.Saga;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SagaRepository extends JpaRepository<Saga, String> {

}

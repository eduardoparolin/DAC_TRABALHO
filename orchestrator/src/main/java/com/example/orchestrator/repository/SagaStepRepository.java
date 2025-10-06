package com.example.orchestrator.repository;

import com.example.orchestrator.model.SagaStep;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SagaStepRepository extends JpaRepository<SagaStep, Long> {

}

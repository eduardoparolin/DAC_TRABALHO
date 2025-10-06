package com.example.orchestrator.model;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "saga")
public class Saga {

  @Id
  @Column(name = "saga_id", length = 100)
  private String sagaId;

  @Column(name = "status", length = 50, nullable = false)
  private String status;

  @Column(name = "created_at", nullable = false)
  private Instant createdAt;

  @OneToMany(mappedBy = "saga", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<SagaStep> steps = new ArrayList<>();

  public Saga() {
  }

  public Saga(String sagaId, String status, Instant createdAt) {
    this.sagaId = sagaId;
    this.status = status;
    this.createdAt = createdAt;
  }

  public String getSagaId() {
    return sagaId;
  }

  public void setSagaId(String sagaId) {
    this.sagaId = sagaId;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(Instant createdAt) {
    this.createdAt = createdAt;
  }

  public List<SagaStep> getSteps() {
    return steps;
  }

  public void setSteps(List<SagaStep> steps) {
    this.steps = steps;
  }

  public void addStep(SagaStep step) {
    step.setSaga(this);
    this.steps.add(step);
  }
}

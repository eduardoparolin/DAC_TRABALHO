package com.example.orchestrator.model;

import jakarta.persistence.*;

@Entity
@Table(name = "saga_step")
public class SagaStep {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "saga_id", nullable = false)
  private Saga saga;

  @Column(name = "step_name", length = 100, nullable = false)
  private String name;

  @Column(name = "status", length = 50, nullable = false)
  private String status;

  @Column(name = "payload", columnDefinition = "text")
  private String payload;

  @Column(name = "compensation", length = 100)
  private String compensation;

  public SagaStep() {
  }

  public SagaStep(String name, String status, String payload, String compensation) {
    this.name = name;
    this.status = status;
    this.payload = payload;
    this.compensation = compensation;
  }

  public Long getId() {
    return id;
  }

  public Saga getSaga() {
    return saga;
  }

  public void setSaga(Saga saga) {
    this.saga = saga;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public String getPayload() {
    return payload;
  }

  public void setPayload(String payload) {
    this.payload = payload;
  }

  public String getCompensation() {
    return compensation;
  }

  public void setCompensation(String compensation) {
    this.compensation = compensation;
  }
}

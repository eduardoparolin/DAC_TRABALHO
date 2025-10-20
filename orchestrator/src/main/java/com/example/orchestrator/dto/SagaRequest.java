package com.example.orchestrator.dto;

import java.util.Map;

public class SagaRequest {

  private String sagaId;
  private String action;
  private Map<String, Object> data;

  public SagaRequest() {
  }

  public SagaRequest(String sagaId, String action, Map<String, Object> data) {
    this.sagaId = sagaId;
    this.action = action;
    this.data = data;
  }

  public String getSagaId() {
    return sagaId;
  }

  public void setSagaId(String sagaId) {
    this.sagaId = sagaId;
  }

  public String getAction() {
    return action;
  }

  public void setAction(String action) {
    this.action = action;
  }

  public Map<String, Object> getData() {
    return data;
  }

  public void setData(Map<String, Object> data) {
    this.data = data;
  }
}

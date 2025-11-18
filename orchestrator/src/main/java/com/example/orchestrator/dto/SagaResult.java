package com.example.orchestrator.dto;

import com.fasterxml.jackson.databind.util.JSONPObject;

public class SagaResult {

  private String sagaId;
  private String source;
  private String action;
  private String status;
  private String error;
  private Long clientId;
  private String accountId;
  private Long transactionId;
  private Long managerId;
  private String accountNumber;
  private String generatedPassword;
  private JSONPObject result;
  private Long managerIdLessAccounts;
  private Long oldManagerId;

  public SagaResult() {
  }

  public String getSagaId() {
    return sagaId;
  }

  public void setSagaId(String sagaId) {
    this.sagaId = sagaId;
  }

  public String getSource() {
    return source;
  }

  public void setSource(String source) {
    this.source = source;
  }

  public String getAction() {
    return action;
  }

  public void setAction(String action) {
    this.action = action;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public String getError() {
    return error;
  }

  public void setError(String error) {
    this.error = error;
  }

  public Long getClientId() {
    return clientId;
  }

  public void setClientId(Long clientId) {
    this.clientId = clientId;
  }

  public String getAccountId() {
    return accountId;
  }

  public void setAccountId(String accountId) {
    this.accountId = accountId;
  }

  public Long getTransactionId() {
    return transactionId;
  }

  public void setTransactionId(Long transactionId) {
    this.transactionId = transactionId;
  }

  public Long getManagerId() {
    return managerId;
  }

  public void setManagerId(Long managerId) {
    this.managerId = managerId;
  }

  public String getAccountNumber() {
    return accountNumber;
  }

  public void setAccountNumber(String accountNumber) {
    this.accountNumber = accountNumber;
  }

  public String getGeneratedPassword() {
    return generatedPassword;
  }

  public void setGeneratedPassword(String generatedPassword) {
    this.generatedPassword = generatedPassword;
  }

    public JSONPObject getResult() {
        return result;
    }

    public void setResult(JSONPObject result) {
        this.result = result;
    }

    @Override
  public String toString() {
    return "SagaResult{" +
        "sagaId='" + sagaId + '\'' +
        ", source='" + source + '\'' +
        ", action='" + action + '\'' +
        ", status='" + status + '\'' +
        ", error='" + error + '\'' +
        ", clientId=" + clientId +
        ", accountId=" + accountId +
        ", transactionId=" + transactionId +
        ", managerId=" + managerId +
        ", accountNumber='" + accountNumber + '\'' +
        ", generatedPassword='" + generatedPassword + '\'' +
        ", result='" + result + '\'' +
        ", managerIdLessAccounts='" + managerIdLessAccounts + '\'' +
        '}';
  }

  public Long getManagerIdLessAccounts() {
    return managerIdLessAccounts;
  }

  public void setManagerIdLessAccounts(Long managerIdLessAccounts) {
    this.managerIdLessAccounts = managerIdLessAccounts;
  }

  public Long getOldManagerId() {
    return oldManagerId;
  }

  public void setOldManagerId(Long oldManagerId) {
    this.oldManagerId = oldManagerId;
  }
}

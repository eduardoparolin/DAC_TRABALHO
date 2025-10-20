package com.example.orchestrator.dto;

public class ClientMessageRequest {

  private String action;
  private Long id;
  private String cpf;
  private String name;
  private String email;
  private String phone;

  public ClientMessageRequest() {
  }

  public ClientMessageRequest(String action, Long id, String cpf, String name, String email, String phone) {
    this.action = action;
    this.id = id;
    this.cpf = cpf;
    this.name = name;
    this.email = email;
    this.phone = phone;
  }

  public String getAction() {
    return action;
  }

  public void setAction(String action) {
    this.action = action;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getCpf() {
    return cpf;
  }

  public void setCpf(String cpf) {
    this.cpf = cpf;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getPhone() {
    return phone;
  }

  public void setPhone(String phone) {
    this.phone = phone;
  }
}

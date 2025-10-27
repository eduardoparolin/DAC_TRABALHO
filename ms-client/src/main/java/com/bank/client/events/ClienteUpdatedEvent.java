package com.bank.client.events;

import java.math.BigDecimal;

public class ClienteUpdatedEvent {
    private Long id;
    private String nome;
    private String email;
    private String cpf;
    private BigDecimal salario;

    public ClienteUpdatedEvent() {}

    public ClienteUpdatedEvent(Long id, String nome, String email, String cpf, BigDecimal salario) {
        this.id = id; this.nome = nome; this.email = email; this.cpf = cpf; this.salario = salario;
    }

    public Long getId() { return id; } public void setId(Long id) { this.id = id; }
    public String getNome() { return nome; } public void setNome(String nome) { this.nome = nome; }
    public String getEmail() { return email; } public void setEmail(String email) { this.email = email; }
    public String getCpf() { return cpf; } public void setCpf(String cpf) { this.cpf = cpf; }
    public BigDecimal getSalario() { return salario; } public void setSalario(BigDecimal salario) { this.salario = salario; }
}

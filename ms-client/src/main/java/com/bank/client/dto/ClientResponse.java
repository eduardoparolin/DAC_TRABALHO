package com.bank.client.dto;

import java.math.BigDecimal;

public class ClientResponse {
    private Long id; private String nome; private String email; private String cpf; private String telefone; private BigDecimal salario;

    public ClientResponse() {}
    public ClientResponse(Long id, String nome, String email, String cpf, String telefone, BigDecimal salario) {
        this.id=id; this.nome=nome; this.email=email; this.cpf=cpf; this.telefone=telefone; this.salario=salario;
    }

    public Long getId() { return id; } public void setId(Long id) { this.id = id; }
    public String getNome() { return nome; } public void setNome(String nome) { this.nome = nome; }
    public String getEmail() { return email; } public void setEmail(String email) { this.email = email; }
    public String getCpf() { return cpf; } public void setCpf(String cpf) { this.cpf = cpf; }
    public String getTelefone() { return telefone; } public void setTelefone(String telefone) { this.telefone = telefone; }
    public BigDecimal getSalario() { return salario; } public void setSalario(BigDecimal salario) { this.salario = salario; }
}

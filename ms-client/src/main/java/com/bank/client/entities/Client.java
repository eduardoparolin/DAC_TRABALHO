package com.bank.client.entities;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "client")
public class Client {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false, length=120)
    private String nome;

    @Column(nullable=false, length=150)
    private String email;

    @Column(nullable=false, length=11)
    private String cpf;

    @Column(length=20)
    private String telefone;

    @Column(nullable=false, precision=14, scale=2)
    private BigDecimal salario;

    public Long getId() { return id; } public void setId(Long id) { this.id = id; }
    public String getNome() { return nome; } public void setNome(String nome) { this.nome = nome; }
    public String getEmail() { return email; } public void setEmail(String email) { this.email = email; }
    public String getCpf() { return cpf; } public void setCpf(String cpf) { this.cpf = cpf; }
    public String getTelefone() { return telefone; } public void setTelefone(String telefone) { this.telefone = telefone; }
    public BigDecimal getSalario() { return salario; } public void setSalario(BigDecimal salario) { this.salario = salario; }
}


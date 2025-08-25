package com.bank.client.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public class ClientRequest {
    @NotBlank @Size(max=120) private String nome;
    @NotBlank @Email @Size(max=150) private String email;
    @NotBlank @Pattern(regexp="\\d{11}") private String cpf;
    @Size(max=20) private String telefone;
    @NotNull @DecimalMin("0.00") private BigDecimal salario;

    public String getNome() { return nome; } public void setNome(String nome) { this.nome = nome; }
    public String getEmail() { return email; } public void setEmail(String email) { this.email = email; }
    public String getCpf() { return cpf; } public void setCpf(String cpf) { this.cpf = cpf; }
    public String getTelefone() { return telefone; } public void setTelefone(String telefone) { this.telefone = telefone; }
    public BigDecimal getSalario() { return salario; } public void setSalario(BigDecimal salario) { this.salario = salario; }
}

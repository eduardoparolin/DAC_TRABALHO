package com.bank.client.dto;

import com.bank.client.validation.Cpf;
import com.bank.client.validation.TelefoneBr;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public class ClientRequest {
    @NotBlank @Size(max = 120)
    private String nome;

    @NotBlank @Email @Size(max = 150)
    private String email;

    // troca o @Pattern por @Cpf (aceita com/sem máscara e valida DV)
    @NotBlank
    @Cpf(allowFormatted = true)
    private String cpf;

    // mantém @Size e adiciona validação brasileira
    @Size(max = 20)
    @TelefoneBr(allowFormatted = true)
    private String telefone;

    @NotNull @DecimalMin("0.00")
    private BigDecimal salario;

    public String getNome() { return nome; } public void setNome(String nome) { this.nome = nome; }
    public String getEmail() { return email; } public void setEmail(String email) { this.email = email; }
    public String getCpf() { return cpf; } public void setCpf(String cpf) { this.cpf = cpf; }
    public String getTelefone() { return telefone; } public void setTelefone(String telefone) { this.telefone = telefone; }
    public BigDecimal getSalario() { return salario; } public void setSalario(BigDecimal salario) { this.salario = salario; }
}

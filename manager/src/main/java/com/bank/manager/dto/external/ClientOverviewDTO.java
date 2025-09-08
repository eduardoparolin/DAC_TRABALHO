package com.bank.manager.dto.external;

import java.math.BigDecimal;

public class ClientOverviewDTO {
    private String cpf;
    private String nome;
    private String cidade;
    private String estado;
    private BigDecimal saldo;
    private BigDecimal limite;

    public ClientOverviewDTO() {}

    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getCidade() { return cidade; }
    public void setCidade(String cidade) { this.cidade = cidade; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public BigDecimal getSaldo() { return saldo; }
    public void setSaldo(BigDecimal saldo) { this.saldo = saldo; }
    public BigDecimal getLimite() { return limite; }
    public void setLimite(BigDecimal limite) { this.limite = limite; }
}

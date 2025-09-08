package com.bank.manager.dto.external;

import java.math.BigDecimal;

/** DTO used to map response from MS-Cliente /clients/{cpf}/summary */
public class ClientSummaryDTO {
    private String cpf;
    private String email;
    private BigDecimal salary;

    public ClientSummaryDTO() {}

    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public BigDecimal getSalary() { return salary; }
    public void setSalary(BigDecimal salary) { this.salary = salary; }
}

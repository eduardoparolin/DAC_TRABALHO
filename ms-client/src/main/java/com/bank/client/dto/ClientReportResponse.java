package com.bank.client.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClientReportResponse {
    private String cpf;
    private String name;
    private String email;
    private BigDecimal salary;
    private String accountId;
    private Long managerId;
}

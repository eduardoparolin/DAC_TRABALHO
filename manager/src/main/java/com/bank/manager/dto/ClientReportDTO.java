package com.bank.manager.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClientReportDTO {
    private String cpf;
    private String name;
    private String email;
    private BigDecimal salary;
    private Long accountId;
    private Long managerId;
}

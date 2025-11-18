package com.bank.client.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClientResponse {
    private Long id;
    private String name;
    private String email;
    private String cpf;
    private String phone;
    private BigDecimal salary;
    private String accountId;
    private String status;
    private String rejectionReason;
    private Long managerId;
    private OffsetDateTime creationDate;
    private OffsetDateTime approvalDate;
    private String street;
    private String complement;
    private String zipCode;
    private String city;
    private String state;
}

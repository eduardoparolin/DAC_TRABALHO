package com.bank.client.dto;

import com.bank.client.enums.ClientAction;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClientRequest {
    private String sagaId;
    private ClientAction action;
    private Long clientId;
    private String rejectionReason;
    private String name;
    private String email;
    private String cpf;
    private String phone;
    private BigDecimal salary;
    private String street;
    private String number;
    private String complement;
    private String zipCode;
    private String city;
    private String state;
    private Long managerId;
    private Long accountNumber;
}

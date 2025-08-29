package com.dac.bank_account.command.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountCreatedEvent implements Serializable {
    private Long id;
    private Long clientId;
    private String accountNumber;
    private BigDecimal balance;
    private BigDecimal limitAmount;
    private Long managerId;
    private OffsetDateTime creationDate;
}

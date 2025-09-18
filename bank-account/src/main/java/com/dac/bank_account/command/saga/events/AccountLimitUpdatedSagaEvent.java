package com.dac.bank_account.command.saga.events;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class AccountLimitUpdatedSagaEvent {
    private Long clientId;
    private BigDecimal limitAmount;
}

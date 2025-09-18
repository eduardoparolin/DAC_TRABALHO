package com.dac.bank_account.command.events.cqrs;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class AccountLimitChangedEvent {
    String accountNumber;
    BigDecimal newLimit;
}

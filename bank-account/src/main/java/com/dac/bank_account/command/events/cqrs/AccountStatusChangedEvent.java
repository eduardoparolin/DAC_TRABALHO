package com.dac.bank_account.command.events.cqrs;

import com.dac.bank_account.enums.AccountStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AccountStatusChangedEvent {
    private String accountNumber;
    private AccountStatus accountStatus;
}

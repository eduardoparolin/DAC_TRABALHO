package com.dac.bank_account.command.saga.events;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AccountCreatedSagaEvent {
    private Long accountId;
    private Long clientId;
    private Long managerId;
}

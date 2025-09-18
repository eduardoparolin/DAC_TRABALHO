package com.dac.bank_account.command.saga.events;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class AccountCreationFailedSagaEvent {
    private Long clientId;
    private String reason;
}

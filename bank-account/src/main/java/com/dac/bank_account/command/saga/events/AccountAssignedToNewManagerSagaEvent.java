package com.dac.bank_account.command.saga.events;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AccountAssignedToNewManagerSagaEvent {
    private String accountNumber;
    private Long managerId;
}

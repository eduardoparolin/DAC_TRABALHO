package com.dac.bank_account.command.saga.events.success;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class AccountStatusUpdatedSagaEvent {
    private Long clientId;
    private String status;
}

package com.dac.bank_account.command.saga.events.failed;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class AccountStatusUpdateFailedSagaEvent {
    private Long clientId;
    private String reason;
}

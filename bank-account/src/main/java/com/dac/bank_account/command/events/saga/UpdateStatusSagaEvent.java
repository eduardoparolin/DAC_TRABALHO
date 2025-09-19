package com.dac.bank_account.command.events.saga;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateStatusSagaEvent {
    private Long clientId;
    private Boolean isApproved;
}

package com.dac.bank_account.command.saga.events.success;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ManagerReassignedSagaEvent {
    private Long oldManagerId;
}

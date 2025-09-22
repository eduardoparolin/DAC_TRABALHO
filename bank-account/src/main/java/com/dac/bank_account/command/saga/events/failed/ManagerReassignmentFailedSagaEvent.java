package com.dac.bank_account.command.saga.events.failed;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ManagerReassignmentFailedSagaEvent {
    private Long oldManagerId;
    private String reason;
}

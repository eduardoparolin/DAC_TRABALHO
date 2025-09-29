package com.dac.bank_account.config.consumer.events;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AssignNewManagerSagaEvent {
    private Long oldManagerId;
    private Long newManagerId;
}

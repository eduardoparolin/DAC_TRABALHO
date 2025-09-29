package com.dac.bank_account.config.consumer.events;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UpdateLimitSagaEvent {
    private Long clientId;
    private Double salary;
}

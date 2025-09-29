package com.dac.bank_account.config.consumer.events;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CreateAccountSagaEvent {
    private Long clientId;
    private Double salary;
    private Long managerId;
}

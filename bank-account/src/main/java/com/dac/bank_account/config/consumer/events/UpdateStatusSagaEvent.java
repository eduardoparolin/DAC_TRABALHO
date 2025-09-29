package com.dac.bank_account.config.consumer.events;

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

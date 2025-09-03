package com.dac.bank_account.command.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RemovedManagerEvent {
    private Long oldManagerId;
    private Long newManagerId;
}

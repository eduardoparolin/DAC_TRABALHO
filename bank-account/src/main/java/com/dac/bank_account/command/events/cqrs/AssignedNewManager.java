package com.dac.bank_account.command.events.cqrs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AssignedNewManager {
    String accountNumber;
    Long newManagerId;
}

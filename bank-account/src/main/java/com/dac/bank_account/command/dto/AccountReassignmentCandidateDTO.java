package com.dac.bank_account.command.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AccountReassignmentCandidateDTO {
    private Long accountId;
    private Long oldManagerId;
    private String accountNumber;
}

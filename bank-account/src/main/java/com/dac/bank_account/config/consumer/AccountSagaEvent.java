package com.dac.bank_account.config.consumer;

import com.dac.bank_account.enums.AccountAction;
import lombok.Data;

@Data
public class AccountSagaEvent {
    private String sagaId;
    private AccountAction action; // CREATE_ACCOUNT, UPDATE_STATUS, UPDATE_LIMIT, DELETE_MANAGER, NEW_MANAGER
    private Long clientId;
    private Double salary;
    private Long managerId;
    private Boolean isApproved;
    private Long oldManagerId;
    private Long newManagerId;
    private Long accountId;
    private String accountNumber;

}

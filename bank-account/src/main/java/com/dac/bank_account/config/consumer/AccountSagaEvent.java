package com.dac.bank_account.config.consumer;

import com.dac.bank_account.enums.AccountAction;
import lombok.Data;

@Data
public class AccountSagaEvent {
    private AccountAction action; // CREATE_ACCOUNT, UPDATE_STATUS, UPDATE_LIMIT, DELETE_MANAGER, NEW_MANAGER

    private Long clientId;
    private Double salary;
    private Long managerId;
    private Boolean isApproved;
    private Long oldManagerId;
    private Long newManagerId;

    public static AccountSagaEvent createAccount(Long clientId, Double salary, Long managerId) {
        AccountSagaEvent event = new AccountSagaEvent();
        event.action = AccountAction.CREATE_ACCOUNT;
        event.clientId = clientId;
        event.salary = salary;
        event.managerId = managerId;
        return event;
    }

    public static AccountSagaEvent updateStatus(Long clientId, Boolean isApproved) {
        AccountSagaEvent event = new AccountSagaEvent();
        event.action = AccountAction.UPDATE_ACCOUNT_STATUS;
        event.clientId = clientId;
        event.isApproved = isApproved;
        return event;
    }

    public static AccountSagaEvent updateLimit(Long clientId, Double salary) {
        AccountSagaEvent event = new AccountSagaEvent();
        event.action = AccountAction.UPDATE_LIMIT;
        event.clientId = clientId;
        event.salary = salary;
        return event;
    }

    public static AccountSagaEvent deleteManager(Long oldManagerId, Long newManagerId) {
        AccountSagaEvent event = new AccountSagaEvent();
        event.action = AccountAction.DELETE_MANAGER;
        event.oldManagerId = oldManagerId;
        event.newManagerId = newManagerId;
        return event;
    }

    public static AccountSagaEvent newManager(Long oldManagerId, Long newManagerId) {
        AccountSagaEvent event = new AccountSagaEvent();
        event.action = AccountAction.NEW_MANAGER;
        event.oldManagerId = oldManagerId;
        event.newManagerId = newManagerId;
        return event;
    }

}

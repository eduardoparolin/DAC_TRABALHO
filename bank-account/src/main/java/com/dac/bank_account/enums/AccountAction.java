package com.dac.bank_account.enums;

public enum AccountAction {
    CREATE_ACCOUNT,
    UPDATE_ACCOUNT_STATUS,
    UPDATE_LIMIT,
    DELETE_MANAGER,
    NEW_MANAGER,
    FIND_ACCOUNT_FOR_NEW_MANAGER,
    ASSIGN_ACCOUNT_TO_NEW_MANAGER,
    BALANCE_MANAGER_ACCOUNTS,  // Consolidated action for simplified manager creation saga
    ASSIGN_MANAGER_WITH_LEAST_ACCOUNTS  // Find manager with least accounts for new client
}

package com.dac.bank_account.config.consumer.handlers.interfaces;

import com.dac.bank_account.config.consumer.AccountSagaEvent;

public interface AccountMessageHandler {
    void handle(AccountSagaEvent event);
}

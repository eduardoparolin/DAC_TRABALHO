package com.dac.bank_account.config.consumer.factory;

import com.dac.bank_account.config.consumer.handlers.interfaces.AccountMessageHandler;
import com.dac.bank_account.config.consumer.handlers.*;
import com.dac.bank_account.enums.AccountAction;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class MessageHandlerFactory {

    private final Map<AccountAction, AccountMessageHandler> strategies = new HashMap<>();

    public MessageHandlerFactory(
            CreateAccountHandler createHandler,
            UpdateStatusHandler updateStatusHandler,
            UpdateLimitHandler updateLimitHandler,
            DeleteManagerHandler deleteManagerHandler,
            NewManagerHandler newManagerHandler
    ){
        strategies.put(AccountAction.CREATE_ACCOUNT, createHandler);
        strategies.put(AccountAction.UPDATE_ACCOUNT_STATUS, updateStatusHandler);
        strategies.put(AccountAction.UPDATE_LIMIT, updateLimitHandler);
        strategies.put(AccountAction.DELETE_MANAGER, deleteManagerHandler);
        strategies.put(AccountAction.NEW_MANAGER, newManagerHandler);
    }

    public AccountMessageHandler getStrategy(AccountAction action) {
        return strategies.getOrDefault(action,
                data -> {throw new IllegalArgumentException("No handler found for action: " + action);});
    }
}

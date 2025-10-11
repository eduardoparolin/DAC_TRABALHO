package com.dac.bank_account.config.consumer.handlers;

import com.dac.bank_account.command.service.AccountCommandService;
import com.dac.bank_account.config.consumer.handlers.interfaces.AccountMessageHandler;
import com.dac.bank_account.config.consumer.AccountSagaEvent;
import com.dac.bank_account.config.producer.AccountProducer;
import com.dac.bank_account.enums.AccountAction;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DeleteManagerHandler implements AccountMessageHandler {

    private final AccountCommandService accountCommandService;
    private final AccountProducer accountProducer;

    @Override
    public void handle(AccountSagaEvent event){
        try {
            accountCommandService.reassignManager(event.getOldManagerId());
            accountProducer.sendSuccessResult(AccountAction.DELETE_MANAGER);
        } catch (Exception e) {
            accountProducer.sendFailureResult(AccountAction.DELETE_MANAGER, e.getMessage());
        }
    }
}

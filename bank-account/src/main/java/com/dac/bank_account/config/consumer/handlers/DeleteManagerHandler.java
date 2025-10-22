package com.dac.bank_account.config.consumer.handlers;

import com.dac.bank_account.command.dto.request.DeleteManagerDTO;
import com.dac.bank_account.command.service.AccountCommandService;
import com.dac.bank_account.config.consumer.handlers.interfaces.AccountMessageHandler;
import com.dac.bank_account.config.consumer.AccountSagaEvent;
import com.dac.bank_account.config.producer.AccountProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DeleteManagerHandler implements AccountMessageHandler {

    private final AccountCommandService accountCommandService;

    @Override
    public void handle(AccountSagaEvent event) {
        DeleteManagerDTO dto = new DeleteManagerDTO(
                event.getOldManagerId(),
                event.getNewManagerId()
        );
        accountCommandService.reassignManager(dto);
    }
}

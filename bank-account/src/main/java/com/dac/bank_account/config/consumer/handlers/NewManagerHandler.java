package com.dac.bank_account.config.consumer.handlers;

import com.dac.bank_account.command.dto.request.NewManagerDTO;
import com.dac.bank_account.command.service.AccountCommandService;
import com.dac.bank_account.config.consumer.handlers.interfaces.AccountMessageHandler;
import com.dac.bank_account.config.consumer.AccountSagaEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class NewManagerHandler implements AccountMessageHandler {

    private final AccountCommandService accountCommandService;

    @Override
    public void handle(AccountSagaEvent event){
        NewManagerDTO dto = new NewManagerDTO(
                event.getNewManagerId()
        );
        accountCommandService.assignAccountToNewManager(dto);
    }
}

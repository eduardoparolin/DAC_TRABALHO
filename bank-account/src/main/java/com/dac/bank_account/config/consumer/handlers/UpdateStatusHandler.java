package com.dac.bank_account.config.consumer.handlers;

import com.dac.bank_account.command.dto.request.UpdateStatusDTO;
import com.dac.bank_account.command.service.AccountCommandService;
import com.dac.bank_account.config.consumer.handlers.interfaces.AccountMessageHandler;
import com.dac.bank_account.config.consumer.AccountSagaEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UpdateStatusHandler implements AccountMessageHandler {

    private final AccountCommandService accountCommandService;


    @Override
    public void handle(AccountSagaEvent event) {
        UpdateStatusDTO dto = new UpdateStatusDTO(
                event.getClientId(),
                event.getIsApproved()
        );
            accountCommandService.updateAccountStatus(dto);
    }
}

package com.dac.bank_account.config.consumer.handlers;

import com.dac.bank_account.command.dto.request.CreateAccountDTO;
import com.dac.bank_account.command.entity.Account;
import com.dac.bank_account.command.service.AccountCommandService;
import com.dac.bank_account.config.consumer.handlers.interfaces.AccountMessageHandler;
import com.dac.bank_account.config.consumer.AccountSagaEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CreateAccountHandler implements AccountMessageHandler {

    private final AccountCommandService accountCommandService;

    @Override
    public void handle(AccountSagaEvent event) {
        CreateAccountDTO dto = new CreateAccountDTO(
                event.getClientId(),
                event.getSalary(),
                event.getManagerId());
        Account account = accountCommandService.createAccount(dto);
        event.setManagerId(account.getManagerId());
        event.setAccountId(account.getId());
        event.setAccountNumber(account.getAccountNumber());
    }
}

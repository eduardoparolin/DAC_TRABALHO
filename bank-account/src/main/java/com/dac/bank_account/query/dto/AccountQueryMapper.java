package com.dac.bank_account.query.dto;

import com.dac.bank_account.command.events.AccountCreatedEvent;
import com.dac.bank_account.query.entity.AccountView;
import org.springframework.stereotype.Component;

@Component
public class AccountQueryMapper {

    public AccountView toEntity(AccountCreatedEvent event) {
        AccountView account = new AccountView();
        account.setId(event.getId());
        account.setClientId(event.getClientId());
        account.setBalance(event.getBalance());
        account.setAccountNumber(event.getAccountNumber());
        account.setLimitAmount(event.getLimitAmount());
        account.setManagerId(event.getManagerId());
        account.setCreationDate(event.getCreationDate());
        
        return account;
    }
}

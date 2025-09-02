package com.dac.bank_account.query.dto;

import com.dac.bank_account.command.events.AccountCreatedEvent;
import com.dac.bank_account.command.events.MoneyTransactionEvent;
import com.dac.bank_account.query.entity.AccountView;
import com.dac.bank_account.query.entity.TransactionView;
import org.springframework.stereotype.Component;

import java.util.Optional;

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

    public TransactionView toEntity(MoneyTransactionEvent event) {
        TransactionView tx = new TransactionView();
        tx.setSourceAccountNumber(event.getAccountNumber());
        tx.setAmount(event.getAmount());
        tx.setType(event.getTransactionType());
        tx.setDateTime(event.getTimestamp());
        tx.setTargetAccountNumber(Optional.ofNullable(event.getTargetAccountNumber()).orElse(null));

        return tx;
    }
}

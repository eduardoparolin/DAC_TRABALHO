package com.dac.bank_account.query.dto;

import com.dac.bank_account.command.events.AccountCreatedEvent;
import com.dac.bank_account.command.events.MoneyTransactionEvent;
import com.dac.bank_account.query.entity.AccountView;
import com.dac.bank_account.query.entity.TransactionView;
import org.springframework.stereotype.Component;

import java.util.List;
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

    public BalanceResponseDTO toBalanceResponseDTO(AccountView account) {
        return new BalanceResponseDTO(
                account.getClientId().toString(),
                account.getAccountNumber(),
                account.getBalance()
        );
    }

    public AccountResponseDTO toAccountResponseDTO(AccountView account) {
        return new AccountResponseDTO(
                account.getClientId().toString(),
                account.getAccountNumber(),
                account.getBalance(),
                account.getLimitAmount(),
                account.getManagerId().toString(),
                account.getCreationDate().toString()
        );
    }

    public TransactionResponseDTO toTransactionResponseDTO(TransactionView transaction) {
        return new TransactionResponseDTO(
                transaction.getDateTime().toString(),
                transaction.getType().toString(),
                transaction.getSourceAccountNumber(),
                transaction.getTargetAccountNumber(),
                transaction.getAmount()
        );
    }

    public StatementResponseDTO toStatementResponseDTO(AccountView account, List<TransactionView> transactions) {
        List<TransactionResponseDTO> statements = transactions.stream()
                .map(this::toTransactionResponseDTO)
                .toList();

        return new StatementResponseDTO(
                account.getAccountNumber(),
                account.getBalance(),
                statements
        );
    }

    public List<AccountResponseDTO> toAccountResponseDTOList(List<AccountView> accounts) {
        return accounts.stream()
                .map(this::toAccountResponseDTO)
                .toList();
    }

    public ManagerAccountsResponseDTO toManagerAccountsResponseDTO(String managerId, List<AccountView> accounts) {
        return new ManagerAccountsResponseDTO(
                managerId,
                toAccountResponseDTOList(accounts)
        );
    }
}

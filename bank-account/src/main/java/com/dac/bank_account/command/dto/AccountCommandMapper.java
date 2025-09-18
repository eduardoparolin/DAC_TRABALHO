package com.dac.bank_account.command.dto;

import com.dac.bank_account.command.dto.response.MovementResponseDTO;
import com.dac.bank_account.command.dto.response.TransferResponseDTO;
import com.dac.bank_account.command.entity.Account;
import com.dac.bank_account.command.entity.Transaction;
import com.dac.bank_account.command.events.cqrs.*;
import com.dac.bank_account.enums.TransactionType;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class AccountCommandMapper {

    public Transaction toEntity(String sourceAccount, TransactionType type, BigDecimal amount, String targetAccount) {
        Transaction transaction = new Transaction();
        transaction.setSourceAccountNumber(sourceAccount);
        transaction.setTargetAccountNumber(targetAccount);
        transaction.setType(type);
        transaction.setAmount(amount);
        transaction.setDateTime(OffsetDateTime.now());
        return transaction;
    }

    public MovementResponseDTO toMovementDTO(Account account) {
        return new MovementResponseDTO(
                account.getAccountNumber(),
                OffsetDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX")),
                account.getBalance().doubleValue()
        );
    }

    public TransferResponseDTO toTransferDTO(Account source, Account target, BigDecimal amount) {
        return new TransferResponseDTO(
                source.getAccountNumber(),
                OffsetDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX")),
                target.getAccountNumber(),
                source.getBalance().doubleValue(),
                amount.doubleValue()
        );
    }

    public AccountCreatedEvent accountToCreatedEvent(Account account) {
        return new AccountCreatedEvent(
                account.getId(),
                account.getClientId(),
                account.getAccountNumber(),
                account.getBalance(),
                account.getLimitAmount(),
                account.getManagerId(),
                account.getCreationDate()
        );
    }

    public MoneyTransactionEvent toMoneyTransactionEvent(Account account, BigDecimal amount, Transaction transaction) {
        return new MoneyTransactionEvent(
                account.getAccountNumber(),
                amount,
                transaction.getType(),
                transaction.getDateTime()
        );
    }

    public MoneyTransactionEvent toMoneyTransferEvent(Account source, Account target, BigDecimal amount, Transaction transaction) {
        return new MoneyTransactionEvent(
                source.getAccountNumber(),
                target.getAccountNumber(),
                amount,
                transaction.getDateTime()
        );
    }

    public RemovedManagerEvent toRemovedManagerEvent(Long oldManagerId, Long newManagerId) {
        return new RemovedManagerEvent(
                oldManagerId,
                newManagerId
        );
    }

    public AccountLimitChangedEvent accountToLimitChangedEvent(Account account) {
        return new AccountLimitChangedEvent(
                account.getAccountNumber(),
                account.getLimitAmount()
        );
    }

    public AssignedNewManager toAssignedNewManager(String accountNumber, Long newManagerId) {
        return new AssignedNewManager(
                accountNumber,
                newManagerId
        );
    }
}

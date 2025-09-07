package com.dac.bank_account.command.dto;

import com.dac.bank_account.command.dto.request.AccountRequestDTO;
import com.dac.bank_account.command.dto.response.AccountResponseDTO;
import com.dac.bank_account.command.dto.response.MovementResponseDTO;
import com.dac.bank_account.command.dto.response.TransferResponseDTO;
import com.dac.bank_account.command.entity.Account;
import com.dac.bank_account.command.entity.Transaction;
import com.dac.bank_account.command.events.AccountCreatedEvent;
import com.dac.bank_account.command.events.AccountLimitChangedEvent;
import com.dac.bank_account.command.events.MoneyTransactionEvent;
import com.dac.bank_account.command.events.RemovedManagerEvent;
import com.dac.bank_account.enums.TransactionType;
import com.dac.bank_account.command.repository.AccountCommandRepository;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

@Component
public class AccountCommandMapper {

    private final AccountCommandRepository accountRepository;

    public AccountCommandMapper(AccountCommandRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public Account toEntity(AccountRequestDTO dto) {
        Account account = new Account();
        account.setClientId(dto.clientId());
        account.setAccountNumber(generateAccountNumber());
        account.setBalance(BigDecimal.ZERO);
        account.setLimitAmount(BigDecimal.valueOf(dto.salary()));
        account.setManagerId(dto.managerId());
        account.setCreationDate(OffsetDateTime.now());
        return account;
    }

    public Transaction toEntity(String sourceAccount, TransactionType type, BigDecimal amount, String targetAccount) {
        Transaction transaction = new Transaction();
        transaction.setSourceAccountNumber(sourceAccount);
        transaction.setTargetAccountNumber(targetAccount);
        transaction.setType(type);
        transaction.setAmount(amount);
        transaction.setDateTime(OffsetDateTime.now());
        return transaction;
    }

    public AccountResponseDTO accountToDTO(Account account) {
        return new AccountResponseDTO(
                account.getClientId().toString(),
                account.getAccountNumber(),
                account.getBalance().doubleValue(),
                account.getLimitAmount().doubleValue(),
                account.getManagerId().toString(),
                account.getCreationDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX"))
        );
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

    public String generateAccountNumber() {
        Random random = new Random();
        String number;
        do {
            number = String.valueOf(1000 + random.nextInt(9000));
        } while (accountRepository.existsByAccountNumber(number));
        return number;
    }


    public AccountLimitChangedEvent accountToLimitChangedEvent(Account account) {
        return new AccountLimitChangedEvent(
                account.getAccountNumber(),
                account.getLimitAmount()
        );
    }
}

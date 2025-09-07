package com.dac.bank_account.command.service;

import com.dac.bank_account.command.dto.*;
import com.dac.bank_account.command.dto.request.AccountRequestDTO;
import com.dac.bank_account.command.dto.response.AccountResponseDTO;
import com.dac.bank_account.command.dto.response.MovementResponseDTO;
import com.dac.bank_account.command.dto.response.TransferResponseDTO;
import com.dac.bank_account.command.entity.Account;
import com.dac.bank_account.command.events.*;
import com.dac.bank_account.enums.TransactionType;
import com.dac.bank_account.command.repository.AccountCommandRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class AccountCommandService {
    private final AccountCommandRepository accountCommandRepository;
    private final AccountCommandMapper accountMapper;
    private final EventPublisher eventPublisher;

    public AccountCommandService(AccountCommandRepository accountCommandRepository, AccountCommandMapper accountMapper, EventPublisher eventPublisher) {
        this.accountCommandRepository = accountCommandRepository;
        this.accountMapper = accountMapper;
        this.eventPublisher = eventPublisher;
    }

    @Transactional("commandTransactionManager")
    public AccountResponseDTO createAccount(AccountRequestDTO dto) {
        Account account = accountMapper.toEntity(dto);

        double limit = dto.salary().doubleValue() / 2;
        account.setLimitAmount(BigDecimal.valueOf(limit));

        accountCommandRepository.save(account);

        AccountCreatedEvent event = accountMapper.accountToCreatedEvent(account);

        eventPublisher.publishEvent("bank.account", event);

        return accountMapper.accountToDTO(account);
    }

    @Transactional("commandTransactionManager")
    public MovementResponseDTO deposit(String accountNumber, Double amount) {
        Account account = accountCommandRepository.findByAccountNumber(accountNumber)
                        .orElseThrow(() -> new IllegalArgumentException("Account not found with account number: " + accountNumber));
        account.deposit(BigDecimal.valueOf(amount));

        var transaction = accountMapper.toEntity(account.getAccountNumber(), TransactionType.DEPOSITO, BigDecimal.valueOf(amount), null);
        account.getTransactions().add(transaction);
        accountCommandRepository.save(account);

        MoneyTransactionEvent event = accountMapper.toMoneyTransactionEvent(account, BigDecimal.valueOf(amount), transaction);

        eventPublisher.publishEvent("bank.account", event);

        return accountMapper.toMovementDTO(account);
    }

    @Transactional("commandTransactionManager")
    public MovementResponseDTO withdraw(String accountNumber, Double amount) {
        Account account = accountCommandRepository.findByAccountNumber(accountNumber)
                        .orElseThrow(() -> new IllegalArgumentException("Account not found with account number: " + accountNumber));
        account.withdraw(BigDecimal.valueOf(amount));

        var transaction = accountMapper.toEntity(account.getAccountNumber(), TransactionType.SAQUE, BigDecimal.valueOf(amount), null);
        account.getTransactions().add(transaction);
        accountCommandRepository.save(account);

        MoneyTransactionEvent event = accountMapper.toMoneyTransactionEvent(account, BigDecimal.valueOf(amount), transaction);

        eventPublisher.publishEvent("bank.account", event);

        return accountMapper.toMovementDTO(account);
    }

    @Transactional("commandTransactionManager")
    public TransferResponseDTO transfer(String sourceAccountNumber, Double amount, String targetAccountNumber) {
        Account source = accountCommandRepository.findByAccountNumber(sourceAccountNumber)
                .orElseThrow(() -> new IllegalArgumentException("Source account not found with account number: " + sourceAccountNumber));
        Account target = accountCommandRepository.findByAccountNumber(targetAccountNumber)
                .orElseThrow(() -> new IllegalArgumentException("Target account not found with account number: " + targetAccountNumber));

        source.withdraw(BigDecimal.valueOf(amount));
        target.deposit(BigDecimal.valueOf(amount));

        var transaction = accountMapper.toEntity(source.getAccountNumber(), TransactionType.TRANSFERENCIA, BigDecimal.valueOf(amount), target.getAccountNumber());
        source.getTransactions().add(transaction);
        accountCommandRepository.save(source);
        accountCommandRepository.save(target);

        MoneyTransactionEvent event = accountMapper.toMoneyTransferEvent(source, target, BigDecimal.valueOf(amount), transaction);

        eventPublisher.publishEvent("bank.account", event);

        return accountMapper.toTransferDTO(source, target, BigDecimal.valueOf(amount));

    }

    @Transactional("commandTransactionManager")
    public AccountResponseDTO setLimit(String accountNumber, Double salario) {
        Account account = accountCommandRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new IllegalArgumentException("Account not found with account number: " + accountNumber));
        double limit = salario / 2;
        account.setLimitAmount(BigDecimal.valueOf(limit));
        accountCommandRepository.save(account);

        AccountLimitChangedEvent event = accountMapper.accountToLimitChangedEvent(account);

        eventPublisher.publishEvent("bank.account", event);


        return accountMapper.accountToDTO(account);
    }

    @Transactional("commandTransactionManager")
    public void reassignManager(Long oldManagerId, Long newManagerId) {
        int updated = accountCommandRepository.updateManagerForAccounts(oldManagerId, newManagerId);
        if (updated == 0) {
            throw new IllegalArgumentException("No accounts found for the given old manager ID: " + oldManagerId);
        }
        RemovedManagerEvent event = accountMapper.toRemovedManagerEvent(oldManagerId, newManagerId);
        eventPublisher.publishEvent("bank.account", event );

    }
}

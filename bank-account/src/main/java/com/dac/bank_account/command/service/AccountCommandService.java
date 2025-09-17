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
import com.dac.bank_account.exception.InsufficientFundsException;
import com.dac.bank_account.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

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

        double limit = dto.salary() / 2;
        account.setLimitAmount(BigDecimal.valueOf(limit));

        accountCommandRepository.save(account);

        AccountCreatedEvent event = accountMapper.accountToCreatedEvent(account);

        eventPublisher.publishEvent("bank.account", event);

        return accountMapper.accountToDTO(account);
    }

    @Transactional("commandTransactionManager")
    public MovementResponseDTO deposit(String accountNumber, Double amount) {
        Account account = accountCommandRepository.findByAccountNumber(accountNumber)
                        .orElseThrow(() -> new ResourceNotFoundException("Account not found with account number: " + accountNumber));
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
                        .orElseThrow(() -> new ResourceNotFoundException("Account not found with account number: " + accountNumber));

        if(amount > account.getBalance().doubleValue() + account.getLimitAmount().doubleValue()) {
            throw new InsufficientFundsException("Insufficient funds for withdrawal in account number: " + accountNumber);
        }

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
                .orElseThrow(() -> new ResourceNotFoundException("Source account not found with account number: " + sourceAccountNumber));
        Account target = accountCommandRepository.findByAccountNumber(targetAccountNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Target account not found with account number: " + targetAccountNumber));

        if(amount > source.getBalance().doubleValue() + source.getLimitAmount().doubleValue()) {
            throw new InsufficientFundsException("Insufficient funds for transfer in account number: " + sourceAccountNumber);
        }

        source.withdraw(BigDecimal.valueOf(amount));
        target.deposit(BigDecimal.valueOf(amount));

        var transaction = accountMapper.toEntity(source.getAccountNumber(), TransactionType.TRANSFERENCIA, BigDecimal.valueOf(amount), target.getAccountNumber());
        source.getTransactions().add(transaction);
        target.getTransactions().add(transaction);

        accountCommandRepository.save(source);
        accountCommandRepository.save(target);

        MoneyTransactionEvent event = accountMapper.toMoneyTransferEvent(source, target, BigDecimal.valueOf(amount), transaction);

        eventPublisher.publishEvent("bank.account", event);

        return accountMapper.toTransferDTO(source, target, BigDecimal.valueOf(amount));

    }

    @Transactional("commandTransactionManager")
    public AccountResponseDTO setLimit(String accountNumber, Double salary) {
        Account account = accountCommandRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with account number: " + accountNumber));
        double limit = salary / 2;
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
            throw new ResourceNotFoundException("No accounts found for the given old manager ID: " + oldManagerId);
        }
        RemovedManagerEvent event = accountMapper.toRemovedManagerEvent(oldManagerId, newManagerId);
        eventPublisher.publishEvent("bank.account", event );

    }

    @Transactional("commandTransactionManager")
    public Optional<AccountResponseDTO> assignAccountToNewManager(Long newManagerId) {
        Optional<Account> optionalAccount = accountCommandRepository.findAccountWithLowestPositiveBalanceFromTopManagers();

        if (optionalAccount.isEmpty()) {
            return  Optional.empty();
        }

        Account chosen = optionalAccount.get();
        Long previousManagerId = chosen.getManagerId();

        long countAccounts = accountCommandRepository.countByManagerId(previousManagerId);
        if (countAccounts == 1) {
            return Optional.empty();
        }

        chosen.setManagerId(newManagerId);
        accountCommandRepository.save(chosen);

        AssignedNewManager event = accountMapper.toAssignedNewManager(chosen.getAccountNumber(), newManagerId);

        eventPublisher.publishEvent("bank.account", event);

        return Optional.of(accountMapper.accountToDTO(chosen));
    }
}

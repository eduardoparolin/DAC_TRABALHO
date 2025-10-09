package com.dac.bank_account.command.service;

import com.dac.bank_account.command.dto.*;
import com.dac.bank_account.command.dto.response.MovementResponseDTO;
import com.dac.bank_account.command.dto.response.TransferResponseDTO;
import com.dac.bank_account.command.entity.Account;
import com.dac.bank_account.command.events.*;
import com.dac.bank_account.command.events.cqrs.*;
import com.dac.bank_account.enums.AccountStatus;
import com.dac.bank_account.enums.TransactionType;
import com.dac.bank_account.command.repository.AccountCommandRepository;
import com.dac.bank_account.exception.AccountAlreadyExistsException;
import com.dac.bank_account.exception.InsufficientFundsException;
import com.dac.bank_account.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.Random;

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
    public void createAccount(Long clientId, Double salary, Long managerId) {

        if(accountCommandRepository.findByClientId(clientId).isPresent()) {
            throw new AccountAlreadyExistsException("Account already exists for client id: " + clientId);
        }
        Account account = new Account();
        account.setClientId(clientId);
        account.setAccountNumber(generateAccountNumber());
        account.setBalance(BigDecimal.ZERO);
        account.setManagerId(managerId);
        account.setCreationDate(OffsetDateTime.now());
        account.setStatus(AccountStatus.PENDENTE);

        if(salary >= 2000){
            account.setLimitAmount(BigDecimal.valueOf(salary / 2));
        }else{
            account.setLimitAmount(BigDecimal.ZERO);
        }

        accountCommandRepository.save(account);

        AccountCreatedEvent event = accountMapper.accountToCreatedEvent(account);
        eventPublisher.publishEvent("bank.account", event);

    }

    @Transactional("commandTransactionManager")
    public void updateAccountStatus(Long clientId, Boolean approved) {
        Account account = accountCommandRepository.findByClientId(clientId)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with client id: " + clientId));

        if(approved){
            account.setStatus(AccountStatus.ATIVA);
        }else{
            account.setStatus(AccountStatus.REJEITADA);
        }
        accountCommandRepository.save(account);

        AccountStatusChangedEvent event = accountMapper.accountToStatusChangedEvent(account);
        eventPublisher.publishEvent("bank.account", event);
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
    public void setLimit(Long clientId, Double salary) {
        Account account = accountCommandRepository.findByClientId(clientId)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with client id: " + clientId));

        if(salary != null && salary >= 2000){
            account.setLimitAmount(BigDecimal.valueOf(salary / 2));
        }else{
            account.setLimitAmount(BigDecimal.ZERO);
        }
        accountCommandRepository.save(account);

        AccountLimitChangedEvent event = accountMapper.accountToLimitChangedEvent(account);
        eventPublisher.publishEvent("bank.account", event);
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
    public void assignAccountToNewManager(Long oldManagerId, Long newManagerId) {
        Account account = accountCommandRepository.
                findFirstByManagerIdAndBalanceGreaterThanOrderByBalanceAsc(oldManagerId, BigDecimal.ZERO)
                .orElseThrow(() -> new ResourceNotFoundException("No account with positive balance found for the given old manager ID: " + oldManagerId));

        account.setManagerId(newManagerId);
        accountCommandRepository.save(account);

        AssignedNewManager event = accountMapper.toAssignedNewManager(account.getAccountNumber(), newManagerId);
        eventPublisher.publishEvent("bank.account", event);
    }

    public String generateAccountNumber() {
        Random random = new Random();
        String number;
        do {
            number = String.valueOf(1000 + random.nextInt(9000));
        } while (accountCommandRepository.existsByAccountNumber(number));
        return number;
    }

}

package com.dac.bank_account.command.service;

import com.dac.bank_account.command.dto.*;
import com.dac.bank_account.command.dto.request.*;
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
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

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
    public Long createAccount(CreateAccountDTO dto) {
        Long clientId = dto.getClientId();
        Double salary = dto.getSalary();
        Long managerId = accountCommandRepository.findManagerWithLeastAccounts();
        if (managerId == null){
            throw new ResourceNotFoundException("Not found manager to assign account");
        }
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

        return account.getManagerId();
    }

    @Transactional("commandTransactionManager")
    public void updateAccountStatus(UpdateStatusDTO dto) {
        Long clientId = dto.getClientId();
        Boolean approved = dto.getIsApproved();
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
    public void setLimit(UpdateLimitDTO dto) {
        Long clientId = dto.getClientId();
        Double salary = dto.getSalary();
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
    public void reassignManager(DeleteManagerDTO dto) {
        Long oldManagerId = dto.getOldManagerId();
        Long newManagerId = dto.getNewManagerId();
        int updated = accountCommandRepository.updateManagerForAccounts(oldManagerId, newManagerId);
        if (updated == 0) {
            throw new ResourceNotFoundException("No accounts found for the given old manager ID: " + oldManagerId);
        }
        RemovedManagerEvent event = accountMapper.toRemovedManagerEvent(oldManagerId, newManagerId);
        eventPublisher.publishEvent("bank.account", event );

    }

    @Transactional("commandTransactionManager")
    public void assignAccountToNewManager(NewManagerDTO dto) {
        Long newManagerId = dto.getNewManagerId();

        Map<Long, Long> managerAccountsCount = accountCommandRepository.findManagerAccountCountsRaw().stream()
                .collect(Collectors.toMap(
                        row -> (Long) row[0],
                        row -> (Long) row[1]
                ));

        if(managerAccountsCount.isEmpty()){
            return;
        }

        if(managerAccountsCount.size() == 1){
            Long onlyManagerId = managerAccountsCount.keySet().iterator().next();
            Long count = managerAccountsCount.get(onlyManagerId);

            if(count <= 1){
                return;
            }
        }

        boolean allManagerHaveOneAccount = managerAccountsCount.values().stream()
                .allMatch(count -> count == 1);

        if(allManagerHaveOneAccount){
            return;
        }

        Long oldManagerId = accountCommandRepository.findManagerWithMostAccountsAndLowestBalance();

        if(oldManagerId == null){
            return;
        }

        Optional<Account> accountToTransfer = accountCommandRepository.findAll()
                .stream()
                .filter(a -> a.getManagerId().equals(oldManagerId))
                .filter(a -> a.getStatus().equals(AccountStatus.ATIVA))
                .findFirst();

        if(accountToTransfer.isEmpty()){
            return;
        }

        Account account = accountToTransfer.get();
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

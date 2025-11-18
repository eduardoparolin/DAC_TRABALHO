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
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

@Service
public class AccountCommandService {
    private final AccountCommandRepository accountCommandRepository;
    private final AccountCommandMapper accountMapper;
    private final EventPublisher eventPublisher;

    public AccountCommandService(AccountCommandRepository accountCommandRepository, AccountCommandMapper accountMapper,
            EventPublisher eventPublisher) {
        this.accountCommandRepository = accountCommandRepository;
        this.accountMapper = accountMapper;
        this.eventPublisher = eventPublisher;
    }

    @Transactional("commandTransactionManager")
    public Account createAccount(CreateAccountDTO dto) {
        Long clientId = dto.getClientId();
        Double salary = dto.getSalary();
        Long managerId = dto.getManagerId();
        if (managerId == null) {
            throw new ResourceNotFoundException("Manager ID not provided");
        }
        if (accountCommandRepository.findByClientId(clientId).isPresent()) {
            throw new AccountAlreadyExistsException("Account already exists for client id: " + clientId);
        }
        Account account = new Account();
        account.setClientId(clientId);
        account.setAccountNumber(generateAccountNumber());
        account.setBalance(BigDecimal.ZERO);
        account.setManagerId(managerId);
        account.setCreationDate(OffsetDateTime.now());
        account.setStatus(AccountStatus.PENDENTE);

        if (salary >= 2000) {
            account.setLimitAmount(BigDecimal.valueOf(salary / 2));
        } else {
            account.setLimitAmount(BigDecimal.ZERO);
        }

        account = accountCommandRepository.save(account);

        AccountCreatedEvent event = accountMapper.accountToCreatedEvent(account);
        eventPublisher.publishEvent("bank.account", event);

        return account;
    }

    @Transactional("commandTransactionManager")
    public void updateAccountStatus(UpdateStatusDTO dto) {
        Long clientId = dto.getClientId();
        Boolean approved = dto.getIsApproved();
        Account account = accountCommandRepository.findByClientId(clientId)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with client id: " + clientId));

        if (approved) {
            account.setStatus(AccountStatus.ATIVA);
        } else {
            account.setStatus(AccountStatus.REJEITADA);
        }
        accountCommandRepository.save(account);

        AccountStatusChangedEvent event = accountMapper.accountToStatusChangedEvent(account);
        eventPublisher.publishEvent("bank.account", event);
    }

    @Transactional("commandTransactionManager")
    public MovementResponseDTO deposit(String accountNumber, Double amount) {
        Account account = getAccountByNumber(accountNumber);

        account.deposit(BigDecimal.valueOf(amount));

        var transaction = accountMapper.toEntity(account.getAccountNumber(), TransactionType.DEPOSITO,
                BigDecimal.valueOf(amount), null);
        account.getTransactions().add(transaction);
        accountCommandRepository.save(account);

        MoneyTransactionEvent event = accountMapper.toMoneyTransactionEvent(account, BigDecimal.valueOf(amount),
                transaction);

        eventPublisher.publishEvent("bank.account", event);

        return accountMapper.toMovementDTO(account);
    }

    @Transactional("commandTransactionManager")
    public MovementResponseDTO withdraw(String accountNumber, Double amount) {
        Account account = getAccountByNumber(accountNumber);

        if (amount > account.getBalance().doubleValue() + account.getLimitAmount().doubleValue()) {
            throw new InsufficientFundsException(
                    "Insufficient funds for withdrawal in account number: " + accountNumber);
        }

        account.withdraw(BigDecimal.valueOf(amount));

        var transaction = accountMapper.toEntity(account.getAccountNumber(), TransactionType.SAQUE,
                BigDecimal.valueOf(amount), null);
        account.getTransactions().add(transaction);
        accountCommandRepository.save(account);

        MoneyTransactionEvent event = accountMapper.toMoneyTransactionEvent(account, BigDecimal.valueOf(amount),
                transaction);

        eventPublisher.publishEvent("bank.account", event);

        return accountMapper.toMovementDTO(account);
    }

    @Transactional("commandTransactionManager")
    public TransferResponseDTO transfer(String sourceAccountNumber, Double amount, String targetAccountNumber) {
        Account source = getAccountByNumber(sourceAccountNumber);
        Account target = getAccountByNumber(targetAccountNumber);

        if (amount > source.getBalance().doubleValue() + source.getLimitAmount().doubleValue()) {
            throw new InsufficientFundsException(
                    "Insufficient funds for transfer in account number: " + sourceAccountNumber);
        }

        source.withdraw(BigDecimal.valueOf(amount));
        target.deposit(BigDecimal.valueOf(amount));

        var transaction = accountMapper.toEntity(source.getAccountNumber(), TransactionType.TRANSFERENCIA,
                BigDecimal.valueOf(amount), target.getAccountNumber());
        source.getTransactions().add(transaction);
        target.getTransactions().add(transaction);

        accountCommandRepository.save(source);
        accountCommandRepository.save(target);

        MoneyTransactionEvent event = accountMapper.toMoneyTransferEvent(source, target, BigDecimal.valueOf(amount),
                transaction);

        eventPublisher.publishEvent("bank.account", event);

        return accountMapper.toTransferDTO(source, target, BigDecimal.valueOf(amount));

    }

    @Transactional("commandTransactionManager")
    public void setLimit(UpdateLimitDTO dto) {
        Long clientId = dto.getClientId();
        Double salary = dto.getSalary();
        Account account = accountCommandRepository.findByClientId(clientId)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with client id: " + clientId));

        BigDecimal calculatedLimit;
        if (salary != null && salary >= 2000) {
            calculatedLimit = BigDecimal.valueOf(salary / 2);
        } else {
            calculatedLimit = BigDecimal.ZERO;
        }

        // Business rule: if new limit < current negative balance, adjust limit to negative balance
        BigDecimal currentBalance = account.getBalance();
        if (currentBalance.compareTo(BigDecimal.ZERO) < 0) {
            BigDecimal negativeBalance = currentBalance.abs();
            if (calculatedLimit.compareTo(negativeBalance) < 0) {
                calculatedLimit = negativeBalance;
            }
        }

        account.setLimitAmount(calculatedLimit);
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
        eventPublisher.publishEvent("bank.account", event);

    }

    @Transactional("commandTransactionManager")
    public Map<String, Long> assignAccountToNewManager(NewManagerDTO dto) {
        Long newManagerId = dto.getNewManagerId();
        Long requestedOldManagerId = dto.getOldManagerId();
        Long requestedAccountId = dto.getAccountId();

        if (requestedOldManagerId != null && requestedAccountId != null) {
            Account account = accountCommandRepository.findById(requestedAccountId)
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Account not found with id: " + requestedAccountId));

            if (!account.getManagerId().equals(requestedOldManagerId)) {
                throw new ResourceNotFoundException(
                        "Account " + requestedAccountId + " is not managed by manager id: " + requestedOldManagerId);
            }

            account.setManagerId(newManagerId);
            accountCommandRepository.save(account);

            AssignedNewManager event = accountMapper.toAssignedNewManager(account.getAccountNumber(), newManagerId);
            eventPublisher.publishEvent("bank.account", event);

            return Map.of(
                    "oldManagerId", requestedOldManagerId,
                    "newManagerId", newManagerId,
                    "accountId", account.getId());
        }

        Optional<Account> accountToTransfer = findEligibleAccountForReassignment();

        if (accountToTransfer.isEmpty()) {
            return Map.of();
        }

        Account account = accountToTransfer.get();
        Long oldManagerId = account.getManagerId();

        account.setManagerId(newManagerId);
        accountCommandRepository.save(account);

        AssignedNewManager event = accountMapper.toAssignedNewManager(account.getAccountNumber(), newManagerId);
        eventPublisher.publishEvent("bank.account", event);

        return Map.of(
                "oldManagerId", oldManagerId,
                "newManagerId", newManagerId,
                "accountId", account.getId());
    }

    public Optional<AccountReassignmentCandidateDTO> findAccountForNewManager() {
        return findEligibleAccountForReassignment()
                .map(account -> new AccountReassignmentCandidateDTO(
                        account.getId(),
                        account.getManagerId(),
                        account.getAccountNumber()
                ));
    }

    /**
     * Consolidated method for balancing manager accounts when a new manager is created.
     * This method handles all the logic for determining if an account should be reassigned
     * and performs the reassignment in a single transaction.
     *
     * Logic:
     * 1. Check if there are existing managers with >1 account
     * 2. If not, return empty (no rebalancing needed)
     * 3. Find manager with most accounts (tie-breaker: lowest total balance)
     * 4. Find account with lowest positive balance from that manager
     * 5. Reassign that account to the new manager
     *
     * @param newManagerId The ID of the newly created manager
     * @return Map with oldManagerId and accountId if reassignment occurred, empty otherwise
     */
    @Transactional("commandTransactionManager")
    public Map<String, Long> balanceManagerAccounts(Long newManagerId) {
        // Get all manager account counts (only ACTIVE accounts with positive balance)
        Map<Long, Long> managerAccountsCount = accountCommandRepository.findManagerAccountCountsRaw().stream()
                .collect(Collectors.toMap(
                        row -> (Long) row[0],
                        row -> (Long) row[1]));

        // No managers with accounts - no rebalancing needed
        if (managerAccountsCount.isEmpty()) {
            return Map.of();
        }

        // Only one manager exists (should be the newly created one) - no rebalancing needed
        if (managerAccountsCount.size() == 1) {
            return Map.of();
        }

        // Check if any manager has more than 1 account
        boolean anyManagerHasMultipleAccounts = managerAccountsCount.values().stream()
                .anyMatch(count -> count > 1);

        if (!anyManagerHasMultipleAccounts) {
            return Map.of();
        }

        // Find manager with most accounts (positive balance only)
        // Tie-breaker: manager with lowest total positive balance
        Long oldManagerId = accountCommandRepository.findManagerWithMostAccountsAndLowestBalance();

        if (oldManagerId == null) {
            return Map.of();
        }

        // Find account with lowest positive balance from that manager
        Optional<Account> accountToTransfer = accountCommandRepository.findAccountWithLowestPositiveBalance(oldManagerId);

        if (accountToTransfer.isEmpty()) {
            return Map.of();
        }

        Account account = accountToTransfer.get();

        // Reassign account to new manager
        account.setManagerId(newManagerId);
        accountCommandRepository.save(account);

        // Publish event
        AssignedNewManager event = accountMapper.toAssignedNewManager(account.getAccountNumber(), newManagerId);
        eventPublisher.publishEvent("bank.account", event);

        return Map.of(
                "oldManagerId", oldManagerId,
                "newManagerId", newManagerId,
                "accountId", account.getId());
    }

    private Optional<Account> findEligibleAccountForReassignment() {
        Map<Long, Long> managerAccountsCount = accountCommandRepository.findManagerAccountCountsRaw().stream()
                .collect(Collectors.toMap(
                        row -> (Long) row[0],
                        row -> (Long) row[1]));

        if (managerAccountsCount.isEmpty()) {
            return Optional.empty();
        }

        if (managerAccountsCount.size() == 1) {
            Long onlyManagerId = managerAccountsCount.keySet().iterator().next();
            Long count = managerAccountsCount.get(onlyManagerId);

            if (count <= 1) {
                return Optional.empty();
            }
        }

        boolean allManagerHaveOneAccount = managerAccountsCount.values().stream()
                .allMatch(count -> count == 1);

        if (allManagerHaveOneAccount) {
            return Optional.empty();
        }

        Long oldManagerId = accountCommandRepository.findManagerWithMostAccountsAndLowestBalance();

        if (oldManagerId == null) {
            return Optional.empty();
        }

        // Use new method to find account with lowest positive balance
        return accountCommandRepository.findAccountWithLowestPositiveBalance(oldManagerId);
    }

    /**
     * Finds the manager with the least number of accounts assigned.
     * If there's a tie (multiple managers with the same minimum count),
     * randomly selects one from the tied managers.
     *
     * @return managerId of the manager with least accounts, or null if no managers exist
     */
    public Long findManagerWithLeastAccountsRandomTieBreaker() {
        List<Long> managersWithLeastAccounts = accountCommandRepository.findManagersWithLeastAccounts();

        if (managersWithLeastAccounts == null || managersWithLeastAccounts.isEmpty()) {
            return null;
        }

        // If only one manager has the least accounts, return it
        if (managersWithLeastAccounts.size() == 1) {
            return managersWithLeastAccounts.get(0);
        }

        // If there's a tie, randomly select one
        Random random = new Random();
        int randomIndex = random.nextInt(managersWithLeastAccounts.size());
        return managersWithLeastAccounts.get(randomIndex);
    }

    public String generateAccountNumber() {
        Random random = new Random();

        String number;

        do {
            number = String.valueOf(1000 + random.nextInt(9000));
        } while (accountCommandRepository.existsByAccountNumber(number));

        return number;
    }

    public Account getAccountByNumber(String accountNumber) {
        Account acc = accountCommandRepository.findByAccountNumber(accountNumber)
                .orElseThrow(
                        () -> new ResourceNotFoundException("Account not found with account number: " + accountNumber));

        return acc;
    }

}

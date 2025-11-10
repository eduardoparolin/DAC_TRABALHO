package com.dac.bank_account.query.service;

import com.dac.bank_account.enums.AccountStatus;
import com.dac.bank_account.exception.ResourceNotFoundException;
import com.dac.bank_account.query.dto.*;
import com.dac.bank_account.query.entity.AccountView;
import com.dac.bank_account.query.entity.TransactionView;
import com.dac.bank_account.query.repository.AccountQueryRepository;
import com.dac.bank_account.query.repository.TransactionQueryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AccountQueryService {

    private final AccountQueryRepository accountQueryRepository;
    private final TransactionQueryRepository transactionQueryRepository;
    private final AccountQueryMapper accountQueryMapper;

    public AccountQueryService(AccountQueryRepository accountQueryRepository,
                               TransactionQueryRepository transactionQueryRepository,
                               AccountQueryMapper accountQueryMapper) {
        this.accountQueryRepository = accountQueryRepository;
        this.transactionQueryRepository = transactionQueryRepository;
        this.accountQueryMapper = accountQueryMapper;
    }

    @Transactional("queryTransactionManager")
    public BalanceResponseDTO getBalance(String accountNumber) {
        AccountView account = getAccountByNumber(accountNumber);

        return accountQueryMapper.toBalanceResponseDTO(account);
    }

    @Transactional("queryTransactionManager")
    public StatementResponseDTO getStatement(String accountNumber) {
        AccountView account = getAccountByNumber(accountNumber);

        List<TransactionView> transactions = transactionQueryRepository
                .findBySourceAccountNumberOrTargetAccountNumber(accountNumber, accountNumber);

        return  accountQueryMapper.toStatementResponseDTO(account, transactions);
    }

    @Transactional("queryTransactionManager")
    public AccountResponseDTO getAccountDetails(String accountNumber) {
        AccountView account = getAccountByNumber(accountNumber);

        return accountQueryMapper.toAccountResponseDTO(account);
    }

    @Transactional("queryTransactionManager")
    public ManagerAccountsResponseDTO getManagerAccounts(String managerId) {
        List<AccountView> accounts = accountQueryRepository.findByManagerId(Long.valueOf(managerId));

        if(accounts.isEmpty()) {
            throw new ResourceNotFoundException("No accounts found for manager with ID: " + managerId);
        }

        return accountQueryMapper.toManagerAccountsResponseDTO(managerId, accounts);
    }

    @Transactional("queryTransactionManager")
    public List<AccountResponseDTO> getTop3Accounts(String managerId) {
        List<AccountView> accounts = accountQueryRepository.findTop3ByManagerIdOrderByBalanceDesc(Long.valueOf(managerId));

        if(accounts.isEmpty()) {
            throw new ResourceNotFoundException("No accounts found for manager with ID: " + managerId);
        }

        return accountQueryMapper.toAccountResponseDTOList(accounts);
    }

    @Transactional("queryTransactionManager")
    public List<AccountResponseDTO> getAccounts(AccountsRequestDTO request) {
        List<String> accountNumbers = request.getAccountNumbers();

        List<AccountView> accounts = accountQueryRepository.findByAccountNumberIn(accountNumbers);
        if(accounts.isEmpty()) {
            throw new ResourceNotFoundException("No accounts found for account number: " + accountNumbers);
        }
        return accountQueryMapper.toAccountResponseDTOList(accounts);
    }

    @Transactional("queryTransactionManager")
    public List<StatementResponseDTO> getAccountsWithTransactions(AccountsRequestDTO request) {
        List<String> accountNumbers = request.getAccountNumbers();

        List<AccountView> accounts = accountQueryRepository.findByAccountNumberIn(accountNumbers);

        if (accounts.isEmpty()) {
            throw new ResourceNotFoundException("No accounts found for account number: " + accountNumbers);
        }

        return accounts.stream()
                .map(account -> {
                    List<TransactionView> transactions =
                            transactionQueryRepository.findBySourceAccountNumberOrTargetAccountNumber(account.getAccountNumber(), account.getAccountNumber());
                    return accountQueryMapper.toStatementResponseDTO(account, transactions);
                })
                .toList();
    }

    @Transactional("queryTransactionManager")
    public ManagerAccountsResponseDTO getActiveAccountsByManager(String managerId) {
        List<AccountView> accounts = accountQueryRepository.findByManagerIdAndStatus(Long.valueOf(managerId), AccountStatus.ATIVA);

        if(accounts.isEmpty()) {
            throw new ResourceNotFoundException("No active accounts found for manager with ID: " + managerId);
        }

        return accountQueryMapper.toManagerAccountsResponseDTO(managerId, accounts);
    }

    @Transactional("queryTransactionManager")
    public AccountResponseDTO getClientAccount(String clientId) {
        AccountView account = accountQueryRepository.findByClientId(Long.valueOf(clientId))
                .orElseThrow(() -> new ResourceNotFoundException("Account not found for client with ID: " + clientId));

        return accountQueryMapper.toAccountResponseDTO(account);
    }

    @Transactional("queryTransactionManager")
    public AccountView getAccountByNumber(String accountNumber) {
        AccountView acc = accountQueryRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with account number: " + accountNumber));
        return acc;
    }
}

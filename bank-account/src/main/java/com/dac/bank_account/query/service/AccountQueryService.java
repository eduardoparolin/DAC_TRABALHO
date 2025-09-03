package com.dac.bank_account.query.service;

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

    public AccountQueryService(AccountQueryRepository accountQueryRepository, TransactionQueryRepository transactionQueryRepository, AccountQueryMapper accountQueryMapper) {
        this.accountQueryRepository = accountQueryRepository;
        this.transactionQueryRepository = transactionQueryRepository;
        this.accountQueryMapper = accountQueryMapper;
    }

    @Transactional("queryTransactionManager")
    public BalanceResponseDTO getBalance(String accountNumber) {
        AccountView account = accountQueryRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new IllegalArgumentException("Account not found with account number: " + accountNumber));

        return accountQueryMapper.toBalanceResponseDTO(account);
    }

    @Transactional("queryTransactionManager")
    public StatementResponseDTO getStatement(String accountNumber) {
        AccountView account = accountQueryRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new IllegalArgumentException("Account not found with account number: " + accountNumber));

        List<TransactionView> transactions = transactionQueryRepository.findBySourceAccountNumber(accountNumber);

        return  accountQueryMapper.toStatementResponseDTO(account, transactions);
    }

    @Transactional("queryTransactionManager")
    public AccountResponseDTO getAccountDetails(String accountNumber) {
        AccountView account = accountQueryRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new IllegalArgumentException("Account not found with account number: " + accountNumber));

        return accountQueryMapper.toAccountResponseDTO(account);
    }

    @Transactional("queryTransactionManager")
    public ManagerAccountsResponseDTO getManagerAccounts(String managerId) {
        List<AccountView> accounts = accountQueryRepository.findByManagerId(Long.valueOf(managerId));
        return accountQueryMapper.toManagerAccountsResponseDTO(managerId, accounts);
    }
}

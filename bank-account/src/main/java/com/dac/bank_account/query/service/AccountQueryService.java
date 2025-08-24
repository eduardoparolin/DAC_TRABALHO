package com.dac.bank_account.query.service;

import com.dac.bank_account.query.dto.AccountResponseDTO;
import com.dac.bank_account.query.dto.StatementResponseDTO;
import com.dac.bank_account.query.dto.BalanceResponseDTO;
import com.dac.bank_account.query.dto.TransactionResponseDTO;
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

    public AccountQueryService(AccountQueryRepository accountQueryRepository, TransactionQueryRepository transactionQueryRepository) {
        this.accountQueryRepository = accountQueryRepository;
        this.transactionQueryRepository = transactionQueryRepository;
    }

    @Transactional("queryTransactionManager")
    public BalanceResponseDTO getBalance(String accountNumber) {
        AccountView account = accountQueryRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new IllegalArgumentException("Account not found with account number: " + accountNumber));

        return new BalanceResponseDTO(
                account.getClientId().toString(),
                account.getAccountNumber(),
                account.getBalance()
        );
    }

    @Transactional("queryTransactionManager")
    public StatementResponseDTO getStatement(String accountNumber) {
        AccountView account = accountQueryRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new IllegalArgumentException("Account not found with account number: " + accountNumber));

        List<TransactionView> transactions = transactionQueryRepository.findBySourceAccountNumber(accountNumber);

        List<TransactionResponseDTO> statements = transactions.stream()
                .map(t -> new TransactionResponseDTO(
                        t.getDateTime().toString(),
                        t.getType().toString(),
                        t.getSourceAccountNumber(),
                        t.getTargetAccountNumber(),
                        t.getAmount()
                ))
                .toList();

        return new StatementResponseDTO(
                account.getAccountNumber(),
                account.getBalance(),
                statements
        );
    }

    @Transactional("queryTransactionManager")
    public AccountResponseDTO getAccountDetails(String accountNumber) {
        AccountView account = accountQueryRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new IllegalArgumentException("Account not found with account number: " + accountNumber));

        return new AccountResponseDTO(
                account.getClientId().toString(),
                account.getAccountNumber(),
                account.getBalance(),
                account.getLimitAmount(),
                account.getManagerId().toString(),
                account.getCreationDate().toString()
        );
    }
}

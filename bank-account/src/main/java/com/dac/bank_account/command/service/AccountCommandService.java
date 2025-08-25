package com.dac.bank_account.command.service;

import com.dac.bank_account.command.dto.*;
import com.dac.bank_account.command.dto.request.AccountRequestDTO;
import com.dac.bank_account.command.dto.response.AccountResponseDTO;
import com.dac.bank_account.command.dto.response.MovementResponseDTO;
import com.dac.bank_account.command.dto.response.TransferResponseDTO;
import com.dac.bank_account.command.entity.Account;
import com.dac.bank_account.enums.TransactionType;
import com.dac.bank_account.command.repository.AccountCommandRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Service
public class AccountCommandService {
    private final AccountCommandRepository accountCommandRepository;
    private final AccountMapper accountMapper;

    public AccountCommandService(AccountCommandRepository accountCommandRepository, AccountMapper accountMapper) {
        this.accountCommandRepository = accountCommandRepository;
        this.accountMapper = accountMapper;
    }

    @Transactional("commandTransactionManager")
    public AccountResponseDTO createAccount(AccountRequestDTO dto) {
        Account account = accountMapper.toEntity(dto);
        accountCommandRepository.save(account);

        return new AccountResponseDTO(
                account.getClientId().toString(),
                account.getAccountNumber(),
                account.getBalance(),
                account.getLimitAmount(),
                account.getManagerId().toString(),
                OffsetDateTime.now(ZoneOffset.of("-03:00")).toString()
        );
    }

    @Transactional("commandTransactionManager")
    public MovementResponseDTO deposit(String accountNumber, BigDecimal amount) {
        Account account = accountCommandRepository.findByAccountNumber(accountNumber)
                        .orElseThrow(() -> new IllegalArgumentException("Account not found with account number: " + accountNumber));
        account.deposit(amount);

        var transaction = accountMapper.toEntity(account.getAccountNumber(), TransactionType.DEPOSITO, amount, null);
        account.getTransactions().add(transaction);
        accountCommandRepository.save(account);

        return new MovementResponseDTO(
                account.getAccountNumber(),
                OffsetDateTime.now(ZoneOffset.of("-03:00")).toString(),
                account.getBalance()
        );
    }

    @Transactional("commandTransactionManager")
    public MovementResponseDTO withdraw(String accountNumber, BigDecimal amount) {
        Account account = accountCommandRepository.findByAccountNumber(accountNumber)
                        .orElseThrow(() -> new IllegalArgumentException("Account not found with account number: " + accountNumber));
        account.withdraw(amount);

        var transaction = accountMapper.toEntity(account.getAccountNumber(), TransactionType.SAQUE, amount, null);
        account.getTransactions().add(transaction);
        accountCommandRepository.save(account);

        return new MovementResponseDTO(
                account.getAccountNumber(),
                OffsetDateTime.now(ZoneOffset.of("-03:00")).toString(),
                account.getBalance()
        );
    }

    @Transactional("commandTransactionManager")
    public TransferResponseDTO transfer(String sourceAccountNumber, BigDecimal amount, String targetAccountNumber) {
        Account source = accountCommandRepository.findByAccountNumber(sourceAccountNumber)
                .orElseThrow(() -> new IllegalArgumentException("Source account not found with account number: " + sourceAccountNumber));
        Account target = accountCommandRepository.findByAccountNumber(targetAccountNumber)
                .orElseThrow(() -> new IllegalArgumentException("Target account not found with account number: " + targetAccountNumber));

        source.withdraw(amount);
        target.deposit(amount);

        var transaction = accountMapper.toEntity(source.getAccountNumber(), TransactionType.TRANSFERENCIA, amount, target.getAccountNumber());
        source.getTransactions().add(transaction);
        accountCommandRepository.save(source);
        accountCommandRepository.save(target);

        return new TransferResponseDTO(
                source.getAccountNumber(),
                OffsetDateTime.now(ZoneOffset.of("-03:00")).toString(),
                target.getAccountNumber(),
                source.getBalance(),
                amount
        );

    }

    @Transactional("commandTransactionManager")
    public AccountResponseDTO setLimit(String accountNumber, BigDecimal limite) {
        Account account = accountCommandRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new IllegalArgumentException("Account not found with account number: " + accountNumber));
        account.setLimitAmount(limite);
        accountCommandRepository.save(account);

        return new AccountResponseDTO(
                account.getClientId().toString(),
                account.getAccountNumber(),
                account.getBalance(),
                account.getLimitAmount(),
                account.getManagerId().toString(),
                account.getCreationDate().toString()
//                LocalDateTime.now(ZoneOffset.of("-03:00")).toString()
        );
    }
}

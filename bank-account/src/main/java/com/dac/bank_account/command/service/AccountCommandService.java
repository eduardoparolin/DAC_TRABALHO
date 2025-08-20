package com.dac.bank_account.command.service;

import com.dac.bank_account.command.dto.*;
import com.dac.bank_account.command.dto.request.AccountRequestDTO;
import com.dac.bank_account.command.dto.response.AccountResponseDTO;
import com.dac.bank_account.command.dto.response.MovementResponseDTO;
import com.dac.bank_account.command.dto.response.TransferResponseDTO;
import com.dac.bank_account.command.entity.Account;
import com.dac.bank_account.command.enums.TransactionType;
import com.dac.bank_account.command.events.*;
import com.dac.bank_account.command.repository.AccountCommandRepository;
import org.springframework.stereotype.Service;

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

    public MovementResponseDTO deposit(String accountNumber, BigDecimal amount) {
        Account account = accountCommandRepository.findByAccountNumber(accountNumber);
        if (account == null) {
            throw new IllegalArgumentException("Account not found with account number: " + accountNumber);
        }
        account.deposit(amount);

        var mov = accountMapper.toEntity(account.getAccountNumber(), TransactionType.DEPOSITO, amount, null);
        account.getTransactions().add(mov);
        accountCommandRepository.save(account);

        return new MovementResponseDTO(
                account.getAccountNumber(),
                LocalDateTime.now(ZoneOffset.of("-03:00")).toString(),
                account.getBalance()
        );
    }

    public MovementResponseDTO withdraw(String accountNumber, BigDecimal amount) {
        Account account = accountCommandRepository.findByAccountNumber(accountNumber);
        if (account == null) {
            throw new IllegalArgumentException("Account not found with account number: " + accountNumber);
        }
        account.withdraw(amount);

        var mov = accountMapper.toEntity(account.getAccountNumber(), TransactionType.SAQUE, amount, null);
        account.getTransactions().add(mov);
        accountCommandRepository.save(account);

        return new MovementResponseDTO(
                account.getAccountNumber(),
                LocalDateTime.now(ZoneOffset.of("-03:00")).toString(),
                account.getBalance()
        );
    }

    public TransferResponseDTO transfer(String sourceAccountNumber, BigDecimal amount, String targetAccountNumber) {
        Account source = accountCommandRepository.findByAccountNumber(sourceAccountNumber);
        Account target = accountCommandRepository.findByAccountNumber(targetAccountNumber);
        if (source == null || target == null) {
            throw new IllegalArgumentException("Source or target account not found");
        }

        source.withdraw(amount);
        target.deposit(amount);

        var mov = accountMapper.toEntity(source.getAccountNumber(), TransactionType.TRANSFERENCIA, amount, target.getAccountNumber());
        source.getTransactions().add(mov);
        accountCommandRepository.save(source);
        accountCommandRepository.save(target);

        return new TransferResponseDTO(
                source.getAccountNumber(),
                LocalDateTime.now(ZoneOffset.of("-03:00")).toString(),
                target.getAccountNumber(),
                source.getBalance(),
                amount
        );

    }

    public AccountResponseDTO setLimit(String numero, BigDecimal limite) {
        Account account = accountCommandRepository.findByAccountNumber(numero);
        if (account == null) {
            throw new IllegalArgumentException("Account not found with account number: " + numero);
        }
        account.setLimitAmount(limite);
        accountCommandRepository.save(account);

        return new AccountResponseDTO(
                account.getClientId().toString(),
                account.getAccountNumber(),
                account.getBalance(),
                account.getLimitAmount(),
                account.getManagerId().toString(),
                account.getCriationDate().toString()
//                LocalDateTime.now(ZoneOffset.of("-03:00")).toString()
        );
    }
}

package com.dac.bank_account.command.dto;

import com.dac.bank_account.command.dto.request.AccountRequestDTO;
import com.dac.bank_account.command.entity.Account;
import com.dac.bank_account.command.entity.Transaction;
import com.dac.bank_account.command.enums.TransactionType;
import com.dac.bank_account.command.repository.AccountCommandRepository;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Random;

@Component
public class AccountMapper {

    private final AccountCommandRepository accountRepository;

    public AccountMapper(AccountCommandRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public Account toEntity(AccountRequestDTO dto) {
        Account account = new Account();
        account.setClientId(dto.clientId());
        account.setAccountNumber(generateAccountNumber());
        account.setBalance(BigDecimal.ZERO);
        account.setLimitAmount(dto.limitAmount());
        account.setManagerId(dto.managerId());
        account.setCriationDate(OffsetDateTime.now(ZoneOffset.of("-03:00")));
        return account;
    }

    public Transaction toEntity(String sourceAccount, TransactionType type, BigDecimal amount, String targetAccount) {
        Transaction transaction = new Transaction();
        transaction.setSourceAccountNumber(sourceAccount);
        transaction.setTargetAccountNumber(targetAccount);
        transaction.setType(type);
        transaction.setAmount(amount);
        transaction.setDateTime(OffsetDateTime.now(ZoneOffset.of("-03:00")));
        return transaction;
    }

    public String generateAccountNumber() {
        Random random = new Random();
        String number;
        do {
            number = String.valueOf(1000 + random.nextInt(9000));
        } while (accountRepository.existsByAccountNumber(number));
        return number;
    }


}

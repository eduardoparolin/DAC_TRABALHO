package com.dac.bank_account.command.dto;

import com.dac.bank_account.command.dto.request.AccountRequestDTO;
import com.dac.bank_account.command.dto.response.AccountResponseDTO;
import com.dac.bank_account.command.dto.response.MovementResponseDTO;
import com.dac.bank_account.command.dto.response.TransferResponseDTO;
import com.dac.bank_account.command.entity.Account;
import com.dac.bank_account.command.entity.Transaction;
import com.dac.bank_account.enums.TransactionType;
import com.dac.bank_account.command.repository.AccountCommandRepository;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Random;

@Component
public class AccountCommandMapper {

    private final AccountCommandRepository accountRepository;

    public AccountCommandMapper(AccountCommandRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public Account toEntity(AccountRequestDTO dto) {
        Account account = new Account();
        account.setClientId(dto.clientId());
        account.setAccountNumber(generateAccountNumber());
        account.setBalance(BigDecimal.ZERO);
        account.setLimitAmount(dto.limitAmount());
        account.setManagerId(dto.managerId());
        account.setCreationDate(OffsetDateTime.now(ZoneOffset.of("-03:00")));
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

    public AccountResponseDTO accountToDTO(Account account) {
        return new AccountResponseDTO(
                account.getClientId().toString(),
                account.getAccountNumber(),
                account.getBalance(),
                account.getLimitAmount(),
                account.getManagerId().toString(),
                account.getCreationDate().toString()
        );
    }

    public MovementResponseDTO toMovementDTO(Account account) {
        return new MovementResponseDTO(
                account.getAccountNumber(),
                OffsetDateTime.now(ZoneOffset.of("-03:00")).toString(),
                account.getBalance()
        );
    }

    public TransferResponseDTO toTransferDTO(Account source, Account target, BigDecimal amount) {
        return new TransferResponseDTO(
                source.getAccountNumber(),
                OffsetDateTime.now(ZoneOffset.of("-03:00")).toString(),
                target.getAccountNumber(),
                source.getBalance(),
                amount
        );
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

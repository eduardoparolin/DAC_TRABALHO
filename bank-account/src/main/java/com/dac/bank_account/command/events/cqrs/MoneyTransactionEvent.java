package com.dac.bank_account.command.events.cqrs;

import com.dac.bank_account.enums.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MoneyTransactionEvent {
    private String accountNumber;
    private String targetAccountNumber;
    private BigDecimal amount;
    private TransactionType transactionType;
    private OffsetDateTime timestamp;

    // Construtor para DEPOSITO/SAQUE
    public MoneyTransactionEvent(String accountNumber,
                                 BigDecimal amount,
                                 TransactionType transactionType,
                                 OffsetDateTime timestamp) {
        this.accountNumber = accountNumber;
        this.amount = amount;
        this.transactionType = transactionType;
        this.timestamp = timestamp;
    }

    // Construtor para TRANSFERENCIA
    public MoneyTransactionEvent(String accountNumber,
                                 String targetAccountNumber,
                                 BigDecimal amount,
                                 OffsetDateTime timestamp) {
        this.accountNumber = accountNumber;
        this.targetAccountNumber = targetAccountNumber;
        this.amount = amount;
        this.transactionType = TransactionType.TRANSFERENCIA;
        this.timestamp = timestamp;
    }
}

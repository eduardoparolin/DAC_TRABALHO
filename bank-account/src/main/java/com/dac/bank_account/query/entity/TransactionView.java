package com.dac.bank_account.query.entity;

import com.dac.bank_account.enums.TransactionType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "transaction")
public class TransactionView {
    @Id
    private Long id;
    private OffsetDateTime dateTime;

    @Enumerated(EnumType.STRING)
    private TransactionType type;

    @Column(name = "source_account_number", nullable = false)
    private String sourceAccountNumber;

    @Column(name = "target_account_number", nullable = true)
    private String targetAccountNumber;
    private BigDecimal amount;
}

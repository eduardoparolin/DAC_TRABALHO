package com.dac.bank_account.query.entity;

import com.dac.bank_account.enums.AccountStatus;
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
@Table(name = "account")
public class AccountView {

    @Id
    private Long id;
    private Long clientId;
    private String accountNumber;
    private BigDecimal balance;
    private BigDecimal limitAmount;
    private Long managerId;
    private OffsetDateTime creationDate;
    @Enumerated(EnumType.STRING)
    private AccountStatus status;

}

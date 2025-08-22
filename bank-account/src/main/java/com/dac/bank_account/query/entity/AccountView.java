package com.dac.bank_account.query.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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
    private OffsetDateTime criationDate;
    private BigDecimal balance;
    private BigDecimal limitAmount;
    private Long managerId;

}

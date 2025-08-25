package com.dac.bank_account.query.dto;

import java.math.BigDecimal;

public record BalanceResponseDTO(
        String cliente,
        String conta,
        BigDecimal saldo
) {}

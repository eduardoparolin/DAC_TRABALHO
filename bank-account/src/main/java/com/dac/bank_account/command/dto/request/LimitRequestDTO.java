package com.dac.bank_account.command.dto.request;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record LimitRequestDTO(
        @NotNull
        @Positive
        Double salario
) {
}

package com.dac.bank_account.command.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record MovementRequestDTO(
        @NotNull
        @Positive
        BigDecimal valor
) {
}

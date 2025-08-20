package com.dac.bank_account.command.dto.request;

import jakarta.validation.constraints.Positive;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;

public record AccountRequestDTO(
        @NotNull
        Long clientId,

        @NotNull
        @Positive
        BigDecimal limitAmount,

        @NotNull
        Long managerId
) {
}

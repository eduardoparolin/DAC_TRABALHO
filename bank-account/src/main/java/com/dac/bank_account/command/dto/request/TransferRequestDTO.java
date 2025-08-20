package com.dac.bank_account.command.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record TransferRequestDTO(
        @NotBlank
        String destino,
        @NotNull
        @Positive
        BigDecimal valor
) {
}

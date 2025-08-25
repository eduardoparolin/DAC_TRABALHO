package com.dac.bank_account.command.dto.response;



import java.math.BigDecimal;

public record MovementResponseDTO(
        String conta,
        String data,
        BigDecimal saldo
) {
}

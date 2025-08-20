package com.dac.bank_account.command.dto.response;


import java.math.BigDecimal;

public record AccountResponseDTO(
        String cliente,
        String numero,
        BigDecimal saldo,
        BigDecimal limite,
        String gerente,
        String criacao
) {
}

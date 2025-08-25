package com.dac.bank_account.command.dto.response;


import java.math.BigDecimal;

public record TransferResponseDTO(
        String conta,
        String data,
        String destino,
        BigDecimal saldo,
        BigDecimal valor
) {
}

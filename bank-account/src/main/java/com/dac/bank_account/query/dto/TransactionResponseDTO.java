package com.dac.bank_account.query.dto;

import java.math.BigDecimal;

public record TransactionResponseDTO(
        String data,
        String tipo,
        String origem,
        String destino,
        Double valor
) {
}

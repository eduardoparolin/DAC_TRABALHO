package com.dac.bank_account.query.dto;

import java.math.BigDecimal;
import java.util.List;

public record StatementResponseDTO(
        String conta,
        BigDecimal saldo,
        List<TransactionResponseDTO> movimentacoes
) {}

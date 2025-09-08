package com.dac.bank_account.query.dto;

import java.math.BigDecimal;
import java.util.List;

public record StatementResponseDTO(
        String conta,
        Double saldo,
        List<TransactionResponseDTO> movimentacoes
) {}

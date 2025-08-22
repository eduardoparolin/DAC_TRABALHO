package com.dac.bank_account.query.dto;

import java.math.BigDecimal;

public record AccountResponseDTO (
        String cliente,
        String numero,
        BigDecimal saldo,
        BigDecimal limite,
        String gerente,
        String criacao
){
}

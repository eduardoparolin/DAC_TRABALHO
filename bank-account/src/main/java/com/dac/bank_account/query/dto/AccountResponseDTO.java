package com.dac.bank_account.query.dto;

import java.math.BigDecimal;

public record AccountResponseDTO (
        String cliente,
        String numero,
        Double saldo,
        Double limite,
        String gerente,
        String criacao,
        String status
){
}

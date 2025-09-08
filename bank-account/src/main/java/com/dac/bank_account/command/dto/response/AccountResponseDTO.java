package com.dac.bank_account.command.dto.response;

public record AccountResponseDTO(
        String cliente,
        String numero,
        Double saldo,
        Double limite,
        String gerente,
        String criacao
) {
}

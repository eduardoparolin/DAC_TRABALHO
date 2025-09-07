package com.dac.bank_account.command.dto.response;

public record MovementResponseDTO(
        String conta,
        String data,
        Double saldo
) {
}

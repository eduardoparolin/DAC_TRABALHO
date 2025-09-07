package com.dac.bank_account.command.dto.response;

public record TransferResponseDTO(
        String conta,
        String data,
        String destino,
        Double saldo,
        Double valor
) {
}

package com.dac.bank_account.query.dto;
import java.util.List;

public record ManagerAccountsResponseDTO(
        String gerente,
        List<AccountResponseDTO> contas
) {
}

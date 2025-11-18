package com.bank.client.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClientLinkAccountDTO {
    @NotNull
    private Long clientId;

    @NotNull
    private String accountNumber;
}

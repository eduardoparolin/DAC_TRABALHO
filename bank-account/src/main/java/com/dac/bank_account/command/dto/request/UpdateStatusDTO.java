package com.dac.bank_account.command.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateStatusDTO {

    @NotNull
    private Long clientId;

    @NotNull
    private Boolean isApproved;
}

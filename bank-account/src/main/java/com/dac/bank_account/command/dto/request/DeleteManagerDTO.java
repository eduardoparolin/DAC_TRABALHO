package com.dac.bank_account.command.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeleteManagerDTO {

    @NotNull
    private Long oldManagerId;

    @NotNull
    private Long newManagerId;
}

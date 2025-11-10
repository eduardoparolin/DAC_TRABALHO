package com.dac.bank_account.command.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateAccountDTO {

    @NotNull
    private Long clientId;

    @NotNull
    @DecimalMin(value = "0.0")
    private Double salary;

    private Long managerId;
}

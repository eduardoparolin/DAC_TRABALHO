package com.dac.bank_account.command.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class NewManagerDTO {

    @NotNull
    private Long newManagerId;
    private Long oldManagerId;
    private Long accountId;

    public NewManagerDTO(Long newManagerId) {
        this.newManagerId = newManagerId;
    }

    public NewManagerDTO(Long newManagerId, Long oldManagerId, Long accountId) {
        this.newManagerId = newManagerId;
        this.oldManagerId = oldManagerId;
        this.accountId = accountId;
    }
}

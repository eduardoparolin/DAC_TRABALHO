package com.bank.client.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClientRejectDTO {
    @NotNull
    private Long clientId;
    @NotBlank
    private String rejectionReason;
}

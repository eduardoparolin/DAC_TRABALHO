package com.bank.client.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RejectClientRequest {
    @NotBlank(message = "Motivo da rejeição é obrigatório")
    private String rejectionReason;
}

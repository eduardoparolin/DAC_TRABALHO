package com.bank.manager.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ManagersResponseDTO {
    private Long id;
    private String cpf;
    private String name;
    private String email;
}

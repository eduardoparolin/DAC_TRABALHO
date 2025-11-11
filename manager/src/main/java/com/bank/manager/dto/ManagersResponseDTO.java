package com.bank.manager.dto;

import com.bank.manager.model.ManagerType;
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
    private ManagerType managerType;
}

package com.bank.manager.dto;

//SEPARANDO ENTITY E DTO PARA MELHORES PRATICAS DE DESACOPLAMENTO

import com.bank.manager.model.Manager;
import com.bank.manager.model.ManagerType;

public record ManagerDTO(
        String cpf,
        String name,
        String email,
        ManagerType type
){
    public static ManagerDTO fromEntity(Manager entity) {
        return new ManagerDTO(
                entity.getCpf(),
                entity.getName(),
                entity.getEmail(),
                entity.getType()
        );
    }
}
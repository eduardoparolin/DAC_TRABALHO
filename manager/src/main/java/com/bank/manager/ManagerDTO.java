package com.bank.manager.dto.ManagerDTO;

//SEPARANDO ENTITY E DTO PARA MELHORES PRATICAS DE DESACOPLAMENTO

public record ManagerDTO(
        String cpf,
        String name,
        String email,
        String telefone,
        String type
){
}
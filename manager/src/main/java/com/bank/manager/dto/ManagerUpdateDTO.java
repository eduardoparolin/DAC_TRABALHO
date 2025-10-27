package com.bank.manager.dto;

import com.bank.manager.model.ManagerType;

public record ManagerUpdateDTO(
        String name,
        String email,
        ManagerType type
) {
}

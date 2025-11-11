package com.bank.client.dto;

import java.util.List;

public record ClientsRequestDTO(
        List<Long> clientIds
) {
}

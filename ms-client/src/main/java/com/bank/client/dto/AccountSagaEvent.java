package com.bank.client.dto;

import lombok.Data;

@Data
public class AccountSagaEvent {
    private String sagaId;
    private String action;
    private Long clientId;
    private Boolean isApproved;
    private Long accountId;
}

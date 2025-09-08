package com.bank.manager.dto.events;

public record ClientRejectionEvent(
        String clientCpf,
        String managerCpf,
        String reason,
        String email
) {}

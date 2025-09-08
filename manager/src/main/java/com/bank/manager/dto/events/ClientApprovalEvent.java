package com.bank.manager.dto.events;

import java.math.BigDecimal;

public record ClientApprovalEvent(
        String clientCpf,
        String managerCpf,
        String generatedPassword,
        BigDecimal calculatedLimit,
        String email
) {}

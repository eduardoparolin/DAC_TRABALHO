package com.bank.client.infra.producer;

import com.bank.client.dto.AccountSagaEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountProducer {

    private final RabbitTemplate rabbitTemplate;
    private final String ACCOUNT_SAGA_QUEUE = "account-saga-queue";

    public void sendUpdateAccountStatusEvent(AccountSagaEvent event) {
        log.info("Sending UPDATE_ACCOUNT_STATUS event to account-saga-queue for clientId: {}", event.getClientId());
        try {
            rabbitTemplate.convertAndSend(ACCOUNT_SAGA_QUEUE, event);
            log.info("Successfully sent UPDATE_ACCOUNT_STATUS event");
        } catch (Exception e) {
            log.error("Error sending UPDATE_ACCOUNT_STATUS event: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to send account status update event", e);
        }
    }
}

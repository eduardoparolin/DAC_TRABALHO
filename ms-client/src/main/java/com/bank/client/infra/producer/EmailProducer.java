package com.bank.client.infra.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailProducer {

    private final RabbitTemplate rabbitTemplate;
    private final String EMAIL_QUEUE = "email-queue";

    public void sendRejectionEmail(String clientName, String clientEmail, String rejectionReason) {
        log.info("Sending rejection email to email-queue for client: {}", clientEmail);
        try {
            Map<String, Object> emailEvent = new HashMap<>();
            emailEvent.put("action", "SEND_REJECTION_EMAIL");
            emailEvent.put("clientName", clientName);
            emailEvent.put("clientEmail", clientEmail);
            emailEvent.put("rejectionReason", rejectionReason);

            rabbitTemplate.convertAndSend(EMAIL_QUEUE, emailEvent);
            log.info("Successfully sent rejection email event");
        } catch (Exception e) {
            log.error("Error sending rejection email event: {}", e.getMessage(), e);
            // Don't throw - we don't want to rollback the rejection if email fails
            log.warn("Client was rejected but email notification failed");
        }
    }
}

package com.dac.auth.infra.consumer;

import com.dac.auth.infra.email.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class EmailConsumer {

    private final EmailService emailService;

    @RabbitListener(queues = "email-queue")
    public void listenEmailQueue(Map<String, Object> message) {
        log.info("Received email message: {}", message);

        String action = (String) message.get("action");

        if ("SEND_REJECTION_EMAIL".equals(action)) {
            handleRejectionEmail(message);
        } else {
            log.warn("Unknown email action: {}", action);
        }
    }

    private void handleRejectionEmail(Map<String, Object> message) {
        try {
            String clientName = (String) message.get("clientName");
            String clientEmail = (String) message.get("clientEmail");
            String rejectionReason = (String) message.get("rejectionReason");

            log.info("Sending rejection email to: {}", clientEmail);
            emailService.sendRejectionEmail(clientName, clientEmail, rejectionReason);
            log.info("Rejection email sent successfully");

        } catch (Exception e) {
            log.error("Error sending rejection email", e);
            // Don't throw - we don't want to retry email sending indefinitely
        }
    }
}

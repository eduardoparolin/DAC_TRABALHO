package com.dac.bank_account.query.consumer;

import com.dac.bank_account.config.RabbitConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class DeadLetterQueueConsumer {

    @RabbitListener(queues = RabbitConfig.QUEUE_BANK_ACCOUNT_DLQ)
    public void handleDeadLetterMessage(Message message) {
        log.error("!!! Mensagem recebida na Dead-Letter Queue !!!");
        log.error("Causa da falha: {}", message.getMessageProperties().getHeaders().get("x-death"));
        log.error("Conteúdo da mensagem: {}", new String(message.getBody()));
    }
}

package com.dac.auth.infra.consumer;

import com.dac.auth.exception.custom.ApiException;
import com.dac.auth.infra.consumer.utils.factory.MessageHandlerFactory;
import com.dac.auth.infra.consumer.utils.handlers.interfaces.MessageHandler;
import com.dac.auth.dto.payload.AuthPayload;
import com.dac.auth.infra.producer.AuthProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthConsumer {

    private final MessageHandlerFactory handlerFactory;
    private final AuthProducer producer;

    @RabbitListener(queues = "user-queue")
    public void listenAuthQueue(@Payload AuthPayload payload) {
        try {
            MessageHandler<AuthPayload> strategyHandler = handlerFactory.getStrategy(payload.getAction());
            strategyHandler.handle(payload);
        } catch (ApiException ex) {
            producer.sendResult(payload, "FAIL", ex.getMessage());
        }

    }
}

package com.dac.auth.infra.consumer;

import com.dac.auth.infra.consumer.utils.handlers.factory.MessageHandlerFactory;
import com.dac.auth.infra.consumer.utils.handlers.interfaces.MessageHandler;
import com.dac.auth.dto.payload.AuthPayload;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthConsumer {

    private final MessageHandlerFactory handlerFactory;

    @RabbitListener(queues = "user-queue")
    public void listenAuthQueue(@Payload AuthPayload payload) {
        System.out.println("Consumindo fila: " + payload);
        MessageHandler<AuthPayload> strategyHandler = handlerFactory.getStrategy(payload.getAction());
        strategyHandler.handle(payload);
    }
}

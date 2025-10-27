package com.bank.client.infra.consumer;

import com.bank.client.dto.ClientRequest;
import com.bank.client.infra.consumer.factory.ClientMessageHandleFactory;
import com.bank.client.infra.consumer.handler.interfaces.ClientMessageHandler;
import com.bank.client.infra.producer.ClientProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ClientConsumer {

    public final ClientMessageHandleFactory clientMessageHandleFactory;
    private final ClientProducer clientProducer;

    @RabbitListener(queues = "client-queue")
    public void listenAccountQueue(ClientRequest event) {
        try {
            ClientMessageHandler handler = clientMessageHandleFactory.getStrategy(event.getAction());
            handler.handle(event);
            clientProducer.sendSuccessResult(event.getAction(), event.getClientId());
        } catch (Exception ex) {
            clientProducer.sendFailureResult(ex.getMessage(), event.getAction());
        }
    }
}

package com.bank.client.events;

import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Cria beans de AMQP apenas quando msclient.events.enabled=true
 * Assim o app roda normalmente sem RabbitMQ.
 */
@Configuration
@ConditionalOnProperty(value = "msclient.events.enabled", havingValue = "true")
public class RabbitConfig {

    @Bean
    public TopicExchange sagaExchange(org.springframework.core.env.Environment env) {
        String name = env.getProperty("msclient.events.exchange", "saga.autocadastro");
        return new TopicExchange(name, true, false);
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory cf) {
        return new RabbitTemplate(cf);
    }
}

package com.ias.config;

import com.ias.SecretsManagerHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class RabbitMqConfig {
    private final SecretsManagerHelper secretsManagerHelper;

    @Bean
    public ConnectionFactory rabbitConnectionFactory(@Value("${aws.secrets.rabbitmq-secret-name}") String secretName) {
        Map<String, String> config = secretsManagerHelper.getSecretAsMap(secretName);

        System.out.println("here");
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory(config.get("host"), Integer.parseInt(config.get("port")));
        connectionFactory.setUsername(config.get("username"));
        connectionFactory.setPassword(config.get("password"));
        connectionFactory.setVirtualHost(config.get("virtual-host"));

        return connectionFactory;
    }

    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public AmqpTemplate amqpTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter());
        return template;
    }
}

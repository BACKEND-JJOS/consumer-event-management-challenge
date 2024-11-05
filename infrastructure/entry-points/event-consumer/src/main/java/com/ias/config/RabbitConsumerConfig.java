package com.ias.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableRabbit
public class RabbitConsumerConfig {
    public static final String EVENT_EXCHANGE = "event.exchange";

    public static final String EVENT_CREATED_QUEUE = "event.created.notifications.queue";
    public static final String EVENT_UPDATED_QUEUE = "event.updated.notifications.queue";
    public static final String EVENT_DELETED_QUEUE = "event.deleted.notifications.queue";
    public static final String EVENT_USER_REGISTER_QUEUE = "user.event.register.notifications.queue";

    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(EVENT_EXCHANGE, true, false);
    }

    @Bean
    public Queue createdQueue() {
        return new Queue(EVENT_CREATED_QUEUE, true);
    }

    @Bean
    public Queue updatedQueue() {
        return new Queue(EVENT_UPDATED_QUEUE, true);
    }

    @Bean
    public Queue deletedQueue() {
        return new Queue(EVENT_DELETED_QUEUE, true);
    }

    @Bean
    public Queue userRegisterQueue() {
        return new Queue(EVENT_USER_REGISTER_QUEUE, true);
    }

    @Bean
    public Binding bindingCreatedQueue(Queue createdQueue, TopicExchange exchange) {
        return BindingBuilder.bind(createdQueue).to(exchange).with("event.created");
    }

    @Bean
    public Binding bindingUpdatedQueue(Queue updatedQueue, TopicExchange exchange) {
        return BindingBuilder.bind(updatedQueue).to(exchange).with("event.updated");
    }

    @Bean
    public Binding bindingDeletedQueue(Queue deletedQueue, TopicExchange exchange) {
        return BindingBuilder.bind(deletedQueue).to(exchange).with("event.deleted");
    }

    @Bean
    public Binding bindingUserRegisterQueue(Queue userRegisterQueue, TopicExchange exchange) {
        return BindingBuilder.bind(userRegisterQueue).to(exchange).with("user.event.register");
    }
}

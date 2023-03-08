package com.amit.order.rabbit;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for the service to connect to RabbitMQ on startup.
 */
@Configuration
public class RabbitConfig {

    /**
     * Queue to receive status for orders placed
     */
    @Value("${status.queue.name}")
    private String statusQueue;

    /**
     * Initialize the Rabbit queue to receive the status
     */
    @Bean(name = "statusQueue")
    public Queue statusQueue() {
        return new Queue(statusQueue, true);
    }

    /**
     * Initialize the {@link RabbitTemplate} to connect to Rabbit
     */
    @Bean
    public RabbitTemplate rabbitTemplate(final ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(producerJackson2MessageConverter());
        return rabbitTemplate;
    }

    /**
     * Create specific convertor to be used for RabbitMQ interactions
     */
    @Bean
    public Jackson2JsonMessageConverter producerJackson2MessageConverter() {
        ObjectMapper mapper = new ObjectMapper();
        return new Jackson2JsonMessageConverter(mapper);
    }
}

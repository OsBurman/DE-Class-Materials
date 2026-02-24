package com.academy.kafka.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

/**
 * Kafka topic definitions.
 *
 * TODO Task 2: Complete the topic beans.
 */
@Configuration
public class KafkaTopicConfig {

    @Value("${kafka.topics.orders}")
    private String ordersTopic;

    @Value("${kafka.topics.notifications}")
    private String notificationsTopic;

    @Value("${kafka.topics.inventory}")
    private String inventoryTopic;

    @Value("${kafka.topics.orders-dlq}")
    private String ordersDlqTopic;

    // TODO Task 2a: Create the orders topic with 3 partitions and 1 replica
    @Bean
    public NewTopic ordersTopic() {
        return TopicBuilder.name(ordersTopic)
                .partitions(3)
                .replicas(1)
                .build();
    }

    // TODO Task 2b: Create notifications topic
    @Bean
    public NewTopic notificationsTopic() {
        return TopicBuilder.name(notificationsTopic).partitions(1).replicas(1).build();
    }

    // TODO Task 2c: Create inventory topic
    @Bean
    public NewTopic inventoryTopic() {
        return TopicBuilder.name(inventoryTopic).partitions(1).replicas(1).build();
    }

    // TODO Task 2d: Create the Dead Letter Queue (DLQ) topic for failed messages
    @Bean
    public NewTopic ordersDlqTopic() {
        return TopicBuilder.name(ordersDlqTopic).partitions(1).replicas(1).build();
    }
}

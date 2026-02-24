package com.example.kafka.config;

import com.example.kafka.model.OrderEvent;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

// TODO: Add @EnableKafka to activate @KafkaListener scanning
@Configuration
public class KafkaConsumerConfig {

    @Bean
    public ConsumerFactory<String, OrderEvent> consumerFactory() {
        Map<String, Object> props = new HashMap<>();

        // TODO: Add ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG → "localhost:9092"

        // TODO: Add ConsumerConfig.GROUP_ID_CONFIG → "order-processor"

        // TODO: Add ConsumerConfig.AUTO_OFFSET_RESET_CONFIG → "earliest"

        // TODO: Add ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG → StringDeserializer.class

        // TODO: Add ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG → JsonDeserializer.class

        // TODO: Add JsonDeserializer.TRUSTED_PACKAGES → "*"
        //       (restrict to "com.example.kafka.model" in production)

        // TODO: Add JsonDeserializer.VALUE_DEFAULT_TYPE → "com.example.kafka.model.OrderEvent"

        return new DefaultKafkaConsumerFactory<>(props);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, OrderEvent> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, OrderEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();

        // TODO: Set the consumer factory on the container factory

        // TODO: Set concurrency to 3 (one thread per partition)

        return factory;
    }
}

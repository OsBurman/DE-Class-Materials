package com.example.kafka.config;

import com.example.kafka.model.OrderEvent;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaProducerConfig {

    @Bean
    public ProducerFactory<String, OrderEvent> producerFactory() {
        Map<String, Object> configProps = new HashMap<>();

        // TODO: Add ProducerConfig.BOOTSTRAP_SERVERS_CONFIG → "localhost:9092"

        // TODO: Add ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG → StringSerializer.class

        // TODO: Add ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG → JsonSerializer.class

        // TODO: Add JsonSerializer.ADD_TYPE_INFO_HEADERS → false
        //       (prevents __TypeId__ headers being added to each message)

        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    public KafkaTemplate<String, OrderEvent> kafkaTemplate() {
        // TODO: Return a new KafkaTemplate using the producerFactory() bean
        return null;
    }
}

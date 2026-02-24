package com.example.kafka.config;

// Reflection answers (fill in before writing the config code):
//
// Q1: What is the difference between FixedBackOff and ExponentialBackOffWithMaxRetries?
//     When would you prefer each?
//     TODO:
//
// Q2: What happens to a failed message when all retries are exhausted and there is NO
//     DeadLetterPublishingRecoverer?
//     TODO:
//
// Q3: Why should DeserializationException always be added to the non-retryable list?
//     TODO:

import com.example.kafka.model.OrderEvent;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.util.backoff.FixedBackOff;

import java.util.HashMap;
import java.util.Map;

// TODO: Add @EnableKafka
@Configuration
public class KafkaErrorHandlingConfig {

    // ── Producer (for DeadLetterPublishingRecoverer) ──────────────────────────

    @Bean
    public ProducerFactory<Object, Object> dltProducerFactory() {
        Map<String, Object> props = new HashMap<>();
        // TODO: Add BOOTSTRAP_SERVERS_CONFIG → "localhost:9092"
        // TODO: Add KEY_SERIALIZER_CLASS_CONFIG → StringSerializer.class
        // TODO: Add VALUE_SERIALIZER_CLASS_CONFIG → StringSerializer.class
        return new DefaultKafkaProducerFactory<>(props);
    }

    @Bean
    public KafkaTemplate<Object, Object> dltKafkaTemplate() {
        // TODO: Return a new KafkaTemplate using dltProducerFactory()
        return null;
    }

    // ── Error Handler ─────────────────────────────────────────────────────────

    @Bean
    public DeadLetterPublishingRecoverer deadLetterPublishingRecoverer() {
        // TODO: Return a new DeadLetterPublishingRecoverer using dltKafkaTemplate()
        //       It will automatically publish failed messages to "<topic>.DLT"
        return null;
    }

    @Bean
    public DefaultErrorHandler defaultErrorHandler() {
        // TODO: Create a FixedBackOff with 1000ms interval and 3 max attempts

        // TODO: Create a DefaultErrorHandler passing the recoverer and the backoff

        // TODO: Add IllegalArgumentException as a non-retryable exception
        //       errorHandler.addNotRetryableExceptions(IllegalArgumentException.class)

        return null; // replace with actual errorHandler
    }

    // ── Consumer Factory + Container Factory ─────────────────────────────────

    @Bean
    public ConsumerFactory<String, OrderEvent> consumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "order-error-handler");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
        props.put(JsonDeserializer.VALUE_DEFAULT_TYPE, "com.example.kafka.model.OrderEvent");
        return new DefaultKafkaConsumerFactory<>(props);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, OrderEvent> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, OrderEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        // TODO: Wire the defaultErrorHandler into the container factory
        //       factory.setCommonErrorHandler(defaultErrorHandler())
        return factory;
    }
}

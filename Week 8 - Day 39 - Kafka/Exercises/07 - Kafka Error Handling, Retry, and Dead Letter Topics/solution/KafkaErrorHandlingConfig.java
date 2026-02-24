package com.example.kafka.config;

// Reflection answers:
//
// Q1: FixedBackOff vs ExponentialBackOffWithMaxRetries:
//     FixedBackOff waits the same interval between every retry attempt.
//     Example: FixedBackOff(1000L, 3) → retry at T+1s, T+2s, T+3s.
//     Use FixedBackOff when the expected recovery time is predictable (e.g., 1s database blip).
//
//     ExponentialBackOffWithMaxRetries doubles the wait time between retries up to a cap.
//     Example: initial=500ms, multiplier=2 → retry at T+500ms, T+1s, T+2s, T+4s, ...
//     Use exponential backoff when a downstream service is overwhelmed — increasing delays
//     reduce the retry storm and give the service more time to recover.
//
// Q2: Without a DeadLetterPublishingRecoverer:
//     When DefaultErrorHandler exhausts all retries, it uses its default recovery action,
//     which is to LOG the error and SKIP the record (move past it). The message is lost —
//     it is neither retried nor sent anywhere for later inspection. For any production system
//     where message loss is unacceptable, a DLT recoverer (or custom recoverer) is mandatory.
//
// Q3: DeserializationException should always be non-retryable because the message bytes are
//     malformed (bad JSON, wrong schema, wrong class). No amount of retrying will make a
//     malformed message deserialize correctly — every retry attempt will produce the same
//     exception. Without marking it non-retryable, the consumer wastes time on 3+ retries
//     before finally giving up, and every subsequent poll for the same partition is blocked
//     for the entire backoff period. Skipping straight to the DLT is the correct response.

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

@EnableKafka
@Configuration
public class KafkaErrorHandlingConfig {

    // ── Producer for DeadLetterPublishingRecoverer ────────────────────────────

    @Bean
    public ProducerFactory<Object, Object> dltProducerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        return new DefaultKafkaProducerFactory<>(props);
    }

    @Bean
    public KafkaTemplate<Object, Object> dltKafkaTemplate() {
        return new KafkaTemplate<>(dltProducerFactory());
    }

    // ── Error Handler ─────────────────────────────────────────────────────────

    @Bean
    public DeadLetterPublishingRecoverer deadLetterPublishingRecoverer() {
        // Publishes failed records to "<originalTopic>.DLT" automatically
        return new DeadLetterPublishingRecoverer(dltKafkaTemplate());
    }

    @Bean
    public DefaultErrorHandler defaultErrorHandler() {
        // Retry up to 3 attempts with 1-second fixed delay between each
        FixedBackOff backOff = new FixedBackOff(1000L, 3);
        DefaultErrorHandler errorHandler =
                new DefaultErrorHandler(deadLetterPublishingRecoverer(), backOff);
        // IllegalArgumentException is never transient — skip retries, send straight to DLT
        errorHandler.addNotRetryableExceptions(IllegalArgumentException.class);
        return errorHandler;
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
        // Wire in the error handler — all listener exceptions flow through it
        factory.setCommonErrorHandler(defaultErrorHandler());
        return factory;
    }
}

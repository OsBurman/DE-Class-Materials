package com.example.kafka.consumer;

// Reflection answers:
//
// Q1: Why does the second listener use a different groupId?
//     If both methods used "order-processor", they would be in the same consumer group.
//     Kafka assigns each partition to exactly one consumer in a group. With 3 partitions
//     and 2 listener methods in the same group, each message would only reach ONE of the
//     two methods — not both. Using a separate group "order-metadata-inspector" ensures
//     Kafka tracks its offsets independently, and both groups receive every message.
//
// Q2: If the topic has 3 partitions and concurrency=3, how many threads does Spring create?
//     Spring creates 3 KafkaMessageListenerContainer instances (one per thread). Each thread
//     is assigned one partition by Kafka's group coordinator. This means up to 3 records can
//     be processed in parallel, one per partition.
//
// Q3: TRUSTED_PACKAGES = "*" allows the JsonDeserializer to deserialize any class from any
//     package. This is a security risk because a malicious Kafka producer could publish a
//     message with a __TypeId__ header pointing to a harmful class (e.g., one that runs code
//     on deserialization). In production, always restrict to your specific model package:
//     JsonDeserializer.TRUSTED_PACKAGES → "com.example.kafka.model"

import com.example.kafka.model.OrderEvent;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class OrderEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(OrderEventConsumer.class);

    /**
     * Listener 1: Receives a deserialized OrderEvent directly.
     * Spring injects only the value (the OrderEvent POJO) — no metadata.
     */
    @KafkaListener(topics = "order-events", groupId = "order-processor")
    public void handleOrderEvent(OrderEvent event) {
        log.info("Received OrderEvent: orderId={}, status={}, amount={}",
                event.getOrderId(), event.getStatus(), event.getAmount());
    }

    /**
     * Listener 2: Receives the full ConsumerRecord to inspect Kafka metadata.
     * Uses a separate consumer group so it independently receives every message.
     */
    @KafkaListener(topics = "order-events", groupId = "order-metadata-inspector")
    public void handleOrderEventWithMetadata(ConsumerRecord<String, OrderEvent> record) {
        log.info("[Metadata] topic={}, partition={}, offset={}, key={}, orderId={}",
                record.topic(),
                record.partition(),
                record.offset(),
                record.key(),
                record.value().getOrderId());
    }
}

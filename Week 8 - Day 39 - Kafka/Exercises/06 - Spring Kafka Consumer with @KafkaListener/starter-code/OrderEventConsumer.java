package com.example.kafka.consumer;

// Reflection answers (fill in before writing the listener code):
//
// Q1: Why does the second listener use a different groupId?
//     What would happen if both methods used "order-processor"?
//     TODO:
//
// Q2: If the topic has 3 partitions and concurrency=3, how many threads does Spring create?
//     TODO:
//
// Q3: What does TRUSTED_PACKAGES = "*" do, and why is it a security concern in production?
//     TODO:

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
     * Group: order-processor
     */
    // TODO: Add @KafkaListener for topic "order-events" and groupId "order-processor"
    public void handleOrderEvent(OrderEvent event) {
        // TODO: Log orderId, status, and amount
    }

    /**
     * Listener 2: Receives the full ConsumerRecord to access Kafka metadata.
     * Group: order-metadata-inspector (independent group — receives all messages separately)
     */
    // TODO: Add @KafkaListener for topic "order-events" and groupId "order-metadata-inspector"
    public void handleOrderEventWithMetadata(ConsumerRecord<String, OrderEvent> record) {
        // TODO: Log topic, partition, offset, key, and orderId from record.value()
    }
}

package com.example.kafka.consumer;

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
     * Main listener. Simulates failures based on event status:
     * - "FAIL"    → throws RuntimeException  (retried, then sent to DLT after exhaustion)
     * - "INVALID" → throws IllegalArgumentException (non-retryable, goes straight to DLT)
     * - otherwise → processes normally
     */
    // TODO: Add @KafkaListener for topic "order-events", groupId "order-error-handler"
    public void handleOrderEvent(OrderEvent event) {
        // TODO: If status == "FAIL", throw new RuntimeException("Simulated transient failure")

        // TODO: If status == "INVALID", throw new IllegalArgumentException("Non-retryable: invalid event")

        // TODO: Otherwise, log "Processing order: {orderId}"
    }

    /**
     * DLT listener. Receives messages that exhausted all retries (or were non-retryable).
     * Uses a separate consumer group to avoid offset conflicts with the main group.
     */
    // TODO: Add @KafkaListener for topic "order-events.DLT", groupId "order-dlt-inspector"
    public void handleDlt(ConsumerRecord<String, Object> record) {
        // TODO: Log "[DLT] Received failed message: topic={}, partition={}, offset={}, value={}"
    }
}

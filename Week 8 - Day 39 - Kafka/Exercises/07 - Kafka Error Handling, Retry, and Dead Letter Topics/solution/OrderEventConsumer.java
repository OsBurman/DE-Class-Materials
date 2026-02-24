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
     * Main listener. Simulates failures based on event status.
     * - "FAIL"    → RuntimeException (retried up to 3x, then sent to order-events.DLT)
     * - "INVALID" → IllegalArgumentException (non-retryable, skips retries, goes to DLT)
     * - other     → processed normally
     */
    @KafkaListener(topics = "order-events", groupId = "order-error-handler")
    public void handleOrderEvent(OrderEvent event) {
        if ("FAIL".equals(event.getStatus())) {
            log.error("Processing failed for orderId={}, will retry...", event.getOrderId());
            throw new RuntimeException("Simulated transient failure for orderId=" + event.getOrderId());
        }
        if ("INVALID".equals(event.getStatus())) {
            throw new IllegalArgumentException("Non-retryable: invalid event for orderId=" + event.getOrderId());
        }
        log.info("Processing order: {}", event.getOrderId());
    }

    /**
     * DLT listener. All messages that exhausted retries (or were non-retryable) arrive here.
     * Separate consumer group ensures its offsets are tracked independently.
     */
    @KafkaListener(topics = "order-events.DLT", groupId = "order-dlt-inspector")
    public void handleDlt(ConsumerRecord<String, Object> record) {
        log.warn("[DLT] Received failed message: topic={}, partition={}, offset={}, value={}",
                record.topic(),
                record.partition(),
                record.offset(),
                record.value());
    }
}

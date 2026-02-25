package com.academy.kafka.consumer;

import com.academy.kafka.model.OrderEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.retrytopic.TopicSuffixingStrategy;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Service;

/**
 * Inventory Consumer — reserves stock when an order is created.
 * Demonstrates Dead Letter Queue (DLQ) pattern.
 *
 * TODO Task 5: Implement the listener with retry + DLQ.
 */
@Slf4j
@Service
public class InventoryConsumer {

    // TODO Task 5a: Add @RetryableTopic to auto-retry on failure with DLQ
    // @RetryableTopic(
    // attempts = "3",
    // backoff = @Backoff(delay = 1000, multiplier = 2),
    // dltTopicSuffix = ".DLQ",
    // topicSuffixingStrategy = TopicSuffixingStrategy.SUFFIX_WITH_INDEX_VALUE
    // )
    @KafkaListener(topics = "${kafka.topics.orders}", groupId = "inventory-group")
    public void handleOrderEvent(@Payload OrderEvent event) {
        log.info("[Inventory] Processing order: orderId={}, productId={}, qty={}",
                event.getOrderId(), event.getProductId(), event.getQuantity());

        // TODO Task 5b: Simulate stock reservation
        // Throw an exception to test the DLQ — uncomment the line below:
        // if ("P999".equals(event.getProductId())) throw new RuntimeException("Product
        // not found in inventory");

        log.info("[Inventory] ✅ Reserved {} units of {} for order {}",
                event.getQuantity(), event.getProductId(), event.getOrderId());
    }

    // TODO Task 5c: Handle messages that failed all retries (landed in DLQ)
    @DltHandler
    public void handleDlt(@Payload OrderEvent event,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
        log.error("[Inventory DLQ] ❌ Failed event from topic '{}': orderId={}", topic, event.getOrderId());
        // TODO: alert ops team, store in error database, etc.
    }
}

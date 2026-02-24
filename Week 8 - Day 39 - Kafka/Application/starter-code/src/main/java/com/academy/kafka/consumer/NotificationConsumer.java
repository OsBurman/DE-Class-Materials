package com.academy.kafka.consumer;

import com.academy.kafka.model.OrderEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.retrytopic.TopicSuffixingStrategy;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Service;

/**
 * Notification Consumer — listens to the orders topic and sends notifications.
 *
 * TODO Task 4: Implement the listener.
 */
@Slf4j
@Service
public class NotificationConsumer {

    // TODO Task 4a: Implement the @KafkaListener
    // topics = "${kafka.topics.orders}", groupId = "notification-group"
    @KafkaListener(topics = "${kafka.topics.orders}", groupId = "notification-group")
    public void handleOrderEvent(
            @Payload OrderEvent event,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset) {

        log.info("[Notification] Received: orderId={}, status={}, partition={}, offset={}",
            event.getOrderId(), event.getStatus(), partition, offset);

        // TODO Task 4b: Send appropriate notification based on order status
        switch (event.getStatus()) {
            case "CREATED"   -> log.info("[Notification] 📧 Order confirmation sent to customer {}", event.getCustomerId());
            case "CONFIRMED" -> log.info("[Notification] 📦 Order {} confirmed — preparing shipment", event.getOrderId());
            case "SHIPPED"   -> log.info("[Notification] 🚚 Shipping notification sent for order {}", event.getOrderId());
            case "CANCELLED" -> log.info("[Notification] ❌ Cancellation confirmation sent for order {}", event.getOrderId());
            default          -> log.warn("[Notification] Unknown status: {}", event.getStatus());
        }
    }
}

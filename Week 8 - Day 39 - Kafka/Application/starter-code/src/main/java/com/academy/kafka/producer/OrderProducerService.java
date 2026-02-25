package com.academy.kafka.producer;

import com.academy.kafka.model.OrderEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

/**
 * Order Producer — publishes OrderEvent messages to Kafka.
 *
 * TODO Task 3: Implement the send methods.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OrderProducerService {

    private final KafkaTemplate<String, OrderEvent> kafkaTemplate;

    @Value("${kafka.topics.orders}")
    private String ordersTopic;

    // TODO Task 3a: Send an order event (key = orderId for partition affinity)
    public void publishOrderEvent(OrderEvent event) {
        log.info("Publishing order event: orderId={}, status={}", event.getOrderId(), event.getStatus());

        // TODO: use kafkaTemplate.send(topic, key, value)
        // The key (orderId) ensures all events for the same order go to the same
        // partition

        CompletableFuture<SendResult<String, OrderEvent>> future = kafkaTemplate.send(ordersTopic, event.getOrderId(),
                event);

        future.whenComplete((result, ex) -> {
            if (ex != null) {
                // TODO Task 3b: Handle send failure — log the error
                log.error("Failed to send order event for order {}: {}", event.getOrderId(), ex.getMessage());
            } else {
                // TODO Task 3c: Log success with partition and offset info
                log.info("Order event sent: orderId={}, partition={}, offset={}",
                        event.getOrderId(),
                        result.getRecordMetadata().partition(),
                        result.getRecordMetadata().offset());
            }
        });
    }
}

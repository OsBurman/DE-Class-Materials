package com.example.kafka.producer;

import com.example.kafka.model.OrderEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderEventProducer {

    private static final Logger log = LoggerFactory.getLogger(OrderEventProducer.class);
    private static final String TOPIC = "order-events";

    private final KafkaTemplate<String, OrderEvent> kafkaTemplate;

    public OrderEventProducer(KafkaTemplate<String, OrderEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    /**
     * Sends a single OrderEvent to Kafka, keyed by orderId for partition routing.
     * Uses CompletableFuture callback to log result without blocking.
     */
    public void sendOrderEvent(OrderEvent event) {
        kafkaTemplate.send(TOPIC, event.getOrderId(), event)
                .whenComplete((result, ex) -> {
                    if (ex == null) {
                        log.info("Successfully sent OrderEvent: orderId={} → partition={}, offset={}",
                                event.getOrderId(),
                                result.getRecordMetadata().partition(),
                                result.getRecordMetadata().offset());
                    } else {
                        log.error("Failed to send OrderEvent: orderId={}, error={}",
                                event.getOrderId(), ex.getMessage());
                    }
                });
    }

    /**
     * Sends a batch of OrderEvents. Each event is sent independently and non-blocking.
     */
    public void sendBatch(List<OrderEvent> events) {
        events.forEach(this::sendOrderEvent);
    }
}

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

    // TODO: Declare the topic name as a static final String "order-events"

    // TODO: Inject KafkaTemplate<String, OrderEvent> via constructor injection

    /**
     * Sends a single OrderEvent to Kafka, keyed by orderId.
     * Log the partition and offset on success, or the exception on failure.
     */
    public void sendOrderEvent(OrderEvent event) {
        // TODO: Call kafkaTemplate.send(TOPIC, event.getOrderId(), event)
        // TODO: Chain .whenComplete((result, ex) -> { ... }) to handle success/failure
        //   On success:  log partition + offset from result.getRecordMetadata()
        //   On failure:  log the exception message
    }

    /**
     * Sends a batch of OrderEvents by calling sendOrderEvent for each.
     */
    public void sendBatch(List<OrderEvent> events) {
        // TODO: Iterate over events and call sendOrderEvent for each
    }
}

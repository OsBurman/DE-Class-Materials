package com.academy.kafka.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * OrderEvent — the message payload sent to Kafka.
 * This class is COMPLETE.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderEvent {
    private String orderId;
    private String customerId;
    private String productId;
    private int quantity;
    private BigDecimal totalPrice;
    private String status; // CREATED, CONFIRMED, SHIPPED, CANCELLED
    private Instant timestamp;
    private String source; // service that published the event
}

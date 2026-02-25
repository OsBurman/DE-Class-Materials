package com.academy.kafka.controller;

import com.academy.kafka.model.OrderEvent;
import com.academy.kafka.producer.OrderProducerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;

/**
 * Order Controller — triggers Kafka events via REST.
 *
 * TODO Task 6: Implement the endpoints.
 */
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderProducerService producerService;

    // TODO Task 6a: POST /api/orders — create an order and publish CREATED event
    @PostMapping
    public ResponseEntity<Map<String, Object>> createOrder(@RequestBody Map<String, Object> body) {
        OrderEvent event = OrderEvent.builder()
                .orderId(UUID.randomUUID().toString())
                .customerId((String) body.getOrDefault("customerId", "CUST-001"))
                .productId((String) body.getOrDefault("productId", "P001"))
                .quantity((int) body.getOrDefault("quantity", 1))
                .totalPrice(new BigDecimal(body.getOrDefault("totalPrice", "99.99").toString()))
                .status("CREATED")
                .timestamp(Instant.now())
                .source("order-service")
                .build();

        producerService.publishOrderEvent(event);

        return ResponseEntity.status(201).body(Map.of(
                "orderId", event.getOrderId(),
                "status", event.getStatus(),
                "message", "Order event published to Kafka"));
    }

    // TODO Task 6b: PUT /api/orders/{orderId}/status
    // Publish a status-change event (e.g., CONFIRMED, SHIPPED, CANCELLED)
    @PutMapping("/{orderId}/status")
    public ResponseEntity<Map<String, Object>> updateStatus(
            @PathVariable String orderId,
            @RequestBody Map<String, String> body) {

        String newStatus = body.get("status");
        OrderEvent event = OrderEvent.builder()
                .orderId(orderId)
                .status(newStatus)
                .timestamp(Instant.now())
                .source("order-service")
                .build();

        producerService.publishOrderEvent(event);

        return ResponseEntity.ok(Map.of(
                "orderId", orderId,
                "newStatus", newStatus,
                "message", "Status update event published"));
    }
}

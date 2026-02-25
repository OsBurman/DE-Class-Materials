package com.academy.order.controller;

import com.academy.order.client.ProductClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Order Controller.
 *
 * TODO Task 6: Implement the order endpoints.
 * Use ProductClient to call the product-service.
 */
@Slf4j
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final ProductClient productClient;
    private final List<Map<String, Object>> orders = new ArrayList<>();
    private final AtomicLong nextId = new AtomicLong(1);

    // TODO Task 6a: GET /api/orders — list all orders
    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getOrders() {
        return ResponseEntity.ok(orders);
    }

    // TODO Task 6b: POST /api/orders
    // Body: { "productId": "P001", "quantity": 2, "customerId": "C001" }
    // Steps:
    // 1. Call productClient.checkAvailability() — if not available, return 409
    // 2. Call productClient.getProduct() to get product details
    // 3. Create order with status "PENDING"
    // 4. Return 201
    @PostMapping
    public ResponseEntity<Map<String, Object>> createOrder(@RequestBody Map<String, Object> body) {
        String productId = (String) body.get("productId");
        int quantity = (int) body.get("quantity");

        // TODO Step 1: Check availability via Feign client
        Map<String, Object> availability = productClient.checkAvailability(productId, quantity);
        if (!(boolean) availability.getOrDefault("available", false)) {
            return ResponseEntity.status(409).body(Map.of(
                    "error", "Product not available",
                    "productId", productId,
                    "requestedQuantity", quantity));
        }

        // TODO Step 2: Get product details
        Map<String, Object> product = productClient.getProduct(productId);

        // TODO Step 3: Build and save order
        Map<String, Object> order = new HashMap<>();
        order.put("orderId", "ORD-" + nextId.getAndIncrement());
        order.put("productId", productId);
        order.put("productName", product.getOrDefault("name", "Unknown"));
        order.put("quantity", quantity);
        order.put("status", "PENDING");
        order.put("customerId", body.get("customerId"));
        order.put("createdAt", Instant.now().toString());
        orders.add(order);

        log.info("Created order {}", order.get("orderId"));
        return ResponseEntity.status(201).body(order);
    }
}

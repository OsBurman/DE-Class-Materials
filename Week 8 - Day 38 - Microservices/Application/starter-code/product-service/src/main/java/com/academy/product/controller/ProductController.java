package com.academy.product.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.*;

/**
 * Product Controller — simple in-memory product catalog.
 *
 * TODO Task 3: Implement GET all, GET by ID, POST, DELETE endpoints.
 */
@Slf4j
@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final Map<String, Map<String, Object>> products = new LinkedHashMap<>(Map.of(
        "P001", Map.of("id", "P001", "name", "Laptop Pro",    "price", new BigDecimal("1299.99"), "stock", 50),
        "P002", Map.of("id", "P002", "name", "Wireless Mouse","price", new BigDecimal("29.99"),   "stock", 200),
        "P003", Map.of("id", "P003", "name", "USB-C Hub",     "price", new BigDecimal("49.99"),   "stock", 150)
    ));

    // TODO Task 3a: GET /api/products
    @GetMapping
    public ResponseEntity<Collection<Map<String, Object>>> getAllProducts() {
        log.info("Getting all products");
        return ResponseEntity.ok(products.values());
    }

    // TODO Task 3b: GET /api/products/{id}
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getProduct(@PathVariable String id) {
        // TODO: return 404 if not found
        var product = products.get(id);
        if (product == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(product);
    }

    // TODO Task 3c: Check stock availability (called by order-service)
    @GetMapping("/{id}/available")
    public ResponseEntity<Map<String, Object>> checkAvailability(
            @PathVariable String id,
            @RequestParam(defaultValue = "1") int quantity) {
        var product = products.get(id);
        if (product == null) return ResponseEntity.notFound().build();
        int stock = (int) product.get("stock");
        return ResponseEntity.ok(Map.of(
            "productId", id,
            "requestedQuantity", quantity,
            "available", stock >= quantity,
            "currentStock", stock
        ));
    }
}

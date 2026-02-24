package com.academy.order.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Fallback implementation for ProductClient when product-service is down.
 *
 * TODO Task 5: Implement fallback responses.
 * This class is used by the Resilience4J circuit breaker.
 */
@Slf4j
@Component
public class ProductClientFallback implements ProductClient {

    @Override
    public Map<String, Object> getProduct(String id) {
        log.warn("product-service unavailable — returning fallback for product {}", id);
        // TODO: return a meaningful default/error response
        return Map.of("id", id, "error", "product-service unavailable", "available", false);
    }

    @Override
    public Map<String, Object> checkAvailability(String id, int quantity) {
        log.warn("product-service unavailable — returning fallback availability for product {}", id);
        // TODO: return false availability
        return Map.of("productId", id, "available", false, "error", "product-service unavailable");
    }
}

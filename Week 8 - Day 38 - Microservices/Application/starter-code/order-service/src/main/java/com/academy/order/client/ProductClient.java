package com.academy.order.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

/**
 * Feign client for the product-service.
 *
 * TODO Task 4: Complete the Feign client.
 * The @FeignClient name must match the spring.application.name of product-service.
 */
// TODO Task 4a: Add @FeignClient(name = "product-service", fallback = ProductClientFallback.class)
public interface ProductClient {

    // TODO Task 4b: Declare the GET /api/products/{id} endpoint
    @GetMapping("/api/products/{id}")
    Map<String, Object> getProduct(@PathVariable("id") String id);

    // TODO Task 4c: Declare the GET /api/products/{id}/available endpoint
    @GetMapping("/api/products/{id}/available")
    Map<String, Object> checkAvailability(@PathVariable("id") String id,
                                          @RequestParam("quantity") int quantity);
}

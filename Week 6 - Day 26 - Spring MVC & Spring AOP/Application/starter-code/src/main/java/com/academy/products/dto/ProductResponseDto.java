package com.academy.products.dto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO returned in API responses.
 *
 * TODO Task 3: Add a formattedPrice field (String, e.g., "$19.99")
 * - Use BigDecimal.setScale(2) and String.format("$%.2f", price)
 * - Compute it in the constructor or a static factory method
 * - No setter — immutable response DTO
 */
@Getter
@Builder
public class ProductResponseDto {

    private Long id;
    private String name;
    private String description;
    private BigDecimal price;

    // TODO Task 3: Add formattedPrice field and compute it
    // private String formattedPrice;

    private String category;
    private int stockQuantity;
    private LocalDateTime createdAt;
}

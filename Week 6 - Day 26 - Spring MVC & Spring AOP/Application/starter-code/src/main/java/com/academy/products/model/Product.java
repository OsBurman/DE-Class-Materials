package com.academy.products.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Product domain entity.
 * Stored in-memory — no database needed for this exercise.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    private Long id;

    // TODO Task 1: All fields are declared. Your job is in the DTOs and service layer.

    private String name;
    private String description;
    private BigDecimal price;
    private String category;
    private int stockQuantity;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
}

package com.academy.products.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

/**
 * DTO for creating or updating a product.
 *
 * TODO Task 2: Add the following validation annotations:
 *   - name:          @NotBlank(message = "Name is required")
 *   - description:   @Size(max = 500)
 *   - price:         @NotNull + @DecimalMin(value = "0.0", inclusive = false)
 *   - stockQuantity: @Min(0)
 *   - category:      @NotBlank
 */
@Data
public class ProductRequestDto {

    // TODO: add @NotBlank
    private String name;

    // TODO: add @Size(max = 500)
    private String description;

    // TODO: add @NotNull and @DecimalMin
    private BigDecimal price;

    // TODO: add @NotBlank
    private String category;

    // TODO: add @Min(0)
    private int stockQuantity;
}

package com.academy.mongodb.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

/**
 * Ingredient — embedded in Recipe (not a separate collection).
 * This class is COMPLETE.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Ingredient {
    @NotBlank
    private String name;
    private String quantity;   // e.g., "2 cups", "1 tsp"
    private String unit;       // optional alternative unit
}

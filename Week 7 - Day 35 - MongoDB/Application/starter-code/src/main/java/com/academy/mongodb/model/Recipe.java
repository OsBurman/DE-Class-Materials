package com.academy.mongodb.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Recipe document.
 *
 * TODO Task 1: Complete the MongoDB annotations.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
// TODO Task 1a: Add @Document(collection = "recipes")
public class Recipe {

    // TODO Task 1b: Add @Id annotation
    private String id;

    @NotBlank
    private String title;

    private String description;

    // TODO Task 1c: Add @NotNull and list of Ingredient (embedded document, no @Document needed)
    @Builder.Default
    private List<Ingredient> ingredients = new ArrayList<>();

    private String category;   // e.g., "Italian", "Dessert", "Vegan"

    private int prepTimeMinutes;
    private int cookTimeMinutes;

    // TODO Task 1d: Add @DBRef (lazy=true) — maps to an Author document in a separate collection
    private AuthorSnapshot author;  // embedded snapshot (denormalized)

    @Builder.Default
    private List<String> tags = new ArrayList<>();

    @Builder.Default
    private List<Rating> ratings = new ArrayList<>();

    private Instant createdAt;
    private Instant updatedAt;

    // Computed field — not stored in DB
    public Double getAverageRating() {
        if (ratings == null || ratings.isEmpty()) return null;
        return ratings.stream().mapToInt(Rating::getScore).average().orElse(0);
    }
}

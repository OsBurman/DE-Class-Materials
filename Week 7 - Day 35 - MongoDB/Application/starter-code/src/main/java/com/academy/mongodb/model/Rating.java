package com.academy.mongodb.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import java.time.Instant;

/**
 * Rating — embedded in Recipe.
 * This class is COMPLETE.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Rating {
    @Min(1) @Max(5)
    private int score;
    private String reviewer;
    private String comment;
    private Instant createdAt;
}

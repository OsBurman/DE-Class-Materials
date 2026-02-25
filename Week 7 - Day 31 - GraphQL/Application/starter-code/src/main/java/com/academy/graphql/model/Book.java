package com.academy.graphql.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * Book entity — this class is COMPLETE.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Book {
    private String id;
    private String title;
    private String genre;
    private int publishedYear;
    private String authorId;

    @Builder.Default
    private List<Review> reviews = new ArrayList<>();

    public Double getAverageRating() {
        if (reviews.isEmpty())
            return null;
        return reviews.stream()
                .mapToInt(Review::getRating)
                .average()
                .orElse(0.0);
    }
}

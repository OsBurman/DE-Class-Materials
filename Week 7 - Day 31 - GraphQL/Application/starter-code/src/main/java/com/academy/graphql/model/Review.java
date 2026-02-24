package com.academy.graphql.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Review entity — this class is COMPLETE.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Review {
    private String id;
    private int rating;
    private String comment;
    private String reviewer;
}

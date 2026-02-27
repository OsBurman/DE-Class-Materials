package com.academy.mongodb.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * AuthorSnapshot — denormalized author info embedded in Recipe.
 * Avoids a $lookup (JOIN) on every read.
 * This class is COMPLETE.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthorSnapshot {
    private String authorId;
    private String name;
    private String avatarUrl;
}

package com.academy.graphql.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Author entity — this class is COMPLETE.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Author {
    private String id;
    private String name;
    private String bio;
}

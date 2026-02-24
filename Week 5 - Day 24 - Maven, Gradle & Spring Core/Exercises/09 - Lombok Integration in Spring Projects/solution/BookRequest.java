package com.library;

import lombok.Builder;
import lombok.Getter;

/**
 * Data Transfer Object for creating a new book.
 *
 * @Getter  — generates getTitle() and getAuthorId() (read-only DTO, no setters)
 * @Builder — generates a static nested Builder class, accessible via:
 *
 *   BookRequest request = BookRequest.builder()
 *                                    .title("Effective Java")
 *                                    .authorId(1)
 *                                    .build();
 *
 * The builder pattern is preferred for DTOs and value objects because it:
 *   - Makes construction readable (named parameters)
 *   - Handles optional fields gracefully (only set what you need)
 *   - Supports immutability (no setters required)
 */
@Getter
@Builder
public class BookRequest {

    private String title;
    private int authorId;
}

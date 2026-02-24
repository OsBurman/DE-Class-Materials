package com.library;

// TODO: Import the necessary Lombok annotations: @Builder, @Getter

/**
 * Data Transfer Object used to create a new book.
 *
 * Immutable-style DTO — getters only, no setters.
 * Clients construct instances using the builder pattern.
 *
 * TODO:
 *   1. Add @Getter   — generates getTitle() and getAuthorId()
 *   2. Add @Builder  — generates a static builder() method so callers can write:
 *                      BookRequest.builder().title("X").authorId(1).build()
 */
public class BookRequest {

    private String title;
    private int authorId;
}

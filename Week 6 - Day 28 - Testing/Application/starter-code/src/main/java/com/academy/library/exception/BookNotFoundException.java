package com.academy.library.exception;

/**
 * Thrown when a book is not found by ID or ISBN.
 * This class is COMPLETE.
 */
public class BookNotFoundException extends RuntimeException {
    public BookNotFoundException(String message) {
        super(message);
    }

    public BookNotFoundException(Long id) {
        super("Book not found with id: " + id);
    }
}

package com.academy.library.exception;

/**
 * Thrown when a book is already checked out and cannot be borrowed.
 * This class is COMPLETE.
 */
public class BookNotAvailableException extends RuntimeException {
    public BookNotAvailableException(String isbn) {
        super("Book is not available: " + isbn);
    }
}

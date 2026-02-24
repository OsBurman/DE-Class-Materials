package com.library;

import java.util.Optional;

/**
 * Simulates a data access layer for books.
 * In a real application this would use Spring Data JPA.
 * For this exercise it acts as a simple stub.
 */
public class BookRepository {

    public Optional<Book> findById(int id) {
        if (id <= 0) {
            return Optional.empty();
        }
        return Optional.of(new Book(id, "Simulated Book #" + id, 1));
    }
}

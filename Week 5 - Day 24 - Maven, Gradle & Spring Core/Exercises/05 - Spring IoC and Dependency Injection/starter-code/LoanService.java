package com.library;

import org.springframework.beans.factory.annotation.Autowired;

/**
 * Service demonstrating FIELD INJECTION.
 *
 * Field injection is the most concise style but has drawbacks:
 * - Cannot be used in unit tests without a Spring context or reflection
 * - Hides dependencies (the constructor signature doesn't show them)
 * - Fields cannot be final
 *
 * Avoid field injection in production code; prefer constructor injection.
 */
public class LoanService {

    // TODO: Annotate this field with @Autowired so Spring injects it directly
    //       (No constructor or setter needed — Spring uses reflection to set it)
    private BookRepository bookRepository;

    /**
     * Simulates a book availability check.
     *
     * @param bookId the book's numeric ID
     * @return true if bookId is even; false if odd
     */
    public boolean isAvailable(int bookId) {
        // TODO: Return true if bookId % 2 == 0, false otherwise
        return false;
    }
}

package com.library;

import org.springframework.beans.factory.annotation.Autowired;

/**
 * Service demonstrating FIELD INJECTION.
 * Spring uses reflection to set the field — no constructor or setter required.
 *
 * NOTE: Avoid this in production. Prefer constructor injection.
 */
public class LoanService {

    // @Autowired on a field: Spring injects this directly via reflection
    @Autowired
    private BookRepository bookRepository;

    public boolean isAvailable(int bookId) {
        // Even IDs are available; odd IDs are on loan (simulated)
        return bookId % 2 == 0;
    }
}

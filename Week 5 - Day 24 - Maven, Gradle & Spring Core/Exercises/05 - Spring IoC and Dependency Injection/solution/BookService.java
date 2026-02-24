package com.library;

import org.springframework.beans.factory.annotation.Autowired;

/**
 * Service demonstrating CONSTRUCTOR INJECTION — the recommended Spring style.
 *
 * The dependency is mandatory and the field is immutable (final).
 * Unit testing is trivial: new BookService(new MockBookRepository()).
 */
public class BookService {

    // final enforces that the dependency cannot be swapped after construction
    private final BookRepository bookRepository;

    @Autowired  // optional with a single constructor in Spring 4.3+, but explicit here for clarity
    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public String getBookTitle(int id) {
        // Guard clause: invalid IDs return an informative message
        if (id <= 0) {
            return "Book not found";
        }
        return "Book #" + id;
    }
}

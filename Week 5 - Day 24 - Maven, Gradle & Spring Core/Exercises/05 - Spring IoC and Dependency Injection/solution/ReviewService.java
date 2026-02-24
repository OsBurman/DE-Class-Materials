package com.library;

import org.springframework.beans.factory.annotation.Autowired;

/**
 * Service demonstrating SETTER INJECTION.
 * Spring calls setBookRepository() after constructing the object.
 */
public class ReviewService {

    // Not final — setter injection sets this after construction
    private BookRepository bookRepository;

    @Autowired
    public void setBookRepository(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public int getReviewCount(int bookId) {
        // Simulated: each book has bookId * 3 reviews
        return bookId * 3;
    }
}

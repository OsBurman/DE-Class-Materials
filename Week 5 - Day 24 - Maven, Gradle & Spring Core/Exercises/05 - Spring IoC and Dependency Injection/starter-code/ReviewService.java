package com.library;

import org.springframework.beans.factory.annotation.Autowired;

/**
 * Service demonstrating SETTER INJECTION.
 *
 * Setter injection is appropriate when:
 * - A dependency is optional (the class can work without it)
 * - You need to change the dependency after construction (rare)
 */
public class ReviewService {

    // TODO: Declare a private BookRepository field named 'bookRepository'
    //       (not final — setter injection sets it after construction)


    // TODO: Write a public setter method: public void setBookRepository(BookRepository bookRepository)
    //       Annotate it with @Autowired so Spring calls it automatically
    //       Assign the parameter to the field


    /**
     * Returns a simulated review count for a given book ID.
     *
     * @param bookId the book's numeric ID
     * @return bookId multiplied by 3 (simulated data)
     */
    public int getReviewCount(int bookId) {
        // TODO: Return bookId * 3
        return 0;
    }
}

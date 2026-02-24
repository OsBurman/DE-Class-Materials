package com.library;

import org.springframework.beans.factory.annotation.Autowired;

/**
 * Service demonstrating CONSTRUCTOR INJECTION.
 *
 * Constructor injection is the recommended style:
 * - Dependencies are mandatory (the object cannot be created without them)
 * - Fields can be declared final (immutability)
 * - Easier to test: you can call new BookService(mockRepo) in unit tests
 */
public class BookService {

    // TODO: Declare a private final BookRepository field named 'bookRepository'
    //       Mark it final — constructor injection allows this


    // TODO: Add a constructor that accepts a BookRepository parameter
    //       Annotate it with @Autowired (required for explicit Java config in this exercise)
    //       Assign the parameter to the field


    /**
     * Returns the title for the given book ID.
     *
     * @param id the book's numeric ID
     * @return "Book #N" for valid IDs; "Book not found" for id <= 0
     */
    public String getBookTitle(int id) {
        // TODO: Return "Book #" + id for positive IDs
        //       Return "Book not found" for id <= 0
        return null;
    }
}

package com.academy.library.service;

import com.academy.library.exception.BookNotAvailableException;
import com.academy.library.exception.BookNotFoundException;
import com.academy.library.model.Book;
import com.academy.library.repository.BookRepository;

/**
 * Library service — manages book checkout and return.
 *
 * ⚠️ TDD INSTRUCTIONS:
 * DO NOT look at this file first!
 * Write your tests in LibraryServiceTest.java BEFORE implementing this class.
 * Follow: Red → Green → Refactor
 *
 * The class skeleton is here to help your tests compile.
 * All methods throw UnsupportedOperationException until you implement them.
 */
public class LibraryService {

    private final BookRepository bookRepository;

    public LibraryService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    /**
     * Check out a book by ISBN.
     * - If the book doesn't exist: throw BookNotFoundException
     * - If the book is already checked out (available == false): throw
     * BookNotAvailableException
     * - Otherwise: set available = false, save, and return the book
     *
     * TODO Task 5: Implement this method AFTER writing the tests.
     */
    public Book checkOutBook(String isbn) {
        throw new UnsupportedOperationException("TODO: implement checkOutBook");
    }

    /**
     * Return a book by ISBN.
     * - If the book doesn't exist: throw BookNotFoundException
     * - Otherwise: set available = true, save, and return the book
     *
     * TODO Task 5: Implement this method AFTER writing the tests.
     */
    public Book returnBook(String isbn) {
        throw new UnsupportedOperationException("TODO: implement returnBook");
    }
}

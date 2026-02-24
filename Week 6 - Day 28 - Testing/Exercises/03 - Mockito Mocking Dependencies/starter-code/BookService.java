package com.testing;

import java.util.List;
import java.util.Optional;

/**
 * Service layer that orchestrates Book operations.
 * All data access is delegated to BookRepository — do NOT modify this class.
 */
public class BookService {

    private final BookRepository bookRepository;

    // Constructor injection — Mockito's @InjectMocks will use this constructor
    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    /** Returns every book in the repository. */
    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    /**
     * Persists a book and returns the saved instance (with its generated id).
     */
    public Book saveBook(Book book) {
        return bookRepository.save(book);
    }

    /**
     * Finds a book by id.
     *
     * @throws RuntimeException if no book exists with the given id
     */
    public Book findById(Long id) {
        Optional<Book> book = bookRepository.findById(id);
        return book.orElseThrow(() ->
                new RuntimeException("Book not found with id: " + id));
    }

    /** Deletes the book with the given id. */
    public void deleteBook(Long id) {
        bookRepository.deleteById(id);
    }
}

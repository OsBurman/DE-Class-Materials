package com.exercise.library.service;

import com.exercise.library.model.Book;
import com.exercise.library.model.LibraryStats;
import com.exercise.library.repository.BookRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

// TODO 2: Add @Service to mark this as a Spring-managed service bean.
//         Spring will discover it during component scan and register it in the application context.
//
// This class implements BookService — it provides the actual business logic.
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;

    // TODO 3: This constructor enables constructor injection.
    //         Spring will inject BookRepository automatically (no @Autowired needed).
    //         The `final` keyword ensures the field is set once and never changed.
    public BookServiceImpl(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    // TODO 4: Implement getAllBooks() — return all books from the repository
    @Override
    public List<Book> getAllBooks() {
        // your code here
        return null;
    }

    // TODO 5: Implement getBookById(Long id) — return Optional<Book> from repository
    @Override
    public Optional<Book> getBookById(Long id) {
        // your code here
        return null;
    }

    // TODO 6: Implement getAvailableBooks() — return only books where isAvailable() == true
    //         Hint: Use bookRepository.findByAvailableTrue()
    @Override
    public List<Book> getAvailableBooks() {
        // your code here
        return null;
    }

    // TODO 7: Implement addBook(Book book)
    //         - Set book.setAddedDate(LocalDate.now()) before saving
    //         - Set book.setAvailable(true) since new books start as available
    //         - Save and return the book
    @Override
    public Book addBook(Book book) {
        // your code here
        return null;
    }

    // TODO 8: Implement checkOutBook(Long id)
    //         1. Find the book: bookRepository.findById(id)
    //            - If not found: throw new NoSuchElementException("Book not found: " + id)
    //         2. Check availability:
    //            - If !book.isAvailable(): throw new IllegalStateException("Book is already checked out")
    //         3. Update: book.setAvailable(false)
    //         4. Save and return
    @Override
    public Book checkOutBook(Long id) {
        // your code here
        return null;
    }

    // TODO 9: Implement returnBook(Long id)
    //         1. Find the book or throw NoSuchElementException("Book not found: " + id)
    //         2. If book.isAvailable(): throw new IllegalStateException("Book is not checked out")
    //         3. Set available = true, save, return
    @Override
    public Book returnBook(Long id) {
        // your code here
        return null;
    }

    // TODO 10: Implement removeBook(Long id) — delegate to bookRepository.deleteById(id)
    @Override
    public boolean removeBook(Long id) {
        // your code here
        return false;
    }

    // TODO 11: Implement getStats()
    //          - Get all books
    //          - Count total, available, and checked-out
    //          - Return a new LibraryStats(total, available, checkedOut)
    //          Hint: Use stream().filter(...).count()
    @Override
    public LibraryStats getStats() {
        // your code here
        return null;
    }
}

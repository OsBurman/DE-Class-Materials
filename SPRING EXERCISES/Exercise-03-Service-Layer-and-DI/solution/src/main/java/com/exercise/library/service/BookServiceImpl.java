package com.exercise.library.service;

import com.exercise.library.model.Book;
import com.exercise.library.model.LibraryStats;
import com.exercise.library.repository.BookRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;

    public BookServiceImpl(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @Override
    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    @Override
    public Optional<Book> getBookById(Long id) {
        return bookRepository.findById(id);
    }

    @Override
    public List<Book> getAvailableBooks() {
        return bookRepository.findByAvailableTrue();
    }

    @Override
    public Book addBook(Book book) {
        book.setAvailable(true);
        book.setAddedDate(LocalDate.now());
        return bookRepository.save(book);
    }

    @Override
    public Book checkOutBook(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Book not found: " + id));
        if (!book.isAvailable()) {
            throw new IllegalStateException("Book is already checked out");
        }
        book.setAvailable(false);
        return bookRepository.save(book);
    }

    @Override
    public Book returnBook(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Book not found: " + id));
        if (book.isAvailable()) {
            throw new IllegalStateException("Book is not checked out");
        }
        book.setAvailable(true);
        return bookRepository.save(book);
    }

    @Override
    public boolean removeBook(Long id) {
        return bookRepository.deleteById(id);
    }

    @Override
    public LibraryStats getStats() {
        List<Book> all = bookRepository.findAll();
        long total = all.size();
        long available = all.stream().filter(Book::isAvailable).count();
        long checkedOut = total - available;
        return new LibraryStats(total, available, checkedOut);
    }
}

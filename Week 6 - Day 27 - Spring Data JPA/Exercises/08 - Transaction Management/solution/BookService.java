package com.library.service;

import com.library.model.Book;
import com.library.repository.BookRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BookService {

    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    // All saves in one transaction — either all succeed or none do
    @Transactional
    public void saveAll(List<Book> books) {
        for (Book book : books) {
            bookRepository.save(book);
        }
    }

    // readOnly = true: JPA skips dirty-checking; database can use read-only connection optimizations
    @Transactional(readOnly = true)
    public List<Book> findAll() {
        return bookRepository.findAll();
    }

    // RuntimeException triggers automatic rollback — book1 save is undone
    @Transactional
    public void saveWithFailure(Book book1, Book book2) {
        bookRepository.save(book1);
        throw new RuntimeException("Simulated failure after first save");
        // book2 never saved; entire transaction rolled back
    }
}

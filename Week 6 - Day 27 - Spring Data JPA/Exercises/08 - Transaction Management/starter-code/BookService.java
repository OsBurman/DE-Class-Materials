package com.library.service;

import com.library.model.Book;
import com.library.repository.BookRepository;
import org.springframework.stereotype.Service;
// TODO: import @Transactional from org.springframework.transaction.annotation

import java.util.List;

@Service
public class BookService {

    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    // TODO 1: Add @Transactional — all saves succeed or none do
    public void saveAll(List<Book> books) {
        for (Book book : books) {
            bookRepository.save(book);
        }
    }

    // TODO 2: Add @Transactional(readOnly = true)
    public List<Book> findAll() {
        return bookRepository.findAll();
    }

    // TODO 3: Add @Transactional
    //         This method saves the first book, then throws RuntimeException
    //         The transaction should roll back the first save
    public void saveWithFailure(Book book1, Book book2) {
        bookRepository.save(book1);
        // TODO: throw new RuntimeException("Simulated failure after first save");
        bookRepository.save(book2);  // This line should never be reached
    }
}

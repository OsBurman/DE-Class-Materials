package com.testing;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookService {

    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public List<Book> getAllBooks() { return bookRepository.findAll(); }

    public Book findById(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Book not found: " + id));
    }

    public Book saveBook(Book book) { return bookRepository.save(book); }

    public void deleteBook(Long id) { bookRepository.deleteById(id); }
}

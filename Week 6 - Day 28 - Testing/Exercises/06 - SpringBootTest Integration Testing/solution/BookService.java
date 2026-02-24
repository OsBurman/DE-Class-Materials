package com.testing;

import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class BookService {
    private final BookRepository repo;
    public BookService(BookRepository repo) { this.repo = repo; }
    public List<Book> getAllBooks() { return repo.findAll(); }
    public Book findById(Long id) {
        return repo.findById(id).orElseThrow(() -> new RuntimeException("Not found: " + id));
    }
    public Book saveBook(Book b) { return repo.save(b); }
    public void deleteBook(Long id) { repo.deleteById(id); }
}

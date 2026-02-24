package com.library;

import com.library.model.Author;
import com.library.model.Book;
import com.library.repository.AuthorRepository;
import com.library.repository.BookRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class DataLoader implements ApplicationRunner {

    private final AuthorRepository authorRepository;
    private final BookRepository bookRepository;

    public DataLoader(AuthorRepository authorRepository, BookRepository bookRepository) {
        this.authorRepository = authorRepository;
        this.bookRepository = bookRepository;
    }

    @Override
    public void run(ApplicationArguments args) {
        System.out.println("=== Fetch Types & Cascade Demo ===\n");

        // --- Cascade PERSIST ---
        Author author = new Author("Martin Fowler");
        author.addBook(new Book("Refactoring"));
        author.addBook(new Book("Patterns of Enterprise Application Architecture"));
        author.addBook(new Book("Domain-Driven Design"));
        Author saved = authorRepository.save(author);

        System.out.println("--- After cascade PERSIST ---");
        System.out.println("Saved author with " + bookRepository.count() + " books");

        // --- LAZY load books ---
        // Re-fetch from DB so the persistence context is fresh; getBooks() triggers a second SELECT
        Author reloaded = authorRepository.findById(saved.getId()).orElseThrow();
        System.out.println("\n--- Books via lazy load ---");
        reloaded.getBooks().forEach(System.out::println);

        // --- Cascade REMOVE ---
        authorRepository.deleteById(saved.getId());
        System.out.println("\n--- After cascade REMOVE (author deleted) ---");
        System.out.println("Book count: " + bookRepository.count());

        // --- orphanRemoval ---
        Author author2 = new Author("Robert C. Martin");
        author2.addBook(new Book("Clean Code"));
        author2.addBook(new Book("Clean Architecture"));
        author2.addBook(new Book("The Clean Coder"));
        Author saved2 = authorRepository.save(author2);

        Author reloaded2 = authorRepository.findById(saved2.getId()).orElseThrow();
        // Remove the first book from the collection — orphanRemoval issues a DELETE automatically
        reloaded2.getBooks().remove(0);
        authorRepository.save(reloaded2);

        System.out.println("\n--- After orphanRemoval ---");
        System.out.println("Books remaining: " + bookRepository.count());
    }
}

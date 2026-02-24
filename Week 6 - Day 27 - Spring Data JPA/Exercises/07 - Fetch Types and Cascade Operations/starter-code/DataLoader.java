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

        // TODO: Create an Author and add 3 Books using author.addBook()
        // TODO: Save the author — CascadeType.ALL should persist the books automatically
        System.out.println("--- After cascade PERSIST ---");
        // TODO: Print "Saved author with " + bookRepository.count() + " books"

        // TODO: Reload the author using authorRepository.findById(authorId)
        //       then call author.getBooks() and print each book
        //       With FetchType.LAZY: watch for a second SELECT in the SQL output
        //       Then switch to FetchType.EAGER and re-run — compare the SQL (JOIN vs two SELECTs)
        System.out.println("\n--- Books via lazy load ---");
        // TODO: Print each book

        // TODO: Delete the author using authorRepository.deleteById(authorId)
        System.out.println("\n--- After cascade REMOVE (author deleted) ---");
        // TODO: Print "Book count: " + bookRepository.count()

        // --- orphanRemoval demo ---
        // TODO: Re-create the author with 3 books and save
        // TODO: Reload the author, remove one book from author.getBooks(), then save the author
        System.out.println("\n--- After orphanRemoval ---");
        // TODO: Print "Books remaining: " + bookRepository.count()
    }
}

package com.library;

import com.library.model.Book;
import com.library.repository.BookRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class DataLoader implements ApplicationRunner {

    private final BookRepository bookRepository;

    public DataLoader(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @Override
    public void run(ApplicationArguments args) {
        System.out.println("=== Spring Data CRUD Demo ===\n");

        // --- CREATE ---
        System.out.println("--- CREATE ---");
        // TODO: Save 3 books using bookRepository.save()
        //       Example: Book book1 = bookRepository.save(new Book("Clean Code", "Programming", 2008));
        //       Print "Saved: " + book1 after each save

        // --- READ ALL ---
        System.out.println("\n--- READ ALL ---");
        // TODO: Call bookRepository.findAll() and print each book
        //       Hint: use .forEach(System.out::println)

        // --- READ BY ID ---
        System.out.println("\n--- READ BY ID ---");
        // TODO: Call bookRepository.findById(1L) and print the result if present
        //       Hint: use .ifPresent(System.out::println)

        // --- UPDATE ---
        System.out.println("\n--- UPDATE ---");
        // TODO: Create a Book with the same id (1L) but a modified title
        //       Example: new Book(1L, "Clean Code (2nd Ed.)", "Programming", 2008)
        //       Call bookRepository.save() — JPA issues an UPDATE because the id already exists
        //       Print "Updated: " + the returned book

        // --- DELETE ---
        System.out.println("\n--- DELETE ---");
        // TODO: Call bookRepository.deleteById(2L) to remove the second book
        //       Print "Books remaining: " + bookRepository.count()
    }
}

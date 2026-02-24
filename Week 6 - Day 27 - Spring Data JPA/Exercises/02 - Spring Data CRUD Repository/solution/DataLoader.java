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
        Book book1 = bookRepository.save(new Book("Clean Code", "Programming", 2008));
        System.out.println("Saved: " + book1);

        Book book2 = bookRepository.save(new Book("The Pragmatic Programmer", "Programming", 1999));
        System.out.println("Saved: " + book2);

        Book book3 = bookRepository.save(new Book("Designing Data-Intensive Applications", "Architecture", 2017));
        System.out.println("Saved: " + book3);

        // --- READ ALL ---
        System.out.println("\n--- READ ALL ---");
        bookRepository.findAll().forEach(System.out::println);

        // --- READ BY ID ---
        System.out.println("\n--- READ BY ID ---");
        bookRepository.findById(1L).ifPresent(b -> System.out.println("Found: " + b));

        // --- UPDATE ---
        System.out.println("\n--- UPDATE ---");
        // Passing an existing id causes JPA to issue UPDATE instead of INSERT
        Book updated = bookRepository.save(new Book(1L, "Clean Code (2nd Ed.)", "Programming", 2008));
        System.out.println("Updated: " + updated);

        // --- DELETE ---
        System.out.println("\n--- DELETE ---");
        bookRepository.deleteById(2L);
        System.out.println("Books remaining: " + bookRepository.count());
        bookRepository.findAll().forEach(System.out::println);
    }
}

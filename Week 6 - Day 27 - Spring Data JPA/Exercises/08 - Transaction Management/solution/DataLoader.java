package com.library;

import com.library.model.Book;
import com.library.service.BookService;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DataLoader implements ApplicationRunner {

    private final BookService bookService;

    public DataLoader(BookService bookService) {
        this.bookService = bookService;
    }

    @Override
    public void run(ApplicationArguments args) {
        System.out.println("=== Transaction Management Demo ===\n");

        // --- Successful saveAll ---
        bookService.saveAll(List.of(
            new Book("Clean Code", "Programming", 2008),
            new Book("Refactoring", "Programming", 1999),
            new Book("The Pragmatic Programmer", "Programming", 1999)
        ));
        System.out.println("--- After saveAll ---");
        System.out.println("Book count: " + bookService.findAll().size());

        // --- saveWithFailure — should rollback ---
        try {
            bookService.saveWithFailure(
                new Book("Domain-Driven Design", "Architecture", 2003),
                new Book("Clean Architecture", "Architecture", 2017)
            );
        } catch (RuntimeException e) {
            System.out.println("\n--- After saveWithFailure (should rollback) ---");
            System.out.println("Exception caught: " + e.getMessage());
            System.out.println("Book count: " + bookService.findAll().size()); // still 3
        }

        // --- Read-only findAll ---
        System.out.println("\n--- Read-only findAll ---");
        bookService.findAll().forEach(System.out::println);
    }
}

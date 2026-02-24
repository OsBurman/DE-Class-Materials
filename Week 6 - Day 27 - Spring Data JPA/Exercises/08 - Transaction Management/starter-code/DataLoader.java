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

        // TODO: Call bookService.saveAll() with a list of 3 valid books
        System.out.println("--- After saveAll ---");
        // TODO: Print "Book count: " + bookService.findAll().size()

        // TODO: Call bookService.saveWithFailure() inside a try/catch
        //       Catch RuntimeException, print "Exception caught: " + e.getMessage()
        System.out.println("\n--- After saveWithFailure (should rollback) ---");
        // TODO: Print "Book count: " + bookService.findAll().size()
        //       Verify the count did NOT increase (rollback worked)

        // TODO: Call bookService.findAll() to demonstrate the readOnly transaction
        System.out.println("\n--- Read-only findAll ---");
        // TODO: Print each book
    }
}

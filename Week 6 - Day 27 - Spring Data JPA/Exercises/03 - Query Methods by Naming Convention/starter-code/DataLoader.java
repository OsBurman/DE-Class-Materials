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
        System.out.println("=== Derived Query Methods Demo ===\n");

        // TODO: Save 4+ books spanning at least 2 genres and a range of years
        //       Suggested data:
        //         new Book("Clean Code", "Programming", 2008)
        //         new Book("The Pragmatic Programmer", "Programming", 1999)
        //         new Book("Designing Data-Intensive Applications", "Architecture", 2017)
        //         new Book("The Phoenix Project", "Management", 2013)

        // --- findByGenre ---
        System.out.println("--- findByGenre(\"Programming\") ---");
        // TODO: Call bookRepository.findByGenre("Programming") and print each result

        // --- findByTitleContaining ---
        System.out.println("\n--- findByTitleContaining(\"Code\") ---");
        // TODO: Call bookRepository.findByTitleContaining("Code") and print each result

        // --- findByPublishedYearBetween ---
        System.out.println("\n--- findByPublishedYearBetween(2000, 2020) ---");
        // TODO: Call bookRepository.findByPublishedYearBetween(2000, 2020) and print each result

        // --- countByGenre ---
        System.out.println("\n--- countByGenre ---");
        // TODO: Call bookRepository.countByGenre("Programming") and print the count

        // --- existsById ---
        System.out.println("\n--- existsById ---");
        // TODO: Call bookRepository.existsById(1L) — should print true
        // TODO: Call bookRepository.existsById(99L) — should print false
    }
}

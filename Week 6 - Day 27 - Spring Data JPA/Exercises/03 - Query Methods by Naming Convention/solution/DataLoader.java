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

        // Seed data
        bookRepository.save(new Book("Clean Code", "Programming", 2008));
        bookRepository.save(new Book("The Pragmatic Programmer", "Programming", 1999));
        bookRepository.save(new Book("Designing Data-Intensive Applications", "Architecture", 2017));
        bookRepository.save(new Book("The Phoenix Project", "Management", 2013));

        // --- findByGenre ---
        System.out.println("--- findByGenre(\"Programming\") ---");
        bookRepository.findByGenre("Programming").forEach(System.out::println);

        // --- findByTitleContaining ---
        System.out.println("\n--- findByTitleContaining(\"Code\") ---");
        bookRepository.findByTitleContaining("Code").forEach(System.out::println);

        // --- findByPublishedYearBetween ---
        System.out.println("\n--- findByPublishedYearBetween(2000, 2020) ---");
        bookRepository.findByPublishedYearBetween(2000, 2020).forEach(System.out::println);

        // --- countByGenre ---
        System.out.println("\n--- countByGenre ---");
        System.out.println("Programming count: " + bookRepository.countByGenre("Programming"));

        // --- existsById ---
        System.out.println("\n--- existsById ---");
        System.out.println("Exists id=1: " + bookRepository.existsById(1L));
        System.out.println("Exists id=99: " + bookRepository.existsById(99L));
    }
}

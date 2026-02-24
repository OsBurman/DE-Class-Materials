package com.library;

import com.library.model.Book;
import com.library.repository.BookRepository;
import com.library.service.BookQueryService;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class DataLoader implements ApplicationRunner {

    private final BookRepository bookRepository;
    private final BookQueryService bookQueryService;

    public DataLoader(BookRepository bookRepository, BookQueryService bookQueryService) {
        this.bookRepository = bookRepository;
        this.bookQueryService = bookQueryService;
    }

    @Override
    public void run(ApplicationArguments args) {
        System.out.println("=== Criteria API Demo ===\n");

        // Seed data
        bookRepository.save(new Book("Clean Code", "Programming", 2008));
        bookRepository.save(new Book("The Pragmatic Programmer", "Programming", 1999));
        bookRepository.save(new Book("Designing Data-Intensive Applications", "Architecture", 2017));
        bookRepository.save(new Book("The Phoenix Project", "Management", 2013));

        // --- findByGenre ---
        System.out.println("--- findByGenre(\"Programming\") ---");
        bookQueryService.findByGenre("Programming").forEach(System.out::println);

        // --- findByGenreAndMinYear ---
        System.out.println("\n--- findByGenreAndMinYear(\"Programming\", 2000) ---");
        bookQueryService.findByGenreAndMinYear("Programming", 2000).forEach(System.out::println);

        // --- findByTitleKeyword ---
        System.out.println("\n--- findByTitleKeyword(\"Pragmatic\") ---");
        bookQueryService.findByTitleKeyword("Pragmatic").forEach(System.out::println);
    }
}

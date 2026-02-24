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

        // TODO: Save 4 books across at least 2 genres and a range of years
        //       Suggested:
        //         new Book("Clean Code", "Programming", 2008)
        //         new Book("The Pragmatic Programmer", "Programming", 1999)
        //         new Book("Designing Data-Intensive Applications", "Architecture", 2017)
        //         new Book("The Phoenix Project", "Management", 2013)

        // --- findByGenre ---
        System.out.println("--- findByGenre(\"Programming\") ---");
        // TODO: Call bookQueryService.findByGenre("Programming") and print each result

        // --- findByGenreAndMinYear ---
        System.out.println("\n--- findByGenreAndMinYear(\"Programming\", 2000) ---");
        // TODO: Call bookQueryService.findByGenreAndMinYear("Programming", 2000) and print each result

        // --- findByTitleKeyword ---
        System.out.println("\n--- findByTitleKeyword(\"Pragmatic\") ---");
        // TODO: Call bookQueryService.findByTitleKeyword("Pragmatic") and print each result
    }
}

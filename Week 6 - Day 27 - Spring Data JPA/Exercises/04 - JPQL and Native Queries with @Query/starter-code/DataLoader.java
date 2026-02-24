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
        System.out.println("=== @Query Demo ===\n");

        // TODO: Save 4 books spanning at least 2 genres and a range of years
        //       Suggested:
        //         new Book("Clean Code", "Programming", 2008)
        //         new Book("The Pragmatic Programmer", "Programming", 1999)
        //         new Book("Designing Data-Intensive Applications", "Architecture", 2017)
        //         new Book("The Phoenix Project", "Management", 2013)

        // --- JPQL by genre ---
        System.out.println("--- findByGenreJpql(\"Programming\") ---");
        // TODO: Call bookRepository.findByGenreJpql("Programming") and print each result

        // --- JPQL published after year ---
        System.out.println("\n--- findPublishedAfterJpql(2005) ---");
        // TODO: Call bookRepository.findPublishedAfterJpql(2005) and print each result

        // --- Native title search ---
        System.out.println("\n--- searchByTitleNative(\"Pragmatic\") ---");
        // TODO: Call bookRepository.searchByTitleNative("Pragmatic") and print each result

        // --- JPQL count ---
        System.out.println("\n--- countByGenreJpql(\"Programming\") ---");
        // TODO: Call bookRepository.countByGenreJpql("Programming") and print the count
    }
}

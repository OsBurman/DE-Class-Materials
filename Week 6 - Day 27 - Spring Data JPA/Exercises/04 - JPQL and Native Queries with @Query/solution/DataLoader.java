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

        // Seed data
        bookRepository.save(new Book("Clean Code", "Programming", 2008));
        bookRepository.save(new Book("The Pragmatic Programmer", "Programming", 1999));
        bookRepository.save(new Book("Designing Data-Intensive Applications", "Architecture", 2017));
        bookRepository.save(new Book("The Phoenix Project", "Management", 2013));

        // --- JPQL by genre ---
        System.out.println("--- findByGenreJpql(\"Programming\") ---");
        bookRepository.findByGenreJpql("Programming").forEach(System.out::println);

        // --- JPQL published after year ---
        System.out.println("\n--- findPublishedAfterJpql(2005) ---");
        bookRepository.findPublishedAfterJpql(2005).forEach(System.out::println);

        // --- Native title search ---
        System.out.println("\n--- searchByTitleNative(\"Pragmatic\") ---");
        bookRepository.searchByTitleNative("Pragmatic").forEach(System.out::println);

        // --- JPQL count ---
        System.out.println("\n--- countByGenreJpql(\"Programming\") ---");
        System.out.println("Count: " + bookRepository.countByGenreJpql("Programming"));
    }
}

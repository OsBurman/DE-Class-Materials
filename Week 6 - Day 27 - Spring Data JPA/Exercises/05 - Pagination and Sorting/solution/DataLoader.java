package com.library;

import com.library.model.Book;
import com.library.repository.BookRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

@Component
public class DataLoader implements ApplicationRunner {

    private final BookRepository bookRepository;

    public DataLoader(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @Override
    public void run(ApplicationArguments args) {
        System.out.println("=== Pagination & Sorting Demo ===\n");

        // Seed data
        bookRepository.save(new Book("Clean Code", "Programming", 2008));
        bookRepository.save(new Book("The Pragmatic Programmer", "Programming", 1999));
        bookRepository.save(new Book("Domain-Driven Design", "Programming", 2003));
        bookRepository.save(new Book("Designing Data-Intensive Applications", "Architecture", 2017));
        bookRepository.save(new Book("The Phoenix Project", "Management", 2013));
        bookRepository.save(new Book("Accelerate", "Management", 2018));

        // --- Page 0, size 2 ---
        System.out.println("--- Page 0 (size 2) ---");
        Page<Book> page0 = bookRepository.findAll(PageRequest.of(0, 2));
        System.out.println("Content: " + page0.getContent());
        System.out.println("Page: " + page0.getNumber() + " of " + page0.getTotalPages()
                + " | Total elements: " + page0.getTotalElements());

        // --- Page 1, size 2 ---
        System.out.println("\n--- Page 1 (size 2) ---");
        Page<Book> page1 = bookRepository.findAll(PageRequest.of(1, 2));
        System.out.println("Content: " + page1.getContent());

        // --- Sort by title ascending ---
        System.out.println("\n--- Sorted by title ASC ---");
        bookRepository.findAll(Sort.by("title").ascending()).forEach(System.out::println);

        // --- Sort by publishedYear descending ---
        System.out.println("\n--- Sorted by publishedYear DESC ---");
        bookRepository.findAll(Sort.by("publishedYear").descending()).forEach(System.out::println);

        // --- Paginated + filtered by genre ---
        System.out.println("\n--- Page 0 of 'Programming' books (size 1) ---");
        Page<Book> programmingPage = bookRepository.findByGenre("Programming", PageRequest.of(0, 1));
        System.out.println("Content: " + programmingPage.getContent());
        System.out.println("Total Programming pages: " + programmingPage.getTotalPages());
    }
}

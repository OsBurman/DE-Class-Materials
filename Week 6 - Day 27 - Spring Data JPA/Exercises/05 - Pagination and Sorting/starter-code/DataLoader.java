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

        // TODO: Save 6 books across multiple genres
        //       Suggested:
        //         new Book("Clean Code", "Programming", 2008)
        //         new Book("The Pragmatic Programmer", "Programming", 1999)
        //         new Book("Domain-Driven Design", "Programming", 2003)
        //         new Book("Designing Data-Intensive Applications", "Architecture", 2017)
        //         new Book("The Phoenix Project", "Management", 2013)
        //         new Book("Accelerate", "Management", 2018)

        // --- Page 0, size 2 ---
        System.out.println("--- Page 0 (size 2) ---");
        // TODO: Create PageRequest.of(0, 2) and call bookRepository.findAll(pageable)
        // TODO: Print page.getContent(), page.getNumber(), page.getTotalPages(), page.getTotalElements()

        // --- Page 1, size 2 ---
        System.out.println("\n--- Page 1 (size 2) ---");
        // TODO: Create PageRequest.of(1, 2) and call bookRepository.findAll(pageable)
        // TODO: Print page.getContent()

        // --- Sort by title ascending ---
        System.out.println("\n--- Sorted by title ASC ---");
        // TODO: Call bookRepository.findAll(Sort.by("title").ascending()) and print each book

        // --- Sort by publishedYear descending ---
        System.out.println("\n--- Sorted by publishedYear DESC ---");
        // TODO: Call bookRepository.findAll(Sort.by("publishedYear").descending()) and print each book

        // --- Paginated + filtered by genre ---
        System.out.println("\n--- Page 0 of 'Programming' books (size 1) ---");
        // TODO: Call bookRepository.findByGenre("Programming", PageRequest.of(0, 1))
        // TODO: Print page.getContent() and page.getTotalPages()
    }
}

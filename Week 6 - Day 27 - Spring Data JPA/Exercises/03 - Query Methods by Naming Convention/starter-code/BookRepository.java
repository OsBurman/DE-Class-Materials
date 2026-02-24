package com.library.repository;

import com.library.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookRepository extends JpaRepository<Book, Long> {

    // TODO 1: Add a method to find all books by genre
    //         Method name: findByGenre(String genre)

    // TODO 2: Add a method to find all books whose title contains a keyword
    //         Method name: findByTitleContaining(String keyword)

    // TODO 3: Add a method to find all books published between two years (inclusive)
    //         Method name: findByPublishedYearBetween(int startYear, int endYear)

    // TODO 4: Add a method to count books in a given genre
    //         Return type: long

    // TODO 5: existsById is already inherited from JpaRepository — no need to add it
}

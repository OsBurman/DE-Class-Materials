package com.testing;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA repository for Book.
 * Spring generates the implementation automatically from the method names.
 * Do NOT modify.
 */
public interface BookRepository extends JpaRepository<Book, Long> {

    List<Book> findByGenre(String genre);

    List<Book> findByAuthor(String author);

    Optional<Book> findByTitle(String title);
}

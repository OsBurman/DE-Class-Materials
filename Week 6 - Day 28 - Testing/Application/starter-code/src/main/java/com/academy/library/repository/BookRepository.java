package com.academy.library.repository;

import com.academy.library.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Book repository — used via @MockBean in controller tests.
 * This interface is COMPLETE — do not modify.
 */
public interface BookRepository extends JpaRepository<Book, Long> {
    Optional<Book> findByIsbn(String isbn);
}

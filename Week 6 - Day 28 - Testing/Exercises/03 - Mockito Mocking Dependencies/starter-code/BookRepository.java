package com.testing;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface that BookService depends on.
 * In a real Spring app this would extend JpaRepository;
 * here it is a plain interface so Mockito can mock it without a database.
 */
public interface BookRepository {

    List<Book> findAll();

    Optional<Book> findById(Long id);

    Book save(Book book);

    void deleteById(Long id);
}

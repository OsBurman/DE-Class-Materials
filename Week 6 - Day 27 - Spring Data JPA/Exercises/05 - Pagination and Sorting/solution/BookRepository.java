package com.library.repository;

import com.library.model.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Book, Long> {

    // Filtered pagination — Spring Data derives the WHERE clause from the method name
    Page<Book> findByGenre(String genre, Pageable pageable);
}

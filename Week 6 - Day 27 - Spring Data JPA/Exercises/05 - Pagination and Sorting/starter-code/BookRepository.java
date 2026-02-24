package com.library.repository;

import com.library.model.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Book, Long> {

    // TODO: Add a method that finds books by genre and accepts a Pageable parameter
    //       Return type: Page<Book>
    //       Spring Data provides findAll(Pageable) for free — this is for filtered pagination
}

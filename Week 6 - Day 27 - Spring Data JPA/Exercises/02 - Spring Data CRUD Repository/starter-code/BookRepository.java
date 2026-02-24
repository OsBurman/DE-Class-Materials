package com.library.repository;

import com.library.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;

// TODO: Extend JpaRepository with the correct entity type and id type
//       This interface needs NO method bodies — Spring Data generates the implementation
public interface BookRepository extends JpaRepository<Book, Long> {
    // No code needed here for basic CRUD
}

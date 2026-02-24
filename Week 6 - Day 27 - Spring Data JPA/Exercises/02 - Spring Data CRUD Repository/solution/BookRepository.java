package com.library.repository;

import com.library.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;

// Spring Data auto-generates a full CRUD implementation at runtime — no method bodies required
public interface BookRepository extends JpaRepository<Book, Long> {
}

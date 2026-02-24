package com.library.repository;

import com.library.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookRepository extends JpaRepository<Book, Long> {

    // Spring Data derives SQL from method names — no implementation needed

    List<Book> findByGenre(String genre);

    List<Book> findByTitleContaining(String keyword);

    List<Book> findByPublishedYearBetween(int startYear, int endYear);

    long countByGenre(String genre);

    // existsById is inherited from JpaRepository — no need to redeclare it
}

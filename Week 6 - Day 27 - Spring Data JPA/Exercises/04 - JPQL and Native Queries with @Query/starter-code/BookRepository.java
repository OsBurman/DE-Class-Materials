package com.library.repository;

import com.library.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BookRepository extends JpaRepository<Book, Long> {

    // TODO 1: JPQL — find all books where genre = :genre
    //         @Query("SELECT b FROM Book b WHERE b.genre = :genre")
    //         List<Book> findByGenreJpql(@Param("genre") String genre);

    // TODO 2: JPQL — find all books published after :year, ordered by title ascending
    //         Use entity field name publishedYear (not column name published_year)

    // TODO 3: Native SQL — find all books whose title contains :keyword
    //         Use nativeQuery = true and the actual table/column names: books, title

    // TODO 4: JPQL — count books in a given genre
    //         Return type: long
}

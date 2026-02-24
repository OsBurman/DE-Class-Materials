package com.library.repository;

import com.library.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BookRepository extends JpaRepository<Book, Long> {

    // JPQL — uses entity class name (Book) and Java field names (genre, publishedYear)
    @Query("SELECT b FROM Book b WHERE b.genre = :genre")
    List<Book> findByGenreJpql(@Param("genre") String genre);

    @Query("SELECT b FROM Book b WHERE b.publishedYear > :year ORDER BY b.title ASC")
    List<Book> findPublishedAfterJpql(@Param("year") int year);

    @Query("SELECT COUNT(b) FROM Book b WHERE b.genre = :genre")
    long countByGenreJpql(@Param("genre") String genre);

    // Native SQL — uses table name (books) and column names (title)
    @Query(value = "SELECT * FROM books WHERE title LIKE %:keyword%", nativeQuery = true)
    List<Book> searchByTitleNative(@Param("keyword") String keyword);
}

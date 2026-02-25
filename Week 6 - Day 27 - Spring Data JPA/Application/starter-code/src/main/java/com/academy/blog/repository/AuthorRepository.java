package com.academy.blog.repository;

import com.academy.blog.model.Author;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

/**
 * Repository for Author entity.
 *
 * TODO Task 6: Add the two query methods below.
 */
public interface AuthorRepository extends JpaRepository<Author, Long> {

    // TODO Task 6a: Find author by email (returns Optional)
    // Optional<Author> findByEmail(String email);

    // TODO Task 6b: JPQL — returns list of [Author, post count] pairs
    // @Query("SELECT a, COUNT(p) FROM Author a LEFT JOIN a.posts p GROUP BY a ORDER
    // BY COUNT(p) DESC")
    // List<Object[]> findAuthorsWithPostCount();
}

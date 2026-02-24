package com.academy.blog.repository;

import com.academy.blog.model.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Repository for Post entity.
 *
 * TODO Task 5: Add derived query methods and @Query annotations.
 */
public interface PostRepository extends JpaRepository<Post, Long> {

    // TODO Task 5a: Derived query — find by status
    // List<Post> findByStatus(Post.Status status);

    // TODO Task 5b: Derived query — find by author id
    // List<Post> findByAuthorId(Long authorId);

    // TODO Task 5c: Paginated — find published posts
    // Page<Post> findByStatus(Post.Status status, Pageable pageable);

    // TODO Task 5d: JPQL search in title OR content
    // @Query("SELECT p FROM Post p WHERE p.title LIKE %:keyword% OR p.content LIKE %:keyword%")
    // List<Post> search(@Param("keyword") String keyword);

    // TODO Task 5e: Native SQL — find N most recent posts
    // @Query(value = "SELECT * FROM posts ORDER BY published_at DESC LIMIT :n", nativeQuery = true)
    // List<Post> findTopNRecent(@Param("n") int n);

    // TODO Task 9 (N+1 fix): JOIN FETCH to load posts with their authors in one query
    // @Query("SELECT p FROM Post p JOIN FETCH p.author WHERE p.status = :status")
    // List<Post> findPublishedWithAuthor(@Param("status") Post.Status status);
}

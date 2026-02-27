package com.exercise.blog.repository;

import com.exercise.blog.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

// TODO 12: Extend JpaRepository<Post, Long>
@Repository
public interface PostRepository {

    // TODO 13: Add: Page<Post> findByAuthorId(Long authorId, Pageable pageable)
    //          Spring generates: SELECT * FROM posts WHERE author_id = ? LIMIT ? OFFSET ?

    // TODO 14: Add: List<Post> findByTagsId(Long tagId)
    //          Spring generates a JOIN with post_tags to find posts that have a given tag

    // TODO 15: Add a custom @Query to search posts by keyword in title OR content:
    //          Method name: searchPosts(@Param("keyword") String keyword)
    //          JPQL: SELECT p FROM Post p WHERE LOWER(p.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
    //                OR LOWER(p.content) LIKE LOWER(CONCAT('%', :keyword, '%'))
}

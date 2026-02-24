package com.academy.mongodb.repository;

import com.academy.mongodb.model.Recipe;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Recipe Repository.
 *
 * TODO Task 2: Uncomment and implement each query method.
 * Spring Data MongoDB generates queries from method names.
 */
@Repository
public interface RecipeRepository extends MongoRepository<Recipe, String> {

    // TODO Task 2a: Find by category (exact match)
    // List<Recipe> findByCategory(String category);

    // TODO Task 2b: Find by tag (MongoDB array field — Spring Data handles this)
    // List<Recipe> findByTagsContaining(String tag);

    // TODO Task 2c: Find by author.authorId (nested field query)
    // List<Recipe> findByAuthorAuthorId(String authorId);

    // TODO Task 2d: Full-text search on title (case-insensitive contains)
    // List<Recipe> findByTitleContainingIgnoreCase(String keyword);

    // TODO Task 2e: Find recipes with total time under a limit (prep + cook)
    // Use a @Query with JSON filter
    // @Query("{ $expr: { $lte: [ { $add: ['$prepTimeMinutes', '$cookTimeMinutes'] }, ?0 ] } }")
    // List<Recipe> findByTotalTimeLessThanEqual(int maxTotalMinutes);

    // TODO Task 2f: Paginated retrieval by category
    // Page<Recipe> findByCategory(String category, Pageable pageable);

    // TODO Task 2g: Count by category
    // long countByCategory(String category);
}

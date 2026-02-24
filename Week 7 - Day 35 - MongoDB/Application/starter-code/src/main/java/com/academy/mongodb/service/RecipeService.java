package com.academy.mongodb.service;

import com.academy.mongodb.model.Ingredient;
import com.academy.mongodb.model.Rating;
import com.academy.mongodb.model.Recipe;
import com.academy.mongodb.repository.RecipeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

/**
 * Recipe Service.
 *
 * TODO Task 3: Implement all methods.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RecipeService {

    private final RecipeRepository recipeRepository;
    private final MongoTemplate mongoTemplate;

    // TODO Task 3a: Get all recipes (sorted by createdAt desc)
    public List<Recipe> getAllRecipes() {
        // TODO: use recipeRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"))
        return recipeRepository.findAll();
    }

    // TODO Task 3b: Get a single recipe by ID (throw IllegalArgumentException if not found)
    public Recipe getRecipeById(String id) {
        return recipeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Recipe not found: " + id));
    }

    // TODO Task 3c: Create a new recipe — set createdAt and updatedAt
    public Recipe createRecipe(Recipe recipe) {
        recipe.setCreatedAt(Instant.now());
        recipe.setUpdatedAt(Instant.now());
        // TODO
        return recipeRepository.save(recipe);
    }

    // TODO Task 3d: Update a recipe — update updatedAt, save
    public Recipe updateRecipe(String id, Recipe updated) {
        Recipe existing = getRecipeById(id);
        // TODO: copy fields from updated to existing
        existing.setUpdatedAt(Instant.now());
        return recipeRepository.save(existing);
    }

    // TODO Task 3e: Delete a recipe
    public void deleteRecipe(String id) {
        // TODO: verify exists, then delete
        recipeRepository.deleteById(id);
    }

    // TODO Task 3f: Add a rating — use MongoTemplate $push to avoid reloading document
    public Recipe addRating(String recipeId, Rating rating) {
        rating.setCreatedAt(Instant.now());
        // TODO:
        // Query query  = Query.query(Criteria.where("_id").is(recipeId));
        // Update update = new Update().push("ratings", rating);
        // mongoTemplate.updateFirst(query, update, Recipe.class);
        // return getRecipeById(recipeId);
        Recipe recipe = getRecipeById(recipeId);
        recipe.getRatings().add(rating);
        return recipeRepository.save(recipe);
    }

    // TODO Task 3g: Search by category
    public List<Recipe> getByCategory(String category) {
        // TODO: recipeRepository.findByCategory(category)
        return List.of();
    }

    // TODO Task 3h: Search by tag
    public List<Recipe> getByTag(String tag) {
        // TODO: recipeRepository.findByTagsContaining(tag)
        return List.of();
    }

    // TODO Task 3i: Full-text search
    public List<Recipe> search(String keyword) {
        // TODO: recipeRepository.findByTitleContainingIgnoreCase(keyword)
        return List.of();
    }

    // TODO Task 3j: MongoTemplate custom query — recipes with total time <= maxMinutes
    public List<Recipe> getQuickRecipes(int maxMinutes) {
        // TODO:
        // Query query = new Query(Criteria.where("$expr").is(
        //     new org.bson.Document("$lte",
        //         List.of(new org.bson.Document("$add", List.of("$prepTimeMinutes","$cookTimeMinutes")),
        //                 maxMinutes))));
        // return mongoTemplate.find(query, Recipe.class);
        return List.of();
    }
}

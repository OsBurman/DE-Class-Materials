package com.academy.mongodb.controller;

import com.academy.mongodb.model.Rating;
import com.academy.mongodb.model.Recipe;
import com.academy.mongodb.service.RecipeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

/**
 * Recipe REST Controller.
 *
 * TODO Task 4: Implement all endpoints.
 */
@RestController
@RequestMapping("/api/recipes")
@RequiredArgsConstructor
public class RecipeController {

    private final RecipeService recipeService;

    // TODO Task 4a: GET /api/recipes — all recipes (or ?category=X or ?tag=X or ?search=X or ?maxMinutes=N)
    @GetMapping
    public ResponseEntity<List<Recipe>> getAllRecipes(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String tag,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Integer maxMinutes) {

        if (category != null) return ResponseEntity.ok(recipeService.getByCategory(category));
        if (tag      != null) return ResponseEntity.ok(recipeService.getByTag(tag));
        if (search   != null) return ResponseEntity.ok(recipeService.search(search));
        if (maxMinutes != null) return ResponseEntity.ok(recipeService.getQuickRecipes(maxMinutes));
        return ResponseEntity.ok(recipeService.getAllRecipes());
    }

    // TODO Task 4b: GET /api/recipes/{id}
    @GetMapping("/{id}")
    public ResponseEntity<Recipe> getById(@PathVariable String id) {
        // TODO
        return ResponseEntity.ok(recipeService.getRecipeById(id));
    }

    // TODO Task 4c: POST /api/recipes — 201 + Location header
    @PostMapping
    public ResponseEntity<Recipe> create(@Valid @RequestBody Recipe recipe) {
        Recipe saved = recipeService.createRecipe(recipe);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(saved.getId()).toUri();
        return ResponseEntity.created(location).body(saved);
    }

    // TODO Task 4d: PUT /api/recipes/{id}
    @PutMapping("/{id}")
    public ResponseEntity<Recipe> update(@PathVariable String id, @Valid @RequestBody Recipe recipe) {
        // TODO
        return ResponseEntity.ok(recipeService.updateRecipe(id, recipe));
    }

    // TODO Task 4e: DELETE /api/recipes/{id} — 204 No Content
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        recipeService.deleteRecipe(id);
        return ResponseEntity.noContent().build();
    }

    // TODO Task 4f: POST /api/recipes/{id}/ratings
    @PostMapping("/{id}/ratings")
    public ResponseEntity<Recipe> addRating(@PathVariable String id, @Valid @RequestBody Rating rating) {
        // TODO
        return ResponseEntity.ok(recipeService.addRating(id, rating));
    }
}

package com.academy.mongodb;

import com.academy.mongodb.model.*;
import com.academy.mongodb.repository.RecipeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;

/**
 * Seeds sample data on startup.
 * This class is COMPLETE.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final RecipeRepository recipeRepository;

    @Override
    public void run(String... args) {
        if (recipeRepository.count() > 0)
            return;
        log.info("Seeding recipe data…");

        AuthorSnapshot alice = AuthorSnapshot.builder()
                .authorId("author-1").name("Alice Chen").build();
        AuthorSnapshot bob = AuthorSnapshot.builder()
                .authorId("author-2").name("Bob Smith").build();

        recipeRepository.saveAll(List.of(
                Recipe.builder()
                        .title("Classic Spaghetti Carbonara")
                        .description("A rich and creamy Italian pasta dish.")
                        .category("Italian")
                        .prepTimeMinutes(15).cookTimeMinutes(20)
                        .tags(List.of("pasta", "quick", "classic"))
                        .author(alice)
                        .ingredients(List.of(
                                Ingredient.builder().name("spaghetti").quantity("400g").build(),
                                Ingredient.builder().name("pancetta").quantity("150g").build(),
                                Ingredient.builder().name("eggs").quantity("4").build(),
                                Ingredient.builder().name("Pecorino Romano").quantity("100g").build()))
                        .ratings(List.of(
                                Rating.builder().score(5).reviewer("Tom").comment("Amazing!").createdAt(Instant.now())
                                        .build()))
                        .createdAt(Instant.now()).updatedAt(Instant.now())
                        .build(),

                Recipe.builder()
                        .title("Chocolate Lava Cake")
                        .description("Warm chocolate cake with a molten centre.")
                        .category("Dessert")
                        .prepTimeMinutes(10).cookTimeMinutes(12)
                        .tags(List.of("chocolate", "dessert", "quick"))
                        .author(bob)
                        .ingredients(List.of(
                                Ingredient.builder().name("dark chocolate").quantity("200g").build(),
                                Ingredient.builder().name("butter").quantity("100g").build(),
                                Ingredient.builder().name("eggs").quantity("2").build(),
                                Ingredient.builder().name("sugar").quantity("50g").build()))
                        .createdAt(Instant.now()).updatedAt(Instant.now())
                        .build(),

                Recipe.builder()
                        .title("Vegan Buddha Bowl")
                        .description("Colourful and nutritious plant-based bowl.")
                        .category("Vegan")
                        .prepTimeMinutes(20).cookTimeMinutes(30)
                        .tags(List.of("vegan", "healthy", "bowls"))
                        .author(alice)
                        .ingredients(List.of(
                                Ingredient.builder().name("quinoa").quantity("200g").build(),
                                Ingredient.builder().name("chickpeas").quantity("1 can").build(),
                                Ingredient.builder().name("sweet potato").quantity("1 large").build(),
                                Ingredient.builder().name("kale").quantity("2 cups").build()))
                        .createdAt(Instant.now()).updatedAt(Instant.now())
                        .build()));

        log.info("Seeded {} recipes.", recipeRepository.count());
    }
}

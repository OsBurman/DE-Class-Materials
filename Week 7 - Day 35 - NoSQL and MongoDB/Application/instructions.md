# Day 35 Application — MongoDB: Recipe Book API

## Overview

Build a **Recipe Book REST API** using **Spring Data MongoDB**, exploring document design, embedded vs referenced documents, queries, aggregation, and text search.

---

## Learning Goals

- Design MongoDB documents and collections
- Use `@Document`, `@Field`, `@Id`
- Extend `MongoRepository` for CRUD
- Write custom queries with `@Query` and `MongoTemplate`
- Use aggregation pipelines
- Enable full-text search with text indexes
- Compare document vs relational modeling

---

## Prerequisites

- Java 17+, Maven
- MongoDB running locally (`mongod`) OR use the embedded MongoDB (Flapdoodle — included in pom.xml)
- `mvn spring-boot:run` → `http://localhost:8080`

---

## Document Design

```
recipes (collection)
├── _id: ObjectId
├── title: String
├── description: String
├── prepTimeMinutes: int
├── cookTimeMinutes: int
├── servings: int
├── difficulty: String (EASY | MEDIUM | HARD)
├── cuisine: String
├── tags: [String]  ← embedded array
├── ingredients: [  ← embedded documents
│   { name, quantity, unit }
│   ]
├── steps: [String]
├── author: {       ← embedded author snapshot
│   id, name, email
│   }
└── ratings: [      ← embedded ratings
    { userId, score, comment, date }
    ]
```

---

## Part 1 — Domain Model

**Task 1 — `Recipe.java`**  
```java
@Document(collection = "recipes")
public class Recipe {
    @Id private String id;
    // TODO: add all fields with @Field annotations
    // TODO: @TextIndexed on title and description
    // TODO: @Indexed on cuisine and tags
}
```

**Task 2 — Embedded classes**  
`Ingredient.java` (name, quantity, unit).  
`Rating.java` (userId, score 1-5, comment, date).  
`AuthorSnapshot.java` (id, name, email).

---

## Part 2 — Repository

**Task 3 — `RecipeRepository`**  
```java
public interface RecipeRepository extends MongoRepository<Recipe, String> {

    // TODO: find by cuisine
    // TODO: find by difficulty
    // TODO: find by tag (hint: MongoDB can query array fields directly)
    // TODO: find recipes where prepTime + cookTime <= totalMinutes
    @Query("{ 'ingredients.name': { $regex: ?0, $options: 'i' } }")
    List<Recipe> findByIngredientName(String ingredient);

    // TODO: @Query — find recipes with ALL specified tags
    // TODO: text search method
}
```

---

## Part 3 — Service & Controller

**Task 4 — `RecipeService`**  
Implement:
- `createRecipe(RecipeDto)` — map DTO → Document, save
- `addRating(String recipeId, Rating rating)` — push to ratings array using `MongoTemplate`
- `getTopRatedByGenre(String cuisine, int limit)` — use aggregation

**Task 5 — `RecipeController`**  
```
GET  /api/recipes                   — all recipes (with optional ?cuisine= ?difficulty= ?tag=)
GET  /api/recipes/{id}              — single recipe
GET  /api/recipes/search?q=chicken  — text search
POST /api/recipes                   — create
PUT  /api/recipes/{id}              — update
POST /api/recipes/{id}/ratings      — add rating
GET  /api/recipes/top?cuisine=Italian — top rated per cuisine
```

---

## Part 4 — Aggregation

**Task 6 — `RecipeAggregationService`**  
```java
// Average rating per cuisine
Aggregation agg = Aggregation.newAggregation(
    // TODO: unwind ratings
    // TODO: group by cuisine, avg of ratings.score
    // TODO: sort by avg desc
);
```

---

## Part 5 — Seed Data

**Task 7 — `DataInitializer.java`**  
`@Component` that implements `ApplicationRunner`. Insert 10 recipes on startup if the collection is empty.

---

## Submission Checklist

- [ ] `@Document` with embedded Ingredient, Rating, AuthorSnapshot
- [ ] Text index on title + description
- [ ] `findByIngredientName` with `@Query` regex
- [ ] Rating added via `MongoTemplate` (push to array)
- [ ] Aggregation pipeline for avg rating per cuisine
- [ ] Text search endpoint works with `?q=`
- [ ] DataInitializer seeds 10 recipes on startup

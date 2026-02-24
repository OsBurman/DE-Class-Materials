# Exercise 04 — JPQL and Native Queries with @Query

## Learning Objectives
- Write JPQL (Java Persistence Query Language) queries using `@Query`
- Write native SQL queries using `@Query(nativeQuery = true)`
- Bind named parameters with `@Param`
- Understand the difference between JPQL (entity-based) and native SQL (table-based)

## Background

Derived method names work for simple queries, but `@Query` gives you full control over the query string when logic becomes complex.

| Feature | JPQL | Native SQL |
|---|---|---|
| References | Entity class & field names | Table & column names |
| Portable | Yes (across databases) | No (dialect-specific) |
| Annotation flag | _(default)_ | `nativeQuery = true` |

**JPQL example:**
```java
@Query("SELECT b FROM Book b WHERE b.genre = :genre")
List<Book> findByGenreJpql(@Param("genre") String genre);
```

**Native SQL example:**
```java
@Query(value = "SELECT * FROM books WHERE published_year > :year", nativeQuery = true)
List<Book> findPublishedAfterNative(@Param("year") int year);
```

## Instructions

### Step 1 — Add `@Query` methods to `BookRepository`

Open `starter-code/BookRepository.java` and add:

```java
// TODO 1: JPQL — find all books by genre using a named parameter :genre
@Query("SELECT b FROM Book b WHERE b.genre = :genre")
List<Book> findByGenreJpql(@Param("genre") String genre);

// TODO 2: JPQL — find all books published after a given year, ordered by title
@Query("SELECT b FROM Book b WHERE b.publishedYear > :year ORDER BY b.title ASC")
List<Book> findPublishedAfterJpql(@Param("year") int year);

// TODO 3: Native SQL — find all books whose title contains a keyword
@Query(value = "SELECT * FROM books WHERE title LIKE %:keyword%", nativeQuery = true)
List<Book> searchByTitleNative(@Param("keyword") String keyword);

// TODO 4: JPQL — count books in a given genre
@Query("SELECT COUNT(b) FROM Book b WHERE b.genre = :genre")
long countByGenreJpql(@Param("genre") String genre);
```

### Step 2 — Exercise each method in `DataLoader`

Open `starter-code/DataLoader.java` and follow the TODO comments to:
1. Save 4 books across 2 genres and different years
2. Call each repository method and print results

### Step 3 — Run and inspect the SQL

```
mvn spring-boot:run
```

Compare the `show-sql` output for JPQL vs native queries.  
Notice JPQL translates entity/field names; native queries reach the table directly.

## Expected Output (example)

```
--- findByGenreJpql("Programming") ---
Book{id=1, title='Clean Code', genre='Programming', publishedYear=2008}
Book{id=2, title='The Pragmatic Programmer', genre='Programming', publishedYear=1999}

--- findPublishedAfterJpql(2005) ---
Book{id=1, title='Clean Code', genre='Programming', publishedYear=2008}
Book{id=3, title='Designing Data-Intensive Applications', genre='Architecture', publishedYear=2017}
Book{id=4, title='The Phoenix Project', genre='Management', publishedYear=2013}

--- searchByTitleNative("Pragmatic") ---
Book{id=2, title='The Pragmatic Programmer', genre='Programming', publishedYear=1999}

--- countByGenreJpql("Programming") ---
Count: 2
```

## Key Concepts

- **JPQL** operates on entity names and Java field names, not table/column names
- **`@Param("name")`** binds the method parameter to `:name` in the query string
- **`nativeQuery = true`** passes the query string straight to the JDBC driver — use table and column names
- **`ORDER BY`** works in both JPQL and native SQL
- Named parameters (`:name`) are preferred over positional (`?1`) for readability

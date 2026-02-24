# Exercise 03 — Query Methods by Naming Convention

## Learning Objectives
- Understand how Spring Data derives SQL queries from method names
- Write derived query methods for common search patterns
- Use keyword expressions: `findBy`, `Containing`, `Between`, `countBy`, `existsBy`

## Background

Spring Data JPA inspects method names in your repository interface and automatically generates the correct JPQL query at runtime. No `@Query` annotation or SQL string is needed — the method name *is* the query.

| Method Name | Generated SQL Equivalent |
|---|---|
| `findByGenre(String genre)` | `WHERE genre = ?` |
| `findByTitleContaining(String kw)` | `WHERE title LIKE '%?%'` |
| `findByPublishedYearBetween(int a, int b)` | `WHERE published_year BETWEEN ? AND ?` |
| `countByGenre(String genre)` | `SELECT COUNT(*) WHERE genre = ?` |
| `existsById(Long id)` | `SELECT COUNT(*) > 0 WHERE id = ?` |

## Instructions

### Step 1 — Add derived query methods to `BookRepository`

Open `starter-code/BookRepository.java` and add the following method signatures:

```java
// TODO 1: Find all books in a given genre
List<Book> findByGenre(String genre);

// TODO 2: Find all books whose title contains the given keyword (case-sensitive)
List<Book> findByTitleContaining(String keyword);

// TODO 3: Find all books published between two years (inclusive)
List<Book> findByPublishedYearBetween(int startYear, int endYear);

// TODO 4: Count how many books belong to a given genre
long countByGenre(String genre);

// TODO 5: Check whether a book with the given id exists
boolean existsById(Long id);
```

> No method body is needed — Spring Data implements them automatically.

### Step 2 — Exercise each method in `DataLoader`

Open `starter-code/DataLoader.java` and follow the TODO comments to:
1. Save at least 4 books covering 2 genres
2. Call `findByGenre` and print results
3. Call `findByTitleContaining` and print results
4. Call `findByPublishedYearBetween` with a range that matches 2 books
5. Call `countByGenre` and print the count
6. Call `existsById` for an id that exists and one that does not

### Step 3 — Run the application

```
mvn spring-boot:run
```

Verify the output shows each derived query firing and returning the expected results.  
Check the SQL printed by Hibernate — notice `WHERE` clauses matching your method names.

## Expected Output (example)

```
--- findByGenre("Programming") ---
Book{id=1, title='Clean Code', genre='Programming', publishedYear=2008}
Book{id=2, title='The Pragmatic Programmer', genre='Programming', publishedYear=1999}

--- findByTitleContaining("Code") ---
Book{id=1, title='Clean Code', genre='Programming', publishedYear=2008}

--- findByPublishedYearBetween(2000, 2020) ---
Book{id=1, title='Clean Code', genre='Programming', publishedYear=2008}
Book{id=3, title='Designing Data-Intensive Applications', genre='Architecture', publishedYear=2017}

--- countByGenre("Programming") ---
Count: 2

--- existsById ---
Exists id=1: true
Exists id=99: false
```

## Key Concepts

- **Method naming rules:** `findBy<Property><Keyword>` — property names must match the Java field names (camelCase)
- **`Containing`** wraps the argument in `%…%` automatically (LIKE)
- **`Between`** takes two arguments and generates `BETWEEN ? AND ?`
- **`countBy`** returns `long`; **`existsBy`** returns `boolean`
- Spring Data resolves property paths at startup — a typo causes an application context failure (fail-fast)

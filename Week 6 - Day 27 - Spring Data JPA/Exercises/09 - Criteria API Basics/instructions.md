# Exercise 09 — Criteria API Basics

## Learning Objectives
- Use `CriteriaBuilder` and `CriteriaQuery` to build type-safe queries programmatically
- Create `Predicate` conditions and combine them with `and()` / `or()`
- Execute Criteria queries via `EntityManager`
- Understand when the Criteria API is preferred over JPQL strings

## Background

The **Criteria API** lets you construct JPA queries entirely in Java — no query strings.  
This makes queries refactor-safe (renaming a field causes a compile error, not a runtime one).

```java
// 1. Get the CriteriaBuilder from the EntityManager
CriteriaBuilder cb = entityManager.getCriteriaBuilder();

// 2. Create a typed query (returns Book objects)
CriteriaQuery<Book> cq = cb.createQuery(Book.class);

// 3. Define the root entity (FROM clause)
Root<Book> root = cq.from(Book.class);

// 4. Build predicates (WHERE conditions)
Predicate genrePredicate = cb.equal(root.get("genre"), "Programming");
Predicate yearPredicate  = cb.greaterThan(root.get("publishedYear"), 2000);

// 5. Combine and apply
cq.where(cb.and(genrePredicate, yearPredicate));

// 6. Execute
List<Book> results = entityManager.createQuery(cq).getResultList();
```

## Instructions

### Step 1 — Complete `BookQueryService`

Open `starter-code/BookQueryService.java` and fill in each TODO:

```java
// TODO 1: Inject EntityManager using @PersistenceContext

// TODO 2: findByGenre(String genre)
//         Build: SELECT b FROM Book b WHERE b.genre = :genre

// TODO 3: findByGenreAndMinYear(String genre, int minYear)
//         Build: SELECT b FROM Book b WHERE b.genre = :genre AND b.publishedYear > :minYear

// TODO 4: findByTitleKeyword(String keyword)
//         Build: SELECT b FROM Book b WHERE b.title LIKE %keyword%
//         Hint: use cb.like(root.get("title"), "%" + keyword + "%")
```

### Step 2 — Exercise each method in `DataLoader`

Follow the TODO comments to seed data and call each query service method.

### Step 3 — Run

```
mvn spring-boot:run
```

The SQL output will show parameterised queries built entirely from Java code.

## Expected Output (example)

```
--- findByGenre("Programming") ---
Book{id=1, title='Clean Code', genre='Programming', publishedYear=2008}
Book{id=2, title='The Pragmatic Programmer', genre='Programming', publishedYear=1999}

--- findByGenreAndMinYear("Programming", 2000) ---
Book{id=1, title='Clean Code', genre='Programming', publishedYear=2008}

--- findByTitleKeyword("Pragmatic") ---
Book{id=2, title='The Pragmatic Programmer', genre='Programming', publishedYear=1999}
```

## Key Concepts

- `CriteriaBuilder` is the factory for predicates, expressions, and orderings
- `Root<T>` represents the entity in the FROM clause; `root.get("fieldName")` is type-safe with metamodel
- `Predicate` objects are composable: `cb.and(p1, p2)`, `cb.or(p1, p2)`, `cb.not(p)`
- `cb.like(expression, pattern)` — use `%` wildcards just like SQL
- The Criteria API is verbose for simple queries; prefer derived methods or `@Query` for straightforward cases

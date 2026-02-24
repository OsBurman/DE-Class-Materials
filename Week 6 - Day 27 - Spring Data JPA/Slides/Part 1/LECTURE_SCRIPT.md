# Day 27 Part 1 — Spring Data JPA: Repositories, Queries, Pagination & Transactions
## Lecture Script

**Total Time:** 60 minutes
**Delivery pace:** ~165 words/minute — conversational, instructor-led

---

## [00:00–02:00] Opening — The Boilerplate Problem

Welcome to Day 27. Yesterday we built the controller and service layers. Today we build the foundation everything sits on: the database layer.

Before we look at any code, let me ask you to imagine building a data access layer the traditional way. You have a `Book` entity. You need to find books by title. So you write: create a query, set parameters, get results, handle exceptions. Now you need to find books by author. Same thing again. Find by ISBN. Same pattern again. Count books by category. Same pattern. Twenty methods, twenty nearly-identical blocks of boilerplate.

This is the problem Spring Data JPA was built to eliminate. After today, you'll know how to build a fully functional database access layer with almost zero implementation code. Spring generates the implementation. You write the interface.

By the end of Part 1 you'll have a complete mental model of repositories, query methods, pagination, and how `@Transactional` works inside JPA's session lifecycle.

---

## [02:00–10:00] Slides 2–4 — Spring Data and the Repository Hierarchy

Let me show you the before and after directly. Without Spring Data JPA, every repository method requires you to inject an `EntityManager`, write a JPQL string, set parameters, and call `getResultList()` or `getSingleResult()`. A `save()` method requires checking whether the entity has an ID, calling `persist()` for new entities and `merge()` for existing ones.

With Spring Data JPA, you write one line: `public interface BookRepository extends JpaRepository<Book, Long> {}`. That's it. Spring generates the entire `save`, `findById`, `findAll`, `deleteById`, and fifteen more methods automatically at startup.

Let me walk you through the repository hierarchy so you understand what each level provides.

At the top: `Repository<T, ID>`. This is just a marker interface with no methods. It tells Spring Data: "this interface is a repository." You'd extend this directly if you want to define a completely custom API surface.

Next: `CrudRepository<T, ID>`. This is where the standard CRUD operations live: `save`, `saveAll`, `findById`, `findAll`, `findAllById`, `count`, `delete`, `deleteById`, `existsById`. These eleven methods cover the vast majority of what you need for any entity.

Then: `PagingAndSortingRepository<T, ID>`. Adds `findAll(Pageable)` and `findAll(Sort)`. This is where pagination and sorting support comes in.

Finally: `JpaRepository<T, ID>`. This is what you'll extend in virtually every Spring Boot project. It adds JPA-specific operations: `flush()`, `saveAndFlush()`, `deleteAllInBatch()`, `getReferenceById()`.

Two type parameters: `T` is the entity class, `ID` is the primary key type. So `JpaRepository<Book, Long>` means: a repository for `Book` entities whose primary key is a `Long`.

Now the complete method reference on slide 4. Let me highlight the ones that trip people up.

`save()` is an upsert — if the entity has no ID, it does an INSERT. If it has an ID, it does an UPDATE via merge. You use the same method for both. `saveAndFlush()` is the same but forces immediate SQL execution. Normally, Spring defers the actual SQL to transaction commit. `saveAndFlush()` is needed in test scenarios or when another piece of code in the same transaction needs to read that row immediately after saving.

`getReferenceById()` returns a lazy proxy — it doesn't hit the database immediately. It's useful when you need a reference to an entity for a foreign key relationship without loading the full entity. Must be used inside a `@Transactional` method.

`deleteAllInBatch()` is critical for performance. `deleteAll()` loads every entity first, then issues N individual DELETE statements. `deleteAllInBatch()` issues a single `DELETE FROM books` statement. When you have thousands of rows, this is the difference between a two-second operation and an instant one.

---

## [10:00–20:00] Slides 5–7 — Auto-Implementation and Query Methods

How does Spring generate the implementation? At application startup, Spring scans all packages for interfaces that extend `Repository<T, ID>`. For each one, it creates a JDK dynamic proxy backed by `SimpleJpaRepository<T, ID>` — Spring's default implementation.

Here's something important: `SimpleJpaRepository` already has `@Transactional` applied. It's `@Transactional(readOnly = true)` at the class level, and write methods like `save()` and `delete()` override with `@Transactional` (read-write). This means repository operations are already transactional. When your service method is annotated with `@Transactional`, the repository call joins the same transaction — it doesn't start a new one.

Now — query methods. This is where Spring Data starts feeling like magic.

Spring parses your method name and generates JPQL from it. The format is: a subject keyword like `findBy` or `countBy`, followed by property names and condition keywords. Let me make this concrete.

`findByIsbn(String isbn)` — Spring generates `SELECT b FROM Book b WHERE b.isbn = :isbn`. You never write that SQL. `findByAuthorLastName(String lastName)` — note the nested property. Spring knows that `Author` has a `lastName` field and generates a join automatically. `findTop5ByOrderByPriceAsc()` — returns the five cheapest books. No parameters. The order is in the method name itself.

`countByCategory(String category)` — returns a `long`. `existsByIsbn(String isbn)` — returns a `boolean`. The return type drives what Spring generates.

The slide shows a complete `BookRepository` with ten different query method examples. Look at the pattern: you're describing what you want in English, and Spring builds the query. This is the core value proposition of Spring Data.

Now let me cover the keyword reference on slide 7. These are the building blocks.

For comparisons: `LessThan`, `GreaterThan`, `LessThanEqual`, `GreaterThanEqual`, `Between`. For strings: `Containing` maps to `LIKE '%value%'`, `StartingWith` maps to `LIKE 'value%'`, `EndingWith` maps to `LIKE '%value'`. `ContainingIgnoreCase` for case-insensitive search. For null checks: `IsNull`, `IsNotNull`. For collections: `In` maps to `IN (...)`, `NotIn` maps to `NOT IN (...)`. For booleans: `True` and `False`.

You can chain conditions with `And` and `Or`. `findByTitleContainingAndCategoryIn(String title, List<String> categories)` becomes `WHERE title LIKE '%title%' AND category IN (...)`.

Important caveat: there's a practical limit to method name complexity. When you need more than two or three conditions, or dynamic conditions that depend on runtime values, it's time for `@Query`.

---

## [20:00–30:00] Slides 8–9 — @Query

`@Query` lets you write JPQL directly. Two critical concepts before we look at code.

First: JPQL references entity class names and field names — not table names, not column names. Your class is `Book`. Your field is `author`. The JPQL is `FROM Book b JOIN b.author a`. If your table is named `books` and the column is `author_id`, JPQL doesn't know or care.

Second: named parameters with `@Param`. In the annotation you write `:keyword`. In the method parameter you add `@Param("keyword") String keyword`. The name in the annotation must match the name in the `@Param`.

The search query example: `"SELECT b FROM Book b WHERE b.title LIKE %:keyword% OR b.author.lastName LIKE %:keyword%"`. The `%` wildcards are inside the JPQL string, outside the `:keyword` placeholder. This does a case-sensitive `LIKE` search across both title and author last name.

Now the `JOIN FETCH` query: `"SELECT b FROM Book b JOIN FETCH b.author WHERE b.category = :category"`. This is the solution to the N+1 problem — I'll explain N+1 in Part 2, but the short version is: when you load books and then access each book's author, without JOIN FETCH you get one query for the books plus one query per book for the author. With `JOIN FETCH`, Spring loads everything in one query.

Constructor expressions: `SELECT new com.example.dto.BookSummaryDto(b.id, b.title, b.price) FROM Book b`. This maps results directly to a DTO — no entity materialization. Efficient, and the DTO is what your controller needs anyway. The fully qualified class name and a matching constructor are required.

When you return `Page<T>` with a `@Query` that uses `JOIN FETCH`, you need a separate `countQuery`. This is because the count query cannot contain a `FETCH` clause — Spring Data needs the `countQuery` for calculating total pages. I've shown this pattern on slide 8.

Now `@Modifying`. This annotation is required for any `@Query` that performs an `UPDATE` or `DELETE`. Without it, Spring Data throws an exception because it detects you're trying to modify data through a select-style query method.

`clearAutomatically = true` is the gotcha-preventer. If your service method loads some `Book` entities, then calls a `@Modifying` `UPDATE` query that changes their prices, those loaded entities still have the old prices in the persistence context. `clearAutomatically = true` evicts the persistence context cache after the update, so any subsequent `findById` calls return fresh data.

The return type of a `@Modifying` query is `int` — the count of rows affected.

---

## [30:00–40:00] Slides 10–11 — Pagination and Sorting

Pagination is how you avoid loading 10,000 records into memory when a client requests page 1 of 20. It's non-negotiable for any production API with collections.

Spring Data's pagination uses two objects: `Pageable` for the request parameters and `Page<T>` for the response.

`PageRequest.of(0, 20)` — page zero (zero-indexed), twenty items. `PageRequest.of(2, 20)` — third page, twenty items. For page one, you'd pass `1` from the client and subtract one. Or tell your client the API uses zero-based page numbers — either convention works, just be consistent.

Combine with sort: `PageRequest.of(0, 20, Sort.by("title"))`. Sort by title ascending by default. `Sort.by(Direction.DESC, "price")` for descending.

In your repository, add a `Pageable` parameter to any query method. `findAll(Pageable pageable)` is already provided by `JpaRepository`. For custom methods: `findByCategory(String category, Pageable pageable)` — Spring Data recognizes the `Pageable` parameter and adds `LIMIT` and `OFFSET` to the generated query.

The `Page<T>` response is a wrapper. `getContent()` gives you the `List<T>`. `getTotalElements()` gives you the total count across all pages. `getTotalPages()` gives you how many pages there are. `isFirst()` and `isLast()` tell you where you are. Spring serializes all of this to JSON automatically when you return `Page<T>` from a controller method.

`Page<T>` vs `Slice<T>`. When you return `Page<T>`, Spring runs two SQL queries: the data query and a count query. The count query is needed to calculate `totalPages` and `totalElements`. If you're building an infinite scroll or "load more" UI, you don't need the total count — `Slice<T>` skips the count query and just tells you `hasNext()`. Same interface, one less database round-trip.

The `map()` method on `Page<T>` is your best friend. `bookPage.map(bookMapper::toDto)` converts `Page<Book>` to `Page<BookDto>` while preserving all pagination metadata. You return `Page<BookDto>` from your controller and the client gets both the data and the pagination info.

Sorting. You can pass `Sort` directly to any `findAll(Sort)` or query method that accepts `Sort`. But accepting sort parameters from HTTP clients requires a safety step. Never blindly pass a client-provided field name to `Sort.by()`. A user could send `sortBy=internalSecretField` and expose your schema. Keep an allowlist of sortable fields and validate against it. I showed this pattern on slide 11.

---

## [40:00–50:00] Slides 12–13 — @Transactional in Depth with JPA

Yesterday in Part 2 we established that `@Transactional` is AOP. Today we understand what it actually manages in the JPA context: the `EntityManager` and the session lifecycle.

The `EntityManager` is JPA's unit of work. It's a per-request, short-lived object. When a `@Transactional` method starts, Spring opens an `EntityManager`. While that method runs, every entity you load from the database is tracked by this `EntityManager` — this is called the "persistence context" or "first-level cache." When the method returns, the `EntityManager` flushes pending changes to the database and closes.

This is what makes dirty checking work. When you load a `Book`, change its price with `book.setPrice(newPrice)`, and return from the method — you never call `save()`. Spring's dirty checking detects the price changed during the transaction and issues an `UPDATE` automatically on commit. This is correct JPA behavior. You set the new value on the managed entity. Done.

The `@Transactional(readOnly = true)` optimization is real and significant. With read-only, Hibernate knows you won't be modifying entities. It skips snapshot creation — the mechanism that enables dirty checking. For a method that loads a hundred entities, skipping snapshots means less memory allocation and a faster method. Some JDBC drivers and database proxies also use the read-only hint to route to read replicas.

The pattern I recommend: put `@Transactional(readOnly = true)` at the class level on your service, and override with `@Transactional` on write methods. This makes read-only the safe default and forces you to opt-in to writes explicitly.

Now the `LazyInitializationException`. This is the most common JPA error you will encounter, and I want you to understand it precisely.

`@OneToMany` collections are lazy by default. Lazy means: when you load a `Book`, the `reviews` list is not loaded. The JPA proxy just holds a placeholder. When code accesses `book.getReviews()`, the proxy tries to load the collection. But that requires a database query. That database query requires an open `EntityManager`. If the `EntityManager` is already closed — because the `@Transactional` method that loaded the book has already returned — you get `LazyInitializationException`.

Three solutions, and you need to pick the right one for each situation. First: keep the `EntityManager` open by adding `@Transactional(readOnly = true)` to the service method. Most common solution. Second: use `JOIN FETCH` in the repository query to load the collection in the same query as the entity — one SQL statement, everything loaded. Third: use `@EntityGraph(attributePaths = {"reviews"})` on the repository method — declarative version of JOIN FETCH.

---

## [50:00–58:00] Slides 14–15 — Custom Repos, Projections, and DAO Pattern

Two more patterns before the summary.

Custom repository implementations. Sometimes you need a query that's too complex for naming conventions and `@Query` isn't enough — perhaps you're building a dynamic query where the number of WHERE clauses varies based on which search filters are provided. For that, you use a custom repository fragment.

Create a `BookRepositoryCustom` interface with your method signature. Create `BookRepositoryCustomImpl` — this naming convention is critical, Spring Data looks for it by name. Inject `EntityManager` and build the query manually using a `StringBuilder` for the JPQL or the Criteria API. Then have `BookRepository` extend both `JpaRepository<Book, Long>` and `BookRepositoryCustom`. Spring Data merges both implementations behind the scenes.

A cleaner alternative for dynamic filtering is Spring Data `Specification`. A `Specification<Book>` is a composable predicate — you build individual specifications like `hasCategory(category)` and `hasPriceLessThan(maxPrice)`, then combine them: `spec1.and(spec2)`. You pass the combined spec to `bookRepository.findAll(spec, pageable)`. This requires your repository to extend `JpaSpecificationExecutor<Book>` in addition to `JpaRepository`. The Criteria API underneath is covered in Part 2.

Projections. Instead of loading full entity objects when you only need two or three fields, define an interface with getters matching the fields you want. `BookSummary` with `getId()`, `getTitle()`, `getPrice()`. Declare a repository method returning `List<BookSummary>` — Spring Data generates a SELECT that fetches only those three columns. This is a real performance win for read-heavy endpoints.

For the DAO pattern awareness: if you interview at companies with legacy Spring codebases, you'll see classes named `BookDaoImpl` implementing `BookDao`. It's the same concept — abstracting data access from business logic. Spring Data Repository is the modern, Spring-idiomatic implementation of that pattern. When you see DAO in job postings, they likely mean what you've been building today.

---

## [58:00–60:00] Slide 16 — Summary

Today's Part 1 gave you the complete repository layer toolkit.

Extend `JpaRepository<T, ID>` for every entity. You get 20+ operations for free. Spring generates `SimpleJpaRepository` as the implementation at startup.

Name methods with Spring Data keywords for simple queries. `@Query` for complex JPQL or native SQL. `@Modifying` for UPDATE and DELETE. `Pageable` and `Page<T>` for pagination. `Sort` for ordering.

`@Transactional` at the service layer manages the `EntityManager` lifecycle. Dirty checking means no redundant `save()` calls. `readOnly = true` is the performance-optimized default for all reads. `LazyInitializationException` means you accessed a lazy collection after the session closed — fix with `@Transactional`, `JOIN FETCH`, or `@EntityGraph`.

Part 2 is where we build the entities these repositories operate on. JPA annotations, relationships, fetch strategies, JPQL, and the Criteria API.

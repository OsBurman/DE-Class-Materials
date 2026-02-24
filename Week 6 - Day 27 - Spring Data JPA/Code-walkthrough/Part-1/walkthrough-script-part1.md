# Walkthrough Script — Part 1: Spring Data JPA
## Day 27 — Week 6

**Total time:** ~90 minutes
**Files covered:** `01-spring-data-overview.md`, `02-book-repository.java`, `03-book-service-dao.java`

---

## SEGMENT 1 — Opening: The Problem Spring Data Solves (~10 min)

### Before opening any file

"Good morning, everyone. Today we're talking about one of the most practically useful things you'll use as a backend developer: Spring Data JPA. This is what connects your Java objects to your database — and once you understand it, you'll wonder how anyone ever lived without it.

Let me start with a question: who here has written database code in raw JDBC? Maybe in a college course or a tutorial? Show of hands."

*(pause)*

"For those who have, you know the pain. Let me show you what that looks like."

---

### Open `01-spring-data-overview.md` → JDBC example block

"This is what connecting to a database looked like without Spring Data — 20 lines of code just to find ONE record. You have to open a connection, prepare a statement, execute it, loop over the result set, manually map every column to a field, then close everything.

And if you forget to close the connection — now you have a connection leak in production. Fun stuff.

**Ask the class:** What are some problems you can see with this approach just from looking at it?

*(let them answer — expect: verbose, error-prone, easy to forget null checks, no type safety)*

Exactly. Every table you interact with requires its own version of this boilerplate. In a real app with 20 tables, that's thousands of lines of near-identical code."

---

### Spring Data Benefits table

"Spring Data's answer is: you declare what you want, and Spring figures out how to do it. One line: `bookRepository.findById(bookId)` — done.

Let me walk you through the benefits table quickly:

- **Zero boilerplate** — standard CRUD is completely generated for you
- **Query derivation** — write a method name like `findByAuthorAndPriceLessThan`, and Spring *parses that name* and generates the SQL. We'll see this in action in a few minutes.
- **Pagination built-in** — pass a `Pageable` object and you get back a `Page<T>` with the records AND metadata like total count. No extra query needed.
- **Multi-store support** — the same patterns work for MongoDB, Redis, Cassandra. Once you learn Spring Data JPA, switching to a NoSQL store is almost the same API.

> **Watch Out:** 'Zero boilerplate' doesn't mean zero responsibility. You still need to understand what SQL is being generated — especially for performance. Later today we'll enable `show-sql: true` so you can see every query Spring generates."

---

### Repository Hierarchy diagram

"Let me show you how these repositories are organized. Think of it as a family tree.

At the top, `Repository` — just a marker interface, no methods at all, just tells Spring 'this thing is a repository.'

One level down: `CrudRepository` — this is where you get your first useful methods: save, findById, findAll, count, delete.

Then `PagingAndSortingRepository` — adds `findAll(Pageable)` and `findAll(Sort)`.

Then `JpaRepository` — adds JPA-specific operations like `flush`, `saveAndFlush`, and batch delete.

**Ask the class:** Which one do you think we'll use in almost every Spring Boot application?

*(answer: JpaRepository)*

Right — `JpaRepository`. It inherits everything from all three layers above it, and it gives you the most flexibility. You almost always extend `JpaRepository`."

---

### How Automatic Implementation Works

"Here's the magic I want you to understand before we write any code.

You write an INTERFACE. Just the method signatures. No class, no implementation.

At startup, Spring scans your project, finds every interface that extends `Repository`, and uses Java dynamic proxies to generate an implementation class at runtime. That generated class delegates to `SimpleJpaRepository`, which uses `EntityManager` under the hood.

Your `@Autowired BookRepository` gets one of these generated proxy objects injected into it. You never write a class that implements your interface.

This is one of the most powerful examples of the Template Method pattern in Spring — you define the what, Spring provides the how.

> **Watch Out:** This means you can never `new BookRepository()`. There is no class to instantiate. It only exists as a Spring bean. If you try to create it outside a Spring context, you'll get an error."

---

### application.yml block

"Let me also point out the configuration before we look at code.

`ddl-auto: create-drop` — this tells Hibernate to create the database schema when the application starts and drop it when it stops. Perfect for development and demos. For production you'd use `validate` or `none` and manage schema with Flyway or Liquibase.

`show-sql: true` combined with `format_sql: true` — this prints every SQL statement Spring Data generates to your console. I strongly encourage you to leave this on during development. It's how you verify the query is what you expect."

---

## SEGMENT 2 — Repository Interface: Query Methods (~20 min)

### Open `02-book-repository.java` → top declaration

"Now let's see the real thing. Open `02-book-repository.java`.

Look at this single line: `public interface BookRepository extends JpaRepository<Book, Long>`.

That's it. That single line gives us about 20 database operations for free. No class body needed — if this file were empty, we'd already have full CRUD.

The two type parameters: `Book` — the entity this repository manages, and `Long` — the type of the primary key. Every repository has these two."

---

### Section 1 comment block (CRUD operations)

"Section 1 is just a comment — I'm documenting the CRUD methods we inherit for free. Look at the table: `save()`, `findById()`, `findAll()`, `count()`, `deleteById()`, `existsById()`.

**Ask the class:** Notice that `findById` returns `Optional<Book>` — not `Book`. Why do you think they made it an Optional?

*(let them answer — expect: because the book might not exist / null safety)*

Exactly. Instead of returning null when the book isn't found, it returns an empty `Optional`. This forces the caller to handle the 'not found' case explicitly rather than getting a NullPointerException at some random point later."

---

### Section 2 — Query methods by naming convention (walk each one)

"Section 2 is where things get interesting. These are query methods derived purely from the method name.

`findByAuthor(String author)` — Spring reads 'findBy' then 'Author', looks for a field named `author` in the `Book` entity, and generates: `SELECT * FROM books WHERE author = ?`. That's it. No SQL written.

`findByTitleContainingIgnoreCase(String keyword)` — reads 'findBy', 'Title', 'Containing' means `LIKE '%keyword%'`, 'IgnoreCase' wraps both sides in `LOWER()`. Generated SQL: `WHERE LOWER(title) LIKE LOWER('%keyword%')`. One method name, complex SQL.

`findByPriceLessThan(BigDecimal maxPrice)` — 'LessThan' becomes `< ?`.

`findByPriceBetween(BigDecimal min, BigDecimal max)` — 'Between' becomes `BETWEEN ? AND ?`. Note: two parameters here — the order matches the method parameter order.

`findByGenreAndAvailableTrue(String genre)` — 'And' combines two conditions; 'True' is shorthand for `= true`. No boolean parameter needed — it's baked into the name.

> **Watch Out:** Query method names can get very long. When you have more than two or three conditions, consider using `@Query` instead — it's easier to read and maintain. The naming convention is great for simple queries; `@Query` is better for complex ones."

**Ask the class:** `findTop5ByOrderByPriceDesc()` — what do you think the generated SQL looks like?

*(let them answer)*

Right: `SELECT * FROM books ORDER BY price DESC LIMIT 5`. The 'Top5' becomes `LIMIT 5`, 'OrderByPriceDesc' becomes `ORDER BY price DESC`."

---

### Section 3 — @Query JPQL

"Section 3: the `@Query` annotation. This is where you write queries yourself — but using JPQL instead of raw SQL.

**Key difference from SQL:** JPQL operates on *entity class names* and *field names*, not table names and column names.

Look at this: `SELECT b FROM Book b WHERE b.author = :author` — 'Book' is the Java class name, 'b' is just an alias, 'author' is the Java field name. If your entity class were called `LibraryBook`, you'd write `FROM LibraryBook lb`.

`@Param("author")` on the method parameter matches `:author` in the query. This is named parameter binding — much cleaner than positional `?1`.

`averagePriceByGenre()` returns `List<Object[]>`. Each `Object[]` element is one row, and the array contains `[genre, avgPrice]`. This is JPA's way of returning multiple columns that don't map to a single entity — you have to cast yourself.

The bulk update query at the bottom — `markOutOfStockBooksUnavailable()` — has two extra annotations:
- `@Modifying` — required for any UPDATE or DELETE statement. Without it, Spring throws an exception because it expects a SELECT.
- `@Transactional` — bulk modifications must run in a transaction. Without it, you'll get a `TransactionRequiredException`.

> **Watch Out:** `@Modifying` without `@Transactional` will throw at runtime, not compile time. This is a very common gotcha."

---

### Section 4 — Native SQL

"Section 4 is native SQL. `nativeQuery = true` tells Spring to send this query straight to the database driver without any JPQL parsing.

The main use cases: database-specific features like PostgreSQL's full-text search `to_tsvector`, complex joins that are hard to express in JPQL, or legacy SQL you need to reuse.

Notice the positional parameter `?1` — in native SQL you use `?1`, `?2`, not `:paramName`. Named parameters work differently in native SQL depending on the JPA provider.

> **Watch Out:** Native SQL is NOT database-portable. If you write `?1` syntax for PostgreSQL and switch to MySQL, you might get different behavior. Use native SQL sparingly and document WHY you're using it."

---

### Section 5 — Pagination

"Section 5: Pagination. This is one of the features that makes Spring Data genuinely excellent.

`Page<Book> findAll(Pageable pageable)` — callers pass a `PageRequest` object specifying the page number (0-indexed), page size, and optional sort. Spring runs TWO queries: one for the actual data `LIMIT x OFFSET y`, and one `COUNT(*)` for metadata.

The `Page<Book>` response contains:
- The list of books for that page
- `getTotalElements()` — total rows in the database
- `getTotalPages()` — calculated from total/size
- `isFirst()` / `isLast()` — useful for disabling 'previous'/'next' buttons in the UI
- `hasNext()` / `hasPrevious()`

**Ask the class:** If we have 95 books and we request page size 10, how many pages will there be?

*(answer: 10 pages — 9 full pages of 10 + 1 page with 5)*

Right. And `page.getTotalPages()` gives you 10. Page 0 through page 9. First page is `PageRequest.of(0, 10)`, last is `PageRequest.of(9, 10)`."

---

## SEGMENT 3 — Service Layer and Transactions (~25 min)

### Open `03-book-service-dao.java` → Section 1 (DAO pattern)

"Open `03-book-service-dao.java`. Section 1 explains the DAO pattern.

The DAO pattern predates Spring Data by many years. The idea is simple: separate your data access logic from your business logic. You have an interface defining the contract — `BookDao` — and an implementation class — `BookDaoImpl` — that does the actual SQL or ORM calls.

I've left `BookDaoImpl` in a comment block on purpose. Read through it — it shows you exactly what Spring Data generates for you. Notice: `entityManager.find()`, `entityManager.persist()`, `entityManager.merge()`, `entityManager.createQuery()`. This is all the boilerplate Spring Data replaces.

Understanding this helps you debug. When something goes wrong with a Spring Data query, knowing that `save()` calls `merge()` or `persist()` on the EntityManager helps you understand the error message."

---

### Class declaration — @Transactional(readOnly=true) at class level

"Look at the class-level annotation: `@Transactional(readOnly = true)`.

This is a best practice: put the default transaction behavior on the class, then override per method when needed.

`readOnly = true` means: 'for reads, don't acquire a write lock, and let the database/JPA optimize for read-only access.' Hibernate can skip dirty-checking when it knows a transaction is read-only — that's a free performance win for all your GET endpoints.

Then methods that DO write to the database override this with `@Transactional` (which defaults to `readOnly = false`)."

---

### CRUD methods in service

"Look at `getBookById`. It calls `findById`, chains `.orElseThrow()`, and throws a custom `BookNotFoundException` if the book doesn't exist. This is the correct pattern — never return null from a service method.

`createBook` — notice the business rule: we check `existsByIsbn()` before saving. This is where business logic lives — not in the repository, not in the controller. The repository is only responsible for data access; the service is responsible for business rules.

`updateBook` — this demonstrates Hibernate's *dirty checking*. We load the entity, modify its fields, and call `save()`. But here's the interesting part: even without calling `save()`, Hibernate would detect the changes and generate an `UPDATE` statement when the transaction commits. The `save()` call here is explicit and clear — I recommend always calling it, even though it's technically optional for managed entities."

---

### Section 7 — Transaction Propagation

"Section 7 is the most important section for understanding Spring transactions in real applications.

**Propagation** answers the question: 'What should happen when a `@Transactional` method calls another `@Transactional` method?'

Look at `processBookOrder` and `logAuditEntry`.

`processBookOrder` is `REQUIRED` — the default. When it runs, it creates a transaction. Then it calls `logAuditEntry`, which is `REQUIRES_NEW`.

`REQUIRES_NEW` says: 'I don't want to be part of your transaction. I'll create my own, run it, commit it, then return to you.'

The practical effect: even if `processBookOrder` rolls back — maybe the stock update fails — the audit log entry is already committed in its own separate transaction. This is how you get 'always log, even on failure' behavior.

> **Watch Out:** `REQUIRES_NEW` creates a new database connection. In high-traffic applications, this can exhaust your connection pool if used carelessly. Use it intentionally."

---

### Isolation levels

"Isolation answers: 'Can this transaction see uncommitted changes from other transactions running at the same time?'

The four levels — READ_UNCOMMITTED, READ_COMMITTED, REPEATABLE_READ, SERIALIZABLE — trade off performance for accuracy.

READ_UNCOMMITTED is fastest but dangerous: you can read data another transaction hasn't committed yet. If that other transaction rolls back, you just read phantom data.

READ_COMMITTED is the PostgreSQL default: you only see committed data, but if you read the same row twice, it might return different values if another transaction committed between reads.

REPEATABLE_READ (MySQL default): within a single transaction, the same read always returns the same values.

SERIALIZABLE: the safest but slowest. Transactions execute as if no one else is running at the same time.

**Ask the class:** For a financial calculation like `calculateTotalInventoryValue`, which isolation level would you want?

*(answer: REPEATABLE_READ or SERIALIZABLE — you want consistent values)*"

---

### Rollback rules

"The last key piece: rollback.

By default `@Transactional` ONLY rolls back for `RuntimeException` (unchecked) and `Error`. Checked exceptions do NOT cause a rollback unless you add `rollbackFor = Exception.class`.

This trips up a lot of developers. You throw a `CheckedServiceException`, it bubbles up, Spring doesn't roll back because it's checked — and now you have a half-written row in the database.

Rule of thumb: if your transactional method can throw checked exceptions that should abort the whole operation, use `rollbackFor = Exception.class`.

> **Watch Out:** Don't wrap your exceptions in a try/catch inside a `@Transactional` method and swallow them. If you catch an exception, Spring never sees it, so it never rolls back."

---

## SEGMENT 4 — Wrap-Up and Q&A (~5 min)

"Let's review what we covered in Part 1:

✅ Spring Data eliminates JDBC boilerplate — repositories are interfaces, Spring generates the implementation  
✅ Repository hierarchy: `CrudRepository` → `PagingAndSortingRepository` → `JpaRepository`  
✅ Query methods by naming convention — Spring parses the method name and generates SQL  
✅ `@Query` for JPQL (entity-based) or native SQL (database-specific)  
✅ `@Modifying` required for UPDATE/DELETE JPQL  
✅ Pagination with `Pageable` and `Page<T>` — built in, no extra code  
✅ DAO pattern — Spring Data IS your DAO  
✅ `@Transactional` — readOnly for reads, propagation for nested calls, rollbackFor for checked exceptions  

**Questions before we take a break?**

Common interview questions around this material:
- 'What is the difference between `CrudRepository` and `JpaRepository`?' — scope and JPA-specific methods
- 'How does Spring Data generate implementations?' — dynamic proxies at startup
- 'What is the N+1 problem?' — we'll see this in Part 2 with fetch types
- 'When would you use native SQL over JPQL?' — database-specific features, legacy SQL, performance

Part 2 dives into what's happening under the hood: Hibernate, entity mappings, relationships, fetch strategies, and querying with HQL and the Criteria API."

---

*[15 min break]*

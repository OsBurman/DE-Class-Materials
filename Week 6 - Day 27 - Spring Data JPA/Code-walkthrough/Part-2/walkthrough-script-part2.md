# Walkthrough Script — Part 2: Hibernate, Entity Mappings & Querying
## Day 27 — Week 6

**Total time:** ~90 minutes
**Files covered:** `01-hibernate-and-jpa-concepts.md`, `02-entity-mappings.java`, `03-hql-jpql-and-criteria-api.java`

---

## SEGMENT 1 — Opening: What Is Hibernate and How Does It Relate to JPA? (~12 min)

### Before opening any file

"Welcome back from break. Part 1 was all about Spring Data — the high-level API that makes database access easy. Part 2 is about what's happening underneath that API: Hibernate ORM and the JPA specification.

You can use Spring Data without deeply understanding Hibernate. But when you get a LazyInitializationException at midnight in production, you'll want to know what Hibernate is doing. So let's build that mental model now."

---

### Open `01-hibernate-and-jpa-concepts.md` → ORM table

"The ORM table at the top shows the mapping problem Hibernate solves.

In Java we have classes — in a database we have tables. In Java we have objects — in a database we have rows. In Java we navigate relationships by following references (`book.getAuthor()`) — in a database you do a JOIN.

These two worlds don't naturally align. That mismatch is called the **Object-Relational Impedance Mismatch** — a fancy name for 'objects and tables are fundamentally different structures.'

Hibernate's job is to translate between them so you can think in objects while the database gets relational SQL."

---

### JPA vs Hibernate diagram

"Look at the layer diagram. This is the relationship you need to have clear in your head:

**JPA** is a *specification*. It's a document that says 'here's how Java persistence should work, here are the annotations, here are the interfaces.' There is no JPA runtime — it's just a contract.

**Hibernate** is an *implementation* of that contract. It's the actual code that runs at runtime, talks to the database, generates SQL, manages the cache.

**Spring Data JPA** sits on top and adds the repository pattern — you write interfaces, Spring Data generates implementations using the JPA API (which Hibernate fulfills at runtime).

**Ask the class:** Why is it useful that JPA is a specification rather than an implementation?

*(let them answer — expect: portability, swap implementations)*

Exactly. If you code to JPA interfaces and annotations, you could theoretically swap Hibernate for EclipseLink or OpenJPA. In practice, almost everyone uses Hibernate — but the abstraction is still valuable.

> **Watch Out:** Hibernate has extensions beyond JPA — annotations like `@Formula`, `@Where`, `@Cache`. If you use those, your code is Hibernate-specific. Stick to JPA-standard annotations unless you have a specific reason."

---

### Entity States diagram

"The four entity states are important to understand for debugging.

An object in memory with no database connection is **Transient** — Hibernate doesn't know about it.

Call `save()` or `persist()` — now it's **Managed**. Hibernate is watching it. Any change to a managed entity within a transaction will be automatically written to the database when the transaction commits. This is called **dirty checking**.

Transaction ends — entity becomes **Detached**. It still has all its data, but Hibernate is no longer tracking it. If you change a detached entity and don't `merge()` it back, those changes are lost.

Call `delete()` on a managed entity — it's **Removed**. The DELETE will run when the transaction commits.

**Ask the class:** If I load a `Book` entity, set `book.setPrice(newPrice)`, but never call `bookRepository.save(book)` — does the price get updated in the database?

*(pause — let them think)*

The answer: it depends. If you're inside a `@Transactional` method and the entity is *managed*, YES — Hibernate's dirty checking will detect the change and generate an UPDATE automatically. If the entity is detached, NO. This is a common source of confusion."

---

### First-level cache code example

"The first-level cache is a nice optimization that comes for free.

Look at the code: two `find()` calls with the same ID. The first one hits the database. The second one? No SQL — Hibernate returns the cached object from the same persistence context. And because it's the same reference, `book1 == book2` is `true`.

This means within a single transaction, you'll never get two database hits for the same entity. Hibernate is already protecting you from redundant queries."

---

### `ddl-auto` table

"The `ddl-auto` setting is critical and worth memorizing.

`create-drop` is perfect for what we've been doing all week — schema gets created on boot, dropped on shutdown, clean slate every time.

`validate` is for production — it checks that your entity definitions match the actual schema and throws if they don't. It does NOT make changes.

`update` sounds convenient but is dangerous — it tries to ALTER the database to match your entities. It can fail, leave inconsistent state, or silently lose columns.

> **Watch Out: Never use `create`, `update`, or `create-drop` in production.** Use `validate` and manage schema changes with Flyway or Liquibase. This is a classic mistake that leads to data loss."

---

## SEGMENT 2 — Entity Mappings (~30 min)

### Open `02-entity-mappings.java` → @Entity and @Table

"Open `02-entity-mappings.java`. Let's look at our `Book` entity.

`@Entity` — this is the most fundamental JPA annotation. It says: 'this class represents a row in a database table.' Hibernate will manage it.

`@Table(name = "books")` — we're being explicit about the table name. Without this, JPA uses the class name. The table configuration also defines unique constraints and indexes using `@UniqueConstraint` and `@Index` — these appear in the DDL when Hibernate creates the schema.

The `uniqueConstraints` array at `@Table` level is how you handle multi-column unique constraints — constraints that span more than one column together. A single `unique = true` on `@Column` only handles single-column uniqueness.

The Lombok annotations: `@Getter`, `@Setter`, `@NoArgsConstructor` — Hibernate **requires** a no-args constructor to create entity instances via reflection. Never omit it. `@ToString(exclude = {...})` — we exclude `reviews` and `authors` for two reasons: to prevent infinite recursion (since Review has a back-reference to Book) and to prevent accidentally triggering lazy-loaded collections in toString."

---

### @Id and @GeneratedValue

"Now the primary key section.

`@Id` marks this field as the primary key. That's simple.

`@GeneratedValue` tells JPA how to auto-generate the ID. There are four strategies:

`IDENTITY` — delegates to the database auto-increment feature. Works great for MySQL and PostgreSQL. One catch: because the ID is generated by the DB, Hibernate has to INSERT first and then read the generated ID back. This makes batch inserts less efficient.

`SEQUENCE` — uses a database sequence object. Hibernate can pre-allocate a range of IDs (`allocationSize = 50` means grab 50 IDs at once) without hitting the database every time. Much better for batch performance. I've left the sequence example commented out — show students what it looks like.

`AUTO` — Hibernate chooses based on the database dialect. Usually SEQUENCE for PostgreSQL, IDENTITY for MySQL.

> **Watch Out:** `IDENTITY` and batch inserts don't mix well. If you're inserting thousands of records, use `SEQUENCE` with a large `allocationSize` for a major performance improvement."

---

### @Column section

"The `@Column` annotations let you fine-tune how each field maps to a database column.

The most important properties:
- `name` — explicit column name (use snake_case to match DB conventions)
- `nullable = false` — generates NOT NULL in DDL
- `length` — VARCHAR length (default 255 if omitted)
- `precision` and `scale` — for BigDecimal fields: `precision = 10, scale = 2` means 8 digits before decimal, 2 after — perfect for prices up to $99,999,999.99

`@Lob` on the `coverImage` byte array — this stores binary data in a BLOB column. For PostgreSQL it uses a `bytea` type.

`@Transient` on `computedDisplayTitle` — this field exists in Java but is NOT stored. Maybe it's calculated from title + author, or formatted for display. Hibernate completely ignores it.

`@Enumerated(EnumType.STRING)` — the enum is stored as its string name ('HARDCOVER', 'PAPERBACK'). **Always use `EnumType.STRING`**, never `ORDINAL`. With ORDINAL, adding a new enum value in the middle reorders all positions and corrupts your data.

**Ask the class:** What happens if you have `@Column(nullable = false)` but you insert an entity with that field as null?

*(answer: database constraint violation — throws DataIntegrityViolationException)*"

---

### @ManyToOne — Book → Category

"Now the relationships. These are where most students get confused, so let's take them slowly.

`@ManyToOne` on `category` — many books belong to one category. This is the most common relationship type.

`@JoinColumn(name = "category_id")` — this tells JPA: 'the foreign key column is called `category_id` and it lives in the `books` table.'

The side with `@JoinColumn` is called the **owning side** — it holds the actual FK column in the database.

`FetchType.LAZY` — don't load the category until we actually access `book.getCategory()`. This is correct. Loading 100 books should not immediately load 100 categories unless you need them.

> **Watch Out:** JPA's default for `@ManyToOne` is actually EAGER. This trips people up. Always explicitly set `FetchType.LAZY` on `@ManyToOne` unless you have a specific reason to be eager."

---

### @OneToMany — Book → Reviews

"The reverse side: `@OneToMany` on `reviews`.

`mappedBy = 'book'` — this is critical. It tells JPA: 'I'm the inverse side of this relationship. The Review entity owns the relationship. The FK column is in the reviews table, not here.'

Without `mappedBy` you'd get a **join table** created — a separate `book_reviews` table. That's wrong for this scenario.

`cascade = CascadeType.ALL` — any operation on Book cascades to its Reviews: save, merge, delete, refresh.

`orphanRemoval = true` — if I do `book.getReviews().remove(review)`, that review gets deleted from the database. The child has no parent → delete it.

**Ask the class:** If I delete a Book, what happens to its Reviews with these cascade settings?

*(answer: Reviews are also deleted — CascadeType.REMOVE cascades the delete)*"

---

### @ManyToMany — Book ↔ Author

"The most complex one: `@ManyToMany`.

A book can have multiple authors; an author can have multiple books. The database handles this with a join table — `book_authors` — that has two FK columns: `book_id` and `author_id`.

`@JoinTable` specifies:
- The join table name
- `joinColumns` — the FK pointing TO this entity (Book → book_id)
- `inverseJoinColumns` — the FK pointing TO the other entity (Author → author_id)

On the Author side, we have `mappedBy = 'authors'` — Author is the inverse side.

For cascade: `CascadeType.PERSIST` and `CascadeType.MERGE` only. **Never put `CascadeType.REMOVE` on a ManyToMany.** If you delete a Book, you do NOT want to delete the Author entities — other books still have those authors.

Notice we use `Set<Author>` not `List<Author>` — Sets prevent duplicates. In ManyToMany, accidentally adding the same author twice would create a duplicate row in the join table."

---

### Fetch types and N+1 comment block

"The comment block under Section 5 is crucial. Let me read through the N+1 problem with you.

Imagine `@ManyToOne` on Category is EAGER. You call `bookRepository.findAll()` which returns 100 books. For EACH book, Hibernate fires a separate SELECT to load its Category. That's 1 query for books + 100 queries for categories = 101 queries total. As your data grows, this gets worse.

The solution: `JOIN FETCH` in your JPQL query forces Hibernate to load everything in a single JOIN. We'll see this in file 3.

Or use `@EntityGraph` — shown in the comment. It tells Spring Data to add a `LEFT JOIN FETCH` for specified relationships automatically, without modifying every query method.

> **Watch Out: The N+1 problem is the #1 Hibernate performance issue in real applications.** You will encounter it. Turn on `show-sql: true` and count the queries — if loading 50 entities generates 51 SQL statements, you have N+1."

---

### Cascade comment block

"The cascade rules summary:
- `@OneToMany` (parent owns children): `CascadeType.ALL` + `orphanRemoval = true` ✅
- `@ManyToOne`: no cascade — the child doesn't own the parent ✅
- `@ManyToMany`: `PERSIST` + `MERGE` only — never `REMOVE` ✅

Memorize those three rules and you'll avoid most cascade mistakes."

---

## SEGMENT 3 — Querying: JPQL, HQL, and Criteria API (~35 min)

### Open `03-hql-jpql-and-criteria-api.java` → opening comment

"Open the final file. Three query approaches: JPQL strings, HQL extensions, and Criteria API.

The key rule for JPQL: use entity and field names, not table and column names. If you write `FROM books` instead of `FROM Book`, it fails. If you write `b.author_name` instead of `b.author`, it fails. The queries operate on your Java class definitions."

---

### Section 1 — JPQL Fundamentals

"Let's look at `findAllBooks`. We call `entityManager.createQuery()` with two arguments: the JPQL string and the return type class. The `TypedQuery<Book>` gives us type safety — no casting needed.

`findByGenre` introduces named parameters with `:genre`. Then `.setParameter("genre", genre)` binds the value. Always use named parameters over positional `?1` — they're more readable and order-independent.

`searchByTitle` demonstrates LIKE: `LOWER(b.title) LIKE LOWER(:pattern)` — case-insensitive search. We pass the pattern with the `%` wildcards as part of the string value, not baked into the JPQL.

`findAllOrderedByGenreAndPrice` shows multi-column ORDER BY — `genre ASC, price DESC`.

`findByMultipleGenres` uses `IN :genres` — the parameter is a `List<String>`. JPA expands it to `IN (?, ?, ?)` automatically."

---

### Section 2 — Aggregates

"`countBooks()` returns a single Long. Note `getSingleResult()` — use this only when you're sure the query returns exactly one row. If it returns 0 or more than 1, it throws an exception. For queries that might return no results, use `getResultList().stream().findFirst()` instead.

`countBooksPerGenre()` returns `List<Object[]>`. Each `Object[]` is a row; index 0 is the genre string, index 1 is the count Long. You have to cast:
```java
for (Object[] row : result) {
    String genre = (String) row[0];
    Long count = (Long) row[1];
}
```

**Ask the class:** If you want to use this data in a REST API response, what should you return instead of raw `Object[]`?

*(answer: a DTO — cleaner and type-safe)*"

---

### Section 3 — Joins and JOIN FETCH

"Section 3 is where we solve the N+1 problem.

`findBooksWithCategory()` uses a regular JOIN — only returns books that HAVE a category.

`findBooksWithCategoryFetched()` uses `JOIN FETCH` — Hibernate generates a SQL JOIN and populates both `book` and `book.category` from the single query. Compare the SQL output with `show-sql: true` — you'll see one query with a JOIN vs many separate queries.

`findAllBooksWithOptionalCategory()` uses `LEFT JOIN FETCH b.authors` and adds `DISTINCT`. Why `DISTINCT`? If a book has 3 authors, the JOIN creates 3 rows for that book. Without `DISTINCT`, you'd get the book 3 times in your list. JPQL's `DISTINCT` deduplicates at the object level, not just the SQL level.

> **Watch Out:** `DISTINCT` in JPQL is different from SQL DISTINCT. JPQL DISTINCT removes duplicate *entity objects* from the result list. SQL DISTINCT removes duplicate rows before Hibernate maps them. They're related but not identical."

---

### Section 4 — Constructor Expressions

"Constructor expressions are an elegant solution for projections — when you don't need the whole entity.

`SELECT new com.bookstore.dto.BookSummaryDTO(b.title, b.author, b.price)` — Hibernate calls this constructor directly. Result: you get a `List<BookSummaryDTO>` with only the three fields you need. No unnecessary columns loaded, no extra memory usage.

This is preferable to loading full entities when you're rendering a list view that only shows title, author, and price."

---

### Section 5 — HQL Extensions

"`findBooksWithPagination` demonstrates `setFirstResult()` and `setMaxResults()` — the API-level equivalent of LIMIT and OFFSET. This is exactly what Spring Data's `Pageable` uses internally.

`page * pageSize` = offset (how many to skip). `pageSize` = limit (how many to return).

Named queries — shown at the bottom of the file. Define once on the entity with `@NamedQuery`, use everywhere with `createNamedQuery("Book.findByGenre")`. The big advantage: validated at startup. Syntax errors in the JPQL string are caught when the application boots, not when the query runs for the first time at 3am."

---

### Section 6 — Bulk Operations

"Bulk UPDATE and DELETE bypass the persistence context entirely. They go straight to the database.

This means: if you have managed `Book` entities in your current session and you run a bulk UPDATE, the managed objects in memory are NOT updated — only the database changes. If you try to use the managed entities after the bulk update, they're stale.

That's why `@Modifying(clearAutomatically = true)` exists in Spring Data — it clears the persistence context after a bulk operation so stale entities can't cause problems."

---

### Section 7 — Criteria API

"Now the Criteria API. This looks verbose compared to JPQL strings, and it is — but it has one huge advantage: **dynamic queries**.

Look at the four-step pattern: get `CriteriaBuilder` from `EntityManager`, create a `CriteriaQuery`, define a `Root`, apply conditions, execute.

The basic `findAllWithCriteria()` — just SELECT FROM Book. More lines than `"SELECT b FROM Book b"` but same result.

`findByGenreAndMaxPriceWithCriteria()` combines two predicates with `cb.and()`. The `cb` (CriteriaBuilder) is your factory for ALL the pieces: `cb.equal()`, `cb.lessThanOrEqualTo()`, `cb.greaterThan()`, `cb.like()`, `cb.and()`, `cb.or()`, `cb.count()`, `cb.avg()` — everything.

**The real use case: `dynamicSearch()`**. This method accepts four optional filter parameters. For each one that's not null, it adds a predicate to the list. At the end, it combines them all with `cb.and()`.

**Ask the class:** How would you implement this same logic with a JPQL string?

*(let them think — they'll realize you'd have to string-concatenate conditionally, which is messy and SQL-injection-prone)*

Exactly — you'd be building a string dynamically, which is ugly and fragile. Criteria API is the clean solution for dynamic search forms where users can filter by any combination of fields."

---

### Criteria COUNT example

"`countBooksWithCriteria()` shows using Criteria with aggregates. Note: `CriteriaQuery<Long>` — the return type matches the aggregate. `cb.count(book)` generates `COUNT(b)` in SQL.

`getSingleResult()` is safe here because COUNT always returns exactly one row."

---

## SEGMENT 4 — Wrap-Up (~8 min)

"Let's summarize Part 2:

✅ **Hibernate** is the JPA implementation — it generates SQL, manages entity state, handles caching  
✅ **JPA** is the specification — your code targets JPA interfaces and annotations for portability  
✅ **Entity states:** Transient → Managed → Detached / Removed — Hibernate tracks managed entities for dirty checking  
✅ **@Entity, @Table** — mark the class and table; use `@UniqueConstraint` for multi-column constraints  
✅ **@Id, @GeneratedValue** — PK mapping; prefer `SEQUENCE` for batch inserts  
✅ **@Column** — column names, nullability, length, precision/scale  
✅ **Relationships:** `@OneToMany` (inverse side has `mappedBy`), `@ManyToOne` (owning side has `@JoinColumn`), `@ManyToMany` (join table with `@JoinTable`)  
✅ **Fetch types:** Default LAZY for collections — always explicit. Use `JOIN FETCH` to solve N+1.  
✅ **Cascade:** `ALL + orphanRemoval` for owned children; never `REMOVE` on ManyToMany  
✅ **JPQL:** Entity/field names, not table/column names; named parameters with `:param`; `JOIN FETCH` for performance  
✅ **HQL:** Hibernate superset of JPQL — nearly identical, adds pagination via `setMaxResults()`  
✅ **Criteria API:** Verbose but powerful for dynamic queries — build predicates conditionally  

**Common interview questions:**
- 'What is the N+1 problem and how do you fix it?' — lazy loading + missing JOIN FETCH → fix with JOIN FETCH or @EntityGraph
- 'What is dirty checking in Hibernate?' — managed entities are automatically flushed on transaction commit
- 'What is the difference between `persist()` and `merge()`?' — persist: new entity (transient → managed); merge: re-attach detached entity
- 'When would you use the Criteria API over JPQL?' — dynamic queries with optional/variable filters
- 'What is `ddl-auto: validate` and when do you use it?' — production setting that validates schema without modifying it

**Congratulations — you've covered Spring Data JPA front to back today. Tomorrow we'll put it all to the test with Testing, where you'll write unit and integration tests for the very repositories and services you built today.**"

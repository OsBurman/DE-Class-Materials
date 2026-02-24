# Day 27 Part 2 — Hibernate ORM: Entities, Relationships, Fetch Types & JPQL
## Lecture Script

**Total Time:** 60 minutes
**Delivery pace:** ~165 words/minute — conversational, instructor-led

---

## [00:00–02:00] Opening — From Repository Interface to Database Row

Welcome back. In Part 1 we built the repository layer — interfaces that give us CRUD, query methods, pagination. But we've been hand-waving the entity. We've talked about `Book` and `Author` without showing what makes them JPA entities.

Part 2 fills that in. We're going from the interface down to the database. By the end of this session you'll know how to map every Java class to a database table, how to define every type of entity relationship, why EAGER loading is dangerous and LAZY loading is the right default, and how to write JPQL queries.

One framing note before we dive in. You've been using `bookRepository.findById(id)` and it returns a `Book`. Who decided that `Book` maps to the `books` table? Who decided that the `author` field maps to an `author_id` foreign key? Those decisions live in the entity class — the class annotated with `@Entity`. That's what we're building today.

---

## [02:00–10:00] Slides 2–3 — JPA vs Hibernate and Entity Basics

Let me clear up terminology first because these words get used interchangeably and they shouldn't be.

**JPA** — Java Persistence API, now Jakarta Persistence — is a specification. It defines a set of annotations and interfaces: `@Entity`, `@Id`, `@ManyToOne`, `EntityManager`, `TypedQuery`. It's a contract. JPA itself doesn't connect to a database.

**Hibernate** is an implementation of that contract. It takes your JPA annotations, connects to the database, generates SQL, manages sessions, handles caching. Spring Boot auto-configures Hibernate as the JPA provider when you add `spring-boot-starter-data-jpa`.

The relationship: your code uses `jakarta.persistence` annotations — that's JPA. Hibernate reads those annotations and does the work. This separation matters for portability: if you use standard JPA annotations, you could theoretically swap Hibernate for EclipseLink. In practice, virtually all Spring Boot applications use Hibernate, and Hibernate has its own useful extensions that go beyond the JPA standard. I'll flag when we're using JPA vs Hibernate-specific features.

Now the minimum entity. The `@Entity` annotation tells JPA: "this class maps to a database table." That's the declaration. `@Table(name = "books")` overrides the default table name. Without it, JPA uses the class name in lowercase: `book`. It's good practice to be explicit.

There's a requirement you must not forget: every entity class needs a no-argument constructor. Not necessarily public — protected is fine. JPA uses it to instantiate entities when loading from the database. If your class only has a parameterized constructor, JPA will throw a runtime error. With Lombok, `@NoArgsConstructor(access = AccessLevel.PROTECTED)` handles this cleanly.

Two things to think about with `equals()` and `hashCode()`. If you use Lombok's `@Data` or `@EqualsAndHashCode`, it generates these from all fields. That's dangerous with JPA because of lazy proxies — a Hibernate proxy object wrapping an entity may not have all fields initialized. The safest approaches: implement `equals` based on a business key like ISBN, or implement based on the ID field alone after checking for null (the ID is null before the entity is persisted). I showed both patterns on slide 3.

---

## [10:00–20:00] Slides 4–5 — Primary Keys and Column Mappings

`@Id` marks the primary key field. Every entity must have one.

`@GeneratedValue` tells JPA how to generate the ID. Four strategies.

`IDENTITY` is the most common for MySQL and PostgreSQL. It delegates ID generation to the database's auto-increment or SERIAL mechanism. The database assigns the ID when the row is inserted. Because Hibernate needs to know the ID after INSERT to put the entity in the first-level cache, it cannot batch INSERT statements when using `IDENTITY` — every save is a separate round-trip. For most applications this is fine. For bulk inserts of thousands of records, the next strategy is better.

`SEQUENCE` uses a database sequence object. Hibernate pre-allocates a block of IDs from the sequence — the `allocationSize` on `@SequenceGenerator` defaults to 50, meaning Hibernate fetches a sequence value and then assigns 50 IDs locally without database calls. This enables batching. PostgreSQL defaults to sequence-based ID generation, so `GenerationType.AUTO` on PostgreSQL effectively uses sequences.

`AUTO` lets JPA pick the strategy based on the database dialect. Works, but less predictable across different databases. I recommend being explicit.

`TABLE` creates a separate table to simulate sequences. Portable but requires row-level locking — it's slow and rarely used.

For UUIDs: since JPA 3.1 / Spring Boot 3, `@GeneratedValue(strategy = GenerationType.UUID)` works. Before that, you'd set `private UUID id = UUID.randomUUID()` in the class body.

Now column mappings. `@Column` lets you customize every aspect of how a field maps to a column.

`name` overrides the column name. Without it, the column name is the field name in snake_case by default with Hibernate's naming convention. `nullable = false` adds a `NOT NULL` constraint. `length = 200` sets the VARCHAR length — the default is 255, which is often too long or too short. `unique = true` adds a `UNIQUE` constraint at the column level. Use `@Table`'s `uniqueConstraints` for composite unique constraints spanning multiple columns.

`columnDefinition = "TEXT"` lets you specify the raw SQL type. Use this when you need PostgreSQL's `TEXT` type or a `JSONB` column.

`insertable = false, updatable = false` — a pattern you'll see for timestamps: `@Column(updatable = false, nullable = false)` on `createdAt` ensures the creation time is set once and never changes.

Three special Hibernate annotations: `@CreationTimestamp` sets the value on INSERT automatically. `@UpdateTimestamp` updates on every UPDATE. `@Version` for optimistic locking — Hibernate increments this field on every UPDATE and throws `OptimisticLockException` if two concurrent transactions both read the same version and try to save. This prevents lost updates without database-level locks.

For enums: always use `@Enumerated(EnumType.STRING)`. The alternative, `EnumType.ORDINAL`, stores the integer position of the enum constant — and if you ever reorder or insert enum values, all your stored data becomes wrong. `STRING` stores the name and is immune to enum reordering.

One more mapping pattern before we get to relationships: `@Embeddable` and `@Embedded`. An `@Embeddable` class is a value object — it has no table of its own. Its fields become columns in the owning entity's table. The canonical example is an `Address` class: `street`, `city`, `postalCode`, `country`. Instead of creating a separate `addresses` table with a foreign key, you annotate `Address` with `@Embeddable` and embed it directly in the `Customer` entity with `@Embedded`. The database gets four extra columns on the `customers` table — no join needed. If you need to embed the same type twice — a shipping address and a billing address — use `@AttributeOverrides` to rename the column set for the second embedding so the names don't collide. It's one of the cleanest patterns in JPA for grouping related fields into a reusable class without the overhead of a separate table.

---

## [20:00–32:00] Slides 6–9 — Relationships

All four relationship types. This is the heart of entity modeling.

**@ManyToOne and @OneToMany.** Many books belong to one author. One author has many books. This is the most common relationship you'll define.

Start with the simplest form: unidirectional `@ManyToOne` on the `Book` entity. The `books` table has an `author_id` column that's a foreign key to `authors`. In JPA, you express this as: `@ManyToOne(fetch = FetchType.LAZY)` followed by `@JoinColumn(name = "author_id")`.

The `@JoinColumn` says: "the foreign key column in this entity's table is named `author_id`." The entity that has `@JoinColumn` is called the **owning side**. I need you to remember this term — it's the controlling side.

Now, if you also need to navigate from an author to their books, you add `@OneToMany` to `Author`. The key attribute: `mappedBy = "author"`. This string is the name of the field on `Book` that has the `@JoinColumn`. `mappedBy` is how you tell JPA: "the foreign key is managed on the other side — this side is just the inverse view."

Why does this matter? Because of the golden rule: **only changes to the owning side are persisted.** If you add a book to `author.getBooks()` but don't set `book.setAuthor(author)`, the `author_id` foreign key will be null in the database. The `List<Book>` on the author is the inverse side — it's read-only from JPA's perspective.

The solution is helper methods. `author.addBook(book)` does two things: adds the book to the list AND calls `book.setAuthor(this)`. You always call the helper to maintain both sides of the relationship.

**Bidirectional relationship pitfalls.** Two to know. First, the `StackOverflowError`. If `toString()` on Author includes the books list, and `toString()` on Book includes the author, you have infinite recursion. Lombok's `@ToString.Exclude` on the collection field prevents this. Second, in JSON serialization with Jackson, you can get the same infinite loop. Use `@JsonManagedReference` on the parent's collection and `@JsonBackReference` on the child's reference — but honestly, the better solution is to use DTOs from Day 26. Never serialize entities directly to JSON.

**@OneToOne.** One user has one user profile. The profile table has the foreign key column. The entity with `@JoinColumn(name = "user_id", unique = true)` is the owning side — it has the FK. The `User` entity has `@OneToOne(mappedBy = "user")` — it's the inverse side. The `UNIQUE` constraint on the FK column is what enforces the one-to-one at the database level.

Always override `@OneToOne` fetch to `FetchType.LAZY`. The default is EAGER, which means every time you load a `User`, the profile is loaded too — even if you never use it.

The `@MapsId` pattern: if you want the profile to share the user's primary key — no separate ID column — annotate the FK field with `@MapsId`. The profile's `id` is now the same value as the user's `id`. One column does double duty.

**@ManyToMany.** A book can have many tags, a tag applies to many books. The join table has two foreign key columns and no primary key of its own. `@JoinTable` on the owning side configures the join table: name, `joinColumns` for the FK to this entity, `inverseJoinColumns` for the FK to the other entity.

Use `Set` instead of `List` for many-to-many collections. Sets prevent accidental duplicates. With a `List`, if Hibernate needs to remove one element from a join table, it deletes all rows for that parent and re-inserts the remaining ones. With a `Set`, it deletes only the specific join row.

When you need extra columns in the join table — a timestamp for when a tag was applied, or a note field — `@ManyToMany` can't do it. Create an explicit entity for the join table with `@ManyToOne` to both sides. This is the preferred pattern for non-trivial many-to-many relationships.

---

## [32:00–42:00] Slides 10–11 — Fetch Types and Cascades

Default fetch types by relationship. `@ManyToOne` defaults to `EAGER`. `@OneToOne` defaults to `EAGER`. `@OneToMany` defaults to `LAZY`. `@ManyToMany` defaults to `LAZY`.

The `EAGER` defaults on `@ManyToOne` and `@OneToOne` are a historical mistake in the JPA spec. They exist for legacy reasons and should always be overridden. Always write `@ManyToOne(fetch = FetchType.LAZY)`. Always write `@OneToOne(fetch = FetchType.LAZY)`.

Why does this matter? The N+1 problem. Here's the scenario.

You load 100 books from the database: `bookRepository.findAll()`. That's one `SELECT * FROM books` query. Then your code iterates the books and accesses each book's author — maybe you're building a summary string, maybe you're mapping to a DTO that includes `authorName`. With the `@ManyToOne` on LAZY, each access to `book.getAuthor()` triggers a separate SELECT to load that author. 100 books, 100 separate author queries, total 101 queries. The "1" query for the list, plus "N" queries for each association. N+1.

This isn't hypothetical. It's one of the most common performance problems in JPA applications. The fix: load the association in the same query.

`JOIN FETCH` in JPQL: `SELECT b FROM Book b JOIN FETCH b.author`. This tells Hibernate to generate a SQL JOIN that loads the author with the books in a single query. One SQL statement, no extra round-trips.

`@EntityGraph(attributePaths = {"author"})` is the declarative equivalent. Add it to the repository method. Spring Data generates a LEFT JOIN for the listed attributes. Use this when you want JOIN FETCH behavior but don't want to write a `@Query`.

Now cascades. Cascade types define what happens to child entities when you perform an operation on the parent.

`CascadeType.PERSIST`: when you save a new author and call `authorRepository.save(author)`, unsaved books in the `author.books` list are also persisted. No need to call `bookRepository.save(book)` separately.

`CascadeType.MERGE`: when you `save()` a detached author, their books are also merged.

`CascadeType.REMOVE`: when you delete an author, their books are deleted too. Use with care — only cascade `REMOVE` when children don't exist independently.

`CascadeType.ALL` is the combination of all five types. It's appropriate for true parent-child relationships where the child's lifecycle is entirely owned by the parent. Author → Books is a good candidate. Don't use `ALL` on `@ManyToOne` — it would cascade a delete of the Author when you delete a Book.

`orphanRemoval = true` is different from `CascadeType.REMOVE`. With `orphanRemoval`, if you remove a book from `author.getBooks()` list, Hibernate schedules a `DELETE` for that book in the database. Without it, removing from the list only nulls the FK. This makes `orphanRemoval = true` appropriate when books cannot exist without an author.

---

## [42:00–52:00] Slides 12–14 — Entity Lifecycle, JPQL, and Named Queries

The four entity lifecycle states. Transient, Managed, Detached, Removed.

**Transient**: you just wrote `new Book("Clean Code", 39.99)`. JPA doesn't know this object exists. No `id`. No database row.

**Managed**: after `bookRepository.save(book)` for a new entity, or after `bookRepository.findById(id)`. The entity is now inside the `EntityManager`'s persistence context. Changes to this object are tracked. You don't need to call `save()` again — dirty checking will flush the changes automatically at transaction commit.

**Detached**: the `@Transactional` method returns. The `EntityManager` closes. The entity object still exists in your Java heap, but it's no longer tracked. Changes you make to it after this point are invisible to JPA. To re-attach and persist changes, you call `save()` on the detached entity — under the hood, this calls `EntityManager.merge()`.

**Removed**: you called `bookRepository.deleteById(id)` or `bookRepository.delete(book)`. The entity is scheduled for deletion. The actual `DELETE` SQL runs when the transaction commits.

Why does this matter for your day-to-day code? The biggest implication: you can update an entity inside a `@Transactional` method without calling `save()`. The entity is managed. Dirty checking handles it. But if that method returns and you try to make changes outside the transaction, those changes are lost. This is a common source of silent bugs.

Now JPQL. The key insight: JPQL is object-oriented, not table-oriented. You write `FROM Book b` not `FROM books b`. You write `b.author.lastName` not `b.author_id JOIN authors a ON ... WHERE a.last_name`. Hibernate translates the dot-notation traversal into the appropriate SQL JOIN automatically.

The syntax mirrors SQL closely: `SELECT`, `FROM`, `WHERE`, `JOIN`, `GROUP BY`, `HAVING`, `ORDER BY`. Named parameters with `:paramName` and the corresponding `@Param("paramName")` in the repository method. Positional parameters with `?1`, `?2` — less readable, but you'll see them in older code.

Aggregate functions work as expected: `COUNT`, `SUM`, `AVG`, `MIN`, `MAX`. You can group by entity fields, apply HAVING clauses.

The `LEFT JOIN FETCH` vs `JOIN FETCH` distinction: `JOIN FETCH` is an inner join — books without an author are excluded. `LEFT JOIN FETCH` includes books even if the joined collection is empty. For optional relationships, use `LEFT JOIN FETCH`.

Named Queries: `@NamedQuery` on the entity class, referencing a query string by name. Valid JPA. You'll see them in legacy codebases. The naming convention is `EntityName.methodName`. In Spring Data, you reference them via `@Query(name = "Book.findByCategory")`. In modern code, putting the `@Query` directly in the repository is cleaner — it's closer to where it's used and keeps entities focused purely on persistence mapping.

---

## [52:00–56:00] Slide 15 — Criteria API and Spring Data Specification

The Criteria API is JPQL written in Java. It's type-safe — compilation fails if your query references a non-existent field. It's programmatic — you build the query structure at runtime based on conditions.

Here's when you need it: a search form with ten optional filter fields. The user fills in some of them. You need a WHERE clause that only includes the filters the user provided. A fixed JPQL string can't do this. Naming conventions can't do this. The Criteria API can.

`em.getCriteriaBuilder()` gives you the factory for building query predicates. `cb.createQuery(Book.class)` starts a type-safe query for `Book`. `cq.from(Book.class)` is the `FROM` clause — it returns a `Root<Book>` representing the entity. Build predicates from the root — `cb.like(book.get("title"), "%keyword%")`, `cb.equal(book.get("category"), category)`. Collect only the non-null predicates. Combine with `cb.and(predicates.toArray(...))`. Execute with `em.createQuery(cq).getResultList()`.

This works. It's verbose. **Spring Data Specification** is a cleaner abstraction over the same underlying Criteria API. You define individual, composable specifications as lambda expressions. Each is a reusable predicate: `hasCategory`, `priceLessThan`, `titleContains`. Combine them with `Specification.where(spec1).and(spec2).and(spec3)`. Pass to `bookRepository.findAll(spec, pageable)`. Your repository extends `JpaSpecificationExecutor<Book>` to enable this.

Specification is the preferred approach in Spring Data applications when you need dynamic queries. It's readable, composable, and keeps the query logic out of the entity class.

---

## [56:00–58:30] Slides 16–17 — @Embeddable and Flyway

Two more topics. Both short. Both important.

`@Embeddable` and `@Embedded`. When you have a group of related fields — an address, a money amount, a date range — that belong logically together but don't need their own table, make the class `@Embeddable`. Use `@Embedded` in the parent entity. Those fields land in the parent's table with no foreign key, no join. If you embed the same type twice — shipping address and billing address — `@AttributeOverrides` renames the second set of columns. Clean, simple, and avoids an unnecessary join every time you load the parent.

Flyway. In development, `spring.jpa.hibernate.ddl-auto=create-drop` is great — Hibernate regenerates the schema every restart. In production, set `ddl-auto=validate` and hand schema management to Flyway. Flyway reads numbered SQL files from `src/main/resources/db/migration/` — naming convention `V1__create_books_table.sql`, `V2__add_author_table.sql`, two underscores. It tracks which scripts have run in a `flyway_schema_history` table. Every startup: check history, run only new scripts, skip the rest. Your schema evolves reliably across every environment — development, staging, production — all converging to the same state. Add `flyway-core` to your dependencies and Flyway auto-configures with Spring Boot. That's all the setup it takes. Liquibase is a solid alternative with built-in rollback support — worth knowing it exists.

---

## [58:30–60:00] Slide 18 — Wrap-Up and Day 28 Preview

Full-stack JPA complete. Let me summarize what you built today.

Part 1: the repository layer. `JpaRepository` gives you all CRUD for free. Query methods derive SQL from method names. `@Query` handles complex JPQL and native SQL. Pagination with `Pageable` and `Page<T>`. Sorting with `Sort`. `@Transactional` at the service layer manages the `EntityManager` lifecycle — dirty checking means no redundant `save()` calls on managed entities.

Part 2: the entity layer. `@Entity` + `@Table` maps the class. `@Id` + `@GeneratedValue` handles primary keys — `IDENTITY` for most uses, `SEQUENCE` for bulk inserts. `@Column` customizes every field. `@Embeddable`/`@Embedded` for value objects that live in the parent's table. The four relationship types: `@ManyToOne` (FK on this entity), `@OneToMany(mappedBy)` (inverse), `@OneToOne`, `@ManyToMany` with `@JoinTable`. Always `FetchType.LAZY` — the N+1 problem will find you if you use `EAGER`. Cascade types propagate operations to children. JPQL is object-oriented SQL. Criteria API is programmatic query building. And Flyway manages your schema evolution in production with versioned SQL migration files.

Tomorrow, Day 28: Testing. You'll write unit tests for your service layer with JUnit 5 and Mockito, controller tests with `@WebMvcTest` and `MockMvc`, and repository tests with `@DataJpaTest` — which spins up a real H2 database and your actual JPA configuration. Everything you built today becomes testable tomorrow.

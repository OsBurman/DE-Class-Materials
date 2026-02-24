# Day 27 Review — Spring Data JPA
## Complete Reference Guide

---

## 1. Spring Data JPA Overview

**What it is:** A layer over JPA (Hibernate) that eliminates boilerplate repository code. Provides automatic CRUD operations, query derivation from method names, and pagination/sorting support through interface definitions.

**What it eliminates:** Manual `EntityManager` calls, `TypedQuery` creation, transaction boilerplate, and row-mapping code.

**Dependency:**
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>
```

**Auto-configuration:** `@EnableJpaRepositories` is automatically applied by Spring Boot. Spring scans for interfaces extending one of the `Repository` marker interfaces and generates proxy implementations at startup.

---

## 2. Repository Interface Hierarchy

```
Repository<T, ID>                          ← marker interface, no methods
    └── CrudRepository<T, ID>              ← save, findById, findAll, delete, count
        └── PagingAndSortingRepository<T, ID>  ← findAll(Sort), findAll(Pageable)
            └── JpaRepository<T, ID>       ← flush, saveAndFlush, deleteInBatch
```

**ListCrudRepository** (Spring Data 3.x / Spring Boot 3): same as `CrudRepository` but returns `List<T>` instead of `Iterable<T>` where applicable.

**Which to extend:**
- `JpaRepository<T, ID>` — default choice for JPA-backed repositories
- `CrudRepository<T, ID>` — if you want to hide JPA-specific methods
- Custom marker interface only — for read-only repositories

---

## 3. JpaRepository Method Reference

| Method | Description |
|--------|-------------|
| `save(entity)` | INSERT or UPDATE (based on ID presence) |
| `saveAll(iterable)` | Save multiple entities |
| `saveAndFlush(entity)` | Save and immediately flush to DB |
| `findById(id)` | Returns `Optional<T>` |
| `findAll()` | All entities |
| `findAll(Sort)` | All entities with sorting |
| `findAll(Pageable)` | Paginated results |
| `findAllById(ids)` | `List<T>` for given IDs |
| `getReferenceById(id)` | Lazy proxy (throws if not found when accessed) |
| `existsById(id)` | `boolean` |
| `count()` | Total entity count |
| `deleteById(id)` | DELETE by primary key |
| `delete(entity)` | DELETE by entity reference |
| `deleteAll(iterable)` | DELETE multiple entities |
| `deleteAllInBatch()` | Single DELETE statement (no cascade) |
| `flush()` | Force pending changes to DB |

**`save()` vs `saveAndFlush()`:** `save()` queues the operation — it's executed when the transaction commits or Hibernate decides to flush. `saveAndFlush()` forces an immediate `INSERT`/`UPDATE`. Use `saveAndFlush()` when downstream code in the same transaction relies on the row being in the database.

**`deleteAll()` vs `deleteAllInBatch()`:** `deleteAll()` loads entities then deletes one-by-one (triggers cascades). `deleteAllInBatch()` issues one `DELETE FROM books` statement — fast but bypasses cascades and listeners.

---

## 4. How Spring Generates Repository Implementations

1. On startup, Spring scans for interfaces that extend a `Repository` sub-interface.
2. For each, it creates a dynamic proxy backed by `SimpleJpaRepository<T, ID>`.
3. `SimpleJpaRepository` is annotated with `@Transactional(readOnly = true)` at the class level — all read operations are already transactional with optimized settings.
4. Write methods (`save`, `delete`) have `@Transactional` without `readOnly`.
5. Custom query methods are processed by `QueryExecutorMethodInterceptor`.

**You never write the implementation class.** The bean is available for injection by the repository interface type.

---

## 5. Query Method Naming Convention

Spring Data parses method names to derive JPA queries automatically.

**Structure:** `[Subject][Predicate][Criteria]`

**Subject keywords:**
| Keyword | Returns |
|---------|---------|
| `findBy` | `T`, `Optional<T>`, `List<T>` |
| `findAllBy` | `List<T>` |
| `findFirstBy` / `findTopBy` | `T`, `Optional<T>` |
| `findTop3By` | `List<T>` (limit 3) |
| `countBy` | `long` |
| `existsBy` | `boolean` |
| `deleteBy` | `void`, `long` |

**Complete example:**
```java
public interface BookRepository extends JpaRepository<Book, Long> {
    Optional<Book> findByIsbn(String isbn);
    List<Book> findByCategory(String category);
    List<Book> findByPriceLessThan(BigDecimal price);
    List<Book> findByTitleContainingIgnoreCase(String keyword);
    long countByCategory(String category);
    boolean existsByIsbn(String isbn);
    List<Book> findByAuthor_LastName(String lastName);             // nested property
    List<Book> findTop5ByOrderByCreatedAtDesc();                  // limit + sort
    List<Book> findByCategoryOrderByPriceAsc(String category);    // inline sort
}
```

---

## 6. Query Method Property Expression Keywords

| Keyword | SQL Equivalent | Example |
|---------|---------------|---------|
| `And` | `AND` | `findByFirstNameAndLastName` |
| `Or` | `OR` | `findByCategoryOrGenre` |
| `Not` | `<>` | `findByStatusNot` |
| `Between` | `BETWEEN a AND b` | `findByPriceBetween` |
| `LessThan` | `<` | `findByPriceLessThan` |
| `LessThanEqual` | `<=` | `findByPriceLessThanEqual` |
| `GreaterThan` | `>` | `findByPriceGreaterThan` |
| `GreaterThanEqual` | `>=` | `findByPriceGreaterThanEqual` |
| `Like` | `LIKE` | `findByTitleLike` |
| `NotLike` | `NOT LIKE` | `findByTitleNotLike` |
| `Containing` | `LIKE %value%` | `findByTitleContaining` |
| `StartingWith` | `LIKE value%` | `findByTitleStartingWith` |
| `EndingWith` | `LIKE %value` | `findByTitleEndingWith` |
| `IgnoreCase` | `UPPER(col)` | `findByTitleContainingIgnoreCase` |
| `IsNull` | `IS NULL` | `findByDeletedAtIsNull` |
| `IsNotNull` | `IS NOT NULL` | `findByDeletedAtIsNotNull` |
| `In` | `IN (...)` | `findByCategoryIn(List<String>)` |
| `NotIn` | `NOT IN (...)` | `findByCategoryNotIn` |
| `True` | `= true` | `findByActiveTrue` |
| `False` | `= false` | `findByActiveFalse` |
| `OrderBy` | `ORDER BY` | `findByOrderByPriceDesc` |

---

## 7. @Query Annotation Reference

### JPQL (default)
```java
@Query("SELECT b FROM Book b WHERE LOWER(b.title) LIKE LOWER(CONCAT('%', :keyword, '%'))")
List<Book> searchByTitle(@Param("keyword") String keyword);
```

```java
@Query("SELECT b FROM Book b JOIN FETCH b.author WHERE b.category = :category")
List<Book> findByCategoryWithAuthor(@Param("category") String category);
```

```java
@Query(value = "SELECT b FROM Book b WHERE b.price < :maxPrice",
       countQuery = "SELECT COUNT(b) FROM Book b WHERE b.price < :maxPrice")
Page<Book> findByMaxPrice(@Param("maxPrice") BigDecimal price, Pageable pageable);
```

**Why `countQuery` is needed with `Page<T>` + `JOIN FETCH`:** A `JOIN FETCH` in the main query conflicts with the auto-generated count query (can't count a join-fetched query). Provide a separate `countQuery` without the `JOIN FETCH`.

### Native SQL
```java
@Query(value = "SELECT * FROM books WHERE price < :price ORDER BY price ASC",
       nativeQuery = true)
List<Book> findCheaperThanNative(@Param("price") BigDecimal price);
```

### @Modifying (required for UPDATE / DELETE)
```java
@Modifying(clearAutomatically = true, flushAutomatically = true)
@Transactional
@Query("UPDATE Book b SET b.price = :price WHERE b.category = :category")
int updatePriceByCategory(@Param("price") BigDecimal price,
                          @Param("category") String category);
```

| Attribute | Purpose |
|-----------|---------|
| `clearAutomatically = true` | Clears first-level cache after the bulk update (prevents stale entities) |
| `flushAutomatically = true` | Flushes pending dirty changes before running the bulk update |
| Return type | `int` (rows affected) or `void` |

---

## 8. Pagination with Pageable and Page\<T\>

### Creating a PageRequest
```java
Pageable pageable = PageRequest.of(0, 10);                                    // page 0, size 10
Pageable sorted = PageRequest.of(0, 10, Sort.by("price").ascending());
Pageable multiSort = PageRequest.of(0, 10,
    Sort.by("category").ascending().and(Sort.by("price").descending()));
```

**Note:** Page numbers are **zero-indexed** — page 0 is the first page.

### Repository method
```java
Page<Book> findByCategory(String category, Pageable pageable);
```

### Page\<T\> structure
```json
{
  "content": [...],
  "totalElements": 150,
  "totalPages": 15,
  "number": 0,
  "size": 10,
  "first": true,
  "last": false,
  "numberOfElements": 10
}
```

### Converting Page\<Entity\> to Page\<DTO\>
```java
Page<BookDto> dtoPage = bookRepository.findByCategory(category, pageable)
    .map(book -> new BookDto(book.getId(), book.getTitle()));
```

---

## 9. Page\<T\> vs Slice\<T\>

| Feature | `Page<T>` | `Slice<T>` |
|---------|-----------|------------|
| Total count query | ✅ Yes (additional SELECT COUNT) | ❌ No |
| `getTotalElements()` | ✅ Available | ❌ Not available |
| `getTotalPages()` | ✅ Available | ❌ Not available |
| `hasNext()` | ✅ Available | ✅ Available |
| Performance | Slightly slower (extra query) | Faster (no count query) |
| Use case | Numbered pagination UI | Infinite scroll / "Load more" |

---

## 10. Sorting Reference

```java
Sort byPrice = Sort.by("price");
Sort byPriceDesc = Sort.by(Sort.Direction.DESC, "price");
Sort multi = Sort.by("category").ascending().and(Sort.by("price").descending());

// As repository parameter
List<Book> findByCategory(String category, Sort sort);
```

**Security note:** Never pass user-provided sort field names directly to `Sort.by()`. Validate against an allowlist of permitted fields to prevent information disclosure through error messages or unexpected sort behavior.

---

## 11. @Transactional in JPA Context

### What it does in JPA (beyond AOP)
- Opens an `EntityManager` (persistence context) for the method
- All JPA operations within the method share the same `EntityManager`
- First-level cache is active — same entity loaded twice returns the same object instance
- Dirty checking is active — changes to managed entities are flushed automatically at commit
- Session closes when the method returns

### Dirty Checking — No Redundant save() Calls
```java
@Transactional
public BookDto updatePrice(Long id, BigDecimal newPrice) {
    Book book = bookRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Book not found: " + id));
    book.setPrice(newPrice);        // entity is MANAGED
    // No bookRepository.save(book) needed — dirty checking handles it
    return new BookDto(book);
}
```

### readOnly = true Optimization
```java
@Transactional(readOnly = true)
public Page<BookDto> findAll(Pageable pageable) { ... }
```

- Disables dirty checking (no snapshot comparison at flush)
- Hibernate can optimize first-level cache
- JDBC connection may be routed to a read replica by some connection pools
- Apply at class level as default; override for write methods

### Class-Level Pattern
```java
@Service
@Transactional(readOnly = true)     // default for all methods
public class BookService {

    public List<BookDto> findAll() { ... }          // uses readOnly

    @Transactional                                   // override: readOnly = false
    public BookDto create(CreateBookRequest req) { ... }

    @Transactional
    public BookDto update(Long id, UpdateBookRequest req) { ... }
}
```

---

## 12. @Transactional Propagation Reference

| Propagation | Behavior |
|-------------|---------|
| `REQUIRED` (default) | Join existing transaction; create new if none |
| `REQUIRES_NEW` | Always create a new transaction; suspend existing |
| `SUPPORTS` | Use existing if present; run non-transactionally if none |
| `NOT_SUPPORTED` | Always run non-transactionally; suspend existing |
| `MANDATORY` | Must join an existing transaction; throw if none |
| `NEVER` | Must run non-transactionally; throw if existing |
| `NESTED` | Create savepoint in existing transaction |

Most service methods use `REQUIRED` (the default). Use `REQUIRES_NEW` when you need an operation to commit independently — e.g., an audit log entry that must persist even if the main transaction rolls back.

---

## 13. LazyInitializationException — Causes and Solutions

**Cause:** Accessing a lazy-loaded collection or association after the `EntityManager` (persistence context) has closed — typically after the `@Transactional` service method has returned.

```java
// ❌ LazyInitializationException — transaction closed before accessing books
Book book = bookService.findById(id);   // @Transactional method returns, session closes
book.getAuthor().getBooks();            // triggers lazy load — no session!
```

**Solution 1 — @Transactional on the calling method:** Keep the session open across all lazy accesses.

**Solution 2 — JOIN FETCH in the query:** Load the association eagerly for this specific query.
```java
@Query("SELECT b FROM Book b JOIN FETCH b.author WHERE b.id = :id")
Optional<Book> findByIdWithAuthor(@Param("id") Long id);
```

**Solution 3 — @EntityGraph:** Declarative JOIN FETCH without writing JPQL.
```java
@EntityGraph(attributePaths = {"author", "tags"})
Optional<Book> findById(Long id);
```

**Never use `spring.jpa.open-in-view=true` in production.** This keeps the `EntityManager` open for the entire HTTP request/response cycle and causes lazy loads in the view layer — harder to predict and optimize.

---

## 14. Custom Repository Implementation Pattern

When Spring Data query derivation is insufficient (complex dynamic queries, stored procedures, native queries with complex mappings):

**Step 1 — Define the custom interface:**
```java
public interface BookRepositoryCustom {
    List<Book> searchBooks(BookSearchCriteria criteria);
}
```

**Step 2 — Implement it (naming convention: InterfaceName + `Impl`):**
```java
@Repository
@RequiredArgsConstructor
public class BookRepositoryCustomImpl implements BookRepositoryCustom {
    private final EntityManager em;

    @Override
    public List<Book> searchBooks(BookSearchCriteria criteria) {
        // EntityManager queries here
    }
}
```

**Step 3 — Extend both interfaces:**
```java
public interface BookRepository extends JpaRepository<Book, Long>, BookRepositoryCustom {
}
```

Spring automatically finds the `Impl` class and delegates calls to it.

---

## 15. Spring Data Specification

Alternative to custom implementations for dynamic queries. Cleaner and composable.

**Repository must extend `JpaSpecificationExecutor<T>`:**
```java
public interface BookRepository extends JpaRepository<Book, Long>,
                                        JpaSpecificationExecutor<Book> {
}
```

**Define specifications as static factory methods:**
```java
public class BookSpecs {
    public static Specification<Book> hasCategory(String category) {
        return (root, query, cb) ->
            category == null ? null : cb.equal(root.get("category"), category);
    }

    public static Specification<Book> priceLessThan(BigDecimal max) {
        return (root, query, cb) ->
            max == null ? null : cb.lessThan(root.get("price"), max);
    }

    public static Specification<Book> titleContains(String keyword) {
        return (root, query, cb) ->
            keyword == null ? null :
                cb.like(cb.lower(root.get("title")), "%" + keyword.toLowerCase() + "%");
    }
}
```

**Composing and executing:**
```java
Specification<Book> spec = Specification
    .where(BookSpecs.hasCategory(criteria.getCategory()))
    .and(BookSpecs.priceLessThan(criteria.getMaxPrice()))
    .and(BookSpecs.titleContains(criteria.getKeyword()));

Page<Book> results = bookRepository.findAll(spec, pageable);
```

Returning `null` from a Specification is the standard way to skip that predicate when the input is null.

---

## 16. Projections

### Interface-Based Projection (Closed)
```java
public interface BookSummary {
    Long getId();
    String getTitle();
    BigDecimal getPrice();
}

List<BookSummary> findByCategory(String category);
```
Spring generates a SELECT for only the projected columns — more efficient than loading the full entity.

### DTO Constructor Expression in @Query
```java
@Query("SELECT new com.example.dto.BookSummaryDto(b.id, b.title, b.price) " +
       "FROM Book b WHERE b.category = :category")
List<BookSummaryDto> findSummariesByCategory(@Param("category") String category);
```

### Open Projection
```java
public interface BookView {
    @Value("#{target.title + ' by ' + target.author.lastName}")
    String getDisplayName();
}
```
Open projections load the full entity — no SELECT optimization.

---

## 17. DAO Pattern vs Spring Data Repository

| Aspect | DAO Pattern | Spring Data Repository |
|--------|-------------|----------------------|
| Definition location | You write the interface AND implementation | You write only the interface |
| CRUD boilerplate | Manual (EntityManager calls) | Auto-generated |
| Query location | DAO implementation class | Method name derivation or @Query |
| Testability | Mock the interface | Mock the interface (same) |
| Flexibility | Full control | High; custom impl for complex cases |
| Learning curve | More code, explicit | Less code, convention-based |

---

## 18. JPA vs Hibernate

| Aspect | JPA | Hibernate |
|--------|-----|-----------|
| Type | Specification (standard) | Implementation |
| Package | `jakarta.persistence.*` | `org.hibernate.*` |
| Portability | Portable across providers | Hibernate-specific |
| Spring Boot default | Yes — auto-configures Hibernate | Yes |

Always prefer standard `jakarta.persistence` annotations. Use Hibernate-specific annotations (`@CreationTimestamp`, `@UpdateTimestamp`, `@NaturalId`) for convenience features not in the JPA spec.

---

## 19. @Entity Requirements

```java
@Entity
@Table(name = "books")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    // fields...
}
```

**Requirements:**
1. `@Entity` annotation on the class
2. A no-arg constructor (public or protected)
3. An `@Id` field
4. Class must not be `final` (Hibernate creates subclass proxies)

**equals() / hashCode() guidance:**
- Avoid Lombok `@EqualsAndHashCode` on all fields — proxies may not have all fields initialized
- Preferred: business key (`isbn`, `email`) with null-safe comparison
- Acceptable: ID-based equality checking for null (entities not yet persisted have null ID)
- Never include mutable fields or lazy collections in `equals()`

---

## 20. @Id and @GeneratedValue Strategies

| Strategy | Mechanism | Best For | Batch Insert |
|----------|-----------|----------|-------------|
| `IDENTITY` | DB auto-increment / SERIAL | MySQL, PostgreSQL | ❌ (no batching) |
| `SEQUENCE` | DB sequence + `allocationSize` | PostgreSQL, Oracle | ✅ (batch enabled) |
| `AUTO` | JPA picks based on dialect | Portability | Depends |
| `TABLE` | Separate table for sequence tracking | Any DB (portable) | ❌ (slow) |
| `UUID` (JPA 3.1+) | UUID generation | Distributed systems | N/A |

```java
// IDENTITY
@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
private Long id;

// SEQUENCE with custom allocationSize
@Id @GeneratedValue(generator = "book_seq")
@SequenceGenerator(name = "book_seq", sequenceName = "books_id_seq", allocationSize = 50)
private Long id;

// UUID (Spring Boot 3 / JPA 3.1+)
@Id @GeneratedValue(strategy = GenerationType.UUID)
private UUID id;
```

---

## 21. @Column and @Table Reference

### @Column Attributes
```java
@Column(
    name = "title",           // column name (default: field name in snake_case)
    nullable = false,         // NOT NULL constraint
    length = 200,             // VARCHAR(200)
    unique = false,           // UNIQUE constraint
    columnDefinition = "TEXT",// raw SQL type
    insertable = true,        // include in INSERT
    updatable = true          // include in UPDATE
)
private String title;

@Column(updatable = false, nullable = false)
private LocalDateTime createdAt;
```

### @Table Attributes
```java
@Table(
    name = "books",
    schema = "inventory",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_isbn", columnNames = {"isbn"}),
        @UniqueConstraint(columnNames = {"title", "author_id"})
    },
    indexes = {
        @Index(name = "idx_category", columnList = "category"),
        @Index(name = "idx_price", columnList = "price DESC")
    }
)
```

---

## 22. Special Field Annotations

| Annotation | Purpose |
|-----------|---------|
| `@Transient` | Field is not persisted to the database |
| `@Enumerated(EnumType.STRING)` | Store enum name as VARCHAR |
| `@Enumerated(EnumType.ORDINAL)` | Store enum position as INT — **avoid** |
| `@CreationTimestamp` | Hibernate sets on INSERT (not JPA standard) |
| `@UpdateTimestamp` | Hibernate updates on every UPDATE |
| `@Version` | Optimistic locking — incremented on each UPDATE |

```java
@Transient
private String computedDisplayName;

@Enumerated(EnumType.STRING)
@Column(nullable = false)
private BookStatus status;

@CreationTimestamp
@Column(updatable = false, nullable = false)
private LocalDateTime createdAt;

@UpdateTimestamp
private LocalDateTime updatedAt;

@Version
private Long version;   // throws OptimisticLockException on concurrent update conflict
```

---

## 23. Relationship Annotations — @ManyToOne and @OneToMany

### Unidirectional @ManyToOne (Simplest)
```java
@Entity
public class Book {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private Author author;
}
```

### Bidirectional @ManyToOne / @OneToMany
```java
@Entity
public class Author {
    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Book> books = new ArrayList<>();

    // Helper method — always maintain both sides
    public void addBook(Book book) {
        books.add(book);
        book.setAuthor(this);
    }

    public void removeBook(Book book) {
        books.remove(book);
        book.setAuthor(null);
    }
}

@Entity
public class Book {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id")
    @ToString.Exclude   // prevent StackOverflowError in Lombok @ToString
    private Author author;
}
```

**The owning side rule:** The entity with `@JoinColumn` (Book) controls the foreign key. Changes to `author.getBooks()` alone will NOT persist the FK. You must set `book.setAuthor(author)` on the owning side.

---

## 24. @OneToOne Reference

```java
// Owning side (has the FK column)
@Entity
public class UserProfile {
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", unique = true)
    private User user;
}

// Inverse side
@Entity
public class User {
    @OneToOne(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private UserProfile profile;
}

// Shared primary key (@MapsId pattern)
@Entity
public class UserProfile {
    @Id
    private Long id;    // same value as user.id

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "id")
    private User user;
}
```

**Important:** Always override `@OneToOne` to `FetchType.LAZY`. The default EAGER causes the profile to be loaded every time a User is loaded.

---

## 25. @ManyToMany Reference

### Basic Pattern
```java
@Entity
public class Book {
    @ManyToMany
    @JoinTable(
        name = "book_tag",
        joinColumns = @JoinColumn(name = "book_id"),
        inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private Set<Tag> tags = new HashSet<>();   // Set prevents duplicates
}

@Entity
public class Tag {
    @ManyToMany(mappedBy = "tags")
    private Set<Book> books = new HashSet<>();
}
```

### Explicit Join Entity (when extra columns needed)
```java
@Entity
@Table(name = "book_tag")
public class BookTag {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id")
    private Book book;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tag_id")
    private Tag tag;

    private LocalDateTime taggedAt;       // extra column
    private String taggedBy;
}
```

**Use `Set` over `List`:** With a `List`, Hibernate deletes all join table rows for a parent and re-inserts the remaining ones when removing one element. With a `Set`, only the specific row is deleted.

---

## 26. Fetch Types Reference

| Relationship | Default | Recommended |
|-------------|---------|-------------|
| `@ManyToOne` | `EAGER` ❌ | `LAZY` ✅ |
| `@OneToOne` | `EAGER` ❌ | `LAZY` ✅ |
| `@OneToMany` | `LAZY` ✅ | `LAZY` ✅ |
| `@ManyToMany` | `LAZY` ✅ | `LAZY` ✅ |

Always override `@ManyToOne` and `@OneToOne` to `FetchType.LAZY`. The EAGER defaults are legacy decisions that cause N+1 problems.

---

## 27. N+1 Problem and Solutions

**The problem:**
```java
// 1 query: SELECT * FROM books WHERE category = 'Java'
List<Book> books = bookRepository.findByCategory("Java");

// N queries: SELECT * FROM authors WHERE id = ? (one per book)
books.forEach(book -> System.out.println(book.getAuthor().getLastName()));
```

**Solution 1 — JOIN FETCH in @Query:**
```java
@Query("SELECT b FROM Book b JOIN FETCH b.author WHERE b.category = :category")
List<Book> findByCategoryWithAuthor(@Param("category") String category);
```

**Solution 2 — @EntityGraph:**
```java
@EntityGraph(attributePaths = {"author", "tags"})
List<Book> findByCategory(String category);
```

**Solution 3 — @Transactional on the calling method:** Keeps the session open so lazy loads work, but doesn't reduce the number of queries.

---

## 28. Cascade Types Reference

| CascadeType | Effect |
|-------------|--------|
| `PERSIST` | Child is persisted when parent is persisted |
| `MERGE` | Child is merged when parent is merged |
| `REMOVE` | Child is deleted when parent is deleted |
| `REFRESH` | Child is refreshed when parent is refreshed |
| `DETACH` | Child is detached when parent is detached |
| `ALL` | All of the above |

**When to use `CascadeType.ALL`:** On `@OneToMany` when children cannot exist without their parent.

**When NOT to use `CascadeType.REMOVE`:** On `@ManyToOne`. Cascading remove from a book to the author would delete the author when a single book is deleted.

```java
// Correct: cascade on parent side only
@OneToMany(mappedBy = "author", cascade = CascadeType.ALL, orphanRemoval = true)
private List<Book> books;

// Never do this — would delete the Author when deleting a Book
@ManyToOne(cascade = CascadeType.REMOVE)   // ❌
private Author author;
```

---

## 29. orphanRemoval

```java
@OneToMany(mappedBy = "author", cascade = CascadeType.ALL, orphanRemoval = true)
private List<Book> books;
```

| Scenario | `orphanRemoval = true` | `orphanRemoval = false` |
|---------|------------------------|------------------------|
| Remove book from list | `DELETE FROM books WHERE id = ?` | Sets `author_id = NULL` |
| Delete parent author | Cascades delete to all books (via `CascadeType.REMOVE`) | Depends on cascade setting |

Use `orphanRemoval = true` when child records should be deleted when disassociated from their parent.

---

## 30. Entity Lifecycle States

```
new Book()          ─── save()/persist() ──▶  MANAGED
TRANSIENT                                     (dirty checking on)
                                                    │
                    ◀── session closes / detach() ──┘
                    DETACHED
                    (changes ignored)
                         │
                    ─── save()/merge() ──▶ back to MANAGED

MANAGED ─── delete() / deleteById() ──▶ REMOVED
                                        (DELETE on commit)
```

| State | JPA Awareness | Dirty Checking | Changes Persisted |
|-------|--------------|----------------|------------------|
| Transient | None | No | No |
| Managed | Yes | Yes | Yes (at flush) |
| Detached | None | No | No (until merge) |
| Removed | Yes | No | No (deletion pending) |

---

## 31. JPQL Syntax Reference

```sql
-- Basic select
SELECT b FROM Book b WHERE b.price < 30.00

-- Named parameters
SELECT b FROM Book b WHERE b.category = :category

-- Positional parameters
SELECT b FROM Book b WHERE b.category = ?1

-- JOIN (navigation-based)
SELECT b FROM Book b JOIN b.author a WHERE a.lastName = :name

-- JOIN FETCH (loads association eagerly)
SELECT b FROM Book b JOIN FETCH b.author WHERE b.category = :category

-- LEFT JOIN FETCH (includes nulls)
SELECT b FROM Book b LEFT JOIN FETCH b.tags WHERE b.id IN :ids

-- Aggregate functions
SELECT COUNT(b) FROM Book b WHERE b.category = :category
SELECT AVG(b.price) FROM Book b

-- GROUP BY / HAVING
SELECT b.category, COUNT(b) FROM Book b GROUP BY b.category HAVING COUNT(b) > 5

-- ORDER BY
SELECT b FROM Book b ORDER BY b.price DESC, b.title ASC

-- Constructor expression (creates DTO in query)
SELECT new com.example.dto.BookDto(b.id, b.title, b.price) FROM Book b

-- Bulk UPDATE
UPDATE Book b SET b.price = b.price * 0.9 WHERE b.category = :category

-- Bulk DELETE
DELETE FROM Book b WHERE b.category = :category
```

**Key rules:**
- Use entity class names (not table names): `Book`, not `books`
- Use field names (not column names): `b.authorId` → use `b.author` for the relationship
- JPQL is case-sensitive for entity and field names

---

## 32. HQL vs JPQL

| Feature | JPQL | HQL |
|---------|------|-----|
| Standard | JPA (portable) | Hibernate-specific |
| Package | `jakarta.persistence` | `org.hibernate` |
| Syntax | Subset of HQL | Superset of JPQL |
| Extra features | — | `fetch all properties`, Hibernate filter integration |
| Day-to-day usage | Identical for common queries | Identical for common queries |
| Recommendation | Prefer for portability | Use when JPQL falls short |

For everyday queries, JPQL and HQL are effectively identical. Prefer JPQL.

---

## 33. @NamedQuery

```java
@Entity
@NamedQuery(
    name = "Book.findByCategory",
    query = "SELECT b FROM Book b WHERE b.category = :category"
)
public class Book { ... }
```

Reference in repository:
```java
@Query(name = "Book.findByCategory")
List<Book> findByCategory(@Param("category") String category);
```

**When to use:** Legacy codebases. Modern practice is to put the `@Query` string directly in the repository interface — it's closer to the usage, easier to find, and keeps entity classes focused on mapping.

---

## 34. Criteria API Basics

```java
@Repository
@RequiredArgsConstructor
public class BookRepositoryCustomImpl implements BookRepositoryCustom {
    private final EntityManager em;

    public List<Book> searchBooks(String category, BigDecimal maxPrice) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Book> cq = cb.createQuery(Book.class);
        Root<Book> book = cq.from(Book.class);

        List<Predicate> predicates = new ArrayList<>();

        if (category != null) {
            predicates.add(cb.equal(book.get("category"), category));
        }
        if (maxPrice != null) {
            predicates.add(cb.lessThan(book.get("price"), maxPrice));
        }

        cq.where(cb.and(predicates.toArray(new Predicate[0])));
        cq.orderBy(cb.asc(book.get("price")));

        return em.createQuery(cq).getResultList();
    }
}
```

**When to use:** Dynamic queries where the query structure can't be determined at compile time. Spring Data Specification is the preferred abstraction for this in modern Spring Data applications.

---

## 35. Common Mistakes and Fixes

| Mistake | Symptom | Fix |
|---------|---------|-----|
| Using `EAGER` fetch on `@ManyToOne` | N+1 queries | Add `fetch = FetchType.LAZY` |
| Setting only the inverse side of a bidirectional relationship | FK is NULL in DB | Always set the owning side; use helper methods |
| `LazyInitializationException` | Exception when accessing lazy field outside session | Wrap caller in `@Transactional`, use `JOIN FETCH`, or `@EntityGraph` |
| `@ToString` on bidirectional entities | `StackOverflowError` | Add `@ToString.Exclude` to the collection field |
| `@EqualsAndHashCode` on all fields | Broken hashCode on uninitialized proxies | Use business key or ID-only equality |
| `CascadeType.REMOVE` on `@ManyToOne` | Deleting child deletes parent | Remove `REMOVE` from `@ManyToOne` cascade |
| `@Enumerated(EnumType.ORDINAL)` | Data corruption when enum is reordered | Use `EnumType.STRING` |
| `@Modifying` without `clearAutomatically` | Stale entity in first-level cache after bulk update | Add `clearAutomatically = true` |
| Calling `save()` on already-managed entity inside `@Transactional` | Redundant SQL (minor, not a bug) | Omit `save()` — dirty checking handles it |
| No `@Transactional` on `@Modifying` method | `InvalidDataAccessApiUsageException` | Add `@Transactional` to the method |

---

## 36. Full Annotation Cheat Sheet

### Entity and Table
| Annotation | Purpose |
|-----------|---------|
| `@Entity` | Marks class as JPA entity |
| `@Table(name = "...")` | Customize table name |
| `@Id` | Primary key field |
| `@GeneratedValue(strategy = ...)` | ID generation strategy |
| `@Column(...)` | Column customization |
| `@Transient` | Not persisted |
| `@Enumerated(EnumType.STRING)` | Enum as string |
| `@Version` | Optimistic locking |
| `@CreationTimestamp` | Set on INSERT (Hibernate) |
| `@UpdateTimestamp` | Set on UPDATE (Hibernate) |

### Relationships
| Annotation | Purpose |
|-----------|---------|
| `@ManyToOne(fetch = LAZY)` | FK in this entity's table |
| `@OneToMany(mappedBy = "...")` | Inverse of @ManyToOne |
| `@OneToOne(fetch = LAZY)` | FK in this entity's table |
| `@ManyToMany` | Join table required |
| `@JoinColumn(name = "...")` | FK column name |
| `@JoinTable(...)` | Join table for @ManyToMany |
| `@MapsId` | Shared primary key for @OneToOne |

### Cascade
| Annotation | Purpose |
|-----------|---------|
| `cascade = CascadeType.ALL` | Propagate all operations |
| `cascade = CascadeType.PERSIST` | Propagate save |
| `cascade = CascadeType.REMOVE` | Propagate delete |
| `orphanRemoval = true` | DELETE detached children |

### Spring Data Repository
| Annotation | Purpose |
|-----------|---------|
| `@Query("JPQL...")` | Explicit query string |
| `@Param("name")` | Named parameter binding |
| `@Modifying` | Required for UPDATE/DELETE queries |
| `@EntityGraph(attributePaths = {...})` | Eager load specified associations |
| `@Transactional` | Transaction boundary |
| `@Transactional(readOnly = true)` | Read-only optimization |

---

## 37. @Embeddable / @Embedded — Value Objects

An `@Embeddable` class is a **value object**: it has no table of its own. Its fields become columns in the owning entity's table. Use it for reusable groupings of related fields that belong to a parent entity and have no independent identity.

### Basic Example
```java
@Embeddable
public class Address {
    @Column(name = "street", length = 200)
    private String street;
    @Column(name = "city", length = 100)
    private String city;
    @Column(name = "postal_code", length = 20)
    private String postalCode;
    @Column(name = "country", length = 50)
    private String country;
    protected Address() {}   // required no-arg constructor
    // getters, all-args constructor...
}

@Entity
@Table(name = "customers")
public class Customer {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;

    @Embedded
    private Address shippingAddress;        // → street, city, postal_code, country columns

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "street",     column = @Column(name = "billing_street")),
        @AttributeOverride(name = "city",       column = @Column(name = "billing_city")),
        @AttributeOverride(name = "postalCode", column = @Column(name = "billing_postal_code")),
        @AttributeOverride(name = "country",    column = @Column(name = "billing_country"))
    })
    private Address billingAddress;         // same type, renamed columns
}
```

### Resulting Table (no join, no FK)
```
customers: id, name, street, city, postal_code, country,
           billing_street, billing_city, billing_postal_code, billing_country
```

### Rules
| Rule | Detail |
|------|--------|
| No-arg constructor | `@Embeddable` class must have one (same as `@Entity`) |
| `@Embedded` optional | If field type is `@Embeddable`, annotation can be omitted |
| Duplicate type | Use `@AttributeOverrides` to rename column set for second embedding |
| Null handling | If all embedded columns are `NULL`, Hibernate sets field to `null` |
| No `@Id` | `@Embeddable` classes must NOT have an `@Id` field |

### Common Use Cases
- `Address` (street, city, postalCode, country)
- `Money` (amount, currency)
- `DateRange` (startDate, endDate)
- `GeoCoordinates` (latitude, longitude)
- `PhoneNumber` (countryCode, number)

---

## 38. Flyway and Schema Migrations

Flyway is a database migration tool that manages schema evolution through versioned SQL scripts.

### Why Not `ddl-auto` in Production?
| Setting | Suitable for | Problem in production |
|---------|-------------|-----------------------|
| `create-drop` | Development (H2) | Destroys all data on restart |
| `update` | Development only | Can't drop columns; can corrupt schema |
| `validate` | Production (with Flyway) | ✅ Only checks — Flyway manages changes |
| `none` | Production (with Flyway) | ✅ Hibernate does nothing |

### How Flyway Works
```
src/main/resources/db/migration/
├── V1__create_books_table.sql
├── V2__create_authors_table.sql
├── V3__add_category_to_books.sql
└── V4__add_category_index.sql
```
1. Flyway scans the migration folder at application startup
2. Compares scripts against `flyway_schema_history` table
3. Runs any scripts not yet applied (in version order)
4. Records applied scripts with checksum — modifying applied scripts throws an error

### Naming Convention
`V{version}__{description}.sql` — two underscores between version and description

| Part | Example | Rule |
|------|---------|------|
| Prefix | `V` | Always uppercase V |
| Version | `1`, `2`, `1.1` | Integer or dot-separated |
| Separator | `__` | Two underscores |
| Description | `create_books_table` | Underscores become spaces in history |
| Extension | `.sql` | SQL file |

### Spring Boot Setup
```xml
<dependency>
    <groupId>org.flywaydb</groupId>
    <artifactId>flyway-core</artifactId>
</dependency>
```
```properties
spring.jpa.hibernate.ddl-auto=validate
# Flyway auto-configures and runs on startup — no extra @EnableFlyway needed
```

### Example Migration Files
```sql
-- V1__create_books_table.sql
CREATE TABLE books (
    id         BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    title      VARCHAR(200) NOT NULL,
    isbn       VARCHAR(20)  NOT NULL UNIQUE,
    price      DECIMAL(10,2) NOT NULL,
    category   VARCHAR(50),
    author_id  BIGINT,
    created_at TIMESTAMP NOT NULL
);

-- V3__add_category_index.sql
CREATE INDEX idx_books_category ON books(category);

-- V4__add_published_date.sql
ALTER TABLE books ADD COLUMN published_date DATE;
```

### Flyway vs Liquibase
| | Flyway | Liquibase |
|---|---|---|
| Changelog format | SQL (or Java) | SQL, XML, YAML, JSON |
| Learning curve | Very low | Moderate |
| Rollback support | Manual (write undo script) | Built-in rollback |
| Spring Boot integration | Full auto-config | Full auto-config |
| Recommendation | Start here | When rollback or multi-format changelogs needed |

---

## 39. Looking Ahead — Day 28

**Testing Spring Applications**

What you'll learn:
- **Unit testing** with JUnit 5 and Mockito — test service methods in isolation by mocking repositories
- **`@DataJpaTest`** — slice test that loads only JPA context + H2 in-memory database — test repository queries against a real database
- **`@WebMvcTest`** — slice test for controllers with `MockMvc` — test HTTP request/response without starting a full server
- **`@SpringBootTest`** — full integration test — entire application context, real database (or Testcontainers)
- **Testcontainers** — spin up real PostgreSQL/MySQL containers for tests that need exact DB behavior
- **WireMock** — mock external HTTP services in integration tests

Everything you built in Days 26–27 (controllers, services, repositories, entities) gets tested in Day 28.

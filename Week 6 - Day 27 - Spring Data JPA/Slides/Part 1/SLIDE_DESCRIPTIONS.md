# Day 27 Part 1 — Spring Data JPA: Repositories, Queries, Pagination & Transactions
## Slide Descriptions

---

### Slide 1 — Title Slide
**Title:** Spring Data JPA — The Repository Layer
**Subtitle:** Part 1: Repositories, Query Methods, Pagination & Transactions

**Learning objectives listed on slide:**
- Explain what Spring Data JPA provides and how it reduces boilerplate
- Define the repository hierarchy: `CrudRepository` → `JpaRepository`
- Use all `JpaRepository` CRUD operations
- Derive queries from method names using Spring Data conventions
- Write custom queries with `@Query` (JPQL and native SQL)
- Return paginated and sorted results using `Pageable` and `Page<T>`
- Apply `@Transactional` effectively in the JPA context

---

### Slide 2 — The Problem Spring Data JPA Solves
**Header:** From Boilerplate to Interface — What Spring Data Eliminates

**Without Spring Data JPA — raw JPA using EntityManager:**
```java
// Manual JPA: every repository method looks like this
@Repository
public class BookRepository {

    @PersistenceContext
    private EntityManager em;

    public Optional<Book> findById(Long id) {
        return Optional.ofNullable(em.find(Book.class, id));
    }

    public List<Book> findAll() {
        return em.createQuery("SELECT b FROM Book b", Book.class).getResultList();
    }

    public Book save(Book book) {
        if (book.getId() == null) {
            em.persist(book);
            return book;
        } else {
            return em.merge(book);
        }
    }

    public void deleteById(Long id) {
        Book book = em.find(Book.class, id);
        if (book != null) em.remove(book);
    }

    public List<Book> findByTitle(String title) {
        return em.createQuery("SELECT b FROM Book b WHERE b.title = :title", Book.class)
                 .setParameter("title", title)
                 .getResultList();
    }
}
```

**With Spring Data JPA — just an interface:**
```java
public interface BookRepository extends JpaRepository<Book, Long> {
    List<Book> findByTitle(String title);
}
```

**Spring generates the entire implementation at startup. You never write it.**

**Spring Data JPA gives you:**
- All CRUD operations for free (`save`, `findById`, `findAll`, `delete`, ...)
- Query derivation from method names (`findByTitle`, `findByAuthorLastNameContaining`, ...)
- Pagination and sorting out of the box
- `@Query` for complex JPQL and native SQL
- Integration with Spring's transaction management

---

### Slide 3 — The Repository Hierarchy
**Header:** Repository Interfaces — Choosing the Right Level

```
Repository<T, ID>
        │  (marker interface — no methods)
        ▼
CrudRepository<T, ID>
        │  save, saveAll, findById, findAll, findAllById,
        │  count, delete, deleteById, deleteAll, existsById
        ▼
PagingAndSortingRepository<T, ID>
        │  Adds: findAll(Pageable), findAll(Sort)
        ▼
JpaRepository<T, ID>
           Adds: flush, saveAndFlush, deleteAllInBatch,
                 getReferenceById, findAll(Example),
                 saveAllAndFlush, deleteAllById
```

**Spring Data 3.x also provides:**
- `ListCrudRepository<T, ID>` — same as `CrudRepository` but returns `List<T>` instead of `Iterable<T>`
- `ListPagingAndSortingRepository<T, ID>` — same but returns `List<T>`

**Which interface to extend?**
| Interface | Use When |
|---|---|
| `CrudRepository` | Simple CRUD only; want minimal API surface |
| `JpaRepository` | Spring Boot + Hibernate; want full JPA features including batch ops |
| Custom interface | Need to restrict available methods (hide delete from certain repos) |

**Type parameters:**
- `T` — the entity class (e.g., `Book`)
- `ID` — the primary key type (e.g., `Long`)

```java
// Declaring a repository
public interface BookRepository extends JpaRepository<Book, Long> { }
// Spring generates SimpleJpaRepository<Book, Long> as the implementation at startup
```

---

### Slide 4 — JpaRepository — Complete Method Reference
**Header:** JpaRepository — Everything You Get for Free

**Save / Update:**
```java
<S extends T> S   save(S entity)               // INSERT or UPDATE (uses entity ID)
<S extends T> List<S> saveAll(Iterable<S>)      // batch save
<S extends T> S   saveAndFlush(S entity)        // save + immediate flush to DB
<S extends T> List<S> saveAllAndFlush(Iterable<S>)
```

**Find:**
```java
Optional<T>       findById(ID id)
T                 getReferenceById(ID id)        // lazy proxy — use inside @Transactional
List<T>           findAll()
List<T>           findAllById(Iterable<ID> ids)
List<T>           findAll(Sort sort)
Page<T>           findAll(Pageable pageable)
boolean           existsById(ID id)
long              count()
```

**Delete:**
```java
void              deleteById(ID id)
void              delete(T entity)
void              deleteAll()
void              deleteAll(Iterable<? extends T> entities)
void              deleteAllById(Iterable<? extends ID> ids)
void              deleteAllInBatch()             // single DELETE statement — much faster
void              deleteAllByIdInBatch(Iterable<ID> ids)
```

**Flush:**
```java
void              flush()                        // sync persistence context to DB immediately
```

**Key difference — `save()` vs `saveAndFlush()`:**
- `save()` — schedules INSERT/UPDATE; actual SQL may be deferred until transaction commits
- `saveAndFlush()` — forces immediate SQL execution; needed if other code in same transaction reads that row

---

### Slide 5 — How Spring Generates Repository Implementations
**Header:** Behind the Interface — Spring Data's Proxy Magic

**Spring Boot auto-configures `@EnableJpaRepositories`.** You don't need it explicitly.

**What happens at application startup:**
```
1. Spring scans packages for interfaces extending Repository<T, ID>
2. For each, creates a JDK dynamic proxy backed by SimpleJpaRepository<T, ID>
3. Parses method names for derived queries → generates JPQL
4. Registers as Spring beans (@Repository scope)
5. SimpleJpaRepository already has @Transactional applied to all write methods
```

**SimpleJpaRepository — the actual implementation (simplified):**
```java
// This is the default implementation Spring generates — you never write this
@Repository
@Transactional(readOnly = true)                 // default: all methods read-only
public class SimpleJpaRepository<T, ID> implements JpaRepository<T, ID> {

    private final EntityManager em;

    @Override
    @Transactional                               // write methods override to readOnly = false
    public <S extends T> S save(S entity) {
        if (entityInfo.isNew(entity)) {
            em.persist(entity);
            return entity;
        } else {
            return em.merge(entity);
        }
    }

    @Override
    public Optional<T> findById(ID id) {
        return Optional.ofNullable(em.find(domainClass, id));
    }
    // ...
}
```

**Key takeaway:** `SimpleJpaRepository` already applies `@Transactional(readOnly = true)` at the class level and overrides with `@Transactional` on write methods. Your service layer `@Transactional` wraps the repository call — the repository joins the existing transaction via `REQUIRED` propagation.

---

### Slide 6 — Query Methods — Naming Convention
**Header:** Derived Queries — Spring Parses Your Method Names

Spring Data reads method names and generates JPQL automatically. No implementation required.

**Basic structure:**
```
findBy + [PropertyName] + [Keyword] + [PropertyName] + ...
```

**Complete BookRepository example:**
```java
public interface BookRepository extends JpaRepository<Book, Long> {

    // Find by exact match
    Optional<Book> findByIsbn(String isbn);
    List<Book> findByTitle(String title);
    List<Book> findByAuthor(Author author);

    // Find by nested property (author.lastName)
    List<Book> findByAuthorLastName(String lastName);
    List<Book> findByAuthorLastNameAndAuthorFirstName(String lastName, String firstName);

    // Top / First limiting
    Optional<Book> findFirstByOrderByCreatedAtDesc();   // most recently added book
    List<Book> findTop5ByOrderByPriceAsc();             // 5 cheapest books

    // Count and exists
    long countByCategory(String category);
    boolean existsByIsbn(String isbn);

    // Delete
    void deleteByIsbn(String isbn);

    // Return types vary
    List<Book>      findByCategory(String category);
    Page<Book>      findByCategory(String category, Pageable pageable);   // with pagination
    Stream<Book>    findByCategory(String category);                       // Java stream
}
```

**Supported subject keywords:** `find...By`, `read...By`, `get...By`, `query...By`, `search...By`, `stream...By`, `count...By`, `exists...By`, `delete...By`

---

### Slide 7 — Query Method Keywords Reference
**Header:** Property Expression Keywords — The Full Toolkit

**Comparison:**
```java
findByPrice(BigDecimal price)                     // = price
findByPriceNot(BigDecimal price)                  // != price
findByPriceLessThan(BigDecimal max)               // < max
findByPriceLessThanEqual(BigDecimal max)          // <= max
findByPriceGreaterThan(BigDecimal min)            // > min
findByPriceGreaterThanEqual(BigDecimal min)       // >= min
findByPriceBetween(BigDecimal min, BigDecimal max) // BETWEEN min AND max
```

**String operations:**
```java
findByTitleLike(String pattern)                   // LIKE pattern (you provide % wildcards)
findByTitleContaining(String word)                // LIKE '%word%'
findByTitleStartingWith(String prefix)            // LIKE 'prefix%'
findByTitleEndingWith(String suffix)              // LIKE '%suffix'
findByTitleContainingIgnoreCase(String word)      // case-insensitive LIKE
```

**Logical combinations:**
```java
findByTitleAndCategory(String title, String cat)       // AND
findByTitleOrIsbn(String title, String isbn)            // OR
findByCategoryIn(List<String> categories)              // IN (...)
findByCategoryNotIn(List<String> categories)           // NOT IN
```

**Null checks:**
```java
findByDeletedAtIsNull()                           // IS NULL
findByDeletedAtIsNotNull()                        // IS NOT NULL
```

**Boolean:**
```java
findByActiveTrue()                                // WHERE active = true
findByActiveFalse()                               // WHERE active = false
```

**Ordering:**
```java
findByCategoryOrderByTitleAsc(String category)
findByCategoryOrderByPriceDescTitleAsc(String category)
```

---

### Slide 8 — @Query with JPQL
**Header:** @Query — When Naming Conventions Aren't Enough

**JPQL uses entity class names and field names — not table names.**

```java
public interface BookRepository extends JpaRepository<Book, Long> {

    // Named parameters with @Param
    @Query("SELECT b FROM Book b WHERE b.title LIKE %:keyword% OR b.author.lastName LIKE %:keyword%")
    List<Book> searchByKeyword(@Param("keyword") String keyword);

    // Positional parameters (less readable, but valid)
    @Query("SELECT b FROM Book b WHERE b.price BETWEEN ?1 AND ?2 ORDER BY b.price ASC")
    List<Book> findByPriceRange(BigDecimal min, BigDecimal max);

    // JOIN FETCH — solves N+1 by loading association in one query
    @Query("SELECT b FROM Book b JOIN FETCH b.author WHERE b.category = :category")
    List<Book> findByCategoryWithAuthor(@Param("category") String category);

    // Count query
    @Query("SELECT COUNT(b) FROM Book b WHERE b.category = :category AND b.price < :maxPrice")
    long countByCategoryAndPriceLessThan(@Param("category") String category,
                                          @Param("maxPrice") BigDecimal maxPrice);

    // Constructor expression — map directly to DTO without entity materialization
    @Query("SELECT new com.example.dto.BookSummaryDto(b.id, b.title, b.price) FROM Book b WHERE b.active = true")
    List<BookSummaryDto> findActiveSummaries();

    // With pagination — add Pageable parameter; Spring Data handles count query
    @Query(
        value = "SELECT b FROM Book b JOIN FETCH b.author WHERE b.category = :category",
        countQuery = "SELECT COUNT(b) FROM Book b WHERE b.category = :category"
    )
    Page<Book> findByCategoryPaged(@Param("category") String category, Pageable pageable);
}
```

**Why a separate `countQuery`?** When you use `JOIN FETCH` and return a `Page<T>`, Spring Data needs a count query to calculate `totalPages`. `JOIN FETCH` in a count query causes errors — the separate `countQuery` removes the `FETCH` for the count.

---

### Slide 9 — @Query with Native SQL and @Modifying
**Header:** Native Queries and Modifying Operations

**Native SQL — uses actual table and column names:**
```java
@Query(value = "SELECT * FROM books WHERE isbn = :isbn", nativeQuery = true)
Optional<Book> findByIsbnNative(@Param("isbn") String isbn);

// Native with pagination — Spring handles the count query wrapping
@Query(
    value = "SELECT * FROM books WHERE category = :category ORDER BY price ASC",
    countQuery = "SELECT COUNT(*) FROM books WHERE category = :category",
    nativeQuery = true
)
Page<Book> findByCategoryNative(@Param("category") String category, Pageable pageable);
```

**@Modifying — required for UPDATE and DELETE operations:**
```java
// UPDATE query — @Modifying required
@Modifying
@Query("UPDATE Book b SET b.price = b.price * :multiplier WHERE b.category = :category")
int applyPriceAdjustment(@Param("multiplier") BigDecimal multiplier,
                          @Param("category") String category);
// Returns int = number of rows affected

// DELETE query
@Modifying
@Query("DELETE FROM Book b WHERE b.active = false AND b.deletedAt < :cutoff")
int deleteInactiveBooksBefore(@Param("cutoff") LocalDateTime cutoff);

// Native UPDATE
@Modifying
@Query(value = "UPDATE books SET price = price * :mult WHERE category = :cat", nativeQuery = true)
int adjustPricesNative(@Param("mult") BigDecimal mult, @Param("cat") String cat);
```

**Important `@Modifying` attributes:**
```java
@Modifying(clearAutomatically = true)   // clears persistence context after — prevents stale reads
@Modifying(flushAutomatically = true)   // flushes pending changes before executing — prevents missed updates
```

**When to use `@Modifying(clearAutomatically = true)`:** If you load entities, then call a bulk UPDATE in the same transaction, the persistence context (first-level cache) would still show old values. `clearAutomatically = true` evicts the cache so subsequent `findById` calls hit the database.

---

### Slide 10 — Pagination
**Header:** Pagination — Returning Data in Pages

**Pageable — the request:**
```java
// PageRequest.of(pageNumber, pageSize)  — zero-based page number
Pageable page0 = PageRequest.of(0, 20);                           // first page, 20 items
Pageable page2 = PageRequest.of(2, 20);                           // third page, 20 items
Pageable sorted = PageRequest.of(0, 20, Sort.by("title"));        // with sort
Pageable multiSort = PageRequest.of(0, 20,
    Sort.by(Direction.ASC, "category").and(Sort.by(Direction.DESC, "price")));
```

**Page<T> — the response:**
```java
@GetMapping("/api/books")
public ResponseEntity<Page<BookDto>> getBooks(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size,
        @RequestParam(defaultValue = "title") String sortBy) {

    Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
    Page<Book> bookPage = bookRepository.findAll(pageable);

    Page<BookDto> result = bookPage.map(bookMapper::toDto);
    return ResponseEntity.ok(result);
}
```

**Page<T> JSON response includes:**
```json
{
  "content": [...],
  "pageable": { "pageNumber": 0, "pageSize": 20 },
  "totalElements": 247,
  "totalPages": 13,
  "last": false,
  "first": true,
  "numberOfElements": 20,
  "empty": false
}
```

**Page<T> vs Slice<T>:**
| | `Page<T>` | `Slice<T>` |
|---|---|---|
| Runs count query | Yes | No |
| Knows total pages | Yes | No |
| Use when | Need "Page X of Y" UI | Infinite scroll / "Load more" |
| Performance | 2 queries | 1 query |

---

### Slide 11 — Sorting
**Header:** Sorting — Controlling Result Order

**Sort in repository methods:**
```java
// findAll with Sort
List<Book> books = bookRepository.findAll(Sort.by("title"));
List<Book> books = bookRepository.findAll(Sort.by(Direction.DESC, "price"));

// Combining sort fields
Sort sort = Sort.by(Direction.ASC, "category")
               .and(Sort.by(Direction.DESC, "price"))
               .and(Sort.by(Direction.ASC, "title"));
List<Book> books = bookRepository.findAll(sort);

// Sort in derived query methods
List<Book> findByCategoryOrderByPriceAsc(String category);
List<Book> findByCategoryOrderByPriceDescTitleAsc(String category);

// Sort as method parameter (allows dynamic sort at runtime)
List<Book> findByCategory(String category, Sort sort);
Page<Book>  findByCategory(String category, Pageable pageable);  // Pageable includes sort
```

**Accepting sort from the HTTP client:**
```java
@GetMapping("/api/books")
public List<BookDto> getBooks(
        @RequestParam(defaultValue = "title") String sortBy,
        @RequestParam(defaultValue = "asc") String direction) {

    Sort sort = direction.equalsIgnoreCase("desc")
            ? Sort.by(Direction.DESC, sortBy)
            : Sort.by(Direction.ASC, sortBy);

    return bookRepository.findAll(sort)
                         .stream()
                         .map(bookMapper::toDto)
                         .toList();
}
```

**⚠️ Security note:** Don't pass client-provided sort field directly to Sort.by() without validation. A malicious client could provide a field that causes an error or leaks schema info. Validate the sort field against an allowed list:
```java
private static final Set<String> ALLOWED_SORT_FIELDS = Set.of("title", "price", "createdAt");
if (!ALLOWED_SORT_FIELDS.contains(sortBy)) sortBy = "title";
```

---

### Slide 12 — @Transactional in Depth with JPA
**Header:** @Transactional and the JPA Session Lifecycle

**The EntityManager (JPA session) is the unit of work:**
- Created when a transaction begins
- Tracks all managed entities (first-level cache / identity map)
- Detects changes to managed entities (dirty checking)
- Flushes changes to DB (on transaction commit or explicit `flush()`)
- Closed when transaction ends

```java
@Service
@Transactional(readOnly = true)              // default for all methods
public class BookService {

    @Transactional                            // override for writes
    public BookDto createBook(CreateBookRequest request) {
        Author author = authorRepository.findById(request.getAuthorId())
                .orElseThrow(() -> new AuthorNotFoundException(request.getAuthorId()));

        Book book = bookMapper.toEntity(request);
        book.setAuthor(author);
        Book saved = bookRepository.save(book);    // EntityManager.persist()
        // No need to call save() again — any changes to 'saved' are detected automatically
        saved.setCreatedAt(LocalDateTime.now());   // ← dirty checking: this IS saved
        return bookMapper.toDto(saved);
        // Transaction commits here → flush → INSERT SQL executed
    }

    @Transactional
    public BookDto updatePrice(Long id, BigDecimal newPrice) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new BookNotFoundException(id));
        book.setPrice(newPrice);                   // ← dirty checking tracks this change
        // NO save() call needed — entity is managed, changes are flushed on commit
        return bookMapper.toDto(book);
        // Transaction commits → UPDATE books SET price = ? WHERE id = ? executed
    }
}
```

**`readOnly = true` optimization:**
- Tells Hibernate to skip dirty checking (no need to track changes)
- Allows DB driver/replica routing to read replicas
- Hibernate skips snapshot creation for all loaded entities — measurable performance improvement on large reads

---

### Slide 13 — @Transactional Patterns and LazyInitializationException
**Header:** Common @Transactional Patterns and the Lazy Loading Trap

**The `LazyInitializationException` — the most common JPA error:**
```java
// PROBLEM: loading entity outside transaction, then accessing lazy collection
public BookDto findById(Long id) {
    Book book = bookRepository.findById(id).orElseThrow(...);
    // Transaction from repository is DONE — EntityManager is closed
    book.getReviews().size();   // ← BOOM: LazyInitializationException!
    // 'reviews' is a @OneToMany LAZY collection. EntityManager is gone.
}

// SOLUTION 1: @Transactional on service method — keeps session open
@Transactional(readOnly = true)
public BookDto findById(Long id) {
    Book book = bookRepository.findById(id).orElseThrow(...);
    book.getReviews().size();   // ✅ EntityManager still open
    return bookMapper.toDto(book);
}

// SOLUTION 2: JOIN FETCH in repository — load it eagerly in one query
@Query("SELECT b FROM Book b LEFT JOIN FETCH b.reviews WHERE b.id = :id")
Optional<Book> findByIdWithReviews(@Param("id") Long id);

// SOLUTION 3: @EntityGraph — declarative eager loading
@EntityGraph(attributePaths = {"reviews", "author"})
Optional<Book> findById(Long id);
// Spring generates: SELECT b, r, a FROM books b LEFT JOIN reviews r ... LEFT JOIN authors a ...
```

**Class-level `@Transactional` pattern (recommended):**
```java
@Service
@Transactional(readOnly = true)     // all methods read-only by default
public class BookService {

    public List<BookDto> getAllBooks() { ... }     // inherits readOnly = true

    @Transactional                               // writes get their own @Transactional
    public BookDto createBook(CreateBookRequest req) { ... }

    @Transactional
    public void deleteBook(Long id) { ... }
}
```

---

### Slide 14 — Custom Repository Implementations
**Header:** Custom Repository Methods — Beyond What Spring Data Can Generate

**When naming conventions and `@Query` aren't enough:** Dynamic queries with runtime conditions (search with optional filters), bulk operations with complex logic, using `EntityManager` directly.

**Three-piece pattern:**
```java
// Step 1: Custom interface
public interface BookRepositoryCustom {
    List<Book> searchBooks(String title, String category, BigDecimal minPrice, BigDecimal maxPrice);
}

// Step 2: Implementation (MUST be named: interface name + "Impl")
@RequiredArgsConstructor
public class BookRepositoryCustomImpl implements BookRepositoryCustom {

    private final EntityManager em;

    @Override
    public List<Book> searchBooks(String title, String category,
                                  BigDecimal minPrice, BigDecimal maxPrice) {
        StringBuilder jpql = new StringBuilder("SELECT b FROM Book b WHERE 1=1");
        Map<String, Object> params = new HashMap<>();

        if (title != null && !title.isBlank()) {
            jpql.append(" AND b.title LIKE :title");
            params.put("title", "%" + title + "%");
        }
        if (category != null) {
            jpql.append(" AND b.category = :category");
            params.put("category", category);
        }
        if (minPrice != null) {
            jpql.append(" AND b.price >= :minPrice");
            params.put("minPrice", minPrice);
        }
        TypedQuery<Book> query = em.createQuery(jpql.toString(), Book.class);
        params.forEach(query::setParameter);
        return query.getResultList();
    }
}

// Step 3: Extend BOTH in the main repository interface
public interface BookRepository extends JpaRepository<Book, Long>, BookRepositoryCustom {
    // Spring Data merges the two implementations
}
```

For more readable dynamic queries, prefer **Spring Data `Specification`** (built on Criteria API — covered in Part 2).

---

### Slide 15 — Projections
**Header:** Projections — Returning Only the Data You Need

Instead of loading the full entity graph, projections let you select specific columns. Better performance, smaller payloads.

**Interface-based projection (closed projection):**
```java
// Define an interface — Spring Data generates a proxy that maps query results
public interface BookSummary {
    Long getId();
    String getTitle();
    BigDecimal getPrice();
}

// Use in repository — Spring automatically selects only the projected columns
public interface BookRepository extends JpaRepository<Book, Long> {
    List<BookSummary> findByCategory(String category);
}
// Generated SQL: SELECT id, title, price FROM books WHERE category = ?
// NOT: SELECT * FROM books ...
```

**DTO-based projection with constructor expression in @Query:**
```java
// DTO record
public record BookSummaryDto(Long id, String title, BigDecimal price) {}

// In repository
@Query("SELECT new com.example.dto.BookSummaryDto(b.id, b.title, b.price) FROM Book b WHERE b.active = true")
List<BookSummaryDto> findActiveSummaries();
```

**Open projection — computed from entity data:**
```java
public interface BookWithAuthorName {
    String getTitle();
    BigDecimal getPrice();

    @Value("#{target.author.firstName + ' ' + target.author.lastName}")
    String getAuthorFullName();   // computed — forces loading the author
}
```

**When to use projections vs DTOs + MapStruct:**
- Projections: query-specific read models; slight less code for simple cases
- MapStruct: full entity → DTO mapping; better for complex transformations; compile-time safety; reusable across the application

---

### Slide 16 — Part 1 Summary + DAO Pattern
**Header:** Part 1 Summary — Repository Layer Reference

**DAO Pattern vs Spring Data Repository:**
| | DAO Pattern | Spring Data Repository |
|---|---|---|
| Definition | Class implementing data access | Interface with Spring-generated implementation |
| Boilerplate | High — write every method | None for standard CRUD |
| Custom queries | In the DAO class | @Query or custom impl |
| Testing | Mock the DAO class | Mock the repository interface |
| Use in Spring Boot | Rare (legacy or raw JDBC) | Standard approach |

Spring Data Repository is a modern, opinionated implementation of the DAO pattern. When you see DAO in job postings or legacy code, it's the same concept — a layer that abstracts data access from the service layer.

**Quick reference:**
```
JpaRepository<T,ID>    extend this for all Spring Boot JPA repos
findById(id)           returns Optional<T>
findAll(pageable)      returns Page<T>
save(entity)           INSERT if new, UPDATE if existing
deleteById(id)         DELETE by PK
existsById(id)         boolean check

findByPropertyName(v)  → WHERE property_name = v
findByNameContaining   → LIKE '%v%'
findByPriceBetween     → BETWEEN min AND max
findByNameOrderByAsc   → ORDER BY name ASC

@Query("SELECT b FROM Book b WHERE ...")   custom JPQL
@Query(nativeQuery=true)                   native SQL
@Modifying                                 for UPDATE/DELETE queries

Pageable pageable = PageRequest.of(page, size, sort);
Page<T> result    = repo.findAll(pageable);
result.getContent()      → List<T>
result.getTotalPages()   → int
result.getTotalElements()→ long
```

**Part 2 preview:** Now we implement the entity classes that these repositories operate on — `@Entity`, `@Id`, `@Column`, and all four relationship types. We'll also cover the N+1 query problem and how to fix it.

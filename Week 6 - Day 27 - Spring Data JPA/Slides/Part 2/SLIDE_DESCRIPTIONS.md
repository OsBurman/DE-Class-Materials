# Day 27 Part 2 — Hibernate ORM: Entities, Relationships, Fetch Types & JPQL
## Slide Descriptions

---

### Slide 1 — Title Slide
**Title:** Hibernate ORM & JPA — The Entity Layer
**Subtitle:** Part 2: Entities, Relationships, Fetch Types, Cascade & Querying

**Learning objectives listed on slide:**
- Distinguish JPA (specification) from Hibernate (implementation)
- Map Java classes to database tables with `@Entity`, `@Table`, `@Column`
- Define primary key generation strategies with `@Id` and `@GeneratedValue`
- Model all four relationship types: `@OneToOne`, `@OneToMany`, `@ManyToOne`, `@ManyToMany`
- Understand the owning side and `mappedBy`
- Apply EAGER vs LAZY fetch strategies and avoid the N+1 problem
- Use Cascade types and `orphanRemoval`
- Write JPQL queries and understand the Criteria API
- Embed value objects with `@Embeddable` and `@Embedded`
- Manage schema evolution with Flyway

---

### Slide 2 — JPA vs Hibernate and ORM Fundamentals
**Header:** ORM — Bridging the Object-Relational Gap

**The Object-Relational Impedance Mismatch:**
- Java objects have inheritance, polymorphism, and object references
- Relational databases have tables, foreign keys, and rows
- Manually mapping between them is tedious and error-prone

**ORM (Object-Relational Mapping) handles this automatically.** You define the mapping once with annotations. The ORM generates SQL.

**JPA vs Hibernate:**
```
Your Code
    │
    ▼
JPA API  (jakarta.persistence.*)
    │  Standard specification — defines annotations and interfaces
    │  JPA is to Hibernate what JDBC is to MySQL connector
    ▼
Hibernate  (org.hibernate.*)
    │  JPA implementation — does the actual work
    │  Spring Boot default: Hibernate via spring-boot-starter-data-jpa
    ▼
JDBC / Database Driver
    │
    ▼
Database (PostgreSQL, MySQL, H2, ...)
```

**Why the distinction matters:**
- Annotations from `jakarta.persistence.*` (JPA) are portable — switch from Hibernate to EclipseLink without changing your entities
- Hibernate-specific features (`@NaturalId`, `@BatchSize`, `@SelectBeforeUpdate`) are powerful but tie you to Hibernate
- In practice, virtually all Spring Boot applications use Hibernate — but using standard JPA annotations is best practice

**Spring Boot auto-configures:** `EntityManagerFactory`, `JpaTransactionManager`, Hibernate as JPA provider, database schema generation (`spring.jpa.hibernate.ddl-auto`).

---

### Slide 3 — @Entity Basics and the Entity Lifecycle
**Header:** @Entity — Declaring a Persistent Class

**Minimum viable entity:**
```java
@Entity
@Table(name = "books")          // optional — defaults to class name in lowercase
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;        // defaults to column name "title"
    private BigDecimal price;

    // JPA REQUIRES a no-argument constructor (can be protected)
    protected Book() {}

    public Book(String title, BigDecimal price) {
        this.title = title;
        this.price = price;
    }

    // getters and setters
}
```

**JPA requirements for entity classes:**
- `@Entity` annotation
- No-arg constructor (public or protected)
- Not final, not abstract (CGLIB proxying requirement)
- Persistent fields accessed via getters (field access) or directly (field access via annotations on fields)

**`equals()` and `hashCode()` — the JPA gotcha:**
Hibernate uses proxies and lazy loading. If you generate `equals`/`hashCode` from all fields, you risk issues with uninitialized lazy proxies. Best practice:
```java
// Option 1: business key equality (e.g., ISBN)
@Override
public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Book)) return false;
    Book book = (Book) o;
    return isbn != null && isbn.equals(book.isbn);
}

// Option 2: ID-only equality (simplest, works after persist)
@Override
public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Book)) return false;
    Book book = (Book) o;
    return id != null && id.equals(book.id);
}
```

---

### Slide 4 — @Id and @GeneratedValue Strategies
**Header:** Primary Keys — How Hibernate Generates IDs

**@Id marks the primary key field:**
```java
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Long id;
```

**Four generation strategies:**
```java
// IDENTITY — database auto-increment (MySQL, PostgreSQL SERIAL)
// Most common for MySQL/PostgreSQL. Executes INSERT to get DB-generated ID.
@GeneratedValue(strategy = GenerationType.IDENTITY)

// SEQUENCE — database sequence (PostgreSQL default for JPA)
// Allows batch inserts — Hibernate can pre-allocate IDs from the sequence
@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "book_seq")
@SequenceGenerator(name = "book_seq", sequenceName = "book_id_seq", allocationSize = 50)

// AUTO — JPA picks based on database dialect
// Predictable for PostgreSQL (sequence), less predictable for MySQL
@GeneratedValue(strategy = GenerationType.AUTO)

// TABLE — JPA-managed table to simulate sequences (portable but slow)
// Rarely used — requires table lock for each insert
@GeneratedValue(strategy = GenerationType.TABLE)
```

**UUID primary keys:**
```java
@Id
@GeneratedValue(strategy = GenerationType.UUID)   // JPA 3.1 / Spring Boot 3+
private UUID id;

// Or generate manually:
@Id
private UUID id = UUID.randomUUID();   // set in constructor
```

**Natural ID with Hibernate (non-surrogate key lookup):**
```java
@NaturalId                                         // Hibernate-specific
@Column(unique = true, nullable = false)
private String isbn;
// Enables: session.byNaturalId(Book.class).using("isbn", "978...").load()
```

---

### Slide 5 — @Column, @Table, and Field Annotations
**Header:** Mapping Fields and Tables

**@Column attributes:**
```java
@Column(
    name = "book_title",          // column name (default = field name)
    nullable = false,             // NOT NULL constraint
    unique = false,               // UNIQUE constraint
    length = 200,                 // VARCHAR length (default 255 for String)
    columnDefinition = "TEXT",    // override with raw SQL type
    insertable = true,            // include in INSERT statements
    updatable = true              // include in UPDATE statements
)
private String title;

// @Column(updatable = false) for fields that should never change after insert:
@Column(updatable = false, nullable = false)
private LocalDateTime createdAt;
```

**@Table — customize the database table:**
```java
@Entity
@Table(
    name = "books",
    schema = "bookstore",         // database schema
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_books_isbn", columnNames = {"isbn"}),
        @UniqueConstraint(name = "uk_books_title_author", columnNames = {"title", "author_id"})
    },
    indexes = {
        @Index(name = "idx_books_category", columnList = "category"),
        @Index(name = "idx_books_price", columnList = "price DESC")
    }
)
public class Book { ... }
```

**Special field annotations:**
```java
@Transient                                    // not persisted to database at all
private String computedDisplayName;

@Enumerated(EnumType.STRING)                  // store enum as VARCHAR "FICTION", "SCIENCE"
private BookCategory category;                // EnumType.ORDINAL stores 0,1,2 — avoid it

@CreationTimestamp                            // Hibernate: set on INSERT, never update
private LocalDateTime createdAt;

@UpdateTimestamp                              // Hibernate: update on every UPDATE
private LocalDateTime updatedAt;

@Version                                      // Optimistic locking — Hibernate increments on UPDATE
private Long version;                         // Throws OptimisticLockException on concurrent update
```

---

### Slide 6 — @ManyToOne and @OneToMany
**Header:** The Most Common Relationship — Many Books, One Author

**The relationship:** Many books belong to one author. One author has many books.

**Unidirectional @ManyToOne (simplest — start here):**
```java
@Entity
public class Book {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @ManyToOne(fetch = FetchType.LAZY)       // default is EAGER — always override to LAZY
    @JoinColumn(name = "author_id")          // FK column in the 'books' table
    private Author author;
}

// No 'books' field on Author — unidirectional
@Entity
public class Author {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String firstName;
    private String lastName;
}
```

**Bidirectional @ManyToOne / @OneToMany:**
```java
@Entity
public class Book {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id")          // ← OWNING SIDE: has @JoinColumn
    private Author author;
}

@Entity
public class Author {
    @OneToMany(
        mappedBy = "author",                 // ← INVERSE SIDE: mappedBy = field name in Book
        fetch = FetchType.LAZY,             // default for @OneToMany — keep it
        cascade = CascadeType.ALL,           // author saves → cascade to books
        orphanRemoval = true                 // delete Book if removed from author.books
    )
    private List<Book> books = new ArrayList<>();  // initialize to avoid NPE

    // Helper methods to keep both sides in sync
    public void addBook(Book book) {
        books.add(book);
        book.setAuthor(this);                // sync the owning side
    }

    public void removeBook(Book book) {
        books.remove(book);
        book.setAuthor(null);                // sync the owning side
    }
}
```

**The owning side is the one with `@JoinColumn`.** The owning side controls the foreign key. `mappedBy` on the inverse side tells JPA: "the foreign key is managed by the `author` field on `Book`, not by this collection."

---

### Slide 7 — Bidirectional Relationships — The Owning Side Rule
**Header:** Owning Side vs Inverse Side — The Rule That Controls Everything

**The golden rule:** Only changes to the owning side are persisted to the database.

```java
// PROBLEM: only setting the inverse side — FK is NOT saved
Author author = authorRepository.findById(1L).orElseThrow();
Book book = new Book("Clean Code", new BigDecimal("39.99"));
author.getBooks().add(book);          // adds to inverse side list
bookRepository.save(book);
// Result: book.author_id = NULL in the database!
// The List<Book> on Author is the inverse side — changes to it don't set the FK.

// SOLUTION 1: set the owning side
book.setAuthor(author);               // sets the FK (owning side)
bookRepository.save(book);
// Result: book.author_id = 1 ✓

// SOLUTION 2: use the helper method that syncs both sides
author.addBook(book);
// helper: books.add(book) + book.setAuthor(this)
authorRepository.save(author);        // cascade = ALL saves the book too
```

**Preventing `StackOverflowError` with bidirectional relationships:**
```java
// PROBLEM: toString() on Author calls toString() on Book which calls toString() on Author...
@Entity
public class Author {
    private List<Book> books;
    // @Data from Lombok generates toString() that includes books
    // Each Book's toString() includes author → infinite recursion
}

// SOLUTIONS:
// 1. Exclude the collection from @ToString with Lombok:
@ToString.Exclude
private List<Book> books;

// 2. Write toString() manually without the collection field
// 3. Use @JsonManagedReference / @JsonBackReference for JSON serialization
```

---

### Slide 8 — @OneToOne
**Header:** @OneToOne — One Entity, One Related Entity

**Example:** A `User` has exactly one `UserProfile`. The profile's table has a FK back to users.

**Basic @OneToOne:**
```java
@Entity
public class User {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String email;

    @OneToOne(
        mappedBy = "user",         // inverse side — profile owns the FK
        cascade = CascadeType.ALL,
        fetch = FetchType.LAZY,    // @OneToOne defaults to EAGER — always override
        optional = false           // user always has a profile
    )
    private UserProfile profile;
}

@Entity
public class UserProfile {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", unique = true)   // FK column, UNIQUE ensures 1-1
    private User user;                             // owning side — has @JoinColumn

    private String bio;
    private String avatarUrl;
}
```

**Shared primary key strategy (the most efficient @OneToOne):**
```java
@Entity
public class UserProfile {
    @Id
    private Long id;              // same as User's ID — no separate column

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId                        // tells JPA: use the User's ID as this entity's PK
    @JoinColumn(name = "user_id")
    private User user;
}
```

With `@MapsId`: `UserProfile` has `user_id` as both its primary key and the foreign key. One column, no separate ID sequence.

---

### Slide 9 — @ManyToMany
**Header:** @ManyToMany — Books and Tags

**Example:** A book can have many tags. A tag can apply to many books. Requires a join table.

```java
@Entity
public class Book {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToMany
    @JoinTable(
        name = "book_tags",                                      // join table name
        joinColumns = @JoinColumn(name = "book_id"),             // FK to this entity
        inverseJoinColumns = @JoinColumn(name = "tag_id")        // FK to the other entity
    )
    private Set<Tag> tags = new HashSet<>();                     // Set avoids duplicates
}

@Entity
public class Tag {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;

    @ManyToMany(mappedBy = "tags")    // inverse side — mappedBy the field in Book
    private Set<Book> books = new HashSet<>();
}
```

**When @ManyToMany isn't enough — use an explicit join entity:**
```java
// If the join table needs extra columns (e.g., when a book was added to a list):
@Entity
@Table(name = "book_reading_lists")
public class BookReadingList {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "book_id")
    private Book book;

    @ManyToOne
    @JoinColumn(name = "reading_list_id")
    private ReadingList readingList;

    private LocalDate addedOn;          // extra column — impossible with pure @ManyToMany
    private String notes;
}
// Now ReadingList has: @OneToMany(mappedBy = "readingList") List<BookReadingList> items
```

---

### Slide 10 — Fetch Types — EAGER vs LAZY
**Header:** Fetch Types — Controlling When Data Is Loaded

**Default fetch types by relationship:**
| Annotation | Default Fetch |
|---|---|
| `@ManyToOne` | `EAGER` |
| `@OneToOne` | `EAGER` |
| `@OneToMany` | `LAZY` |
| `@ManyToMany` | `LAZY` |

**Always override `@ManyToOne` and `@OneToOne` to `LAZY`.** The EAGER defaults exist for legacy reasons. Loading every associated entity immediately is almost never what you want.

```java
// ❌ Default EAGER — every Book load triggers a SELECT for Author
@ManyToOne
@JoinColumn(name = "author_id")
private Author author;

// ✅ Explicit LAZY — Author only loaded when book.getAuthor() is called
@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "author_id")
private Author author;
```

**The N+1 Problem:**
```
// You load 100 books:
SELECT * FROM books                          -- 1 query

// For each book, when you access book.getAuthor() with LAZY loading:
SELECT * FROM authors WHERE id = 1           -- +1 query per book
SELECT * FROM authors WHERE id = 2
SELECT * FROM authors WHERE id = 3
...
SELECT * FROM authors WHERE id = 100         -- 100 MORE queries = N+1 problem
```

**Solutions to N+1:**

**Solution 1 — JOIN FETCH in `@Query`:**
```java
@Query("SELECT b FROM Book b JOIN FETCH b.author")
List<Book> findAllWithAuthor();
// One query: SELECT b.*, a.* FROM books b INNER JOIN authors a ON b.author_id = a.id
```

**Solution 2 — `@EntityGraph`:**
```java
@EntityGraph(attributePaths = {"author", "reviews"})   // fetch both associations
List<Book> findByCategory(String category);
// Spring generates LEFT JOIN FETCH for each listed attribute
```

---

### Slide 11 — Cascade Operations and orphanRemoval
**Header:** Cascade — Propagating Operations Through Relationships

**Cascade types** tell JPA: "when you perform this operation on the parent, perform it on the children too."

```java
@OneToMany(mappedBy = "author", cascade = CascadeType.ALL, orphanRemoval = true)
private List<Book> books = new ArrayList<>();
```

**All cascade types:**
| CascadeType | What It Does |
|---|---|
| `PERSIST` | When you `persist()` the parent, persist children too |
| `MERGE` | When you `merge()` the parent, merge children too |
| `REMOVE` | When you `remove()` the parent, remove children too |
| `REFRESH` | When you `refresh()` the parent, refresh children |
| `DETACH` | When you `detach()` the parent, detach children |
| `ALL` | All of the above |

**When to use each:**
```java
// Author owns Books — ALL makes sense
@OneToMany(mappedBy = "author", cascade = CascadeType.ALL)
private List<Book> books;

// Don't cascade REMOVE to authors when deleting books (many books → one author)
// Would delete the Author when the last book is deleted!
@ManyToOne
@JoinColumn(name = "author_id")   // NO cascade here — this is the child side
private Author author;
```

**`orphanRemoval = true`:**
```java
// WITH orphanRemoval: removing a book from the list deletes it from the database
author.getBooks().remove(book);   // schedules DELETE FROM books WHERE id = ?

// WITHOUT orphanRemoval: removing from the list just nulls the FK
author.getBooks().remove(book);   // UPDATE books SET author_id = NULL WHERE id = ?
```

**⚠️ Never use `CascadeType.REMOVE` or `CascadeType.ALL` on `@ManyToOne`. It would delete the parent entity when you delete a child.**

---

### Slide 12 — Entity Lifecycle States
**Header:** Entity States — The JPA Lifecycle

**Four states every entity can be in:**
```
new Book("Clean Code", 39.99)
          │
          │  entityManager.persist(book)
          │  OR  bookRepository.save(book)  [if id is null]
          ▼
      TRANSIENT ─────────────────────────────────────────────
      (new, unknown to EntityManager)                        │
                                                             │
          │  em.persist() / repo.save()                      │
          ▼                                                  │
       MANAGED ─────────────────────────────────────────────┤
      (tracked by EntityManager; dirty checking active)      │
          │  transaction commits → SQL flushed               │
          │  em.detach() / session closes                    │
          ▼                                                  │
      DETACHED ─────────────────────────────────────────────┤
      (was managed; changes NOT tracked)                     │
          │  em.merge() / repo.save()  [if id is present]   │
          └──────────────────────────────►  MANAGED again    │
                                                             │
          │  em.remove()                                     │
          ▼                                                  │
       REMOVED                                              │
      (scheduled for DELETE; SQL on commit)◄────────────────┘
```

**What this means in practice:**
```java
@Transactional
public void updateTitle(Long id, String newTitle) {
    Book book = bookRepository.findById(id).orElseThrow();
    // book is now MANAGED (inside @Transactional)

    book.setTitle(newTitle);
    // Dirty checking detects the change — NO save() needed

    // Transaction commits → Hibernate flushes → UPDATE SQL runs
}   // EntityManager closes → book becomes DETACHED

// After return: book is DETACHED
// Any changes to 'book' here are NOT tracked
book.setPrice(new BigDecimal("29.99"));   // silently ignored!
```

---

### Slide 13 — JPQL Syntax
**Header:** JPQL — Object-Oriented SQL

JPQL is the JPA Query Language. It looks like SQL but operates on entity classes and fields, not tables and columns.

```java
// SELECT
// Entity name (not table name) and field names (not column names)
"SELECT b FROM Book b"
"SELECT b FROM Book b WHERE b.active = true"
"SELECT b FROM Book b WHERE b.price BETWEEN :min AND :max"

// Accessing related entity fields via dot notation:
"SELECT b FROM Book b WHERE b.author.lastName = :lastName"
// Hibernate generates: SELECT b.* FROM books b INNER JOIN authors a ON b.author_id = a.id WHERE a.last_name = ?

// JOIN types:
"SELECT b FROM Book b JOIN b.author a WHERE a.country = 'UK'"        // INNER JOIN
"SELECT b FROM Book b LEFT JOIN b.reviews r WHERE r IS NULL"         // LEFT JOIN (books with no reviews)
"SELECT b FROM Book b JOIN FETCH b.author"                            // EAGER JOIN for N+1 fix
"SELECT b FROM Book b LEFT JOIN FETCH b.reviews"                      // LEFT JOIN FETCH (optional)

// Aggregate functions:
"SELECT COUNT(b) FROM Book b WHERE b.category = :cat"
"SELECT AVG(b.price) FROM Book b"
"SELECT MAX(b.price), MIN(b.price) FROM Book b WHERE b.category = :cat"

// GROUP BY and HAVING:
"SELECT b.category, COUNT(b) FROM Book b GROUP BY b.category HAVING COUNT(b) > 10"

// ORDER BY:
"SELECT b FROM Book b ORDER BY b.price ASC, b.title DESC"

// Constructor expression (map to DTO without full entity):
"SELECT new com.example.dto.BookSummaryDto(b.id, b.title, b.price) FROM Book b"

// UPDATE and DELETE (use @Modifying):
"UPDATE Book b SET b.price = b.price * 1.1 WHERE b.category = :cat"
"DELETE FROM Book b WHERE b.active = false"
```

---

### Slide 14 — HQL vs JPQL and Named Queries
**Header:** HQL, Named Queries, and Query Best Practices

**HQL vs JPQL:**
| | JPQL | HQL |
|---|---|---|
| Standard | JPA specification | Hibernate-specific |
| Syntax | Mostly identical to HQL | Superset of JPQL |
| Portability | Works with any JPA provider | Hibernate only |
| Extra features | None | `TREAT`, natural IDs, full-text, etc. |
| When to use | Always prefer | When you need Hibernate-specific SQL |

In practice, JPQL and HQL are nearly identical for everyday queries. You'll use JPQL via `@Query` in Spring Data repositories.

**Named Queries — defining queries on the entity class:**
```java
@Entity
@NamedQuery(
    name = "Book.findByCategory",
    query = "SELECT b FROM Book b WHERE b.category = :category ORDER BY b.title"
)
@NamedQuery(
    name = "Book.findActiveWithAuthor",
    query = "SELECT b FROM Book b JOIN FETCH b.author WHERE b.active = true"
)
public class Book { ... }

// Using named queries in repositories:
public interface BookRepository extends JpaRepository<Book, Long> {
    @Query(name = "Book.findByCategory")
    List<Book> findByCategory(@Param("category") String category);
}
```

**Why `@Query` in the repository is preferred over `@NamedQuery` on the entity:**
- Keeps query close to usage (in the repository)
- Entities stay clean (only persistence mapping, no query logic)
- Easier to find and maintain
- `@NamedQuery` is valid JPA and exists in legacy codebases — know it when you see it

---

### Slide 15 — Criteria API Basics
**Header:** Criteria API — Programmatic Type-Safe Queries

The Criteria API is JPQL in Java code. It's more verbose but completely type-safe and ideal for dynamic queries.

**Basic Criteria query:**
```java
@Repository
@RequiredArgsConstructor
public class BookSearchRepository {

    private final EntityManager em;

    public List<Book> searchBooks(String title, String category, BigDecimal maxPrice) {
        CriteriaBuilder cb = em.getCriteriaBuilder();       // factory for predicates
        CriteriaQuery<Book> cq = cb.createQuery(Book.class); // type-safe query for Book
        Root<Book> book = cq.from(Book.class);              // FROM Book b

        List<Predicate> predicates = new ArrayList<>();      // dynamic WHERE clauses

        if (title != null && !title.isBlank()) {
            predicates.add(cb.like(cb.lower(book.get("title")), "%" + title.toLowerCase() + "%"));
        }
        if (category != null) {
            predicates.add(cb.equal(book.get("category"), category));
        }
        if (maxPrice != null) {
            predicates.add(cb.lessThanOrEqualTo(book.get("price"), maxPrice));
        }

        cq.where(cb.and(predicates.toArray(new Predicate[0])));
        cq.orderBy(cb.asc(book.get("title")));

        return em.createQuery(cq).getResultList();
    }
}
```

**Spring Data `Specification` — a cleaner wrapper over Criteria API:**
```java
// Define composable specs
public class BookSpecifications {
    public static Specification<Book> hasCategory(String category) {
        return (root, query, cb) -> cb.equal(root.get("category"), category);
    }
    public static Specification<Book> priceLessThan(BigDecimal max) {
        return (root, query, cb) -> cb.lessThanOrEqualTo(root.get("price"), max);
    }
    public static Specification<Book> titleContains(String keyword) {
        return (root, query, cb) ->
            cb.like(cb.lower(root.get("title")), "%" + keyword.toLowerCase() + "%");
    }
}

// Repository: extend JpaSpecificationExecutor
public interface BookRepository extends JpaRepository<Book, Long>, JpaSpecificationExecutor<Book> {}

// Service: compose and execute
Specification<Book> spec = Specification.where(BookSpecifications.hasCategory("Fiction"))
        .and(BookSpecifications.priceLessThan(new BigDecimal("30")))
        .and(BookSpecifications.titleContains("sea"));
Page<Book> results = bookRepository.findAll(spec, pageable);
```

---

### Slide 16 — @Embeddable and @Embedded — Value Objects
**Header:** @Embeddable — Embed a Value Object in the Parent Table

An `@Embeddable` class has no table of its own. Its fields become columns in the owning entity's table. Use this for **value objects** — types that have no independent identity and whose lifecycle belongs entirely to the parent entity.

**Classic example: Address embedded in Customer:**
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

    protected Address() {}    // JPA requires no-arg constructor

    public Address(String street, String city, String postalCode, String country) {
        this.street = street;  this.city = city;
        this.postalCode = postalCode;  this.country = country;
    }
    // getters...
}

@Entity
@Table(name = "customers")
public class Customer {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;

    @Embedded   // adds street, city, postal_code, country columns to customers table
    private Address shippingAddress;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "street",     column = @Column(name = "billing_street")),
        @AttributeOverride(name = "city",       column = @Column(name = "billing_city")),
        @AttributeOverride(name = "postalCode", column = @Column(name = "billing_postal_code")),
        @AttributeOverride(name = "country",    column = @Column(name = "billing_country"))
    })
    private Address billingAddress;   // same @Embeddable type, different column names
}
```

**Resulting table — no join, no FK:**
```
customers
├── id                      BIGINT PK
├── name                    VARCHAR
├── street                  VARCHAR(200)   ← shippingAddress.street
├── city                    VARCHAR(100)
├── postal_code             VARCHAR(20)
├── country                 VARCHAR(50)
├── billing_street          VARCHAR(200)   ← billingAddress.street
├── billing_city            VARCHAR(100)
├── billing_postal_code     VARCHAR(20)
└── billing_country         VARCHAR(50)
```

**Rules:**
- `@Embeddable` class must have a no-arg constructor
- `@Embedded` annotation is optional when the field type is annotated `@Embeddable` — JPA finds it automatically
- When embedding the same type more than once, `@AttributeOverrides` renames the column set for the second embedding
- If all embedded columns are `NULL`, Hibernate sets the embedded field to `null`
- Common use cases: `Address`, `Money`, `DateRange`, `GeoCoordinates`, `PhoneNumber`

---

### Slide 17 — Flyway — Schema Migration Management
**Header:** Flyway — Versioned Database Migrations

`spring.jpa.hibernate.ddl-auto=create-drop` is convenient in development — Hibernate rebuilds the schema on every restart. In production, **you never let Hibernate touch the schema.** Production databases have data. Dropping and recreating them destroys it.

**Flyway takes over schema management in production.**

**How Flyway works:**
```
src/main/resources/db/migration/
├── V1__create_books_table.sql          ← runs once, never again
├── V2__create_authors_table.sql
├── V3__add_category_to_books.sql       ← only runs if not yet applied
└── V4__add_category_index.sql
```
- Naming convention: `V{version}__{description}.sql` (two underscores)
- Flyway records each applied script in `flyway_schema_history` table
- On restart: checks history → runs only new scripts → skips already-applied ones
- Modifying an already-applied script throws an error (checksum protection)

**Spring Boot setup:**
```xml
<dependency>
    <groupId>org.flywaydb</groupId>
    <artifactId>flyway-core</artifactId>
</dependency>
```
```properties
# application.properties
spring.jpa.hibernate.ddl-auto=validate   # Hibernate checks; Flyway manages
```

**Example migration file:**
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
```

**Flyway vs Liquibase:**
| | Flyway | Liquibase |
|---|---|---|
| Format | SQL (or Java callbacks) | SQL, XML, YAML, JSON |
| Learning curve | Very low | Moderate |
| Rollback | Manual (write undo script) | Built-in rollback |
| Adoption | Very common in Spring Boot | Very common, more features |
| Recommendation | Start here | When rollback support is required |

Both are fully supported by Spring Boot auto-configuration.

---

### Slide 18 — Full Day Summary
**Header:** Day 27 Complete — Spring Data JPA Reference

**Entity mapping quick reference:**
```
@Entity                         Marks class as JPA entity
@Table(name = "books")          Customize table name, add indexes/constraints
@Id                             Primary key field
@GeneratedValue(IDENTITY)       DB auto-increment (MySQL / PostgreSQL common)
@GeneratedValue(SEQUENCE)       DB sequence (better for bulk inserts)
@Column(name, nullable, length) Customize column mapping
@Transient                      Not persisted
@Enumerated(EnumType.STRING)    Store enum as VARCHAR
@Embeddable / @Embedded         Value object embedded in parent's table (no separate table)
@AttributeOverrides             Rename embedded columns when embedding same type twice
@CreationTimestamp              Hibernate: set on INSERT
@UpdateTimestamp                Hibernate: update on every UPDATE
@Version                        Optimistic locking version field
```

**Relationship quick reference:**
```
@ManyToOne(fetch = LAZY)        Many books → one author (FK in books table)
@JoinColumn(name = "author_id") Specifies the FK column (OWNING side)

@OneToMany(mappedBy = "author") Author has list of books (INVERSE side)
                                 mappedBy = field name on the OWNING entity

@OneToOne(fetch = LAZY)         + @JoinColumn or @MapsId

@ManyToMany                     + @JoinTable defines the join table
```

**Query reference:**
```
@Query("SELECT b FROM Book b WHERE b.title LIKE %:kw%")   JPQL
@Query(nativeQuery = true, value = "SELECT * FROM books WHERE ...") Native SQL
@Modifying   required for UPDATE / DELETE @Query methods
JOIN FETCH   load association in one query (N+1 fix)
@EntityGraph(attributePaths = {"author"})   declarative eager load per method
```

**Looking ahead — Day 28:** Testing. `@DataJpaTest` sets up a real (H2) database and your full repository layer — you'll test every query you wrote today. `@WebMvcTest` + `MockMvc` tests your controllers. Mockito mocks your service layer. `@SpringBootTest` for full integration tests. Testcontainers for real databases in tests.

package com.bookstore.entity;

// =============================================================================
// ENTITY MAPPINGS — Full demonstration of JPA / Hibernate annotations
// =============================================================================
// This file contains several entity classes that together model a bookstore:
//   Book         — main entity
//   Author       — OneToOne and ManyToMany relationships
//   Category     — ManyToOne from Book
//   Review       — OneToMany from Book
//   BookAuthor   — join table entity for ManyToMany with extra columns
// =============================================================================

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


// =============================================================================
// SECTION 1: ENTITY CLASS AND @ENTITY, @TABLE
// =============================================================================

// @Entity — marks this class as a JPA entity.
// Hibernate will map it to a database table.
// REQUIREMENT: must have a no-args constructor (Hibernate uses it to create instances via reflection)
@Entity

// @Table — maps this entity to a specific table name.
// Without @Table, JPA uses the class name as the table name ("Book" → "book" or "Book").
// uniqueConstraints lets you enforce composite unique constraints (more than one column together).
@Table(
    name = "books",   // explicit table name in the database
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_books_isbn", columnNames = {"isbn"}),
        @UniqueConstraint(name = "uk_books_title_author", columnNames = {"title", "author_name"})
    },
    indexes = {
        @Index(name = "idx_books_genre", columnList = "genre"),
        @Index(name = "idx_books_price", columnList = "price")
    }
)
// Lombok: generates getters, setters, toString (excludes lazy collections), equals/hashCode
@Getter
@Setter
@NoArgsConstructor              // required by JPA spec — Hibernate needs it for reflection
@AllArgsConstructor
@ToString(exclude = {"reviews", "authors"})  // exclude collections to prevent infinite loop + lazy load
public class Book {

    // =========================================================================
    // SECTION 2: PRIMARY KEYS — @Id and @GeneratedValue
    // =========================================================================

    // @Id — designates this field as the primary key
    @Id

    // @GeneratedValue — tells JPA how to generate the PK value automatically
    // Strategy options:
    //   IDENTITY  — delegates to DB auto-increment (MySQL, PostgreSQL SERIAL)
    //               → INSERT first, then DB generates ID → Hibernate reads it back
    //   SEQUENCE  — uses a DB sequence object (best for PostgreSQL)
    //               → Hibernate pre-fetches a range of IDs for batching
    //   TABLE     — stores current ID in a special generator table (slow — avoid)
    //   AUTO      — Hibernate picks based on the database dialect (usually SEQUENCE or IDENTITY)
    //   UUID      — generates a UUID (useful for distributed systems)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Sequence-based generation (preferred for PostgreSQL — allows batch inserts)
    // @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "book_seq")
    // @SequenceGenerator(name = "book_seq", sequenceName = "books_id_seq", allocationSize = 50)
    // private Long id;


    // =========================================================================
    // SECTION 3: COLUMN MAPPINGS — @Column
    // =========================================================================

    // @Column — customizes column name, constraints, and SQL type.
    // Without @Column, JPA uses the field name as the column name (camelCase → snake_case by default).

    @Column(
        name = "title",         // explicit column name (redundant here, shown for clarity)
        nullable = false,       // → NOT NULL constraint in DDL
        length = 255            // VARCHAR(255) — default is 255 if omitted
    )
    private String title;

    @Column(name = "author_name", nullable = false, length = 150)
    private String author;

    @Column(
        name = "isbn",
        nullable = false,
        unique = true,          // unique constraint (single column — could also use @Table uniqueConstraints)
        length = 17             // ISBN-13 format: 978-0-306-40615-7 (17 chars with dashes)
    )
    private String isbn;

    @Column(
        name = "price",
        nullable = false,
        precision = 10,         // total number of digits (e.g., 10 → up to 99,999,999.99)
        scale = 2               // digits after decimal point (e.g., 2 → 19.99)
    )
    private BigDecimal price;

    @Column(name = "stock_quantity")
    private Integer stockQuantity;

    @Column(name = "genre", length = 50)
    private String genre;

    @Column(name = "description", columnDefinition = "TEXT")  // TEXT type for long strings
    private String description;

    @Column(name = "published_date")
    private LocalDate publishedDate;

    @Column(name = "available", nullable = false)
    private boolean available = true;

    // @Column with insertable=false, updatable=false — read-only field
    // (managed elsewhere, e.g., set by DB trigger)
    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    // Hibernate-specific: auto-populate on INSERT
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAtAuto;

    // Hibernate-specific: auto-update on every UPDATE
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // @Lob — for large binary objects or very large strings (mapped to BLOB/CLOB)
    @Lob
    @Column(name = "cover_image")
    private byte[] coverImage;

    // @Transient — field is NOT persisted to the database (exists in Java only)
    @Transient
    private String computedDisplayTitle;  // calculated at runtime, not stored

    // @Enumerated — map a Java enum to the database
    // EnumType.STRING  → stores the enum name ("PAPERBACK") — recommended (readable, rename-safe)
    // EnumType.ORDINAL → stores the enum position (0, 1, 2) — fragile, avoid
    @Enumerated(EnumType.STRING)
    @Column(name = "book_format", length = 20)
    private BookFormat format;

    public enum BookFormat {
        HARDCOVER, PAPERBACK, EBOOK, AUDIOBOOK
    }


    // =========================================================================
    // SECTION 4: RELATIONSHIPS
    // =========================================================================

    // ---- @ManyToOne ----
    // Many books belong to one category.
    // This is the "owning side" — the foreign key (category_id) lives in the books table.
    //
    // FetchType.LAZY (default for @ManyToOne when explicit, but JPA spec says EAGER) —
    // we override to LAZY here as a best practice.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "category_id",           // FK column name in books table
        foreignKey = @ForeignKey(name = "fk_books_category")  // named FK constraint
    )
    private Category category;


    // ---- @OneToMany ----
    // One book has many reviews.
    // mappedBy = "book" means: "the Review entity owns this relationship;
    //            the FK column is in the reviews table, not here."
    // Without mappedBy you'd get a join table created automatically — usually wrong.
    //
    // cascade = CascadeType.ALL — any operation on Book cascades to Reviews:
    //   PERSIST, MERGE, REMOVE, REFRESH, DETACH
    // orphanRemoval = true — if a Review is removed from this list, DELETE it from DB
    @OneToMany(
        mappedBy = "book",
        cascade = CascadeType.ALL,
        orphanRemoval = true,
        fetch = FetchType.LAZY          // LAZY is the DEFAULT for collections — but be explicit
    )
    private List<Review> reviews = new ArrayList<>();  // always initialize to empty — never null


    // ---- @ManyToMany ----
    // Many books can have many authors; many authors can have many books.
    // @JoinTable specifies the join table that holds both FKs.
    //
    // This is the OWNING side (has @JoinTable).
    // The inverse side (Author.books) has mappedBy = "authors".
    @ManyToMany(
        fetch = FetchType.LAZY,
        cascade = {CascadeType.PERSIST, CascadeType.MERGE}  // don't cascade REMOVE — deleting a book
        // should NOT delete the author
    )
    @JoinTable(
        name = "book_authors",              // join table name
        joinColumns = @JoinColumn(name = "book_id"),       // FK to this entity (Book)
        inverseJoinColumns = @JoinColumn(name = "author_id") // FK to the other entity (Author)
    )
    private Set<Author> authors = new HashSet<>();   // Set preferred for ManyToMany (no duplicates)


    // ---- @OneToOne ----
    // One book has one bookDetail (extended metadata).
    // @MapsId + @OneToOne creates a shared PK — bookDetail uses the same PK as book.
    @OneToOne(
        mappedBy = "book",
        cascade = CascadeType.ALL,
        fetch = FetchType.LAZY,
        optional = true             // bookDetail is optional — book can exist without it
    )
    private BookDetail bookDetail;


    // =========================================================================
    // SECTION 5: HELPER METHODS FOR BIDIRECTIONAL RELATIONSHIPS
    // =========================================================================
    // When managing bidirectional relationships, always use helper methods
    // to keep BOTH sides of the relationship in sync.
    // Forgetting to set both sides is a very common bug.

    public void addReview(Review review) {
        reviews.add(review);
        review.setBook(this);  // set the owning side — this is what writes the FK to DB
    }

    public void removeReview(Review review) {
        reviews.remove(review);
        review.setBook(null);
    }

    public void addAuthor(Author author) {
        authors.add(author);
        author.getBooks().add(this);  // keep the other side in sync
    }
}


// =============================================================================
// SECTION 4a: @OneToOne — BookDetail entity
// =============================================================================

@Entity
@Table(name = "book_details")
@Getter
@Setter
@NoArgsConstructor
class BookDetail {

    // Shared primary key with Book — same ID value in both tables
    @Id
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId   // tells JPA: use the Book's PK as this entity's PK
    @JoinColumn(name = "book_id")
    private Book book;

    @Column(name = "table_of_contents", columnDefinition = "TEXT")
    private String tableOfContents;

    @Column(name = "sample_chapter", columnDefinition = "TEXT")
    private String sampleChapter;

    @Column(name = "awards")
    private String awards;
}


// =============================================================================
// SECTION 4b: Category entity — for @ManyToOne from Book
// =============================================================================

@Entity
@Table(name = "categories")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, unique = true, length = 100)
    private String name;

    @Column(name = "description")
    private String description;

    // Inverse side of the @ManyToOne relationship (mappedBy = "category")
    // mappedBy means: "the FK is on the Book side — don't create another FK or join table"
    @OneToMany(mappedBy = "category", fetch = FetchType.LAZY)
    private List<Book> books = new ArrayList<>();
}


// =============================================================================
// SECTION 4c: Author entity — for @ManyToMany with Book
// =============================================================================

@Entity
@Table(name = "authors")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
class Author {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, length = 150)
    private String name;

    @Column(name = "bio", columnDefinition = "TEXT")
    private String bio;

    @Column(name = "email", unique = true)
    private String email;

    // Inverse side of ManyToMany — mappedBy points to the field name in Book
    @ManyToMany(mappedBy = "authors", fetch = FetchType.LAZY)
    private Set<Book> books = new HashSet<>();
}


// =============================================================================
// SECTION 4d: Review entity — for @OneToMany from Book
// =============================================================================

@Entity
@Table(name = "reviews")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Owning side of OneToMany — the FK is here (book_id column in reviews table)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @Column(name = "reviewer_name", nullable = false, length = 100)
    private String reviewerName;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "rating", nullable = false)
    private int rating;   // 1–5

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}


// =============================================================================
// SECTION 5: FETCH TYPES — EAGER vs LAZY
// =============================================================================
/*
LAZY  (default for @OneToMany, @ManyToMany):
  - The related collection/entity is NOT loaded when the parent is loaded
  - A proxy is placed in the field; SQL fires only when you ACCESS the field
  - Pro: avoids loading unnecessary data (performance)
  - Con: "LazyInitializationException" if you access the collection OUTSIDE a transaction
  - Use LAZY almost always

EAGER (default for @ManyToOne, @OneToOne):
  - The related entity IS loaded immediately with the parent (JOIN or separate SELECT)
  - Pro: always available, no lazy-init exceptions
  - Con: N+1 problem — loading 100 books = 100 extra queries for authors if EAGER

N+1 PROBLEM EXAMPLE (EAGER on @ManyToOne):
  Book has ManyToOne to Category with EAGER fetch.
  bookRepository.findAll() — loads 100 books.
  For each book, Hibernate fires a separate SELECT to load its Category.
  Result: 1 query for books + 100 queries for categories = 101 queries. VERY BAD.

SOLUTION — JPQL JOIN FETCH:
  @Query("SELECT b FROM Book b JOIN FETCH b.category WHERE b.genre = :genre")
  Forces Hibernate to load books AND categories in a single JOIN query.

@EntityGraph alternative (avoids modifying every query):
  @EntityGraph(attributePaths = {"category", "authors"})
  Page<Book> findByGenre(String genre, Pageable pageable);
  → Hibernate adds LEFT JOIN FETCH for category and authors automatically
*/


// =============================================================================
// SECTION 6: CASCADE TYPES
// =============================================================================
/*
Cascade means: "when I do X to this entity, also do X to its related entities"

CascadeType.PERSIST  — when you save parent, also save new children
CascadeType.MERGE    — when you merge parent, also merge children
CascadeType.REMOVE   — when you delete parent, also delete children  ⚠️ dangerous on ManyToMany
CascadeType.REFRESH  — when you refresh parent, also refresh children
CascadeType.DETACH   — when you detach parent, also detach children
CascadeType.ALL      — all of the above

RULES OF THUMB:
  @OneToMany (parent owns children) → CascadeType.ALL + orphanRemoval=true is usually right
  @ManyToOne                        → no cascade (child doesn't own parent)
  @ManyToMany                       → CascadeType.PERSIST + MERGE only
                                      NEVER CascadeType.REMOVE on ManyToMany —
                                      deleting one book would delete the shared author entity!

orphanRemoval = true:
  If a Review is removed from book.getReviews() list → DELETE that review from DB
  Even without an explicit deleteById call — just removing from the collection triggers DELETE
  Only works on @OneToMany and @OneToOne (entity "belongs to" the parent)
*/

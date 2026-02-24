package com.bookstore.query;

// =============================================================================
// HQL, JPQL, and Criteria API — Query Approaches in JPA / Hibernate
// =============================================================================
// This file demonstrates all three programmatic query approaches:
//   1. HQL (Hibernate Query Language) — Hibernate's own query language
//   2. JPQL (Jakarta Persistence Query Language) — the JPA-standard version
//   3. Criteria API — type-safe, programmatic query building (no strings)
//
// HQL vs JPQL:
//   They are nearly identical. JPQL is the standardized subset defined by the JPA spec.
//   HQL is Hibernate's superset — everything JPQL supports, plus Hibernate extensions.
//   In practice, you write queries that work as both. When people say "JPQL" in Spring Boot,
//   they usually mean "the query string syntax" — backed by Hibernate.
// =============================================================================

import com.bookstore.entity.Book;
import com.bookstore.entity.Author;
import com.bookstore.entity.Category;
import jakarta.persistence.*;
import jakarta.persistence.criteria.*;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;


@Repository
public class BookQueryExamples {

    // EntityManager is the JPA way to run JPQL and Criteria queries directly.
    // In Spring Data repositories you never inject this yourself — Spring Data does it.
    // But when writing custom queries manually (or in tests), you use it directly.
    @PersistenceContext
    private EntityManager entityManager;


    // =========================================================================
    // SECTION 1: JPQL FUNDAMENTALS
    // =========================================================================
    // JPQL syntax recap:
    //   SELECT  <select expression>
    //   FROM    <entity name> [AS] <alias>
    //   [WHERE  <condition>]
    //   [ORDER BY <field> ASC|DESC]
    //   [GROUP BY <field>]
    //   [HAVING <condition>]
    //
    // Key rule: use ENTITY CLASS NAMES and JAVA FIELD NAMES — NOT table/column names.
    //   ✅ "SELECT b FROM Book b"     (Book = class, b.title = field)
    //   ❌ "SELECT b FROM books b"    (books = table — that's SQL, not JPQL)
    // =========================================================================

    // --- Basic SELECT all ---
    public List<Book> findAllBooks() {
        // TypedQuery<Book> is preferred over plain Query — gives type safety at compile time
        TypedQuery<Book> query = entityManager.createQuery(
            "SELECT b FROM Book b", Book.class
        );
        return query.getResultList();
    }

    // --- WHERE clause ---
    public List<Book> findByGenre(String genre) {
        return entityManager.createQuery(
            "SELECT b FROM Book b WHERE b.genre = :genre ORDER BY b.title ASC",
            Book.class
        )
        .setParameter("genre", genre)   // named parameter binding
        .getResultList();
    }

    // --- LIKE (pattern matching) ---
    public List<Book> searchByTitle(String keyword) {
        return entityManager.createQuery(
            "SELECT b FROM Book b WHERE LOWER(b.title) LIKE LOWER(:pattern)",
            Book.class
        )
        .setParameter("pattern", "%" + keyword + "%")
        .getResultList();
    }

    // --- ORDER BY multiple fields ---
    public List<Book> findAllOrderedByGenreAndPrice() {
        return entityManager.createQuery(
            "SELECT b FROM Book b ORDER BY b.genre ASC, b.price DESC",
            Book.class
        ).getResultList();
    }

    // --- BETWEEN ---
    public List<Book> findByPriceRange(BigDecimal min, BigDecimal max) {
        return entityManager.createQuery(
            "SELECT b FROM Book b WHERE b.price BETWEEN :min AND :max",
            Book.class
        )
        .setParameter("min", min)
        .setParameter("max", max)
        .getResultList();
    }

    // --- IN clause ---
    public List<Book> findByMultipleGenres(List<String> genres) {
        return entityManager.createQuery(
            "SELECT b FROM Book b WHERE b.genre IN :genres",
            Book.class
        )
        .setParameter("genres", genres)
        .getResultList();
    }

    // --- IS NULL check ---
    public List<Book> findBooksWithNoStock() {
        return entityManager.createQuery(
            "SELECT b FROM Book b WHERE b.stockQuantity IS NULL OR b.stockQuantity = 0",
            Book.class
        ).getResultList();
    }


    // =========================================================================
    // SECTION 2: JPQL — AGGREGATE FUNCTIONS
    // =========================================================================

    // COUNT
    public long countBooks() {
        return entityManager.createQuery(
            "SELECT COUNT(b) FROM Book b", Long.class
        ).getSingleResult();
    }

    // AVG — returns Double
    public Double averagePrice() {
        return entityManager.createQuery(
            "SELECT AVG(b.price) FROM Book b", Double.class
        ).getSingleResult();
    }

    // MAX / MIN
    public BigDecimal mostExpensivePrice() {
        return entityManager.createQuery(
            "SELECT MAX(b.price) FROM Book b", BigDecimal.class
        ).getSingleResult();
    }

    // GROUP BY — count books per genre
    // Returns List<Object[]> — each Object[] is [genre (String), count (Long)]
    public List<Object[]> countBooksPerGenre() {
        return entityManager.createQuery(
            "SELECT b.genre, COUNT(b) FROM Book b GROUP BY b.genre ORDER BY COUNT(b) DESC",
            Object[].class
        ).getResultList();
    }

    // HAVING — filter groups (genres with more than 5 books)
    public List<Object[]> popularGenres(long minCount) {
        return entityManager.createQuery(
            "SELECT b.genre, COUNT(b) FROM Book b GROUP BY b.genre HAVING COUNT(b) > :minCount",
            Object[].class
        )
        .setParameter("minCount", minCount)
        .getResultList();
    }


    // =========================================================================
    // SECTION 3: JPQL — JOINS
    // =========================================================================

    // INNER JOIN — only books that HAVE a category
    public List<Book> findBooksWithCategory() {
        return entityManager.createQuery(
            "SELECT b FROM Book b JOIN b.category c WHERE c.name = :catName",
            Book.class
        )
        .setParameter("catName", "Fiction")
        .getResultList();
    }

    // JOIN FETCH — solves the N+1 problem by loading both sides in ONE query
    // Without JOIN FETCH: loading 100 books → 100 additional queries for category (N+1)
    // With JOIN FETCH: single SQL with JOIN loads books + categories together
    public List<Book> findBooksWithCategoryFetched() {
        return entityManager.createQuery(
            "SELECT b FROM Book b JOIN FETCH b.category",   // FETCH = load category eagerly
            Book.class
        ).getResultList();
    }

    // LEFT JOIN FETCH — include books that have NO category (LEFT = include nulls)
    public List<Book> findAllBooksWithOptionalCategory() {
        return entityManager.createQuery(
            "SELECT DISTINCT b FROM Book b LEFT JOIN FETCH b.authors",
            // DISTINCT prevents duplicate Book rows when a book has multiple authors
            Book.class
        ).getResultList();
    }

    // Multi-level join — books → authors → author name contains keyword
    public List<Book> findBooksByAuthorName(String authorName) {
        return entityManager.createQuery(
            "SELECT b FROM Book b JOIN b.authors a WHERE LOWER(a.name) LIKE LOWER(:name)",
            Book.class
        )
        .setParameter("name", "%" + authorName + "%")
        .getResultList();
    }


    // =========================================================================
    // SECTION 4: JPQL — CONSTRUCTOR EXPRESSIONS (Projections)
    // =========================================================================
    // Instead of returning whole entity objects (which loads all columns),
    // use a constructor expression to return only the fields you need.
    // The DTO class must have a matching constructor.

    // BookSummaryDTO(String title, String author, BigDecimal price) constructor needed
    public List<Object[]> findBookSummaries() {
        return entityManager.createQuery(
            "SELECT new com.bookstore.dto.BookSummaryDTO(b.title, b.author, b.price) " +
            "FROM Book b WHERE b.available = true ORDER BY b.price ASC",
            Object[].class
        ).getResultList();
    }


    // =========================================================================
    // SECTION 5: HQL — Hibernate-Specific Extensions
    // =========================================================================
    // HQL is Hibernate's superset of JPQL. Most queries are identical.
    // Hibernate adds a few extensions that JPA doesn't define:

    // HQL LIMIT / OFFSET via API (setMaxResults / setFirstResult)
    public List<Book> findBooksWithPagination(int page, int pageSize) {
        return entityManager.createQuery("SELECT b FROM Book b ORDER BY b.title", Book.class)
            .setFirstResult(page * pageSize)  // OFFSET: skip first N results
            .setMaxResults(pageSize)          // LIMIT: return at most N results
            .getResultList();
    }

    // HQL MEMBER OF — check if a value is in a collection
    public List<Author> findAuthorsWhoWroteBook(Book book) {
        return entityManager.createQuery(
            "SELECT a FROM Author a WHERE :book MEMBER OF a.books",
            Author.class
        )
        .setParameter("book", book)
        .getResultList();
    }

    // Named Queries — defined on the entity, reusable anywhere
    // (see @NamedQuery annotation on the entity for definition)
    public List<Book> executeNamedQuery(String genre) {
        return entityManager.createNamedQuery("Book.findByGenre", Book.class)
            .setParameter("genre", genre)
            .getResultList();
    }


    // =========================================================================
    // SECTION 6: UPDATE and DELETE via JPQL (Bulk Operations)
    // =========================================================================
    // JPQL UPDATE/DELETE bypass the persistence context (no dirty checking needed).
    // Must run inside a @Transactional method.

    @jakarta.transaction.Transactional
    public int bulkUpdatePrice(String genre, BigDecimal priceIncrease) {
        // executeUpdate() returns the number of rows affected
        return entityManager.createQuery(
            "UPDATE Book b SET b.price = b.price + :increase WHERE b.genre = :genre"
        )
        .setParameter("increase", priceIncrease)
        .setParameter("genre", genre)
        .executeUpdate();  // NOT getResultList() — this is a modification
    }

    @jakarta.transaction.Transactional
    public int deleteUnavailableBooks() {
        return entityManager.createQuery(
            "DELETE FROM Book b WHERE b.available = false AND b.stockQuantity = 0"
        ).executeUpdate();
    }


    // =========================================================================
    // SECTION 7: CRITERIA API BASICS
    // =========================================================================
    // The Criteria API lets you build queries programmatically using Java objects
    // instead of query strings. Benefits:
    //   ✅ Type-safe — compiler catches typos in field names (if using metamodel)
    //   ✅ Dynamic queries — easy to add WHERE clauses conditionally
    //   ✅ IDE autocompletion
    //   ❌ More verbose than JPQL strings
    //
    // Use Criteria API when: you need dynamic queries where the number of
    // conditions varies based on user input (e.g., a search filter with 10 optional fields)
    //
    // Building blocks:
    //   CriteriaBuilder  — factory for all query elements (conditions, functions, etc.)
    //   CriteriaQuery    — the query itself (SELECT type, DISTINCT, ORDER BY)
    //   Root<T>          — represents the "FROM" entity — gives access to its fields/paths
    //   Predicate        — a WHERE condition (can combine multiple with and/or)
    // =========================================================================

    // --- Basic Criteria query ---
    public List<Book> findAllWithCriteria() {
        // Step 1: get the CriteriaBuilder from EntityManager
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();

        // Step 2: create a CriteriaQuery specifying the return type
        CriteriaQuery<Book> cq = cb.createQuery(Book.class);

        // Step 3: define the root entity (equivalent to "FROM Book b" in JPQL)
        Root<Book> bookRoot = cq.from(Book.class);

        // Step 4: build the SELECT clause
        cq.select(bookRoot);

        // Step 5: execute and return results
        return entityManager.createQuery(cq).getResultList();
    }

    // --- Criteria with WHERE clause ---
    public List<Book> findByGenreWithCriteria(String genre) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Book> cq = cb.createQuery(Book.class);
        Root<Book> book = cq.from(Book.class);

        // cb.equal() creates an equality predicate — like "WHERE b.genre = :genre"
        Predicate genrePredicate = cb.equal(book.get("genre"), genre);

        cq.select(book).where(genrePredicate);

        return entityManager.createQuery(cq).getResultList();
    }

    // --- Criteria with multiple conditions (AND / OR) ---
    public List<Book> findByGenreAndMaxPriceWithCriteria(String genre, BigDecimal maxPrice) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Book> cq = cb.createQuery(Book.class);
        Root<Book> book = cq.from(Book.class);

        // cb.lessThanOrEqualTo() — like "b.price <= :maxPrice"
        Predicate pricePredicate = cb.lessThanOrEqualTo(book.get("price"), maxPrice);
        Predicate genrePredicate = cb.equal(book.get("genre"), genre);

        // cb.and() combines predicates — both must be true
        Predicate combined = cb.and(genrePredicate, pricePredicate);

        // ORDER BY price ASC
        cq.select(book)
          .where(combined)
          .orderBy(cb.asc(book.get("price")));

        return entityManager.createQuery(cq).getResultList();
    }

    // --- DYNAMIC query — the real power of Criteria API ---
    // This is where Criteria API shines over JPQL strings.
    // The user might filter by genre, price range, author — or any combination.
    // With JPQL strings you'd have to concatenate conditionally (messy and error-prone).
    // With Criteria API, you add predicates conditionally and combine at the end.
    public List<Book> dynamicSearch(String genre, BigDecimal minPrice, BigDecimal maxPrice,
                                    String authorKeyword) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Book> cq = cb.createQuery(Book.class);
        Root<Book> book = cq.from(Book.class);

        // Start with an empty list of predicates
        java.util.List<Predicate> predicates = new java.util.ArrayList<>();

        // Add each filter ONLY if the caller provided a value
        if (genre != null && !genre.isBlank()) {
            predicates.add(cb.equal(book.get("genre"), genre));
        }
        if (minPrice != null) {
            predicates.add(cb.greaterThanOrEqualTo(book.get("price"), minPrice));
        }
        if (maxPrice != null) {
            predicates.add(cb.lessThanOrEqualTo(book.get("price"), maxPrice));
        }
        if (authorKeyword != null && !authorKeyword.isBlank()) {
            predicates.add(cb.like(
                cb.lower(book.get("author")),
                "%" + authorKeyword.toLowerCase() + "%"
            ));
        }

        // Convert list to array and apply as AND conditions
        cq.select(book)
          .where(cb.and(predicates.toArray(new Predicate[0])))
          .orderBy(cb.asc(book.get("title")));

        return entityManager.createQuery(cq).getResultList();
    }

    // --- Criteria with COUNT (aggregate) ---
    public long countBooksWithCriteria(String genre) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();

        // Note: createQuery(Long.class) — return type is Long for COUNT
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<Book> book = cq.from(Book.class);

        // cb.count() — generates COUNT(b) in the SELECT clause
        cq.select(cb.count(book));

        if (genre != null) {
            cq.where(cb.equal(book.get("genre"), genre));
        }

        return entityManager.createQuery(cq).getSingleResult();
    }

    // --- Criteria with JOIN ---
    public List<Book> findBooksInCategoryWithCriteria(String categoryName) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Book> cq = cb.createQuery(Book.class);
        Root<Book> book = cq.from(Book.class);

        // Join to the category relationship — "book.category"
        Join<Book, Category> categoryJoin = book.join("category", JoinType.INNER);

        // Filter by category name
        Predicate catNamePredicate = cb.equal(categoryJoin.get("name"), categoryName);

        cq.select(book).where(catNamePredicate);

        return entityManager.createQuery(cq).getResultList();
    }
}


// =============================================================================
// SECTION 8: @NamedQuery — Define queries on the entity class
// =============================================================================
// Named queries are defined ONCE on the entity and reusable everywhere.
// They are validated at startup — typos in JPQL are caught on boot, not at runtime.

@Entity
@NamedQuery(
    name = "Book.findByGenre",
    query = "SELECT b FROM Book b WHERE b.genre = :genre ORDER BY b.title"
)
@NamedQuery(
    name = "Book.findCheapAvailable",
    query = "SELECT b FROM Book b WHERE b.available = true AND b.price < :maxPrice"
)
// For multiple NamedQueries on one entity, use @NamedQueries wrapper:
// @NamedQueries({
//     @NamedQuery(name = "...", query = "..."),
//     @NamedQuery(name = "...", query = "...")
// })
class BookWithNamedQuery {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String genre;
    private String title;
    private BigDecimal price;
    private boolean available;
}

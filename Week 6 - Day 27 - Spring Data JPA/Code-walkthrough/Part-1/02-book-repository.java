package com.bookstore.repository;

import com.bookstore.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

// =============================================================================
// SPRING DATA REPOSITORY — BookRepository
// =============================================================================
// Extending JpaRepository<Book, Long> gives us:
//   - All CrudRepository methods (save, findById, findAll, delete, count…)
//   - All PagingAndSortingRepository methods (findAll(Pageable), findAll(Sort))
//   - JPA-specific methods (flush, saveAndFlush, deleteInBatch, getById…)
//
// T  = Book  (the entity type this repository manages)
// ID = Long  (the type of the entity's primary key)
//
// Spring will automatically generate an implementation at startup —
// we never write a class that implements this interface.
// =============================================================================
@Repository  // Optional (JpaRepository already triggers detection), but adds clarity
public interface BookRepository extends JpaRepository<Book, Long> {

    // =========================================================================
    // SECTION 1: CRUD OPERATIONS
    // Inherited from JpaRepository — no code needed here, just documenting them
    //
    //   save(book)          → INSERT if new (no id), UPDATE if existing (has id)
    //   findById(1L)        → Optional<Book> — forces caller to handle "not found"
    //   findAll()           → List<Book>
    //   findAll(sort)       → List<Book> ordered by a field
    //   count()             → long (total rows)
    //   deleteById(1L)      → DELETE WHERE id = 1
    //   existsById(1L)      → boolean
    //
    // These come for FREE — no @Query, no SQL, nothing to write.
    // =========================================================================


    // =========================================================================
    // SECTION 2: QUERY METHODS BY NAMING CONVENTION
    // Spring Data parses the method name and generates the SQL automatically.
    //
    // Pattern: findBy<Property>[Condition][And/Or<Property>[Condition]]
    //
    // Keywords: findBy, existsBy, countBy, deleteBy
    // Conditions: And, Or, Like, Containing, StartingWith, EndingWith,
    //             IgnoreCase, Between, LessThan, GreaterThan, IsNull, IsNotNull,
    //             OrderBy, In, NotIn, True, False
    // =========================================================================

    // Find all books by a specific author (exact match)
    // Generated SQL: SELECT * FROM books WHERE author = ?
    List<Book> findByAuthor(String author);

    // Find by title — case-insensitive contains (like %title%)
    // Generated SQL: SELECT * FROM books WHERE LOWER(title) LIKE LOWER('%keyword%')
    List<Book> findByTitleContainingIgnoreCase(String keyword);

    // Find books cheaper than a given price
    // Generated SQL: SELECT * FROM books WHERE price < ?
    List<Book> findByPriceLessThan(BigDecimal maxPrice);

    // Find books within a price range
    // Generated SQL: SELECT * FROM books WHERE price BETWEEN ? AND ?
    List<Book> findByPriceBetween(BigDecimal min, BigDecimal max);

    // Find books published after a specific date
    // Generated SQL: SELECT * FROM books WHERE published_date > ?
    List<Book> findByPublishedDateAfter(LocalDate date);

    // Find by genre AND available = true
    // Generated SQL: SELECT * FROM books WHERE genre = ? AND available = true
    List<Book> findByGenreAndAvailableTrue(String genre);

    // Find by author OR genre
    // Generated SQL: SELECT * FROM books WHERE author = ? OR genre = ?
    List<Book> findByAuthorOrGenre(String author, String genre);

    // Find by ISBN (should be unique — returns Optional to handle "not found")
    // Generated SQL: SELECT * FROM books WHERE isbn = ?
    Optional<Book> findByIsbn(String isbn);

    // Count books by genre
    // Generated SQL: SELECT COUNT(*) FROM books WHERE genre = ?
    long countByGenre(String genre);

    // Check if a book with a given ISBN already exists
    // Generated SQL: SELECT COUNT(*) > 0 FROM books WHERE isbn = ?
    boolean existsByIsbn(String isbn);

    // Delete all books by a given author (returns count of deleted rows)
    // ⚠️ Must be in a @Transactional method — Spring Data requires it for delete
    long deleteByAuthor(String author);

    // Find top 5 most expensive books, sorted by price descending
    // "Top5" limits results; "OrderByPriceDesc" adds ORDER BY
    List<Book> findTop5ByOrderByPriceDesc();

    // Find all books with a null stock quantity (never set after creation)
    List<Book> findByStockQuantityIsNull();

    // Find books ordered by title ascending (property expression with Sort)
    List<Book> findByGenre(String genre, Sort sort);


    // =========================================================================
    // SECTION 3: @QUERY ANNOTATION — JPQL
    // Use @Query when the method name would be too complex, or you need
    // features that naming convention can't express (aggregation, JOIN, etc.)
    //
    // JPQL (Java Persistence Query Language) — looks like SQL but operates
    // on ENTITY NAMES and FIELD NAMES (not table/column names).
    // "SELECT b FROM Book b" — "Book" = entity class, "b" = alias
    // =========================================================================

    // Simple JPQL — select all books (equivalent to findAll, shown for reference)
    @Query("SELECT b FROM Book b")
    List<Book> findAllBooks();

    // JPQL with a named parameter (:author) — clearer than positional (?)
    @Query("SELECT b FROM Book b WHERE b.author = :author ORDER BY b.title ASC")
    List<Book> findByAuthorJpql(@Param("author") String author);

    // JPQL with multiple conditions
    @Query("SELECT b FROM Book b WHERE b.genre = :genre AND b.price <= :maxPrice")
    List<Book> findByGenreAndMaxPrice(@Param("genre") String genre,
                                      @Param("maxPrice") BigDecimal maxPrice);

    // JPQL with LIKE — use % in the value or concat in query
    @Query("SELECT b FROM Book b WHERE LOWER(b.title) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Book> searchByTitle(@Param("keyword") String keyword);

    // JPQL aggregate — average price per genre
    // Returns List<Object[]> — each element is [genre, avgPrice]
    @Query("SELECT b.genre, AVG(b.price) FROM Book b GROUP BY b.genre")
    List<Object[]> averagePriceByGenre();

    // JPQL with JOIN — fetch books along with their category (relationship)
    @Query("SELECT b FROM Book b JOIN b.category c WHERE c.name = :categoryName")
    List<Book> findByCategoryName(@Param("categoryName") String categoryName);

    // JPQL projection — return only title and author (not the whole entity)
    // Uses a constructor expression: DTO must have matching constructor
    @Query("SELECT new com.bookstore.dto.BookSummaryDTO(b.title, b.author, b.price) " +
           "FROM Book b WHERE b.available = true")
    List<Object> findAvailableBookSummaries();

    // JPQL update — bulk update (no need to load each entity)
    // ⚠️ @Modifying required for UPDATE/DELETE JPQL — without it Spring throws
    // ⚠️ @Transactional required — bulk modifications must run in a transaction
    @Modifying
    @Query("UPDATE Book b SET b.available = false WHERE b.stockQuantity = 0")
    int markOutOfStockBooksUnavailable();

    // JPQL delete
    @Modifying
    @Query("DELETE FROM Book b WHERE b.publishedDate < :cutoffDate")
    int deleteOldBooks(@Param("cutoffDate") LocalDate cutoffDate);


    // =========================================================================
    // SECTION 4: @QUERY — NATIVE SQL
    // Use nativeQuery = true when:
    //   - You need a DB-specific feature (e.g. PostgreSQL array ops, full-text search)
    //   - You're optimizing a complex query that JPQL can't express efficiently
    //   - You're working with a legacy schema you can't change
    //
    // ⚠️ Native SQL is NOT portable — if you switch databases, it may break.
    // =========================================================================

    // Native SQL — exact same result as findAll, but using raw SQL
    @Query(value = "SELECT * FROM books", nativeQuery = true)
    List<Book> findAllNative();

    // Native SQL with parameter — uses ? positional parameter (not :name)
    @Query(value = "SELECT * FROM books WHERE genre = ?1", nativeQuery = true)
    List<Book> findByGenreNative(String genre);

    // Native SQL — full-text search (PostgreSQL specific)
    @Query(value = "SELECT * FROM books WHERE to_tsvector('english', title || ' ' || description) " +
                   "@@ plainto_tsquery('english', ?1)", nativeQuery = true)
    List<Book> fullTextSearch(String searchTerm);

    // Native SQL — complex aggregation with JOIN (hard to express in JPQL)
    @Query(value = """
            SELECT a.name, COUNT(b.id) AS book_count, AVG(b.price) AS avg_price
            FROM books b
            JOIN authors a ON a.id = b.author_id
            GROUP BY a.name
            ORDER BY book_count DESC
            LIMIT ?1
            """, nativeQuery = true)
    List<Object[]> topAuthorsByBookCount(int limit);


    // =========================================================================
    // SECTION 5: PAGINATION AND SORTING
    // Spring Data has built-in support for pagination via Pageable and Page<T>
    //
    // Pageable = page number + page size + optional sort
    // Page<T>  = the results + metadata (total pages, total elements, current page)
    //
    // Callers create: PageRequest.of(pageNumber, pageSize, Sort.by("field"))
    // =========================================================================

    // Returns a Page<Book> — Spring automatically adds LIMIT and OFFSET to the SQL
    // Also runs a COUNT(*) query to populate total element/page metadata
    Page<Book> findAll(Pageable pageable);

    // Paginated query by genre
    Page<Book> findByGenre(String genre, Pageable pageable);

    // Paginated search by keyword
    @Query("SELECT b FROM Book b WHERE LOWER(b.title) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Book> searchByTitlePaged(@Param("keyword") String keyword, Pageable pageable);

    // Sort-only (no pagination) — find all, sorted
    List<Book> findByAvailableTrue(Sort sort);
}

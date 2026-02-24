package com.bookstore.service;

import com.bookstore.dao.BookDao;
import com.bookstore.dao.BookDaoImpl;
import com.bookstore.dto.BookDTO;
import com.bookstore.entity.Book;
import com.bookstore.exception.BookNotFoundException;
import com.bookstore.exception.DuplicateIsbnException;
import com.bookstore.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

// =============================================================================
// SECTION 1: DATA ACCESS OBJECT (DAO) PATTERN
// =============================================================================
// The DAO pattern is a design pattern that abstracts the persistence layer.
// It separates "business logic" from "data access logic."
//
// DAO Pattern structure:
//   BookDao (interface) — contract for data operations
//   BookDaoImpl (class) — implementation using EntityManager / JDBC / etc.
//   BookService — uses BookDao, unaware of HOW data is fetched
//
// BEFORE Spring Data JPA, you had to write BookDaoImpl by hand.
// With Spring Data JPA, the REPOSITORY IS the DAO — it implements the pattern
// automatically. You rarely write a separate DAO class anymore, but understanding
// the pattern helps you know WHY repositories are structured the way they are.
// =============================================================================

// ---- DAO Interface (what you would have written pre-Spring Data) ----
interface BookDao {
    Book findById(Long id);
    List<Book> findAll();
    Book save(Book book);
    void delete(Long id);
    List<Book> findByAuthor(String author);
}

// ---- DAO Implementation (hand-written with EntityManager — no Spring Data) ----
// This is what Spring Data generates FOR you automatically
/*
@Repository
class BookDaoImpl implements BookDao {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Book findById(Long id) {
        return entityManager.find(Book.class, id);   // direct entity lookup by PK
    }

    @Override
    public List<Book> findAll() {
        return entityManager.createQuery("SELECT b FROM Book b", Book.class)
                            .getResultList();
    }

    @Override
    public Book save(Book book) {
        if (book.getId() == null) {
            entityManager.persist(book);     // INSERT
            return book;
        } else {
            return entityManager.merge(book); // UPDATE
        }
    }

    @Override
    public void delete(Long id) {
        Book book = findById(id);
        if (book != null) {
            entityManager.remove(book);
        }
    }

    @Override
    public List<Book> findByAuthor(String author) {
        return entityManager.createQuery(
                "SELECT b FROM Book b WHERE b.author = :author", Book.class)
                .setParameter("author", author)
                .getResultList();
    }
}
*/

// =============================================================================
// SECTION 2: BOOKSERVICE — Using Spring Data Repository
// =============================================================================
// In modern Spring Boot apps, the SERVICE layer depends on the Repository (DAO).
// The service contains business logic; the repository handles data access.
//
// Service layer responsibilities:
//   - Orchestrate multiple repository calls
//   - Apply business rules (e.g., "can't place order if stock = 0")
//   - Manage transactions (@Transactional goes HERE, not on repositories)
//   - Map entities to DTOs (so controllers never see raw entities)
// =============================================================================

@Service
@Transactional(readOnly = true)   // Default: all methods are read-only transactions
                                   // (no write lock acquired — better performance for reads)
@RequiredArgsConstructor           // Lombok: generates constructor for final fields
@Slf4j                             // Lombok: generates 'log' field
public class BookService {

    // =========================================================================
    // SECTION 3: CONSTRUCTOR INJECTION (preferred over @Autowired field injection)
    // @RequiredArgsConstructor generates: BookService(BookRepository bookRepository)
    // =========================================================================
    private final BookRepository bookRepository;


    // =========================================================================
    // SECTION 4: BASIC CRUD OPERATIONS via repository
    // =========================================================================

    // READ — findById returns Optional<Book>, not null — forces null-safety
    public Book getBookById(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new BookNotFoundException("Book not found with id: " + id));
    }

    // READ — all books
    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    // CREATE — @Transactional(readOnly=false) overrides the class-level readOnly=true
    @Transactional   // removes readOnly=true (default is readOnly=false)
    public Book createBook(Book book) {
        // Business rule: ISBN must be unique
        if (bookRepository.existsByIsbn(book.getIsbn())) {
            throw new DuplicateIsbnException("A book with ISBN " + book.getIsbn() + " already exists");
        }
        Book saved = bookRepository.save(book);  // save() = INSERT because id is null
        log.info("Created new book: {} (id={})", saved.getTitle(), saved.getId());
        return saved;
    }

    // UPDATE — load entity, modify fields, let JPA auto-detect changes (dirty checking)
    @Transactional
    public Book updateBook(Long id, Book updateData) {
        Book existing = getBookById(id);  // throws if not found

        // Update fields — Hibernate's dirty checking will generate UPDATE SQL
        // automatically when the transaction commits (no explicit save() needed,
        // though calling save() is fine too and makes it explicit)
        existing.setTitle(updateData.getTitle());
        existing.setAuthor(updateData.getAuthor());
        existing.setPrice(updateData.getPrice());
        existing.setStockQuantity(updateData.getStockQuantity());

        return bookRepository.save(existing);  // merge + flush
    }

    // DELETE
    @Transactional
    public void deleteBook(Long id) {
        if (!bookRepository.existsById(id)) {
            throw new BookNotFoundException("Book not found with id: " + id);
        }
        bookRepository.deleteById(id);
        log.info("Deleted book with id={}", id);
    }


    // =========================================================================
    // SECTION 5: QUERY METHODS IN ACTION
    // Calling the named-convention methods from BookRepository
    // =========================================================================

    public List<Book> getBooksByAuthor(String author) {
        return bookRepository.findByAuthor(author);
    }

    public List<Book> searchBooks(String keyword) {
        return bookRepository.findByTitleContainingIgnoreCase(keyword);
    }

    public List<Book> getBooksByGenreUnderPrice(String genre, BigDecimal maxPrice) {
        return bookRepository.findByGenreAndMaxPrice(genre, maxPrice);
    }

    public List<Book> getTop5MostExpensive() {
        return bookRepository.findTop5ByOrderByPriceDesc();
    }


    // =========================================================================
    // SECTION 6: PAGINATION AND SORTING
    // PageRequest.of(page, size) — 0-indexed page number
    // PageRequest.of(page, size, Sort.by("field")) — with sort
    // =========================================================================

    // Returns a Page<Book> — contains books + total count + page metadata
    public Page<Book> getBooksPage(int page, int size) {
        // PageRequest.of(0, 10) = first page, 10 items per page
        Pageable pageable = PageRequest.of(page, size, Sort.by("title").ascending());
        return bookRepository.findAll(pageable);
    }

    // Paginated search by genre
    public Page<Book> getBooksByGenrePaged(String genre, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("price").descending());
        return bookRepository.findByGenre(genre, pageable);
    }

    // Using Page<T> response metadata
    public void demonstratePaginationMetadata(Page<Book> page) {
        log.info("=== Pagination Metadata ===");
        log.info("Current page: {}", page.getNumber());       // 0-indexed
        log.info("Page size: {}", page.getSize());            // items per page
        log.info("Total elements: {}", page.getTotalElements()); // total rows in DB
        log.info("Total pages: {}", page.getTotalPages());
        log.info("Is first page: {}", page.isFirst());
        log.info("Is last page: {}", page.isLast());
        log.info("Has next page: {}", page.hasNext());
        log.info("Has previous page: {}", page.hasPrevious());
    }

    // Sort-only (no pagination) — useful when list is bounded
    public List<Book> getAvailableBooksSorted() {
        Sort sort = Sort.by(Sort.Order.asc("genre"), Sort.Order.desc("price"));
        return bookRepository.findByAvailableTrue(sort);
    }


    // =========================================================================
    // SECTION 7: TRANSACTION MANAGEMENT — @Transactional deep dive
    // =========================================================================

    // --- PROPAGATION ---
    // Controls what happens when a transactional method calls another transactional method.
    //
    // REQUIRED (default)    — join existing transaction; create new one if none exists
    // REQUIRES_NEW          — always create a new transaction (suspends the outer one)
    // SUPPORTS              — join if exists; run without transaction if none
    // MANDATORY             — must run inside an existing transaction; throws if none
    // NOT_SUPPORTED         — suspend any existing transaction; run without one
    // NEVER                 — must NOT run inside a transaction; throws if one exists
    // NESTED                — run in a nested transaction (savepoint) if one exists

    @Transactional(propagation = Propagation.REQUIRED)   // default — join or create
    public void processBookOrder(Long bookId, int quantity) {
        Book book = getBookById(bookId);

        // Business rule: reduce stock
        if (book.getStockQuantity() < quantity) {
            throw new IllegalStateException("Not enough stock for book: " + book.getTitle());
        }
        book.setStockQuantity(book.getStockQuantity() - quantity);
        bookRepository.save(book);

        // log the audit — runs in the SAME transaction (REQUIRED propagation)
        logAuditEntry(bookId, "ORDER_PLACED", quantity);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)  // always NEW transaction
    public void logAuditEntry(Long bookId, String action, int quantity) {
        // Even if processBookOrder rolls back, this audit log is committed separately
        // because it runs in its OWN transaction.
        log.info("AUDIT: bookId={}, action={}, quantity={}", bookId, action, quantity);
        // In a real app: auditRepository.save(new AuditEntry(bookId, action, quantity));
    }

    // --- ISOLATION ---
    // Controls what uncommitted changes from OTHER transactions are visible to this one.
    //
    // READ_UNCOMMITTED  — can read dirty (uncommitted) data from others (fastest, least safe)
    // READ_COMMITTED    — only reads committed data (PostgreSQL default)
    // REPEATABLE_READ   — same read returns same data within transaction (MySQL default)
    // SERIALIZABLE      — full isolation, transactions execute as if serial (slowest, safest)

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public BigDecimal calculateTotalInventoryValue() {
        // REPEATABLE_READ ensures that if we read prices twice in this transaction,
        // we get the same values even if another transaction updates them between reads
        List<Book> books = bookRepository.findAll();
        return books.stream()
                .map(b -> b.getPrice().multiply(BigDecimal.valueOf(b.getStockQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    // --- ROLLBACK ---
    // By default @Transactional only rolls back on RuntimeException and Error.
    // Checked exceptions (IOException, SQLException) do NOT trigger rollback unless specified.

    @Transactional(rollbackFor = Exception.class)   // rolls back on ANY exception (including checked)
    public void importBooksFromFile(List<Book> books) throws Exception {
        for (Book book : books) {
            if (book.getIsbn() == null || book.getIsbn().isBlank()) {
                // This checked exception WILL trigger rollback because rollbackFor = Exception.class
                throw new Exception("Invalid ISBN found during import — rolling back all inserts");
            }
            bookRepository.save(book);
        }
    }

    @Transactional(noRollbackFor = BookNotFoundException.class)  // don't rollback for this exception
    public void softDeleteBook(Long id) {
        try {
            Book book = getBookById(id);  // throws BookNotFoundException if not found
            book.setAvailable(false);
            bookRepository.save(book);
        } catch (BookNotFoundException e) {
            // Log the warning but don't roll back — it's okay if the book doesn't exist
            log.warn("Attempted to soft-delete non-existent book id={}", id);
        }
    }


    // =========================================================================
    // SECTION 8: BULK OPERATIONS
    // =========================================================================

    // Bulk update via @Modifying @Query — far more efficient than loading each entity
    @Transactional
    public int markOutOfStockBooksUnavailable() {
        int updated = bookRepository.markOutOfStockBooksUnavailable();
        log.info("Marked {} books as unavailable (stock = 0)", updated);
        return updated;
    }

    // saveAll — batch insert for performance (Spring Data sends them together)
    @Transactional
    public List<Book> bulkCreateBooks(List<Book> books) {
        return bookRepository.saveAll(books);  // more efficient than iterating and calling save()
    }
}

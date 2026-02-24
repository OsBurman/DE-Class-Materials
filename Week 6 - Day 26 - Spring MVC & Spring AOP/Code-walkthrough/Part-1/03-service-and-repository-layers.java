package com.bookstore.layers;

// =============================================================================
// FILE: 03-service-and-repository-layers.java
//
// This file demonstrates the THREE-LAYER ARCHITECTURE in Spring MVC:
//   1. Repository Layer  — data access (shown first, bottom-up)
//   2. Service Layer     — business logic
//   3. (Controller Layer — shown in 02-bookstore-controller.java)
//
// Domain: Bookstore API
//   - Book entity
//   - BookRepository (data access)
//   - BookService + BookServiceImpl (business logic)
// =============================================================================

import com.bookstore.dto.BookDTO;
import com.bookstore.dto.CreateBookRequest;
import com.bookstore.dto.UpdateBookRequest;
import com.bookstore.exception.BookNotFoundException;
import com.bookstore.exception.DuplicateIsbnException;
import com.bookstore.model.Book;
import jakarta.persistence.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

// =============================================================================
// SECTION 1: THE ENTITY CLASS
// =============================================================================
// The Book entity maps to the 'books' table in the database.
// This is the PERSISTENCE model — it mirrors the DB structure.
// We do NOT expose entities directly to the client (use DTOs for that).
// =============================================================================

@Entity
@Table(name = "books")
class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(nullable = false, length = 100)
    private String author;

    @Column(unique = true, length = 13)
    private String isbn;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "published_date")
    private LocalDate publishedDate;

    @Column(name = "genre", length = 50)
    private String genre;

    @Column(name = "in_stock")
    private boolean inStock = true;

    // Default constructor required by JPA
    protected Book() {}

    public Book(String title, String author, String isbn,
                BigDecimal price, LocalDate publishedDate, String genre) {
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.price = price;
        this.publishedDate = publishedDate;
        this.genre = genre;
    }

    // --- Getters and setters ---
    public Long getId()                 { return id; }
    public String getTitle()            { return title; }
    public void setTitle(String t)      { this.title = t; }
    public String getAuthor()           { return author; }
    public void setAuthor(String a)     { this.author = a; }
    public String getIsbn()             { return isbn; }
    public BigDecimal getPrice()        { return price; }
    public void setPrice(BigDecimal p)  { this.price = p; }
    public LocalDate getPublishedDate() { return publishedDate; }
    public String getGenre()            { return genre; }
    public boolean isInStock()          { return inStock; }
    public void setInStock(boolean s)   { this.inStock = s; }
}

// =============================================================================
// SECTION 2: REPOSITORY LAYER
// =============================================================================
// The Repository layer is responsible for ONE thing: talking to the database.
//
// @Repository marks this interface as a Spring-managed data-access component.
// When you extend JpaRepository<Book, Long>, Spring Data generates a full
// implementation at startup — you write zero SQL for basic CRUD.
//
// Rules for the repository layer:
//   ✅ Database queries (by ID, by field, by custom @Query)
//   ✅ Save, update, delete
//   ❌ Business logic (that belongs in the service)
//   ❌ HTTP concerns (no HttpServletRequest, no status codes)
// =============================================================================

@Repository
interface BookRepository extends JpaRepository<Book, Long> {

    // -------------------------------------------------------------------------
    // Spring Data DERIVED QUERY METHODS
    // -------------------------------------------------------------------------
    // Spring Data reads the method name and generates the JPQL automatically.
    // Naming convention: find[By][Property][Constraint]
    //
    // findByGenre("fiction") →
    //   SELECT b FROM Book b WHERE b.genre = :genre
    // -------------------------------------------------------------------------

    List<Book> findByGenre(String genre);

    Optional<Book> findByIsbn(String isbn);

    List<Book> findByAuthorContainingIgnoreCase(String authorFragment);

    List<Book> findByPriceLessThanEqual(BigDecimal maxPrice);

    boolean existsByIsbn(String isbn);

    // -------------------------------------------------------------------------
    // Combined conditions
    // -------------------------------------------------------------------------
    List<Book> findByGenreAndInStockTrue(String genre);

    List<Book> findByPublishedDateAfterOrderByPublishedDateDesc(LocalDate date);
}

// =============================================================================
// SECTION 3: SERVICE LAYER — the interface
// =============================================================================
// Always define the service as an interface + implementation.
// Benefits:
//   1. Controller depends on the interface, not the impl — loose coupling
//   2. Easy to swap implementations (e.g., real vs mock for tests)
//   3. Spring's @Transactional, AOP, and proxies work cleanly with interfaces
// =============================================================================

interface BookService {
    List<BookDTO> findAll(String genre, int page, int size);
    BookDTO findById(Long id);
    BookDTO createBook(CreateBookRequest request);
    BookDTO updateBook(Long id, UpdateBookRequest request);
    BookDTO updatePrice(Long id, double newPrice);
    void deleteBook(Long id);
}

// =============================================================================
// SECTION 4: SERVICE LAYER — the implementation
// =============================================================================
// @Service is a specialization of @Component that marks this bean as
// "business logic lives here". Spring creates it as a singleton.
//
// Rules for the service layer:
//   ✅ Business logic (discount calculation, stock checks, order validation)
//   ✅ Orchestrating multiple repository calls in one transaction
//   ✅ Mapping between entities and DTOs
//   ✅ Transaction management (@Transactional)
//   ❌ HTTP concerns (no HttpServletRequest, no ResponseEntity)
//   ❌ Direct Jackson/JSON operations
//   ❌ Logging at DEBUG level for every line (use AOP for cross-cutting logs)
// =============================================================================

@Service
@Transactional(readOnly = true)   // Default: all methods are read-only transactions
                                  // Overridden per-method for write operations
class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;

    // Constructor injection — preferred over @Autowired field injection
    // Reasons: immutable, testable (can pass a mock), clear dependencies
    public BookServiceImpl(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    // =========================================================================
    // READ OPERATIONS (readOnly = true inherited from class-level @Transactional)
    // =========================================================================

    @Override
    public List<BookDTO> findAll(String genre, int page, int size) {
        // If genre filter provided, use derived query; otherwise return all
        List<Book> books = (genre != null && !genre.isBlank())
                ? bookRepository.findByGenre(genre)
                : bookRepository.findAll();

        // Map entity list → DTO list (covered fully in 05-dtos-and-mapping.java)
        return books.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public BookDTO findById(Long id) {
        // findById returns Optional<Book>
        // orElseThrow throws a custom exception if not found
        // @ExceptionHandler in GlobalExceptionHandler catches it → 404 response
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new BookNotFoundException(
                        "Book not found with id: " + id));
        return toDTO(book);
    }

    // =========================================================================
    // WRITE OPERATIONS — override @Transactional to allow writes
    // =========================================================================

    @Override
    @Transactional   // readOnly = false (default) → allows INSERT/UPDATE/DELETE
    public BookDTO createBook(CreateBookRequest request) {
        // ---- Business rule: ISBN must be unique ----
        if (bookRepository.existsByIsbn(request.getIsbn())) {
            throw new DuplicateIsbnException(
                    "A book with ISBN " + request.getIsbn() + " already exists");
        }

        // Map DTO → Entity
        Book book = new Book(
                request.getTitle(),
                request.getAuthor(),
                request.getIsbn(),
                BigDecimal.valueOf(request.getPrice()),
                request.getPublishedDate(),
                request.getGenre()
        );

        Book saved = bookRepository.save(book);   // INSERT, returns the persisted entity with generated id
        return toDTO(saved);
    }

    @Override
    @Transactional
    public BookDTO updateBook(Long id, UpdateBookRequest request) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new BookNotFoundException("Book not found with id: " + id));

        // Update only the provided fields (PUT = full replace in this example)
        book.setTitle(request.getTitle());
        book.setAuthor(request.getAuthor());
        book.setPrice(BigDecimal.valueOf(request.getPrice()));

        // No explicit save() needed — JPA detects changes to a managed entity
        // and flushes them at transaction commit (dirty checking)
        return toDTO(book);
    }

    @Override
    @Transactional
    public BookDTO updatePrice(Long id, double newPrice) {
        // ---- Business rule: price cannot be negative ----
        if (newPrice < 0) {
            throw new IllegalArgumentException("Price cannot be negative: " + newPrice);
        }
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new BookNotFoundException("Book not found with id: " + id));
        book.setPrice(BigDecimal.valueOf(newPrice));
        return toDTO(book);
    }

    @Override
    @Transactional
    public void deleteBook(Long id) {
        if (!bookRepository.existsById(id)) {
            throw new BookNotFoundException("Book not found with id: " + id);
        }
        bookRepository.deleteById(id);
    }

    // =========================================================================
    // PRIVATE HELPER — Entity → DTO mapping
    // =========================================================================
    // Manual mapping is simple and explicit. In 05-dtos-and-mapping.java we'll
    // show how MapStruct generates this automatically.
    // =========================================================================

    private BookDTO toDTO(Book book) {
        BookDTO dto = new BookDTO();
        dto.setId(book.getId());
        dto.setTitle(book.getTitle());
        dto.setAuthor(book.getAuthor());
        dto.setIsbn(book.getIsbn());
        dto.setPrice(book.getPrice().doubleValue());
        dto.setGenre(book.getGenre());
        dto.setInStock(book.isInStock());
        return dto;
    }
}

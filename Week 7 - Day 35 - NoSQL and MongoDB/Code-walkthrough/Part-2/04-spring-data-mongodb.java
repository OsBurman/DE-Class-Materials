// =============================================================================
// Day 35 — MongoDB Part 2: Spring Data MongoDB Integration
// Bookstore Application
//
// Topics covered:
//   1. Maven dependencies and application.properties configuration
//   2. @Document and @Id — mapping Java classes to MongoDB collections
//   3. MongoRepository — CRUD and derived query methods
//   4. Custom @Query annotations with MongoDB query language
//   5. MongoTemplate — advanced queries and bulk operations
//   6. Aggregation with Spring Data MongoDB
//   7. REST controller wiring it all together
// =============================================================================

package com.bookstore.mongodb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

// =============================================================================
// SECTION 1: Dependencies and Configuration
// =============================================================================
//
// ─── pom.xml dependency ──────────────────────────────────────────────────────
//
//   <dependency>
//     <groupId>org.springframework.boot</groupId>
//     <artifactId>spring-boot-starter-data-mongodb</artifactId>
//   </dependency>
//
// ─── application.properties ──────────────────────────────────────────────────
//
//   # Local MongoDB
//   spring.data.mongodb.host=localhost
//   spring.data.mongodb.port=27017
//   spring.data.mongodb.database=bookstore_db
//
//   # OR use a full URI (required for Atlas)
//   spring.data.mongodb.uri=mongodb://localhost:27017/bookstore_db
//
//   # MongoDB Atlas connection string:
//   # spring.data.mongodb.uri=mongodb+srv://user:${MONGO_PASSWORD}@cluster0.xyz.mongodb.net/bookstore_db
//
//   # Optional: enable MongoDB query logging
//   logging.level.org.springframework.data.mongodb.core.MongoTemplate=DEBUG

// =============================================================================
// SECTION 2: @Document — Mapping Java Class to MongoDB Collection
// =============================================================================

// @Document marks this class as a MongoDB document
// collection = the collection name in MongoDB (defaults to the class name in camelCase)
@Document(collection = "books")
class Book {

    @Id                             // maps to MongoDB's _id field
    private String id;              // Spring Data uses String; MongoDB stores as ObjectId

    private String title;

    private Author author;          // nested object — stored as embedded document

    private List<String> genres;   // array field in MongoDB

    private double price;

    private boolean inStock;

    private int publishedYear;

    private String isbn;

    private double rating;

    private int reviewCount;

    @Field("pageCount")            // explicit field name mapping (optional here — same name)
    private int pageCount;

    private LocalDateTime createdAt;

    // ── Constructor, getters, setters ────────────────────────────────────────
    public Book() {}

    public Book(String title, Author author, List<String> genres, double price,
                boolean inStock, int publishedYear, String isbn) {
        this.title = title;
        this.author = author;
        this.genres = genres;
        this.price = price;
        this.inStock = inStock;
        this.publishedYear = publishedYear;
        this.isbn = isbn;
        this.createdAt = LocalDateTime.now();
    }

    // Getters and setters omitted for brevity — use Lombok @Data in real projects
    public String getId() { return id; }
    public String getTitle() { return title; }
    public Author getAuthor() { return author; }
    public List<String> getGenres() { return genres; }
    public double getPrice() { return price; }
    public boolean isInStock() { return inStock; }
    public int getPublishedYear() { return publishedYear; }
    public String getIsbn() { return isbn; }
    public double getRating() { return rating; }
    public int getReviewCount() { return reviewCount; }
    public void setPrice(double price) { this.price = price; }
    public void setInStock(boolean inStock) { this.inStock = inStock; }
    public void setRating(double rating) { this.rating = rating; }
}

// Embedded document — no @Document needed (it's embedded, not a top-level collection)
class Author {
    private String name;
    private String nationality;

    public Author() {}
    public Author(String name, String nationality) {
        this.name = name;
        this.nationality = nationality;
    }
    public String getName() { return name; }
    public String getNationality() { return nationality; }
}

// DTO for aggregation results
class GenreStats {
    private String genre;
    private int bookCount;
    private double avgPrice;
    private double avgRating;

    // Getters
    public String getGenre() { return genre; }
    public int getBookCount() { return bookCount; }
    public double getAvgPrice() { return avgPrice; }
    public double getAvgRating() { return avgRating; }
}

// =============================================================================
// SECTION 3: MongoRepository — CRUD and Derived Query Methods
// =============================================================================
//
// MongoRepository<T, ID>  ←  T = document class, ID = _id type (String for ObjectId)
// Extends CrudRepository — gives you save(), findById(), findAll(), delete(), etc.

interface BookRepository extends MongoRepository<Book, String> {

    // ── Derived query methods — Spring Data generates the implementation ────
    // Method name follows convention: findBy<Field><Condition>

    // SELECT * FROM books WHERE title = ?
    Optional<Book> findByTitle(String title);

    // SELECT * FROM books WHERE inStock = true
    List<Book> findByInStock(boolean inStock);

    // SELECT * FROM books WHERE price < ?
    List<Book> findByPriceLessThan(double maxPrice);

    // SELECT * FROM books WHERE price BETWEEN ? AND ?
    List<Book> findByPriceBetween(double min, double max);

    // SELECT * FROM books WHERE rating >= ?
    List<Book> findByRatingGreaterThanEqual(double minRating);

    // SELECT * FROM books WHERE isbn = ? (useful for unique lookups)
    Optional<Book> findByIsbn(String isbn);

    // SELECT * FROM books WHERE publishedYear = ? ORDER BY title ASC
    List<Book> findByPublishedYearOrderByTitleAsc(int year);

    // SELECT * FROM books WHERE title LIKE '%keyword%' (case-insensitive regex)
    List<Book> findByTitleContainingIgnoreCase(String keyword);

    // SELECT * FROM books WHERE genres contains this value (array membership)
    List<Book> findByGenresContaining(String genre);

    // SELECT * FROM books WHERE inStock = ? AND price < ?
    List<Book> findByInStockAndPriceLessThan(boolean inStock, double maxPrice);

    // SELECT COUNT(*) FROM books WHERE inStock = ?
    long countByInStock(boolean inStock);

    // SELECT * FROM books WHERE author.name = ?  (nested field)
    List<Book> findByAuthorName(String authorName);

    // ── Custom @Query annotation — use MongoDB query syntax ────────────────
    // { } is the filter, parameters are referenced as ?0, ?1, ?2...
    @org.springframework.data.mongodb.repository.Query("{ 'inStock': true, 'price': { $lte: ?0 } }")
    List<Book> findInStockBooksUnderPrice(double maxPrice);

    // Query with projection — return only title and price fields
    @org.springframework.data.mongodb.repository.Query(
            value = "{ 'genres': ?0 }",
            fields = "{ 'title': 1, 'price': 1, 'rating': 1, '_id': 0 }"
    )
    List<Book> findBookSummariesByGenre(String genre);

    // Query using $regex
    @org.springframework.data.mongodb.repository.Query("{ 'title': { $regex: ?0, $options: 'i' } }")
    List<Book> findByTitleRegex(String pattern);

    // ── Spring Data Aggregation shortcut ──────────────────────────────────
    @Aggregation(pipeline = {
            "{ $match: { 'inStock': true } }",
            "{ $group: { _id: null, avgPrice: { $avg: '$price' }, count: { $sum: 1 } } }"
    })
    AggregationResult getInStockStats();
}

// Projection result for aggregation
class AggregationResult {
    private Double avgPrice;
    private Integer count;
    public Double getAvgPrice() { return avgPrice; }
    public Integer getCount() { return count; }
}

// =============================================================================
// SECTION 4: Service Layer Using MongoRepository
// =============================================================================

@Service
class BookService {

    private final BookRepository bookRepository;
    private final MongoTemplate mongoTemplate;

    BookService(BookRepository bookRepository, MongoTemplate mongoTemplate) {
        this.bookRepository = bookRepository;
        this.mongoTemplate = mongoTemplate;
    }

    // ── Basic CRUD ────────────────────────────────────────────────────────────
    public Book createBook(Book book) {
        return bookRepository.save(book);   // INSERT (if no id) or UPSERT (if id exists)
    }

    public Optional<Book> getBookById(String id) {
        return bookRepository.findById(id);  // findById uses the _id field
    }

    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    public Book updateBookPrice(String id, double newPrice) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Book not found: " + id));
        book.setPrice(newPrice);
        return bookRepository.save(book);    // save() on existing doc = UPDATE
    }

    public void deleteBook(String id) {
        bookRepository.deleteById(id);
    }

    // ── Derived query examples ────────────────────────────────────────────────
    public List<Book> getInStockBooks() {
        return bookRepository.findByInStock(true);
    }

    public List<Book> searchByTitle(String keyword) {
        return bookRepository.findByTitleContainingIgnoreCase(keyword);
    }

    public List<Book> getBooksByGenre(String genre) {
        return bookRepository.findByGenresContaining(genre);
    }

    public List<Book> getAffordableInStockBooks(double maxPrice) {
        return bookRepository.findInStockBooksUnderPrice(maxPrice);
    }

    // ── MongoTemplate — more granular control ─────────────────────────────────
    // MongoTemplate gives you direct access to MongoDB's query API from Java

    // Update a specific field without fetching and saving the entire document
    public void incrementReviewCount(String bookId) {
        Query query = new Query(Criteria.where("_id").is(bookId));
        Update update = new Update().inc("reviewCount", 1);  // $inc
        mongoTemplate.updateFirst(query, update, Book.class);
    }

    // Mark all out-of-stock books with a restock date
    public long markForRestock(java.time.LocalDate restockDate) {
        Query query = new Query(Criteria.where("inStock").is(false));
        Update update = new Update().set("restockDate", restockDate);
        return mongoTemplate.updateMulti(query, update, Book.class).getModifiedCount();
    }

    // Complex query using Criteria builder
    public List<Book> findPremiumBooks(double minRating, double maxPrice) {
        Query query = new Query(
                new Criteria().andOperator(
                        Criteria.where("rating").gte(minRating),
                        Criteria.where("price").lte(maxPrice),
                        Criteria.where("inStock").is(true)
                )
        );
        query.with(org.springframework.data.domain.Sort.by(
                org.springframework.data.domain.Sort.Direction.DESC, "rating"
        ));
        return mongoTemplate.find(query, Book.class);
    }

    // Aggregation pipeline via MongoTemplate
    public List<GenreStats> getGenreStatistics() {
        UnwindOperation unwind = Aggregation.unwind("genres");

        GroupOperation group = Aggregation.group("genres")
                .count().as("bookCount")
                .avg("price").as("avgPrice")
                .avg("rating").as("avgRating");

        ProjectionOperation project = Aggregation.project()
                .andExpression("_id").as("genre")
                .andInclude("bookCount", "avgPrice", "avgRating")
                .andExclude("_id");

        SortOperation sort = Aggregation.sort(
                org.springframework.data.domain.Sort.Direction.DESC, "avgRating"
        );

        org.springframework.data.mongodb.core.aggregation.Aggregation pipeline =
                Aggregation.newAggregation(unwind, group, project, sort);

        return mongoTemplate.aggregate(pipeline, "books", GenreStats.class).getMappedResults();
    }
}

// =============================================================================
// SECTION 5: REST Controller
// =============================================================================

@RestController
@RequestMapping("/api/books")
class BookController {

    private final BookService bookService;

    BookController(BookService bookService) {
        this.bookService = bookService;
    }

    // GET /api/books
    @GetMapping
    public List<Book> getAllBooks() {
        return bookService.getAllBooks();
    }

    // GET /api/books/{id}
    @GetMapping("/{id}")
    public Book getBook(@PathVariable String id) {
        return bookService.getBookById(id)
                .orElseThrow(() -> new RuntimeException("Book not found"));
    }

    // POST /api/books
    @PostMapping
    public Book createBook(@RequestBody Book book) {
        return bookService.createBook(book);
    }

    // PUT /api/books/{id}/price?newPrice=39.99
    @PutMapping("/{id}/price")
    public Book updatePrice(@PathVariable String id, @RequestParam double newPrice) {
        return bookService.updateBookPrice(id, newPrice);
    }

    // DELETE /api/books/{id}
    @DeleteMapping("/{id}")
    public void deleteBook(@PathVariable String id) {
        bookService.deleteBook(id);
    }

    // GET /api/books/search?keyword=code
    @GetMapping("/search")
    public List<Book> search(@RequestParam String keyword) {
        return bookService.searchByTitle(keyword);
    }

    // GET /api/books/genre/programming
    @GetMapping("/genre/{genre}")
    public List<Book> byGenre(@PathVariable String genre) {
        return bookService.getBooksByGenre(genre);
    }

    // GET /api/books/in-stock
    @GetMapping("/in-stock")
    public List<Book> inStock() {
        return bookService.getInStockBooks();
    }

    // GET /api/books/affordable?maxPrice=25
    @GetMapping("/affordable")
    public List<Book> affordable(@RequestParam double maxPrice) {
        return bookService.getAffordableInStockBooks(maxPrice);
    }

    // GET /api/books/genre-stats
    @GetMapping("/genre-stats")
    public List<GenreStats> genreStats() {
        return bookService.getGenreStatistics();
    }
}

// =============================================================================
// SECTION 6: Application Entry Point
// =============================================================================

@SpringBootApplication
class BookstoreMongoApplication {
    public static void main(String[] args) {
        SpringApplication.run(BookstoreMongoApplication.class, args);
    }
}

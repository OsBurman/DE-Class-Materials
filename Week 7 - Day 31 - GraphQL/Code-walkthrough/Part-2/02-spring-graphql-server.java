package com.bookstore.graphql;

// =============================================================================
// SPRING FOR GRAPHQL SERVER IMPLEMENTATION — Day 31: GraphQL
// =============================================================================
// This file demonstrates:
//   1. Project setup — Maven dependencies and application.properties
//   2. Schema file location — src/main/resources/graphql/bookstore.graphqls
//   3. Data model — Book, Author, Review, BookInput Java records
//   4. BookController — @QueryMapping, @MutationMapping, @Argument, @SchemaMapping
//   5. AuthorController — @SchemaMapping for nested resolvers
//   6. ReviewController — @SchemaMapping
//   7. SubscriptionController — @SubscriptionMapping with Flux
//   8. DataFetcher concept — the low-level API behind annotations
//   9. Exception handling — DataFetchingException, GraphQlException
//  10. Spring for GraphQL vs GraphQL Java directly — library comparison
//
// pom.xml dependencies (show in class):
//   <dependency>
//     <groupId>org.springframework.boot</groupId>
//     <artifactId>spring-boot-starter-graphql</artifactId>
//   </dependency>
//   <dependency>
//     <groupId>org.springframework.boot</groupId>
//     <artifactId>spring-boot-starter-web</artifactId>  ← for HTTP transport
//   </dependency>
//   <dependency>
//     <groupId>org.springframework.boot</groupId>
//     <artifactId>spring-boot-starter-websocket</artifactId>  ← for Subscriptions
//   </dependency>
//
// application.properties:
//   spring.graphql.graphiql.enabled=true       ← browser-based IDE at /graphiql
//   spring.graphql.schema.locations=classpath:graphql/
//   spring.graphql.schema.file-extensions=.graphqls,.graphql
//   spring.graphql.path=/graphql               ← the single HTTP endpoint
//
// Schema file: src/main/resources/graphql/bookstore.graphqls
//   (see comments throughout this file for the matching schema SDL)
// =============================================================================

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.*;
import org.springframework.graphql.execution.ErrorType;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import graphql.GraphQLException;
import graphql.execution.DataFetcherExceptionHandler;
import graphql.execution.DataFetcherExceptionHandlerParameters;
import graphql.execution.DataFetcherExceptionHandlerResult;
import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

// =============================================================================
// SECTION 1: DATA MODEL — Java records / classes representing our domain
// =============================================================================

/**
 * Book — maps to the `type Book` in bookstore.graphqls.
 * Spring for GraphQL uses reflection to map fields by name.
 * Java records work perfectly because the component names match SDL field names.
 */
record Book(
    String id,
    String title,
    String isbn,
    Integer pageCount,
    Double rating,
    Boolean inStock,
    String genre,       // String representation of Genre enum
    Double price,
    String authorId     // We store the ID; the resolver fetches the Author separately
) {}

/**
 * Author — maps to `type Author` in the schema.
 */
record Author(
    String id,
    String name,
    String email,
    Integer birthYear
) {}

/**
 * Review — maps to `type Review` in the schema.
 */
record Review(
    String id,
    Integer rating,
    String comment,
    String reviewer,
    String createdAt,
    String bookId       // Link back to the book
) {}

/**
 * CreateBookInput — maps to `input CreateBookInput` in the schema.
 * Input types are used for mutation arguments.
 */
record CreateBookInput(
    String title,
    String isbn,
    Integer pageCount,
    String genre,
    Double price,
    String authorId
) {}

/**
 * UpdateBookInput — maps to `input UpdateBookInput` in the schema.
 * All fields are Optional to support partial updates.
 */
record UpdateBookInput(
    String title,
    Integer pageCount,
    Double price,
    Boolean inStock
) {}


// =============================================================================
// SECTION 2: IN-MEMORY REPOSITORIES
// (Simulates a real database — replace with JPA repositories in production)
// =============================================================================

@Repository
class BookRepository {

    private final Map<String, Book> books = new ConcurrentHashMap<>();
    private int nextId = 10;

    public BookRepository() {
        // Seed data
        books.put("1", new Book("1", "Clean Code",           "978-0132350884", 431, 4.9, true,  "TECHNOLOGY", 34.99, "101"));
        books.put("2", new Book("2", "Effective Java",       "978-0134685991", 412, 4.8, true,  "TECHNOLOGY", 44.99, "102"));
        books.put("3", new Book("3", "The Pragmatic Programmer","978-0135957059",352, 4.7, true,  "TECHNOLOGY", 39.99, "103"));
        books.put("4", new Book("4", "Dune",                 "978-0441172719", 896, 4.6, false, "FICTION",    14.99, "104"));
        books.put("5", new Book("5", "1984",                 "978-0451524935", 328, 4.7, true,  "FICTION",    12.99, "105"));
    }

    public List<Book> findAll()                      { return new ArrayList<>(books.values()); }
    public Optional<Book> findById(String id)        { return Optional.ofNullable(books.get(id)); }
    public List<Book> findByAuthorId(String authorId){
        return books.values().stream()
                .filter(b -> authorId.equals(b.authorId()))
                .toList();
    }
    public Book save(Book book) {
        books.put(book.id(), book);
        return book;
    }
    public boolean delete(String id) {
        return books.remove(id) != null;
    }
    public String nextId() { return String.valueOf(nextId++); }
}

@Repository
class AuthorRepository {

    private final Map<String, Author> authors = new ConcurrentHashMap<>();

    public AuthorRepository() {
        authors.put("101", new Author("101", "Robert C. Martin", "uncle.bob@example.com", 1952));
        authors.put("102", new Author("102", "Joshua Bloch",     "jbloch@example.com",    1961));
        authors.put("103", new Author("103", "Dave Thomas",      "dave@example.com",      1956));
        authors.put("104", new Author("104", "Frank Herbert",    null,                    1920));
        authors.put("105", new Author("105", "George Orwell",    null,                    1903));
    }

    public Optional<Author> findById(String id) { return Optional.ofNullable(authors.get(id)); }
    public List<Author> findAll()               { return new ArrayList<>(authors.values()); }
}

@Repository
class ReviewRepository {

    private final List<Review> reviews = new CopyOnWriteArrayList<>();
    private int nextId = 1;

    public ReviewRepository() {
        reviews.add(new Review("1", 5, "Life-changing. Every developer must read.", "alice",   "2024-01-10", "1"));
        reviews.add(new Review("2", 4, "Excellent practical advice.",               "bob",     "2024-02-15", "1"));
        reviews.add(new Review("3", 5, "The best Java book ever written.",          "charlie", "2024-03-01", "2"));
    }

    public List<Review> findByBookId(String bookId) {
        return reviews.stream().filter(r -> bookId.equals(r.bookId())).toList();
    }

    public Review save(Review review) {
        reviews.add(review);
        return review;
    }

    public String nextId() { return String.valueOf(nextId++); }
}


// =============================================================================
// SECTION 3: GRAPHQL JAVA LIBRARIES — Comparison
// =============================================================================

/*
 * GRAPHQL JAVA LIBRARIES OVERVIEW
 * ==================================
 *
 * 1. GraphQL Java (graphql-java)
 *    The foundational library. Low-level. You define DataFetchers manually.
 *    Repository: github.com/graphql-java/graphql-java
 *    Everything else builds on top of it.
 *
 *    DataFetcher<Object> bookFetcher = env -> {
 *        String id = env.getArgument("id");
 *        return bookRepository.findById(id).orElse(null);
 *    };
 *
 *    RuntimeWiring wiring = RuntimeWiring.newRuntimeWiring()
 *        .type("Query", t -> t.dataFetcher("book", bookFetcher))
 *        .build();
 *
 *    Very verbose. Spring for GraphQL wraps this into clean annotations.
 *
 * 2. Spring for GraphQL (spring-graphql)
 *    Official Spring integration. Annotation-driven. Auto-wires resolvers.
 *    Annotation         → Maps to
 *    @QueryMapping      → DataFetcher for Query root type
 *    @MutationMapping   → DataFetcher for Mutation root type
 *    @SubscriptionMapping → DataFetcher for Subscription root type (returns Flux)
 *    @SchemaMapping     → DataFetcher for any type's field
 *    @Argument          → binds argument from DataFetchingEnvironment
 *
 *    Spring for GraphQL also provides:
 *    - GraphQL over HTTP (POST /graphql with JSON body)
 *    - GraphQL over WebSocket (for subscriptions)
 *    - GraphiQL browser IDE (spring.graphql.graphiql.enabled=true)
 *    - Integration with Spring Security
 *    - Integration with Spring Data (QuerydslDataFetcher, etc.)
 *
 * 3. DGS Framework (Netflix DGS)
 *    An alternative annotation-based framework, built by Netflix.
 *    github.com/Netflix/dgs-framework
 *    Richer feature set: code generation, testing utilities.
 *    If you see @DgsComponent, @DgsQuery, @DgsMutation — that's DGS.
 *    The concepts are the same; the annotations differ.
 *
 * For this course: we use Spring for GraphQL — official, well-maintained, integrates
 * with everything we've learned in Spring Boot.
 */


// =============================================================================
// SECTION 4: BOOK CONTROLLER — Resolvers for Query and Mutation
// =============================================================================

/**
 * BookController handles resolvers for:
 *   - Query.books (list all books, with optional filtering)
 *   - Query.book (single book by ID)
 *   - Mutation.createBook
 *   - Mutation.updateBook
 *   - Mutation.deleteBook
 *   - Mutation.addReview (delegated to ReviewController, shown here for completeness)
 *
 * Note: This is a @Controller, NOT @RestController.
 * Spring for GraphQL uses plain @Controller — no @ResponseBody needed.
 * The return values are handled by the GraphQL execution engine, not Spring MVC.
 *
 * Matching schema (bookstore.graphqls):
 *   type Query {
 *     books(genre: Genre, sortBy: SortOrder, limit: Int): [Book!]!
 *     book(id: ID!): Book
 *   }
 *   type Mutation {
 *     createBook(input: CreateBookInput!): Book!
 *     updateBook(id: ID!, input: UpdateBookInput!): Book!
 *     deleteBook(id: ID!): ID!
 *   }
 */
@Controller
class BookController {

    @Autowired
    private BookRepository bookRepository;

    // -------------------------------------------------------------------------
    // 4a. @QueryMapping — resolves Query.books
    // -------------------------------------------------------------------------
    /**
     * @QueryMapping maps this method to the `books` field on the Query type.
     * Method name must match the schema field name (or use @QueryMapping("books") explicitly).
     *
     * @Argument injects a named argument from the GraphQL query.
     * If the client sends books(genre: TECHNOLOGY), Spring binds it to the genre parameter.
     *
     * The method is the DataFetcher — it's called by the GraphQL execution engine
     * when a query requests the `books` field.
     */
    @QueryMapping
    public List<Book> books(
            @Argument String genre,      // optional — null if not provided
            @Argument String sortBy,     // optional
            @Argument Integer limit      // optional
    ) {
        List<Book> result = bookRepository.findAll();

        // Apply genre filter if provided
        if (genre != null) {
            result = result.stream()
                    .filter(b -> genre.equals(b.genre()))
                    .toList();
        }

        // Apply sorting if provided
        if ("RATING_DESC".equals(sortBy)) {
            result = result.stream()
                    .sorted(Comparator.comparingDouble(Book::rating).reversed())
                    .toList();
        } else if ("TITLE_ASC".equals(sortBy)) {
            result = result.stream()
                    .sorted(Comparator.comparing(Book::title))
                    .toList();
        }

        // Apply limit if provided
        if (limit != null) {
            result = result.stream().limit(limit).toList();
        }

        return result;
    }

    // -------------------------------------------------------------------------
    // 4b. @QueryMapping — resolves Query.book(id: ID!)
    // -------------------------------------------------------------------------
    /**
     * Returns null if the book doesn't exist — this is valid because the schema
     * defines book(id: ID!): Book (nullable return type, no !)
     */
    @QueryMapping
    public Book book(@Argument String id) {
        return bookRepository.findById(id).orElse(null);
        // Returning null tells GraphQL to put null in the response for this field
        // The errors array will NOT have an entry (null is valid for a nullable field)
    }

    // -------------------------------------------------------------------------
    // 4c. @MutationMapping — resolves Mutation.createBook
    // -------------------------------------------------------------------------
    /**
     * @MutationMapping maps to the `createBook` field on the Mutation type.
     * @Argument("input") binds the entire input object to a Java record.
     * Spring for GraphQL auto-converts the input map to CreateBookInput.
     */
    @MutationMapping
    public Book createBook(@Argument CreateBookInput input) {
        String newId = bookRepository.nextId();
        Book newBook = new Book(
                newId,
                input.title(),
                input.isbn(),
                input.pageCount(),
                null,              // new books have no rating yet
                true,              // new books are in stock by default
                input.genre(),
                input.price(),
                input.authorId()
        );
        Book saved = bookRepository.save(newBook);

        // Publish to subscription listeners (see SubscriptionController below)
        BookEventPublisher.publishNewBook(saved);

        return saved;
    }

    // -------------------------------------------------------------------------
    // 4d. @MutationMapping — resolves Mutation.updateBook
    // -------------------------------------------------------------------------
    @MutationMapping
    public Book updateBook(@Argument String id, @Argument UpdateBookInput input) {
        Book existing = bookRepository.findById(id)
                .orElseThrow(() -> new BookNotFoundException(id));

        // Apply partial update — only update non-null input fields
        Book updated = new Book(
                existing.id(),
                input.title()     != null ? input.title()     : existing.title(),
                existing.isbn(),
                input.pageCount() != null ? input.pageCount() : existing.pageCount(),
                existing.rating(),
                input.inStock()   != null ? input.inStock()   : existing.inStock(),
                existing.genre(),
                input.price()     != null ? input.price()     : existing.price(),
                existing.authorId()
        );

        return bookRepository.save(updated);
    }

    // -------------------------------------------------------------------------
    // 4e. @MutationMapping — resolves Mutation.deleteBook
    // -------------------------------------------------------------------------
    /**
     * Returns the ID of the deleted book (ID! in schema — non-null).
     * Throw an exception if the book doesn't exist.
     */
    @MutationMapping
    public String deleteBook(@Argument String id) {
        boolean deleted = bookRepository.delete(id);
        if (!deleted) {
            throw new BookNotFoundException(id);
        }
        return id;
    }
}


// =============================================================================
// SECTION 5: AUTHOR CONTROLLER — @SchemaMapping for Nested Resolvers
// =============================================================================

/**
 * @SchemaMapping resolves a field on a SPECIFIC TYPE — not a root type.
 *
 * The Book type has an `author` field. But Book.java only stores authorId (a String).
 * The author field needs to be RESOLVED — fetch the Author from the Author repository
 * given the Book's authorId.
 *
 * @SchemaMapping(typeName = "Book", field = "author")
 * says: "when the GraphQL engine needs to resolve the `author` field on a `Book`,
 *        call this method and pass me the parent Book object."
 *
 * This is how nested resolvers work — each field in your schema can have its own resolver
 * function that knows how to fetch that specific piece of data.
 *
 * Schema:
 *   type Book {
 *     ...
 *     author: Author!    ← this field needs a resolver
 *     reviews: [Review!]! ← this field needs a resolver
 *   }
 */
@Controller
class AuthorController {

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private BookRepository bookRepository;

    // -------------------------------------------------------------------------
    // 5a. Nested resolver — Book.author
    // -------------------------------------------------------------------------
    /**
     * When a query requests book { author { name } }, the engine:
     *   1. Calls BookController.book() to get the Book object
     *   2. Sees the query needs `author`
     *   3. Calls THIS method, passing the Book as the first parameter
     *   4. Method returns the Author for that book
     *
     * The Book parameter is the "parent" — the object the field belongs to.
     * Spring automatically injects it.
     */
    @SchemaMapping(typeName = "Book", field = "author")
    public Author bookAuthor(Book book) {
        return authorRepository.findById(book.authorId())
                .orElseThrow(() -> new RuntimeException("Author not found: " + book.authorId()));
    }

    // -------------------------------------------------------------------------
    // 5b. Query.author — get an author by ID
    // -------------------------------------------------------------------------
    @QueryMapping
    public Author author(@Argument String id) {
        return authorRepository.findById(id).orElse(null);
    }

    // -------------------------------------------------------------------------
    // 5c. Author.books — get all books by an author (nested in Author queries)
    // -------------------------------------------------------------------------
    /**
     * When a query requests author { books { title } }, this resolver:
     *   1. Receives the Author object (parent)
     *   2. Queries BookRepository for all books with that author's ID
     *   3. Returns the list
     *
     * Schema:
     *   type Author {
     *     ...
     *     books: [Book!]!   ← resolved here
     *   }
     */
    @SchemaMapping(typeName = "Author", field = "books")
    public List<Book> authorBooks(Author author) {
        return bookRepository.findByAuthorId(author.id());
    }
}


// =============================================================================
// SECTION 6: REVIEW CONTROLLER — Resolvers for Reviews
// =============================================================================

@Controller
class ReviewController {

    @Autowired
    private ReviewRepository reviewRepository;

    // -------------------------------------------------------------------------
    // 6a. Book.reviews — nested resolver
    // -------------------------------------------------------------------------
    /**
     * Resolves the `reviews` field on a Book.
     * Called for every Book in the result that has reviews requested.
     *
     * ⚠️ N+1 PROBLEM WARNING:
     * If the query returns 100 books with reviews, this method is called 100 times —
     * one per book. That's 100 separate database queries (N+1).
     *
     * Solution: DataLoader (covered in 03-schema-design-best-practices.md).
     * DataLoader batches all the review lookups into a single query:
     *   SELECT * FROM reviews WHERE book_id IN (1, 2, 3, ... 100)
     * This reduces 100 queries to 1.
     *
     * For today: we demonstrate the concept. DataLoader is Day 32 territory.
     */
    @SchemaMapping(typeName = "Book", field = "reviews")
    public List<Review> bookReviews(Book book) {
        return reviewRepository.findByBookId(book.id());
    }

    // -------------------------------------------------------------------------
    // 6b. Mutation.addReview
    // -------------------------------------------------------------------------
    @MutationMapping
    public Review addReview(
            @Argument String bookId,
            @Argument Integer rating,
            @Argument String comment) {

        // Validate rating range
        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }

        Review newReview = new Review(
                reviewRepository.nextId(),
                rating,
                comment,
                "anonymous",                        // in real app: get from SecurityContext
                java.time.Instant.now().toString(),
                bookId
        );

        Review saved = reviewRepository.save(newReview);

        // Publish to subscription listeners
        BookEventPublisher.publishNewReview(saved);

        return saved;
    }
}


// =============================================================================
// SECTION 7: SUBSCRIPTION CONTROLLER — Real-Time with Flux
// =============================================================================

/**
 * Subscriptions in Spring for GraphQL use Project Reactor's Flux.
 * A Flux is a reactive stream that emits zero or more items over time.
 *
 * When a client subscribes, the resolver returns a Flux.
 * Spring for GraphQL sends each item the Flux emits to the client
 * over the WebSocket connection.
 *
 * Transport: WebSocket at the same /graphql endpoint.
 * Protocol: graphql-ws (newer) or subscriptions-transport-ws (legacy)
 *
 * Schema:
 *   type Subscription {
 *     bookAdded: Book!
 *     bookStockChanged(bookId: ID!): Book!
 *     newReview(bookId: ID!): Review!
 *   }
 */
@Controller
class BookSubscriptionController {

    // -------------------------------------------------------------------------
    // 7a. Subscription.bookAdded
    // -------------------------------------------------------------------------
    @SubscriptionMapping
    public Flux<Book> bookAdded() {
        // Returns a Flux that emits whenever a new book is created.
        // BookEventPublisher.newBookFlux() is a Flux backed by a Processor/Sink
        // that we publish to in BookController.createBook().
        return BookEventPublisher.newBookFlux();
    }

    // -------------------------------------------------------------------------
    // 7b. Subscription.bookStockChanged
    // -------------------------------------------------------------------------
    @SubscriptionMapping
    public Flux<Book> bookStockChanged(@Argument String bookId) {
        // Filter the stream — only emit events for the specific book
        return BookEventPublisher.stockChangeFlux()
                .filter(book -> bookId.equals(book.id()));
    }

    // -------------------------------------------------------------------------
    // 7c. Subscription.newReview
    // -------------------------------------------------------------------------
    @SubscriptionMapping
    public Flux<Review> newReview(@Argument String bookId) {
        return BookEventPublisher.newReviewFlux()
                .filter(review -> bookId.equals(review.bookId()));
    }
}

/**
 * BookEventPublisher — manages the event streams for subscriptions.
 * In production, you'd use a proper event bus (Spring ApplicationEvent,
 * Redis pub/sub, or Kafka). Here we use simple reactive Sinks.
 */
class BookEventPublisher {

    // reactor.core.publisher.Sinks — hot publishers (emit regardless of subscribers)
    private static final reactor.core.publisher.Sinks.Many<Book>   newBookSink    =
            reactor.core.publisher.Sinks.many().multicast().onBackpressureBuffer();
    private static final reactor.core.publisher.Sinks.Many<Book>   stockChangeSink =
            reactor.core.publisher.Sinks.many().multicast().onBackpressureBuffer();
    private static final reactor.core.publisher.Sinks.Many<Review> newReviewSink  =
            reactor.core.publisher.Sinks.many().multicast().onBackpressureBuffer();

    public static void publishNewBook(Book book)       { newBookSink.tryEmitNext(book); }
    public static void publishStockChange(Book book)   { stockChangeSink.tryEmitNext(book); }
    public static void publishNewReview(Review review) { newReviewSink.tryEmitNext(review); }

    public static Flux<Book>   newBookFlux()     { return newBookSink.asFlux(); }
    public static Flux<Book>   stockChangeFlux() { return stockChangeSink.asFlux(); }
    public static Flux<Review> newReviewFlux()   { return newReviewSink.asFlux(); }
}


// =============================================================================
// SECTION 8: THE RAW DataFetcher API (What Annotations Replace)
// =============================================================================

/**
 * For completeness — here's what @QueryMapping is replacing under the hood.
 * This is GraphQL Java's raw DataFetcher API.
 * You would only write DataFetchers directly if you need low-level control
 * that the annotations don't support.
 *
 * This class is INFORMATIONAL — not wired into Spring context.
 */
class DataFetcherExample {

    /**
     * A DataFetcher<T> is a functional interface with one method:
     *   T get(DataFetchingEnvironment environment)
     *
     * DataFetchingEnvironment gives you:
     *   - getArgument("name") — get an argument value
     *   - getSource()          — the parent object (for nested fields)
     *   - getContext()         — request context (auth info etc.)
     *   - getSelectionSet()    — what fields were actually requested (for optimization)
     */
    static final DataFetcher<Book> bookDataFetcher = (DataFetchingEnvironment env) -> {
        String id = env.getArgument("id");
        // return bookRepository.findById(id).orElse(null);
        return null; // placeholder
    };

    static final DataFetcher<Author> bookAuthorFetcher = (DataFetchingEnvironment env) -> {
        Book parentBook = env.getSource(); // the Book object from the parent resolver
        String authorId = parentBook.authorId();
        // return authorRepository.findById(authorId).orElse(null);
        return null; // placeholder
    };

    /**
     * In a RuntimeWiring configuration, you'd register these:
     *
     * RuntimeWiring wiring = RuntimeWiring.newRuntimeWiring()
     *     .type("Query", builder -> builder
     *         .dataFetcher("book",  bookDataFetcher)
     *         .dataFetcher("books", booksDataFetcher))
     *     .type("Book", builder -> builder
     *         .dataFetcher("author",  bookAuthorFetcher)
     *         .dataFetcher("reviews", bookReviewsFetcher))
     *     .build();
     *
     * Spring for GraphQL does all of this automatically from @SchemaMapping annotations.
     */
}


// =============================================================================
// SECTION 9: EXCEPTION HANDLING
// =============================================================================

/**
 * Custom exception — automatically converted to a GraphQL error.
 */
class BookNotFoundException extends RuntimeException {
    private final String bookId;

    public BookNotFoundException(String bookId) {
        super("Book with id '" + bookId + "' not found");
        this.bookId = bookId;
    }

    public String getBookId() { return bookId; }
}

/**
 * Spring for GraphQL converts exceptions to GraphQL errors automatically.
 * The error appears in the response "errors" array with code NOT_FOUND.
 *
 * To customize error classification, implement DataFetcherExceptionResolverAdapter:
 *
 * @Component
 * class BookstoreExceptionResolver extends DataFetcherExceptionResolverAdapter {
 *
 *     @Override
 *     protected GraphQLError resolveToSingleError(Throwable ex, DataFetchingEnvironment env) {
 *         if (ex instanceof BookNotFoundException) {
 *             return GraphqlErrorBuilder.newError(env)
 *                 .errorType(ErrorType.NOT_FOUND)
 *                 .message(ex.getMessage())
 *                 .extensions(Map.of("bookId", ((BookNotFoundException)ex).getBookId()))
 *                 .build();
 *         }
 *         return null; // let other resolvers handle it
 *     }
 * }
 *
 * Response when book not found:
 * {
 *   "data": { "book": null },
 *   "errors": [{
 *     "message": "Book with id '999' not found",
 *     "path": ["book"],
 *     "extensions": { "classification": "NOT_FOUND", "bookId": "999" }
 *   }]
 * }
 */

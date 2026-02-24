# Day 31 Review — GraphQL
## Quick Reference Guide

---

## 1. REST Limitations That Led to GraphQL

| Problem | Example | GraphQL Solution |
|---------|---------|-----------------|
| **Over-fetching** | `GET /books` returns 50 fields; client needs 3 | Query exactly the fields you need |
| **Under-fetching (N+1 requests)** | `GET /books/1` → then `GET /authors/5` → then `GET /books/1/reviews` | One query fetches nested data |
| **Versioning explosion** | `/api/v1/`, `/api/v2/`, `/api/v3/` | Schema evolves with `@deprecated` |
| **Fixed response shapes** | Server decides what's returned | Client drives the shape |
| **No self-documentation** | Requires separate API docs (Swagger) | Introspection built in |

---

## 2. GraphQL Core Concepts

| Concept | Definition |
|---------|-----------|
| **Schema** | Contract between client and server — defines all types and operations |
| **SDL** | Schema Definition Language — declarative syntax for defining types |
| **Type System** | Strongly typed — every field has a declared type |
| **Single endpoint** | All operations go to `POST /graphql` (Spring default) |
| **Query** | Read operation |
| **Mutation** | Write operation (create / update / delete) |
| **Subscription** | Real-time data over WebSocket |
| **Resolver** | Function that fetches data for a field |

---

## 3. GraphQL vs REST

| Aspect | REST | GraphQL |
|--------|------|---------|
| Endpoints | Multiple (`/books`, `/authors`, `/orders`) | Single (`/graphql`) |
| HTTP Method | GET, POST, PUT, DELETE | POST (all operations) |
| Response shape | Fixed by server | Defined by client query |
| Over-fetching | Common | Eliminated |
| Under-fetching | Common (requires multiple requests) | Eliminated (nested queries) |
| Versioning | `/v1/`, `/v2/` | `@deprecated` directive + additive changes |
| Caching | HTTP caching (ETag, Cache-Control) | Requires Apollo/client-side cache |
| Self-documenting | Requires Swagger / OpenAPI | Introspection built in |
| Best for | Simple CRUD, public APIs | Complex data graphs, diverse clients |

---

## 4. GraphQL Request Architecture

```
Client (React / Angular / Postman)
    │
    │  POST /graphql
    │  Content-Type: application/json
    │  { "query": "{ books { id title } }" }
    ↓
DispatcherServlet (Spring MVC)
    ↓
GraphQlHttpHandler (Spring for GraphQL)
    ↓
ExecutionEngine (graphql-java)
    │  Parses query → validates against schema → calls resolvers
    ↓
@QueryMapping / @SchemaMapping / @MutationMapping  (your resolvers)
    ↓
Service → Repository → Database
    ↓
ExecutionResult assembled from all field resolvers
    ↓
JSON response: { "data": {...} }
```

---

## 5. SDL — Schema Definition Language Syntax

The schema is defined in a `.graphqls` file (not Java code). Spring loads it automatically from `src/main/resources/graphql/`.

```graphql
# Scalar types
String, Int, Float, Boolean, ID

# Object type
type Book {
    id: ID!
    title: String!
    price: Float!
    category: String
    author: Author!
    reviews: [Review!]!
}

# Input type (used as argument to mutations)
input CreateBookInput {
    title: String!
    authorId: ID!
    price: Float!
    category: String
}

# Root types — entry points for operations
type Query {
    book(id: ID!): Book
    books(category: String): [Book!]!
    author(id: ID!): Author
}

type Mutation {
    createBook(input: CreateBookInput!): Book!
    updateBook(id: ID!, input: CreateBookInput!): Book!
    deleteBook(id: ID!): Boolean!
}

type Subscription {
    bookAdded: Book!
}
```

---

## 6. Scalar Types Reference

| Type | Java | Notes |
|------|------|-------|
| `String` | `String` | |
| `Int` | `int` / `Integer` | 32-bit signed integer |
| `Float` | `double` / `Double` | Double-precision float |
| `Boolean` | `boolean` / `Boolean` | |
| `ID` | `String` or `Long` | Opaque identifier; serialized as String |
| Custom scalars | e.g., `scalar DateTime` | Requires resolver bean |

Custom scalar example:
```graphql
scalar DateTime
scalar BigDecimal
```
```java
@Bean
public RuntimeWiringConfigurer runtimeWiringConfigurer() {
    return wiringBuilder -> wiringBuilder
        .scalar(ExtendedScalars.DateTime)
        .scalar(ExtendedScalars.GraphQLBigDecimal);
}
```

---

## 7. Non-Null and List Modifiers

| SDL Syntax | Java Return Type | Meaning |
|-----------|-----------------|---------|
| `String` | `String` (nullable) | Field may be null |
| `String!` | `String` (non-null) | Field is never null |
| `[Book]` | `List<Book>` | List may be null; elements may be null |
| `[Book!]` | `List<Book>` (non-null elements) | List may be null; elements are never null |
| `[Book!]!` | `List<Book>` (never null, non-null elements) | Neither list nor elements are null |
| `[Book]!` | `List<Book>` (never null) | List is never null; elements may be null |

**Rule:** If your Java method returns `null` for a `!` field, graphql-java will propagate a null error up the tree.

---

## 8. Object Type Definition

```graphql
type Author {
    id: ID!
    firstName: String!
    lastName: String!
    biography: String
    books: [Book!]!
}

type Review {
    id: ID!
    rating: Int!
    text: String
    reviewer: String!
    book: Book!
}

type Query {
    book(id: ID!): Book         # nullable — returns null if not found
    books: [Book!]!             # never null, elements never null
    booksByCategory(category: String!): [Book!]!
}
```

---

## 9. GraphQL Query Syntax

### Basic Query
```graphql
query {
    books {
        id
        title
        price
    }
}
```

### Named Query (best practice — easier to debug)
```graphql
query GetAllBooks {
    books {
        id
        title
        price
        author {
            firstName
            lastName
        }
    }
}
```

### Nested Query
```graphql
query GetBookWithReviews {
    book(id: "1") {
        title
        reviews {
            rating
            text
            reviewer
        }
    }
}
```

---

## 10. Arguments

```graphql
# In the schema
type Query {
    books(category: String, maxPrice: Float, first: Int, after: String): [Book!]!
    book(id: ID!): Book
}

# In the query
query {
    books(category: "Java", maxPrice: 49.99) {
        id
        title
        price
    }
}
```

---

## 11. Variables — Parameterize Queries

```graphql
# Query definition with variable declarations
query GetBooksByCategory($category: String!, $limit: Int) {
    books(category: $category, first: $limit) {
        id
        title
    }
}
```

```json
// Sent in the request body alongside the query
{
    "query": "query GetBooksByCategory($category: String!, $limit: Int) { books(category: $category, first: $limit) { id title } }",
    "variables": {
        "category": "Java",
        "limit": 10
    }
}
```

**Always use variables** instead of string interpolation in client code — prevents injection attacks.

---

## 12. Mutations — Write Operations

```graphql
mutation CreateBook($input: CreateBookInput!) {
    createBook(input: $input) {
        id
        title
        price
        author {
            firstName
            lastName
        }
    }
}
```

```json
{
    "variables": {
        "input": {
            "title": "Effective Java",
            "authorId": "5",
            "price": 49.99,
            "category": "Java"
        }
    }
}
```

**Mutation response:** GraphQL mutations should return the modified/created object (not just a success boolean) so clients can update their cache.

---

## 13. Subscriptions — Schema Definition

```graphql
type Subscription {
    bookAdded: Book!
    priceChanged(bookId: ID!): PriceUpdate!
    orderStatusChanged(orderId: ID!): Order!
}

type PriceUpdate {
    book: Book!
    oldPrice: Float!
    newPrice: Float!
    changedAt: String!
}
```

Subscriptions use **WebSocket** (built into Spring for GraphQL via `graphql-transport-ws` or `subscriptions-transport-ws` protocol).

---

## 14. Introspection — Self-Documentation

```graphql
# List all types
{ __schema { types { name } } }

# Inspect a specific type
{
    __type(name: "Book") {
        name
        fields {
            name
            type { name kind }
            description
        }
    }
}

# Get entire schema
{ __schema { queryType { name } types { name fields { name } } } }
```

- GraphiQL and Apollo Sandbox use introspection to build their query editors
- **Disable in production** to reduce attack surface: `spring.graphql.schema.introspection.enabled=false`

---

## 15. GraphQL Error Format

```json
// Partial success — data AND errors can coexist
{
    "data": {
        "books": [...],
        "missingBook": null
    },
    "errors": [
        {
            "message": "Book not found: 999",
            "locations": [{"line": 3, "column": 5}],
            "path": ["missingBook"],
            "extensions": {
                "classification": "NOT_FOUND",
                "code": "BOOK_NOT_FOUND"
            }
        }
    ]
}
```

**Key difference from REST:** A GraphQL response can include both `data` and `errors`. Partial success is valid — successfully resolved fields are returned alongside errors for failed fields.

---

## 16. Aliases — Request Two Fields of Same Type

```graphql
query {
    cheapBooks: books(maxPrice: 20.00) {
        id
        title
        price
    }
    expensiveBooks: books(minPrice: 50.00) {
        id
        title
        price
    }
}
```

Without aliases, two `books` fields in one query would conflict. Aliases (`cheapBooks`, `expensiveBooks`) map the results to different keys in the response.

---

## 17. Fragments — Reuse Field Sets

```graphql
fragment BookDetails on Book {
    id
    title
    price
    category
}

fragment AuthorDetails on Author {
    id
    firstName
    lastName
}

query GetBooksWithAuthors {
    books {
        ...BookDetails
        author {
            ...AuthorDetails
        }
    }
}

query GetBook {
    book(id: "1") {
        ...BookDetails
        reviews {
            rating
            text
        }
    }
}
```

---

## 18. Directives

```graphql
query GetBook($id: ID!, $withReviews: Boolean!, $skipPrice: Boolean!) {
    book(id: $id) {
        title
        price @skip(if: $skipPrice)                  # omit price if skipPrice = true
        reviews @include(if: $withReviews) {          # include reviews if withReviews = true
            rating
            text
        }
        status @deprecated(reason: "Use bookStatus field instead")
    }
}
```

| Directive | Purpose |
|-----------|---------|
| `@skip(if: Boolean!)` | Omit the field when condition is true |
| `@include(if: Boolean!)` | Include the field when condition is true |
| `@deprecated(reason: "...")` | Mark field as deprecated in schema |

---

## 19. Spring for GraphQL Dependency

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-graphql</artifactId>
</dependency>
<!-- spring-boot-starter-web included automatically with graphql starter in Boot 3 -->
```

**Auto-configuration provides:**
- GraphQL endpoint at `POST /graphql`
- GraphiQL playground at `GET /graphiql` (enable in properties)
- Schema file scanning from `classpath:graphql/**/*.graphqls`
- `GraphQlSource` bean with the parsed schema
- Exception resolving via `@GraphQlExceptionHandler`

---

## 20. Schema File Location and Properties

```
src/main/resources/
    graphql/
        schema.graphqls          ← main schema file
        books.graphqls           ← can split across multiple files
        authors.graphqls
```

```properties
# application.properties
spring.graphql.graphiql.enabled=true        # GraphiQL browser UI at /graphiql
spring.graphql.schema.introspection.enabled=false   # disable in production
spring.graphql.path=/graphql                # default endpoint path
spring.graphql.websocket.path=/graphql      # WebSocket path for subscriptions
```

---

## 21. @QueryMapping

Maps a method to a field on the root `Query` type. Method name must match the schema field name (or override with `@QueryMapping(name = "...")`).

```java
@Controller
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @QueryMapping
    public List<Book> books() {
        return bookService.findAll();
    }

    @QueryMapping
    public Optional<Book> book(@Argument Long id) {
        return bookService.findById(id);
    }

    @QueryMapping("booksByCategory")
    public List<Book> getByCategory(@Argument String category) {
        return bookService.findByCategory(category);
    }
}
```

---

## 22. @MutationMapping

Maps a method to a field on the root `Mutation` type.

```java
@Controller
public class BookMutationController {

    private final BookService bookService;

    @MutationMapping
    public Book createBook(@Argument CreateBookInput input) {
        return bookService.createBook(input);
    }

    @MutationMapping
    public Book updateBook(@Argument Long id, @Argument CreateBookInput input) {
        return bookService.updateBook(id, input);
    }

    @MutationMapping
    public boolean deleteBook(@Argument Long id) {
        bookService.deleteBook(id);
        return true;
    }
}
```

---

## 23. @SchemaMapping

Maps a method to a non-root type field — resolves a field on an object that is itself a GraphQL type.

```java
// Resolves Book.author
@SchemaMapping(typeName = "Book", field = "author")
public Author author(Book book) {    // book = the parent object
    return authorService.findById(book.getAuthorId());
}

// Shorthand — method name IS the field name
@SchemaMapping(typeName = "Book")
public List<Review> reviews(Book book) {
    return reviewService.findByBookId(book.getId());
}
```

**When to use @SchemaMapping:** When the parent object (Book) doesn't already have the nested data — the resolver fetches it on demand. This is the pattern that causes N+1 without `@BatchMapping`.

---

## 24. @SubscriptionMapping

```java
@Controller
public class BookSubscriptionController {

    private final BookEventPublisher publisher;

    @SubscriptionMapping
    public Flux<Book> bookAdded() {
        return publisher.getBookFlux();      // Flux<T> — Project Reactor
    }
}

// Publisher:
@Component
public class BookEventPublisher {
    private final Sinks.Many<Book> sink = Sinks.many().multicast().onBackpressureBuffer();

    public void publishBookAdded(Book book) {
        sink.tryEmitNext(book);
    }

    public Flux<Book> getBookFlux() {
        return sink.asFlux();
    }
}
```

---

## 25. @BatchMapping — N+1 Solution Preview

N+1 problem with `@SchemaMapping`:
```java
// For 50 books, this fires 50 separate queries:
@SchemaMapping(typeName = "Book")
public Author author(Book book) {
    return authorService.findById(book.getAuthorId());  // 1 query per book!
}
```

N+1 solution with `@BatchMapping`:
```java
// Called ONCE with all books; returns a Map of book → author
@BatchMapping(typeName = "Book")
public Map<Book, Author> author(List<Book> books) {
    List<Long> authorIds = books.stream()
        .map(Book::getAuthorId)
        .distinct()
        .collect(Collectors.toList());

    Map<Long, Author> authorMap = authorService.findAllById(authorIds)
        .stream().collect(Collectors.toMap(Author::getId, a -> a));

    return books.stream()
        .collect(Collectors.toMap(
            book -> book,
            book -> authorMap.get(book.getAuthorId())
        ));
}
```

---

## 26. @Argument Reference

Binds a GraphQL argument to a Java method parameter. Supports automatic type conversion.

```java
// Simple scalar
@QueryMapping
public List<Book> books(@Argument String category, @Argument Float maxPrice) { ... }

// Input type — auto-mapped to Java class by field name
@MutationMapping
public Book createBook(@Argument CreateBookInput input) { ... }

// Corresponding Java class
public record CreateBookInput(String title, Long authorId, BigDecimal price, String category) {}
// OR
public class CreateBookInput {
    private String title;
    private Long authorId;
    private BigDecimal price;
    private String category;
    // getters / setters / no-arg constructor
}
```

---

## 27. Schema Design Best Practices

| Practice | Reasoning |
|---------|-----------|
| Use `input` types for mutations | Separates query return shapes from mutation arguments |
| Return the modified object from mutations | Enables client cache update |
| Non-null `!` by default for required fields | Explicit nullability intent |
| Use semantic types over generic strings | `scalar DateTime` over `String` for timestamps |
| Pagination via Cursor-based Connection pattern | More efficient than offset for large datasets |
| Add `description` strings to types and fields | Shown in introspection / GraphiQL |
| Never break existing fields | Add new fields; deprecate old ones |
| Keep queries cohesive | Design types that model the domain, not the DB tables |

---

## 28. Pagination — Connection Pattern

The industry standard (Relay spec) for cursor-based pagination:

```graphql
type BookConnection {
    edges: [BookEdge!]!
    pageInfo: PageInfo!
}

type BookEdge {
    node: Book!
    cursor: String!
}

type PageInfo {
    hasNextPage: Boolean!
    hasPreviousPage: Boolean!
    startCursor: String
    endCursor: String
}

type Query {
    books(first: Int, after: String): BookConnection!
}
```

```graphql
query {
    books(first: 10, after: "cursor123") {
        edges {
            node { id title }
            cursor
        }
        pageInfo {
            hasNextPage
            endCursor
        }
    }
}
```

---

## 29. Error Handling Patterns

### Method 1: Return null (field-level failure)
```java
@QueryMapping
public Book book(@Argument Long id) {
    return bookService.findById(id).orElse(null);
    // null → field is null in response + no error entry
}
```

### Method 2: Throw exception (error in errors array)
```java
@QueryMapping
public Book book(@Argument Long id) {
    return bookService.findById(id)
        .orElseThrow(() -> new BookNotFoundException("Book not found: " + id));
}
```

### Method 3: @GraphQlExceptionHandler
```java
@ControllerAdvice
public class GraphQlExceptionHandlers {

    @GraphQlExceptionHandler(BookNotFoundException.class)
    public GraphQlErrorBuilder<?> handleNotFound(BookNotFoundException ex) {
        return GraphqlErrorBuilder.newError()
            .errorType(ErrorType.NOT_FOUND)
            .message(ex.getMessage())
            .build();
    }
}
```

| Exception | Error Type |
|-----------|-----------|
| `BookNotFoundException` | `NOT_FOUND` |
| `AccessDeniedException` | `FORBIDDEN` |
| `ConstraintViolationException` | `BAD_USER_INPUT` |

---

## 30. Field Deprecation in Schema

```graphql
type Book {
    id: ID!
    title: String!
    author: Author!

    # Old field — will be removed
    authorName: String @deprecated(reason: "Use author.firstName + author.lastName")

    # Replacement
    authorFullName: String
}
```

GraphiQL shows deprecated fields with strikethrough. Clients receive a warning but the field still works. This is the GraphQL approach to versioning — never remove fields immediately; deprecate and add replacements.

---

## 31. SDL Descriptions (Documentation in Schema)

```graphql
"""
Represents a book in the catalog.
Books have authors and can have multiple reviews.
"""
type Book {
    "Unique identifier for the book"
    id: ID!

    "Full title of the book"
    title: String!

    """
    Price in USD.
    Must be greater than 0.00.
    """
    price: Float!
}

"""
Represents operations that read data.
All queries are available to authenticated users.
"""
type Query {
    """
    Retrieve all books, optionally filtered by category.
    Returns empty list if no books match.
    """
    books(
        "Filter by book category"
        category: String
    ): [Book!]!
}
```

Triple-quoted (`"""`) or single-quoted (`"`) strings become documentation in introspection and GraphiQL.

---

## 32. Common Mistakes — Quick Reference

| Mistake | Symptom | Fix |
|---------|---------|-----|
| Schema field name ≠ method name | `Field 'books' is not resolved` | Match method name to schema field, or use `@QueryMapping(name="...")` |
| Returning wrong Java type from resolver | ClassCastException or mapping error | Ensure return type matches schema type |
| N+1 queries from `@SchemaMapping` | Hundreds of SQL queries for one GraphQL request | Use `@BatchMapping` |
| Missing `@Argument` on method parameter | Parameter is always null | Add `@Argument` to every GraphQL argument parameter |
| Forgetting `input` type for mutations | Schema validation error | Use `input CreateBookInput` not `type CreateBookInput` |
| Returning entity directly (exposes all fields) | Security — internal fields exposed | Return DTO from resolvers |
| `@Controller` not `@RestController` | Method works but wrong annotation style | GraphQL controllers use `@Controller` (not `@RestController`) |
| Introspection enabled in production | Schema exposed to attackers | `spring.graphql.schema.introspection.enabled=false` |

---

## 33. DataFetchingEnvironment — Low-Level Access

```java
@QueryMapping
public List<Book> books(DataFetchingEnvironment env) {
    // Access requested fields — for optimization (only query needed DB columns)
    Set<String> requestedFields = env.getSelectionSet().getFields()
        .stream().map(SelectedField::getName)
        .collect(Collectors.toSet());

    // Access the full execution context
    GraphQLContext context = env.getGraphQlContext();

    // Access raw arguments
    Map<String, Object> args = env.getArguments();

    return bookService.findAll(requestedFields);
}
```

Most resolvers don't need `DataFetchingEnvironment` directly — use `@Argument`, `@AuthenticationPrincipal`, etc. instead. Use `DataFetchingEnvironment` when you need to inspect the query structure or access context.

---

## 34. @GraphQlExceptionHandler — Full Pattern

```java
@ControllerAdvice
public class GraphQlExceptionHandlers {

    @GraphQlExceptionHandler(ResourceNotFoundException.class)
    public GraphQlError handleNotFound(ResourceNotFoundException ex,
                                       ErrorHandlerMethodArgumentResolver resolver) {
        return GraphqlErrorBuilder.newError()
            .errorType(ErrorType.NOT_FOUND)
            .message(ex.getMessage())
            .extensions(Map.of("code", "RESOURCE_NOT_FOUND"))
            .build();
    }

    @GraphQlExceptionHandler(ConstraintViolationException.class)
    public GraphQlError handleValidation(ConstraintViolationException ex) {
        return GraphqlErrorBuilder.newError()
            .errorType(ErrorType.BAD_USER_INPUT)
            .message("Validation failed: " + ex.getMessage())
            .build();
    }

    @GraphQlExceptionHandler(AccessDeniedException.class)
    public GraphQlError handleAccessDenied(AccessDeniedException ex) {
        return GraphqlErrorBuilder.newError()
            .errorType(ErrorType.FORBIDDEN)
            .message("Access denied")
            .build();
    }
}
```

**Spring Security integration:** Spring for GraphQL automatically handles `AccessDeniedException` and maps it to `FORBIDDEN` when Spring Security is on the classpath.

---

## 35. Automatic Persisted Queries (APQ)

**Problem:** Large query strings are sent on every request — waste bandwidth, slower latency.

**Solution (APQ):**
1. First request: client sends `{ "extensions": { "persistedQuery": { "sha256Hash": "abc123" } } }` — no query string
2. Server responds with `"PersistedQueryNotFound"` error
3. Client retries with full query + hash
4. Server caches query by hash
5. Future requests: send hash only

Spring for GraphQL supports APQ with a cache bean:
```java
@Bean
public PreparsedDocumentProvider preparsedDocumentProvider() {
    // Use Caffeine or other cache
    return new AutomaticPersistedQueryCachingProvider(caffeineCache);
}
```

---

## 36. Looking Ahead — Day 32: GraphQL Client

**Day 32 — GraphQL Client (React & Angular)**

| Topic | Detail |
|-------|--------|
| Apollo Client for React | `ApolloProvider`, `useQuery`, `useMutation`, `useSubscription` |
| Apollo Angular | `provideApollo()`, `Apollo.watchQuery()`, `Apollo.mutate()` |
| `gql` template tag | Write typesafe query strings in the client |
| InMemoryCache | Automatic client-side cache + normalization |
| Cache update after mutation | `refetchQueries` vs `update` function |
| Authentication link | `setContext` to add JWT to every request |
| Error handling | Network errors vs GraphQL errors |
| N+1 on server | Review Day 31's `@BatchMapping` |
| Postman testing | Send GraphQL queries via Postman |
| GraphiQL / Apollo Sandbox | Browser-based query explorers |

# Day 31 Part 2 — Arguments, Variables, Fragments, Spring for GraphQL, and Schema Design
## Slide Descriptions

---

### Slide 1 — Title Slide

**Title:** Day 31 Part 2: Advanced Queries and Building a GraphQL Server in Spring Boot

**Subtitle:** Dynamic operations, reusable queries, resolvers, and schema design best practices

**Learning Objectives:**
- Write parameterized queries using arguments and variables
- Use aliases to request the same field multiple times
- Build reusable query components with named fragments and inline fragments
- Control field inclusion with `@include` and `@skip` directives
- Set up Spring for GraphQL with Maven, schema files, and auto-configuration
- Implement resolvers with `@QueryMapping`, `@MutationMapping`, and `@SchemaMapping`
- Handle subscription fields with `@SubscriptionMapping` and `Publisher<T>`
- Extract method parameters with `@Argument`
- Use `DataFetchingEnvironment` for resolver look-ahead and request context access
- Handle resolver exceptions cleanly with `@GraphQlExceptionHandler`
- Understand Automatic Persisted Queries (APQ) for production optimization
- Apply GraphQL schema design best practices: nullability, pagination, error patterns, deprecation

---

### Slide 2 — Arguments

**Title:** Arguments — Parameterizing Fields

**Content:**

Arguments let clients pass values to fields to filter, sort, or modify the data returned. Arguments are defined in the schema and can appear on any field — root fields (queries/mutations) or type fields.

**Schema definition with arguments:**
```graphql
type Query {
  book(id: ID!): Book                        # required ID argument
  books(limit: Int, offset: Int): [Book!]!   # optional pagination arguments
  booksByAuthor(authorId: ID!, limit: Int): [Book!]!
  searchBooks(query: String!, genre: String): [Book!]!
}

type Book {
  id: ID!
  title: String!
  reviews(minRating: Int): [Review!]!        # arguments on non-root fields too
}
```

**Queries with inline arguments:**
```graphql
# Simple argument
query GetSpecificBook {
  book(id: "42") {
    title
    pageCount
  }
}

# Multiple arguments on a field
query SearchBooks {
  searchBooks(query: "design patterns", genre: "Programming") {
    id
    title
    author { name }
  }
}

# Paginated books
query GetBooksPage {
  books(limit: 10, offset: 20) {
    id
    title
  }
}

# Argument on a non-root field
query GetBookWithHighRatedReviews {
  book(id: "1") {
    title
    reviews(minRating: 4) {     # only reviews with rating >= 4
      rating
      text
    }
  }
}
```

**Default values for arguments:**
```graphql
type Query {
  books(limit: Int = 10, offset: Int = 0): [Book!]!
}
```

---

### Slide 3 — Variables

**Title:** Variables — Making Queries Reusable and Safe

**Content:**

Hardcoding arguments inline (like `book(id: "42")`) works for one-off queries but creates problems in applications: string concatenation is error-prone, values must be escaped, and the query can't be reused. Variables solve this.

**Without variables (don't do this in application code):**
```graphql
# Bad — value is hardcoded; string interpolation to change id is unsafe
query {
  book(id: "42") { title }
}
```

**With variables (correct approach):**
```graphql
# Operation document — defines the variable and its type
query GetBook($bookId: ID!) {
  book(id: $bookId) {
    title
    pageCount
    author { name }
  }
}
```

```json
// Variables JSON — sent alongside the query in the request body
{
  "bookId": "42"
}
```

**Variable syntax:**
- Variables are declared in the operation signature: `($varName: Type)`
- Variable names start with `$`
- Variable types must match the argument type in the schema
- `!` in the variable type means the variable is required
- Default values: `($limit: Int = 10)`

**Complete request body (what is sent to POST /graphql):**
```json
{
  "query": "query GetBook($bookId: ID!) { book(id: $bookId) { title } }",
  "variables": { "bookId": "42" },
  "operationName": "GetBook"
}
```

**Variables with input types (mutations):**
```graphql
mutation CreateBook($input: CreateBookInput!) {
  createBook(input: $input) {
    id
    title
    author { name }
  }
}
```
```json
{
  "input": {
    "title": "Clean Code",
    "authorId": "5",
    "pageCount": 431
  }
}
```

---

### Slide 4 — Aliases

**Title:** Aliases — Requesting the Same Field Multiple Times

**Content:**

A problem arises when you want to request the same field with different arguments in one query. Without aliases, the JSON keys would collide.

**The problem — without aliases (invalid):**
```graphql
# This doesn't work — "book" appears twice, JSON keys would collide
query {
  book(id: "1") { title }
  book(id: "2") { title }
}
```

**The solution — aliases:**
```graphql
query CompareTwoBooks {
  firstBook: book(id: "1") {
    title
    pageCount
    author { name }
  }
  secondBook: book(id: "2") {
    title
    pageCount
    author { name }
  }
}
```

**Response:**
```json
{
  "data": {
    "firstBook": { "title": "Clean Code", "pageCount": 431, "author": { "name": "Robert Martin" } },
    "secondBook": { "title": "Refactoring", "pageCount": 448, "author": { "name": "Martin Fowler" } }
  }
}
```

**Alias syntax:** `aliasName: fieldName(arguments) { selectionSet }`

**Other use cases for aliases:**

**1. Rename a field in the response:**
```graphql
query {
  book(id: "1") {
    bookTitle: title       # response key will be "bookTitle", not "title"
    wordCount: pageCount   # rename to match frontend model
  }
}
```

**2. Request filtered and unfiltered versions:**
```graphql
query {
  allBooks: books {
    id
    title
  }
  availableBooks: books(availableOnly: true) {
    id
    title
  }
}
```

---

### Slide 5 — Fragments

**Title:** Fragments — Reusable Selection Sets

**Content:**

Fragments let you define a named selection set once and reuse it across multiple queries. They eliminate repetition when many queries need the same set of fields.

**Defining and using a named fragment:**
```graphql
# Fragment definition — names the selection set and the type it applies to
fragment BookFields on Book {
  id
  title
  pageCount
  available
  author {
    name
  }
}

# Use the fragment with spread syntax (...)
query GetSingleBook {
  book(id: "1") {
    ...BookFields       # expands to: id, title, pageCount, available, author { name }
    reviews {
      rating
      text
    }
  }
}

query GetAllBooks {
  books {
    ...BookFields       # same fragment reused here
  }
}
```

**Fragment rules:**
- Fragment names must be unique within a document
- The `on TypeName` part must match the type where the fragment is used
- Fragments cannot reference themselves (no recursion)
- Fragments can reference other fragments

**Inline fragments — for union and interface types:**
```graphql
# Schema defines a union type
union SearchResult = Book | Author

type Query {
  search(term: String!): [SearchResult!]!
}

# Query uses inline fragments to handle each union member
query Search {
  search(term: "design") {
    ... on Book {           # applies only when the result is a Book
      title
      pageCount
    }
    ... on Author {         # applies only when the result is an Author
      name
      books { title }
    }
  }
}
```

**`__typename` — discovering the concrete type:**
```graphql
query Search {
  search(term: "Martin") {
    __typename    # built-in field — returns the type name as a string
    ... on Book { title }
    ... on Author { name }
  }
}
# Response: [{ "__typename": "Author", "name": "Robert Martin" }, ...]
```

---

### Slide 6 — Directives

**Title:** Directives — Annotating Queries and Schema Elements

**Content:**

Directives modify the behavior of a query or schema element. They start with `@`.

**Built-in client-side directives:**

**`@include(if: Boolean)` — conditionally include a field:**
```graphql
query GetBook($includeReviews: Boolean!) {
  book(id: "1") {
    title
    reviews @include(if: $includeReviews) {
      rating
      text
    }
  }
}
```
```json
{ "includeReviews": false }
```
When `includeReviews` is false, the `reviews` field is not requested and not returned.

**`@skip(if: Boolean)` — conditionally skip a field:**
```graphql
query GetBook($skipDetails: Boolean!) {
  book(id: "1") {
    title
    pageCount @skip(if: $skipDetails)
    author @skip(if: $skipDetails) {
      name
    }
  }
}
```
`@include(if: false)` and `@skip(if: true)` are equivalent — both exclude the field.

**Built-in schema directive:**

**`@deprecated(reason: String)` — mark a field as deprecated:**
```graphql
type Book {
  id: ID!
  title: String!
  isbn10: String @deprecated(reason: "Use isbn13 instead")
  isbn13: String
}
```
Deprecated fields still work — clients that use them receive a warning in GraphiQL and Apollo tooling. This is how GraphQL handles versioning: you add new fields, deprecate old ones, and remove them only after all clients have migrated. No breaking version bump needed.

---

### Slide 7 — Spring for GraphQL Setup

**Title:** Spring for GraphQL — Project Setup

**Content:**

Spring for GraphQL is the official Spring project for building GraphQL APIs with Spring Boot. It uses GraphQL Java under the hood and integrates with the Spring ecosystem (Spring MVC, Spring WebFlux, Spring Data).

**Maven dependency:**
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-graphql</artifactId>
</dependency>
<!-- Spring MVC for the HTTP transport -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
<!-- Optional: for the browser-based GraphiQL IDE -->
<!-- Enabled via property, not a separate dependency -->
```

**Schema file location:**
```
src/main/resources/graphql/schema.graphqls     ← Spring for GraphQL auto-discovers this
src/main/resources/graphql/book.graphqls       ← or split into multiple files
src/main/resources/graphql/author.graphqls
```

Spring for GraphQL scans `classpath:graphql/**/*.graphqls` (and `.gql`) at startup. All files are merged into one schema automatically. Split by domain for large schemas.

**Application properties:**
```properties
# Enable the browser-based GraphiQL IDE at /graphiql
spring.graphql.graphiql.enabled=true

# The GraphQL endpoint (default: /graphql)
spring.graphql.path=/graphql

# Disable introspection in production
spring.graphql.schema.introspection.enabled=false
```

**Project structure:**
```
src/main/
├── java/com/example/bookstore/
│   ├── controller/
│   │   └── BookController.java     ← @QueryMapping, @MutationMapping
│   ├── service/
│   │   └── BookService.java
│   └── repository/
│       └── BookRepository.java
└── resources/
    └── graphql/
        └── schema.graphqls         ← SDL schema file
```

---

### Slide 8 — @QueryMapping

**Title:** @QueryMapping — Implementing Query Resolvers

**Content:**

`@QueryMapping` annotates a method that handles a root-level query field. The method name must match the field name in the `Query` type of the schema (or you can specify it explicitly).

**Schema:**
```graphql
type Query {
  book(id: ID!): Book
  books: [Book!]!
  searchBooks(query: String!): [Book!]!
}
```

**Controller:**
```java
@Controller   // NOT @RestController — Spring for GraphQL uses @Controller
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    // Method name "book" matches schema field "book" on Query type
    @QueryMapping
    public Book book(@Argument String id) {
        return bookService.findById(Long.parseLong(id));
    }

    // Method name "books" matches schema field "books"
    @QueryMapping
    public List<Book> books() {
        return bookService.findAll();
    }

    // @Argument extracts the "query" argument from the GraphQL operation
    @QueryMapping
    public List<Book> searchBooks(@Argument String query) {
        return bookService.search(query);
    }
}
```

**`@Argument`** extracts a named argument from the GraphQL operation and binds it to the method parameter. The parameter name must match the argument name in the schema, or specify it: `@Argument("query") String searchTerm`.

**What `@Controller` vs `@RestController` means here:**
- Spring for GraphQL uses `@Controller` — the framework handles serialization itself
- `@RestController` adds `@ResponseBody` which is for HTTP response bodies; GraphQL responses are handled differently
- `@Controller` is the correct annotation for GraphQL resolver classes

---

### Slide 9 — @MutationMapping

**Title:** @MutationMapping — Implementing Mutation Resolvers

**Content:**

`@MutationMapping` annotates a method that handles a root-level mutation field.

**Schema:**
```graphql
input CreateBookInput {
  title: String!
  authorId: ID!
  isbn: String
  pageCount: Int
}

type Mutation {
  createBook(input: CreateBookInput!): Book!
  updateBook(id: ID!, input: UpdateBookInput!): Book!
  deleteBook(id: ID!): Boolean!
}
```

**Controller:**
```java
@Controller
public class BookMutationController {

    private final BookService bookService;

    @MutationMapping
    public Book createBook(@Argument CreateBookInput input) {
        // @Argument maps the GraphQL "input" argument to a CreateBookInput POJO
        // Spring for GraphQL automatically deserializes the GraphQL input object
        // to the Java type — field names must match
        return bookService.createBook(input);
    }

    @MutationMapping
    public Book updateBook(@Argument String id, @Argument UpdateBookInput input) {
        return bookService.updateBook(Long.parseLong(id), input);
    }

    @MutationMapping
    public boolean deleteBook(@Argument String id) {
        bookService.deleteBook(Long.parseLong(id));
        return true;
    }
}
```

**The `CreateBookInput` Java class:**
```java
// Simple POJO — field names match the SDL input type field names
public record CreateBookInput(
    String title,
    String authorId,
    String isbn,
    Integer pageCount
) {}
```

Spring for GraphQL uses Jackson to deserialize the input argument to the Java record/class. Field names must match the SDL field names exactly (camelCase both sides).

---

### Slide 10 — @SchemaMapping — Non-Root Resolvers

**Title:** @SchemaMapping — Resolving Fields on Object Types

**Content:**

`@SchemaMapping` handles fields on non-root types — for example, resolving the `author` field on a `Book`, or the `books` field on an `Author`.

**Why do we need this?**
When you return a `Book` from a query, the `author` field on `Book` is an `Author` type. Your `Book` Java entity might only have an `authorId`, not the full `Author` object. A separate resolver fetches the `Author` from the `authorId`.

```java
@Controller
public class BookController {

    private final BookService bookService;
    private final AuthorService authorService;

    // Root query resolver — returns Book entity (may only have authorId)
    @QueryMapping
    public Book book(@Argument String id) {
        return bookService.findById(Long.parseLong(id));
    }

    // Field resolver — called for the "author" field on Book
    // "source" is the Book instance returned by the query resolver
    @SchemaMapping(typeName = "Book", field = "author")
    public Author author(Book source) {
        return authorService.findById(source.getAuthorId());
    }

    // Field resolver for reviews on Book
    @SchemaMapping(typeName = "Book", field = "reviews")
    public List<Review> reviews(Book source) {
        return reviewService.findByBookId(source.getId());
    }
}
```

**Shorthand — when method name matches field name:**
```java
// If the method is in a class annotated with @SchemaMapping(typeName = "Book")
// and the method name matches the field name, you can omit field= parameter

@Controller
@SchemaMapping(typeName = "Book")   // class-level default
public class BookController {

    @SchemaMapping   // resolves Book.author because method name = "author"
    public Author author(Book source) {
        return authorService.findById(source.getAuthorId());
    }
}
```

**When Spring handles it automatically:**
If your `Book` Java class has an `author` field of type `Author` (not just `authorId`), Spring for GraphQL resolves it automatically from the property. You only need `@SchemaMapping` when the Java object doesn't directly have the field value.

---

### Slide 11 — @SubscriptionMapping

**Title:** @SubscriptionMapping — Real-Time Event Streams

**Content:**

`@SubscriptionMapping` handles real-time subscription fields. The return type is `Publisher<T>` — typically `Flux<T>` from Project Reactor.

**Schema:**
```graphql
type Subscription {
  bookAdded: Book!
  reviewAdded(bookId: ID!): Review!
}
```

**Controller:**
```java
@Controller
public class BookSubscriptionController {

    private final BookEventPublisher bookEventPublisher;

    // Returns Flux<Book> — a stream that emits a Book every time one is added
    @SubscriptionMapping
    public Flux<Book> bookAdded() {
        return bookEventPublisher.getBookAddedFlux();
    }

    // Subscription with an argument — filtered stream
    @SubscriptionMapping
    public Flux<Review> reviewAdded(@Argument String bookId) {
        return bookEventPublisher.getReviewFlux()
            .filter(review -> review.getBookId().equals(bookId));
    }
}
```

**BookEventPublisher — a simple Sink-based publisher:**
```java
@Component
public class BookEventPublisher {

    // Sinks.Many is a Reactor construct for a hot publisher
    private final Sinks.Many<Book> bookSink = Sinks.many().multicast().onBackpressureBuffer();

    // Called when a new book is created (e.g., from a mutation)
    public void publishBook(Book book) {
        bookSink.tryEmitNext(book);
    }

    public Flux<Book> getBookAddedFlux() {
        return bookSink.asFlux();
    }
}
```

**Spring for GraphQL + subscriptions requires WebSocket or SSE support:**
```xml
<!-- Add for WebSocket transport -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-websocket</artifactId>
</dependency>
```

```properties
spring.graphql.websocket.path=/graphql-ws
```

---

### Slide 12 — @BatchMapping — Brief Introduction

**Title:** @BatchMapping — Preview of the N+1 Solution

**Content:**

**The N+1 problem in GraphQL:**
When you query a list of books and each book needs its author resolved:
```
Query: { books { title author { name } } }
→ books resolver runs once, returns 10 Book objects
→ author resolver runs 10 times (once per book), 10 database queries
→ 1 + 10 = 11 queries total
```

This is the N+1 problem. With 100 books it becomes 101 queries.

**`@BatchMapping` solves this:**
Instead of resolving one author per book, `@BatchMapping` collects all the parent objects first and resolves them in a single batch.

```java
// Instead of:
@SchemaMapping(typeName = "Book", field = "author")
public Author author(Book book) {
    return authorService.findById(book.getAuthorId()); // runs N times
}

// Use @BatchMapping:
@BatchMapping(typeName = "Book", field = "author")
public Map<Book, Author> author(List<Book> books) {
    // Called once with ALL books — fetch all authors in a single query
    List<Long> authorIds = books.stream()
        .map(Book::getAuthorId)
        .distinct()
        .collect(Collectors.toList());

    Map<Long, Author> authorsById = authorService.findAllByIds(authorIds).stream()
        .collect(Collectors.toMap(Author::getId, a -> a));

    return books.stream()
        .collect(Collectors.toMap(book -> book, book -> authorsById.get(book.getAuthorId())));
}
```

**`@BatchMapping` returns `Map<ParentType, FieldType>`** — a mapping from each parent object to its resolved field value. Spring for GraphQL calls this once per query (not once per parent) and uses the map to assemble results.

**Day 32 coverage:** DataLoader pattern, the Apollo client-side equivalent (batching and caching) — full treatment in the next session.

---

### Slide 13 — DataFetchingEnvironment

**Title:** DataFetchingEnvironment — Inspecting the Request Inside a Resolver

**Content:**

`DataFetchingEnvironment` is a low-level object, injected by the framework into any resolver method, that exposes request-level context the Spring for GraphQL annotations don't surface individually.

**Declaring it in a resolver:**
```java
@QueryMapping
public Book book(@Argument String id, DataFetchingEnvironment env) {
    // env is automatically injected alongside @Argument parameters
}
```

**Primary use — look-ahead field selection:**
The client's selection set tells you which fields were actually requested. You can check this before hitting the database to skip joins the client doesn't need.

```java
@QueryMapping
public Book book(@Argument String id, DataFetchingEnvironment env) {
    boolean needsAuthor = env.getSelectionSet().contains("author");
    // if needsAuthor is false, skip the author JOIN in the SQL query
    return bookService.findByIdWithOptionalAuthor(Long.parseLong(id), needsAuthor);
}
```

**Other available context:**
```java
// The parent object (same as "source" in @SchemaMapping)
Book source = env.getSource();

// All arguments as a raw map
Map<String, Object> args = env.getArguments();

// Access Spring's GraphQL context (security, request headers, etc.)
GraphQLContext context = env.getGraphQlContext();

// The field definition from the schema
SelectedField field = env.getField();
```

**When to use it:**
- Look-ahead optimization — skip expensive joins when the client didn't request the associated type
- Accessing security context or request headers inside a resolver
- Custom logging or instrumentation at the field level

**When not to use it for N+1:** `@BatchMapping` handles the N+1 problem across a list of parents — do not substitute `DataFetchingEnvironment` look-ahead for `@BatchMapping`. They solve different problems.

---

### Slide 14 — @GraphQlExceptionHandler

**Title:** @GraphQlExceptionHandler — Structured, Client-Safe Error Responses

**Content:**

When a resolver throws an unhandled exception, Spring for GraphQL catches it and produces a generic error message. That message may expose JPA internals, class names, or stack traces — information clients should never see. `@GraphQlExceptionHandler` lets you intercept exceptions and produce clean, controlled error responses.

**Without exception handling — implementation details leak:**
```java
@QueryMapping
public Book book(@Argument String id) {
    return bookRepository.findById(Long.parseLong(id))
        .orElseThrow(() -> new EntityNotFoundException("Book id=" + id));
}
// Error: "Unable to locate Book with id: 999; nested exception is javax.persistence..."
// ↑ Exposes JPA class names — bad!
```

**With @GraphQlExceptionHandler — clean, structured error:**
```java
@Controller
public class BookController {

    @QueryMapping
    public Book book(@Argument String id) {
        return bookRepository.findById(Long.parseLong(id))
            .orElseThrow(() -> new BookNotFoundException(id));
    }

    @GraphQlExceptionHandler
    public GraphQLError handleBookNotFound(BookNotFoundException ex) {
        return GraphQLError.newError()
            .errorType(ErrorType.NOT_FOUND)
            .message("Book not found: " + ex.getId())
            .extensions(Map.of("code", "BOOK_NOT_FOUND"))
            .build();
    }
}
// Error: { "message": "Book not found: 999", "extensions": { "code": "BOOK_NOT_FOUND" } }
```

**Centralized in @ControllerAdvice (recommended):**
```java
@ControllerAdvice
public class GraphQlExceptionAdvice {

    @GraphQlExceptionHandler
    public GraphQLError handleNotFound(BookNotFoundException ex) {
        return GraphQLError.newError()
            .errorType(ErrorType.NOT_FOUND)
            .message("Book not found: " + ex.getId())
            .extensions(Map.of("code", "BOOK_NOT_FOUND", "bookId", ex.getId()))
            .build();
    }

    @GraphQlExceptionHandler
    public GraphQLError handleAccessDenied(AccessDeniedException ex) {
        return GraphQLError.newError()
            .errorType(ErrorType.FORBIDDEN)
            .message("Access denied")
            .build();
    }
}
```

**Standard error types:**

| `ErrorType` | When to use |
|-------------|-------------|
| `NOT_FOUND` | Resource doesn't exist |
| `BAD_REQUEST` | Invalid client input |
| `FORBIDDEN` | Authenticated but not authorized |
| `UNAUTHORIZED` | Authentication required |
| `INTERNAL_ERROR` | Unhandled server error (default) |

**Rule:** Treat `message` as a public-facing string. Never expose stack traces, internal class names, or database details.

---

### Slide 15 — Persisted Queries

**Title:** Automatic Persisted Queries — Production Bandwidth and Security Optimization

**Content:**

In production, GraphQL query strings can be hundreds of characters long. Every request re-sends the full query text. **Automatic Persisted Queries (APQ)** address this by replacing the query string with a short SHA-256 hash.

**How APQ works:**
```
Normal request:   POST /graphql
  Body: { "query": "query GetBook($id: ID!) { book(id: $id) { title author { name } } }", "variables": {...} }
  → 80+ character query string sent on every request

APQ request:      POST /graphql
  Body: { "extensions": { "persistedQuery": { "sha256Hash": "a1b2c3..." } }, "variables": {...} }
  → ~64 character hash only
```

**First-request registration flow:**
1. Client sends only the hash
2. Server has not seen this hash → returns `PERSISTED_QUERY_NOT_FOUND`
3. Client re-sends with hash + full query string (registers it in server cache)
4. All future requests for that query use only the hash

**Why it matters:**
- **Bandwidth:** hash (~64 bytes) vs query string (100–1000+ bytes) — significant on mobile networks
- **Security (allowlist mode):** with static persisted queries, the server rejects any query hash not in the pre-registered allowlist — no arbitrary query execution from untrusted clients
- **Caching:** stable query hash enables CDN and HTTP response caching

**Where it lives in your stack:**
APQ is typically handled at the **gateway layer** — Apollo Router, a reverse proxy — not in the Spring Boot application server. Apollo Client enables APQ by default; your server will receive hashed requests from Apollo Client frontends automatically. Spring for GraphQL supports custom `WebInterceptor` hooks for APQ if a gateway is not used.

**What to know for now:**
- APQ is a production optimization — not something you configure during development
- GraphiQL and testing tools always send full query strings (no APQ)
- Apollo Client apps send APQ requests by default; be aware when testing manually with raw HTTP tools

---

### Slide 16 — Schema Design Best Practices

**Title:** Schema Design Best Practices — Part 1: Naming, Nullability, and Input Types

**Content:**

**Naming conventions:**
```graphql
type Author { ... }            # PascalCase for type names
input CreateAuthorInput { ... } # PascalCase + purpose suffix for inputs

type Query {
  author(id: ID!): Author      # camelCase for field and query names
  searchAuthors(query: String!): [Author!]!
}

enum BookGenre {
  FICTION                      # SCREAMING_SNAKE_CASE for enum values
  NON_FICTION
  TECHNICAL
}
```

**Nullability philosophy — two schools of thought:**

*Nullable by default (permissive):* every field is nullable unless explicitly non-null. Clients handle nulls defensively. Schema evolves easily — a field can start nullable and become non-null without breaking clients.

*Non-null by default (strict):* make fields non-null unless there's a specific reason they might be null. Clients can trust values are present. Safer for typed client codegen. **Preferred for new schemas.**

**Input types for all mutations — no exceptions:**
```graphql
# ❌ Don't do this — individual scalar arguments don't compose well
mutation createBook(title: String!, authorId: ID!, pageCount: Int): Book!

# ✅ Do this — grouped input type
mutation createBook(input: CreateBookInput!): Book!
```

**Separate input and output types:**
```graphql
input CreateBookInput { authorId: ID! ... }   # for mutations (input)
type Book { author: Author! ... }             # for queries (output)
# Note: author in output is a full Author object, not just authorId
```

---

### Slide 17 — Schema Design Best Practices: Pagination and Errors

**Title:** Schema Design Best Practices — Part 2: Pagination and Error Patterns

**Content:**

**Pagination — the Relay Connection pattern (recommended for large lists):**

Instead of `books(offset: Int, limit: Int): [Book!]!` (offset pagination — doesn't handle concurrent inserts well), use cursor-based pagination:

```graphql
type PageInfo {
  hasNextPage: Boolean!
  hasPreviousPage: Boolean!
  startCursor: String
  endCursor: String
}

type BookEdge {
  node: Book!        # the actual book
  cursor: String!    # opaque cursor for this position
}

type BookConnection {
  edges: [BookEdge!]!
  pageInfo: PageInfo!
  totalCount: Int!
}

type Query {
  books(first: Int, after: String, last: Int, before: String): BookConnection!
}
```

**Query with connection pagination:**
```graphql
query GetBooksPage {
  books(first: 10, after: "cursor123") {
    edges {
      node {
        title
        author { name }
      }
      cursor
    }
    pageInfo {
      hasNextPage
      endCursor
    }
    totalCount
  }
}
```

**Error handling patterns in the schema:**

Option 1 — implicit errors (GraphQL default):
```graphql
# Throws exception → GraphQL catches → returns in errors array
mutation DeleteBook {
  deleteBook(id: "999")  # returns null in data + error in errors if not found
}
```

Option 2 — result union types (explicit, type-safe errors):
```graphql
union DeleteBookResult = Book | NotFoundError | PermissionDeniedError

type NotFoundError { message: String!, id: ID! }
type PermissionDeniedError { message: String!, requiredRole: String! }

type Mutation {
  deleteBook(id: ID!): DeleteBookResult!
}
```
```graphql
mutation {
  deleteBook(id: "999") {
    ... on Book { id title }
    ... on NotFoundError { message id }
    ... on PermissionDeniedError { message requiredRole }
  }
}
```

The result union pattern makes error cases explicit in the schema and type-safe on the client. It's more verbose but cleaner for complex mutation workflows.

---

### Slide 18 — Schema Design Best Practices: Versioning and Deprecation

**Title:** Schema Design Best Practices — Part 3: Versioning-Free Evolution and Documentation

**Content:**

**GraphQL is designed to evolve without versioning:**

REST versioning:
```
GET /api/v1/books
GET /api/v2/books    # breaking change requires new version
```

GraphQL approach — add, deprecate, remove:
```graphql
type Book {
  id: ID!
  title: String!
  
  # Step 1: Add new field
  isbn13: String!
  
  # Step 2: Deprecate old field (clients still work)
  isbn10: String @deprecated(reason: "Use isbn13 instead. Will be removed 2025-01-01.")
  
  # Step 3: Remove after all clients migrated (monitor usage via field-level analytics)
}
```

Clients using `isbn10` continue to work. GraphiQL shows a strikethrough. Apollo Studio tracks usage so you know when it's safe to remove.

**Document your schema with descriptions:**
```graphql
"""
Represents a book in the library catalog.
"""
type Book {
  "Unique identifier for the book"
  id: ID!

  "The full title of the book"
  title: String!

  """
  Number of pages. May be null for books without a known page count,
  such as digital-only formats with reflowable text.
  """
  pageCount: Int
}

"""
Input type for creating a new book. All required fields must be provided.
"""
input CreateBookInput {
  "The full title of the book"
  title: String!
}
```

Triple-quoted strings (`"""`) are block descriptions; single-quoted strings are inline descriptions. Both appear in GraphiQL tooltips, auto-generated docs, and client codegen.

**Schema design checklist:**
- [ ] Use non-null (`!`) for fields that will always be present
- [ ] Group mutation arguments into input types
- [ ] Return the mutated object from mutations (not just Boolean/ID)
- [ ] Use Connection pattern for paginated lists
- [ ] Describe every type and field with SDL descriptions
- [ ] Use `@deprecated` instead of removing fields immediately
- [ ] Use result union types for operations with multiple error cases
- [ ] Disable introspection in production

---

### Slide 19 — Day 31 Summary

**Title:** Day 31 Summary — GraphQL Foundations

**Content:**

**Query language features:**

| Feature | Purpose | Example |
|---------|---------|---------|
| **Arguments** | Pass values to fields | `book(id: "1")` |
| **Variables** | Make queries dynamic, safe from injection | `$bookId: ID!` → JSON `{ "bookId": "1" }` |
| **Aliases** | Request same field multiple times | `firstBook: book(id: "1")` |
| **Fragments** | Reusable selection sets | `fragment BookFields on Book { ... }` |
| **Inline fragments** | Handle union/interface types | `... on Book { title }` |
| **`@include`** | Conditionally include field | `reviews @include(if: $showReviews)` |
| **`@skip`** | Conditionally skip field | `details @skip(if: $compact)` |
| **`@deprecated`** | Mark schema field as deprecated | `isbn @deprecated(reason: "...")` |

**Spring for GraphQL annotations:**

| Annotation | Maps to |
|------------|---------|
| `@QueryMapping` | Field on `Query` type |
| `@MutationMapping` | Field on `Mutation` type |
| `@SubscriptionMapping` | Field on `Subscription` type — returns `Flux<T>` |
| `@SchemaMapping(typeName, field)` | Any field on any type |
| `@BatchMapping(typeName, field)` | Field resolved for a list of parents at once |
| `@Argument` | Method parameter bound to a GraphQL argument |
| `@GraphQlExceptionHandler` | Maps resolver exceptions to structured `GraphQLError` responses |

**Schema design principles:**
- Non-null by default for fields that should always be present
- Input types for all mutation arguments
- Return modified objects from mutations
- Connection pattern for paginated lists
- `@deprecated` + descriptions for schema evolution without versioning

**Additional resolver tools:**
- `DataFetchingEnvironment` — look-ahead field selection and request context access inside any resolver
- `@GraphQlExceptionHandler` in `@ControllerAdvice` — centralized, clean, client-safe error responses
- Automatic Persisted Queries (APQ) — Apollo Client sends query hashes by default; handled at the gateway layer in production

**Day 32 Preview:** GraphQL clients — Apollo Client for React and Angular, executing queries and mutations from the frontend, handling errors client-side, GraphiQL and Playground for API exploration, the N+1 / DataLoader pattern in depth, query optimization and caching.

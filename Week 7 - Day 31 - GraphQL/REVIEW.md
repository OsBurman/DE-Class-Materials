# Day 31 Review — GraphQL
## Quick Reference Guide

---

## 1. REST Limitations GraphQL Addresses

| Problem | Description | GraphQL Solution |
|---------|-------------|-----------------|
| **Over-fetching** | Endpoint returns more fields than the client needs | Client specifies exactly which fields to return |
| **Under-fetching** | Related data requires additional requests (N+1 trips) | Nested queries fetch everything in one request |
| **Endpoint explosion** | Different clients need different shapes → multiple endpoints | One endpoint; query shape determines response shape |
| **Versioning** | Breaking changes require `/v2` endpoints | Add fields freely; deprecate old ones with `@deprecated` |

---

## 2. GraphQL Core Concepts

- **Single endpoint:** All operations go to `POST /graphql`
- **Client-driven:** Client specifies exactly which fields to return in each request
- **Typed schema:** SDL defines every type, field, and operation — the API contract
- **Introspectable:** `__schema` and `__type` queries expose the schema for tooling
- **Versionless:** Fields are added and deprecated, not versioned
- **Resolver:** A function that fetches data for a specific field

---

## 3. GraphQL vs REST — Comparison Table

| Aspect | REST | GraphQL |
|--------|------|---------|
| Endpoints | Many | One (`POST /graphql`) |
| Response shape | Fixed by server | Defined by client's query |
| Over-fetching | Common | Eliminated |
| Under-fetching | Common (multiple round trips) | Eliminated (nested queries) |
| Type system | Optional (OpenAPI) | Built-in, mandatory |
| Versioning | `/v1`, `/v2` | Field deprecation |
| HTTP caching | Easy (GET requests) | Harder (all POST) |
| File uploads | Standard multipart | Requires extension |
| Error format | HTTP status codes | Always 200 + `errors` array |
| Learning curve | Low | Medium |

---

## 4. GraphQL Request Architecture

```
Client          GraphQL Server                 Data Sources
  │                  │                              │
  │── POST /graphql ─►│                              │
  │   { query, vars } │── 1. Parse (AST) ────────────│
  │                   │── 2. Validate (schema check)  │
  │                   │── 3. Execute (call resolvers) ─► DB / REST / etc.
  │                   │── 4. Shape response ──────────│
  │◄── { data: {...}} ─│                              │
```

---

## 5. SDL Syntax Reference

```graphql
# Scalar field
fieldName: ScalarType       # nullable
fieldName: ScalarType!      # non-null

# List field
fieldName: [ObjectType]     # nullable list of nullable items
fieldName: [ObjectType!]!   # non-null list of non-null items (most common)

# Object type
type TypeName {
  fieldName: FieldType
}

# Input type (for mutation arguments — not usable as query return type)
input InputTypeName {
  fieldName: FieldType!
}

# Query entry points
type Query {
  operationName(arg: Type): ReturnType
}

# Mutation entry points
type Mutation {
  operationName(input: InputType!): ReturnType!
}

# Subscription entry points
type Subscription {
  eventName: ReturnType!
}

# Enum
enum EnumName {
  VALUE_ONE
  VALUE_TWO
}

# Union
union UnionName = TypeA | TypeB

# Description (appears in GraphiQL tooltips)
"Short description" fieldName: Type
"""
Block description for longer docs
"""
type TypeName { ... }
```

---

## 6. Scalar Types Reference

| GraphQL Scalar | Java Type | Description |
|----------------|-----------|-------------|
| `String` | `String` | UTF-8 text |
| `Int` | `int` / `Integer` | 32-bit signed integer |
| `Float` | `double` / `Double` | Double-precision float |
| `Boolean` | `boolean` / `Boolean` | true / false |
| `ID` | `String` / `Long` | Unique identifier — always serialized as String |

**Spring for GraphQL extended scalars** (via `graphql-java-extended-scalars`):
`Date`, `DateTime`, `Time`, `Url`, `JSON`, `BigDecimal`, `Long`

---

## 7. Non-Null and List Type Modifiers

| Type notation | Meaning |
|---------------|---------|
| `String` | Nullable String — server may return `null` |
| `String!` | Non-null String — server guarantees a value |
| `[String]` | Nullable list of nullable Strings |
| `[String!]` | Nullable list of non-null Strings |
| `[String]!` | Non-null list of nullable Strings |
| `[String!]!` | Non-null list of non-null Strings (most common for collections) |

---

## 8. Object Type Definition

```graphql
type Book {
  id:          ID!
  title:       String!
  isbn:        String           # nullable — not all books have an ISBN
  pageCount:   Int
  available:   Boolean!
  author:      Author!          # relationship to another type
  reviews:     [Review!]!       # one-to-many relationship
  categories:  [String!]!       # list of scalars
}
```

**Naming conventions:**
- Type names: `PascalCase`
- Field names: `camelCase`
- Enum values: `SCREAMING_SNAKE_CASE`
- Query/mutation names: `camelCase`

---

## 9. Queries — Syntax Reference

```graphql
# Anonymous shorthand (development only — no name, hard to debug)
{
  books { id title }
}

# Named query (always use in application code)
query OperationName {
  books {
    id
    title
    author { name }
  }
}

# Query with argument
query GetBook {
  book(id: "1") {
    title
    pageCount
  }
}

# Nested query — one request for multiple levels of data
query GetBookDeep {
  book(id: "1") {
    title
    author {
      name
      books { title }   # all books by this author, fetched in same request
    }
  }
}
```

---

## 10. Arguments — Inline and Schema Declaration

```graphql
# Schema declaration
type Query {
  book(id: ID!): Book                          # required argument
  books(limit: Int = 10, offset: Int = 0): [Book!]!   # optional with defaults
  searchBooks(query: String!, genre: String): [Book!]! # mix of required and optional
}

type Book {
  reviews(minRating: Int): [Review!]!          # argument on non-root field
}

# Usage
query {
  book(id: "42") { title }
  books(limit: 5) { title }
  searchBooks(query: "patterns") { title author { name } }
}
```

---

## 11. Variables — Declaration and Usage

```graphql
# Query with variable declaration
query GetBook($bookId: ID!, $limit: Int = 10) {
  book(id: $bookId) { title }
  books(limit: $limit) { title }
}
```

```json
// Variables JSON (sent with query in request body)
{
  "bookId": "42",
  "limit": 5
}
```

**Complete request body:**
```json
{
  "query": "query GetBook($bookId: ID!) { book(id: $bookId) { title } }",
  "variables": { "bookId": "42" },
  "operationName": "GetBook"
}
```

**Variable with input type:**
```graphql
mutation CreateBook($input: CreateBookInput!) {
  createBook(input: $input) { id title }
}
```
```json
{ "input": { "title": "Clean Code", "authorId": "5" } }
```

---

## 12. Mutations — Schema and Operations

```graphql
# Input type for mutation arguments
input CreateBookInput {
  title:     String!
  authorId:  ID!
  isbn:      String
  pageCount: Int
}

# Mutation type
type Mutation {
  createBook(input: CreateBookInput!): Book!
  updateBook(id: ID!, input: UpdateBookInput!): Book!
  deleteBook(id: ID!): Boolean!
}
```

```graphql
# Mutation operation
mutation CreateNewBook($input: CreateBookInput!) {
  createBook(input: $input) {
    id
    title
    author { name }
  }
}
```

**Key rules:**
- Use input types for mutation arguments — never inline scalars for complex inputs
- Return the modified object (not just `Boolean` or `ID`)
- Mutations execute serially — multiple mutation fields run in order

---

## 13. Subscriptions — Schema and Operations

```graphql
# Schema
type Subscription {
  bookAdded: Book!
  reviewAdded(bookId: ID!): Review!
}

# Operation
subscription OnNewBook {
  bookAdded {
    id
    title
    author { name }
  }
}
```

- Uses WebSocket (or SSE) — not HTTP request/response
- Connection stays open; server pushes data on events
- Spring implementation: `@SubscriptionMapping` returns `Flux<T>`
- Add `spring-boot-starter-websocket` for WebSocket transport

---

## 14. Introspection

```graphql
# List all types
query { __schema { types { name kind description } } }

# Inspect a specific type
query { __type(name: "Book") { fields { name type { name kind } } } }
```

**Security:** Disable in production:
```properties
spring.graphql.schema.introspection.enabled=false
```

---

## 15. GraphQL Error Format

```json
{
  "data": {
    "book": null
  },
  "errors": [
    {
      "message": "Book not found with id: 999",
      "locations": [{ "line": 2, "column": 3 }],
      "path": ["book"],
      "extensions": {
        "code": "NOT_FOUND"
      }
    }
  ]
}
```

**Critical:** HTTP status is always 200. Always check the `errors` array — `data.errors` present means something failed even if `response.status === 200`.

**Partial success:** `data` and `errors` can coexist — some fields resolved, others failed.

---

## 16. Aliases

```graphql
query CompareTwoBooks {
  firstBook: book(id: "1") {   # "firstBook" is the alias
    title pageCount
  }
  secondBook: book(id: "2") {  # "secondBook" is the alias
    title pageCount
  }
}

# Response keys are the aliases, not "book"
# { "data": { "firstBook": {...}, "secondBook": {...} } }
```

**Also used for:** renaming fields in the response (`bookTitle: title`), requesting same field with different filters in one query.

---

## 17. Fragments — Named and Inline

```graphql
# Named fragment definition
fragment BookFields on Book {
  id title pageCount available
  author { name }
}

# Usage — spreads the fragment
query GetBook { book(id: "1") { ...BookFields reviews { rating } } }
query GetBooks { books { ...BookFields } }

# Inline fragment — for union/interface types
query Search {
  search(term: "patterns") {
    __typename
    ... on Book   { title pageCount }
    ... on Author { name books { title } }
  }
}
```

---

## 18. Directives

```graphql
# @include — include field when condition is true
query GetBook($showReviews: Boolean!) {
  book(id: "1") {
    title
    reviews @include(if: $showReviews) { rating text }
  }
}

# @skip — skip field when condition is true
query GetBook($compact: Boolean!) {
  book(id: "1") {
    title
    pageCount @skip(if: $compact)
    author @skip(if: $compact) { name }
  }
}
```

**Schema directive:**
```graphql
# @deprecated — marks a field as deprecated but still functional
isbn10: String @deprecated(reason: "Use isbn13 instead")
```

---

## 19. Spring for GraphQL — Maven Dependency

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-graphql</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
<!-- For subscriptions via WebSocket: -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-websocket</artifactId>
</dependency>
```

---

## 20. Schema File Location and Auto-Configuration

```
src/main/resources/
└── graphql/
    ├── schema.graphqls      ← auto-discovered by Spring for GraphQL
    ├── book.graphqls        ← or split by domain
    └── author.graphqls
```

```properties
spring.graphql.graphiql.enabled=true          # browser IDE at /graphiql
spring.graphql.path=/graphql                  # endpoint path (default)
spring.graphql.schema.introspection.enabled=false  # disable in production
spring.graphql.websocket.path=/graphql-ws     # WebSocket for subscriptions
```

---

## 21. @QueryMapping

```java
@Controller   // NOT @RestController
public class BookController {

    @QueryMapping                                  // method name = "book" matches Query.book
    public Book book(@Argument String id) {        // @Argument extracts the "id" argument
        return bookService.findById(Long.parseLong(id));
    }

    @QueryMapping
    public List<Book> books() {
        return bookService.findAll();
    }

    @QueryMapping
    public List<Book> searchBooks(@Argument String query) {
        return bookService.search(query);
    }
}
```

---

## 22. @MutationMapping

```java
@Controller
public class BookMutationController {

    @MutationMapping
    public Book createBook(@Argument CreateBookInput input) {
        // Spring for GraphQL deserializes the GraphQL input type to CreateBookInput
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

```java
// Java record for CreateBookInput — field names must match SDL input type fields
public record CreateBookInput(String title, String authorId, String isbn, Integer pageCount) {}
```

---

## 23. @SchemaMapping — Non-Root Field Resolvers

```java
@Controller
public class BookController {

    // Resolves the "author" field on the Book type
    // source = the Book instance returned by the parent resolver
    @SchemaMapping(typeName = "Book", field = "author")
    public Author author(Book source) {
        return authorService.findById(source.getAuthorId());
    }

    // Resolves the "reviews" field on the Book type
    @SchemaMapping(typeName = "Book", field = "reviews")
    public List<Review> reviews(Book source) {
        return reviewService.findByBookId(source.getId());
    }
}
```

Use `@SchemaMapping` when:
- The Java object doesn't have the field value directly (e.g., only has `authorId`, not `Author`)
- The field requires a separate database query to resolve

---

## 24. @SubscriptionMapping

```java
@Controller
public class BookSubscriptionController {

    @SubscriptionMapping
    public Flux<Book> bookAdded() {                         // return type must be Publisher<T>
        return bookEventPublisher.getBookAddedFlux();
    }

    @SubscriptionMapping
    public Flux<Review> reviewAdded(@Argument String bookId) {
        return bookEventPublisher.getReviewFlux()
            .filter(review -> review.getBookId().equals(bookId));
    }
}
```

---

## 25. @BatchMapping — N+1 Solution Preview

```java
// Instead of running N queries (one per book):
@SchemaMapping(typeName = "Book", field = "author")
public Author author(Book book) { return authorService.findById(book.getAuthorId()); }

// Use @BatchMapping — called once with ALL books:
@BatchMapping(typeName = "Book", field = "author")
public Map<Book, Author> author(List<Book> books) {
    List<Long> ids = books.stream().map(Book::getAuthorId).distinct().toList();
    Map<Long, Author> byId = authorService.findAllByIds(ids).stream()
        .collect(Collectors.toMap(Author::getId, a -> a));
    return books.stream()
        .collect(Collectors.toMap(b -> b, b -> byId.get(b.getAuthorId())));
}
// Returns Map<Book, Author> — framework distributes values to each book
```

Full N+1 / DataLoader coverage in Day 32.

---

## 26. @Argument Reference

```java
// Scalar argument
@QueryMapping
public Book book(@Argument String id) { ... }

// Multiple arguments
@MutationMapping
public Book updateBook(@Argument String id, @Argument UpdateBookInput input) { ... }

// Explicit name when parameter name differs from schema argument name
@QueryMapping
public List<Book> searchBooks(@Argument("query") String searchTerm) { ... }

// Collection argument
@QueryMapping
public List<Book> booksByIds(@Argument List<String> ids) { ... }
```

---

## 27. Schema Design Best Practices

**Naming:**
```graphql
type Author { ... }             # PascalCase types
input CreateAuthorInput { ... } # Input types: TypeName + action suffix
query { author(id: ID!) ... }   # camelCase fields and operations
enum BookGenre { FICTION NON_FICTION }  # SCREAMING_SNAKE_CASE enums
```

**Nullability:**
- Non-null by default for fields that will always be present
- Only mark nullable when the field can genuinely be absent

**Mutations:**
- Always use input types: `createBook(input: CreateBookInput!): Book!`
- Always return the mutated object — not just `Boolean` or `ID`

---

## 28. Pagination — Connection Pattern (Relay)

```graphql
type BookConnection {
  edges:      [BookEdge!]!
  pageInfo:   PageInfo!
  totalCount: Int!
}

type BookEdge {
  node:   Book!
  cursor: String!
}

type PageInfo {
  hasNextPage:     Boolean!
  hasPreviousPage: Boolean!
  startCursor:     String
  endCursor:       String
}

type Query {
  books(first: Int, after: String, last: Int, before: String): BookConnection!
}
```

```graphql
query GetBooksPage($cursor: String) {
  books(first: 10, after: $cursor) {
    edges { node { title author { name } } cursor }
    pageInfo { hasNextPage endCursor }
    totalCount
  }
}
```

---

## 29. Error Handling Patterns

**Implicit (throw exception → goes to `errors` array):**
```java
@QueryMapping
public Book book(@Argument String id) {
    return bookRepository.findById(Long.parseLong(id))
        .orElseThrow(() -> new RuntimeException("Book not found: " + id));
}
// Response: { "data": { "book": null }, "errors": [{ "message": "Book not found: 999" }] }
```

**Explicit — result union types (type-safe, errors in `data`):**
```graphql
union DeleteBookResult = Book | NotFoundError | PermissionDeniedError

type Mutation {
  deleteBook(id: ID!): DeleteBookResult!
}
```
```graphql
mutation {
  deleteBook(id: "999") {
    ... on Book             { id title }
    ... on NotFoundError    { message id }
    ... on PermissionDeniedError { message requiredRole }
  }
}
```

---

## 30. Field Deprecation — Versioning Without Versions

```graphql
type Book {
  isbn13: String!
  isbn10: String @deprecated(reason: "Use isbn13 instead. Removing 2026-06-01.")
}
```

**Workflow:**
1. Add new field (`isbn13`)
2. Deprecate old field (`isbn10` with `@deprecated`)
3. Communicate timeline to consumers
4. Monitor field usage (Apollo Studio, custom metrics)
5. Remove field after usage reaches zero

Clients using deprecated fields still work — they receive a warning in tooling.

---

## 31. SDL Descriptions (Documentation)

```graphql
"Short single-line description"
type Author {
  "The author's full legal name"
  name: String!

  """
  All books published by this author.
  May return an empty list for newly registered authors.
  """
  books: [Book!]!
}
```

Descriptions appear as:
- Tooltips in GraphiQL / GraphQL Playground
- Documentation in Apollo Studio
- Comments in client code generated by Apollo Codegen / graphql-code-generator

---

## 32. Common GraphQL Mistakes

| Mistake | Consequence | Fix |
|---------|-------------|-----|
| Not naming operations | Undebuggable server logs, no APM grouping | Always use named operations |
| Using `@RestController` for GraphQL resolvers | Response handling conflicts | Use `@Controller` only |
| Forgetting `@EnableMethodSecurity` with Spring Security | Security annotations silently ignored | Add if using `@PreAuthorize` alongside GraphQL |
| Not disabling introspection in production | Exposes full API schema to attackers | `spring.graphql.schema.introspection.enabled=false` |
| Checking HTTP status for GraphQL errors | Misses errors — GraphQL always returns 200 | Always check `errors` array in response body |
| N+1 queries without `@BatchMapping` | Hundreds of DB queries per request | Use `@BatchMapping` or DataLoader |
| Using `@RestController` in GraphQL | Serialization conflicts | Use `@Controller` |
| Nullable fields without purpose | Clients can't trust any field value | Be intentional — non-null unless genuinely optional |
| Inline scalars on mutations instead of input types | Poor maintainability and reusability | Always use input types for mutation arguments |
| Not returning modified object from mutations | Extra client round-trip to get new ID/state | Return the mutated entity |

---

## 33. DataFetchingEnvironment — Look-Ahead and Request Context

`DataFetchingEnvironment` is injected automatically into any resolver method that declares it as a parameter. It exposes request-level context the Spring for GraphQL annotations don't surface individually.

```java
@QueryMapping
public Book book(@Argument String id, DataFetchingEnvironment env) {
    boolean needsAuthor = env.getSelectionSet().contains("author");
    return bookService.findByIdWithOptionalAuthor(Long.parseLong(id), needsAuthor);
}
```

**Available context:**

| Method | Returns |
|--------|---------|
| `env.getSelectionSet()` | Fields the client actually requested |
| `env.getArguments()` | All arguments as a raw `Map<String, Object>` |
| `env.getSource()` | The parent object (same as `source` in `@SchemaMapping`) |
| `env.getGraphQlContext()` | Spring's `GraphQLContext` (security, headers, etc.) |
| `env.getField()` | The field definition from the schema |

**Use cases:**
- **Look-ahead optimization:** skip a SQL JOIN when the client didn't request the associated type
- **Security context access:** read the authenticated user inside a resolver without method parameter threading
- **Custom instrumentation:** log which fields were requested and resolution time

**Not for N+1:** Use `@BatchMapping` for the N+1 problem across a list of parents. `DataFetchingEnvironment` look-ahead is for single-object optimization only.

---

## 34. @GraphQlExceptionHandler — Structured Error Responses

`@GraphQlExceptionHandler` intercepts resolver exceptions before they reach the default error serializer, letting you produce clean, client-safe error messages with stable error codes.

```java
// In @ControllerAdvice (recommended — centralized)
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

**Standard `ErrorType` values:**

| `ErrorType` | When to use |
|-------------|-------------|
| `NOT_FOUND` | Resource doesn't exist |
| `BAD_REQUEST` | Invalid client input |
| `FORBIDDEN` | Authenticated but not authorized |
| `UNAUTHORIZED` | Authentication required |
| `INTERNAL_ERROR` | Unhandled server error (default) |

**Rule:** `message` is a public-facing string. Never expose stack traces, JPA class names, or database error details.

---

## 35. Automatic Persisted Queries (APQ)

APQ replaces full query strings with a SHA-256 hash in production requests, reducing bandwidth and enabling query allowlisting.

**Request flow:**
```
1. Client sends: { "extensions": { "persistedQuery": { "sha256Hash": "a1b2c3..." } }, "variables": {...} }
2. Server: hash known → execute immediately (fast path)
3. Server: hash unknown → return PERSISTED_QUERY_NOT_FOUND
4. Client: re-sends with hash + full query string (registers it)
5. All future requests: hash only
```

**Benefits:**
- **Bandwidth:** hash (~64 bytes) vs query string (100–1000+ bytes)
- **Security (allowlist mode):** server rejects any hash not pre-registered at build time — no arbitrary query execution
- **CDN caching:** stable hash enables HTTP layer caching of responses

**Where it lives:** APQ is handled at the **gateway layer** (Apollo Router, reverse proxy), not in the Spring Boot application server. Spring for GraphQL supports `WebInterceptor` hooks for custom APQ implementation if a gateway isn't used.

**What to know day-to-day:**
- Apollo Client enables APQ by default — your server will receive hashed requests from any Apollo Client frontend
- GraphiQL and Postman always send full query strings (no APQ)
- For development, APQ can be disabled in Apollo Client config

---

## 36. Looking Ahead — Day 32: GraphQL Client

**Day 32 covers the client side:**
- **Apollo Client** (React) and **Apollo Angular** — executing queries and mutations from the frontend
- **Query hooks** — `useQuery`, `useMutation`, `useSubscription` in React
- **GraphQL Playground** and **GraphiQL** — browser-based API exploration tools
- **N+1 / DataLoader** — full treatment of the batching problem and solution
- **Apollo InMemoryCache** — automatic client-side caching and cache normalization
- **Error handling** in Apollo Client — `error`, `loading`, `data` response states
- **Query optimization** — field selection, query batching
- **Testing GraphQL APIs** with Postman

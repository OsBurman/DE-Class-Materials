# Day 31 Part 1 — GraphQL: Schema, Types, Queries, Mutations, and Subscriptions
## Slide Descriptions

---

### Slide 1 — Title Slide

**Title:** Day 31 Part 1: GraphQL — A New Way to Think About APIs

**Subtitle:** Single endpoint, typed schema, client-driven data fetching

**Learning Objectives:**
- Explain what GraphQL is and the REST limitations it addresses
- Compare GraphQL and REST across fetching model, versioning, and tooling
- Understand the GraphQL request/response architecture
- Read and write Schema Definition Language (SDL)
- Use all built-in scalar types and apply non-null and list modifiers
- Define object types and relationships between them
- Write GraphQL queries including nested field selection and named operations
- Write GraphQL mutations with proper input types and return types
- Understand subscriptions for real-time data and when to use them

---

### Slide 2 — The REST Problems GraphQL Solves

**Title:** What REST Gets Right — and Where It Breaks Down

**Content:**

REST works well for simple CRUD resources. As APIs grow more complex, three problems appear repeatedly:

**Problem 1 — Over-fetching:**
You need a book's title and author name. The REST endpoint returns the whole book object.
```
GET /api/books/1

Response: {
  "id": 1,
  "title": "Clean Code",
  "author": { "id": 5, "name": "Robert Martin", "email": "...", "bio": "...", "website": "..." },
  "isbn": "978-0132350884",
  "publisher": "Prentice Hall",
  "publicationDate": "2008-08-01",
  "pageCount": 431,
  "categories": [...],
  "reviews": [...],
  ...
}
```
The client needed 2 fields. The server sent 20+. Wasted bandwidth, slower mobile performance.

**Problem 2 — Under-fetching (the N+1 request problem):**
You need to display a list of books with each book's author name.
```
GET /api/books              → list of books (but only has authorId, not name)
GET /api/authors/1          → to get author for book 1
GET /api/authors/3          → to get author for book 2
GET /api/authors/7          → to get author for book 3
... (one request per book)
```
Requires N+1 HTTP round trips. Slow. Brittle.

**Problem 3 — Endpoint explosion and versioning:**
Different clients (web, mobile, admin) need different shapes of the same data.
```
/api/books                      ← desktop web
/api/books/mobile               ← mobile (fewer fields)
/api/books/v2                   ← breaking change workaround
/api/books/v2/mobile            ← combination
```
Each new client need potentially adds a new endpoint. Versioning compounds the problem.

**GraphQL addresses all three directly.**

---

### Slide 3 — GraphQL Overview

**Title:** GraphQL — What It Is

**Content:**

GraphQL is a query language for APIs and a runtime for executing those queries, developed by Facebook in 2012 and open-sourced in 2015. It is not a database — it's a layer that sits in front of your data sources.

**Core properties:**

**1. Single endpoint:**
```
All requests go to:  POST /graphql
```
The operation type (query, mutation, subscription) is in the request body, not the URL.

**2. Client specifies exactly what it needs:**
```graphql
{
  book(id: 1) {
    title
    author {
      name
    }
  }
}
```
The server returns exactly `title` and `author.name`. Nothing else.

**3. Strongly typed schema:**
The API is defined by a schema written in SDL. The schema is a contract between client and server. Every field has a type. Every type is documented in the schema.

**4. Introspectable:**
Clients can query the schema itself (`__schema`, `__type`) to discover what types and operations are available. This powers IDE autocomplete, GraphQL Playground, and GraphiQL.

**5. Versionless:**
Instead of versioning the API, you add new fields and deprecate old ones. The schema evolves without breaking existing clients.

---

### Slide 4 — GraphQL vs REST Comparison

**Title:** GraphQL vs REST — Side by Side

**Content:**

| Aspect | REST | GraphQL |
|--------|------|---------|
| **Endpoints** | Multiple — one (or more) per resource | Single — `POST /graphql` |
| **Data shape** | Fixed — server decides what's returned | Flexible — client specifies fields |
| **Over-fetching** | Common — endpoint returns full resource | Eliminated — only requested fields returned |
| **Under-fetching** | Common — multiple round trips | Eliminated — nested queries in one request |
| **Type system** | Optional (OpenAPI/Swagger adds it) | Built-in — schema is the contract |
| **Versioning** | Typically versioned (`/v1`, `/v2`) | Versionless — add fields, deprecate old ones |
| **Error format** | HTTP status codes (4xx, 5xx) + body | Always HTTP 200 + `errors` array in body |
| **Caching** | Easy — HTTP GET requests are cacheable | Harder — all requests are POST |
| **File uploads** | Easy with multipart | Non-standard (requires extension) |
| **Learning curve** | Low — familiar HTTP verbs and URLs | Medium — schema, resolver concepts |
| **Best for** | Simple CRUD, public APIs, caching-heavy | Complex data relationships, multiple clients, rapid iteration |

**GraphQL is not universally better — it's better for specific problems.** For a simple CRUD API serving one client, REST is often the right choice. GraphQL shines when you have complex data graphs, multiple clients with different data needs, or teams iterating quickly on a shared API.

---

### Slide 5 — GraphQL Request Architecture

**Title:** How GraphQL Works End to End

**Content:**

```
                        GraphQL Server
┌──────────┐            ┌────────────────────────────────────┐
│  Client  │            │                                    │
│          │            │  1. Parse    — tokenize & build AST │
│ query {  │──POST ──→  │  2. Validate — check against schema │
│   book { │  /graphql  │  3. Execute  — call resolvers       │
│    title │            │  4. Return   — shape matches query  │
│   }      │            │                                    │
│ }        │            │  Resolvers fetch from:             │
│          │←── JSON ───│   - Databases (JPA, MongoDB)       │
└──────────┘            │   - REST APIs                      │
                        │   - Microservices                  │
                        │   - In-memory data                 │
                        └────────────────────────────────────┘
```

**Key concept — resolver:** A resolver is a function that knows how to fetch the data for a specific field. Every field in a GraphQL schema has a resolver, though many are handled automatically by the framework. The resolver for `book` queries the database. The resolver for `author` within a book result fetches the author. The server orchestrates all resolvers to assemble the response.

**The response always mirrors the query shape:**
```
Query                   Response
─────────────────       ────────────────────────
query {                 {
  book(id: 1) {           "data": {
    title                   "book": {
    author {                  "title": "Clean Code",
      name                    "author": {
    }                           "name": "Robert Martin"
  }                           }
}                           }
                          }
                        }
```

---

### Slide 6 — Schema Definition Language (SDL)

**Title:** SDL — The Contract Between Client and Server

**Content:**

The GraphQL schema is written in Schema Definition Language (SDL). It describes every type, every field, and every operation the API supports. The schema is the single source of truth — it defines what queries clients can make and what shape the responses will have.

**A complete minimal schema:**
```graphql
# Scalar types are primitives — String, Int, Float, Boolean, ID

# Object type — defines a named type with fields
type Book {
  id: ID!
  title: String!
  author: Author!
  pageCount: Int
  available: Boolean!
}

type Author {
  id: ID!
  name: String!
  books: [Book!]!
}

# The Query type is the entry point for reads
# Every field on Query is a root-level query operation
type Query {
  book(id: ID!): Book
  books: [Book!]!
  author(id: ID!): Author
}

# The Mutation type is the entry point for writes
type Mutation {
  createBook(input: CreateBookInput!): Book!
  deleteBook(id: ID!): Boolean!
}

# Input types are used for mutation arguments (not query return types)
input CreateBookInput {
  title: String!
  authorId: ID!
  pageCount: Int
}
```

**Two approaches to building a GraphQL server:**
- **Schema-first:** write the SDL file first, then implement resolvers that satisfy it. Promotes clear API contract up front.
- **Code-first:** annotate Java code and the framework generates the schema. Keeps schema and code in sync automatically.

Spring for GraphQL uses **schema-first** by default — SDL lives in `src/main/resources/graphql/*.graphqls`.

---

### Slide 7 — Scalar Types

**Title:** Built-in Scalar Types — The Primitives of GraphQL

**Content:**

Scalars are leaf types — they don't have sub-fields. They're the primitive values returned for individual fields.

**Five built-in scalars:**

| Scalar | Maps to (Java) | Description |
|--------|----------------|-------------|
| `String` | `String` | UTF-8 text |
| `Int` | `int` / `Integer` | 32-bit signed integer |
| `Float` | `double` / `Double` | Double-precision floating point |
| `Boolean` | `boolean` / `Boolean` | `true` or `false` |
| `ID` | `String` or `Long` | Unique identifier — serialized as String, can be any format |

**Non-null modifier `!`:**
By default, every field in GraphQL is nullable — the server can return `null` for it. The `!` modifier makes a field non-null — the server guarantees it will never return `null`.

```graphql
title: String      # can be null
title: String!     # guaranteed non-null

author: Author     # can be null
author: Author!    # guaranteed non-null
```

**List modifier `[]`:**
```graphql
books: [Book]      # nullable list of nullable Books
books: [Book!]     # nullable list of non-null Books (list can be null, items cannot)
books: [Book!]!    # non-null list of non-null Books (neither list nor items can be null)
books: [Book]!     # non-null list of nullable Books (list cannot be null, items can)
```

**In practice:** `[Book!]!` is the most common pattern for "give me a list of books that definitely exists and contains real Book objects."

**Custom scalars:** GraphQL allows defining custom scalars for domain-specific types like `Date`, `DateTime`, `URL`, `Email`, or `JSON`. Spring for GraphQL provides built-in support for `java.time` types via extended scalars.

---

### Slide 8 — Object Types and Relationships

**Title:** Object Types — Building the Data Graph

**Content:**

Object types have named fields, each with its own type. Fields can reference other object types — this is what makes GraphQL a graph API.

```graphql
type Book {
  id:           ID!
  title:        String!
  isbn:         String
  pageCount:    Int
  available:    Boolean!
  author:       Author!         # relationship to Author
  reviews:      [Review!]!      # list relationship to Review
  categories:   [String!]!      # list of scalars
  publishedAt:  String          # nullable — not all books have a date
}

type Author {
  id:       ID!
  name:     String!
  email:    String
  books:    [Book!]!            # bidirectional relationship — Author → Books
}

type Review {
  id:        ID!
  rating:    Int!               # 1–5
  text:      String
  reviewer:  String!
  book:      Book!              # Review → Book back-reference
}
```

**SDL naming conventions:**
- Type names: `PascalCase` — `Book`, `Author`, `CreateBookInput`
- Field names: `camelCase` — `pageCount`, `publishedAt`, `authorId`
- Enum values: `SCREAMING_SNAKE_CASE` — `FICTION`, `NON_FICTION`
- Query/mutation names: `camelCase` — `getBook`, `createBook`, `deleteReview`

**Circular references are fine.** `Book` has an `Author`, `Author` has `[Book]`. GraphQL handles this because the client controls traversal depth through the query. The server never auto-traverses — it only resolves what the client asks for.

---

### Slide 9 — Queries

**Title:** GraphQL Queries — Requesting Data

**Content:**

A query asks for data. It maps to the `Query` type in the schema.

**Schema:**
```graphql
type Query {
  book(id: ID!): Book
  books: [Book!]!
  booksByAuthor(authorId: ID!): [Book!]!
}
```

**Queries:**

**Basic query — anonymous shorthand (development only):**
```graphql
{
  books {
    id
    title
  }
}
```

**Named query (recommended):**
```graphql
query GetAllBooks {
  books {
    id
    title
    author {
      name
    }
  }
}
```

**Query with an argument:**
```graphql
query GetBook {
  book(id: "1") {
    title
    pageCount
    available
    author {
      name
      books {
        title
      }
    }
  }
}
```

**Rules:**
- You must request at least one field on every object type — you can't just say `author` without selecting fields from `Author`
- Fields not listed in the query are never returned — even if the server has them
- You can nest as deep as the schema allows
- Operation names (`GetBook`, `GetAllBooks`) are optional but strongly recommended — they appear in server logs, error messages, and monitoring tools

---

### Slide 10 — Named Queries and Field Selection Depth

**Title:** Named Operations and Nested Queries

**Content:**

**Why name your operations:**
- Appear in server-side logs for debugging and performance monitoring
- Required when sending multiple operations in one document
- Best practice for any production query

**Deep nesting — traversing the data graph in a single request:**
```graphql
query GetBookWithFullDetails {
  book(id: "1") {
    title
    isbn
    author {
      name
      books {          # all books by this author
        title
        reviews {      # reviews for each of those books
          rating
          text
          reviewer
        }
      }
    }
    reviews {          # reviews for the original book
      rating
      text
    }
  }
}
```

In REST, this would require:
1. `GET /api/books/1`
2. `GET /api/authors/{authorId}`
3. `GET /api/authors/{authorId}/books`
4. For each of N author books: `GET /api/books/{id}/reviews`
5. `GET /api/books/1/reviews`

GraphQL retrieves all of this in **one request**.

**Schema introspection queries (built-in):**
```graphql
# Discover all types in the schema
query {
  __schema {
    types {
      name
      kind
    }
  }
}

# Inspect a specific type
query {
  __type(name: "Book") {
    fields {
      name
      type {
        name
        kind
      }
    }
  }
}
```

**Security note:** Disable introspection in production to prevent exposing your schema to attackers. Spring for GraphQL provides a property for this.

---

### Slide 11 — Mutations

**Title:** GraphQL Mutations — Modifying Data

**Content:**

Mutations create, update, or delete data. They map to the `Mutation` type.

**Schema:**
```graphql
input CreateBookInput {
  title:      String!
  authorId:   ID!
  isbn:       String
  pageCount:  Int
}

input UpdateBookInput {
  title:      String       # nullable — only update provided fields
  isbn:       String
  pageCount:  Int
  available:  Boolean
}

type Mutation {
  createBook(input: CreateBookInput!): Book!
  updateBook(id: ID!, input: UpdateBookInput!): Book!
  deleteBook(id: ID!): Boolean!
}
```

**Why use `input` types instead of inline arguments?**
- Groups related arguments together
- Reusable across multiple mutations
- Better for validation and documentation
- Required by some clients (Apollo)
- Convention in production GraphQL APIs

**Mutations:**
```graphql
mutation CreateBook {
  createBook(input: {
    title: "Clean Code",
    authorId: "5",
    isbn: "978-0132350884",
    pageCount: 431
  }) {
    id
    title
    author {
      name
    }
  }
}

mutation DeleteBook {
  deleteBook(id: "1")
}
```

**Best practice — always return the modified object:**
Return the created/updated `Book` from `createBook`/`updateBook` instead of just `Boolean` or `ID`. The client gets the server-assigned ID and any computed fields without needing a follow-up query.

**Mutations execute serially** (unlike queries which may execute in parallel). If you send multiple mutation fields, they run in order, top to bottom. This ensures data consistency.

---

### Slide 12 — Subscriptions

**Title:** GraphQL Subscriptions — Real-Time Data

**Content:**

Subscriptions allow clients to subscribe to server events and receive data pushed in real-time. They use WebSocket connections (or Server-Sent Events) instead of HTTP request/response.

**Schema:**
```graphql
type Subscription {
  bookAdded: Book!
  reviewAdded(bookId: ID!): Review!
  bookAvailabilityChanged(bookId: ID!): Book!
}
```

**Subscription operation (sent once to open the stream):**
```graphql
subscription OnBookAdded {
  bookAdded {
    id
    title
    author {
      name
    }
  }
}
```

**How it works:**
```
1. Client sends subscription operation over WebSocket
2. Server registers the subscription
3. Whenever the subscribed event fires (book added, review posted),
   the server pushes a response to the client
4. Client receives event data in the same shape as the subscription selection set
5. Connection stays open until client unsubscribes or disconnects
```

**Spring for GraphQL subscription implementation:**
- Return type is `Publisher<T>` (Project Reactor's `Flux<T>`)
- The framework handles the WebSocket protocol
- Annotate the method with `@SubscriptionMapping`

**When to use subscriptions:**
- ✅ Live notifications (new message, order status change)
- ✅ Real-time collaborative features (document editing, shared whiteboards)
- ✅ Live dashboards (metrics, prices, inventory levels)
- ❌ Polling (use queries on a timer instead — subscriptions have overhead)
- ❌ One-time data fetch (use a query)

**Subscriptions add WebSocket infrastructure complexity.** For simple notification scenarios, polling with queries or Server-Sent Events may be simpler to operate.

---

### Slide 13 — GraphQL Introspection

**Title:** Introspection — Querying the Schema Itself

**Content:**

Introspection is a built-in GraphQL feature that lets clients query the schema to discover what types, fields, queries, and mutations are available.

**Built-in introspection types:**
```graphql
__Schema    # The full schema
__Type      # Information about a specific type
__Field     # Information about a field
__EnumValue # Information about an enum value
__InputValue # Information about an input field or argument
```

**Practical introspection query:**
```graphql
# List all available queries and mutations
query IntrospectOperations {
  __schema {
    queryType { name }
    mutationType { name }
    subscriptionType { name }
    types {
      name
      kind
      description
    }
  }
}
```

**What introspection powers:**
- **GraphiQL / GraphQL Playground** — the browser-based IDE that reads your schema and provides autocomplete
- **Apollo Studio** — schema registry and performance monitoring
- **Code generation** — clients generate TypeScript types from the schema
- **Documentation** — auto-generated API docs

**Security consideration:**
```properties
# Disable introspection in production
spring.graphql.schema.introspection.enabled=false
```

Exposing your full schema in production gives attackers a roadmap to your API. Disable introspection for public-facing production APIs while keeping it enabled in development.

---

### Slide 14 — GraphQL Execution Lifecycle

**Title:** How GraphQL Executes a Request

**Content:**

When a GraphQL request arrives, the server processes it in four phases:

```
Phase 1: PARSE
─────────────────────────────────────────
Client sends:    query { book(id:"1") { title } }
Server builds:   Abstract Syntax Tree (AST)
                 OperationDefinition
                   SelectionSet
                     Field: "book"
                       Argument: id = "1"
                       SelectionSet
                         Field: "title"

Phase 2: VALIDATE
─────────────────────────────────────────
- Does "book" exist on Query type? ✅
- Does "book" accept argument "id" of type ID? ✅
- Does "Book" type have a "title" field? ✅
- Are all required arguments provided? ✅
Validation errors: none → proceed

Phase 3: EXECUTE
─────────────────────────────────────────
- Call resolver for Query.book(id: "1")
  → bookRepository.findById(1L)  → Book entity
- Call resolver for Book.title
  → book.getTitle()  → "Clean Code"
(leaf fields resolve to their scalar values)

Phase 4: RESPONSE
─────────────────────────────────────────
{
  "data": {
    "book": {
      "title": "Clean Code"
    }
  }
}
```

**Validation catches errors before any resolver runs.** If the query references a field that doesn't exist or an argument of the wrong type, the server returns an error without touching the database.

---

### Slide 15 — GraphQL Error Format

**Title:** Errors in GraphQL — Always HTTP 200

**Content:**

GraphQL has a unique error handling model: **all responses are HTTP 200 OK**, even when errors occur. Errors are in the response body.

**Successful response:**
```json
{
  "data": {
    "book": { "title": "Clean Code" }
  }
}
```

**Error response (field-level error):**
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
        "code": "NOT_FOUND",
        "classification": "DataFetchingException"
      }
    }
  ]
}
```

**Partial success — `data` and `errors` coexist:**
```json
{
  "data": {
    "book": {
      "title": "Clean Code",
      "author": null        ← this field failed
    }
  },
  "errors": [
    {
      "message": "Author resolver failed",
      "path": ["book", "author"]
    }
  ]
}
```

**What this means:**
- HTTP 200 doesn't mean success in GraphQL — always check `errors`
- Fields can partially fail — the rest of the response is still useful
- The `path` array tells you exactly which field failed
- The `extensions` object carries custom error codes

**For REST clients used to checking HTTP status codes:** this is a significant shift. Client-side code must check for `errors` in the response body, not just `response.status === 200`.

---

### Slide 16 — Part 1 Summary

**Title:** Part 1 Summary — GraphQL Fundamentals

**Content:**

**Key concepts:**

| Concept | What to remember |
|---------|-----------------|
| **Over-fetching** | REST returns too much; GraphQL returns exactly what you ask for |
| **Under-fetching** | REST requires multiple trips; GraphQL nests in one request |
| **SDL** | The schema is the API contract; schema-first is the Spring for GraphQL default |
| **Scalars** | String, Int, Float, Boolean, ID — plus `!` (non-null) and `[]` (list) modifiers |
| **Object types** | Named types with fields that can reference other types (the "graph") |
| **Query type** | Entry point for reads — every field is a root operation |
| **Mutation type** | Entry point for writes — executes serially; return the modified object |
| **Subscription type** | Entry point for real-time push — WebSocket connection, returns `Publisher<T>` |
| **Execution lifecycle** | Parse → Validate → Execute → Response |
| **Error format** | Always HTTP 200 — check `errors` array in body; partial success is possible |

**SDL quick reference:**
```graphql
type Book {
  id:        ID!          # non-null ID
  title:     String!      # non-null String
  rating:    Float        # nullable Float
  reviews:   [Review!]!   # non-null list of non-null Reviews
}
```

**Part 2 Preview:**
- Arguments and variables — make queries dynamic and reusable
- Aliases — request the same field multiple times with different arguments
- Fragments — reusable selection sets for sharing field lists across queries
- Directives — `@include`, `@skip`, `@deprecated`
- Building the server: Spring for GraphQL, `@QueryMapping`, `@MutationMapping`, `@SchemaMapping`
- Schema design best practices: nullability decisions, pagination patterns, error patterns

# GraphQL Overview and Schema — Day 31

---

## 1. What Is GraphQL?

GraphQL is a **query language for APIs** and a **runtime for executing those queries**,
created by Facebook in 2012 and open-sourced in 2015.

The core idea: instead of the server deciding what data each endpoint returns,
**the client asks for exactly what it needs** — and gets exactly that, nothing more.

---

## 2. GraphQL vs REST — The Problems GraphQL Solves

### The REST Problems

**Over-fetching:** You need just a book title, but the endpoint returns the full book object
with 20 fields including fields you'll never display.

```
GET /api/books/42
→ {
    "id": 42,
    "title": "Clean Code",      ← I need this
    "author": "Robert Martin",  ← and this
    "isbn": "978-0132350884",
    "publisher": "Prentice Hall",
    "publishedYear": 2008,
    "pageCount": 431,
    "weight": 0.82,
    "dimensions": {...},        ← I don't need any of this
    "categories": [...],
    "tags": [...],
    "inventory": {...},
    ...20 more fields
  }
```

**Under-fetching (N+1 problem):** You need a list of books AND each book's author details.
With REST, you need multiple round trips:

```
GET /api/books          → [{ id:1 }, { id:2 }, { id:3 }]
GET /api/authors/101    → (for book 1)
GET /api/authors/102    → (for book 2)
GET /api/authors/103    → (for book 3)
```
4 round trips for data you could have fetched at once.

**Multiple endpoint explosion:** As features grow, REST APIs accumulate endpoints:
```
/api/books
/api/books/:id
/api/books/:id/reviews
/api/books/:id/reviews/:reviewId
/api/authors
/api/authors/:id/books
/api/users/:id/reading-list
/api/users/:id/reading-list/:bookId
...
```
Each new client feature potentially requires a new endpoint or a server change.

---

### The GraphQL Solution

GraphQL replaces all of those with **one endpoint**: `POST /graphql`

The client sends a **query document** describing exactly the shape of data it needs.
The server returns exactly that shape — no more, no less.

```graphql
# I need book title, author name, and first 3 reviews only
query {
  book(id: 42) {
    title
    author {
      name
    }
    reviews(limit: 3) {
      rating
      comment
    }
  }
}
```

Response shape mirrors the query exactly:
```json
{
  "data": {
    "book": {
      "title": "Clean Code",
      "author": { "name": "Robert C. Martin" },
      "reviews": [
        { "rating": 5, "comment": "Life-changing" }
      ]
    }
  }
}
```

---

### GraphQL vs REST Comparison Table

| Aspect              | REST                             | GraphQL                               |
|---------------------|----------------------------------|---------------------------------------|
| **Endpoints**       | Many (`/books`, `/authors`, ...) | One (`POST /graphql`)                 |
| **Data fetching**   | Server-defined shape             | Client-defined shape                  |
| **Over-fetching**   | Common                           | Eliminated                            |
| **Under-fetching**  | Common (multiple round trips)    | Eliminated (nested queries)           |
| **Type system**     | Informal (OpenAPI/Swagger)       | First-class — schema IS the contract  |
| **Versioning**      | `/v1`, `/v2`, etc.               | Schema evolution (add fields, deprecate) |
| **Real-time**       | Polling or WebSockets (manual)   | Subscriptions (built-in)              |
| **Caching**         | HTTP caching (URLs)              | Complex (queries not cacheable by URL)|
| **Learning curve**  | Low                              | Higher (schema, resolvers, DataLoader)|
| **Best for**        | Simple CRUD, public APIs         | Complex data, mobile, SPAs            |

> **Trade-offs:** GraphQL is not always better. REST is simpler, HTTP caching works out of
> the box, and public APIs are easier to document with REST. Use GraphQL when clients need
> flexible querying and you control both frontend and backend.

---

## 3. GraphQL Architecture

```
┌─────────────────────────────────────────────────────────┐
│                     Client                              │
│   (React/Angular/Mobile — sends query documents)       │
└─────────────────────────┬───────────────────────────────┘
                          │ POST /graphql
                          │ { "query": "{ books { title } }" }
                          ▼
┌─────────────────────────────────────────────────────────┐
│                  GraphQL Runtime                        │
│                                                         │
│  1. PARSE     — tokenize and parse the query string     │
│  2. VALIDATE  — check against the schema                │
│  3. EXECUTE   — call resolvers for each field           │
└──────┬──────────────────┬──────────────────┬────────────┘
       │                  │                  │
       ▼                  ▼                  ▼
  BookResolver      AuthorResolver      ReviewResolver
  (database)        (database)          (database/cache)
```

Three key concepts:
- **Schema** — the contract: what types exist, what queries/mutations are available
- **Resolvers** — functions that fetch the actual data for each field
- **Execution engine** — the runtime that parses queries, validates them, and calls resolvers

---

## 4. Schema Definition Language (SDL)

The **SDL** is how you define your GraphQL schema. It's a human-readable syntax
independent of any programming language.

```graphql
# This is a comment in SDL

# Define a custom object type
type Book {
  id:          ID!
  title:       String!
  isbn:        String
  pageCount:   Int
  rating:      Float
  inStock:     Boolean!
  genre:       Genre!
  author:      Author!
  reviews:     [Review!]!
}
```

The schema lives in `.graphqls` (or `.graphql`) files in your project.
Spring for GraphQL looks in `src/main/resources/graphql/` by default.

---

## 5. Types and Scalars

GraphQL has **built-in scalar types** (leaf values — the data the fields actually return):

| Scalar    | Description                                             | Example values          |
|-----------|---------------------------------------------------------|-------------------------|
| `String`  | UTF-8 text                                              | `"Clean Code"`, `""`   |
| `Int`     | 32-bit signed integer                                   | `42`, `-1`, `0`         |
| `Float`   | Double-precision floating point                         | `4.5`, `19.99`, `3.14` |
| `Boolean` | `true` or `false`                                       | `true`, `false`         |
| `ID`      | Unique identifier — serialized as String, treated as opaque | `"1"`, `"abc-123"` |

You can also define **custom scalars**:
```graphql
scalar Date      # represented as ISO-8601 string "2024-01-15"
scalar DateTime  # "2024-01-15T14:30:00Z"
scalar URL       # "https://bookstore.com/cover.jpg"
scalar Email     # "alice@bookstore.com"
```

---

## 6. Object Types and Fields

```graphql
# ── Object types ────────────────────────────────────────

type Author {
  id:        ID!           # ! = non-null — this field will NEVER be null
  name:      String!       # required
  email:     String        # nullable — author may not have a public email
  birthYear: Int           # nullable
  books:     [Book!]!      # non-null list of non-null Books
                           # the list always exists; each item is never null
}

type Review {
  id:         ID!
  rating:     Int!         # 1–5
  comment:    String       # nullable — rating without comment is valid
  reviewer:   String!
  createdAt:  String!      # we'd use custom scalar Date in production
  book:       Book!
}

# ── Enums ─────────────────────────────────────────────────
# Enums restrict a field to a fixed set of values

enum Genre {
  FICTION
  NON_FICTION
  SCIENCE
  TECHNOLOGY
  BIOGRAPHY
  MYSTERY
  FANTASY
}

enum SortOrder {
  TITLE_ASC
  TITLE_DESC
  RATING_ASC
  RATING_DESC
  NEWEST_FIRST
}

# ── Interfaces ────────────────────────────────────────────
# Interfaces define a common contract that types can implement

interface Node {
  id: ID!  # all types implementing Node have an id
}

# Types implementing Node must include the id field
type Book implements Node {
  id:        ID!
  title:     String!
  isbn:      String
  pageCount: Int
  rating:    Float
  inStock:   Boolean!
  genre:     Genre!
  price:     Float!
  author:    Author!
  reviews:   [Review!]!
}

# ── Unions ─────────────────────────────────────────────────
# Unions allow a field to return one of several different types
# (unlike Interface, they share no common fields)

union SearchResult = Book | Author | Review

# Usage in a query: you'd use inline fragments to handle each type
# { search(query: "Martin") { ... on Book { title } ... on Author { name } } }

# ── Input types ────────────────────────────────────────────
# Input types are used for mutation arguments (can't use regular types for input)

input CreateBookInput {
  title:     String!
  isbn:      String!
  pageCount: Int
  genre:     Genre!
  price:     Float!
  authorId:  ID!
}

input UpdateBookInput {
  title:     String      # all fields optional for partial update
  pageCount: Int
  price:     Float
  inStock:   Boolean
}
```

---

## 7. The Root Types — Query, Mutation, Subscription

Every GraphQL schema has up to three **root types**. These are special — they define the
entry points into your graph.

```graphql
# ── Query — read operations (GET equivalent) ──────────────

type Query {
  # Fetch all books (optionally filtered/sorted)
  books(genre: Genre, sortBy: SortOrder, limit: Int): [Book!]!

  # Fetch a single book by ID — returns null if not found
  book(id: ID!): Book

  # Fetch a single author by ID
  author(id: ID!): Author

  # Search across books and authors
  search(query: String!): [SearchResult!]!
}

# ── Mutation — write operations (POST/PUT/DELETE equivalent) ──

type Mutation {
  # Create a new book
  createBook(input: CreateBookInput!): Book!

  # Update an existing book
  updateBook(id: ID!, input: UpdateBookInput!): Book!

  # Delete a book — returns the ID of the deleted book
  deleteBook(id: ID!): ID!

  # Add a review to a book
  addReview(bookId: ID!, rating: Int!, comment: String): Review!
}

# ── Subscription — real-time events (WebSocket) ───────────

type Subscription {
  # Get notified whenever a new book is added to inventory
  bookAdded: Book!

  # Get notified when a book's stock status changes
  bookStockChanged(bookId: ID!): Book!

  # Get notified when a new review is posted for a specific book
  newReview(bookId: ID!): Review!
}
```

---

## 8. Nullability — An Important Design Decision

In GraphQL, fields are **nullable by default**.
Append `!` to make a field **non-null** (required).

```graphql
title: String     # nullable — can return null (use when data might not exist)
title: String!    # non-null — MUST return a value (never null)

reviews: [Review]   # nullable list of nullable reviews
reviews: [Review!]  # nullable list of non-null reviews
reviews: [Review]!  # non-null list of nullable reviews
reviews: [Review!]! # non-null list of non-null reviews (most common for collections)
```

> **Design guidance:**
> - Mark `!` (non-null) when the field is fundamental to the type's identity
> - Leave nullable when the data might legitimately be absent
> - Be conservative with `!` — once you mark non-null, removing it is a breaking change

---

## Summary — Part 1 Key Concepts

| Concept         | What It Is                                                |
|-----------------|-----------------------------------------------------------|
| GraphQL         | Query language where clients specify the shape they need  |
| Over-fetching   | REST problem: too much data returned; GraphQL solves this |
| Under-fetching  | REST problem: multiple round trips; GraphQL nesting solves |
| SDL             | Human-readable language for defining the schema           |
| Scalar types    | String, Int, Float, Boolean, ID — leaf value types        |
| Object type     | A named group of fields (`type Book { ... }`)             |
| `!` non-null    | Guarantees a field will never be null                     |
| Enum            | Field restricted to a fixed set of string values          |
| Interface       | Shared contract multiple types can implement              |
| Union           | A field that can return one of multiple distinct types    |
| Input type      | A type used only for mutation arguments                   |
| Query           | Root type for read operations                             |
| Mutation        | Root type for write operations                            |
| Subscription    | Root type for real-time event streams (WebSocket)         |

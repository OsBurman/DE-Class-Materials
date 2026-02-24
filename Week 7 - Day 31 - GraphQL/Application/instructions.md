# Day 31 Application — GraphQL: Book Library API

## Overview

Build a **Book Library GraphQL API** using Spring for GraphQL. Define a schema, implement resolvers (queries, mutations, subscriptions), and explore how GraphQL compares to REST.

---

## Learning Goals

- Understand the GraphQL type system (Object, Query, Mutation, Scalar)
- Define a `.graphqls` schema file
- Implement `@QueryMapping`, `@MutationMapping`, `@SchemaMapping`
- Handle relationships between types
- Use fragments and variables in queries
- Handle errors with `DataFetcherExceptionResolver`

---

## Prerequisites

- Java 17+, Maven
- `mvn spring-boot:run` → `http://localhost:8080`
- GraphQL Playground: `http://localhost:8080/graphiql`

---

## Part 1 — Schema Design

**Task 1 — `schema.graphqls`**  
Define the following schema in `src/main/resources/graphql/schema.graphqls`:

```graphql
type Author {
    id: ID!
    name: String!
    email: String
    books: [Book!]!
}

type Book {
    id: ID!
    title: String!
    isbn: String!
    publishedYear: Int
    genre: Genre!
    author: Author!
    reviews: [Review!]!
    averageRating: Float
}

type Review {
    id: ID!
    rating: Int!
    comment: String
    reviewerName: String!
}

enum Genre {
    # TODO: add at least 5 genres
}

type Query {
    # TODO: list all books
    # TODO: get book by ID
    # TODO: search books by title or author
    # TODO: books by genre
}

type Mutation {
    # TODO: add a book
    # TODO: update book
    # TODO: delete book
    # TODO: add a review to a book
}

input BookInput {
    # TODO: define input fields
}

input ReviewInput {
    # TODO: define input fields
}
```

---

## Part 2 — Data Layer

**Task 2**  
Use in-memory `Map` stores (no DB needed). Pre-seed 5 authors, 10 books, 15 reviews.

---

## Part 3 — Query Resolvers

**Task 3 — `BookController.java`**  
```java
@Controller
public class BookController {

    @QueryMapping
    public List<Book> books() { /* TODO */ }

    @QueryMapping
    public Book bookById(@Argument Long id) { /* TODO */ }

    @QueryMapping
    public List<Book> searchBooks(@Argument String query) { /* TODO */ }

    @SchemaMapping(typeName = "Book", field = "averageRating")
    public Double averageRating(Book book) { /* TODO: calculate from reviews */ }
}
```

**Task 4 — Relationship resolver**  
```java
@SchemaMapping(typeName = "Author", field = "books")
public List<Book> booksForAuthor(Author author) { /* TODO */ }
```

---

## Part 4 — Mutations

**Task 5 — `BookMutationController.java`**  
```java
@MutationMapping
public Book addBook(@Argument BookInput input) { /* TODO */ }

@MutationMapping
public Book updateBook(@Argument Long id, @Argument BookInput input) { /* TODO */ }

@MutationMapping
public Boolean deleteBook(@Argument Long id) { /* TODO */ }

@MutationMapping
public Review addReview(@Argument Long bookId, @Argument ReviewInput input) { /* TODO */ }
```

---

## Part 5 — Test Queries

In GraphiQL, test ALL of the following:

**Task 6 — Write these queries in a `test-queries.graphql` file:**

```graphql
# Query 1: Fetch all books with title, genre, author name
# Query 2: Fetch a single book with all fields including reviews
# Query 3: Search books by "fantasy"
# Query 4: Use a fragment for Book fields
# Query 5: Add a new book (mutation) with variables
# Query 6: Add a review to a book
```

---

## Submission Checklist

- [ ] `schema.graphqls` has all types, queries, mutations
- [ ] GraphiQL opens at `/graphiql`
- [ ] All Query resolvers return data
- [ ] All Mutation resolvers work (add/update/delete/review)
- [ ] `@SchemaMapping` used for `averageRating`
- [ ] `test-queries.graphql` file with 6 annotated queries

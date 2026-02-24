# Performance Optimization — N+1 Problem & DataLoader

## Overview

This file covers the two biggest performance topics in GraphQL:
1. **The N+1 Problem** — what it is, how to spot it, how it emerges from nested resolvers
2. **DataLoader** — the batching + caching solution that eliminates N+1

These are topics you WILL be asked about in GraphQL interviews. Know them cold.

---

## SECTION 1: The N+1 Problem

### What Is It?

The N+1 problem occurs when fetching a list of N items triggers N additional queries — one for each item — instead of one batched query.

### Concrete Example (Bookstore)

**GraphQL query from the client:**
```graphql
query GetBooksWithAuthors {
  books {
    id
    title
    author {
      name
    }
  }
}
```

**What the naive resolver chain does:**
```
1.  SELECT * FROM books                          ← 1 query for the books list
2.  SELECT * FROM authors WHERE id = 1          ← resolver for book[0].author
3.  SELECT * FROM authors WHERE id = 2          ← resolver for book[1].author
4.  SELECT * FROM authors WHERE id = 3          ← resolver for book[2].author
5.  SELECT * FROM authors WHERE id = 1          ← resolver for book[3].author (DUPLICATE!)
...
N+1. SELECT * FROM authors WHERE id = N        ← resolver for book[N-1].author
```

**If you have 100 books, that's 101 database queries for one GraphQL request.**

### Why Does This Happen in GraphQL?

GraphQL resolvers are designed to be independent. The `author` resolver on a `Book` type only knows about ONE book — it doesn't know it's being called 100 times in parallel. Each call fires its own query.

```java
// Spring for GraphQL — THIS IS THE PROBLEMATIC PATTERN
@SchemaMapping(typeName = "Book", field = "author")
public Author getAuthor(Book book) {
    // This runs once per book in the result set!
    // For 100 books → 100 separate DB calls
    return authorRepository.findById(book.getAuthorId()).orElse(null);
}
```

### How to Spot N+1 in Development

**Method 1: Enable SQL logging in Spring Boot**
```properties
# application.properties
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
logging.level.org.hibernate.SQL=DEBUG
```
Watch the console — if you see the same SELECT repeated many times with different IDs, you have N+1.

**Method 2: Use a slow-query indicator**
Count queries per request. If `books` returns 50 items and you see 51+ queries, you have N+1.

**Method 3: Apollo DevTools (client-side)**
Check response time. An N+1 query for 100 books might take 2-3 seconds when it should take 50ms.

---

## SECTION 2: DataLoader — The Solution

### What Is DataLoader?

DataLoader is a utility that:
1. **Batches** — collects all resolver calls within a single "tick" (event loop cycle) and fires ONE query with all IDs
2. **Caches** — within a single request, if the same ID is requested twice, DataLoader returns the cached result

It was created by Facebook for their GraphQL server and is now a standard pattern across all GraphQL implementations.

### How DataLoader Works — Step by Step

```
Tick 1 (collect):
  Book[0].author resolver fires → DataLoader.load("author", id=1)  ← queued, not yet executed
  Book[1].author resolver fires → DataLoader.load("author", id=2)  ← queued
  Book[2].author resolver fires → DataLoader.load("author", id=3)  ← queued
  Book[3].author resolver fires → DataLoader.load("author", id=1)  ← DUPLICATE, deduplicated

Tick 2 (batch):
  DataLoader batches all unique IDs: [1, 2, 3]
  Fires ONE query: SELECT * FROM authors WHERE id IN (1, 2, 3)
  Returns results to each waiting resolver

Result: 1 query instead of 4. For 100 books with authors: 2 queries instead of 101.
```

### DataLoader in Spring for GraphQL

Spring for GraphQL has built-in DataLoader support.

```java
// FILE: BookstoreDataLoaderConfig.java
// ─────────────────────────────────────────────────────────────────────────────
import org.springframework.graphql.execution.BatchLoaderRegistry;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import java.util.*;

@Component
public class BookstoreDataLoaderConfig {

    private final AuthorRepository authorRepository;

    public BookstoreDataLoaderConfig(AuthorRepository authorRepository) {
        this.authorRepository = authorRepository;
    }

    // Register the DataLoader with a name that matches what you use in resolvers
    // The BatchLoaderRegistry is provided by Spring for GraphQL's auto-config
    public void registerDataLoaders(BatchLoaderRegistry registry) {
        registry.forTypePair(Long.class, Author.class)
                .withName("authorDataLoader")
                .registerMappedBatchLoader((authorIds, batchLoaderEnvironment) -> {
                    // This method receives a SET of all accumulated author IDs
                    // from all resolver calls in the current request
                    List<Author> authors = authorRepository.findAllById(authorIds);

                    // Return a Map from ID → Author so DataLoader can route
                    // results back to the correct resolver calls
                    Map<Long, Author> authorMap = new HashMap<>();
                    for (Author author : authors) {
                        authorMap.put(author.getId(), author);
                    }
                    return Mono.just(authorMap);
                });
    }
}
```

```java
// FILE: BookController.java (DataLoader-aware resolver)
// ─────────────────────────────────────────────────────────────────────────────
import org.dataloader.DataLoader;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import java.util.concurrent.CompletableFuture;

@Controller
public class BookController {

    // BEFORE (N+1 problem):
    @SchemaMapping(typeName = "Book", field = "author")
    public Author getAuthorNPlus1(Book book) {
        // Runs N times, once per book — BAD
        return authorRepository.findById(book.getAuthorId()).orElse(null);
    }

    // AFTER (DataLoader — 1 batched query):
    @SchemaMapping(typeName = "Book", field = "author")
    public CompletableFuture<Author> getAuthorWithDataLoader(
            Book book,
            DataLoader<Long, Author> authorDataLoader  // injected by Spring by name
    ) {
        // load() returns immediately — queues the ID for batching.
        // CompletableFuture is resolved later when the batch executes.
        return authorDataLoader.load(book.getAuthorId());
    }
}
```

### Why CompletableFuture?

DataLoader is asynchronous by design. `dataLoader.load(id)` doesn't execute the query immediately — it returns a `CompletableFuture` that will be resolved when the batch runs. Spring for GraphQL understands `CompletableFuture` return types and suspends the resolver until the batch is ready.

---

## SECTION 3: Field Selection — Only Request What You Need

### Why Field Selection Matters for Performance

In REST, the server decides what data to return. In GraphQL, **the client decides**.

If your GraphQL query only requests `{ books { id title } }`, the server's resolver only needs to execute the query and return `id` and `title`. Fields like `genre`, `publishedYear`, and `author` are not fetched — the `author` resolver never even runs.

```graphql
# This query does NOT trigger the author resolver
# → Zero "author" database calls
query TitleOnlyList {
  books {
    id
    title
    # author { name }   ← commented out = resolver never runs
  }
}

# This query DOES trigger the author resolver for every book
query TitleWithAuthor {
  books {
    id
    title
    author {
      name    # ← author resolver runs once per book (N+1 without DataLoader)
    }
  }
}
```

**Rule:** Only request fields your UI actually displays. Every nested field you add potentially adds resolver calls.

---

## SECTION 4: Query Complexity Limits

For production APIs, you should limit how expensive a query can be. Without limits, a client could write:

```graphql
# Deeply nested "Bathtub" attack query
query EvilQuery {
  books {
    author {
      books {
        author {
          books {
            author { name }
          }
        }
      }
    }
  }
}
```

This is exponentially expensive. GraphQL servers can enforce **query depth limits** and **query complexity scores**.

```java
// Spring for GraphQL — configure max query depth
// application.properties:
// spring.graphql.schema.introspection.enabled=true

// For query complexity, use graphql-java's built-in analysis:
//
// GraphQL graphQL = GraphQL.newGraphQL(schema)
//     .queryExecutionStrategy(new AsyncExecutionStrategy())
//     .instrumentation(new MaxQueryDepthInstrumentation(10))   // max 10 levels deep
//     .instrumentation(new MaxQueryComplexityInstrumentation(100))  // max complexity 100
//     .build();
```

---

## SECTION 5: Query Complexity Scoring Concept

Each field can be assigned a cost. Simple scalar fields cost 1. List fields cost more (they multiply). The total query cost is calculated before execution.

```
query GetBooksWithAuthors {
  books {              ← cost: 10 (list field, assume 10 items)
    title              ← cost: 1
    author {           ← cost: 1 × 10 books = 10
      name             ← cost: 1 × 10 books = 10
    }
  }
}
Total cost: 10 + 10 + 10 + 10 = 40
```

If your limit is 100, this query passes. A deeply nested query that scores 500 is rejected before hitting the database.

---

## SECTION 6: Caching at the HTTP Level

While Apollo InMemoryCache caches on the CLIENT, you can also cache at the HTTP layer with **Automatic Persisted Queries (APQ)**.

### Automatic Persisted Queries (APQ)

Problem: Large GraphQL queries in POST bodies can be expensive to parse. On repeated calls (same query, different variables), you're re-sending the full query string every time.

APQ solution:
1. Client hashes the query string → `sha256("query GetAllBooks { books { id title } }")` → hash
2. First request: client sends ONLY the hash. Server says "I don't have that hash."
3. Second request: client sends hash + full query. Server stores the mapping.
4. All future requests: client sends only the hash. Server looks it up and executes.

Result: Subsequent requests for the same query structure are tiny — just a hash + variables.

```ts
// Apollo Client — enable APQ
import { createPersistedQueryLink } from '@apollo/client/link/persisted-queries';
import { sha256 } from 'crypto-hash';

const persistedQueriesLink = createPersistedQueryLink({ sha256 });
const client = new ApolloClient({
  link: from([persistedQueriesLink, httpLink]),
  cache: new InMemoryCache(),
});
```

---

## Summary — Performance Optimization Checklist

| Problem | Solution |
|---|---|
| N+1 database queries | DataLoader — batch + deduplicate by ID |
| Too many fields fetched | Field selection — only request what UI needs |
| Same query repeatedly | Apollo InMemoryCache — cache-first policy |
| Large query string overhead | Automatic Persisted Queries (APQ) |
| Multiple simultaneous requests | BatchHttpLink — batch into one HTTP call |
| Unbounded query depth | MaxQueryDepthInstrumentation on the server |
| Expensive query attacks | MaxQueryComplexityInstrumentation on the server |
| Slightly stale data OK | cache-and-network fetch policy |
| Real-time data | WebSocket subscriptions instead of polling |

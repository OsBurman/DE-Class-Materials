# Day 32 Part 2 — Caching, DataLoader, Testing, and GraphQL Tooling
## Slide Descriptions

---

### Slide 1 — Title Slide

**Title:** Day 32 Part 2: Caching, N+1/DataLoader, Testing GraphQL APIs, and Developer Tooling

**Subtitle:** Making GraphQL applications efficient, testable, and maintainable

**Learning Objectives:**
- Understand Apollo InMemoryCache normalization and how cache keys work
- Apply fetch policies for optimal cache/network balance
- Update the cache after mutations using `refetchQueries` and the `update` function
- Implement optimistic UI for instant user feedback
- Use `BatchHttpLink` for request batching
- Explain the N+1 problem and how DataLoader solves it with batching and per-request caching
- Implement DataLoader batching in Spring for GraphQL with `@BatchMapping`
- Test GraphQL APIs using Postman, GraphiQL, Apollo Sandbox, and Altair
- Write efficient queries with proper field selection and debugging strategies

---

### Slide 2 — Apollo InMemoryCache — Normalization

**Title:** InMemoryCache — How Apollo Stores and Deduplicates Data

**Content:**

Apollo doesn't store query results by query name. It normalizes them: each object is stored once, keyed by `__typename` + `id`, and queries become references to those objects.

**Normalization in action:**
```javascript
// Query 1 result:
// { books: [{ id: "1", title: "Clean Code", author: { id: "5", name: "Robert Martin" } },
//           { id: "2", title: "Refactoring", author: { id: "6", name: "Martin Fowler" } }] }

// Query 2 result:
// { author: { id: "5", name: "Robert Martin", bio: "Uncle Bob..." } }

// What Apollo stores (the normalized cache):
// {
//   "ROOT_QUERY": { books: [{ __ref: "Book:1" }, { __ref: "Book:2" }] }
//   "Book:1": { id: "1", title: "Clean Code", author: { __ref: "Author:5" } }
//   "Book:2": { id: "2", title: "Refactoring", author: { __ref: "Author:6" } }
//   "Author:5": { id: "5", name: "Robert Martin", bio: "Uncle Bob..." }  ← merged from both queries
//   "Author:6": { id: "6", name: "Martin Fowler" }
// }
```

**Key behavior:** When Query 2 returns `Author:5` with a `bio` field, Apollo merges it with the existing `Author:5` entry from Query 1. Any component displaying `Author:5` (including the book list) automatically receives the updated data.

**Custom cache key fields:**
```javascript
const cache = new InMemoryCache({
  typePolicies: {
    Book: { keyFields: ['id'] },          // default — uses id
    BookEdge: { keyFields: false },        // no caching (treat as embedded object)
    AuthorBio: {
      keyFields: ['authorId', 'version'], // composite key for types without id
    },
  },
});
```

---

### Slide 3 — Fetch Policies

**Title:** Fetch Policies — Controlling Cache vs Network Trade-offs

**Content:**

**The five fetch policies:**
```javascript
useQuery(GET_BOOKS, {
  fetchPolicy: 'cache-first',         // (default)
  fetchPolicy: 'cache-and-network',
  fetchPolicy: 'network-only',
  fetchPolicy: 'no-cache',
  fetchPolicy: 'cache-only',
});
```

**When to use each:**

| Policy | Behavior | Use Case |
|--------|----------|---------|
| `cache-first` | Return cache if available; fetch only if missing | Static/slow-changing data (config, user profile) |
| `cache-and-network` | Return cache immediately + fetch in background; update when response arrives | Lists and feeds — shows stale data quickly, updates soon |
| `network-only` | Always fetch, update cache, return network result | Critical data (cart, checkout, live inventory) |
| `no-cache` | Always fetch, never write to cache | Sensitive data (auth info, one-time tokens) |
| `cache-only` | Cache only; error if not found | Offline mode; reading previously fetched data |

**Setting a default for the whole client:**
```javascript
const client = new ApolloClient({
  link: httpLink,
  cache: new InMemoryCache(),
  defaultOptions: {
    watchQuery: { fetchPolicy: 'cache-and-network' }, // for useQuery
    query:      { fetchPolicy: 'network-only' },      // for useLazyQuery one-shots
  },
});
```

**`nextFetchPolicy`** — sets the policy for subsequent fetches after the first one:
```javascript
useQuery(GET_BOOKS, {
  fetchPolicy: 'network-only',      // first load: always fetch
  nextFetchPolicy: 'cache-first',   // subsequent: use cache
});
```

---

### Slide 4 — Updating the Cache After Mutations — refetchQueries

**Title:** Cache Updates — refetchQueries (Simple Approach)

**Content:**

After a mutation modifies server data, the Apollo cache may be stale. Two approaches to keep it in sync.

**Option 1 — `refetchQueries`: simple, always correct, costs a network request:**
```typescript
const [createBook] = useMutation(CREATE_BOOK, {
  // Re-run these queries automatically after the mutation succeeds
  refetchQueries: [
    { query: GET_BOOKS },                                      // all books
    { query: GET_BOOKS, variables: { authorId: bookAuthorId } }, // specific variables
    'GetFeaturedBooks',                                         // by operation name
  ],
  // Default: send refetch requests, don't wait for them
  awaitRefetchQueries: false,
  // Set to true to delay mutation's onCompleted until refetch finishes:
  // awaitRefetchQueries: true,
});

// Angular equivalent:
this.apollo.mutate({
  mutation: CREATE_BOOK,
  variables: { input },
  refetchQueries: [{ query: GET_BOOKS }],
}).subscribe(...);
```

**`evict` + `gc` — surgical cache removal:**
```typescript
const [deleteBook] = useMutation(DELETE_BOOK, {
  update(cache, { data }) {
    // Remove the specific Book entry from the cache entirely
    cache.evict({ id: cache.identify({ __typename: 'Book', id: data.deleteBook.id }) });
    // Run garbage collection to remove dangling references
    cache.gc();
  }
});
```

---

### Slide 5 — Cache Updates — update Function and Optimistic UI

**Title:** Cache Updates — Manual update Function and Optimistic UI

**Content:**

**Option 2 — `update` function: no extra network request, more code:**
```typescript
const [createBook] = useMutation(CREATE_BOOK, {
  update(cache, { data }) {
    const newBook = data?.createBook;
    if (!newBook) return;

    // Read the current books list from cache
    const existing = cache.readQuery<{ books: Book[] }>({ query: GET_BOOKS });

    // Write the updated list back to cache — Apollo updates all subscribers
    cache.writeQuery({
      query: GET_BOOKS,
      data: {
        books: [...(existing?.books ?? []), newBook],
      },
    });
  },
});
```

**Optimistic UI — show result before server confirms:**
```typescript
const [updateBook] = useMutation(UPDATE_BOOK, {
  // Tell Apollo what the result will look like — it writes this to the cache immediately
  optimisticResponse: {
    updateBook: {
      __typename: 'Book',   // must be correct — used as the cache key
      id: bookId,           // must be correct — used as the cache key
      title: newTitle,      // assume the update will succeed
      pageCount: newPageCount,
    },
  },
  // If the mutation fails, Apollo automatically rolls back the optimistic update
  // Components re-render with the original cached data
});
```

**Optimistic UI timing:**
1. User clicks "Update" → `optimisticResponse` immediately written to cache → UI updates
2. Mutation request sent to server
3. Server responds with real data → Apollo replaces optimistic entry with real entry
4. If server returns error → optimistic entry removed → UI reverts to original state

---

### Slide 6 — Query Batching and Request Optimization

**Title:** Query Batching with BatchHttpLink

**Content:**

In complex pages, multiple components may each call `useQuery` independently. Without batching, each fires a separate HTTP request to `/graphql` simultaneously.

**BatchHttpLink — bundles operations into a single HTTP request:**
```javascript
import { BatchHttpLink } from '@apollo/client/link/batch-http';

const batchLink = new BatchHttpLink({
  uri: 'http://localhost:8080/graphql',
  batchMax: 5,         // maximum operations to include in one batch
  batchInterval: 20,   // wait 20ms for operations to accumulate before sending
});

const client = new ApolloClient({
  link: batchLink,   // replaces HttpLink
  cache: new InMemoryCache(),
});
```

**What a batched request looks like:**
```json
// Single HTTP POST body (array instead of object)
[
  { "query": "query GetBooks { books { id title } }", "variables": {} },
  { "query": "query GetFeaturedAuthors { featuredAuthors { id name } }", "variables": {} }
]
// Server must support array body — returns array response
```

**Important:** Spring for GraphQL does not natively support request batching (array body). Batching at the HTTP layer requires a gateway (Apollo Router) or a custom `WebInterceptor`. For most development setups, batching is less critical than DataLoader (which solves the more common N+1 problem within a single request).

**Built-in Apollo deduplication (no config needed):**
```javascript
// If two components query for the same data at the same time,
// Apollo automatically deduplicates to ONE network request.
// Both components receive the same result.
// No extra configuration required.
```

---

### Slide 7 — Field Selection and Fragment Reuse

**Title:** Field Selection and Fragment Reuse — Bandwidth Optimization

**Content:**

**Request only the fields you render — every field has a cost:**
```graphql
# ❌ Over-fetching — requesting fields you don't display
query GetBookList {
  books {
    id title isbn pageCount available createdAt updatedAt
    author { id name bio birthDate photoUrl books { id title isbn } }
    reviews { id rating text createdAt reviewer { id name email bio avatar } }
  }
}

# ✅ Minimal selection — only what the book list component displays
query GetBookList {
  books {
    id
    title
    author { name }
  }
}
```

**Fragment strategy — DRY queries across multiple operations:**
```graphql
# Define once
fragment BookListItem on Book {
  id
  title
  author { name }
}

# Reuse everywhere
query GetBooks         { books           { ...BookListItem } }
query SearchBooks      { searchBooks(query: $q) { ...BookListItem } }
query GetAuthorBooks   { author(id: $id) { books { ...BookListItem } } }
```

```javascript
// In Apollo Client JavaScript:
const BOOK_LIST_ITEM = gql`
  fragment BookListItem on Book {
    id
    title
    author { name }
  }
`;

const GET_BOOKS = gql`
  ${BOOK_LIST_ITEM}
  query GetBooks { books { ...BookListItem } }
`;
```

Fragment changes propagate automatically to all queries that include it.

---

### Slide 8 — The N+1 Problem — Deep Dive

**Title:** The N+1 Problem — Why GraphQL Servers Must Handle This

**Content:**

**The scenario:**
```graphql
query GetBooks {
  books {
    title
    author { name }   ← requires a separate resolver call per book
  }
}
```

**What happens inside the server (without DataLoader):**
```
[Resolver: Query.books]
  → SQL: SELECT * FROM books
  → Returns: Book[id=1, authorId=10], Book[id=2, authorId=11], ... Book[id=100, authorId=50]

[Resolver: Book.author — called for Book 1]
  → SQL: SELECT * FROM authors WHERE id = 10

[Resolver: Book.author — called for Book 2]
  → SQL: SELECT * FROM authors WHERE id = 11

[Resolver: Book.author — called for Book 3]
  → SQL: SELECT * FROM authors WHERE id = 12

... (repeated 100 times)

TOTAL: 101 SQL queries for one GraphQL request
```

**Why the resolver can't see the full picture:**
```java
// @SchemaMapping is called independently for each Book instance.
// It has no visibility into how many other Book instances are waiting.
@SchemaMapping(typeName = "Book", field = "author")
public Author author(Book source) {
    return authorService.findById(source.getAuthorId());  // runs N times, no way to batch here
}
```

**The problem scales:**
- 10 books → 11 queries
- 100 books → 101 queries
- 1,000 books → 1,001 queries
- If each `Book` also has `reviews { reviewer { ... } }` → O(N²) queries

---

### Slide 9 — DataLoader — Batching and Per-Request Caching

**Title:** DataLoader — The Batching Solution

**Content:**

DataLoader was created at Facebook and open-sourced via the `dataloader` npm package. It's been ported to Java and integrated into graphql-java (which Spring for GraphQL uses).

**How DataLoader works:**

```
Tick 1 (collecting phase — happens within a single event loop tick):
  Book 1 resolver: authorLoader.load(authorId=10)  → queued
  Book 2 resolver: authorLoader.load(authorId=11)  → queued
  Book 3 resolver: authorLoader.load(authorId=10)  → deduplicated (already queued)
  ...
  Book 100 resolver: authorLoader.load(authorId=50) → queued
  → 100 load() calls, but only unique IDs are batched

Tick 2 (dispatch phase — end of tick):
  Batch function called with: [10, 11, 12, ..., 50]  ← unique IDs only
  → ONE SQL query: SELECT * FROM authors WHERE id IN (10, 11, 12, ..., 50)
  → Returns Map: { 10: Author("Robert Martin"), 11: Author("Martin Fowler"), ... }
  → DataLoader distributes results to each waiting resolver promise
```

**Two key DataLoader features:**

**1. Batching:** Groups all `load()` calls within the same event loop tick into a single batch function call.

**2. Per-request caching:** Within a single request, if the same ID is loaded multiple times (e.g., many books by the same author), DataLoader returns a cached promise after the first load. Zero redundant queries.

**Result:**
- Without DataLoader: 101 queries (N+1)
- With DataLoader: 2 queries (books + authors) regardless of N

---

### Slide 10 — DataLoader in Spring for GraphQL — @BatchMapping Deep Dive

**Title:** @BatchMapping — Spring for GraphQL's DataLoader Integration

**Content:**

Spring for GraphQL's `@BatchMapping` abstracts the DataLoader API. Instead of configuring `DataLoader` beans manually, you declare a batch resolver method with the right signature and the framework handles the rest.

**@SchemaMapping (N+1) → @BatchMapping (batched):**
```java
// BEFORE — N+1 problem:
@SchemaMapping(typeName = "Book", field = "author")
public Author author(Book book) {
    return authorService.findById(book.getAuthorId()); // called N times
}

// AFTER — @BatchMapping:
@BatchMapping(typeName = "Book", field = "author")
public Map<Book, Author> author(List<Book> books) {
    // Called ONCE with ALL Books that need their author resolved
    Set<Long> authorIds = books.stream()
        .map(Book::getAuthorId)
        .collect(Collectors.toSet());

    // One database query — fetch all needed authors
    Map<Long, Author> authorsById = authorService.findAllByIds(authorIds).stream()
        .collect(Collectors.toMap(Author::getId, a -> a));

    // Map each Book to its resolved Author
    return books.stream()
        .collect(Collectors.toMap(
            book -> book,
            book -> authorsById.get(book.getAuthorId())
        ));
}
// @BatchMapping returns Map<ParentType, FieldType>
// Spring for GraphQL distributes the map values to each waiting parent
```

**For one-to-many relationships (List field):**
```java
// Book.reviews: [Review!]! — each Book has many Reviews
@BatchMapping(typeName = "Book", field = "reviews")
public Map<Book, List<Review>> reviews(List<Book> books) {
    List<Long> bookIds = books.stream().map(Book::getId).collect(Collectors.toList());

    // One query: SELECT * FROM reviews WHERE book_id IN (...)
    Map<Long, List<Review>> reviewsByBookId = reviewService.findAllByBookIds(bookIds)
        .stream()
        .collect(Collectors.groupingBy(Review::getBookId));

    return books.stream()
        .collect(Collectors.toMap(
            book -> book,
            book -> reviewsByBookId.getOrDefault(book.getId(), Collections.emptyList())
        ));
}
```

**Required `equals` and `hashCode`:** The parent type (e.g., `Book`) must implement `equals()` and `hashCode()` correctly for use as Map keys. Use Lombok `@EqualsAndHashCode` or Java records.

---

### Slide 11 — Testing GraphQL APIs in Postman

**Title:** Testing GraphQL APIs with Postman

**Content:**

**Basic request setup:**
```
1. Create a new HTTP request
2. Method: POST
3. URL: http://localhost:8080/graphql
4. Body → select "GraphQL" tab
```

**Postman's GraphQL tab:**
```graphql
# Query field
query GetBook($id: ID!) {
  book(id: $id) {
    id
    title
    pageCount
    author { name }
  }
}
```
```json
// Variables panel (below query)
{ "id": "1" }
```

**Headers (auto-set by Postman GraphQL tab; set manually for raw JSON):**
```
Content-Type: application/json
Authorization: Bearer eyJhbGciOiJSUzI1NiJ9...
```

**Raw JSON alternative (works when GraphQL tab unavailable):**
```json
{
  "query": "query GetBook($id: ID!) { book(id: $id) { id title author { name } } }",
  "variables": { "id": "1" },
  "operationName": "GetBook"
}
```

**Import schema via introspection:**
```
1. Collections → New → GraphQL collection
2. Enter endpoint URL → Postman fetches schema via introspection
3. Schema explorer shows all available queries, mutations, subscriptions
4. Auto-generates example requests for each operation
5. Saves schema for offline use
```

**Team usage:**
- Save queries as a Postman Collection
- Export and commit to version control
- Share collection URL with team
- Use Postman environments for dev/staging/production URLs and auth tokens

---

### Slide 12 — GraphiQL — In-Depth Usage

**Title:** GraphiQL — The Embedded Development IDE

**Content:**

GraphiQL is enabled with `spring.graphql.graphiql.enabled=true`. Access at `http://localhost:8080/graphiql`. It's a browser-based IDE that connects to your server via introspection and provides a full development environment for GraphQL.

**Key features:**

**1. Schema Explorer / Docs Panel (right side):**
- Lists all Query, Mutation, Subscription fields
- Click any type → see all its fields, arguments, types, descriptions
- SDL descriptions you wrote in `.graphqls` files appear here as documentation
- Essential for exploring unfamiliar schemas

**2. Autocomplete:**
- `Ctrl+Space` (Windows/Linux) or `Cmd+Space` (Mac) — trigger manually
- Also triggers automatically after typing `{` or `:` inside a query
- Suggests fields for the current type context
- Shows argument types and required markers

**3. Keyboard shortcuts:**

| Shortcut | Action |
|----------|--------|
| `Ctrl+Enter` / `Cmd+Enter` | Run selected operation |
| `Shift+Ctrl+P` | Prettify query (auto-format) |
| `Shift+Ctrl+M` | Merge fragments |
| `Ctrl+/` | Comment/uncomment line |

**4. Variables panel (bottom left):** Write variable JSON here instead of in the query string.

**5. Request Headers panel (bottom):** Add `Authorization`, custom headers.

**6. History panel (clock icon):** Browse previous queries — useful for recovering a complex query you didn't save.

**7. Multiple operations in one document:**
```graphql
query GetBook { book(id: "1") { title } }
query GetBooks { books { title } }
```
With two operations, click the ▶ dropdown to choose which one to run.

---

### Slide 13 — Apollo Sandbox and Altair GraphQL Client

**Title:** Apollo Sandbox and Altair — Beyond GraphiQL

**Content:**

**Apollo Sandbox:** `sandbox.apollo.dev`
- Browser-based, cloud-hosted — no installation
- Connects to any GraphQL endpoint via introspection (requires CORS headers that allow the Sandbox origin, or run locally)
- Schema explorer with field-level search
- Operation collections — save and organize queries
- Environment variables for tokens and base URLs
- Field usage indicators when connected to Apollo Studio (which fields are actually used in production)
- Explorer tracks which operations are run most — useful for schema health

**Altair GraphQL Client:** `altairgraphql.dev`
- Desktop app (macOS, Windows, Linux) + browser extension (Chrome, Firefox)
- Free and open-source
- Features: operation collections, environments (dev/staging/prod), import from URL or file, subscriptions support (WebSocket and SSE), pre/post-request scripts (JavaScript), query variables and headers per-environment, team export/import
- Best for: advanced testing workflows, subscriptions testing, pre-request token refresh scripts, offline use

**When to use each tool:**

| Tool | Best For |
|------|---------|
| **GraphiQL** | Active development — fast iteration against your local server |
| **Postman** | API testing collections, CI/CD integration, team sharing |
| **Apollo Sandbox** | Apollo Studio integration, schema exploration, field analytics |
| **Altair** | Advanced scripting, subscriptions testing, complex environments |

**CORS note for Sandbox:** When connecting from `sandbox.apollo.dev` to `localhost`, you may need to allow the Sandbox origin in your Spring Boot CORS config.

---

### Slide 14 — Writing Effective GraphQL Queries

**Title:** Writing Effective Queries — Field Selection and Named Operations

**Content:**

**Always use named operations:**
```graphql
# ❌ Anonymous — impossible to trace in server logs, no APM grouping
{
  books { id title }
}

# ✅ Named — appears in server logs, Apollo Studio, DataDog
query GetBookList { books { id title } }
```

**Request only fields you render:**
- Every extra field is a field resolver call on the server, potentially a DB column read, and bytes over the wire
- Start with the minimum, add fields as the UI demands them
- The schema explorer in GraphiQL helps discover available fields

**Use `__typename` for diagnostics:**
```graphql
# If a field is returning null unexpectedly, check the type:
query Debug {
  book(id: "1") {
    __typename    # "Book" if resolved, null if not found
    id
  }
  search(term: "patterns") {
    __typename    # "Book" or "Author" — discriminates union types
    ... on Book { title }
    ... on Author { name }
  }
}
```

**Check the `errors` array — even with a 200 response:**
```javascript
const { data, error } = useQuery(GET_BOOKS);

// error may be null/undefined but data.someField may be null
// with "errors" present in the response when errorPolicy: 'all'
if (error?.graphQLErrors?.length) {
  error.graphQLErrors.forEach(e => console.warn('Field error:', e.path, e.message));
}
```

**Apollo DevTools (browser extension):**
- Chrome Web Store: "Apollo Client Devtools"
- Inspect the cache contents in real-time
- View all queries/mutations fired by the current page
- Replay queries with different variables
- Invaluable for debugging cache updates and stale data

---

### Slide 15 — Nested Queries and Client-Side Pagination

**Title:** Nested Queries and Pagination with fetchMore

**Content:**

**Nested queries — fetching related data in one request:**
```graphql
query GetBookWithContext($id: ID!) {
  book(id: $id) {
    id
    title
    pageCount
    author {
      name
      books {           # other books by the same author
        id title
      }
    }
    reviews(minRating: 4) {
      rating
      text
      reviewer { name }
    }
  }
}
```

One HTTP request, multiple server-side resolvers, one response. The client specifies exactly the shape.

**Client-side pagination with `fetchMore` (Relay Connection pattern):**
```typescript
const GET_BOOKS_PAGE = gql`
  query GetBooksPage($cursor: String) {
    books(first: 10, after: $cursor) {
      edges { node { id title author { name } } cursor }
      pageInfo { hasNextPage endCursor }
    }
  }
`;

function BookList() {
  const { data, fetchMore, loading } = useQuery(GET_BOOKS_PAGE);

  const loadMore = () => {
    fetchMore({
      variables: {
        cursor: data?.books.pageInfo.endCursor,  // pass endCursor as the "after" arg
      },
      // updateQuery merges the new page with the existing cached page
      updateQuery: (previousResult, { fetchMoreResult }) => {
        if (!fetchMoreResult) return previousResult;
        return {
          books: {
            ...fetchMoreResult.books,
            edges: [
              ...previousResult.books.edges,
              ...fetchMoreResult.books.edges,   // append new page
            ],
          },
        };
      },
    });
  };

  return (
    <>
      {data?.books.edges.map(edge => <div key={edge.node.id}>{edge.node.title}</div>)}
      {data?.books.pageInfo.hasNextPage && (
        <button onClick={loadMore} disabled={loading}>Load More</button>
      )}
    </>
  );
}
```

---

### Slide 16 — Part 2 Summary

**Title:** Day 32 Summary — GraphQL Client, Caching, DataLoader, and Tooling

**Content:**

**Caching and performance:**

| Topic | Key Point |
|-------|-----------|
| Cache normalization | Objects stored by `__typename + id` — single source of truth |
| Fetch policies | `cache-first` for static data; `network-only` for critical fresh data |
| Cache update strategies | `refetchQueries` for simplicity; `update` function for no extra network cost |
| Optimistic UI | `optimisticResponse` for instant feedback — auto-rolls back on failure |
| Request batching | `BatchHttpLink` — requires server support; Apollo deduplicates by default |

**N+1 and DataLoader:**

| Without DataLoader | With DataLoader |
|-------------------|----------------|
| 1 query for N books | 1 query for N books |
| N queries for N authors | 1 query for all authors |
| Total: N+1 queries | Total: 2 queries |

Spring for GraphQL: use `@BatchMapping` (returns `Map<Parent, Child>`) — the framework handles DataLoader integration.

**Developer tooling:**

| Tool | When to Use |
|------|-------------|
| GraphiQL | Development — always enabled locally |
| Postman | Testing collections, team sharing |
| Apollo Sandbox | Schema exploration, Apollo Studio |
| Altair | Subscriptions, scripting, environments |
| Apollo DevTools | Browser extension — cache inspection |

**Day 33 Preview:** Tomorrow we take a sharp turn — AI and Developer Productivity. We'll cover AI/ML fundamentals, how large language models work, prompt engineering, RAG pipelines, vector databases, and AI developer tools including GitHub Copilot and MCP server integration.

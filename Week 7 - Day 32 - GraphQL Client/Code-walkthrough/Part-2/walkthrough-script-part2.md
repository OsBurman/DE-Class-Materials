# Day 32 — GraphQL Client: Part 2
## Instructor Walkthrough Script

**Duration:** ~90 minutes  
**Format:** Live code walkthrough + tool demos  
**Files referenced:**
- `01-caching-and-optimization.tsx`
- `02-testing-graphql-apis.md`
- `03-performance-optimization.md`

---

## SECTION 1 — Opening & Part 1 Recap (5 min)

**Say:**
> "Welcome back. In Part 1 we wired up Apollo Client for React and Apollo Angular to our bookstore GraphQL API. We covered `useQuery`, `useMutation`, `useLazyQuery`, and how to handle the two categories of errors: network errors and GraphQL errors."

> "Part 2 goes deeper into the professional side of GraphQL client work — caching strategy, request batching, testing with real tools, and the performance issue that trips up almost everyone: the N+1 problem."

> "Quick question before we start — after a `useMutation` adds a new book, how does the `BookList` component know to re-render? …Right, we used `refetchQueries`. Today we'll see a more efficient way: updating the cache directly, without a network round trip."

---

## SECTION 2 — How Apollo's InMemoryCache Works (12 min)

**Open `01-caching-and-optimization.tsx` — Section 1 (cache normalization comments)**

**Say:**
> "Before we talk about cache strategies, you need to understand what Apollo's cache actually IS. It's not a simple key-value store where the key is the URL."

> "Open Section 1 in the file. Read the comment block describing the flat normalized structure."

**Point to the flat structure diagram:**
> "When Apollo receives a response with books and their authors, it doesn't store the response as-is. It NORMALIZES it. It splits the nested data into a flat map. Each object gets an entry keyed by `__typename + id`. So `Book:1` is the key for the first book, `Author:10` is the key for the author."

> "Now here's why this matters: Author:10 exists EXACTLY ONCE in the cache, even if 50 books reference that author. If a mutation updates Author:10's name, ALL 50 books automatically re-render. That's the power of normalization."

**Point to `typePolicies`:**
> "By default, Apollo uses `__typename + id` as the cache key. If your object doesn't have an `id` field — maybe it has `bookId`, or a compound key — you configure that here in `typePolicies`. See the `OrderItem` example: compound key from `orderId + bookId`."

> "If you get an Apollo warning that says 'Store already contains an id-less object', it means Apollo can't figure out a unique key for that type. Fix it with `typePolicies`."

---

## SECTION 3 — Fetch Policies (10 min)

**Scroll to Section 2 (fetch policies table and examples)**

**Say:**
> "The `fetchPolicy` controls how Apollo decides between the cache and the network for each query. There are five options — let's walk through the table."

**Point to `cache-first` (default):**
> "`cache-first` is the default. Apollo checks the cache first. If the data is there, return it instantly. If not, hit the network, store the result, and return it. Great for stable data like a product catalog that doesn't change often."

**Point to `network-only`:**
> "`network-only` always goes to the network. Still writes to the cache. Use this for detail pages where you want the most current data — you don't want to show a customer a product that was just discontinued."

**Point to `no-cache`:**
> "`no-cache` bypasses the cache entirely in both directions. Use for sensitive data — like account balances or private user info — that should never be stored in client memory."

**Point to `cache-and-network`:**
> "`cache-and-network` is the best of both worlds for UX. It returns cached data IMMEDIATELY — the user sees something right away — then fires a network request in the background and updates if the data changed. You'll see this pattern in apps that feel instant."

**Point to the `PollingBookList` component:**
> "For data that changes frequently but doesn't justify WebSockets, use `pollInterval`. Set `pollInterval: 5000` and Apollo automatically re-queries every 5 seconds. The user gets a 'Refreshing…' indicator. You can call `stopPolling()` and `startPolling()` to control it."

---

## SECTION 4 — Manual Cache Updates (15 min)

**Scroll to Section 3 (AddBookWithCacheUpdate)**

**Say:**
> "Now the more efficient alternative to `refetchQueries`: manually updating the cache in the mutation's `update` function."

**Point to the `update` callback:**
> "The `update` function receives two things: the `cache` object — that's the InMemoryCache — and the mutation result. It runs after the mutation succeeds."

**Walk through the three steps:**
> "Step 1: `cache.readQuery` — read the current state of the cache for `GET_BOOKS`. This gives you the existing list of books."

> "Step 2: Construct the new state — here we prepend the newly created book to the front of the list."

> "Step 3: `cache.writeQuery` — write the new array back into the cache. Apollo immediately notifies all components that subscribed to `GET_BOOKS`. They re-render with the new book. No extra network request."

**Ask:**
> "What's the trade-off between `refetchQueries` and `update`? …`refetchQueries` is simpler to write but costs a network round trip. `update` is more code but saves the trip and gives instant updates. For small apps, `refetchQueries` is fine. For high-traffic apps, use `update`."

**Scroll to Section 4 (cache eviction)**

> "For deletions, use `cache.evict()`. You identify the object by its cache key — `cache.identify()` generates that key from `__typename + id`. Then `cache.gc()` removes any orphaned references — other cache entries that used to point to the evicted object. After eviction, any query that included the deleted book automatically re-renders without it."

---

## SECTION 5 — Batching Requests (8 min)

**Scroll to Section 5 (BatchHttpLink)**

**Say:**
> "Let's talk about a scenario: your dashboard page has five components that all mount at the same time, each with their own `useQuery`. Without batching, that's five separate POST requests to `/graphql`."

> "Apollo's `BatchHttpLink` solves this. Instead of `HttpLink`, you use `BatchHttpLink`. It waits up to `batchInterval` milliseconds — here 20ms — collecting any operations that come in during that window, then fires ONE HTTP request with all of them as an array."

**Point to the comment about the batch request format:**
> "The server receives an array of operations and returns an array of results. Apollo maps them back to the correct queries automatically."

> "⚠️ Important caveat: your server must support batch requests. Spring for GraphQL doesn't support batching by default — you'd need to configure it. For most projects, `BatchHttpLink` is an optimization you add later if profiling shows it's needed."

**Scroll to PaginatedBookList (Section 7)**

> "One more optimization: `fetchMore` for pagination. Instead of loading all books at once, you load 10, then 10 more when the user clicks 'Load More'. `fetchMore` fires a new query with an updated `offset` and the `updateQuery` function merges the new page with the existing cache data. The list grows without replacing."

---

## SECTION 6 — Testing with Postman (15 min)

**Open `02-testing-graphql-apis.md`**

**Say:**
> "Now let's look at testing GraphQL APIs with real tools. Let's start with Postman since most of you have used it for REST."

**Demo setup (or describe):**
> "In Postman, create a new POST request to `http://localhost:8080/graphql`. Go to the Body tab and select GraphQL — Postman has native GraphQL support. You'll see a query editor and a variables editor."

**Point to the basic query example:**
> "Type in the `GetAllBooks` query. Hit Send. Notice the response is always HTTP 200. This is a critical difference from REST — GraphQL never returns 404 or 500 at the HTTP level for application errors."

**Point to the error response example:**
> "The errors are in the response body under the `errors` key. Look at this example: the `author` field is `null` in `data`, but the `errors` array explains why: 'Author not found for book id: 1'. And crucially — the `path` field tells you exactly which field in the response failed: `['book', 'author']`."

> "This is partial data. GraphQL can return partial data — some fields succeed, some fail — all in one response. Your client must handle `data` AND `errors` in the same response."

**Point to the variables section:**
> "For parameterized queries, the query string goes in the query editor with `$variables`, and the actual values go in the Variables panel as JSON. This is exactly how Apollo Client sends them — the query is static, the variables are dynamic."

**Point to Schema Introspection:**
> "Click the Schema tab in Postman and hit 'Fetch Schema'. Postman runs the introspection query and loads your entire schema. Now you get autocomplete as you type. This is how Postman knows what fields are available — it queried your server's schema."

> "💡 Save your operations as a Postman Collection. Your entire GraphQL API is now documented with runnable examples. Share it with QA, share it with teammates."

---

## SECTION 7 — GraphiQL & GraphQL Playground (10 min)

**Still in `02-testing-graphql-apis.md`**

**Say:**
> "Now let's look at the tools built for GraphQL specifically."

> "**GraphQL Playground** is the older of the two — you'll see it embedded in older APIs and available as a standalone app. It has a Schema tab on the right that auto-generates documentation from your SDL. The key feature: the schema browser is live — it reads from the server's introspection endpoint. Any change to your schema is immediately reflected."

**Point to the introspection query examples:**
> "You can run introspection queries directly in Playground or GraphiQL. `__schema` lists all types. `__type(name: 'Book')` inspects a specific type and its fields. This is how tools like Postman, Apollo DevTools, and code generators discover your schema programmatically."

> "**GraphiQL** — with an 'i' — is what Spring for GraphQL ships built-in. Enable it with `spring.graphql.graphiql.enabled=true` and hit `http://localhost:8080/graphiql`. The standout feature is the **Explorer panel** — a sidebar where you can check boxes to add fields to your query, without typing GraphQL syntax. Perfect for learning a new schema."

**Point to the field selection debugging guidance:**
> "Here's a debugging workflow I use constantly — start minimal, add one field at a time. If your query is complex and something returns null, strip it down to `{ books { id } }`, confirm that works, then add fields back. This isolates exactly which field triggers the problem."

**Point to the nested query examples:**
> "Notice the comment about three-level deep queries. Traversing `books → author → books` looks innocent but could trigger resolver chains. Every level of nesting is a potential performance problem. Field selection isn't just about network payload — it directly controls which resolvers run on the server."

---

## SECTION 8 — N+1 Problem Deep Dive (12 min)

**Open `03-performance-optimization.md` — Section 1**

**Say:**
> "Now the most important performance topic in GraphQL. If you remember nothing else from today, remember N+1."

**Read through the concrete example:**
> "Here's the scenario: a query for books with their authors. Looks innocent. The books resolver runs once — one SQL query. But then GraphQL needs to resolve the `author` field for each book. The naive resolver calls `authorRepository.findById()` for each book."

**Whiteboard the math:**
> "If you have 100 books, that's 1 query for the book list, plus 100 queries for authors = 101 database calls. For 1000 books, that's 1001 queries. This is called N+1: you need 1 query but get N+1."

**Point to the Java resolver code:**
> "Here's exactly what causes it in Spring for GraphQL — the `@SchemaMapping` resolver calls `findById(book.getAuthorId())`. This runs once per book in the result. The resolver doesn't know it's being called in a loop."

**Point to the SQL logging tip:**
> "The easiest way to detect N+1 in development: add `spring.jpa.show-sql=true` to `application.properties`. Watch your console when you run a books query. If you see the same SELECT repeated 50 times with different IDs, you found it."

---

## SECTION 9 — DataLoader Solution (12 min)

**Scroll to Section 2 (DataLoader)**

**Say:**
> "The solution is DataLoader, invented by Facebook. Here's the key insight: instead of resolving each author immediately when the resolver is called, you QUEUE the ID and wait. At the end of the current 'tick', DataLoader looks at all the queued IDs, deduplicates them, and fires ONE query: `SELECT * FROM authors WHERE id IN (1, 2, 3, ...)`."

**Point to the "how it works" diagram:**
> "Walk through this step by step. Tick 1: each book's author resolver calls `dataLoader.load(authorId)`. These don't execute — they queue. Tick 2: DataLoader fires ONE batched query. Notice ID 1 appears twice in the example — DataLoader deduplicates it. The same Author:1 is returned to both resolvers from the batch."

**Point to the Spring for GraphQL DataLoader code:**
> "In Spring for GraphQL, you register DataLoaders using `BatchLoaderRegistry`. The `registerMappedBatchLoader` callback receives a SET of all accumulated IDs and returns a Map from ID to result. Spring routes the results back to each waiting resolver."

**Point to `CompletableFuture` return type:**
> "`dataLoader.load(id)` returns a `CompletableFuture<Author>`, not an `Author`. The future resolves when the batch executes. This is how GraphQL Java suspends the resolver — it awaits the future. Spring handles all the threading."

**Ask:**
> "What's the difference between the N+1 resolver and the DataLoader resolver? …The N+1 version returns `Author` directly. The DataLoader version returns `CompletableFuture<Author>`. That's the tell in a code review — if you see a `@SchemaMapping` returning a plain object and doing a DB call, that's a potential N+1. If you see `CompletableFuture`, DataLoader is in play."

---

## SECTION 10 — Query Complexity & Field Selection (8 min)

**Scroll to Sections 3-6**

**Say:**
> "Two more production-grade concepts: field selection discipline and query complexity limits."

**Point to the field selection section:**
> "We touched on this in GraphiQL — only request what your UI displays. But it's more than UX. In GraphQL, every field you request in a nested type is a potential resolver call. If your `BookList` component only shows `title`, don't request `bio`, `publishedYear`, and `author.bio` in the same query. Those fields trigger resolvers that may hit the database."

**Point to the query complexity section:**
> "For public GraphQL APIs, you MUST add query depth and complexity limits. Otherwise a malicious client could send a deeply nested 'bathtub query' that recurses through relationships and brings your server down. Spring for GraphQL can use `MaxQueryDepthInstrumentation` and `MaxQueryComplexityInstrumentation` from GraphQL Java to enforce limits."

**Point to the summary table:**
> "Here's your performance checklist. Before we go to wrap-up, read through this table. Notice DataLoader is at the top — it's the most impactful optimization for most GraphQL APIs."

---

## SECTION 11 — Wrap-Up & Interview Prep (5 min)

**Say:**
> "Let's recap Part 2:"
> - "Apollo InMemoryCache stores data normalized — flat map of `__typename + id` objects"
> - "Fetch policies: `cache-first` (default), `network-only` (always fresh), `cache-and-network` (instant + update), `no-cache` (never store)"
> - "Manual cache updates with `update()`, `readQuery`, `writeQuery`, and `cache.evict()`"
> - "BatchHttpLink groups simultaneous requests into one HTTP call"
> - "Postman for team testing and sharing; GraphiQL for development; Playground for schema exploration"
> - "N+1: N list items → N resolver calls → N database queries (bad)"
> - "DataLoader: batch all IDs in one tick → 1 database query (good)"
> - "Field selection controls which resolvers run; only request what you display"
> - "Query depth + complexity limits protect production APIs"

---

### 🎯 Interview Questions to Leave With Students

1. **"Explain the N+1 problem in GraphQL and how you solve it."**
   > *N+1 occurs when a list resolver spawns individual sub-resolver calls per item, each firing a separate DB query. DataLoader solves it by batching all IDs within a single execution tick into one query.*

2. **"What does Apollo's `InMemoryCache` normalize, and why does it matter?"**
   > *It flattens nested objects into a map keyed by `__typename + id`. Objects referenced by multiple queries exist once. Updating an object in any mutation automatically updates all queries that reference it.*

3. **"What is the difference between `cache-first` and `network-only` fetch policies?"**
   > *`cache-first` returns cached data and only hits the network on a miss. `network-only` always hits the network but still writes to the cache.*

4. **"Why does GraphQL return HTTP 200 for errors?"**
   > *GraphQL errors are application-level, not transport-level. The HTTP protocol was used successfully — the request was received and processed. Errors are in the response body under the `errors` key.*

5. **"What is the difference between a `networkError` and `graphQLErrors` in Apollo?"**
   > *`networkError` means the request never reached the server or the server returned a non-2xx. `graphQLErrors` means the server responded but reported one or more field-level errors in the response body.*

6. **"When would you use `cache.writeQuery` instead of `refetchQueries` after a mutation?"**
   > *`cache.writeQuery` updates the cache directly without a network round trip — more efficient. `refetchQueries` is simpler to write but costs an extra request. Use `cache.writeQuery` for high-traffic or performance-sensitive mutations.*

---

### 📋 Quick Reference Cheat Card

| Concept | React | Angular |
|---|---|---|
| Query on mount | `useQuery(GQL)` | `apollo.watchQuery({ query })` |
| Query on demand | `useLazyQuery(GQL)` | `apollo.query({ query })` |
| Mutation | `useMutation(GQL)` | `apollo.mutate({ mutation })` |
| Refetch after mutation | `refetchQueries: [...]` | `refetchQueries: [...]` |
| Manual cache update | `update(cache, result)` | `update(cache, result)` |
| Delete from cache | `cache.evict() + cache.gc()` | `cache.evict() + cache.gc()` |
| Error types | `networkError`, `graphQLErrors` | Same, in Observable error |
| Fetch policy | `fetchPolicy: 'cache-first'` | `fetchPolicy: 'cache-first'` |
| Polling | `pollInterval: 5000` | — (use RxJS `interval` + refetch) |

---

*End of Day 32 — Part 2 Script*

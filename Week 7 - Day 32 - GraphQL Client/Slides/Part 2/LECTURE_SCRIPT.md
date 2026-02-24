# Day 32 Part 2 — Caching, DataLoader, Testing, and GraphQL Tooling
## Lecture Script

---

**[00:00–02:30] Opening — Inside the Cache**

Welcome back. Part 1 gave you the mechanics: install Apollo Client, wrap the app in `ApolloProvider`, write queries with `useQuery`, mutations with `useMutation`, and the Angular equivalents. You can fetch and display GraphQL data from a Spring Boot server.

Part 2 goes deeper on two major topics and then covers tooling. First: the Apollo InMemoryCache — how it actually stores and normalizes data, how to control whether it talks to the network or reads from memory, and how to keep it synchronized after mutations. Second: the N+1 problem and DataLoader — arguably the most important performance concept in GraphQL server development. We introduced `@BatchMapping` on Day 31, and today we fully unpack why it exists. And then: the developer tooling ecosystem — Postman, GraphiQL, Apollo Sandbox, Altair — how to use them effectively to test and debug GraphQL APIs.

---

**[02:30–10:00] Slides 2–3 — InMemoryCache Normalization and Fetch Policies**

When Apollo executes a query and gets data back, it doesn't just store it as `{ GET_BOOKS: { data: [...] } }` by query name. It normalizes it. Every object in the response that has a `__typename` and an `id` field gets stored as a separate entry in the cache, keyed by `__typename:id`. So a book with `id: "1"` becomes `Book:1` in the cache. An author with `id: "5"` becomes `Author:5`.

References replace embedded objects. If `Book:1` has an `author` field pointing to `Author:5`, the cache stores it as `{ author: { __ref: "Author:5" } }` — a reference, not a copy. There's one canonical entry for `Author:5` in the entire cache, and every book that references that author points to the same entry.

Why does this matter? Because it means updates are automatic and consistent. Suppose you run a query that fetches the author list and it returns `Author:5` with just a name. Later you run a query that fetches the same author's full profile, including a bio field. Apollo merges the new fields into the existing `Author:5` cache entry. Now every component displaying anything from `Author:5` — whether it came from the book list query or the author profile query — has the full merged data. You didn't have to manually update anything. Single source of truth, maintained automatically.

This is what makes Apollo's cache powerful compared to just storing query results. It's a normalized relational store in memory.

For this to work, Apollo needs to identify objects. The default is `__typename` plus `id`. Apollo automatically adds `__typename` to every query it sends — you don't have to include it manually. But your schema objects need an `id` field. For objects without a standard `id`, you can configure custom `keyFields` in the `InMemoryCache` constructor via `typePolicies`. For objects that shouldn't be cached at all — like `PageInfo` which is not a standalone entity — you can set `keyFields: false` to treat them as embedded value objects with no cache identity.

Now fetch policies. The policy controls Apollo's decision: should I read from the cache, go to the network, or both?

The default is `cache-first`. Apollo checks the cache. If all the requested fields are present, it returns the cached data immediately — no network request. If any field is missing, it goes to the network, caches the result, and returns it. Cache-first is optimal for data that doesn't change often: user profile, application configuration, reference data like countries or categories.

`cache-and-network` returns the cached data immediately, then fires a network request in the background. When the network response arrives, it updates the cache and the component re-renders with fresh data. The user sees something immediately — no loading spinner on repeat visits — and the data catches up to the current server state. This is the right default for most lists and feeds where you want fast display but fresh data.

`network-only` always goes to the network. It updates the cache with the result but doesn't read from it first. Use this for anything where freshness is critical: a checkout page, a live inventory count, a financial balance.

`no-cache` always goes to the network and doesn't write to the cache at all. Use this for sensitive data that shouldn't persist in memory: authentication tokens, security-sensitive user data.

`cache-only` reads from the cache and throws if the data isn't there. This is the offline mode policy — when the app is offline and you're serving from a previously populated cache.

You can set a default at the client level via `defaultOptions` and override per query. You can also use `nextFetchPolicy` to set the policy for subsequent fetches: use `network-only` for the first load to guarantee freshness, then `cache-first` for all subsequent renders of the same component.

---

**[10:00–18:00] Slides 4–5 — Cache Updates After Mutations**

After a mutation, the server has changed. The cache doesn't know this unless you tell it. Apollo has two main strategies for keeping the cache in sync after a write operation.

The first is `refetchQueries`. You list the queries that should be re-run after the mutation succeeds, and Apollo fires those queries automatically. If you create a book and want the book list to refresh, you pass `refetchQueries: [{ query: GET_BOOKS }]` to `useMutation`. Apollo runs the mutation, waits for it to complete successfully, and then fires a fresh `GET_BOOKS` request. The result updates the cache, and all components subscribed to that data re-render.

This is the simplest and most reliable approach. The downside is the extra network request. If you create one book, you refetch the entire book list just to add one item. For most applications this is fine — the list query is cheap. For high-traffic apps or large datasets, that extra round-trip matters.

The `update` function is the alternative. It gives you direct access to the cache before and after the mutation, and you modify it manually. You read the current books from cache with `cache.readQuery`, append the new book to the array, and write it back with `cache.writeQuery`. Apollo sees the cache write, considers the affected cache keys dirty, and notifies all subscribers — components re-render with the updated data. Zero extra network requests.

The tradeoff: the `update` function is more code, and you have to know the exact cache shape and query structure to read and write correctly. For simple add/delete/update operations it's manageable. For complex queries with variables or pagination it can get complicated. A common pattern: use `refetchQueries` during development, optimize to `update` in production if the extra network request matters.

For delete operations, there's a dedicated approach: `cache.evict` and `cache.gc`. When you delete a book, call `cache.evict({ id: cache.identify({ __typename: 'Book', id: deletedId }) })` — this removes the specific `Book:N` entry from the cache. Then call `cache.gc()` to run garbage collection, which removes any references in other cache entries that now point to the evicted object. Clean, surgical, no extra queries.

Now optimistic UI. The idea: assume your mutation will succeed, show the result immediately, and reconcile with the server response when it arrives. This makes the application feel instant. The user clicks "Update Title" and sees the new title immediately — no loading spinner, no waiting.

You pass `optimisticResponse` to `useMutation`. It's the object shape you expect the mutation to return. Apollo immediately writes this to the cache under a temporary optimistic key. Components that subscribe to the relevant cache entries re-render right away showing the optimistic data.

Two things happen next. If the mutation succeeds, Apollo replaces the optimistic entry with the real server response. Components might re-render again if the real data differs from the optimistic assumption — for example, the server assigned a real ID instead of the temp one. If the mutation fails, Apollo rolls back the optimistic entry completely, restoring the original cached data. Components revert to what they showed before the optimistic write.

The critical requirement for optimistic UI: you must provide correct `__typename` and `id` in the `optimisticResponse`. These are the cache keys. If they're wrong, Apollo writes to the wrong cache entry and your components may not update correctly.

---

**[18:00–24:00] Slide 6–7 — Batching and Field Selection**

Query batching with `BatchHttpLink`: in complex UIs, multiple components may each fire a `useQuery` on mount. Without batching, these are five separate HTTP POST requests to `/graphql`. With `BatchHttpLink`, Apollo collects all operations that fire within a short time window — typically 20 milliseconds — and sends them as a single HTTP request with an array body.

The server receives an array of operations and returns an array of results. The advantage: one TCP connection round trip instead of five. This matters on high-latency connections like mobile networks.

The constraint: Spring for GraphQL doesn't support batch requests by default. A batch-aware server layer is required — Apollo Router, a custom Spring `WebInterceptor`, or another gateway. If your team uses Apollo Router as a gateway, batching just works. Without it, you'd need to implement the array-body handling yourself in Spring.

Apollo does have one deduplication feature built in by default with no configuration: if the same query with the same variables fires twice simultaneously, Apollo fires only one network request and delivers the result to both callers. This handles the common case of two components mounting at the same time and each requesting the same data.

Field selection. This isn't a library setting — it's a practice. Request only the fields you render. Every extra field in your GraphQL selection set costs something: a resolver call on the server, potentially a SQL column read, bytes transmitted, and cache memory. If your book list component shows title and author name, query only those. Don't query `isbn`, `pageCount`, `bio`, `reviews`, and twenty other fields because "they might be useful someday."

Fragment reuse makes field selection maintainable. Define a fragment for each component's data needs, and reference that fragment everywhere that component's data is used. When the component's data needs change, update the fragment once — all queries that use it update automatically. This is a practice Angular developers with NgRx or React developers with Redux selectors will recognize: colocating data requirements with components.

---

**[24:00–35:00] Slides 8–10 — N+1 Problem and DataLoader**

The N+1 problem. This is important enough that we're spending ten minutes on it.

Here's the scenario. A client sends this query: `{ books { title author { name } } }`. On the server, the `books` resolver runs first — one database query that returns a list of `Book` objects. Let's say it returns 100 books. Now each of those 100 books needs its `author` field resolved. The `@SchemaMapping` for `author` is called once per book. One hundred separate calls to `authorService.findById(book.getAuthorId())`. One hundred separate SQL queries.

Total: one query for books, one hundred queries for authors. 101 queries for one GraphQL request. That's the N+1 problem — one query for the list, plus one additional query for each item in the list.

At 10 books it's annoying. At 100 books it's a performance problem. At 1,000 books it brings your database to its knees. And if each book also has a `reviews { reviewer { ... } }` relationship, you're now at O(N²) — the book resolver runs N times, and for each book the reviewer resolver runs M times. 1,000 books × 10 reviews = 10,001 queries for one GraphQL request.

The reason the `@SchemaMapping` resolver can't solve this on its own: it's called independently for each parent object. When it runs for Book 1, it doesn't know that Books 2 through 100 are waiting. It has no visibility into the batch. It can only fetch the author for the book it was given.

DataLoader solves this with a two-phase approach. In the collecting phase — which happens within a single event loop tick or thread dispatch — every resolver that needs an author calls `authorLoader.load(authorId)`. This doesn't execute anything. It queues the ID and returns a pending future or promise.

Then, at the end of the tick, the dispatch phase fires. DataLoader collects all the queued IDs, deduplicates them — if five books share the same author, that author ID is in the batch only once — and calls the batch function exactly once with the complete, deduplicated list of IDs. The batch function executes a single SQL query: `SELECT * FROM authors WHERE id IN (10, 11, 12, ...)`. It returns a map from IDs to entities. DataLoader distributes the results back to each waiting future or promise.

Result: two queries for any value of N. The book list query, and one author batch query. It doesn't matter whether you're fetching 10 books or 10,000.

DataLoader also maintains a per-request cache. Within a single GraphQL request, if the same author ID is loaded multiple times, DataLoader returns the same cached result after the first load without any additional queries. This is a request-scoped cache — it's created fresh for each incoming request, so you don't have stale data across requests.

Now let's talk about how Spring for GraphQL exposes this. `@BatchMapping` is the annotation that replaces `@SchemaMapping` for fields where N+1 is a concern. The signature change is the key: instead of receiving one parent object, you receive `List<Book> books` — all the books that need their author resolved. Instead of returning one `Author`, you return `Map<Book, Author>` — a mapping from each book to its resolved author.

You implement the batch function body to execute one efficient query. `authorService.findAllByIds(authorIds)` returns all the authors in a single database call. You stream the result into a map keyed by author ID, then stream the input books into a map from book to its author. Return that map and Spring for GraphQL handles the rest.

For one-to-many relationships — `Book.reviews` — the return type is `Map<Book, List<Review>>`. Group the reviews by book ID with `Collectors.groupingBy(Review::getBookId)`. Return empty lists for books with no reviews — don't return `null` for missing keys.

One important implementation detail: the parent type used as the map key must correctly implement `equals()` and `hashCode()`. If you're using Lombok, `@EqualsAndHashCode` on your entity class. If you're using Java records, this is automatic. JPA entities with `@Entity` often have problematic `equals()` implementations — if you're using JPA entities directly as batch mapping keys, verify they work correctly as hash map keys, or use a DTO projection.

---

**[35:00–46:00] Slides 11–13 — Postman, GraphiQL, and Apollo Sandbox**

Testing GraphQL APIs. Let's start with Postman. Postman has first-class GraphQL support — you don't need to hand-craft JSON request bodies.

Create a new request, set the method to POST, set the URL to your GraphQL endpoint. In the Body tab, select "GraphQL". You'll get a query editor pane and a Variables pane below it. Write your operation in the query editor — you get syntax highlighting. Write your variables JSON in the variables pane. Hit Send.

Postman sends a properly formatted GraphQL request body: `{ "query": "...", "variables": {...}, "operationName": "..." }`. You see the raw response, which you can switch between JSON view and pretty-print.

For authentication, use the Authorization tab — select Bearer Token and paste your JWT. Postman adds the `Authorization: Bearer ...` header automatically. You can also set this in a Postman environment variable so all requests in the collection use the same token.

The import-by-introspection feature is excellent for team workflows. Go to Collections, click New, select GraphQL. Enter your endpoint URL and Postman fetches the full schema via introspection. It then generates a collection with example requests for every query, mutation, and subscription in your schema. Your team has an always-up-to-date API client generated from the live schema. Export the collection to your repository so teammates can import it.

GraphiQL. You already know the basics — enabled via `spring.graphql.graphiql.enabled=true`, access at `/graphiql`. Let me show you how to use it effectively.

The Schema Explorer is the docs panel on the right side. Click the book icon. You'll see your schema organized by Query, Mutation, and Subscription. Click `book` in the Query section — you see the argument `id: ID!` and the return type `Book`. Click `Book` — you see every field with its type and the SDL description you wrote. This is live documentation generated directly from your schema. Every `"..."` and `"""..."""` description in your `.graphqls` files appears here. This is why those descriptions matter — they show up in the tool your team uses every day.

Keyboard workflow. Type `{` in the editor, press `Ctrl+Space` — autocomplete lists every available field on the Query type. Select one, press Tab, and it inserts the field. Now you're inside the selection set. Press `Ctrl+Space` again to see available sub-fields. You can build a complex nested query entirely by keyboard.

`Ctrl+Enter` runs the query. If you have multiple named operations in the document — which you can do for testing multiple queries in one file — use the ▶ dropdown to select which one runs.

The Variables panel at the bottom left is where you write your variables JSON. Change the variable and re-run without editing the query. The Headers panel is next to it — add your Authorization header there for authenticated endpoints.

Apollo Sandbox at `sandbox.apollo.dev`: cloud-hosted, no installation, connects to any endpoint. It's especially powerful when connected to Apollo Studio for a deployed API — you get field usage analytics showing which fields are actually queried in production. That data guides schema cleanup and deprecation decisions.

Altair is the tool for subscriptions testing. GraphiQL's WebSocket support is minimal. Altair handles the full `graphql-ws` protocol, shows the subscription stream in real time, and lets you send messages. It's also the tool to reach for when you need scripting — pre-request scripts for OAuth2 token exchange, for example, run before every request and populate the Authorization header automatically.

---

**[46:00–56:00] Slides 14–15 — Debugging and Nested Queries**

Debugging GraphQL. The most effective strategy is the incremental approach: start with the smallest possible selection set that should work, run it, confirm it works, then add fields one at a time. If something breaks, you know exactly which field caused it.

Start: `{ book(id: "1") { id } }`. This should work. If it returns `null`, the resolver isn't finding the book. If it errors, there's a resolver exception. Check the `errors` array in the response — the `path` field tells you exactly which field errored and the `message` tells you why.

Add `title`. Works? Add `author { id }`. Works? Add `author { name }`. At each step you're narrowing down the problem. This might seem tedious but it's far faster than staring at a full complex query trying to guess which field is broken.

`__typename` is your type diagnostic tool. If you're unsure whether a field is resolving to the correct type — especially with union types — add `__typename` to the selection set. `{ search(term: "patterns") { __typename ... on Book { title } ... on Author { name } } }`. The `__typename` value in the response tells you what the server is actually returning for each item.

The `errors` array habit. Every time you make a GraphQL request — in your component code, in tests, in Postman — look at the `errors` array in the response, not just the `data`. Partial success is possible: `data.books` might have results AND `data.book` might be null with an error in `errors`. If you only check `data`, you miss the error entirely. Make it a habit to log or display `errors` in development mode.

Named operations — always. In server logs, an anonymous query shows up as `anonymous`. A named operation shows up as `GetBookList`. When you're debugging a production issue and scanning server logs at 2am, you want to see `GetBookList` and `CreateBookMutation`, not a wall of `anonymous`. When you configure APM tools like DataDog or New Relic, they group traces by operation name — named operations make your performance dashboards meaningful.

Apollo DevTools: the Chrome and Firefox browser extension. Install it, open your React app, open developer tools, and you'll see an "Apollo" panel. It shows your entire cache contents in real time — you can see every `Book:N` and `Author:N` entry, browse their fields, and watch them update as queries run. It shows every query and mutation fired by the current page, with timing information. You can re-run any query with modified variables directly from DevTools. For debugging cache stale data issues, this tool is indispensable.

Nested queries and pagination. A major GraphQL benefit is the ability to request deeply nested data in a single round trip. `book(id: "1") { title author { name books { title } } reviews(minRating: 4) { rating reviewer { name } } }` — one request, multiple levels of related data. The Spring for GraphQL server runs the root resolver for `book`, then the `@SchemaMapping` resolvers for `author` and `reviews` in parallel where possible, then the nested resolvers.

For pagination with the Relay Connection pattern, `fetchMore` is the Apollo hook method for loading the next page. You call it with the cursor from `pageInfo.endCursor` as the `after` variable. `fetchMore` fires a new query and gives you an `updateQuery` callback where you merge the new page's edges with the existing ones. The result is stored in the cache and the component shows the accumulated list.

The `useIntersectionObserver` pattern — watching when the user scrolls to the bottom of the list and calling `fetchMore` automatically — is the infinite scroll implementation. Wire an intersection observer to the last list item; when it becomes visible, call `loadMore()`.

---

**[56:00–60:00] Slide 16 — Summary and Day 33 Preview**

That completes Day 32 and the two-day GraphQL arc. You built the server yesterday — SDL, Spring for GraphQL annotations, resolvers, subscriptions, schema design. Today you connected the client — Apollo Client for React and Angular, cache management, DataLoader via `@BatchMapping`, and the full tooling ecosystem.

The key takeaways: Apollo's normalized cache is a relational store in memory — understand its key structure to use it correctly. DataLoader / `@BatchMapping` is not optional for any GraphQL server with related types — N+1 will destroy your database performance at scale. And GraphiQL is your best friend during development — use the schema explorer, use autocomplete, use the variables panel.

Tomorrow, Day 33, we make a sharp turn. AI and Developer Productivity. We'll look at how machine learning and large language models actually work — neural networks, transformers, tokenization, embeddings. Then in Part 2: prompt engineering, few-shot learning, agentic AI, RAG pipelines, vector databases, and how tools like GitHub Copilot and MCP server integration fit into your development workflow. It's a big topic and one you'll use immediately.

# Day 31 Part 2 — Arguments, Variables, Fragments, Spring for GraphQL, and Schema Design
## Lecture Script

---

**[00:00–02:30] Opening — From Basic Queries to a Full Server**

Welcome back. In Part 1 we covered the fundamentals: what GraphQL is, why it exists, the SDL type system, scalars, object types, queries, mutations, and subscriptions. You now know how to read a schema and understand what operations are available.

Part 2 has two main movements. First, we'll finish the query language — arguments and variables to make queries dynamic, aliases to request the same field with different arguments, fragments to build reusable query components, and directives to control field inclusion. Second, we build the actual server in Spring Boot — Maven setup, schema file location, the annotation-driven resolver model with `@QueryMapping`, `@MutationMapping`, `@SchemaMapping`. We'll close with schema design best practices that distinguish a thoughtfully built GraphQL API from a hastily thrown-together one.

---

**[02:30–09:00] Slides 2–3 — Arguments and Variables**

In Part 1 we wrote `book(id: "1")` — an argument hardcoded inline. That's fine for understanding the syntax, but it's not how you'd use it in an application. When your React component needs to fetch a book based on what the user clicked, the ID comes from application state, not a hardcoded string. Variables are how you pass that state into a GraphQL operation safely.

Let's start with arguments themselves. Arguments are defined in the schema on any field — root fields like `book(id: ID!)` on the `Query` type, or non-root fields like `reviews(minRating: Int)` on the `Book` type. When you query, you pass argument values. Required arguments marked with `!` must be provided or the query fails validation before execution. Optional arguments without `!` can be omitted — the resolver receives `null` for them or the default value if one is defined in the schema.

Now variables. The pattern: declare the variable in the operation signature with a dollar sign and a type. `query GetBook($bookId: ID!) { book(id: $bookId) { title } }`. The `$bookId` variable is declared with type `ID!` — required. Inside the operation, it's used as the argument value. Then separately, you send a variables JSON object: `{ "bookId": "42" }`. The two travel together in the HTTP request body.

Why variables instead of string interpolation? A few reasons. Safety — you avoid accidentally breaking the query syntax by concatenating user-provided strings. If the ID was `"abc\"badstring"` and you concatenated it, you'd corrupt the query. Variables are sanitized by the transport layer. Reusability — the same query document can be sent repeatedly with different variable values without rebuilding the string. Caching — the query document stays constant while variables change, which enables server-side query caching by query hash. And tooling — IDEs and clients can validate variable types against the schema.

Variables with mutations: `mutation CreateBook($input: CreateBookInput!) { createBook(input: $input) { id title } }`, and the variables JSON is `{ "input": { "title": "Clean Code", "authorId": "5", "pageCount": 431 } }`. The entire input object is passed as one variable. Clean.

---

**[09:00–15:00] Slides 4–5 — Aliases and Fragments**

Aliases solve a specific problem: what if you want to query the same field twice with different arguments in one operation? Without aliases, the JSON keys would collide. Both results would be named `book` — which one does the client use?

The alias syntax: `aliasName: fieldName(arguments)`. So `firstBook: book(id: "1")` and `secondBook: book(id: "2")`. The response comes back with keys `firstBook` and `secondBook`. The schema field is still `book` — the alias just names the result in the response.

You can also use aliases just to rename fields for the client's convenience. If your schema says `title` but your frontend model expects `bookTitle`, you can write `bookTitle: title` in the selection set. The server resolves `title` and names the result `bookTitle` in the JSON. No schema change, no API version bump.

Another alias use case: requesting different filtered views of the same field. `allBooks: books` and `availableBooks: books(availableOnly: true)` in the same query — you get both sets back in one request.

Fragments. Imagine you have 10 queries in your frontend application and every one of them requests the same 6 fields from `Book`: id, title, pageCount, available, and the author's name. Copy-pasting those fields into every query is fine when you have 2 queries. When you have 10 and the schema changes, you update in 10 places. Fragments solve this.

A fragment is a named selection set bound to a type. `fragment BookFields on Book { id title pageCount available author { name } }`. To use it: `...BookFields` — the three dots are the spread operator, same as JavaScript object spread. The fragment expands in place.

Multiple queries in the same document can use the same fragment. Change the fragment definition once and all queries that use it update automatically.

Inline fragments are for union types and interface types. Suppose the schema defines `union SearchResult = Book | Author`. A query on `search` returns an array of either Books or Authors. Without inline fragments, you can't request `title` (a Book field) or `name` (an Author field) without the server not knowing which one to resolve. With inline fragments: `... on Book { title pageCount }` and `... on Author { name books { title } }`. The server applies the appropriate selection set based on the actual type of each result.

The `__typename` built-in field tells you which concrete type you got back. `__typename` on a `SearchResult` returns `"Book"` or `"Author"`. Use it to drive client-side type discrimination.

---

**[15:00–18:00] Slide 6 — Directives**

Directives annotate elements in a query or schema. Two built-in client-side directives you'll use regularly.

`@include(if: Boolean)` — include this field only if the condition is true. `reviews @include(if: $includeReviews)` — if the variable `includeReviews` is false, the `reviews` field is not included in the request and not returned. This is useful for conditional rendering — the book details page shows full info, the book list shows a compact version, and one query template with a variable controls the difference.

`@skip(if: Boolean)` is the inverse — skip this field if the condition is true. `@skip(if: true)` and `@include(if: false)` are equivalent. Which one you use depends on whether your variable name reads more naturally as "includeX" or "skipX."

On the schema side: `@deprecated`. When you add a field to the schema, you can later mark the old one as deprecated. `isbn10: String @deprecated(reason: "Use isbn13 instead. Will be removed 2026-01-01.")`. The field still works — existing clients that use it continue to function. GraphiQL shows it struck through. Apollo Studio tracks how often it's queried. When usage drops to zero, you remove it. This is the GraphQL versioning story — no `/v2` endpoint, just field-level deprecation and migration.

---

**[18:00–24:00] Slide 7 — Spring for GraphQL Setup**

Let's build the server. In your `pom.xml`, add `spring-boot-starter-graphql` and `spring-boot-starter-web`. The GraphQL starter brings in GraphQL Java — the underlying engine — and Spring for GraphQL's annotation processing. The web starter provides the HTTP transport.

One Maven dependency is all you need to turn a Spring Boot app into a GraphQL server. Contrast that with adding GraphQL to Express.js or a Django project — multiple packages, manual wiring. Spring Boot makes this opinionated and simple.

Schema file location: `src/main/resources/graphql/`. Spring for GraphQL auto-discovers all `.graphqls` and `.gql` files in that directory and its subdirectories. You can have one big `schema.graphqls` or split by domain — `book.graphqls`, `author.graphqls`, `review.graphqls`. All files are merged at startup into one schema. For larger projects, splitting by domain is much cleaner.

Three application properties worth knowing. `spring.graphql.graphiql.enabled=true` — this enables the browser-based GraphiQL IDE at `/graphiql`. You'll use this constantly during development. It's a full-featured editor that reads your schema via introspection and gives you autocomplete, type information, error highlighting, and a response pane. Think of it as Swagger UI but for GraphQL. `spring.graphql.path=/graphql` — the default endpoint path; change if needed. `spring.graphql.schema.introspection.enabled=false` — disable in production for security.

Project structure: your resolver classes live in the `controller` package by convention. The `service` and `repository` layers stay the same as any Spring Boot app — GraphQL is just another entry point, the same as a REST controller. The schema lives in resources.

---

**[24:00–33:00] Slides 8–9 — @QueryMapping and @MutationMapping**

Now let's write resolver code. The first thing to understand: GraphQL resolver classes use `@Controller`, not `@RestController`. The distinction matters. `@RestController` is shorthand for `@Controller` plus `@ResponseBody`, which tells Spring to write return values directly to the HTTP response body. Spring for GraphQL handles the response serialization itself — adding `@ResponseBody` would interfere. So: `@Controller` only for GraphQL resolvers.

`@QueryMapping` goes on a method that handles a root-level query field. The method name must match the field name in the schema's `Query` type by default. `field book in the Query type` → method named `book()` in the controller. If you need a different method name, use `@QueryMapping("book")` explicitly.

Let's read through the example. `@Controller`, constructor-inject `BookService`. Method `book()` annotated `@QueryMapping`. Parameter `@Argument String id` — the `@Argument` annotation extracts the `id` argument from the GraphQL operation and binds it to the `id` parameter. Type conversion happens automatically — the GraphQL `ID` scalar arrives as a String, and you convert it in the method body with `Long.parseLong(id)`.

Method `books()` — no arguments, returns `List<Book>`. Straightforward.

Method `searchBooks()` with `@Argument String query` — the argument name in the schema is `query` and the parameter name is `query`. They match, so it works. If they didn't match, you'd write `@Argument("query") String searchTerm`.

Now `@MutationMapping`. Same principle — method name matches the field name in the `Mutation` type. The interesting part is the `@Argument` usage with a complex type.

`@Argument CreateBookInput input` — here `@Argument` doesn't just extract a scalar. It extracts the entire `input` argument — which in GraphQL is an object type (an input type from the schema) — and deserializes it to the Java `CreateBookInput` class. Spring for GraphQL uses Jackson for this. The field names in the input type must match the field names in the Java class. Using Java records is clean here — immutable, no boilerplate.

The `deleteBook` mutation returns `boolean`. In the schema it's `Boolean!`. The Java method returns `boolean` or `Boolean`. Straightforward. But note: returning `boolean` from a mutation means the client has no additional context from the operation — they only know it succeeded or failed. In most cases, returning the deleted object's ID, or the deleted object itself, is more useful.

---

**[33:00–40:00] Slides 10–11 — @SchemaMapping and @SubscriptionMapping**

`@SchemaMapping` is the annotation for non-root field resolvers. Root fields — `book`, `books`, `createBook` — sit on the `Query` or `Mutation` types. Everything else — `author` on `Book`, `books` on `Author`, `reviews` on `Book` — are non-root fields.

Why do you need a resolver for these? Consider the `Book` Java class returned by `@QueryMapping book()`. Your JPA entity might have an `authorId` field — the foreign key — but not a fully-loaded `Author` object. The GraphQL schema says `author: Author!`. When the client requests `author { name }`, something needs to resolve that `Author` from the `authorId`. That's a `@SchemaMapping` resolver.

`@SchemaMapping(typeName = "Book", field = "author")` on a method that takes a `Book source` parameter. The `source` parameter is the parent object — the `Book` instance that was returned by the parent resolver. The method loads the `Author` from the `authorId` on that source book and returns it. Simple.

For the `reviews` field: `@SchemaMapping(typeName = "Book", field = "reviews")`, method takes `Book source`, calls `reviewService.findByBookId(source.getId())`, returns `List<Review>`.

If you're writing a whole class of resolvers for one type, you can put `@SchemaMapping(typeName = "Book")` at the class level and then each method with `@SchemaMapping` just needs its name to match the field name. Cleaner for types with many resolved fields.

Important: if your Java `Book` class does have an `author` field of type `Author` already loaded — say, via a JPA `@ManyToOne` fetch — you don't need a `@SchemaMapping` for it. Spring for GraphQL reads properties via reflection automatically. You only need `@SchemaMapping` when the Java object doesn't already have the value and you need to fetch it separately.

Subscriptions. `@SubscriptionMapping` on a method whose return type is `Publisher<T>` — in practice, `Flux<T>` from Project Reactor. The method is called once when a client subscribes. It returns the reactive stream. The framework keeps the WebSocket open and pushes each emitted item to the subscriber in the selection set shape.

For the `bookAdded` subscription: the method returns `bookEventPublisher.getBookAddedFlux()` — a hot Flux. In a `BookEventPublisher` component, you maintain a `Sinks.Many<Book>` — a Reactor sink that acts like a broadcast channel. When a mutation creates a new book, it calls `bookEventPublisher.publishBook(book)`. That emits the book on the Flux. Every active subscriber receives it.

The subscription with argument `reviewAdded(bookId: ID!)` — the method uses `@Argument String bookId` and filters the Flux: `.filter(review -> review.getBookId().equals(bookId))`. Only reviews for the requested book are emitted to that subscriber.

For WebSocket transport, add `spring-boot-starter-websocket` and set `spring.graphql.websocket.path=/graphql-ws`. The GraphQL over WebSocket protocol is standard — Apollo Client and other clients support it out of the box.

---

**[40:00–43:00] Slide 12 — @BatchMapping Brief Introduction**

Before we get to schema design, I want to plant a seed for Day 32. The N+1 problem.

When you have a `@SchemaMapping` for `author` on `Book`, what happens when the client queries a list of books? The `books` resolver returns 10 books. For each of those 10 books, the `author` resolver runs. That's 10 separate calls to `authorService.findById()`. Ten separate database queries. For 100 books, 100 queries. This is N+1.

`@BatchMapping` is Spring for GraphQL's solution. Instead of resolving one author per book, you receive all the books that need author resolution at once — a `List<Book>` parameter. You execute one database query: `authorService.findAllByIds(allTheAuthorIds)`. You return a `Map<Book, Author>` mapping each book to its resolved author. The framework distributes the results.

One database query instead of N. This is conceptually the same as the DataLoader pattern from the JavaScript GraphQL ecosystem. We'll dig into DataLoader and the full N+1 discussion in Day 32 — it deserves its own session because the client-side batching story is also important. For now: know that `@BatchMapping` exists and that the N+1 problem is something you need to handle in any GraphQL server with related types.

---

**[43:00–46:00] Slide 13 — DataFetchingEnvironment**

Three more tools before schema design — each fills a gap in the Spring for GraphQL picture.

`DataFetchingEnvironment` is an object you can add as a parameter to any resolver method, and the framework injects it automatically. It exposes what the Spring annotations don’t: the client’s selection set, the full argument map, the parent object, and the Spring context.

The most practical use is look-ahead optimization. When a client queries `book(id: "1") { title }` without requesting `author`, there’s no point executing a JOIN to fetch the author. Check: `env.getSelectionSet().contains("author")` — if false, skip the join. Your database query does less work for compact queries.

Other common uses: accessing the Spring security context inside a resolver without threading it through method parameters, and custom logging where you want to record which fields were requested and how long resolution took.

When not to use it for N+1: `@BatchMapping` is the right tool for the N+1 problem across a list of parent objects. `DataFetchingEnvironment` look-ahead is for single-object optimization. Don’t substitute one for the other — they solve different problems.

---

**[46:00–49:00] Slide 14 — @GraphQlExceptionHandler**

When a resolver throws an unhandled exception, Spring for GraphQL catches it, produces a generic error message, and puts it in the `errors` array. The default message may include JPA class names, entity IDs in the form of internal exception messages, or other implementation details — not something you want to expose publicly.

`@GraphQlExceptionHandler` is the fix. It works like Spring MVC’s `@ExceptionHandler` but for GraphQL. Annotate a method with it, declare the exception type as the parameter, and return a `GraphQLError`. You control exactly what the client sees.

In a controller, you handle `BookNotFoundException` by building a `GraphQLError` with `ErrorType.NOT_FOUND`, a clean message like `Book not found: 999`, and an extensions map with a stable error code. The client gets `errors[0].message` and `errors[0].extensions.code` — useful, structured, no internals exposed.

Better practice: centralize these handlers in a `@ControllerAdvice` class. One class, all your GraphQL exception handlers, applied across all controllers. It’s the same pattern as centralized REST exception handling — consistent error responses across your entire API.

The standard `ErrorType` values: `NOT_FOUND`, `BAD_REQUEST`, `FORBIDDEN`, `UNAUTHORIZED`, `INTERNAL_ERROR`. Always treat the `message` field as a public-facing string. Never expose stack traces, class names, or database error details.

---

**[49:00–51:00] Slide 15 — Persisted Queries**

One more concept before schema design: Automatic Persisted Queries, or APQ.

In a production GraphQL app, query strings can be 500+ characters of SDL text. Every request re-sends the full string. APQ replaces the query string with a SHA-256 hash — 64 characters. The first time a client uses a query, it registers the full string with the server. All subsequent requests send only the hash. The server looks it up and executes. Smaller requests, faster on mobile networks.

There’s also a security angle. With a static query allowlist, the server rejects any query hash that isn’t pre-registered at build time — no arbitrary query execution from untrusted clients.

What you need to know for your day-to-day work: APQ is enabled by default in Apollo Client. Your Spring Boot server will receive hashed requests from any Apollo Client frontend. The APQ handling typically lives in the gateway layer — Apollo Router or a reverse proxy — not in your application server code. Spring for GraphQL supports custom `WebInterceptor` hooks for APQ if you’re implementing it without a gateway. During development with GraphiQL, you’ll never see APQ — it always sends full query strings.

---

**[51:00–58:00] Slides 16–18 — Schema Design Best Practices**

Let’s close with what separates a well-designed GraphQL schema from a thrown-together one.

Naming conventions: PascalCase types, camelCase fields and operations, SCREAMING_SNAKE_CASE enum values. Not enforced — but the entire GraphQL toolchain assumes them.

Nullability: make fields non-null by default. Only nullable when a field can genuinely be absent. Don’t use nullability as a substitute for error handling — a bug that fails to load data is not a reason to mark a field nullable.

Input types and mutation returns: every mutation takes an input type — no bare scalar arguments. Always return the modified object. The client needs the server-assigned ID and computed fields in the same response.

Pagination. Offset pagination breaks with live data — new inserts between pages cause the client to see duplicates. Use the Relay Connection pattern for anything user-facing: `BookConnection` with `edges`, `node`, cursor, and `PageInfo`. It’s more schema to write but it’s the industry standard, every client library handles it natively, and it’s correct with concurrent writes.

Error handling: the implicit approach is to throw an exception and let the `errors` array catch it. The explicit approach is result union types — `union DeleteBookResult = Book | NotFoundError | PermissionDeniedError`. All outcomes in `data`, typed and schema-visible, handled with inline fragments. More upfront work, much clearer client contract.

Schema docs and versioning: describe every type and field with SDL strings — GraphiQL tooltips, generated docs, client codegen all use them. For API evolution: add new fields freely, deprecate old ones with `@deprecated` and a removal date, monitor usage, remove only after migration is complete. No version bump needed. The schema carries the migration message.

---

**[58:00–60:00] Slide 19 — Summary and Day 32 Preview**

Day 31 complete. You now know GraphQL end to end: the type system, the SDL, queries and mutations and subscriptions, the full Spring for GraphQL annotation set, and schema design best practices. We added three production tools today — `DataFetchingEnvironment` for look-ahead optimization, `@GraphQlExceptionHandler` for clean structured errors, and APQ for production bandwidth and security.

Tomorrow, Day 32, is the client side: Apollo Client for React and Angular, GraphiQL and Playground in depth, the N+1 and DataLoader pattern in full, and the Apollo InMemoryCache. See you then.

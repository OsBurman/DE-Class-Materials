# Day 31 Part 1 — GraphQL: Schema, Types, Queries, Mutations, and Subscriptions
## Lecture Script

---

**[00:00–03:00] Opening — A New Mental Model for APIs**

Good morning. Welcome to Week 7. This week we're covering GraphQL, GraphQL clients, AI tools, Spring AI, and MongoDB. It's a varied week, but we start with GraphQL, and I want to say upfront: this one is going to change how you think about API design.

For the past five weeks you've been building REST APIs. You know the pattern. A resource has a URL. You GET from it, POST to it, PUT, PATCH, DELETE. Different URL, different resource. Multiple resources, multiple endpoints. It's a familiar, intuitive model.

GraphQL doesn't replace REST everywhere — but it solves a specific set of problems that REST handles awkwardly. By the end of today you'll understand what those problems are, how GraphQL solves them, and how to write a GraphQL API in Spring Boot. By the end of Day 32 you'll be able to consume it from a React or Angular frontend.

Let me start with the three problems that drove Facebook to build GraphQL in the first place.

---

**[03:00–11:00] Slide 2 — The REST Problems GraphQL Solves**

Problem one: over-fetching. You're building a mobile app. You need to show a list of books — just the title and the cover image. You hit `GET /api/books`. The server sends back an array of book objects, each with 25 fields: title, ISBN, publication date, page count, categories array, reviews array, author with full profile, publisher details, and so on. You needed 2 fields. The server gave you 25. On a mobile network, that extra data costs bandwidth, battery, and time. Over-fetching.

Problem two: under-fetching, also called the N+1 request problem. You want to show a list of 10 books, each with the author's name next to it. You call `GET /api/books` — you get 10 books back, each with an `authorId` but no author name. Now you need to make 10 more requests: `GET /api/authors/1`, `GET /api/authors/3`, `GET /api/authors/7`, and so on. One request per book. Ten books become eleven requests. One hundred books become one hundred and one requests. Under-fetching.

Problem three: endpoint explosion. Different clients need different data shapes. The web dashboard needs everything. The mobile app needs a slim version. The admin panel needs fields the regular API doesn't expose. So you end up with `/api/books`, `/api/books/mobile`, `/api/books/v2`, `/api/admin/books`. Each new client requirement potentially spawns a new endpoint. Then versioning compounds it — you can't change an existing endpoint because it'll break existing clients, so you add a `/v2`. The API grows in complexity proportional to the number of clients, not the number of resources.

Facebook hit all three of these problems simultaneously in 2012. Their mobile app needed to show a News Feed — a complex data structure pulling together posts, user profiles, comments, likes, media attachments, notifications. REST wasn't cutting it. So they built GraphQL internally, shipped it on their mobile apps, proved it worked at scale, and open-sourced it in 2015. That's the origin story.

---

**[11:00–16:00] Slides 3–4 — GraphQL Overview and Comparison Table**

So what is GraphQL? It's a query language for APIs — meaning the client sends a structured query describing exactly what data it needs — and a runtime that executes those queries on the server. It is not a database. It doesn't store data. It's a layer between your client and your data sources.

Three properties I want you to internalize. First: single endpoint. Everything goes to `POST /graphql`. The operation is in the request body, not the URL. Second: the client decides the shape of the response. You write a query saying "I want these three fields from Book and this one field from Author." That's exactly what you get back. Nothing more, nothing less. Third: strongly typed schema. The API is defined by a schema that acts as a contract. Every type is named. Every field has a type. Every operation is documented. You can't make a query that the schema doesn't support — the server will reject it with a validation error before touching any data.

Let me put REST and GraphQL side by side, and I want you to be clear-eyed about this — GraphQL is not universally better. It's better for specific problems.

REST has multiple endpoints, one or more per resource. GraphQL has one. REST returns a fixed shape per endpoint. GraphQL returns whatever the client asks for. REST commonly causes over-fetching and under-fetching. GraphQL eliminates both. REST has an optional type system — you can document with OpenAPI, but it's not enforced. GraphQL has a mandatory built-in type system.

On the other side: REST caching is trivial — GET requests are cacheable by browsers, CDNs, and proxies by URL. GraphQL requests are all POST — HTTP caching doesn't apply out of the box. File uploads in REST are multipart form data — standard and well-supported. File uploads in GraphQL require non-standard extensions. REST has a low learning curve — everyone knows HTTP verbs. GraphQL has a medium learning curve — you need to understand the schema, the query language, resolvers.

For simple CRUD services, one team, one client? REST is often the right call. For complex data graphs with multiple client types, rapid schema evolution, and clients with very different data needs? GraphQL pays off. For this course, we're teaching both because both are in the industry.

---

**[16:00–21:00] Slide 5 — GraphQL Request Architecture**

Let me trace exactly what happens from the moment a client sends a GraphQL request to when it gets a response.

Client sends a POST request to `/graphql`. The request body contains the GraphQL query string — a structured text document — and optionally a variables object and an operation name. The server receives it and processes it in four phases.

Phase one: parse. The server tokenizes the query string and builds an Abstract Syntax Tree — an internal representation of the operation. This is like how a Java compiler first tokenizes your source code before it can work with it.

Phase two: validate. This is where GraphQL gets its power. The server checks the AST against the schema: does this field exist on this type? Is this argument the right type? Are all required arguments provided? If the query references a field that doesn't exist in the schema, you get a validation error immediately — before any resolver runs, before any database is queried. This is comparable to compile-time type checking.

Phase three: execute. The server calls the resolver functions for each field in the query. A resolver is a function you write that knows how to fetch data for a specific field. The resolver for `book(id: "1")` might call `bookRepository.findById(1L)`. The resolver for `author` within a Book might call `authorRepository.findById(book.getAuthorId())`. The framework orchestrates all of this automatically from the query structure.

Phase four: response. The resolved data is assembled into a JSON response that exactly mirrors the structure of the query. Every field you asked for is in the response. Nothing you didn't ask for is there.

The response is always wrapped: `{"data": {...}}` on success, with an optional `"errors": [...]` array for failures. We'll come back to error handling.

---

**[21:00–27:00] Slide 6 — SDL: Schema Definition Language**

Now let's learn to write GraphQL schemas. The Schema Definition Language — SDL — is how you define the contract for a GraphQL API.

SDL is a simple, readable language. Not Java, not JSON, not YAML. Its own syntax. You write it in `.graphqls` files — by convention, in `src/main/resources/graphql/` in a Spring Boot project. Spring for GraphQL automatically picks up any `.graphqls` or `.gql` files in that directory at startup.

Let me read through a minimal schema for our bookstore. We start with type definitions. `type Book` — the opening brace, then fields. `id: ID!` — the ID scalar, exclamation mark for non-null. `title: String!` — a required string. `author: Author!` — this field's type is another object type, also non-null. `pageCount: Int` — nullable integer, no exclamation mark. `available: Boolean!` — non-null boolean.

Then `type Author` with its own fields. `id: ID!`, `name: String!`, `books: [Book!]!` — a non-null list of non-null Books.

Then the special types: `type Query` and `type Mutation`. These are the entry points. Every field on `Query` is a root-level read operation you can call. Every field on `Mutation` is a root-level write operation. These are the only two types that have special meaning to GraphQL — everything else is just a named type.

`Query` has: `book(id: ID!): Book` — takes a non-null ID argument, returns a nullable Book (nullable because the book might not exist). `books: [Book!]!` — no arguments, returns a non-null list of non-null Books.

Two approaches to schema building. Schema-first: you write the SDL file first, then implement Java code to match it. Code-first: you annotate Java code and the framework generates the schema. Spring for GraphQL uses schema-first by default. The SDL file is the source of truth. This is the approach we'll use today — it makes the contract explicit and visible.

---

**[27:00–33:00] Slides 7–8 — Scalars, Types, and Relationships**

Let's dig into the type system details. Five built-in scalar types. `String` — UTF-8 text, maps to Java `String`. `Int` — 32-bit signed integer, maps to Java `int` or `Integer`. `Float` — double-precision floating point, maps to Java `double` or `Double`. `Boolean` — true or false, maps to Java `boolean` or `Boolean`. `ID` — a unique identifier, always serialized as a string even if you store it as a Long internally, maps to Java `String` or `Long`.

The `ID` type deserves a moment. It means "this field uniquely identifies a resource." It's always serialized as a string in JSON, so even if your database ID is a long integer, the client sees it as `"1"`, not `1`. This is intentional — it allows IDs to be UUIDs, composite keys, or opaque strings without changing the client contract.

Now the non-null modifier. By default — and this trips people up — every field in GraphQL is nullable. The server can return `null` for any field unless you add `!`. `title: String` means the server might return null for title. `title: String!` means the server guarantees it won't. In Java, this corresponds roughly to returning an `Optional` versus a concrete value.

The list modifier with brackets. `[Book]` is a nullable list of nullable Books. `[Book!]` is a nullable list of non-null Books — the list might be null, but if it exists, each item in it is a real Book. `[Book!]!` is a non-null list of non-null Books — what you almost always want when a relationship should always return a list. `[Book]!` is a non-null list of nullable Books — rarely used.

For object type relationships: `Book` has a field `author: Author!`. `Author` has a field `books: [Book!]!`. These form a bidirectional graph. But this does not mean GraphQL will automatically follow both directions simultaneously. It means you can query in either direction. If you query a Book and include `author`, the author resolver runs. If you query an Author and include `books`, the books resolver runs. Only what the client explicitly asks for gets resolved. That's the model.

Custom scalars exist for types like Date, DateTime, URL. Spring for GraphQL includes an extended scalars library — adding `@Bean` for scalar types like `LocalDate`, `LocalDateTime`, and `URL`. We'll use these in Part 2.

---

**[33:00–40:00] Slides 9–10 — Queries**

Let's write some queries. A query is an operation that reads data. It's the GraphQL equivalent of a GET request.

The simplest form — the anonymous shorthand. Just an opening brace, field names, closing brace. `{ books { id title } }`. No keyword, no name. This works and many tutorials show it, but don't use it in production — it has no name, which makes debugging impossible.

The proper form: the `query` keyword, an operation name, then the selection set. `query GetAllBooks { books { id title author { name } } }`. The operation name is `GetAllBooks` — PascalCase by convention. This name appears in server logs, error messages, and APM tools. When you see a slow query in your monitoring dashboard, you want a name, not just `anonymous`.

Notice the selection set for `books` includes an `author` sub-selection with `name`. This is one request. The server will fetch books and for each book, resolve the author's name. One HTTP round trip.

You can pass arguments inline. `book(id: "1")` — the `id` argument matches the parameter defined on the `Query.book` field in the schema. The argument is quoted because `ID` is serialized as a string. Arguments can be of any scalar type, enum, or input type.

Let's talk about the introspection feature briefly because it's important for tooling. You can query `__schema` and `__type` to discover the API's capabilities. This is how GraphiQL and Playground provide autocomplete — they query `__schema` and build a type-aware editor. This is incredibly powerful for developer experience.

And a security note for when you go to production: disable introspection. The property in Spring Boot is `spring.graphql.schema.introspection.enabled=false`. In development it's fine — in production, exposing your full schema to the public gives attackers a map of your API surface.

---

**[40:00–48:00] Slides 11–12 — Mutations and Subscriptions**

Mutations are how you write data. They map to the `Mutation` type in the schema. Same query language, same selection set syntax, but the keyword is `mutation` instead of `query`.

Before we write mutations, let me explain input types. An `input` type in GraphQL is like a DTO for mutation arguments. You can't use a regular object type like `Book` as an argument — object types are for output only. For input, you define a separate `input CreateBookInput { ... }`. Fields in an input type follow the same non-null and list rules.

Why the separation? Regular types can have circular references and complex relationships that don't make sense as input. Input types are flat or shallowly nested DTOs that represent what the client is sending. And it's good practice — your "create book" input and your "book" response type have different shapes. The input might have `authorId`. The response has `author { name }` — a resolved object, not an ID.

Write the mutation operation: `mutation CreateBook { createBook(input: { title: "Clean Code", authorId: "5", pageCount: 431 }) { id title author { name } } }`. The mutation calls `createBook`, passes the input inline, and the selection set says "give me back the id, title, and author name of the created book." Best practice: always return the created or updated object from a mutation. The client gets the server-assigned ID and any computed fields without a follow-up query.

One important behavior: mutations execute serially. If you send a mutation document with two mutation fields, the server runs them in order, one after the other. Queries may be parallelized, mutations are not. This prevents race conditions in multi-step write operations.

Subscriptions are for real-time push. Clients open a WebSocket connection to the server, send a subscription operation, and the server pushes data whenever the subscribed event occurs. Schema-side, you define a `Subscription` type just like `Query` and `Mutation`. Fields on `Subscription` are the events clients can subscribe to.

`subscription OnBookAdded { bookAdded { id title author { name } } }`. This operation, once sent, keeps the connection open. Every time a book is added on the server, the subscriber receives a push with the requested fields.

When do you use subscriptions? Live notifications — "new message in your chat." Real-time collaborative features — document editing, shared cursor positions. Live dashboards — inventory levels, live prices, streaming metrics. When not to use them: don't replace polling with subscriptions unless you need low latency. Subscriptions add WebSocket infrastructure complexity. If "refresh every 30 seconds" meets the requirement, use it. If you need true real-time push, subscriptions are the answer. In Spring for GraphQL, the server method returns a `Publisher<T>` — which is Project Reactor's `Flux<T>` — and the framework handles the WebSocket transport.

---

**[48:00–54:00] Slides 13–14 — Introspection and Execution Lifecycle**

Let's talk about introspection more carefully because it's foundational to the GraphQL ecosystem.

Every GraphQL server automatically exposes a meta-API about itself. Query `__schema` and you get back a full description of every type in the system, every field on every type, every argument, every deprecation notice, every description string you wrote in SDL. Query `__type(name: "Book")` and you get back every field on `Book` with their types.

This powers everything. GraphiQL — the browser-based IDE — queries `__schema` when it loads and uses that to provide context-aware autocomplete. You start typing a field name and it shows you only fields that exist on that type. You hover over a field and it shows the type. That IDE runs entirely from introspection. Apollo Studio, the enterprise GraphQL tooling platform, reads your schema via introspection to generate documentation and analyze query performance. TypeScript clients can generate type-safe code from introspection.

The execution lifecycle. I walked through it on the architecture slide, but let me put it in concrete terms. A client sends `query { book(id: "1") { title } }`. The server parses it — tokenizes and builds an AST. Validates — checks that `book` exists on `Query`, that `id` is a valid argument for it, that `title` exists on `Book`. If anything fails validation, the entire request is rejected with a detailed error message indicating the line and column of the problem. No resolver runs. No database is touched. This is like a compile error — caught before execution.

If validation passes, execution begins. The root resolver for `Query.book` is called with argument `id = "1"`. It calls the repository, finds a book, returns a `Book` entity. Now for each field the client requested on that book — `title` — the resolver for `Book.title` runs. For a simple field backed by a Java getter, this is automatic — the framework calls `book.getTitle()`. The scalar value is serialized and placed in the response at the correct path.

---

**[54:00–58:00] Slide 15 — GraphQL Error Format**

The error model is different from REST and it catches people off guard. In REST, you return HTTP 200 for success and 4xx/5xx for errors, and clients check `response.status`. In GraphQL, the HTTP status is almost always 200, including when there's a domain error like "book not found." The error information is in the response body.

The response has two top-level fields: `data` and `errors`. On full success: `data` has your results, no `errors` field. On failure: `data` might be null or partial, and `errors` is an array with one or more error objects. Each error object has: `message` — human-readable description. `locations` — line and column in the query where the error relates. `path` — the path in the response data where the error occurred — `["book", "author"]` means the `author` field within the `book` result failed. `extensions` — a custom object where you can put application-specific error codes.

The partial success case is interesting. Suppose a query asks for a book's `title` and `author`. The title resolves fine. The author resolver throws an exception. GraphQL can return `{ "data": { "book": { "title": "Clean Code", "author": null } }, "errors": [{ "message": "Author resolver failed", "path": ["book", "author"] }] }`. The client gets the title it asked for, plus an error saying the author failed. Partial success.

What this means for you as an API client: always check for `errors` in the response, not just `response.status === 200`. This is a shift from REST habits. In Day 32, we'll look at how Apollo Client handles this automatically.

---

**[58:00–60:00] Slide 16 — Summary and Part 2 Preview**

Let me summarize Part 1.

GraphQL solves three REST problems: over-fetching (server returns too much), under-fetching (multiple round trips needed), and endpoint explosion (different clients need different shapes). It does this with a single endpoint, client-driven field selection, and a typed schema.

SDL defines the contract. Scalars are the primitives: String, Int, Float, Boolean, ID. Non-null modifier `!` and list modifier `[]` control nullability and collections. Object types define the graph with relationships between types. The `Query` type is the entry point for reads. The `Mutation` type for writes — runs serially, should return the modified object. The `Subscription` type for real-time push over WebSocket.

Execution goes: parse → validate → execute → response. Errors are always in the body with an `errors` array — HTTP 200 doesn't mean success in GraphQL.

After break, Part 2. We'll add the advanced query features: arguments and variables to make queries dynamic, aliases to request the same field twice, fragments to share field lists across queries. Then we'll build the actual server in Spring Boot — Maven setup, schema file location, `@QueryMapping`, `@MutationMapping`, `@SchemaMapping`. And we'll cover schema design best practices: nullability philosophy, pagination patterns, error handling patterns, and how GraphQL handles versioning. See you in an hour.

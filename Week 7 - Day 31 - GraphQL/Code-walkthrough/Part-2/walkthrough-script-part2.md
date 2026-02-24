# Day 31 — GraphQL: Part 2
## Instructor Walkthrough Script

**Duration:** ~90 minutes  
**Format:** Live code walkthrough + slides  
**Files referenced:**
- `01-advanced-queries.graphql`
- `02-spring-graphql-server.java`

---

## SECTION 1 — Opening & Part 1 Recap (5 min)

**Say:**
> "Welcome back, everyone. In Part 1 we covered the *what* of GraphQL — what it is, why it exists, how the schema definition language works, and how to write basic queries, mutations, and subscriptions. If REST is ordering from a fixed menu, GraphQL is ordering exactly what you want, one trip to the kitchen."

> "In Part 2 we go deeper. We're going to look at advanced query features that make GraphQL really powerful — arguments, variables, aliases, and fragments — and then we're going to flip to the server side and wire everything up using Spring for GraphQL. By the end of this session you'll have a complete picture of how a GraphQL API is built and consumed."

**Quick verbal check:**
> "Before we dive in — what's the difference between a Query and a Mutation? …Right. Query = read. Mutation = write. Good."

---

## SECTION 2 — Arguments (10 min)

**Say:**
> "Open `01-advanced-queries.graphql`. The first thing I want to show you is **arguments**."

> "In REST, when you want a specific resource you encode the identifier in the URL — `/books/42`. In GraphQL, you pass it directly as an argument to the field."

**Point to the `book(id: "1")` query in the file:**
> "Notice the parentheses after the field name. That's the argument syntax. The field `book` accepts an `id` argument, and we're passing the string `"1"`. The server resolves *only that book* and returns exactly the fields we asked for."

> "Arguments aren't limited to IDs. Look at the `books(genre: SCIENCE_FICTION, limit: 5)` example. We're filtering by genre — which is an enum value — and limiting the result count. The schema defines which arguments each field accepts, so the client can't just make up argument names."

**Key point to emphasize:**
> "Arguments are part of the schema contract. The SDL you write on the server says `books(genre: Genre, limit: Int): [Book!]!` — that's what the client is allowed to pass. Anything outside that is a schema validation error before your resolver even runs."

---

## SECTION 3 — Variables (12 min)

**Say:**
> "Now look at the next section — **variables**. This is the feature that takes GraphQL from a toy to a production tool."

**Point to the hardcoded query first:**
> "In the arguments section, we hardcoded the value directly in the query string: `book(id: "1")`. That works for demos, but think about a real app. Your UI doesn't know at compile time which book the user clicked. You'd have to build the query string dynamically, which is messy and opens the door to injection bugs."

**Point to the variable version:**
> "Variables solve this. We declare variables at the operation level using a `$` prefix and a type: `query GetBook($bookId: ID!)`. That `$bookId` is now a named slot. We pass it into the argument: `book(id: $bookId)`. The actual value is sent separately in the variables JSON object — completely separate from the query string."

> "Your query string is now a static, pre-parseable template. The dynamic part is just a JSON payload. This is cleaner, safer, and it enables query caching on the client."

**Whiteboard if helpful:**
```
Query string (static):  query GetBook($bookId: ID!) { book(id: $bookId) { title } }
Variables (dynamic):    { "bookId": "42" }
```

> "One important syntax note: the `!` after `ID` means the variable is non-null. If you don't pass it, GraphQL throws a validation error before the request hits your server. That's free input validation."

**Ask the class:**
> "What's the GraphQL equivalent of a SQL prepared statement? …Variables. Same concept — parameterize the query, pass data separately."

---

## SECTION 4 — Aliases (8 min)

**Say:**
> "Next up: **aliases**. This one is short but surprisingly useful."

**Point to the aliases section in the file:**
> "What if I need two books in the same query? I can't write `book(id: "1") { ... } book(id: "2") { ... }` — GraphQL won't allow duplicate field names in the same selection set. That's where aliases come in."

> "An alias lets you rename the result field. `firstBook: book(id: "1")` means 'fetch `book(id: "1")` and put the result under the key `firstBook` in the response'. Then `secondBook: book(id: "2")` puts the second result under `secondBook`."

**Expected response shape:**
```json
{
  "data": {
    "firstBook": { "title": "Clean Code" },
    "secondBook": { "title": "The Pragmatic Programmer" }
  }
}
```

> "The response mirrors the alias names, not the field names. This is purely client-side naming — the server sees two separate resolver calls."

> "Real-world use case: a dashboard that compares two items side by side. Aliases let you make both requests in one round trip."

---

## SECTION 5 — Fragments (12 min)

**Say:**
> "The last advanced query feature is **fragments**, and this one directly impacts maintainability."

**Set up the problem:**
> "Imagine you have twenty different queries across your app that all need the same book fields: `id`, `title`, `author`, `genre`, `publishedYear`. Right now you'd copy-paste that list into every query. What happens when you need to add a new field — say `isbn`? You hunt down all twenty queries and add it."

**Point to the fragment definition:**
> "Fragments solve this. `fragment BookFields on Book` defines a reusable selection set. `on Book` is the type condition — this fragment can only be spread on a `Book` type."

> "Then wherever you need those fields, you spread the fragment with `...BookFields`. That's three dots — same syntax as JavaScript spread, intentionally similar."

**Point to the inline fragment:**
> "There's also **inline fragments**, used primarily with interfaces and unions. If a field returns a `SearchResult` which could be a `Book` or an `Author`, you use `... on Book { title }` and `... on Author { name }` to conditionally select fields based on the actual runtime type."

**Why this matters for production:**
> "In large applications, fragments are the GraphQL equivalent of reusable component prop types. They keep your queries DRY, they can be co-located with UI components in frameworks like React, and tools like GraphQL Code Generator use them to auto-generate TypeScript types."

---

## SECTION 6 — Spring for GraphQL: Setup (10 min)

**Say:**
> "Now we flip to the server side. Open `02-spring-graphql-server.java`. We're using **Spring for GraphQL**, which is Spring's official integration — it lives at `spring.io/projects/spring-graphql`."

**Dependency setup (describe verbally or show pom.xml):**
> "The Maven dependency you need is `spring-boot-starter-graphql`. That's it. Spring Boot's auto-configuration does the rest — it creates the `/graphql` HTTP endpoint, configures the GraphQL Java engine, and scans for your schema file."

**Schema file location:**
> "Your SDL schema file lives at `src/main/resources/graphql/schema.graphqls`. Spring for GraphQL automatically picks it up. The `.graphqls` extension is conventional — you'll also see `.graphql`. Both work."

> "So the dev loop is: write your schema in SDL, write resolver methods in Java, run the app. No XML configuration, no manual wiring."

**GraphQL Playground / Altair:**
> "In development, hit `http://localhost:8080/graphiql` — note the extra `i` — for a built-in browser IDE where you can explore the schema and test queries. You enable it with `spring.graphql.graphiql.enabled=true` in your `application.properties`."

---

## SECTION 7 — @QueryMapping (10 min)

**Say:**
> "Now let's look at the controller. In Spring MVC you use `@RestController`. With Spring for GraphQL you use `@Controller` — and that's intentional. GraphQL controllers are not HTTP controllers; they're schema resolvers."

**Point to the `@QueryMapping` method:**
> "The `@QueryMapping` annotation maps a method to a field on the root `Query` type. The method name must match the field name in the schema. If the schema says `books: [Book!]!`, the method must be named `books`."

> "The return type must match too. Spring for GraphQL uses reflection to serialize the Java object into the GraphQL response. It handles `List`, `Optional`, primitive types — all the usual Java patterns."

**Ask:**
> "What does the `books()` method do here? …It calls the service layer and returns a list. The GraphQL engine then applies the field selections the client asked for — so even if the service returns 30 fields, only the ones the client requested travel over the wire."

---

## SECTION 8 — @Argument and @MutationMapping (10 min)

**Say:**
> "When a query field takes arguments, you extract them in the resolver method using `@Argument`. Look at the `bookById` method."

**Point to the annotation:**
> "`@Argument String id` — Spring for GraphQL reads the `id` argument from the incoming request and binds it to the method parameter. You can use `@Argument` with primitives, Strings, and custom input types."

> "For input objects — like when you create a book — the argument is usually typed as your Java input class. Spring deserializes the incoming GraphQL input map into the Java object automatically. Look at the `createBook` mutation method."

**Point to `@MutationMapping`:**
> "`@MutationMapping` works exactly like `@QueryMapping` — the method name must match the mutation field name in the schema. The business logic lives in the service layer; the controller just extracts arguments and calls the service."

> "Important: mutations should return something. In the schema you define what a mutation returns — typically the modified object. Never return `void` from a mutation resolver; the client needs confirmation data."

---

## SECTION 9 — @SubscriptionMapping and Reactor (8 min)

**Say:**
> "Subscriptions are where Spring for GraphQL gets interesting. Look at the `bookAdded` method with `@SubscriptionMapping`."

**Point to the return type:**
> "Notice it returns `Flux<Book>` — that's Project Reactor. For subscriptions, you *must* return a reactive `Publisher`. `Flux` is a stream of zero-or-more items over time. The GraphQL engine subscribes to this publisher and pushes each emitted item to the connected client."

> "The transport is WebSocket by default. The client opens a WebSocket connection, sends the subscription operation, and receives events asynchronously as the server emits them."

> "In our example, the service maintains a `Sinks.Many<Book>` — a Reactor hot sink. When `createBook` is called, it emits the new book into the sink. The subscription's `Flux` is backed by that sink, so all subscribed clients receive the event."

**Caution note:**
> "In production you'd use a message broker like Redis pub/sub or Kafka to back subscriptions, so they work across multiple server instances. The local `Sink` approach only works on a single node."

---

## SECTION 10 — @SchemaMapping and Nested Resolvers (8 min)

**Say:**
> "The last annotation to cover is `@SchemaMapping`. This handles nested field resolution."

**Point to the `booksForAuthor` method:**
> "Our schema has an `Author` type with a `books` field. When the client queries `author { books { title } }`, GraphQL needs to know how to fetch the books for a given author. That's what `@SchemaMapping(typeName = \"Author\", field = \"books\")` declares."

> "The method receives the parent `Author` object as a parameter — Spring injects it automatically. The resolver uses the author's ID to fetch the associated books."

**The N+1 Problem — mention it:**
> "This is where the famous **N+1 problem** lurks. If you query 10 authors and each triggers a separate `booksForAuthor` resolver call, that's 1 query for authors + 10 queries for books = 11 database round trips."

> "The solution is **DataLoader** — a batching utility built into GraphQL Java. Instead of resolving each author's books immediately, you accumulate all author IDs and resolve them in one batch query. Spring for GraphQL has first-class `DataLoader` support. That's a more advanced topic but worth knowing it exists."

---

## SECTION 11 — Schema Design Best Practices (7 min)

**Say:**
> "Let's step back from code and talk about schema design philosophy, because the schema is your API contract — once clients depend on it, changing it is painful."

**Naming conventions:**
> "Fields use `camelCase`. Types use `PascalCase`. Enum values use `SCREAMING_SNAKE_CASE`. This is the GraphQL community standard — don't deviate."

**Non-null discipline:**
> "Be deliberate with `!`. Adding `!` to a field later is a breaking change — if a client was handling null and you add non-null, nothing breaks. But removing `!` is safe. Default to nullable, add non-null only when you're confident the field will always have a value."

**Pagination:**
> "Never return a raw list for fields that could be large. Use the **Connections pattern** — `BooksConnection` with `edges`, `node`, and `pageInfo`. This is the Relay spec and it's widely adopted. Spring for GraphQL has `ScrollSubrange` support for cursor-based pagination."

**Versioning:**
> "GraphQL APIs don't version the way REST does. Instead of `/v2/graphql`, you deprecate fields with `@deprecated(reason: \"Use newField instead\")`. Clients migrate at their own pace. Old fields stay until usage drops to zero."

**Error handling:**
> "GraphQL returns HTTP 200 even for errors — the errors are in the response body under the `errors` key. Spring for GraphQL maps exceptions to `GraphQLError` objects. Use `@GraphQlExceptionHandler` (similar to `@ExceptionHandler` in MVC) to customize error responses."

---

## SECTION 12 — Wrap-Up & Interview Prep (5 min)

**Say:**
> "Let's recap what we covered in Part 2:"
> - "Arguments — pass parameters directly to fields"
> - "Variables — parameterize operations for safety and reuse"
> - "Aliases — request the same field multiple times with different keys"
> - "Fragments — reusable selection sets, inline fragments for union types"
> - "Spring for GraphQL — `@QueryMapping`, `@MutationMapping`, `@SubscriptionMapping`, `@SchemaMapping`"
> - "Flux for subscriptions, N+1 problem and DataLoader"
> - "Schema design best practices"

---

### 🎯 Interview Questions to Leave With Students

1. **"What is the N+1 problem in GraphQL and how do you solve it?"**
   > *Expected: N+1 occurs when a list resolver triggers individual sub-resolvers per item. DataLoader batches and caches those calls.*

2. **"What's the difference between `@QueryMapping` and `@SchemaMapping`?"**
   > *Expected: `@QueryMapping` maps to a root Query field. `@SchemaMapping` maps to a field on any non-root type, used for nested resolvers.*

3. **"Why do GraphQL mutations return data instead of void?"**
   > *Expected: The client needs to update its local cache/state after a write. Returning the modified object lets the client update without a follow-up query.*

4. **"How does GraphQL handle versioning compared to REST?"**
   > *Expected: GraphQL uses field deprecation (`@deprecated`) instead of URL versioning. Fields are deprecated, clients migrate, then deprecated fields are removed.*

5. **"What is a fragment and when would you use it in production?"**
   > *Expected: Reusable selection sets. Used to keep queries DRY, co-locate data requirements with UI components, and enable code generation tools.*

---

### 📋 Quick Reference Cheat Card

| Feature | Syntax |
|---|---|
| Argument | `book(id: "1") { title }` |
| Variable declaration | `query GetBook($id: ID!) { ... }` |
| Variable usage | `book(id: $id) { ... }` |
| Alias | `myBook: book(id: "1") { title }` |
| Fragment definition | `fragment F on Book { id title }` |
| Fragment spread | `...F` |
| Inline fragment | `... on Book { title }` |
| Query resolver | `@QueryMapping` |
| Mutation resolver | `@MutationMapping` |
| Subscription resolver | `@SubscriptionMapping` returns `Flux<T>` |
| Nested resolver | `@SchemaMapping(typeName="Author", field="books")` |
| Extract argument | `@Argument String id` |

---

*End of Day 31 — Part 2 Script*


SLIDE 1: Title Slide
Slide content: "GraphQL: Schemas, Queries, and Server Implementation" | Today's Date | Your Name

SCRIPT:
"Welcome everyone. Today we're doing a deep dive into GraphQL — not just what it is, but how it works at every layer, from the type system all the way down to a running Java server. By the end of this session you should be able to read a GraphQL schema, write queries with variables and fragments, and have a solid mental model for building a server with Spring Boot. Let's get into it."

SLIDE 2: Today's Learning Objectives
Slide content:

Understand GraphQL fundamentals and architecture
Define schemas with types and fields
Write queries with variables and fragments
Build GraphQL servers with Java/Spring Boot
Implement resolvers for queries and mutations
Compare GraphQL benefits and trade-offs vs REST


SCRIPT:
"Here's what we're covering today. These aren't abstract — by the end of the hour, each of these should feel concrete and actionable. We'll move through theory quickly and spend a lot of time on actual syntax and code."

SLIDE 3: What Is GraphQL?
Slide content:

Query language for APIs (not a database query language)
Runtime for executing those queries
Developed by Facebook (2012), open-sourced 2015
Single endpoint, client-driven data fetching
Strongly typed by design


SCRIPT:
"First, let's be precise about what GraphQL is. It is a query language — but not for databases. It's for your API. Clients write queries describing exactly what data they want, send them to a single endpoint, and the server returns exactly that shape of data. Nothing more, nothing less.
Facebook built this internally in 2012 because they were rebuilding their mobile apps and REST was causing real problems — too many round trips, over-fetching huge payloads on slow mobile connections, under-fetching requiring multiple requests to assemble one screen's worth of data. They open-sourced it in 2015 and it's been widely adopted since.
The key words here are single endpoint and client-driven. In REST you have many endpoints, each returning a fixed shape. In GraphQL you have one endpoint, and the client tells the server exactly what it wants."

SLIDE 4: GraphQL vs REST — The Core Problems REST Has
Slide content:

Over-fetching: Getting more data than you need
Under-fetching: Needing multiple requests to get enough data (N+1 requests)
Versioning: REST APIs accumulate v1, v2, v3...
Documentation drift: Endpoints and docs fall out of sync


SCRIPT:
"Let me make the REST comparison concrete. Say you're building a profile page that needs a user's name, their last 3 posts, and the title of each post's author. In REST you might hit /users/42 — which gives you the full user object including fields you don't need — then /users/42/posts — which gives you all posts with all fields — then maybe /users/{id} for each post's author. That's three round trips, lots of wasted data, and it gets worse as screens get more complex.
Over-fetching wastes bandwidth and slows mobile clients. Under-fetching forces waterfalls of sequential requests. API versioning creates a maintenance headache. And REST documentation is often wrong because nothing enforces that the docs match the actual API.
GraphQL solves all four of these directly."

SLIDE 5: GraphQL vs REST — The Benefits
Slide content:

Ask for exactly what you need — no over/under-fetching
One request assembles data from multiple resources
Strongly typed schema = self-documenting API
Schema is the contract — evolve without versioning
Introspection: query the API about itself
Great for complex, interconnected data


SCRIPT:
"In GraphQL, one query can traverse relationships and pull exactly the fields you need across multiple resources in a single round trip. The schema is the documentation — and unlike a README, it's enforced at runtime, so it can't drift. You can add fields to types without breaking existing clients. You can deprecate fields. All of this without versioning.
Introspection is a killer feature — you can query the API itself to discover what types and fields exist. This is how tools like GraphiQL and GraphQL Playground work. The IDE knows your schema and gives you autocomplete.
Now, GraphQL isn't always better. It has trade-offs. HTTP caching is harder because everything is a POST to one URL. It introduces complexity on the server side. It can enable expensive queries if you're not careful. For simple CRUD APIs, REST may be perfectly fine. But for complex, client-driven data requirements, GraphQL shines."

SLIDE 6: GraphQL Architecture Overview
Slide content:

Diagram: Client → HTTP POST to /graphql → GraphQL Server → Resolvers → Data Sources (DB, REST APIs, etc.)
The schema sits at the center — it's the contract
Request contains: query/mutation/subscription + variables
Response: { "data": {...}, "errors": [...] }


SCRIPT:
"Here's the architecture. The client sends an HTTP POST to a single /graphql endpoint. The body contains the operation — a query, mutation, or subscription — and optionally some variables. The GraphQL server parses and validates this against the schema. If valid, it executes it by calling resolvers — functions you write that know how to fetch each piece of data. Resolvers can hit a database, call another REST API, read from cache, anything.
The response always has the same shape: a data field with the results, and optionally an errors array. Note that GraphQL can return partial results — some fields succeed, some fail, and both are communicated in the same response.
The schema is the center of gravity. Everything orbits it. The client uses it to write valid queries. The server uses it to validate requests. Resolvers are registered against it. Let's talk about the schema now."

SLIDE 7: Schema Definition Language (SDL)
Slide content:
graphqltype Book {
  id: ID!
  title: String!
  year: Int
  inPrint: Boolean!
  rating: Float
  author: Author!
}

type Author {
  id: ID!
  name: String!
  books: [Book!]!
}

SCRIPT:
"The Schema Definition Language — SDL — is the syntax you use to define your GraphQL schema. It's human-readable and language-agnostic. Every GraphQL server, regardless of language, understands SDL.
Here we're defining two types: Book and Author. Look at the fields — each has a name and a type. The exclamation mark means non-null. title: String! means title will never be null. year: Int without the bang means year can be null — maybe we don't always have it.
Author! on the author field means every book has an author, and it will never be null. [Book!]! on the books field means the list itself is non-null AND every item in the list is non-null. These nullability rules are important — they affect client code and server contracts significantly."

SLIDE 8: Scalar Types
Slide content:
ScalarDescriptionExampleStringUTF-8 text"Hello"Int32-bit integer42FloatDouble-precision decimal3.14Booleantrue/falsetrueIDUnique identifier (serialized as String)"user_42"
Custom scalars: Date, DateTime, URL, Email, etc.

SCRIPT:
"GraphQL has five built-in scalar types. String, Int, Float, Boolean — these are what you'd expect. ID is interesting: it represents a unique identifier, always serialized as a string over the wire even if the underlying value is a number. Use ID for primary keys.
You can also define custom scalars. Libraries provide common ones like Date, DateTime, Email, URL. You define the serialization and validation logic in your server code, then declare it in your schema like scalar Date. This lets you have semantic types beyond primitives."

SLIDE 9: Object Types, Fields, and Special Types
Slide content:
graphqltype Query {
  book(id: ID!): Book
  books: [Book!]!
  searchBooks(title: String!): [Book!]!
}

type Mutation {
  createBook(title: String!, authorId: ID!): Book!
  deleteBook(id: ID!): Boolean!
}

type Subscription {
  bookAdded: Book!
}

Query, Mutation, Subscription are entry points — every schema needs at least Query


SCRIPT:
"In GraphQL, Query, Mutation, and Subscription are special root types. They're the entry points into your schema — think of them as the top-level controllers.
Query is for reads. Mutation is for writes — creating, updating, deleting data. Subscription is for real-time event streams. Every schema must have a Query type. Mutation and Subscription are optional.
Fields on these root types can take arguments — notice book(id: ID!) takes an id argument, and searchBooks takes a title. These arguments are how clients filter and parameterize their requests.
The return types follow the same rules as regular fields. book(id: ID!) returns Book — nullable, because we might not find a book with that id. books returns [Book!]! — always a list, never null, and no null items."

SLIDE 10: Other SDL Constructs
Slide content:
graphqlenum Genre {
  FICTION
  NON_FICTION
  SCIENCE
  HISTORY
}

input CreateBookInput {
  title: String!
  year: Int
  genre: Genre!
  authorId: ID!
}

interface Node {
  id: ID!
}

union SearchResult = Book | Author

SCRIPT:
"Beyond object types and scalars, SDL gives us a few more building blocks.
Enums define a closed set of values. Genre can only be FICTION, NON_FICTION, SCIENCE, or HISTORY — the server will reject anything else.
Input types are like object types but used only as arguments. You can't use a regular Book type as a mutation argument — you use an input type instead. This separation is intentional: output types can have resolvers and computed fields; input types are pure data.
Interfaces define a contract — any type implementing Node must have an id field. This is useful for building generic patterns like pagination.
Unions say 'this field could be one of these types.' A search result might be a Book or an Author — the client uses inline fragments to handle each case. We'll touch on fragments in a moment."

SLIDE 11: Writing Queries — Basic Syntax
Slide content:
graphqlquery GetBook {
  book(id: "1") {
    id
    title
    year
    author {
      name
    }
  }
}
Response:
json{
  "data": {
    "book": {
      "id": "1",
      "title": "Dune",
      "year": 1965,
      "author": { "name": "Frank Herbert" }
    }
  }
}

SCRIPT:
"Here's a basic query. The keyword query followed by an optional operation name — GetBook. Operation names are optional but strongly recommended. They show up in logs, error messages, and developer tools, making debugging far easier. Always name your operations.
Then we have the selection set — the curly braces define what fields we want. We ask for book with id: 1, and within it we want id, title, year, and the author's name. We don't ask for rating or inPrint — we don't need them for this view, so we don't get them.
The response mirrors the query shape exactly. The server returns a JSON object under data that matches the structure you asked for. This is one of GraphQL's core guarantees: the response shape matches the query shape."

SLIDE 12: Arguments and Variables
Slide content:
graphql# With inline argument (not recommended for dynamic values)
query {
  book(id: "1") { title }
}

# With variables (correct approach)
query GetBook($bookId: ID!) {
  book(id: $bookId) {
    title
    author { name }
  }
}
Variables passed separately:
json{ "bookId": "42" }

SCRIPT:
"Arguments let you pass parameters into queries. You can inline them, but for dynamic values you should always use variables. Variables are declared in the operation signature with a dollar sign and a type. Then you pass the actual values as a separate JSON object alongside the query — not interpolated into the query string.
Why does this matter? Two reasons. First, security — never concatenate user input into a query string. That's the GraphQL equivalent of SQL injection. Variables keep data separate from the query structure. Second, performance — a query with variables can be parsed and validated once and cached. The same query shape is reused with different variable values.
Variables are typed. $bookId: ID! means this variable is a required ID. If you pass null or omit it, the server rejects the request before it even reaches your resolvers."

SLIDE 13: Aliases
Slide content:
graphqlquery TwoBooks {
  firstBook: book(id: "1") {
    title
    author { name }
  }
  secondBook: book(id: "2") {
    title
    author { name }
  }
}
Response:
json{
  "data": {
    "firstBook": { "title": "Dune", "author": {...} },
    "secondBook": { "title": "Foundation", "author": {...} }
  }
}

SCRIPT:
"What if you want to query the same field twice with different arguments in one request? Without aliases, you'd have a naming conflict — two fields both named book. Aliases solve this. You prefix the field with aliasName: and the result is keyed by that alias in the response.
Here we fetch two books in one request, aliasing them as firstBook and secondBook. This is also useful when you want to rename fields in the response to something more meaningful for your client, without the API changing."

SLIDE 14: Fragments
Slide content:
graphqlfragment BookDetails on Book {
  id
  title
  year
  rating
}

query LibraryPage {
  featured: book(id: "1") {
    ...BookDetails
    author { name }
  }
  recent: book(id: "2") {
    ...BookDetails
  }
}

SCRIPT:
"Fragments are reusable selection sets. You define a fragment on a type — fragment BookDetails on Book — and then spread it into any selection set using the ... spread syntax.
This is DRY for queries. If multiple queries or parts of a query need the same fields from Book, you define it once as a fragment and reuse it. If the required fields change, you update one fragment.
Fragments also enable a powerful pattern called colocation in frontend frameworks — React components can define their own fragments declaring what data they need, and those fragments are composed into a single query at the top level. This keeps data requirements close to the code that uses them.
There are also inline fragments, used with interfaces and unions: ... on Book { title } and ... on Author { name }. These let you conditionally select fields based on the concrete type."

SLIDE 15: Mutations
Slide content:
graphqlmutation CreateBook($input: CreateBookInput!) {
  createBook(input: $input) {
    id
    title
    author {
      name
    }
  }
}
Variables:
json{
  "input": {
    "title": "The Left Hand of Darkness",
    "year": 1969,
    "genre": "FICTION",
    "authorId": "author_7"
  }
}

SCRIPT:
"Mutations use the same syntax as queries, just with the mutation keyword. You send arguments describing the change you want to make, and you get to specify a selection set on what comes back. This is great — after creating a book you can immediately get back the generated ID, the full object, related data, whatever you need, in the same request.
One important rule: mutations at the same level in a query execute sequentially, not in parallel. This prevents race conditions when mutations depend on each other. Fields within a mutation's selection set still resolve in parallel like queries.
Best practice: use input types for mutation arguments rather than flat scalar arguments. As your mutations grow in complexity, input types are much easier to maintain and evolve."

SLIDE 16: Subscriptions
Slide content:
graphqlsubscription OnBookAdded {
  bookAdded {
    id
    title
    author { name }
  }
}

Uses WebSocket (or SSE) instead of HTTP
Server pushes updates when events occur
Client subscribes once, receives many responses
Common use cases: live feeds, notifications, dashboards, chat


SCRIPT:
"Subscriptions are GraphQL's real-time mechanism. The client sends a subscription operation — same syntax, subscription keyword — but instead of getting one response and closing the connection, it establishes a persistent connection, typically a WebSocket, and receives events pushed from the server as they happen.
Here, a client subscribes to bookAdded. Whenever a book is added to the system, every subscribed client receives a push with the specified fields.
On the server side, subscriptions are typically backed by a pub/sub system. When a mutation creates a book, it publishes an event. The subscription resolver subscribes to that event stream and forwards matching events to connected clients.
Spring for GraphQL supports subscriptions via WebSocket out of the box. We'll see how this looks in implementation shortly."

SLIDE 17: GraphQL Java Ecosystem
Slide content:

graphql-java — Core execution engine (low-level, verbose)
Spring for GraphQL — Official Spring integration (recommended for Spring Boot apps)

Auto-configuration, annotation-driven
Tight integration with Spring MVC / WebFlux
Built on graphql-java under the hood


Netflix DGS — Domain Graph Service framework (also Spring-based, popular at scale)


SCRIPT:
"In the Java world, the foundational library is graphql-java. It's the execution engine — it parses queries, validates them against a schema, and executes the resolver functions you provide. It works but it's quite verbose to use directly.
Spring for GraphQL is the official Spring integration, introduced in Spring Boot 2.7 and matured in 3.x. It builds on graphql-java and gives you annotation-driven development, auto-configuration, and deep integration with the Spring ecosystem. This is what you'd use in a typical Spring Boot application and what we'll focus on.
Netflix DGS is another popular option — it's a framework built at Netflix, also on top of graphql-java, with some additional features. Worth knowing about but Spring for GraphQL is a solid default choice today."

SLIDE 18: Spring Boot Project Setup
Slide content:
pom.xml dependencies:
xml<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-graphql</artifactId>
</dependency>
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-web</artifactId>
</dependency>
application.properties:
propertiesspring.graphql.graphiql.enabled=true
spring.graphql.schema.locations=classpath:graphql/
Schema file: src/main/resources/graphql/schema.graphqls

SCRIPT:
"Setting up is straightforward. Add spring-boot-starter-graphql and a web starter to your project. Spring Boot auto-configures a /graphql HTTP endpoint and, with graphiql.enabled=true, a browser-based IDE at /graphiql for testing your API interactively.
By convention, you place your SDL files in src/main/resources/graphql/. Spring automatically loads all .graphqls and .gql files from that directory. You can split your schema across multiple files — Spring merges them. This is great for larger projects where you want to organize schema by domain.
That's the setup. No manual schema registration, no manual servlet configuration. Spring Boot handles it."

SLIDE 19: Defining a Controller — Queries
Slide content:
java@Controller
public class BookController {

    @Autowired
    private BookRepository bookRepository;

    @QueryMapping
    public Book book(@Argument String id) {
        return bookRepository.findById(id).orElse(null);
    }

    @QueryMapping
    public List<Book> books() {
        return bookRepository.findAll();
    }
}

SCRIPT:
"In Spring for GraphQL, you use @Controller classes with @QueryMapping methods. Each @QueryMapping method maps to a field on your schema's Query type by method name — the method book maps to the book query field. Arguments from the query are injected using @Argument.
Under the hood, these become DataFetchers — the graphql-java term for resolver functions. Spring registers them automatically. The return type should match the schema — returning null for a nullable field, throwing an exception (which becomes a GraphQL error) for non-nullable fields that can't be resolved.
This annotation model is clean and familiar to Spring developers. You stay in the Spring idiom — dependency injection, repositories, services — and just add GraphQL-specific annotations."

SLIDE 20: Mutations and Nested Resolvers
Slide content:
java@MutationMapping
public Book createBook(@Argument CreateBookInput input) {
    Book book = new Book(input.title(), input.year(), input.authorId());
    return bookRepository.save(book);
}

// Nested resolver — resolves Author for a Book
@SchemaMapping(typeName = "Book", field = "author")
public Author author(Book book) {
    return authorRepository.findById(book.getAuthorId());
}

SCRIPT:
"@MutationMapping works exactly like @QueryMapping but for mutation fields. The method name maps to the mutation field name, arguments come in via @Argument.
Now, @SchemaMapping is important. When you have nested types — like author on Book — you need a resolver that fetches the author given a book. @SchemaMapping(typeName = "Book", field = "author") registers a resolver that receives the parent Book object and returns the associated Author.
This is how GraphQL resolves graphs of data. The root resolver fetches the top-level object. Then field-level resolvers for nested types are called as needed, only if the client actually requested those fields. If the client doesn't ask for author, that resolver never runs.
One caution here — this naive implementation has the N+1 problem. If you fetch a list of 100 books and each needs its author, you run 100 separate author queries. The solution is DataLoader — we'll cover that in a future lesson when we get to performance optimization."

SLIDE 21: Subscriptions in Spring for GraphQL
Slide content:
java@SubscriptionMapping
public Flux<Book> bookAdded() {
    return bookEventPublisher.getBookStream();
}

Returns Flux<T> (Project Reactor) — reactive stream
Spring auto-configures WebSocket endpoint
Client connects via WebSocket using graphql-ws protocol


SCRIPT:
"Subscriptions in Spring for GraphQL use Project Reactor. Your @SubscriptionMapping method returns a Flux<T> — a reactive stream of events. Spring handles the WebSocket connection, the subscription protocol, and pushing each emission to the connected client.
You'd typically have a publisher service that maintains these streams and emits events when mutations happen. The subscription resolver just subscribes to that stream.
This is a brief overview — subscriptions and reactive programming deserve a deeper session, but you now know the shape of how it works in Spring."

SLIDE 22: Schema Design Best Practices
Slide content:

Design for the client, not the database
Use meaningful, domain-driven names
Prefer specific input types over long argument lists
Use non-null (!) deliberately — wrong nullability is hard to fix
Paginate list fields (Relay-style cursor pagination or simple offset)
Keep mutations focused — one mutation, one intent
Deprecate don't delete: @deprecated(reason: "Use newField instead")
Avoid deeply nested mutations


SCRIPT:
"A few best practices before we wrap up.
Design your schema for the client's needs, not as a reflection of your database schema. These are often different. Your database might have a normalized book_authors join table — your GraphQL schema doesn't need to expose that.
Use non-null carefully. Mark fields non-null when you're confident they'll always have a value. Getting this wrong is painful because clients code against nullability guarantees. If you later make a non-null field nullable, that's a breaking change.
Paginate list fields. Never return an unbounded list of records. Use cursor-based pagination for large, frequently-updated datasets or simple offset pagination for simpler cases.
Use @deprecated to evolve the schema gracefully. Clients can see deprecation warnings in their IDE. You add the new field, deprecate the old one, give clients time to migrate, then remove it. No versioning needed.
These practices make your schema a stable, trustworthy contract."

SLIDE 23: Putting It All Together — Request Lifecycle
Slide content:

Client sends POST /graphql with query + variables
Spring parses the HTTP request
graphql-java parses and validates the query against the schema
If invalid: return errors, stop
Execution begins at root resolvers (@QueryMapping / @MutationMapping)
Nested field resolvers (@SchemaMapping) called as needed
Results assembled into response shape
{ "data": {...}, "errors": [...] } returned


SCRIPT:
"Let's walk through the full request lifecycle once so it all connects. Client sends a POST to /graphql. Spring deserializes the body — pulls out the query string, operation name, and variables. Hands it to graphql-java. graphql-java parses the query into an AST, validates it against your schema — checks that fields exist, types match, required variables are present. If anything is wrong, it returns errors right there without touching your resolvers.
If valid, execution starts. The root resolver is called — your @QueryMapping method. It returns some data. If the client asked for nested fields, the corresponding @SchemaMapping resolvers are called with the parent as input. All of this happens field by field, level by level. Results are assembled, and the final JSON response is returned.
Understanding this lifecycle is key to debugging GraphQL servers — knowing which phase a problem is in tells you exactly where to look."

SLIDE 24: Common Trade-offs and When to Use GraphQL
Slide content:
GraphQLRESTClient-driven, flexible queriesServer-driven, fixed responsesSingle endpointMultiple endpointsStrongly typed, self-documentingVaries — often needs separate docsHTTP caching harderHTTP caching straightforwardComplex queries can be expensivePredictable performance per endpointGreat for complex, interconnected dataGreat for simple CRUD, public APIs

SCRIPT:
"Let's be balanced. GraphQL is powerful but it's not universally better than REST.
HTTP caching with REST is easy — GET requests with URLs are natively cacheable by browsers, CDNs, and proxies. GraphQL's single POST endpoint makes this harder. You can work around it with persisted queries but it adds complexity.
Expensive queries are a real concern. A malicious or careless client could write a deeply nested query that hammers your database. REST endpoints have predictable cost. With GraphQL you need query complexity analysis and depth limiting — again, a future topic.
For simple APIs, especially public ones, REST's simplicity and ubiquity are genuine advantages. For complex product APIs serving multiple clients with varying data needs — mobile, web, third-party — GraphQL's flexibility is a significant win.
Use the right tool for the job, and now you know enough to make that call."

SLIDE 25: Summary & Key Takeaways
Slide content:

GraphQL: query language for APIs, client-driven, single endpoint
SDL defines the schema — types, scalars, queries, mutations, subscriptions
Queries fetch data, mutations change it, subscriptions stream it
Variables keep queries safe and cacheable; fragments keep them DRY
Spring for GraphQL: @QueryMapping, @MutationMapping, @SchemaMapping
Schema is a contract — design it thoughtfully, evolve it gracefully
GraphQL solves over/under-fetching; trade-offs exist around caching and query cost


SCRIPT:
"Let's bring it together. GraphQL is a query language for APIs backed by a strongly typed schema. The SDL gives you a clear, readable way to define your data model. Clients write queries that mirror the response shape they want, use variables for safety, and use fragments for reuse. The server implements resolvers — in Spring Boot, annotated controller methods — that fetch data for each field.
The schema is the backbone of everything. Invest in designing it well. It's the contract between your server and every client that uses it.
Coming up in future lessons: the N+1 problem and DataLoaders, authentication and authorization in GraphQL, pagination patterns, error handling, query complexity limiting, and testing.
Any questions before we move on?"

---

## INSTRUCTOR NOTES

**Missing:** The N+1 problem and DataLoaders are referenced as "coming up in future lessons" at the end but should ideally have at least a brief awareness slide in this session. Students building even a simple schema with nested resolvers will encounter N+1 immediately, and knowing the term and the solution (DataLoader batching) prevents hours of confusion. Error handling patterns (partial results, the `errors` array alongside `data`) are important for production use and should be confirmed as covered in the Spring Boot integration portion.

**Unnecessary/Too Advanced:** Nothing to remove. The content is well-scoped and appropriate for the topic.

**Density:** The SDL and type system sections are well-paced. The Spring Boot integration section should have enough practical code to let students build a working endpoint — verify it does not just show annotations without a runnable example.

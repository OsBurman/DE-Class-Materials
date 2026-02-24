# Walkthrough Script — Part 1: GraphQL Overview, Schema & Operations

**Day 31: GraphQL**
**Total estimated time: ~95 minutes**

---

## Segment 1 — Opening (5 min)

"Good morning, everyone. Welcome to Week 7. We've spent six weeks on the full Java/Spring
stack — REST APIs, databases, security, JWT. All of that is going to feel very familiar
when we look at today's topic, because GraphQL is solving the same problems we've been
solving — just with a completely different approach.

Here's the key mental shift: with REST, the server decides what data you get. With GraphQL,
the CLIENT decides. The client writes a query that says exactly what fields it needs, and
the server returns exactly that — no more, no less.

Today is the concept and syntax day. Part 1: what GraphQL is, how schemas work, and how to
write queries, mutations, and subscriptions. Part 2: we'll build a real GraphQL server with
Spring Boot.

Let's open `01-graphql-overview-and-schema.md` first."

---

## Segment 2 — GraphQL vs REST: The Problem (15 min)

**[Open: `01-graphql-overview-and-schema.md` — Section 1 & 2]**

"I want to start with the problem GraphQL is solving. Let me show you two scenarios that you
have probably already hit in your own work."

**[Point to Section 2 — Over-fetching example]**

"Scenario 1: over-fetching. You're building a mobile app. On the home screen, you need to show
a list of book titles. Just titles. But when you call `GET /api/books`, the server sends you
20 fields per book — ISBN, publisher, weight, dimensions, tags, inventory counts.

**Question:** What's the problem with this?"

*[Wait for responses. Expected: bandwidth waste, slower mobile load times, unnecessary data.]*

"Right — bandwidth, battery, and performance. On a mobile connection, sending 20 fields when
you need 1 is genuinely expensive. GraphQL solves this completely — you ask for `title`, you
get `title`."

**[Point to under-fetching section]**

"Scenario 2: under-fetching. You're building a book detail page. You need the book data AND
the author data. With REST you have to call two endpoints: `/api/books/42`, then
`/api/authors/101`. If you have a list of 10 books, that's 11 requests — the initial list
request plus one per author. This is the famous N+1 problem.

With GraphQL, you write ONE query that describes BOTH the book and its author, and you get
both in a single round trip. The server does the joining for you."

**[Point to comparison table — Section 2]**

"Walk through the table with me. One endpoint vs many. Client-defined shape vs server-defined.
Type system is first-class — the schema IS the contract between frontend and backend. Versioning
is different — instead of `/v2/books`, you add new fields and deprecate old ones.

Note the trade-offs column: caching is harder because all GraphQL requests are POST to the same
URL. HTTP caching (ETags, CDN caching) doesn't work out of the box. For public APIs with lots
of anonymous users, REST + HTTP caching is often better. GraphQL shines when you control
both client and server and clients need flexible querying."

**[Point to Architecture diagram — Section 3]**

"Three things happen when a GraphQL request arrives:
1. PARSE — the query string is tokenized and parsed into an AST
2. VALIDATE — the query is checked against the schema. If you ask for a field that doesn't
   exist, the server rejects it before execution. Compare to REST: a client could call
   `/api/books?fields=title,nonExistentField` and get a 200 with inconsistent results.
3. EXECUTE — the server calls RESOLVERS for each requested field. Resolvers are just functions
   that return data.

We'll implement resolvers in Part 2 — for now, think of each field in your schema as having
a function behind it that fetches the actual data."

---

## Segment 3 — Schema Definition Language (SDL) (10 min)

**[Point to Section 4 — SDL]**

"The SDL is how you write your schema. It's a declarative language — you're describing WHAT
exists, not how to fetch it. That's the resolver's job.

The SDL lives in `.graphqls` or `.graphql` files. Spring for GraphQL looks in
`src/main/resources/graphql/` by default — we'll see this in Part 2.

The syntax is clean and readable. Look at this Book type — even someone who's never seen
GraphQL can probably read this and understand what a Book is."

---

## Segment 4 — Scalars (8 min)

**[Point to Section 5 — Types and Scalars]**

"Five built-in scalar types — these are the leaf values that fields actually resolve to.

`String` — text. `Int` — 32-bit integer. `Float` — double-precision decimal. `Boolean` —
true/false. `ID` — unique identifier. **Watch out about ID:** it's always serialized as a
String in JSON, even if your database uses integer IDs. This is intentional — IDs should
be treated as opaque handles, not numbers you do arithmetic with.

Custom scalars: you can define your own. `Date`, `DateTime`, `URL`, `Email`. The SDL declares
them; the actual serialization/deserialization logic lives in Java code. You'll often see
`scalar DateTime` in production schemas to handle ISO-8601 timestamps properly.

**Question:** Why is ID a String even if the database uses an integer primary key?"

*[Expected: IDs should be opaque; comparing or sorting IDs numerically is an anti-pattern;
it allows switching to UUID without breaking clients.]*

---

## Segment 5 — Object Types and Fields (12 min)

**[Point to Section 6 — Object Types]**

"Now the most important part of schema design — object types.

Look at the Author type. `id: ID!` — the exclamation mark is CRITICAL. This means non-null.
The server GUARANTEES this field will never return null. Without `!`, a field is nullable by
default.

**Question:** Why would you want a nullable field? Isn't it annoying to handle nulls?"

*[Expected: some data legitimately doesn't exist — an author's email might be private, a
book might not have a publication year yet.]*

"Right. Use `!` for fields that are fundamental to the type's identity. Omit `!` when the
data might legitimately be absent.

Now look at `books: [Book!]!` on the Author type. Two exclamation marks, different positions.
This trips up almost everyone. The outer `!` means the LIST itself will never be null.
The inner `!` (inside `[]`) means each ITEM in the list will never be null.

So `[Book!]!` means: 'I will always return a list (possibly empty), and every element
in that list will be a real Book, never null.'

**Watch out:** Once you declare a field as `!` non-null and clients start relying on it,
making it nullable later is a BREAKING CHANGE. Clients have to add null checks. Be thoughtful
about where you put `!`."

**[Point to Enums]**

"Enums restrict a field to a fixed set of string values. `genre: Genre!` — the genre can only
be one of FICTION, NON_FICTION, SCIENCE, etc. The schema enforces this — if a client sends an
invalid genre in a mutation, the server rejects it at validation time, before any resolver runs."

**[Point to Interfaces]**

"Interfaces define a contract. `interface Node { id: ID! }` is a famous GraphQL pattern —
the Node interface comes from the Relay spec. Every type that implements Node has an `id`,
which enables a generic node(id: ID!): Node query to fetch any object by ID.

**Question:** What does this remind you of from Java?"

*[Expected: Java interfaces / contracts.]*

"Exactly the same concept. Types implement the interface, must provide all declared fields."

**[Point to Input types]**

"Input types are used for mutation arguments. Important distinction: you CANNOT use a regular
object type (like `Book`) as mutation input. You must define a separate `input` type.
Why? Because regular types can have circular references and resolvers — that doesn't make
sense for input. Input types must be simple data bags."

---

## Segment 6 — Root Types: Query, Mutation, Subscription (10 min)

**[Point to Section 7 — Root Types]**

"Every GraphQL schema has up to three root types. These define your API's entry points.

Query is like GET — read operations. Mutation is like POST/PUT/DELETE — write operations.
Subscription is new — real-time event streams over WebSocket.

Look at the Query type. `books(genre: Genre, sortBy: SortOrder, limit: Int): [Book!]!` —
this defines a query called `books` that accepts optional filtering/sorting arguments
and returns a non-null list of non-null Books.

`book(id: ID!): Book` — note the return type is nullable (no `!`). This is intentional:
if the book with that ID doesn't exist, return null. If we made this `Book!`, the server
would throw an error instead of returning null, which is a worse developer experience.

Mutations return the modified resource. This is intentional — after creating a book, the
client needs the new ID for cache updates. Returning the created/modified resource avoids
a second round trip."

---

## Segment 7 — Now Open the .graphql File (15 min)

**[Switch to: `02-queries-mutations-subscriptions.graphql`]**

"Now let's look at what the CLIENT sends. This file is all query documents — what goes
in the HTTP request body. The schema we just looked at is on the server. These are the
client-side operations."

**[Point to Section 1 — Basic Queries]**

"Section 1a — the simplest query. `query GetAllBookTitles { books { title } }`. Three things
to note: the operation type keyword `query`, the operation name `GetAllBookTitles` (optional
but strongly recommended for debugging), and the selection set in curly braces.

Look at the expected response. The shape MIRRORS the query. `data.books` is an array of objects,
each with only `title`. No id, no price, nothing else. That's the contract."

**[Point to 1c — nested query]**

"Section 1c — nested queries. This is the killer feature. `books { ... author { name email }
reviews { rating comment } }`. One request. Book data, author data, review data. With REST:
GET /books + for each book GET /authors/:id = N+1 requests. With GraphQL: one request.

The response shape is deeply nested, matching the query structure perfectly."

**[Point to Section 3 — Mutations]**

"Mutations look almost identical to queries, except the keyword is `mutation`. The key insight
is that mutations ALSO have a selection set — you specify what you want BACK after the mutation.
Look at `createBook` — after creating, we ask for `id`, `title`, `isbn`, `genre`, `author { name }`.
The server creates the book AND returns those fields immediately. No second request needed."

**Watch out callout:** "Every mutation argument that uses an input type sends the data as a nested
object: `input: { title: ..., isbn: ... }`. Don't confuse this with regular arguments like
`deleteBook(id: '200')` which is a simple scalar argument."

**[Point to Section 4 — Subscriptions]**

"Subscriptions. The syntax looks exactly like a query, but with the `subscription` keyword.
The difference is the transport layer — instead of HTTP request/response, this establishes
a WebSocket. The server sends new data every time the event fires.

`bookAdded` fires whenever a createBook mutation runs. `bookStockChanged(bookId: '42')` — you
can filter subscriptions with arguments. Only events for book 42 arrive at this subscription.

**Question:** When would you use a subscription instead of just polling every 5 seconds?"

*[Expected: real-time critical data (stock prices, live chat, sports scores, collaborative
editing); polling wastes resources on the server and introduces latency.]*

**[Point to Section 5 — Error Handling]**

"Final concept for Part 1: error handling. This surprises REST developers. GraphQL ALWAYS
returns HTTP 200. Errors don't change the HTTP status code. Instead, errors appear in an
`errors` array alongside partial data.

Look at the example response: `data.book` is null, AND there's an `errors` array explaining
why. But — and this is important — if the SAME query also asked for other root fields that
DID succeed, those would still be in `data`. GraphQL returns partial results. This is a
feature: one bad field doesn't tank the whole response."

---

## Segment 8 — Wrap-Up Part 1 (5 min)

"Let's wrap up Part 1.

GraphQL is a query language where clients define the shape of the data they need. It solves
over-fetching (too many fields) and under-fetching (too many round trips). One endpoint replaces
many.

The schema is your API contract — defined in SDL files. Object types have fields. Fields have
scalar types (String, Int, Float, Boolean, ID) or other object types. `!` means non-null.
`[Type!]!` means a non-null list of non-null items.

Three root types: Query (read), Mutation (write — sequential), Subscription (real-time WebSocket).

In Part 2, we add arguments, variables, aliases, fragments — and then we build the actual
Spring Boot server with resolvers.

**Four questions to answer before break:**
1. What two REST problems does GraphQL solve?
2. What does `!` mean in a GraphQL schema?
3. What's the difference between a Query and a Mutation?
4. What transport protocol do Subscriptions typically use?

Take 10 minutes."

---

## Quick Reference

| File | Topics Covered |
|------|----------------|
| `01-graphql-overview-and-schema.md` §1–2 | GraphQL definition, over-fetching, under-fetching, REST vs GraphQL table |
| `01-graphql-overview-and-schema.md` §3 | Architecture: parse → validate → execute → resolvers |
| `01-graphql-overview-and-schema.md` §4 | SDL syntax, where schema files live |
| `01-graphql-overview-and-schema.md` §5 | All 5 scalars (String, Int, Float, Boolean, ID), custom scalars |
| `01-graphql-overview-and-schema.md` §6 | Object types, `!` non-null, `[Type!]!` lists, enums, interfaces (Node), unions, input types |
| `01-graphql-overview-and-schema.md` §7 | Query / Mutation / Subscription root types |
| `02-queries-mutations-subscriptions.graphql` §1 | Basic queries, selection sets, nested queries |
| `02-queries-mutations-subscriptions.graphql` §2 | Shorthand syntax, multiple root fields, inline arguments |
| `02-queries-mutations-subscriptions.graphql` §3 | Create/update/delete mutations, sequential execution |
| `02-queries-mutations-subscriptions.graphql` §4 | Subscriptions (bookAdded, filtered with argument, newReview) |
| `02-queries-mutations-subscriptions.graphql` §5 | Error handling: HTTP 200 always, errors array, partial data |

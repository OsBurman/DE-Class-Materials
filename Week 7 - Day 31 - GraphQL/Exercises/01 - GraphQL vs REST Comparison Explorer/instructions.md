# Exercise 01: GraphQL vs REST Comparison Explorer

## Objective
Understand the fundamental differences between GraphQL and REST by building a structured comparison that highlights GraphQL's over-fetching, under-fetching, and schema benefits.

## Background
Before writing a single line of GraphQL server code, developers need to understand *why* GraphQL exists. REST APIs have been the standard for over a decade, but they suffer from predictable problems: you often get too much data (over-fetching) or too little (under-fetching), and adding new clients requires new endpoints. GraphQL solves these problems with a single flexible endpoint and a strongly-typed schema.

## Requirements

1. Implement `printComparisonTable()` — print a formatted side-by-side comparison table with **at least 6 rows** covering: endpoint model, data fetching, type system, versioning, real-time support, and tooling/introspection.
2. Implement `demonstrateOverFetching()` — print a simulated REST response (hardcoded JSON-like string) for a "get user" request that returns 8 fields, then print the equivalent GraphQL response that returns only the 3 fields the client asked for.
3. Implement `demonstrateUnderFetching()` — print an example showing that fetching a user's posts in REST requires 2 HTTP calls, while GraphQL satisfies the same need in 1 request.
4. Implement `printWhenToUseEach()` — print a short list of 3 scenarios where REST is the better choice and 3 scenarios where GraphQL is the better choice.
5. Call all four methods from `main()` with clear section headers (e.g., `=== COMPARISON TABLE ===`).

## Hints
- Use `System.out.printf` with format strings like `"%-35s %-35s%n"` to create aligned columns.
- The "responses" in tasks 2 and 3 are just printed strings — no real HTTP calls needed.
- For the over-fetching demo, REST returns fields like `id`, `username`, `email`, `bio`, `createdAt`, `lastLogin`, `avatarUrl`, `role`; GraphQL returns only `id`, `username`, `email`.
- Think about what "N+1 HTTP requests" means for under-fetching.

## Expected Output

```
=== GRAPHQL VS REST COMPARISON TABLE ===
Feature                             REST                                GraphQL
-------------------------------------------------------------------------------------------
Endpoint model                      Multiple endpoints (/users, /posts) Single endpoint (/graphql)
Data fetching                       Fixed response shape                Client specifies exact fields
Type system                         No enforced schema                  Strongly-typed SDL schema
Versioning                          /v1, /v2 URL versioning             Schema evolution (no versions)
Real-time support                   Polling or WebSocket add-ons        Built-in Subscriptions
Tooling / Introspection             OpenAPI/Swagger (manual)            Introspection + GraphiQL auto

=== OVER-FETCHING DEMO ===
REST GET /users/42 returns 8 fields:
  { id, username, email, bio, createdAt, lastLogin, avatarUrl, role }

GraphQL query { user(id: "42") { id username email } } returns 3 fields:
  { id, username, email }

Over-fetching eliminated: 5 unnecessary fields not transmitted.

=== UNDER-FETCHING DEMO ===
REST requires 2 HTTP calls:
  Call 1: GET /users/42        → user object
  Call 2: GET /users/42/posts  → posts array

GraphQL satisfies both in 1 request:
  query { user(id: "42") { username posts { title } } }

=== WHEN TO USE REST vs GRAPHQL ===
Prefer REST when:
  1. Public API consumed by many unknown clients
  2. Simple CRUD with well-defined, stable resources
  3. Team is already proficient with REST/OpenAPI tooling

Prefer GraphQL when:
  1. Multiple client types (mobile, web, TV) need different data shapes
  2. Rapid frontend iteration requires frequent field additions
  3. Aggregating data from multiple backend services into one request
```

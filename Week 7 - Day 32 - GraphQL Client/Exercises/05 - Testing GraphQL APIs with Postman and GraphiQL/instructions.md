# Exercise 05: Testing GraphQL APIs with Postman and GraphiQL

## Objective
Practice sending and debugging GraphQL requests using Postman (HTTP collection) and GraphiQL (browser-based IDE), covering queries, mutations, variables, and error cases.

## Background
GraphQL APIs accept HTTP POST requests with a JSON body containing `query`, `variables`, and optionally `operationName`. Both Postman and GraphiQL let you craft and execute these requests interactively. GraphiQL is typically served by the API itself (e.g., at `/graphiql`) and provides schema introspection, auto-complete, and inline documentation — making it ideal for exploration. Postman is better for saving, sharing, and automating request collections.

## Requirements

1. Complete the Postman collection file `bookstore-collection.json` by filling in the `body.raw` field for **all five** requests:
   - **GET_ALL_BOOKS** — a simple query that fetches all books with `id`, `title`, `genre`, `year`, and nested `author { name }`
   - **GET_BOOK_BY_ID** — a query using the variable `$id: ID!` to fetch a single book
   - **ADD_BOOK** — a mutation using variables `$title`, `$genre`, `$year`, `$authorId` to create a book
   - **GET_BOOKS_BY_GENRE** — a query that filters books by `genre` using a `$genre: String!` variable
   - **INTROSPECTION** — a standard introspection query that returns `__schema { types { name } }`

2. For each request body in the collection, set the `variables` field correctly (where applicable). Leave `variables` as `{}` for requests with no variables.

3. Complete the `graphiql-queries.md` file by writing out the full query/mutation text (as you would type into the GraphiQL editor) for each of the same five operations. Include the `Variables` panel content where applicable.

4. In `graphiql-queries.md`, add a **Debugging Tips** section with at least three tips specific to using GraphiQL (e.g., how to use the Docs panel, how to use autocomplete, what to look for in error responses).

## Hints
- All GraphQL requests are `POST` to `/graphql` with `Content-Type: application/json`.
- The Postman body is a JSON string: `{"query": "...", "variables": {...}}` — make sure inner quotes are escaped.
- In GraphiQL's Variables panel, paste valid JSON (not the `$varName:` syntax — that's only in the query signature).
- Introspection queries start with `{ __schema { ... } }` or use the named `query IntrospectionQuery` form.

## Expected Output

**Postman** — when GET_ALL_BOOKS request is sent to the running server:
```json
{
  "data": {
    "books": [
      { "id": "b1", "title": "Clean Code", "genre": "Programming", "year": 2008, "author": { "name": "Robert C. Martin" } },
      { "id": "b2", "title": "Effective Java", "genre": "Programming", "year": 2018, "author": { "name": "Joshua Bloch" } }
    ]
  }
}
```

**GraphiQL** — when running the introspection query:
```json
{
  "data": {
    "__schema": {
      "types": [
        { "name": "Query" },
        { "name": "Book" },
        { "name": "Author" },
        { "name": "Mutation" },
        ...
      ]
    }
  }
}
```

# Exercise 02: Apollo React App — Queries and Response Handling

## Objective
Build a React application that uses Apollo Client to execute a GraphQL query, render the response data, and handle loading and error states.

## Background
Apollo Client is the most popular GraphQL client for React. It wraps your app in an `ApolloProvider`, gives you the `useQuery` hook for declarative data fetching, and handles caching automatically. Instead of writing `fetch` calls manually, you declare *what data you need* and Apollo takes care of the rest.

In this exercise you will wire up Apollo Client to a public GraphQL API and display a list of books with their authors — a nested query.

## Requirements

1. In `client.ts`, create and export an `ApolloClient` instance configured with:
   - `uri` pointing to `http://localhost:4000/graphql` (a mock server)
   - `InMemoryCache` for caching

2. In `main.tsx`, wrap the `<App />` component with `<ApolloProvider client={client}>`.

3. In `BookList.tsx`, define a `GET_BOOKS` GraphQL query using `gql` that:
   - Fetches `books` with fields: `id`, `title`, `genre`, `year`
   - Also fetches the nested `author` object with fields: `id` and `name`

4. Use the `useQuery(GET_BOOKS)` hook and handle all three states:
   - **Loading:** render `<p>Loading books...</p>`
   - **Error:** render `<p>Error loading books: {error.message}</p>`
   - **Success:** render an unordered list (`<ul>`) where each `<li>` shows: `"[title] ([year]) — genre: [genre] — by [author.name]"`

5. In `App.tsx`, import and render `<BookList />`.

6. The `package.json` must include all required dependencies: `@apollo/client`, `graphql`, `react`, `react-dom`.

## Hints
- `InMemoryCache` is imported from `@apollo/client` — the same package as `ApolloClient` and `gql`.
- The `useQuery` hook returns `{ loading, error, data }` — destructure all three.
- Nested GraphQL queries follow the same field selection syntax as top-level queries, just indented inside the parent type's `{}` block.
- `data?.books` will be `undefined` while loading — always guard with optional chaining or a conditional check.

## Expected Output

When the mock server returns books, the page renders:

```
Loading books...        ← shown briefly while fetching

(once data arrives)
• Clean Code (2008) — genre: Programming — by Robert C. Martin
• Effective Java (2018) — genre: Programming — by Joshua Bloch
• The Pragmatic Programmer (1999) — genre: Programming — by Andy Hunt
```

If the network is unavailable:
```
Error loading books: Failed to fetch
```

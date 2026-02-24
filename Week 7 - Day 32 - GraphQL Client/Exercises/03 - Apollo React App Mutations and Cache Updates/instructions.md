# Exercise 03: Apollo React App — Mutations and Cache Updates

## Objective
Use Apollo Client's `useMutation` hook to send GraphQL mutations from a React component and update the local Apollo cache so the UI reflects the change without a full re-fetch.

## Background
Queries read data; mutations change it. Apollo Client tracks a normalized in-memory cache of all query results. After a mutation, you need to tell Apollo how to update that cache — either by calling `refetchQueries` (simple but expensive) or by writing directly to the cache with the `update` callback (efficient). In this exercise you will build an "Add Book" form that uses `useMutation` and updates the cache manually.

## Requirements

1. Reuse the `GET_BOOKS` query from Exercise 02 (copy `client.ts`, `App.tsx`, `main.tsx`, and the `BookList.tsx` query definition into your starter files — they are already provided).

2. In `AddBook.tsx`, define an `ADD_BOOK` mutation using `gql` that:
   - Accepts variables `$title: String!`, `$genre: String!`, `$year: Int!`, `$authorId: ID!`
   - Returns the newly created book's `id`, `title`, `genre`, `year`, and nested `author { id name }`

3. Call `useMutation(ADD_BOOK)` with an `update` callback that:
   - Reads the current `books` array from the Apollo cache using `cache.readQuery({ query: GET_BOOKS })`
   - Writes an updated array back using `cache.writeQuery(...)`, appending the new book returned by the mutation
   - This means the `BookList` will update instantly without a network re-fetch

4. Build a controlled form with four inputs: **Title** (text), **Genre** (text), **Year** (number), **Author ID** (text). Each input must be bound to component state.

5. On form submit, call the `addBook` mutation function with the four variables, then clear all form fields.

6. Show a mutation error if one occurs: `<p style={{ color: "red" }}>Error: {mutationError.message}</p>` (render only when `mutationError` is defined).

7. In `App.tsx`, render both `<BookList />` and `<AddBook />`.

## Hints
- `cache.readQuery` returns `null` if the query has not been executed yet — guard against this with `existingData?.books ?? []`.
- The `update` function receives `(cache, { data })` — the `data` property contains the server response from the mutation.
- Setting state to empty strings (`""`) and `0` after submit clears the form fields when inputs are controlled.
- `useMutation` returns `[mutateFunction, { loading, error }]` — destructure the array, not an object.

## Expected Output

Initial render (books already loaded from Ex 02):
```
• Clean Code (2008) — genre: Programming — by Robert C. Martin
• Effective Java (2018) — genre: Programming — by Joshua Bloch

[Title:  ___________] [Genre: ___________] [Year: ____] [Author ID: ____]  [Add Book]
```

After submitting "Refactoring / Programming / 2018 / a1":
```
• Clean Code (2008) — genre: Programming — by Robert C. Martin
• Effective Java (2018) — genre: Programming — by Joshua Bloch
• Refactoring (2018) — genre: Programming — by Robert C. Martin
```
The new book appears immediately — no page reload needed.

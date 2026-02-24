# GraphiQL Query Sheet — Solution

Use this file as a reference when working in the GraphiQL browser IDE at `http://localhost:4000/graphiql`.

---

## 1. GET_ALL_BOOKS

**Query:**
```graphql
{
  books {
    id
    title
    genre
    year
    author {
      name
    }
  }
}
```

**Variables:**
```json
{}
```

**Expected response excerpt:**
```json
{ "data": { "books": [ { "id": "b1", "title": "Clean Code", "genre": "Programming", "year": 2008, "author": { "name": "Robert C. Martin" } } ] } }
```

---

## 2. GET_BOOK_BY_ID

**Query:**
```graphql
query GetBook($id: ID!) {
  book(id: $id) {
    id
    title
    genre
    year
    author {
      name
    }
  }
}
```

**Variables:**
```json
{
  "id": "b1"
}
```

**Expected response:**
```json
{ "data": { "book": { "id": "b1", "title": "Clean Code", "genre": "Programming", "year": 2008, "author": { "name": "Robert C. Martin" } } } }
```

---

## 3. ADD_BOOK (Mutation)

**Query:**
```graphql
mutation AddBook($title: String!, $genre: String!, $year: Int!, $authorId: ID!) {
  addBook(title: $title, genre: $genre, year: $year, authorId: $authorId) {
    id
    title
    genre
    year
    author {
      name
    }
  }
}
```

**Variables:**
```json
{
  "title": "Refactoring",
  "genre": "Programming",
  "year": 2018,
  "authorId": "a1"
}
```

**Expected response:**
```json
{ "data": { "addBook": { "id": "b4", "title": "Refactoring", "genre": "Programming", "year": 2018, "author": { "name": "Robert C. Martin" } } } }
```

---

## 4. GET_BOOKS_BY_GENRE

**Query:**
```graphql
query BooksByGenre($genre: String!) {
  booksByGenre(genre: $genre) {
    id
    title
    author {
      name
    }
  }
}
```

**Variables:**
```json
{
  "genre": "Programming"
}
```

**Expected response:**
```json
{ "data": { "booksByGenre": [ { "id": "b1", "title": "Clean Code", "author": { "name": "Robert C. Martin" } }, { "id": "b2", "title": "Effective Java", "author": { "name": "Joshua Bloch" } } ] } }
```

---

## 5. INTROSPECTION

**Query:**
```graphql
{
  __schema {
    types {
      name
    }
  }
}
```

**Variables:**
```json
{}
```

**Expected response (excerpt):**
```json
{ "data": { "__schema": { "types": [ { "name": "Query" }, { "name": "Book" }, { "name": "Author" }, { "name": "Mutation" }, { "name": "String" }, { "name": "Boolean" } ] } } }
```

---

## Debugging Tips

1. **Docs panel (top-right "Docs" button):** Click it to open the schema documentation. You can browse every type, query, mutation, and field — with their argument names and return types — without reading the schema file. Start here when you don't know what fields are available.

2. **Autocomplete (Ctrl+Space or Cmd+Space):** Inside a selection set, press the shortcut to see all available fields for the current type. Works for argument names too. This prevents typos and unknown-field errors before you even send the request.

3. **Read the `errors` array:** A GraphQL response always returns HTTP 200 — even for errors. Check the `"errors"` key in the response JSON. Each error object has a `"message"` and a `"locations"` field pointing to the line/column in your query that caused the problem. A missing required argument, a wrong type, or an unknown field will show up here rather than as an HTTP 4xx/5xx.

4. **Query history (clock icon):** GraphiQL stores your previous queries. Click the history icon to re-run or compare earlier versions of a query — useful when debugging subtle changes.

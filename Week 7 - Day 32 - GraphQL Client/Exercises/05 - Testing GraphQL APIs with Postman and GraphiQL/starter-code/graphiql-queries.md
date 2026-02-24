# GraphiQL Query Sheet

Use this file as a reference when working in the GraphiQL browser IDE at `http://localhost:4000/graphiql`.
Paste each query into the editor panel and (where shown) paste the Variables JSON into the Variables panel.

---

## 1. GET_ALL_BOOKS

**Query:**
```graphql
# TODO: Write a query that fetches all books.
# Select: id, title, genre, year, and nested author { name }
```

**Variables:**
```json
{}
```

---

## 2. GET_BOOK_BY_ID

**Query:**
```graphql
# TODO: Write a query that accepts variable $id: ID! and fetches a single book by that id.
# Select: id, title, genre, year, author { name }
```

**Variables:**
```json
{
  "id": "b1"
}
```

---

## 3. ADD_BOOK (Mutation)

**Query:**
```graphql
# TODO: Write a mutation that accepts $title: String!, $genre: String!, $year: Int!, $authorId: ID!
# Call addBook with those variables and return id, title, genre, year, author { name }
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

---

## 4. GET_BOOKS_BY_GENRE

**Query:**
```graphql
# TODO: Write a query that accepts $genre: String! and calls booksByGenre.
# Return id, title, and author { name }
```

**Variables:**
```json
{
  "genre": "Programming"
}
```

---

## 5. INTROSPECTION

**Query:**
```graphql
# TODO: Write the standard introspection query that returns __schema { types { name } }
# Hint: all introspection fields start with two underscores: __schema, __type, etc.
```

**Variables:**
```json
{}
```

---

## Debugging Tips

<!-- TODO: Add at least 3 tips specific to GraphiQL -->
<!-- Tip 1: How to use the Docs panel -->
<!-- Tip 2: How to use autocomplete (Ctrl+Space) -->
<!-- Tip 3: What to look for in the "errors" array of a response -->
<!-- Tip 4 (optional): How to use query history -->

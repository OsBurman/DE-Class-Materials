# Testing GraphQL APIs — Postman, Playground & GraphiQL

## Overview

Before wiring up a full React or Angular client, you need a way to:
1. Explore the API's schema (what types and fields exist?)
2. Test operations interactively without writing UI code
3. Debug queries — find out why a field is null or an error is thrown
4. Share query examples with teammates

Three tools cover this: **Postman**, **GraphQL Playground**, and **GraphiQL**.

---

## TOOL 1: Postman

Postman has first-class GraphQL support. It can introspect your schema, provide autocompletion, and send operations just like a client would.

### Setup

1. Open Postman → New Request → **POST**
2. Set URL: `http://localhost:8080/graphql`
3. Go to the **Body** tab → select **GraphQL**
4. Postman will show a Query editor and a Variables editor

### Sending a Basic Query

**Query panel:**
```graphql
query GetAllBooks {
  books {
    id
    title
    genre
    publishedYear
    author {
      id
      name
    }
  }
}
```

**Expected response:**
```json
{
  "data": {
    "books": [
      {
        "id": "1",
        "title": "Clean Code",
        "genre": "Technology",
        "publishedYear": 2008,
        "author": {
          "id": "10",
          "name": "Robert C. Martin"
        }
      }
    ]
  }
}
```

> ⚠️ **Important:** GraphQL always returns HTTP 200, even for errors. Check the response body for an `"errors"` array — not the HTTP status code.

### Sending a Query with Variables

**Query panel:**
```graphql
query GetBook($id: ID!) {
  book(id: $id) {
    id
    title
    genre
    author {
      name
      bio
    }
  }
}
```

**Variables panel (JSON):**
```json
{
  "id": "1"
}
```

Variables are always sent as a separate JSON object — never hardcoded into the query string. This mirrors exactly how Apollo Client sends them from your frontend code.

### Sending a Mutation

```graphql
mutation CreateBook($input: BookInput!) {
  createBook(input: $input) {
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
  "input": {
    "title": "The Pragmatic Programmer",
    "authorId": "10",
    "genre": "Technology",
    "publishedYear": 2019
  }
}
```

### Schema Introspection in Postman

1. Open a GraphQL request
2. Click **Schema** tab → **Fetch Schema**
3. Postman queries the introspection endpoint and loads all your types and fields
4. You get **autocomplete** as you type in the query editor

> 💡 **Tip:** Save all your test operations as a Postman Collection. Share the collection with your team — it serves as living API documentation.

### Debugging with Postman

**Scenario:** A field returns `null` unexpectedly.

1. Run the query in Postman
2. Check the `"errors"` array in the response:
```json
{
  "data": {
    "book": {
      "id": "1",
      "title": "Clean Code",
      "author": null     ← null because the resolver threw an error
    }
  },
  "errors": [
    {
      "message": "Author not found for book id: 1",
      "locations": [{ "line": 5, "column": 5 }],
      "path": ["book", "author"],
      "extensions": {
        "code": "NOT_FOUND",
        "classification": "DataFetchingException"
      }
    }
  ]
}
```

3. The `path` tells you exactly which field failed: `book.author`
4. The `locations` tell you the line number in YOUR query
5. The `extensions.code` is the error code from the server (set in your Spring resolver)

---

## TOOL 2: GraphQL Playground

GraphQL Playground is a standalone browser-based IDE that was the original interactive explorer for GraphQL APIs. It's now largely superseded by GraphiQL (with plugins) but you'll still encounter it on older servers.

**Enable in Spring for GraphQL:** (`application.properties`)
```properties
spring.graphql.graphiql.enabled=true
```

> Note: Spring Boot ships GraphiQL, not Playground. For Playground specifically, you'd need a standalone Playground app (available as an Electron app or Docker container).

### Features of GraphQL Playground

- **Schema tab (on the right):** Auto-generated documentation from your SDL. Browse every type, field, argument, and description. No manual documentation needed.
- **History tab:** Recent queries are saved automatically
- **Multiple tabs:** Run multiple operations side by side
- **Keyboard shortcuts:** `Ctrl+Space` for autocomplete, `Ctrl+Enter` to execute

### Writing Queries in Playground

```graphql
# Use comments to annotate your queries
# This is useful when building a query step by step

query BookWithNestedAuthor {
  book(id: "1") {
    id
    title
    # Field selection: only request what you need
    # Omit 'bio' if the UI doesn't show it
    author {
      name
      # bio   ← commented out — not requested, not sent over wire
    }
  }
}
```

### Debugging in Playground

**Step 1: Start with the simplest possible query**
```graphql
{ books { id } }        # shorthand query syntax — no operation name needed
```

**Step 2: Add fields one at a time**
```graphql
{ books { id title } }
{ books { id title author { id } } }
{ books { id title author { id name } } }
```

This isolates which field causes an error or unexpected null.

**Step 3: Use introspection queries to explore the schema**
```graphql
# List all types in the schema
{
  __schema {
    types {
      name
      kind
    }
  }
}

# Inspect a specific type
{
  __type(name: "Book") {
    fields {
      name
      type {
        name
        kind
      }
    }
  }
}
```

---

## TOOL 3: GraphiQL

GraphiQL (with an 'i') is the official, maintained GraphQL IDE. Spring for GraphQL ships with GraphiQL built in.

**Access it at:** `http://localhost:8080/graphiql` (requires `spring.graphql.graphiql.enabled=true`)

### Features

- **Explorer panel (left sidebar):** Click-to-build queries. Check boxes to add fields — GraphiQL writes the query for you. Perfect for learning a new schema.
- **Documentation explorer:** Browse the full schema as rendered docs
- **Query history:** Recent operations are saved
- **Keyboard shortcuts:** `Ctrl+Space` autocomplete, `Ctrl+Enter` execute
- **Variable editor:** Bottom panel for JSON variables
- **Response panel:** Color-coded response with error highlighting

### Writing and Debugging Queries in GraphiQL

**Field selection — only request what you need:**
```graphql
# BAD — requesting everything (like SELECT *)
query BadQuery {
  books {
    id
    title
    genre
    publishedYear
    author {
      id
      name
      bio           # ← bio loads from a separate DB call
      booksWritten  # ← triggers another resolver — only request if displayed
    }
  }
}

# GOOD — field selection matches what the UI actually shows
query GoodQuery {
  books {
    id
    title
    author {
      name    # ← only the display name, nothing else
    }
  }
}
```

**Nested queries — traversing relationships:**
```graphql
# Traversing two levels deep
query DeepQuery {
  author(id: "10") {
    name
    books {          # Author → Books relationship
      title
      genre
    }
  }
}

# Traversing three levels deep (be careful — each level may trigger resolvers)
query VeryDeepQuery {
  books {
    title
    author {
      name
      books {        # Author → Books → Author's other books
        title
      }
    }
  }
}
```

> ⚠️ **Watch out:** Deep nested queries can trigger many resolver calls. Three levels may look innocent but can cause dozens of database queries. Use the N+1 detection tools and DataLoader (covered in the next file).

### Using GraphiQL to Debug a Real Problem

**Scenario:** `createBook` mutation returns a book but the `author` field is null.

**Step 1:** Run the mutation in GraphiQL with minimal fields:
```graphql
mutation Test {
  createBook(input: { title: "Test", authorId: "999", genre: "Tech", publishedYear: 2024 }) {
    id
    title
  }
}
```
→ If `id` and `title` work, the mutation itself is fine.

**Step 2:** Add the `author` field:
```graphql
mutation Test {
  createBook(input: { title: "Test", authorId: "999", genre: "Tech", publishedYear: 2024 }) {
    id
    title
    author {
      id
      name
    }
  }
}
```
→ If `author` is null, check the `errors` array. Likely the `authorId: "999"` doesn't exist.

**Step 3:** Verify the author exists:
```graphql
query CheckAuthor {
  author(id: "999") {
    id
    name
  }
}
```
→ If null, the author doesn't exist. Use a real author ID.

---

## Comparison: When to Use Each Tool

| Use Case | Postman | GraphQL Playground | GraphiQL |
|---|---|---|---|
| API already has a URL | ✅ | ✅ | ✅ |
| Schema exploration | Good (fetch schema) | Excellent (right sidebar) | Excellent (doc explorer) |
| Building queries visually | ❌ | ❌ | ✅ (Explorer panel) |
| Saving & organizing tests | ✅ (Collections) | Limited (history) | Limited (history) |
| Sharing with non-devs | ✅ | ❌ | ❌ |
| CI/CD integration | ✅ (Newman CLI) | ❌ | ❌ |
| Authorization headers | ✅ | ✅ (HTTP Headers tab) | ✅ (custom headers) |
| Embedded in Spring Boot | ❌ | ❌ | ✅ (built-in) |

**Rule of thumb:**
- Use **GraphiQL** during development — it's built into Spring Boot and has the best query building experience
- Use **Postman** for team API testing, sharing test suites, and integration test documentation
- Know **Playground** because you'll encounter it on other people's servers

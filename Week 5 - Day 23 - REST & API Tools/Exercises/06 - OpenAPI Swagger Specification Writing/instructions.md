# Exercise 06 — OpenAPI/Swagger Specification Writing

## Learning Objectives
By the end of this exercise you will be able to:
- Understand the structure of an OpenAPI 3.0 specification document
- Write `paths`, `operations`, `parameters`, and `requestBody` sections
- Define reusable schemas in `components/schemas` and reference them with `$ref`
- Document success and error responses for each operation
- Validate and visualise a spec in Swagger Editor

## Prerequisites
- Familiarity with REST resource design (Exercise 03)
- YAML syntax basics (indentation, mappings, sequences)

---

## Background

An **OpenAPI Specification (OAS)** is a machine-readable, language-agnostic contract that describes a REST API. It enables:
- Auto-generated interactive documentation (Swagger UI)
- Client SDK generation (OpenAPI Generator)
- Contract-first development — teams agree on the spec before writing code
- Automated validation of requests and responses

**Key OpenAPI 3.0 document structure:**

```yaml
openapi: 3.0.3
info:          # API title, version, description
paths:         # each URL path → HTTP methods → operations
components:
  schemas:     # reusable data models ($ref targets)
```

---

## Setup

1. Open **Swagger Editor** at https://editor.swagger.io/
2. Clear the default spec and paste in the contents of `starter-code/openapi.yaml`.
3. Edit the YAML on the left; the rendered documentation updates live on the right.

---

## Tasks

### Part 1 — Fill in the `info` Section

Complete the `info` block with:
- `title`: Library Management API
- `version`: 1.0.0
- `description`: A REST API for managing books, authors, and loans

---

### Part 2 — Define the `Book` Schema

In `components/schemas`, fill in the `Book` schema with these properties:
| Property | Type | Required? | Notes |
|---|---|---|---|
| `id` | integer | No (read-only) | Assigned by the server |
| `title` | string | Yes | |
| `isbn` | string | Yes | |
| `publishedYear` | integer | No | |
| `authorId` | integer | Yes | FK to author |

---

### Part 3 — Define the `BookInput` Schema

Create a second schema called `BookInput` (used for POST/PUT bodies — no `id` field):
- Same as `Book` but without the `id` property
- `title`, `isbn`, and `authorId` are required

---

### Part 4 — Document `GET /books`

Under `paths`, document the `GET /books` operation:
- `operationId`: `listBooks`
- `summary`: List all books
- `parameters`: optional query param `author` (string) for filtering
- `responses`:
  - `200`: array of `$ref: '#/components/schemas/Book'`
  - `500`: Internal server error

---

### Part 5 — Document `POST /books`

Under `paths`, document the `POST /books` operation:
- `operationId`: `createBook`
- `summary`: Create a new book
- `requestBody`: required, `application/json`, schema `$ref: '#/components/schemas/BookInput'`
- `responses`:
  - `201`: the created `$ref: '#/components/schemas/Book'`
  - `400`: Bad request (validation error)

---

### Part 6 — Document `GET /books/{bookId}`

- `operationId`: `getBookById`
- `parameters`: path param `bookId` (integer, required)
- `responses`:
  - `200`: `$ref: '#/components/schemas/Book'`
  - `404`: Book not found

---

### Part 7 — Document `PUT /books/{bookId}`

- `operationId`: `updateBook`
- `parameters`: path param `bookId` (integer, required)
- `requestBody`: required, `BookInput` schema
- `responses`:
  - `200`: updated `Book`
  - `400`: validation error
  - `404`: not found

---

### Part 8 — Document `DELETE /books/{bookId}`

- `operationId`: `deleteBook`
- `parameters`: path param `bookId` (integer, required)
- `responses`:
  - `204`: No content (deleted successfully)
  - `404`: not found

---

## Reflection Questions

1. What is the difference between a `path` parameter and a `query` parameter? When should you use each?
2. Why are `BookInput` and `Book` defined as separate schemas rather than one schema?
3. What is the benefit of using `$ref` to reference schemas instead of inline definitions?

---

## Deliverable
A complete `openapi.yaml` validated in Swagger Editor with no errors and a fully rendered UI for all 5 operations. The reference solution is in `solution/openapi.yaml`.

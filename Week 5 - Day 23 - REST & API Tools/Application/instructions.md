# Day 23 Application — REST & API Tools: API Design Challenge

## Overview

You'll document and test a **Bookstore REST API** using Postman (testing an existing mock API) and design your own API specification using the OpenAPI/Swagger format.

---

## Learning Goals

- Understand REST principles (resources, verbs, statelessness)
- Use all HTTP methods: GET, POST, PUT, PATCH, DELETE
- Interpret HTTP status codes
- Use Postman to send requests and write test scripts
- Write an OpenAPI 3.0 specification in YAML

---

## Prerequisites

- Postman installed (`https://www.postman.com/downloads/`)
- Mock API: `https://jsonplaceholder.typicode.com` (free, no auth)
- Your OpenAPI spec goes in `openapi-spec.yaml`

---

## Part 1 — Exploring REST with Postman (Tasks 1–6)

Use `https://jsonplaceholder.typicode.com` as your mock backend.

**Task 1 — GET**  
`GET /posts` — fetch all posts. In Postman Tests tab, write:
```js
pm.test("Status is 200", () => pm.response.to.have.status(200));
pm.test("Response is array", () => pm.expect(pm.response.json()).to.be.an('array'));
```

**Task 2 — GET with path param**  
`GET /posts/1` — fetch a single post. Assert the id equals 1.

**Task 3 — POST**  
`POST /posts` with JSON body `{ "title": "My Post", "body": "Content...", "userId": 1 }`. Assert status 201.

**Task 4 — PUT**  
`PUT /posts/1` — replace the entire resource. Assert status 200. Observe the difference from PATCH.

**Task 5 — PATCH**  
`PATCH /posts/1` — update only the title. Assert status 200.

**Task 6 — DELETE**  
`DELETE /posts/1`. Assert status 200 or 204.

---

## Part 2 — Status Code Mapping (Task 7)

In `status-codes.md`, document what status code SHOULD be returned for:

| Scenario | Status Code |
|---|---|
| Successfully retrieved resource | |
| Successfully created resource | |
| Successfully deleted (no body) | |
| Validation error (bad input) | |
| Resource not found | |
| Unauthorized (no token) | |
| Forbidden (no permission) | |
| Server crash | |

---

## Part 3 — OpenAPI Spec (Tasks 8–10)

Write an OpenAPI 3.0 spec for a **Bookstore API** in `openapi-spec.yaml`.

**Task 8 — Schema**  
Define a `Book` schema component:
```yaml
Book:
  type: object
  required: [title, author, isbn]
  properties:
    id:     { type: integer }
    title:  { type: string }
    author: { type: string }
    isbn:   { type: string, pattern: '^[0-9]{13}$' }
    price:  { type: number, minimum: 0 }
```

**Task 9 — Paths**  
Document these endpoints:
- `GET /books` — list all books (200 response with array of Book)
- `POST /books` — create book (201, request body required)
- `GET /books/{id}` — get one book (200 or 404)
- `PUT /books/{id}` — update book (200 or 404)
- `DELETE /books/{id}` — delete book (204 or 404)

**Task 10 — Validation in spec**  
Add a query parameter `?search=` to `GET /books` and document it.  
Add `securitySchemes` with a Bearer token scheme and apply it to POST, PUT, DELETE.

---

## Stretch Goals

1. Import your OpenAPI spec into Swagger Editor (`editor.swagger.io`) and verify it renders correctly.
2. Use Postman Environments to store the base URL as a variable.
3. Add a Postman Collection Runner test sequence.

---

## Submission Checklist

- [ ] 6 Postman requests created with test assertions
- [ ] All test assertions pass (green)
- [ ] Status code table completed in `status-codes.md`
- [ ] `openapi-spec.yaml` valid with 5 endpoints documented
- [ ] Book schema with required fields and validation
- [ ] Security scheme documented

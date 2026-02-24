# Exercise 01 — Worksheet: HTTP Methods and Request/Response Lifecycle

---

## Part 1A — Anatomy of an HTTP Request

Label each component of the request below:

```
POST /api/v1/books HTTP/1.1
Host: api.library.com
Content-Type: application/json
Authorization: Bearer eyJhbGci...

{
  "title": "1984",
  "author": "George Orwell"
}
```

| Component | Value from above | Your description |
|---|---|---|
| HTTP Method | POST | TODO: Describe what POST does |
| Request Target | /api/v1/books | TODO: What does this path represent? |
| HTTP Version | HTTP/1.1 | TODO: What does this specify? |
| Host header | api.library.com | TODO: Why is this header required? |
| Content-Type header | application/json | TODO: What does this tell the server? |
| Authorization header | Bearer eyJhbGci... | TODO: What type of auth scheme is "Bearer"? |
| Request Body | { "title": ... } | TODO: When is a request body present? |

---

## Part 1B — Anatomy of an HTTP Response

Label each component of the response below:

```
HTTP/1.1 201 Created
Content-Type: application/json
Location: /api/v1/books/42

{
  "id": 42,
  "title": "1984",
  "author": "George Orwell"
}
```

| Component | Value from above | Your description |
|---|---|---|
| HTTP Version | HTTP/1.1 | TODO: |
| Status Code | 201 | TODO: What does 201 mean? Which family (2xx)? |
| Reason Phrase | Created | TODO: What is the reason phrase's purpose? |
| Content-Type header | application/json | TODO: What does this tell the client? |
| Location header | /api/v1/books/42 | TODO: When is Location used? |
| Response Body | { "id": 42, ... } | TODO: |

---

## Part 2 — HTTP Method Semantics

Complete the table for a hypothetical `books` REST API:

| Method | Purpose | Idempotent? | Body? | Example URL | Example Body (if any) |
|---|---|---|---|---|---|
| GET | TODO | TODO | TODO | TODO | N/A |
| POST | TODO | TODO | TODO | TODO | TODO |
| PUT | TODO | TODO | TODO | TODO | TODO |
| PATCH | TODO | TODO | TODO | TODO | TODO |
| DELETE | TODO | TODO | TODO | TODO | N/A |

---

## Part 3 — Request/Response Lifecycle

For a request `GET /api/v1/books/1`, describe what happens at each step:

1. **Client initiates TCP connection** — TODO: describe the TCP handshake and TLS (for HTTPS)
2. **Client sends HTTP request** — TODO: describe the formatted request being transmitted
3. **Server receives and parses request** — TODO: describe how the server reads method, path, headers, body
4. **Server processes request** — TODO: describe business logic and database lookup
5. **Server sends HTTP response** — TODO: describe the formatted response being transmitted
6. **Client receives and processes response** — TODO: describe how the client uses the status code and body

---

## Part 4 — Curl Commands

See `curl-examples.sh` for the command stubs.

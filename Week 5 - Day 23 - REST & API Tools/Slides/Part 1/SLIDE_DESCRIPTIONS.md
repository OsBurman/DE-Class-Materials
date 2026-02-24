# Day 23 Part 1 — HTTP Protocol, REST Principles & API Design
## Slide Descriptions

**Total slides: 16**

---

### Running Example

Throughout Part 1 we design a `Book Store API` — a simple REST API for managing books, authors, and orders. All examples reference this fictional API so students see how every concept applies to a single coherent system.

---

### Slide 1 — Title Slide

**Title:** REST & API Tools
**Subtitle:** HTTP Protocol · REST Principles · Resource Design · API Versioning
**Day:** Week 5 — Day 23 | Part 1 of 2

**Objectives listed on slide:**
- Explain how HTTP works at the protocol level
- Use and distinguish GET, POST, PUT, PATCH, DELETE correctly
- Read and construct HTTP requests and responses
- Use the right status code for every situation
- Apply HTTP headers for content negotiation and authentication
- Design clean, RESTful APIs following six core constraints
- Name resources and structure URLs correctly
- Choose and implement an API versioning strategy

---

### Slide 2 — HTTP Protocol Overview

**Title:** HTTP — The Language of the Web

**Concept:** HTTP (HyperText Transfer Protocol) is a stateless, request-response protocol that runs over TCP/IP. It is the foundation of every REST API.

**Key characteristics:**

| Property | Meaning |
|---|---|
| **Stateless** | Every request is independent — the server holds no memory of previous requests |
| **Request-Response** | Client always initiates; server always responds |
| **Text-based** | Messages are human-readable text (headers, status lines) |
| **Layered** | Runs over TCP (port 80 for HTTP, port 443 for HTTPS) |

**HTTP versions:**

| Version | Key Feature |
|---|---|
| HTTP/1.1 (1997) | Keep-alive connections, chunked transfer — still widely used |
| HTTP/2 (2015) | Multiplexing, header compression, server push — faster |
| HTTP/3 (2022) | Runs over QUIC (UDP-based) — better on lossy networks |

**Anatomy of an HTTP exchange:**
```
CLIENT                                    SERVER
  │                                          │
  │──── HTTP Request ──────────────────────>│
  │     (method + URL + headers + body)      │
  │                                          │
  │<──── HTTP Response ─────────────────────│
  │     (status code + headers + body)       │
```

**Why stateless matters:** The server doesn't remember request #1 when request #2 arrives. Every request must carry all information needed to process it — auth tokens, parameters, body. This is what makes REST APIs horizontally scalable: any server instance can handle any request.

---

### Slide 3 — HTTP Request Anatomy

**Title:** HTTP Request — What You Send

**A complete HTTP request has four parts:**

```
1. Request Line
2. Headers
3. Blank Line (separates headers from body)
4. Body (optional)
```

**Full raw request example — GET:**
```http
GET /api/v1/books?genre=fiction&limit=10 HTTP/1.1
Host: api.bookstore.com
Accept: application/json
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
User-Agent: PostmanRuntime/7.36
```

**Full raw request example — POST:**
```http
POST /api/v1/books HTTP/1.1
Host: api.bookstore.com
Content-Type: application/json
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
Content-Length: 87

{
  "title": "Clean Code",
  "authorId": 42,
  "price": 39.99,
  "genre": "technology"
}
```

**Parts explained:**

| Part | Example | Purpose |
|---|---|---|
| Method | `GET` | What action to perform |
| Path | `/api/v1/books` | Which resource |
| Query string | `?genre=fiction&limit=10` | Filters, pagination, options |
| Headers | `Content-Type: application/json` | Metadata about the request |
| Body | `{ "title": "..." }` | Data being sent (POST, PUT, PATCH) |

---

### Slide 4 — HTTP Response Anatomy

**Title:** HTTP Response — What You Receive

**A complete HTTP response:**

```
1. Status Line
2. Headers
3. Blank Line
4. Body
```

**Full raw response example — 201 Created:**
```http
HTTP/1.1 201 Created
Content-Type: application/json
Location: /api/v1/books/1337
Date: Mon, 23 Feb 2026 14:30:00 GMT
Content-Length: 156

{
  "id": 1337,
  "title": "Clean Code",
  "authorId": 42,
  "price": 39.99,
  "genre": "technology",
  "createdAt": "2026-02-23T14:30:00Z"
}
```

**Full raw response example — 404 Not Found:**
```http
HTTP/1.1 404 Not Found
Content-Type: application/json
Date: Mon, 23 Feb 2026 14:30:05 GMT

{
  "error": "NOT_FOUND",
  "message": "Book with ID 9999 not found",
  "path": "/api/v1/books/9999",
  "timestamp": "2026-02-23T14:30:05Z"
}
```

**Key response parts:**

| Part | Example | Purpose |
|---|---|---|
| Status line | `HTTP/1.1 201 Created` | Outcome of the request |
| `Content-Type` | `application/json` | Format of the body |
| `Location` | `/api/v1/books/1337` | URL of newly created resource |
| Body | `{ "id": 1337, ... }` | The response data or error detail |

---

### Slide 5 — HTTP Methods

**Title:** HTTP Methods — What Action You're Performing

**The five core REST methods:**

| Method | Action | Has Body | Idempotent | Safe |
|---|---|---|---|---|
| `GET` | Read / retrieve | No | ✅ Yes | ✅ Yes |
| `POST` | Create / trigger | Yes | ❌ No | ❌ No |
| `PUT` | Full replace | Yes | ✅ Yes | ❌ No |
| `PATCH` | Partial update | Yes | Usually | ❌ No |
| `DELETE` | Delete | Optional | ✅ Yes | ❌ No |

**Definitions:**
- **Safe:** Does not modify server state — only reads
- **Idempotent:** Calling it multiple times produces the same result as calling it once

**Book Store API examples:**

```http
GET    /api/v1/books           → list all books
GET    /api/v1/books/42        → get book with ID 42
POST   /api/v1/books           → create a new book
PUT    /api/v1/books/42        → replace book 42 completely (full body required)
PATCH  /api/v1/books/42        → update only provided fields (e.g., just the price)
DELETE /api/v1/books/42        → delete book 42
```

**PUT vs PATCH — the critical distinction:**
```json
// Current book: { "title": "Clean Code", "price": 39.99, "genre": "technology" }

// PUT /books/42 — replaces EVERYTHING (must send full object)
{ "title": "Clean Code", "price": 44.99, "genre": "technology" }
// omitting genre → genre becomes null/missing

// PATCH /books/42 — only updates what you send
{ "price": 44.99 }
// title and genre unchanged ✅
```

**HEAD and OPTIONS (bonus):**
- `HEAD` — same as GET but returns headers only, no body (check if resource exists)
- `OPTIONS` — returns which methods are allowed on a resource (used in CORS preflight)

---

### Slide 6 — HTTP Status Codes

**Title:** HTTP Status Codes — The Server's Response Signal

**Five status code categories:**

| Range | Category | Meaning |
|---|---|---|
| **2xx** | Success | Request was received, understood, and accepted |
| **3xx** | Redirection | Client must take additional action |
| **4xx** | Client Error | The request was wrong — client's fault |
| **5xx** | Server Error | The server failed — server's fault |

**Most important codes to know:**

| Code | Name | When to use |
|---|---|---|
| `200 OK` | Success | GET, PUT, PATCH, DELETE succeeded |
| `201 Created` | Resource created | POST succeeded — new resource was created |
| `204 No Content` | Success, no body | DELETE succeeded or PUT with no response body |
| `400 Bad Request` | Invalid request | Malformed JSON, missing required field, validation failure |
| `401 Unauthorized` | Not authenticated | No valid auth token provided |
| `403 Forbidden` | Not authorized | Authenticated but not allowed |
| `404 Not Found` | Resource doesn't exist | Wrong ID, wrong URL |
| `405 Method Not Allowed` | Wrong HTTP method | POST to a read-only endpoint |
| `409 Conflict` | State conflict | Duplicate entry, version mismatch |
| `422 Unprocessable Entity` | Semantic validation failure | Valid JSON but business rule violated |
| `429 Too Many Requests` | Rate limited | Client is sending too many requests |
| `500 Internal Server Error` | Unhandled server exception | Bug in the server |
| `503 Service Unavailable` | Server temporarily down | Maintenance, overload |

**301 vs 302 redirects:**
- `301 Moved Permanently` — bookmark the new URL
- `302 Found` — temporary redirect, keep using the original URL

---

### Slide 7 — HTTP Headers

**Title:** HTTP Headers — The Metadata Layer

**Headers are key-value pairs that carry metadata about a request or response.**

**Request headers:**

| Header | Example | Purpose |
|---|---|---|
| `Content-Type` | `application/json` | Format of the request body |
| `Accept` | `application/json` | Format the client can handle in the response |
| `Authorization` | `Bearer <token>` | Authentication credentials |
| `User-Agent` | `PostmanRuntime/7.36` | Client software identification |
| `Accept-Language` | `en-US,en;q=0.9` | Preferred language |
| `Cache-Control` | `no-cache` | Caching directive |

**Response headers:**

| Header | Example | Purpose |
|---|---|---|
| `Content-Type` | `application/json; charset=utf-8` | Format of the response body |
| `Location` | `/api/v1/books/1337` | URL of newly created resource |
| `Cache-Control` | `max-age=3600` | How long client can cache response |
| `ETag` | `"abc123"` | Version fingerprint for cache validation |
| `X-Request-Id` | `req_8f4a2c` | Unique ID for tracing this request |
| `Retry-After` | `30` | Seconds to wait before retrying (429) |

**Content negotiation in practice:**
```http
GET /api/v1/books/42 HTTP/1.1
Accept: application/json       ← client wants JSON

HTTP/1.1 200 OK
Content-Type: application/json ← server confirms JSON
```

**Custom headers:** Prefix with `X-` (conventional, though deprecated in RFC 6648). Common examples: `X-Request-Id`, `X-Correlation-Id`, `X-API-Key`. You'll implement `X-Request-Id` in Spring MVC (Day 26) for distributed request tracing.

---

### Slide 8 — The Six REST Constraints

**Title:** REST — Representational State Transfer

**REST is an architectural style, not a protocol.** An API that follows REST constraints is called a "RESTful API." The term was defined by Roy Fielding in his 2000 doctoral dissertation.

**The six constraints:**

| Constraint | What It Means |
|---|---|
| **1. Client-Server** | UI and data storage are separated. Client doesn't know how data is stored; server doesn't know how it's rendered. |
| **2. Stateless** | Each request contains all necessary information. No session state stored on the server. |
| **3. Cacheable** | Responses must declare themselves as cacheable or non-cacheable. Enables CDNs and browser caching. |
| **4. Uniform Interface** | Resources are identified by URIs. Interaction is through standard HTTP methods and media types. |
| **5. Layered System** | Client doesn't know if it's talking to the actual server, a load balancer, or a cache. Each layer only sees the adjacent layer. |
| **6. Code on Demand** (optional) | Servers can send executable code to clients (JavaScript in browsers). Rarely used in REST APIs. |

**Most important for daily work:** Stateless + Uniform Interface. These two constraints define most of what "RESTful" means in practice.

**REST vs "REST-ish":** Many APIs call themselves REST but violate constraints — e.g., storing session state, using GET for mutations, returning 200 for errors. Understanding the constraints lets you recognize and critique these issues.

---

### Slide 9 — RESTful Resource Naming

**Title:** Resource Naming — Designing Clean URLs

**Core principle:** URLs identify resources (nouns), not actions (verbs). HTTP methods are the verbs.

**Naming rules:**

| Rule | Bad ❌ | Good ✅ |
|---|---|---|
| Use nouns, not verbs | `/getBooks`, `/createBook` | `/books` |
| Use plural nouns | `/book/42` | `/books/42` |
| Use lowercase with hyphens | `/BookAuthors`, `/book_authors` | `/book-authors` |
| Nest for relationships | `/books?authorId=5` (ok) | `/authors/5/books` (better) |
| No file extensions | `/books/42.json` | `/books/42` + `Accept: application/json` |
| No trailing slash | `/books/` | `/books` |

**Book Store API — complete URL structure:**
```
GET    /api/v1/books                → list all books
POST   /api/v1/books                → create a book
GET    /api/v1/books/42             → get book 42
PUT    /api/v1/books/42             → replace book 42
PATCH  /api/v1/books/42             → update book 42 partially
DELETE /api/v1/books/42             → delete book 42

GET    /api/v1/authors/5/books      → books by author 5
GET    /api/v1/books/42/reviews     → reviews for book 42
POST   /api/v1/books/42/reviews     → add a review to book 42

GET    /api/v1/orders               → list orders
GET    /api/v1/orders/99            → get order 99
POST   /api/v1/orders               → place an order
GET    /api/v1/orders/99/items      → items in order 99
```

**Query parameters — for filtering, sorting, pagination:**
```
GET /api/v1/books?genre=fiction                → filter by genre
GET /api/v1/books?sort=price&order=asc         → sort by price
GET /api/v1/books?page=2&size=20               → paginate
GET /api/v1/books?genre=fiction&sort=title     → combined
GET /api/v1/books?search=clean+code            → text search
```

---

### Slide 10 — REST Best Practices

**Title:** REST Best Practices

**Consistent JSON response structure:**
```json
// Success — single resource
{
  "data": {
    "id": 42,
    "title": "Clean Code",
    "price": 39.99
  }
}

// Success — collection with pagination metadata
{
  "data": [ { "id": 42, ... }, { "id": 43, ... } ],
  "pagination": {
    "page": 1,
    "size": 20,
    "total": 847,
    "totalPages": 43
  }
}

// Error
{
  "error": {
    "code": "VALIDATION_ERROR",
    "message": "title is required",
    "details": [
      { "field": "title", "message": "must not be blank" }
    ],
    "timestamp": "2026-02-23T14:30:00Z"
  }
}
```

**Best practices checklist:**

| Practice | Why |
|---|---|
| Return the created resource in POST responses | Client gets the ID without a second request |
| Use `204 No Content` for successful DELETE | No body needed — deletion has no return value |
| Use `Location` header on 201 responses | Client knows the URL of the new resource |
| Validate input and return 400 with field details | Clients know exactly what to fix |
| Never return 200 with an error in the body | Breaks all standard error handling |
| Use `ETag` for optimistic concurrency | Prevents lost-update race conditions |
| Set `Content-Type: application/json` always | Explicit over implicit |

---

### Slide 11 — API Versioning Strategies

**Title:** API Versioning — Evolving Without Breaking

**Why version?** Real APIs evolve. You'll rename fields, change response shapes, deprecate endpoints. Versioning lets you make breaking changes without breaking existing clients.

**Four versioning strategies:**

**1. URI Path (most common, most visible):**
```
/api/v1/books
/api/v2/books
```
✅ Clear, easy to test in browser, obvious in logs
❌ URI should identify resources, not versions (minor REST purist objection)

**2. Query Parameter:**
```
/api/books?version=1
/api/books?version=2
```
✅ Simple
❌ Easy to forget, harder to cache, less conventional

**3. Custom Header:**
```http
GET /api/books HTTP/1.1
X-API-Version: 2
```
✅ Clean URI, follows REST purist view
❌ Can't test directly in a browser, harder to see in logs

**4. Accept Header / Media Type Versioning:**
```http
GET /api/books HTTP/1.1
Accept: application/vnd.bookstore.v2+json
```
✅ Most REST-correct
❌ Complex for clients, hardest to document and use

**Recommended for this course:** URI path versioning (`/api/v1/...`). It's the most widely used, the most discoverable, and the easiest to understand and test.

**Versioning principles:**
- Start with `v1` from day one — retrofitting versioning is painful
- Never remove a version without a deprecation period
- Communicate sunset dates in response headers: `Deprecation: Mon, 01 Jan 2027` and `Sunset: Mon, 01 Jul 2027`
- Aim to never version if you can make additive (non-breaking) changes — adding new fields to a response is backward-compatible

---

### Slide 12 — Idempotency and Safety Deep Dive

**Title:** Idempotency & Safety — Why They Matter in Practice

**Safe methods (read-only, no server state change):**
- `GET`, `HEAD`, `OPTIONS`
- Calling them ten times is no different from calling once

**Idempotent methods (same result regardless of how many times called):**
- `GET`, `PUT`, `DELETE`, `HEAD`, `OPTIONS`
- `DELETE /books/42` called three times → book is deleted, second and third calls return 404 — but no additional harm is done
- `PUT /books/42` called three times with the same body → book has that state, nothing worse

**POST is NOT idempotent:**
- `POST /books` called three times → three books created
- This is why payment APIs are so careful about idempotency keys

**Idempotency keys for POST:**
```http
POST /api/v1/orders HTTP/1.1
Idempotency-Key: order_abc123_client_xyz

{ "bookId": 42, "quantity": 1 }
```
Server caches the response for this key. If the client retries (e.g., due to a network timeout), the server returns the cached response instead of creating a duplicate order. This pattern is used by Stripe, PayPal, and most payment APIs.

---

### Slide 13 — Authentication vs Authorization in HTTP

**Title:** Auth in HTTP — Headers and Flows

**Authentication:** Who are you?
**Authorization:** What are you allowed to do?

**Common auth schemes in HTTP headers:**

```http
# Basic Auth — base64(username:password) — only use over HTTPS
Authorization: Basic dXNlcjpwYXNzd29yZA==

# Bearer Token (JWT) — most common for REST APIs
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1c2VyMSJ9...

# API Key — simple but less secure
Authorization: ApiKey your_api_key_here
# or as a header:
X-API-Key: your_api_key_here
```

**Token-based auth flow:**
```
Client                          Auth Server              Resource Server
  │── POST /auth/login ───────>│                               │
  │   { username, password }   │                               │
  │<── { access_token } ───────│                               │
  │                            │                               │
  │── GET /api/v1/books ───────────────────────────────────>  │
  │   Authorization: Bearer <token>                            │
  │<── 200 OK { data: [...] } ──────────────────────────────  │
```

> **Note:** JWT structure, validation, and Spring Security implementation are covered in depth on Days 29–30.

---

### Slide 14 — Designing a Complete REST API

**Title:** Putting It All Together — Full Book Store API Design

**Complete endpoint table:**

| Method | URL | Body | Success | Error |
|---|---|---|---|---|
| `GET` | `/api/v1/books` | — | `200` + array | `500` |
| `POST` | `/api/v1/books` | Book JSON | `201` + book + `Location` | `400`, `409` |
| `GET` | `/api/v1/books/{id}` | — | `200` + book | `404` |
| `PUT` | `/api/v1/books/{id}` | Full book JSON | `200` + book | `400`, `404` |
| `PATCH` | `/api/v1/books/{id}` | Partial JSON | `200` + book | `400`, `404` |
| `DELETE` | `/api/v1/books/{id}` | — | `204` No Content | `404` |
| `GET` | `/api/v1/authors/{id}/books` | — | `200` + array | `404` |

**Request/Response examples:**

```http
POST /api/v1/books HTTP/1.1
Content-Type: application/json
Authorization: Bearer <token>

{
  "title": "Clean Code",
  "authorId": 42,
  "price": 39.99,
  "genre": "technology"
}

HTTP/1.1 201 Created
Location: /api/v1/books/1337
Content-Type: application/json

{
  "id": 1337,
  "title": "Clean Code",
  "authorId": 42,
  "price": 39.99,
  "genre": "technology",
  "createdAt": "2026-02-23T14:30:00Z"
}
```

---

### Slide 15 — Common REST Anti-Patterns

**Title:** REST Anti-Patterns — What NOT to Do

**Anti-patterns to avoid:**

```http
# ❌ Verb in URL
GET  /api/getBooks
POST /api/createBook
POST /api/deleteBook/42

# ✅ Method IS the verb
GET    /api/v1/books
POST   /api/v1/books
DELETE /api/v1/books/42

# ❌ Returning 200 for errors
HTTP/1.1 200 OK
{ "success": false, "error": "Book not found" }

# ✅ Use the right status code
HTTP/1.1 404 Not Found
{ "error": "NOT_FOUND", "message": "Book 9999 not found" }

# ❌ Inconsistent naming
/api/v1/Books          (PascalCase)
/api/v1/book_authors   (snake_case)
/api/v1/getOrders      (verb)

# ✅ Consistent lowercase, plural nouns
/api/v1/books
/api/v1/book-authors
/api/v1/orders

# ❌ Exposing database IDs in ways that leak schema
GET /api/v1/users?sql=SELECT+*+FROM+users  (obvious SQL injection)
GET /api/v1/users/1  (OK — but auto-increment IDs can be enumerated)

# ✅ Consider UUIDs for resources
GET /api/v1/users/3fa85f64-5717-4562-b3fc-2c963f66afa6
```

---

### Slide 16 — Part 1 Summary

**Title:** Part 1 Complete — HTTP & REST Reference

**HTTP request structure:** Method + URL + Headers + Body

**HTTP method quick reference:**

| Method | Action | Idempotent | Safe |
|---|---|---|---|
| GET | Read | ✅ | ✅ |
| POST | Create | ❌ | ❌ |
| PUT | Full replace | ✅ | ❌ |
| PATCH | Partial update | Usually | ❌ |
| DELETE | Delete | ✅ | ❌ |

**Status code summary:** 2xx = success · 3xx = redirect · 4xx = client error · 5xx = server error

**Key headers:** `Content-Type`, `Accept`, `Authorization`, `Location`, `Cache-Control`

**REST constraints:** Client-Server · Stateless · Cacheable · Uniform Interface · Layered System · Code on Demand

**URL design rules:** Nouns not verbs · Plural · Lowercase-hyphen · Nest relationships · Query params for filters

**Versioning:** URI path (`/api/v1/`) is the most practical choice for most APIs

**Up next — Part 2:** Postman for testing and automating API requests · Swagger/OpenAPI for documenting APIs · Error handling patterns

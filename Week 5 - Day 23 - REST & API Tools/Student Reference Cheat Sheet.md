# Day 23 — REST & API Tools
## Comprehensive Review Reference

---

## Table of Contents

1. [HTTP Protocol Overview](#1-http-protocol-overview)
2. [URL Anatomy](#2-url-anatomy)
3. [HTTP Request Structure](#3-http-request-structure)
4. [HTTP Response Structure](#4-http-response-structure)
5. [HTTP Methods Reference](#5-http-methods-reference)
6. [HTTP Status Codes — Full Reference](#6-http-status-codes--full-reference)
7. [HTTP Headers Reference](#7-http-headers-reference)
8. [Content Negotiation](#8-content-negotiation)
9. [REST Constraints — Fielding's Six](#9-rest-constraints--fieldings-six)
10. [Resource Naming Rules](#10-resource-naming-rules)
11. [HTTP Methods → CRUD Mapping](#11-http-methods--crud-mapping)
12. [Query Parameters — Filters, Sorting, Pagination](#12-query-parameters--filters-sorting-pagination)
13. [API Versioning Strategies](#13-api-versioning-strategies)
14. [API Design Best Practices](#14-api-design-best-practices)
15. [Postman Interface and Features](#15-postman-interface-and-features)
16. [Environment Variables in Postman](#16-environment-variables-in-postman)
17. [Collections and Organization](#17-collections-and-organization)
18. [Writing Tests in Postman](#18-writing-tests-in-postman)
19. [Pre-Request Scripts](#19-pre-request-scripts)
20. [Collection Runner and Newman CLI](#20-collection-runner-and-newman-cli)
21. [OpenAPI 3.0 Document Structure](#21-openapi-30-document-structure)
22. [Defining Paths and Operations](#22-defining-paths-and-operations)
23. [Parameters in OpenAPI](#23-parameters-in-openapi)
24. [Schemas and $ref](#24-schemas-and-ref)
25. [Error Response Design](#25-error-response-design)
26. [Common Mistakes and Fixes](#26-common-mistakes-and-fixes)
27. [Quick Reference Cheat Sheet](#27-quick-reference-cheat-sheet)
28. [Looking Ahead — Day 24](#28-looking-ahead--day-24)

---

## 1. HTTP Protocol Overview

**HTTP (HyperText Transfer Protocol)** is a stateless, request-response application protocol that runs over TCP/IP. It is the foundation of every REST API.

| Property | Meaning |
|---|---|
| **Stateless** | Each request is independent — no server-side session memory |
| **Request-Response** | Client always initiates; server always responds |
| **Text-based** | Messages are human-readable (headers are plain key-value text) |
| **Runs over TCP** | Reliability (ordered, guaranteed delivery) handled by TCP |

**HTTP Versions:**

| Version | Key Innovation | Notes |
|---|---|---|
| HTTP/1.1 (1997) | Persistent (keep-alive) connections, chunked transfer | Still widely deployed |
| HTTP/2 (2015) | Multiplexing, header compression, server push | Significantly faster |
| HTTP/3 (2022) | Runs over QUIC/UDP, built-in TLS | Best on mobile/lossy networks |

**Why statelessness matters for REST:** Any server in a cluster can handle any request. No "sticky sessions" required. This is the foundation of horizontal scalability.

---

## 2. URL Anatomy

A complete URL has up to six parts:

```
https://api.bookstore.com:8443/api/v1/books?genre=fiction&page=2#results
  │         │              │       │              │                │
scheme    host            port    path       query string       fragment
```

| Part | Example | Notes |
|---|---|---|
| **Scheme** | `https` | Protocol — `http` or `https` |
| **Host** | `api.bookstore.com` | Domain name or IP address |
| **Port** | `8443` | Omit for defaults (80=http, 443=https) |
| **Path** | `/api/v1/books` | Resource identifier — the noun |
| **Query string** | `?genre=fiction&page=2` | Filters, sorting, pagination; starts with `?`, separated by `&` |
| **Fragment** | `#results` | Client-side anchor — never sent to server |

**URL encoding:** Special characters in query values must be percent-encoded. Spaces → `%20` or `+`. Slashes → `%2F`. Ampersands → `%26`. Postman handles this automatically.

---

## 3. HTTP Request Structure

Every HTTP request consists of four parts:

```
[Request Line]
[Headers]
[Blank Line]
[Body — optional]
```

**Complete GET request:**
```http
GET /api/v1/books?genre=fiction&limit=10 HTTP/1.1
Host: api.bookstore.com
Accept: application/json
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
User-Agent: PostmanRuntime/7.36
```

**Complete POST request:**
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

**Request line anatomy:** `METHOD /path?query HTTP/version`

---

## 4. HTTP Response Structure

Every HTTP response consists of four parts:

```
[Status Line]
[Headers]
[Blank Line]
[Body — optional]
```

**Complete 201 response:**
```http
HTTP/1.1 201 Created
Content-Type: application/json; charset=utf-8
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

**Complete 404 response:**
```http
HTTP/1.1 404 Not Found
Content-Type: application/json

{
  "status": 404,
  "error": "Not Found",
  "message": "Book with ID 9999 not found",
  "path": "/api/v1/books/9999",
  "timestamp": "2026-02-23T14:30:05Z"
}
```

---

## 5. HTTP Methods Reference

| Method | Action | Request Body | Safe | Idempotent |
|---|---|---|---|---|
| `GET` | Read / retrieve | ❌ No | ✅ Yes | ✅ Yes |
| `POST` | Create / trigger action | ✅ Yes | ❌ No | ❌ No |
| `PUT` | Full replace | ✅ Yes | ❌ No | ✅ Yes |
| `PATCH` | Partial update | ✅ Yes | ❌ No | Usually |
| `DELETE` | Delete | Optional | ❌ No | ✅ Yes |
| `HEAD` | Like GET, headers only | ❌ No | ✅ Yes | ✅ Yes |
| `OPTIONS` | Allowed methods query | ❌ No | ✅ Yes | ✅ Yes |

**Definitions:**
- **Safe:** Does not modify server state (read-only operations)
- **Idempotent:** Calling N times produces the same result as calling once

**PUT vs PATCH — critical distinction:**

```json
// Current state: { "title": "Clean Code", "price": 39.99, "genre": "technology" }

// PUT /books/42 — REPLACES ENTIRE RESOURCE
// You must send every field or it becomes null/missing
{ "title": "Clean Code", "price": 44.99, "genre": "technology" }

// PATCH /books/42 — UPDATES ONLY WHAT YOU SEND
// Unmentioned fields are untouched
{ "price": 44.99 }
```

**POST idempotency keys:** For operations where duplicate POST requests could cause harm (payments, order creation), include an `Idempotency-Key` header. The server caches the response and returns it on retry without re-executing the operation.

```http
POST /api/v1/orders HTTP/1.1
Idempotency-Key: order_abc123_2026-02-23

{ "bookId": 42, "quantity": 1 }
```

---

## 6. HTTP Status Codes — Full Reference

**2xx — Success:**

| Code | Name | When to use |
|---|---|---|
| `200 OK` | Standard success | GET, PUT, PATCH — request succeeded with response body |
| `201 Created` | Resource created | POST succeeded — new resource created; include `Location` header |
| `202 Accepted` | Processing async | Request accepted but not yet complete (background job) |
| `204 No Content` | Success, no body | DELETE succeeded; PUT/PATCH when no response body needed |

**3xx — Redirection:**

| Code | Name | When to use |
|---|---|---|
| `301 Moved Permanently` | Permanent redirect | Resource has permanently moved — update bookmarks/cache |
| `302 Found` | Temporary redirect | Temporary redirect — keep using original URL |
| `304 Not Modified` | Not modified | Cached response is still valid (ETag / If-None-Match) |

**4xx — Client Errors (client's fault):**

| Code | Name | When to use |
|---|---|---|
| `400 Bad Request` | Invalid request | Malformed JSON, wrong types, missing required field |
| `401 Unauthorized` | Not authenticated | No valid credentials provided (confusingly named) |
| `403 Forbidden` | Not authorized | Authenticated but lacking permission |
| `404 Not Found` | Resource missing | Wrong ID, wrong URL path |
| `405 Method Not Allowed` | Wrong HTTP method | POST to read-only, DELETE on non-deletable resource |
| `409 Conflict` | State conflict | Duplicate key violation, optimistic lock failure |
| `410 Gone` | Permanently removed | Resource existed but was permanently deleted |
| `422 Unprocessable Entity` | Semantic validation | Valid JSON but business rules violated |
| `429 Too Many Requests` | Rate limited | Client exceeded request quota — include `Retry-After` |

**5xx — Server Errors (your fault):**

| Code | Name | When to use |
|---|---|---|
| `500 Internal Server Error` | Unhandled exception | Bug in your code — never leak stack traces |
| `502 Bad Gateway` | Upstream failure | Your server got a bad response from an upstream service |
| `503 Service Unavailable` | Server down/overloaded | Maintenance mode — include `Retry-After` |
| `504 Gateway Timeout` | Upstream timeout | Upstream service didn't respond in time |

**Key distinction:** 4xx = client did something wrong. 5xx = your code/infrastructure is broken. Monitoring systems page on-call engineers for 5xx, not 4xx.

---

## 7. HTTP Headers Reference

**Common request headers:**

| Header | Example | Purpose |
|---|---|---|
| `Content-Type` | `application/json` | Body format — required when sending a body |
| `Accept` | `application/json` | Desired response format |
| `Authorization` | `Bearer <token>` | Authentication credentials |
| `User-Agent` | `PostmanRuntime/7.36` | Client software identifier |
| `Accept-Language` | `en-US,en;q=0.9` | Language preference |
| `Cache-Control` | `no-cache` | Caching behavior |
| `If-None-Match` | `"abc123"` | Conditional request — only return if ETag differs |
| `If-Modified-Since` | `Sat, 01 Jan 2026` | Conditional request — only return if modified since |

**Common response headers:**

| Header | Example | Purpose |
|---|---|---|
| `Content-Type` | `application/json; charset=utf-8` | Body format |
| `Content-Length` | `256` | Body size in bytes |
| `Location` | `/api/v1/books/1337` | URL of newly created resource (201) |
| `Cache-Control` | `max-age=3600` | How long to cache (seconds) |
| `ETag` | `"abc123"` | Response fingerprint for cache validation |
| `Retry-After` | `30` | Seconds before retrying (429, 503) |
| `X-Request-Id` | `req_8f4a2c` | Trace ID for request correlation |
| `WWW-Authenticate` | `Bearer realm="api"` | Auth challenge on 401 |

**Custom headers:** Conventionally prefixed with `X-`. Examples: `X-Request-Id`, `X-Correlation-Id`, `X-API-Key`, `X-Rate-Limit-Remaining`.

---

## 8. Content Negotiation

Content negotiation is the mechanism by which client and server agree on data format.

**Client specifies desired format:**
```http
Accept: application/json
Accept: application/xml
Accept: application/json, application/xml;q=0.9, */*;q=0.8
```
The `q=` parameter is quality weight (0 to 1). Higher = preferred.

**Client specifies body format:**
```http
Content-Type: application/json
Content-Type: application/xml
Content-Type: multipart/form-data; boundary=----WebKitFormBoundary
Content-Type: application/x-www-form-urlencoded
```

**Common media types:**

| Media Type | Used for |
|---|---|
| `application/json` | JSON bodies — REST API standard |
| `application/xml` | XML bodies — older enterprise APIs |
| `application/x-www-form-urlencoded` | HTML form submissions |
| `multipart/form-data` | File uploads with form fields |
| `text/plain` | Plain text |
| `text/html` | HTML documents |
| `application/octet-stream` | Binary file download |

**Server can't satisfy Accept → 406 Not Acceptable**
**Client sends unsupported Content-Type → 415 Unsupported Media Type**

---

## 9. REST Constraints — Fielding's Six

Roy Fielding's 2000 doctoral dissertation defined six architectural constraints for REST:

| Constraint | Meaning | Practical Implication |
|---|---|---|
| **1. Client-Server** | UI and data separated | Frontend and backend deploy independently |
| **2. Stateless** | No server-side session state | Auth token in every request; horizontal scaling works |
| **3. Cacheable** | Responses declare cacheability | CDNs and browser caches work correctly |
| **4. Uniform Interface** | Standard methods, standard URIs, standard media types | Every developer reads `GET /books/42` the same way |
| **5. Layered System** | Client doesn't know about intermediaries | Load balancers, caches, API gateways are transparent |
| **6. Code on Demand** *(optional)* | Server can send executable code | JavaScript in browsers; not used in most REST APIs |

**Richardson Maturity Model** — a pragmatic way to assess how "RESTful" an API is:
- **Level 0** — One URL, one HTTP method (like XML-RPC or SOAP)
- **Level 1** — Resources have individual URLs
- **Level 2** — Uses HTTP methods correctly (GET for reads, POST for creates, etc.)
- **Level 3** — Hypermedia controls (HATEOAS) — responses include links to related actions
- Most APIs stop at Level 2 and are still considered "RESTful" in practice

---

## 10. Resource Naming Rules

| Rule | Bad ❌ | Good ✅ |
|---|---|---|
| Nouns, not verbs | `/getBooks`, `/createBook`, `/deleteUser` | `/books`, `/users` |
| Plural nouns | `/book/42`, `/user/5` | `/books/42`, `/users/5` |
| Lowercase | `/Books`, `/USERS` | `/books`, `/users` |
| Hyphens for multi-word | `/bookAuthor`, `/book_author` | `/book-authors` |
| Nest for relationships | `/books?authorId=5` (ok but less clear) | `/authors/5/books` |
| No file extensions | `/books/42.json`, `/books/42.xml` | `/books/42` + `Accept` header |
| No trailing slash | `/books/`, `/users/` | `/books`, `/users` |
| Query params for filters | `/fiction-books`, `/sorted-books` | `/books?genre=fiction&sort=title` |

**Nesting depth:** Stop at two levels to keep URLs manageable. `/authors/5/books/42` is fine. `/authors/5/books/42/reviews/7/comments/3` is not.

**Collection vs singleton:**
```
GET /books           → collection (returns array)
GET /books/42        → singleton (returns one object)
GET /books/42/cover  → sub-singleton (a book has one cover)
```

---

## 11. HTTP Methods → CRUD Mapping

| HTTP Method | SQL Equivalent | Collection URL `/books` | Singleton URL `/books/{id}` |
|---|---|---|---|
| `GET` | SELECT | List all books (paginated) | Get one book by ID |
| `POST` | INSERT | Create a new book | Not used (405) |
| `PUT` | UPDATE (full) | Bulk replace (rare) | Replace entire book |
| `PATCH` | UPDATE (partial) | Not common | Update specific fields |
| `DELETE` | DELETE | Delete all (dangerous!) | Delete one book by ID |

**Standard REST API behavior:**

```
GET    /api/v1/books                 → 200 OK + array
POST   /api/v1/books                 → 201 Created + book + Location header
GET    /api/v1/books/42              → 200 OK + book  |  404 Not Found
PUT    /api/v1/books/42              → 200 OK + book  |  404 Not Found
PATCH  /api/v1/books/42              → 200 OK + book  |  404 Not Found
DELETE /api/v1/books/42              → 204 No Content  |  404 Not Found
```

---

## 12. Query Parameters — Filters, Sorting, Pagination

**Filtering:**
```
GET /api/v1/books?genre=fiction
GET /api/v1/books?genre=fiction&price_max=50
GET /api/v1/books?search=clean+code
GET /api/v1/books?authorId=42
GET /api/v1/books?status=available
```

**Sorting:**
```
GET /api/v1/books?sort=price             → sort by price ascending
GET /api/v1/books?sort=price&order=desc  → sort by price descending
GET /api/v1/books?sort=title,asc         → alternative convention
GET /api/v1/books?sort=-price            → leading - = descending (GitHub style)
```

**Pagination — page/size:**
```
GET /api/v1/books?page=0&size=20    → first page, 20 per page (0-indexed)
GET /api/v1/books?page=1&size=20    → second page
```

**Pagination — cursor-based (better for large datasets):**
```
GET /api/v1/books?limit=20                      → first page
GET /api/v1/books?limit=20&after=book_id_123    → next page (no skipping)
```

**Pagination response metadata:**
```json
{
  "data": [ ... ],
  "pagination": {
    "page": 0,
    "size": 20,
    "total": 847,
    "totalPages": 43,
    "hasNext": true,
    "hasPrevious": false
  }
}
```

---

## 13. API Versioning Strategies

| Strategy | Example | Pros | Cons |
|---|---|---|---|
| **URI Path** | `/api/v1/books` | Visible, easy to test, great logs | Version in URI isn't strictly RESTful |
| **Query Parameter** | `/api/books?version=1` | Simple | Easy to forget, hard to cache |
| **Custom Header** | `X-API-Version: 1` | Clean URI | Can't test in browser, hidden from logs |
| **Media Type** | `Accept: application/vnd.api+json;version=1` | Most "correct" REST | Very complex for clients |

**Recommendation:** URI path versioning. Start with `v1`. Increment for breaking changes only.

**Breaking changes (require new version):**
- Removing a field from a response
- Renaming a field
- Changing a field's type
- Removing an endpoint
- Changing authentication requirements

**Non-breaking changes (no version bump needed):**
- Adding new optional fields to responses
- Adding new optional query parameters
- Adding new endpoints
- Adding new enum values (clients should handle unknown values gracefully)

**Deprecation headers:**
```http
Deprecation: Mon, 01 Jun 2026 00:00:00 GMT
Sunset: Mon, 01 Jan 2027 00:00:00 GMT
Link: <https://api.bookstore.com/api/v2/books>; rel="successor-version"
```

---

## 14. API Design Best Practices

**Response envelopes:**
```json
// Single resource
{
  "data": { "id": 42, "title": "Clean Code" }
}

// Collection
{
  "data": [ {...}, {...} ],
  "pagination": { "page": 0, "size": 20, "total": 847 }
}

// Error
{
  "error": {
    "code": "RESOURCE_NOT_FOUND",
    "message": "Book with ID 9999 not found",
    "details": []
  }
}
```

**Checklist:**

| Practice | Reason |
|---|---|
| Return created resource body on POST | Client gets ID without second request |
| Include `Location` header on 201 | Client knows URL of new resource |
| Use `204 No Content` for DELETE | No return value needed |
| Return field-level details on 400/422 | Client knows exactly what to fix |
| Never return 200 with error in body | Breaks standard HTTP error handling |
| Use `ETag` for concurrent update safety | Prevents lost-update race conditions |
| Always set `Content-Type` on responses | Explicit over implicit |
| Validate all inputs server-side | Never trust client-provided data |
| Support `OPTIONS` method (CORS) | Required for browser-to-API calls |

---

## 15. Postman Interface and Features

**Key interface regions:**
- **Left sidebar:** Collections, Environments, History
- **Request builder:** Method dropdown, URL bar, Params/Auth/Headers/Body/Pre-req/Tests tabs
- **Response panel:** Status code, Time, Size, Body/Headers/Cookies tabs

**Request builder tabs:**

| Tab | Purpose |
|---|---|
| **Params** | Add/edit query string key-value pairs (visual) |
| **Authorization** | Set auth type (Bearer, Basic, API Key, OAuth 2.0) |
| **Headers** | Add/edit request headers |
| **Body** | Select body type: none, form-data, urlencoded, raw (JSON), binary |
| **Pre-request Script** | JavaScript before request |
| **Tests** | JavaScript assertions after response |

**Body types:**

| Type | Use case |
|---|---|
| `none` | GET, DELETE — no body |
| `form-data` | Multipart form or file upload |
| `x-www-form-urlencoded` | HTML form submission |
| `raw → JSON` | REST API calls — sets `Content-Type: application/json` |
| `binary` | File upload as binary stream |

---

## 16. Environment Variables in Postman

**Variable syntax:** `{{variableName}}` — used anywhere in Postman (URL, headers, body, scripts)

**Creating an environment:**
1. Click Environments in sidebar
2. Click `+` (New Environment)
3. Name it (e.g., "Book Store - Dev")
4. Add variables: key → `baseUrl`, value → `http://localhost:8080`
5. Click Save
6. Select active environment from top-right dropdown

**Variable scopes (narrower overrides wider):**

| Scope | Set via | Lifetime |
|---|---|---|
| **Global** | Globals panel | Permanent — all workspaces |
| **Collection** | Collection variables tab | Permanent — scoped to collection |
| **Environment** | Environment panel | Permanent — active when env selected |
| **Local** | Pre-request script `pm.variables.set()` | Current request only |

**Common environment variables:**
```
baseUrl     = http://localhost:8080
authToken   = eyJhbGciOiJIUzI1NiJ9...
apiKey      = your-api-key-here
testUserId  = 42
```

**Script access:**
```javascript
pm.environment.set("key", "value");     // set
pm.environment.get("key");              // get
pm.environment.unset("key");            // remove
pm.globals.set("key", "value");         // set global
pm.variables.set("key", "value");       // set local (current request only)
```

---

## 17. Collections and Organization

**Folder structure best practice:**
```
📁 Book Store API
  (Collection-level Authorization: Bearer {{authToken}})
  📂 Auth
    📄 POST - Login
    📄 POST - Refresh Token
    📄 POST - Logout
  📂 Books
    📄 GET - List Books
    📄 GET - Get Book by ID
    📄 POST - Create Book
    📄 PUT - Replace Book
    📄 PATCH - Update Book Price
    📄 DELETE - Delete Book
  📂 Authors
    📄 GET - List Authors
    📄 GET - Books by Author
  📂 Orders
    📄 POST - Place Order
    📄 GET - Get Order
```

**Collection-level Authorization:** Set once on the collection → all requests inherit it. Individual requests can override with "Inherit from parent" → override option.

**Export and Import:** `...` menu → Export → Collection v2.1 JSON. Share with teammates. They import and configure their own environment values.

---

## 18. Writing Tests in Postman

**`pm.test()` signature:**
```javascript
pm.test("descriptive name string", function () {
    // assertions here
    // assertion failure = test fails
});
```

**Common assertions:**
```javascript
// Status code
pm.test("Status is 201", () => pm.response.to.have.status(201));

// Status code family
pm.test("Status is success", () => pm.expect(pm.response.code).to.be.oneOf([200, 201, 204]));

// Response header
pm.test("Has Location header", () => pm.response.to.have.header("Location"));
pm.test("Content-Type is JSON", () => {
    pm.expect(pm.response.headers.get("Content-Type")).to.include("application/json");
});

// Parse body
pm.test("Body is valid JSON", () => {
    const body = pm.response.json();
    pm.expect(body).to.be.an("object");
});

// Field value
pm.test("Title matches", () => {
    pm.expect(pm.response.json().title).to.equal("Clean Code");
});

// Type check
pm.test("Price is a number", () => {
    pm.expect(pm.response.json().price).to.be.a("number");
});

// Field existence
pm.test("ID exists", () => pm.expect(pm.response.json().id).to.exist);

// Array length
pm.test("Returns array", () => {
    pm.expect(pm.response.json()).to.be.an("array");
    pm.expect(pm.response.json().length).to.be.greaterThan(0);
});

// Response time
pm.test("Response under 500ms", () => {
    pm.expect(pm.response.responseTime).to.be.below(500);
});

// Save to environment for next request
pm.test("Save book ID", () => {
    const body = pm.response.json();
    pm.expect(body.id).to.exist;
    pm.environment.set("createdBookId", body.id);
});
```

**Chai chaining words:** `.to.be.a()`, `.to.equal()`, `.to.include()`, `.to.have.property()`, `.to.be.above()`, `.to.be.below()`, `.to.have.lengthOf()`, `.to.exist`, `.to.be.null`, `.to.be.true`, `.to.be.false`

---

## 19. Pre-Request Scripts

```javascript
// Set dynamic variables before request is sent

// Current timestamp
pm.environment.set("currentTimestamp", new Date().toISOString());

// Random test data
pm.environment.set("randomPrice", (Math.random() * 100 + 1).toFixed(2));

// Unique idempotency key
pm.environment.set("idempotencyKey", "req_" + Date.now() + "_" + Math.random().toString(36).slice(2));

// Set a request header dynamically
pm.request.headers.add({ key: "X-Request-Id", value: "req_" + Date.now() });

// Auto-login if token is missing/expired
const expiry = pm.environment.get("tokenExpiry");
if (!expiry || Date.now() > parseInt(expiry)) {
    pm.sendRequest({
        url: pm.environment.get("baseUrl") + "/api/v1/auth/login",
        method: "POST",
        header: { "Content-Type": "application/json" },
        body: {
            mode: "raw",
            raw: JSON.stringify({
                username: pm.environment.get("testUsername"),
                password: pm.environment.get("testPassword")
            })
        }
    }, (err, res) => {
        if (!err) {
            pm.environment.set("authToken", res.json().accessToken);
            pm.environment.set("tokenExpiry", Date.now() + 3600000);
        }
    });
}
```

---

## 20. Collection Runner and Newman CLI

**Collection Runner (in Postman UI):**
1. Click the `▶` (Run) button on a collection
2. Select environment
3. Optionally select a data file (CSV/JSON) for parameterized runs
4. Click Run
5. See pass/fail summary per test

**End-to-end workflow example:**
```
1. POST /auth/login           → save authToken
2. POST /books                → create book, save bookId
3. GET  /books/{{bookId}}     → verify created correctly
4. PATCH /books/{{bookId}}    → update price
5. GET  /books/{{bookId}}     → verify price updated
6. DELETE /books/{{bookId}}   → delete book
7. GET  /books/{{bookId}}     → verify 404
```

**Newman CLI:**
```bash
# Install
npm install -g newman

# Basic run
newman run collection.postman_collection.json \
  -e environment.postman_environment.json

# With data file
newman run collection.postman_collection.json \
  -e environment.postman_environment.json \
  -d test-data.csv

# HTML report
npm install -g newman-reporter-htmlextra
newman run collection.postman_collection.json \
  -e environment.postman_environment.json \
  -r htmlextra --reporter-htmlextra-export report.html

# Exit codes: 0 = all tests pass, 1 = test failure, non-zero = error
```

**GitHub Actions integration:**
```yaml
- name: Run API Tests
  run: |
    npm install -g newman
    newman run tests/BookStoreAPI.postman_collection.json \
      -e tests/env-staging.postman_environment.json
```

---

## 21. OpenAPI 3.0 Document Structure

**Minimal complete document:**
```yaml
openapi: 3.0.3

info:
  title: Book Store API
  description: API for managing books, authors, and orders
  version: 1.0.0
  contact:
    name: API Support
    email: api@bookstore.com

servers:
  - url: http://localhost:8080
    description: Local development
  - url: https://staging-api.bookstore.com
    description: Staging
  - url: https://api.bookstore.com
    description: Production

tags:
  - name: Books
    description: Book management operations
  - name: Authors
    description: Author management operations

paths:
  # defined below

components:
  # schemas, responses, security schemes

security:
  - bearerAuth: []   # apply globally to all operations
```

**Top-level fields:**

| Field | Required | Purpose |
|---|---|---|
| `openapi` | ✅ | Spec version string (e.g., `"3.0.3"`) |
| `info` | ✅ | API metadata (title, version, description, contact) |
| `servers` | — | Base URL(s) — defaults to `/` if omitted |
| `paths` | ✅ | Endpoint definitions |
| `components` | — | Reusable schemas, responses, parameters, security schemes |
| `security` | — | Global security requirements |
| `tags` | — | Tag definitions for grouping in Swagger UI |

---

## 22. Defining Paths and Operations

```yaml
paths:
  /api/v1/books/{bookId}:
    get:
      operationId: getBookById
      summary: Get a book by ID
      description: Returns the full book details for the specified ID.
      tags:
        - Books
      parameters:
        - name: bookId
          in: path
          required: true
          schema:
            type: integer
            format: int64
          example: 42
      responses:
        "200":
          description: Book found
          content:
            application/json:
              schema:
                $ref: "#/components/schemas/Book"
        "404":
          $ref: "#/components/responses/NotFound"
        "401":
          $ref: "#/components/responses/Unauthorized"
      security:
        - bearerAuth: []
```

**Operation fields:**

| Field | Required | Purpose |
|---|---|---|
| `operationId` | Recommended | Unique ID — used as method name in SDK generation |
| `summary` | Recommended | One-line description for Swagger UI |
| `description` | — | Detailed description, supports Markdown |
| `tags` | — | Groups operations in Swagger UI |
| `parameters` | — | Path, query, header, cookie parameters |
| `requestBody` | — | Body for POST/PUT/PATCH |
| `responses` | ✅ | Response definitions (at least one required) |
| `security` | — | Override global security for this operation |
| `deprecated` | — | Mark operation as deprecated |

---

## 23. Parameters in OpenAPI

**Four parameter locations:**

| `in:` | Example | Notes |
|---|---|---|
| `path` | `/books/{bookId}` | Always `required: true` |
| `query` | `/books?genre=fiction` | `required: false` by default |
| `header` | `X-Request-Id: req_123` | Case-insensitive per HTTP spec |
| `cookie` | `session=abc` | Rarely used in REST APIs |

**Full parameter definition:**
```yaml
parameters:
  - name: genre
    in: query
    required: false
    description: Filter books by genre
    schema:
      type: string
      enum: [fiction, non-fiction, technology, biography, science]
      example: technology

  - name: page
    in: query
    schema:
      type: integer
      minimum: 0
      default: 0

  - name: size
    in: query
    schema:
      type: integer
      minimum: 1
      maximum: 100
      default: 20

  - name: bookId
    in: path
    required: true
    schema:
      type: integer
      format: int64
```

**Schema validation keywords:** `minimum`, `maximum`, `minLength`, `maxLength`, `pattern` (regex), `enum` (allowed values), `default`, `example`

---

## 24. Schemas and $ref

**Common schema types and formats:**

| `type` | `format` | JSON Example | Java Type |
|---|---|---|---|
| `integer` | `int32` | `42` | `int` / `Integer` |
| `integer` | `int64` | `42` | `long` / `Long` |
| `number` | `float` | `3.14` | `float` / `Float` |
| `number` | `double` | `3.14` | `double` / `Double` |
| `string` | — | `"hello"` | `String` |
| `string` | `date` | `"2026-02-23"` | `LocalDate` |
| `string` | `date-time` | `"2026-02-23T14:30:00Z"` | `LocalDateTime` |
| `string` | `uuid` | `"3fa85f64-..."` | `UUID` |
| `boolean` | — | `true` | `boolean` / `Boolean` |
| `array` | — | `[1, 2, 3]` | `List<T>` |
| `object` | — | `{ "key": "value" }` | POJO / DTO |

**Complete schema with $ref:**
```yaml
components:
  schemas:
    Book:
      type: object
      required: [id, title, authorId, price, genre]
      properties:
        id:
          type: integer
          format: int64
          readOnly: true
          example: 42
        title:
          type: string
          minLength: 1
          maxLength: 255
        authorId:
          type: integer
          format: int64
        price:
          type: number
          format: double
          minimum: 0.01
        genre:
          type: string
          enum: [fiction, non-fiction, technology, biography, science]
        createdAt:
          type: string
          format: date-time
          readOnly: true

    CreateBookRequest:
      type: object
      required: [title, authorId, price, genre]
      properties:
        title:
          type: string
          minLength: 1
          maxLength: 255
        authorId:
          type: integer
          format: int64
        price:
          type: number
          minimum: 0.01
        genre:
          type: string
          enum: [fiction, non-fiction, technology, biography, science]
```

**$ref usage:** `$ref: "#/components/schemas/Book"` — JSON Pointer syntax. `#` = document root. `/components/schemas/Book` = path. Change schema once, updates everywhere.

**readOnly vs writeOnly:** `readOnly: true` — appears in responses only, ignored in requests (e.g., `id`, `createdAt`). `writeOnly: true` — appears in requests only, omitted from responses (e.g., `password`).

---

## 25. Error Response Design

**Standard error response envelope:**
```json
{
  "status": 422,
  "error": "Unprocessable Entity",
  "message": "Validation failed for 2 field(s)",
  "path": "/api/v1/books",
  "timestamp": "2026-02-23T14:30:00Z",
  "details": [
    { "field": "price", "message": "must be greater than 0" },
    { "field": "title", "message": "must not be blank" }
  ]
}
```

**OpenAPI error schema:**
```yaml
components:
  schemas:
    ErrorResponse:
      type: object
      required: [status, error, message, timestamp]
      properties:
        status:
          type: integer
          example: 404
        error:
          type: string
          example: "Not Found"
        message:
          type: string
          example: "Book with ID 9999 not found"
        path:
          type: string
          example: "/api/v1/books/9999"
        timestamp:
          type: string
          format: date-time
        details:
          type: array
          items:
            type: object
            properties:
              field:
                type: string
              message:
                type: string

  responses:
    BadRequest:
      description: Invalid request
      content:
        application/json:
          schema:
            $ref: "#/components/schemas/ErrorResponse"
    Unauthorized:
      description: Not authenticated
      content:
        application/json:
          schema:
            $ref: "#/components/schemas/ErrorResponse"
    NotFound:
      description: Resource not found
      content:
        application/json:
          schema:
            $ref: "#/components/schemas/ErrorResponse"
    InternalServerError:
      description: Unexpected server error
      content:
        application/json:
          schema:
            $ref: "#/components/schemas/ErrorResponse"
```

**Status → error string mapping:**

| Status | `error` field |
|---|---|
| 400 | `"Bad Request"` |
| 401 | `"Unauthorized"` |
| 403 | `"Forbidden"` |
| 404 | `"Not Found"` |
| 405 | `"Method Not Allowed"` |
| 409 | `"Conflict"` |
| 422 | `"Unprocessable Entity"` |
| 429 | `"Too Many Requests"` |
| 500 | `"Internal Server Error"` |
| 503 | `"Service Unavailable"` |

**Design rule:** Every endpoint that can return an error returns the same `ErrorResponse` structure. Clients write error handling code once.

---

## 26. Common Mistakes and Fixes

| Mistake | Fix |
|---|---|
| Verbs in URLs (`/getBooks`, `/createUser`) | Use nouns + HTTP methods: `GET /books`, `POST /users` |
| Using GET for mutations | Never use GET for anything that changes state |
| Returning 200 for errors | Use the appropriate 4xx or 5xx status code |
| Missing `Content-Type` header | Always set `Content-Type: application/json` on JSON responses |
| Missing `Location` header on 201 | Always include `Location: /resource/newId` with 201 responses |
| PUT without sending full resource | Send all fields or use PATCH instead |
| No versioning strategy | Add `/api/v1/` prefix from day one |
| Inconsistent naming (`userId` vs `user_id` vs `UserId`) | Pick one convention (camelCase for JSON) and enforce it |
| Returning HTML error pages for JSON API | Configure error handling to always return JSON |
| Hardcoding localhost URLs in Postman | Use `{{baseUrl}}` environment variable |
| Not saving Postman tests with requests | Save after writing tests so they persist |
| Leaking stack traces in 500 responses | Log internally, return clean JSON error body to client |
| No pagination on list endpoints | Always paginate — unbounded lists become a performance crisis |
| Deeply nested URLs | Keep nesting ≤ 2 levels |
| Missing required fields in schema | Use `required` array in OpenAPI schema to document requirements |

---

## 27. Quick Reference Cheat Sheet

**HTTP Method → Use Case:**
```
GET     → Read/list (no body, no side effects)
POST    → Create (body required, not idempotent)
PUT     → Full replace (send complete object)
PATCH   → Partial update (send only changed fields)
DELETE  → Delete (idempotent)
```

**Response code quick pick:**
```
Created resource?      → 201 + Location header
Deleted resource?      → 204 No Content
Resource not found?    → 404
Bad input?             → 400 (structural) or 422 (business rule)
Not logged in?         → 401
Not allowed?           → 403
Server crashed?        → 500
```

**Postman test template:**
```javascript
pm.test("Status 200", () => pm.response.to.have.status(200));
pm.test("Body has expected field", () => {
    pm.expect(pm.response.json().fieldName).to.equal("expectedValue");
});
pm.test("Response under 500ms", () => {
    pm.expect(pm.response.responseTime).to.be.below(500);
});
```

**OpenAPI minimal operation template:**
```yaml
/api/v1/resource/{id}:
  get:
    operationId: getResourceById
    summary: Short description
    tags: [ResourceTag]
    parameters:
      - name: id
        in: path
        required: true
        schema: { type: integer, format: int64 }
    responses:
      "200":
        description: Success
        content:
          application/json:
            schema:
              $ref: "#/components/schemas/Resource"
      "404":
        $ref: "#/components/responses/NotFound"
```

**Error response template:**
```json
{
  "status": 404,
  "error": "Not Found",
  "message": "Human-readable description",
  "path": "/api/v1/resource/id",
  "timestamp": "2026-02-23T14:30:00Z",
  "details": []
}
```

---

## 28. Looking Ahead — Day 24

**Day 24: Maven, Gradle & Spring Core**

With REST design and tooling complete, you're ready to build. Day 24 starts the implementation phase:

| Topic | What You'll Learn |
|---|---|
| **Maven** | `pom.xml`, GAV coordinates, dependency management, build lifecycle (clean → compile → test → package), Maven repositories |
| **Gradle** | `build.gradle`, Groovy/Kotlin DSL, task system, Gradle vs Maven comparison |
| **Spring Core** | IoC container, dependency injection, `@Component`, `@Service`, `@Repository`, `@Autowired`, `@Configuration`, `@Bean` |
| **ApplicationContext** | How Spring scans classes, resolves dependencies, and wires the application together |

**Connection to Day 23:** The Book Store API you designed today becomes a Spring Boot application. Day 24 sets up the project structure. Day 25 (Spring Boot) adds the embedded server and auto-configuration. Day 26 (Spring MVC) implements the REST controllers — with `@RestController`, `@GetMapping`, `@PostMapping`, `ResponseEntity`, and exception handling using `@ControllerAdvice` that produces the same `ErrorResponse` JSON you defined today.

The OpenAPI spec you'd write by hand today — in Day 26, adding the `springdoc-openapi` dependency automatically generates it from your annotations and serves Swagger UI at `http://localhost:8080/swagger-ui.html`.

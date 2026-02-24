# HTTP Protocol & RESTful API Principles
# Day 23 — REST & API Tools | Part 1

---

## Section 1 — HTTP Protocol Overview

HTTP (HyperText Transfer Protocol) is the **foundation of data communication on the web**.
It is a **stateless, request-response protocol** that operates over TCP/IP.

### Key Characteristics

| Characteristic | Description |
|----------------|-------------|
| **Stateless**  | Each request is independent. The server does NOT remember previous requests. |
| **Text-based** | Messages are human-readable (headers and bodies are plain text or structured text). |
| **Client-Server** | A client initiates a request; the server processes it and returns a response. |
| **Layered**    | HTTP operates at the Application Layer (Layer 7) of the OSI model. |

### HTTP vs HTTPS

```
HTTP  → Plain text communication. Susceptible to eavesdropping.
HTTPS → HTTP over TLS (Transport Layer Security). Encrypted in transit.
         Standard for all production APIs.
```

### HTTP Versions (Awareness)

```
HTTP/1.1 — Most widely used. One request per TCP connection (keep-alive added).
HTTP/2   — Multiplexing: multiple requests over a single connection. Binary framing.
HTTP/3   — Uses QUIC (UDP-based). Faster connection setup. Used by modern APIs.
```

> **Today's focus is HTTP/1.1**, which is the version you'll interact with in 99% of REST API work.

---

## Section 2 — HTTP Request/Response Lifecycle

Every interaction with an API follows this cycle:

```
CLIENT                                         SERVER
  │                                               │
  │  1. DNS Resolution (hostname → IP address)    │
  │──────────────────────────────────────────────>│
  │                                               │
  │  2. TCP Handshake (SYN → SYN-ACK → ACK)      │
  │<─────────────────────────────────────────────>│
  │                                               │
  │  3. HTTP Request                              │
  │  ┌──────────────────────────────────────┐     │
  │  │ Request Line:  GET /api/users HTTP/1.1│     │
  │  │ Headers:       Host: api.example.com │     │
  │  │                Authorization: Bearer │     │
  │  │                Accept: application/  │     │
  │  │                json                  │     │
  │  │ Body:          (empty for GET)        │     │
  │  └──────────────────────────────────────┘     │
  │──────────────────────────────────────────────>│
  │                                               │
  │  4. Server processes the request              │
  │     - Route matching                          │
  │     - Authentication check                    │
  │     - Business logic (query DB, etc.)         │
  │                                               │
  │  5. HTTP Response                             │
  │  ┌──────────────────────────────────────┐     │
  │  │ Status Line:  HTTP/1.1 200 OK         │     │
  │  │ Headers:      Content-Type: applic-  │<────│
  │  │               ation/json             │     │
  │  │               Content-Length: 234    │     │
  │  │ Body:         [{"id":1,"name":"..."}] │     │
  │  └──────────────────────────────────────┘     │
  │<──────────────────────────────────────────────│
  │                                               │
  │  6. Client processes the response             │
```

### Anatomy of an HTTP Request

```http
GET /api/v1/products/42?includeReviews=true HTTP/1.1
Host: api.shopify-demo.com
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
Accept: application/json
Accept-Language: en-US
```

**Breakdown:**
- `GET` — HTTP method
- `/api/v1/products/42` — Request path (resource identifier)
- `?includeReviews=true` — Query parameter
- `HTTP/1.1` — Protocol version
- `Host` — Which server to route to (required in HTTP/1.1)
- `Authorization` — Carries the auth token
- `Accept` — What content type the client can handle

### Anatomy of an HTTP Response

```http
HTTP/1.1 200 OK
Content-Type: application/json
Content-Length: 187
Cache-Control: max-age=3600

{
  "id": 42,
  "name": "Mechanical Keyboard",
  "price": 129.99,
  "inStock": true,
  "category": "Electronics"
}
```

**Breakdown:**
- `HTTP/1.1 200 OK` — Protocol version + status code + reason phrase
- `Content-Type: application/json` — The format of the body
- `Content-Length: 187` — Size of the body in bytes
- `Cache-Control: max-age=3600` — Client may cache this for 1 hour
- Body — The actual data (JSON in this case)

---

## Section 3 — HTTP Methods

HTTP defines a standard set of **verbs** that indicate the desired action on a resource.

### The 5 Core REST Methods

```
┌─────────┬──────────┬─────────────────────────────────────────────────────────────┐
│ Method  │ Body?    │ Meaning                                                     │
├─────────┼──────────┼─────────────────────────────────────────────────────────────┤
│ GET     │ No       │ Retrieve a resource. Should NEVER modify data.              │
│ POST    │ Yes      │ Create a new resource. Not idempotent.                      │
│ PUT     │ Yes      │ Replace a resource entirely. Must include the full payload. │
│ PATCH   │ Yes      │ Partially update a resource. Only send what changes.        │
│ DELETE  │ Optional │ Remove a resource.                                          │
└─────────┴──────────┴─────────────────────────────────────────────────────────────┘
```

### Key Properties: Safe vs Idempotent

```
Safe:         Does NOT modify server state. (GET is safe; POST is NOT safe.)
Idempotent:   Calling it once vs calling it 100 times produces the same result.
              GET, PUT, DELETE are idempotent.
              POST is NOT idempotent (creates a new record each time).
              PATCH may or may not be idempotent depending on implementation.
```

### Method Examples — Products API

```
GET    /api/v1/products        → Get all products (list)
GET    /api/v1/products/42     → Get product with ID 42
POST   /api/v1/products        → Create a new product (body contains data)
PUT    /api/v1/products/42     → Replace product 42 entirely (full object in body)
PATCH  /api/v1/products/42     → Update only specific fields of product 42
DELETE /api/v1/products/42     → Delete product 42
```

### PUT vs PATCH — Common Confusion

```http
--- PUT (replace entire resource) ---
PUT /api/v1/users/5
{
  "firstName": "Alice",
  "lastName":  "Johnson",   ← MUST include even if unchanged
  "email":     "alice@example.com",
  "role":      "admin"      ← MUST include even if unchanged
}

--- PATCH (partial update) ---
PATCH /api/v1/users/5
{
  "email": "alice.new@example.com"   ← Only the changed field
}
```

> ⚠️ **Watch out:** If you use PUT and omit a field, many APIs will set it to null.
> Use PATCH when you only want to change specific fields.

### Other HTTP Methods (Awareness)

```
HEAD    — Like GET but returns ONLY headers. Used to check if a resource exists.
OPTIONS — Returns what methods the server supports for a resource. Used in CORS preflight.
CONNECT — Used to establish a tunnel (HTTPS through a proxy).
TRACE   — Diagnostic: echoes the request back to the client.
```

---

## Section 4 — HTTP Status Codes

Status codes tell the client **exactly what happened** on the server.
They are grouped into 5 classes by the first digit.

### 2xx — Success

```
200 OK           → Request succeeded. Body contains the result.
                   Use for: GET, PUT, PATCH responses.

201 Created      → A new resource was created successfully.
                   Use for: POST responses. Include Location header pointing to new resource.
                   Location: /api/v1/products/43

202 Accepted     → Request received but processing is asynchronous (not yet done).
                   Use for: long-running jobs, queued operations.

204 No Content   → Request succeeded but there is no body to return.
                   Use for: DELETE responses (nothing to return after deletion).
```

### 3xx — Redirection

```
301 Moved Permanently  → Resource has a new permanent URL.
                          Client should update bookmarks.

302 Found              → Temporary redirect. Client should use original URL next time.

304 Not Modified       → Cached version is still valid. No body returned.
                          Used with If-None-Match / ETag headers.
```

### 4xx — Client Errors (The client did something wrong)

```
400 Bad Request        → Malformed request syntax, invalid data, missing required field.
                          e.g., "price" is a string instead of a number.

401 Unauthorized       → Authentication is required and has failed or not been provided.
                          e.g., Missing or expired Bearer token.
                          (Confusingly named — this is about AUTHENTICATION, not authorization)

403 Forbidden          → Authenticated, but you don't have PERMISSION to do this.
                          e.g., A regular user trying to access an admin endpoint.

404 Not Found          → The resource does not exist at this URL.

405 Method Not Allowed → You used the wrong HTTP method.
                          e.g., POSTing to a read-only endpoint.

409 Conflict           → State conflict. Typically a duplicate resource.
                          e.g., Creating a user with an email that already exists.

422 Unprocessable Entity → Request is well-formed but semantically invalid.
                            e.g., "expiryDate" is before "startDate".

429 Too Many Requests  → Rate limit exceeded. Client is being throttled.
```

### 5xx — Server Errors (The server failed)

```
500 Internal Server Error  → Generic server-side failure. Something went wrong in your code.
                              Never expose stack traces in production.

501 Not Implemented        → The server doesn't support the requested method.

502 Bad Gateway            → A proxy received an invalid response from an upstream server.

503 Service Unavailable    → Server is down for maintenance or overloaded.

504 Gateway Timeout        → Upstream server timed out.
```

### Status Code Decision Tree

```
Did the request succeed?
  ├── Yes, created something   → 201
  ├── Yes, no body needed      → 204
  ├── Yes, with body           → 200
  └── No…
      ├── Client's fault?
      │   ├── Missing/bad auth  → 401
      │   ├── No permission     → 403
      │   ├── Not found         → 404
      │   ├── Bad data          → 400 or 422
      │   └── Duplicate         → 409
      └── Server's fault?
          ├── Code error        → 500
          └── Overloaded        → 503
```

---

## Section 5 — HTTP Headers

Headers carry **metadata** about the request or response. They are key-value pairs separated by a colon.

### Request Headers

```http
# Content negotiation
Accept: application/json
Accept-Language: en-US, en;q=0.9
Accept-Encoding: gzip, deflate, br

# Authentication
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0In0.abc
# OR Basic Auth (not recommended):
Authorization: Basic dXNlcm5hbWU6cGFzc3dvcmQ=

# Body format (required when sending a body)
Content-Type: application/json
Content-Length: 145

# Routing
Host: api.example.com

# Caching
If-None-Match: "abc123etag"
If-Modified-Since: Wed, 21 Oct 2025 07:28:00 GMT

# API Key (common alternative to Bearer)
X-API-Key: sk_live_abcdef123456

# Request tracking (custom header convention)
X-Request-ID: 550e8400-e29b-41d4-a716-446655440000
X-Correlation-ID: req-7f8d9e12
```

### Response Headers

```http
# Body metadata
Content-Type: application/json; charset=utf-8
Content-Length: 398
Content-Encoding: gzip

# CORS (Cross-Origin Resource Sharing)
Access-Control-Allow-Origin: https://myapp.com
Access-Control-Allow-Methods: GET, POST, PUT, DELETE
Access-Control-Allow-Headers: Authorization, Content-Type

# Caching
Cache-Control: no-cache, no-store, must-revalidate
ETag: "abc123etag"
Last-Modified: Mon, 23 Feb 2026 12:00:00 GMT

# Redirect
Location: /api/v1/products/43

# Rate limiting (common convention)
X-RateLimit-Limit: 1000
X-RateLimit-Remaining: 987
X-RateLimit-Reset: 1708700400

# Security headers (HTTPS only)
Strict-Transport-Security: max-age=31536000; includeSubDomains
```

### Content-Type Values You'll See Most

```
application/json          → JSON data (most common for REST APIs)
application/xml           → XML data (older APIs)
multipart/form-data       → File uploads
application/x-www-form-urlencoded → HTML form submissions
text/plain                → Plain text
text/html                 → HTML document
application/octet-stream  → Binary data / file downloads
```

---

## Section 6 — RESTful API Principles

REST (REpresentational State Transfer) is an **architectural style**, not a protocol.
Defined by Roy Fielding in his 2000 PhD dissertation.

### The 6 REST Constraints

```
1. CLIENT-SERVER SEPARATION
   ─────────────────────────
   The client and server are independent. The client doesn't know how data is stored.
   The server doesn't know how the UI is built.
   → Enables frontend and backend teams to evolve independently.

2. STATELESS
   ──────────
   The server stores NO client session state between requests.
   Every request must contain all information needed to process it.
   → Auth token must be sent with EVERY request.
   → Session state lives in the client (e.g., localStorage) or in a database.

3. CACHEABLE
   ──────────
   Responses must indicate whether they can be cached.
   GET responses are generally cacheable (use Cache-Control, ETag headers).
   POST/PUT/DELETE responses typically should NOT be cached.

4. UNIFORM INTERFACE
   ──────────────────
   Resources are identified by URIs.
   Resources are manipulated through representations (JSON, XML).
   Self-descriptive messages (Content-Type tells you how to parse the body).
   HATEOAS: responses include links to related actions (advanced — awareness only).

5. LAYERED SYSTEM
   ───────────────
   Client doesn't know if it's talking directly to the server or through a proxy,
   load balancer, or API gateway. The layers are transparent.

6. CODE ON DEMAND (optional)
   ─────────────────────────
   Server can send executable code to the client (e.g., JavaScript).
   Rarely used explicitly in REST API design.
```

### REST Best Practices

```
✅ Use nouns for resource names, not verbs
   /api/v1/orders           NOT  /api/v1/getOrders
   /api/v1/users/5/orders   NOT  /api/v1/getUserOrders?userId=5

✅ Use plural nouns for collection resources
   /api/v1/products         NOT  /api/v1/product
   /api/v1/categories       NOT  /api/v1/category

✅ Represent relationships with nested paths
   /api/v1/customers/7/orders        → Orders belonging to customer 7
   /api/v1/orders/15/items           → Items within order 15
   Avoid nesting deeper than 2 levels to prevent unwieldy URLs.

✅ Use lowercase letters and hyphens in URLs
   /api/v1/order-items      NOT  /api/v1/orderItems
   /api/v1/order-items      NOT  /api/v1/order_items (underscores are OK but hyphens preferred)

✅ Use HTTP methods correctly (don't encode actions in the URL)
   DELETE /api/v1/orders/15         NOT  POST /api/v1/orders/15/delete

✅ Return consistent error structures
   {
     "status": 400,
     "error": "Validation Failed",
     "message": "price must be a positive number",
     "timestamp": "2026-02-23T10:30:00Z",
     "path": "/api/v1/products"
   }

✅ Use appropriate status codes (see Section 4)

✅ Support filtering, sorting, and pagination on collection endpoints
   GET /api/v1/products?category=electronics&sortBy=price&order=asc&page=2&size=20

✅ Version your API (see Section 7)
```

---

## Section 7 — Resource Naming Conventions

### Collections vs Single Resources

```
Collection (returns list):
  GET  /api/v1/products           → All products (with pagination)
  POST /api/v1/products           → Create new product

Single Resource (returns one item):
  GET    /api/v1/products/42      → Get product 42
  PUT    /api/v1/products/42      → Replace product 42
  PATCH  /api/v1/products/42      → Update product 42 partially
  DELETE /api/v1/products/42      → Delete product 42

Nested Resource (sub-collection):
  GET  /api/v1/orders/15/items    → All items in order 15
  POST /api/v1/orders/15/items    → Add item to order 15
  GET  /api/v1/orders/15/items/3  → Specific item 3 in order 15
```

### Action Resources (When You Must Use a Verb)

Sometimes a resource maps to an action, not an entity. These are acceptable exceptions:

```
POST /api/v1/orders/15/cancel       → Cancel order 15 (not a CRUD operation)
POST /api/v1/users/forgot-password  → Trigger password reset flow
POST /api/v1/payments/refund        → Process a refund
POST /api/v1/auth/login             → Authenticate and return token
POST /api/v1/auth/logout            → Invalidate session/token
POST /api/v1/emails/send            → Send an email
```

> ✅ These are fine — but keep them as exceptions, not the norm.

### Query Parameters for Filtering, Sorting, Pagination

```
# Filtering
GET /api/v1/products?category=electronics
GET /api/v1/orders?status=pending&customerId=7

# Sorting
GET /api/v1/products?sortBy=price&order=asc
GET /api/v1/products?sortBy=name&order=desc

# Pagination
GET /api/v1/products?page=2&size=20
GET /api/v1/products?limit=10&offset=20

# Search
GET /api/v1/products?search=keyboard

# Combined
GET /api/v1/products?category=electronics&sortBy=price&order=asc&page=1&size=10
```

### Bad Naming Anti-Patterns

```
❌  /api/v1/getProduct/42          → Verb in the URL
❌  /api/v1/product_list           → Underscore + singular + action verb hybrid
❌  /api/v1/Products               → Uppercase
❌  /api/v1/deleteOrder?id=15      → Action in URL + query param for ID
❌  /api/v1/orders/15/items/3/delete   → Action in URL (use DELETE method instead)
❌  /api/v1/user/7/orders/15/items/3/notes/1   → Too deeply nested
```

---

## Section 8 — API Versioning Strategies

APIs evolve over time. Versioning allows you to make **breaking changes** without
breaking existing clients.

### Strategy 1 — URI Path Versioning (Most Common)

```
/api/v1/products
/api/v2/products

Example:
  GET https://api.myshop.com/api/v1/products   → Old behavior
  GET https://api.myshop.com/api/v2/products   → New behavior with breaking changes
```

**Pros:** Very explicit. Easy to test in browser. Easy to document.  
**Cons:** Pollutes the URL. Purists argue the URI should identify a resource, not a version.

### Strategy 2 — Header Versioning

```http
GET /api/products HTTP/1.1
Host: api.myshop.com
Accept-Version: v2
```

OR using a custom header:
```http
GET /api/products HTTP/1.1
Host: api.myshop.com
X-API-Version: 2
```

**Pros:** Keeps URIs clean. Follows REST principles more strictly.  
**Cons:** Not visible in the URL. Harder to test in a browser. Less discoverable.

### Strategy 3 — Accept Header (Content Negotiation)

```http
GET /api/products HTTP/1.1
Host: api.myshop.com
Accept: application/vnd.myshop.v2+json
```

**Pros:** Fully RESTful. Resources have one canonical URI.  
**Cons:** Complex. Hard to use without tooling. Rare in practice.

### Strategy 4 — Query Parameter Versioning

```
GET /api/products?version=2
GET /api/products?api_version=2
```

**Pros:** Easy to switch version per request.  
**Cons:** Not idiomatic. Not cacheable (query params affect caching).

### Industry Recommendation

```
For most APIs:        URI path versioning (/v1, /v2) — clear and explicit
For internal APIs:    Header versioning
For public APIs:      URI path versioning (developers can see it immediately)

Major providers:
  GitHub API:         Accept: application/vnd.github.v3+json  (header)
  Twitter API:        /2/tweets  (URI path)
  Stripe API:         /v1/charges  (URI path) + Stripe-Version header
  Twilio API:         /2010-04-01/Accounts/  (date-based URI)
```

### Version Lifecycle

```
v1   → Active (supported, new clients should use v2)
v2   → Current (latest stable, actively developed)
v3   → Beta / Preview (available but not yet stable)

Deprecation policy example:
  - Announce deprecation at least 6 months before sunset
  - Send deprecation headers in responses: Deprecation: Sun, 01 Jan 2027 00:00:00 GMT
  - Email registered developers before sunset
```

---

## Section 9 — Putting It Together: Full API Design Example

### Bookstore API — Well-Designed

```
Base URL: https://api.bookstore.com/api/v1

Resources:
  /books                      → Book collection
  /books/{bookId}             → Single book
  /books/{bookId}/reviews     → Reviews for a book
  /authors                    → Author collection
  /authors/{authorId}         → Single author
  /authors/{authorId}/books   → Books by a specific author
  /orders                     → Order collection
  /orders/{orderId}           → Single order
  /orders/{orderId}/items     → Items in an order
  /users                      → User collection
  /users/{userId}             → Single user
  /users/{userId}/wishlist    → User's wishlist

Endpoint Examples:
  GET  /books                         → List books (paginated)
  GET  /books?genre=fiction&page=1    → List fiction books
  POST /books                         → Create book
  GET  /books/978-0-385-54734-7      → Get book by ISBN
  PUT  /books/978-0-385-54734-7      → Replace book record
  PATCH /books/978-0-385-54734-7     → Update price only
  DELETE /books/978-0-385-54734-7    → Remove book

  GET  /books/978-0-385-54734-7/reviews         → All reviews for this book
  POST /books/978-0-385-54734-7/reviews         → Submit a review

Action endpoints (exceptional):
  POST /orders/{orderId}/cancel                  → Cancel an order
  POST /auth/login                               → Authenticate user
  POST /auth/refresh                             → Refresh access token

Response format:
  Collection: { "data": [...], "meta": { "total": 150, "page": 1, "size": 20 } }
  Single:     { "id": "...", "title": "...", ... }
  Error:      { "status": 404, "error": "Not Found", "message": "Book not found" }
```

---

## Summary — Part 1 Checklist

- [x] HTTP protocol overview — stateless, text-based, client-server
- [x] HTTP request/response lifecycle — DNS, TCP, request line, headers, body
- [x] HTTP methods — GET, POST, PUT, PATCH, DELETE (safe vs idempotent)
- [x] HTTP status codes — 2xx, 3xx, 4xx, 5xx with common codes and when to use each
- [x] HTTP headers — Content-Type, Authorization, Accept, Cache-Control, CORS, custom headers
- [x] RESTful API principles — all 6 constraints, best practices
- [x] Resource naming conventions — nouns, plurals, nesting, query params, anti-patterns
- [x] API versioning strategies — URI path, header, content negotiation, query param

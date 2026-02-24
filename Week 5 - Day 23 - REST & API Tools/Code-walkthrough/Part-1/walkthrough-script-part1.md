# Walkthrough Script — Part 1
# Day 23: REST & API Tools
# Estimated Time: ~90 minutes

---

## Segment 1 — Day 23 Opener (5 min)

Good morning, everyone! Today is a really important day because we're talking about **REST APIs and HTTP** — the foundation of everything we build in the back half of this course.

Every single Spring Boot endpoint you'll write in Week 6, every Postman test you'll run, every database call that goes over the network — all of it rides on top of what we're covering today.

By the end of today you'll be able to:
- Explain exactly what happens when a browser or app makes an HTTP request
- Design a REST API that follows industry best practices
- Test any API using Postman
- Read and write OpenAPI documentation

Let's open the file `Part-1/01-http-and-rest-principles.md`. This is a reference document — think of it as your REST cheat sheet. We're going to read through it together, but the things I highlight are the things that will come up in every interview and on every job.

---

## Segment 2 — HTTP Protocol Overview (8 min)

**[Point to Section 1]**

Let's start at the very beginning. HTTP stands for **HyperText Transfer Protocol**. It's the language that web browsers and servers use to talk to each other.

The most important characteristic you need to internalize is right here at the top of the table:

**Stateless.**

Every request is completely independent. The server does NOT remember that you sent a request 30 seconds ago. This is why — when you log into a website — you have to send your login credentials or a token on every single request. The server doesn't "remember" you're logged in.

> **Ask the class:** "If HTTP is stateless, how does Netflix know you're still logged in when you go from the home page to a show? Where does that state live?"

Give them a moment to think. The answer: the client stores a token (in a cookie or localStorage), and it sends that token with every request. The server validates the token each time.

**[Point to HTTP vs HTTPS]**

HTTPS is just HTTP with TLS encryption wrapped around it. In production, you will always use HTTPS. Everything we demo today will work over either, but remember: if you ever call an API with a password or token over plain HTTP, that data is visible to anyone on the network.

**[Point to HTTP Versions]**

Don't get lost in the version differences right now. Just know: HTTP/1.1 is what you'll work with day to day. HTTP/2 is faster but transparent — your code doesn't change.

---

## Segment 3 — HTTP Request/Response Lifecycle (12 min)

**[Point to Section 2 — the ASCII diagram]**

This diagram is the lifecycle of a single API call. Let me walk you through every step.

Step 1 is DNS resolution. You type `api.example.com` — but computers communicate with IP addresses, not names. DNS translates the hostname to an IP like `93.184.216.34`.

Step 2 is the TCP handshake. Before any data moves, the client and server do a three-way handshake: SYN, SYN-ACK, ACK. This establishes the connection. This is why the first request to a server has slightly higher latency.

**[Point to the Request box]**

Step 3: the actual HTTP request. Look at the structure:
- **Request Line** — the method, the path, and the protocol version. Everything the server needs to route this request.
- **Headers** — metadata about the request. The `Host` header tells the server which domain you're targeting (important when one server hosts multiple domains). `Authorization` carries the token. `Accept` says "I can handle JSON responses."
- **Body** — for GET requests, the body is empty. For POST/PUT/PATCH, the body carries the data you're sending.

**[Point to the Response box]**

Step 5: the server sends back a response. The structure mirrors the request:
- **Status Line** — the protocol, the code, and a human-readable reason phrase.
- **Response Headers** — metadata about the response. `Content-Type` is critical: it tells you how to parse the body.
- **Body** — the actual data.

**[Point to the Anatomy of an HTTP Request example]**

This is a real HTTP request. Let's read it line by line:
- `GET /api/v1/products/42?includeReviews=true HTTP/1.1` — We want product #42, and we've added a query parameter to also include reviews.
- `Host` — Required. Tells the server which domain to route to.
- `Authorization: Bearer ...` — The client is proving its identity with a JWT token.
- `Accept: application/json` — "Please send me JSON."

> **Ask the class:** "What do you think happens if you leave out the `Authorization` header on an endpoint that requires it? What status code should the server return?"

Answer: 401 Unauthorized. The client failed to authenticate.

**[Point to the Anatomy of an HTTP Response]**

`200 OK` — success. `Content-Type: application/json` — parse the body as JSON. `Content-Length: 187` — the body is 187 bytes. `Cache-Control: max-age=3600` — you can cache this for up to one hour.

> **Watch out:** Students often confuse headers with query parameters. Headers are invisible metadata — they don't appear in the URL. Query parameters appear in the URL after the `?`. They serve different purposes.

---

## Segment 4 — HTTP Methods (10 min)

**[Point to Section 3]**

HTTP methods are the **verbs** — they tell the server what you want to DO with a resource.

Look at this table. Let's go through each one:

**GET** — Retrieve data. GET requests should NEVER modify anything on the server. If I send a GET request 1,000 times to the same URL, the result should be the same every time, and nothing on the server should change.

**POST** — Create something new. Send the data in the body. Not idempotent — if you call it twice, you might create two records.

**PUT** — Replace entirely. You send the complete replacement object. If you omit a field, it might get set to null.

**PATCH** — Partial update. Only send the fields you want to change. This is the one you'll use most often when updating a single field.

**DELETE** — Remove. Returns 204 No Content (nothing to return after deletion).

**[Point to Safe vs Idempotent]**

Two important concepts:

**Safe** means the operation doesn't change server state. GET is safe. POST, PUT, PATCH, DELETE are NOT safe.

**Idempotent** means calling it multiple times has the same effect as calling it once. If I DELETE product 42, then DELETE product 42 again — the result is the same: product 42 is gone. GET, PUT, and DELETE are idempotent. POST is not.

> **Ask the class:** "Why does idempotency matter in a distributed system? Think about network failures."

Answer: If a request fails and you're not sure whether the server received it, you can safely retry idempotent operations without worrying about creating duplicates. This is critical for distributed systems.

**[Point to PUT vs PATCH example]**

This is the most common confusion point. Read the PUT example — you have to send the entire user object, even the fields you're not changing. With PATCH, you only send the email field. If you use PUT and forget to include a field, many APIs will set that field to null or empty. That's a real bug I've seen in production.

> **Watch out:** Some older APIs don't implement PATCH at all — they use POST for everything. When you join a team, always check the API documentation before assuming.

---

## Segment 5 — HTTP Status Codes (12 min)

**[Point to Section 4]**

Status codes are one of the most important things to know as an API developer, because they communicate intent. A well-designed API uses the right status code every time.

The groups:
- **2xx** — Something good happened
- **3xx** — Go look somewhere else
- **4xx** — YOU (the client) made a mistake
- **5xx** — WE (the server) made a mistake

This distinction between 4xx and 5xx is critical. When you're debugging, a 4xx means check your request. A 5xx means the server is broken.

**[Point to 2xx]**

`200 OK` — the workhorse. GET, PUT, PATCH responses.  
`201 Created` — always return this for successful POST requests. And include a `Location` header pointing to the new resource.  
`204 No Content` — the correct response for DELETE. "I deleted it, there's nothing to return."

**[Point to 4xx]**

`400 Bad Request` — invalid data. Maybe the client sent a string where a number was expected.  
`401 Unauthorized` — this is AUTHENTICATION. The client isn't logged in or their token expired.  
`403 Forbidden` — this is AUTHORIZATION. The client IS logged in, but doesn't have permission for this action.

> **Ask the class:** "What's the difference between 401 and 403? Give me a real-world scenario for each."

Answer: 401 — you walked into a building without a badge. You haven't proved who you are. 403 — you have a badge, you're identified as an employee, but you're trying to enter the server room that only IT admins can access.

`404 Not Found` — the resource doesn't exist. Common mistake: returning 404 when you should return 403 to hide the existence of a resource for security reasons.  
`409 Conflict` — duplicate. Someone tries to register with an email that's already in the system.  
`422 Unprocessable Entity` — the request is syntactically valid JSON, but the data makes no logical sense (e.g., checkout date before check-in date).  
`429 Too Many Requests` — rate limiting. The client is sending too many requests too fast.

**[Point to 5xx]**

`500 Internal Server Error` — something threw an exception that wasn't caught. In development you see this often. In production, this should trigger an alert.

> **Watch out:** Never expose a stack trace in a 500 error response in production. Stack traces reveal your framework, library versions, and code structure — all useful to an attacker.

**[Point to the Decision Tree]**

This decision tree is your cheat sheet. Before you write any endpoint, walk through this tree to decide what status code it should return.

---

## Segment 6 — HTTP Headers (8 min)

**[Point to Section 5]**

Headers are metadata — information about the request or response that travels alongside the data but isn't part of the data itself.

**[Point to Request Headers]**

`Content-Type: application/json` — REQUIRED when you're sending a body. Tells the server how to parse what you're sending. If you forget this when POSTing JSON, the server might reject it.

`Authorization: Bearer <token>` — This is how you send a JWT. The format is literally the word "Bearer", a space, then your token. Don't forget the space.

`Accept: application/json` — "I want JSON back." If the server can't respond in JSON, it should return 406 Not Acceptable.

`X-API-Key` — Custom header for API key authentication. The `X-` prefix was historically used for custom/experimental headers. Technically deprecated as a convention, but still widely used.

`X-Request-ID` and `X-Correlation-ID` — Custom headers for tracing requests through distributed systems. You'll see these extensively in microservices. We cover this in Week 8.

**[Point to Response Headers]**

`Content-Type` in the response — always check this when you get a response. If you expect JSON but the server returns `text/html`, something went wrong (often you're hitting a 404 HTML page from an nginx proxy instead of your API).

`Cache-Control: no-cache, no-store, must-revalidate` — "Do not cache this under any circumstances." Use this for sensitive endpoints like user profile data.

`X-RateLimit-*` — Many public APIs include these headers to tell you how close you are to your rate limit. Track these in your applications.

**[Point to Content-Type values]**

The two you'll use most: `application/json` for REST API bodies, and `multipart/form-data` for file uploads. When you get to Spring Boot, the framework reads `Content-Type` to know whether to parse the body as JSON.

---

## Segment 7 — RESTful API Principles (10 min)

**[Point to Section 6]**

REST is not a protocol — it's an architectural style. Roy Fielding defined it in his PhD dissertation in 2000. The 6 constraints are what make an API "RESTful." Let me highlight the two most important.

**Stateless** — we covered this. No server-side session. The client is responsible for sending its state (the auth token) on every request.

**Uniform Interface** — this is the core of REST. Resources are identified by URIs. You interact with them using standard HTTP methods. The response format is described by `Content-Type`. Every API should feel consistent and predictable.

**[Point to REST Best Practices]**

This list is what separates a professional API from a beginner API.

`/api/v1/orders` — **noun**, **plural**. Not `/getOrders`. The method (GET) already tells you the action. Don't repeat it in the URL.

`/api/v1/customers/7/orders` — Nested resource. Customer 7's orders. This is clean and readable.

Lowercase with hyphens — `/order-items`, not `/orderItems`. URL conventions follow kebab-case.

Consistent error structure — this is huge. When your API always returns errors in the same shape, frontend developers can write one error handler and it works everywhere.

> **Ask the class:** "What's wrong with this URL: `POST /api/v1/orders/15/delete`?"

Answer: Two things wrong — you're using POST instead of DELETE, and you're embedding the action in the URL. The correct URL is `DELETE /api/v1/orders/15`.

---

## Segment 8 — Resource Naming Conventions (7 min)

**[Point to Section 7]**

Collections vs single resources. This is the pattern you'll use for every resource:

```
GET  /products        → list
POST /products        → create
GET  /products/42     → single item
PUT  /products/42     → replace
PATCH /products/42    → update fields
DELETE /products/42   → remove
```

**[Point to Action Resources]**

Sometimes you genuinely need a verb in the URL — things that don't map cleanly to CRUD. Canceling an order, sending an email, processing a refund. The key is using POST and keeping these as exceptions. If you find yourself adding verbs all over the place, your resource design needs rethinking.

**[Point to Query Parameters section]**

Filtering, sorting, and pagination all go in query parameters — NOT in the path. The path identifies a resource; query parameters describe how you want it filtered or formatted.

**[Point to Anti-Patterns]**

Read through these quickly. The most common I see from junior developers: putting verbs in the URL. It feels natural because we're used to function names like `getUser()`, but REST URLs should be nouns with the verb implied by the HTTP method.

---

## Segment 9 — API Versioning Strategies (8 min)

**[Point to Section 8]**

Why do we version APIs? Because APIs need to evolve. You might need to change a field name, remove a field, or completely restructure a response. If you do that without versioning, you break every client that's already using your API.

**[Point to URI Path Versioning]**

This is the most common approach and what you'll use in this course. `/api/v1/products` vs `/api/v2/products`. Simple. Explicit. Easy to test in a browser.

**[Point to Header Versioning]**

Used by companies like GitHub. The version goes in the `Accept` header. Keeps URLs clean. But harder to test without tooling.

**[Point to Industry Recommendation]**

For public APIs — URI path versioning. Developers can see the version immediately in the URL. Stripe, Twitter, and most major REST APIs use this.

**[Point to Version Lifecycle]**

Even after you release v2, you don't immediately shut down v1. You announce deprecation months in advance. Many enterprise APIs support 2-3 versions simultaneously. Stripe has been on v1 for 15 years and adds backward-compatible changes rather than bumping to v2 — that's a deliberate design choice.

---

## Segment 10 — Putting It Together (5 min)

**[Point to Section 9 — Bookstore API]**

This is what a well-designed API looks like. Notice:
- Every resource is plural and lowercase
- Nested resources show relationships (`/books/{bookId}/reviews`)
- The base URL includes the version (`/api/v1`)
- Actions that don't fit CRUD are POST with verbs (`/orders/{orderId}/cancel`)
- The response format is consistent — collections always have `data` and `meta`, single items are just the object, errors always have `status`, `error`, and `message`

> **Ask the class:** "If you were designing an API for a ride-sharing app, what would be your top-level resources?"

Let them brainstorm: drivers, riders, trips, vehicles, ratings, payments. Then show how they'd nest: `/trips/{tripId}/rating`, `/drivers/{driverId}/vehicle`.

---

## Segment 11 — Part 1 Wrap-Up (5 min)

Let's take a quick breather. Here's what we've covered this morning:

- HTTP is the protocol underneath every REST API — stateless, text-based, request-response
- HTTP methods tell the server WHAT to do (GET/POST/PUT/PATCH/DELETE)
- Status codes tell the client WHAT HAPPENED (2xx success, 4xx client error, 5xx server error)
- Headers carry metadata — Content-Type, Authorization, caching, CORS
- REST is an architectural style with 6 constraints — stateless and uniform interface are the most important
- URL design: nouns, plural, lowercase, hyphens, nest up to 2 levels, query params for filtering
- Versioning: URI path versioning is the industry default

After the break, we're moving into the tools side. You're going to use Postman to actually CALL some APIs and see these concepts in action. Then we'll look at Swagger/OpenAPI — how you write documentation that machines can read.

Any questions on Part 1 before we move on?

---

## Q&A Prompts

1. "What's the difference between `401 Unauthorized` and `403 Forbidden`? When would you return each?"
2. "Why is POST NOT idempotent? Why does that matter when building a retry mechanism?"
3. "If your team is building a new public API today, which versioning strategy would you recommend and why?"
4. "What is the correct HTTP method and status code for: a user tries to create an account with an email that already exists?"
   *(Answer: POST → 409 Conflict)*
5. "Why should you never expose a stack trace in a 500 error response in production?"

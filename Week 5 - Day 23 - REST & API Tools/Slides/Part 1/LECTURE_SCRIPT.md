# Day 23 Part 1 — HTTP Protocol, REST Principles & API Design
## Lecture Script — 60 Minutes

---

### [00:00–02:00] Opening

Good morning, everyone! Today we hit a major milestone — we're leaving the database world behind and stepping into the territory where everything connects. Today is REST and API tools. After today, you will understand the language that every application on the internet speaks. You'll be able to look at a request flying across the wire and know exactly what every line means. By the time we get to Spring Boot in Week 5 and Spring MVC in Week 6, you'll already understand the protocol layer underneath — the framework will feel like a thin shell over concepts you already own.

Let me give you the big picture for today. Part 1 is all about the foundation: how HTTP works, what a request and response look like, the five HTTP methods you'll use every single day, status codes, headers, and then REST design principles — how to design an API that actually makes sense. Part 2 this afternoon shifts to tooling — Postman for testing APIs, and Swagger slash OpenAPI for documenting them.

We're going to build all our examples around a Book Store API. One fictional API, end to end. You'll see how every single concept maps to real requests and responses in that system. Let's go.

---

### [02:00–08:00] Slide 2 — HTTP Protocol Overview

Before we write a single endpoint, you need to understand what HTTP is at the wire level. HTTP stands for HyperText Transfer Protocol. It is the foundation of every REST API you'll ever build.

Here are the four properties that define HTTP. First: stateless. Every single request is completely independent. The server has zero memory of previous requests. Request number one and request number two are strangers to each other. If you need context — authentication, preferences, session data — the client has to send it with every request. This is not a bug. This is a feature. It's what makes REST APIs horizontally scalable. Any server in a cluster can handle any request because no request depends on a conversation history. We'll come back to this.

Second: request-response. The client always initiates. Always. The server never pushes data unless explicitly asked — unless you use something like WebSockets or Server-Sent Events, which we'll touch on later in the course, but REST is always request-response. Client asks, server answers.

Third: text-based. HTTP messages are human-readable text. That's why you can look at a raw request in Postman or in your terminal and understand it without a decoder ring. The headers are plain key-value pairs, the status line is plain English. This makes debugging HTTP infinitely easier than binary protocols.

Fourth: it runs over TCP. TCP gives us reliability — packets arrive, in order, guaranteed. HTTP/1.1, which is still incredibly common, opens one or more TCP connections per client. HTTP/2 introduced multiplexing, which lets multiple requests fly over a single connection simultaneously, much faster. HTTP/3 shifts to QUIC over UDP and is designed for mobile networks. But here's the thing — as a developer building REST APIs, HTTP version is mostly transparent to you. The protocol behaves the same way regardless. You write the same endpoints, you get the same requests, you return the same responses.

That diagram in the middle of the slide shows the fundamental exchange. Client sends a request — method, URL, headers, optional body. Server returns a response — status code, headers, optional body. That's it. That's all of HTTP. Everything else is just filling in the details of those two messages. Let's fill them in.

---

### [08:00–16:00] Slides 3 & 4 — Request and Response Anatomy

Let's dissect a real HTTP request. I want you to see this raw, not through a framework, not through Spring, just the actual text that travels over the wire.

Look at the GET request on the slide. Line one is the request line. It says `GET /api/v1/books?genre=fiction&limit=10 HTTP/1.1`. Three things: the method, which is `GET`. The path, which is `/api/v1/books`. And the query string — everything after the question mark — `genre=fiction&limit=10`. The `HTTP/1.1` at the end declares the protocol version. That's your request line.

Then come the headers. `Host: api.bookstore.com` — required in HTTP/1.1, tells the server which domain you're talking to. `Accept: application/json` — this tells the server "I want the response in JSON format." `Authorization: Bearer eyJ...` — we'll discuss auth headers more in a moment. `User-Agent` — identifies the client software. Then a blank line. That blank line is the separator between headers and body. GET requests have no body, so after the blank line there's nothing.

Now the POST request. Same structure — request line, headers, blank line — but this time there's a body. The `Content-Type: application/json` header tells the server "I'm sending you JSON." The `Content-Length` header tells the server how many bytes the body contains. Then the blank line. Then the JSON body — the actual data for the new book.

Two things I want you to notice. One: headers are just key-value pairs. There's nothing magic about them. They're text. Two: the body is completely separate from the headers, separated by that blank line. If you forget `Content-Type` and send a JSON body, the server might reject it or misparse it because it doesn't know what you sent.

Now the response. The status line is the first thing the server sends back. `HTTP/1.1 201 Created`. That `201` is the status code — we're dedicating a whole slide to codes in a moment. Then response headers — `Content-Type` tells you the format of the body, `Location` tells you the URL of the newly created resource, which is a very important convention we'll see again. Then the blank line. Then the body — the created resource with its new `id` and `createdAt` timestamp.

And the error response — `HTTP/1.1 404 Not Found`. Headers are the same pattern. The body is a structured JSON error object. Notice it has `error`, `message`, `path`, and `timestamp`. This is the error response structure we'll standardize on. It's not part of the HTTP spec, it's a convention — but it's a good one, and it's what Spring Boot gives you out of the box. Clients can parse this reliably, they know exactly where to find the error message, they know the path that failed, they can log the timestamp.

This right here — request anatomy and response anatomy — is the mental model everything else builds on. When you're debugging an API call in three months, you'll come back to this. Where's my Content-Type? What's in my body? What did the status line say?

---

### [16:00–26:00] Slide 5 — HTTP Methods

Alright, the five HTTP methods you'll use constantly. I want to be very precise about each one because developers get these wrong all the time.

`GET` is for reading. It retrieves data. It has no body — you send parameters either in the URL path, like `/books/42`, or in the query string, like `?genre=fiction`. GET is both safe and idempotent. I need to define those terms.

Safe means the method does not modify server state. A GET request only reads. You can call it a thousand times and the server is unchanged. Idempotent means calling the method multiple times produces the same result as calling it once. Calling `GET /books/42` once or ten times — same book comes back, server state unchanged either way. Because GET is safe, browsers cache GET responses, CDNs cache GET responses. Never use GET for anything that causes side effects. Never write `GET /deleteBook/42`. That is an anti-pattern we'll see on the anti-patterns slide.

`POST` is for creating. It has a request body. It is neither safe — it definitely changes state — nor idempotent. If you call `POST /books` three times with the same body, you get three books. This is why payment processing has to be so careful about duplicate requests. POST is also used for "actions" that don't map cleanly to CRUD, like `POST /books/42/publish` to trigger a publishing workflow.

`PUT` is for full replacement. You're sending the complete representation of a resource. The server replaces whatever is stored at that URL with exactly what you sent. It is idempotent — calling `PUT /books/42` three times with the same full body leaves the book in the same state. The critical implication: if you omit a field in a PUT request, that field becomes null or missing. PUT says "the resource is now exactly what I sent you." This is why you see PUT used less often in practice — clients have to fetch the full resource first, modify what they want, and send it all back.

`PATCH` is for partial updates. You send only the fields you want to change. Everything else stays as-is. This is what most clients actually want — "just change the price, leave everything else alone." PATCH is generally considered idempotent when the operation is a simple field update, though theoretically some PATCH operations could be non-idempotent (like "increment price by 10%"). For all practical purposes in what we build, treat it as idempotent.

`DELETE` removes the resource. It's idempotent — calling `DELETE /books/42` once deletes the book. Calling it again should return 404 (book gone) — additional harm done? None. Some teams return 200 with the deleted resource body, some return 204 No Content, some return 404 on the second call. Any of these is defensible. I prefer `204 No Content` for a successful first delete.

Now let me drive home the `PUT` versus `PATCH` distinction with the example at the bottom of the slide because this trips up junior developers constantly. Current book state: title is "Clean Code", price is 39.99, genre is "technology". If you `PUT /books/42` and only send title and price, leaving out genre, you have now erased the genre. The database row for genre is gone. PUT says "I am describing the entire resource." If you `PATCH /books/42` and only send price, title and genre are untouched. PATCH says "here's what changed." Know which one your API is doing. Know which one your client should call.

---

### [26:00–34:00] Slide 6 — HTTP Status Codes

Status codes. This is where so many APIs are built incorrectly, and it makes life miserable for every developer who has to consume them. Let me give you the mental model and then the specific codes.

Five ranges. 1xx is informational — you'll almost never deal with these. 2xx means success — the request worked. 3xx means redirection — the client needs to go somewhere else. 4xx means client error — the client sent something wrong. 5xx means server error — the server failed.

Here's the most important thing I'll say about status codes: 4xx versus 5xx is not just a categorization, it has operational implications. A 4xx tells your monitoring system "the client did something wrong." A 5xx tells your monitoring system "you need to wake someone up at 3am." You never want 5xx errors. 4xx errors are the client's problem. 5xx errors are your problem.

Let me go through the ones you'll actually use. `200 OK` is your workhorse — it's the default success for GET, PUT, PATCH. `201 Created` — POST requests that create a resource should return 201, not 200. This distinction matters. It tells the client "not only did I succeed, I made something new." The `Location` header should always accompany a 201 — it tells the client where the new thing lives. `204 No Content` — use this for DELETE. The operation succeeded but there's nothing to return. Also for PUT or PATCH when you've decided not to return the updated resource.

`400 Bad Request` — the client sent malformed data. Could be invalid JSON, could be a missing required field, could be a field with the wrong type. Return this with a descriptive error body telling the client exactly what's wrong. `401 Unauthorized` — and here's a naming irony that confuses everyone — 401 doesn't mean unauthorized, it means unauthenticated. The client didn't provide valid credentials. This is the "who are you?" response. `403 Forbidden` — this one actually means unauthorized. The client is authenticated, we know who they are, but they're not allowed to do this. "I know who you are and the answer is no." `404 Not Found` — the resource doesn't exist. Wrong ID, wrong URL. `405 Method Not Allowed` — you sent a DELETE to a read-only endpoint, or a POST to a singleton resource. `409 Conflict` — there's a state conflict. You're trying to create a book that already exists with that ISBN, or two clients are trying to update the same record simultaneously. `422 Unprocessable Entity` — the JSON is syntactically valid, but the business logic says no. Maybe the price is negative, maybe the genre is invalid, maybe the author ID doesn't exist. Use 400 for structural problems, 422 for semantic problems. `429 Too Many Requests` — rate limiting. The client has exceeded their request quota. Always return a `Retry-After` header telling them when to try again.

`500 Internal Server Error` — your code threw an exception. This is what Spring Boot returns when you have an unhandled exception. In production, you never want to leak stack traces here — return a clean error body, log the stack trace internally. `503 Service Unavailable` — the server is temporarily down for maintenance or overwhelmed. Return a `Retry-After` header.

One more thing: `304 Not Modified`. When a client has a cached response and sends `If-None-Match` or `If-Modified-Since`, if nothing changed the server returns 304 with no body. The client uses its cached version. This is how ETags and cache validation work.

---

### [34:00–42:00] Slides 7 & 8 — HTTP Headers and REST Constraints

Let's talk about headers. Headers are metadata — they describe the request or response without being the content itself. You can think of them like the envelope around a letter. The body is the letter, the headers are everything written on the envelope.

The most important request header is `Content-Type`. It tells the server what format your body is in. If you're sending JSON, `Content-Type: application/json`. If you're sending a form, `application/x-www-form-urlencoded`. If you're uploading a file, `multipart/form-data`. The server uses this to know how to parse the body. Get this wrong and the server will either reject your request or misparse your data.

`Accept` is the companion — it tells the server what format you want back. `Accept: application/json` means "please respond in JSON." The server uses this to choose the response format. If the server can't satisfy your Accept header, it returns `406 Not Acceptable`. In practice, most REST APIs only speak JSON, so `Accept: application/json` is the standard.

`Authorization` carries authentication credentials. The most common pattern you'll use in this course is Bearer token: `Authorization: Bearer <jwt_token>`. The word "Bearer" means "the bearer of this token." The JWT is a signed token the server issued during login. The server validates it on every request. We're not going to implement this today — Days 29 and 30 cover JWT and Spring Security in depth — but you'll see this header in Postman constantly.

Response headers to know: `Location` on 201 responses — always include it. `Cache-Control` controls how long clients and intermediaries can cache the response. `ETag` is a fingerprint of the response — if the resource hasn't changed, the fingerprint hasn't changed. `X-Request-Id` is a trace ID that lets you correlate a client request with a specific log line on the server. This is invaluable in production debugging.

Custom headers by convention use an `X-` prefix — `X-Request-Id`, `X-API-Key`, `X-Correlation-Id`. Note that RFC 6648 deprecated the `X-` prefix recommendation in 2012, but it's still widely used and you'll see it everywhere.

Now REST constraints. REST is an architectural style defined by six constraints. It's not a protocol, not a specification — it's a set of design decisions. If you follow the constraints, you get an API that is scalable, cacheable, and understandable.

The two that matter most: Stateless and Uniform Interface. Stateless means the server stores no session state between requests. Every request is self-contained. The implication: you can't rely on the server to remember login state — that's why we send the auth token on every single request. Uniform Interface means you use standard HTTP methods, standard status codes, and resources are identified by URLs. Every developer in the world knows what `GET /books/42` means. That universality is the power of REST.

Client-Server separation means the frontend and backend evolve independently. Your React or Angular app doesn't care whether the backend is Spring Boot or Node.js or Go. It just speaks HTTP. Cacheable means responses carry enough information for clients to know if they can cache them. Layered system means you can put a load balancer, a CDN, an API gateway, or a caching proxy between client and server — the client doesn't know or care. Code on Demand is optional — it means servers can send executable code to clients, like JavaScript sent to a browser. Most REST APIs don't use this.

---

### [42:00–50:00] Slides 9 & 10 — Resource Naming and Best Practices

Resource naming. This is where you really design your API. And there are patterns that are right, and patterns that are going to make every developer who uses your API want to find you.

Core principle: URLs identify resources, HTTP methods describe the action. Resources are nouns. Methods are verbs. That means no verbs in your URLs. Not `/getBooks`. Not `/createBook`. Not `/deleteBook`. Just `/books`, and the method tells you what's happening to books.

Use plural nouns. Not `/book/42`, use `/books/42`. This is overwhelmingly the convention and it reads naturally — "give me the books collection, give me books item 42."

Use lowercase, separate words with hyphens. `book-authors`, not `bookAuthors`, not `book_authors`, not `BookAuthors`. Lowercase hyphenated is the REST community standard.

Nest for relationships. If you want the books by a specific author, `/authors/5/books` is better than `/books?authorId=5`. Both work, but the nested path makes the relationship explicit and the endpoint is self-documenting. Keep nesting to one or two levels — `/authors/5/books/42` is fine, `/authors/5/books/42/reviews/7/comments/3` is getting hard to understand.

Use query parameters for filters, sorting, and pagination. Not new URL paths. `GET /books?genre=fiction` not `GET /fiction-books`. `GET /books?sort=price&order=asc`. `GET /books?page=2&size=20` for pagination.

The Book Store API URL structure on the slide is the pattern you'll implement in Spring MVC on Day 26. Study it now. The URL design decisions you make up front are very hard to change once clients are using the API.

Best practices quickly: always return the created resource in POST responses — the client needs the new ID without making a second request. Use 204 No Content for DELETE — there's nothing to return, no body needed. Always include a Location header on 201 responses. Validate input, return 400 with field-level detail so clients know exactly what to fix. Never ever return 200 with an error body — "success: false" inside a 200 response is a trap that breaks every client expecting standard status codes. And always set Content-Type on your responses.

---

### [50:00–58:00] Slides 11–15 — Versioning, Auth, and Anti-Patterns

API versioning. You will need it eventually. The question is which strategy to use.

URI path versioning is `/api/v1/books`, `/api/v2/books`. It's visible in the URL, easy to test in a browser, easy to trace in logs. It's the most common approach by a wide margin. This is what you'll use in this course. Start with `v1` from day one — it's much harder to add versioning retroactively.

Query parameter versioning is `?version=1`. Simple but easy to forget, harder to cache. Header versioning uses a custom header like `X-API-Version: 2`. Clean URIs but can't test in a browser. Media type versioning uses `Accept: application/vnd.bookstore.v2+json`. The most theoretically pure REST approach — the version is in the content type, not the URL — but the most painful for clients to implement.

Use URI versioning. When you make a breaking change — you rename a field, you change a response structure, you remove an endpoint — increment the version. But first ask: is this change backward-compatible? Adding a new field to a response is backward-compatible. Removing a field is a breaking change. Renaming a field is a breaking change. If you can make additive changes, you don't need a new version.

The idempotency and safety deep dive — this is important for understanding why POST needs special care. POST is the only core method that's neither safe nor idempotent. Calling `POST /books` three times creates three books. Calling `DELETE /books/42` three times deletes the book once and then harmlessly returns 404. Calling `PUT /books/42` three times with the same body — same state each time, no extra harm. Payment APIs solve the POST problem with idempotency keys. You include a unique `Idempotency-Key` header. The server stores the response for that key and returns the same response if you retry. Stripe does this. We won't implement it in this course, but you should know it exists.

Authentication in HTTP: Bearer token is the pattern you'll use. `Authorization: Bearer <jwt>` on every request. The server validates the JWT on every request — no session, perfectly stateless. Days 29 and 30 cover the JWT structure and Spring Security implementation. Today you just need to know the header format because you'll use it in Postman.

Anti-patterns — let me go through the list fast. Verbs in URLs — no. Using GET for mutations — absolutely not, ever, for any reason. A spider, a monitoring tool, or a browser prefetching GET links will delete your data. Returning 200 for errors — no, use the right code. No versioning strategy — start with v1 now. Inconsistent naming — pick lowercase-hyphen and stick to it. Returning HTML error pages for a JSON API — Spring Boot's default is to return HTML for unhandled errors; we'll configure it to return JSON in Week 6.

---

### [58:00–60:00] Slide 16 — Part 1 Wrap-Up

We just covered the entire foundation of HTTP and REST API design. HTTP request: method, URL, headers, body. HTTP response: status code, headers, body. Five methods: GET reads, POST creates, PUT replaces, PATCH updates, DELETE removes. Status codes: 2xx success, 4xx client error, 5xx server error. REST: stateless, uniform interface, nouns in URLs, methods as verbs, query params for filters, URI versioning.

Take a ten minute break. When we come back: Postman — the tool you will live in for the rest of this course. You'll be making HTTP requests to APIs you haven't built yet, and Postman is how you do that. Then Swagger and OpenAPI — how to describe your API in a machine-readable format that generates documentation automatically. See you in ten.

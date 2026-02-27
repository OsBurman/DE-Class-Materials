# Presentation 1 — HTTP, REST, Postman & Error Handling
## Full Script with Numbered Slides
**Audience:** Students with Java & JavaScript background
**Estimated Time:** ~90 minutes

---

## SLIDE 1 — Title Slide
**Content:**
- Title: "APIs in Practice: HTTP, REST, Postman & Error Handling"
- Subtitle: "The foundation every developer needs"
- Your name | Date | Course name

---

## SLIDE 2 — What We're Covering Today
**Content:**
- Agenda list with icons:
  - 🌐 HTTP Protocol & How the Web Communicates
  - 📋 HTTP Methods, Status Codes & Headers
  - 🏗️ RESTful API Design Principles
  - 🧪 Testing APIs with Postman
  - ❌ Error Handling & Exception Patterns

**SCRIPT:**
"Here's the roadmap for today. Everything on this slide feeds into the next — HTTP is the foundation, REST is the design philosophy built on top of HTTP, Postman is the tool we use to interact with and test APIs, and error handling is what makes those APIs actually usable in the real world. By the end you'll have the mental model to build, test, and reason about any REST API you encounter."

---

---

# SECTION 1 — HTTP PROTOCOL
## ⏱ ~15 minutes

---

## SLIDE 3 — What Is HTTP?
**Content:**
- Large heading: "HyperText Transfer Protocol"
- Simple diagram: Browser/Client → arrow labeled "HTTP Request" → Server → arrow labeled "HTTP Response" → back to client
- Three bullet points below:
  - Stateless — no memory between requests
  - Text-based — human readable
  - Built on TCP/IP — reliable delivery guaranteed
- Bottom callout box: "HTTPS = HTTP + TLS Encryption"

**SCRIPT:**
"HTTP stands for HyperText Transfer Protocol. It is the language that clients and servers use to talk to each other over the web. When your browser loads a page, when your JavaScript code fetches data, when your mobile app loads a feed — HTTP is what's happening underneath all of it.

The single most important characteristic to understand right now is that HTTP is **stateless**. Every single request is completely independent. The server has zero memory of any previous request you made. There is no connection that persists. Each call is a fresh conversation that starts from zero. Everything you need the server to know — who you are, what you want, what format you want it in — must be sent with every single request.

HTTPS is simply HTTP with an added security layer called TLS. It encrypts the traffic in transit so nobody between you and the server can read it. In any real-world API, HTTPS is not optional. It's the baseline."

---

## SLIDE 4 — HTTP vs HTTPS — Why It Matters
**Content:**
- Two-column comparison:
  - Left (HTTP): Unencrypted | Data sent in plain text | Anyone on the network can read it | Fine for local development
  - Right (HTTPS): Encrypted with TLS | Data unreadable in transit | Certificate validates server identity | Required in production
- Bottom note: "Your tokens, passwords, and user data travel in the body — always encrypt them"

**SCRIPT:**
"I want to spend one extra moment on this because it's a mistake junior developers make in the real world. HTTP sends everything as plain text. If you're calling an API over HTTP and you're putting a JWT token or a password in a header or body, anyone on the same network can intercept and read that. Coffee shop WiFi, a compromised router, a monitoring tool — it doesn't matter. Plain text is readable.

HTTPS solves this entirely. The TLS certificate also proves the server is who it claims to be, protecting against impersonation attacks. When you build APIs, use HTTPS from staging onwards — not just production."

---

## SLIDE 5 — The HTTP Request Anatomy
**Content:**
- Styled code block showing a raw HTTP request with each line labeled:
  ```
  GET /api/users/42 HTTP/1.1          ← Request Line (Method | Path | Version)
  Host: api.example.com               ← Required header — identifies the server
  Authorization: Bearer eyJhbGci...   ← Auth credential
  Content-Type: application/json      ← Format of the body being sent
  Accept: application/json            ← Format we want back
                                      ← Blank line separates headers from body
  { "example": "body goes here" }     ← Body (only for POST, PUT, PATCH)
  ```

**SCRIPT:**
"Let's look at what a raw HTTP request actually looks like. This is what travels over the wire every time your code makes an API call.

The first line is the **request line**. Three pieces: the method (what you want to do), the path (what resource you're acting on), and the HTTP version.

Everything below that until the blank line is **headers**. Headers are key-value pairs that carry metadata about the request. They tell the server things like: who you are, what format you're sending data in, what format you want back, and what language you prefer.

After the blank line comes the **body** — the actual data payload. Not all requests have a body. GET and DELETE typically don't. POST, PUT, and PATCH do.

Think of it like mailing a package. The headers are the shipping label — destination, sender, contents type. The body is what's inside the box."

---

## SLIDE 6 — The HTTP Response Anatomy
**Content:**
- Styled code block showing a raw HTTP response with each line labeled:
  ```
  HTTP/1.1 200 OK                     ← Status Line (Version | Code | Reason)
  Content-Type: application/json      ← Format of the body being returned
  Content-Length: 82                  ← Size of the body in bytes
  Location: /api/users/42             ← Where the new resource lives (after 201)
                                      ← Blank line
  {                                   ← Response body
    "id": 42,
    "name": "Jane Doe",
    "email": "jane@example.com"
  }
  ```

**SCRIPT:**
"The response mirrors the structure of the request. You get a status line first — the HTTP version and the status code. We'll spend a lot of time on status codes shortly because they carry important meaning.

Then response headers from the server, giving you metadata about what's coming back — the content type, the size, and in the case of a newly created resource, the `Location` header pointing you to where that resource lives.

Then the blank line, and then the body — almost always JSON for modern APIs.

This request-response pair is the entire lifecycle of an API call. You send a request, you get a response. Everything we learn today builds on top of this pattern."

---

## SLIDE 7 — The Full Request/Response Lifecycle
**Content:**
- Flowchart / numbered steps:
  1. Client builds HTTP request (method + URL + headers + body)
  2. DNS resolves hostname to IP address
  3. TCP connection established (TLS handshake if HTTPS)
  4. Request travels to server
  5. Server receives and routes the request (to the right controller/handler)
  6. Server processes logic (database, validation, etc.)
  7. Server builds HTTP response (status + headers + body)
  8. Response travels back to client
  9. Client reads status code and parses body
  10. TCP connection closed (or kept alive for reuse)

**SCRIPT:**
"Let's zoom out and trace what actually happens from the moment you fire an API call to the moment your code gets a response back.

First, your code builds the request. Then the OS looks up the IP address for the hostname via DNS. Then a TCP connection is established — if it's HTTPS, there's a TLS handshake in there too. The request travels across the network to the server.

On the server, a router looks at the URL and method and hands the request to the right handler — in Spring Boot that would be the matching `@GetMapping` method, in Express it would be the matching `app.get()` route.

The handler processes the request — maybe it hits a database, runs some validation logic, calls another service. Then it builds a response with a status code, some headers, and a body, and sends it back.

Your code receives the response, reads the status code to know if it succeeded or failed, and parses the body to get the data.

This whole round trip for a typical API call on a fast network is somewhere between 20 and 200 milliseconds. That's the entire lifecycle."

---

## SLIDE 8 — HTTP Methods Overview
**Content:**
- Title: "HTTP Methods — The Verbs of the Web"
- Table:
  | Method | Purpose | Has Body? | Safe? | Idempotent? |
  |--------|---------|-----------|-------|-------------|
  | GET | Read a resource | No | ✅ | ✅ |
  | POST | Create a resource | Yes | ❌ | ❌ |
  | PUT | Replace a resource entirely | Yes | ❌ | ✅ |
  | PATCH | Partially update a resource | Yes | ❌ | ⚠️ |
  | DELETE | Remove a resource | Optional | ❌ | ✅ |
- Footer note: "Safe = no side effects on the server | Idempotent = same result no matter how many times called"

**SCRIPT:**
"HTTP methods — also called HTTP verbs — communicate your *intent* to the server. Not just what resource you're targeting but what you want to *do* to it.

I want to draw your attention to the two rightmost columns: **Safe** and **Idempotent**.

Safe means the operation has no side effects. GET is safe — calling GET a thousand times changes nothing on the server. It's purely reading. This matters because browsers, proxies, and caches can freely retry safe requests without worrying about breaking anything.

Idempotent means calling the operation multiple times produces the same result as calling it once. PUT is idempotent — if you replace a user with the same data a hundred times, the user is still in the same state. DELETE is idempotent — deleting something that's already deleted still results in it not existing (you might get a 404 on the second call, but the server state is the same).

POST is neither safe nor idempotent. Calling POST twice creates two records. This matters in error recovery — if your POST request times out, you cannot safely retry it automatically."

---

## SLIDE 9 — GET & POST In Depth
**Content:**
- Two side-by-side panels:
  - **GET:**
    - Use for: reading, listing, searching
    - Never use for: creating, updating, deleting
    - Parameters go in the URL query string
    - Should be cacheable
    - Example: `GET /api/products?category=electronics&sort=price`
  - **POST:**
    - Use for: creating new resources
    - Body contains the new resource data (JSON)
    - Returns 201 Created + Location header on success
    - Not idempotent — don't retry blindly
    - Example: `POST /api/products` with body `{ "name": "Laptop", "price": 999 }`

**SCRIPT:**
"Let's go deeper on each method. GET is your read operation. Never use GET to create or modify data — GET requests can be cached, pre-fetched, and retried freely, which would be catastrophic if they had side effects. Query parameters on GET requests are for filtering, sorting, searching, and pagination — things that narrow down *what* you're reading but don't change the underlying data.

POST is your create operation. You send a body containing the representation of the new resource. The server creates it, assigns it an ID, and ideally returns a 201 status with a Location header telling you where the new resource now lives. POST is not idempotent — if you POST the same request twice, you could end up with two identical records. Always handle this carefully in your error recovery logic."

---

## SLIDE 10 — PUT & PATCH In Depth
**Content:**
- Two side-by-side panels:
  - **PUT:**
    - Full replacement — send the entire resource
    - If a field is omitted, it gets cleared/nulled
    - Idempotent — safe to retry
    - Use when you always have the full object available
    - Example: `PUT /api/users/42` with complete user object
  - **PATCH:**
    - Partial update — send only changed fields
    - Unmentioned fields are untouched
    - More efficient for large objects
    - Example: `PATCH /api/users/42` with `{ "email": "newemail@example.com" }`
- Common mistake callout: "PUT without all fields = data loss"

**SCRIPT:**
"PUT and PATCH are the two update methods and they're frequently confused.

PUT is a **full replacement**. You send the complete, new version of the resource. If the user object has ten fields and you only send five in your PUT request, the other five are gone. This is a really common source of bugs — a developer fetches a user, changes one field, and calls PUT with only the changed data, accidentally wiping out all the other fields.

PATCH solves this. PATCH says 'here are the specific fields I want to change — leave everything else alone.' If you only need to update a user's email, you PATCH with just `{ "email": "newemail@example.com" }`. Everything else is untouched.

In practice, most modern APIs use PATCH far more often than PUT because it's safer and more efficient."

---

## SLIDE 11 — DELETE & Other Methods
**Content:**
- **DELETE:**
  - Removes the resource at the given URL
  - Returns 204 No Content on success (no body needed)
  - Returns 404 if resource doesn't exist
  - Idempotent — deleting an already-deleted resource still results in "not there"
- **Other methods you may encounter:**
  - `HEAD` — like GET but returns only headers, no body (good for checking if resource exists)
  - `OPTIONS` — asks what methods are allowed on a resource (used in CORS preflight)

**SCRIPT:**
"DELETE is straightforward — remove the resource. Return 204 No Content to confirm success. There's nothing to send back in the body — the resource no longer exists.

You'll also encounter HEAD and OPTIONS occasionally. HEAD is useful when you want to check if a resource exists or get its metadata without downloading the full body. OPTIONS is used automatically by browsers in CORS preflight requests — it asks the server 'what HTTP methods are you willing to accept on this endpoint from this origin?' You'll deal with OPTIONS when you start building APIs that serve frontend applications from different domains."

---

## SLIDE 12 — HTTP Status Code Groups
**Content:**
- Five large colored blocks:
  - 🔵 **1xx — Informational** — Request received, processing continues (rarely seen in APIs)
  - 🟢 **2xx — Success** — Request received, understood, and accepted ✅
  - 🟡 **3xx — Redirection** — Further action needed to complete the request 🔄
  - 🔴 **4xx — Client Error** — The request contains bad syntax or cannot be fulfilled ❌
  - 💥 **5xx — Server Error** — The server failed to fulfill a valid request 🔥
- Rule of thumb: "4xx = your fault | 5xx = their fault"

**SCRIPT:**
"Status codes are the language the server uses to tell you how your request went. They're grouped into five ranges and the group alone tells you a huge amount.

The most important distinction you'll make every day is 4xx versus 5xx. 4xx means the client — the code calling the API — did something wrong. Bad data, wrong credentials, resource not found. 5xx means the server is broken. If you're getting 5xx errors, the problem is not in your request — it's in the server code, the database, or the infrastructure.

This matters practically when debugging. A 400 or 404 means look at what you're sending. A 500 means look at the server logs."

---

## SLIDE 13 — 2xx Success Codes
**Content:**
- **200 OK** — General success. Returned by GET, PUT, PATCH
- **201 Created** — Resource successfully created. Returned by POST. Include `Location` header
- **202 Accepted** — Request accepted but processing not yet complete (async operations)
- **204 No Content** — Success, but nothing to return. Returned by DELETE
- Visual tip: color them all green, with 201 and 204 starred as "most commonly misused"

**SCRIPT:**
"Within the 2xx range, precision matters. Don't just return 200 for everything successful.

201 is specifically for creation — after a successful POST, return 201 not 200. The difference communicates to any consumer of your API exactly what happened. Pair it with a Location header and it's self-documenting.

204 is for successful operations where there's nothing to return. After a DELETE, there's no resource to describe — return 204. Returning 200 with an empty body is less clean.

202 is underused and very powerful for long-running operations. 'I've accepted your request, I'm processing it, come check back later.' You'd pair this with a polling endpoint or a webhook. Very useful for things like generating reports or sending mass emails."

---

## SLIDE 14 — 4xx Client Error Codes
**Content:**
- **400 Bad Request** — Malformed request syntax, missing required fields
- **401 Unauthorized** — Not authenticated (no token or invalid token)
- **403 Forbidden** — Authenticated but not permitted to access this resource
- **404 Not Found** — Resource doesn't exist at this URL
- **405 Method Not Allowed** — HTTP method not supported on this endpoint
- **409 Conflict** — State conflict (e.g., duplicate email, version mismatch)
- **422 Unprocessable Entity** — Structurally valid but business logic validation failed
- **429 Too Many Requests** — Rate limit exceeded
- Callout box: "401 vs 403 — Know the difference!"

**SCRIPT:**
"The 4xx range is where most of your day-to-day debugging lives. Let me highlight the ones that trip people up.

**401 versus 403** is the most common confusion. 401 means the server doesn't know who you are — you haven't authenticated, or your token has expired or is invalid. The fix is to log in and get a fresh token. 403 means the server knows exactly who you are, and you're simply not allowed to do what you're trying to do. The fix is permissions — not a new token.

**409 Conflict** is for state conflicts. If a user tries to register with an email that already exists, that's a 409. If you're using optimistic locking and the resource was modified by someone else since you fetched it, that's a 409.

**422 Unprocessable Entity** — use this for validation failures. The difference from 400: a 400 is for when the request itself is malformed — invalid JSON, missing Content-Type header, that sort of thing. A 422 is for when the request is perfectly formed but the *data inside it* fails validation — an email field that's not a valid email format, a negative price, a date that's in the past when it should be future.

**429 Too Many Requests** — you'll encounter this on third-party APIs. It means slow down. Always handle 429 in your client code with retry logic and backoff."

---

## SLIDE 15 — 5xx Server Error Codes
**Content:**
- **500 Internal Server Error** — Unhandled exception, generic server crash
- **501 Not Implemented** — Method or feature not yet implemented
- **502 Bad Gateway** — Upstream server returned invalid response
- **503 Service Unavailable** — Server is down for maintenance or overloaded
- **504 Gateway Timeout** — Upstream server didn't respond in time
- Callout: "When you see 5xx — check server logs, not your request"
- Developer note: "Never let a raw stack trace reach the 500 response body"

**SCRIPT:**
"The 5xx range means something broke on the server.

500 is the catch-all. Any unhandled exception that bubbles up and isn't caught becomes a 500. This is what you're trying to prevent — you want to catch your exceptions, handle them gracefully, and return a clean, formatted error response.

503 is important to handle in client code because it's usually temporary. If a service is overloaded or restarting, a 503 today might be a 200 in ten seconds. Good client code retries 503s with exponential backoff.

502 and 504 often indicate network infrastructure problems — a load balancer or API gateway in front of your service got a bad or no response from the actual backend. When you see these in production, the problem is usually in your infrastructure configuration, not your application code."

---

## SLIDE 16 — Request Headers Deep Dive
**Content:**
- Table with three columns: Header Name | What It Does | Example Value
  - `Content-Type` | Format of the body being sent | `application/json`
  - `Accept` | Format the client wants back | `application/json`
  - `Authorization` | Authentication credentials | `Bearer eyJhbGci...`
  - `Content-Length` | Size of the body | `142`
  - `Accept-Language` | Preferred language | `en-US`
  - `X-Request-ID` | Unique tracing ID | `a3b4-c5d6-e7f8`
  - `Cache-Control` | Caching directives | `no-cache`

**SCRIPT:**
"Headers are the metadata layer of HTTP. Let me walk through the most important ones you'll deal with.

`Content-Type` tells the receiver what format your body is in. If you're sending JSON and you forget this header, servers will often reject the request or misinterpret it. Always set it when you have a body.

`Accept` tells the server what format you want back. Most modern JSON APIs don't require this since they only speak JSON, but it's proper HTTP etiquette to include it.

`Authorization` is where your credentials live. The most common modern pattern is `Bearer <token>`, where the token is a JWT. The server reads this on every request, validates the token, extracts your identity, and decides what you're allowed to do.

`X-Request-ID` is a custom header — the `X-` prefix means custom. It's a best practice to generate a unique ID for every incoming request, attach it to all your logs, and return it in error responses. When something goes wrong and a user reports an error, you can find that exact request instantly in your logs."

---

## SLIDE 17 — Response Headers Deep Dive
**Content:**
- Table:
  - `Content-Type` | Format of the response body | `application/json`
  - `Location` | URL of a newly created resource | `/api/users/42`
  - `Cache-Control` | How long to cache the response | `max-age=3600`
  - `WWW-Authenticate` | Tells client how to authenticate | `Bearer realm="api"`
  - `X-RateLimit-Limit` | Total requests allowed per window | `1000`
  - `X-RateLimit-Remaining` | Requests left in current window | `847`
  - `X-RateLimit-Reset` | When the window resets (Unix timestamp) | `1700000000`
  - `Retry-After` | How long to wait before retrying (after 503/429) | `30`

**SCRIPT:**
"Response headers carry important metadata back from the server. Some of these you'll see constantly.

`Location` — we've talked about this. Every 201 response should have one.

Cache-Control headers tell your client and any intermediate caches how to cache the response. `max-age=3600` means this response can be cached for one hour. This is a huge performance lever — if clients cache responses properly, they make fewer API calls and your server handles less traffic.

Rate limit headers are on most production third-party APIs. They tell you your quota and how much you've used. Good client code reads these and proactively slows down before hitting the limit, rather than getting a 429 and backing off reactively."

---

---

# SECTION 2 — REST PRINCIPLES
## ⏱ ~15 minutes

---

## SLIDE 18 — What Is REST?
**Content:**
- Large text: REST = Representational State Transfer
- Subtext: "An architectural style, not a technology"
- Created by Roy Fielding in 2000 (PhD dissertation)
- Key idea: "A set of constraints that, when followed, produce APIs that are consistent, scalable, and easy to consume"
- Contrast: REST vs SOAP (old, XML-heavy, strict protocol) vs GraphQL (newer, query language)

**SCRIPT:**
"REST is not a framework, a library, or a protocol. You don't install REST. It's an architectural style — a set of design constraints that, when you follow them, produce APIs with very desirable properties.

Roy Fielding defined REST in his doctoral dissertation in 2000. He was one of the main authors of HTTP itself, so REST was his description of the principles that make HTTP work well for distributed systems.

You might have heard of SOAP — that's the older alternative to REST. SOAP is a strict protocol, XML-only, verbose, and complex. REST became dominant because it's simpler, uses HTTP naturally, and works well with JSON. You might also hear about GraphQL — that's a newer query language for APIs with different trade-offs. Today we're focused on REST because it's still the overwhelming standard for backend APIs you'll be building."

---

## SLIDE 19 — The 6 REST Constraints
**Content:**
- Six numbered boxes:
  1. **Client-Server** — Frontend and backend are completely separated. They communicate only through the API.
  2. **Stateless** — Every request contains everything the server needs. No sessions stored server-side between calls.
  3. **Cacheable** — Responses must declare whether they can be cached.
  4. **Uniform Interface** — Consistent, standardized way to interact with resources.
  5. **Layered System** — Client doesn't know (or care) if it's talking to a load balancer, proxy, or the actual server.
  6. **Code on Demand** *(optional)* — Server can send executable code to the client (almost never used).

**SCRIPT:**
"The six constraints give REST its properties. Let me translate each from academic language into what it means for your day-to-day work.

**Client-Server** means the front end and back end are completely separate. Your React app has no knowledge of how the database works. Your API has no knowledge of how the UI renders. They talk only through the API contract. This is why you can build a single API that serves a web app, a mobile app, and a third-party integration simultaneously.

**Stateless** — we've hammered this. No server-side sessions. Every request carries its own identity and context.

**Cacheable** — your GET responses should declare how long they're valid for. This is free performance.

**Uniform Interface** is the big one for practical work. It means: resources are identified by URLs, you interact with them via standard HTTP methods, and representations are consistent JSON objects. Every endpoint should follow the same conventions.

**Layered System** means you can add load balancers, caches, and API gateways in front of your service without the client knowing or caring. This is how you scale.

**Code on Demand** is real but almost never used — it would be something like returning JavaScript for a browser to execute."

---

## SLIDE 20 — Uniform Interface — The Core REST Principle
**Content:**
- Four sub-constraints of Uniform Interface explained simply:
  1. **Resource Identification** — Resources are named by URIs (`/users/42` is the "user 42" resource)
  2. **Manipulation Through Representations** — You interact with resources by sending representations (JSON) not by calling functions
  3. **Self-Descriptive Messages** — Each message includes enough information to describe how to process it (Content-Type, etc.)
  4. **HATEOAS** *(advanced)* — Responses include links to related actions (rarely implemented fully, worth knowing the concept)

**SCRIPT:**
"The Uniform Interface constraint is what most people actually mean when they say 'REST.' Let me unpack it.

Resources are identified by URIs. The user with ID 42 lives at `/api/users/42`. Always. Every time. That URI is stable. You don't have separate endpoints for 'get user as admin' versus 'get user as regular person' — it's the same resource, and authorization determines what you see in the response.

You manipulate those resources by sending representations. You don't call `updateUserEmail()` — you send a PATCH request to `/api/users/42` with the updated representation. The HTTP method is the verb, the URL is the noun. That's the whole interface.

HATEOAS is the most academic part — it stands for Hypermedia as the Engine of Application State. The idea is that API responses include links to related actions, like a webpage includes links to navigate elsewhere. A user response might include a link to their orders, a link to update their profile, a link to delete their account. In practice, very few APIs implement full HATEOAS, but you'll see partial implementations and it's worth understanding the concept."

---

## SLIDE 21 — Resource Naming — The Rules
**Content:**
- Rule 1: **URLs are nouns, never verbs**
  - ✅ `GET /users`
  - ❌ `GET /getUsers`
  - ❌ `POST /createUser`
- Rule 2: **Use plural nouns**
  - ✅ `/users`, `/orders`, `/products`
  - ❌ `/user`, `/order`, `/product`
- Rule 3: **Lowercase with hyphens for multi-word**
  - ✅ `/product-categories`
  - ❌ `/productCategories`, `/ProductCategories`, `/product_categories`
- Rule 4: **Never expose implementation details**
  - ❌ `/getUserFromDatabase`, `/api/v1/callStoredProcedure`

**SCRIPT:**
"Resource naming is one of the most immediately visible indicators of whether an API is well-designed.

The core rule: URLs are nouns. The HTTP method is the verb. You already have `GET`, `POST`, `PUT`, `PATCH`, and `DELETE` — you don't need to duplicate that in the URL. `GET /users` is right. `GET /getUsers` is wrong. The method already tells me you're getting. The URL should tell me *what* you're getting.

Use plural nouns consistently. The collection of users lives at `/users`. User 42 specifically lives at `/users/42`. Both plural — the collection and the individual item within it. This is the widely accepted convention.

Lowercase with hyphens for multi-word resource names. URLs are not JavaScript variable names. `product-categories` reads cleanly as a URL. `productCategories` is CamelCase in a URL — it looks weird and some servers are case-sensitive.

Never expose implementation details. Your consumers should not know or care whether you're using PostgreSQL, MongoDB, or a stored procedure. The URL describes the resource, not the implementation."

---

## SLIDE 22 — Resource Naming — Hierarchy & Relationships
**Content:**
- Relationship hierarchy examples:
  - All users: `GET /users`
  - Specific user: `GET /users/42`
  - All orders for user 42: `GET /users/42/orders`
  - Specific order for user 42: `GET /users/42/orders/99`
  - All items in that order: `GET /users/42/orders/99/items`
- Rule: Don't go deeper than 3 levels — it gets unwieldy
- Query params for filtering:
  - `GET /orders?status=pending`
  - `GET /products?category=electronics&minPrice=100&sort=price&page=2`
- Difference: Hierarchy = identity | Query params = filtering

**SCRIPT:**
"When resources are related to each other, express that relationship in the URL hierarchy. User 42's orders live at `/users/42/orders`. A specific order lives at `/users/42/orders/99`. This URL is self-documenting — it tells you exactly what you're looking at and the context it belongs to.

Be careful about going too deep. Beyond three levels, URLs become unwieldy. If you find yourself writing `/users/42/orders/99/items/5/attributes`, that's a sign you might need to rethink the resource model or break it into separate top-level resources.

Query parameters are for filtering, sorting, searching, and pagination — they change *which* resources you get back, not *which resource* you're identifying. `/orders?status=pending` returns the pending orders from the whole orders collection. `/users/42/orders?status=pending` returns the pending orders for user 42 specifically. The hierarchy identifies where you are; the query params narrow down what you see there."

---

## SLIDE 23 — API Versioning — Why It's Necessary
**Content:**
- The hard truth: "Your API WILL change"
- Breaking change = a change that causes existing clients to fail:
  - Removing a field
  - Renaming a field
  - Changing a field's data type
  - Changing the behavior of an endpoint
- Non-breaking change = safe to make without a new version:
  - Adding a new optional field to a response
  - Adding a new endpoint
  - Adding a new optional request parameter
- Quote: "Start with v1 from day one. It costs nothing now and saves everything later."

**SCRIPT:**
"Here's the reality: your API will change. Business requirements change. You'll discover your first design was wrong. Data models evolve. You need to add fields, remove fields, restructure responses.

The problem is that once other code is consuming your API — your own frontend, mobile apps, third-party integrations — breaking changes break them. Silently. At runtime. Often in production. Versioning is the mechanism that lets you make breaking changes without breaking existing clients.

The key distinction is breaking versus non-breaking. Adding a new optional field to a response is non-breaking — existing clients just ignore the new field. Removing a field or changing a field's type is breaking — code that was reading that field now fails.

My strong recommendation: put `v1` in your URL from the very first day you build an API, even if you have no intention of changing it. The moment you need to make a breaking change, you'll be incredibly grateful you did. The cost of adding it later — updating every existing client, every piece of documentation — is enormous."

---

## SLIDE 24 — API Versioning Strategies Compared
**Content:**
- Three strategies side-by-side:
  - **URI Versioning** (most common)
    - `/api/v1/users`
    - Visible in URLs and logs
    - Easy to route and test
    - Slightly "impure" from strict REST theory
  - **Header Versioning**
    - `Accept: application/vnd.myapi+json;version=2`
    - "Purest" from REST theory — resource is the same
    - Hard to test in browser / Postman without configuration
    - Complex routing
  - **Query Parameter Versioning**
    - `/api/users?version=2`
    - Easy to use and test
    - Easy to accidentally omit
    - Mixes versioning with filtering params
- Recommendation callout: "URI versioning — use it. Simple, obvious, widely understood."

**SCRIPT:**
"There are three main approaches to API versioning and teams argue about them endlessly. Here's my take.

URI versioning puts the version right in the URL. `/api/v1/users`, `/api/v2/users`. It's explicit, it shows up in every log, it's instantly obvious to any developer reading the URL, and it's trivial to route. The theoretical objection is that the resource is the same user whether you're on v1 or v2, so the URL shouldn't change. Fair point. But in practice, URI versioning wins on simplicity.

Header versioning is theoretically cleaner — you pass the version in an Accept header. The URL stays stable. But it's much harder to test, harder to observe in logs, and harder to explain to someone new.

Query parameter versioning works but pollutes your query string with infrastructure concerns. It also mixes versioning — which is about your API contract — with filtering, which is about data selection. Keep those separate.

Use URI versioning. It's what the vast majority of production APIs use, it's what developers expect, and it's what you'll find in every major API guide."

---

## SLIDE 25 — REST Best Practices Checklist
**Content:**
- Checklist format:
  - ✅ Use the correct HTTP method for the operation
  - ✅ Return specific, meaningful status codes
  - ✅ Use nouns for resource URLs, never verbs
  - ✅ Use plural nouns and lowercase with hyphens
  - ✅ Version your API from day one
  - ✅ Validate all input and return useful error messages
  - ✅ Keep response shapes consistent across all endpoints
  - ✅ Always use HTTPS in staging and production
  - ✅ Document everything (we'll cover Swagger in the next presentation)
  - ✅ Handle errors gracefully — no raw stack traces ever

**SCRIPT:**
"Here's your REST design checklist. These are the things that separate a good API from one that frustrates everyone who has to consume it.

I want to call out 'keep response shapes consistent.' This is deceptively simple and incredibly important. If your success responses look different across different endpoints — sometimes a root object, sometimes a data wrapper, sometimes an array — consuming code becomes a maze of special cases. Pick one pattern and use it everywhere. Same for error responses. We'll talk about exactly how to design that error shape in the error handling section."

---

---

# SECTION 3 — POSTMAN
## ⏱ ~25 minutes

---

## SLIDE 26 — What Is Postman?
**Content:**
- Logo + tagline: "The API Platform for Developers"
- Core uses:
  - 🧪 Test API endpoints manually during development
  - 📁 Organize requests into reusable Collections
  - 🔄 Automate API tests and run them in sequence
  - 📖 Document and share API usage with your team
  - 🔧 Debug and inspect request/response details
- Available: Desktop app (Windows, Mac, Linux) + Web app

**SCRIPT:**
"Postman is the tool you'll use more than almost anything else as an API developer. Before tools like Postman, testing an API endpoint required writing code — a curl command in the terminal, a small test script, a unit test. That's slow when you're actively building and debugging.

Postman gives you a full graphical environment for making HTTP requests. You can test an endpoint in seconds, inspect every detail of the response, save the request for later, organize it with related requests, and write automated tests that verify the API behaves correctly.

Think of Postman as three things rolled into one: an HTTP client (for making requests), a test runner (for validating responses), and documentation tool (for sharing how your API works). We'll use all three capabilities today."

---

## SLIDE 27 — The Postman Interface
**Content:**
- Annotated interface diagram (describe what to draw or show as a labeled screenshot):
  - **Far left sidebar** — Workspaces, Collections, Environments, History
  - **Top bar** — Method dropdown + URL bar + Send button + Save button
  - **Tab row below URL** — Params | Authorization | Headers | Body | Pre-request Script | Tests | Settings
  - **Response panel (bottom half)** — Body | Cookies | Headers | Test Results tabs
  - **Response metadata bar** — Status code (color coded) | Time | Size

**SCRIPT:**
"Let me orient you to the interface. Everything in Postman flows around the central request builder in the middle of the screen.

At the top you have your request line — the method dropdown on the left, the URL in the middle, and the Send button on the right. Simple.

Below the URL is a row of tabs. **Params** is a table editor for query parameters — instead of typing `?role=admin&sort=name` into the URL, you fill in key-value pairs and Postman builds the URL for you. **Authorization** is incredibly useful — you pick your auth type (Bearer Token, Basic Auth, API Key, etc.) and fill in your credentials, and Postman adds the correct header automatically. **Headers** lets you manually add any headers. **Body** is where your JSON payload goes for POST, PUT, and PATCH. **Tests** is where you write JavaScript assertions — we'll dig into that soon.

The bottom half is your response. You'll see the status code prominently, color-coded green for 2xx and red for 4xx/5xx. Response time and size are right there. And you have tabs for the body, the response headers, and your test results."

---

## SLIDE 28 — Your First Request in Postman
**Content:**
- Step by step:
  1. Open Postman — click "New" → "HTTP Request"
  2. Set method to `GET`
  3. Enter URL: `https://jsonplaceholder.typicode.com/posts/1`
  4. Click **Send**
  5. Observe: 200 OK, response body with id, title, body, userId
- JSONPlaceholder intro box:
  - Free fake REST API at `jsonplaceholder.typicode.com`
  - Supports GET, POST, PUT, PATCH, DELETE
  - Always returns realistic-looking data
  - Perfect for practice — no setup needed

**SCRIPT:**
"We're going to use JSONPlaceholder for our demos. It's a free, public, always-available fake REST API. It behaves like a real REST API — it supports all the methods, returns real-looking data, and responds with correct status codes. You don't need an account or any setup.

Let me walk through your very first request. In Postman, click New, then HTTP Request. The method is already GET. In the URL bar type `https://jsonplaceholder.typicode.com/posts/1`. Hit Send.

Look at the response panel. You got a 200 OK — that's in the status indicator in the top right of the response panel. The response time is there too — probably somewhere around 100-200 milliseconds. And the body is a JSON object with id, title, body, and userId. That's it. That's a complete HTTP request-response cycle — you just did it."

---

## SLIDE 29 — Making a POST Request
**Content:**
- Steps:
  1. Change method to `POST`
  2. URL: `https://jsonplaceholder.typicode.com/posts`
  3. Go to **Body** tab → select **raw** → select **JSON** from dropdown
  4. Enter body:
     ```json
     {
       "title": "My First Post",
       "body": "This is the content of my post.",
       "userId": 1
     }
     ```
  5. Click Send
  6. Observe: 201 Created, new resource returned with generated id
- Note: JSONPlaceholder fakes persistence — nothing is actually saved, but the response is accurate

**SCRIPT:**
"Now let's try a POST. Change the method dropdown to POST. Same base URL but without the `/1` at the end — we're posting to the collection, not a specific resource.

Go to the Body tab. Select 'raw' — this lets you type raw text. Then from the dropdown on the right change it from Text to JSON. Postman will automatically set your Content-Type header to application/json for you.

Type in a JSON object with a title, body, and userId. Hit Send.

You should get back a 201 Created. JSONPlaceholder echoes back the object you sent plus an id it generated. In a real API, that id would be the one the database assigned to the newly created record. Notice how the status code is different from our GET — 201 not 200. That's the API communicating precisely what happened.

If you look at the response headers tab, you'll see the Content-Type: application/json from the server confirming what it's sending back."

---

## SLIDE 30 — The Authorization Tab
**Content:**
- Types of authorization in the dropdown:
  - No Auth — public endpoints
  - Bearer Token — paste your JWT
  - Basic Auth — username + password fields
  - API Key — key name and value, choose header or query param location
  - OAuth 2.0 — full OAuth flow (for third-party APIs like Google, GitHub)
- How Bearer Token works: Postman adds `Authorization: Bearer <token>` header automatically
- Best practice: store the token in an environment variable `{{authToken}}`

**SCRIPT:**
"The Authorization tab is one of Postman's most useful quality-of-life features. Instead of manually typing `Authorization: Bearer <your-token>` in the headers tab every time, you pick your auth type from the dropdown and Postman handles the correct header format automatically.

Bearer Token is what you'll use most for APIs you build. You paste in your JWT and Postman inserts the `Authorization: Bearer <token>` header on every request automatically.

Basic Auth takes a username and password and base64-encodes them into the Authorization header format. API Key lets you specify a key name and value and place it in either a header or a query parameter, depending on what the API requires.

I'll mention this again soon, but you should be storing your token in an environment variable called `authToken` and referencing it here as `{{authToken}}`. That way when your token expires, you update it in one place and every request in your collection picks it up automatically."

---

## SLIDE 31 — Collections — Organizing Your Work
**Content:**
- A Collection = a named folder of saved API requests
- Why collections matter:
  - Organize related requests together
  - Share with teammates (export as JSON)
  - Run all requests automatically (Collection Runner)
  - Apply shared settings (base URL, auth) to all requests in the collection
- How to create: Click + icon next to Collections → Give it a name
- Suggested structure visual:
  ```
  📁 Users API
  ├── 📂 Authentication
  │   ├── POST /login
  │   └── POST /logout
  ├── 📂 Users
  │   ├── GET /users (list all)
  │   ├── GET /users/:id
  │   ├── POST /users
  │   ├── PUT /users/:id
  │   └── DELETE /users/:id
  └── 📂 Admin
      └── GET /admin/users
  ```

**SCRIPT:**
"Collections are what transform Postman from a request tool into a full API workspace. Without collections, Postman is just a bunch of saved tabs. With collections, it's an organized, shareable, runnable representation of everything your API can do.

Create a collection per API or per microservice. Inside, create folders per resource. This maps almost perfectly to your REST URL structure, which is not a coincidence — a well-organized REST API and a well-organized Postman collection mirror each other naturally.

When you share a collection with a teammate, they have instant access to every endpoint, pre-configured with the right methods, URLs, headers, and even test scripts. It's living documentation that's always up to date because the developers using it keep it updated."

---

## SLIDE 32 — Environment Variables — Stop Hardcoding
**Content:**
- The problem: `https://api.dev.example.com/v1/users` hardcoded in 40 requests
  - Now you need to test against staging — edit 40 URLs
  - Now you need to go to production — edit 40 URLs again
  - Now your auth token expired — update 40 headers
- The solution: Environment Variables
  - Define `baseUrl` once per environment
  - Use `{{baseUrl}}` in every request URL
  - Switch active environment → all requests update instantly
- Common environment variables:
  - `baseUrl` — server base URL
  - `authToken` — current JWT
  - `userId` — test user ID reused across requests

**SCRIPT:**
"Environment variables are one of Postman's most powerful features and one of the most underused by beginners. Let me show you the problem they solve.

Imagine you've built a collection with 40 API requests. Every URL starts with `https://api.dev.example.com/v1`. Your sprint is done and you need to test against staging before release. That's `https://api.staging.example.com/v1`. Do you really want to edit 40 URLs? And then when you want to hit production, edit them all again?

With environment variables, you create a variable called `baseUrl`. In your dev environment it's `https://api.dev.example.com/v1`. In staging it's `https://api.staging.example.com/v1`. Every URL in your collection uses `{{baseUrl}}`. Click the environment dropdown in the top-right of Postman, switch from Dev to Staging, and every single request now points to staging. One click.

Same with auth tokens. Store the token in `{{authToken}}`, use it in your Authorization tab. Token expires? Update the environment variable once. Done."

---

## SLIDE 33 — Creating and Using Environments
**Content:**
- How to create an environment:
  1. Click **Environments** in the left sidebar
  2. Click **+** to create new
  3. Name it (e.g. "Dev", "Staging")
  4. Add key-value pairs
  5. Click the checkmark to set as active
- Use `{{variableName}}` syntax anywhere in Postman:
  - URLs: `{{baseUrl}}/users/{{userId}}`
  - Headers: `Authorization: Bearer {{authToken}}`
  - Body: `{ "userId": "{{userId}}" }`
- Multiple environment types:
  - **Environment** — per-deployment config (dev, staging, prod)
  - **Collection variable** — shared across all environments in a collection
  - **Global variable** — shared across all collections

**SCRIPT:**
"Creating an environment is straightforward. Go to the Environments section in the left sidebar, click the plus icon, give it a name, and add your key-value pairs. Set it as active with the checkmark.

From that point on, anywhere in Postman you can use double curly brace syntax — `{{variableName}}` — and Postman substitutes the actual value at runtime.

Postman has three scope levels for variables. Environment variables are specific to a deployment environment. Collection variables are shared across all environments for a specific collection — great for things like API version numbers. Global variables apply everywhere — use these sparingly, they tend to create confusion.

One pro tip: when you run your login request and get back a JWT token, you can write a small test script that automatically captures that token and saves it into your `authToken` environment variable. Then your entire collection runs authenticated automatically without you having to manually copy and paste tokens."

---

## SLIDE 34 — Test Scripts — Introduction
**Content:**
- Tests tab in Postman — runs JavaScript after every response
- Uses the `pm` (Postman) object API
- Three core building blocks:
  - `pm.test("description", () => { ... })` — defines a test case
  - `pm.expect(value).to...` — Chai-style assertion
  - `pm.response` — access status, headers, body, time
- Results shown in:
  - "Test Results" tab in the response panel (pass/fail per test)
  - Collection Runner results dashboard

**SCRIPT:**
"Now we get to one of Postman's most powerful features: automated test scripts. In the Tests tab of any request, you write JavaScript that Postman executes automatically every time that request runs.

You already know JavaScript, so this will feel natural. The `pm` object is Postman's built-in API for writing tests. `pm.test` takes a descriptive string — this becomes the test name in your results — and a callback function where you write your assertion.

Assertions use Chai syntax — `pm.expect(value).to.equal(something)`, `pm.expect(value).to.be.a('string')`, `pm.expect(value).to.include('text')`. If you've used Jest or Mocha, this will be familiar.

The results show up in the Test Results tab at the bottom of the response panel with a green pass or red fail indicator next to each test name. When you run a whole Collection, you get a dashboard showing every test across every request."

---

## SLIDE 35 — Test Scripts — Status Code & Response Body
**Content:**
- Code examples with comments:
  ```javascript
  // Check status code
  pm.test("Status code is 200", () => {
    pm.response.to.have.status(200);
  });

  // Parse and check response body
  pm.test("User has an id", () => {
    const user = pm.response.json();
    pm.expect(user.id).to.be.a('number');
  });

  pm.test("User has name and email", () => {
    const user = pm.response.json();
    pm.expect(user.name).to.be.a('string').and.not.be.empty;
    pm.expect(user.email).to.include('@');
  });

  // Check response time
  pm.test("Response time under 500ms", () => {
    pm.expect(pm.response.responseTime).to.be.below(500);
  });
  ```

**SCRIPT:**
"Let me show you the most common test patterns you'll write every day.

Status code check — always write this first. Every test should verify the response came back with the right status code. A 200 when you expected a 201, or a 200 when there was actually an error in the body, is a sneaky bug that proper status code testing catches immediately.

Body validation — call `pm.response.json()` to parse the response body into a JavaScript object, then use `pm.expect` to assert on the fields. Check that required fields exist, that they're the right type, that values are within expected ranges.

Response time testing is something many people overlook. A test that passes but takes 5 seconds is a warning sign. Set reasonable thresholds and make response time a first-class test concern."

---

## SLIDE 36 — Test Scripts — Saving Variables & Chaining
**Content:**
- Code examples:
  ```javascript
  // After POST /users — save the new user's ID
  pm.test("User was created", () => {
    pm.response.to.have.status(201);
    const user = pm.response.json();
    pm.expect(user.id).to.be.a('number');

    // Save to environment variable for use in next request
    pm.environment.set("createdUserId", user.id);
  });
  ```
- Use in next request URL: `/users/{{createdUserId}}`
- This enables **request chaining** — full CRUD lifecycle test:
  1. POST /users → save ID
  2. GET /users/{{createdUserId}} → verify exists
  3. PATCH /users/{{createdUserId}} → update
  4. GET /users/{{createdUserId}} → verify update
  5. DELETE /users/{{createdUserId}} → clean up

**SCRIPT:**
"Request chaining is where test automation in Postman really shines. The idea is that the output of one request becomes the input to the next.

After your POST creates a user and gives you back a 201 with the user's new ID, your test script captures that ID and saves it to an environment variable. Every subsequent request in your collection then uses that variable in the URL — `/users/{{createdUserId}}`. 

Now you have a real, automated integration test. Create a user, verify it was created, update it, verify the update, then delete it to clean up. Run that against any environment in seconds. If any step fails, the Collection Runner tells you exactly which request failed and why.

This is a testing pattern you'd use in professional API development. It's also a great way to understand exactly how your API behaves end to end."

---

## SLIDE 37 — The Collection Runner
**Content:**
- How to access: Click the ▶ Play button on a Collection → "Run collection"
- Configuration options:
  - Select which requests to include (all or subset)
  - Number of iterations (run the whole suite N times)
  - Delay between requests (milliseconds)
  - Data file — CSV or JSON to feed different test data per iteration
- Results view:
  - Each request listed with pass/fail
  - Each individual test assertion listed
  - Total passed/failed count
  - Option to export results as JSON

**SCRIPT:**
"The Collection Runner puts everything together. Once your collection has test scripts on every request, you click the Run button, choose your environment, and Postman executes every request in sequence and shows you a full test report.

The data file feature is particularly powerful — you can provide a CSV file with multiple rows of test data, and the Collection Runner executes the collection once per row, substituting values from the CSV into your variables. So you can test 'create user with these 10 different inputs, verify the right behavior for each one.' That's parameterized testing with no code beyond what you've already written.

For command-line usage and CI/CD integration, look up Newman — it's Postman's CLI companion. You export your collection and environment, and Newman runs them from the terminal. `newman run my-collection.json -e dev-environment.json`. That command can live inside a GitHub Actions workflow or a Jenkins pipeline, giving you automated API regression testing on every code push."

---

## SLIDE 38 — API Testing Best Practices
**Content:**
- Test the happy path ✅
- Test error paths ❌:
  - Missing required fields → expect 400/422
  - Invalid auth token → expect 401
  - Correct user, wrong permission → expect 403
  - Non-existent resource ID → expect 404
  - Duplicate resource → expect 409
- Test edge cases:
  - Maximum string length inputs
  - Boundary values (0, -1, max integer)
  - Empty arrays, null fields
- Keep tests independent where possible
- Name tests descriptively — they are documentation
- Run the collection after every code change

**SCRIPT:**
"Good API testing is not just testing the happy path. The happy path is the scenario where everything goes right — valid data, authenticated user, resource exists. That's maybe 20% of the scenarios your API actually handles.

The real value is in testing the error paths. Does your API return 401 when there's no token? Test it. Does it return 422 with field-level errors when the email is missing? Test it. Does it return 404 when you request a user that doesn't exist? Test it.

These tests also protect you over time. When you add a new feature next month, you run the collection and immediately know if you accidentally broke error handling on an unrelated endpoint. That's regression testing with minimal overhead.

Name your tests like documentation. 'Returns 401 when Authorization header is missing' is far more useful than 'Auth test 1'. When a test fails in a CI pipeline, the name tells you exactly what broke without having to dig into the test code."

---

---

# SECTION 4 — ERROR HANDLING
## ⏱ ~12 minutes

---

## SLIDE 39 — Why Error Handling Is a First-Class Concern
**Content:**
- "Good errors are as important as good responses"
- What a bad error response causes:
  - Hours of debugging from a generic "Internal Server Error"
  - Security risks from exposed stack traces
  - Frustrated developers consuming your API
- What a good error response provides:
  - Immediate understanding of what went wrong
  - Clear path to fixing it
  - Safety — no internal implementation details exposed
- Goal: Every possible error state should produce a consistent, informative, safe response

**SCRIPT:**
"Error handling is the part of API development that separates a professional API from one that was just 'got it working.' It's not glamorous, but it is absolutely critical.

Think about the last time you consumed an API that returned a generic 500 error with no message. You had to figure out what went wrong by guessing, trying different inputs, reading documentation that might be outdated. That's wasted time that good error design prevents.

On the other side, think about what a raw Java stack trace in an API response tells a malicious user — it shows your technology stack, your file structure, your library versions, potentially your database schema. That's a security risk masquerading as laziness.

Good error handling is a service to every developer who will ever consume your API — including future you."

---

## SLIDE 40 — The Standard Error Response Shape
**Content:**
- Code block showing recommended error shape:
  ```json
  {
    "error": {
      "code": "VALIDATION_ERROR",
      "message": "The request body contains invalid data.",
      "details": [
        {
          "field": "email",
          "issue": "Must be a valid email address."
        },
        {
          "field": "name",
          "issue": "Name is required and cannot be empty."
        }
      ],
      "timestamp": "2024-11-15T10:30:00Z",
      "requestId": "a3b4-c5d6-e7f8-9012"
    }
  }
  ```
- Labels:
  - `code` → machine-readable, switch on this in client code
  - `message` → human-readable, show to developers (not end users)
  - `details` → field-level errors, incredibly useful for form validation
  - `requestId` → ties this response to a specific log entry on the server

**SCRIPT:**
"Every error response in your API should follow a consistent shape. Here's a solid standard to adopt.

The `code` field is a string constant that identifies the type of error. Client code can switch on this — `if (error.code === 'VALIDATION_ERROR')` — without parsing the human-readable message, which might change. Keep these codes in a constants file and document them.

The `message` is human-readable. It should tell a developer clearly what went wrong. This is not an end-user message — it's for the developer consuming your API. End-user messages are the consuming application's responsibility.

The `details` array is gold for validation errors. Instead of 'validation failed,' you tell the developer exactly which fields failed and exactly what rule they violated. This turns a frustrating 'what went wrong?' into an immediate 'oh, I need to fix the email field.'

The `requestId` is something I urge you to implement from day one. When a user or developer reports an error, they give you the requestId. You search your logs for that ID and find the exact request, the full stack trace, the database query that failed — the complete picture in seconds rather than minutes."

---

## SLIDE 41 — Mapping Exceptions to Status Codes
**Content:**
- Table:
  | Exception / Situation | HTTP Status Code |
  |----------------------|-----------------|
  | Resource not found | 404 Not Found |
  | Validation failure (field level) | 422 Unprocessable Entity |
  | Malformed JSON / bad syntax | 400 Bad Request |
  | No auth token / invalid token | 401 Unauthorized |
  | Valid token, insufficient permissions | 403 Forbidden |
  | Duplicate resource / state conflict | 409 Conflict |
  | Rate limit exceeded | 429 Too Many Requests |
  | Any unhandled/unexpected exception | 500 Internal Server Error |
  | Service dependency down | 503 Service Unavailable |

**SCRIPT:**
"Your job is to map your exception types to the correct HTTP status codes. This table is your reference.

The key principle: never let an exception that's fundamentally a client error (they asked for something that doesn't exist, their data was invalid) result in a 5xx code. 5xx means the server failed. If the server correctly determined that the user's input was bad and returned a 422, that's a success — the server did exactly what it should. The client needs to fix their request.

Similarly, never return a 200 for an error condition. Some legacy systems do this — they return 200 with an error flag in the body. This breaks all tooling that relies on status codes (including Postman's test assertions) and violates the HTTP contract. Status codes mean something. Use them."

---

## SLIDE 42 — Centralized Exception Handling in Java Spring
**Content:**
- The problem: throwing exceptions all over the codebase, inconsistent responses
- The solution: `@ControllerAdvice` + `@ExceptionHandler`
- Code sketch:
  ```java
  @ControllerAdvice
  public class GlobalExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(EntityNotFoundException e) {
      return ResponseEntity.status(404).body(
        new ErrorResponse("NOT_FOUND", e.getMessage())
      );
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponse> handleValidation(ValidationException e) {
      return ResponseEntity.status(422).body(
        new ErrorResponse("VALIDATION_ERROR", e.getMessage(), e.getDetails())
      );
    }

    @ExceptionHandler(Exception.class)  // catch-all
    public ResponseEntity<ErrorResponse> handleGeneric(Exception e) {
      log.error("Unhandled exception: ", e);  // log the real error
      return ResponseEntity.status(500).body(
        new ErrorResponse("INTERNAL_ERROR", "An unexpected error occurred.")
      );
    }
  }
  ```

**SCRIPT:**
"In Spring Boot, the cleanest way to handle exceptions is with a `@ControllerAdvice` class. This is a special component that intercepts exceptions thrown anywhere in your controllers and maps them to HTTP responses in one central place.

You write `@ExceptionHandler` methods that each handle a specific exception type. When your service layer throws an `EntityNotFoundException`, Spring finds the matching handler, calls it, and returns the `ResponseEntity` you build — with the right status code and your standard error shape.

The catch-all handler at the bottom is critical. It catches any exception type you didn't anticipate. Notice what it does: it logs the real exception with the full stack trace so you can debug it, and it returns a clean, generic error message to the client. The client gets something useful. You get the full error in your logs. Nobody gets an exposed stack trace.

This pattern means you throw exceptions anywhere in your business logic without worrying about how they get turned into HTTP responses. The handler takes care of it consistently across your entire application."

---

## SLIDE 43 — Centralized Error Handling in Express (Node.js)
**Content:**
- Express error-handling middleware signature: `(err, req, res, next)`
- Code sketch:
  ```javascript
  // Error-handling middleware — must be registered LAST
  app.use((err, req, res, next) => {
    console.error('Unhandled error:', err);

    if (err.type === 'NOT_FOUND') {
      return res.status(404).json({
        error: { code: 'NOT_FOUND', message: err.message }
      });
    }

    if (err.type === 'VALIDATION_ERROR') {
      return res.status(422).json({
        error: { code: 'VALIDATION_ERROR', message: err.message, details: err.details }
      });
    }

    // Catch-all
    res.status(500).json({
      error: { code: 'INTERNAL_ERROR', message: 'An unexpected error occurred.' }
    });
  });

  // Trigger it by calling next(err) anywhere in your routes
  app.get('/users/:id', async (req, res, next) => {
    try {
      const user = await findUser(req.params.id);
      if (!user) return next({ type: 'NOT_FOUND', message: 'User not found' });
      res.json(user);
    } catch (err) {
      next(err);
    }
  });
  ```

**SCRIPT:**
"In Express, error handling works through a special middleware with four parameters — error, request, response, and next. Express recognizes it as an error handler because of that four-parameter signature.

You register this middleware last, after all your routes. When any route calls `next(err)` — passing an error object to next — Express skips all remaining regular middleware and routes and goes straight to your error handler.

In your routes, wrap async code in try-catch. If something throws, catch it and pass it to `next(err)`. If a resource isn't found, create an error object with a type field and pass it to `next`. Your central handler then pattern-matches on the type and builds the appropriate response.

Same principle as the Spring approach: centralized, consistent, no stack traces leaking, full logging on the server side."

---

## SLIDE 44 — Validating API Responses End-to-End
**Content:**
- The full validation checklist:
  - ✅ Correct HTTP status code for every scenario
  - ✅ Response body matches expected schema (required fields present, correct types)
  - ✅ 401 returned when Authorization header is missing
  - ✅ 403 returned when token is valid but permission is insufficient
  - ✅ 404 returned for non-existent resource IDs
  - ✅ 422 returned with field details for validation failures
  - ✅ 500 never exposes internal details (stack traces, SQL, file paths)
  - ✅ Response time within acceptable bounds
- This checklist = your Postman test script coverage requirements

**SCRIPT:**
"Let me bring everything together. This checklist is what complete API testing looks like. For every endpoint you build, you should be able to run Postman tests that verify all of these conditions.

The status code checks are the most mechanically straightforward — one `pm.test` per scenario. The schema validation checks require parsing the response JSON and asserting on field types. The security checks require running requests with missing or invalid tokens.

When this entire checklist is green across your whole collection, you have real confidence that your API behaves correctly. Not just that it works in the happy path, but that it handles failure gracefully, securely, and consistently.

This is the standard you want to hold yourself to."

---

## SLIDE 45 — Recap & What Comes Next
**Content:**
- What we covered:
  - HTTP protocol, request/response lifecycle
  - All 5 HTTP methods with idempotency
  - Status codes — all five groups with key codes
  - Request and response headers
  - REST principles — 6 constraints, uniform interface
  - Resource naming, versioning strategies
  - Postman — interface, collections, environments, test scripts, Collection Runner
  - Error handling — consistent shapes, centralized handlers, Spring + Express patterns
- Coming next: Building these APIs in Java Spring Boot & Node.js Express | Swagger/OpenAPI documentation

**SCRIPT:**
"That's our full session. Let me leave you with the mental model to carry forward.

HTTP is the foundation — learn it deeply. REST is the design philosophy that makes HTTP work beautifully for APIs. Postman is your workbench for building and validating them. Error handling is what makes your APIs trustworthy and debuggable in the real world.

In upcoming sessions you'll build REST APIs yourself in Spring Boot and Express, and everything today is the mental model you'll operate from. When you're writing a controller method and wondering what status code to return — this is what you come back to. When you're wondering how to name a URL — this. When you're testing an endpoint — Postman.

The Swagger/OpenAPI session covers how to formally document your APIs — I'd strongly recommend going through that presentation as well since it complements everything we covered today."

---

## SLIDE 46 — Practice Tasks
**Content:**
- **Task 1 — Postman Exploration**
  - Use JSONPlaceholder (`jsonplaceholder.typicode.com`)
  - Make a GET, POST, PUT, PATCH, and DELETE request
  - Write at least 3 test assertions per request
  - Verify you get the correct status codes for each method
- **Task 2 — Collections & Environments**
  - Create a Postman Collection called "JSONPlaceholder Practice"
  - Create a Dev environment with `baseUrl = https://jsonplaceholder.typicode.com`
  - Use `{{baseUrl}}` in all your request URLs
  - Run the collection with the Collection Runner
- **Task 3 — Error Scenarios**
  - Request a post with an ID that doesn't exist — what do you get?
  - Try sending a POST with an empty body — what happens?
  - Try changing the Content-Type to text/plain with a JSON body — does behavior change?

**SCRIPT:**
"These three tasks will solidify everything we covered. Task 1 gives you hands-on reps with every HTTP method and test assertions. Task 2 gets you comfortable with collections and environment variables — two things you'll use constantly. Task 3 has you deliberately causing errors to understand how APIs handle bad input.

Do Task 1 right now if you have Postman open. It will take about 20 minutes and you'll learn more from doing it than from any slide."

---

## APPENDIX — TIMING GUIDE

| Slides | Topic | Time |
|--------|-------|------|
| 1–2 | Intro & Agenda | 2 min |
| 3–7 | HTTP Protocol & Lifecycle | 13 min |
| 8–11 | HTTP Methods | 8 min |
| 12–15 | Status Codes | 8 min |
| 16–17 | Headers | 5 min |
| 18–21 | REST Principles | 8 min |
| 22–25 | Resource Naming, Versioning, Best Practices | 8 min |
| 26–29 | Postman Intro & First Requests | 10 min |
| 30–33 | Auth, Collections, Environments | 8 min |
| 34–38 | Test Scripts & Collection Runner | 10 min |
| 39–44 | Error Handling | 12 min |
| 45–46 | Recap & Tasks | 4 min |
| **Total** | | **~96 min** |



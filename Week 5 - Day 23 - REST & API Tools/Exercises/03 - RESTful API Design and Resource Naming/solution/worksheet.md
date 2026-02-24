# Exercise 03 — Solution: RESTful API Design and Resource Naming

---

## Part 1 — REST Architectural Constraints

1. **Uniform Interface** — All resources are accessed through a consistent, standardised interface (HTTP methods + URIs + standard representations), making the API predictable for any client. Example: `GET /books/42` always returns the book resource in JSON, regardless of which client calls it.

2. **Stateless** — Each request from the client contains all information needed to process it; the server stores no session state between requests. Example: Every request to a protected endpoint must include the `Authorization: Bearer <token>` header — the server doesn't remember previous logins.

3. **Cacheable** — Responses must label themselves as cacheable or non-cacheable using headers (`Cache-Control`, `ETag`), allowing clients and proxies to store and reuse responses. Example: `GET /books` with `Cache-Control: max-age=300` tells CDNs to cache the book list for 5 minutes.

4. **Client-Server** — The UI/client and the data storage/server are separated; they can evolve independently as long as the interface between them remains stable. Example: A mobile app and a web app can both consume the same REST API without the API knowing anything about how the UI is built.

5. **Layered System** — A client cannot tell whether it's connected directly to the server or to an intermediary (load balancer, cache, API gateway). Example: A request to `api.library.com` may pass through a CDN, then an API gateway, then a microservice — the client sends one request and gets one response.

6. **Code on Demand** (optional) — The server can extend client functionality by transferring executable code (JavaScript). Example: A REST API that returns a JavaScript widget the browser executes to render a custom UI component.

---

## Part 2 — Fix the Bad URLs

| # | Bad URL | Fixed URL | Rule Violated |
|---|---|---|---|
| 1 | `GET /getBooks` | `GET /api/v1/books` | URIs should be nouns; the HTTP method (GET) is the verb |
| 2 | `GET /BooksList` | `GET /api/v1/books` | URIs must be lowercase; "List" is redundant — a collection is already a list |
| 3 | `POST /createBook` | `POST /api/v1/books` | No verbs in URIs; POST to the collection implies creation |
| 4 | `GET /books/getById/42` | `GET /api/v1/books/42` | No verbs; the ID is the path parameter, not a sub-path after a verb |
| 5 | `DELETE /deleteBook/42` | `DELETE /api/v1/books/42` | No verbs; DELETE method communicates intent |
| 6 | `GET /books/42/getAuthor` | `GET /api/v1/books/42/author` | No verbs in path; sub-resource is just `/author` |
| 7 | `POST /books/42/addReview` | `POST /api/v1/books/42/reviews` | No verbs; POST to the reviews sub-collection creates a review |
| 8 | `GET /books?action=search&q=orwell` | `GET /api/v1/books?author=orwell` | Use query params for filtering/searching; `action` param is verb-like and unnecessary |
| 9 | `GET /BOOKS` | `GET /api/v1/books` | URIs must be lowercase |
| 10 | `GET /book` | `GET /api/v1/books` | Collection resources must use plural nouns |
| 11 | `GET /books/42/reviews/7/comments/3/author` | `GET /api/v1/comments/3/author` (or flatten) | Nesting beyond 2–3 levels is unmanageable; access deeply nested resources directly by ID |
| 12 | `PUT /api/books/v2update/42` | `PUT /api/v2/books/42` | Version belongs at the API root prefix, not embedded inside a resource path segment |

---

## Part 3 — Complete Resource URL Set

| Method | URL | Description |
|---|---|---|
| GET | `/api/v1/books` | List all books (supports `?author=&genre=&page=&size=`) |
| GET | `/api/v1/books/{id}` | Get a single book by ID |
| POST | `/api/v1/books` | Create a new book |
| PUT | `/api/v1/books/{id}` | Fully replace a book |
| PATCH | `/api/v1/books/{id}` | Partially update a book |
| DELETE | `/api/v1/books/{id}` | Delete a book |
| GET | `/api/v1/authors/{id}/books` | Get all books by a specific author |
| — | — | — |
| GET | `/api/v1/authors` | List all authors |
| GET | `/api/v1/authors/{id}` | Get a single author by ID |
| POST | `/api/v1/authors` | Create a new author |
| PUT | `/api/v1/authors/{id}` | Fully update an author |
| PATCH | `/api/v1/authors/{id}` | Partially update an author |
| DELETE | `/api/v1/authors/{id}` | Delete an author |
| — | — | — |
| GET | `/api/v1/loans` | List all loans (supports `?memberId=&status=`) |
| GET | `/api/v1/loans/{id}` | Get a single loan by ID |
| POST | `/api/v1/loans` | Create a loan (check out a book) |
| PATCH | `/api/v1/loans/{id}` | Return a book — update `returnDate` field |

---

## Part 4 — API Versioning Strategies

### Strategy 1: URL Path Versioning
- Example URL: `https://api.library.com/api/v1/books`
- Advantage: Immediately visible in the URL; easy to test in a browser or share as a link; straightforward to route at the API gateway level.
- Disadvantage: Violates the REST principle that a URI should uniquely identify a resource (the same resource has two URIs: `/v1/books/42` and `/v2/books/42`); clients must update all hardcoded URLs when upgrading.

### Strategy 2: Query Parameter Versioning
- Example URL: `https://api.library.com/books?version=1`
- Advantage: The base URL stays clean; easy to add to any existing URL without restructuring routing.
- Disadvantage: Query parameters are semantically for filtering/searching, not versioning; caches may strip or ignore query params; harder to enforce at the gateway level.

### Strategy 3: Header Versioning
- Example header: `Accept: application/vnd.library.v1+json`
- Advantage: Keeps the URI clean and resource-centric (most REST-purist approach); the URI identifies *what* you want, the header identifies *how* you want it represented.
- Disadvantage: Not visible in the browser address bar; harder to share as a link or test manually; requires custom header handling everywhere.

### Recommendation
**URL path versioning** (`/api/v1/`) is recommended for most new public APIs. It is immediately visible, easy to document, straightforward for API gateways to route, and well-understood by developers consuming the API for the first time. While it technically bends REST purity, the practical benefits in discoverability and operability far outweigh the theoretical objection.

# Exercise 03: RESTful API Design and Resource Naming

## Objective
Design a well-structured RESTful API by applying REST principles, correct resource naming conventions, and appropriate API versioning strategies.

## Background
REST (Representational State Transfer) is an architectural style, not a protocol. A "RESTful" API uses HTTP's existing semantics — methods, status codes, and URLs — to model resources and the operations on them. Getting the URL structure right from the start saves enormous refactoring effort later and makes the API intuitive for any developer who uses it.

## Requirements

**Part 1 — REST Principles (6 Constraints)**

In `starter-code/worksheet.md`, write a one-sentence explanation of each of the 6 REST architectural constraints and give one concrete example of how it applies to an API:

1. Uniform Interface
2. Stateless
3. Cacheable
4. Client-Server
5. Layered System
6. Code on Demand (optional)

**Part 2 — Fix the Bad URLs**

The `starter-code/worksheet.md` contains 12 poorly designed API URLs. Rewrite each one using correct REST conventions, then explain what rule was violated.

Bad URLs to fix:
```
1.  GET  /getBooks
2.  GET  /BooksList
3.  POST /createBook
4.  GET  /books/getById/42
5.  DELETE /deleteBook/42
6.  GET  /books/42/getAuthor
7.  POST /books/42/addReview
8.  GET  /books?action=search&q=orwell
9.  GET  /BOOKS
10. GET  /book
11. GET  /books/42/reviews/7/comments/3/author
12. PUT  /api/books/v2update/42
```

**Part 3 — Design a Complete Resource URL Set**

Design the full set of REST endpoints for a **Library API** that manages `books`, `authors`, and `loans`. For each resource, define URLs and methods for:

- List all resources (collection)
- Get a single resource (item)
- Create a new resource
- Fully update a resource
- Partially update a resource
- Delete a resource
- Get a sub-resource (e.g., all books by a specific author)

Fill in the table in `starter-code/worksheet.md`.

**Part 4 — API Versioning**

In `starter-code/worksheet.md`, describe the three most common API versioning strategies, give an example URL or header for each, and list one advantage and one disadvantage:

1. URL path versioning (`/api/v1/books`)
2. Query parameter versioning (`/books?version=1`)
3. Header versioning (`Accept: application/vnd.library.v1+json`)

Then answer: Which strategy would you recommend for a new public API and why?

## Hints
- Resource names should be **nouns**, not verbs — the HTTP method provides the verb.
- Use **plural** noun forms for collections: `/books`, not `/book`.
- Use **lowercase** and **hyphens** for multi-word paths: `/book-reviews`, not `/bookReviews`.
- Nested resources should be limited to 2 levels deep to avoid `/a/1/b/2/c/3/d/4` URLs.

## Expected Output

Part 2, Example fix #1:
```
Bad:  GET /getBooks
Good: GET /books
Rule violated: URLs should be nouns; the HTTP method (GET) already provides the verb.
```

Part 3, Example row:
```
| GET | /api/v1/books | List all books with optional query params (?author=&genre=) |
```

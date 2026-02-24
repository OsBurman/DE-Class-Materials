# Exercise 01: HTTP Methods and Request/Response Lifecycle

## Objective
Understand the purpose of each HTTP method (GET, POST, PUT, PATCH, DELETE) and trace the complete structure of an HTTP request and response.

## Background
HTTP (Hypertext Transfer Protocol) is the foundation of data communication on the web. Every interaction between a client and a server follows a request/response cycle: the client sends a structured request specifying *what it wants to do* (via a method) and *where* (via a URL), and the server returns a structured response. RESTful APIs are built entirely on these semantics.

## Requirements

**Part 1 — Anatomy of an HTTP Request**

Complete the worksheet below in `starter-code/worksheet.md`:

1. Label every component of this raw HTTP request:
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
   Identify and describe: HTTP method, request target (path), HTTP version, Host header, Content-Type header, Authorization header, and request body.

2. Label every component of this raw HTTP response:
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
   Identify and describe: status line, status code, reason phrase, Content-Type header, Location header, and response body.

**Part 2 — HTTP Method Semantics**

In `starter-code/worksheet.md`, complete the method mapping table by filling in:
- The purpose of each method
- Whether it is idempotent (repeating the same call produces the same result)
- Whether it includes a request body
- A concrete example URL + request body (if applicable) for a `books` API

Methods to cover: GET, POST, PUT, PATCH, DELETE

**Part 3 — Request/Response Lifecycle**

In `starter-code/worksheet.md`, write out the 6-step lifecycle of an HTTP request from the moment a client calls `GET /api/v1/books/1`:
1. Client initiates connection (TCP handshake)
2. Client sends HTTP request
3. Server receives and parses request
4. Server processes request (business logic / database)
5. Server sends HTTP response
6. Client receives and processes response

For each step, add one sentence describing what happens.

**Part 4 — Curl Practice**

In `starter-code/curl-examples.sh`, write the `curl` commands for:
1. A `GET` request to `https://jsonplaceholder.typicode.com/posts/1`
2. A `POST` request to `https://jsonplaceholder.typicode.com/posts` with JSON body `{"title":"foo","body":"bar","userId":1}` and `Content-Type: application/json` header
3. A `PUT` request to update post 1 with a full replacement body
4. A `PATCH` request to update only the `title` of post 1
5. A `DELETE` request to delete post 1

## Hints
- The HTTP method describes the *intent* of the request — GET retrieves, POST creates, PUT replaces, PATCH partially updates, DELETE removes.
- **Idempotency**: calling GET or DELETE 10 times produces the same final server state as calling it once. POST is not idempotent — each call creates a new resource.
- In curl, use `-X METHOD` for non-GET methods, `-H "Header: value"` for headers, and `-d 'body'` or `--data` for the request body.
- The `Location` header in a 201 response tells the client where the newly created resource lives.

## Expected Output

Running the GET curl command:
```bash
curl https://jsonplaceholder.typicode.com/posts/1
# Returns JSON with id, title, body, userId fields
```

Running the POST curl command should return something like:
```json
{
  "title": "foo",
  "body": "bar",
  "userId": 1,
  "id": 101
}
```

Running the DELETE curl command returns:
```json
{}
```
(JSONPlaceholder simulates deletion — empty body with 200 OK)

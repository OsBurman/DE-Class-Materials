# Exercise 02: HTTP Status Codes and Headers

## Objective
Identify the correct HTTP status code for common API scenarios and understand the role of the most important request and response headers.

## Background
Status codes tell the client *what happened* on the server — success, redirection, client error, or server error. Headers carry metadata that controls how the request and response are interpreted: content format, caching, authentication, CORS, and more. Every REST API you build or consume relies on these building blocks.

## Requirements

**Part 1 — Status Code Classification**

In `starter-code/worksheet.md`, classify each of the following scenarios by:
- The correct HTTP status code
- The reason phrase
- The code family (2xx, 3xx, 4xx, 5xx) and what each family means

Scenarios to classify:
1. A `GET /books/42` request where book 42 exists — return the book data ✅
2. A `POST /books` request that successfully creates a new book 🆕
3. A `DELETE /books/42` request that succeeds but returns no body ✅
4. A `GET /books` request that is permanently redirected to `/api/v1/books`
5. A `GET /books/42` request where book 42 does not exist ❌
6. A `POST /books` request that is missing the required `title` field ❌
7. A `POST /books` request with a duplicate ISBN that already exists ❌
8. An `Authorization` header with an expired or invalid token ❌
9. A valid request to `DELETE /admin/users` from a user who lacks admin rights ❌
10. A server-side database error during `GET /books` ❌

**Part 2 — Header Reference Guide**

In `starter-code/worksheet.md`, complete the description for each header:

*Request headers:*
- `Content-Type: application/json`
- `Accept: application/json`
- `Authorization: Bearer <token>`
- `Authorization: Basic <base64>`
- `X-Request-ID: abc-123`

*Response headers:*
- `Content-Type: application/json`
- `Location: /api/v1/books/42`
- `Cache-Control: no-cache`
- `ETag: "abc123"`
- `WWW-Authenticate: Bearer realm="api"`
- `Access-Control-Allow-Origin: *`

For each header, describe: what it does, which side (request/response) sends it, and when you would use it.

**Part 3 — Match the Status Code**

In `starter-code/worksheet.md`, draw lines (or fill in the table) matching each description to its status code:

| Description | Status Code |
|---|---|
| Request succeeded, body contains the resource | ? |
| Resource created, Location header present | ? |
| Request succeeded, no content to return | ? |
| Malformed request syntax | ? |
| Authentication required | ? |
| Authenticated but not authorised | ? |
| Resource not found | ? |
| Method not allowed on this endpoint | ? |
| Conflict with existing resource state | ? |
| Unprocessable entity (validation failure) | ? |
| Server threw an unexpected exception | ? |
| Service is temporarily unavailable | ? |

Status codes to use: 200, 201, 204, 400, 401, 403, 404, 405, 409, 422, 500, 503

## Hints
- The 4xx family means the **client** made an error; the 5xx family means the **server** failed.
- 401 Unauthorized means "you haven't proved who you are yet"; 403 Forbidden means "I know who you are, but you can't do this."
- 204 No Content is the correct response for a successful DELETE when no body is returned.
- The `Content-Type` header appears on **both** requests (describing the body you're sending) and responses (describing the body you're receiving).

## Expected Output

Part 3 completed table example for two rows:

```
| Request succeeded, body contains the resource | 200 |
| Resource created, Location header present     | 201 |
```

Part 1 example for scenario 1:
```
Scenario 1: GET /books/42 (found)
  Status code: 200
  Reason phrase: OK
  Family: 2xx — Success: The request was received, understood, and completed.
```

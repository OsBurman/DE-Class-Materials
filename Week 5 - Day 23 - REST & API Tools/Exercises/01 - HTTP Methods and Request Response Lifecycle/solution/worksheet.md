# Exercise 01 — Solution: HTTP Methods and Request/Response Lifecycle

---

## Part 1A — Anatomy of an HTTP Request

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

| Component | Value from above | Description |
|---|---|---|
| HTTP Method | POST | Creates a new resource on the server; not idempotent |
| Request Target | /api/v1/books | The path on the server identifying the "books" collection resource |
| HTTP Version | HTTP/1.1 | Specifies the HTTP protocol version for connection/feature negotiation |
| Host header | api.library.com | Required in HTTP/1.1; identifies which virtual host should handle the request |
| Content-Type header | application/json | Tells the server the format of the request body so it can be parsed correctly |
| Authorization header | Bearer eyJhbGci... | "Bearer" is the token-based OAuth 2.0 scheme; the token proves the client's identity |
| Request Body | { "title": ... } | Present on POST, PUT, PATCH — carries the data the client wants to send to the server |

---

## Part 1B — Anatomy of an HTTP Response

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

| Component | Value from above | Description |
|---|---|---|
| HTTP Version | HTTP/1.1 | Protocol version used for this connection |
| Status Code | 201 | Numeric code: 201 = "Created". The 2xx family means success. |
| Reason Phrase | Created | Human-readable label for the status code (informational only) |
| Content-Type header | application/json | Tells the client the format of the response body |
| Location header | /api/v1/books/42 | Returned with 201 Created to tell the client the URL of the new resource |
| Response Body | { "id": 42, ... } | The newly created resource returned for the client to use |

---

## Part 2 — HTTP Method Semantics

| Method | Purpose | Idempotent? | Body? | Example URL | Example Body |
|---|---|---|---|---|---|
| GET | Retrieve a resource or list of resources | ✅ Yes | ❌ No | `GET /api/v1/books/42` | N/A |
| POST | Create a new resource | ❌ No | ✅ Yes | `POST /api/v1/books` | `{"title":"1984","author":"Orwell"}` |
| PUT | Fully replace an existing resource | ✅ Yes | ✅ Yes | `PUT /api/v1/books/42` | `{"id":42,"title":"1984","author":"Orwell","year":1949}` |
| PATCH | Partially update an existing resource | ✅ Yes* | ✅ Yes | `PATCH /api/v1/books/42` | `{"title":"Nineteen Eighty-Four"}` |
| DELETE | Remove a resource | ✅ Yes | ❌ No | `DELETE /api/v1/books/42` | N/A |

*PATCH is technically idempotent when the patch is deterministic (e.g. "set title to X"), but this depends on implementation.

---

## Part 3 — Request/Response Lifecycle

`GET /api/v1/books/1` step by step:

1. **Client initiates TCP connection** — The browser/app performs a TCP three-way handshake (SYN, SYN-ACK, ACK) with the server. For HTTPS, a TLS handshake follows to establish an encrypted channel.
2. **Client sends HTTP request** — The client formats and transmits the request: `GET /api/v1/books/1 HTTP/1.1\r\nHost: api.library.com\r\n\r\n`
3. **Server receives and parses request** — The server reads the raw bytes, extracts the method (`GET`), path (`/api/v1/books/1`), protocol version, and all headers.
4. **Server processes request** — The web framework routes the request to the correct controller method, which performs a database lookup for book with id=1 and constructs a response object.
5. **Server sends HTTP response** — The server serialises the result to JSON, prepends the status line and headers (`HTTP/1.1 200 OK\r\nContent-Type: application/json\r\n\r\n`), and transmits the full response.
6. **Client receives and processes response** — The client reads the status code (200 = success), parses the JSON body, and renders the data to the user.

---

## Part 4 — curl Commands (Solutions)

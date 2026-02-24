# Exercise 02 — Solution: HTTP Status Codes and Headers

---

## Part 1 — Status Code Classification

**Scenario 1:** `GET /books/42` — book 42 exists
- Status code: **200**
- Reason phrase: **OK**
- Family: **2xx — Success**: The request was received, understood, and completed successfully.

**Scenario 2:** `POST /books` — successfully created
- Status code: **201**
- Reason phrase: **Created**
- Family: **2xx — Success**: New resource was created; the response should include a `Location` header.

**Scenario 3:** `DELETE /books/42` — success, no body
- Status code: **204**
- Reason phrase: **No Content**
- Family: **2xx — Success**: Operation succeeded but there is no body to return.

**Scenario 4:** `GET /books` — permanently moved
- Status code: **301**
- Reason phrase: **Moved Permanently**
- Family: **3xx — Redirection**: The client must repeat the request at the new URL. The new URL is in the `Location` header.

**Scenario 5:** `GET /books/42` — not found
- Status code: **404**
- Reason phrase: **Not Found**
- Family: **4xx — Client Error**: The client made a request for a resource that doesn't exist. The client should fix the URL.

**Scenario 6:** `POST /books` — missing required `title` (validation error)
- Status code: **422** (or 400 — both are acceptable; 422 is more precise for semantic validation failures)
- Reason phrase: **Unprocessable Entity**
- Family: **4xx — Client Error**: The request was well-formed but the data didn't pass business validation.

**Scenario 7:** `POST /books` — duplicate ISBN
- Status code: **409**
- Reason phrase: **Conflict**
- Family: **4xx — Client Error**: The request conflicts with the current state of the resource (duplicate key).

**Scenario 8:** Expired or invalid `Authorization` token
- Status code: **401**
- Reason phrase: **Unauthorized** (confusingly named — really means "unauthenticated")
- Family: **4xx — Client Error**: The request lacks valid authentication credentials. The server responds with `WWW-Authenticate`. **Difference from 403**: 401 = "I don't know who you are"; 403 = "I know who you are, but you can't do this."

**Scenario 9:** Authenticated but lacks admin role
- Status code: **403**
- Reason phrase: **Forbidden**
- Family: **4xx — Client Error**: The server understood the request and the client's identity, but access is denied.

**Scenario 10:** Server NullPointerException
- Status code: **500**
- Reason phrase: **Internal Server Error**
- Family: **5xx — Server Error**: The server failed to fulfil a valid request. This is a bug on the server side. Never expose stack traces to clients.

---

## Part 2 — Header Reference Guide

### Request Headers

**`Content-Type: application/json`**
- What it does: Tells the server the media type (format) of the request body being sent.
- Sent by: Client
- When used: Any request with a body — POST, PUT, PATCH.

**`Accept: application/json`**
- What it does: Tells the server which response format the client prefers.
- Sent by: Client
- When used: Every request. If the server can't respond in that format, it returns 406 Not Acceptable.

**`Authorization: Bearer <token>`**
- What it does: Sends an OAuth 2.0 / JWT token to prove the client's identity.
- Sent by: Client
- When used: Every request to a protected endpoint.

**`Authorization: Basic <base64>`**
- What it does: Sends base64-encoded `username:password` for HTTP Basic authentication.
- Sent by: Client
- When used: Simple auth schemes (dev environments, internal tools). NOT recommended over plain HTTP — always use HTTPS.

**`X-Request-ID: abc-123`**
- What it does: A custom correlation ID that flows through microservices for distributed tracing and log correlation.
- Sent by: Client (or injected by API gateway)
- When used: Production APIs for observability and debugging distributed systems.

### Response Headers

**`Content-Type: application/json`**
- What it does: Tells the client the format of the response body so it can parse it correctly.
- Sent by: Server
- When used: Any response with a body.

**`Location: /api/v1/books/42`**
- What it does: Provides the URL of a newly created or redirected resource.
- Sent by: Server
- When used: 201 Created (URL of new resource), 301/302 Redirects (new URL).

**`Cache-Control: no-cache`**
- What it does: Instructs clients and proxies not to serve a cached copy without revalidating with the server.
- Sent by: Server
- When used: Endpoints returning dynamic data that must always be fresh.

**`ETag: "abc123"`**
- What it does: A version token for the resource. Clients can send `If-None-Match: "abc123"` on future requests; if unchanged, the server returns 304 Not Modified (no body, saving bandwidth).
- Sent by: Server
- When used: GET responses on resources that clients may cache.

**`WWW-Authenticate: Bearer realm="api"`**
- What it does: Tells the client what authentication scheme to use when a 401 is returned.
- Sent by: Server
- When used: Paired with 401 Unauthorized responses.

**`Access-Control-Allow-Origin: *`**
- What it does: A CORS header that tells browsers which origins (domains) are permitted to read the response via JavaScript.
- Sent by: Server
- When used: APIs that need to be consumed from web frontends on different domains.

---

## Part 3 — Status Code Match (Answers)

| Description | Status Code |
|---|---|
| Request succeeded, body contains the resource | **200** |
| Resource created, Location header present | **201** |
| Request succeeded, no content to return | **204** |
| Malformed request syntax | **400** |
| Authentication required (no valid credentials) | **401** |
| Authenticated but not authorised for this action | **403** |
| Resource not found | **404** |
| Method not allowed on this endpoint | **405** |
| Conflict with existing resource state | **409** |
| Unprocessable entity (validation failure) | **422** |
| Server threw an unexpected exception | **500** |
| Service is temporarily unavailable | **503** |

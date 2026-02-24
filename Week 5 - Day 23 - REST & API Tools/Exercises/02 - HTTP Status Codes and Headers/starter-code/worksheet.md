# Exercise 02 — Worksheet: HTTP Status Codes and Headers

---

## Part 1 — Status Code Classification

For each scenario, fill in the Status Code, Reason Phrase, and Family explanation.

**Scenario 1:** `GET /books/42` — book 42 exists, return the book
- Status code: TODO
- Reason phrase: TODO
- Family: TODO — What does the 2xx family mean?

**Scenario 2:** `POST /books` — successfully created a new book
- Status code: TODO
- Reason phrase: TODO
- Family: TODO

**Scenario 3:** `DELETE /books/42` — success, no body returned
- Status code: TODO
- Reason phrase: TODO
- Family: TODO

**Scenario 4:** `GET /books` — endpoint has permanently moved to `/api/v1/books`
- Status code: TODO
- Reason phrase: TODO
- Family: TODO — What does the 3xx family mean?

**Scenario 5:** `GET /books/42` — book 42 does not exist
- Status code: TODO
- Reason phrase: TODO
- Family: TODO — What does the 4xx family mean?

**Scenario 6:** `POST /books` — missing required `title` field (validation error)
- Status code: TODO
- Reason phrase: TODO
- Family: TODO

**Scenario 7:** `POST /books` — ISBN already exists in the database (conflict)
- Status code: TODO
- Reason phrase: TODO
- Family: TODO

**Scenario 8:** Request has an expired or invalid `Authorization` token
- Status code: TODO
- Reason phrase: TODO
- Family: TODO — What is the difference between this and 403?

**Scenario 9:** `DELETE /admin/users` — user is authenticated but lacks admin role
- Status code: TODO
- Reason phrase: TODO
- Family: TODO

**Scenario 10:** `GET /books` — server throws a NullPointerException
- Status code: TODO
- Reason phrase: TODO
- Family: TODO — What does the 5xx family mean?

---

## Part 2 — Header Reference Guide

### Request Headers

**`Content-Type: application/json`**
- What it does: TODO
- Sent by: TODO (client / server / both)
- When used: TODO

**`Accept: application/json`**
- What it does: TODO
- Sent by: TODO
- When used: TODO

**`Authorization: Bearer <token>`**
- What it does: TODO
- Sent by: TODO
- When used: TODO

**`Authorization: Basic <base64>`**
- What it does: TODO
- Sent by: TODO
- When used: TODO

**`X-Request-ID: abc-123`**
- What it does: TODO
- Sent by: TODO
- When used: TODO

### Response Headers

**`Content-Type: application/json`**
- What it does: TODO
- Sent by: TODO
- When used: TODO

**`Location: /api/v1/books/42`**
- What it does: TODO
- Sent by: TODO
- When used: TODO (hint: 201 Created, 301/302 redirects)

**`Cache-Control: no-cache`**
- What it does: TODO
- Sent by: TODO
- When used: TODO

**`ETag: "abc123"`**
- What it does: TODO
- Sent by: TODO
- When used: TODO

**`WWW-Authenticate: Bearer realm="api"`**
- What it does: TODO
- Sent by: TODO
- When used: TODO (hint: paired with which status code?)

**`Access-Control-Allow-Origin: *`**
- What it does: TODO
- Sent by: TODO
- When used: TODO (hint: CORS)

---

## Part 3 — Match the Status Code

Fill in the correct status code (choose from: 200, 201, 204, 400, 401, 403, 404, 405, 409, 422, 500, 503)

| Description | Status Code |
|---|---|
| Request succeeded, body contains the resource | TODO |
| Resource created, Location header present | TODO |
| Request succeeded, no content to return | TODO |
| Malformed request syntax | TODO |
| Authentication required (no valid credentials) | TODO |
| Authenticated but not authorised for this action | TODO |
| Resource not found | TODO |
| Method not allowed on this endpoint | TODO |
| Conflict with existing resource state (e.g. duplicate) | TODO |
| Unprocessable entity (passed validation structure, failed business rules) | TODO |
| Server threw an unexpected exception | TODO |
| Service is temporarily unavailable (overloaded / maintenance) | TODO |

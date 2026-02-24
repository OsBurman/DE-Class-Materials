# Day 23 Part 2 — Postman & Swagger/OpenAPI
## Slide Descriptions

**Total slides: 16**

---

### Running Example

Continuing from Part 1 — the Book Store API. All Postman demos use requests to the Book Store API. All OpenAPI YAML examples describe the Book Store API's `/books` and `/authors` endpoints. Students finish Part 2 with a complete OpenAPI spec for the API they've been designing all day.

---

### Slide 1 — Title Slide

**Title:** REST & API Tools
**Subtitle:** Postman · Environment Variables · Test Automation · Swagger / OpenAPI 3.0
**Part:** 2 of 2

**Objectives listed on slide:**
- Navigate the Postman interface and build requests
- Use environment variables to manage multiple environments
- Organize requests into collections
- Write automated tests with `pm.test()` and `pm.expect()`
- Run a collection with the Collection Runner
- Understand OpenAPI 3.0 document structure
- Define paths, operations, parameters, and schemas in YAML
- Write reusable schemas with `$ref` and `components`
- Design consistent error responses across all endpoints

---

### Slide 2 — Postman Interface Overview

**Title:** Postman — Your API Testing Workbench

**Main interface regions:**

```
┌─────────────────────────────────────────────────────────┐
│  Sidebar                │  Main Request Builder         │
│  ├─ Collections         │  ┌───────────────────────┐   │
│  │  └─ Book Store API   │  │  Method ▾  URL         │   │
│  │     ├─ GET Books     │  ├───────────────────────┤   │
│  │     ├─ POST Book     │  │  Params│Auth│Headers│  │   │
│  │     └─ GET Book/:id  │  │  Body│Pre-req│Tests  │   │
│  ├─ Environments        │  └───────────────────────┘   │
│  │  └─ Dev / Staging    │  ┌───────────────────────┐   │
│  └─ History             │  │  Response              │   │
│                         │  │  200 OK · 234ms · 1KB  │   │
│                         │  │  Body│Cookies│Headers  │   │
│                         └──┴───────────────────────┘   │
└─────────────────────────────────────────────────────────┘
```

**Request builder tabs:**

| Tab | Purpose |
|---|---|
| **Params** | Add query string parameters visually (auto-builds `?key=value`) |
| **Authorization** | Configure auth type (Bearer Token, Basic, API Key, etc.) |
| **Headers** | Add/edit request headers |
| **Body** | Select body type and enter body content |
| **Pre-request Script** | JavaScript that runs before the request is sent |
| **Tests** | JavaScript assertions that run after the response arrives |
| **Settings** | Request-specific options (SSL, follow redirects, timeouts) |

**Response panel:**
- **Status code** — highlighted in green (2xx) or red (4xx/5xx)
- **Time** — how long the request took in milliseconds
- **Size** — response body size in KB
- **Body tab** — rendered JSON (Pretty view) or raw text
- **Headers tab** — all response headers
- **Cookies tab** — any `Set-Cookie` headers

---

### Slide 3 — Building Your First Request

**Title:** Building a Request in Postman — Step by Step

**Building a GET request:**
1. Click `New Request` (+ tab)
2. Select `GET` from the method dropdown
3. Enter URL: `{{baseUrl}}/api/v1/books`
4. Click the **Params** tab — add `genre=fiction`, `limit=10` visually
5. Click **Send**
6. View response body in Pretty JSON view

**Building a POST request:**
1. Select `POST` from method dropdown
2. URL: `{{baseUrl}}/api/v1/books`
3. Click the **Authorization** tab → Type: `Bearer Token` → paste `{{authToken}}`
4. Click the **Headers** tab — `Content-Type: application/json` (auto-added when you choose JSON body)
5. Click the **Body** tab → select **raw** → select **JSON** from dropdown
6. Enter body:
```json
{
  "title": "Clean Code",
  "authorId": 42,
  "price": 39.99,
  "genre": "technology"
}
```
7. Click **Send** → expect `201 Created` with `Location` header

**Reading the response:**
- Check status code badge (green 201, red 4xx, etc.)
- Check response time (should be < 500ms for local dev)
- Switch to **Headers** tab — find `Location: /api/v1/books/1337`
- Switch to **Cookies** tab — any auth cookies set?

**Saving a request:**
- Click **Save** → assign to a collection → give it a descriptive name like `POST - Create Book`

---

### Slide 4 — Environment Variables

**Title:** Environment Variables — One Request, Multiple Environments

**The problem:** Your API runs on three URLs depending on environment:
```
Development:  http://localhost:8080
Staging:      https://staging-api.bookstore.com
Production:   https://api.bookstore.com
```

**Without variables:** You'd have to manually edit the URL in every request before sending. With 50 requests in your collection, this is untenable.

**The solution — Environment Variables:**

```
Create Environment: "Book Store - Dev"
  baseUrl  =  http://localhost:8080
  authToken = eyJhbGciOiJIUzI1NiJ9.dev.token

Create Environment: "Book Store - Staging"
  baseUrl  =  https://staging-api.bookstore.com
  authToken = eyJhbGciOiJIUzI1NiJ9.staging.token
```

**In requests, use `{{variableName}}` syntax:**
```
URL:           {{baseUrl}}/api/v1/books
Authorization: Bearer {{authToken}}
Header value:  {{apiKey}}
```

**Switch environments:** Top-right environment dropdown → select "Dev" or "Staging" → all `{{variables}}` update instantly across every request.

**Variable scopes (widest to narrowest):**

| Scope | Where set | Accessible from |
|---|---|---|
| **Global** | Postman Globals panel | Every workspace, every collection |
| **Collection** | Collection variables tab | Requests in that collection |
| **Environment** | Environment panel | Active requests when env is selected |
| **Local** | Pre-request script (temp) | Current request only |

**Narrower scope overrides wider scope.** A collection variable named `baseUrl` overrides a global named `baseUrl`.

**Best practice:** Never hardcode tokens, URLs, or IDs in requests. Always use `{{variables}}`. This makes your collection shareable with teammates — they import and set their own environment values.

---

### Slide 5 — Collections and Organization

**Title:** Collections — Organizing Your API Workspace

**What is a collection?** A collection is a named group of saved requests — the equivalent of a folder for your API. A collection can contain subfolders to group related endpoints.

**Book Store API collection structure:**
```
📁 Book Store API
  📂 Books
    📄 GET - List Books (with pagination)
    📄 GET - Get Book by ID
    📄 POST - Create Book
    📄 PUT - Replace Book
    📄 PATCH - Update Book Price
    📄 DELETE - Delete Book
  📂 Authors
    📄 GET - List Authors
    📄 GET - Books by Author
    📄 POST - Create Author
  📂 Orders
    📄 POST - Place Order
    📄 GET - Get Order
    📄 GET - Order Items
  📂 Auth
    📄 POST - Login (saves token to environment)
    📄 POST - Refresh Token
```

**Collection-level Authorization:** Set a default auth type on the collection so every request inherits it. Individual requests can override the collection default. No more repeating Bearer token on every request.

**Export and Import:** Collections can be exported as JSON files and shared. Teammates import the JSON and swap in their own environment values. This is how you share API test suites across a team.

**Postman Workspaces:** Personal, Team, or Public. Team workspaces allow real-time collaboration — multiple developers working on the same collection.

---

### Slide 6 — Pre-Request Scripts

**Title:** Pre-Request Scripts — Automating Setup

**What is a pre-request script?** JavaScript code that runs before the request is sent. You use it to dynamically set variables, generate test data, or perform setup steps.

**Postman sandbox APIs:**
```javascript
// Set an environment variable programmatically
pm.environment.set("timestamp", new Date().toISOString());

// Set a global variable
pm.globals.set("sessionId", "abc123");

// Get a variable
const token = pm.environment.get("authToken");

// Generate a random number for test data
pm.environment.set("randomPrice", Math.random() * 100 + 1);

// Set a dynamic request header
pm.request.headers.add({
    key: "X-Request-Id",
    value: "req_" + Date.now()
});
```

**Common use case — auto-login before tests:**
```javascript
// Pre-request script on the collection level:
// If authToken is expired or missing, call login first
const tokenExpiry = pm.environment.get("tokenExpiry");
const now = Date.now();

if (!tokenExpiry || now > parseInt(tokenExpiry)) {
    pm.sendRequest({
        url: pm.environment.get("baseUrl") + "/api/v1/auth/login",
        method: "POST",
        header: { "Content-Type": "application/json" },
        body: {
            mode: "raw",
            raw: JSON.stringify({
                username: pm.environment.get("testUser"),
                password: pm.environment.get("testPassword")
            })
        }
    }, (err, response) => {
        const json = response.json();
        pm.environment.set("authToken", json.accessToken);
        pm.environment.set("tokenExpiry", now + 3600000); // 1 hour
    });
}
```

---

### Slide 7 — Writing Tests with pm.test and pm.expect

**Title:** Response Assertions — Automated API Testing in Postman

**Postman Tests tab:** JavaScript code that runs after the response arrives. Tests appear in the Test Results tab at the bottom.

**`pm.test()` signature:**
```javascript
pm.test("test name", function () {
    // assertion code
    // throw = test fails
    // no throw = test passes
});
```

**`pm.expect()` — Chai assertion library:**

```javascript
// 1. Check status code
pm.test("Status code is 200", function () {
    pm.response.to.have.status(200);
});

// 2. Check a specific header
pm.test("Content-Type is JSON", function () {
    pm.response.to.have.header("Content-Type");
    pm.expect(pm.response.headers.get("Content-Type"))
      .to.include("application/json");
});

// 3. Parse and check response body
pm.test("Response has correct title", function () {
    const body = pm.response.json();
    pm.expect(body.title).to.equal("Clean Code");
    pm.expect(body.price).to.be.a("number");
    pm.expect(body.id).to.exist;
});

// 4. Check response time
pm.test("Response time is under 500ms", function () {
    pm.expect(pm.response.responseTime).to.be.below(500);
});

// 5. Check schema structure
pm.test("Response has required fields", function () {
    const body = pm.response.json();
    pm.expect(body).to.have.all.keys("id", "title", "price", "genre");
});

// 6. Save value for later requests
pm.test("Save created book ID", function () {
    const body = pm.response.json();
    pm.environment.set("createdBookId", body.id);
});
```

**Test Results panel:** Shows green checkmarks for passing tests, red X for failures. Run the entire collection and see a pass/fail count.

---

### Slide 8 — Collection Runner and Newman CLI

**Title:** Collection Runner — Testing the Full Workflow

**Collection Runner:** Run every request in a collection sequentially, in order. Useful for integration testing a complete workflow.

**Book Store API end-to-end test flow:**
```
1. POST /auth/login           → saves authToken to environment
2. POST /books                → creates a book, saves bookId
3. GET  /books/{{bookId}}     → verifies book was created correctly
4. PATCH /books/{{bookId}}    → updates price
5. GET  /books/{{bookId}}     → verifies price changed
6. DELETE /books/{{bookId}}   → deletes book
7. GET  /books/{{bookId}}     → verifies 404 is returned
```

Each request has tests that must pass before the next runs. If step 2 fails, step 3 never executes.

**Data-driven testing with CSV/JSON files:**
```csv
title,authorId,price,genre
Clean Code,42,39.99,technology
Design Patterns,15,44.99,technology
The Pragmatic Programmer,7,49.99,technology
```
The runner iterates over each row, running all requests with that row's values. Creates three books, verifies each, deletes each.

**Newman — Postman's CLI runner:**
```bash
# Install globally
npm install -g newman

# Run a collection
newman run BookStoreAPI.postman_collection.json \
  -e BookStore-Dev.postman_environment.json

# Run with data file
newman run BookStoreAPI.postman_collection.json \
  -e BookStore-Dev.postman_environment.json \
  -d books-test-data.csv

# Generate HTML report
newman run BookStoreAPI.postman_collection.json \
  -e BookStore-Dev.postman_environment.json \
  -r htmlextra
```

**Newman in CI/CD pipelines:** Add the `newman run` command to your GitHub Actions or Jenkins pipeline. Every push runs the API test suite. If any test fails, the pipeline fails. This is automated API regression testing — and it's something you can put directly on your resume.

---

### Slide 9 — Swagger/OpenAPI Introduction

**Title:** Swagger / OpenAPI — Machine-Readable API Contracts

**The problem without OpenAPI:** Your team builds an API. Frontend developers ask: what endpoints exist? What fields does POST /books require? What does the error response look like? Without documentation, they have to read your source code or ask you constantly.

**OpenAPI is the solution:** A machine-readable description of your REST API. A single YAML or JSON file that describes every endpoint, every parameter, every request body, every response.

**Brief history:**
- **2011** — Tony Tam creates Swagger as an open source project at Wordnik
- **2015** — Swagger donated to the Linux Foundation and renamed **OpenAPI Specification (OAS)**
- **2017** — OpenAPI 3.0 released — major rewrite, cleaner structure
- **2021** — OpenAPI 3.1 released — aligns fully with JSON Schema 2020-12

**What OpenAPI gives you:**

| Capability | How |
|---|---|
| **Interactive docs** | Swagger UI renders the spec as a "Try it out" web page |
| **Client SDK generation** | Tools like openapi-generator create client libraries in 50+ languages |
| **Server stub generation** | Generate controller skeletons from the spec |
| **Mock servers** | Tools like Prism serve mock responses from the spec |
| **Contract testing** | Validate real responses against the spec automatically |
| **Design-first workflow** | Write the spec before writing code — align frontend/backend teams early |

**YAML vs JSON:** OpenAPI files can be written in either. YAML is more readable for humans (less noise, no braces), JSON is easier for programmatic consumption. We'll use YAML.

**Note:** Spring Boot integration using `springdoc-openapi` — auto-generating an OpenAPI spec from your annotations — is covered on Day 26 when we implement the controllers. Today we write the spec by hand.

---

### Slide 10 — OpenAPI 3.0 Document Structure

**Title:** OpenAPI 3.0 — Top-Level Structure

**A complete minimal OpenAPI 3.0 document:**
```yaml
openapi: 3.0.3

info:
  title: Book Store API
  description: REST API for managing books, authors, and orders
  version: 1.0.0
  contact:
    name: Book Store Team
    email: api@bookstore.com

servers:
  - url: http://localhost:8080
    description: Local development
  - url: https://staging-api.bookstore.com
    description: Staging
  - url: https://api.bookstore.com
    description: Production

paths:
  /api/v1/books:
    get:      # GET /api/v1/books
      ...
    post:     # POST /api/v1/books
      ...

  /api/v1/books/{bookId}:
    get:      # GET /api/v1/books/{bookId}
      ...
    put:      # PUT /api/v1/books/{bookId}
      ...
    patch:    # PATCH /api/v1/books/{bookId}
      ...
    delete:   # DELETE /api/v1/books/{bookId}
      ...

components:
  schemas:
    Book:         # reusable schema definition
      ...
    ErrorResponse:
      ...
  securitySchemes:
    bearerAuth:
      type: http
      scheme: bearer
      bearerFormat: JWT
```

**Top-level fields:**

| Field | Required | Purpose |
|---|---|---|
| `openapi` | ✅ | Version of the OpenAPI spec (`3.0.3`) |
| `info` | ✅ | API metadata — title, version, description |
| `servers` | ❌ | Base URLs for the API |
| `paths` | ✅ | The endpoints — the heart of the spec |
| `components` | ❌ | Reusable schemas, parameters, responses, security schemes |
| `security` | ❌ | Default security requirements for all operations |
| `tags` | ❌ | Groups for organizing operations in documentation |

---

### Slide 11 — Defining Paths and Operations

**Title:** Paths and Operations — Describing Your Endpoints

**A full path item with operations:**
```yaml
paths:
  /api/v1/books:

    get:
      operationId: listBooks
      summary: List all books
      description: Returns a paginated list of books with optional filters
      tags:
        - Books
      parameters:
        - name: genre
          in: query
          description: Filter by genre
          required: false
          schema:
            type: string
        - name: page
          in: query
          schema:
            type: integer
            default: 0
        - name: size
          in: query
          schema:
            type: integer
            default: 20
      responses:
        "200":
          description: Successfully retrieved book list
          content:
            application/json:
              schema:
                $ref: "#/components/schemas/BookListResponse"
        "500":
          $ref: "#/components/responses/InternalServerError"
      security:
        - bearerAuth: []

    post:
      operationId: createBook
      summary: Create a new book
      tags:
        - Books
      requestBody:
        required: true
        content:
          application/json:
            schema:
              $ref: "#/components/schemas/CreateBookRequest"
      responses:
        "201":
          description: Book created successfully
          headers:
            Location:
              schema:
                type: string
              description: URL of the newly created book
          content:
            application/json:
              schema:
                $ref: "#/components/schemas/Book"
        "400":
          $ref: "#/components/responses/BadRequest"
        "401":
          $ref: "#/components/responses/Unauthorized"
      security:
        - bearerAuth: []
```

**Operation fields:**

| Field | Purpose |
|---|---|
| `operationId` | Unique identifier — used in SDK generation as method name |
| `summary` | Short one-line description |
| `description` | Detailed description, can use Markdown |
| `tags` | Groups operations in Swagger UI |
| `parameters` | Path, query, header, or cookie parameters |
| `requestBody` | The request body for POST/PUT/PATCH |
| `responses` | Response definitions keyed by status code |
| `security` | Override global security for this operation |

---

### Slide 12 — Parameters in Depth

**Title:** Parameters — Describing What the Client Sends

**Four parameter locations:**

| `in:` value | Where the value appears | Example |
|---|---|---|
| `path` | URL path segment | `/books/{bookId}` |
| `query` | URL query string | `/books?genre=fiction` |
| `header` | HTTP request header | `X-Request-Id: req_abc` |
| `cookie` | Cookie | `session=abc` |

**Path parameter example:**
```yaml
paths:
  /api/v1/books/{bookId}:
    get:
      operationId: getBookById
      summary: Get a book by ID
      tags:
        - Books
      parameters:
        - name: bookId
          in: path
          required: true          # path params are always required
          description: The unique identifier of the book
          schema:
            type: integer
            format: int64
            example: 42
      responses:
        "200":
          description: Book found
          content:
            application/json:
              schema:
                $ref: "#/components/schemas/Book"
        "404":
          $ref: "#/components/responses/NotFound"
```

**Query parameter with validation:**
```yaml
- name: page
  in: query
  required: false
  schema:
    type: integer
    minimum: 0
    default: 0
    example: 2

- name: size
  in: query
  required: false
  schema:
    type: integer
    minimum: 1
    maximum: 100
    default: 20

- name: sort
  in: query
  schema:
    type: string
    enum: [title, price, createdAt]
    default: title
```

**Rules:**
- Path parameters: always `required: true` (they're part of the URL, they can't be omitted)
- Query parameters: default to `required: false`
- Use `schema.enum` to constrain allowed values
- Use `schema.minimum`, `schema.maximum`, `schema.pattern` for validation constraints

---

### Slide 13 — Schemas and $ref

**Title:** Schemas and Components — Reusable Definitions

**The problem without `$ref`:** If you copy-paste the Book schema into every operation that returns a book, you'll have to update it in 20 places when a field changes.

**The solution — `components/schemas` and `$ref`:**

```yaml
components:
  schemas:

    Book:
      type: object
      required:
        - id
        - title
        - authorId
        - price
        - genre
      properties:
        id:
          type: integer
          format: int64
          readOnly: true
          example: 42
        title:
          type: string
          minLength: 1
          maxLength: 255
          example: "Clean Code"
        authorId:
          type: integer
          format: int64
          example: 7
        price:
          type: number
          format: double
          minimum: 0.01
          example: 39.99
        genre:
          type: string
          enum: [fiction, non-fiction, technology, biography, science]
          example: technology
        createdAt:
          type: string
          format: date-time
          readOnly: true
          example: "2026-02-23T14:30:00Z"

    CreateBookRequest:
      type: object
      required:
        - title
        - authorId
        - price
        - genre
      properties:
        title:
          type: string
          minLength: 1
          maxLength: 255
        authorId:
          type: integer
          format: int64
        price:
          type: number
          format: double
          minimum: 0.01
        genre:
          type: string
          enum: [fiction, non-fiction, technology, biography, science]
```

**Using `$ref` in operations:**
```yaml
requestBody:
  content:
    application/json:
      schema:
        $ref: "#/components/schemas/CreateBookRequest"   # ← reference, not copy

responses:
  "200":
    content:
      application/json:
        schema:
          $ref: "#/components/schemas/Book"             # ← reused everywhere
```

**Common schema types and formats:**

| `type` | `format` | Java equivalent |
|---|---|---|
| `integer` | `int32` | `int` / `Integer` |
| `integer` | `int64` | `long` / `Long` |
| `number` | `float` | `float` / `Float` |
| `number` | `double` | `double` / `Double` |
| `string` | — | `String` |
| `string` | `date-time` | `LocalDateTime` / `ZonedDateTime` |
| `string` | `date` | `LocalDate` |
| `boolean` | — | `boolean` / `Boolean` |
| `array` | — | `List<T>` |

---

### Slide 14 — Response Definitions and Error Schemas

**Title:** Response Definitions — Reusable Responses

**Reusable responses in `components/responses`:**
```yaml
components:
  responses:

    BadRequest:
      description: Invalid request body or parameters
      content:
        application/json:
          schema:
            $ref: "#/components/schemas/ErrorResponse"

    Unauthorized:
      description: Missing or invalid authentication token
      content:
        application/json:
          schema:
            $ref: "#/components/schemas/ErrorResponse"

    Forbidden:
      description: Authenticated but not authorized
      content:
        application/json:
          schema:
            $ref: "#/components/schemas/ErrorResponse"

    NotFound:
      description: Resource not found
      content:
        application/json:
          schema:
            $ref: "#/components/schemas/ErrorResponse"

    InternalServerError:
      description: Unexpected server error
      content:
        application/json:
          schema:
            $ref: "#/components/schemas/ErrorResponse"

  schemas:
    ErrorResponse:
      type: object
      required:
        - status
        - error
        - message
        - timestamp
      properties:
        status:
          type: integer
          example: 404
        error:
          type: string
          example: "Not Found"
        message:
          type: string
          example: "Book with ID 9999 not found"
        path:
          type: string
          example: "/api/v1/books/9999"
        timestamp:
          type: string
          format: date-time
          example: "2026-02-23T14:30:00Z"
        details:
          type: array
          items:
            type: object
            properties:
              field:
                type: string
                example: "price"
              message:
                type: string
                example: "must be greater than 0"
```

**Using reusable responses in path items:**
```yaml
paths:
  /api/v1/books/{bookId}:
    get:
      responses:
        "200":
          description: Book found
          content:
            application/json:
              schema:
                $ref: "#/components/schemas/Book"
        "401":
          $ref: "#/components/responses/Unauthorized"    # ← reused
        "403":
          $ref: "#/components/responses/Forbidden"       # ← reused
        "404":
          $ref: "#/components/responses/NotFound"        # ← reused
        "500":
          $ref: "#/components/responses/InternalServerError"  # ← reused
```

---

### Slide 15 — Error Handling Design Patterns

**Title:** Error Handling — Consistent API Error Design

**The goal:** Every error from your API looks the same. Clients write error handling code once and it works for every endpoint.

**Standard error response envelope:**
```json
{
  "status": 422,
  "error": "Unprocessable Entity",
  "message": "Validation failed",
  "path": "/api/v1/books",
  "timestamp": "2026-02-23T14:30:00Z",
  "details": [
    {
      "field": "title",
      "message": "must not be blank"
    },
    {
      "field": "price",
      "message": "must be greater than 0"
    }
  ]
}
```

**HTTP status → error type mapping:**

| Status | `error` string | Trigger |
|---|---|---|
| 400 | Bad Request | Malformed JSON, wrong types |
| 401 | Unauthorized | No auth token / expired token |
| 403 | Forbidden | Valid token but wrong role/permission |
| 404 | Not Found | Resource ID doesn't exist |
| 405 | Method Not Allowed | Wrong HTTP method for endpoint |
| 409 | Conflict | Duplicate key, version mismatch |
| 422 | Unprocessable Entity | Business rule violation |
| 429 | Too Many Requests | Rate limit exceeded |
| 500 | Internal Server Error | Unhandled exception |

**Client-side error handling pseudocode:**
```javascript
async function apiCall(url, options) {
    const response = await fetch(url, options);
    if (!response.ok) {                         // not 2xx
        const error = await response.json();    // always JSON
        switch (response.status) {
            case 401: redirectToLogin(); break;
            case 403: showForbiddenMessage(); break;
            case 404: showNotFoundMessage(error.message); break;
            case 422: showValidationErrors(error.details); break;
            default:  showGenericError(error.message);
        }
        return null;
    }
    return response.json();
}
```

**In Spring Boot (Day 26 preview):** `@ControllerAdvice` with `@ExceptionHandler` methods map exceptions to these HTTP responses. `springdoc-openapi` auto-generates an OpenAPI spec from your controller annotations. Today you understand what that spec describes — on Day 26, you'll generate it automatically.

---

### Slide 16 — Part 2 Summary and Full Day Recap

**Title:** Day 23 Complete — Your API Tooling Reference

**Postman workflow:**
1. Create collection with folder structure
2. Set up Dev/Staging/Prod environments with `{{variables}}`
3. Configure collection-level auth (Bearer token)
4. Write tests in the Tests tab using `pm.test()` / `pm.expect()`
5. Run pre-request scripts for dynamic setup
6. Execute full workflow with Collection Runner
7. Automate in CI with Newman CLI

**OpenAPI 3.0 document structure:**
```
openapi: 3.0.3
info:       → API metadata
servers:    → Base URLs (dev/staging/prod)
paths:      → Endpoints
  /path:
    get/post/put/patch/delete:
      operationId, summary, tags
      parameters: [path, query, header, cookie]
      requestBody → content → schema → $ref
      responses → status codes → content → schema → $ref
components:
  schemas:    → Reusable data models
  responses:  → Reusable response definitions
  securitySchemes: → Auth configuration
```

**Error response template:**
```json
{
  "status": 404,
  "error": "Not Found",
  "message": "Resource description",
  "path": "/api/v1/resource/id",
  "timestamp": "ISO-8601 datetime",
  "details": []
}
```

**Coming up — Day 24:**
- Maven and Gradle build tools — dependency management, build lifecycle, plugins
- Spring Core — IoC container, beans, dependency injection, `@Component`, `@Autowired`
- Spring configuration with Java-based config
- ApplicationContext and the Spring container

Everything we designed today — the Book Store API's endpoints, request shapes, response shapes, and error format — you will implement as a real Spring Boot application over Days 24–27. The REST design is done. Now we build it.

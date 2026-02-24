# Postman Guide — API Testing & Automation
# Day 23 — REST & API Tools | Part 2

---

## Section 1 — Postman Interface and Features

Postman is the industry-standard GUI tool for designing, testing, and documenting REST APIs.
It lets you build and send HTTP requests without writing a single line of client code.

### Download and Installation

```
https://www.postman.com/downloads/
Available for: macOS, Windows, Linux
Browser version: https://web.postman.co (limited features)
```

### The Postman Interface — Key Areas

```
┌─────────────────────────────────────────────────────────────────────┐
│  SIDEBAR                    │  MAIN PANEL                           │
│  ──────────                 │  ─────────                            │
│  My Workspace               │  [ Method ▼] [ URL bar          ] SEND│
│  ├── Collections            │  ─────────────────────────────────────│
│  │   ├── 📁 Bookstore API   │  Params  Auth  Headers  Body  Pre-req │
│  │   │   ├── GET Books      │  Tests  Settings                      │
│  │   │   ├── POST Book      │  ─────────────────────────────────────│
│  │   │   └── GET Book by ID │                                       │
│  │   └── 📁 User API        │  RESPONSE                             │
│  ├── Environments           │  ─────────                            │
│  │   ├── 🌍 Development     │  Status: 200 OK  Time: 142ms          │
│  │   ├── 🌍 Staging         │  Size: 1.2 KB                         │
│  │   └── 🌍 Production      │  ─────────────────────────────────────│
│  └── History                │  Body  Cookies  Headers  Test Results │
└─────────────────────────────────────────────────────────────────────┘
```

### Core Concepts

| Concept | Description |
|---------|-------------|
| **Request** | An individual HTTP call with method, URL, headers, body, and tests |
| **Collection** | A folder of related requests. Like a project for your API. |
| **Environment** | A set of key-value variables scoped to a deployment environment (dev/staging/prod) |
| **Variable** | A reusable value referenced with `{{variableName}}` syntax |
| **Pre-request Script** | JavaScript that runs BEFORE the request is sent |
| **Tests** | JavaScript that runs AFTER the response arrives (assertions) |
| **Runner** | Execute all requests in a collection automatically (great for CI/CD) |

---

## Section 2 — Creating and Organizing API Requests

### Building Your First Request

**Step 1 — Select the HTTP method:**  
Click the method dropdown (defaults to GET). Select from GET, POST, PUT, PATCH, DELETE, etc.

**Step 2 — Enter the URL:**
```
https://jsonplaceholder.typicode.com/posts
```
*(JSONPlaceholder is a free fake API perfect for learning)*

**Step 3 — For GET requests, add query parameters:**  
Click the **Params** tab. Add key-value pairs:
```
Key: _limit    Value: 5
Key: userId    Value: 1
```
This builds the URL: `https://jsonplaceholder.typicode.com/posts?_limit=5&userId=1`

**Step 4 — For POST/PUT/PATCH, add a request body:**  
Click the **Body** tab → select **raw** → change the type dropdown to **JSON**.
```json
{
  "title": "Introduction to REST APIs",
  "body": "REST stands for Representational State Transfer...",
  "userId": 1
}
```

**Step 5 — Add headers (if needed):**  
Click the **Headers** tab. Common headers to add:
```
Content-Type:   application/json
Authorization:  Bearer {{authToken}}
Accept:         application/json
```

**Step 6 — Send the request:**  
Click the blue **Send** button.

### Saving Requests to Collections

1. After building a request, click **Save** (top right).
2. Choose or create a **Collection** (e.g., "Bookstore API").
3. Give the request a descriptive name (e.g., "GET All Posts" or "POST Create Post").

### Organizing Collections with Folders

Collections can have nested folders:
```
📁 Bookstore API
├── 📁 Books
│   ├── GET All Books
│   ├── GET Book by ID
│   ├── POST Create Book
│   ├── PUT Update Book
│   └── DELETE Book
├── 📁 Authors
│   ├── GET All Authors
│   └── GET Author by ID
└── 📁 Authentication
    ├── POST Login
    └── POST Refresh Token
```

### Practical Exercises Using Public APIs

```
JSONPlaceholder (no auth required):
  GET https://jsonplaceholder.typicode.com/posts
  GET https://jsonplaceholder.typicode.com/posts/1
  POST https://jsonplaceholder.typicode.com/posts
  PUT https://jsonplaceholder.typicode.com/posts/1
  DELETE https://jsonplaceholder.typicode.com/posts/1

Open Library API (no auth required):
  GET https://openlibrary.org/api/books?bibkeys=ISBN:0385472579&format=json

Dog API (no auth required):
  GET https://dog.ceo/api/breeds/image/random
  GET https://dog.ceo/api/breed/husky/images/random/3
```

---

## Section 3 — Environment Variables in Postman

Environment variables let you switch between dev/staging/production without editing every request.

### Creating an Environment

1. Click **Environments** in the sidebar → **+** to create new.
2. Name it (e.g., "Development").
3. Add variables:

```
Development Environment:
┌────────────────────┬────────────────────────────────────────┐
│ Variable           │ Initial Value (Current Value)          │
├────────────────────┼────────────────────────────────────────┤
│ baseUrl            │ http://localhost:8080/api/v1           │
│ authToken          │ (empty — will be set programmatically) │
│ userId             │ 1                                      │
└────────────────────┴────────────────────────────────────────┘

Staging Environment:
┌────────────────────┬────────────────────────────────────────┐
│ Variable           │ Initial Value                          │
├────────────────────┼────────────────────────────────────────┤
│ baseUrl            │ https://staging.myapp.com/api/v1       │
│ authToken          │ (empty)                                │
│ userId             │ 1                                      │
└────────────────────┴────────────────────────────────────────┘

Production Environment:
┌────────────────────┬────────────────────────────────────────┐
│ Variable           │ Initial Value                          │
├────────────────────┼────────────────────────────────────────┤
│ baseUrl            │ https://api.myapp.com/api/v1           │
│ authToken          │ (empty)                                │
│ userId             │ (set per test)                         │
└────────────────────┴────────────────────────────────────────┘
```

### Using Variables in Requests

```
URL:     {{baseUrl}}/products
Headers: Authorization: Bearer {{authToken}}
Body:    { "userId": {{userId}}, "name": "New Product" }
```

The double curly brace syntax `{{variableName}}` is resolved at runtime.
Unresolved variables turn **red** in the URL bar — a helpful warning.

### Variable Scopes (Hierarchy)

```
Global Variables    → Available across all collections and environments
Collection Variables → Scoped to a specific collection
Environment Variables → Scoped to the active environment (most common)
Local Variables     → Only exist within a single request's scripts
```

### Setting Variables Programmatically

In the **Tests** tab (runs after the response):
```javascript
// Parse the response
const response = pm.response.json();

// Store the token for subsequent requests
pm.environment.set("authToken", response.token);

// Store a created resource's ID for later use
pm.environment.set("createdBookId", response.id);

console.log("Token saved:", response.token);
```

In the **Pre-request Script** tab (runs before the request):
```javascript
// Generate a unique timestamp for idempotency
pm.environment.set("timestamp", new Date().toISOString());

// Build a dynamic value
const randomId = Math.floor(Math.random() * 10000);
pm.environment.set("randomUserId", randomId);
```

### Authentication — Postman Auth Tab

Instead of manually adding the `Authorization` header, use the **Auth** tab:

```
Type: Bearer Token → paste or reference {{authToken}}
Type: Basic Auth   → Username: {{username}}, Password: {{password}}
Type: API Key      → Key: X-API-Key, Value: {{apiKey}}, Add to: Header
Type: OAuth 2.0    → Full OAuth flow with authorization URLs
```

Setting auth at the **Collection level** propagates it to all child requests automatically.

---

## Section 4 — Collections and Test Automation

### The Collection Runner

The Collection Runner executes all requests in a collection sequentially.
Perfect for smoke testing an entire API after a deployment.

**How to run:**
1. Right-click a collection → **Run collection**
2. Configure: number of iterations, delay between requests, data file
3. Click **Run [Collection Name]**

### Data-Driven Testing with CSV/JSON Files

You can run the same collection with different input data:

```csv
bookTitle,bookIsbn,expectedStatus
"Clean Code","978-0132350884",201
"The Pragmatic Programmer","978-0201616224",201
"Design Patterns","978-0201633610",201
```

In the request body, reference the file columns:
```json
{
  "title": "{{bookTitle}}",
  "isbn": "{{bookIsbn}}"
}
```

In Tests:
```javascript
pm.test("Status is expected", function () {
    pm.response.to.have.status(parseInt(pm.iterationData.get("expectedStatus")));
});
```

### Newman — Running Postman from the Command Line

Newman is the CLI runner for Postman. Integrates with CI/CD pipelines.

```bash
# Install Newman globally
npm install -g newman

# Export your collection from Postman (Collection → ... → Export → v2.1)
# Export your environment

# Run the collection
newman run bookstore-api.postman_collection.json \
  --environment development.postman_environment.json \
  --reporters cli,html \
  --reporter-html-export test-results.html
```

Output example:
```
┌─────────────────────────┬──────────┬──────────┐
│ iteration               │ executed │ failed   │
├─────────────────────────┼──────────┼──────────┤
│ 1                       │ 10       │ 0        │
├─────────────────────────┼──────────┼──────────┤
│ total run duration: 2.3s
│ total data received: 4.51kB
│ average response time: 187ms
└─────────────────────────┴──────────┴──────────┘
```

---

## Section 5 — API Testing and Validation

### Writing Tests in Postman

Tests are written in JavaScript in the **Tests** tab. They run after every response.

#### Status Code Assertions

```javascript
// Assert exact status code
pm.test("Status code is 200", function () {
    pm.response.to.have.status(200);
});

pm.test("Status code is 201 Created", function () {
    pm.response.to.have.status(201);
});

// Assert status code is in a range
pm.test("Status is a success code", function () {
    pm.expect(pm.response.code).to.be.within(200, 299);
});
```

#### Response Body Assertions

```javascript
// Parse response body (assumes JSON)
const responseBody = pm.response.json();

// Check a specific field value
pm.test("Response has correct title", function () {
    pm.expect(responseBody.title).to.equal("Introduction to REST APIs");
});

// Check field exists and has correct type
pm.test("ID is a number", function () {
    pm.expect(responseBody.id).to.be.a("number");
});

// Check for presence of a field
pm.test("Response contains userId field", function () {
    pm.expect(responseBody).to.have.property("userId");
});

// Check array length
pm.test("Response contains at least one item", function () {
    pm.expect(responseBody).to.be.an("array").that.is.not.empty;
});

pm.test("Response contains exactly 5 items", function () {
    pm.expect(responseBody).to.have.lengthOf(5);
});

// Check nested object
pm.test("Author has required fields", function () {
    pm.expect(responseBody.author).to.have.property("firstName");
    pm.expect(responseBody.author).to.have.property("lastName");
});
```

#### Header Assertions

```javascript
pm.test("Content-Type is application/json", function () {
    pm.response.to.have.header("Content-Type");
    pm.expect(pm.response.headers.get("Content-Type")).to.include("application/json");
});

pm.test("Response has Location header on 201", function () {
    pm.response.to.have.header("Location");
});
```

#### Response Time Assertions

```javascript
pm.test("Response time is under 500ms", function () {
    pm.expect(pm.response.responseTime).to.be.below(500);
});
```

#### Negative Testing — Checking Error Responses

```javascript
// Test that invalid data returns 400
pm.test("Returns 400 for missing required field", function () {
    pm.response.to.have.status(400);
});

pm.test("Error message is descriptive", function () {
    const error = pm.response.json();
    pm.expect(error).to.have.property("message");
    pm.expect(error.message).to.be.a("string").and.to.not.be.empty;
});
```

### Chaining Requests — Login → Use Token

**Request 1: POST /auth/login**

Pre-request Script:
```javascript
// Nothing needed before this request
```

Tests tab:
```javascript
pm.test("Login successful", function () {
    pm.response.to.have.status(200);
});

// Extract token and save to environment for next request
const body = pm.response.json();
pm.environment.set("authToken", body.token);
pm.environment.set("userId", body.user.id);
console.log("Logged in. Token saved.");
```

**Request 2: GET /users/{{userId}}/profile** (runs after login)

Auth tab: Bearer Token → `{{authToken}}`  
Tests tab:
```javascript
pm.test("Profile loaded successfully", function () {
    pm.response.to.have.status(200);
});

const profile = pm.response.json();
pm.test("Profile belongs to logged-in user", function () {
    pm.expect(profile.id.toString()).to.equal(pm.environment.get("userId"));
});
```

---

## Section 6 — Error Handling and Exception Patterns in APIs

A well-designed API communicates errors clearly and consistently.

### Consistent Error Response Schema

Always return errors in the same structure. Your frontend can write one error handler.

```json
{
  "status": 400,
  "error": "Validation Failed",
  "message": "The 'price' field must be a positive number",
  "timestamp": "2026-02-23T10:30:00Z",
  "path": "/api/v1/products",
  "details": [
    {
      "field": "price",
      "rejectedValue": "-5.00",
      "message": "must be greater than 0"
    },
    {
      "field": "sku",
      "rejectedValue": null,
      "message": "must not be blank"
    }
  ]
}
```

### Testing Error Scenarios in Postman

Create a dedicated **"Error Cases"** folder in your collection:

```
📁 Bookstore API
├── 📁 Happy Path
│   ├── GET All Books (expect 200)
│   ├── POST Create Book (expect 201)
│   └── DELETE Book (expect 204)
└── 📁 Error Cases
    ├── GET Book — Not Found (expect 404)
    ├── POST Book — Missing Required Field (expect 400)
    ├── POST Book — Duplicate ISBN (expect 409)
    ├── GET Protected Endpoint — No Token (expect 401)
    └── GET Admin Endpoint — Wrong Role (expect 403)
```

**Example: Test for 404**
- URL: `{{baseUrl}}/books/99999999`
- Method: GET
- Tests:
```javascript
pm.test("Returns 404 for non-existent book", function () {
    pm.response.to.have.status(404);
});

const error = pm.response.json();
pm.test("Error response has correct structure", function () {
    pm.expect(error).to.have.property("status");
    pm.expect(error).to.have.property("message");
    pm.expect(error.status).to.equal(404);
});
```

**Example: Test for 400 (missing required field)**
- URL: `{{baseUrl}}/books`
- Method: POST
- Body (raw JSON):
```json
{
  "title": "A book without an ISBN"
}
```
- Tests:
```javascript
pm.test("Returns 400 when ISBN is missing", function () {
    pm.response.to.have.status(400);
});
```

**Example: Test for 401 (missing token)**
- URL: `{{baseUrl}}/books`
- Method: POST
- Auth: **No Auth** (override the collection-level auth)
- Body: valid JSON book object
- Tests:
```javascript
pm.test("Returns 401 when no token provided", function () {
    pm.response.to.have.status(401);
});
```

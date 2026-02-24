# Walkthrough Script — Part 2
# Day 23: REST & API Tools
# Estimated Time: ~90 minutes

---

## Segment 1 — Part 2 Opener (3 min)

Welcome back from break. This afternoon is where things get hands-on.

In Part 1 we covered the theory — HTTP, REST principles, status codes. Now we're going to put that knowledge to work with **two real tools** that you will use literally every day as a developer:

1. **Postman** — the go-to tool for sending HTTP requests, testing APIs, and sharing API workflows with your team
2. **Swagger/OpenAPI** — the standard for documenting REST APIs in a machine-readable format

Let's start with Postman. Everyone open your browser or the Postman desktop app.

---

## Segment 2 — Postman Interface and Features (8 min)

**[Point to Section 1 of 01-postman-guide.md — the interface diagram]**

Here's a bird's-eye view of the Postman interface. I know it can look overwhelming when you first open it, but there are really only a few areas you need to know.

The **sidebar** on the left has two key sections: Collections and Environments. Collections are like folders — you organize all your API requests for a project into one collection. Environments hold your variables — things like base URLs and auth tokens that change between dev and production.

The **main panel** is where you build your requests. At the top: the method dropdown and the URL bar. Below that are tabs for query params, auth, headers, the request body, and tests.

The **response panel** at the bottom shows what came back — the status code, response time, size, body, and headers.

**[Point to the Core Concepts table]**

Two things I want you to commit to memory: **Collections** and **Environment Variables**.

A Collection is not just a folder. It can have shared authentication, shared variables, and a Runner that executes all requests in sequence — great for automated smoke testing.

Environment Variables use `{{doublebraces}}` syntax. When Postman sends the request, it substitutes the real value. This is what allows the exact same request to hit your localhost in dev and your production server in prod — you just switch the active environment.

---

## Segment 3 — Creating and Organizing API Requests (10 min)

**[Point to Section 2 — "Building Your First Request"]**

Let's build a live request together. If you have Postman open, follow along.

Step 1: Change the method to GET.  
Step 2: Paste this URL: `https://jsonplaceholder.typicode.com/posts`

JSONPlaceholder is a free fake REST API — it's perfect for learning. It supports GET, POST, PUT, PATCH, and DELETE with fake data.

Step 3: Click the **Params** tab. Add a key `_limit` with value `5`. Watch how the URL updates automatically in the URL bar — Postman is building `?_limit=5` for you.

**[Point to the Params section]**

This is much better than manually editing the URL. Add another param: `userId` = `1`. Now the URL shows `?_limit=5&userId=1`.

Hit **Send**.

> **Ask the class:** "What status code did we get? What does that tell us?"

Answer: 200 OK — request succeeded, body contains JSON data.

**[Point to the "Saving Requests" section]**

Save this request. Click **Save**, give it a name like "GET All Posts", and create a new collection called "Day 23 Demo." Now it's saved for next time.

**[Point to the folder structure diagram]**

This is how professionals organize collections. Folders by resource, then naming requests descriptively. When you join a team and someone shares their Postman collection, this structure tells you everything the API can do before you read a single line of documentation.

> **Ask the class:** "If you're building a ride-sharing API, how would you organize the collection?"

Let them answer: folders for Drivers, Riders, Trips, Payments, Auth.

Now let's do a POST request.

Step 1: Method → POST  
Step 2: URL → `https://jsonplaceholder.typicode.com/posts`  
Step 3: Headers tab → Add `Content-Type: application/json`  
Step 4: Body tab → raw → JSON type → paste:
```json
{
  "title": "My First API Test",
  "body": "Testing POST with Postman",
  "userId": 1
}
```

Hit Send. You should get `201 Created`.

> **Watch out:** The single most common mistake I see is forgetting to set `Content-Type: application/json` on POST requests. If your server rejects your POST and you can't figure out why, check the Content-Type header first.

---

## Segment 4 — Environment Variables in Postman (10 min)

**[Point to Section 3 — "Creating an Environment"]**

Hard-coding URLs is dangerous. If you have 20 requests all pointing to `http://localhost:8080` and you need to test against staging, you'd have to edit 20 requests. Environment variables solve this.

Let me create an environment live. Click **Environments** in the sidebar → **+** → name it "Development."

Add a variable:
- Variable: `baseUrl`
- Initial Value: `https://jsonplaceholder.typicode.com`
- Current Value: `https://jsonplaceholder.typicode.com`

Click Save. Now select "Development" from the environment dropdown in the top right.

**[Point to "Using Variables in Requests"]**

Go back to your GET All Posts request. Change the URL from:
```
https://jsonplaceholder.typicode.com/posts
```
to:
```
{{baseUrl}}/posts
```

Send it. Same result. But now if I switch the active environment to "Staging" where `baseUrl` points to a staging server, ALL my requests point to staging instantly.

**[Point to "Setting Variables Programmatically"]**

This is where it gets powerful. Add this code to the **Tests** tab of your GET All Posts request:
```javascript
const posts = pm.response.json();
pm.environment.set("firstPostId", posts[0].id);
console.log("Saved post ID:", posts[0].id);
```

Send the request. Now open the environment and look at `firstPostId` — it has a value. Create a new request: `GET {{baseUrl}}/posts/{{firstPostId}}`. Run it. It uses the ID we just saved.

> **Ask the class:** "Why is this so powerful? Think about a login flow."

Answer: You run your login request, it saves the token to `{{authToken}}`, and every subsequent request that references `{{authToken}}` in the Authorization header is automatically authenticated. No copy-pasting tokens.

**[Point to the Auth tab section]**

Show the Auth tab. Select Bearer Token. Set the value to `{{authToken}}`. If you set this at the **Collection** level, every request in the collection inherits it automatically — unless a specific request overrides it with "No Auth".

---

## Segment 5 — Collections and Test Automation (8 min)

**[Point to Section 4 — "The Collection Runner"]**

Now let's talk about automation. Up to this point we've been sending requests manually. The Collection Runner lets you run every request in a collection automatically, in order.

Right-click your collection → "Run collection." You'll see a configuration screen where you can:
- Set iterations (run the whole thing 5 times)
- Add a delay between requests (useful if you're hitting a real API with rate limits)
- Attach a data file for data-driven testing

Click Run. Watch all requests execute in sequence and see which tests pass/fail.

**[Point to Newman section]**

Newman takes this further. It's a command-line tool that runs Postman collections from a terminal or CI/CD pipeline.

```bash
newman run my-collection.json --environment dev.json
```

This means your API tests can run automatically on every git push as part of your CI/CD pipeline. We'll connect this to GitHub Actions in Week 8. But now you understand the foundation.

> **Watch out:** Newman must be installed separately — `npm install -g newman`. If students get a "command not found" error, they need to install Node.js first.

---

## Segment 6 — API Testing and Response Assertions (12 min)

**[Point to Section 5 — "Writing Tests in Postman"]**

This is the most important section for making Postman more than just a manual testing tool.

Let's open the Postman collection JSON file: `Part-2/02-postman-collection.json`. This is a fully-built collection you can import directly into Postman.

**How to import:**
1. Postman → File → Import
2. Drag the JSON file in
3. The collection appears in your sidebar

Let's look at the "GET All Posts" request in the collection. Open the Tests tab.

**[Read through the status code assertion]**

```javascript
pm.test('Status is 200 OK', function () {
    pm.response.to.have.status(200);
});
```

Every test follows this pattern: `pm.test(description, function)`. The description is what shows in the test results — make it readable, because this is what your team sees when they run the collection.

**[Point to Response Body Assertions]**

```javascript
pm.test('Each post has id, title, and body', function () {
    posts.forEach(function (post) {
        pm.expect(post).to.have.property('id');
        pm.expect(post).to.have.property('title');
        pm.expect(post).to.have.property('body');
    });
});
```

This is a data contract test. If the API changes its response structure, this test fails — and you know immediately, before your frontend code breaks.

> **Ask the class:** "Why is it valuable to test the STRUCTURE of the response, not just the status code?"

Answer: APIs change. Fields get renamed, removed, or moved. Structure tests catch breaking changes before they affect production code.

**[Point to the Response Time assertion]**

```javascript
pm.test('Response time is under 1000ms', function () {
    pm.expect(pm.response.responseTime).to.be.below(1000);
});
```

This is a performance test. If the API starts taking 3 seconds to respond, this test fails. It's a simple but effective SLA check.

**[Point to "Chaining Requests"]**

Walk through the STEP 1 / STEP 2 requests in the "Environment Variable Demo" folder. Run STEP 1 — it fetches all users and saves the first user's ID and username to the environment. Run STEP 2 — it uses `{{savedUserId}}` in the URL path.

> **Ask the class:** "What real scenario does this replicate?"

Answer: Login → use the returned token for all subsequent requests. Create a resource → use the returned ID to retrieve, update, or delete it.

---

## Segment 7 — Error Handling and Exception Patterns (5 min)

**[Point to Section 6 — "Consistent Error Response Schema"]**

A great API never leaves you guessing when something goes wrong. Every error response should have the same structure.

Look at this error format:
```json
{
  "status": 400,
  "error": "Validation Failed",
  "message": "The 'price' field must be a positive number",
  "timestamp": "...",
  "path": "/api/v1/products",
  "details": [...]
}
```

The `status` field mirrors the HTTP status code — so clients can check the body OR the header. `message` is human-readable. `details` is an array of field-level validation errors — perfect for displaying inline error messages in a form.

**[Point to "Error Cases folder"]**

Show the Error Cases folder in the collection. Testing happy paths isn't enough. Professional API testing covers:
- What happens when a resource doesn't exist (404)
- What happens when required data is missing (400)
- What happens when you're not authenticated (401)
- What happens when you don't have permission (403)
- What happens when you try to create a duplicate (409)

Approximately **40-50% of your test coverage should be error cases**. Errors are where bugs hide.

> **Watch out:** Students often test only the happy path and ship their API assuming errors "just work." Then the first time a user submits bad data, the API returns a raw stack trace with no useful information. Always test your error handling.

---

## Segment 8 — Swagger/OpenAPI Introduction (7 min)

**[Point to 03-openapi-spec.yaml — the top section]**

Now let's switch gears to Swagger and OpenAPI.

OpenAPI is a **specification** — a standardized way to describe REST APIs in YAML or JSON. Swagger UI is the most popular tool for RENDERING that specification into interactive documentation.

The benefits are huge:
1. **Humans** can explore and understand your API without reading source code
2. **Machines** can use the spec to auto-generate client SDKs in any language
3. **Postman** can import an OpenAPI spec and automatically create a collection for you
4. **Spring Boot** can serve the Swagger UI automatically from your running application (we'll add this in Week 6)

Let's go to `https://editor.swagger.io`. This is the live Swagger editor. Paste our YAML file in — or go to File → Import File.

The editor validates the YAML in real-time. Errors appear in the right panel. The left panel is the spec, the right panel is the rendered documentation.

> **Ask the class:** "Have you ever used API documentation on sites like Stripe, Twilio, or GitHub? Those are all built from OpenAPI specs."

---

## Segment 9 — OpenAPI Structure Deep Dive (12 min)

**[Point to the `info` section]**

```yaml
info:
  title: Bookstore REST API
  version: "1.0.0"
  description: |
    A complete RESTful API for managing a bookstore's catalog...
```

`info` is metadata. The `title` and `version` appear at the top of the Swagger UI. The `description` supports markdown — you can write formatted documentation right here.

**[Point to `servers`]**

```yaml
servers:
  - url: http://localhost:8080/api/v1
    description: Local development server
  - url: https://api.bookstore.example.com/api/v1
    description: Production server
```

The Swagger UI "Try it out" feature sends requests to whichever server you select from this dropdown. In production, you'd list only your production URL.

**[Point to `tags`]**

Tags group operations in the Swagger UI. All operations tagged "Books" collapse into one group. Makes navigation easy for large APIs.

**[Point to `security`]**

```yaml
security:
  - BearerAuth: []
```

This global security declaration says: every endpoint requires a Bearer token unless it explicitly overrides with `security: []`. Look at the `POST /auth/login` operation — it has `security: []` to opt OUT because login doesn't require authentication.

**[Point to a path definition — `/books`]**

```yaml
/books:
  get:
    tags: [Books]
    summary: Get all books (paginated, filterable)
    operationId: getAllBooks
    parameters: [...]
    responses: [...]
```

Every path entry has operations (`get`, `post`, etc.). Each operation has:
- `summary` — one-line description shown in the Swagger UI sidebar
- `description` — detailed docs (markdown supported)
- `operationId` — unique identifier, used for code generation
- `parameters` — query, path, header, or cookie params
- `requestBody` — for POST/PUT/PATCH
- `responses` — all possible status codes and what they return

**[Point to the parameters section]**

```yaml
- name: page
  in: query
  description: Page number (0-indexed)
  required: false
  schema:
    type: integer
    minimum: 0
    default: 0
```

`in` can be: `query`, `path`, `header`, or `cookie`.
`required: true` for path parameters (they're part of the URL — they must always be there).
`schema` defines the type and validation constraints.

**[Point to a path parameter — `/books/{bookId}`]**

The `{bookId}` in the path is a path parameter. OpenAPI documents it under `parameters` with `in: path` and `required: true`. In Swagger UI, this becomes a text input field.

---

## Segment 10 — Schemas and Components (8 min)

**[Point to the `components/schemas` section]**

This is the most powerful part of OpenAPI — **reusable schemas** defined once and referenced everywhere.

Look at the `Book` schema:
```yaml
Book:
  type: object
  properties:
    id:
      type: integer
      format: int64
      readOnly: true
    isbn:
      type: string
      pattern: '^978-[0-9]{1,5}-...'
    title:
      type: string
      minLength: 1
      maxLength: 255
    price:
      type: number
      format: double
      minimum: 0
```

Every property has a `type`, optional `format`, and optional validation constraints like `minimum`, `maximum`, `minLength`, `maxLength`, `pattern` (regex), `enum`.

`readOnly: true` means this field is returned in responses but should not be sent in request bodies.

**[Point to `$ref`]**

```yaml
author:
  $ref: '#/components/schemas/AuthorSummary'
```

`$ref` is the most important keyword in OpenAPI. It's a reference pointer — "go look at `AuthorSummary` in the `components/schemas` section." This avoids duplicating schema definitions. Change it once, it updates everywhere.

**[Point to the difference between Book, CreateBookRequest, and UpdateBookRequest]**

This is great API design. Three separate schemas for three different operations:
- `Book` — full representation including `readOnly` computed fields (returned by GET)
- `CreateBookRequest` — what you send to POST (no ID, no computed fields, all required fields marked)
- `UpdateBookRequest` — what you send to PATCH (ALL fields optional)

Having separate schemas for input vs output is a best practice. Don't force your clients to send back fields they didn't create.

**[Point to the `ErrorResponse` schema]**

```yaml
ErrorResponse:
  type: object
  required:
    - status
    - error
    - message
    - timestamp
    - path
  properties:
    status:
      type: integer
    error:
      type: string
    message:
      type: string
    ...
```

This schema is referenced by every `4xx` and `5xx` response. Look at `components/responses/NotFound`:

```yaml
NotFound:
  description: Not Found — resource does not exist
  content:
    application/json:
      schema:
        $ref: '#/components/schemas/ErrorResponse'
```

And it's used in path operations as `$ref: '#/components/responses/NotFound'`. One definition, used in dozens of places. If you ever change the error format, change it once here.

> **Ask the class:** "What is the benefit of having a shared `ErrorResponse` schema vs defining the error shape separately for each endpoint?"

Answer: Consistency, maintainability, and it enables auto-generated client SDKs to have a single error type that all error scenarios share.

---

## Segment 11 — Swagger UI Exploration (5 min)

**[In the Swagger editor at editor.swagger.io]**

Let's explore the rendered Swagger UI:

1. **Click on a tag** — it expands to show all operations with that tag
2. **Click on an operation** — it shows the full documentation: description, parameters, request body, all response codes
3. **Click "Try it out"** — the parameter fields become editable
4. **Click "Execute"** — it sends a real HTTP request and shows the response

Walk through this for `GET /books`:
- Set `page` to `0`, `size` to `5`
- Click Execute
- Show the response body, response code, response headers in the UI

> **Watch out:** "Try it out" sends real requests. In production documentation, the server URL might be your live production server. Don't use "Try it out" on a production spec with delete endpoints unless you mean it.

Show the **Schemas** section at the bottom of the Swagger UI — every component schema is rendered there, showing all properties and their types.

---

## Segment 12 — Connecting It All: Postman ↔ OpenAPI (5 min)

**[Back in Postman]**

Here's a workflow tip that will save you hours.

If a team has an OpenAPI spec, you don't have to manually create every request in Postman. You can **import the spec directly**:

1. Postman → Import
2. Upload the YAML file (or paste the URL if it's hosted)
3. Postman reads all the paths, methods, parameters, and schemas
4. It creates a complete collection automatically

Every endpoint, with all the parameters pre-filled. You just add your test scripts and environment variables.

In Week 6, when we build a Spring Boot REST API, Spring will auto-generate the OpenAPI spec from your code using the `springdoc-openapi` library. You add a dependency, and your running application serves:
- `/v3/api-docs` — the raw JSON spec
- `/swagger-ui.html` — the live Swagger UI

So the workflow is:
1. Build your Spring Boot API
2. Hit `http://localhost:8080/swagger-ui.html`
3. Explore and test your own API in the browser
4. Export the spec and import it into Postman

---

## Segment 13 — Part 2 Wrap-Up (5 min)

Outstanding work today. Let me summarize what we covered:

**Postman:**
- Building requests with all HTTP methods
- Organizing requests into collections with folders
- Environment variables with `{{doublebraces}}` for switching between environments
- Setting variables programmatically from response data (for chaining requests)
- Test assertions: status codes, response body fields, headers, response time
- Collection Runner for automated testing
- Newman for CI/CD integration

**Swagger/OpenAPI:**
- YAML structure: `info`, `servers`, `tags`, `paths`, `components`
- Defining operations with `get`, `post`, `put`, `patch`, `delete`
- Path parameters (`in: path`) and query parameters (`in: query`)
- Request body schemas with `required` fields and validation constraints
- Response schemas with all status codes documented
- Reusable components with `$ref`
- Input schemas (CreateRequest) vs output schemas (full resource) vs patch schemas
- Consistent error response schema shared across all error codes
- Swagger UI for exploring and testing APIs interactively

**The connection:**
- Design your API with an OpenAPI spec first (design-first)
- Or generate the spec from your code (code-first with springdoc)
- Import the spec into Postman to get a free test collection
- Run the collection with Newman in your CI/CD pipeline

In Week 6, you'll build your first Spring Boot REST API — everything from today will apply immediately.

Any questions?

---

## Q&A Prompts

1. "What's the difference between a Collection variable and an Environment variable in Postman? When would you use each?"

2. "Why would you have separate OpenAPI schemas for `CreateBookRequest`, `UpdateBookRequest`, and `Book`? Couldn't you just use one schema for everything?"
   *(Answer: Different fields are required/optional/read-only for different operations. A single schema would either require fields on PATCH that shouldn't be required, or mark fields as optional on POST that must be present.)*

3. "What is `operationId` in OpenAPI used for? When does it matter?"
   *(Answer: Used by code generation tools to name the generated method. Matters when you generate client SDKs — `operationId: getBookById` becomes a method `getBookById()` in the generated client.)*

4. "If the Postman test `Status is 201 Created` fails and you get `200 OK` instead, is that a problem? Why?"
   *(Answer: Yes — 200 and 201 mean different things. 201 communicates that a NEW resource was created. 200 says the operation succeeded but doesn't say what happened. RESTful APIs should be precise.)*

5. "What is the `readOnly: true` flag in OpenAPI used for? What happens if a client accidentally sends a `readOnly` field in a request body?"
   *(Answer: Marks fields that the server generates (like `id`, `createdAt`). A well-designed API ignores read-only fields in request bodies. Some validators reject requests that include them.)*

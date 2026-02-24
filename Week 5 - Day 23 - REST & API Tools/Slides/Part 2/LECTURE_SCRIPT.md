# Day 23 Part 2 — Postman & Swagger/OpenAPI
## Lecture Script — 60 Minutes

---

### [00:00–02:00] Opening

Welcome back. Part 1 gave you the protocol — you understand HTTP requests, responses, methods, status codes, headers, and REST design principles. Part 2 is about tools. Specifically, two tools you will use every single day as a developer: Postman and Swagger.

Postman is how you interact with APIs that aren't done yet — or APIs you didn't write — or your own API when you're debugging it. It's your HTTP client. We're going to learn it end to end: building requests, managing environments so you can switch between local, staging, and production with one click, writing automated test scripts, and running an entire collection of requests as an integration test suite.

Swagger slash OpenAPI is how you describe an API. It's a machine-readable contract — a YAML file that says "here are my endpoints, here's what they accept, here's what they return." From that contract you can auto-generate documentation with a live Try It Out interface, generate client SDKs in dozens of languages, and validate your implementation against the spec. We're going to write OpenAPI YAML by hand today, which means when Spring Boot generates it automatically for you on Day 26, you'll know exactly what it's producing.

Let's go.

---

### [02:00–10:00] Slides 2 & 3 — Postman Interface and Building Requests

Let's orient ourselves in Postman. When you open Postman you see three main regions. The left sidebar has your Collections — organized groups of saved requests — your Environments, and your history of recent requests. The center is the main request builder. The bottom is the response panel.

The request builder is where you spend most of your time. At the top there's a method dropdown — GET, POST, PUT, PATCH, DELETE and more — and a URL bar. Below that are the tabs. Params is where you add query string parameters visually — you type the key and value in a table and Postman builds `?key=value` for you. Authorization lets you configure auth — Bearer Token, Basic Auth, API Key — at the request level. Headers is where you add any custom headers. Body is where you write your request body for POST, PUT, and PATCH. Then Pre-request Script and Tests — those are JavaScript and we'll spend time on both.

The response panel at the bottom shows you everything the server returned: the status code (green for 2xx, red for 4xx and 5xx), response time in milliseconds, size, and then tabs for Body, Headers, and Cookies.

Let me walk you through building both request types you'll use constantly. First, a GET request. New request tab, method dropdown stays on GET, type in a URL — let's say `http://localhost:8080/api/v1/books`. Now instead of hardcoding that URL, and I want you to do this from day one, use a variable. Type `{{baseUrl}}/api/v1/books`. Those double curly braces are Postman's variable syntax. We haven't created the `baseUrl` variable yet but let's see how the complete workflow comes together — I'll show you environments in the next section.

Click over to the Params tab. You see a table. Type `genre` in the key column, `fiction` in the value column. Postman adds `?genre=fiction` to your URL automatically. Add another row: `limit`, `10`. URL becomes `?genre=fiction&limit=10`. Much easier than typing query strings manually and it's easy to toggle parameters on and off with the checkbox.

Now the Authorization tab. Select "Bearer Token" from the dropdown. In the Token field, type `{{authToken}}` — another variable. Every request in this collection that needs auth will reference this same variable.

Click Send. In the Response panel, check the status code — green 200 means success. Click Pretty in the Body tab to see formatted JSON. Check the time — should be well under 500ms for local dev.

Now a POST request. Change the method to POST. URL: `{{baseUrl}}/api/v1/books`. Authorization tab — same Bearer Token, same `{{authToken}}` variable. Now the Body tab. Select "raw" radio button. In the dropdown on the right, change "Text" to "JSON" — Postman automatically adds `Content-Type: application/json` to your headers. In the text area, write your JSON body. Title, authorId, price, genre. Click Send. Expect a 201 response. Go to the Headers tab in the response panel and find the `Location` header — that's the URL of your newly created book. Note the ID — we'll use it in the next request.

One critical habit: after you send a request, save it. Give it a descriptive name like `POST - Create Book` and put it in your collection. Next time you need to test book creation, it's one click.

---

### [10:00–18:00] Slides 4 & 5 — Environments and Collections

Here's the problem environment variables solve. Your API runs on three different URLs depending on where you are in the development lifecycle. Locally it's `http://localhost:8080`. On the staging server it's `https://staging-api.bookstore.com`. In production it's `https://api.bookstore.com`. Without environment variables, every time you switch contexts you have to manually edit every single URL in every single request. With 50 requests in a collection, that's 50 edits. And you'll mess one up. And you'll send a production request when you meant to send a staging request, which is the kind of mistake that ends careers.

Environment variables fix this. In Postman, click the Environments tab in the sidebar. Create an environment called "Book Store - Dev". Add a variable called `baseUrl` with the value `http://localhost:8080`. Add another called `authToken` — leave the value blank for now or put a dev token in. Save.

Now create "Book Store - Staging". Same variables, different values — the staging URL and a staging token.

Back in your requests, everywhere you typed the base URL, put `{{baseUrl}}` instead. Everywhere you need an auth token, put `{{authToken}}`. To switch environments, use the dropdown in the top right corner of Postman. Select "Book Store - Dev" and all `{{baseUrl}}` references resolve to localhost. Select "Book Store - Staging" and they all resolve to the staging URL. One click, all requests updated, zero chance of accidentally hitting the wrong environment.

Let me explain variable scope because this matters when things behave unexpectedly. Postman has four scopes from widest to narrowest: Global, Collection, Environment, and Local. Global variables are accessible from every collection in every workspace. Collection variables are scoped to a single collection. Environment variables are active when that environment is selected. Local variables are set in a pre-request script and only exist for the duration of that single request. Narrower scope overrides wider scope — so a collection variable named `baseUrl` overrides a global variable named `baseUrl`. This lets you override defaults for specific collections.

The golden rule: never hardcode URLs, tokens, IDs, or credentials in requests. Always use variables. This makes your collection shareable with teammates — they import it, set their own environment values, and everything works.

Now collections. A collection is the organizational unit for your API work. Think of it as a folder for related requests. Create a collection called "Book Store API." Inside it, create subfolders: Books, Authors, Orders, Auth. Move each saved request into the appropriate folder.

There's a feature that saves a lot of repetitive work: collection-level Authorization. Go to the collection settings, Authorization tab. Set it to Bearer Token, token value `{{authToken}}`. Now every request in the collection inherits this — you don't need to configure auth on each individual request. If a request has its own auth configured, it overrides the collection default.

Collections can be exported as a JSON file and imported by teammates. Your whole test suite travels with one file. This is something you should absolutely mention in a job interview — "I built a Postman collection with 40 requests, automated tests, and Newman integration for CI."

---

### [18:00–26:00] Slides 6 & 7 — Pre-Request Scripts and Tests

Two JavaScript tabs in Postman: Pre-request Script runs before your request is sent, Tests runs after the response arrives. Together they make Postman a genuine automated testing platform.

Pre-request Script. The Postman sandbox gives you access to a `pm` object — the Postman API. Let me show you the most useful methods. `pm.environment.set("key", "value")` sets an environment variable. `pm.environment.get("key")` retrieves one. `pm.globals.set()` and `pm.globals.get()` do the same for global variables.

Simplest use case: `pm.environment.set("timestamp", new Date().toISOString())` — you want to include a timestamp in your request body but it needs to be the actual current time, not a hardcoded value. Set it in the pre-request script, reference `{{timestamp}}` in your body.

More sophisticated use case — and this is something real teams do — auto-login. You put a pre-request script at the collection level that checks whether `authToken` is expired. If it is, it fires a login request, grabs the token from the response, and sets it in the environment before your actual request runs. Your entire collection stays authenticated without you manually refreshing tokens.

Now the Tests tab. This is where Postman transforms from a request builder into an automated testing tool. The tests run after the response arrives and they use the Chai assertion library through the `pm.expect()` interface.

Every test follows this pattern: `pm.test("descriptive name", function() { /* assertions */ })`. If an assertion throws, the test fails and Postman marks it red. If no assertion throws, the test passes and it's marked green.

Let's go through the most useful assertions. Status code check: `pm.response.to.have.status(200)`. You'll write this on nearly every request — it immediately tells you if the server returned what you expected. Header check: `pm.expect(pm.response.headers.get("Content-Type")).to.include("application/json")` — verify the response is actually JSON before you try to parse it. Parse and inspect the body: `const body = pm.response.json()` — this parses the JSON for you — then `pm.expect(body.title).to.equal("Clean Code")` or `pm.expect(body.price).to.be.a("number")` or `pm.expect(body.id).to.exist`. Response time: `pm.expect(pm.response.responseTime).to.be.below(500)` — instant performance regression testing. If this test suddenly starts failing, your API got slower.

The one that chains requests together: `pm.environment.set("createdBookId", pm.response.json().id)`. This saves the ID of the book you just created into an environment variable. The next request in your collection can reference `{{createdBookId}}` in its URL. This is how you build a full workflow test — login, create, read, update, verify, delete — with each step passing data to the next.

A quick word on `pm.expect` chaining. Chai assertions chain with `.to.be`, `.to.have`, `.to.equal`, `.to.include`. `pm.expect(value).to.be.a("number")` checks type. `pm.expect(value).to.equal(42)` checks exact equality. `pm.expect(value).to.be.greaterThan(0)` checks comparison. `pm.expect(array).to.have.lengthOf(5)` checks array length. These read like English sentences, which is why Chai is so popular.

---

### [26:00–34:00] Slide 8 — Collection Runner and Newman

Collection Runner. This is the feature that elevates Postman from a manual testing tool to an automated testing tool. Click the Runner button, select your collection, select your environment, click Run.

Postman executes every request in your collection in order. After each request, it runs the Tests. It shows you a pass/fail summary: how many tests ran, how many passed, how many failed, which specific tests failed and why.

Now here's where it gets powerful. Design your test sequence as a workflow. Request one: POST to login, save the token. Request two: POST to create a book, save the book ID. Request three: GET that book by ID, verify all fields are correct. Request four: PATCH the price, verify the price changed. Request five: GET again, verify the update persisted. Request six: DELETE the book. Request seven: GET again, verify you get a 404. This is an end-to-end integration test of your entire CRUD workflow. You run it against dev, it passes. You run it against staging before a deploy, it passes. You run it against production after a deploy to verify nothing broke. If any test fails at any step, you know immediately and the pipeline stops.

Data-driven testing. The Collection Runner accepts a CSV or JSON data file. You set up a template request that uses variables for the test data — `{{title}}`, `{{price}}`, `{{genre}}` — and the runner iterates over each row in the file, running the full collection once per row. To test creating books for five different genres, you make a CSV with five rows. Postman runs the full workflow five times, one for each genre. This is parameterized testing with minimal setup.

Newman. Newman is Postman's command-line runner. Install it globally with npm: `npm install -g newman`. Then you can run a collection from the terminal. `newman run BookStoreAPI.postman_collection.json -e BookStore-Dev.postman_environment.json`. Newman exits with a non-zero status code if any tests fail, which is exactly what CI/CD systems need. Add this command to your GitHub Actions workflow, run it on every pull request. If any API test fails, the PR pipeline fails. You've just implemented automated API regression testing.

Newman has a reporter option — add `-r htmlextra` after installing the `newman-reporter-htmlextra` package — and it generates a beautiful HTML report with request/response details, test results, pass rates over time. Great for sharing results with non-technical stakeholders.

---

### [34:00–42:00] Slides 9 & 10 — OpenAPI Introduction and Document Structure

Postman is for testing APIs that exist. OpenAPI is for describing APIs before or after they exist. Let's shift gears.

You're building the Book Store API backend. Your frontend team — or another backend team building a mobile app — needs to know: what endpoints exist? What fields does POST /books require? Is `authorId` an integer or a string? What does the 400 error response look like? Without documentation, they have to ask you, or read your source code, or guess. All three options are painful.

OpenAPI solves this by giving your API a machine-readable contract. A single YAML or JSON file describes every endpoint, every parameter, every request body, every possible response. Tools read this file and generate documentation, generate client code, generate test stubs, validate responses, power mock servers. The contract becomes the single source of truth.

Quick history. Swagger was created in 2011 by Tony Tam at Wordnik. It was a tool for documenting REST APIs. The spec became so widely adopted that in 2015 it was donated to the Linux Foundation and renamed the OpenAPI Specification. In 2017, OpenAPI 3.0 was released — a significant rewrite that separated the API description from implementation details, added better support for modern auth patterns, and introduced a cleaner structure for schemas. OpenAPI 3.1, released in 2021, aligns fully with JSON Schema so there's no mismatch between the two specs. We'll use OpenAPI 3.0.3, which is still the most widely deployed version.

The most visible output of an OpenAPI spec is Swagger UI — the interactive documentation page. You've probably seen it: a web page that lists all your endpoints, groups them by tag, shows request parameters and body schemas, and has a "Try it out" button that lets you send real requests from the browser. On Day 26 we'll add `springdoc-openapi` to our Spring Boot application and it will generate the spec automatically from your annotations and serve Swagger UI at `/swagger-ui.html`. Understanding what the YAML looks like makes that feel less like magic.

Document structure. The OpenAPI 3.0 document has six top-level keys. `openapi` declares the spec version — always `3.0.3` for us. `info` is API metadata — title, description, version number, contact info. `servers` is an array of base URLs — dev, staging, production. `paths` is the heart of the document, where you define all your endpoints. `components` holds reusable definitions. `security` defines global security requirements.

Let me trace through the minimal document on the slide. The `openapi: 3.0.3` line must be first. The `info` block gives Swagger UI the API title and version. The `servers` block lists your environments — this is how Swagger UI knows which URL to hit when you click "Try it out." The `paths` block is what we'll expand next. And `components` is where we put reusable schemas so we don't copy-paste them everywhere.

This file lives in your project. Convention is to put it in `src/main/resources/openapi.yaml` or as a top-level `openapi.yaml`. When you use springdoc-openapi, Spring Boot generates and serves this automatically. When you're designing API first, you write this file before you write any Java code.

---

### [42:00–50:00] Slides 11 & 12 — Paths, Operations, and Parameters

Let's fill in the `paths` section. Every key in `paths` is a URL pattern. `/api/v1/books` is one key. `/api/v1/books/{bookId}` is another — notice the curly braces, that's an OpenAPI path parameter. Under each path key, you have HTTP method keys: `get`, `post`, `put`, `patch`, `delete`.

An operation is the combination of a path and a method. Let's read the `POST /api/v1/books` operation on the slide. The `operationId` is `createBook` — this is the method name that SDK generators use. `summary` is the one-liner in Swagger UI. `tags` groups the operation in the documentation — all operations tagged "Books" appear together. `requestBody` describes the request body — `required: true` means the body is mandatory, then under `content`, `application/json` is the media type, and `schema: $ref: "#/components/schemas/CreateBookRequest"` points to a schema definition in `components`. The `$ref` is a JSON pointer — `#` is the document root, `/components/schemas/CreateBookRequest` is the path through the document to the schema.

The `responses` block uses status codes as keys — note they're strings with quotes, not integers. `"201"` describes the success case. `description` is required. Under `content`, we describe the response body — again pointing to a schema. We also define a `headers` entry for the `Location` header, because returning Location on 201 is the pattern we specified in Part 1.

The error responses use `$ref` at the response level — `$ref: "#/components/responses/Unauthorized"` — pointing to a reusable response definition in `components/responses`. This is much cleaner than repeating the error schema in every operation.

Now parameters. The `parameters` key takes an array. Each parameter has four required pieces: `name`, `in`, `required`, and `schema`. The `in` field tells OpenAPI where the parameter is: `path` means it's in the URL path like `/books/{bookId}`, `query` means it's in the query string, `header` means it's an HTTP header, `cookie` is a cookie. Path parameters are always `required: true` because they're part of the URL itself — you can't make a request to `/books/{bookId}` without providing `bookId`. Query parameters default to `required: false`.

The `schema` object inside a parameter is the same as any other schema — you can add `type`, `minimum`, `maximum`, `enum` for constrained values, `default`, `example`. If you add `enum: [title, price, createdAt]` to the `sort` parameter, Swagger UI renders a dropdown instead of a text field. Clients know exactly what values are valid. And validators can check actual requests against the spec to reject invalid values.

One more thing on path parameters: the path variable name in the URL must match the `name` in the parameter definition. If your path is `/books/{bookId}`, your parameter must be `name: bookId`. Spelling mismatch is a common source of spec validation errors.

---

### [50:00–58:00] Slides 13 & 14 — Schemas, $ref, and Error Response Design

Schemas. This is where you describe the shape of your data — what fields a resource has, what types they are, which ones are required. In OpenAPI, schemas follow the JSON Schema specification, which you may encounter in other contexts like JSON form validators or TypeScript type generation.

A schema has a `type` — usually `object` for resources. Under `properties`, you list each field with its own sub-schema. A field schema has `type`, and optionally `format`, `minimum`, `maximum`, `minLength`, `maxLength`, `pattern`, `enum`, `example`, `readOnly`, `writeOnly`, and more.

Look at the `Book` schema on the slide. The `required` array at the top level lists which properties must be present. Then `properties` defines each field. `id` is `type: integer`, `format: int64` (because Java's `long` maps to int64). It's `readOnly: true` — the server assigns it, clients can't set it. `price` is `type: number`, `format: double`, with `minimum: 0.01` — a price can't be zero or negative. `genre` has an `enum` array listing the allowed values. `createdAt` is `type: string`, `format: date-time` — OpenAPI doesn't have a native date type, it uses `string` with format hints.

I've also defined a `CreateBookRequest` schema that's separate from `Book`. Why? Because the client doesn't send `id` or `createdAt` — those are server-generated. If I used the `Book` schema for both requests and responses, I'd either require clients to send fields they don't have, or mark server-generated fields as optional, which makes the contract misleading. Separate request and response schemas are a best practice.

The `$ref` syntax is powerful and you'll use it constantly. Anywhere you need a schema — in a `requestBody`, in a response `content`, in a parameter `schema`, in another schema's properties — you can use `$ref: "#/components/schemas/SomeSchema"` instead of inlining the full schema. Change the schema once, it updates everywhere it's referenced. This is the DRY principle applied to API specifications.

Now let's talk about error responses — and this is something that trips up API designers constantly. Error response design.

Your API will have errors. You will have 400s, 401s, 404s, 500s. The question is: are they consistent? Does every error from every endpoint have the same structure so a client can handle it the same way? Or does each endpoint have its own error format — some return plain strings, some return objects with different field names, some return HTML? Inconsistent error responses are a maintenance nightmare.

Define one `ErrorResponse` schema. Put it in `components/schemas`. Reference it from every error response in every operation. Every 400, 401, 403, 404, 409, 422, 429, and 500 returns the same structure. Five fields: `status` (the HTTP status code as an integer), `error` (the HTTP reason phrase), `message` (a human-readable description of what went wrong), `path` (the request URL that failed), and `timestamp` (ISO 8601 datetime). Optionally a `details` array for field-level validation errors.

When a client writes error handling code, they write it once. `if (!response.ok) { const error = await response.json(); showMessage(error.message); }`. They don't need conditional logic for different error shapes from different endpoints.

Define your `ErrorResponse` schema today. You'll implement it in Spring Boot on Day 26 with `@ControllerAdvice`. The shape you define in OpenAPI today is exactly what your exception handler will produce.

---

### [58:00–60:00] Slide 16 — Full Day Recap

What a day. You've covered more ground in one day than many developers accumulate over months of piecing things together.

From Part 1: HTTP is a stateless request-response protocol. Requests have a method, URL, headers, and optional body. Responses have a status code, headers, and optional body. GET reads, POST creates, PUT replaces, PATCH updates, DELETE removes. 2xx success, 4xx client error, 5xx your problem. REST means stateless, uniform interface, nouns in URLs, methods as verbs.

From Part 2: Postman lets you build, save, and automate HTTP requests. Use environment variables — never hardcode URLs or tokens. Write tests with `pm.test` and `pm.expect`. Run full workflows with Collection Runner. Automate in CI with Newman. OpenAPI 3.0 gives your API a machine-readable contract in YAML. Paths define endpoints. Operations define methods. Parameters describe inputs. Schemas describe data shapes. `$ref` keeps it DRY. Consistent error responses make your API a joy to integrate with.

Day 24: we move into Spring. Maven and Gradle for build management, then Spring Core — the IoC container, dependency injection, beans. Everything we designed today becomes a real implementation starting Day 24 and especially Day 26 when we build the Spring MVC REST API. Keep your Book Store API design in mind — you'll be writing those controllers soon. See you tomorrow.

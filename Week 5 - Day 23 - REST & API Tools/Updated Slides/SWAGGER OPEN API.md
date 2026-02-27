Presentation 2 — Swagger & OpenAPI Specification
Full Script with Numbered Slides
Audience: Students with Java & JavaScript background (have completed Presentation 1)
Estimated Time: ~90–100 minutes (see Session Split note in Appendix)

SLIDE 1 — Title Slide
Content:

Title: "Swagger & OpenAPI: Describing, Documenting & Exploring APIs"
Subtitle: "Write it once — generate docs, tests, and client code for free"
Your name | Date | Course name


SLIDE 2 — What We're Covering Today
Content:

Agenda:

📄 What OpenAPI is and why it exists
🏗️ OpenAPI spec structure — top to bottom
🛣️ Defining paths, operations, and parameters
📦 Request bodies and response schemas
🔗 Components, $ref, and reusability
🔒 Documenting authentication and security
🖥️ Swagger UI — interactive documentation
☕ Spring Boot integration with springdoc-openapi
✅ Tying it all together — spec as a contract



SCRIPT:
"Welcome back. In the previous session we covered HTTP, REST, Postman, and error handling — the foundations of understanding and testing APIs. Today we're going to cover how to formally describe APIs.
As a developer, you'll both consume APIs built by others and build APIs consumed by others. OpenAPI and Swagger are the industry standard for making that second part — building APIs others can understand — systematic, clear, and powerful. By the end of this session you'll be able to write a full OpenAPI specification from scratch, understand the interactive documentation it generates, and wire it directly into a Spring Boot project."


SECTION 1 — INTRODUCTION TO OPENAPI & SWAGGER
⏱ ~10 minutes

SLIDE 3 — The Documentation Problem
Content:

Scenario: You've built an API. Someone else needs to use it.
Option A — Write a README

Gets outdated immediately
Inconsistent format
No way to try requests
Easily missed


Option B — Use OpenAPI

Machine-readable + human-readable
Always reflects the spec as written
Auto-generates interactive documentation
Enables code generation, validation, mocking


Quote: "The spec IS the documentation. Keeping them in sync is not optional."

SCRIPT:
"Let's start with the problem OpenAPI solves. You've built a REST API. It has 20 endpoints. Someone on your team — or a third-party developer — needs to use it. How do you communicate what it does?
If you write a README, it's out of date within a week. Endpoints change, new parameters get added, response shapes evolve, and the README sits there slowly becoming fiction. Consumers read it, try a request, and it doesn't match. Frustrating.
OpenAPI solves this by giving you a structured, machine-readable format for describing your API. You write the spec file, and tools like Swagger UI read it and automatically render it as beautiful, interactive documentation. The spec and the docs are one and the same. When you update the spec, the docs update instantly."

SLIDE 4 — What Is the OpenAPI Specification?
Content:

Full name: OpenAPI Specification (OAS)
A standard format for describing REST APIs using YAML or JSON
Governed by the OpenAPI Initiative (Linux Foundation)
Current major version: OpenAPI 3.x (3.0.3 / 3.1.0)
What a spec file describes:

Every endpoint (path + method)
Every parameter (path, query, header, cookie)
Every request body
Every possible response (per status code)
All data models (schemas)
Authentication methods



SCRIPT:
"The OpenAPI Specification is a community standard — not owned by any one company — for describing REST APIs in a structured way. You write a single YAML or JSON file that fully describes every aspect of your API.
The spec started life as part of the Swagger project, which was created by a company called SmartBear. In 2016 the specification was donated to the Linux Foundation and renamed the OpenAPI Specification. The tooling — the UI, the editor, the code generators — kept the Swagger name. So when you see 'Swagger' today, it refers to the tooling ecosystem, and 'OpenAPI' refers to the specification standard itself. People use them interchangeably, which causes confusion, but now you know the distinction.
Version 3.x is what you'll be writing. It's a significant improvement over the older 2.x (also called Swagger 2). If you encounter documentation that references swagger: '2.0' at the top, that's the older version."

SLIDE 5 — What Is Swagger?
Content:

Swagger = The tooling ecosystem built around OpenAPI
Key tools:

Swagger Editor — Browser-based YAML editor with live validation and preview
Swagger UI — Renders OpenAPI spec as interactive HTML documentation
Swagger Codegen / OpenAPI Generator — Generates client SDKs and server stubs from a spec


Where you'll encounter Swagger UI:

Spring Boot with springdoc-openapi → /swagger-ui.html
Express with swagger-ui-express → /api-docs
Public APIs that publish their spec (Stripe, Twilio, GitHub, etc.)



SCRIPT:
"Swagger is the tooling layer. The three main tools you need to know.
Swagger Editor is a browser-based YAML editor at editor.swagger.io. The layout is split in two: on the left you type your OpenAPI YAML, and on the right it renders a live preview of what the documentation will look like. As you type, it validates your YAML in real time — red markers appear on any line where your spec has an error, and it tells you exactly what's wrong. In the practice tasks at the end of the session, this is the tool you'll write your spec in.
Swagger UI is what end users actually see — the rendered documentation. It's a JavaScript library that reads your OpenAPI file and produces an interactive HTML page. Your endpoints are listed by tag group, each one expandable to show its parameters, request body, and all possible responses. There's a Try it Out button on every operation that lets someone make a real HTTP request directly from the docs page.
Swagger Codegen (or the community fork OpenAPI Generator) takes your spec and generates client code in dozens of languages — Java, JavaScript, Python, Go, and more. You can also generate server stub code — the skeleton controllers and models. This is huge for developer productivity."

SLIDE 6 — The OpenAPI Workflow
Content:

Two approaches:

Design-First → Write the spec → Implement the API → Spec stays the authority

Pros: Clear contract before coding | Stakeholders can review | Easier to parallelize frontend/backend
Cons: More upfront work


Code-First → Write the API code → Generate the spec from annotations → Keep in sync

Pros: Less extra work | Spec always matches code
Cons: Spec can lag if you forget to annotate




Recommendation: Design-first for APIs consumed by others. Code-first with good annotation discipline for internal APIs.

SCRIPT:
"There are two ways to approach OpenAPI in a project, and it's worth understanding both because you'll encounter both in your career.
Design-first means you write the OpenAPI spec before you write any implementation code. The spec is the contract that the whole team agrees on. The frontend team can mock against it. The backend team implements to it. QA writes tests against it. This is the most rigorous approach and it's common in companies with mature API practices.
Code-first means you write your Spring Boot code, use annotations to add metadata, and generate the spec from your code automatically. In Spring Boot with springdoc-openapi, just adding a dependency and some annotations generates a full OpenAPI spec at a /v3/api-docs endpoint. The Swagger UI reads from that endpoint live. We'll look at exactly how to do this later in the session.
Both are valid. I'd recommend learning both — write a spec manually today to understand the format deeply, then the Spring Boot section will show you how code-first wires up to that same format."

SLIDE 7 — OpenAPI Tools Ecosystem Overview
Content:

Diagram / mind map style:

Center: OpenAPI Spec File (.yaml / .json)
Connected tools:

Swagger UI → Interactive Documentation
Swagger Editor → Write & Validate Spec
OpenAPI Generator → Client SDKs, Server Stubs
Postman → Import spec to auto-generate a Collection
Validators → Verify spec is valid OAS
API Gateways (AWS, Kong) → Import spec for routing & auth
Mock Servers (Stoplight Prism) → Generate a fake API from spec





SCRIPT:
"Here's something that will change how you think about the spec file. It's not just documentation — it's a source of truth that the entire tooling ecosystem can consume.
Postman can import an OpenAPI spec and automatically generate a full Collection with every endpoint pre-populated. So your spec file essentially writes your Postman collection for you. We'll try that in the practice tasks.
API gateways like AWS API Gateway and Kong can import an OpenAPI spec and automatically configure routing, auth, and rate limiting. Your spec becomes infrastructure configuration.
Mock servers like Stoplight Prism read your spec and spin up a fake API server that responds with example data from your spec. Frontend developers can code against a mock before the backend is built.
This is why the spec file is so valuable — it's not a document you write once and forget. It's a living artifact that feeds your entire development toolchain."


SECTION 2 — OPENAPI FILE STRUCTURE
⏱ ~10 minutes

SLIDE 8 — Top-Level Structure of an OpenAPI File
Content:

YAML example with each section labeled:

yaml  openapi: 3.0.3           # OAS version — required

  info:                    # API metadata — required
    title: ...
    version: ...

  servers:                 # Where the API is hosted
    - url: ...

  paths:                   # All endpoints — the core
    /users:
      get: ...

  components:              # Reusable definitions
    schemas: ...
    securitySchemes: ...

  tags:                    # Organize operations in UI
    - name: Users

  security:                # Global auth requirements
    - bearerAuth: []

Note: Only openapi, info, and paths are required. Everything else is optional but recommended.

SCRIPT:
"Let's walk through the skeleton of an OpenAPI 3.x file. You'll write these in YAML — it's more readable than JSON for large nested documents, though both are valid.
openapi declares which version of the spec you're using. Always 3.0.3 or later for new specs.
info is metadata about your API. At minimum it needs a title and version. You can add a description, contact info, license, and terms of service.
servers is a list of base URLs. You can list multiple — dev, staging, production. Swagger UI shows a dropdown to switch between them.
paths is where the actual API definition lives. Every endpoint you expose gets a section here. This is the heart of the spec.
components is your library of reusable definitions. Schemas, responses, parameters, examples, security schemes — anything you want to reference from multiple places goes here.
tags and security are optional but important. Tags group operations in the UI. Security defines global authentication requirements.
Only three are required: openapi, info, and paths. You can write a valid spec with just those three. Everything else layers on top."

SLIDE 9 — The info Section
Content:

Full info block example:

yaml  info:
    title: Users Management API
    description: |
      API for creating, reading, updating, and deleting users.
      
      ## Authentication
      All endpoints except /health require Bearer token authentication.
      
      ## Rate Limiting
      Requests are limited to 1000/hour per API key.
    version: 1.0.0
    contact:
      name: API Support Team
      email: api-support@example.com
      url: https://docs.example.com
    license:
      name: MIT
      url: https://opensource.org/licenses/MIT

Note: description supports Markdown — use it for rich documentation
Note: version here is your API version (e.g. 1.0.0) — distinct from the openapi format version

SCRIPT:
"The info section is your API's introduction. The title and version are required. Everything else is optional but valuable.
Pay attention to the description field. It supports full Markdown, which means you can write rich documentation here — headers, bullet lists, code blocks, links. Use this section to explain authentication requirements, rate limits, important concepts, and any gotchas that consumers need to know before they start. This content appears at the top of the Swagger UI page, so it's prime real estate.
The version in info is your API version — the business version, like '1.0.0' or '2.3'. This is distinct from the openapi version, which is the spec format version. Two different version fields, two different meanings."

SLIDE 10 — The servers Section
Content:

Example with multiple servers:

yaml  servers:
    - url: https://api.dev.example.com/v1
      description: Development server
    - url: https://api.staging.example.com/v1
      description: Staging server
    - url: https://api.example.com/v1
      description: Production server
    - url: http://localhost:8080/v1
      description: Local development

Swagger UI renders this as a dropdown — consumer can switch environments
Server URLs can use variables:

yaml  servers:
    - url: https://api.{environment}.example.com/v1
      variables:
        environment:
          default: dev
          enum: [dev, staging, prod]
SCRIPT:
"The servers section lists the base URLs for your API. When you list multiple, Swagger UI renders a dropdown at the top of the docs page, letting users choose which environment they're testing against. This is a great UX feature for your consumers.
The server URL is the base — all paths defined in paths get appended to this. So if your server URL is https://api.example.com/v1 and your path is /users, the full URL Swagger UI generates is https://api.example.com/v1/users.
Server variables let you parametrize the URL. You define a variable and its allowed values, and Swagger UI renders it as an input field. This is powerful for multi-tenant APIs where each customer has their own subdomain."

SLIDE 11 — YAML Basics for OpenAPI
Content:

Quick YAML refresher since OpenAPI is YAML-heavy:

Indentation = structure (2 spaces, always consistent)
key: value — simple pair
- prefix — list item
| — multi-line string (preserves newlines)
> — folded multi-line string (collapses newlines to spaces)
$ref: '#/components/schemas/User' — reference to another location in the file
Comments: # this is a comment


Common mistake: mixing tabs and spaces (use only spaces — tabs break YAML)
Tool tip: Use Swagger Editor (editor.swagger.io) for real-time validation as you type — it is part of the practice tasks at the end of this session

SCRIPT:
"Before we dive deep into paths and schemas, let me give you a YAML quick reference since OpenAPI specs are almost always written in YAML.
YAML uses indentation to define structure, just like Python uses indentation for code blocks. Always use spaces — never tabs. Inconsistent indentation is the number one cause of confusing parse errors in YAML files. If something seems right but the validator is complaining, check your indentation.
Key-value pairs are key: value. Lists start each item with a dash and a space. Multi-line strings use the pipe character — everything below it at the next indentation level is part of the string, with line breaks preserved.
The $ref syntax is how you reference another part of the file. '#/components/schemas/User' means 'go to the root of this document, find components, find schemas, find User.' The # means the root of the current document. You'll see $ref constantly in well-written specs — it's the key to avoiding repetition.
In the practice tasks at the end of the session, you'll open Swagger Editor, which validates your YAML as you type and shows the rendered output side by side. The feedback loop is extremely fast and it's the best way to learn the format."


SECTION 3 — DEFINING PATHS & OPERATIONS
⏱ ~15 minutes

SLIDE 12 — Path Structure Overview
Content:

How paths works:

yaml  paths:
    /users:            # The path (appended to server URL)
      get:             # HTTP method
        ...
      post:            # Another method on the same path
        ...
    /users/{id}:       # Path with parameter
      get:
        ...
      put:
        ...
      delete:
        ...

Each path can have multiple HTTP methods defined
Path parameters use curly braces: {id}
Operations are the method-level objects (get, post, put, patch, delete)

SCRIPT:
"The paths object is where you define every endpoint your API exposes. The key is the path — the URL relative to the server base URL. The value is an object that maps HTTP methods to their operation definitions.
Notice that multiple methods can be defined on the same path. /users has both get (list all users) and post (create a user) — two operations on the same resource collection. /users/{id} has get, put, and delete — operating on a specific resource.
Path parameters use curly braces in the path — {id}. This signals to OpenAPI and to Swagger UI that this is a variable segment of the URL that needs to be filled in. We'll define those parameters in the operation."

SLIDE 13 — Operation Object — All the Fields
Content:

Full list of fields available on an operation:

yaml  get:
    summary: Short, one-line description (shown in UI)
    description: Long description with Markdown support
    operationId: listUsers  # Unique ID — used by code generators
    tags:
      - Users  # Groups this operation in UI
    parameters: [...]  # Query, path, header, cookie params
    requestBody: {...}  # Only for POST, PUT, PATCH
    responses:  # Required — at least one response
      '200': {...}
    security:  # Override global security for this operation
      - bearerAuth: []
    deprecated: false  # Marks the operation as deprecated in UI

operationId naming: use camelCase describing the action — listUsers, getUserById, createUser

Code generators use this as the method name verbatim: operationId: getUserById → generated Java method getUserById()



SCRIPT:
"Let's look at everything you can put on a single operation. Not all of these are required, but knowing what's available helps you write rich, complete documentation.
summary is a short one-liner — this is what shows next to the method in the Swagger UI list view. description is longer and supports Markdown — use it for detailed explanation.
operationId is a unique string identifier for this operation across the whole spec. This is required for code generation — Swagger Codegen uses it as the method name in the generated client SDK. If you write operationId: getUserById, the generated Java client will have a method literally called getUserById(). Name them like methods: camelCase, verb-noun — listUsers, getUserById, createUser, updateUser, deleteUser. Get in the habit of always including it.
tags groups operations in the Swagger UI. All operations with the same tag collapse into one section. This is how you organize a large API into logical groups.
deprecated: true adds a strikethrough in Swagger UI and is a clean way to signal that an operation is being removed in a future version without immediately breaking consumers."

SLIDE 14 — Defining Parameters — Query Parameters
Content:

Parameter object structure:

yaml  parameters:
    - name: role
      in: query          # query | path | header | cookie
      required: false
      description: Filter users by role
      schema:
        type: string
        enum: [admin, user, moderator]
        default: user

    - name: page
      in: query
      required: false
      schema:
        type: integer
        minimum: 1
        default: 1

    - name: limit
      in: query
      required: false
      schema:
        type: integer
        minimum: 1
        maximum: 100
        default: 20
SCRIPT:
"Parameters describe the inputs your operation accepts. There are four in values: query, path, header, and cookie. Query parameters are the most common for GET requests — they're the key-value pairs after the ? in the URL.
Each parameter needs a name, an in location, whether it's required, and a schema describing its type and constraints.
The schema field is the OpenAPI schema object — the same structure you use for request bodies and response schemas. You can specify type, enum values, min/max, default, examples, and more.
enum is particularly useful — it limits the parameter to a specific set of values. Swagger UI renders enum parameters as a dropdown, which is a great user experience and prevents invalid values from being submitted through the docs.
minimum and maximum on integer parameters document the valid range. A page number of 0 or -5 doesn't make sense; documenting and enforcing that constraint prevents confusing bugs."

SLIDE 15 — Defining Parameters — Path & Header Parameters
Content:

Path parameter:

yaml  parameters:
    - name: id
      in: path
      required: true         # Always true for path params
      description: The numeric ID of the user
      schema:
        type: integer
        minimum: 1
      example: 42

Header parameter:

yaml  parameters:
    - name: X-Request-ID
      in: header
      required: false
      description: Client-provided request ID for tracing
      schema:
        type: string
        format: uuid
      example: "a3b4c5d6-e7f8-9012-abcd-ef1234567890"

Note: Auth headers go in securitySchemes, not parameters

SCRIPT:
"Path parameters are always required: true — if the parameter is in the path, it must be there. There's no optional path parameter. If you need an optional identifier, use a query parameter instead.
Path parameters must match exactly the name you used in curly braces in the path string. If your path is /users/{userId}, your parameter name must be userId — not id, not user_id. They must match precisely or your spec is invalid.
Header parameters work the same way. You document custom headers your API accepts here. One important note: your authorization header goes in securitySchemes under components, not here as a parameter. The authorization mechanism is separate from operational parameters — it has its own dedicated section in OpenAPI for a reason.
The example field on a parameter shows up in Swagger UI as a pre-filled value in the Try it Out form. Always add examples — they dramatically improve the experience of trying out your API."

SLIDE 16 — Defining Request Bodies
Content:

requestBody is used for POST, PUT, and PATCH

yaml  requestBody:
    required: true
    description: The user data to create
    content:
      application/json:
        schema:
          $ref: '#/components/schemas/CreateUserRequest'
        example:
          name: "Jane Doe"
          email: "jane@example.com"
          role: "user"

Multiple content types are supported:

yaml  content:
    application/json:
      schema:
        $ref: '#/components/schemas/CreateUserRequest'
    application/xml:
      schema:
        $ref: '#/components/schemas/CreateUserRequest'

required: true on requestBody means the body cannot be absent
required on individual schema properties means those fields cannot be absent in the body

SCRIPT:
"The requestBody field is where you describe what a client needs to send in the body of a POST, PUT, or PATCH request. It's nested under the operation, not under parameters.
The content key maps content types to their schemas. application/json is what you'll use almost exclusively. But the format does support multiple content types on one endpoint — if your API accepts both JSON and XML, you document both here and they show up as separate tabs in Swagger UI.
The schema field references (or inline-defines) the shape of the body. Use $ref to point to a schema you've defined in components/schemas. This keeps your spec DRY — define the schema once, reference it everywhere.
example at the requestBody level gives Swagger UI a sample body to pre-fill in the Try it Out form. This is extremely helpful for consumers — they don't have to figure out the expected format, they just see a working example and can modify it.
One thing to be careful about: there are two different uses of required here. required: true on the requestBody itself means the body must be present — the request cannot omit it entirely. required inside your schema properties list means specific fields within the body are mandatory. These are different levels and both matter."

SLIDE 17 — Defining Responses
Content:

Responses object structure:

yaml  responses:
    '200':
      description: User retrieved successfully
      content:
        application/json:
          schema:
            $ref: '#/components/schemas/User'
          example:
            id: 42
            name: "Jane Doe"
            email: "jane@example.com"
            role: "user"
            createdAt: "2024-01-15T10:30:00Z"
    '404':
      description: User not found
      content:
        application/json:
          schema:
            $ref: '#/components/schemas/ErrorResponse'
    '401':
      $ref: '#/components/responses/UnauthorizedError'

Document ALL meaningful response codes — not just the success case
Use $ref for commonly repeated responses (401, 403, 500)
Status codes are quoted strings: '200' not 200

SCRIPT:
"The responses object maps HTTP status codes to their descriptions. This is where you tell consumers exactly what they can expect back from your API for every possible outcome.
The status codes are quoted strings — '200' not 200. This is a YAML quirk — numbers can be interpreted ambiguously, so they're quoted in this context.
Every response has at minimum a description. If the response has a body, add the content object with the content type and schema.
Here's what separates good specs from lazy ones: document your error responses too. Don't just document 200. Document 401, 403, 404, 422, 500. A developer consuming your API deserves to know what they'll receive when things go wrong. This is also where your ErrorResponse schema in components becomes invaluable — reference it from every error response.
For responses that appear on every single endpoint — like 401 and 500 — define them once in components/responses and use $ref to reference them. One definition, referenced everywhere."

SLIDE 18 — The 201 Created Response Pattern
Content:

Complete example for a POST operation's 201 response:

yaml  post:
    summary: Create a new user
    requestBody:
      required: true
      content:
        application/json:
          schema:
            $ref: '#/components/schemas/CreateUserRequest'
    responses:
      '201':
        description: User created successfully
        headers:
          Location:
            description: URL of the newly created user
            schema:
              type: string
              example: /api/v1/users/42
        content:
          application/json:
            schema:
              $ref: '#/components/schemas/User'
      '409':
        description: User with this email already exists
        content:
          application/json:
            schema:
              $ref: '#/components/schemas/ErrorResponse'
      '422':
        description: Validation failed
        content:
          application/json:
            schema:
              $ref: '#/components/schemas/ValidationErrorResponse'
SCRIPT:
"Let's look at a complete POST operation with proper 201 handling. Notice that the responses object for this endpoint documents three different scenarios — success, duplicate, and validation failure.
The 201 response includes a headers section. This is how you document the Location header we discussed in the previous session — the URL of the newly created resource. This is part of the REST contract and it belongs in your spec.
The content on a 201 response shows what the server returns — typically the full representation of the created resource, now including server-generated fields like the ID and creation timestamp.
I've referenced two different error schemas here — ErrorResponse for the conflict case and ValidationErrorResponse for the validation failure. Depending on your design, these might be the same schema or different — maybe validation errors always include the details array while other errors don't. Your spec makes this distinction explicit. We'll look at how to define both of those schemas in the components section."


SECTION 4 — SCHEMAS AND COMPONENTS
⏱ ~15 minutes

SLIDE 19 — Schema Object — Core Types and Formats
Content:

A schema describes the shape and constraints of data
Foundation properties:

type — string | integer | number | boolean | array | object
format — adds precision to basic types:

String formats: email, date-time, uuid, uri, date
Integer formats: int32, int64
Number formats: float, double


description — explains the field's purpose
example — sample value shown in docs
default — default value if not provided
nullable — whether null is a valid value (distinct from the field being optional — see next slide)




⚠️ Format note: Format values like email, date-time, and uuid are primarily documentation hints in the OpenAPI spec itself. Enforcement depends on your tooling — API gateways and request validators will actively reject values that don't match the format; standard Swagger UI will not.

SCRIPT:
"The schema object is the most important building block in an OpenAPI spec. Every request body, every response body, every parameter is described using a schema. Let me walk through the properties.
type is foundational — it's the data type. Six possible values for JSON data: string, integer, number, boolean, array, object. String and integer will be your most common.
format adds precision to basic types. A string is just characters — but format: email says it must be a valid email address. format: date-time means it must be an ISO 8601 timestamp. format: uuid means it must be a UUID. Worth knowing: format values are primarily documentation hints in the spec itself. Swagger UI won't reject a badly formatted email for you — but if you connect an API gateway or a validation library, those tools will read the format and actively enforce it.
nullable: true means the field can explicitly be set to null. This is different from a field being optional — optional means it can be absent from the object entirely. Nullable means it must be present, but its value can be null. I'll say that again on the next slide because it trips people up constantly."

SLIDE 20 — Schema Object — Object & Array Properties
Content:

Object-specific properties:

properties — defines the fields an object contains (each is itself a schema)
required — array of property names that must be present (object-level, not property-level)


Array-specific property:

items — schema describing each element in the array
minItems / maxItems — size constraints


Validation constraints (work on any type):

enum — list of allowed values
minimum / maximum — numeric range
minLength / maxLength — string length
pattern — regex validation




⚠️ required vs nullable — important distinction:

required: [name, email] on an object = these fields must be present in the JSON
nullable: true on a field = the field can be present but set to null
A field can be required AND nullable (must be present, but can be null)
A field can be optional AND non-nullable (can be absent, but if present cannot be null)


SCRIPT:
"Here's the second half of schema properties — the ones specific to objects and arrays.
For objects, properties defines the fields and required lists which ones must be present. Notice that required is an array at the object level — it's not a property-level flag. You don't write required: true inside a property definition; you list the required property names in the parent object's required array.
For arrays, items describes what's in the array. An array of strings is type: array, items: {type: string}. An array of User objects is type: array, items: {$ref: '#/components/schemas/User'}.
Now — the required vs nullable distinction. This is the thing that trips up almost every student. required means the field must be present in the JSON. nullable: true means the value of the field can be null. These are independent. A field can be required AND nullable — it must be present, but you can pass null as its value. A field can be optional AND non-nullable — you can leave it out, but if you include it, it can't be null. Get comfortable with that distinction before you write your first real spec."

SLIDE 21 — Defining a Schema — Simple Object
Content:

Full User schema example:

yaml  components:
    schemas:
      User:
        type: object
        required:
          - id
          - name
          - email
        properties:
          id:
            type: integer
            minimum: 1
            description: Unique identifier
            example: 42
          name:
            type: string
            minLength: 1
            maxLength: 100
            description: Full name of the user
            example: "Jane Doe"
          email:
            type: string
            format: email
            description: Email address (must be unique)
            example: "jane@example.com"
          role:
            type: string
            enum: [admin, user, moderator]
            default: user
            description: User's role in the system
          createdAt:
            type: string
            format: date-time
            description: When the user was created (server-generated)
            example: "2024-01-15T10:30:00Z"
          updatedAt:
            type: string
            format: date-time
            description: When the user was last updated
SCRIPT:
"Here's a complete User schema. Let me point out the important details.
The required array at the top level lists which properties must always be present. id, name, and email are required. role, createdAt, and updatedAt are not in the required array — role has a default, and createdAt/updatedAt might not be present in all contexts.
Every property has a description. This shows up in Swagger UI as explanatory text. Descriptions should be concise but complete — explain what the field means, not just what type it is.
Notice that createdAt and updatedAt have no constraints on their values — they're server-generated, so the client has no control over them. Documenting them in the response schema tells consumers they'll receive them, even though they can't set them.
This User schema represents the response shape — what the server sends back. It's different from what a client sends when creating a user. That's a separate schema, which we'll look at next."

SLIDE 22 — Request vs Response Schemas
Content:

Why have separate schemas for input and output?

Request schema: fields the client provides
Response schema: fields the server returns (includes server-generated fields)


Example pair:

yaml  CreateUserRequest:     # What client sends in POST body
    type: object
    required:
      - name
      - email
    properties:
      name:
        type: string
        minLength: 1
        maxLength: 100
      email:
        type: string
        format: email
      role:
        type: string
        enum: [admin, user, moderator]
        # Note: optional — defaults to "user" if not provided

  User:                  # What server returns
    type: object
    required:
      - id
      - name
      - email
      - role
      - createdAt
    properties:
      id:
        type: integer
      name:
        type: string
      email:
        type: string
        format: email
      role:
        type: string
        enum: [admin, user, moderator]
      createdAt:
        type: string
        format: date-time
      updatedAt:
        type: string
        format: date-time
SCRIPT:
"This is an important pattern to adopt from the start: separate schemas for input (what the client sends) and output (what the server returns).
The CreateUserRequest schema has name and email as required — those are the minimum fields needed to create a user. role is optional — if not provided, the server defaults to user. There's no id, no createdAt, no updatedAt — the client doesn't provide those, the server generates them.
The User schema is the full representation. It includes everything, including id, createdAt, and updatedAt. These are always present in the response because the server sets them.
Why does this matter? Because if you used one schema for both, you'd either have to make id optional — misleading, since it's always in responses — or required — incorrect, since you never send it on creation. Separate schemas let each context be precisely correct.
You'll also likely want an UpdateUserRequest schema for PATCH — it might only expose certain fields as patchable. Maybe you can change name and role but not email or id. Separate input schema makes that explicit."

SLIDE 23 — Nested Objects & Arrays
Content:

Schema with nested object:

yaml  Order:
    type: object
    properties:
      id:
        type: integer
      status:
        type: string
        enum: [pending, processing, shipped, delivered, cancelled]
      customer:
        $ref: '#/components/schemas/User'   # Nested object via $ref
      shippingAddress:
        type: object                         # Inline nested object
        required: [street, city, country]
        properties:
          street:
            type: string
          city:
            type: string
          postalCode:
            type: string
          country:
            type: string
      items:
        type: array
        minItems: 1
        items:
          $ref: '#/components/schemas/OrderItem'   # Array of objects
      total:
        type: number
        format: float
        minimum: 0
SCRIPT:
"Real-world response objects are rarely flat — they contain nested objects and arrays. OpenAPI handles this naturally.
To nest a schema you've already defined, use $ref. The customer field references the full User schema — Swagger UI will render this as an expandable nested object showing all the User fields. This is reuse at its best.
You can also define nested objects inline — the shippingAddress here is defined directly within the Order schema rather than in components. This is fine for small sub-objects that aren't reused elsewhere. If you find yourself copy-pasting the same inline object definition multiple places, that's a signal to extract it to components/schemas.
Arrays use type: array combined with items to describe what the array contains. minItems: 1 is a validation constraint — this order must have at least one item."

SLIDE 24 — The $ref Mechanism
Content:

$ref = JSON Reference — points to a location in the document
Syntax: $ref: '#/components/schemas/SchemaName'
The # means root of the current document
Can reference external files too: $ref: './schemas/user.yaml'
What you can $ref:

Schemas: '#/components/schemas/User'
Responses: '#/components/responses/NotFound'
Parameters: '#/components/parameters/PageParam'
Request bodies: '#/components/requestBodies/CreateUser'
Examples: '#/components/examples/UserExample'


Rule: If you're defining the same thing twice, extract it and use $ref


⚠️ Important limitation: In OpenAPI 3.0.x, when you use $ref, you cannot add sibling properties alongside it. This will NOT work:
yaml# WRONG — sibling properties alongside $ref are ignored in 3.0.x
myField:
  $ref: '#/components/schemas/User'
  description: "This will be ignored"
To extend a schema, use allOf (covered in the Advanced Patterns section).

SCRIPT:
"The $ref mechanism is OpenAPI's version of DRY — Don't Repeat Yourself. Instead of copy-pasting a schema definition into every operation that needs it, you define it once in components and reference it everywhere.
The syntax '#/components/schemas/User' breaks down as: # is the root of this YAML file, then it navigates to components, then schemas, then User.
You're not limited to schemas. Common responses — like 401 or 500 — can be defined in components/responses and $ref'd from every endpoint. Common pagination parameters can be defined in components/parameters and reused.
One gotcha worth knowing: in OpenAPI 3.0.x, $ref is a takeover. When you put $ref on an object, you cannot put anything else alongside it — sibling properties are silently ignored. So if you want to extend a referenced schema with additional fields, you can't just add properties next to $ref. You have to use allOf, which we'll cover later.
Rule of thumb: if you write the same definition more than once, extract it. Your spec will be shorter, easier to maintain, and when you need to change a schema, you change it in one place."

SLIDE 25 — Components — Schemas, Responses, and Parameters
Content:

The components section is your spec's shared library
schemas — your data models:

yaml  components:
    schemas:
      User: {...}
      CreateUserRequest: {...}
      ErrorResponse: {...}
      ValidationErrorResponse: {...}

responses — reusable response objects (use for anything repeated across many endpoints):

yaml    responses:
      UnauthorizedError:
        description: Authentication required
        content:
          application/json:
            schema:
              $ref: '#/components/schemas/ErrorResponse'
      NotFoundError:
        description: Resource not found
        content:
          application/json:
            schema:
              $ref: '#/components/schemas/ErrorResponse'

parameters — reusable parameter objects (pagination params are the classic case):

yaml    parameters:
      PageParam:
        name: page
        in: query
        schema:
          type: integer
          default: 1
      LimitParam:
        name: limit
        in: query
        schema:
          type: integer
          default: 20
          maximum: 100
SCRIPT:
"The components section is your spec's library. Let me walk through the most commonly used subsections.
schemas is where your data models live — User, Order, Product, CreateUserRequest, ErrorResponse, and so on. Everything you want to reference via $ref goes here. This is the most heavily used part of components.
responses holds reusable response objects. Your 401 Unauthorized response is going to be the same on every single secured endpoint. Define it once here and $ref it everywhere. Same for 500. You end up writing two lines — $ref: '#/components/responses/UnauthorizedError' — instead of a full response block on every endpoint.
parameters holds reusable parameters. Pagination parameters — page, limit — are identical on every list endpoint. Define them once in components, reference them from every GET list operation. Consistency across all your list endpoints is a sign of a well-designed API."

SLIDE 26 — Components — requestBodies and securitySchemes
Content:

requestBodies — reusable request body objects (less commonly used but valuable for large APIs):

yaml  components:
    requestBodies:
      CreateUserBody:
        required: true
        content:
          application/json:
            schema:
              $ref: '#/components/schemas/CreateUserRequest'

securitySchemes — defines your authentication mechanisms:

yaml    securitySchemes:
      bearerAuth:
        type: http
        scheme: bearer
        bearerFormat: JWT
        description: |
          JWT Bearer token. Obtain by calling POST /auth/login.
          Include in requests as: Authorization: Bearer <token>

Note: Defining a security scheme here does not apply it to any operation — that requires a separate security declaration (covered in the next section)

SCRIPT:
"Two more subsections of components.
requestBodies is for reusable request body definitions. This is less commonly used because most endpoints have unique request bodies. But if you have a body that's shared across multiple operations — say a shared update format — you can define it once here.
securitySchemes is where you register your authentication mechanisms. Every auth scheme your API supports gets an entry here with a name you choose — bearerAuth in this example. This name is just a label; you'll reference it by this label when you apply security to operations.
Important: defining a security scheme in securitySchemes does not automatically secure anything. It's just registration — 'here are the auth mechanisms my API understands.' You still need to declare security requirements at the global level or per operation. We cover that on the next slides."


SECTION 5 — SECURITY IN OPENAPI
⏱ ~8 minutes

SLIDE 27 — Documenting Authentication
Content:

Two steps to document auth in OpenAPI:

Define the security scheme in components/securitySchemes
Apply the security requirement globally or per-operation


Common security scheme types:

http with scheme: bearer — JWT Bearer tokens ← most common for this course
http with scheme: basic — Basic Auth (username:password)
apiKey — API key in header, query, or cookie
oauth2 — Full OAuth 2.0 flows
openIdConnect — OpenID Connect



SCRIPT:
"Security documentation in OpenAPI is a two-step process. First you define what authentication mechanisms your API supports — this goes in components/securitySchemes. Then you declare which operations require which schemes — this can be global or per-operation.
The most common scheme type you'll implement is http bearer — JWT tokens. We'll build that in full on the next slide. API key and OAuth2 are covered briefly after. Basic auth you should recognize from HTTP fundamentals — it's just a base64-encoded username and password in the Authorization header, rarely used in modern APIs."

SLIDE 28 — Bearer Token Security Scheme
Content:

Define the scheme (in components/securitySchemes):

yaml  components:
    securitySchemes:
      bearerAuth:
        type: http
        scheme: bearer
        bearerFormat: JWT
        description: |
          JWT Bearer token. Obtain a token by calling POST /auth/login.
          Include it in every request as:
          Authorization: Bearer <your-token>

Apply globally (all endpoints require auth by default):

yaml  security:
    - bearerAuth: []   # [] means no scopes required (relevant for OAuth2)

Override per operation to make a specific endpoint public:

yaml  paths:
    /auth/login:
      post:
        security: []   # Empty array = no security required (public endpoint)
        summary: Login and receive a token
SCRIPT:
"Here's the complete bearer auth pattern. Define bearerAuth in components/securitySchemes with type http, scheme bearer, and optionally bearerFormat: JWT as documentation metadata.
Then apply it globally with the top-level security field. The value - bearerAuth: [] means all operations require this scheme by default. The empty array would contain OAuth scopes if we were using OAuth2 — for simple bearer tokens there are no scopes, so it stays empty.
Now here's the key override pattern: your login endpoint needs to be publicly accessible — you can't require a token to obtain a token. So on the login operation, you set security: [] — an empty array — which explicitly overrides the global security and says 'this operation requires no authentication.' This pattern applies to any public endpoint: health checks, public product listings, public documentation endpoints.
When you load a secured spec into Swagger UI, every secured operation shows a small lock icon next to it. At the top of the entire page there's an Authorize button — you click it, paste your JWT into the input field, and click Authorize. From that point on, every Try it Out request automatically includes the Authorization header with your token. Unlocked operations show an open lock, secured ones show a closed lock."

SLIDE 29 — API Key Security Scheme
Content:

API Key (common for third-party integrations):

yaml  components:
    securitySchemes:
      apiKeyAuth:
        type: apiKey
        in: header          # header | query | cookie
        name: X-API-Key     # The header/param name
        description: API key obtained from the developer portal

Apply the same way as bearer auth:

yaml  security:
    - apiKeyAuth: []

In Swagger UI, renders as an input field in the Authorize dialog — user pastes their key

SCRIPT:
"API Key authentication is common with third-party SaaS APIs — you sign up, get a long-lived key, and include it in every request. In OpenAPI, you define the apiKey type, specify whether it goes in a header or query parameter, and name the header or parameter. The example here puts the key in a header called X-API-Key.
When applied to operations in Swagger UI, it renders as a text input in the Authorize dialog — paste your key, click Authorize, and every subsequent Try it Out request includes it."

SLIDE 30 — OAuth2 Security Scheme (Reference)
Content:

OAuth2 for APIs that delegate authentication to an external identity provider:

yaml  components:
    securitySchemes:
      oauth2:
        type: oauth2
        flows:
          authorizationCode:
            authorizationUrl: https://auth.example.com/oauth/authorize
            tokenUrl: https://auth.example.com/oauth/token
            scopes:
              read:users: Read user data
              write:users: Create and update users

Scopes let you document granular permissions per operation:

yaml  security:
    - oauth2: [read:users]

You will not implement OAuth2 in this course — this slide is a reference for when you encounter it in the wild

SCRIPT:
"OAuth2 is the most complex scheme but you'll encounter it frequently with external APIs. The core idea: instead of your API issuing tokens itself, it delegates to an external auth server. The spec documents the URLs for that auth server and the scopes — named permissions — your API exposes.
Scopes are powerful: you can require different scopes on different operations. A read endpoint only needs read:users; a delete endpoint needs delete:users. Consumers of your API know exactly what permissions they need to call each endpoint.
You won't be implementing this in the course, but you'll see it in the wild when integrating with APIs that use Google, GitHub, or Auth0 for authentication. Knowing how to read the OAuth2 spec block will help you navigate those integrations."


SECTION 6 — SWAGGER UI
⏱ ~10 minutes

SLIDE 31 — Swagger UI Overview
Content:

What Swagger UI renders from your spec:

API title, description, version at the top
Server selector dropdown (if multiple servers)
Authorize button (if security schemes defined)
Operations grouped by tags, each expandable
For each operation: method badge, path, summary
Expanded view: parameters, request body schema, response schemas
"Try it out" button on each operation


It's a JavaScript library — embed it in any web page
Available as a Docker container for standalone hosting

SCRIPT:
"Swagger UI takes your OpenAPI spec file and renders it as a dynamic, interactive HTML page. Let me describe what a user sees when they land on it.
At the top is the API metadata from your info section — title, description, version. If you defined multiple servers, there's a dropdown to choose which environment. If you defined security schemes, there's an Authorize button to set credentials.
Below that, your operations are organized into collapsible groups by tag. Within each group, operations are listed as rows — each one shows a colored badge for the HTTP method (green for GET, blue for POST, yellow for PUT and PATCH, red for DELETE), then the path, then the summary text.
Clicking a row expands it. The expanded view shows everything: each parameter listed with its type, whether it's required, and its description. If there's a request body, it shows the schema. Below that, every documented response code is listed — click any one and it shows the full schema for that response.
The Try it Out button in the top right of an expanded operation is what makes this more than just documentation — it converts the whole thing into a live form. We'll look at that on the next slide."

SLIDE 32 — Try It Out in Swagger UI
Content:

Workflow in Swagger UI Try it Out:

Click the operation to expand it
Click "Try it out" button
Fill in parameters (path, query) in the form fields
Fill in request body using the pre-filled example JSON
Click "Execute"
See: Curl command equivalent, request URL, response code, response headers, response body


The curl command shown is copy-pasteable — great for sharing and debugging
Requires: API must be running and CORS must be configured to allow requests from the Swagger UI origin

SCRIPT:
"The Try it Out flow is extremely intuitive. You expand an operation, click the Try it Out button in the top right of that section, and the static documentation transforms into an interactive form.
Path parameters and query parameters become labeled input fields. Any enum parameters become dropdowns so you can only pick valid values. If the operation has a request body, the body field is pre-filled with your example JSON — so instead of starting from a blank text box, you start from a working example you can modify.
You fill in what you want and click Execute. Below the button, three things appear: first, the actual curl command that was run — you can copy this and paste it in a terminal or send it to someone for debugging. Second, the full request URL with all parameters substituted in. Third, the response: status code, response headers, and the response body pretty-printed.
One thing to be aware of: Try it Out makes real HTTP requests from the browser. For it to work, your API needs to be running and accessible, and CORS must allow requests from the Swagger UI origin. When you integrate Swagger UI into your Spring Boot app — which we're about to cover — this is all handled automatically."


SECTION 7 — SPRING BOOT INTEGRATION WITH SPRINGDOC-OPENAPI
⏱ ~12 minutes

SLIDE 33 — Introducing springdoc-openapi
Content:

springdoc-openapi is the library that bridges Spring Boot and OpenAPI
Add one dependency → get:

Auto-generated OpenAPI spec at /v3/api-docs
Swagger UI at /swagger-ui/index.html
Spec reflects your actual controllers in real time


Maven dependency:

xml  <dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>2.x.x</version>
  </dependency>

Optional configuration in application.properties:

properties  springdoc.api-docs.path=/v3/api-docs
  springdoc.swagger-ui.path=/swagger-ui.html
  springdoc.swagger-ui.operationsSorter=method
SCRIPT:
"This is the section Java students will use immediately in their projects. The springdoc-openapi library is the bridge between your Spring Boot controllers and the OpenAPI spec format we've been studying.
Add the dependency, run your application, and navigate to /swagger-ui/index.html. Swagger UI is live — it reads a spec auto-generated from your controllers at runtime. No YAML file to maintain. The spec is derived directly from your code.
The configuration properties let you customize the paths and UI behavior. The defaults work fine for most projects — but it's useful to know you can move the UI to /api-docs or change how operations are sorted in the UI."

SLIDE 34 — Enriching the Spec — @Operation and @ApiResponse
Content:

Auto-generation produces a valid spec, but without descriptions or documented error responses
Use annotations to enrich it:

java  @Operation(
      summary = "Get user by ID",
      description = "Returns a single user. Requires authentication."
  )
  @ApiResponse(responseCode = "200", description = "User found",
      content = @Content(schema = @Schema(implementation = User.class)))
  @ApiResponse(responseCode = "404", description = "User not found",
      content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  @ApiResponse(responseCode = "401", description = "Authentication required")
  @GetMapping("/users/{id}")
  public ResponseEntity<User> getUserById(@PathVariable Long id) {
      // ...
  }

@Operation — adds summary and description to the operation
@ApiResponse — documents a specific status code and its response body schema
The annotations in your code become the spec — update the annotation, the spec updates automatically

SCRIPT:
"The auto-generated spec is a great starting point, but it won't have descriptions, examples, or documented error responses unless you add annotations.
@Operation maps directly to the summary and description fields we defined in YAML. @ApiResponse maps to each entry in the responses object. The content and schema parameters on @ApiResponse tell springdoc which Java class represents the response body — it introspects the class and generates the schema automatically.
Notice that I'm annotating all three responses: 200, 404, and 401. This is the same discipline we talked about when writing YAML — document your error cases, not just the happy path. The annotation-based approach makes it easy to forget error responses because they're not part of the method's return type. Be deliberate about adding them."

SLIDE 35 — Enriching the Spec — @Parameter and @Schema
Content:

@Parameter — enriches path and query parameters:

java  @GetMapping("/users/{id}")
  public ResponseEntity<User> getUserById(
      @Parameter(description = "The numeric ID of the user", example = "42")
      @PathVariable Long id
  ) { ... }

@Schema — enriches model classes used in request/response bodies:

java  public class CreateUserRequest {

      @Schema(description = "Full name of the user",
               example = "Jane Doe",
               minLength = 1, maxLength = 100)
      private String name;

      @Schema(description = "Email address. Must be unique.",
               example = "jane@example.com",
               format = "email")
      private String email;

      @Schema(description = "User role. Defaults to 'user' if not provided.",
               allowableValues = {"admin", "user", "moderator"},
               defaultValue = "user")
      private String role;
  }

@Schema on model fields generates the same description, example, format, enum properties we wrote in YAML

SCRIPT:
"Two more annotations to know.
@Parameter goes on method parameters — path variables, query params, request headers. It maps to the parameter-level fields we discussed: description, example. Without this annotation, springdoc will generate the parameter from the method signature but with no description or example, which makes the Swagger UI less useful.
@Schema goes on your model classes and their fields. This is where you add all the same metadata we defined in YAML schemas — description, example, format, allowable values, min/max length. When you put @Schema on your fields and springdoc reads your class, the generated schema in the spec will have all that richness.
The key insight: the annotations in your Java code are the spec. When you change @Schema(maxLength = 100) to @Schema(maxLength = 200), your OpenAPI spec updates automatically on the next server restart. This is the power of code-first — the spec stays in sync with the code because it's derived from it."

SLIDE 36 — Global API Metadata with @OpenAPIDefinition
Content:

To populate the info, servers, and global security sections from code:

java  @OpenAPIDefinition(
      info = @Info(
          title = "Users Management API",
          version = "1.0.0",
          description = "API for managing users. All endpoints require authentication."
      ),
      servers = {
          @Server(url = "http://localhost:8080", description = "Local"),
          @Server(url = "https://api.example.com", description = "Production")
      }
  )
  @SecurityRequirement(name = "bearerAuth")
  @SpringBootApplication
  public class Application { ... }

Register the security scheme itself in a @Configuration class:

java  @Configuration
  public class OpenApiConfig {
      @Bean
      public OpenAPI openAPI() {
          return new OpenAPI()
              .components(new Components()
                  .addSecuritySchemes("bearerAuth",
                      new SecurityScheme()
                          .type(SecurityScheme.Type.HTTP)
                          .scheme("bearer")
                          .bearerFormat("JWT")));
      }
  }
SCRIPT:
"To populate the top-level metadata — the info block, servers, and global security — you use @OpenAPIDefinition on your main application class.
This maps directly to everything we covered in the YAML structure section. @Info becomes the info block. @Server entries become the servers array. @SecurityRequirement applies the global security requirement.
The security scheme itself — the equivalent of components/securitySchemes in YAML — is registered in a @Configuration bean. You create an OpenAPI bean, add your security schemes to its components, and springdoc picks it up automatically.
If you've been following along from the YAML sections, you'll notice the one-to-one mapping between what we wrote by hand and what these annotations produce. The format is the same — you're just driving it from Java code instead of a YAML file."

SLIDE 37 — Overriding Security Per-Endpoint
Content:

To make a specific endpoint public (override global security):

java  @Operation(security = {})  // Empty array = no security required
  @PostMapping("/auth/login")
  public ResponseEntity<TokenResponse> login(@RequestBody LoginRequest req) {
      // ...
  }

This is the code-first equivalent of security: [] in YAML
Use @SecurityRequirement to apply a different security scheme to a specific operation:

java  @SecurityRequirement(name = "bearerAuth")
  @GetMapping("/users")
  public ResponseEntity<List<User>> listUsers() { ... }
SCRIPT:
"The same security override pattern from YAML applies in code-first. On your login endpoint — or any public endpoint — add security = {} to the @Operation annotation. This tells springdoc to generate security: [] for that operation, overriding the global requirement.
This is exactly the pattern we covered in the security section: apply security globally, then explicitly opt out on public endpoints. The approach is the same whether you're writing YAML by hand or using annotations. The spec it produces is identical."


SECTION 8 — ADVANCED SCHEMA PATTERNS
⏱ ~8 minutes

SLIDE 38 — Schema Composition — allOf (Inheritance)
Content:

allOf — The value must match ALL of the listed schemas
Primary use: schema inheritance / extension

yaml  AdminUser:
    allOf:
      - $ref: '#/components/schemas/User'    # Inherit all User fields
      - type: object
        properties:
          adminLevel:
            type: integer
            enum: [1, 2, 3]
          lastAuditDate:
            type: string
            format: date-time

Result: AdminUser has all fields of User PLUS adminLevel and lastAuditDate
This is also how you extend a $ref with additional properties — the only correct way in OpenAPI 3.0.x

SCRIPT:
"OpenAPI supports schema composition — combining schemas — using allOf, oneOf, and anyOf. Each has a distinct use case. Let's go through them one at a time.
allOf means 'the value must match all of these schemas simultaneously.' Its primary use is inheritance — extending an existing schema with additional fields. In this example, AdminUser is a User plus some admin-specific fields. Rather than copy-pasting all the User properties into AdminUser, we reference User and add only the new fields.
This is also the answer to the $ref limitation I mentioned earlier. If you want to use a $ref and add extra properties, you can't put them alongside the $ref directly. Instead, wrap both in an allOf — one entry for the $ref, one entry for the new properties. The validator combines them."

SLIDE 39 — Schema Composition — oneOf (Polymorphism)
Content:

oneOf — The value must match EXACTLY ONE of the listed schemas
Primary use: polymorphic fields — a value that can be one of several distinct shapes

yaml  Payment:
    oneOf:
      - $ref: '#/components/schemas/CreditCardPayment'
      - $ref: '#/components/schemas/BankTransferPayment'
      - $ref: '#/components/schemas/CryptoPayment'
    discriminator:
      propertyName: type   # Field that identifies which schema applies
      mapping:
        credit_card: '#/components/schemas/CreditCardPayment'
        bank_transfer: '#/components/schemas/BankTransferPayment'
        crypto: '#/components/schemas/CryptoPayment'

anyOf — The value must match AT LEAST ONE schema (less strict, less common)
Rule of thumb: prefer oneOf over anyOf when the types are mutually exclusive

SCRIPT:
"'oneOfis for polymorphism — when a field can take several distinct shapes, but only one at a time. A Payment might be a credit card payment, a bank transfer, or a crypto payment. It's never a mix of two.oneOf` says exactly that: match exactly one of these schemas.
The discriminator field is important for code generation. It tells validators and generators which property to look at to decide which schema applies. When the type field says credit_card, use CreditCardPayment. When it says bank_transfer, use BankTransferPayment. This generates clean, type-safe client code in Java or TypeScript.
anyOf is similar but less strict — at least one schema must match, but multiple matches are allowed. It's less commonly used in practice. When in doubt about which to use between oneOf and anyOf, ask yourself: 'Can a valid value match more than one of these schemas?' If no, use oneOf. If yes, use anyOf."

SLIDE 40 — Documenting Error Schemas
Content:

Reusable error schemas in components:

yaml  components:
    schemas:
      ErrorResponse:
        type: object
        required: [error]
        properties:
          error:
            type: object
            required: [code, message]
            properties:
              code:
                type: string
                description: Machine-readable error identifier
                example: NOT_FOUND
              message:
                type: string
                description: Human-readable error description
                example: The requested resource was not found.
              requestId:
                type: string
                description: Unique request ID for tracing

      ValidationErrorResponse:
        allOf:                                  # ← extends ErrorResponse
          - $ref: '#/components/schemas/ErrorResponse'
          - type: object
            properties:
              error:
                type: object
                properties:
                  details:
                    type: array
                    items:
                      type: object
                      properties:
                        field:
                          type: string
                        issue:
                          type: string
SCRIPT:
"ErrorResponse is the base error shape — every error includes an error object with code and message at minimum.
ValidationErrorResponse extends ErrorResponse using allOf — notice we're using the composition pattern from the previous slides here. It has everything a base error has, plus it adds the details array with field-level error information.
This accurately reflects our error handling design: all errors have code and message, but only validation errors have the details array. The spec makes this structural difference explicit, and your consumers can write defensive code that handles each case correctly.
Reference these from every operation that can produce errors — $ref: '#/components/schemas/ErrorResponse' on your 404, 401, 409 responses; $ref: '#/components/schemas/ValidationErrorResponse' on your 422 responses."

SLIDE 41 — Documenting Pagination
Content:

Reusable pagination query parameters:

yaml  components:
    parameters:
      PageParam:
        name: page
        in: query
        required: false
        schema:
          type: integer
          minimum: 1
          default: 1
      LimitParam:
        name: limit
        in: query
        required: false
        schema:
          type: integer
          minimum: 1
          maximum: 100
          default: 20

Use on list operations:

yaml  parameters:
    - $ref: '#/components/parameters/PageParam'
    - $ref: '#/components/parameters/LimitParam'

Paginated response schema:

yaml  PaginatedUsersResponse:
    type: object
    properties:
      data:
        type: array
        items:
          $ref: '#/components/schemas/User'
      pagination:
        type: object
        properties:
          page: { type: integer, example: 2 }
          limit: { type: integer, example: 20 }
          total: { type: integer, example: 150 }
          totalPages: { type: integer, example: 8 }
SCRIPT:
"Pagination is common on any list endpoint, and OpenAPI makes it easy to document consistently. Define the pagination parameters once in components/parameters and reference them from every list operation. Two lines of $ref instead of repeating the same parameter definitions on every GET endpoint.
The pagination metadata in the response is equally important. Include the current page, the limit, the total number of records, and the total number of pages. This is everything a client needs to build a pagination UI or iterate through all records programmatically.
Wrapping the actual data in a data property and the pagination info in a pagination property is a clean, widely-used pattern. It leaves room to add other envelope fields in the future — links, metadata — without changing the shape of the data array."


SECTION 9 — PUTTING IT ALL TOGETHER
⏱ ~8 minutes

SLIDE 42 — A Complete Endpoint — From Scratch
Content:

Full example — GET /users endpoint, completely documented:

yaml  paths:
    /users:
      get:
        summary: List all users
        description: |
          Returns a paginated list of users. Results can be filtered by role.
          Requires authentication.
        operationId: listUsers
        tags:
          - Users
        parameters:
          - $ref: '#/components/parameters/PageParam'
          - $ref: '#/components/parameters/LimitParam'
          - name: role
            in: query
            required: false
            schema:
              type: string
              enum: [admin, user, moderator]
        responses:
          '200':
            description: Paginated list of users
            content:
              application/json:
                schema:
                  $ref: '#/components/schemas/PaginatedUsersResponse'
          '400':
            description: Invalid query parameters
            content:
              application/json:
                schema:
                  $ref: '#/components/schemas/ErrorResponse'
          '401':
            $ref: '#/components/responses/UnauthorizedError'
SCRIPT:
"Let's look at a complete, production-quality endpoint definition. Take a moment to read through it.
Every field is present and purposeful. operationId uniquely names this operation. The description explains what the endpoint does, what filters are available, and that it requires authentication. Parameters are a mix of $ref for reusable pagination params and an inline definition for the role filter. Responses document the happy path and two error cases, with the 401 using a $ref to avoid repeating that response definition.
This is the standard you want to hold yourself to. Every endpoint should be this complete. When someone reads this spec — whether it's a teammate, a future you, or a generated client SDK — they have everything they need."

SLIDE 43 — The Spec as a Contract
Content:

The OpenAPI spec is a contract between API producer and consumers
What that means in practice:

Frontend and backend can work in parallel using the spec
QA writes tests against the spec before code is complete
Breaking changes are visible in spec diffs
Client SDKs are generated from the spec — reliable, always current
API gateways enforce the spec — invalid requests are rejected before reaching your code


Tools that use your spec:

Postman (import spec → auto-generate collection)
Prism (mock server from spec)
Dredd (contract testing — run tests derived from spec against live API)
AWS API Gateway, Kong (import spec for routing/validation)



SCRIPT:
"The most important mental model I want you to leave with is this: the OpenAPI spec is not a nice-to-have documentation artifact. It's a contract. It's the formal agreement between your API and anyone who depends on it.
When you have a complete, accurate spec, remarkable things become possible. Your frontend team can start building against a mock server generated from the spec while the backend is still being built. You can diff two versions of your spec and immediately see every breaking change — removed fields, changed types, removed endpoints. You can generate client SDKs in any language and they'll be accurate because they come directly from the spec.
Dredd is a tool worth knowing — it reads your OpenAPI spec, generates HTTP requests from it, runs them against your live API, and tells you whether your API's actual behavior matches its documented behavior. That's automated contract testing.
The spec-driven workflow is what mature API teams do. You're learning it at the right time."

SLIDE 44 — Common Spec Mistakes to Avoid
Content:

❌ Only documenting the 200 response — document all error responses
❌ Not using $ref — duplicated schemas get out of sync
❌ Missing operationId — breaks code generation
❌ Vague descriptions — "Returns a user" tells me nothing useful
❌ No examples — makes Try it Out painful to use
❌ Confusing required and nullable — different concepts, both matter
❌ Using the same schema for input and output — separate CreateRequest from the response model
❌ Adding sibling properties alongside $ref — use allOf to extend
❌ Not versioning the spec alongside the code — spec should be committed to the repo and updated with every PR that changes the API

SCRIPT:
"Let me give you the shortlist of the most common mistakes so you can avoid them from the start.
The most frequent is only documenting the happy path. Your spec is useless as a contract if it doesn't tell consumers what to expect when things go wrong. Document every status code your endpoint can realistically return.
Not using $ref means your User schema is defined in fifteen different places. When the User gets a new field, you update it in one place and fourteen others are now wrong. DRY applies to specs just as much as code.
Missing operationId breaks code generation entirely. Always include it.
Confusing required and nullable — I've mentioned this twice because I see it wrong in real-world specs constantly. They're independent concepts. Know the difference.
The last one is critical for professional work: commit your spec to version control alongside your code. A PR that changes an API should always include the updated spec."

SLIDE 45 — Validating Your OpenAPI Spec
Content:

Tools for validation:

Swagger Editor (editor.swagger.io) — Real-time visual validation — use during practice tasks
openapi-generator validate — CLI validation
Spectral (by Stoplight) — Linting and style guide enforcement

Enforce: operationId present, descriptions exist, examples present
Configure as pre-commit hook or CI step


VS Code with OpenAPI extension — shows errors inline as you type


Validation levels:

Structural — is this valid YAML? Is this valid OpenAPI?
Semantic — does the $ref point to something that exists?
Style — do all operations have descriptions? Are all fields camelCase?



SCRIPT:
"Spec validation is part of your quality workflow. Swagger Editor handles structural validation live — you'll use it in the practice tasks. The VS Code OpenAPI extension does the same inline as you type in your editor.
Spectral is worth knowing for team environments. It's a linter for OpenAPI specs — you define rules that your spec must follow. Things like: every operation must have a description, every schema property must have an example, all error responses must use the standard ErrorResponse schema. Run it as a pre-commit hook or in CI and it will fail the build if the spec doesn't meet your quality standards. This is the API equivalent of ESLint for JavaScript or Checkstyle for Java."

SLIDE 46 — Recap — What We Covered
Content:

✅ OpenAPI vs Swagger — the spec vs the tooling
✅ Design-first vs code-first workflows
✅ Top-level spec structure — info, servers, paths, components, security
✅ Operations — all fields, tags, operationId
✅ Parameters — query, path, header with types and constraints
✅ Request bodies — content types, schema, examples
✅ Responses — all status codes, headers, error responses
✅ Components — schemas, responses, parameters, securitySchemes
✅ $ref — DRY principle applied to spec files
✅ Security schemes — bearer, API key, OAuth2
✅ Swagger UI — integration and Try it Out
✅ Spring Boot integration — springdoc-openapi, @Operation, @ApiResponse, @Parameter, @Schema
✅ Schema composition — allOf, oneOf, anyOf
✅ The spec as a contract — tooling ecosystem

SCRIPT:
"We've covered the full OpenAPI specification from top to bottom, and how it connects to your Spring Boot controllers. You now have everything you need to write a professional-quality API spec from scratch and wire it into a running application.
The most valuable thing to do next is to write one. Take a simple API — a Tasks API, a Books API, a Products API — and write the complete spec. Define at least three endpoints. Use components and $ref. Document your error responses. Add security. Paste it into Swagger Editor and look at the UI you get."

SLIDE 47 — Practice Tasks
Content:

Task 1 — Read a Real Spec

Open editor.swagger.io in your browser
The default Petstore spec is pre-loaded — explore it
Find in the spec: a path parameter, a query parameter, a request body, a 404 response
Explore the rendered Swagger UI on the right side
Try it out: look at the $ref usage and the components section


Task 2 — Write a Spec from Scratch

In Swagger Editor, clear the default spec and write an OpenAPI 3.0.3 spec for a "Books API":

GET /books — list books, with query params: genre, page, limit
GET /books/{id} — get a book by ID
POST /books — create a book (separate request and response schemas)
DELETE /books/{id} — delete a book


Document: 200/201/204 success responses AND 400/401/404/422 errors
Define all schemas in components
Add bearer auth globally, with DELETE requiring auth and GET endpoints public


Task 3 — Import Into Postman

In Swagger Editor, File → Save as YAML
In Postman, click Import → drag in your YAML file
Explore the auto-generated collection — all endpoints pre-populated



SCRIPT:
"Three tasks. Task 1 is exploration — Swagger Editor has the Petstore spec pre-loaded when you open it. You can read the spec on the left and see the rendered UI on the right without writing a single line. Find all the patterns we covered today in a real spec.
Task 2 is creation — write a real spec. This is where the learning happens. Use the Swagger Editor validator to catch mistakes as you go.
Task 3 completes the loop — import your spec into Postman and see how the collection is generated automatically.
If you get through Task 2, you know OpenAPI. Everything else is practice."
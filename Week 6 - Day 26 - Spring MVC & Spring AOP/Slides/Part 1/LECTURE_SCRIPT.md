# Day 26 Part 1 — Spring MVC: REST APIs, Validation, DTOs & Error Handling
## Lecture Script

**Total Time:** 60 minutes
**Delivery pace:** ~165 words/minute — conversational, instructor-led

---

## [00:00–02:00] Opening — Spring MVC Is the Bridge

Welcome to Day 26. We're now deep in the Spring ecosystem. You've learned Spring Boot's auto-configuration, you know how beans work, you understand dependency injection. Today we add the piece that most enterprise Java developers spend the majority of their day working with — **Spring MVC and Aspect-Oriented Programming**.

Part 1 is Spring MVC. Part 2 is AOP. These two topics are deeply connected. By the end of today, when someone asks you how Spring's transaction management or caching works under the hood, you'll know exactly how to answer that.

Let me start with a question. You've used REST APIs from the client side — fetch in JavaScript, HTTP requests in Postman. You know a request goes out and a response comes back. But what actually happens on the server side? Between the moment a request hits your running application and the moment JSON appears in the response body — what is Spring doing?

That's what we're building a precise mental model of right now.

---

## [02:00–10:00] Slides 2–3 — Architecture and Your First Controller

The entry point for every HTTP request in a Spring Boot web application is the **DispatcherServlet**. One servlet. All requests. This is the Front Controller pattern.

When a request for `GET /api/books/42` arrives:

The DispatcherServlet receives it. It asks the HandlerMapping: "Which method handles this URL with this HTTP method?" HandlerMapping scans your `@Controller` and `@RestController` classes and finds the match — the `getById` method annotated with `@GetMapping("/{id}")`.

Then the HandlerAdapter invokes that method. It resolves the `@PathVariable` from the URL, injects any `@RequestParam` values from the query string, deserializes `@RequestBody` JSON if there is one. Your method runs. It returns an object.

Then what? Since you're using `@RestController`, Spring applies a `MessageConverter` — by default, Jackson — to serialize your Java object to JSON. The serialized JSON goes into the response body. Spring sets the `Content-Type: application/json` header. The response is sent.

**Spring Boot handles all of this automatically.** You don't configure a DispatcherServlet. You don't wire up Jackson. Boot's auto-configuration detects `spring-boot-starter-web` on your classpath and sets the entire pipeline up for you.

Now let me show you the first important distinction. Two annotations: `@Controller` and `@RestController`.

`@Controller` was designed for traditional server-side rendering. Your method returns a string — a template name — and Spring resolves it to a Thymeleaf or FreeMarker template and returns HTML.

`@RestController` is what you use for REST APIs. It's `@Controller` plus `@ResponseBody` on every method. `@ResponseBody` tells Spring to serialize the return value directly to the response body as JSON, rather than treating it as a view name.

Here's the controller we'll build on throughout Part 1. This is the complete `BookController`. Five endpoints, all five HTTP methods, all on one class.

`@RestController` at the top — that marks this class as a component that handles HTTP requests and returns data.

`@RequestMapping("/api/books")` — this is the base path. Every method in this class has this prefix. So `@GetMapping` becomes `GET /api/books`. `@GetMapping("/{id}")` becomes `GET /api/books/42`. You don't repeat the base path on every method.

`private final BookService bookService` — constructor injection. The service layer is where business logic lives. The controller's only job is to handle HTTP and delegate.

Notice each method maps to exactly one HTTP verb: `@GetMapping` for reads, `@PostMapping` for creates, `@PutMapping` for full updates, `@DeleteMapping` for deletes. This is clean RESTful design.

---

## [10:00–18:00] Slides 4–5 — Request Params, Path Variables, Request Body

Let's dig into how you get data out of a request.

**Path variables** — you've seen `@GetMapping("/{id}")`. The `{id}` in curly braces is a template variable in the URL. `@PathVariable Long id` extracts it and converts it to a `Long`. If the URL is `/api/books/42`, `id` is 42.

You can have multiple path variables. If you have `@GetMapping("/api/authors/{authorSlug}/books/{bookSlug}")`, you declare two `@PathVariable` parameters — one for each.

If your Java variable name doesn't match the URL template name, use `@PathVariable("bookId") Long id` to be explicit.

When do you use path variables? When you're identifying a specific resource. The book with ID 42. The author with slug "hemingway". The resource identity belongs in the path.

**Query parameters** — the `?` part of the URL. `@RequestParam(required = false) String category` picks up `/api/books?category=fiction`. If the parameter isn't in the URL and `required = false`, the value is null. Use `defaultValue = "0"` to provide a fallback instead of null.

Query parameters are for filtering, sorting, and pagination. `?category=fiction&page=2&size=20&sort=title`. They're optional modifiers on a collection endpoint, not identifiers.

You can also bind a list: `@RequestParam List<Long> ids` picks up `/api/books?ids=1&ids=2&ids=3` and gives you a list of three longs.

Now — **@RequestBody**. This is how POST and PUT requests send data. The HTTP request has a body. That body is JSON. `@RequestBody CreateBookRequest request` tells Jackson to deserialize the JSON body into a `CreateBookRequest` object.

Here's what that looks like in practice. The client sends:
```
POST /api/books
Content-Type: application/json
{ "title": "Clean Code", "authorId": 5, "price": 39.99 }
```

Jackson maps the JSON field `"title"` to the Java field `title`, `"authorId"` to `authorId`, `"price"` to `price`. You have a populated `CreateBookRequest` object in your method.

Notice I said `@Valid @RequestBody` together. That `@Valid` is critical — it tells Spring to run Bean Validation on the object after Jackson deserializes it. Without `@Valid`, your validation annotations do nothing.

One more annotation to mention before we move on — `@ResponseStatus(HttpStatus.CREATED)` on a method. This sets the default response status to 201 Created without needing `ResponseEntity`. It's a shortcut, but `ResponseEntity` gives you more control. Let's look at that next.

---

## [18:00–25:00] Slide 6 — ResponseEntity

When you return a plain object from a controller method, Spring gives you a 200 OK. That's fine for reads. But REST has semantics around status codes, and you should use them correctly.

Creating a resource should return 201 Created. Deleting a resource should return 204 No Content — success, but no body to return. Some operations return 202 Accepted — "we got your request, we're processing it asynchronously."

`ResponseEntity` is a wrapper that lets you control exactly what the response looks like — status code, headers, and body. Let me walk through the most common patterns.

`ResponseEntity.ok(body)` — 200 OK with a body. Same as returning the object directly, but explicit.

`ResponseEntity.created(uri).body(dto)` — this is the pattern for POST endpoints. You create the resource, get back the created object with its generated ID, build a URI for the new resource's location, and return 201 with the `Location` header set. Clients who receive a 201 can follow the Location header to retrieve the newly created resource.

`ResponseEntity.noContent().build()` — 204 No Content. Use this for DELETE endpoints. The operation succeeded, there's nothing to return. You don't need a body.

`ResponseEntity.status(HttpStatus.ACCEPTED).body(result)` — any custom status with a body.

`ResponseEntity.ok().header("X-Total-Count", String.valueOf(count)).body(books)` — custom headers. API conventions often return pagination metadata in headers.

Know your status codes. I put the full table on this slide. Commit the common ones to memory: 200 for success with body, 201 for created, 204 for success without body, 400 for client validation error, 401 for not authenticated, 403 for forbidden, 404 for not found, 409 for conflict, 500 for server error. When you see these in API responses you'll immediately know what happened.

---

## [25:00–30:00] Slide 7 — Three-Layer Architecture

Before we go deeper on validation and exception handling, I want to cement the layered architecture pattern. This is how every enterprise Spring Boot application is structured.

Three layers. Controller, Service, Repository.

The **Controller layer** handles the HTTP boundary. It receives requests, triggers validation, calls the service, and returns responses. It does not contain business logic. If you see an `if` statement in a controller that's deciding whether something is allowed to happen — that business rule belongs in the service.

The **Service layer** is where business logic lives. All of it. This is where you decide "can this user perform this action?", "does this entity already exist?", "what rules apply to this transaction?" Services call repositories to read and write data. Services call other services when needed. Services throw business exceptions. Services never know about HTTP — no `HttpServletRequest`, no `ResponseEntity`.

The **Repository layer** talks to the database. Nothing else. No business logic. No HTTP concerns. On Day 27, you'll implement Spring Data JPA repositories in depth.

Why does this matter? Testing. When you test your service layer, you mock the repository and don't need a database. When you test exception handling, you mock the service and don't need real data. Each layer is independently testable.

It also matters for reusability. The same service can be called by a REST controller, a GraphQL resolver, a message queue consumer, a scheduled task. They all share the same business logic because business logic doesn't live in the transport layer.

---

## [30:00–38:00] Slides 8–9 — Bean Validation and Custom Validators

Let's talk about validation. Your controller receives a request. The JSON has been deserialized into a `CreateBookRequest` object. Before that object reaches your service, you need to ensure the data is valid. Empty titles, negative prices, malformed ISBNs — none of that should reach your business logic.

Bean Validation is the standard approach. Add `spring-boot-starter-validation` to your dependencies. Annotate your request DTO fields with constraint annotations. Add `@Valid` to the `@RequestBody` parameter in the controller. Done. Spring validates before your method runs.

The annotations are intuitive. `@NotBlank` — not null and not all whitespace. Use this for String fields. `@NotNull` — not null, but can be empty. Use this for non-String fields. `@Size(min = 1, max = 200)` — String length or collection size. `@Min` and `@Max` for integer ranges. `@Email` for email format. `@Pattern(regexp = "...")` for regex. `@Positive` for numbers greater than zero. `@Past` and `@Future` for dates. `@DecimalMin` and `@DecimalMax` for BigDecimal ranges.

You can combine multiple annotations on one field. Title is `@NotBlank` AND `@Size(min = 1, max = 200)`.

If validation fails, Spring throws `MethodArgumentNotValidException`. Your `@ControllerAdvice` catches it, extracts the field errors from the `BindingResult`, and returns a 400 with a list of what was wrong. We'll write that handler in a moment.

But standard annotations have limits. How do you validate that an ISBN-13 checksum is correct? You write a custom validator.

Two pieces. First, an annotation: `@ValidIsbn`. Annotate it with `@Constraint(validatedBy = IsbnValidator.class)` and include the three required attributes: `message`, `groups`, and `payload`.

Second, a class implementing `ConstraintValidator<ValidIsbn, String>`. Implement `isValid(String value, ConstraintValidatorContext ctx)` and return true or false.

The `isValid` method: if null, return true. Let `@NotBlank` handle null. Your validator handles format only. Strip dashes and spaces. Check length. Run the ISBN-13 checksum algorithm. Return the result.

Spring auto-detects these validators when they're on the classpath. No registration required. Just annotate your field with `@ValidIsbn` and it works.

---

## [38:00–47:00] Slides 10–11 — Global Exception Handling and ProblemDetail

Without global exception handling, a `BookNotFoundException` thrown in your service propagates up through the controller and Spring returns a 500 Internal Server Error. That's wrong — 500 means something unexpected happened on the server. A missing book is a 404 — an expected, normal client error. And regardless of status code, the raw Spring error response often contains stack traces that expose your implementation details.

`@RestControllerAdvice` solves this. One class, annotated with `@RestControllerAdvice`, and Spring routes exceptions from any controller to the appropriate handler method.

`@ExceptionHandler(BookNotFoundException.class)` — this method handles `BookNotFoundException` specifically. Spring calls this method instead of returning a 500. You control the status code and the response body.

`@ExceptionHandler(MethodArgumentNotValidException.class)` — this catches validation failures. The exception's `getBindingResult().getFieldErrors()` gives you a list of `FieldError` objects. Each one has a field name and a default message — the message you put on the annotation, or a default. Map those to a list of error strings and return them in your error response.

`@ExceptionHandler(Exception.class)` — the catch-all. Any exception not handled by a more specific handler comes here. Log the full stack trace server-side so you can investigate. Return a generic 500 response to the client with no internal details. Never expose stack traces to API clients.

Now let me show you the modern approach with Spring Boot 3: **ProblemDetail**, which implements RFC 9457. This is the standardized HTTP Problem Details format. Instead of each team inventing their own error JSON structure, there's now a standard: `type`, `title`, `status`, `detail`, `instance`.

`ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, "Book not found with id: 99")` creates the base problem. Then `setTitle()`, `setType()`, `setInstance()`, and `setProperty()` for custom extension fields.

Enable it with `spring.mvc.problemdetails.enabled=true` in `application.properties`. With this enabled, Spring also automatically converts its own exceptions (like `MethodArgumentNotValidException`) to `ProblemDetail` format. One configuration line, standardized error responses across the board.

---

## [47:00–54:00] Slides 12–13 — DTOs and MapStruct

I want to talk about something you should never do: return JPA entities directly from your controllers. I've seen this in real codebases. It's a time bomb.

Your entity has all the raw database fields — internal notes, soft-delete flags, audit timestamps, foreign key IDs that only make sense internally. You return the entity from a GET endpoint and all of that is in the JSON response. Clients start depending on those fields. Now you can't refactor your database schema without breaking the API contract.

DTOs — Data Transfer Objects — are the separation layer. Your entity is your database model. Your DTO is your API model. They can differ. A `BookDto` might have `authorName` — a string combining first and last name — even though the entity has a `@ManyToOne Author author` relationship.

Define separate DTOs for responses, for create requests, and for update requests. They have different fields, different validations.

Now the tedious part: mapping. You need to convert `Book` entity to `BookDto`. Manually writing setters for every field is error-prone and breaks silently when you add new fields.

**MapStruct** solves this with generated code. Define an interface with `@Mapper(componentModel = "spring")`. Declare the method signatures. MapStruct's annotation processor generates the implementation at compile time.

`BookDto toDto(Book book)` — MapStruct generates a class that calls getters on `Book` and setters on `BookDto`. If field names match, it maps automatically. For fields that don't match — like mapping `author.firstName` to `authorFirstName` — add `@Mapping(source = "author.firstName", target = "authorFirstName")`.

The generated code is plain Java — no reflection, compile-time type checking, immediately debuggable. If you add a new field to `BookDto` and forget to handle it in the mapper, MapStruct can warn you at compile time.

Inject it like any Spring bean: `private final BookMapper bookMapper`. Call `bookMapper.toDto(book)` in your service.

**ModelMapper** exists as an alternative. It uses reflection to map by name convention at runtime. Less setup — you just create a `ModelMapper` bean and call `modelMapper.map(book, BookDto.class)`. But it's slower, there's no compile-time safety, mapping errors show up at runtime, and debugging is harder. For production applications, MapStruct is preferred.

---

## [54:00–58:00] Slide 14 — CORS

CORS — Cross-Origin Resource Sharing. Browser security rule: JavaScript running at `http://localhost:3000` cannot call `http://localhost:8080/api/books` unless the server explicitly allows it. This is the Same-Origin Policy. Origin means scheme + domain + port. All three must match for a request to be same-origin.

When they don't match, the browser sends a preflight `OPTIONS` request first: "I'm from this origin, I want to call this endpoint with this method — is that allowed?" Your server must respond with appropriate `Access-Control-Allow-*` headers. If it does, the browser sends the real request.

Two ways to configure CORS in Spring.

`@CrossOrigin(origins = "https://bookstore-frontend.com")` on a controller class. Fine-grained, per controller. Works fine if you have one frontend URL and one controller.

For everything else, implement `WebMvcConfigurer` and override `addCorsMappings`. This is global configuration. One place, all endpoints. Specify allowed origins (your frontend URL plus `http://localhost:3000` for development), allowed methods, allowed headers, whether to allow credentials, and how long to cache the preflight response.

Critical warning: never use `allowedOrigins("*")` combined with `allowCredentials(true)`. The wildcard `*` means any origin. Credentials means cookies or `Authorization` headers. Combined, you're saying "any website can make authenticated requests on behalf of your users." Spring will throw an exception if you try this — it's caught at configuration time.

---

## [58:00–60:00] Slides 15–16 — WebSockets and Wrap-Up

Quick note on WebSockets before we break.

HTTP is request-response. Client asks, server answers, connection closes. What if the server needs to push data to the client? Stock prices, chat messages, live notifications? HTTP polling — asking every few seconds — works but is inefficient.

WebSockets maintain a persistent bidirectional connection. The server can push data at any time without the client asking. Spring has `@EnableWebSocket` and handler abstractions for raw WebSocket connections, and STOMP over WebSocket for a higher-level pub/sub messaging model. We won't implement a full WebSocket system today — that's in later weeks — but understand it exists and what problem it solves.

Take a 10-minute break. When we come back, we tackle AOP — Aspect-Oriented Programming. You'll see how Spring uses AOP for transaction management, caching, security, and logging. You'll write aspects yourself. And you'll understand why certain Spring features seem like "magic" — they're proxies generated by the AOP framework.

See you in 10.

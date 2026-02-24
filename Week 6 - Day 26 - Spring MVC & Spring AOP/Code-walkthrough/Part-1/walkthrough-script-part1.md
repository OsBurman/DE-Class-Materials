# Walkthrough Script — Part 1: Spring MVC

**Delivery Time:** ~90 minutes  
**Format:** Live demo — walk through files 01 through 06 in order  
**Project:** Bookstore REST API running on Spring Boot 3.x

---

## Segment 1 — What Is Spring MVC? (10 minutes)

> "Good morning everyone. Today we're building REST APIs with Spring MVC — and I want to start with a question.

When a browser or a mobile app sends `GET /api/books/42` to your server, what actually happens? Who receives that request? How does it end up in your Java code?

That's what Spring MVC answers.

Open `01-spring-mvc-architecture.md`. Let's look at the architecture before we write any code."

---

### 1a — The MVC Pattern

> "MVC stands for Model-View-Controller. You've heard this term a lot. In Spring's context for REST APIs:

- **Model** — your data and business objects. Book, Order, User.
- **View** — for us, this is always JSON. Jackson renders it.
- **Controller** — the Java code that receives the HTTP request and returns a response.

For server-side rendering apps like Thymeleaf, the View is an HTML template. We're building REST APIs, so the View is JSON."

---

### 1b — DispatcherServlet (the most important concept of the day)

> "Every HTTP request that enters a Spring Boot application goes through one Java Servlet called the **DispatcherServlet**. This is the *Front Controller* pattern.

Look at the diagram in the file:"

*[Show the DispatcherServlet flow diagram on screen]*

> "Walk through the five steps with me:

1. Request arrives → DispatcherServlet receives it
2. DispatcherServlet asks `HandlerMapping`: which controller method matches this URL and HTTP verb?
3. `HandlerMapping` scans all your `@Controller` beans and finds the match
4. `HandlerAdapter` calls the method, resolving parameters — `@PathVariable`, `@RequestBody`, etc.
5. The return value hits `HttpMessageConverter` — specifically Jackson — which serializes your Java object to JSON

**Watch out:** Students often think their controller is the entry point. It's not. DispatcherServlet is. Your controller is just a delegate that DispatcherServlet calls.

Now look at the Key Components table. The one to know cold is `HttpMessageConverter`. That's what makes Spring MVC 'just return an object and it becomes JSON' — Jackson does that translation."

---

### 1c — Three-Layer Architecture

> "The architecture diagram at the bottom of the file shows three layers. Let me draw this on the board.

```
Controller  → receives HTTP, maps requests, calls service
Service     → business logic, rules, transactions
Repository  → database access, queries, saves
```

These layers are not just a convention — they're a discipline. The rules are strict:
- Controllers call services. Controllers do NOT call repositories.
- Services contain ALL business logic. Services do NOT know about HTTP.
- Repositories do ONE thing: talk to the database.

If you find yourself writing `if (price < 0) throw` in a controller — that's wrong. That belongs in the service. If you find yourself injecting a repository into a controller — that's wrong too.

Questions before we start coding?"

---

## Segment 2 — Controllers and Request Mapping (20 minutes)

> "Let's open `02-bookstore-controller.java`. This is the controller for our Bookstore API."

---

### 2a — @RestController vs @Controller

> "First thing at the top — the class annotation. Look at the comment block."

*[Show @RestController vs @Controller comment]*

> "@Controller marks a class as a Spring MVC controller. Return values from its methods are resolved as **view names** — like 'books/list' which would look for a Thymeleaf template.

@RestController is a shorthand for `@Controller + @ResponseBody on every method`. The `@ResponseBody` tells Spring: *don't look for a view — serialize the return value to JSON and write it directly to the HTTP response body.*

For REST APIs, you always use `@RestController`.

**Ask the class:** If you used `@Controller` without `@ResponseBody`, and your method returned a `BookDTO`, what would Spring do? *(It would look for a view template named 'BookDTO' — which doesn't exist — and throw an error.)*"

---

### 2b — Class-Level @RequestMapping

> "Below the class annotation: `@RequestMapping(\"/api/v1/books\")`. This sets the base path for all endpoints in this controller. Every mapping inside is **relative to** this prefix.

`@GetMapping` → `GET /api/v1/books`  
`@GetMapping(\"/{id}\")` → `GET /api/v1/books/{id}`  

The `/v1` in the path is API versioning. You'll hear about this in more advanced contexts — putting the version in the URL is the most common approach."

---

### 2c — @GetMapping and @RequestParam

> "Look at `getAllBooks()`. This handles `GET /api/v1/books`.

The parameters use `@RequestParam`. Show me the method signature:"

```java
public ResponseEntity<List<BookDTO>> getAllBooks(
    @RequestParam(required = false) String genre,
    @RequestParam(defaultValue = "0")  int page,
    @RequestParam(defaultValue = "10") int size)
```

> "These map to query string parameters: `GET /api/v1/books?genre=fiction&page=1&size=20`

`required = false` means Spring won't throw an error if this param isn't in the URL. `defaultValue` provides a fallback. Notice `page` and `size` are `int` — Spring automatically converts the string '20' from the URL to the integer 20.

**Watch out:** If you omit `required = false` and `defaultValue`, the parameter is required by default. Missing it causes a 400 Bad Request. That's often not what you want for optional filters."

---

### 2d — @PathVariable and @GetMapping("/{id}")

> "Now `getBookById()`. The path template has `{id}` — a placeholder. Spring extracts the value from the URL and injects it into the parameter annotated with `@PathVariable`.

`GET /api/v1/books/42` → Spring extracts '42', converts to `Long`, passes it as `id`.

Notice the type: `Long`, not `String`. Spring's `TypeConversionService` handles the conversion automatically. If the path variable isn't a valid number — like `GET /api/v1/books/abc` — Spring returns a 400 Bad Request automatically."

---

### 2e — @PostMapping, @RequestBody, @Valid, and ResponseEntity 201

> "Now look at `createBook()`. This is where things get interesting.

`@RequestBody` tells Spring: take the JSON in the HTTP request body and deserialize it into a `CreateBookRequest` object. Jackson does this reverse mapping.

`@Valid` tells Spring: run Bean Validation on this object before calling my method. If any constraint fails — required field missing, price negative — Spring throws `MethodArgumentNotValidException` and returns 400 **before my code runs**.

We'll see what those constraints look like in a moment.

Now look at the return value — `ResponseEntity<BookDTO>`. We're not just returning the object this time. We're controlling the response ourselves. Show me the return statement:"

```java
return ResponseEntity
    .created(location)
    .body(created);
```

> "`.created(location)` sets the HTTP status to **201 Created** and adds a `Location` header pointing to the URL of the newly created resource. The client now knows where to find this book.

This is the correct REST pattern for a POST that creates a resource. Just returning 200 OK with a body is technically wrong — 201 is more precise."

**Pause — ask:** *"What's the difference between 200 OK and 201 Created for a POST endpoint?"*  
*(200 means the request was processed. 201 specifically means a new resource was created.)*

---

### 2f — @PutMapping vs @PatchMapping

> "@PutMapping is for **full replacement** — the client sends the complete resource state. @PatchMapping is for **partial update** — only the changed fields.

Look at `updateBookPrice()` using `@PatchMapping(\"/{id}/price\")` — it only updates the price field, not the whole book. This is the PATCH semantics."

---

### 2g — @DeleteMapping and 204 No Content

> "`deleteBook()` returns `ResponseEntity<Void>` with `noContent().build()` — 204 No Content. Successful deletion returns no body. There's nothing to say after deleting something."

---

### 2h — HTTP Status Code Reference

> "Look at the comment block at the bottom of the file — the status code reference. Let me highlight the ones you'll use every day:

- **200 OK** — general success
- **201 Created** — resource created, use with POST
- **204 No Content** — success, no body (DELETE)
- **400 Bad Request** — invalid input
- **401 Unauthorized** — not authenticated
- **403 Forbidden** — authenticated but not allowed
- **404 Not Found** — resource doesn't exist
- **409 Conflict** — duplicate or state conflict
- **500 Internal Server Error** — unexpected server bug

**Watch out:** `401 Unauthorized` is a confusing name. It actually means *unauthenticated* — the user hasn't logged in. `403 Forbidden` means the user IS logged in but doesn't have permission."

---

## Segment 3 — Service and Repository Layers (15 minutes)

> "Now let's open `03-service-and-repository-layers.java`. This file shows the bottom two layers of our architecture."

---

### 3a — The Entity

> "First, the `Book` entity. This is the PERSISTENCE model — it mirrors the DB table. JPA annotations map fields to columns.

Notice what's NOT here: no validation annotations, no HTTP-specific code. Entities are purely about persistence.

I want you to notice something important: this entity has a `BigDecimal price` — not `double`. In Java, never use `double` for money. Floating point arithmetic introduces rounding errors. `BigDecimal` is precise. We'll convert to `double` in the DTO only for JSON output convenience."

---

### 3b — @Repository and JpaRepository

> "The `BookRepository` interface extends `JpaRepository<Book, Long>`. That's all you need for full CRUD. The two generic parameters are the entity type and the ID type.

Look at the derived query methods below. Spring Data reads the method name and generates JPQL:

```java
List<Book> findByGenre(String genre);
```

This generates: `SELECT b FROM Book b WHERE b.genre = ?1`

No query needed. Just the right method name. We'll explore this deeply on Day 27."

---

### 3c — The Service Interface + Implementation

> "Look at `BookService` — it's an **interface**. Then `BookServiceImpl` implements it.

Why the interface? Controller injects `BookService`, not `BookServiceImpl`. This means:
1. In tests, you can inject a mock that implements `BookService`
2. Spring can create a proxy for `@Transactional` on the interface boundary
3. You're programming to an abstraction, not a concrete class

The class is annotated `@Transactional(readOnly = true)`. This sets the default for all methods — they run in a read-only transaction (no writes allowed). Then write methods override this with `@Transactional` (without `readOnly`) to allow database mutations."

---

### 3d — Business Logic in the Service

> "Look at `createBook()`. See the first thing it does?"

```java
if (bookRepository.existsByIsbn(request.getIsbn())) {
    throw new DuplicateIsbnException(...);
}
```

> "This is a business rule: ISBNs must be unique. This check belongs in the service, not the controller and not the repository. Business logic in the service, always.

Then it maps the request DTO to an entity, saves it, maps the entity back to a response DTO, and returns it. That's the full create cycle.

Notice `bookRepository.save(book)` — this returns the persisted entity with the database-generated `id`. You must use the returned object, not the original, because the original doesn't have the ID yet."

---

## Segment 4 — Validation and Exception Handling (15 minutes)

> "Open `04-validation-and-exception-handling.java`. This is one of the most important files of the day."

---

### 4a — Bean Validation Annotations

> "Look at `CreateBookRequest`. Every field has annotations from the Jakarta Validation API.

Let me walk through each one:"

```java
@NotBlank(message = "Title is required")
@Size(min = 1, max = 255, ...)
private String title;
```

> "- `@NotBlank` — not null, not empty string, not '   ' (just spaces)
- `@Size` — character length between min and max
- `@NotNull` — field must be present (but can be empty string — different from @NotBlank)
- `@Positive` — must be > 0
- `@DecimalMax` — maximum decimal value as a string
- `@Past` — date must be before today
- `@Pattern` — regex match with `flags = CASE_INSENSITIVE`

The `message` attribute customizes what goes in the error response. If you omit it, you get a default message that's often confusing.

**Watch out:** `@NotNull` vs `@NotBlank` — students often confuse these. `@NotNull` allows empty strings `\"\"`. `@NotBlank` rejects empty strings AND strings with only whitespace."

---

### 4b — Custom Validator

> "Now look at `@ValidIsbn`. The built-in annotations don't validate ISBN format, so we build our own.

Two pieces:
1. The `@ValidIsbn` annotation — notice `@Constraint(validatedBy = IsbnValidator.class)` links the annotation to its implementation
2. `IsbnValidator` implements `ConstraintValidator<ValidIsbn, String>` — the second generic is the type it validates (String)

In `isValid()`: strip hyphens, check against two regex patterns — ISBN-13 (13 digits) and ISBN-10 (9 digits + optional X).

You can use this annotation anywhere you use `@NotBlank` — on method parameters, fields, return values."

---

### 4c — @ControllerAdvice and @ExceptionHandler

> "This is the most powerful pattern in the file. Look at `GlobalExceptionHandler`.

`@RestControllerAdvice` = `@ControllerAdvice` + `@ResponseBody`. It intercepts exceptions thrown from ANY controller in your application.

Instead of every controller having its own try/catch, you have ONE class that handles everything centrally.

Look at `handleBookNotFound()`:
- `@ExceptionHandler(BookNotFoundException.class)` — this method runs when a `BookNotFoundException` is thrown anywhere
- `@ResponseStatus(HttpStatus.NOT_FOUND)` — sets the HTTP status to 404
- Returns an `ErrorResponse` object which Jackson serializes to JSON

Now look at `handleValidationErrors()`. When `@Valid` fails, Spring throws `MethodArgumentNotValidException`. We extract each field error and build a map:"

```java
{
  "status": 400,
  "validationErrors": {
    "title": "Title is required",
    "price": "Price must be greater than zero"
  }
}
```

> "The client gets a structured error message telling them exactly which fields to fix.

**Watch out:** The catch-all `handleAllOtherExceptions()` is critical. Without it, unexpected exceptions return a default Spring error page (or worse, a raw stack trace). The catch-all should NEVER include `ex.getMessage()` in the response body in production — that leaks internal implementation details."

---

## Segment 5 — DTOs and MapStruct (10 minutes)

> "Open `05-dtos-and-mapping.java`. Let me address the question I hear most often: why not just return the entity from the controller?"

*[Read through Section 1 comment — DTOs vs Entities]*

> "The key reasons are security and decoupling. If your `User` entity has a `passwordHash` field and you return the entity — that hash just got sent to the client. With a `UserDTO` that omits it, problem solved.

The second reason is decoupling. If you change your database schema — rename a column, split a table — and you're returning entities, your API breaks. With DTOs, you control the API surface separately from the DB schema."

---

### 5a — Manual Mapping

> "Look at `ManualBookMapper.toDTO()`. Explicit, clear, simple. The downside is boilerplate — 10 fields means 10 lines. And if you add a field to the entity and forget to add it to this method, the DTO will silently have `null` for that field."

---

### 5b — MapStruct

> "Look at `BookMapper` — the interface. The `@Mapper(componentModel = \"spring\")` annotation tells MapStruct to generate a Spring-managed implementation at compile time.

When you run `mvn compile`, MapStruct reads this interface and generates `BookMapperImpl.java` in `target/generated-sources`. That generated class contains all the mapping code. At runtime, Spring injects it as a regular bean.

Key examples:
- Basic mapping with matching field names — zero config, just declare the method
- `@Mapping(target = \"displayName\", ignore = true)` + `@AfterMapping` — for computed fields that don't exist in the entity
- `booksToBookDTOs(List<Book>)` — MapStruct generates the list loop automatically

Look at the pom.xml comment block — MapStruct requires an annotation processor in the Maven compiler plugin. If you use Lombok too, Lombok must come BEFORE MapStruct in the processor list, or Lombok's generated getters/setters won't be available when MapStruct generates its mappings."

---

## Segment 6 — CORS and WebSocket Overview (10 minutes)

> "Open `06-cors-and-websocket-overview.md`. Two final topics."

---

### 6a — CORS

> "CORS is something every student hits when connecting a frontend to a backend for the first time. Your React app is on port 3000, your Spring Boot API is on port 8080. Browser blocks the request. You get a console error. This is CORS.

The fix: tell Spring to allow that origin. Two options:

`@CrossOrigin` on the controller — quick, fine for development, but you'd repeat it on every controller.

Global `WebMvcConfigurer` — configure once, applies everywhere. This is what you do in real projects.

Look at the `WebConfig.addCorsMappings()` example. `.addMapping(\"/api/**\")` means: apply this CORS config to all routes starting with `/api/`. `.allowedOrigins(...)` is your list of approved frontends.

**Watch out:** Never put `allowedOrigins(\"*\")` in production. That allows ANY website to call your API from a browser. Either list specific origins or use an allowlist from environment variables."

---

### 6b — WebSocket Overview

> "WebSockets are for real-time bidirectional communication. HTTP is one-way, request-response. WebSocket opens a persistent connection and either side can send at any time.

Look at the use cases in the file: live chat, notifications, collaborative editing. These are scenarios where polling (asking 'anything new?' every second) is too slow or too wasteful.

The setup: add `spring-boot-starter-websocket`, create a `@Configuration` with `@EnableWebSocketMessageBroker`, configure endpoints and the message broker.

STOMP is the messaging protocol on top of WebSocket — it gives you pub/sub semantics and Spring integration via `@MessageMapping` and `@SendTo`.

We're not building a WebSocket endpoint today — the overview is here so you understand when you'd reach for it. The main decision point: if you need server-push only, Server-Sent Events (`SseEmitter`) are simpler. If you need bidirectional real-time (chat, games), use WebSocket + STOMP."

---

## Segment 7 — Part 1 Wrap-Up (5 minutes)

> "Let me pull all of Part 1 together. Here's the mental model I want you to leave with:

```
HTTP Request
    ↓
DispatcherServlet (routes everything)
    ↓
@RestController (receives, validates, delegates)
    ↓
@Service (business logic, transactions)
    ↓
@Repository (database)
    ↑
Entity → DTO mapping → JSON response
```

Every controller method:
1. Receives an HTTP request (URL + verb)
2. Extracts inputs (@PathVariable, @RequestParam, @RequestBody)
3. Validates inputs (@Valid)
4. Calls the service
5. Returns ResponseEntity with the right status code

Exceptions flow up to @ControllerAdvice — centralized, clean, consistent.

DTOs protect your API from your database schema. MapStruct generates the boilerplate."

---

## Q&A Prompts

1. **"You have a `GET /api/v1/books` endpoint and a `GET /api/v1/books/{id}` endpoint. A client calls `GET /api/v1/books/search`. Which method does Spring route this to?"**  
   *(Answer: Neither — unless you have `@GetMapping("/search")`. Spring would return 404 because "search" doesn't match `{id}` mappings when a more specific `/search` exists.)*

2. **"What happens if you put business logic — like checking if a book's ISBN is unique — in the controller instead of the service?"**  
   *(Answer: Works technically, but violates separation of concerns. Can't be tested without HTTP. Can't be reused by other callers like a batch job. Controller becomes bloated.)*

3. **"You send `POST /api/v1/books` with a body that has `price: -5`. Your `@Positive` annotation is in place. What HTTP status code does the client get, and which layer of your code runs?"**  
   *(Answer: 400 Bad Request. The GlobalExceptionHandler's handleValidationErrors() runs. The controller method body never executes.)*

4. **"What's the difference between `@NotNull` and `@NotBlank`?"**  
   *(Answer: @NotNull allows empty strings and whitespace. @NotBlank rejects null, empty string, and whitespace-only strings.)*

5. **"Your React app is running at `http://localhost:3000` and Spring Boot at `http://localhost:8080`. You call a `GET` endpoint and it works. Then you add Spring Security. The `GET` stops working. Why?"**  
   *(Answer: Spring Security runs before CORS handling in the filter chain. You must configure CORS inside Spring Security's SecurityFilterChain, not just in WebMvcConfigurer.)*

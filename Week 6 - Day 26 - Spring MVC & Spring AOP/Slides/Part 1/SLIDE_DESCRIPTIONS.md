# Day 26 Part 1 — Spring MVC: REST APIs, Validation, DTOs & Error Handling
## Slide Descriptions

---

### Slide 1 — Title Slide
**Title:** Spring MVC — Building REST APIs
**Subtitle:** Part 1: Architecture, Controllers, Validation, Exception Handling & DTOs

**Learning objectives listed on slide:**
- Explain Spring MVC's architecture and request processing flow
- Build REST API endpoints with `@RestController`
- Extract data from requests using `@RequestParam`, `@PathVariable`, `@RequestBody`
- Return controlled responses using `ResponseEntity`
- Validate request data with Bean Validation and custom validators
- Handle exceptions globally with `@ControllerAdvice`
- Design DTOs and map between DTOs and entities with MapStruct

---

### Slide 2 — Spring MVC Architecture
**Header:** Spring MVC — How a Request Becomes a Response

**The DispatcherServlet is the Front Controller.** Every HTTP request to your application passes through it. One servlet, all requests, zero routing logic in the servlet itself — the `DispatcherServlet` delegates to handler mappings, handler adapters, and view resolvers.

**Full request lifecycle diagram:**
```
HTTP Request
     │
     ▼
DispatcherServlet          ← single entry point, configured by Spring Boot
     │
     ▼ asks:
HandlerMapping             ← "which @Controller method matches this URL + method?"
     │ returns: handler method reference
     ▼
HandlerAdapter             ← invokes the method, resolves parameters
     │
     ▼
@Controller method         ← YOUR CODE executes here
     │ returns: Object (for @RestController) or ModelAndView
     ▼
MessageConverter           ← converts return value → JSON/XML (Jackson by default)
     │
     ▼
HTTP Response              ← 200 OK with JSON body
```

**Spring Boot auto-configures:** `DispatcherServlet`, `HandlerMapping`, `HandlerAdapter`, Jackson `MessageConverter`, `ContentNegotiationStrategy`, `ExceptionHandlerExceptionResolver`. You write controllers. Spring wires the pipeline.

**Two controller types:**
- `@Controller` — returns view names (Thymeleaf templates). Response: HTML page.
- `@RestController` — returns data objects. Response: JSON (via Jackson). This is what you use for REST APIs.

`@RestController` = `@Controller` + `@ResponseBody` on every method.

---

### Slide 3 — @RestController Basics
**Header:** Your First REST Controller

**Complete BookController:**
```java
@RestController
@RequestMapping("/api/books")    ← base path for all methods in this class
public class BookController {

    private final BookService bookService;

    // Constructor injection (Lombok @RequiredArgsConstructor would generate this)
    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping                  // GET /api/books
    public List<BookDto> getAllBooks() {
        return bookService.getAllBooks();
    }

    @GetMapping("/{id}")         // GET /api/books/42
    public BookDto getBookById(@PathVariable Long id) {
        return bookService.findById(id);
    }

    @PostMapping                 // POST /api/books
    public BookDto createBook(@RequestBody @Valid CreateBookRequest request) {
        return bookService.create(request);
    }

    @PutMapping("/{id}")         // PUT /api/books/42
    public BookDto updateBook(@PathVariable Long id,
                              @RequestBody @Valid UpdateBookRequest request) {
        return bookService.update(id, request);
    }

    @DeleteMapping("/{id}")      // DELETE /api/books/42
    public void deleteBook(@PathVariable Long id) {
        bookService.delete(id);
    }
}
```

**Annotation mapping:**
| Annotation | HTTP Method | Typical Use |
|---|---|---|
| `@GetMapping` | GET | Retrieve resource(s) |
| `@PostMapping` | POST | Create resource |
| `@PutMapping` | PUT | Replace resource entirely |
| `@PatchMapping` | PATCH | Partially update resource |
| `@DeleteMapping` | DELETE | Remove resource |
| `@RequestMapping` | Any | Base path, or use `method=` for specific HTTP method |

---

### Slide 4 — Request Data: @PathVariable and @RequestParam
**Header:** Extracting Data From the URL

**@PathVariable — part of the URL path:**
```java
// GET /api/books/42
@GetMapping("/{id}")
public BookDto getById(@PathVariable Long id) { ... }

// GET /api/authors/hemingway/books/old-man-and-the-sea
@GetMapping("/api/authors/{authorSlug}/books/{bookSlug}")
public BookDto getByAuthorAndBook(
        @PathVariable String authorSlug,
        @PathVariable String bookSlug) { ... }

// Variable name differs from parameter name
@GetMapping("/{bookId}")
public BookDto get(@PathVariable("bookId") Long id) { ... }
```

**@RequestParam — query string parameters:**
```java
// GET /api/books?category=fiction&page=0&size=20&sort=title
@GetMapping
public Page<BookDto> searchBooks(
        @RequestParam(required = false) String category,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size,
        @RequestParam(defaultValue = "title") String sort) { ... }

// GET /api/books?ids=1&ids=2&ids=3
@GetMapping("/batch")
public List<BookDto> getByIds(@RequestParam List<Long> ids) { ... }
```

**Key options for @RequestParam:**
- `required = false` — parameter is optional; if absent, value is null
- `defaultValue = "..."` — value to use when parameter is absent
- `name = "..."` — explicit parameter name if it differs from the Java variable name

**URL design conventions:**
- Use plural nouns for collections: `/api/books`, `/api/authors`
- Path variables identify specific resources: `/api/books/{id}`
- Query params filter, sort, or paginate: `/api/books?category=fiction&sort=price`
- Avoid verbs in URLs — the HTTP method is the verb

---

### Slide 5 — @RequestBody and @ResponseBody
**Header:** Reading and Writing JSON

**@RequestBody — deserializes JSON request body into a Java object:**
```java
// POST /api/books
// Request body: { "title": "Clean Code", "authorId": 5, "price": 39.99 }
@PostMapping
@ResponseStatus(HttpStatus.CREATED)   // sets default response status to 201
public BookDto createBook(@RequestBody @Valid CreateBookRequest request) {
    return bookService.create(request);
}
```

Jackson automatically maps JSON fields to Java fields:
```java
// JSON field "title" maps to Java field "title"
// JSON field "authorId" maps to Java field "authorId"
// camelCase in Java matches camelCase in JSON
public class CreateBookRequest {
    @NotBlank(message = "Title is required")
    private String title;

    @NotNull(message = "Author is required")
    private Long authorId;

    @DecimalMin(value = "0.01", message = "Price must be positive")
    private BigDecimal price;
    // getters + setters (or use @Data from Lombok)
}
```

**@ResponseBody — serializes Java return value to JSON:**
- Applied automatically when using `@RestController`
- With `@Controller`, add `@ResponseBody` to individual methods that return data instead of view names

**Jackson field naming:** By default, Java's `camelCase` fields map to JSON `camelCase`. Configure globally with `spring.jackson.property-naming-strategy=SNAKE_CASE` to map to `snake_case`. Or use `@JsonProperty("field_name")` on individual fields.

---

### Slide 6 — ResponseEntity
**Header:** ResponseEntity — Full Control Over the HTTP Response

**Why use ResponseEntity?**
Returning a plain object from a controller method results in HTTP 200 with JSON body. That's fine for simple cases. But real APIs need to:
- Return 201 Created with a Location header when creating a resource
- Return 404 Not Found when a resource doesn't exist
- Return 204 No Content when a delete succeeds
- Include custom headers (pagination links, rate limit info)

**ResponseEntity examples:**
```java
// 200 OK with body (same as returning T directly)
return ResponseEntity.ok(bookDto);

// 201 Created with Location header
@PostMapping
public ResponseEntity<BookDto> createBook(@RequestBody @Valid CreateBookRequest request) {
    BookDto created = bookService.create(request);
    URI location = URI.create("/api/books/" + created.getId());
    return ResponseEntity.created(location).body(created);
    // → 201 Created
    //   Location: /api/books/42
    //   { "id": 42, "title": "Clean Code", ... }
}

// 204 No Content (delete — nothing to return)
@DeleteMapping("/{id}")
public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
    bookService.delete(id);
    return ResponseEntity.noContent().build();
    // → 204 No Content
}

// Custom status + body
return ResponseEntity.status(HttpStatus.ACCEPTED).body(result);

// Custom headers
return ResponseEntity.ok()
        .header("X-Total-Count", String.valueOf(totalCount))
        .header("X-Page", String.valueOf(page))
        .body(books);
```

**Common HTTP status codes for REST APIs:**
| Code | Meaning | Use When |
|---|---|---|
| 200 OK | Success | GET, PUT, PATCH returning a body |
| 201 Created | Resource created | POST that creates a resource |
| 204 No Content | Success, no body | DELETE, or PUT/PATCH with no response |
| 400 Bad Request | Client error | Validation failure, malformed request |
| 401 Unauthorized | Not authenticated | No valid credentials provided |
| 403 Forbidden | Not authorized | Authenticated but no permission |
| 404 Not Found | Resource missing | GET/PUT/DELETE on non-existent resource |
| 409 Conflict | State conflict | Duplicate key, concurrent modification |
| 422 Unprocessable Entity | Semantic validation error | Business rule violation |
| 500 Internal Server Error | Server error | Unhandled exception |

---

### Slide 7 — Layered Architecture
**Header:** Controller → Service → Repository — Clean Separation of Concerns

**The three-layer pattern:**
```
┌───────────────────────────────────────────────────────┐
│  CONTROLLER LAYER   @RestController                    │
│  • Handles HTTP request/response                       │
│  • Validates input (@Valid)                            │
│  • Calls service layer                                 │
│  • Returns ResponseEntity                              │
│  • NEVER contains business logic                       │
└────────────────────┬──────────────────────────────────┘
                     │ calls methods on
┌────────────────────▼──────────────────────────────────┐
│  SERVICE LAYER   @Service                              │
│  • Contains ALL business logic                         │
│  • Orchestrates calls to repositories                  │
│  • Handles transactions (@Transactional — Day 27)      │
│  • Throws business exceptions                          │
│  • NEVER knows about HTTP, requests, or responses      │
└────────────────────┬──────────────────────────────────┘
                     │ calls methods on
┌────────────────────▼──────────────────────────────────┐
│  REPOSITORY LAYER   @Repository / JpaRepository        │
│  • Reads and writes to the database                    │
│  • NEVER contains business logic                       │
│  • Returns domain entities or projections              │
└───────────────────────────────────────────────────────┘
```

**Why this separation matters:**
- Test the service layer in isolation (no HTTP, no database required)
- Swap the data layer (H2 → PostgreSQL) without touching the service
- Swap the web layer (REST → GraphQL) without touching the service
- Multiple clients (REST API + GraphQL + message queue) share the same service layer

**Data flow example — create a book:**
```
POST /api/books + JSON body
  → BookController.createBook()  validates input, maps CreateBookRequest to domain object
  → BookService.createBook()     checks business rules (duplicate ISBN?), saves
  → BookRepository.save()        writes to database, returns entity
  → BookService maps entity → BookDto
  → BookController wraps in ResponseEntity.created(location).body(bookDto)
  → 201 Created + JSON response
```

---

### Slide 8 — Bean Validation
**Header:** Bean Validation — Reject Bad Input Before It Reaches Your Business Logic

**Dependency (included in `spring-boot-starter-validation`):**
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-validation</artifactId>
</dependency>
```

**Annotate the request class:**
```java
public class CreateBookRequest {

    @NotBlank(message = "Title cannot be blank")
    @Size(min = 1, max = 200, message = "Title must be between 1 and 200 characters")
    private String title;

    @NotNull(message = "Author ID is required")
    @Positive(message = "Author ID must be positive")
    private Long authorId;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.01", message = "Price must be at least 0.01")
    @Digits(integer = 8, fraction = 2, message = "Price format invalid")
    private BigDecimal price;

    @NotBlank
    @Pattern(regexp = "^(?:ISBN(?:-13)?:? )?(?=[0-9]{13}$|(?=(?:[0-9]+[- ]){4})[- 0-9]{17}$)...",
             message = "Invalid ISBN-13 format")
    private String isbn;

    @Valid                        // ← validate nested object too
    @NotNull
    private AddressDto publisher;
}
```

**Trigger validation with @Valid in the controller:**
```java
@PostMapping
public ResponseEntity<BookDto> createBook(@RequestBody @Valid CreateBookRequest request) {
    // If any @Valid constraint fails → Spring throws MethodArgumentNotValidException
    // before this method body even executes
    return ResponseEntity.created(location).body(bookService.create(request));
}
```

**Key validation annotations:**
| Annotation | Validates |
|---|---|
| `@NotNull` | Not null (can be empty string) |
| `@NotBlank` | Not null and not all whitespace |
| `@NotEmpty` | Not null and not empty collection/string |
| `@Size(min, max)` | String length or collection size |
| `@Min(n)` / `@Max(n)` | Numeric minimum/maximum |
| `@Positive` / `@PositiveOrZero` | Number > 0 / >= 0 |
| `@Email` | Valid email format |
| `@Pattern(regexp)` | Regex match |
| `@Past` / `@Future` | Date in past/future |
| `@DecimalMin` / `@DecimalMax` | BigDecimal range |
| `@Digits(integer, fraction)` | Max digits in integer and decimal parts |

---

### Slide 9 — Custom Validators
**Header:** Custom Validators — Domain-Specific Validation Rules

**When built-in constraints aren't enough:** Checking that an ISBN-13 checksum is valid. Verifying a username doesn't already exist in the database. Ensuring two fields are consistent with each other.

**Step 1 — Create the annotation:**
```java
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = IsbnValidator.class)
@Documented
public @interface ValidIsbn {
    String message() default "Invalid ISBN-13";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
```

**Step 2 — Create the validator class:**
```java
public class IsbnValidator implements ConstraintValidator<ValidIsbn, String> {

    @Override
    public void initialize(ValidIsbn annotation) {
        // access annotation attributes here if needed
    }

    @Override
    public boolean isValid(String isbn, ConstraintValidatorContext context) {
        if (isbn == null || isbn.isBlank()) return true; // let @NotBlank handle null/blank
        String digits = isbn.replaceAll("[- ]", "");
        if (digits.length() != 13) return false;
        int sum = 0;
        for (int i = 0; i < 13; i++) {
            int digit = Character.getNumericValue(digits.charAt(i));
            sum += (i % 2 == 0) ? digit : digit * 3;
        }
        return sum % 10 == 0;
    }
}
```

**Step 3 — Use it:**
```java
public class CreateBookRequest {
    @ValidIsbn
    private String isbn;
}
```

**Cross-field validation (class-level constraint):**
```java
@ValidDateRange    // annotation on the class — can compare startDate and endDate
public class DateRangeRequest {
    private LocalDate startDate;
    private LocalDate endDate;
}
```

---

### Slide 10 — Exception Handling with @ControllerAdvice
**Header:** @ControllerAdvice — Global Exception Handling

**The problem without global handling:**
```java
// Without @ControllerAdvice, unhandled exceptions return Spring's ugly default error page
// or a 500 with a stack trace exposed to the client
```

**The solution — one class handles all exceptions across all controllers:**
```java
@RestControllerAdvice                    // = @ControllerAdvice + @ResponseBody
@Slf4j
public class GlobalExceptionHandler {

    // Validation failures from @Valid
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationErrors(MethodArgumentNotValidException ex) {
        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(e -> e.getField() + ": " + e.getDefaultMessage())
                .toList();
        return new ErrorResponse(400, "Validation failed", errors);
    }

    // Business "not found" exception
    @ExceptionHandler(BookNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFound(BookNotFoundException ex) {
        log.warn("Book not found: {}", ex.getMessage());
        return new ErrorResponse(404, ex.getMessage(), null);
    }

    // Catch-all for unhandled exceptions — never expose stack traces to clients
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleGeneral(Exception ex) {
        log.error("Unhandled exception", ex);   // log the full stack trace server-side
        return new ErrorResponse(500, "An unexpected error occurred", null);
    }
}
```

**The ErrorResponse DTO:**
```java
@Data
@AllArgsConstructor
public class ErrorResponse {
    private int status;
    private String message;
    private List<String> errors;
}
```

**Custom business exceptions:**
```java
public class BookNotFoundException extends RuntimeException {
    public BookNotFoundException(Long id) {
        super("Book not found with id: " + id);
    }
}

// In BookService:
public BookDto findById(Long id) {
    return bookRepository.findById(id)
            .map(this::toDto)
            .orElseThrow(() -> new BookNotFoundException(id));
}
```

**What clients receive for a 404:**
```json
{ "status": 404, "message": "Book not found with id: 99", "errors": null }
```

---

### Slide 11 — HTTP Problem Details (RFC 9457)
**Header:** ProblemDetail — The Standard Error Response Format

**Spring Boot 3 includes built-in support for RFC 9457 Problem Details:**
```java
// Spring Boot 3 auto-enables ProblemDetail for standard exceptions
// with:
spring.mvc.problemdetails.enabled=true
```

**Standard ProblemDetail format:**
```json
{
  "type": "https://example.com/errors/book-not-found",
  "title": "Book Not Found",
  "status": 404,
  "detail": "Book with id 99 does not exist",
  "instance": "/api/books/99"
}
```

**Using ProblemDetail in exception handlers:**
```java
@ExceptionHandler(BookNotFoundException.class)
public ProblemDetail handleBookNotFound(BookNotFoundException ex,
                                        HttpServletRequest request) {
    ProblemDetail problem = ProblemDetail.forStatusAndDetail(
            HttpStatus.NOT_FOUND, ex.getMessage());
    problem.setTitle("Book Not Found");
    problem.setType(URI.create("https://bookstore.com/errors/book-not-found"));
    problem.setInstance(URI.create(request.getRequestURI()));
    problem.setProperty("bookId", ex.getBookId());  // custom extension fields
    return problem;
}
```

**Why this matters:** Before RFC 9457, every team invented their own error response format. Standard format means API clients can handle errors consistently across different services.

---

### Slide 12 — DTOs vs Entities
**Header:** DTOs vs Entities — What to Expose, What to Hide

**Entity (database representation — Day 27 detail):**
```java
@Entity
public class Book {
    @Id private Long id;
    private String title;
    private BigDecimal price;
    private String isbn;
    @ManyToOne private Author author;
    private LocalDateTime createdAt;
    private String internalNotes;   // ← should NOT be sent to clients
    private boolean deleted;        // soft-delete flag — internal
}
```

**DTO (what the API client sees):**
```java
@Data
@Builder
public class BookDto {
    private Long id;
    private String title;
    private BigDecimal price;
    private String isbn;
    private String authorName;     // flattened from Author entity
    // missing: internalNotes, deleted, createdAt
}
```

**Why DTOs are necessary:**
1. **Security** — never expose fields clients shouldn't see (internal notes, soft-delete flags, audit fields)
2. **API stability** — entity shape changes without breaking the API contract
3. **Flexibility** — flatten, aggregate, or reshape data for the API's needs
4. **Validation** — request DTOs have `@Valid` annotations; entities have JPA constraints

**Three DTO categories:**
- **Response DTO** (`BookDto`) — what GET endpoints return
- **Create request DTO** (`CreateBookRequest`) — what POST expects
- **Update request DTO** (`UpdateBookRequest`) — what PUT/PATCH expects

---

### Slide 13 — MapStruct for Object Mapping
**Header:** MapStruct — Type-Safe, Generated Object Mapping

**The manual mapping problem:**
```java
// Manual mapping — tedious, error-prone, breaks when fields are added
public BookDto toDto(Book book) {
    BookDto dto = new BookDto();
    dto.setId(book.getId());
    dto.setTitle(book.getTitle());
    dto.setPrice(book.getPrice());
    dto.setAuthorName(book.getAuthor().getFirstName() + " " + book.getAuthor().getLastName());
    // forget a field → silently returns null
    return dto;
}
```

**MapStruct — define the mapping, it generates the code:**
```java
@Mapper(componentModel = "spring")   // makes it a Spring @Component, injectable
public interface BookMapper {

    @Mapping(source = "author.firstName", target = "authorFirstName")
    @Mapping(source = "author.lastName",  target = "authorLastName")
    BookDto toDto(Book book);

    @Mapping(target = "id", ignore = true)          // don't map ID for creates
    @Mapping(target = "createdAt", ignore = true)   // set by @PrePersist, not from DTO
    @Mapping(target = "author", ignore = true)      // set in service after loading author
    Book toEntity(CreateBookRequest request);

    List<BookDto> toDtoList(List<Book> books);      // MapStruct generates list mapping
}
```

**Usage in service:**
```java
@Service
@RequiredArgsConstructor
public class BookService {
    private final BookRepository bookRepository;
    private final BookMapper bookMapper;

    public BookDto findById(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new BookNotFoundException(id));
        return bookMapper.toDto(book);                  // clean, generated, null-safe
    }
}
```

**Maven dependency:**
```xml
<dependency>
    <groupId>org.mapstruct</groupId>
    <artifactId>mapstruct</artifactId>
    <version>1.5.5.Final</version>
</dependency>
<dependency>
    <groupId>org.mapstruct</groupId>
    <artifactId>mapstruct-processor</artifactId>
    <version>1.5.5.Final</version>
    <scope>provided</scope>
</dependency>
```

**ModelMapper (awareness):** ModelMapper uses reflection to map by name convention at runtime. Less setup than MapStruct, but slower (reflection overhead), no compile-time verification, harder to debug mapping errors. MapStruct is preferred for production applications.

---

### Slide 14 — CORS Configuration
**Header:** CORS — Allowing Cross-Origin Requests

**What CORS is:** Browsers enforce the Same-Origin Policy — a JavaScript application running on `https://bookstore-frontend.com` cannot call `https://bookstore-api.com/api/books` unless the API server explicitly allows it. CORS (Cross-Origin Resource Sharing) is the mechanism for that permission.

**The browser preflight flow:**
```
Browser (origin: https://bookstore-frontend.com)
  │
  │ OPTIONS /api/books
  │ Origin: https://bookstore-frontend.com
  │ Access-Control-Request-Method: POST
  │
  ▼
Spring Boot Server
  │
  │ 200 OK
  │ Access-Control-Allow-Origin: https://bookstore-frontend.com
  │ Access-Control-Allow-Methods: GET, POST, PUT, DELETE
  │ Access-Control-Allow-Headers: Content-Type, Authorization
  │
  ▼
Browser: "OK, allowed" → sends actual POST request
```

**Method 1 — @CrossOrigin on controller (fine-grained):**
```java
@RestController
@RequestMapping("/api/books")
@CrossOrigin(origins = "https://bookstore-frontend.com")
public class BookController { ... }
```

**Method 2 — Global CORS config (recommended for multiple origins):**
```java
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins(
                    "https://bookstore-frontend.com",
                    "http://localhost:3000"         // React dev server
                )
                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
                .allowedHeaders("Content-Type", "Authorization")
                .allowCredentials(true)             // allow cookies/auth headers
                .maxAge(3600);                      // cache preflight for 1 hour
    }
}
```

**⚠️ Never use `allowedOrigins("*")` with `allowCredentials(true)` — this combination is a security risk and Spring will throw an exception. If credentials are needed, list specific origins.**

---

### Slide 15 — WebSocket Brief Overview
**Header:** WebSockets — When HTTP Request-Response Isn't Enough

**HTTP is request-response:** Client sends request, server responds, connection closes. For chat, live notifications, real-time dashboards — the server needs to push data to the client without the client asking first. HTTP polling (ask every 5 seconds) is inefficient. WebSockets maintain a persistent, bidirectional connection.

**Adding WebSocket support:**
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-websocket</artifactId>
</dependency>
```

**Simple WebSocket endpoint:**
```java
@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(new BookNotificationHandler(), "/ws/notifications")
                .setAllowedOrigins("*");
    }
}
```

**STOMP over WebSocket (Spring's higher-level abstraction):**
- STOMP is a messaging protocol layered over WebSocket
- Enables pub/sub patterns (subscribe to `/topic/book-updates`)
- Spring's `@MessageMapping` handles STOMP messages like `@RequestMapping` handles HTTP
- JavaScript client uses `SockJS` + `STOMP.js` libraries
- Full Spring WebSocket/STOMP details are out of scope for today — the key concept is that WebSockets solve real-time communication, and Spring has first-class support

**Use cases:** Live chat, real-time notifications, collaborative editing, live dashboards, trading feeds.

---

### Slide 16 — Part 1 Summary
**Header:** Part 1 Summary — Spring MVC Reference

**Request handling quick reference:**
```
@GetMapping("/{id}")        → GET  + path variable
@PostMapping                → POST + @RequestBody @Valid DTO
@PutMapping("/{id}")        → PUT  + path variable + @RequestBody @Valid DTO
@DeleteMapping("/{id}")     → DELETE + path variable → 204 No Content

@PathVariable Long id       → /api/books/42
@RequestParam String q      → /api/books?q=clean
@RequestBody CreateReq req  → JSON body → Java object
```

**ResponseEntity cheat sheet:**
```
ResponseEntity.ok(body)                    → 200 OK
ResponseEntity.created(uri).body(dto)      → 201 Created + Location header
ResponseEntity.noContent().build()         → 204 No Content
ResponseEntity.notFound().build()          → 404 Not Found
ResponseEntity.badRequest().body(error)    → 400 Bad Request
```

**Layer responsibilities:**
- Controller: HTTP in/out, validation trigger, delegate to service
- Service: business logic, transactions, exceptions
- Repository: database only

**Error handling:**
- `@RestControllerAdvice` catches exceptions globally
- Domain exceptions → specific status codes
- Never expose stack traces to clients
- `spring.mvc.problemdetails.enabled=true` for RFC 9457 standard format

**Part 2 preview:** Aspect-Oriented Programming — how Spring implements logging, transaction management, security, and caching without cluttering your business code.

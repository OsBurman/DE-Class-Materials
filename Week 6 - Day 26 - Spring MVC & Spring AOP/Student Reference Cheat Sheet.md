# Day 26 Review — Spring MVC & Spring AOP
## Complete Reference Document

---

## 1. Spring MVC Architecture Overview

Spring MVC is a web framework built on the Servlet API. It implements the Front Controller pattern — every HTTP request passes through a single entry point, the `DispatcherServlet`, which delegates to other components.

**Auto-configured by `spring-boot-starter-web`:**
- `DispatcherServlet` — mounted at `/` by default
- `RequestMappingHandlerMapping` — maps URLs to controller methods
- `RequestMappingHandlerAdapter` — resolves parameters and invokes methods
- `HttpMessageConverter` (Jackson) — serializes/deserializes JSON
- `ContentNegotiationStrategy` — determines response format
- `ExceptionHandlerExceptionResolver` — routes exceptions to `@ExceptionHandler`

---

## 2. DispatcherServlet Request Flow

```
HTTP Request arrives
    │
    ▼
DispatcherServlet  (single entry point)
    │
    ▼ finds handler
HandlerMapping  → returns handler method + interceptors
    │
    ▼ invokes handler
HandlerAdapter  → resolves @PathVariable, @RequestParam, @RequestBody, etc.
    │
    ▼ method runs
@Controller / @RestController  → YOUR code executes
    │
    ▼ method returns
MessageConverter  → Java object → JSON (Jackson)
    │
    ▼
HTTP Response  → 200 OK + application/json body
```

---

## 3. @Controller vs @RestController

| Annotation | Returns | Response |
|---|---|---|
| `@Controller` | View name (String) | HTML page (Thymeleaf) |
| `@RestController` | Data object | JSON via Jackson |

`@RestController` = `@Controller` + `@ResponseBody` on every method.

With `@Controller`, add `@ResponseBody` to individual methods to return data instead of a view name.

---

## 4. Request Mapping Annotations Reference

```java
@RequestMapping("/api/books")   // base path; can be at class level
@GetMapping("/{id}")            // GET /api/books/{id}
@PostMapping                    // POST /api/books
@PutMapping("/{id}")            // PUT /api/books/{id}
@PatchMapping("/{id}")          // PATCH /api/books/{id}
@DeleteMapping("/{id}")         // DELETE /api/books/{id}
```

Class-level `@RequestMapping` applies as a prefix to all methods in the class.

---

## 5. @RequestParam Reference

Extract query string parameters from the URL.

```java
// GET /api/books?category=fiction
@RequestParam String category

// Optional — null if absent
@RequestParam(required = false) String category

// With default value
@RequestParam(defaultValue = "0") int page

// Explicit name (when Java variable name differs)
@RequestParam(name = "sort_by") String sortBy

// Collect multiple values: /api/books?ids=1&ids=2&ids=3
@RequestParam List<Long> ids
```

**Use for:** filtering, sorting, pagination — optional modifiers on a collection endpoint.

---

## 6. @PathVariable Reference

Extract a segment from the URL path template.

```java
// GET /api/books/42
@GetMapping("/{id}")
public BookDto get(@PathVariable Long id) { ... }

// Multiple variables
@GetMapping("/api/authors/{authorId}/books/{bookId}")
public BookDto get(@PathVariable Long authorId, @PathVariable Long bookId) { ... }

// Explicit name
@GetMapping("/{bookId}")
public BookDto get(@PathVariable("bookId") Long id) { ... }
```

**Use for:** identifying a specific resource — the identity belongs in the path.

---

## 7. @RequestBody and @ResponseBody

```java
// @RequestBody — deserialize JSON request body → Java object
@PostMapping
public BookDto createBook(@RequestBody @Valid CreateBookRequest request) { ... }

// Always combine @RequestBody with @Valid to trigger Bean Validation

// @ResponseBody — serialize return value → JSON (in response body)
// Applied automatically to every method when class is @RestController
// Add to individual methods with @Controller when returning data
@GetMapping
@ResponseBody
public List<BookDto> getBooks() { ... }
```

Jackson field mapping: camelCase Java ↔ camelCase JSON by default. Configure with:
- `spring.jackson.property-naming-strategy=SNAKE_CASE` for snake_case globally
- `@JsonProperty("field_name")` on individual fields

---

## 8. ResponseEntity Reference

`ResponseEntity<T>` gives full control over HTTP status, headers, and body.

```java
// 200 OK
ResponseEntity.ok(body)
ResponseEntity.ok().body(body)

// 201 Created + Location header
ResponseEntity.created(URI.create("/api/books/" + id)).body(dto)

// 204 No Content (DELETE success)
ResponseEntity.noContent().build()

// 400 Bad Request
ResponseEntity.badRequest().body(errorResponse)

// 404 Not Found
ResponseEntity.notFound().build()

// Custom status
ResponseEntity.status(HttpStatus.ACCEPTED).body(result)
ResponseEntity.status(422).body(errorResponse)

// Custom headers
ResponseEntity.ok()
    .header("X-Total-Count", String.valueOf(total))
    .header("X-Page", String.valueOf(page))
    .body(items)
```

---

## 9. HTTP Status Codes Reference

| Code | Name | Use When |
|---|---|---|
| 200 | OK | GET, PUT, PATCH — returns a body |
| 201 | Created | POST that creates a resource |
| 202 | Accepted | Async operation — response will come later |
| 204 | No Content | DELETE, or PUT/PATCH with no response body |
| 400 | Bad Request | Validation failure, malformed request |
| 401 | Unauthorized | No valid credentials provided |
| 403 | Forbidden | Authenticated but lacks permission |
| 404 | Not Found | Resource does not exist |
| 405 | Method Not Allowed | HTTP method not supported on this endpoint |
| 409 | Conflict | Duplicate key, concurrent modification conflict |
| 422 | Unprocessable Entity | Request valid but semantically incorrect |
| 429 | Too Many Requests | Rate limit exceeded |
| 500 | Internal Server Error | Unhandled server error |
| 503 | Service Unavailable | Downstream dependency unreachable |

---

## 10. Three-Layer Architecture

```
CONTROLLER  @RestController
            Responsibility: HTTP boundary. Validates input, calls service, returns ResponseEntity.
            Never contains business logic.

SERVICE     @Service
            Responsibility: All business logic. Throws business exceptions. Manages transactions.
            Never references HttpServletRequest, ResponseEntity, or HTTP concepts.

REPOSITORY  @Repository / JpaRepository
            Responsibility: Database I/O only. Returns entities or projections.
            Never contains business logic.
```

**Why layers matter:**
- Service layer testable without HTTP or database
- Repository swappable (H2 ↔ PostgreSQL) without touching the service
- Multiple transports (REST + GraphQL + queue) share one service layer

---

## 11. Bean Validation Annotation Reference

Add dependency: `spring-boot-starter-validation`

Trigger validation: `@RequestBody @Valid CreateBookRequest request`

On failure: Spring throws `MethodArgumentNotValidException` before the method body executes.

| Annotation | Validates |
|---|---|
| `@NotNull` | Field is not null (permits empty string) |
| `@NotBlank` | Not null and not whitespace-only (String) |
| `@NotEmpty` | Not null and not empty (String, Collection, Array) |
| `@Size(min, max)` | String length or collection size within range |
| `@Min(value)` | Number ≥ value |
| `@Max(value)` | Number ≤ value |
| `@Positive` | Number > 0 |
| `@PositiveOrZero` | Number ≥ 0 |
| `@Negative` | Number < 0 |
| `@Email` | Valid email format |
| `@Pattern(regexp)` | Matches regular expression |
| `@Past` | Date/time in the past |
| `@PastOrPresent` | Date/time in past or now |
| `@Future` | Date/time in the future |
| `@FutureOrPresent` | Date/time in future or now |
| `@DecimalMin(value)` | BigDecimal ≥ value |
| `@DecimalMax(value)` | BigDecimal ≤ value |
| `@Digits(integer, fraction)` | Max digits in each decimal part |
| `@Valid` | Cascade validation to a nested object |

---

## 12. Custom Validators

Two-step process:

**Step 1 — Create the constraint annotation:**
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

**Step 2 — Create the validator:**
```java
public class IsbnValidator implements ConstraintValidator<ValidIsbn, String> {
    @Override
    public boolean isValid(String value, ConstraintValidatorContext ctx) {
        if (value == null || value.isBlank()) return true; // let @NotBlank handle nulls
        // ... your validation logic
    }
}
```

**Use it:**
```java
public class CreateBookRequest {
    @ValidIsbn
    private String isbn;
}
```

**Cross-field validation:** Apply `@Constraint` at the class level and implement `ConstraintValidator<MyAnnotation, MyClass>`.

---

## 13. Exception Handling with @ControllerAdvice

```java
@RestControllerAdvice   // = @ControllerAdvice + @ResponseBody
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidation(MethodArgumentNotValidException ex) {
        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(e -> e.getField() + ": " + e.getDefaultMessage())
                .toList();
        return new ErrorResponse(400, "Validation failed", errors);
    }

    @ExceptionHandler(BookNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFound(BookNotFoundException ex) {
        return new ErrorResponse(404, ex.getMessage(), null);
    }

    @ExceptionHandler(Exception.class)              // catch-all
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleGeneral(Exception ex) {
        log.error("Unhandled exception", ex);       // log full stack trace server-side
        return new ErrorResponse(500, "An unexpected error occurred", null);  // hide details from client
    }
}
```

**Custom business exceptions:**
```java
public class BookNotFoundException extends RuntimeException {
    private final Long bookId;
    public BookNotFoundException(Long id) {
        super("Book not found with id: " + id);
        this.bookId = id;
    }
    public Long getBookId() { return bookId; }
}
```

---

## 14. ProblemDetail (RFC 9457 / RFC 7807)

Enable: `spring.mvc.problemdetails.enabled=true` in `application.properties`

Standard response format:
```json
{
  "type": "https://bookstore.com/errors/book-not-found",
  "title": "Book Not Found",
  "status": 404,
  "detail": "Book with id 99 does not exist",
  "instance": "/api/books/99"
}
```

```java
@ExceptionHandler(BookNotFoundException.class)
public ProblemDetail handleBookNotFound(BookNotFoundException ex,
                                        HttpServletRequest request) {
    ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
    problem.setTitle("Book Not Found");
    problem.setType(URI.create("https://bookstore.com/errors/book-not-found"));
    problem.setInstance(URI.create(request.getRequestURI()));
    problem.setProperty("bookId", ex.getBookId());  // custom extension field
    return problem;
}
```

---

## 15. DTOs vs Entities

| Entity | DTO |
|---|---|
| Database model — mapped to table columns | API model — shaped for the client |
| Contains all DB fields including internal ones | Contains only fields the client needs |
| Has JPA annotations (`@Entity`, `@Column`, etc.) | Has validation annotations (`@NotBlank`, etc.) |
| Returned from repositories | Returned from controllers |
| Should NEVER be sent directly in HTTP responses | Purpose-built for the API contract |

**Three DTO types per domain object:**
- **Response DTO** (`BookDto`) — returned by GET and written to by POST/PUT
- **Create Request DTO** (`CreateBookRequest`) — received by POST
- **Update Request DTO** (`UpdateBookRequest`) — received by PUT/PATCH

---

## 16. MapStruct Reference

```xml
<!-- pom.xml -->
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

```java
@Mapper(componentModel = "spring")   // registers as @Component, injectable
public interface BookMapper {

    // Simple mapping — fields with matching names are mapped automatically
    BookDto toDto(Book book);

    // Field name mismatch
    @Mapping(source = "author.firstName", target = "authorFirstName")
    BookDto toDtoDetailed(Book book);

    // Ignore specific fields
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    Book toEntity(CreateBookRequest request);

    // List — MapStruct generates the loop
    List<BookDto> toDtoList(List<Book> books);

    // Update existing entity in-place
    @MappingTarget
    void updateEntity(@MappingTarget Book book, UpdateBookRequest request);
}
```

**Inject and use:**
```java
@Service
@RequiredArgsConstructor
public class BookService {
    private final BookMapper bookMapper;

    public BookDto findById(Long id) {
        return bookRepository.findById(id)
                .map(bookMapper::toDto)
                .orElseThrow(() -> new BookNotFoundException(id));
    }
}
```

---

## 17. ModelMapper (Awareness)

ModelMapper maps by name convention using reflection at runtime.

```java
// Bean declaration
@Bean
public ModelMapper modelMapper() { return new ModelMapper(); }

// Usage
BookDto dto = modelMapper.map(book, BookDto.class);
```

**ModelMapper vs MapStruct:**
| | MapStruct | ModelMapper |
|---|---|---|
| Implementation | Generated Java code | Reflection |
| Performance | Fast (compile-time) | Slower (runtime) |
| Type safety | Compile-time errors | Runtime errors |
| Debugging | Read the generated class | Harder to trace |
| Setup | More verbose | Less setup |
| Recommendation | Production applications | Prototypes / small projects |

---

## 18. CORS Configuration

**Method 1 — `@CrossOrigin` (per controller):**
```java
@RestController
@CrossOrigin(origins = {"https://bookstore-frontend.com", "http://localhost:3000"})
public class BookController { ... }
```

**Method 2 — Global configuration (recommended):**
```java
@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins("https://bookstore-frontend.com", "http://localhost:3000")
                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
                .allowedHeaders("Content-Type", "Authorization")
                .allowCredentials(true)
                .maxAge(3600);
    }
}
```

**⚠️ NEVER combine `.allowedOrigins("*")` with `.allowCredentials(true)` — Spring will throw an exception. List specific origins when using credentials.**

---

## 19. WebSocket Brief Overview

WebSockets maintain a persistent bidirectional connection between client and server.

**HTTP:** Client sends request → server responds → connection closes.
**WebSocket:** Connection established once → both sides can send data at any time.

**Use cases:** Real-time chat, live notifications, dashboards, collaborative editing.

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-websocket</artifactId>
</dependency>
```

```java
@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(myHandler(), "/ws/endpoint").setAllowedOrigins("*");
    }
}
```

For production: use STOMP over WebSocket (Spring's messaging abstraction) + SockJS client library. Full WebSocket details are covered in later modules.

---

## 20. AOP Core Terminology

| Term | Definition |
|---|---|
| **Aspect** | Class containing cross-cutting logic. Annotated `@Aspect @Component`. |
| **Join Point** | A point in execution where an aspect can be applied. In Spring AOP: always a method execution. |
| **Pointcut** | Expression selecting which join points the advice applies to. |
| **Advice** | The code that runs at a matched join point. |
| **Weaving** | Linking aspects to target objects. Spring AOP weaves at runtime via proxy creation. |
| **Target** | The object being proxied. |
| **Proxy** | The wrapper object Spring creates around the target to intercept calls. |

---

## 21. Spring AOP vs AspectJ

| | Spring AOP | AspectJ |
|---|---|---|
| Weaving | Runtime (proxy creation) | Compile-time or load-time bytecode |
| Can intercept | Public methods on Spring beans | Anything (private, static, constructors, fields) |
| Setup | Simple (starter dependency) | Requires compiler or agent |
| Self-invocation | Does NOT intercept | Intercepts |
| Performance | Slight proxy overhead | Minimal overhead |
| Use cases | 95% of enterprise applications | Framework development, specialized needs |

**The self-invocation problem:**
```java
// PROBLEM: calling this.internalMethod() bypasses the proxy
@Service
public class BookService {
    public void outerMethod() {
        this.innerMethod();    // ← direct call, no proxy, no @Transactional, no @Before aspect
    }
    @Transactional
    public void innerMethod() { ... }   // @Transactional has NO EFFECT when called via this.innerMethod()
}

// SOLUTION: separate bean, or make outerMethod @Transactional too
```

---

## 22. All Five Advice Types

```java
@Aspect
@Component
public class ExampleAspect {

    // Runs BEFORE the target method
    @Before("execution(* com.example.service.*.*(..))")
    public void before(JoinPoint jp) { ... }

    // Runs AFTER target method — always (like finally)
    @After("execution(* com.example.service.*.*(..))")
    public void after(JoinPoint jp) { ... }

    // Runs only on SUCCESSFUL return — can access return value
    @AfterReturning(pointcut = "execution(* com.example.service.*.*(..))", returning = "result")
    public void afterReturning(JoinPoint jp, Object result) { ... }

    // Runs only on EXCEPTION — can access the exception — does NOT suppress it
    @AfterThrowing(pointcut = "execution(* com.example.service.*.*(..))", throwing = "ex")
    public void afterThrowing(JoinPoint jp, RuntimeException ex) { ... }

    // WRAPS entire execution — must call proceed() — must return result
    @Around("execution(* com.example.service.*.*(..))")
    public Object around(ProceedingJoinPoint pjp) throws Throwable {
        // before logic
        Object result = pjp.proceed();  // invoke target
        // after logic
        return result;                  // return result (MUST)
    }
}
```

**Execution order when multiple advice types apply to the same method:**
1. `@Around` (before `proceed()`)
2. `@Before`
3. Target method executes
4. `@AfterReturning` or `@AfterThrowing`
5. `@After`
6. `@Around` (after `proceed()`)

---

## 23. Pointcut Expression Reference

**`execution()` designator syntax:**
```
execution(modifiers? return-type declaring-type? method-name(params) throws?)
```

```java
// All public methods anywhere
execution(public * *(..))

// All methods in BookService
execution(* com.example.service.BookService.*(..))

// All methods in service package (not sub-packages)
execution(* com.example.service.*.*(..))

// All methods in service package AND all sub-packages (..)
execution(* com.example.service..*.*(..))

// Only find* methods in service package
execution(* com.example.service.*.find*(..))

// Methods returning BookDto specifically
execution(com.example.dto.BookDto com.example.service.*.*(..))

// Methods with exactly one Long parameter
execution(* com.example.service.*.*(Long))

// Methods with any parameters
execution(* com.example.service.*.*(..))
```

**Other designators:**
```java
// All @Service-annotated classes
@within(org.springframework.stereotype.Service)

// Methods with specific annotation
@annotation(com.example.annotation.Timed)

// Within a type (class-level matching, no sub-packages)
within(com.example.service.BookService)
```

**Named pointcuts and combinations:**
```java
@Pointcut("execution(* com.example.service.*.*(..))")
public void serviceLayer() {}

@Pointcut("execution(* com.example.repository.*.*(..))")
public void repositoryLayer() {}

@Pointcut("serviceLayer() || repositoryLayer()")
public void dataLayer() {}

// Combinations in advice:
@Before("serviceLayer() && !@annotation(com.example.annotation.NoLog)")
public void logBefore(JoinPoint jp) { ... }
```

---

## 24. @Transactional as AOP

`@Transactional` is Spring's most-used built-in AOP feature. Spring's `TransactionInterceptor` (an `@Around` advice) wraps every annotated method:

```
proxy receives call
    → TransactionInterceptor: begin transaction
    → target method executes
    → success: commit transaction
    → exception: roll back transaction
```

This is why Spring AOP's self-invocation limitation matters for `@Transactional` — a direct `this.method()` call bypasses the proxy, so no transaction is started.

**Check if your bean is a proxy:**
```java
AopUtils.isAopProxy(bookService)         // true if proxied
AopUtils.isCglibProxy(bookService)       // true if CGLIB subclass proxy
bookService.getClass().getName()         // shows $$SpringCGLIB suffix
```

---

## 25. @Transactional Best Practices

```java
// ✅ Service layer only
@Service
public class BookService {

    @Transactional(readOnly = true)      // all reads
    public List<BookDto> getAllBooks() { ... }

    @Transactional                        // writes
    public BookDto createBook(CreateBookRequest req) { ... }
}

// ✅ Class-level default
@Service
@Transactional(readOnly = true)          // default for all methods
public class BookService {
    public List<BookDto> getAllBooks() { ... }  // uses class default

    @Transactional                        // overrides for write
    public BookDto createBook(CreateBookRequest req) { ... }
}

// Rollback on checked exception
@Transactional(rollbackFor = InsufficientStockException.class)
public void processOrder(OrderRequest req) throws InsufficientStockException { ... }

// Prevent rollback on specific exception
@Transactional(noRollbackFor = OptimisticLockingFailureException.class)
public void updateWithRetry(Long id) { ... }
```

**Propagation levels:**
| Propagation | Behavior |
|---|---|
| `REQUIRED` (default) | Join existing; create new if none |
| `REQUIRES_NEW` | Always create new; suspend any existing |
| `NESTED` | Nested savepoint within existing |
| `NEVER` | Throw if transaction exists |
| `MANDATORY` | Throw if no transaction exists |
| `SUPPORTS` | Join if exists; non-transactional otherwise |
| `NOT_SUPPORTED` | Run non-transactionally; suspend existing |

---

## 26. Logback Configuration Reference

File: `src/main/resources/logback-spring.xml`

```xml
<configuration>
    <property name="LOG_DIR" value="logs" />

    <!-- Console appender -->
    <appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>%d{HH:mm:ss.SSS} %highlight(%-5level) [%thread] %cyan(%logger{36}) - %msg%n</pattern>
        </encoder>
    </appender>

    <!-- Rolling file appender -->
    <appender name="FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <file>${LOG_DIR}/app.log</file>
        <rollingPolicy class="ch.qos.logback.core.rolling.SizeAndTimeBasedRollingPolicy">
            <fileNamePattern>${LOG_DIR}/app.%d{yyyy-MM-dd}.%i.log.gz</fileNamePattern>
            <maxFileSize>100MB</maxFileSize>
            <maxHistory>30</maxHistory>
            <totalSizeCap>2GB</totalSizeCap>
        </rollingPolicy>
        <encoder>
            <pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} %-5level [%thread] %logger{36} [%X{traceId}] - %msg%n</pattern>
        </encoder>
    </appender>

    <springProfile name="dev">
        <logger name="com.example" level="DEBUG" />
        <root level="INFO"><appender-ref ref="CONSOLE" /></root>
    </springProfile>

    <springProfile name="prod">
        <logger name="com.example" level="INFO" />
        <root level="WARN"><appender-ref ref="FILE" /></root>
    </springProfile>
</configuration>
```

**Pattern specifiers:**
| Specifier | Output |
|---|---|
| `%d{pattern}` | Timestamp |
| `%-5level` | Level, left-padded to 5 chars |
| `%thread` | Thread name |
| `%logger{N}` | Logger name, max N chars |
| `%msg` | Log message |
| `%n` | Newline |
| `%X{key}` | MDC value for key |
| `%highlight(...)` | ANSI color based on level |
| `%cyan(...)` | Cyan ANSI color |

**MDC (Mapped Diagnostic Context):**
```java
// At request start (filter/interceptor):
MDC.put("traceId", UUID.randomUUID().toString());
MDC.put("userId", currentUser.getId().toString());

// At request end (finally block):
MDC.clear();

// In logback pattern: [%X{traceId}] appears in every log line for this request
```

---

## 27. Debugging Spring Applications

**Strategy hierarchy:**
1. Read the full stack trace — root cause is at the bottom
2. Enable DEBUG logging: `logging.level.org.springframework=DEBUG`
3. Check startup logs for `BeanCreationException` or condition evaluation messages
4. Use IDE breakpoints — set in `@ExceptionHandler` methods to intercept every error
5. Use conditional breakpoints for specific cases (`id == 42L`)

**Common errors:**

| Error | Cause | Fix |
|---|---|---|
| `BeanCreationException` | Circular dependency, bad config, missing `@Bean` | Read root cause at bottom of trace |
| `NoSuchBeanDefinitionException` | Bean not scanned | Verify `@Component`/`@Service`; check component scan base package |
| `BeanCurrentlyInCreationException` | Circular dependency | Refactor; use `@Lazy` as last resort |
| `HttpMessageNotReadableException` | JSON doesn't match DTO | Check field names and types |
| `MethodArgumentNotValidException` | `@Valid` failed | Check error response for which field failed |
| `LazyInitializationException` | JPA lazy load outside session | Add `@Transactional`; use eager fetch; use DTO projections |
| `NullPointerException` in proxy | Uninitialized field after proxy wrapping | Check constructor vs field injection |

**Verifying AOP is active:**
```java
System.out.println(AopUtils.isAopProxy(yourService));     // should be true
System.out.println(yourService.getClass().getName());      // $$SpringCGLIB in class name
```

If `isAopProxy` is false, your aspect isn't being applied. Check:
- Is `@Aspect @Component` on the aspect class?
- Is the component scan including the aspect's package?
- Is `spring-boot-starter-aop` on the classpath?

---

## 28. Common Mistakes and Fixes

| Mistake | Problem | Fix |
|---|---|---|
| No `@Valid` on `@RequestBody` | Validation annotations are silently ignored | Always pair `@RequestBody` with `@Valid` |
| Returning entity from controller | Exposes internal fields, creates circular JSON | Return DTOs always |
| No `@ControllerAdvice` | Exceptions become 500 with stack traces | Add `GlobalExceptionHandler` with `@RestControllerAdvice` |
| Self-invocation with `@Transactional` | Transaction not started | Separate bean, or annotate the outer calling method |
| Forgot to call `proceed()` in `@Around` | Target method never executes | Always call `pjp.proceed()` and return the result |
| Forgot to return from `@Around` | Method appears to return null | `return pjp.proceed()` — capture and return |
| MDC not cleared | Next request inherits previous trace ID | `MDC.clear()` in `finally` block |
| `allowedOrigins("*")` + `allowCredentials(true)` | Spring exception at startup | List specific origins when using credentials |
| `@CrossOrigin` forgotten | Browser CORS errors | Add `@CrossOrigin` or configure `WebMvcConfigurer` |
| MapStruct processor not in `provided` scope | Duplicate mapping code generated | `<scope>provided</scope>` on processor dependency |

---

## 29. Complete Annotation Cheat Sheet

**Spring MVC:**
```
@RestController            Handles HTTP requests; all methods return JSON
@RequestMapping("/path")   Base URL mapping; class-level or method-level
@GetMapping                HTTP GET
@PostMapping               HTTP POST
@PutMapping                HTTP PUT
@PatchMapping              HTTP PATCH
@DeleteMapping             HTTP DELETE
@PathVariable Long id      /api/books/{id} → id
@RequestParam String q     /api/books?q=value → q
@RequestBody @Valid Req r  JSON body → Java object + validate
@ResponseStatus(CREATED)   Override default response status
@CrossOrigin               Allow cross-origin requests
@RestControllerAdvice      Global exception handler class
@ExceptionHandler(Ex.class) Handles specific exception
```

**Bean Validation:**
```
@Valid                      Trigger validation (on @RequestBody param)
@NotNull / @NotBlank / @NotEmpty
@Size(min, max)
@Min / @Max / @Positive / @PositiveOrZero
@Email / @Pattern(regexp)
@Past / @Future
@Constraint(validatedBy)    Create custom constraint annotation
```

**AOP:**
```
@Aspect @Component          Declare an aspect
@Before("pointcut")         Before advice
@After("pointcut")          After (always) advice
@AfterReturning(returning)  After successful return
@AfterThrowing(throwing)    After exception
@Around                     Wrap execution; call proceed()
@Pointcut("expression")     Named pointcut
```

**Transactions:**
```
@Transactional              Apply transaction to method/class (service layer)
@Transactional(readOnly = true)  Read-only optimization
@Transactional(rollbackFor = Ex.class)  Rollback on checked exception
@Transactional(propagation = REQUIRES_NEW)  New transaction always
```

---

## 30. Looking Ahead — Day 27: Spring Data JPA

**You'll implement:**
- `@Entity` and JPA annotations (`@Id`, `@GeneratedValue`, `@Column`, `@ManyToOne`, `@OneToMany`)
- `JpaRepository<Entity, ID>` — 20+ CRUD methods auto-generated
- Custom queries with `@Query` (JPQL)
- Dynamic queries with `Specification`
- Pagination with `Pageable` and `Page<T>`
- Fetch strategies — lazy vs eager loading
- `@Transactional` in depth — flush modes, optimistic locking, the N+1 problem

**Carry forward from today:**
- `@Service` with `@Transactional` annotations — Day 27 shows them working against a real database
- `@Repository` layer — Day 27 fills this in with JPA repositories
- The DTO → Entity → DTO flow — MapStruct stays, entities get JPA annotations
- `@ControllerAdvice` — catch `DataIntegrityViolationException`, `EntityNotFoundException` from JPA

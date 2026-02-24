# Day 30 Part 2 — RBAC, Method Security, Auth Failure Handling, and REST API Security
## Slide Descriptions

---

### Slide 1 — Title Slide

**Title:** Day 30 Part 2: Role-Based Access Control — Who Can Do What

**Subtitle:** Method-level security, authorization failure handling, and REST API hardening

**Learning Objectives:**
- Explain Role-Based Access Control (RBAC) and distinguish roles from fine-grained permissions
- Enable method-level security with `@EnableMethodSecurity`
- Use `@PreAuthorize` with `hasRole()`, `hasAuthority()`, and SpEL ownership expressions
- Use `@Secured` and understand when to choose it over `@PreAuthorize`
- Protect URLs by HTTP method and role using `requestMatchers`
- Access the authenticated user from `SecurityContextHolder` in service code
- Build custom `AuthenticationEntryPoint` (401) and `AccessDeniedHandler` (403) JSON responses
- Apply REST API security best practices: HTTPS, API keys, rate limiting concepts

---

### Slide 2 — RBAC Principles

**Title:** Role-Based Access Control — Coarse-Grained and Fine-Grained

**Content:**

**RBAC definition:** Users are assigned roles. Roles define what actions are permitted. Access decisions are made based on the user's roles, not the individual user.

**Roles vs Permissions:**

| Concept | Granularity | Example | Spring Security Name |
|---------|-------------|---------|----------------------|
| **Role** | Coarse — a job title | `ADMIN`, `USER`, `MANAGER` | `ROLE_ADMIN` (prefixed) |
| **Permission/Authority** | Fine — a specific action | `BOOK_READ`, `BOOK_DELETE`, `USER_MANAGE` | `BOOK_DELETE` (no prefix) |

**In Spring Security:**
- Roles are authorities with the `ROLE_` prefix
- `hasRole("ADMIN")` checks for an authority named `ROLE_ADMIN`
- `hasAuthority("BOOK_DELETE")` checks for an authority named exactly `BOOK_DELETE`
- A user can have both roles and permissions as authorities

**Spring Security authority source — from UserDetails:**
```java
@Override
public Collection<? extends GrantedAuthority> getAuthorities() {
    return List.of(
        new SimpleGrantedAuthority("ROLE_USER"),
        new SimpleGrantedAuthority("BOOK_READ"),
        new SimpleGrantedAuthority("BOOK_DELETE")
    );
}
```

**Roles in the JWT payload (custom claim):**
```json
{
  "sub": "alice",
  "roles": ["ROLE_ADMIN", "BOOK_DELETE"]
}
```

Reading roles from JWT to populate the `SecurityContext`:
```java
// In JwtAuthenticationFilter — load authorities from the JWT itself
// (avoids hitting the DB on every request just for roles)
List<String> roles = jwtService.extractClaim(jwt, claims ->
    claims.get("roles", List.class));

List<GrantedAuthority> authorities = roles.stream()
    .map(SimpleGrantedAuthority::new)
    .collect(Collectors.toList());
```

---

### Slide 3 — URL-Based Security with requestMatchers

**Title:** Protecting URLs by Role and HTTP Method

**Content:**

Building on Day 29's `SecurityFilterChain`, add role-based URL rules:

```java
.authorizeHttpRequests(auth -> auth
    // Public endpoints — no authentication required
    .requestMatchers("/api/auth/**").permitAll()
    .requestMatchers(HttpMethod.GET, "/api/books/**").permitAll()

    // HTTP method + path + role
    .requestMatchers(HttpMethod.POST,   "/api/books/**").hasRole("ADMIN")
    .requestMatchers(HttpMethod.PUT,    "/api/books/**").hasRole("ADMIN")
    .requestMatchers(HttpMethod.DELETE, "/api/books/**").hasRole("ADMIN")

    // Admin-only area
    .requestMatchers("/api/admin/**").hasRole("ADMIN")

    // Specific authority (not role)
    .requestMatchers(HttpMethod.DELETE, "/api/users/**").hasAuthority("USER_DELETE")

    // Multiple roles allowed
    .requestMatchers("/api/reports/**").hasAnyRole("ADMIN", "MANAGER")

    // All other requests require authentication (any role)
    .anyRequest().authenticated()
)
```

**Rule ordering matters:** Spring Security evaluates rules top to bottom and stops at the first match. Put the most specific rules first, the most general (`anyRequest()`) last.

**Note on `antMatchers`:** In Spring Security 5, URL patterns used `antMatchers("/api/**")`. In Spring Security 6 (Spring Boot 3), `antMatchers` is removed — use `requestMatchers` instead. The syntax is otherwise identical. You may see `antMatchers` in older tutorials and projects.

**Limitation of URL-based security:** It's coarse-grained — it can protect an endpoint by role but can't say "this user can only delete their own books, not others'." That's where method-level security comes in.

---

### Slide 4 — @EnableMethodSecurity and @PreAuthorize

**Title:** Method-Level Security — @PreAuthorize

**Content:**

Enable method security on the configuration class:
```java
@Configuration
@EnableWebSecurity
@EnableMethodSecurity   // Spring Security 6 — enables @PreAuthorize and @PostAuthorize
public class SecurityConfig { ... }
```

**Note:** In Spring Security 5, this was `@EnableGlobalMethodSecurity(prePostEnabled = true)`. In Spring Security 6 / Spring Boot 3, use `@EnableMethodSecurity` — the `prePostEnabled` default is already true.

`@PreAuthorize` runs before the method executes and uses Spring Expression Language (SpEL):

```java
// In BookService or BookController

// Must have ROLE_ADMIN authority
@PreAuthorize("hasRole('ADMIN')")
public void deleteBook(Long id) {
    bookRepository.deleteById(id);
}

// Has ROLE_USER OR ROLE_ADMIN
@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
public List<BookDto> getAllBooks() {
    return bookRepository.findAll().stream().map(this::toDto).toList();
}

// Fine-grained authority (not a role)
@PreAuthorize("hasAuthority('BOOK_READ')")
public BookDto getBook(Long id) {
    return toDto(bookRepository.findById(id).orElseThrow());
}

// Ownership check using SpEL — #username refers to the method parameter named username
@PreAuthorize("hasRole('ADMIN') or authentication.name == #username")
public UserDto getUserProfile(String username) {
    return toDto(userRepository.findByUsername(username).orElseThrow());
}

// Must be authenticated (any role)
@PreAuthorize("isAuthenticated()")
public UserDto getCurrentUserProfile() { ... }
```

**SpEL variables available in @PreAuthorize:**
- `authentication` — the current `Authentication` object
- `authentication.name` — the username (`getPrincipal().getUsername()`)
- `authentication.authorities` — the collection of `GrantedAuthority`
- `#paramName` — the value of the method parameter with that name (requires parameter names in bytecode — default in Spring Boot)
- `@beanName.method()` — call any Spring bean method (for complex checks)

**What happens if `@PreAuthorize` fails?**
- Not authenticated → `AuthenticationException` → `AuthenticationEntryPoint` → 401
- Authenticated but wrong role → `AccessDeniedException` → `AccessDeniedHandler` → 403

---

### Slide 5 — @PostAuthorize and @Secured

**Title:** @PostAuthorize and @Secured — Post-Execution and Simpler Authorization

**Content:**

**@PostAuthorize** — runs AFTER the method returns; can inspect the return value:
```java
// Ensures users can only retrieve their own profile
// returnObject refers to the return value of the method
@PostAuthorize("returnObject.username == authentication.name or hasRole('ADMIN')")
public UserDto getUserById(Long id) {
    // This method executes — query hits the database
    // THEN Spring checks the authorization expression against the returned object
    return toDto(userRepository.findById(id).orElseThrow());
}

// Useful when you need to inspect the actual returned data to make an auth decision
// (can't make the decision before the method runs because you don't know what it'll return)
```

**Important caveat for @PostAuthorize:** The method body executes regardless of the authorization outcome. If the method has side effects (e.g., writes to the database), those happen even if the authorization check fails afterward. Use `@PreAuthorize` for write operations. Use `@PostAuthorize` for reads where you need to inspect the returned data.

**@Secured** — simpler, role-name-only, no SpEL:
```java
// Must enable: @EnableMethodSecurity(securedEnabled = true)

@Secured("ROLE_ADMIN")
public void adminOperation() { ... }

@Secured({"ROLE_USER", "ROLE_ADMIN"})
public void userOrAdminOperation() { ... }
```

**When to use @Secured vs @PreAuthorize:**

| Annotation | SpEL support | Parameter access | Complexity | Use when |
|------------|-------------|-----------------|------------|---------|
| `@PreAuthorize` | ✅ Yes | ✅ `#param` | Higher | Most cases — flexible and readable |
| `@Secured` | ❌ No | ❌ No | Lower | Simple role-only checks in older codebases |
| `@PostAuthorize` | ✅ Yes | ✅ returnObject | Higher | Post-execution checks on return value |

**Recommendation:** Use `@PreAuthorize` for new code. It's more capable and explicit.

---

### Slide 6 — SecurityContextHolder: Accessing the Current User

**Title:** SecurityContextHolder — The Thread-Local Authentication Store

**Content:**

`SecurityContextHolder` holds a `SecurityContext` per thread (thread-local storage). The `JwtAuthenticationFilter` wrote to it at the start of the request. Anywhere in your service or controller code during that request's thread, you can read from it.

**In a Spring service — accessing the username:**
```java
@Service
public class BookService {

    public BookDto createBook(CreateBookRequest request) {
        // Get the current user's username from the SecurityContext
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = auth.getName();

        // Use it to set ownership
        User user = userRepository.findByUsername(currentUsername).orElseThrow();
        Book book = new Book(request.title(), request.author(), user);
        return toDto(bookRepository.save(book));
    }
}
```

**In a Spring controller — cleaner with @AuthenticationPrincipal:**
```java
@RestController
@RequestMapping("/api/books")
public class BookController {

    @PostMapping
    public ResponseEntity<BookDto> createBook(
            @RequestBody @Valid CreateBookRequest request,
            @AuthenticationPrincipal UserDetails currentUser) {   // Spring injects this automatically

        return ResponseEntity.status(HttpStatus.CREATED)
            .body(bookService.createBook(request, currentUser.getUsername()));
    }

    @GetMapping("/my-books")
    public List<BookDto> getMyBooks(@AuthenticationPrincipal UserDetails currentUser) {
        return bookService.getBooksByOwner(currentUser.getUsername());
    }
}
```

**`@AuthenticationPrincipal`** is a controller-specific shortcut that pulls the principal from `SecurityContextHolder.getContext().getAuthentication().getPrincipal()` and injects it as a parameter. Cleaner than calling `SecurityContextHolder` directly in controllers.

**SecurityContextHolder propagation note:** By default, Spring Security copies the `SecurityContext` to child threads via `DelegatingSecurityContextExecutor`. Be careful with `@Async` methods — the async executor may run on a different thread that doesn't inherit the context unless configured.

---

### Slide 7 — Custom Authorization Logic

**Title:** Ownership Checks — Authorization Beyond Simple Role Matching

**Content:**

**The ownership problem:** `@PreAuthorize("hasRole('ADMIN')")` is easy. But what about "users can only update their own books, not other users' books"? You can't express that with a role check alone.

**Solution 1 — SpEL with a service bean call:**
```java
// A dedicated security service for complex authorization logic
@Service
public class BookSecurityService {

    private final BookRepository bookRepository;

    // Returns true if the given username is the owner of the given book
    public boolean isOwner(String username, Long bookId) {
        return bookRepository.findById(bookId)
            .map(book -> book.getOwner().getUsername().equals(username))
            .orElse(false);
    }
}

// In BookService or BookController — @bean reference syntax in SpEL
@PreAuthorize("hasRole('ADMIN') or @bookSecurityService.isOwner(authentication.name, #bookId)")
public BookDto updateBook(Long bookId, UpdateBookRequest request) {
    // Only executed if the user is ADMIN or owns this book
    ...
}
```

**Solution 2 — Check inside the method:**
```java
public BookDto updateBook(Long bookId, UpdateBookRequest request) {
    Book book = bookRepository.findById(bookId).orElseThrow();
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();

    boolean isAdmin = auth.getAuthorities().stream()
        .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    boolean isOwner = book.getOwner().getUsername().equals(auth.getName());

    if (!isAdmin && !isOwner) {
        throw new AccessDeniedException("You don't own this book");
    }
    // proceed with update
}
```

**Preference:** Solution 1 (SpEL + security service) is more testable and declarative. Solution 2 is simpler when the check is very specific to that method and not reused.

---

### Slide 8 — AuthenticationEntryPoint: Custom 401 JSON Response

**Title:** Handling Authentication Failure — JSON 401 Instead of HTML Redirect

**Content:**

**The problem:** Spring Security's default behavior when an unauthenticated request hits a protected endpoint is to redirect to a login page (HTTP 302 with HTML body). REST API clients don't want an HTML redirect — they want a JSON 401.

**`AuthenticationEntryPoint`** is called when authentication fails:

```java
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    public JwtAuthenticationEntryPoint(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException)
            throws IOException {

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("status", 401);
        body.put("error", "Unauthorized");
        body.put("message", authException.getMessage());
        body.put("path", request.getServletPath());

        objectMapper.writeValue(response.getOutputStream(), body);
    }
}
```

**When is `AuthenticationEntryPoint` triggered?**
- Request hits a `authenticated()`-protected endpoint with no JWT
- JWT is present but fails validation (expired, malformed, bad signature)
- Note: the filter doesn't set the `SecurityContext` if validation fails, so the request reaches Spring Security's authorization check unauthenticated

---

### Slide 9 — AccessDeniedHandler: Custom 403 JSON Response

**Title:** Handling Authorization Failure — JSON 403 for Authenticated but Unauthorized Users

**Content:**

**The distinction — 401 vs 403:**

| Status | Meaning | Cause |
|--------|---------|-------|
| **401 Unauthorized** | You are not authenticated | No token, or token is invalid |
| **403 Forbidden** | You are authenticated but not authorized | Valid token, but your roles don't grant access |

This distinction is important and commonly confused (and ironically, the HTTP spec's naming is misleading — 401 "Unauthorized" actually means "unauthenticated"). Always return 401 for "who are you?" and 403 for "you're logged in but can't do this."

```java
@Component
public class JwtAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper;

    public JwtAccessDeniedHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException accessDeniedException)
            throws IOException {

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_FORBIDDEN); // 403

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("status", 403);
        body.put("error", "Forbidden");
        body.put("message", "You don't have permission to access this resource");
        body.put("path", request.getServletPath());

        objectMapper.writeValue(response.getOutputStream(), body);
    }
}
```

---

### Slide 10 — Wiring Exception Handlers into SecurityConfig

**Title:** Connecting Entry Point and Access Denied Handler to the Security Chain

**Content:**

```java
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final UserDetailsService userDetailsService;
    private final JwtAuthenticationEntryPoint authEntryPoint;    // ← new
    private final JwtAccessDeniedHandler accessDeniedHandler;    // ← new

    // Constructor injection...

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/books/**").permitAll()
                .requestMatchers(HttpMethod.DELETE, "/api/books/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint(authEntryPoint)         // 401 handler
                .accessDeniedHandler(accessDeniedHandler)         // 403 handler
            )
            .authenticationProvider(authenticationProvider())
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // ... authenticationProvider(), authenticationManager(), passwordEncoder() ...
}
```

**Response examples after wiring:**

Unauthenticated request:
```json
{ "status": 401, "error": "Unauthorized", "message": "Full authentication is required", "path": "/api/books/1" }
```

Authenticated but insufficient role:
```json
{ "status": 403, "error": "Forbidden", "message": "You don't have permission to access this resource", "path": "/api/books/1" }
```

---

### Slide 11 — REST API Security Best Practices

**Title:** Hardening Your REST API — Beyond Authentication

**Content:**

**1. Use HTTPS everywhere — never HTTP in production**

All traffic must be encrypted. Even if your token is httpOnly and immune to XSS, an unencrypted HTTP request exposes the token in plaintext to any network observer.

**2. Never put sensitive data in URLs or query parameters**

```
❌ GET /api/users?token=eyJhbGci...       (token in URL → logged in access logs, browser history)
✅ GET /api/users  Authorization: Bearer eyJhbGci...   (token in header)

❌ GET /api/reset-password?token=abc123   (use POST with body)
✅ POST /api/reset-password  Body: { "token": "abc123", "newPassword": "..." }
```

**3. Return meaningful error messages without exposing internals**

```
❌ { "error": "org.springframework.dao.DataIntegrityViolationException: constraint violations on table users" }
✅ { "error": "Bad Request", "message": "Username already taken" }
```

**4. Validate all input — authenticated users can also be attackers**

Don't skip input validation because the user has a valid JWT. SQL injection, XSS payloads, and business logic exploits can come from authenticated users.

**5. Version your API**

```
/api/v1/books       ← version in path
/api/v2/books
```

Allows security changes and breaking changes to be deployed in new versions without breaking existing clients.

**6. Use security headers (covered Day 29 — OWASP)**
- `X-Content-Type-Options: nosniff`
- `X-Frame-Options: DENY`
- `Strict-Transport-Security: max-age=31536000`

**7. CORS — a brief mention**

Configure CORS explicitly rather than relying on defaults. CORS controls which origins can make requests from a browser. For REST APIs consumed by mobile or server-to-server clients, CORS doesn't apply — only browser-based clients. Full CORS configuration is covered in Week 9's API security review.

---

### Slide 12 — HTTPS Configuration in Spring Boot

**Title:** Configuring HTTPS — SSL/TLS for Spring Boot

**Content:**

**Development — self-signed certificate:**
```bash
# Generate a self-signed certificate (keystore.p12)
keytool -genkeypair -alias bookstore \
  -keyalg RSA -keysize 2048 \
  -storetype PKCS12 \
  -keystore keystore.p12 \
  -validity 365
```

```properties
# application.properties
server.ssl.key-store=classpath:keystore.p12
server.ssl.key-store-password=${SSL_KEYSTORE_PASSWORD}
server.ssl.key-store-type=PKCS12
server.ssl.key-alias=bookstore
server.port=8443
```

**Production — terminate SSL at the load balancer or reverse proxy (recommended):**

```
Internet → [Load Balancer / Nginx with TLS] → [Spring Boot HTTP on port 8080]
                ↑ HTTPS (port 443)                ↑ Internal HTTP (within VPC only)
```

Benefits: No keystore management in the app, automatic certificate renewal (Let's Encrypt via Certbot), performance offloading.

**Force HTTPS redirect (if handling SSL in Spring Boot):**
```java
// In SecurityConfig
http.requiresChannel(channel -> channel
    .anyRequest().requiresSecure()); // redirect HTTP → HTTPS
```

**Spring Boot minimum TLS version (Spring Boot 3 default: TLS 1.2+):**
```properties
server.ssl.enabled-protocols=TLSv1.3
server.ssl.ciphers=TLS_AES_256_GCM_SHA384,TLS_CHACHA20_POLY1305_SHA256
```

---

### Slide 13 — API Key Authentication

**Title:** API Keys — Machine-to-Machine Authentication

**Content:**

**When to use API keys instead of JWT:**

| Scenario | Recommended |
|----------|-------------|
| User authenticates from browser/mobile | JWT (user identity, short-lived) |
| Service A calls Service B (no user context) | API Key or mTLS |
| Third-party developer accessing your API | API Key |
| Webhook caller from external service | API Key |

**API key implementation pattern:**

```java
@Component
public class ApiKeyAuthFilter extends OncePerRequestFilter {

    private final ApiKeyRepository apiKeyRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String apiKey = request.getHeader("X-API-Key");

        if (apiKey != null) {
            // Hash the received key to compare with stored hash
            // (store hash, not plaintext, in the database — like passwords)
            String hashedKey = hashApiKey(apiKey);
            ApiKeyEntity keyEntity = apiKeyRepository.findByKeyHash(hashedKey)
                .orElse(null);

            if (keyEntity != null && !keyEntity.isRevoked()) {
                // Create service-level authentication
                UsernamePasswordAuthenticationToken auth =
                    new UsernamePasswordAuthenticationToken(
                        keyEntity.getServiceName(),
                        null,
                        keyEntity.getAuthorities()
                    );
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        }

        filterChain.doFilter(request, response);
    }

    private String hashApiKey(String rawKey) {
        // SHA-256 hash — not BCrypt (API keys are long random strings, not passwords)
        // BCrypt is too slow for every request; SHA-256 is sufficient for random keys
        return DigestUtils.sha256Hex(rawKey);
    }
}
```

**API Key best practices:**
- Generate with `UUID.randomUUID()` or `SecureRandom` — long and random
- Store only the hash in the database — never the plaintext key
- Show the full key only once at creation — like GitHub personal access tokens
- Associate with a service name, creation date, last-used date, and optional expiry
- Rate limit per API key (slide 14)

---

### Slide 14 — Rate Limiting and Throttling

**Title:** Rate Limiting — Protecting APIs from Abuse

**Content:**

**Why rate limit?**
- Prevent denial of service from a single client overwhelming the server
- Prevent brute-force attacks on login endpoints
- Enforce fair use in shared APIs
- Control costs when upstream calls have a per-request price

**Common algorithms:**

| Algorithm | Description | Pros | Cons |
|-----------|-------------|------|------|
| **Fixed Window** | Allow N requests per minute window; counter resets every minute | Simple | Burst at window boundary (2N requests in 2 seconds) |
| **Sliding Window** | Track timestamps of recent N requests; drop ones older than window | Smoother | Higher memory use |
| **Token Bucket** | Bucket of N tokens; refills at R tokens/second; each request consumes one | Natural burst allowance | Slightly complex |
| **Leaky Bucket** | Requests enter a queue; processed at a fixed rate | Smooth output | Can introduce latency |

**Bucket4j — Spring Boot implementation:**

```xml
<dependency>
    <groupId>com.bucket4j</groupId>
    <artifactId>bucket4j-core</artifactId>
    <version>8.7.0</version>
</dependency>
```

```java
@Configuration
public class RateLimitConfig {

    // 20 requests per minute per IP — a simple in-memory bucket
    @Bean
    public Bucket createNewBucket() {
        Bandwidth limit = Bandwidth.builder()
            .capacity(20)
            .refillIntervally(20, Duration.ofMinutes(1))
            .build();
        return Bucket.builder().addLimit(limit).build();
    }
}

@Component
public class RateLimitFilter extends OncePerRequestFilter {

    private final Bucket bucket;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        if (bucket.tryConsume(1)) {     // try to consume 1 token
            filterChain.doFilter(request, response);
        } else {
            response.setStatus(429);    // 429 Too Many Requests
            response.setHeader("Retry-After", "60");
            response.getWriter().write("{\"error\":\"Too many requests. Try again in 60 seconds.\"}");
        }
    }
}
```

**Per-user/per-IP rate limiting:** Use a `ConcurrentHashMap<String, Bucket>` keyed by IP or username. A shared bucket like above limits the whole application, not individual users.

**HTTP 429 Too Many Requests** — the correct status code for rate limiting. Include a `Retry-After` header with seconds until the client can retry.

---

### Slide 15 — Day 30 and Week 6 Summary

**Title:** Summary — Your Complete Spring Security Stack

**Content:**

**Day 29 + Day 30 together: a production-ready security layer**

| Layer | What it does | Day |
|-------|-------------|-----|
| `BCryptPasswordEncoder` | Hash passwords before storing | 29 |
| `UserDetailsService` + `DaoAuthenticationProvider` | Load users from DB and validate credentials | 29 |
| `SecurityFilterChain` | Route security decisions for every URL | 29 |
| `JwtService` | Generate and validate signed JWTs | 30 |
| `JwtAuthenticationFilter` | Extract JWT on every request, populate SecurityContext | 30 |
| `@PreAuthorize` / `@Secured` | Method-level role and permission checks | 30 |
| `AuthenticationEntryPoint` | JSON 401 for unauthenticated requests | 30 |
| `AccessDeniedHandler` | JSON 403 for authenticated but unauthorized requests | 30 |
| HTTPS/TLS | Encrypt all traffic | 30 |
| Rate limiting | Prevent abuse | 30 |

**Week 6 complete:** Testing → Spring Security foundations → JWT & RBAC. You have covered the entire Spring Boot security stack.

**Week 7 preview — Day 31:** GraphQL. A completely different API paradigm from REST. Instead of multiple endpoints (`GET /books`, `GET /books/{id}`, `GET /authors/{id}/books`), GraphQL exposes a single endpoint and clients send typed queries specifying exactly what data they need. Spring Boot has first-class GraphQL support via Spring for GraphQL.

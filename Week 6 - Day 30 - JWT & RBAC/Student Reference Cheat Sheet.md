# Day 30 Review — JWT & RBAC
## Quick Reference Guide

---

## 1. Session-Based vs Token-Based Authentication

| Aspect | Session-Based | JWT Token-Based |
|--------|--------------|-----------------|
| Server state | Stores session data in memory/Redis | Stateless — no server state |
| Horizontal scaling | Requires sticky sessions or shared store | Any server validates any token |
| Invalidation | Easy — delete session record | Hard — token valid until expiry |
| DB hit per request | Yes (session lookup) | No (signature verification only) |
| Token size | Small (session ID only) | Larger (full payload in every request) |
| Best for | Traditional web apps | REST APIs, microservices, mobile |

---

## 2. JWT Structure

```
eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9
  └── Header (Base64URL)
.eyJzdWIiOiJhbGljZSIsImlhdCI6MTczNTAwMDAwMCwiZXhwIjoxNzM1MDg2NDAwfQ
  └── Payload / Claims (Base64URL)
.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c
  └── Signature (HMAC-SHA256 of header + "." + payload, using the secret key)
```

**⚠️ Base64URL is encoding, NOT encryption. Anyone can decode the payload. Never store passwords, credit card numbers, or sensitive PII in a JWT.**

Use jwt.io to decode and inspect tokens.

---

## 3. JWT Header Reference

```json
{
  "alg": "HS256",
  "typ": "JWT"
}
```

| Algorithm | Type | Key | Use case |
|-----------|------|-----|---------|
| `HS256` | Symmetric HMAC-SHA256 | Shared secret | Single application |
| `HS512` | Symmetric HMAC-SHA512 | Shared secret | Single app, higher security |
| `RS256` | Asymmetric RSA | Private key signs, public verifies | Microservices — multiple verifiers |

---

## 4. Standard JWT Claims (RFC 7519)

| Claim | Full Name | Type | Description |
|-------|-----------|------|-------------|
| `sub` | Subject | String | Who the token is about (username / user ID) |
| `iat` | Issued At | Unix timestamp | When the token was created |
| `exp` | Expiration Time | Unix timestamp | When the token expires — always set this |
| `iss` | Issuer | String | Who issued the token (e.g., `"bookstore-api"`) |
| `jti` | JWT ID | String | Unique identifier for blacklisting |
| `aud` | Audience | String/Array | Intended recipient |
| `nbf` | Not Before | Unix timestamp | Token not valid before this time |

---

## 5. Custom JWT Claims — What to Include / Exclude

✅ **Include:**
- `sub` — username or user ID
- `roles` / `authorities` — list of role strings
- `userId` — database ID for quick lookups

❌ **Exclude:**
- Password (any form)
- Credit card / SSN / PII
- Large data sets (JWTs travel with every request — keep small)

---

## 6. JWT Signature Verification

```
signature = HMACSHA256(
    base64url(header) + "." + base64url(payload),
    secretKey
)
```

**Tamper detection:** If the payload is modified, the server recomputes the signature and it won't match the one in the token → `SignatureException` → 401.

**Secret key requirements for HS256:**
- Minimum 256 bits (32 bytes)
- Store as environment variable — never hardcode
- Generate: `openssl rand -base64 32`
- JJWT throws `WeakKeyException` if key is too short

---

## 7. JWT Authentication Flow

```
1. POST /api/auth/login  { username, password }
2. Server validates credentials via AuthenticationManager
3. Server builds JWT: sub=username, iat=now, exp=now+expiry, roles=[...]
4. Server signs JWT with secret key
5. Server returns { "token": "eyJhbGci..." }
6. Client stores token (localStorage or httpOnly cookie)

Per subsequent request:
7. Client sends:  Authorization: Bearer eyJhbGci...
8. JwtAuthenticationFilter extracts token from header
9. Validates signature and expiry
10. Loads UserDetails, sets SecurityContext
11. Authorization check (@PreAuthorize / requestMatchers)
12. Controller handles request
```

---

## 8. Maven Dependencies — JJWT

```xml
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-api</artifactId>
    <version>0.12.5</version>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-impl</artifactId>
    <version>0.12.5</version>
    <scope>runtime</scope>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-jackson</artifactId>
    <version>0.12.5</version>
    <scope>runtime</scope>
</dependency>
```

```properties
# application.properties
jwt.secret=${JWT_SECRET}
jwt.expiration=86400000
```

---

## 9. JwtService — generateToken()

```java
@Component
public class JwtService {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration}")
    private long expirationMs;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));
    }

    public String generateToken(UserDetails userDetails) {
        return generateToken(Map.of(), userDetails);
    }

    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return Jwts.builder()
            .claims(extraClaims)
            .subject(userDetails.getUsername())
            .issuedAt(new Date())
            .expiration(new Date(System.currentTimeMillis() + expirationMs))
            .claim("roles", userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList()))
            .signWith(getSigningKey())
            .compact();
    }
}
```

---

## 10. JwtService — isTokenValid() and extractClaims()

```java
public boolean isTokenValid(String token, UserDetails userDetails) {
    final String username = extractUsername(token);
    return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
}

public String extractUsername(String token) {
    return extractClaim(token, Claims::getSubject);
}

private boolean isTokenExpired(String token) {
    return extractClaim(token, Claims::getExpiration).before(new Date());
}

public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
    return claimsResolver.apply(extractAllClaims(token));
}

private Claims extractAllClaims(String token) {
    return Jwts.parser()
        .verifyWith(getSigningKey())
        .build()
        .parseSignedClaims(token)
        .getPayload();
}
```

**Exceptions thrown by `parseSignedClaims()`:**
- `SignatureException` — token was tampered with or wrong secret
- `ExpiredJwtException` — past the `exp` time
- `MalformedJwtException` — not valid JWT format

---

## 11. JwtAuthenticationFilter — Complete Implementation

```java
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtService jwtService,
                                   UserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        final String jwt = authHeader.substring(7);
        final String username = jwtService.extractUsername(jwt);

        if (username != null &&
            SecurityContextHolder.getContext().getAuthentication() == null) {

            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            if (jwtService.isTokenValid(jwt, userDetails)) {
                UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                authToken.setDetails(
                    new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        filterChain.doFilter(request, response);
    }
}
```

---

## 12. SecurityFilterChain — JWT Configuration

```java
@Bean
public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
        .csrf(csrf -> csrf.disable())                              // stateless — no CSRF needed
        .sessionManagement(session -> session
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/api/auth/**").permitAll()
            .requestMatchers(HttpMethod.GET, "/api/books/**").permitAll()
            .requestMatchers(HttpMethod.DELETE, "/api/books/**").hasRole("ADMIN")
            .anyRequest().authenticated()
        )
        .exceptionHandling(ex -> ex
            .authenticationEntryPoint(authEntryPoint)
            .accessDeniedHandler(accessDeniedHandler)
        )
        .authenticationProvider(authenticationProvider())
        .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
    return http.build();
}
```

---

## 13. Login Endpoint with AuthenticationManager

```java
@PostMapping("/login")
public ResponseEntity<JwtResponse> login(@RequestBody @Valid LoginRequest request) {
    authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(
            request.getUsername(), request.getPassword()));
    // throws BadCredentialsException if credentials are wrong → Spring returns 401

    UserDetails userDetails = userDetailsService.loadUserByUsername(request.getUsername());
    String token = jwtService.generateToken(userDetails);
    return ResponseEntity.ok(new JwtResponse(token));
}
```

---

## 14. AuthenticationManager Bean

```java
@Bean
public AuthenticationManager authenticationManager(
        AuthenticationConfiguration config) throws Exception {
    return config.getAuthenticationManager();
}
```

Must be declared as a bean in Spring Boot 3 for injection into `AuthController`.

---

## 15. Refresh Token Pattern

```
Access Token:  15 minutes expiry, stored in memory or httpOnly cookie
Refresh Token: 7–30 days expiry, stored server-side (DB) + httpOnly cookie

Flow:
1. Login → server returns access token + refresh token
2. Access token expires → client gets 401
3. Client sends refresh token to POST /api/auth/refresh
4. Server validates refresh token (checks DB + signature)
5. Server issues new access token
6. Repeat until refresh token expires or logout

Logout:
- Delete refresh token from DB
- Access token expires on its own (15-minute window)
```

---

## 16. Token Storage Comparison

| Storage Method | XSS Safe | CSRF Safe | Persists on Refresh | Complexity |
|----------------|----------|----------|----------------------|------------|
| `localStorage` | ❌ | ✅ | ✅ | Low |
| `httpOnly` Cookie | ✅ | ❌ (mitigate with SameSite=Strict) | ✅ | Medium |
| In-memory (JS var) | ✅ | ✅ | ❌ | High |

**Recommendation:** `httpOnly; Secure; SameSite=Strict` cookie for web apps. `Authorization: Bearer` header for mobile / server-to-server.

---

## 17. JWT Invalidation Strategies

| Strategy | Server-Side Storage | Stateless? | Use case |
|----------|--------------------|-----------| ---------|
| Short expiry (15 min) + refresh tokens | Refresh token in DB | Mostly | Default recommendation |
| Token blacklist (jti in Redis) | Blacklisted jtis in Redis with TTL | No | High-security, need immediate revocation |
| Token version in DB | Version number per user | No | Simpler than Redis, one query per request |

---

## 18. RBAC — Roles vs Authorities

```java
// In UserDetails.getAuthorities():
List.of(
    new SimpleGrantedAuthority("ROLE_ADMIN"),    // role — has ROLE_ prefix
    new SimpleGrantedAuthority("BOOK_DELETE"),   // permission — no prefix
    new SimpleGrantedAuthority("USER_MANAGE")    // permission
)
```

- `hasRole("ADMIN")` → checks for `ROLE_ADMIN`
- `hasAuthority("BOOK_DELETE")` → checks for `BOOK_DELETE` exactly

---

## 19. @EnableMethodSecurity

```java
@Configuration
@EnableWebSecurity
@EnableMethodSecurity           // Spring Security 6 — enables @PreAuthorize + @PostAuthorize
// @EnableMethodSecurity(securedEnabled = true)   // to also enable @Secured
public class SecurityConfig { ... }
```

Spring Security 5 equivalent: `@EnableGlobalMethodSecurity(prePostEnabled = true)`

---

## 20. @PreAuthorize Reference

```java
// Role check
@PreAuthorize("hasRole('ADMIN')")
void deleteBook(Long id);

// Multiple roles
@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
List<BookDto> getAllBooks();

// Fine-grained authority
@PreAuthorize("hasAuthority('BOOK_DELETE')")
void deleteBook(Long id);

// Ownership — #param refers to method parameter name
@PreAuthorize("authentication.name == #username")
UserDto getUserProfile(String username);

// Role OR ownership
@PreAuthorize("hasRole('ADMIN') or authentication.name == #username")
UserDto getUserProfile(String username);

// Bean method call for complex checks
@PreAuthorize("@bookSecurityService.isOwner(authentication.name, #bookId)")
BookDto updateBook(Long bookId, UpdateBookRequest request);

// Must be logged in (any role)
@PreAuthorize("isAuthenticated()")
UserDto getCurrentUser();
```

---

## 21. @PostAuthorize Reference

```java
// Runs AFTER the method — checks return value
@PostAuthorize("returnObject.username == authentication.name or hasRole('ADMIN')")
UserDto getUserById(Long id);
// ⚠️ Method body executes regardless — avoid @PostAuthorize on write operations
```

---

## 22. @Secured Reference

```java
// Must enable: @EnableMethodSecurity(securedEnabled = true)

@Secured("ROLE_ADMIN")
void adminOperation();

@Secured({"ROLE_USER", "ROLE_ADMIN"})
void userOrAdminOperation();

// @Secured: no SpEL, role strings only, good for simple role-only checks in legacy code
// Prefer @PreAuthorize for new code
```

---

## 23. SpEL Expressions Available in @PreAuthorize

| Variable | Type | Description |
|----------|------|-------------|
| `authentication` | `Authentication` | Current authentication object |
| `authentication.name` | `String` | Current username |
| `authentication.authorities` | `Collection<GrantedAuthority>` | Current user's roles/permissions |
| `authentication.principal` | `Object` | Principal object (usually `UserDetails`) |
| `#paramName` | matches param type | Method parameter by name |
| `returnObject` | matches return type | Return value (only in @PostAuthorize) |
| `@beanName` | Spring bean | Reference to any Spring bean |
| `hasRole('X')` | `boolean` | True if user has `ROLE_X` |
| `hasAuthority('X')` | `boolean` | True if user has authority `X` exactly |
| `isAuthenticated()` | `boolean` | True if user is logged in |
| `isAnonymous()` | `boolean` | True if not logged in |

---

## 24. URL-Based Security with requestMatchers

```java
.authorizeHttpRequests(auth -> auth
    .requestMatchers("/api/auth/**").permitAll()
    .requestMatchers(HttpMethod.GET, "/api/books/**").permitAll()
    .requestMatchers(HttpMethod.POST, "/api/books/**").hasRole("ADMIN")
    .requestMatchers(HttpMethod.DELETE, "/api/books/**").hasRole("ADMIN")
    .requestMatchers("/api/admin/**").hasRole("ADMIN")
    .requestMatchers("/api/reports/**").hasAnyRole("ADMIN", "MANAGER")
    .requestMatchers(HttpMethod.DELETE, "/api/users/**").hasAuthority("USER_DELETE")
    .anyRequest().authenticated()          // catch-all — always last
)
```

**Rules are evaluated top-to-bottom. First match wins. `anyRequest()` must be last.**

**Spring Security 6 note:** `antMatchers` was removed in Spring Security 6 / Spring Boot 3. Use `requestMatchers` — identical syntax.

---

## 25. SecurityContextHolder — Accessing Current User

```java
// In a service:
Authentication auth = SecurityContextHolder.getContext().getAuthentication();
String username = auth.getName();
Collection<? extends GrantedAuthority> authorities = auth.getAuthorities();
Object principal = auth.getPrincipal();   // usually UserDetails
```

---

## 26. @AuthenticationPrincipal in Controllers

```java
// Inject current user as a method parameter — cleaner than SecurityContextHolder
@GetMapping("/my-books")
public List<BookDto> getMyBooks(@AuthenticationPrincipal UserDetails currentUser) {
    return bookService.getBooksByOwner(currentUser.getUsername());
}

@PostMapping
public ResponseEntity<BookDto> createBook(
        @RequestBody @Valid CreateBookRequest request,
        @AuthenticationPrincipal UserDetails currentUser) {
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(bookService.createBook(request, currentUser.getUsername()));
}
```

---

## 27. Custom Ownership Authorization Service

```java
@Service
public class BookSecurityService {

    private final BookRepository bookRepository;

    public boolean isOwner(String username, Long bookId) {
        return bookRepository.findById(bookId)
            .map(book -> book.getOwner().getUsername().equals(username))
            .orElse(false);
    }
}

// Usage in @PreAuthorize:
@PreAuthorize("hasRole('ADMIN') or @bookSecurityService.isOwner(authentication.name, #bookId)")
public BookDto updateBook(Long bookId, UpdateBookRequest request) { ... }
```

---

## 28. AuthenticationEntryPoint — Custom 401 JSON Response

```java
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("status", 401);
        body.put("error", "Unauthorized");
        body.put("message", authException.getMessage());
        body.put("path", request.getServletPath());

        objectMapper.writeValue(response.getOutputStream(), body);
    }
}
```

Triggered when: no JWT, expired JWT, invalid signature, malformed JWT.

---

## 29. AccessDeniedHandler — Custom 403 JSON Response

```java
@Component
public class JwtAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper;

    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("status", 403);
        body.put("error", "Forbidden");
        body.put("message", "You don't have permission to access this resource");
        body.put("path", request.getServletPath());

        objectMapper.writeValue(response.getOutputStream(), body);
    }
}
```

Triggered when: valid JWT, but user's roles fail the authorization check.

---

## 30. 401 vs 403 — The Critical Distinction

| Status | HTTP Name | Actual Meaning | When to use |
|--------|-----------|----------------|------------|
| **401** | Unauthorized | **Not authenticated** — "Who are you?" | No token, expired token, invalid signature |
| **403** | Forbidden | **Not authorized** — "You can't do this" | Valid token, but insufficient role |

Note: The HTTP spec's naming is confusing — 401 is called "Unauthorized" but means "unauthenticated." Know this distinction and apply it correctly.

---

## 31. REST API Security Best Practices Checklist

- [ ] **HTTPS everywhere** — no HTTP in production
- [ ] **Tokens in headers** — not in URLs or query parameters
- [ ] **Meaningful error messages** — without implementation details (no stack traces, no DB schema)
- [ ] **Validate all inputs** — even from authenticated users
- [ ] **API versioning** — `/api/v1/`, `/api/v2/` for controlled change
- [ ] **Short-lived tokens** — 15-minute access tokens + refresh token pattern
- [ ] **Secure secret storage** — `jwt.secret` from environment variable, not source code
- [ ] **Security headers** — `X-Content-Type-Options`, `X-Frame-Options`, `HSTS`
- [ ] **Proper 401/403 distinction** — don't swap them
- [ ] **JSON error responses** — not HTML redirects (AuthenticationEntryPoint + AccessDeniedHandler)
- [ ] **Rate limiting** — on login endpoint and sensitive operations

---

## 32. CORS — Brief Configuration Reference

```java
// In SecurityConfig — explicit CORS configuration
@Bean
public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration config = new CorsConfiguration();
    config.setAllowedOrigins(List.of("https://your-frontend.com"));
    config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
    config.setAllowedHeaders(List.of("Authorization", "Content-Type"));
    config.setAllowCredentials(true);

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", config);
    return source;
}

// In filterChain:
http.cors(cors -> cors.configurationSource(corsConfigurationSource()))
```

Full CORS configuration is covered in Week 9 API security review.

---

## 33. API Key Authentication Pattern

```java
@Component
public class ApiKeyAuthFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, ...) {
        String apiKey = request.getHeader("X-API-Key");

        if (apiKey != null) {
            String hashedKey = DigestUtils.sha256Hex(apiKey);   // SHA-256, not BCrypt
            apiKeyRepository.findByKeyHash(hashedKey).ifPresent(keyEntity -> {
                if (!keyEntity.isRevoked()) {
                    UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(
                            keyEntity.getServiceName(), null, keyEntity.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(auth);
                }
            });
        }
        filterChain.doFilter(request, response);
    }
}
```

**Best practices:**
- Store SHA-256 hash in DB, never plaintext
- Show raw key only once at creation
- Associate with service name, creation date, expiry
- Rate limit per API key

---

## 34. Rate Limiting Algorithms

| Algorithm | Behavior | Allows Burst? | Memory |
|-----------|----------|--------------|--------|
| **Fixed Window** | Counter resets every minute | Yes — 2N requests at boundary | Low |
| **Sliding Window** | Track timestamps of recent N requests | No | Higher |
| **Token Bucket** | Bucket refills at rate R; each request consumes 1 token | Yes — up to bucket capacity | Low |
| **Leaky Bucket** | Queue processed at fixed rate | Queued (no burst) | Queue size |

**Correct response:** `HTTP 429 Too Many Requests` + `Retry-After: 60` (seconds)

---

## 35. Bucket4j Rate Limiting Example

```xml
<dependency>
    <groupId>com.bucket4j</groupId>
    <artifactId>bucket4j-core</artifactId>
    <version>8.7.0</version>
</dependency>
```

```java
// Create bucket: 20 requests per minute
Bucket bucket = Bucket.builder()
    .addLimit(Bandwidth.builder()
        .capacity(20)
        .refillIntervally(20, Duration.ofMinutes(1))
        .build())
    .build();

// In filter:
if (bucket.tryConsume(1)) {
    filterChain.doFilter(request, response);
} else {
    response.setStatus(429);
    response.setHeader("Retry-After", "60");
    response.getWriter().write("{\"error\":\"Too many requests\"}");
}
```

**Per-user rate limiting:** Use `ConcurrentHashMap<String, Bucket>` keyed by username or IP.

---

## 36. Complete Security Stack Summary

| Component | Responsibility | Day |
|-----------|---------------|-----|
| `BCryptPasswordEncoder` | Hash passwords before storing | 29 |
| `UserDetailsService` | Load users from DB | 29 |
| `DaoAuthenticationProvider` | Validate credentials (delegates to above two) | 29 |
| `SecurityFilterChain` | Configure URL access rules and filter chain | 29 |
| `JwtService` | Generate and validate signed JWTs | 30 |
| `JwtAuthenticationFilter` | Extract JWT, populate SecurityContext | 30 |
| `@EnableMethodSecurity` | Activate method-level security annotations | 30 |
| `@PreAuthorize` | Declarative authorization on service methods | 30 |
| `AuthenticationEntryPoint` | JSON 401 for unauthenticated requests | 30 |
| `AccessDeniedHandler` | JSON 403 for authenticated-but-unauthorized | 30 |
| HTTPS/TLS | Encrypt all network traffic | 30 |
| API Key filter | Machine-to-machine authentication | 30 |
| Rate limiting filter | Prevent abuse, enforce fair use | 30 |

---

## 37. Common JWT Mistakes

| Mistake | Consequence | Fix |
|---------|-------------|-----|
| Hardcoding `jwt.secret` in source | Secret exposed in version control | Use `${JWT_SECRET}` environment variable |
| Using a short secret key | JJWT `WeakKeyException`, or easy brute-force | Minimum 32 bytes; use `openssl rand -base64 32` |
| Not setting `exp` claim | Token never expires — stolen tokens work forever | Always set `.expiration()` |
| Storing sensitive PII in payload | Anyone with the token can read it (Base64 only) | Put only username, roles, and non-sensitive IDs |
| Using `localStorage` for token | XSS attack steals all tokens | Use `httpOnly; SameSite=Strict` cookie for web apps |
| Returning HTML redirect for 401 on REST | REST clients can't handle HTML redirect | Implement `AuthenticationEntryPoint` |
| Long-lived tokens (7+ days) | Stolen token has long window | Short access tokens + refresh token pattern |
| Missing `@EnableMethodSecurity` | `@PreAuthorize` is silently ignored | Add `@EnableMethodSecurity` to config class |
| Using `antMatchers` in Spring Boot 3 | `NoSuchMethodError` — method removed | Use `requestMatchers` |
| Swapping 401 and 403 | Wrong semantics; misleads clients | 401 = not authenticated; 403 = not authorized |

---

## 38. Looking Ahead — Day 31: GraphQL

**Week 7 begins with GraphQL.**

REST: multiple endpoints, each returning a fixed shape of data.
```
GET /api/books         → array of books
GET /api/books/1       → one book
GET /api/authors/5/books → books by author
```

GraphQL: single endpoint, clients send typed queries for exactly the data they need:
```graphql
query {
  book(id: 1) {
    title
    author {
      name
      otherBooks { title }
    }
    reviews { rating text }
  }
}
```

Spring Boot support via **Spring for GraphQL** (spring-boot-starter-graphql). Topics Day 31: schema definition language (SDL), `@QueryMapping`, `@MutationMapping`, `@SchemaMapping`, N+1 problem, DataLoader pattern.

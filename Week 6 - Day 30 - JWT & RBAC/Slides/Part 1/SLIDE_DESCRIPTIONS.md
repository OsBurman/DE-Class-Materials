# Day 30 Part 1 — JWT: Structure, Generation, and the Custom Filter
## Slide Descriptions

---

### Slide 1 — Title Slide

**Title:** Day 30 Part 1: JSON Web Tokens — How Stateless Authentication Works

**Subtitle:** From session cookies to self-contained, portable identity tokens

**Learning Objectives:**
- Explain the structural difference between token-based and session-based authentication
- Decode a JWT and identify its three parts: header, payload, and signature
- Name the standard registered claims (sub, iat, exp, iss) and explain what each means
- Implement `JwtService` for token generation and validation using the JJWT library
- Build a `JwtAuthenticationFilter` extending `OncePerRequestFilter`
- Wire the filter into `SecurityFilterChain` for stateless JWT authentication
- Configure a secure JWT secret key and expiration strategy
- Understand the token storage tradeoffs between `localStorage` and `httpOnly` cookies

---

### Slide 2 — Session-Based vs Token-Based Authentication

**Title:** Two Models of Authentication — Stateful Sessions vs Stateless Tokens

**Content:**

**Session-Based (Day 29's model):**

```
Client                                  Server
  |                                       |
  |── POST /login (user + pass) ─────────>|
  |                                       |── validates credentials
  |                                       |── creates session: {id: "abc", user: "alice"} → DB/Memory
  |<── Set-Cookie: JSESSIONID=abc ────────|
  |                                       |
  |── GET /api/orders (cookie sent auto)─>|
  |                                       |── looks up session "abc" in store → finds alice
  |<── 200 OK ────────────────────────────|
```

**Token-Based (JWT):**

```
Client                                  Server
  |                                       |
  |── POST /auth/login (user + pass) ────>|
  |                                       |── validates credentials
  |                                       |── creates JWT (signed with secret) → returned
  |<── { "token": "eyJhbG..." } ──────────|
  |── stores token (localStorage / cookie)|
  |                                       |
  |── GET /api/orders ──────────────────>|
  |   Authorization: Bearer eyJhbG...     |
  |                                       |── validates signature, reads claims
  |                                       |── NO database/session store lookup needed
  |<── 200 OK ────────────────────────────|
```

**Comparison:**

| Aspect | Session-Based | JWT (Token-Based) |
|--------|--------------|-------------------|
| **Server state** | Server stores session data | Server stores nothing — token is self-contained |
| **Horizontal scaling** | Requires sticky sessions or shared session store (Redis) | Stateless — any server validates any token |
| **Invalidation** | Easy — delete the session record | Hard — token is valid until expiry |
| **Database hit per request** | Yes — session lookup | No — signature verification only |
| **Token size** | Small cookie (session ID only) | Larger — entire payload travels with every request |
| **Best for** | Traditional web apps, real-time invalidation needed | REST APIs, microservices, mobile clients |

---

### Slide 3 — JWT Structure: The Three Parts

**Title:** JWT Anatomy — Header.Payload.Signature

**Content:**

A JWT is three Base64URL-encoded JSON objects joined by dots:

```
eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9
.eyJzdWIiOiJhbGljZSIsImlhdCI6MTczNTAwMDAwMCwiZXhwIjoxNzM1MDg2NDAwLCJyb2xlcyI6WyJST0xFX1VTRVIiXX0
.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c

│◄── Header ──►│◄──────────────── Payload ─────────────────►│◄── Signature ──►│
```

**Decoded Header:**
```json
{
  "alg": "HS256",
  "typ": "JWT"
}
```
- `alg` — signing algorithm: `HS256` (HMAC-SHA256, symmetric), `RS256` (RSA, asymmetric)
- `typ` — always `JWT`

**Decoded Payload:**
```json
{
  "sub": "alice",
  "iat": 1735000000,
  "exp": 1735086400,
  "roles": ["ROLE_USER"]
}
```

**Signature:**
```
HMACSHA256(
  base64UrlEncode(header) + "." + base64UrlEncode(payload),
  secret
)
```

**Critical security property:** Base64URL encoding is NOT encryption. Anyone can decode the header and payload. Do not put sensitive data (passwords, credit card numbers) in the JWT payload. The signature only proves the token was issued by someone who knows the secret — it does not hide the contents.

---

### Slide 4 — JWT Claims Reference

**Title:** Claims — The Payload's Vocabulary

**Content:**

Claims are key-value pairs in the payload. Three categories:

**Registered Claims (standardized by RFC 7519):**

| Claim | Full Name | Type | Purpose |
|-------|-----------|------|---------|
| `sub` | Subject | String | Who the token is about — usually username or user ID |
| `iat` | Issued At | Unix timestamp | When the token was created |
| `exp` | Expiration Time | Unix timestamp | When the token expires — server rejects tokens past this time |
| `iss` | Issuer | String | Who issued the token — e.g., `"bookstore-api"` |
| `jti` | JWT ID | String | Unique identifier for this token — used for token blacklisting |
| `aud` | Audience | String/Array | Intended recipient — e.g., `"bookstore-client"` |
| `nbf` | Not Before | Unix timestamp | Token is invalid before this time |

**Custom Claims (your application-specific data):**
```json
{
  "sub": "alice",
  "iat": 1735000000,
  "exp": 1735086400,
  "iss": "bookstore-api",
  "roles": ["ROLE_USER", "ROLE_ADMIN"],
  "userId": 42
}
```

**What to include in JWT claims:**
- ✅ Username / user ID (`sub`)
- ✅ Roles / authorities
- ✅ Expiration (`exp`) — always
- ✅ Issued at (`iat`) — for age-checking logic
- ❌ Password (never)
- ❌ Sensitive PII (credit card, SSN)
- ❌ Large data sets — JWT travels with every request; keep it small

---

### Slide 5 — JWT Signature and Why It Matters

**Title:** The Signature — Proof of Authenticity, Not Secrecy

**Content:**

**How HMAC-SHA256 signing works:**
```
secret = "my-256-bit-secret-stored-in-env-variable"

signature = HMACSHA256(
    base64url(header) + "." + base64url(payload),
    secret
)
```

The server computes this signature when issuing the token. On every subsequent request, it recomputes the signature from the received header and payload, then compares to the signature in the token. If they match, the payload was not tampered with.

**Tamper detection example:**
```
Original token payload: {"sub":"alice","roles":["ROLE_USER"]}
Attacker changes to:    {"sub":"alice","roles":["ROLE_ADMIN"]}

→ The signature no longer matches the modified payload
→ Server rejects the token: SignatureException
→ The attacker cannot forge a valid signature without knowing the secret
```

**Algorithm choice:**

| Algorithm | Type | Key | Use case |
|-----------|------|-----|---------|
| `HS256` | Symmetric | Single shared secret | Single application (same app signs and verifies) |
| `HS512` | Symmetric | Single shared secret | Higher security, same use case |
| `RS256` | Asymmetric | Private key signs, public key verifies | Microservices where multiple services verify but only one issues |

**For a single Spring Boot monolith:** `HS256` or `HS512` with a strong secret is appropriate.

**Secret key requirements for HS256:**
- Must be at least 256 bits (32 bytes)
- Store in environment variable — never hardcode
- Generate: `openssl rand -base64 32`

---

### Slide 6 — JJWT Library Setup

**Title:** Adding JWT Support — JJWT Library

**Content:**

JJWT (JSON Web Token for Java) is the most widely used JWT library for Spring Boot:

```xml
<!-- pom.xml -->
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-api</artifactId>
    <version>0.12.5</version>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-impl</artifactId>
    <version>0.12.5</version>
    <scope>runtime</scope>     <!-- implementation, not compile-time dependency -->
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-jackson</artifactId>
    <version>0.12.5</version>
    <scope>runtime</scope>     <!-- Jackson-based JSON serialization for claims -->
</dependency>
```

**Why three artifacts?**
- `jjwt-api` — the interface/API you compile against
- `jjwt-impl` — the runtime implementation (not a compile-time dependency — prevents accidental direct use of internal APIs)
- `jjwt-jackson` — uses Jackson for JSON serialization of claims

**Application properties:**
```properties
# application.properties
jwt.secret=4e6f742d612d5365637265742d6b65792d666f722d70726f64756374696f6e21
jwt.expiration=86400000
# expiration in milliseconds — 86400000ms = 24 hours
# For shorter-lived tokens: 900000 = 15 minutes
```

**Generate a production-safe secret:**
```bash
# Terminal — generates 32 random bytes, Base64-encoded
openssl rand -base64 32
# Output: something like: 4e6f742d612d53656372...
```

---

### Slide 7 — JwtService: Generating Tokens

**Title:** JwtService — Token Generation

**Content:**

```java
@Component
public class JwtService {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration}")
    private long expirationMs;

    // Build a SecretKey object from the Base64-encoded property value
    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);   // validates key is long enough for HS256
    }

    public String generateToken(UserDetails userDetails) {
        return generateToken(Map.of(), userDetails);
    }

    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return Jwts.builder()
            .claims(extraClaims)                                  // custom claims first
            .subject(userDetails.getUsername())                   // sub claim
            .issuedAt(new Date())                                 // iat claim
            .expiration(new Date(System.currentTimeMillis() + expirationMs))  // exp claim
            .claim("roles", userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList()))                    // custom roles claim
            .signWith(getSigningKey())                            // sign with HS256
            .compact();                                           // serialize to String
    }
}
```

**What `.compact()` returns:**
A String like `eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJhbGljZSI...SflKxw`

**Method signature with extra claims:** passing a `Map<String, Object>` allows callers to include additional claims at generation time (e.g., `Map.of("department", "engineering")`) without changing the core method.

---

### Slide 8 — JwtService: Validating and Extracting Claims

**Title:** JwtService — Token Validation and Claims Extraction

**Content:**

```java
// In JwtService — add these methods:

public String extractUsername(String token) {
    return extractClaim(token, Claims::getSubject);   // extracts "sub" claim
}

public boolean isTokenValid(String token, UserDetails userDetails) {
    final String username = extractUsername(token);
    return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
}

private boolean isTokenExpired(String token) {
    return extractClaim(token, Claims::getExpiration).before(new Date());
}

// Generic claim extractor — pass any Claims method reference
public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
    final Claims claims = extractAllClaims(token);
    return claimsResolver.apply(claims);
}

private Claims extractAllClaims(String token) {
    return Jwts.parser()
        .verifyWith(getSigningKey())        // verify signature using our secret
        .build()
        .parseSignedClaims(token)           // throws if signature invalid or token expired
        .getPayload();                      // returns the Claims object
}
```

**What happens during `parseSignedClaims()`:**
1. Splits the token into header, payload, signature
2. Recomputes the expected signature from header + payload using the secret key
3. Compares computed signature to the signature in the token
4. If mismatch → throws `SignatureException`
5. Checks `exp` claim against current time
6. If expired → throws `ExpiredJwtException`
7. If valid → returns the decoded `Claims` object

**Other extractable standard claims:**
```java
extractClaim(token, Claims::getExpiration)   // Date
extractClaim(token, Claims::getIssuedAt)     // Date
extractClaim(token, Claims::getIssuer)       // String
```

---

### Slide 9 — JwtAuthenticationFilter

**Title:** The Custom Filter — Intercepting Every Request to Extract the JWT

**Content:**

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

        // 1. Extract the Authorization header
        final String authHeader = request.getHeader("Authorization");

        // 2. If no Bearer token, pass through — public endpoints don't need one
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 3. Extract the JWT (strip "Bearer " prefix)
        final String jwt = authHeader.substring(7);

        // 4. Extract username from token (may throw if token is malformed)
        final String username = jwtService.extractUsername(jwt);

        // 5. Only authenticate if: username found AND no existing auth in context
        if (username != null &&
            SecurityContextHolder.getContext().getAuthentication() == null) {

            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            if (jwtService.isTokenValid(jwt, userDetails)) {
                // 6. Create authentication token with roles
                UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,                          // credentials (not needed post-auth)
                        userDetails.getAuthorities()   // roles → used for authorization
                    );
                // 7. Attach request details (IP, session ID for audit logging)
                authToken.setDetails(
                    new WebAuthenticationDetailsSource().buildDetails(request));

                // 8. Set authentication in SecurityContext for this request
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        // 9. Continue to next filter (eventually reaches DispatcherServlet)
        filterChain.doFilter(request, response);
    }
}
```

**Why `OncePerRequestFilter`?** Guarantees the filter runs exactly once per request, even in servlet environments that may call the filter chain multiple times (e.g., during forwarding).

**Why check `getAuthentication() == null`?** Prevents re-authenticating a request that's already been authenticated by a previous filter.

---

### Slide 10 — SecurityConfig for JWT

**Title:** Wiring the JWT Filter into the Security Configuration

**Content:**

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final UserDetailsService userDetailsService;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthFilter,
                          UserDetailsService userDetailsService) {
        this.jwtAuthFilter = jwtAuthFilter;
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // CSRF disabled — stateless JWT API (no session cookies)
            .csrf(csrf -> csrf.disable())

            // No session — every request is independently authenticated
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))

            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**").permitAll()    // login + register
                .anyRequest().authenticated()
            )

            // Register the DaoAuthenticationProvider
            .authenticationProvider(authenticationProvider())

            // JWT filter runs BEFORE the default UsernamePasswordAuthenticationFilter
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    // AuthenticationManager is needed by the login endpoint to validate credentials
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
```

**Why `.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)`?**
The JWT filter must run before Spring Security's default login filter. It populates the `SecurityContext` with the authenticated user so that by the time `FilterSecurityInterceptor` runs its authorization check, the context already has the user's identity and roles.

---

### Slide 11 — Login Endpoint — Issuing the Token

**Title:** The Auth Controller — Validating Credentials and Issuing JWTs

**Content:**

```java
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtService jwtService;

    public AuthController(AuthenticationManager authenticationManager,
                          UserDetailsService userDetailsService,
                          JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.jwtService = jwtService;
    }

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@RequestBody @Valid LoginRequest request) {
        // Delegates to DaoAuthenticationProvider → UserDetailsService → BCrypt.matches()
        // Throws AuthenticationException if credentials are invalid
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                request.getUsername(),
                request.getPassword()
            )
        );

        // If we reach here, authentication succeeded
        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getUsername());
        String token = jwtService.generateToken(userDetails);

        return ResponseEntity.ok(new JwtResponse(token));
    }
}
```

```java
// DTOs
public record LoginRequest(
    @NotBlank String username,
    @NotBlank String password
) {}

public record JwtResponse(String token) {}
```

**Request/Response flow:**
```
POST /api/auth/login
Body: { "username": "alice", "password": "mypassword" }

→ authenticationManager.authenticate() checks credentials
→ jwtService.generateToken() creates signed JWT
→ 200 OK: { "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..." }

All subsequent requests:
GET /api/books   Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

---

### Slide 12 — JWT Secret Key: Configuration and Security

**Title:** Securing the JWT Secret — The One Key That Protects Everything

**Content:**

**If the secret key is compromised:** anyone can generate arbitrary valid JWTs, impersonate any user, and claim any role. This is the most sensitive secret in a JWT-based system.

**Application properties:**
```properties
# application.properties (values come from environment)
jwt.secret=${JWT_SECRET}
jwt.expiration=${JWT_EXPIRATION_MS:86400000}
```

**In CI/CD and production:**
```bash
# Environment variable — never in source code
export JWT_SECRET="$(openssl rand -base64 32)"

# Or in Docker/Kubernetes secrets
kubectl create secret generic jwt-secret --from-literal=JWT_SECRET="$(openssl rand -base64 32)"
```

**Generating a proper key at startup (alternative approach):**
```java
// Generate a guaranteed-sufficient key at startup
@Bean
public SecretKey jwtSecretKey() {
    // This creates a random 256-bit key every restart
    // Use only for development — production needs persistent key across restarts
    return Keys.secretKeyFor(SignatureAlgorithm.HS256);
}
```

**Key rotation strategy:**
- Rotate JWT secret on a schedule (quarterly)
- After rotation, all existing JWTs are invalidated (they were signed with the old key)
- Users must log in again to get a new token
- Short expiration times reduce the window for a compromised key

**Minimum size requirements:**
- HS256 → 256 bits (32 bytes)
- HS384 → 384 bits (48 bytes)
- HS512 → 512 bits (64 bytes)
- JJWT throws `WeakKeyException` if the key is too short

---

### Slide 13 — Token Storage: Where Should the Client Keep the JWT?

**Title:** Token Storage Tradeoffs — localStorage vs httpOnly Cookie vs Memory

**Content:**

After login, the client receives the JWT. Where it stores it has security implications:

| Storage | XSS vulnerable | CSRF vulnerable | Survives page refresh | Complexity |
|---------|---------------|-----------------|-----------------------|------------|
| **localStorage / sessionStorage** | ✅ Yes — any JS on the page can read it | ❌ No — must be added to header manually | ✅ Yes | Low |
| **httpOnly Cookie** | ❌ No — JS cannot access it at all | ✅ Yes — browser sends cookies automatically (mitigated by SameSite) | ✅ Yes | Medium |
| **In-memory (JS variable)** | ❌ No — not accessible from another script | ❌ No | ❌ No — lost on refresh | High |

**Recommended approach for production REST APIs:**
Use an **httpOnly, Secure, SameSite=Strict cookie** to store the JWT:
- Immune to XSS (JS cannot access the cookie)
- CSRF mitigated by `SameSite=Strict` (cookie not sent on cross-site requests)
- Survives page refresh

```
Set-Cookie: token=eyJhbGci...; HttpOnly; Secure; SameSite=Strict; Path=/; Max-Age=86400
```

**If you use localStorage:** you accept XSS risk. Mitigate with a strong Content Security Policy and sanitizing all rendered user content. Acceptable for internal tools, admin panels, or prototypes.

**For this course:** localStorage is acceptable in exercises (simpler code). Understand the tradeoff.

---

### Slide 14 — Token Expiration and the Refresh Token Pattern

**Title:** Token Lifetimes — Short Access Tokens + Refresh Tokens

**Content:**

**The problem with long-lived tokens:** If a JWT is stolen, it's valid until expiry. With a 24-hour expiry, an attacker has 24 hours of access. You can't invalidate it without a server-side blacklist, which partially defeats the statelessness benefit.

**Access Token + Refresh Token pattern:**

| Token type | Expiry | Storage | Used for |
|------------|--------|---------|---------|
| **Access Token** | 15 minutes | Memory or httpOnly cookie | Calling protected API endpoints |
| **Refresh Token** | 7–30 days | httpOnly cookie or DB | Getting a new access token without re-login |

```
Flow:
1. Login → server issues access token (15 min) + refresh token (7 days)
2. Client uses access token on every API call
3. Access token expires → 401 response
4. Client sends refresh token to /api/auth/refresh
5. Server validates refresh token (checks DB or signature)
6. Server issues new access token (15 min)
7. User never has to log in again as long as they're active

Logout:
- Invalidate refresh token in the database
- Access token expires on its own after 15 minutes
```

**Refresh token implementation overview:**
- Refresh tokens are typically stored in the database (can be invalidated)
- One row per user+device in `refresh_tokens` table: `(id, username, token_hash, expiry, device_info)`
- On refresh: verify signature → check DB not invalidated → issue new access token

**For this course:** implement with a single access token for simplicity. The refresh token pattern is the production standard — know it conceptually.

---

### Slide 15 — The JWT Invalidation Problem

**Title:** The One Weakness of Stateless JWTs — You Can't Take Them Back

**Content:**

**The core problem:** A JWT is valid until its `exp` claim passes. There is no built-in mechanism to invalidate a specific token before it expires.

**Scenarios where this hurts:**

| Scenario | Session-based | JWT |
|----------|--------------|-----|
| User logs out | Delete server session → immediate invalidation | Token still valid until expiry |
| User changes password | Invalidate all sessions | Old tokens still work |
| Admin revokes user access | Delete session | User retains access until token expiry |
| Token stolen | Delete session | Attacker has access until expiry |

**Solutions (ordered from simplest to most complex):**

**1. Short expiry (15 min access tokens):**
The stolen token window is small. Combined with refresh tokens stored in DB, you can block refresh without blocking immediate API access. Simple, widely used.

**2. Token blacklist in Redis:**
```java
// On logout: add the token's jti claim to a Redis blacklist with TTL = token's remaining lifetime
// In JWT filter: check if jti is in the blacklist before accepting the token
redisTemplate.opsForValue().set("blacklist:" + jti, "true", remainingTime, TimeUnit.MILLISECONDS);
```

**3. Token version in the database:**
```java
// User table has a tokenVersion column
// JWT includes tokenVersion as a claim
// On every request: compare JWT's tokenVersion to DB — reject if different
// To invalidate all tokens: increment the user's tokenVersion in DB
```

**The practical answer for most applications:** short-lived access tokens (15 min) with httpOnly cookie refresh tokens stored in the database. Accept the small invalidation window for access tokens.

---

### Slide 16 — Part 1 Summary

**Title:** Part 1 Summary — JWT Implementation Reference

**Content:**

**JWT Structure:**
```
eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9    ← Header (alg, typ)
.eyJzdWIiOiJhbGljZSIsImV4cCI6MTczNTA4NjQwMCwicm9sZXMiOlsiUk9MRV9VU0VSIl19   ← Payload
.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c   ← Signature
```

**Implementation checklist:**
- [ ] Add JJWT dependencies (`jjwt-api`, `jjwt-impl`, `jjwt-jackson`)
- [ ] Store `jwt.secret` and `jwt.expiration` in properties (from environment)
- [ ] `JwtService.generateToken(UserDetails)` — builds signed JWT
- [ ] `JwtService.isTokenValid(token, UserDetails)` — verifies signature + expiry
- [ ] `JwtAuthenticationFilter extends OncePerRequestFilter` — extracts JWT from `Authorization: Bearer`
- [ ] `SecurityConfig`: `csrf.disable()`, `STATELESS` session, `addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)`
- [ ] `AuthController.POST /api/auth/login` — uses `AuthenticationManager`, returns JWT in body

**Request format for protected endpoints:**
```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

**Part 2 Preview:**
Role-Based Access Control — `@PreAuthorize` and `@Secured` for method-level security; URL-based rules with `requestMatchers`; custom authorization logic with `SecurityContextHolder`; `AuthenticationEntryPoint` and `AccessDeniedHandler` for clean 401/403 JSON responses; REST API security best practices; HTTPS configuration; API keys; rate limiting.

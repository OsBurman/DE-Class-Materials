# Day 29 — Spring Security & OWASP: Complete Review Guide

---

## 1. Secure Coding Principles

| Principle | Definition | Example |
|-----------|-----------|---------|
| **Least Privilege** | Every component/user/process gets only the access it needs | DB service account has SELECT only, not DROP |
| **Defense in Depth** | Layer security controls; no single point of failure | Validate input AND parameterize queries AND encode output |
| **Fail Securely** | On error, default to deny | Auth exception → deny access, not allow |
| **Don't Trust Input** | All external input is hostile until validated | URL params, form fields, API responses, DB values |
| **Security by Default** | Default config is most restrictive; opt-in to enable | All endpoints locked until explicitly whitelisted |
| **Keep It Simple** | Complexity increases attack surface | Prefer simple, auditable implementations |
| **No Security Through Obscurity** | Hiding code/structure is not a security control | Renaming `/admin` is not authorization |

---

## 2. OWASP Top 10 (2021) Quick Reference

| ID | Category | Key Prevention |
|----|----------|---------------|
| **A01** | Broken Access Control | Server-side auth on every request; deny by default; ownership verification |
| **A02** | Cryptographic Failures | HTTPS + HSTS; BCrypt for passwords; AES-256 at rest; no secrets in source code |
| **A03** | Injection | Parameterized queries; ProcessBuilder with args; input validation |
| **A04** | Insecure Design | Threat modeling at design time; secure design patterns |
| **A05** | Security Misconfiguration | Env-specific config; disable dev features in prod; automated scanning |
| **A06** | Vulnerable Components | OWASP Dependency-Check; keep deps current; know transitive tree |
| **A07** | Auth Failures | Rate limiting; server-side logout; session regeneration on login; MFA |
| **A08** | Software Integrity Failures | Verify supply chain; signed artifacts; dependency pinning |
| **A09** | Logging Failures | Log auth events; never log secrets; structured logging; alert on anomalies |
| **A10** | SSRF | Validate outbound URLs; whitelist allowed hosts; block internal IP ranges |
| XSS | Cross-Site Scripting | Output encoding; CSP header; HttpOnly cookies; no `innerHTML` with user data |
| CSRF | Cross-Site Request Forgery | CSRF tokens; SameSite cookies; disabled for stateless JWT APIs |

---

## 3. A01 — Broken Access Control

**Vertical escalation:** lower-privilege user accesses higher-privilege functionality (user calls admin endpoint).
**Horizontal escalation / IDOR:** user accesses another user's data by modifying a resource ID.

```java
// ❌ Vulnerable — no ownership check
@GetMapping("/api/orders/{id}")
public OrderDto getOrder(@PathVariable Long id) {
    return orderService.findById(id);   // any authenticated user reads any order
}

// ✅ Secure — verify ownership
@GetMapping("/api/orders/{id}")
public OrderDto getOrder(@PathVariable Long id, Authentication auth) {
    OrderDto order = orderService.findById(id);
    if (!order.getUsername().equals(auth.getName())) {
        throw new AccessDeniedException("You do not own this order");
    }
    return order;
}
```

**Prevention:**
- Server-side authorization checks — client-side (hidden button) is not security
- Deny by default — only explicitly permitted actions are allowed
- Use UUIDs instead of sequential integers for sensitive resource IDs (harder to enumerate)

---

## 4. A02 — Cryptographic Failures

**What to protect:** passwords, credit cards, SSNs, health records, API keys, session tokens

**Mistakes:**
- MD5 or SHA-1 for password hashing (fast → brute-forceable)
- HTTP instead of HTTPS (data in transit interceptable)
- Hardcoded secrets in source code
- Retaining sensitive data beyond its use

**Prevention:**
```properties
# Force HTTPS
server.ssl.enabled=true

# HSTS — tell browsers to use HTTPS for 1 year
```
```java
// Never use MD5 for passwords — use BCryptPasswordEncoder
// Never store secrets in code — use environment variables or Vault
String dbPassword = System.getenv("DB_PASSWORD");  // ✅
String dbPassword = "mypassword123";               // ❌
```

---

## 5. A03 — SQL Injection

```java
// ❌ VULNERABLE — user input concatenated into SQL
String query = "SELECT * FROM users WHERE username = '" + username + "'";
// Attacker input: ' OR '1'='1  → returns all rows
// Attacker input: '; DROP TABLE users; --  → destroys table

// ✅ PreparedStatement — parameterized
PreparedStatement stmt = conn.prepareStatement("SELECT * FROM users WHERE username = ?");
stmt.setString(1, username);

// ✅ Spring Data JPA derived query — parameterized automatically
Optional<User> findByUsername(String username);

// ✅ JPQL with @Param — parameterized
@Query("SELECT u FROM User u WHERE u.username = :username")
Optional<User> findByUsername(@Param("username") String username);
```

**Rule:** never concatenate user input into a SQL string. No exceptions.

---

## 6. A03 — Command Injection

```java
// ❌ VULNERABLE — shell interprets metacharacters (; | & > <)
Runtime.getRuntime().exec("ls " + userInput);
// userInput = "docs; rm -rf /" → destroys filesystem

// ✅ Prefer Java APIs
Files.list(Paths.get(directory));

// ✅ If OS command is required — ProcessBuilder with separate args
// Shell is NOT involved; metacharacters are literal
new ProcessBuilder("ls", "-la", userInput).start();

// ❌ Still wrong — concatenated string still goes through shell
new ProcessBuilder("ls -la " + userInput).start();
```

---

## 7. A05 — Security Misconfiguration

**Common examples and fixes:**

| Misconfiguration | Fix |
|-----------------|-----|
| Default credentials (`admin/admin`) | Change immediately on every environment |
| Stack traces in HTTP responses | `server.error.include-stacktrace=never` |
| Actuator `/**` publicly exposed | Require auth: `management.endpoints.web.exposure.include=health,info` |
| H2 console in production | `spring.h2.console.enabled=false` in prod profile |
| CORS `*` | Restrict to known origins: `allowedOrigins("https://myapp.com")` |
| Verbose error messages | `server.error.include-message=never` |

---

## 8. A06 — Vulnerable and Outdated Components

```xml
<!-- OWASP Dependency-Check Maven plugin -->
<plugin>
    <groupId>org.owasp</groupId>
    <artifactId>dependency-check-maven</artifactId>
    <version>9.0.0</version>
</plugin>
```

```bash
mvn dependency:tree             # show full transitive dependency tree
mvn dependency-check:check      # scan against CVE database; fails build if critical CVE found
```

**Process:**
- Know all transitive dependencies (Log4Shell was hidden 3 levels deep)
- Subscribe to security advisories for Spring, Java, and key frameworks
- Pin dependency versions; upgrade regularly

---

## 9. A07 — Identification and Authentication Failures

| Vulnerability | Prevention |
|---------------|------------|
| Weak passwords | Enforce minimum length 12; check against breach lists (Have I Been Pwned) |
| Brute-force attacks | Rate limiting; account lockout after N failures; CAPTCHA |
| Credential stuffing | MFA; breach password detection |
| Session tokens in URLs | Use HttpOnly, Secure cookies only |
| No logout invalidation | Invalidate server-side session on logout |
| Session fixation | Regenerate session ID on successful login |
| No MFA | Require TOTP for admin accounts and sensitive operations |

---

## 10. Cross-Site Scripting (XSS)

**Three types:**

| Type | Vector | Example |
|------|--------|---------|
| **Stored XSS** | Script saved in database; served to all viewers | Malicious `<script>` in a book review |
| **Reflected XSS** | Script in URL param; victim clicks crafted link | `?q=<script>alert(1)</script>` |
| **DOM-based XSS** | JavaScript writes URL fragment to DOM without server | `document.write(location.hash)` |

**Prevention:**

```html
<!-- Thymeleaf: th:text auto-encodes (safe) vs th:utext bypasses (dangerous) -->
<p th:text="${userContent}">  ✅ Safe — encodes HTML entities
<p th:utext="${userContent}"> ❌ Dangerous — renders raw HTML

<!-- JavaScript: textContent is safe; innerHTML with user data is dangerous -->
element.textContent = userInput;      // ✅ Safe
element.innerHTML   = userInput;      // ❌ Dangerous
```

```http
Content-Security-Policy: default-src 'self'; script-src 'self'
```
```java
// Make session cookie inaccessible to JavaScript
response.addCookie(cookie);
cookie.setHttpOnly(true);   // JS cannot read even if XSS executes
cookie.setSecure(true);     // Only sent over HTTPS
```

---

## 11. Cross-Site Request Forgery (CSRF)

**Attack flow:**
1. User is logged in to `bankapp.com` (browser holds session cookie)
2. User visits `evil.com` — contains auto-submitting hidden form targeting `bankapp.com/transfer`
3. Browser sends the POST with the session cookie automatically
4. Bank processes the forged request

**Prevention:**
- **CSRF token** — unique, unguessable token per session; sent with every state-changing form; attacker on a different domain cannot know the token value
- **SameSite=Lax** cookie attribute — browser withholds cookie on cross-site form submissions

**When CSRF protection can be disabled:**
- Stateless JWT APIs (no session cookie; JWT sent in `Authorization` header; cross-site forms cannot set custom headers)

---

## 12. HTTP Security Headers

| Header | Value | Protection |
|--------|-------|-----------|
| `Strict-Transport-Security` | `max-age=31536000; includeSubDomains` | Force HTTPS for 1 year |
| `X-Frame-Options` | `DENY` | Prevent clickjacking (iframe embedding) |
| `X-Content-Type-Options` | `nosniff` | Prevent MIME type sniffing |
| `Content-Security-Policy` | `default-src 'self'` | Restrict script/resource origins |
| `Referrer-Policy` | `strict-origin-when-cross-origin` | Limit referrer header exposure |

Spring Security adds `X-Frame-Options`, `X-Content-Type-Options`, and `Strict-Transport-Security` automatically.

---

## 13. Spring Security Dependency

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>
```

**Auto-configuration defaults (without any configuration class):**
- All endpoints require authentication
- Login form at `/login`
- Logout at `/logout`
- Single user `user` with random UUID password printed to console
- CSRF protection enabled
- Session fixation protection enabled
- Common security headers added

**Always replace with your own `SecurityFilterChain` for production.**

---

## 14. Spring Security Filter Chain

```
HTTP Request
    ↓
DelegatingFilterProxy             ← bridges servlet container ↔ Spring context
    ↓
FilterChainProxy                  ← Spring Security entry point
    ↓
SecurityContextPersistenceFilter  ← loads SecurityContext from session
    ↓
UsernamePasswordAuthenticationFilter ← processes /login POST requests
    ↓
BasicAuthenticationFilter         ← processes Authorization: Basic header
    ↓
ExceptionTranslationFilter        ← AuthenticationException → 401; AccessDeniedException → 403
    ↓
FilterSecurityInterceptor         ← enforces authorization rules
    ↓
DispatcherServlet → @Controller
```

- Each filter has one responsibility
- Request reaches `@Controller` only if all security filters pass
- `SecurityContextHolder` stores authenticated user for the current request thread

---

## 15. Authentication vs Authorization

| | Authentication | Authorization |
|---|---|---|
| **Question** | Who are you? Can you prove it? | Are you allowed to do this? |
| **Happens** | First — at login | After authentication — on every request |
| **Spring component** | `AuthenticationManager`, `UserDetailsService` | `SecurityFilterChain` rules, `@PreAuthorize` (Day 30) |
| **On failure** | 401 Unauthorized | 403 Forbidden |

**Accessing the current user anywhere in the request:**
```java
Authentication auth = SecurityContextHolder.getContext().getAuthentication();
String username = auth.getName();
boolean isAdmin  = auth.getAuthorities().stream()
    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
```

---

## 16. @EnableWebSecurity and SecurityConfig Class

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/books/**").permitAll()
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            .formLogin(Customizer.withDefaults())
            .httpBasic(Customizer.withDefaults());

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
```

---

## 17. SecurityFilterChain Authorization Rules Reference

**Rules evaluated top-to-bottom — first match wins:**

```java
http.authorizeHttpRequests(auth -> auth
    .requestMatchers("/public/**").permitAll()
    .requestMatchers(HttpMethod.GET, "/api/books/**").permitAll()
    .requestMatchers("/api/admin/**").hasRole("ADMIN")
    .requestMatchers("/api/**").authenticated()
    .anyRequest().authenticated()          // must be last
);
```

| Method | Who can access |
|--------|---------------|
| `permitAll()` | Anyone — no authentication required |
| `authenticated()` | Any authenticated user |
| `hasRole("ADMIN")` | `ROLE_ADMIN` authority (prefix added automatically) |
| `hasAuthority("ROLE_ADMIN")` | Exact `ROLE_ADMIN` string (no prefix added) |
| `hasAnyRole("ADMIN", "MANAGER")` | At least one of the listed roles |
| `denyAll()` | No one — blocks all requests |

**Common mistake:** placing `anyRequest()` before specific rules — it matches first, specific rules never execute.

---

## 18. In-Memory Authentication

```java
@Bean
public UserDetailsService userDetailsService(PasswordEncoder passwordEncoder) {
    UserDetails user = User.builder()
        .username("user")
        .password(passwordEncoder.encode("password"))
        .roles("USER")                    // stored as ROLE_USER
        .build();

    UserDetails admin = User.builder()
        .username("admin")
        .password(passwordEncoder.encode("admin123"))
        .roles("USER", "ADMIN")
        .build();

    return new InMemoryUserDetailsManager(user, admin);
}
```

- Use for development, prototyping, and integration tests only
- **Never for production** — users not persisted; cleared on restart
- Passwords must be encoded — Spring Security rejects plaintext

---

## 19. BCryptPasswordEncoder

```java
BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();      // strength 10 (default)
BCryptPasswordEncoder stronger = new BCryptPasswordEncoder(12);   // stronger (100–200ms/hash)

String hash = encoder.encode("mypassword");  // $2a$10$...  (includes embedded salt)
// Same password → different hash each time (different salt)

encoder.matches("mypassword", hash);   // true  — re-hashes with stored salt, compares
encoder.matches("wrongpass",  hash);   // false
```

| Algorithm | Speed (GPU 2024) | Brute-force resistance |
|-----------|-----------------|----------------------|
| MD5 | 60 billion/sec | Seconds |
| SHA-1 | 20 billion/sec | Minutes |
| BCrypt cost=10 | ~30,000/sec | Centuries |
| BCrypt cost=12 | ~8,000/sec | Even longer |

**Rules:**
- On save: `encoder.encode(rawPassword)` → store the result
- On verify: Spring calls `encoder.matches()` automatically during login
- Never compare hashes with `.equals()`; never `WHERE password = ?` with a hash

---

## 20. UserDetailsService Interface

```java
public interface UserDetailsService {
    UserDetails loadUserByUsername(String username)
        throws UsernameNotFoundException;
}
```

Spring Security calls this during authentication. Return a `UserDetails` object:

```java
public interface UserDetails {
    String getUsername();
    String getPassword();                                  // the stored bcrypt hash
    Collection<? extends GrantedAuthority> getAuthorities();
    boolean isEnabled();                                   // false = account disabled
    boolean isAccountNonLocked();                          // false = account locked
    boolean isAccountNonExpired();                         // false = account expired
    boolean isCredentialsNonExpired();                     // false = password expired
}
```

If any boolean flag returns `false`, Spring Security rejects the login with the appropriate exception.

---

## 21. Database Authentication — Full Implementation

```java
// Entity
@Entity @Table(name = "app_users")
public class AppUser {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String username;
    private String password;   // bcrypt hash
    private String role;       // "USER" or "ADMIN"
    private boolean enabled = true;
}

// Repository
public interface UserRepository extends JpaRepository<AppUser, Long> {
    Optional<AppUser> findByUsername(String username);
    boolean existsByUsername(String username);
}

// UserDetailsService
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AppUser user = userRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        return User.builder()
            .username(user.getUsername())
            .password(user.getPassword())   // already hashed — Spring verifies it
            .roles(user.getRole())          // "USER" → stores ROLE_USER
            .disabled(!user.isEnabled())
            .build();
    }
}
```

**Auto-discovery:** Spring Boot finds `CustomUserDetailsService` because it implements `UserDetailsService`. No explicit wiring required.

**Timing attack prevention:** `UsernameNotFoundException` is mapped to the same "Bad credentials" message as a wrong password — attackers cannot enumerate which usernames exist.

---

## 22. User Registration — Storing Passwords Correctly

```java
@PostMapping("/api/auth/register")
public ResponseEntity<Void> register(@RequestBody @Valid RegisterRequest request) {
    if (userRepository.existsByUsername(request.getUsername())) {
        throw new ConflictException("Username already taken");
    }

    AppUser user = new AppUser();
    user.setUsername(request.getUsername());
    user.setPassword(passwordEncoder.encode(request.getPassword()));  // ← HASH BEFORE SAVE
    user.setRole("USER");

    userRepository.save(user);

    return ResponseEntity.status(HttpStatus.CREATED).build();
}
```

**Rules:**
- Hash the password **before** calling `save()` — never store raw passwords
- Never log the password at any stage
- Never return the password hash in the response
- Return 201 Created with empty body
- Check for duplicate usernames first for a clean 409 response

---

## 23. DaoAuthenticationProvider

The component that orchestrates username/password authentication:

```
LoginRequest (username + password)
    ↓
DaoAuthenticationProvider.authenticate()
    ↓
UserDetailsService.loadUserByUsername(username)   → UserDetails (with stored hash)
    ↓
PasswordEncoder.matches(submittedPassword, storedHash)  → boolean
    ↓
If true: UsernamePasswordAuthenticationToken (authenticated) stored in SecurityContext
If false: throws BadCredentialsException → 401
```

**Auto-configured** when `UserDetailsService` bean + `PasswordEncoder` bean are both present.

**Custom `AuthenticationProvider` use cases:**
- Multi-factor authentication (TOTP after password)
- Multiple auth sources (database + LDAP)
- Custom credential types (API keys)

---

## 24. CSRF Protection Configuration

**For Thymeleaf web apps — leave CSRF on (default):**
```html
<!-- th:action triggers automatic CSRF token injection -->
<form method="post" th:action="@{/books}">
    <!-- Spring injects: <input type="hidden" name="_csrf" value="token"> -->
</form>
```

**For SPA with session cookies — cookie-based CSRF:**
```java
http.csrf(csrf -> csrf
    .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()));
// Angular HttpClient reads XSRF-TOKEN cookie and sends X-XSRF-TOKEN header automatically
```

**For stateless JWT REST APIs — disable CSRF:**
```java
http
    .csrf(csrf -> csrf.disable())
    .sessionManagement(session -> session
        .sessionCreationPolicy(SessionCreationPolicy.STATELESS));
```

**Why disabling is safe for JWT APIs:** CSRF relies on browsers automatically sending session cookies. JWT APIs use stateless tokens in `Authorization: Bearer` headers. Cross-site forms cannot set custom headers. Forged requests arrive without the JWT and are rejected as unauthenticated.

| App type | CSRF setting |
|----------|-------------|
| Traditional web app (Thymeleaf, server-rendered) | ✅ On (default) |
| SPA + session cookies | ✅ On with `CookieCsrfTokenRepository` |
| Stateless JWT REST API | ❌ Disabled |

---

## 25. @WithMockUser for Testing Secured Endpoints

```java
@WebMvcTest(BookController.class)
class BookControllerTest {

    @Autowired MockMvc mockMvc;
    @MockBean  BookService bookService;

    @Test
    void getBook_returns401_withoutAuthentication() throws Exception {
        mockMvc.perform(get("/api/books/1"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "alice", roles = {"USER"})
    void getBook_returns200_whenAuthenticated() throws Exception {
        when(bookService.findById(1L)).thenReturn(bookDto);
        mockMvc.perform(get("/api/books/1"))
            .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void deleteBook_returns204_forAdmin() throws Exception {
        mockMvc.perform(delete("/api/books/1").with(csrf()))
            .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = {"USER"})
    void deleteBook_returns403_forNonAdmin() throws Exception {
        mockMvc.perform(delete("/api/books/1").with(csrf()))
            .andExpect(status().isForbidden());
    }
}
```

- `@WithMockUser` injects mock `Authentication` into `SecurityContext` — no real login
- Add `.with(csrf())` on POST/PUT/DELETE when CSRF is enabled
- Test matrix: unauthenticated (401), correct role (200/204), wrong role (403)

---

## 26. Security Configuration Property Reference

```properties
# Disable H2 console in production
spring.h2.console.enabled=false

# Never include stack traces in HTTP error responses
server.error.include-stacktrace=never

# Never include exception message in HTTP error responses
server.error.include-message=never

# Limit Actuator exposure in production
management.endpoints.web.exposure.include=health,info

# Force actuator to require authentication
management.endpoints.web.base-path=/actuator
management.endpoint.health.show-details=when-authorized
```

---

## 27. Common Security Mistakes

| Mistake | Consequence | Fix |
|---------|-------------|-----|
| Storing passwords in plaintext | Full account takeover if DB is breached | `passwordEncoder.encode()` before save |
| Using MD5/SHA-1 for passwords | Brute-forced in seconds on GPU | BCryptPasswordEncoder only |
| Hardcoding secrets in source code | Exposed in repo history, logs | Environment variables or secret manager |
| No ownership check on resources | IDOR — user reads/modifies other users' data | Verify resource ownership after fetch |
| Stack traces in HTTP responses | Exposes internal structure to attackers | `server.error.include-stacktrace=never` |
| CSRF disabled for session-based apps | Forged state-changing requests | Keep CSRF on; use `th:action` in Thymeleaf |
| `th:utext` / `dangerouslySetInnerHTML` with user data | XSS | Use `th:text` / `textContent` instead |
| SQL string concatenation | SQL injection | Parameterized queries always |
| `anyRequest()` rule placed before specific rules | Specific rules never execute | Order rules from most-specific to least-specific |
| No logging of auth failures | Brute-force attacks invisible | Log every failed auth attempt |

---

## 28. Full Security Configuration Example

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;

    public SecurityConfig(CustomUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/books/**").permitAll()
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            .sessionManagement(session -> session
                .sessionFixation().newSession()     // regenerate session ID on login
            )
            .headers(headers -> headers
                .frameOptions(frame -> frame.deny())
                .contentTypeOptions(Customizer.withDefaults())
            )
            .formLogin(form -> form
                .loginPage("/login")
                .defaultSuccessUrl("/api/books", true)
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .invalidateHttpSession(true)        // server-side session invalidation
                .deleteCookies("JSESSIONID")
                .permitAll()
            );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
```

---

## 29. Spring Security Annotation Reference

| Annotation | Location | Purpose |
|-----------|----------|---------|
| `@EnableWebSecurity` | Security config class | Activates Spring Security web support |
| `@Configuration` | Security config class | Marks class as Spring configuration |
| `@WithMockUser` | Test method | Injects mock authentication for testing |
| `@Service` | `UserDetailsService` impl | Makes it a Spring bean (auto-discovered) |

---

## 30. Looking Ahead — Day 30

Day 29 configured session-based security. Day 30 replaces the session with stateless JWT tokens.

**Day 30 — JWT & RBAC:**
- JWT structure: header.payload.signature — what each part contains
- Generating and signing JWTs with a secret key
- Validating JWTs on every request
- Custom `OncePerRequestFilter` for JWT authentication
- `SessionCreationPolicy.STATELESS` — no server-side session
- Role-Based Access Control (RBAC) with `@PreAuthorize("hasRole('ADMIN')")`
- `@Secured({"ROLE_USER"})` for method-level security
- URL-based security with `requestMatchers()`
- `SecurityContextHolder` for accessing current user in business logic
- Handling auth failures: `AuthenticationEntryPoint` and `AccessDeniedHandler`
- HTTPS and secure communication best practices
- API key patterns and rate limiting concepts

# Day 29 Part 2 — Spring Security: Architecture, Authentication & CSRF
## Slide Descriptions

---

### Slide 1 — Title Slide

**Title:** Day 29 Part 2: Spring Security in Practice

**Subtitle:** From OWASP principles to the framework that enforces them

**Learning Objectives:**
- Describe Spring Security's filter chain architecture and request flow
- Distinguish authentication from authorization
- Configure `SecurityFilterChain` with `HttpSecurity` lambda DSL
- Implement in-memory authentication for development and testing
- Build database-backed authentication with a custom `UserDetailsService`
- Encode and verify passwords with `BCryptPasswordEncoder`
- Understand `DaoAuthenticationProvider` and the authentication provider chain
- Configure CSRF protection appropriately for web apps vs stateless REST APIs
- Test secured endpoints with `@WithMockUser` in `@WebMvcTest` tests

---

### Slide 2 — Spring Security Overview

**Title:** Spring Security — What It Does and What It Gives You for Free

**Content:**

**What Spring Security provides:**
- Authentication — who is this user, and can they prove it?
- Authorization — is this authenticated user allowed to do what they're asking?
- Protection against common web attacks: CSRF, session fixation, clickjacking, MIME sniffing

**What happens when you add `spring-boot-starter-security` to your classpath (auto-configuration):**

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>
```

Immediately, without any additional code:
- Every endpoint requires authentication
- A login form is served at `/login`
- A logout endpoint exists at `/logout`
- A single in-memory user is created with username `user` and a random UUID password printed to the console at startup
- CSRF protection is enabled
- Session fixation protection is active
- Common security headers are added to every response

**The rule:** never rely on the auto-configuration for production. It's a safe default to start from, not a production configuration. You will always replace it with your own `SecurityFilterChain` bean that explicitly defines your rules.

---

### Slide 3 — The Filter Chain Architecture

**Title:** How Spring Security Physically Intercepts Every Request

**Content:**

Spring Security is implemented as a chain of servlet filters. These filters sit in front of the `DispatcherServlet` — every HTTP request passes through them before reaching your `@Controller`.

```
HTTP Request
    ↓
[DelegatingFilterProxy]           ← registered in servlet container
    ↓
[FilterChainProxy]                ← Spring's main entry point
    ↓
SecurityContextPersistenceFilter  ← loads/saves SecurityContext from session
    ↓
UsernamePasswordAuthenticationFilter  ← processes /login form submissions
    ↓
BasicAuthenticationFilter         ← processes Authorization: Basic headers
    ↓
RememberMeAuthenticationFilter    ← handles "remember me" tokens
    ↓
ExceptionTranslationFilter        ← catches AccessDeniedException, AuthenticationException
    ↓
FilterSecurityInterceptor         ← enforces authorization rules
    ↓
DispatcherServlet → @Controller
```

**Key concepts:**
- Each filter has exactly one responsibility
- Filters execute in a fixed order — the order matters
- `ExceptionTranslationFilter` translates Spring Security exceptions into HTTP responses (401, 403)
- `SecurityContextHolder` stores the authenticated user's identity — available anywhere in the request thread
- After the request completes, the `SecurityContextPersistenceFilter` saves the context back to the session

**`DelegatingFilterProxy`** bridges the servlet container's filter lifecycle with Spring's application context, allowing the security filters to be Spring-managed beans.

---

### Slide 4 — Authentication vs Authorization

**Title:** Two Distinct Questions — Who You Are vs What You Can Do

**Content:**

| Concept | The question it answers | When it happens | Spring Security component |
|---------|------------------------|-----------------|--------------------------|
| **Authentication** | Who are you? Can you prove it? | First — before authorization | `AuthenticationManager`, `UserDetailsService`, `AuthenticationProvider` |
| **Authorization** | Are you allowed to do this specific thing? | After authentication | `SecurityFilterChain` rules, `@PreAuthorize` (Day 30) |

**Authentication examples:**
- Submitting username + password to `/login`
- Sending a JWT in the `Authorization: Bearer` header (Day 30)
- OAuth2 login via Google or GitHub

**Authorization examples:**
- "Only users with `ROLE_ADMIN` can call `DELETE /api/books/{id}`"
- "A user can only read their own orders, not other users' orders"
- "The `/api/public/**` endpoints are accessible without login"

**The SecurityContext:**
After successful authentication, Spring Security stores the authenticated user's `Authentication` object in the `SecurityContextHolder`:
```java
Authentication auth = SecurityContextHolder.getContext().getAuthentication();
String username = auth.getName();                   // "alice"
Collection<GrantedAuthority> roles = auth.getAuthorities();  // [ROLE_USER]
```
This is available on the thread for the duration of the request. Day 30 explores using it directly for authorization logic.

---

### Slide 5 — @EnableWebSecurity and the Configuration Class

**Title:** Security Configuration Class — The Central Control Point

**Content:**

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/public/**").permitAll()
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

**What each piece does:**
- `@Configuration` — marks this as a configuration class; Spring creates beans from it
- `@EnableWebSecurity` — activates Spring Security's web security support; implied by Spring Boot auto-config but explicit is clearer
- `SecurityFilterChain` bean — **replaces** the auto-configured default security; your rules take effect
- `HttpSecurity` — the builder for configuring request rules, login, CSRF, headers, etc.
- `PasswordEncoder` bean — declared here so it can be injected anywhere in the application; Spring Security's `DaoAuthenticationProvider` also picks it up automatically
- `return http.build()` — finalizes the configuration and creates the `SecurityFilterChain`

**Lambda DSL** (Spring Security 6+): configuration methods take lambdas rather than chained method calls. `auth -> auth.requestMatchers(...)` reads left-to-right as natural language.

---

### Slide 6 — SecurityFilterChain Configuration

**Title:** Configuring Request Authorization Rules

**Content:**

Rules are evaluated **top-to-bottom — first match wins.** Order matters.

```java
http.authorizeHttpRequests(auth -> auth
    // 1. Public authentication endpoints — no login required
    .requestMatchers("/api/auth/**").permitAll()

    // 2. Public read access to book catalog
    .requestMatchers(HttpMethod.GET, "/api/books/**").permitAll()

    // 3. Admin-only endpoints
    .requestMatchers("/api/admin/**").hasRole("ADMIN")

    // 4. Write operations on books need any authenticated user
    .requestMatchers("/api/books/**").authenticated()

    // 5. Catch-all — anything not matched above requires authentication
    .anyRequest().authenticated()
);
```

**Authorization methods reference:**

| Method | Who can access |
|--------|---------------|
| `permitAll()` | Anyone — authenticated or not |
| `authenticated()` | Any logged-in user, regardless of role |
| `hasRole("ADMIN")` | Must have `ROLE_ADMIN` — Spring prepends `ROLE_` automatically |
| `hasAuthority("ROLE_ADMIN")` | Must have the exact authority string — no prefix added |
| `hasAnyRole("ADMIN", "MANAGER")` | Must have at least one of the listed roles |
| `hasAnyAuthority("SCOPE_read", "SCOPE_write")` | At least one of the exact authority strings |
| `denyAll()` | No one — block all requests (use for deprecated endpoints) |

**Common mistake:** placing `anyRequest().authenticated()` before more specific rules — it matches first and the specific rules never execute.

---

### Slide 7 — In-Memory Authentication

**Title:** In-Memory Authentication — Development and Testing Only

**Content:**

For local development and integration testing, store users in application memory:

```java
@Bean
public UserDetailsService userDetailsService(PasswordEncoder passwordEncoder) {
    UserDetails regularUser = User.builder()
        .username("user")
        .password(passwordEncoder.encode("password"))
        .roles("USER")                    // Spring stores ROLE_USER internally
        .build();

    UserDetails admin = User.builder()
        .username("admin")
        .password(passwordEncoder.encode("admin123"))
        .roles("USER", "ADMIN")           // multiple roles
        .build();

    UserDetails manager = User.builder()
        .username("manager")
        .password(passwordEncoder.encode("manager123"))
        .authorities("ROLE_USER", "ROLE_MANAGER")  // using authorities() instead of roles()
        .build();

    return new InMemoryUserDetailsManager(regularUser, admin, manager);
}
```

**Important notes:**
- `.roles("USER")` → Spring stores `ROLE_USER`; `.hasRole("USER")` checks for `ROLE_USER`
- `.authorities("ROLE_USER")` → stored as-is; `.hasAuthority("ROLE_USER")` checks exact string
- Passwords must be encoded — Spring Security refuses plaintext passwords (throws `IllegalArgumentException`)
- `InMemoryUserDetailsManager` implements `UserDetailsService` — Spring Security auto-discovers it
- **Use only for:** local development, unit/integration tests, prototyping. **Never production.**
- Application restart clears all users — not persisted

---

### Slide 8 — BCryptPasswordEncoder

**Title:** Password Encoding — Why BCrypt and How It Works

**Content:**

`BCryptPasswordEncoder` is the standard password encoder for Spring Security.

**How BCrypt works:**
- **Salted** — a random salt is generated for each password; the same password produces a completely different hash each time
- **One-way** — the hash cannot be reversed to obtain the original password
- **Adaptive (work factor)** — the cost parameter (default: 10) controls how long the hash takes to compute; can be increased as hardware gets faster, invalidating pre-computed rainbow tables

```java
BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();   // strength 10 (default)
// BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12); // higher strength = slower

String hash1 = encoder.encode("mypassword");  // $2a$10$rBv3...  (different every time due to salt)
String hash2 = encoder.encode("mypassword");  // $2a$10$Kp1...  (different hash, same password)

System.out.println(hash1.equals(hash2));  // false — salts differ

// Verification — re-hashes the raw password with the stored salt and compares
boolean valid   = encoder.matches("mypassword", hash1);  // true
boolean invalid = encoder.matches("wrongpass",  hash1);  // false
```

**Why not MD5 or SHA-1?**

| Algorithm | Hashes per second (GPU, 2024) | Time to crack 8-char password |
|-----------|-------------------------------|-------------------------------|
| MD5 | ~60 billion/sec | Seconds |
| SHA-1 | ~20 billion/sec | Minutes |
| BCrypt (cost 10) | ~30,000/sec | Centuries |

BCrypt is *deliberately* slow. That's the entire point. Fast hashing = easy brute-force.

**The rule:**
- On registration/password change: `encoder.encode(rawPassword)` → store the hash
- On login: `encoder.matches(rawPassword, storedHash)` → Spring Security calls this automatically
- **Never compare password hashes with `.equals()` or a direct database lookup**

---

### Slide 9 — UserDetailsService Interface

**Title:** UserDetailsService — The Contract Between Your Data and Spring Security

**Content:**

`UserDetailsService` is the interface Spring Security calls to load user data during authentication:

```java
public interface UserDetailsService {
    UserDetails loadUserByUsername(String username)
        throws UsernameNotFoundException;
}
```

Spring Security calls `loadUserByUsername(username)` when a login attempt arrives. Your implementation looks up the user from wherever they're stored and returns a `UserDetails` object. Spring Security then handles password verification and populating the `SecurityContext`.

**`UserDetails` provides:**
```java
public interface UserDetails {
    String getUsername();          // identifier used for login
    String getPassword();          // stored (hashed) password — Spring verifies it
    Collection<? extends GrantedAuthority> getAuthorities();   // roles / permissions

    // Account status flags:
    boolean isEnabled();                 // account not disabled
    boolean isAccountNonLocked();        // account not locked (e.g., after failed attempts)
    boolean isAccountNonExpired();       // account not past its expiry date
    boolean isCredentialsNonExpired();   // password not expired
}
```

**Your `loadUserByUsername` must:**
1. Look up the user by username (or email — whatever your login identifier is)
2. Throw `UsernameNotFoundException` if the user doesn't exist
3. Return a populated `UserDetails` — Spring handles the rest

---

### Slide 10 — Database Authentication with UserDetailsService

**Title:** Wiring Spring Security to Your JPA User Repository

**Content:**

Full implementation — entity, repository, and custom `UserDetailsService`:

```java
// 1. JPA Entity
@Entity
@Table(name = "app_users")
public class AppUser {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String username;
    private String password;       // bcrypt hash — never plaintext
    private String role;           // "USER" or "ADMIN"
    private boolean enabled = true;
    // getters / setters
}

// 2. Repository
public interface UserRepository extends JpaRepository<AppUser, Long> {
    Optional<AppUser> findByUsername(String username);
    boolean existsByUsername(String username);
}

// 3. UserDetailsService implementation
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {
        AppUser user = userRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException(
                "User not found: " + username));

        return User.builder()
            .username(user.getUsername())
            .password(user.getPassword())   // already bcrypt-hashed — Spring verifies it
            .roles(user.getRole())          // "USER" → stores ROLE_USER
            .disabled(!user.isEnabled())
            .build();
    }
}
```

Spring Boot **auto-discovers** the `CustomUserDetailsService` bean because it implements `UserDetailsService`. No additional wiring required.

**Timing attack note:** when you throw `UsernameNotFoundException`, Spring Security's `DaoAuthenticationProvider` maps this to the same generic "Bad credentials" error as a wrong password — preventing username enumeration attacks where an attacker can determine which usernames exist based on different error messages.

---

### Slide 11 — Authentication Providers and DaoAuthenticationProvider

**Title:** DaoAuthenticationProvider — Wiring UserDetailsService and PasswordEncoder

**Content:**

`AuthenticationProvider` is the interface that validates credentials and produces an authenticated token:

```java
public interface AuthenticationProvider {
    Authentication authenticate(Authentication authentication)
        throws AuthenticationException;
    boolean supports(Class<?> authentication);
}
```

`DaoAuthenticationProvider` is Spring Security's standard implementation — it uses your `UserDetailsService` to load the user, then your `PasswordEncoder` to verify the password.

**Spring Boot auto-configures this.** When you declare a `UserDetailsService` bean and a `PasswordEncoder` bean, Spring Boot creates a `DaoAuthenticationProvider` and registers it automatically. You don't need to declare it explicitly.

**Explicit declaration (shows what Spring does automatically):**
```java
@Bean
public AuthenticationProvider authenticationProvider(
        CustomUserDetailsService userDetailsService,
        PasswordEncoder passwordEncoder) {
    DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
    provider.setUserDetailsService(userDetailsService);
    provider.setPasswordEncoder(passwordEncoder);
    return provider;
}
```

**When you need a custom `AuthenticationProvider`:**
- Multi-factor authentication (check a TOTP code after password verification)
- Authentication from multiple sources (database primary, LDAP fallback)
- Custom credential types (API key authentication, certificate-based auth)
- Day 30: JWT authentication uses a custom filter rather than an `AuthenticationProvider`

Spring Security calls all registered providers in order until one returns a non-null result or all throw `AuthenticationException`.

---

### Slide 12 — CSRF Protection Configuration

**Title:** CSRF — Enabled by Default; Know When to Disable It

**Content:**

Spring Security enables CSRF protection by default for web applications.

**For traditional web apps with Thymeleaf forms — leave CSRF on:**

Spring Security auto-injects the CSRF token as a hidden field in Thymeleaf forms:
```html
<!-- th:action informs Spring Security to inject the CSRF token -->
<form method="post" th:action="@{/books}">
    <!-- Spring Security injects: <input type="hidden" name="_csrf" value="abc123"> -->
    <input type="text" name="title" placeholder="Book title">
    <button type="submit">Save</button>
</form>
```
The token is validated automatically on POST. No additional developer code needed.

**For AJAX requests in SPAs (Angular, React) — use cookie-based CSRF:**
```java
http.csrf(csrf -> csrf
    .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()));
// Spring sets a XSRF-TOKEN cookie that JavaScript can read
// Angular's HttpClient reads this cookie and sends it as X-XSRF-TOKEN header automatically
// React: read document.cookie and add as header manually
```

**For stateless REST APIs with JWT — disable CSRF:**
```java
http
    .csrf(csrf -> csrf.disable())
    .sessionManagement(session -> session
        .sessionCreationPolicy(SessionCreationPolicy.STATELESS));
```

**Why it's safe to disable CSRF for stateless JWT APIs:**
CSRF attacks work because browsers automatically send cookies with cross-site requests. Stateless JWT APIs don't use session cookies — the client sends the token in an `Authorization: Bearer` header. Cross-site HTML forms cannot set custom headers. Therefore, a forged cross-site request won't include the JWT and the server will reject it as unauthenticated.

**Rule of thumb:** if your API uses session cookies → enable CSRF. If your API uses JWT in `Authorization` headers → disable CSRF.

---

### Slide 13 — User Registration — Saving Passwords Correctly

**Title:** Registration Endpoint — The Right Way to Store User Credentials

**Content:**

```java
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthController(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/register")
    public ResponseEntity<Void> register(@RequestBody @Valid RegisterRequest request) {
        // Check for duplicate username before saving
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new ConflictException("Username already taken");
        }

        AppUser user = new AppUser();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));  // ← HASH IT
        user.setRole("USER");
        user.setEnabled(true);

        userRepository.save(user);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
```

**Key rules:**
- **Always hash the password before calling `save()`** — never store the raw password
- Never log the password at any stage
- Never return the password hash in the response body
- Return 201 Created with an empty body — don't return the saved user entity
- Check for duplicate usernames first to give a useful 409 Conflict response rather than a DB constraint error

**`RegisterRequest` DTO with validation:**
```java
public record RegisterRequest(
    @NotBlank @Size(min = 3, max = 50) String username,
    @NotBlank @Size(min = 8, message = "Password must be at least 8 characters") String password
) {}
```

---

### Slide 14 — Testing Secured Endpoints with @WithMockUser

**Title:** @WithMockUser — Testing Without a Real Login Flow

**Content:**

When Spring Security secures your endpoints, your `@WebMvcTest` tests need to simulate an authenticated user. `@WithMockUser` injects a mock `Authentication` into the `SecurityContext` without executing the login process.

Dependency (included in `spring-boot-starter-test`):
```java
import org.springframework.security.test.context.support.WithMockUser;
```

```java
@WebMvcTest(BookController.class)
class BookControllerTest {

    @Autowired MockMvc mockMvc;
    @MockBean  BookService bookService;

    // Test 1: unauthenticated request → 401
    @Test
    void getBook_returns401_withoutAuthentication() throws Exception {
        mockMvc.perform(get("/api/books/1"))
            .andExpect(status().isUnauthorized());
    }

    // Test 2: authenticated user → 200
    @Test
    @WithMockUser(username = "alice", roles = {"USER"})
    void getBook_returns200_whenUserAuthenticated() throws Exception {
        when(bookService.findById(1L))
            .thenReturn(new BookDto(1L, "Clean Code", new BigDecimal("35.00")));
        mockMvc.perform(get("/api/books/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.title").value("Clean Code"));
    }

    // Test 3: ADMIN role → 204 No Content on delete
    @Test
    @WithMockUser(roles = {"ADMIN"})
    void deleteBook_returns204_whenAdmin() throws Exception {
        mockMvc.perform(delete("/api/books/1"))
            .andExpect(status().isNoContent());
    }

    // Test 4: USER role trying admin endpoint → 403
    @Test
    @WithMockUser(roles = {"USER"})
    void deleteBook_returns403_whenNotAdmin() throws Exception {
        mockMvc.perform(delete("/api/books/1"))
            .andExpect(status().isForbidden());
    }
}
```

**Note:** POST/PUT/DELETE requests in MockMvc tests require CSRF token when CSRF is enabled:
```java
mockMvc.perform(post("/api/books")
    .with(csrf())   // import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf
    .contentType(APPLICATION_JSON)
    .content(json))
```

---

### Slide 15 — Part 2 Summary & Day 30 Preview

**Title:** Part 2 Summary — Spring Security Reference

**Content:**

**Architecture:**
- Filter chain sits in front of `DispatcherServlet` — every request is intercepted
- `SecurityContextHolder` holds the current user's `Authentication` for the request thread
- `ExceptionTranslationFilter` maps `AccessDeniedException` → 403, `AuthenticationException` → 401

**Configuration:**
```java
@Bean SecurityFilterChain filterChain(HttpSecurity http)  // your rules replace auto-config
.authorizeHttpRequests()                                   // define who can access what
.requestMatchers("/path").hasRole("ROLE")                  // first-match-wins ordering
```

**Authentication chain:**
```
Login request
    → UsernamePasswordAuthenticationFilter
    → DaoAuthenticationProvider
    → UserDetailsService.loadUserByUsername()
    → PasswordEncoder.matches(raw, stored)
    → Authentication stored in SecurityContextHolder
```

**Key components:**

| Component | Purpose |
|-----------|---------|
| `UserDetailsService` | Load user from any data source by username |
| `UserDetails` | Contract for user data (username, password, authorities, account flags) |
| `BCryptPasswordEncoder` | Encode on save (`encode()`), verify on login (`matches()`) |
| `DaoAuthenticationProvider` | Wires UserDetailsService + PasswordEncoder; auto-configured |
| `InMemoryUserDetailsManager` | Dev/test only; never production |
| `@WithMockUser` | Inject mock auth in `@WebMvcTest` tests |
| CSRF token | Auto-injected in Thymeleaf forms; disable for stateless JWT APIs |

**Day 30 Preview — JWT & RBAC:**
Everything configured today provides session-based authentication. Day 30 replaces the session with a stateless JWT token — a compact signed object that the client stores and sends with every request. Day 30 also covers Role-Based Access Control with `@PreAuthorize` and `@Secured` for method-level security, and `SecurityContextHolder` for retrieving the current user within business logic.

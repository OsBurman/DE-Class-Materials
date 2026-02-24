# Day 29 — Spring Security & OWASP: Part 2 Walkthrough Script
## Spring Security Architecture, Authentication, Authorization, BCrypt, CSRF

**Total time:** ~90 minutes  
**Files covered:** `01-spring-security-config.java`, `02-database-authentication.java`

---

## Segment 1 — Opening: What Spring Security Does for You (5 min)

> "Welcome to Part 2. In Part 1, we understood the PROBLEMS — SQL injection, broken authentication, XSS, CSRF. Now we're going to look at the SOLUTION that Spring provides to handle most of these for you."

> "Spring Security is a framework that sits in front of every HTTP request before it reaches your controllers. Think of it as a security guard at the door of your application. You configure what the guard checks, what it allows, and what it blocks."

> "Here's what Spring Security gives you out of the box with a single dependency and almost no configuration:
> - All endpoints require authentication
> - A random password printed to the console on startup
> - CSRF protection enabled
> - Session management
> - Password encoding support
> - Standard security response headers"

> "Open `01-spring-security-config.java`. Read the architecture comment block at the top with me."

---

## Segment 2 — Spring Security Architecture (10 min)

**Point to the architecture diagram comment:**

> "Let's trace the path an HTTP request takes through Spring Security."

> "First: `DelegatingFilterProxy`. This is registered in the Servlet container — before Spring's DispatcherServlet even sees the request. It acts as a bridge between the Servlet container and Spring's application context."

> "Next: `FilterChainProxy`. This is Spring Security's own manager that holds one or more SecurityFilterChains. It selects the right chain based on the request URL."

> "Then: `SecurityFilterChain`. THIS is what you configure. It's an ordered list of filters. Each filter has a specific job. Let me name the important ones."

> "`UsernamePasswordAuthenticationFilter` — intercepts POST requests to `/login`, extracts username and password from the form body, and triggers the authentication process."

> "`CsrfFilter` — validates the CSRF token on every non-safe request. It comes early in the chain so malicious requests are rejected before they touch your business logic."

> "`ExceptionTranslationFilter` — catches `AccessDeniedException` and `AuthenticationException` thrown by downstream filters and converts them into proper HTTP responses: 401 Unauthorized or 403 Forbidden."

> "`AuthorizationFilter` — the last filter before your controller. Checks whether the authenticated user is authorized to access the requested URL based on your rules."

> "All of this runs before your controller method is ever called. Your code is protected at the infrastructure level."

---

## Segment 3 — Authentication vs Authorization (5 min)

> "Before we look at the code, I want to nail down this vocabulary distinction because it trips people up constantly in interviews."

> "Read Section 1 with me."

**Walk through the Authentication vs Authorization section:**

> "**Authentication: 'Who are you?'** Verifying identity. You submit username and password. The system checks if you are who you claim. Result: authenticated (or rejected). HTTP failure code: 401 Unauthorized — confusingly named, but 401 means 'not authenticated, please log in'."

> "**Authorization: 'What are you allowed to do?'** AFTER you're authenticated, the system checks your permissions. Can Alice access the admin panel? She's authenticated as Alice — but Alice is a USER, not an ADMIN. Access denied. HTTP failure code: 403 Forbidden."

> ⚠️ **Watch out:** "401 vs 403 is a common interview question and a common place where developers swap the codes. 401 = not authenticated. 403 = authenticated but not authorized."

> "Memory trick: AuthentiCATION = proving you're the CAT you claim to be. AuthoriZATION = checking you're in the right ZONE."

---

## Segment 4 — @EnableWebSecurity and SecurityFilterChain (15 min)

> "Scroll to Section 2 and the `BasicSecurityConfig` class."

> "`@Configuration` tells Spring this is a configuration class. `@EnableWebSecurity` activates Spring Security's web support and tells it that YOU are providing the configuration, overriding any auto-configuration."

**Walk through `basicFilterChain`:**

> "The `SecurityFilterChain` bean takes an `HttpSecurity` builder. You configure it with a fluent lambda DSL — each method opens a section, you configure it via a lambda, and at the end you call `http.build()`."

> "First section: `authorizeHttpRequests`. This is where you define your authorization rules. Look at the `requestMatchers` calls."

> "`requestMatchers(\"/\", \"/home\", \"/login\", \"/register\").permitAll()` — these URLs are public. No authentication required."

> "`requestMatchers(\"/css/**\", \"/js/**\").permitAll()` — static assets are always public. The `**` is a wildcard for any path under that prefix."

> "`requestMatchers(\"/admin/**\").hasRole(\"ADMIN\")` — only users with the ADMIN role can access any URL under `/admin/`. `hasRole(\"ADMIN\")` is shorthand for `hasAuthority(\"ROLE_ADMIN\")` — Spring Security automatically prepends `ROLE_` when you use `hasRole()`."

> "`anyRequest().authenticated()` — this is the catch-all. EVERYTHING else requires authentication. NEVER remove this line. Without it, you're saying 'everything not matched above is public'. That's the opposite of secure-by-default."

> ⚠️ **Watch out:** "Rule order matters. The first matching rule wins. Put SPECIFIC rules before GENERAL rules. If you put `anyRequest().authenticated()` first, your `permitAll()` rules below it will never be reached."

**Walk through `formLogin`:**

> "`.formLogin()` enables Spring Security's built-in form-based login.
> - `loginPage(\"/login\")` — the URL for your custom login form (GET)
> - `loginProcessingUrl(\"/login\")` — the URL Spring Security handles for POST submission
> - `defaultSuccessUrl(\"/dashboard\", true)` — redirect here after successful login
> - `failureUrl(\"/login?error=true\")` — redirect here on bad credentials"

**Walk through `logout`:**

> "`invalidateHttpSession(true)` — destroys the session on logout. Essential for security — prevents session reuse after logout. `deleteCookies(\"JSESSIONID\")` — removes the session cookie from the browser. Without this, the cookie persists but is invalid (harmless but confusing)."

---

## Segment 5 — In-Memory Authentication (8 min)

> "Scroll to Section 3 — `inMemoryUsers()`. This bean creates users that live entirely in the JVM — no database needed."

> "`User.withDefaultPasswordEncoder()` is a builder that hashes the plain-text password for you. It uses BCrypt internally."

> "Notice the roles: `regularUser` gets `USER`, `adminUser` gets both `ADMIN` AND `USER`. In Spring Security, roles don't inherit — if you want an admin to also have user permissions, you explicitly give them both."

> "`InMemoryUserDetailsManager` holds these users in a `ConcurrentHashMap`. When Spring Security needs to authenticate someone, it calls `loadUserByUsername()` on this manager, which looks up the username in the map and returns the `UserDetails`."

> "When would you use in-memory auth? Development. Demos. Internal tools with a fixed set of service accounts. NOT for user-facing applications with real users."

---

## Segment 6 — BCryptPasswordEncoder (10 min)

> "Scroll to Section 4. The `passwordEncoder()` bean is one of the most important beans in your security configuration."

> "Look at `demonstrateBcrypt`. I want to walk through this step by step."

> "`encoder.encode(\"mySecretPassword123!\")` — this generates a random 22-character salt, runs BCrypt with 2^12 iterations, and returns a 60-character string that contains the algorithm version, cost factor, salt, and hash all in one."

> "Now look: I call `encoder.encode(rawPassword)` TWICE and the output is different both times. Same input, different outputs. Why? Different random salts. This is critical — it means rainbow tables are useless. You can't precompute a table of `password123 → hash` because every user's hash is unique even if they have the same password."

> "`encoder.matches(rawPassword, hashed)` — this is what happens during login. Spring Security calls this to verify the submitted password. It extracts the salt from the stored hash, hashes the input with that same salt, and compares. Returns true on match, false otherwise."

> "The key insight: **you never decrypt a BCrypt hash**. You re-hash the attempt and compare. If someone steals your database, they get hashed passwords they cannot reverse."

> "**Ask the class:** What's the difference between BCrypt cost=10 vs cost=12?"

> "Cost=10: 2^10 = 1,024 iterations, ~65ms per hash. Cost=12: 2^12 = 4,096 iterations, ~250ms. The higher the cost, the slower it is for both legitimate logins AND brute-force attacks. At cost=12, an attacker can try only ~4 passwords per second on your server (or more offline with a GPU, but still orders of magnitude slower than MD5)."

---

## Segment 7 — CSRF Configuration (8 min)

> "Scroll to Section 5 — `csrfDemoFilterChain`. We talked about CSRF attacks in Part 1. Now let's see how Spring Security configures the defense."

> "By default, Spring Security's CSRF protection is ENABLED. The `CsrfFilter` runs early in the filter chain. For every non-safe request (POST, PUT, DELETE, PATCH), it checks for a CSRF token."

> "`CookieCsrfTokenRepository.withHttpOnlyFalse()` — this stores the CSRF token in a cookie called `XSRF-TOKEN`. Setting `withHttpOnlyFalse()` means JavaScript CAN read this cookie. Why? Because your Angular or React SPA needs to read it and include it as the `X-XSRF-TOKEN` request header."

> "Note: `withHttpOnlyFalse` doesn't make the session cookie readable — only the CSRF token cookie. The session cookie still has HttpOnly."

**Walk through `restApiFilterChain`:**

> "Now look at Section 6 — the stateless REST API configuration. This is what you'll use for JWT-based APIs."

> "`csrf -> csrf.disable()` — we disable CSRF. Why is this safe for REST APIs? Because CSRF attacks require the browser to automatically send credentials. With cookies: browsers auto-attach them. With JWT in `Authorization: Bearer <token>` header: browsers do NOT auto-attach that header on cross-origin requests. The attacker at evil.com cannot forge the header."

> "`SessionCreationPolicy.STATELESS` — no sessions created, no JSESSIONID cookie. Every request must carry its own credentials (the JWT). This is the correct setting for REST APIs that Day 30 will build on with JWT filters."

> "`formLogin(form -> form.disable()).httpBasic(basic -> basic.disable())` — disable login forms and HTTP Basic for the API layer. Authentication is handled entirely by the JWT filter we'll add tomorrow."

---

## Segment 8 — Database Authentication with UserDetailsService (15 min)

> "Switch to `02-database-authentication.java`. This is the complete production setup."

**Walk through the Entity (Section 1):**

> "Start with `BookstoreUser` — this is your JPA entity, your database table. It has the usual fields: id, username, email. The key field: `passwordHash` — column must be at least 60 characters to hold a BCrypt hash. Never name it `password` in the database — it implies plain text."

> "The `roles` field is a simple comma-separated string. In a real app you'd want a separate `roles` table with a many-to-many join. For simplicity here we use a single column."

> "`enabled` and `accountNonLocked` — these flags let you disable specific accounts (ban a user, lock after failures) without deleting them."

**Walk through UserDetails (Section 3):**

> "This is the bridge. `BookstoreUserDetails` holds your entity AND implements Spring Security's `UserDetails` interface. It's an adapter."

> "Look at `getAuthorities()`. Spring Security needs a `Collection<GrantedAuthority>`. Our roles are stored as `\"ROLE_USER,ROLE_ADMIN\"`. We split on comma, trim whitespace, map each string to a `SimpleGrantedAuthority`, and collect."

> "`getPassword()` returns `user.getPasswordHash()` — the stored bcrypt hash. Spring Security will call `passwordEncoder.matches(submittedPassword, this.getPassword())`."

> "The four boolean methods — `isAccountNonExpired`, `isAccountNonLocked`, `isCredentialsNonExpired`, `isEnabled` — these let you implement account lifecycle policies. If ANY returns false, Spring Security refuses authentication even with correct credentials."

**Walk through UserDetailsService (Section 4):**

> "Now the service. `UserDetailsService` has ONE method: `loadUserByUsername`. This is what Spring Security calls during authentication."

> "We query: `userRepository.findByUsername(username).orElseThrow(...)`. If the user doesn't exist, we throw `UsernameNotFoundException`. **Security note:** We use a generic error message — never say 'username not found' vs 'wrong password'. That leaks information about which usernames are registered. Spring Security's `DaoAuthenticationProvider` catches this exception and converts it to `BadCredentialsException` (same error message either way)."

> "If found, we return `new BookstoreUserDetails(user)` — the adapter wrapping our entity."

**Walk through Registration (Section 5):**

> "Look at `UserRegistrationService.registerUser`. This is where new users are created."

> "The critical line: `passwordEncoder.encode(rawPassword)`. The PLAIN TEXT password is encoded BEFORE it's stored. After this method returns, the plain text is discarded — it never reaches the database."

> "Notice the checks before saving: `existsByUsername` and `existsByEmail`. These prevent duplicate accounts and are a good place to add rate limiting to prevent bulk registration attacks."

**Walk through DaoAuthenticationProvider (Section 6):**

> "`DaoAuthenticationProvider` is the component that ties `UserDetailsService` and `PasswordEncoder` together."

> "You wire them in: `provider.setUserDetailsService(userDetailsService)` and `provider.setPasswordEncoder(passwordEncoder)`. Then in your `SecurityFilterChain`, you register it with `http.authenticationProvider(authProvider)`."

> "`setHideUserNotFoundExceptions(true)` — the default, but important to know. It converts `UsernameNotFoundException` to `BadCredentialsException` so attackers can't enumerate usernames by comparing error messages."

**Walk through the flow diagram (Section 8):**

> "Let's trace a complete login request with all the pieces we've built."

> "User posts to `/login` with `username=alice`, `password=secret`. The `UsernamePasswordAuthenticationFilter` extracts them and creates an unauthenticated token."

> "The `AuthenticationManager` receives the token and delegates to `DaoAuthenticationProvider`. The provider calls `loadUserByUsername(\"alice\")` — which queries the database and returns `BookstoreUserDetails`. Then it calls `BCryptPasswordEncoder.matches(\"secret\", storedHash)` — extracts the salt from the hash, re-hashes `\"secret\"`, compares. Match found."

> "The provider checks `isEnabled()`, `isAccountNonLocked()`, etc. All true. It creates an authenticated token with the user's authorities and stores it in `SecurityContextHolder`."

> "Spring Security then redirects to `defaultSuccessUrl`. Every subsequent request in this session, the `SecurityContextHolder` already has the authenticated user — no more password checking needed until the session expires."

---

## Segment 9 — SecurityContextHolder (5 min)

> "Scroll to Section 7 — `SecurityContextHelper`. This is a utility class showing how to access the authenticated user from anywhere in your application."

> "`SecurityContextHolder.getContext().getAuthentication()` — this gives you the current authentication object for the running thread. Spring stores it in a `ThreadLocal` — it's specific to the current request thread."

> "In practice, you often need the current user in a service method. Maybe you're saving an order and you want to set the `userId` from the authenticated user rather than accepting it from the request body (which the user could tamper with)."

> "In a controller, you can also get it more elegantly via method injection: `@AuthenticationPrincipal BookstoreUserDetails currentUser` — Spring MVC resolves this automatically. But the SecurityContextHolder approach works anywhere, including in services."

---

## Segment 10 — Wrap-Up & Interview Questions (5 min)

> "Let's recap Part 2:
> - **Spring Security Architecture:** DelegatingFilterProxy → FilterChainProxy → SecurityFilterChain → ordered filters
> - **Authentication vs Authorization:** 'who are you' (401) vs 'what can you do' (403)
> - **SecurityFilterChain:** lambda DSL, `requestMatchers`, `hasRole`, `authenticated`, `anyRequest`
> - **In-memory:** `InMemoryUserDetailsManager` — for dev/testing only
> - **BCrypt:** cost factor, random salt, never decrypt — only re-hash and compare
> - **CSRF:** enabled by default, `CookieCsrfTokenRepository` for SPAs, disable for JWT REST APIs
> - **UserDetailsService:** adapter between your database and Spring Security
> - **UserDetails:** adapter wrapping your entity with Spring Security's interface
> - **DaoAuthenticationProvider:** wires UserDetailsService + PasswordEncoder, hides user-not-found errors
> - **Registration:** always `encode()` before saving — never store plain text passwords"

**Interview questions:**

> 1. "What is the difference between `hasRole('ADMIN')` and `hasAuthority('ROLE_ADMIN')`?" *(Equivalent — hasRole prepends ROLE_ automatically)*
> 2. "Why is BCrypt better than SHA-256 for passwords?" *(Slow by design, built-in salt, configurable cost factor)*
> 3. "What is the purpose of `UserDetailsService`?" *(Adapter that loads user data from a store and returns UserDetails for Spring Security to use)*
> 4. "Why should CSRF be disabled for JWT REST APIs?" *(JWT is sent as a Bearer token in a header — browsers don't auto-attach headers, so CSRF attacks can't inject credentials)*
> 5. "What does `anyRequest().authenticated()` do and why is it important?" *(Catch-all: everything not explicitly matched requires authentication — implements deny-by-default)*
> 6. "What does `SessionCreationPolicy.STATELESS` do?" *(Tells Spring Security never to create or use sessions — required for stateless JWT APIs)*

> "Tomorrow in Day 30 we add JWT to this foundation — a filter that reads Bearer tokens, validates them, and populates the SecurityContextHolder on every request without needing a session."

---

## Timing Reference

| Segment | Topic | Time |
|---------|-------|------|
| 1 | Opening: what Spring Security does | 5 min |
| 2 | Architecture (filter chain diagram) | 10 min |
| 3 | Authentication vs Authorization | 5 min |
| 4 | SecurityFilterChain + requestMatchers | 15 min |
| 5 | In-memory authentication | 8 min |
| 6 | BCryptPasswordEncoder | 10 min |
| 7 | CSRF configuration (session vs stateless) | 8 min |
| 8 | Database auth: UserDetails, UserDetailsService, DaoAuthProvider | 15 min |
| 9 | SecurityContextHolder | 5 min |
| 10 | Wrap-up + interview questions | 5 min |
| **Total** | | **~86 min** |

---

## Quick Reference Card

```text
SPRING SECURITY CHEAT SHEET
────────────────────────────────────────────────────────
Dependency:
  spring-boot-starter-security

Core annotations:
  @EnableWebSecurity       → activate and override auto-config
  @Configuration           → required for @Bean methods

Key beans:
  SecurityFilterChain      → define URL rules, login, logout, CSRF
  UserDetailsService       → loadUserByUsername(String) → UserDetails
  PasswordEncoder          → encode(raw), matches(raw, hash)
  AuthenticationProvider   → DaoAuthenticationProvider (wires the above)

Authorization rules (order matters — first match wins):
  .requestMatchers("/path").permitAll()         → public
  .requestMatchers("/admin/**").hasRole("ADMIN") → role required
  .anyRequest().authenticated()                  → deny by default

Password encoding:
  new BCryptPasswordEncoder(12)   → cost factor 12 for production
  encoder.encode(rawPassword)     → hash for storage
  encoder.matches(raw, hash)      → verify during login (NEVER decrypt)

Session vs Stateless:
  SessionCreationPolicy.IF_REQUIRED  → default (session-based apps)
  SessionCreationPolicy.STATELESS    → JWT REST APIs (no sessions)

CSRF:
  Default: ENABLED (required for session-based / form apps)
  .csrf(csrf -> csrf.disable())                  → JWT APIs (safe to disable)
  .csrf(csrf -> csrf.csrfTokenRepository(...))   → SPAs with cookie token

UserDetails interface methods:
  getUsername()           → identify the user
  getPassword()           → the stored hash (Spring calls matches() on it)
  getAuthorities()        → Collection<GrantedAuthority> (roles/permissions)
  isEnabled()             → false = account disabled
  isAccountNonLocked()    → false = account locked
  isCredentialsNonExpired() → false = password expired
  isAccountNonExpired()   → false = account expired
```

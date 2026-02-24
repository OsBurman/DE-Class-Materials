# Day 29 Part 2 — Spring Security: Architecture, Authentication & CSRF
## Lecture Script

**Total Time:** 60 minutes
**Delivery pace:** ~165 words/minute — conversational, instructor-led

---

## [00:00–02:00] Opening — From OWASP Principles to Spring Implementation

In Part 1 we covered what can go wrong and why. Now we cover what Spring Security gives you to stop it.

The most important mental shift for this part: Spring Security is not a set of annotations you sprinkle on methods. It's a security layer that intercepts every single HTTP request before it ever reaches your controller. Understanding how that layer works — physically, mechanically — is what separates developers who configure it correctly from developers who think they've secured their application but haven't.

We'll walk through the filter chain architecture, configure authorization rules with `SecurityFilterChain`, implement two flavors of authentication — in-memory for development and database-backed for production — encode passwords with BCrypt, wire it all together with `DaoAuthenticationProvider`, and configure CSRF protection correctly for different application types.

---

## [02:00–09:00] Slides 2–3 — Spring Security Overview and Filter Chain

When you add `spring-boot-starter-security` to your Maven dependencies and start your application, something happens immediately without you writing a single configuration line. Every endpoint on your application now requires authentication. A login form appears at `/login`. A logout endpoint appears at `/logout`. A random UUID password is printed to the console for a user called `user`. CSRF protection is active. Security headers are being added to every response.

This is the Spring Boot auto-configuration at work. It's a reasonable safe default to bootstrap from — it's saying, "we don't know what your security requirements are, so let's start from 'everything is locked' and you tell us what to open up." That's a good philosophy. But the auto-configured defaults are not production security. They're a starting point. You will always declare your own `SecurityFilterChain` bean, and when you do, your configuration completely replaces the defaults.

Now let's understand how Spring Security actually intercepts requests, because this is fundamental to understanding everything else.

Spring Security is implemented as a chain of servlet filters. Your Spring Boot application, underneath everything, is a servlet application running on embedded Tomcat. Filters are a Java servlet concept — they're interceptors that sit in front of the servlet (in your case, the `DispatcherServlet`) and process every request and response.

Here's the flow. An HTTP request comes in from the browser or API client. It hits the servlet container. The first Spring Security class it encounters is `DelegatingFilterProxy` — this is the bridge between the servlet container's filter mechanism and Spring's IoC container. It delegates to `FilterChainProxy`, which is the real Spring Security entry point.

From there, the request passes through a series of filters, each with one specific job. The `SecurityContextPersistenceFilter` loads the security context — if there's an existing session with an authenticated user, it loads that authentication into the `SecurityContextHolder`. The `UsernamePasswordAuthenticationFilter` is looking for POST requests to `/login` — that's how it handles form login. The `BasicAuthenticationFilter` checks for an `Authorization: Basic` header. The `ExceptionTranslationFilter` sits toward the end and catches two specific exceptions: `AuthenticationException` (you're not logged in) which it converts to a 401 response, and `AccessDeniedException` (you're logged in but not allowed) which becomes a 403. Finally, `FilterSecurityInterceptor` enforces your authorization rules — it checks the current authentication against the rules you configured in `SecurityFilterChain`.

Only after all of these filters run successfully does the request reach your `DispatcherServlet` and eventually your `@Controller`.

The critical implication: your `@Controller` code runs only if all security filters pass. If authentication fails, the controller never sees the request. This is why Spring Security is so effective — the security boundary is physically before your application code, not inside it.

---

## [09:00–14:00] Slide 4 — Authentication vs Authorization

I want to make sure the distinction between authentication and authorization is crystal clear, because people confuse these terms constantly.

Authentication answers: "Who are you, and can you prove it?" It's the login process. The user presents their username and password. Spring Security looks up the user, verifies the password against the stored hash, and if it matches, creates an `Authentication` object that represents the verified identity. That object gets stored in the `SecurityContextHolder` for the duration of the request.

Authorization answers: "Given that I know who you are — are you allowed to do this specific thing?" This happens after authentication. You've proven you're Alice. Now, is Alice allowed to delete books? Only if Alice has the `ROLE_ADMIN` authority.

The order matters: you cannot authorize without first authenticating. You can't check what a user is allowed to do until you know who they are.

Spring Security stores the authenticated user's identity in the `SecurityContextHolder`. This is a thread-local — meaning it's available anywhere in the call stack for the current request thread. In a controller method, you can inject `Authentication auth` as a parameter and get the current user's name and roles. In a service method, you can call `SecurityContextHolder.getContext().getAuthentication()` and get the same object. Day 30 will use this to implement ownership checks — verifying that the authenticated user actually owns the resource they're requesting.

---

## [14:00–22:00] Slides 5–6 — @EnableWebSecurity and SecurityFilterChain Configuration

Let's write the configuration class. This is the class that replaces Spring Boot's auto-configuration with your rules.

Annotate with `@Configuration` and `@EnableWebSecurity`. `@Configuration` tells Spring to process this class for bean definitions. `@EnableWebSecurity` activates Spring Security's web security support. In Spring Boot, it's technically implied by the auto-configuration when you have security on the classpath, but I prefer to be explicit — it makes it obvious that this class is the security configuration.

Declare a `SecurityFilterChain` bean. This method takes an `HttpSecurity` parameter — that's the builder for configuring everything about how HTTP requests are handled. Call `http.build()` at the end and return the result.

The most important configuration call is `authorizeHttpRequests`. This is where you define your authorization rules — who can access what.

Let me walk through a realistic set of rules. You chain matcher-plus-authorization pairs. The rules are evaluated top to bottom, and the first match wins. This means specific rules must come before general rules.

The first rule: `requestMatchers("/api/auth/**").permitAll()` — the authentication endpoints themselves have to be publicly accessible. Users can't log in through an endpoint that requires them to already be logged in.

Second: `requestMatchers(HttpMethod.GET, "/api/books/**").permitAll()` — reading the book catalog is public. Anyone can browse without logging in. Note I'm specifying the HTTP method here — GET only. The same URL pattern for POST will not match this rule.

Third: `requestMatchers("/api/admin/**").hasRole("ADMIN")` — any URL under `/api/admin/` requires the `ADMIN` role. `hasRole("ADMIN")` checks for `ROLE_ADMIN` — Spring Security prepends `ROLE_` automatically when you use `hasRole`. If you want to check the exact authority string, use `hasAuthority("ROLE_ADMIN")`.

Fourth: `requestMatchers("/api/books/**").authenticated()` — write operations on books (POST, PUT, DELETE — everything that didn't match the GET rule above) require any authenticated user.

Fifth: `anyRequest().authenticated()` — the catch-all. Any URL not matched by a previous rule requires authentication. This must always be last.

Also declare a `PasswordEncoder` bean in this class. `new BCryptPasswordEncoder()` returns the standard BCrypt encoder. We're declaring it as a Spring bean so it can be injected anywhere in the application — especially into your `UserDetailsService` for encoding passwords on registration, and Spring Security's authentication machinery also discovers it automatically.

---

## [22:00–28:00] Slide 7 — In-Memory Authentication

For development environments and integration tests, you don't want to set up a real database just to run the application locally. Spring Security provides `InMemoryUserDetailsManager` — a `UserDetailsService` implementation that stores users in application memory.

Declare a `UserDetailsService` bean. Build each user with `User.builder()`. Set the username, password, and roles. One important thing: the password must be encoded. If you pass a plaintext password without encoding, Spring Security will throw an `IllegalArgumentException` with an error about unencoded passwords. This is actually a safety feature — Spring Security refuses to store or use plaintext passwords.

So you inject the `PasswordEncoder` and wrap each password with `passwordEncoder.encode(rawPassword)`.

For roles: `.roles("USER")` stores the authority as `ROLE_USER` internally. When your security rules check `.hasRole("USER")`, Spring checks for `ROLE_USER`. The `ROLE_` prefix handling is automatic — you write `"USER"` in both the user builder and the security rule, and Spring handles the prefix convention. Alternatively, you can use `.authorities("ROLE_USER", "ROLE_ADMIN")` which stores the exact strings you provide.

Return a `new InMemoryUserDetailsManager(user, admin)`. Spring Boot discovers this bean because it implements `UserDetailsService` and automatically wires it into the authentication machinery.

Use in-memory authentication for: local development, integration tests, prototyping before the database schema is ready. Absolutely never for production. Application restart clears all users — nothing persists. Any user you create at runtime is gone the moment the JVM shuts down.

---

## [28:00–36:00] Slide 8 — BCryptPasswordEncoder in Depth

Let's talk about BCrypt specifically, because I want you to understand why it's the standard and what happens when you use it.

First, the property that matters most for password security: BCrypt is deliberately slow. Not accidentally slow — deliberately designed to take milliseconds per hash operation. On a modern GPU, you can compute about sixty billion MD5 hashes per second. Sixty billion. BCrypt with the default cost factor of 10 runs at roughly thirty thousand hashes per second on the same hardware. That difference is why an attacker who gets your user table can crack MD5-hashed passwords in seconds and BCrypt-hashed passwords over centuries.

The cost factor controls how slow BCrypt is. The default is 10 — that's 2^10 = 1,024 rounds of the key derivation function. Increase it to 12 and it's 4,096 rounds. At cost 12, each hash takes about 100–200 milliseconds. That's acceptable overhead for a login operation — users don't notice it. But it makes brute-force attacks 4× harder than cost 10.

The second key property: BCrypt is salted. Every time you call `encoder.encode(rawPassword)`, a new random salt is generated and incorporated into the hash. So encoding the same password twice gives you two completely different hash strings. The salt is embedded in the hash string itself — when Spring Security calls `encoder.matches(rawPassword, storedHash)`, it extracts the salt from the stored hash, runs BCrypt with that salt and the provided raw password, and compares the result. You don't manage the salt separately.

This matters for database breaches. If an attacker gets your user table, they can't use a rainbow table — a precomputed table mapping common passwords to their hashes — because every hash has a unique salt. They have to attack each password individually.

The rules: on registration or password change, call `encoder.encode(rawPassword)` and store the result. On login, Spring Security calls `encoder.matches(rawPassword, storedHash)` for you automatically — you don't call it explicitly during authentication. If you ever need to verify a password in your own code outside of the login flow, use `matches()` explicitly.

Never compare password hashes with `.equals()`. Never do a database lookup like `WHERE password = ?` with the BCrypt hash. The same password produces different hashes every time. The only correct comparison is `encoder.matches()`.

---

## [36:00–44:00] Slides 9–10 — UserDetailsService and Database Authentication

`UserDetailsService` is the interface that makes Spring Security database-agnostic. It has one method: `loadUserByUsername(String username)`. Spring Security calls this during authentication. Your job is to look up the user from wherever they're stored and return a `UserDetails` object. Spring Security handles everything after that — password verification, populating the `SecurityContext`, creating the session.

`UserDetails` is a contract. It gives Spring Security what it needs to work with the user: the username, the stored (already-hashed) password, and the authorities (roles). It also has four boolean flags for account status: `isEnabled()`, `isAccountNonLocked()`, `isAccountNonExpired()`, and `isCredentialsNonExpired()`. If any of these return false, Spring Security will reject the login with the appropriate exception.

Now let's build the full database authentication. Three components.

First, the JPA entity. An `AppUser` with an ID, a username, a password field that stores the BCrypt hash, a role, and an enabled flag. Standard JPA entity annotations.

Second, the repository. A `JpaRepository<AppUser, Long>` with one custom method: `findByUsername(String username)` returning an `Optional<AppUser>`. You'll also want `existsByUsername(String username)` for the registration duplicate check.

Third, the `UserDetailsService` implementation. Annotate it with `@Service`. Inject the repository via constructor. Implement `loadUserByUsername`: look up the user, throw `UsernameNotFoundException` if not found — Spring Security maps this to the same generic "Bad credentials" message as a wrong password, which prevents username enumeration attacks. Then build and return a `UserDetails` using the Spring `User.builder()`, passing the username, the stored password hash, and the role.

One thing worth calling out: you pass `user.getPassword()` — the already-hashed password from the database. Spring Security will call `encoder.matches(rawPasswordFromLoginForm, storedHashFromUserDetails)` to verify the credentials. You do not hash the password again in `loadUserByUsername`.

Spring Boot discovers your `CustomUserDetailsService` bean automatically because it implements `UserDetailsService`. No explicit wiring needed.

---

## [44:00–50:00] Slide 11 — Authentication Providers

`DaoAuthenticationProvider` is the class that orchestrates the authentication process for form-based and basic auth. It takes an `Authentication` object containing the submitted username and password, calls your `UserDetailsService` to load the user, then calls your `PasswordEncoder.matches()` to verify the password. If both succeed, it returns a fully authenticated `Authentication` object that goes into the `SecurityContextHolder`.

Spring Boot auto-configures this completely. When you have a `UserDetailsService` bean and a `PasswordEncoder` bean in your application context, Spring Boot creates a `DaoAuthenticationProvider`, sets your `UserDetailsService` on it, sets your `PasswordEncoder` on it, and registers it. You don't have to write this code. I'm showing you the explicit version so you understand what Spring is doing, not because you need to write it.

You would write a custom `AuthenticationProvider` in specific scenarios. Multi-factor authentication — after password verification succeeds, you need to check a TOTP code from the user's authenticator app before completing the login. Multiple authentication sources — try the database first, fall back to LDAP. Custom credential types — API key authentication where the client sends an API key header instead of a username/password. Day 30's JWT authentication uses a different mechanism — a custom filter — rather than a custom `AuthenticationProvider`, but the concepts are related.

When multiple `AuthenticationProvider` beans are registered, Spring Security calls them in order until one returns a non-null result or all have thrown `AuthenticationException`. This is the provider chain.

---

## [50:00–57:00] Slide 12 — CSRF Configuration

CSRF protection. Spring Security enables it by default for web applications. But the right configuration depends on what kind of application you're building, and getting this wrong is a common mistake.

Three scenarios.

First: traditional web application using Thymeleaf forms. Leave CSRF on. Don't touch it. Spring Security and Thymeleaf have tight integration — when you use `th:action="@{/submit}"` on a form, Spring Security's Thymeleaf dialect automatically injects a hidden `_csrf` input field containing the CSRF token. When the form is submitted, Spring validates the token. The developer writes zero CSRF-specific code for this to work.

Second: Single-page application in React or Angular that makes AJAX requests to your REST API, where the API still uses session cookies for authentication. This is less common now but exists. Use `CookieCsrfTokenRepository.withHttpOnlyFalse()`. Spring Security sets a `XSRF-TOKEN` cookie that JavaScript can read (it must not be HttpOnly for this to work). Angular's `HttpClient` reads this cookie automatically and sends the token back as an `X-XSRF-TOKEN` header on every modifying request. React requires you to read the cookie and add the header manually.

Third: stateless REST API where clients authenticate with JWT tokens in the `Authorization: Bearer` header. Disable CSRF. This is safe because the entire attack relies on cookies. CSRF attacks work because browsers automatically attach cookies to requests from any origin. A stateless JWT API doesn't use session cookies — the client stores the JWT and puts it in the `Authorization` header manually. Cross-site HTML forms cannot set custom headers — they can only send the browser's cookies. So a forged cross-site form submission will arrive at your server without the JWT, and will be rejected as unauthenticated. There's no CSRF attack vector.

When you disable CSRF for a JWT API, you also typically set session creation policy to `STATELESS`. This tells Spring Security not to create or use HTTP sessions at all — every request must be independently authenticated via the JWT. We'll implement this fully in Day 30.

The rule: session cookies → keep CSRF on. JWT headers → disable CSRF.

---

## [57:00–60:00] Slides 13–15 — Registration, @WithMockUser, and Summary

Quick note on user registration. The most important rule is the one everyone violates at least once: hash the password before saving. Call `passwordEncoder.encode(request.getPassword())` and store that result. Not the raw password — the hash. Check for duplicate usernames first to give a useful 409 Conflict response. Return 201 Created with an empty body. Do not return the saved user entity in the response — it contains the password hash, and while a BCrypt hash can't be reversed, there's no reason to expose it.

Testing secured endpoints. From Day 28, you know how to write `@WebMvcTest` tests. Now that your endpoints are secured, undecorated test requests will get 401 responses. `@WithMockUser` is the solution. It's an annotation that injects a mock `Authentication` into the `SecurityContext` before the test method runs. `@WithMockUser(username = "alice", roles = {"USER"})` gives you an authenticated user named alice with the USER role, without going through the login process. Write tests for the unauthenticated case (expect 401), the correct role (expect 200), and the wrong role (expect 403). For POST/PUT/DELETE tests with CSRF enabled, add `.with(csrf())` to the MockMvc request. For CSRF-disabled APIs, you don't need it.

To summarize everything in Part 2: Spring Security's filter chain sits in front of your DispatcherServlet — it's not inside your code, it's a layer before your code. Authentication is "who are you," authorization is "are you allowed." Declare a `SecurityFilterChain` bean with your rules — first match wins, catch-all last. In-memory auth for dev and tests, database auth via `UserDetailsService` for production. BCrypt for passwords — encode on save, matches on verify, never equals. `DaoAuthenticationProvider` wires your `UserDetailsService` and `PasswordEncoder` and is auto-configured when both beans are present. CSRF on for session-based apps, off for stateless JWT APIs.

Day 30 takes this foundation and extends it with stateless JWT authentication — you'll implement a custom filter that validates JWTs on every request instead of relying on sessions. You'll also get method-level security with `@PreAuthorize` and `@Secured`, and role-based access control that builds on the roles you've configured today.

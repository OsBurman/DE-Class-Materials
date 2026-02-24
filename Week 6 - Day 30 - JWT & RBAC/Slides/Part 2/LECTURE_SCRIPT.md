# Day 30 Part 2 — RBAC, Method Security, Auth Failure Handling, and REST API Security
## Lecture Script

---

**[00:00–02:30] Opening — From Authentication to Authorization**

Welcome back. In Part 1 we built the complete authentication pipeline: JWT generation, validation, the custom filter that intercepts every request, and the security config that wires it together. By the end of Part 1, Spring Security knows who you are on every request.

Part 2 answers a different question: now that we know who you are, what are you allowed to do?

Authentication and authorization are two separate concerns, and mixing them up in code is a common mistake. Authentication says "this is alice." Authorization says "alice is allowed to delete books, but not manage users." Part 1 was all authentication. Part 2 is all authorization.

We'll cover four main areas. Role-Based Access Control — how to model permissions as roles and fine-grained authorities. Method-level security — the `@PreAuthorize` annotation that lets you enforce access rules directly on service methods. Failure handling — how to return clean JSON 401 and 403 responses instead of HTML redirects. And REST API security best practices — HTTPS, API keys, and rate limiting concepts that round out a production-ready security story.

Let's go.

---

**[02:30–09:00] Slide 2 — RBAC Principles: Roles vs Permissions**

Role-Based Access Control is the pattern where you assign users to roles, and roles determine what actions are allowed. In Spring Security, every piece of authorization ultimately comes down to checking a user's "authorities" — the collection of strings returned by `getAuthorities()` on the `UserDetails`.

Here's the naming convention you need to understand. Roles are authorities with the `ROLE_` prefix. When you call `hasRole("ADMIN")` in a `@PreAuthorize` annotation, Spring Security internally looks for an authority named `ROLE_ADMIN`. That's a convention. If you call `hasAuthority("BOOK_DELETE")`, it looks for an authority named exactly `BOOK_DELETE` — no prefix added.

So you can have coarse-grained roles like `ROLE_ADMIN` and `ROLE_USER`, and fine-grained permissions like `BOOK_READ`, `BOOK_DELETE`, `USER_MANAGE`, all living in the same authorities collection. A user might have `ROLE_USER`, `BOOK_READ`, and `BOOK_DELETE` but not `BOOK_CREATE`. That's more expressive than just "user vs admin."

In your `UserDetails` implementation, the `getAuthorities()` method returns a collection of `SimpleGrantedAuthority` objects — just wrappers around a string. You build this from whatever roles and permissions are stored in your database.

Now here's a subtle but important optimization for JWT-based systems. Instead of loading roles from the database on every request — which would add a DB hit to every API call — you can include the roles in the JWT claims at login time and read them directly from the token in the filter. The JWT carries the user's authorities, the filter reads them, builds the `GrantedAuthority` collection, and sets it in the `SecurityContext`. No additional database query. This is a common pattern in production JWT implementations.

---

**[09:00–16:00] Slide 3 — URL-Based Security with requestMatchers**

Let's talk about URL-level authorization first — protecting endpoints by role as part of the `SecurityFilterChain` config.

You already saw `anyRequest().authenticated()` yesterday. Now let's add role-specific rules. The method is `requestMatchers()` — and I want to flag something immediately. If you're following a tutorial that uses `antMatchers()`, that method was deprecated in Spring Security 5.8 and completely removed in Spring Security 6, which ships with Spring Boot 3. Everything we write uses `requestMatchers()`. Syntax is otherwise identical.

The pattern is: `.requestMatchers(HttpMethod, pathPattern).hasRole("ROLENAME")`. You can specify the HTTP method to make it even more precise. `GET /api/books/**` can be public — reading books doesn't require login. `POST /api/books/**` requires `ROLE_ADMIN` — only admins create books. `DELETE /api/books/**` also `ROLE_ADMIN`. `GET /api/admin/**` — any admin-prefixed path — also restricted.

Rule ordering is critical. Spring Security evaluates these rules from top to bottom and stops at the first match. So your most specific rules go first — specific path plus specific HTTP method. Then broader paths. `anyRequest().authenticated()` always goes last as the catch-all. If you put `anyRequest().authenticated()` first, it would match everything and none of your other rules would fire.

URL-based rules are good for coarse-grained access control — protecting whole areas of your API. But they have a fundamental limitation. You can say "admins can delete books" — you can't say "a user can delete their own book but not another user's book" purely through URL patterns. For that, you need method-level security. Let's look at that now.

---

**[16:00–26:00] Slides 4–5 — @PreAuthorize, @PostAuthorize, and @Secured**

Three annotations you need to know. All live in `spring-security-core` — no extra dependency. But you must activate them.

On your `SecurityConfig` class, add `@EnableMethodSecurity`. That's the Spring Security 6 way. In Spring Security 5 it was `@EnableGlobalMethodSecurity(prePostEnabled = true)`. Use `@EnableMethodSecurity` — it enables `@PreAuthorize` and `@PostAuthorize` by default.

`@PreAuthorize` is the one you'll use the most. It runs before the method body executes and evaluates a Spring Expression Language string. If the expression is false, the method is blocked — either a 401 if the user isn't authenticated, or a 403 if they're authenticated but the expression returns false.

The simplest forms: `@PreAuthorize("hasRole('ADMIN')")` — check for `ROLE_ADMIN` authority. `@PreAuthorize("hasAnyRole('USER', 'ADMIN')")` — either role works. `@PreAuthorize("hasAuthority('BOOK_DELETE')")` — specific fine-grained permission, no `ROLE_` prefix.

Now the powerful form — SpEL expressions. The `authentication` variable is always available and refers to the current `Authentication` object in the `SecurityContext`. `authentication.name` gives you the username. So `@PreAuthorize("authentication.name == #username")` — the `#username` syntax refers to the method parameter named `username`. This lets you say "you can only access this method if you're asking for your own username."

Put them together: `@PreAuthorize("hasRole('ADMIN') or authentication.name == #username")`. Admins can do anything. Regular users can only do it for themselves. That one expression replaces several dozen lines of boilerplate authorization code that you'd otherwise write manually inside the method.

The most powerful form: calling a Spring bean in SpEL. `@PreAuthorize("@bookSecurityService.isOwner(authentication.name, #bookId)")`. The `@` prefix means "look up this bean in the application context." Then call any method on it with any combination of the authenticated user and method parameters. This lets you offload complex authorization logic into a dedicated service class that you can unit test independently.

`@PostAuthorize` works the same way but runs after the method body completes. The key difference: the method runs first, then the authorization check happens on the return value. `returnObject` is the variable for the return value. `@PostAuthorize("returnObject.username == authentication.name or hasRole('ADMIN')")` — execute the query, get the user object back, then check: is this user asking for their own data? If not, Spring throws `AccessDeniedException`.

Important warning about `@PostAuthorize`: because the method runs before the auth check, any side effects — database writes, email sends — happen regardless of whether authorization succeeds. For read methods, that's fine. For writes, use `@PreAuthorize` so you block before any side effects.

`@Secured` is the simpler cousin. No SpEL, role names only, supports multiple roles as an array. `@Secured("ROLE_ADMIN")`, `@Secured({"ROLE_USER", "ROLE_ADMIN"})`. You need to enable it separately: `@EnableMethodSecurity(securedEnabled = true)`. It's less flexible but less complex. You'll see it in older codebases. For new code, `@PreAuthorize` is the recommendation.

---

**[26:00–33:00] Slide 6 — SecurityContextHolder: Reading the Current User**

Let's talk about accessing the authenticated user in your service code. This is something you'll need constantly — every resource creation needs to know who the owner is, every audit log needs the actor, every ownership check needs the username.

`SecurityContextHolder` maintains a thread-local `SecurityContext`. Thread-local means each request thread has its own isolated context. The `JwtAuthenticationFilter` wrote to it at the start of the request — set the `Authentication` object with the username and authorities. For the duration of that request's thread, any code can read from it.

The way to read it: `SecurityContextHolder.getContext().getAuthentication()`. That gives you the `Authentication` object. `.getName()` gives the principal's name — the username. `.getAuthorities()` gives the roles collection. `.getPrincipal()` gives the full `UserDetails` object if that's what you set as the principal.

In a service class, this works fine. Call `SecurityContextHolder.getContext().getAuthentication().getName()` wherever you need the username. It's a static access, which is a little unusual in Spring code where we usually inject dependencies, but it's the standard pattern for Spring Security.

In a controller, there's a cleaner way. Add `@AuthenticationPrincipal UserDetails currentUser` as a parameter to your handler method. Spring automatically injects the current principal from the `SecurityContext`. `currentUser.getUsername()` gives you the name. `currentUser.getAuthorities()` gives the roles. This is the preferred controller-level approach — it's more testable and more readable than calling the static holder.

You can even create a custom annotation that extends `@AuthenticationPrincipal` if you always want to inject your concrete `User` entity type instead of the `UserDetails` interface — that's a refinement for later.

One gotcha: `@Async` methods. By default, Spring Security copies the `SecurityContext` to child threads through a `DelegatingSecurityContextExecutor`. But if your async executor isn't configured with security context propagation, the async method will have an empty context. Configure `SecurityContextHolder.setStrategyName(SecurityContextHolder.MODE_INHERITABLETHREADLOCAL)` to propagate it. We won't implement that today but know the issue exists.

---

**[33:00–40:00] Slide 7 — Ownership Checks: Custom Authorization Logic**

URL rules and `hasRole()` checks are great for coarse-grained access. But what about: "a user can update their own review but not another user's review"? That's a resource ownership check — and it's one of the most common authorization patterns in real APIs.

The recommended approach: a dedicated security service bean with an ownership check method, called from `@PreAuthorize` using SpEL's bean reference syntax.

Create `BookSecurityService` or `ReviewSecurityService` annotated with `@Service`. Give it a method like `isOwner(String username, Long resourceId)` that queries the repository and returns true if the given username owns the given resource. This method is now a testable unit.

In your `@PreAuthorize`: `@bookSecurityService.isOwner(authentication.name, #reviewId)`. The `authentication.name` gives the current username. `#reviewId` is the method parameter. Boolean return value from the service method determines whether Spring grants access.

Combine with role check: `hasRole('ADMIN') or @reviewSecurityService.isOwner(authentication.name, #reviewId)`. Admins override all ownership restrictions. Users can only touch their own stuff.

The alternative — checking inside the method body — works but has downsides. You're mixing authorization logic into business logic. It's harder to test. It only fires when the method is called; it can't be enforced declaratively. Using `@PreAuthorize` with a service bean keeps the "who can run this method" concern separate from the "what does this method do" concern.

---

**[40:00–49:00] Slides 8–10 — AuthenticationEntryPoint and AccessDeniedHandler**

Now let's handle what happens when authorization fails. Two different failure scenarios, two different handlers.

First: the 401 scenario. An unauthenticated request — no token, expired token, invalid signature — reaches a protected endpoint. Spring Security's default response is a 302 redirect to `/login`. Your REST API client — an Angular app, a React frontend, a mobile app — does not want an HTML redirect. It wants a JSON 401 with a useful message.

The fix is `AuthenticationEntryPoint`. Create a class that implements it and annotate with `@Component`. The one method to implement is `commence()`, which receives the `HttpServletRequest`, `HttpServletResponse`, and the `AuthenticationException` that was thrown. In `commence()`: set the content type to `application/json`, set the status to 401, write your JSON body to the response output stream.

I'm using an injected `ObjectMapper` to serialize a `Map<String, Object>` to JSON. The map has status, error, message, and path. Status 401, error string "Unauthorized", message from the exception, path from `request.getServletPath()`. Clean, consistent format.

Second: the 403 scenario. An authenticated user — valid token, signature checks out, user loaded — tries to access a resource they don't have permission for. `@PreAuthorize` or URL rule blocks them. `AccessDeniedHandler.handle()` is called.

Same pattern: implement `AccessDeniedHandler`, annotate `@Component`, write JSON to the response. Status 403, error "Forbidden", message "You don't have permission to access this resource."

The 401 vs 403 distinction is worth emphasizing to your class because it's commonly confused. The HTTP spec's naming is genuinely misleading — 401 is called "Unauthorized" but it means "unauthenticated." 403 is "Forbidden" and means "authenticated but not authorized." Always: 401 means "I don't know who you are," 403 means "I know who you are but you can't do this."

To wire these handlers in, add `.exceptionHandling()` to your `HttpSecurity` chain in `SecurityConfig`. `.authenticationEntryPoint(authEntryPoint)` registers the 401 handler. `.accessDeniedHandler(accessDeniedHandler)` registers the 403 handler. Now your API returns clean JSON errors instead of HTML.

---

**[49:00–54:00] Slide 11 — REST API Security Best Practices**

Let's talk about the broader security posture for REST APIs. These are the practices that separate a production API from a demo project.

Number one: HTTPS. Everything in production must be encrypted. A valid JWT in an HTTP request is readable by any network observer — the entire header including the token travels in plaintext. HTTPS encrypts the whole request, not just the body. No exceptions in production.

Number two: never put sensitive data in URLs. URLs end up in browser history, server access logs, nginx logs, load balancer logs, anywhere. `GET /api/password-reset?token=abc123` — that token is now in logs. Use a POST with the token in the body instead. Same goes for API keys — never in query parameters.

Number three: don't expose implementation details in error messages. Your error response should say "Username already taken" not "org.springframework.dao.DataIntegrityViolationException: ERROR: duplicate key value violates unique constraint." Attackers learn about your database schema from error messages.

Number four: validate all input even for authenticated users. A valid JWT means you know who the user is. It doesn't mean their input is safe. SQL injection, XSS, and business logic exploits can come from authenticated users — either malicious or compromised. Run `@Valid` on all `@RequestBody` parameters.

Number five: version your API. `/api/v1/books` and `/api/v2/books`. This lets you make security changes in v2 without breaking existing v1 clients immediately. You can deprecate v1 on a schedule.

One brief mention of CORS — Cross-Origin Resource Sharing — because it comes up in REST API security. Configure it explicitly on your `SecurityFilterChain`. Don't use `@CrossOrigin("*")` in production — it allows requests from any origin. Configure a `CorsConfigurationSource` bean that whitelists specific origins. We'll cover full CORS configuration in Week 9 when we do the API security review.

---

**[54:00–57:30] Slides 12–13 — HTTPS Configuration and API Keys**

HTTPS in Spring Boot — two options.

The simple option for development: generate a self-signed certificate with `keytool`, the JDK's key management utility. Five-line command, generates a PKCS12 keystore file. Add four properties to `application.properties`: `server.ssl.key-store`, `server.ssl.key-store-password`, `server.ssl.key-store-type`, and `server.port=8443`. Restart. Spring Boot now serves HTTPS on 8443 and HTTP on the old port. Browsers will warn about the self-signed cert — that's expected in development.

For production: terminate TLS at the load balancer. Your AWS ALB or nginx reverse proxy handles the certificate — usually via Let's Encrypt, which is free and auto-renews. The load balancer decrypts HTTPS and forwards HTTP to your Spring Boot instance on an internal network port. Spring Boot never sees a certificate. This is the standard cloud deployment pattern. Simpler, more flexible, easier certificate renewal.

API keys — a different authentication pattern for when a JWT doesn't fit. The classic case: service-to-service communication. Service A is a reporting service that calls Service B's data API. There's no user identity involved — it's machine-to-machine. API keys are the right tool.

The pattern: the client sends `X-API-Key: <key>` in the request header. Your API key filter — another `OncePerRequestFilter` — extracts the key, hashes it with SHA-256, looks up the hash in the database. If found and not revoked, create an `Authentication` object with the service name and appropriate authorities. Put it in the `SecurityContext`.

Why hash? Same reason you hash passwords — you don't store the raw secret. The difference from BCrypt: API keys are long random strings (UUID or 32+ random bytes), so SHA-256 is appropriate and much faster than BCrypt. You don't need BCrypt's intentional slowness when the secret is already high-entropy random.

Show the key only once at creation — like GitHub personal access tokens — because you only store the hash, not the plaintext.

---

**[57:30–60:00] Slide 14 — Rate Limiting, Slide 15 — Summary**

Rate limiting. Why does it matter? Three main reasons. DoS protection — a single client hammering your login endpoint 10,000 times per second exhausts your server resources. Brute-force prevention — without rate limiting, a patient attacker can try every password in a dictionary against the login endpoint. Cost control — if your API calls a paid downstream service, unrestricted callers can run up your bill.

The most intuitive algorithm is the token bucket. Imagine a bucket that holds N tokens. The bucket refills at a constant rate — say, 20 tokens per minute. Each API request consumes one token. If the bucket is empty, the request is rejected with a 429 Too Many Requests. If there are tokens, the request is allowed and a token is removed. This naturally allows short bursts (the bucket is full) while enforcing a long-term average rate.

Bucket4j is the most popular Spring Boot rate limiting library. The Maven dependency is `bucket4j-core`. Create a `Bucket` bean with a `Bandwidth` configuration. In a `OncePerRequestFilter`, call `bucket.tryConsume(1)` — returns true if a token was available. If false, write a 429 response with a `Retry-After` header saying how many seconds to wait.

The example in the slides is one global bucket. Production systems use per-user or per-IP buckets — a `ConcurrentHashMap<String, Bucket>` where the key is the username from the `SecurityContext` or the client IP from `request.getRemoteAddr()`. That way one bad actor doesn't throttle everyone.

HTTP 429 is the correct status code for rate limiting. Always include `Retry-After` — it tells clients when they can try again without making them guess or poll.

Let's land the plane. Day 29 and Day 30 together give you a complete production security stack. BCrypt for password hashing. UserDetailsService for loading users. DaoAuthenticationProvider for credential validation. SecurityFilterChain for overall config. JwtService for token generation and validation. JwtAuthenticationFilter for stateless authentication on every request. PreAuthorize for method-level authorization. AuthenticationEntryPoint for 401. AccessDeniedHandler for 403. HTTPS for transport security. API keys for machine-to-machine. Rate limiting for abuse prevention.

Week 7 shifts gears completely — Day 31 is GraphQL. Instead of multiple REST endpoints, a single endpoint that accepts typed queries specifying exactly what data clients want. Spring has excellent support through Spring for GraphQL. It's a different mental model and a lot of fun to build. See you then.

# Walkthrough Script — Part 2: RBAC & Security Best Practices

**Day 30: JWT & RBAC**
**Total estimated time: ~90 minutes**

---

## Segment 1 — Opening and Recap (5 min)

"Welcome back from the break. In Part 1 we built the authentication side — the mechanism
that proves WHO you are. The JWT carries your identity.

Part 2 is about authorization — what you're ALLOWED to do once we know who you are.
And we're going to finish with a full production security checklist — the real-world
best practices that separate a secure REST API from a vulnerable one.

We have two files:
- `01-rbac-and-method-security.java` — RBAC design, @PreAuthorize, @Secured, SecurityContextHolder
- `02-security-best-practices.java` — custom error handlers, API keys, rate limiting, HTTPS, token management

Let's start with RBAC."

---

## Segment 2 — RBAC Concepts (10 min)

**[Open: `01-rbac-and-method-security.java` — Section 1, RBAC comments]**

"Role-Based Access Control — RBAC. The concept is simple: instead of granting individual
permissions to each user, you define ROLES, and each role has a set of permissions.
Users are assigned to roles.

Look at our bookstore model in the comment block:
- USER → browse, borrow, view own orders
- LIBRARIAN → USER privileges plus manage inventory
- ADMIN → everything

This is hierarchical RBAC. Notice that LIBRARIAN includes USER's permissions. In practice
you achieve this by just giving LIBRARIAN users both roles, or by building an explicit
hierarchy.

**Important distinction:** In Spring Security:
- ROLE: a coarse bucket — `ROLE_ADMIN`
- AUTHORITY: a fine-grained permission — `books:write`, `plan:premium`

`hasRole('ADMIN')` automatically checks for `ROLE_ADMIN` — it prepends ROLE_ for you.
`hasAuthority('ROLE_ADMIN')` is equivalent — you provide the full string yourself.

For simple apps, roles are enough. For fine-grained apps (e.g., a SaaS with different
feature flags per plan), you'd use custom authorities.

**Question:** In our JWT, where do we store the user's roles?"

*[Expected: the `roles` claim in the payload.]*

"Right. We store `'ROLE_USER,ROLE_ADMIN'` in the `roles` claim. When JwtAuthenticationFilter
creates the UsernamePasswordAuthenticationToken, it sets the authorities from UserDetails —
which loaded roles from that claim."

---

## Segment 3 — @EnableMethodSecurity (5 min)

**[Point to Section 2 — @EnableMethodSecurity]**

"Before any method-level annotation does anything, you need to enable it. Look at the config class.

`@EnableMethodSecurity` is the key annotation. In Spring Security 6, this replaces the older
`@EnableGlobalMethodSecurity`. If you're on an older Spring version, you might see the old one
in legacy code.

Three flags:
- `prePostEnabled = true` — enables @PreAuthorize and @PostAuthorize. Default true.
- `securedEnabled = true` — enables the older @Secured annotation.
- `jsr250Enabled = true` — enables @RolesAllowed from Java EE standard.

**Watch out:** The most common mistake on Day 1 with method security is putting @PreAuthorize
on a method and wondering why it does nothing. Forgot to add @EnableMethodSecurity. The annotation
is SILENTLY IGNORED without it. No error, no warning — it just doesn't work."

---

## Segment 4 — URL-Based Security vs Method Security (8 min)

**[Point to the `rbacFilterChain()` method in Section 2]**

"Look at the requestMatchers rules. Let me explain the layering.

You have TWO layers of security:

Layer 1 — URL-based (filter chain): fires BEFORE the request reaches any code.
Layer 2 — Method-based (@PreAuthorize): fires INSIDE the service layer via AOP.

Why use both? Defense in depth. URL rules are your first wall — they block broad patterns.
Method rules are your second wall — they enforce specific business logic inside.

**Watch out:** The ORDER of requestMatchers rules matters. Rules are evaluated top to bottom.
The FIRST matching rule wins. Put specific rules before broad ones.

Look at the example:
```
.requestMatchers(HttpMethod.DELETE, "/api/books/**").hasRole("ADMIN")
.requestMatchers(HttpMethod.POST, "/api/books/**").hasAnyRole("ADMIN", "LIBRARIAN")
.anyRequest().authenticated()
```

The DELETE rule is first — it's more specific. If we put `anyRequest().authenticated()` FIRST,
every request would match it and none of the role rules would ever fire.

`anyRequest().authenticated()` must ALWAYS be last — it's the catch-all. This is 'deny by
default' — if a path isn't explicitly permitted, authentication is required.

**Question:** What happens if I completely remove the `anyRequest().authenticated()` line?"

*[Expected: unauthenticated users could access any path not explicitly restricted.]*

"Exactly — every endpoint you forgot to mention becomes public. That's a security hole.
Always end with `anyRequest().authenticated()`."

---

## Segment 5 — @PreAuthorize Deep Dive (15 min)

**[Point to Section 3 — BookService]**

"Here's where @PreAuthorize gets powerful. These are all Spring Expression Language (SpEL)
expressions — a mini query language evaluated at runtime.

Let's go through each pattern:"

**[Point to `deleteBook()` — hasRole]**

"`hasRole('ADMIN')` — simplest form. Only ROLE_ADMIN users can call this. If anyone else
calls it, they get `AccessDeniedException`, which becomes a 403."

**[Point to `addBookToInventory()` — hasAnyRole]**

"`hasAnyRole('ADMIN', 'LIBRARIAN')` — either role works. OR semantics."

**[Point to `publishBook()` — hasAuthority]**

"`hasAuthority('books:publish')` — exact string match. No ROLE_ prefix added. Use this
for custom fine-grained permissions."

**[Point to `getAllUserEmails()` — compound AND]**

"`hasRole('ADMIN') and isAuthenticated()` — compound expression. You can use `and` and
`or` in SpEL. Though here, `isAuthenticated()` is redundant if you have `anyRequest().authenticated()`
at the URL level — just showing the syntax."

**[Point to `getOrderHistory()` — #parameterName]**

"This one is really important — `#username == authentication.name or hasRole('ADMIN')`.

The `#username` refers to the method parameter named `username`. This is how you implement
ownership checks — a user can only read their OWN order history, unless they're admin.

Try calling `getOrderHistory('alice')` as Bob → 403.
Try calling `getOrderHistory('alice')` as Alice → ✅
Try calling `getOrderHistory('alice')` as Admin → ✅

This pattern appears constantly in real APIs — 'edit your own profile', 'view your own orders',
'delete your own posts'."

**[Point to `editBook()` — bean reference]**

"Here's the advanced pattern. `@bookAuthorizationService.canEditBook(authentication, #bookId)`.
The `@` prefix in SpEL means 'call a Spring bean'. `bookAuthorizationService` is the bean name.
`canEditBook` is the method. It receives the full Authentication object and the book ID.

This is how you move complex authorization logic OUT of annotations and INTO a testable service
class. Much cleaner and unit-testable than putting 5 conditions in one annotation string."

---

## Segment 6 — @PostAuthorize and @Secured (8 min)

**[Point to Section 4 — BookQueryService]**

"`@PostAuthorize` — runs AFTER the method. The `returnObject` SpEL variable holds the
return value.

Use case: you might not know whether the user is authorized until you've fetched the data.
Say a book has an `ownerUsername` field. After fetching it from the DB, check whether
the return value's owner matches the current user.

**Watch out:** The method ALWAYS executes. The database query happens. Resources are consumed.
PostAuthorize only controls whether you see the result. If the method is expensive, prefer
@PreAuthorize with a lookup first."

**[Point to Section 5 — @Secured]**

"`@Secured` is the legacy annotation from older Spring Security versions. You'll see it
in code written before Spring Security 4-5. Key differences:
- Requires the full `ROLE_` prefix (does NOT auto-prepend like hasRole)
- No SpEL — role strings only
- Multiple roles in an array use OR semantics

For new code: always use `@PreAuthorize`. It's more powerful. `@Secured` is here so you
recognize it when you see it in a codebase."

---

## Segment 7 — Custom Authorization Logic (7 min)

**[Point to Section 6 — BookAuthorizationService]**

"The `@bookAuthorizationService` bean we referenced earlier. Let's see what it actually does.

`canEditBook()` receives the Authentication object and a book ID. First it checks for null
and isAuthenticated() — always do defensive null checks on Authentication. A mis-configured
filter could leave it null.

Then it streams the authorities to check for ROLE_ADMIN. If admin → true.
Otherwise, it looks up the book's owner and compares to the current username.

This is clean, testable authorization logic. You can write a unit test that creates a mock
Authentication with different roles and verifies the boolean result. Much better than
trying to test a complex SpEL annotation.

`canAccessPremiumContent()` shows authority-based checks — looking for specific permissions
like 'plan:premium' rather than roles. This is how you'd implement feature flags or
subscription tier access control."

---

## Segment 8 — SecurityContextHolder in Controllers (7 min)

**[Point to Section 7 — RbacDemoController]**

"Three equivalent ways to access the authenticated user:"

**[Point to `usingContextHolder()`]**

"Direct access: `SecurityContextHolder.getContext().getAuthentication()`. Always works.
Verbose but explicit. Use when you need the full Authentication object."

**[Point to `usingPrincipal()`]**

"`@AuthenticationPrincipal UserDetails currentUser`. Spring MVC annotation — injects the
principal directly. Cleanest for controllers. Works because Spring reads it from the
SecurityContextHolder automatically."

**[Point to `usingAuthentication()`]**

"Inject `Authentication authentication` as a method parameter. Spring MVC also injects this
automatically. Slightly less type-safe than @AuthenticationPrincipal (it's the raw
Authentication interface, not the UserDetails subtype) but sometimes useful."

**Question:** "Which approach would you use if you needed to check `isAccountNonLocked()` on the user?"

*[Expected: @AuthenticationPrincipal with casting to BookstoreUserDetails.]*

---

## Segment 9 — Custom Error Handlers (10 min)

**[Switch to: `02-security-best-practices.java` — Section 1]**

"Now the second file. Start with error handling — this is something most junior devs skip
and it makes a huge difference in API quality.

`BookstoreAuthenticationEntryPoint` implements `AuthenticationEntryPoint`.
Spring Security calls this when a request hits a secured endpoint with no valid credentials.
Without this, Spring would redirect to a login page — useless for an API client.

Our handler: set status 401, set Content-Type to application/json, write a structured JSON
body with status, error, message, timestamp, path.

**Watch out:** The timestamp is ISO-8601 from `Instant.now().toString()`. Standard format.
The `path` is `request.getRequestURI()` — tells the client which endpoint they hit."

**[Point to Section 2 — AccessDeniedHandler]**

"`BookstoreAccessDeniedHandler` handles the 403. Very similar structure, but status is 403
and the message says 'You don't have permission'.

Here's the critical distinction that trips up developers:

401 Unauthorized = not authenticated. Who are you? I don't know.
403 Forbidden = authenticated, but not permitted. I know exactly who you are. You're just not allowed.

If Alice (ROLE_USER) tries to hit /api/admin with a valid token → 403.
If someone hits /api/admin with no token → 401.

The naming is famously confusing — 401 says 'Unauthorized' in the HTTP spec, but it means
'unauthenticated'. 403 says 'Forbidden', which IS what 'unauthorized' actually means."

---

## Segment 10 — API Keys (8 min)

**[Point to Section 3 — ApiKeyAuthFilter]**

"API keys are for machine-to-machine calls — no human user involved. Think of a payment
processor posting webhooks to your API, or a data pipeline service fetching book data.

Pattern: include `X-API-Key: <key>` in the request header.

Our filter: if the header is present, look up the key in our store, get the service name,
create an Authentication with ROLE_SERVICE and api:read/write authorities.

Notice we store only the mapping from key to service name. In production, you'd hash the
key in storage (SHA-256 or bcrypt) just like passwords. The client holds the raw key;
you store the hash. Never store raw API keys.

What to look for in a good API key system:
- Long keys: at least 32 bytes of random data
- Stored hashed in the database
- Scoped permissions per key
- Key rotation support
- Audit log of which key accessed what
- Ability to revoke individual keys without affecting others"

---

## Segment 11 — Rate Limiting (10 min)

**[Point to Section 4 — RateLimitingFilter]**

"Rate limiting. Let me explain the algorithm, then the code.

Token bucket — the most common algorithm:
- Each client (user or IP) has a bucket
- The bucket starts full with N tokens
- Each request costs one token
- Tokens refill at a fixed rate — 100 per minute means refilling after 60 seconds
- If the bucket is empty — 429 Too Many Requests

Why rate limit?
- Prevent brute force on /api/auth/login (someone guessing millions of passwords)
- Prevent DDoS (flood with requests)
- Prevent scraping (bulk data harvesting)
- Protect expensive endpoints (complex aggregation queries)

Our filter: `getClientIdentifier()` — first checks if the user is authenticated. If yes,
rate limit by username. If anonymous, rate limit by IP. This is smarter than IP-only —
multiple users behind the same NAT/proxy wouldn't all share a limit.

For X-Forwarded-For: clients behind a load balancer will have the LB's IP as remoteAddr.
The real client IP is in X-Forwarded-For. We take the FIRST IP in the list — that's the
original client. (Subsequent IPs are proxy hops.)

**Watch out:** Our in-memory implementation doesn't work across multiple servers. If you
have two API servers and Alice's requests alternate between them, she effectively gets
double the rate limit. Production: use Redis-backed rate limiting (Bucket4j + Redis)."

**[Point to the 429 response]**

"Return status 429. Set the `Retry-After` header — tells the client how many seconds to wait.
This is a standard HTTP header. Good API clients will respect it and back off automatically."

---

## Segment 12 — HTTPS and Token Management (5 min)

**[Point to Section 5 — HTTPS comments]**

"HTTPS. In production, you almost always terminate TLS at your load balancer or reverse proxy
(nginx, AWS ALB). Spring Boot sees plain HTTP internally. That's fine — the transport between
load balancer and app server is usually within a private VPC.

If you need Spring Boot to handle TLS directly, configure it in application.properties with
a PKCS12 keystore. You'd use this for local dev HTTPS testing.

HSTS — HTTP Strict Transport Security. Once a browser receives this header, it ONLY uses
HTTPS for this domain for `max-age` seconds. If your site ever temporarily serves HTTP,
browsers that have seen HSTS will refuse to connect. One year is standard."

**[Point to Section 6 — TokenBlacklistService]**

"Token management patterns. Three important patterns:

Refresh token rotation: every time you use a refresh token to get a new access token,
you also issue a new refresh token and invalidate the old one. If someone steals a
refresh token, it becomes invalid the moment the legitimate user next refreshes.

Token blacklist for access tokens: store revoked JTIs in Redis. The filter checks the
blacklist before accepting a token. TTL = remaining token lifetime. Automatic cleanup.

Token family detection: if an already-rotated refresh token is used again, someone stole it.
Invalidate ALL tokens for that user family. Force re-login."

---

## Segment 13 — Production Security Config and Wrap-Up (10 min)

**[Point to Section 7 — ProductionSecurityConfig]**

"Finally, let's look at the production SecurityFilterChain. This is the combination of
everything — CORS, CSRF disabled, session stateless, URL rules, custom error handlers,
security headers, filter registration.

CORS config: explicitly list allowed origins. Never use `*` in production for an API
that needs credentials. `.setAllowCredentials(true)` and `*` are incompatible anyway —
the browser will reject it.

Security headers — run through the list:
- frameOptions deny: no iframes. Clickjacking prevention.
- contentTypeOptions: no MIME sniffing.
- HSTS: force HTTPS.
- CSP for API: `default-src 'none'; frame-ancestors 'none'` — the API serves no content,
  so deny everything.

**[Point to Section 8 — Best Practices Checklist comment block]**

This is your production security checklist. I want you to save this file. Every time you
deploy a new REST API, run through this list before going live."

**[Read through checklist headers quickly]**

"Authentication, authorization, token security, transport security, headers, input/output,
rate limiting, secrets management, monitoring. That's your complete picture."

---

## Segment 14 — Final Wrap-Up (5 min)

"Let's pull it all together for today.

**Part 1 recap:**
- JWT: header + payload + signature. Readable but tamper-proof.
- JwtService: generate with Jwts.builder(), validate with Jwts.parser().verifyWith()
- Three exceptions to handle: Expired, Signature, Malformed
- JwtAuthenticationFilter: OncePerRequestFilter, extract Bearer token, set SecurityContextHolder
- Security config: STATELESS + csrf.disable() + addFilterBefore

**Part 2 recap:**
- RBAC: roles vs authorities, @EnableMethodSecurity, @PreAuthorize with SpEL
- Key SpEL patterns: hasRole(), hasAnyRole(), hasAuthority(), #param == authentication.name
- @PostAuthorize for return value checks, @Secured for legacy code
- Custom bean authorization with `@beanName.method()`
- SecurityContextHolder: 3 ways to access principal in controllers
- Custom entry point (401) and access denied handler (403)
- API keys: X-API-Key header, service authentication
- Rate limiting: token bucket, 429 response, Retry-After header
- HTTPS: TLS termination, HSTS header, Spring Boot SSL config
- Token management: rotation, blacklist, short expiry

**7 interview questions to close the week:**

1. What are the three parts of a JWT and what does each one do?
2. Why do we extend OncePerRequestFilter for the JWT auth filter?
3. What is the difference between 401 and 403?
4. What is the difference between hasRole('ADMIN') and hasAuthority('ROLE_ADMIN')?
5. Why is `anyRequest().authenticated()` the last requestMatchers rule?
6. How would you invalidate a JWT before it expires?
7. What is the difference between URL-based security and method-level security? When would you use each?

---

## JWT + RBAC Quick Reference Cheat Card

### JwtService
```java
// Generate
Jwts.builder()
    .subject(username)
    .issuedAt(new Date())
    .expiration(new Date(now + expiryMs))
    .claims(extraClaims)
    .signWith(key)
    .compact();

// Parse
Jwts.parser().verifyWith(key).build()
    .parseSignedClaims(token).getPayload();
```

### JwtAuthenticationFilter (key lines)
```java
String jwt = authHeader.substring(7); // strip "Bearer "
String username = jwtService.extractUsername(jwt); // may throw
userDetails = userDetailsService.loadUserByUsername(username);
if (jwtService.isTokenValid(jwt, userDetails)) {
    var authToken = new UsernamePasswordAuthenticationToken(
        userDetails, null, userDetails.getAuthorities());
    SecurityContextHolder.getContext().setAuthentication(authToken);
}
filterChain.doFilter(request, response);
```

### Security Config (JWT stateless)
```java
.csrf(csrf -> csrf.disable())
.sessionManagement(s -> s.sessionCreationPolicy(STATELESS))
.formLogin(form -> form.disable())
.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
```

### @PreAuthorize Cheat Sheet
```java
@PreAuthorize("hasRole('ADMIN')")
@PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
@PreAuthorize("hasAuthority('books:write')")
@PreAuthorize("#username == authentication.name or hasRole('ADMIN')")
@PreAuthorize("@beanName.method(authentication, #param)")
```

### 401 vs 403
| Code | Meaning | Triggered by |
|------|---------|-------------|
| 401  | Unauthenticated | No token / bad token → AuthenticationEntryPoint |
| 403  | Authenticated but unauthorized | Wrong role → AccessDeniedHandler |

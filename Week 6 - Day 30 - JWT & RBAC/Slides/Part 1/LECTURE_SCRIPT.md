# Day 30 Part 1 — JWT: Structure, Generation, and the Custom Filter
## Lecture Script

---

**[00:00–02:30] Opening — The Problem That JWT Solves**

Good morning. Day 30. This is one of the most practical days in the entire course because by the end of today, you will know how to build the actual security layer that most real production REST APIs use. If you've ever used a mobile app, a React frontend calling a backend, or a microservices architecture, there's a very good chance JWT was involved.

So let me start with the problem that Day 29's security model — the session-based model — doesn't solve well.

Yesterday we configured `SecurityFilterChain`, `UserDetailsService`, `BCryptPasswordEncoder`. You understood how Spring Security validates a username and password. That works. But the model was implicit: the browser sent a session cookie, Spring Security looked up the session in memory, found the user, and processed the request. Everything was tied to that session ID stored in the server's memory.

Here's the problem. Your backend is not one server. It's three. Or five. Or fifty. Behind a load balancer. Request one goes to server A — session created in server A's memory. Request two is routed to server B — server B has no session for this user. Four-oh-one. User logged out against their will.

You can solve that with sticky sessions — pin each user to one server. But now your load balancer is stateful and your horizontal scaling is compromised. Or you can solve it with a shared session store, like Redis, where all servers look up sessions. That works but now you've added infrastructure.

JWT sidesteps the problem entirely. The server doesn't store anything. The token itself contains everything the server needs to know about who you are. Any server can validate any token. Let me show you how.

---

**[02:30–07:00] Slide 2 — Session-Based vs Token-Based**

Let's put the two models side by side.

Session-based: Client sends username and password. Server validates the credentials, creates a session object — something like key "abc", value containing username, roles, maybe preferences — stores that in memory or Redis. Server sends back a cookie: `Set-Cookie: JSESSIONID=abc`. The browser automatically includes that cookie on every subsequent request. Server looks up "abc" in its store. Finds alice. Processes the request. Works great. The cost is that state lives on the server.

Token-based: Client sends username and password. Server validates the credentials. Server creates a JWT — a self-contained, digitally signed string. Hands it back in the response body. Client stores it. Client includes it in every subsequent request as a header: `Authorization: Bearer eyJhbGci...`. Server receives the token, validates the cryptographic signature, reads the claims directly from the token — username, roles, expiry — all in the token itself. No database lookup. No session store. Any server in the cluster can do this because they all know the signing secret.

The tradeoff column that I want to highlight is invalidation. Sessions are easy to invalidate — delete the session record and the user is immediately logged out. JWTs are hard to invalidate before expiry — the token is valid as long as the signature checks out and the expiry hasn't passed. We'll come back to that. It's an important discussion and there are good solutions, but first I want you to understand the structure of the token itself.

---

**[07:00–14:00] Slides 3–4 — JWT Structure, Claims, and Encoding**

Here's a raw JWT. It looks like gibberish — three chunks of characters joined by dots. Let's decode them.

The first part is the header. Base64URL-decoded, it becomes a small JSON object: `{"alg":"HS256","typ":"JWT"}`. Two fields. `typ` is always JWT. `alg` is the signing algorithm — `HS256` means HMAC-SHA256.

The second part is the payload — the actual claims about the user. Base64URL-decoded: `{"sub":"alice","iat":1735000000,"exp":1735086400,"roles":["ROLE_USER"]}`. Four key-value pairs. `sub` is "subject" — who the token is about. `iat` is "issued at" — a Unix timestamp. `exp` is expiration — also a Unix timestamp. And a custom claim, `roles`, with the user's roles.

The third part is the signature — the cryptographic proof that this header and payload were issued by someone who knows the secret key. We'll dig into how it's computed in a moment.

Now, the one thing I need you to understand deeply before we write any code: Base64URL encoding is NOT encryption. It is not even slightly secret. Go to jwt.io right now — paste a JWT in the left panel and you can read every claim in the payload. Anyone who intercepts a JWT can read everything in it. The signature proves authenticity, not secrecy. That means never put a password in a JWT, never put a credit card number, never put anything sensitive. Sub, roles, expiry — that's what belongs in there.

Now let me walk through the standard registered claims, because you'll use these in code. `sub` — subject — username or user ID. `iat` — issued at — when we created the token. `exp` — expiration — the critical one, always set it, always. `iss` — issuer — your app name or URL. `jti` — JWT ID — a unique identifier for this specific token, used for blacklisting. `aud` — audience — who the token is intended for.

Beyond the registered claims, you can add custom claims — anything you want. Roles is the most common custom claim. You might also include user ID, email, or department. Keep the payload small — this token goes into every request header.

---

**[14:00–19:00] Slide 5 — JWT Signature and Why Tampering Fails**

Let me explain exactly how the signature works and why you can't fake one without the secret.

HMAC-SHA256 takes two inputs: the data to sign, and a secret key. It produces a fixed-length hash. The same inputs always produce the same output. Different inputs or different secret → completely different hash.

When the server issues a token, it computes the signature like this: take the Base64URL-encoded header, add a dot, add the Base64URL-encoded payload — that's the "message". Feed that message plus the secret key into HMAC-SHA256. That hash becomes the signature, also Base64URL-encoded, appended as the third part.

Now an attacker intercepts this token. They can read the payload — alice is `ROLE_USER`. They modify the payload: change `ROLE_USER` to `ROLE_ADMIN`. Now they try to send it to the API. What happens? The server recomputes the expected signature from the received header and modified payload. It gets a completely different hash — because the payload changed. It compares that hash to the signature in the token. They don't match. Spring Security throws `SignatureException`. Four-oh-one. Attack defeated.

This is why the secret key is sacred. If an attacker knows your secret key, they can compute valid signatures for any payload they want. So rule one: never hardcode the secret in source code. Store it as an environment variable. Rule two: minimum 256 bits for HS256. JJWT will throw a `WeakKeyException` at startup if your key is too short. Rule three: use `openssl rand -base64 32` to generate secrets. That gives you 32 random bytes — 256 bits — encoded as a 44-character Base64 string. Let's set that up.

---

**[19:00–24:00] Slide 6 — JJWT Setup: Maven and Application Properties**

To add JWT support, we need the JJWT library. Three Maven dependencies. Write these down.

`jjwt-api` is the compile-time dependency — the interfaces you'll call in your code. `jjwt-impl` is runtime scope — the actual implementation. We don't put it as a compile dependency on purpose; it prevents you from accidentally depending on internal classes. `jjwt-jackson` is also runtime — it handles JSON serialization of your claims map using Jackson, which you already have from Spring Boot.

Versions matter here. Use 0.12.5 or later. The API changed significantly at version 0.12 — older tutorials online use the 0.11.x API which has some deprecated methods. The 0.12 API uses the builder pattern consistently and is cleaner.

Now in `application.properties`, two entries. `jwt.secret` equals the Base64-encoded secret — and don't hardcode the actual value here. Use `${JWT_SECRET}` to pull from an environment variable. `jwt.expiration` in milliseconds — 86400000 is 24 hours. In a production system with high security requirements, you'd set access tokens to 900000 — 15 minutes — and handle refresh separately. For our exercises today, 24 hours is fine.

To generate your secret for development:
```
openssl rand -base64 32
```
That outputs something like `4e6f742d612d53656372657...` — copy that into your environment or `.env` file.

---

**[24:00–33:00] Slides 7–8 — JwtService: Generating and Validating Tokens**

Now let's build the service that handles all JWT operations. Create a class called `JwtService`, annotate it with `@Component`.

Two injected properties — `@Value("${jwt.secret}")` to pull the secret from properties, and `@Value("${jwt.expiration}")` for the expiration in milliseconds.

Private method `getSigningKey()`: this converts our Base64-encoded string property into an actual `SecretKey` object that the JJWT library can use. `Decoders.BASE64.decode(secretKey)` gives us the raw bytes. `Keys.hmacShaKeyFor(bytes)` builds the key and validates it's long enough for HS256.

`generateToken()` takes a `UserDetails` object — that's what Spring Security gives you after loading a user. The method returns the compact JWT string. Here's how the builder chain works:

`.claims(extraClaims)` — optional map for caller-supplied claims, defaults to empty map. `.subject(username)` — sets the `sub` claim. `.issuedAt(new Date())` — sets `iat` to right now. `.expiration(new Date(currentTime + expirationMs))` — sets `exp`. `.claim("roles", ...)` — adds our custom roles claim by streaming the `GrantedAuthority` collection to a list of strings. `.signWith(getSigningKey())` — signs with HS256. `.compact()` — builds and serializes the whole thing into the dot-separated string.

Now validation. `extractUsername()` — calls `extractClaim` with a method reference to `Claims::getSubject`. Clean functional style.

`isTokenValid()` — two checks. First, does the username in the token match the `UserDetails` we loaded? Second, is the token not expired? Both must be true.

`extractAllClaims()` — this is the core validation method. `Jwts.parser().verifyWith(getSigningKey()).build().parseSignedClaims(token)` does everything: splits the token, recomputes the expected signature, compares, checks expiry. If anything is wrong, it throws. The exceptions you'll encounter: `SignatureException` means the token was tampered with or signed by a different key. `ExpiredJwtException` means past the `exp` time. `MalformedJwtException` means the token isn't valid JWT format. All of these should result in a 401 response.

The generic `extractClaim()` method with a function parameter lets you extract any claim type in one line — `extractClaim(token, Claims::getExpiration)` for a Date, `extractClaim(token, Claims::getSubject)` for a String. This pattern avoids code duplication.

---

**[33:00–44:00] Slides 9–10 — JwtAuthenticationFilter and SecurityConfig**

This is the heart of the whole system. Create a class `JwtAuthenticationFilter` that extends `OncePerRequestFilter`. `OncePerRequestFilter` is a Spring class that guarantees our filter logic runs exactly once per HTTP request. Annotate it `@Component` so Spring can inject it into the security config.

Two constructor-injected dependencies: `JwtService` and `UserDetailsService`. No `@Autowired` on the constructor — Spring 5+ injects automatically when there's one constructor.

Override `doFilterInternal()`. This method receives the request, response, and filter chain. Let me walk through every step.

Step one: extract the `Authorization` header. `request.getHeader("Authorization")`. 

Step two: guard clause. If the header is null or doesn't start with `"Bearer "` — note the space — this request either has no token or has a differently formatted auth scheme. Pass it through: `filterChain.doFilter(request, response)` and `return`. We're not blocking it here — an unauthenticated request to a public endpoint is fine; Spring Security's authorization layer will handle protected endpoints.

Step three: extract the token itself. The header format is `Bearer ` followed by the token. `authHeader.substring(7)` skips the seven characters `Bearer `.

Step four: extract the username from the token. This calls our `jwtService.extractUsername()` which in turn calls `extractAllClaims()`. If the token is malformed, this throws — we should handle that with a try-catch in production.

Step five: the double condition. `username != null` — we got something from the token. `SecurityContextHolder.getContext().getAuthentication() == null` — there's no existing authentication in the context. If both are true, we proceed to authenticate. The second condition is important — we don't want to re-authenticate a request that a previous filter already handled.

Step six: load the `UserDetails` from the database. This hits your `UserDetailsService.loadUserByUsername()` — which calls your repository. This is the only database call during token validation.

Step seven: validate the token against the loaded user. This checks the username matches AND the token isn't expired. If valid, create a `UsernamePasswordAuthenticationToken` — three-argument constructor: the principal (UserDetails), null for credentials, and the authorities. The three-argument constructor creates an authenticated token. The two-argument constructor creates a not-yet-authenticated token — subtle but important.

Step eight: `authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request))`. This attaches the IP address and session ID to the authentication — useful for audit logging and security events.

Step nine: `SecurityContextHolder.getContext().setAuthentication(authToken)`. This is the critical line. Everything downstream — authorization checks, `@PreAuthorize`, etc. — reads from the `SecurityContext`. Putting our authenticated token here tells Spring Security "this user is authenticated, these are their roles."

Step ten: `filterChain.doFilter(request, response)`. Always call this at the end. This passes control to the next filter in the chain. Without this, the request stops here and never reaches your controller.

Now for `SecurityConfig`. Five changes from Day 29's config.

One: `csrf.disable()` — we're stateless, no cookies being managed, no CSRF risk from cross-site form submissions.

Two: `sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))` — tell Spring Security explicitly not to create or use HTTP sessions.

Three: `requestMatchers("/api/auth/**").permitAll()` — the login endpoint must be reachable without a token, obviously.

Four: `authenticationProvider(authenticationProvider())` — register our `DaoAuthenticationProvider`.

Five: `addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)` — insert our filter in the chain just before Spring's default username-password filter. This ensures JWT validation happens before Spring Security's own filter tries to process the request.

---

**[44:00–50:00] Slide 11 — Login Endpoint with AuthenticationManager**

Let's build the login endpoint. `@RestController`, `@RequestMapping("/api/auth")`. Constructor-inject `AuthenticationManager`, `UserDetailsService`, and `JwtService`.

`POST /login` receives a `LoginRequest` record — username and password, both `@NotBlank`.

Inside: `authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password))`. This delegates to your `DaoAuthenticationProvider` which calls `userDetailsService.loadUserByUsername()` and `passwordEncoder.matches()`. If credentials are wrong, it throws `BadCredentialsException`, which Spring translates to a 401. If credentials are correct, it returns without throwing.

After that call succeeds, load the `UserDetails` yourself — because you need the authorities for the JWT claims. Call `jwtService.generateToken(userDetails)`. Return the token in the response body: `ResponseEntity.ok(new JwtResponse(token))`.

One thing to add in `SecurityConfig`: the `AuthenticationManager` bean. `config.getAuthenticationManager()` — this is the way to expose the `AuthenticationManager` as a Spring bean in Spring Boot 3. You need it as a bean so it can be injected into `AuthController`.

The full request-response cycle: client POSTs credentials, server validates, server returns JSON with the token string. Client takes that token and uses it on every subsequent call in the `Authorization: Bearer` header.

---

**[50:00–56:00] Slides 13–14 — Secret Key Security and Token Storage**

Two important security discussions before we wrap Part 1.

First: secret key hygiene. The `jwt.secret` property value is the crown jewel. If an attacker gets it, they can forge tokens for any user with any role. Rules: store it in an environment variable, not in `application.properties` committed to source control. Use `${JWT_SECRET}` as the property value and set that environment variable in your deployment environment. Rotate it periodically. For local development, a `.env` file that's in `.gitignore` is acceptable.

Second: where does the client store the token? This is a real-world security question. Three options. `localStorage`: every JavaScript file on your page can read it. If your site has any XSS vulnerability — a malicious script injection — the attacker can steal every user's token. For public-facing applications, this is high risk.

`httpOnly` cookie: the server sends the token as a cookie with the `HttpOnly` flag. JavaScript cannot read `HttpOnly` cookies — `document.cookie` doesn't show them. This eliminates XSS token theft. The tradeoff is CSRF: the browser automatically sends cookies with cross-origin requests, so a malicious site could trigger requests with your cookie. Mitigate with `SameSite=Strict` on the cookie. This is the recommended approach for web apps.

In-memory storage — a JavaScript variable: immune to both XSS (other scripts can't read it) and CSRF (not auto-sent). The cost: if the user refreshes the page, the token is gone and they need to log in again. Some apps handle this by keeping the access token in memory and using a refresh token in a httpOnly cookie to silently get a new access token.

For our course exercises: localStorage is fine. Know the tradeoff.

---

**[56:00–60:00] Slide 15–16 — Part 1 Summary and Part 2 Preview**

Let's summarize what you can now do.

You understand JWT structure: three Base64URL-encoded parts. Header says how it's signed. Payload contains claims about the user. Signature cryptographically proves the other two parts weren't tampered with.

You can implement `JwtService` with `generateToken()` and `isTokenValid()` using JJWT's builder and parser APIs.

You can build `JwtAuthenticationFilter extends OncePerRequestFilter`, extract the Bearer token, validate it, populate the `SecurityContextHolder`.

You can configure `SecurityFilterChain` for stateless JWT: CSRF off, no sessions, `addFilterBefore`.

You can write the login endpoint using `AuthenticationManager.authenticate()` to validate credentials before issuing a token.

After lunch, Part 2 builds on this authentication foundation. We'll tackle authorization — who can do what once they're authenticated. That means `@PreAuthorize` and `@Secured` annotations for method-level security, `requestMatchers` with role restrictions for URL patterns, accessing the current user from `SecurityContextHolder` in your service code, and building clean JSON 401 and 403 responses instead of HTML redirects. We'll also cover REST API security best practices: HTTPS, API keys, and rate limiting. 

Take a break, get some water. Part 2 in an hour.

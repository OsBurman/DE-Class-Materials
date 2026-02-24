# Walkthrough Script — Part 1: JWT Structure & Implementation

**Day 30: JWT & RBAC**
**Total estimated time: ~95 minutes**

---

## Segment 1 — Opening (5 min)

"Good morning everyone. Welcome to Day 30 — the last day of Week 6 and honestly one of the
most practical days we've had. We spent yesterday on Spring Security fundamentals — filter
chains, BCrypt, UserDetailsService. Today we're going to layer JWT on top of that.

Here's the real-world relevance: if you open a job posting for any company building a REST
API — whether it's a startup or a FAANG shop — JWT is in the requirements. It's the industry
standard for stateless authentication. By the end of today, you'll know not just how to USE
a JWT, but how to build the entire auth system from scratch.

We're going to cover two main things today. Part 1: the theory and implementation of JWT —
what the token IS, how to generate and validate it, and how to wire a custom filter into
Spring Security. Part 2: Role-Based Access Control — how to restrict endpoints by role
at both the URL and method level.

Let's open `01-jwt-structure-and-flow.md` first. No code yet — just the anatomy lesson."

---

## Segment 2 — JWT Anatomy (15 min)

**[Open: `01-jwt-structure-and-flow.md`]**

"Let me show you what a JWT actually looks like."

**[Point to the three-part token string near the top of the file]**

"This is a real JWT. Count the dots — two of them. Three sections. Header dot Payload dot
Signature. That's it. Every JWT in the world has exactly this structure.

Each section is base64url-encoded — so it looks like gibberish. But go to jwt.io right now,
paste that string in, and it decodes instantly. You'll see the raw JSON.

**Question for the class:** If you can decode it on jwt.io without a key... what does that
tell you about the payload? Is it secret?"

*[Wait for responses.]*

"Exactly — it is NOT secret. The payload is just encoded, not encrypted. Anyone who gets
their hands on your JWT can read everything in it. That's why the rule is: never put passwords,
social security numbers, credit cards, or any sensitive data in a JWT payload."

**[Point to Section 2 — Header]**

"The header tells the receiver two things: what TYPE of token this is — always 'JWT' — and
which ALGORITHM was used to sign it. We're using HS256 today — HMAC with SHA-256.

See the note here: HS256 is symmetric. One shared secret key signs AND verifies. That works
fine for a single API. If you're building microservices and want only the auth server to sign
tokens, you'd switch to RS256 — RSA asymmetric keys. Private key signs, public key verifies.
Every downstream service gets only the public key — it can verify but never create a token.
That's a more secure architecture. We're using HS256 today for simplicity."

**[Point to Section 3 — Payload / Claims]**

"Now the interesting part — claims. There are two kinds.

**Registered claims** — these are standardized names you'll see in every JWT library:
- `sub` — subject — WHO is this token about. Usually email or user ID.
- `iss` — issuer — WHO created the token. We'll set this to 'bookstore-api'.
- `aud` — audience — WHO should accept this token.
- `exp` — expiration — Unix timestamp. The token is dead after this moment.
- `iat` — issued at — when was it created.
- `nbf` — not before — token isn't valid until this time.
- `jti` — JWT ID — a unique identifier. Used for replay attack prevention.

**Custom claims** — anything you want to add. We're adding a `roles` claim with the user's
roles as a comma-separated string. This is how the API knows the user is ROLE_ADMIN without
a database lookup on every request.

**Watch out:** The claim names `sub`, `exp`, `iat` etc. are SHORT by design — JWTs are sent
on every HTTP request, so compact tokens reduce bandwidth."

**[Point to Section 4 — Signature]**

"The signature is the magic. It's HMAC-SHA256 of the header dot the payload, using your
secret key. If anyone changes even one character in the payload — say they upgrade their role
from USER to ADMIN by editing the base64 — the signature recalculation with your secret key
WON'T match. The token is rejected.

So the signature doesn't HIDE the data — it GUARANTEES INTEGRITY. Tamper-proof. Not secret."

---

## Segment 3 — Token vs Session (10 min)

**[Point to Section 5 — Comparison Table]**

"Before we write any code, I want you to really understand the trade-off, because interviewers
love this question.

Look at the table. Let's go row by row:

**Storage:** Sessions live on the server — in memory or a Redis cache. JWTs live in the token
itself — the server stores nothing. Zero state.

**Scalability:** This is the big one. With sessions, if your load balancer sends Alice's second
request to a different server, that server doesn't have her session. You need sticky sessions
or a shared Redis store. With JWT, any server can validate any token independently — just
need the secret key. Horizontal scaling is trivial.

**Revocation:** Sessions are easy to revoke — delete the session record. JWTs are hard —
once issued, a token is valid until it expires. If you need to invalidate a JWT before expiry
— say the user is banned — you need a token blacklist, typically in Redis.

**CSRF:** Sessions use cookies, which browsers auto-send on every request — that's the CSRF
attack vector. JWTs live in the Authorization header, which JavaScript must set explicitly.
Browsers don't auto-attach headers, so CSRF doesn't apply.

**Question:** Based on this, what type of app is best for sessions? What type is best for JWT?"

*[Take responses. Expected: Sessions = traditional server-rendered MVC; JWT = REST APIs, SPAs,
mobile apps, microservices.]*

"Right. That's why we're using JWT for our bookstore REST API today."

**[Point to Section 6 — Flow Diagram]**

"Walk through this diagram with me. Step 1: client POSTs email and password to /api/auth/login.
Step 2-3: server verifies credentials and builds a JWT with claims. Step 4: server returns
the token. Client stores it — we'll talk about WHERE in a moment.

Step 5: every subsequent request includes `Authorization: Bearer <token>` in the HTTP header.
Step 6: our custom filter extracts the token, validates the signature and expiry, loads the
user, and puts authentication into SecurityContextHolder. Step 7: controller runs normally —
it has no idea there was a JWT involved.

Step 9: eventually the token expires. The filter catches `ExpiredJwtException` and returns 401.
Client must re-login."

**[Point to Section 7 — Token Storage]**

"Where should the client store the JWT? This is a real debate in the industry.

localStorage has XSS risk — any injected script can read it. sessionStorage is slightly better.
HTTP-only cookies can't be read by JavaScript — but then you're back to CSRF risk.

Best practice for most SPAs: store the short-lived access token in memory (a JavaScript variable),
and store the refresh token in an HTTP-only cookie. Access token lost on refresh — that's fine,
you use the refresh token to get a new one silently."

**[Point to Section 8 — Token Expiry]**

"Access tokens: 15 minutes to 1 hour. Refresh tokens: 7 to 30 days. Short access tokens
limit the damage if a token is stolen — it's only valid briefly. Refresh tokens let users
stay logged in without re-entering their password."

---

## Segment 4 — JwtService: Generating Tokens (20 min)

**[Switch to: `02-jwt-implementation.java` — Section 1]**

"Now we build it. Open the Java file. This file has five sections — we'll go through them
in order.

Section 1 is JwtService. This is a Spring @Service that owns everything about tokens:
generating them, validating them, and extracting claims.

First, look at the constructor. We're not hardcoding a secret key in the code — never do that.
We inject it from application.properties with `@Value('${jwt.secret}')`. In production,
application.properties reads from an environment variable: `jwt.secret=${JWT_SECRET}`."

**[Point to `getSigningKey()`]**

"The signing key method. We decode the Base64 string from properties into a byte array,
then call `Keys.hmacShaKeyFor()`. This does two things: converts bytes to a SecretKey object
AND throws a WeakKeyException if the key is less than 256 bits. Free validation."

**[Point to `generateToken()`]**

"Now the main event — generating a token.

First we collect the user's roles from their GrantedAuthority list. We stream them, map to
string, join with comma: 'ROLE_USER,ROLE_ADMIN'.

Then we use `Jwts.builder()` — this is the jjwt fluent builder API:
- `.subject()` — sets the `sub` claim to the username
- `.issuedAt()` — sets `iat` to now
- `.expiration()` — sets `exp` to now plus our expiration duration
- `.issuer()` — sets `iss`
- `.claims()` — adds our custom `roles` claim
- `.signWith()` — signs with our SecretKey using HS256
- `.compact()` — serializes everything to the final dot-separated string

**Question:** What happens if we forget `.signWith()`?"

*[Take responses. Expected: token would have no signature / be invalid.]*

"Correct. jjwt would actually throw an exception — you can't compact an unsigned JWT by
default in modern jjwt."

---

## Segment 5 — JwtService: Parsing and Validating (10 min)

**[Point to `extractAllClaims()`]**

"Validation is the flip side. `Jwts.parser()` — note it's parser, not parserBuilder in
jjwt 0.12.x — `.verifyWith(getSigningKey())` tells jjwt which key to verify the signature
against. `.build()` creates the parser. `.parseSignedClaims(token)` is where the magic happens.

jjwt internally:
1. Splits the token on dots
2. Decodes header and payload
3. Recomputes the signature
4. Compares to the token's signature
5. If they match, proceeds
6. Checks if `exp` is in the past — if so, throws ExpiredJwtException

Then `.getPayload()` gives us the Claims object — a Map of all claim key-value pairs.

**Watch out:** If any of those steps fail, jjwt throws a JwtException subtype.
`ExpiredJwtException` — token past expiry.
`SignatureException` — signature mismatch (tampered or wrong key).
`MalformedJwtException` — not a valid JWT structure.
You MUST catch these — don't let them bubble up as 500s."

**[Point to `isTokenValid()`]**

"The final validation check: does the username in the token match the UserDetails we loaded?
And is the token not expired? Both must be true. The signature was already verified in
extractAllClaims — if we get here without an exception, the signature is good."

---

## Segment 6 — JwtAuthenticationFilter (20 min)

**[Point to Section 3 — JwtAuthenticationFilter]**

"This is the most important class in today's lesson. Let me explain what it IS before we
read the code.

In Spring Security, everything runs through a chain of filters. Our custom filter plugs into
that chain. For EVERY request to our API, this filter runs. It checks: is there a JWT? Is it
valid? If yes, it tells Spring Security who the user is.

`extends OncePerRequestFilter` — Spring guarantees this runs exactly once per request,
even in redirect and forward scenarios."

**[Point to `doFilterInternal()`]**

"Step 1: get the Authorization header. If it's null or doesn't start with 'Bearer ', we
skip — `filterChain.doFilter(request, response); return;`. We pass the request on unchanged.
The request will fail later if the endpoint needs authentication.

**Watch out:** We return IMMEDIATELY after calling filterChain.doFilter() to prevent the
rest of our filter logic from running. Missing that return is a classic bug.

Step 2: strip 'Bearer ' — substring(7) — to get the raw JWT string.

Step 3: try to extract the username. This is wrapped in try-catch because jjwt can throw here.
If the token is expired, we call `sendUnauthorizedError()` and RETURN. We don't continue.
Same for signature exception, malformed exception.

**Question:** Why do we return after writing the error response instead of calling
filterChain.doFilter()?"

*[Take responses. Expected: the filter chain would continue and the request might reach the
controller; we want to stop processing here.]*

"Exactly. If we call doFilter(), the request continues down the chain. We've already written
a 401 to the response. Calling doFilter() too could cause 'headers already committed' errors.
Always return after writing your own response in a filter."

**[Point to the SecurityContextHolder block]**

"If we successfully extracted a username, and the SecurityContext isn't already set — steps
5 through 9.

Step 6: load UserDetails. This hits the database. It serves two purposes: gets the full user
object (needed for role comparison), and verifies the user still exists and is still active.
If a user is disabled after their token was issued, this catches it.

Step 7: `isTokenValid()` — username check + not-expired check.

Step 8: create `UsernamePasswordAuthenticationToken`. Three arguments: principal (UserDetails),
credentials (null — we don't need the password after validation), and authorities (roles from UserDetails).

Step 9: SET the SecurityContextHolder. This one line is what makes Spring Security think
'this request is authenticated.' Everything downstream — controllers, @PreAuthorize — reads
from SecurityContextHolder. That's why this step is critical.

Step 10: `filterChain.doFilter()` — pass the now-authenticated request down the chain."

---

## Segment 7 — Security Config and Login Endpoint (10 min)

**[Point to Section 4 — JwtSecurityConfig]**

"The security config is our familiar @EnableWebSecurity with SecurityFilterChain.

Three key differences from yesterday's session-based config:

First: `csrf.disable()`. We're safe to disable CSRF because we don't use cookies for auth.
The browser can't auto-attach our JWT header. No CSRF risk.

Second: `SessionCreationPolicy.STATELESS`. Spring Security creates NO HttpSession. Each
request is independent. This is the definition of stateless.

Third: `addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)`. This
is how we plug in our custom filter. It runs BEFORE Spring's built-in username/password
filter. So our JWT filter validates the token first, sets SecurityContextHolder, and then
Spring's filter sees an already-authenticated context and skips credential checking."

**[Point to Section 5 — JwtAuthController]**

"The login endpoint. POST /api/auth/login receives email and password as JSON.

`authenticationManager.authenticate()` — this is Spring Security's built-in authentication
pipeline. It calls our UserDetailsService to load the user, then BCrypt to compare the
password. If it succeeds, it returns an Authentication object with the full UserDetails.

We then call `jwtService.generateToken(userDetails)` and return the token.

If authenticate() throws `BadCredentialsException` (wrong password or unknown user), we catch
`AuthenticationException` and return 401 with a GENERIC error message. Never say 'user not found'
vs 'wrong password' — that leaks which usernames exist."

**[Point to logout stub]**

"The logout endpoint shows an important concept: stateless JWT logout is CLIENT-SIDE.
You delete the token from storage. The server can't 'invalidate' a JWT because it doesn't
track them. For true server-side invalidation, you need a token blacklist — store the JTI in
Redis with a TTL matching the token's remaining lifetime."

---

## Segment 8 — Sample Controller (5 min)

**[Point to Section 6 — BookController]**

"Finally, a controller that shows JWT in action.

`/api/books/public/catalog` — no auth needed. Open to the world.

`/api/books/my-library` — authenticated. The filter has already run by this point.
SecurityContextHolder.getContext().getAuthentication().getName() gives us the username
from the token's `sub` claim.

`/api/books/profile` uses `@AuthenticationPrincipal UserDetails currentUser`. This is a
Spring shortcut annotation that injects the principal from SecurityContextHolder directly
as a method parameter. Cleaner than calling getContext() manually."

---

## Segment 9 — Wrap-Up and Transition (5 min)

"Let's recap what we've built in Part 1:

A JWT has three parts: header (algorithm), payload (claims), signature (integrity guarantee).
The payload is readable by anyone — never put secrets there. The signature prevents tampering.

JwtService handles generating tokens with `Jwts.builder()` and validating them with
`Jwts.parser().verifyWith()`. Three exception types to catch: Expired, Signature, Malformed.

JwtAuthenticationFilter extends OncePerRequestFilter. On every request: extract the Bearer
token, validate it, load UserDetails, set SecurityContextHolder. If invalid: write 401 and return.

The security config disables CSRF and sessions, and plugs in our filter with addFilterBefore.

The login endpoint uses authenticationManager to verify credentials, then returns a signed JWT.

**Five interview questions you should be able to answer now:**
1. What are the three parts of a JWT?
2. Is the JWT payload encrypted? Can anyone read it?
3. What is the difference between HS256 and RS256?
4. Why do we extend OncePerRequestFilter instead of GenericFilterBean?
5. How do you invalidate a JWT before it expires?

In Part 2, we'll build Role-Based Access Control on top of this — method-level security
with @PreAuthorize, custom authorization logic, and REST API security best practices."

---

## Quick Reference

| File | Topics Covered |
|------|---------------|
| `01-jwt-structure-and-flow.md` | Anatomy, claims (registered + custom), token vs session, flow diagram, storage, expiry strategy, jjwt Maven deps |
| `02-jwt-implementation.java` §1 | JwtService, generateToken, Jwts.builder(), Keys.hmacShaKeyFor(), extractAllClaims, Jwts.parser(), isTokenValid |
| `02-jwt-implementation.java` §2 | LoginRequest / AuthResponse DTOs |
| `02-jwt-implementation.java` §3 | JwtAuthenticationFilter, OncePerRequestFilter, doFilterInternal, SecurityContextHolder, ExpiredJwtException, SignatureException, MalformedJwtException |
| `02-jwt-implementation.java` §4 | JwtSecurityConfig, csrf.disable(), STATELESS, addFilterBefore, AuthenticationManager bean |
| `02-jwt-implementation.java` §5 | JwtAuthController, /login, /refresh, /logout concept |
| `02-jwt-implementation.java` §6 | BookController, @AuthenticationPrincipal |

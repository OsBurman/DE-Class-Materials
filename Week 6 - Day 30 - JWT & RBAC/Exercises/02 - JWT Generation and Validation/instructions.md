# Exercise 02: JWT Generation and Validation

## Objective
Generate and validate JSON Web Tokens programmatically using the `jjwt` library inside a Spring Boot application.

## Background
The `io.jsonwebtoken` (jjwt) library is the most widely used Java library for creating and parsing JWTs.
You use a **signing key** (a secret string for HMAC-SHA256, or an RSA/EC key pair for asymmetric algorithms) to produce the signature.
During validation the same key is used to verify the signature has not been tampered with.

## Requirements

1. **`pom.xml`** – add the `jjwt-api`, `jjwt-impl`, and `jjwt-jackson` dependencies (version 0.12.x).
2. **`JwtUtil.java`** – implement the following methods:
   - `generateToken(String username, String role)` → builds a signed JWT with:
     - claim `"role"` set to the role argument
     - subject set to `username`
     - issued-at set to `now`
     - expiry set to `now + 1 hour`
     - signed with HMAC-SHA256 using the secret key
   - `validateToken(String token)` → returns `true` if the token parses without throwing an exception (valid signature + not expired), `false` otherwise
   - `extractUsername(String token)` → returns the `sub` claim
   - `extractRole(String token)` → returns the custom `role` claim as a `String`
   - `extractExpiry(String token)` → returns the expiry `Date`
3. **`JwtUtilTest.java`** – complete the five unit tests so they all pass:
   - generated token is not null/blank
   - `validateToken` returns `true` for a freshly generated token
   - `extractUsername` returns the correct value
   - `extractRole` returns the correct value
   - `validateToken` returns `false` for a tampered token (flip one character)

## Hints
- Use `Jwts.builder()` to create a token and `Jwts.parser()` to validate/parse one.
- `Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8))` creates the signing key (secret must be ≥ 32 characters for HMAC-SHA256).
- To simulate a tampered token, change one character in the signature part (after the last `.`).
- `JwtException` is the base exception thrown by jjwt for any invalid token.

## Expected Output

Running `JwtUtilTest` (JUnit 5 via Maven) should show all 5 tests passing:

```
[INFO] Tests run: 5, Failures: 0, Errors: 0, Skipped: 0
```

Running `JwtDemoMain` directly should print:

```
Generated token: eyJhbGciOiJIUzI1NiJ9.<payload>.<signature>

Username : alice
Role     : ADMIN
Expires  : <date ~1 hour from now>
Valid?   : true

=== Tampered token ===
Valid?   : false
```

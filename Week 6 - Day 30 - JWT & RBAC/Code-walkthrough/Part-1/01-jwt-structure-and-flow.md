# JWT Structure and Authentication Flow

## What Is a JSON Web Token?

A **JSON Web Token (JWT)** is a compact, URL-safe string that securely transmits information
between two parties. It is self-contained — everything the server needs to verify the request
is embedded inside the token itself. No database lookup required on every request.

---

## 1. JWT Anatomy — Three Parts, One Dot-Separated String

```
eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9
.
eyJzdWIiOiJhbGljZUBib29rc3RvcmUuY29tIiwicm9sZXMiOiJST0xFX1VTRVIiLCJpYXQiOjE3MDAwMDAwMDAsImV4cCI6MTcwMDAwMzYwMH0
.
SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c
```

A JWT has exactly **three base64url-encoded sections** separated by dots:

```
HEADER . PAYLOAD . SIGNATURE
```

---

## 2. Part 1 — Header

The header declares the token **type** and the **signing algorithm**.

```json
{
  "alg": "HS256",
  "typ": "JWT"
}
```

| Field | Meaning                                         |
|-------|-------------------------------------------------|
| `alg` | Algorithm used to sign: `HS256` (HMAC-SHA256), `RS256` (RSA), `ES256` (ECDSA) |
| `typ` | Always `"JWT"` — tells the receiver what kind of token this is |

> **HS256** uses a single shared secret key (symmetric). Both sides must have the same key.
> **RS256** uses a public/private key pair (asymmetric). Private key signs; public key verifies.
> For microservices, RS256 is preferred so only the auth server holds the private key.

---

## 3. Part 2 — Payload (Claims)

The payload contains **claims** — statements about the user and token metadata.

```json
{
  "sub":   "alice@bookstore.com",
  "roles": "ROLE_USER,ROLE_ADMIN",
  "iss":   "bookstore-api",
  "aud":   "bookstore-client",
  "iat":   1700000000,
  "exp":   1700003600,
  "jti":   "a1b2c3d4-e5f6-7890-abcd-ef1234567890"
}
```

### Registered Claims (standardized, well-known names)

| Claim | Full Name         | Purpose                                                |
|-------|-------------------|--------------------------------------------------------|
| `sub` | Subject           | Who the token is about — typically user ID or email    |
| `iss` | Issuer            | Who created the token — e.g., `"bookstore-api"`        |
| `aud` | Audience          | Who the token is intended for — e.g., `"bookstore-client"` |
| `exp` | Expiration Time   | Unix timestamp after which the token is invalid        |
| `iat` | Issued At         | Unix timestamp when the token was created              |
| `nbf` | Not Before        | Token is not valid before this time                    |
| `jti` | JWT ID            | Unique ID for this token — used to prevent replay attacks |

### Private / Custom Claims

You can add **any claim you need** — just avoid colliding with registered names:

```json
{
  "roles":       "ROLE_USER,ROLE_ADMIN",
  "userId":      42,
  "bookstoreId": "NYC-001",
  "plan":        "premium"
}
```

> ⚠️ **Watch out:** The payload is **base64url-encoded, NOT encrypted**.
> Anyone with the token can decode and read the payload.
> **Never store passwords, SSNs, credit cards, or secrets in a JWT payload.**

---

## 4. Part 3 — Signature

The signature **proves the token has not been tampered with**.

```
HMACSHA256(
  base64url(header) + "." + base64url(payload),
  SECRET_KEY
)
```

If an attacker changes even one character in the header or payload,
the signature recalculation will not match, and the server rejects the token.

> The signature does NOT hide the data — it only **guarantees integrity**.

---

## 5. Token-Based vs Session-Based Authentication

| Aspect              | Session-Based (Stateful)              | JWT Token-Based (Stateless)            |
|---------------------|---------------------------------------|----------------------------------------|
| **Storage**         | Server stores session in memory/DB    | No server storage — all in the token   |
| **Scalability**     | Hard — sticky sessions or shared store | Easy — any server can validate         |
| **Revocation**      | Easy — delete session from store      | Hard — token valid until expiry        |
| **Payload size**    | Cookie: small session ID only         | Token carries all claims (bigger)       |
| **Mobile/SPA**      | Awkward — CORS and cookie issues      | Natural — `Authorization: Bearer` header |
| **Microservices**   | Each service must call session store  | Each service validates independently   |
| **CSRF risk**       | Yes — cookies are auto-sent           | No — `Authorization` header not auto-sent |
| **Logout**          | Immediate — delete session            | Must use token blacklist or short expiry |

**Rule of thumb:**
- Traditional server-rendered web apps → sessions work great
- REST APIs, SPAs, mobile apps, microservices → JWT is the standard

---

## 6. JWT Authentication Flow

```
┌─────────────┐                              ┌─────────────────────┐
│   Client    │                              │   Spring Boot API   │
│  (React /   │                              │                     │
│   Mobile)   │                              │                     │
└──────┬──────┘                              └──────────┬──────────┘
       │                                               │
       │  1. POST /api/auth/login                      │
       │     { email, password }  ─────────────────────▶
       │                                               │
       │                          2. Verify credentials│
       │                             (UserDetailsService + BCrypt)
       │                                               │
       │                          3. Build JWT token   │
       │                             claims: sub, roles, exp
       │                                               │
       │  4. 200 OK                                    │
       │     { "token": "eyJ..." } ◀─────────────────── │
       │                                               │
       │  (Client stores token in localStorage or      │
       │   memory — NOT in a cookie for CSRF safety)   │
       │                                               │
       │  5. GET /api/books/admin                      │
       │     Authorization: Bearer eyJ... ─────────────▶
       │                                               │
       │                          6. JwtAuthenticationFilter
       │                             intercepts request│
       │                             - extract token   │
       │                             - validate signature + expiry
       │                             - load UserDetails│
       │                             - set SecurityContextHolder
       │                                               │
       │                          7. Controller runs   │
       │                             (authenticated)   │
       │                                               │
       │  8. 200 OK (data)         ◀─────────────────── │
       │                                               │
       │  9. GET /api/books/admin  (token expired)      │
       │     Authorization: Bearer eyJ... (old) ────────▶
       │                                               │
       │                          10. ExpiredJwtException
       │                              → 401 Unauthorized│
       │  11. 401 Unauthorized     ◀─────────────────── │
       │      (client must re-login)                   │
└─────────────┘                              └─────────────────────┘
```

---

## 7. Token Storage — Where Should the Client Store the JWT?

| Storage Location  | XSS Risk | CSRF Risk | Recommendation              |
|-------------------|----------|-----------|-----------------------------|
| `localStorage`    | HIGH     | None      | Avoid for sensitive apps    |
| `sessionStorage`  | HIGH     | None      | Better than localStorage    |
| HTTP-only cookie  | None     | Medium    | Best for web apps (+ CSRF protection) |
| In-memory (JS var)| Low      | None      | Best for SPAs — lost on refresh |

> **Common practice:** Store access token in memory; store refresh token in HTTP-only cookie.

---

## 8. Token Expiry Strategy

| Token Type      | Typical Expiry | Purpose                                   |
|-----------------|---------------|-------------------------------------------|
| Access token    | 15 min – 1 hr | Short-lived; used on every request        |
| Refresh token   | 7 – 30 days   | Long-lived; used only to get new access token |

**Refresh flow:**
1. Access token expires → client gets `401`
2. Client sends refresh token to `/api/auth/refresh`
3. Server validates refresh token, issues new access token
4. Client retries original request

---

## 9. JWT Library — jjwt (Java)

The most popular Java JWT library is **jjwt** by Stormpath/JJWT.

### Maven dependency (pom.xml):
```xml
<!-- JWT Core -->
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-api</artifactId>
    <version>0.12.3</version>
</dependency>
<!-- Runtime: parser and builder implementation -->
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-impl</artifactId>
    <version>0.12.3</version>
    <scope>runtime</scope>
</dependency>
<!-- Jackson integration for JSON serialization of claims -->
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-jackson</artifactId>
    <version>0.12.3</version>
    <scope>runtime</scope>
</dependency>
```

### application.properties:
```properties
# JWT configuration
jwt.secret=your-256-bit-super-secret-key-that-is-at-least-32-characters-long
jwt.expiration-ms=3600000
# 3600000 ms = 1 hour

# NEVER commit real secrets to Git — use environment variables:
# jwt.secret=${JWT_SECRET}
```

---

## Summary Checklist

- [x] JWT = Header.Payload.Signature (base64url-encoded, dot-separated)
- [x] Header → algorithm + type
- [x] Payload → registered claims (sub, iss, aud, exp, iat, nbf, jti) + custom claims
- [x] Signature → HMAC(header + "." + payload, secret) — guarantees integrity only
- [x] Payload is readable by anyone — NEVER store secrets in it
- [x] Token-based: stateless, scales horizontally, no CSRF risk, hard to revoke
- [x] Session-based: stateful, easy revocation, CSRF risk, sticky sessions
- [x] JWT flow: login → get token → send `Authorization: Bearer` on every request
- [x] Short-lived access tokens + long-lived refresh tokens is best practice
- [x] jjwt-api / jjwt-impl / jjwt-jackson are the three Maven dependencies

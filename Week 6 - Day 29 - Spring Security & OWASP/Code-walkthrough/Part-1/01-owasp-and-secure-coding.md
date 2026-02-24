# OWASP Top 10 & Secure Coding Fundamentals
## Day 29 Part 1 — Reference Guide

---

## Secure Coding Fundamentals and Principles

Security is not a feature you add at the end — it is a discipline woven into every
decision you make while writing code. The following principles form the foundation
of secure software development.

### Core Principles

| Principle | Meaning | Example |
|-----------|---------|---------|
| **Least Privilege** | Every component gets only the permissions it needs, nothing more | A database user for a read-only report has SELECT only — no INSERT, UPDATE, DELETE |
| **Defense in Depth** | Layer multiple controls so that no single failure opens everything | HTTPS + input validation + parameterized queries + WAF — attacker must bypass all four |
| **Fail Securely** | When something goes wrong, fail closed (deny by default), not open | If the auth check throws an exception, reject the request — don't accidentally allow it |
| **Input Validation** | Never trust data from outside your system | Validate type, length, format, and allowed characters before using any input |
| **Output Encoding** | Encode data before writing it to a different context (HTML, SQL, shell) | HTML-encode user content before rendering it in a page |
| **Separation of Concerns** | Keep authentication, authorization, business logic, and data access separate | Security logic lives in filters/interceptors, not scattered through service methods |
| **Keep It Simple** | Complex code is harder to reason about and easier to get wrong | Prefer a well-known security library over rolling your own crypto |
| **Security by Default** | The secure option should be the default; insecure behavior should require explicit opt-in | Spring Security denies all requests by default; you explicitly open routes |

### The OWASP Foundation

OWASP (Open Web Application Security Project) is a nonprofit foundation that
publishes free, open resources for web application security.

Their most influential document: **OWASP Top 10** — a community-driven list of the
ten most critical security risks to web applications, updated periodically based on
real-world breach data.

The Top 10 is used by:
- Security teams to prioritize risk
- Development teams as a secure coding checklist
- Auditors and pen testers as a baseline assessment framework
- Hiring managers — **it appears in interview questions**

---

## OWASP Top 10 (2021 Edition)

```
A01 — Broken Access Control          ← #1 most common
A02 — Cryptographic Failures          ← formerly "Sensitive Data Exposure"
A03 — Injection                       ← SQL, LDAP, OS command, OGNL
A04 — Insecure Design                 ← threat modeling failures
A05 — Security Misconfiguration       ← default creds, open ports, verbose errors
A06 — Vulnerable & Outdated Components← using old libraries with known CVEs
A07 — Identification & Authentication Failures  ← weak passwords, no MFA, session flaws
A08 — Software & Data Integrity Failures        ← unsigned artifacts, insecure deserialization
A09 — Security Logging & Monitoring Failures    ← no audit trail, no alerting
A10 — Server-Side Request Forgery (SSRF)        ← server fetches attacker-controlled URLs
```

### A01 — Broken Access Control
**What it is:** The application does not properly enforce what authenticated users
are allowed to do. Users can access resources or actions they should not be able to.

**Examples:**
- Changing the URL from `/api/orders/1001` to `/api/orders/1002` and seeing another
  user's order (IDOR — Insecure Direct Object Reference)
- A regular user accessing `/admin/users` because the route wasn't protected
- A user who was logged out can still use a cached API token

**Prevention:**
- Enforce authorization on every request — never rely on the UI hiding a button
- Use deny-by-default: only explicitly grant what is needed
- Implement access control checks in the business layer, not just the URL layer
- Log and alert on access control failures

---

### A02 — Cryptographic Failures (Sensitive Data Exposure)
**What it is:** Sensitive data is transmitted or stored without adequate protection,
or weak/outdated cryptography is used.

**Examples:**
- Storing passwords in plain text or with MD5/SHA1
- Transmitting credit card numbers over HTTP (not HTTPS)
- Using ECB mode for encryption (patterns visible in ciphertext)
- Storing API keys or secrets in source code or plain config files

**Prevention:**
```
✅  Use HTTPS for ALL traffic — no exceptions
✅  Hash passwords with bcrypt, scrypt, or Argon2 (NOT MD5/SHA1)
✅  Use AES-256 for symmetric encryption
✅  Store secrets in environment variables or a secrets manager (AWS Secrets Manager,
    HashiCorp Vault) — NEVER in Git
✅  Disable caching of responses that contain sensitive data
✅  Enforce TLS 1.2+ — reject TLS 1.0, SSL 2/3
```

**The rule:** Classify every piece of data. For anything sensitive (PII, financial,
health, credentials) ask: is it encrypted in transit? At rest? Can it appear in logs?

---

### A03 — Injection
**What it is:** Untrusted user input is sent to an interpreter (SQL engine, OS shell,
LDAP server, XML parser) as part of a command or query. The interpreter cannot
distinguish between the data and the command — the attacker's input *becomes* the command.

Covered in detail in `02-injection-attacks.java`.

---

### A05 — Security Misconfiguration
**What it is:** The application, server, framework, or cloud environment is configured
insecurely. This is one of the easiest vulnerabilities for attackers to find because
they can scan for it automatically.

**Common examples:**

```
❌  Default credentials left in place
       admin/admin, root/root, sa/sa (SQL Server default)

❌  Unnecessary features enabled
       Directory listing on, debug endpoints exposed (/actuator/env, /h2-console)

❌  Verbose error messages
       Stack traces returned to users — reveals framework versions, file paths,
       internal class names

❌  Missing security headers
       No Content-Security-Policy, no X-Frame-Options, no HSTS

❌  Cloud storage buckets left public
       AWS S3 bucket readable by anyone

❌  Default Spring Boot Actuator exposed without authentication
       GET /actuator/env returns all environment variables including DB passwords
```

**Prevention:**
```java
// In application.properties — lock down Actuator in production:
management.endpoints.web.exposure.include=health,info
management.endpoint.env.enabled=false

// Disable H2 console in production:
spring.h2.console.enabled=false

// Don't return stack traces to clients:
server.error.include-stacktrace=never
server.error.include-message=never
```

**Security headers to always add:**
```
Strict-Transport-Security: max-age=31536000; includeSubDomains
X-Content-Type-Options: nosniff
X-Frame-Options: DENY
Content-Security-Policy: default-src 'self'
Cache-Control: no-store  (for responses with sensitive data)
```

---

### A07 — Identification and Authentication Failures (Broken Authentication)

**What it is:** Weaknesses in how users prove who they are, or how sessions are managed,
allowing attackers to impersonate users.

Covered in detail in `03-auth-xss-csrf-vulnerabilities.java`.

---

### A09 — Security Logging and Monitoring Failures
**What it is:** The application does not log security events, or doesn't alert on
suspicious activity, allowing breaches to go undetected for weeks or months.

**What to log (always):**
```
✅  Authentication attempts (success AND failure)
✅  Authorization failures (403 responses)
✅  Input validation failures (especially repeated failures from the same IP)
✅  Account management events (password changes, account creation, role changes)
✅  Sensitive data access (who accessed what PII, when)
✅  Application errors and exceptions
```

**What NOT to put in logs:**
```
❌  Passwords (even hashed)
❌  Full credit card numbers — last 4 digits only
❌  Social Security numbers / national IDs
❌  Session tokens or JWTs
❌  Personal health information beyond what's needed for debugging
```

**Example — good security log entry:**
```
2024-01-15 10:23:44 [SECURITY] WARN  Authentication failure
  user=alice@example.com ip=203.0.113.45 attempt=4 threshold=5
2024-01-15 10:23:55 [SECURITY] WARN  Account locked after repeated failures
  user=alice@example.com ip=203.0.113.45
```

---

## Summary: Secure Coding Checklist

```
INPUT
  [ ] Validate all inputs server-side (whitelist where possible)
  [ ] Parameterize all database queries
  [ ] Reject or escape special characters appropriate to the context

AUTHENTICATION
  [ ] Hash passwords with bcrypt/Argon2 — never MD5/SHA1
  [ ] Require strong passwords (length, complexity)
  [ ] Implement account lockout after N failures
  [ ] Use secure, random session IDs — regenerate on login
  [ ] Set session expiry; invalidate on logout

AUTHORIZATION
  [ ] Check authorization on every request, server-side
  [ ] Never authorize based on client-supplied roles or IDs
  [ ] Use deny-by-default

DATA
  [ ] HTTPS everywhere — HSTS header
  [ ] No secrets in source code or logs
  [ ] Encrypt sensitive data at rest

OUTPUT
  [ ] HTML-encode user content before rendering
  [ ] Set Content-Security-Policy header
  [ ] Never return stack traces to clients

DEPENDENCIES
  [ ] Keep libraries up to date (Dependabot, OWASP Dependency-Check)
  [ ] Pin versions; use a BOM

CONFIGURATION
  [ ] Lock down Actuator endpoints
  [ ] Disable debug features in production
  [ ] Use security headers
```

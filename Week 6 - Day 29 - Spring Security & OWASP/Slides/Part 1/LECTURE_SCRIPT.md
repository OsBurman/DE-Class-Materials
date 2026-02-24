# Day 29 Part 1 — Secure Coding & OWASP Top 10
## Lecture Script

**Total Time:** 60 minutes
**Delivery pace:** ~165 words/minute — conversational, instructor-led

---

## [00:00–02:00] Opening — Why This Day Matters

I want to start today with a question. How many of you have read about a company getting breached — a bank, a hospital, a retailer? Most people. Now: how many of those breaches were caused by something exotic — some nation-state zero-day that nobody could have predicted? Almost none of them. The overwhelming majority of real-world breaches are caused by the same ten categories of vulnerabilities, written about publicly, with solutions that have been known for decades.

That's what today is about. We're going to cover the OWASP Top 10 — the industry's authoritative list of the most common, most exploited, most preventable vulnerabilities. And we're going to cover them as working developers, not as security researchers. You will leave today knowing not just what the attacks are, but what specific lines of code prevent them.

---

## [02:00–07:00] Slide 2 — The Cost of Getting Security Wrong

Let's ground this in numbers first.

IBM publishes a Cost of a Data Breach report every year. In 2023, the average cost of a data breach was $4.45 million. That's the average. For healthcare, it's over $10 million. And that cost includes fines, legal fees, customer notification, credit monitoring, PR recovery, and engineering remediation work — not counting the reputational damage that's harder to quantify.

Here's the number that I think is more useful for you as a developer: the cost to fix a vulnerability at design time is about one dollar of developer time — you just write the code correctly. The cost to fix it in testing is roughly a hundred times more. The cost to fix it after it's in production, after a customer has reported it or an auditor has flagged it, is between one thousand and ten thousand times more. The cost curve is exponential, and it starts at the moment you write the first line.

The Equifax breach in 2017 exposed 147 million Americans' Social Security numbers, birth dates, and addresses. The cause? A critical vulnerability in Apache Struts — a Java web framework they were using — had been publicly disclosed and patched months before the breach. They knew about it. They just hadn't applied the patch. That cost them nearly $700 million in settlements. A software update.

The lesson isn't "security is hard." The lesson is: **security is a design concern, not a phase at the end of a project.** It doesn't get bolted on after features ship. It has to be part of how you write code from day one.

---

## [07:00–14:00] Slide 3 — Secure Coding Principles

Before we get into specific attacks, I want to give you seven principles that, if you internalize them, will prevent most vulnerabilities before you ever write a line of attack-specific code.

**Least Privilege.** Every component, user, and process gets only the access it needs — nothing more. Your service that reads book data should have SELECT permission only, not CREATE TABLE or DROP. Your CI/CD pipeline's database user should only be able to run migrations, not read customer data. When you design a new service, start with zero permissions and add what it specifically needs. Don't start with full access and try to take things away.

**Defense in Depth.** Layer your security controls. Don't rely on a single check. Validate input at the controller level AND use parameterized queries at the database level AND encode output at the view layer. If one layer fails — and at some point something will fail — the next layer still holds.

**Fail Securely.** This one trips people up. When something goes wrong in an authentication or authorization check — an exception, a null pointer, a database timeout — what should happen? Deny access. Always. The default posture on error is: no entry. Not: "well, the auth check threw an exception so we'll just let this one through." I have seen that bug in production code. The auth check crashed and the fallback was `return true`. Instant privilege escalation.

**Don't Trust Input.** This is probably the most violated principle in application code. Treat everything from the outside world as potentially hostile — URL parameters, form fields, HTTP headers, JSON bodies, values read from your own database if they were originally user-supplied, values from third-party APIs. Validate format. Validate length. Validate type. Sanitize before using in contexts where injection is possible. The source doesn't matter — if it was ever touched by a human or an external system, treat it as untrusted.

**Security by Default.** Ship your application in the most secure state by default, requiring explicit opt-in to enable features. Spring Boot does this well in some places — when you add `spring-boot-starter-security`, all endpoints require authentication immediately. You have to explicitly whitelist public endpoints. That's the right default.

**Keep It Simple.** Every line of code is a potential attack surface. Clever, complex code is harder to audit, harder to reason about, and more likely to contain edge cases that become vulnerabilities. When evaluating two implementations, prefer the simpler one.

**Avoid Security Through Obscurity.** This one comes up constantly: "Our API doesn't use standard REST patterns so attackers won't find it." "We renamed `/admin` to `/control-panel` so nobody will know it's there." That's not security. The moment that knowledge leaks — through a breach, a disgruntled employee, a log file — you have nothing. Obscurity can be one layer of defense in a defense-in-depth strategy, but it cannot be the only layer.

---

## [14:00–21:00] Slide 4 — OWASP and the Top 10

Let's talk about OWASP. The Open Web Application Security Project is a non-profit foundation that produces free, open resources for application security. The OWASP Top 10 is their flagship document — a list of the ten most critical web application security risks, compiled from real vulnerability data aggregated across thousands of applications.

It's updated every three to four years. We're currently on the 2021 edition. This list matters for several reasons: it's used as the baseline for PCI-DSS compliance assessments, SOC 2 audits, penetration testing reports, and security reviews at essentially every enterprise company. If you go into a code review and a security engineer says "this is an OWASP A01 finding," you need to know what that means.

The 2021 top ten: A01 is Broken Access Control — it was number five in 2017 and jumped to number one because it became the most widespread finding across the data they analyzed. A02 is Cryptographic Failures — previously called Sensitive Data Exposure, renamed to highlight that the root cause is usually a crypto failure. A03 is Injection — this category now includes XSS in addition to SQL and command injection. A04 is Insecure Design — a brand new entry for 2021, reflecting the industry's recognition that some vulnerabilities originate in the design phase, not the implementation phase. A05 is Security Misconfiguration. A06 is Vulnerable and Outdated Components — the Log4Shell vulnerability drove this one up the list dramatically. A07 is Identification and Authentication Failures. A08 is Software and Data Integrity Failures — a new category that includes supply chain attacks. A09 is Security Logging and Monitoring Failures. A10 is Server-Side Request Forgery, also new for 2021.

We're going to spend time on the ones most relevant to the Spring Boot applications you're building — injection, auth failures, misconfiguration, XSS, CSRF, and sensitive data exposure. Let's start with the number one finding.

---

## [21:00–28:00] Slide 5 — A01: Broken Access Control

Broken Access Control is the number one vulnerability in the OWASP 2021 list. It means a user can do something the application is not supposed to let them do.

There are two flavors. Vertical privilege escalation: a regular user accesses functionality intended only for administrators. They hit `/api/admin/users/delete/5` and it works because nobody checked whether they were an admin. Horizontal privilege escalation: a user accesses another user's data. They look at their order at `/api/orders/1234`, they change the number to `1235`, and they can see someone else's order.

This second pattern has a specific name: Insecure Direct Object Reference, or IDOR. The URL or request parameter contains a direct reference to a database ID, and the server doesn't verify that the requesting user actually owns that resource. Sequential integer IDs make this especially easy to exploit — you just increment the number.

Here's the vulnerable code. A GET endpoint for orders. It takes an ID from the URL, calls the service, returns the order. What's missing? There's no check that the order belongs to the authenticated user. Any logged-in user can read any order.

The fix: after fetching the order, compare its owner to the authenticated user. If they don't match, throw an `AccessDeniedException`. Spring Security will translate that to a 403 response.

The prevention philosophy is this: **deny by default, permit explicitly.** Your authorization checks happen server-side, always. Hiding a button in the UI is not authorization — it's aesthetics. If you can call the endpoint directly with a tool like Postman, your security lives in the server-side check, not in whether there's a visible button.

A quick note on IDs: using UUIDs instead of sequential integers for sensitive resources makes IDOR attacks significantly harder. There's no obvious pattern to enumerate. It's not a substitute for proper authorization checks — you still need those — but it's a layer of defense.

---

## [28:00–34:00] Slides 6–7 — Cryptographic Failures and SQL Injection

A02: Cryptographic Failures. The 2017 name was "Sensitive Data Exposure" and the rename is more accurate — most of the time, data is exposed because of a cryptographic mistake, not just because it wasn't encrypted at all.

The classic mistakes: storing passwords with MD5 or SHA-1. Using HTTP instead of HTTPS for pages that handle credentials. Hardcoding secrets in source code — I have reviewed GitHub repositories for companies where the database password, the API key, and the encryption key were all checked in to version control in plaintext. Those repos get scraped constantly. Storing credit card numbers or Social Security numbers beyond the point where they're needed.

The fixes: HTTPS everywhere, enforced with HSTS so the browser refuses to connect over plain HTTP for a full year. BCrypt for password hashing — we'll cover why BCrypt specifically in Part 2, but the short version is that it's deliberately slow and salted. Environment variables or secret managers for secrets — never source control. AES-256 for sensitive fields stored in the database. And the most important rule: **don't store data you don't need.** If you don't retain the credit card number after the initial transaction, you can't expose it.

Now injection. This one has a simple, non-negotiable fix. Let me show you the vulnerable pattern. You have a login endpoint. The developer builds a SQL query by concatenating the username parameter directly into the query string. Normal input — no problem. But an attacker enters `' OR '1'='1` as the username. The resulting query has a `WHERE` clause that always evaluates to true. It returns every row in the users table. Or they enter `'; DROP TABLE users; --` and the query becomes two statements — the first is their login query, the second drops your users table.

The fix is parameterized queries. You use a placeholder — a question mark in JDBC, a colon-parameter in JPQL — and the database treats the user's input as a data value, not as code. The database driver handles quoting and escaping. The SQL structure is fixed before the input is inserted.

In Spring Data JPA, derived query methods — `findByUsername(String username)` — are parameterized automatically. JPQL queries with `@Query` and `@Param` annotations are parameterized. The only dangerous pattern is string concatenation. **Never concatenate user input into a SQL string. This rule has no exceptions.**

---

## [34:00–40:00] Slides 8–9 — Command Injection and Authentication Failures

Command injection. Same principle as SQL injection, but the victim is the operating system shell instead of a database.

The vulnerability is passing user input to `Runtime.exec()` or similar, as a concatenated string. The shell interprets the string, and shell metacharacters — semicolons, pipes, ampersands — are command separators. An attacker enters `documents; cat /etc/passwd` and your process runs two commands: first the intended `ls documents`, then `cat /etc/passwd`. Or `.; rm -rf /` to destroy the filesystem.

The fix has two parts. First preference: don't invoke shell commands at all. Need to list files? Use `Files.list(Paths.get(directory))`. Need to read a file? Use `Files.readString(path)`. Java has APIs for almost everything you'd need the shell for. Use them.

If you genuinely need to run an external process, use `ProcessBuilder` with arguments as separate array elements — never as a concatenated string. When you use separate elements, the shell is not involved. The arguments are passed directly to the process. The semicolons and pipes are just literal characters in the argument string, not command separators.

A07: Authentication Failures. This is a broad category covering everything that can go wrong with who you are and how you prove it. Weak passwords — no enforcement of minimum length or complexity, so users set `password` and `123456`. No brute-force protection — an attacker can write a script to try ten thousand passwords per minute with no slowdown. Session tokens exposed in URLs — they show up in server logs, browser history, and referrer headers from link clicks. No session invalidation on logout — the old token still works after the user logs out. And credential stuffing — the attacker takes a list of username/password pairs from another company's breach and tries them all on your site. People reuse passwords.

Prevention: rate limiting and account lockout after failed attempts. Server-side session invalidation — not just deleting the cookie client-side, but marking the session as invalid in the server's session store. Session ID regeneration on successful login, to prevent session fixation. HttpOnly and Secure cookie flags. And encouraging or requiring MFA for sensitive accounts.

---

## [40:00–48:00] Slides 10–11 — XSS and CSRF

Cross-Site Scripting. The attacker injects malicious JavaScript into a page that is then executed in other users' browsers. Why can they do this? Because the browser trusts scripts from the site's domain. If the site serves the script — even if it was originally the attacker's input — the browser runs it with full access to that page's DOM, cookies, and local storage.

Three types. Stored XSS: the attacker stores the script in the database — a book review, a username, a comment. Every user who loads that content gets the script served to them. Reflected XSS: the malicious script is in a URL parameter. The attacker crafts a link and tricks the victim into clicking it. The server echoes the parameter into the response without encoding it, and the script runs. DOM-based XSS: no server involvement — the attack lives entirely in JavaScript that reads from a tainted source like the URL fragment and writes it to the DOM.

Here's a stored XSS example. An attacker submits a book review that contains a script tag. The script tag reads the user's session cookie and sends it to `evil.com`. Every user who views that book's reviews page gets their session cookie stolen. The attacker now has the session tokens of everyone who visited that page — they can impersonate all of them.

The primary defense is output encoding. Before you render user-supplied content as HTML, convert angle brackets, quotes, and ampersands to their HTML entity equivalents. `<` becomes `&lt;`. `>` becomes `&gt;`. `"` becomes `&quot;`. The text appears correctly on the page but can't be interpreted as HTML or JavaScript.

Here's the good news: if you're using Thymeleaf or React JSX, both auto-escape by default. When you use `th:text` in Thymeleaf or `{expression}` in JSX, they HTML-encode the output automatically. The dangerous patterns to watch for are `th:utext` in Thymeleaf — "unescaped text" — and `dangerouslySetInnerHTML` in React. Both bypass the auto-encoding. Only use them if you've explicitly sanitized the input yourself.

Content Security Policy is a second layer. You send a `Content-Security-Policy` header that tells the browser: only run scripts from this domain. Even if an attacker's script gets into your HTML, the browser refuses to execute it because it came from the wrong source.

CSRF. This is a different attack class. The attacker doesn't need to read anything from your site — they just need to make your authenticated user's browser send a request.

Here's the attack. The user logs into your banking application. Their browser has a session cookie. The user then visits a malicious site — maybe from a phishing email, maybe a forum post. That malicious site has a hidden HTML form targeting your banking app's transfer endpoint, with the attacker's account number and the amount $5,000. The form is submitted automatically by JavaScript as soon as the page loads. The browser sends the request to your bank — and automatically includes the session cookie, because that's how browsers work. Your bank sees an authenticated POST to `/transfer`. It processes it. The money moves.

The user doesn't click anything meaningful. They just visited a page.

CSRF tokens break this. The server generates a unique, random, unguessable token per session and includes it in every form. When the form is submitted, the token is sent along. The server validates that the token matches what it issued for that session. An attacker on a different domain cannot read this token — they can forge the form, but they can't know the token value. The server rejects any request that doesn't include the correct token.

The second mechanism is the `SameSite` cookie attribute. Setting `SameSite=Lax` tells the browser: don't send this cookie when the request originates from a different site. Cross-site form submissions stop carrying the session cookie. The request arrives without credentials and gets rejected.

Spring Security enables CSRF protection by default for web applications. Part 2 will cover exactly how to configure it.

---

## [48:00–54:00] Slides 12–14 — Misconfiguration, Vulnerable Components, Logging

Security Misconfiguration is consistently the most common finding when security professionals audit real applications. Not because it's hard to prevent — it's often the simplest thing in the room. It's common because developers focus on making features work, and the security of the configuration isn't always in front of their minds when they deploy.

Examples. Default credentials — `admin/admin` on the embedded H2 console, `root/root` on a management interface. Stack traces in error responses — when your application throws an exception and the full stack trace is returned in the HTTP response body, you've handed the attacker your framework version, your class package structure, and possibly your database schema. Spring Boot Actuator with `management.endpoints.web.exposure.include=*` in production — the `/actuator/env` endpoint returns all your environment variables, including database passwords and API keys. AWS S3 bucket set to public access — this has exposed patient records, financial data, and government documents at major organizations. Debug mode enabled in production.

The fix: environment-specific configuration. Dev, staging, and production never share the same `application.properties`. Sensitive features — H2 console, Actuator endpoints, debug logging, detailed error messages — are disabled in production configuration. Use Spring profiles. Before every deployment, review what endpoints are exposed and what information they return.

Vulnerable and Outdated Components — Log4Shell made this category impossible to ignore. A single Java logging statement could execute arbitrary remote code on the server. Not in some exotic configuration — in the default configuration used by essentially every Java application built in the past decade. Companies had Log4j embedded as a transitive dependency — hidden three or four levels deep in the dependency graph — and had no idea it was there.

The tooling exists to prevent this. `mvn dependency:tree` shows your full transitive dependency tree. The OWASP Dependency-Check Maven plugin scans your dependencies against the National Vulnerability Database and fails the build if a known CVE above your configured severity threshold is found. Run it in your CI/CD pipeline on every build. Subscribe to security advisories for Spring and Java.

A09 — Logging and Monitoring Failures. What you must log: every authentication event, successful and failed. Every authorization failure. Every input validation failure. These are the signals that tell you when an attack is underway. An account that fails login a thousand times in five minutes is being brute-forced. Multiple access-denied events across different resource IDs from the same user is someone probing for IDOR. If you're not logging these events, you won't know.

What you must never log: passwords — even hashed ones. Session tokens. Credit card numbers. JWTs. If these appear in your logs, anyone with log access has credential access.

---

## [54:00–58:00] Slide 15 — Security Headers

Security headers are HTTP response headers that tell the browser how to behave. They're a final layer of defense — when something slips through, these headers limit the damage.

`Strict-Transport-Security` — forces HTTPS. Once a browser sees this header, it refuses to connect to your site over plain HTTP for the duration you specify. Set it to one year. `X-Frame-Options: DENY` — prevents your page from being loaded inside an iframe. This defeats clickjacking attacks where an attacker loads your site invisibly and tricks users into clicking on it. `X-Content-Type-Options: nosniff` — prevents the browser from guessing the content type. Browsers that "sniff" content types can be tricked into interpreting a text file as JavaScript. `Content-Security-Policy` — one of the most powerful headers. It tells the browser which sources are allowed to load scripts, styles, images, and other resources. `default-src 'self'` means only load resources from your own domain — no CDNs, no inline scripts unless explicitly permitted.

The good news: Spring Security adds several of these headers automatically when you configure a `SecurityFilterChain`. You don't have to set them all manually. Part 2 will show you which ones Spring adds out of the box and how to customize them.

---

## [58:00–60:00] Slide 16 — Summary and Part 2 Preview

Let me give you the one-line version of everything we covered today.

Security is a design decision, not a cleanup task. Least privilege, defense in depth, fail securely, don't trust input. OWASP A01 is broken access control — check authorization server-side on every request. A02 is cryptographic failures — HTTPS, BCrypt, don't store what you don't need. A03 is injection — parameterized queries, no exceptions, ever. A05 is misconfiguration — environment-specific config, turn off dev features before prod. A06 is vulnerable components — scan your dependencies, keep them current. A07 is auth failures — rate limiting, proper session management. XSS — output encoding, Content Security Policy. CSRF — tokens and SameSite cookies.

In Part 2, we take all of this OWASP knowledge and implement it with Spring Security. We'll walk through the filter chain architecture — how Spring Security physically intercepts every request. We'll configure a `SecurityFilterChain`. We'll implement authentication backed by an in-memory user store and then by a real database using `UserDetailsService`. We'll encode passwords with BCrypt. And we'll configure CSRF protection correctly for both traditional web apps and REST APIs.

Take a five-minute break, and we'll continue.

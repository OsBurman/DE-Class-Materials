package com.academy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * Day 29 — Part 2: Spring Security Configuration
 * =================================================
 * Run: mvn spring-boot:run
 *
 * Test endpoints with HTTP Basic auth:
 *   Public (no auth):  curl http://localhost:8080/api/public/info
 *   User auth:         curl -u user:password123 http://localhost:8080/api/user/profile
 *   Admin auth:        curl -u admin:admin123 http://localhost:8080/api/admin/users
 *   Wrong creds:       curl -u user:wrong http://localhost:8080/api/user/profile  → 401
 *   Wrong role:        curl -u user:password123 http://localhost:8080/api/admin/users → 403
 *
 * Users:
 *   user  / password123 → ROLE_USER
 *   admin / admin123    → ROLE_USER + ROLE_ADMIN
 *   guest / guest123    → ROLE_GUEST
 *
 * Topics: SecurityFilterChain, InMemoryUserDetailsManager,
 *         BCryptPasswordEncoder, authentication vs authorization, CSRF, roles
 */
@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}

// ── Security Configuration ────────────────────────────────────────────────────

@EnableWebSecurity
class SecurityConfig {

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                // Public endpoints — no authentication required
                .requestMatchers("/api/public/**").permitAll()
                .requestMatchers("/api/security-reference").permitAll()
                // User endpoints — ROLE_USER required
                .requestMatchers("/api/user/**").hasRole("USER")
                // Admin endpoints — ROLE_ADMIN required
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                // Everything else requires authentication
                .anyRequest().authenticated()
            )
            .httpBasic(basic -> {})       // HTTP Basic auth (username:password in header)
            .formLogin(form -> form.disable())
            .csrf(csrf -> csrf.disable()); // Disabled for API demo (enable for session-based apps)
        return http.build();
    }

    @Bean
    UserDetailsService userDetailsService(PasswordEncoder encoder) {
        // In-memory users — for production use a database UserDetailsService
        var manager = new InMemoryUserDetailsManager();

        manager.createUser(User.withUsername("user")
            .password(encoder.encode("password123"))
            .roles("USER")
            .build());

        manager.createUser(User.withUsername("admin")
            .password(encoder.encode("admin123"))
            .roles("USER", "ADMIN")
            .build());

        manager.createUser(User.withUsername("guest")
            .password(encoder.encode("guest123"))
            .roles("GUEST")
            .build());

        return manager;
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }
}

// ── Controllers ───────────────────────────────────────────────────────────────

/** Public — no auth needed */
@RestController
@RequestMapping("/api/public")
class PublicController {

    @GetMapping("/info")
    public Map<String, Object> info() {
        return Map.of(
            "message", "This endpoint requires NO authentication",
            "app", "Spring Security Demo",
            "availableRoles", List.of("USER", "ADMIN", "GUEST"),
            "testCredentials", Map.of(
                "user", "user:password123",
                "admin", "admin:admin123",
                "guest", "guest:guest123"
            )
        );
    }
}

/** Auth — get current user info */
@RestController
@RequestMapping("/api/auth")
class AuthController {

    @GetMapping("/me")
    public Map<String, Object> getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("username", auth.getName());
        result.put("authorities", auth.getAuthorities().stream()
            .map(a -> a.getAuthority()).toList());
        result.put("authenticated", auth.isAuthenticated());
        result.put("principal_type", auth.getPrincipal().getClass().getSimpleName());
        return result;
    }
}

/** User — requires ROLE_USER */
@RestController
@RequestMapping("/api/user")
class UserController {

    @GetMapping("/profile")
    public Map<String, Object> getProfile() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return Map.of(
            "message", "You are authenticated with ROLE_USER",
            "username", auth.getName(),
            "roles", auth.getAuthorities().stream().map(a -> a.getAuthority()).toList()
        );
    }

    @GetMapping("/dashboard")
    public Map<String, String> getDashboard() {
        return Map.of("content", "Welcome to your dashboard! Requires ROLE_USER.");
    }
}

/** Admin — requires ROLE_ADMIN */
@RestController
@RequestMapping("/api/admin")
class AdminController {

    @GetMapping("/users")
    public Map<String, Object> getAllUsers() {
        // In production: load from DB
        return Map.of(
            "message", "Admin only — requires ROLE_ADMIN",
            "users", List.of(
                Map.of("username", "user",  "roles", List.of("ROLE_USER")),
                Map.of("username", "admin", "roles", List.of("ROLE_USER", "ROLE_ADMIN")),
                Map.of("username", "guest", "roles", List.of("ROLE_GUEST"))
            )
        );
    }

    @GetMapping("/stats")
    public Map<String, Object> getStats() {
        return Map.of(
            "message", "Admin stats — requires ROLE_ADMIN",
            "totalUsers", 3,
            "activeUsers", 2
        );
    }
}

/** Security Reference */
@RestController
@RequestMapping("/api")
class SecurityReferenceController {

    @GetMapping("/security-reference")
    public Map<String, Object> reference() {
        Map<String, Object> ref = new LinkedHashMap<>();
        ref.put("title", "Spring Security Reference");

        Map<String, String> components = new LinkedHashMap<>();
        components.put("SecurityFilterChain", "Bean that defines URL authorization rules. Processed in order — first match wins.");
        components.put("@EnableWebSecurity", "Enables Spring Security. Activates the security filter chain.");
        components.put("UserDetailsService", "Interface to load user by username. Implement for DB-backed auth.");
        components.put("PasswordEncoder", "BCryptPasswordEncoder — always hash passwords, never store plaintext.");
        components.put("Authentication", "Holds current user: getName() (username), getAuthorities() (roles), isAuthenticated()");
        components.put("SecurityContextHolder", "Thread-local holder of Authentication. Access anywhere: SecurityContextHolder.getContext().getAuthentication()");
        components.put("Principal", "The currently authenticated user (username string or UserDetails object)");
        ref.put("components", components);

        Map<String, String> authz = new LinkedHashMap<>();
        authz.put("permitAll()", "No authentication required");
        authz.put("authenticated()", "Any authenticated user");
        authz.put("hasRole('USER')", "Must have ROLE_USER (Spring adds ROLE_ prefix)");
        authz.put("hasAnyRole('USER','ADMIN')", "Must have any of the specified roles");
        authz.put("hasAuthority('ROLE_USER')", "Exact authority string match (no prefix added)");
        authz.put("denyAll()", "Always deny");
        ref.put("authorizationRules", authz);

        Map<String, String> auth = new LinkedHashMap<>();
        auth.put("httpBasic()", "Base64 credentials in Authorization header — simple but no logout");
        auth.put("formLogin()", "HTML form login with session cookie");
        auth.put("oauth2Login()", "OAuth2/OIDC (Google, GitHub, etc.)");
        auth.put("JWT Filter", "Custom OncePerRequestFilter — stateless, no session (Day 30)");
        ref.put("authenticationMechanisms", auth);

        Map<String, String> csrf = new LinkedHashMap<>();
        csrf.put("What is CSRF?", "Cross-Site Request Forgery — attacker tricks browser into sending authenticated requests");
        csrf.put("When to enable", "Browser-based apps using session cookies — Spring Security enables by default");
        csrf.put("When to disable", "Stateless APIs using JWT — no session = no CSRF risk");
        csrf.put("CSRF Token", "Server includes token in form; browser must send it back; validates request is legitimate");
        ref.put("csrf", csrf);

        Map<String, String> passwords = new LinkedHashMap<>();
        passwords.put("BCrypt cost=10", "~100ms per hash — recommended minimum");
        passwords.put("BCrypt cost=12", "~250ms per hash — more secure");
        passwords.put("BCrypt cost=14", "~1000ms per hash — very secure but slow for login");
        passwords.put("Why BCrypt?", "Salted (unique per hash), adaptive (increase cost as hardware improves), designed for passwords");
        passwords.put("Why NOT MD5/SHA1?", "Not salted, too fast (billions/sec on GPU), rainbow tables, cracked instantly");
        ref.put("passwordStorage", passwords);

        return ref;
    }
}

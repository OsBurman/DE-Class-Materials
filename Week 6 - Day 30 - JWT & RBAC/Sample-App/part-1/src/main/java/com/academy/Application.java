package com.academy;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.util.*;

/**
 * Day 30 — Part 1: JWT Authentication with Spring Security
 * ==========================================================
 * Run: mvn spring-boot:run
 *
 * HOW TO TEST:
 *   Step 1 — Login (get token):
 *     POST http://localhost:8080/api/auth/login
 *     Body: {"username":"alice","password":"password"}
 *
 *   Step 2 — Use token:
 *     GET http://localhost:8080/api/protected/hello
 *     Header: Authorization: Bearer <token from step 1>
 *
 *   Step 3 — Check JWT structure at https://jwt.io
 *
 * Users:
 *   alice / password  → ROLE_USER
 *   admin / admin     → ROLE_USER + ROLE_ADMIN
 *
 * Topics: JWT structure, JJWT library, OncePerRequestFilter,
 *         stateless sessions, SecurityContextHolder, Bearer tokens
 */
@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}

// ── JWT Utility ───────────────────────────────────────────────────────────────
@Component
class JwtUtil {

    private final SecretKey secretKey;
    private final long expiration;

    public JwtUtil(
        @Value("${jwt.secret}") String secret,
        @Value("${jwt.expiration}") long expiration) {
        // Keys.hmacShaKeyFor requires at least 256 bits for HS256
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes());
        this.expiration = expiration;
    }

    /** Generate a signed JWT with username and roles */
    public String generateToken(String username, List<String> roles) {
        return Jwts.builder()
            .subject(username)
            .claim("roles", roles)
            .issuedAt(new Date())
            .expiration(new Date(System.currentTimeMillis() + expiration))
            .signWith(secretKey)
            .compact();
    }

    /** Validate token — returns false if expired or tampered */
    public boolean validateToken(String token) {
        try {
            Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    /** Extract the subject (username) from a valid token */
    public String extractUsername(String token) {
        return getClaims(token).getSubject();
    }

    /** Extract the roles claim from a valid token */
    @SuppressWarnings("unchecked")
    public List<String> extractRoles(String token) {
        Object roles = getClaims(token).get("roles");
        if (roles instanceof List<?>) return (List<String>) roles;
        return List.of();
    }

    private Claims getClaims(String token) {
        return Jwts.parser()
            .verifyWith(secretKey)
            .build()
            .parseSignedClaims(token)
            .getPayload();
    }
}

// ── JWT Auth Filter ────────────────────────────────────────────────────────────
@Component
class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    public JwtAuthFilter(JwtUtil jwtUtil, UserDetailsService userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain)
        throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        // Only process requests with Bearer token
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);

            if (jwtUtil.validateToken(token)) {
                String username = jwtUtil.extractUsername(token);
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                // Set Authentication in SecurityContext — marks request as authenticated
                UsernamePasswordAuthenticationToken auth =
                    new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        }

        chain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        // Skip JWT filter for public auth endpoints
        return request.getServletPath().startsWith("/api/auth/login");
    }
}

// ── Security Config ────────────────────────────────────────────────────────────
@EnableWebSecurity
class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    public SecurityConfig(JwtAuthFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/api/jwt-reference").permitAll()
                .anyRequest().authenticated()
            )
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .csrf(csrf -> csrf.disable())
            .httpBasic(basic -> basic.disable())
            .formLogin(form -> form.disable())
            // Add JWT filter BEFORE the standard username/password filter
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    UserDetailsService userDetailsService(PasswordEncoder encoder) {
        var manager = new InMemoryUserDetailsManager();
        manager.createUser(User.withUsername("alice")
            .password(encoder.encode("password"))
            .roles("USER").build());
        manager.createUser(User.withUsername("admin")
            .password(encoder.encode("admin"))
            .roles("USER", "ADMIN").build());
        return manager;
    }

    @Bean
    PasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(); }

    @Bean
    AuthenticationManager authManager(HttpSecurity http) throws Exception {
        return http.getSharedObject(
            org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder.class)
            .build();
    }
}

// ── Auth Controller ────────────────────────────────────────────────────────────
@RestController
@RequestMapping("/api/auth")
class AuthController {

    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;

    public AuthController(JwtUtil jwtUtil,
                          UserDetailsService userDetailsService,
                          PasswordEncoder passwordEncoder) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> creds) {
        String username = creds.get("username");
        String password = creds.get("password");

        try {
            UserDetails user = userDetailsService.loadUserByUsername(username);
            if (!passwordEncoder.matches(password, user.getPassword())) {
                return ResponseEntity.status(401).body(Map.of("error", "Invalid credentials"));
            }
            List<String> roles = user.getAuthorities().stream()
                .map(a -> a.getAuthority()).toList();
            String token = jwtUtil.generateToken(username, roles);

            Map<String, Object> response = new LinkedHashMap<>();
            response.put("token",     token);
            response.put("username",  username);
            response.put("roles",     roles);
            response.put("expiresIn", "24 hours");
            response.put("type",      "Bearer");
            return ResponseEntity.ok(response);
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(401).body(Map.of("error", "Invalid credentials"));
        }
    }
}

// ── Protected Controller ───────────────────────────────────────────────────────
@RestController
@RequestMapping("/api/protected")
class ProtectedController {

    @GetMapping("/hello")
    public Map<String, String> hello() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return Map.of(
            "message",  "Hello, " + auth.getName() + "! You are authenticated via JWT.",
            "username", auth.getName()
        );
    }

    @GetMapping("/me")
    public Map<String, Object> me() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return Map.of(
            "username",    auth.getName(),
            "authorities", auth.getAuthorities().stream().map(a -> a.getAuthority()).toList(),
            "authenticated", true
        );
    }
}

// ── JWT Reference Controller ───────────────────────────────────────────────────
@RestController
@RequestMapping("/api")
class JwtReferenceController {

    @GetMapping("/jwt-reference")
    public Map<String, Object> jwtReference() {
        Map<String, Object> ref = new LinkedHashMap<>();
        ref.put("topic", "JWT Authentication");

        // JWT Structure
        Map<String, String> structure = new LinkedHashMap<>();
        structure.put("format",    "header.payload.signature (3 parts separated by dots)");
        structure.put("header",    "Base64URL({\"alg\":\"HS256\",\"typ\":\"JWT\"})");
        structure.put("payload",   "Base64URL({\"sub\":\"alice\",\"roles\":[\"ROLE_USER\"],\"iat\":...,\"exp\":...})");
        structure.put("signature", "HMACSHA256(base64(header) + '.' + base64(payload), secret)");
        structure.put("note",      "Payload is BASE64 ENCODED, not encrypted — do NOT store sensitive data");
        ref.put("jwtStructure", structure);

        // Standard Claims
        Map<String, String> claims = new LinkedHashMap<>();
        claims.put("sub", "Subject — who the token is about (username)");
        claims.put("iss", "Issuer — who issued the token (your app)");
        claims.put("aud", "Audience — who the token is intended for");
        claims.put("exp", "Expiration — Unix timestamp when token expires");
        claims.put("iat", "Issued At — Unix timestamp when token was created");
        claims.put("jti", "JWT ID — unique identifier (prevents replay attacks)");
        ref.put("standardClaims", claims);

        // Auth Flow
        ref.put("authFlow", List.of(
            "1. Client sends POST /api/auth/login with {username, password}",
            "2. Server validates credentials",
            "3. Server generates JWT signed with secret key",
            "4. Server returns JWT to client",
            "5. Client stores JWT (localStorage or httpOnly cookie)",
            "6. Client sends JWT in Authorization: Bearer <token> header",
            "7. Server JwtAuthFilter validates signature and extracts user info",
            "8. Server processes request as authenticated user"
        ));

        // Session vs Token
        Map<String, String> comparison = new LinkedHashMap<>();
        comparison.put("session_based", "Server stores session in memory/DB; stateful; uses JSESSIONID cookie");
        comparison.put("token_based",   "Server is stateless; client stores token; scales horizontally");
        comparison.put("jwt_benefit",   "No server-side session storage; microservices-friendly; self-contained");
        comparison.put("jwt_caution",   "Cannot invalidate tokens until expiry (use refresh tokens + short expiry)");
        ref.put("sessionVsToken", comparison);

        // Security best practices
        ref.put("securityBestPractices", List.of(
            "Keep tokens short-lived (15min access + 7d refresh token pattern)",
            "Use HTTPS — tokens in plaintext HTTP can be intercepted",
            "Store in httpOnly cookies (not localStorage) to prevent XSS theft",
            "Use a strong secret key (>= 256 bits for HS256)",
            "Include only necessary claims — payload is visible to anyone",
            "Implement token revocation list for logout (or use short expiry)"
        ));

        ref.put("howToTest", List.of(
            "1. POST http://localhost:8080/api/auth/login  body: {\"username\":\"alice\",\"password\":\"password\"}",
            "2. Copy the token from the response",
            "3. GET http://localhost:8080/api/protected/hello  header: Authorization: Bearer <token>",
            "4. Paste token at https://jwt.io to inspect claims"
        ));

        return ref;
    }
}

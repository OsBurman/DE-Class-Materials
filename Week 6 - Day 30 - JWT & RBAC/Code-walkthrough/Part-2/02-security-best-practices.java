package com.bookstore.security.best_practices;

// =============================================================================
// SECURITY BEST PRACTICES — Day 30: JWT & RBAC
// =============================================================================
// This file demonstrates:
//   1. Custom AuthenticationEntryPoint — JSON 401 for unauthenticated requests
//   2. Custom AccessDeniedHandler — JSON 403 for unauthorized requests
//   3. Handling authentication failures gracefully
//   4. Security headers for REST APIs
//   5. API Keys — header-based authentication (X-API-Key)
//   6. Token management patterns (rotation, blacklisting concepts)
//   7. HTTPS and secure communication configuration
//   8. Rate limiting and throttling concepts (token bucket algorithm)
//   9. Complete production-ready SecurityFilterChain
// =============================================================================

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;


// =============================================================================
// SECTION 1: Custom AuthenticationEntryPoint — 401 Unauthorized
// =============================================================================

/**
 * AuthenticationEntryPoint is invoked when a request arrives at a secured
 * endpoint WITHOUT valid credentials — no token, expired token, or bad token.
 *
 * Default Spring Security behavior: redirect to a login page or return
 * an HTML error page — both useless for a REST API client.
 *
 * Our custom handler: return a clean JSON 401 response.
 *
 * Triggered by: AccessDeniedException when the user is anonymous (not authenticated).
 * Do NOT confuse with 403 — that's handled by AccessDeniedHandler below.
 */
@Component
class BookstoreAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException)
            throws IOException {

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);    // 401
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);  // "application/json"
        response.setCharacterEncoding("UTF-8");

        // Return a structured JSON error body
        Map<String, Object> errorBody = Map.of(
            "status",    401,
            "error",     "Unauthorized",
            "message",   "Authentication required. Please provide a valid Bearer token.",
            "timestamp", Instant.now().toString(),
            "path",      request.getRequestURI()
        );

        // Write JSON to the response
        response.getWriter().write(objectMapper.writeValueAsString(errorBody));
    }
}


// =============================================================================
// SECTION 2: Custom AccessDeniedHandler — 403 Forbidden
// =============================================================================

/**
 * AccessDeniedHandler is invoked when an AUTHENTICATED user tries to access
 * a resource they don't have permission for — wrong role.
 *
 * Example: Alice (ROLE_USER) tries to hit /api/admin/** → 403 Forbidden.
 *
 * 401 vs 403:
 *   401 Unauthorized → not authenticated (no token, bad token)
 *   403 Forbidden    → authenticated but not authorized (wrong role)
 */
@Component
class BookstoreAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException accessDeniedException)
            throws IOException {

        response.setStatus(HttpServletResponse.SC_FORBIDDEN);       // 403
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        Map<String, Object> errorBody = Map.of(
            "status",    403,
            "error",     "Forbidden",
            "message",   "You don't have permission to access this resource.",
            "timestamp", Instant.now().toString(),
            "path",      request.getRequestURI()
        );

        response.getWriter().write(objectMapper.writeValueAsString(errorBody));
    }
}


// =============================================================================
// SECTION 3: API Key Authentication Filter
// =============================================================================

/**
 * Some APIs need machine-to-machine (M2M) authentication without user context.
 * Common pattern: X-API-Key header containing a pre-shared API key.
 *
 * Use cases:
 *   - Third-party integrations (e.g. a payment processor posting webhooks)
 *   - Server-to-server service calls
 *   - Public partner APIs
 *
 * Flow:
 *   1. Client includes X-API-Key: <key> in the request header
 *   2. ApiKeyAuthFilter looks up the key in the key store
 *   3. If valid, creates an Authentication and sets SecurityContextHolder
 *   4. If invalid or missing, passes through (JWT filter handles normal users)
 *
 * IMPORTANT: API keys must be:
 *   - Long random strings (at least 32 bytes of entropy)
 *   - Stored as hashed values in the database (never plaintext)
 *   - Rotatable — clients should be able to refresh keys
 *   - Scoped — each key has defined permissions
 */
@Component
class ApiKeyAuthFilter extends OncePerRequestFilter {

    private static final String API_KEY_HEADER = "X-API-Key";

    // In production: inject from database or secrets manager
    // Key format: sha256 hash of the raw key stored here; raw key given to client
    private final Map<String, String> apiKeyToServiceName = Map.of(
        "bookstore-sk-abc123def456ghi789jkl012mno345p", "inventory-service",
        "bookstore-sk-xyz987uvw654rst321qpo098nml765k", "payment-service"
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String apiKey = request.getHeader(API_KEY_HEADER);

        if (apiKey != null) {
            // Look up the service name for this API key
            String serviceName = apiKeyToServiceName.get(apiKey);

            if (serviceName != null) {
                // Create an Authentication for this service with minimal privileges
                List<SimpleGrantedAuthority> authorities = List.of(
                        new SimpleGrantedAuthority("ROLE_SERVICE"),
                        new SimpleGrantedAuthority("api:read"),
                        new SimpleGrantedAuthority("api:write")
                );

                UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(
                                serviceName,   // principal — identifies the calling service
                                null,          // credentials
                                authorities
                        );

                SecurityContextHolder.getContext().setAuthentication(auth);
                // Note: a service calling via API key doesn't need a user object — just a name
            }
            // If API key not found → security context stays empty → 401 from next filter
        }

        filterChain.doFilter(request, response);
    }
}


// =============================================================================
// SECTION 4: Rate Limiting — Throttling Concepts and Implementation
// =============================================================================

/**
 * RATE LIMITING CONCEPTS
 * =======================
 * Rate limiting protects your API from:
 *   - DDoS attacks — flooding with requests
 *   - Brute force attacks — trying millions of passwords
 *   - Resource exhaustion — expensive queries run too many times
 *   - Scrapers — bulk unauthorized data collection
 *
 * ALGORITHMS:
 *
 * Token Bucket (most common):
 *   - Each user/IP has a "bucket" holding N tokens
 *   - Each request consumes one token
 *   - Tokens refill at a fixed rate (e.g., 10/minute)
 *   - If bucket empty → 429 Too Many Requests
 *
 * Fixed Window:
 *   - Count requests in a fixed time window (e.g., 100/minute)
 *   - Reset counter at the window boundary
 *   - Problem: burst allowed at window boundary
 *
 * Sliding Window:
 *   - Like fixed window but the window slides with time
 *   - Smoother, more accurate, more memory-intensive
 *
 * Production tools:
 *   - Bucket4j (Java library — integrates with Spring)
 *   - Spring Cloud Gateway built-in rate limiter (Redis-backed)
 *   - Nginx / API Gateway rate limiting at the infrastructure level
 *   - AWS API Gateway throttling
 */
@Component
class RateLimitingFilter extends OncePerRequestFilter {

    // In-memory rate limiter — FOR DEMONSTRATION ONLY
    // In production, use Redis so limits work across multiple server instances
    private final ConcurrentHashMap<String, RateLimitBucket> buckets = new ConcurrentHashMap<>();

    // Configuration: 100 requests per 60 seconds per IP
    private static final int MAX_REQUESTS_PER_WINDOW = 100;
    private static final long WINDOW_DURATION_MS     = 60_000; // 1 minute

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String clientIdentifier = getClientIdentifier(request);
        RateLimitBucket bucket = buckets.computeIfAbsent(
                clientIdentifier,
                k -> new RateLimitBucket(MAX_REQUESTS_PER_WINDOW, WINDOW_DURATION_MS)
        );

        if (!bucket.tryConsume()) {
            // 429 Too Many Requests
            response.setStatus(429);
            response.setContentType("application/json");
            response.setHeader("Retry-After", String.valueOf(bucket.secondsUntilReset()));
            response.setHeader("X-RateLimit-Limit",     String.valueOf(MAX_REQUESTS_PER_WINDOW));
            response.setHeader("X-RateLimit-Remaining", "0");
            response.getWriter().write(
                "{\"error\": \"Too Many Requests\", \"message\": \"Rate limit exceeded. " +
                "Try again in " + bucket.secondsUntilReset() + " seconds.\"}"
            );
            return;
        }

        // Add rate limit headers to the response for client awareness
        response.setHeader("X-RateLimit-Limit",     String.valueOf(MAX_REQUESTS_PER_WINDOW));
        response.setHeader("X-RateLimit-Remaining", String.valueOf(bucket.remaining()));

        filterChain.doFilter(request, response);
    }

    /**
     * Identify the client:
     *   - Prefer the authenticated username (if available) — rate limit per user
     *   - Fall back to IP address for unauthenticated requests
     *   - Check X-Forwarded-For for clients behind a proxy/load balancer
     */
    private String getClientIdentifier(HttpServletRequest request) {
        // If authenticated, rate limit by username
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            return "user:" + auth.getName();
        }

        // For anonymous requests, use IP
        String forwardedFor = request.getHeader("X-Forwarded-For");
        if (forwardedFor != null && !forwardedFor.isEmpty()) {
            return "ip:" + forwardedFor.split(",")[0].trim(); // first IP in chain
        }

        return "ip:" + request.getRemoteAddr();
    }

    // -------------------------------------------------------------------------
    // Simple token-bucket implementation (demonstration)
    // -------------------------------------------------------------------------
    /**
     * Token bucket:
     *   - maxTokens fill the bucket at construction
     *   - Each tryConsume() decrements the count
     *   - After windowDurationMs, the bucket refills
     *
     * For production: use Bucket4j's sophisticated implementation with Redis
     */
    static class RateLimitBucket {
        private final int maxTokens;
        private final long windowDurationMs;
        private final AtomicInteger tokens;
        private volatile long windowStart;

        RateLimitBucket(int maxTokens, long windowDurationMs) {
            this.maxTokens = maxTokens;
            this.windowDurationMs = windowDurationMs;
            this.tokens = new AtomicInteger(maxTokens);
            this.windowStart = System.currentTimeMillis();
        }

        synchronized boolean tryConsume() {
            long now = System.currentTimeMillis();
            // Check if the window has expired — if so, reset
            if (now - windowStart >= windowDurationMs) {
                tokens.set(maxTokens);
                windowStart = now;
            }
            if (tokens.get() > 0) {
                tokens.decrementAndGet();
                return true;
            }
            return false; // bucket empty — reject request
        }

        int remaining() {
            return Math.max(0, tokens.get());
        }

        long secondsUntilReset() {
            long elapsed = System.currentTimeMillis() - windowStart;
            return Math.max(0, (windowDurationMs - elapsed) / 1000);
        }
    }
}


// =============================================================================
// SECTION 5: HTTPS and Secure Communication
// =============================================================================

/*
 * HTTPS CONFIGURATION
 * ====================
 * For a Spring Boot REST API, HTTPS is configured in two layers:
 *
 * Layer 1 — Infrastructure (preferred in production):
 *   Use a reverse proxy (nginx, AWS ALB, Cloudflare) to terminate TLS.
 *   The proxy handles HTTPS externally; Spring Boot runs HTTP internally.
 *   No Spring config needed.
 *
 * Layer 2 — Spring Boot (development / simple deployments):
 *   Configure a keystore in application.properties:
 *
 *     server.port=8443
 *     server.ssl.enabled=true
 *     server.ssl.key-store=classpath:bookstore-keystore.p12
 *     server.ssl.key-store-password=${KEYSTORE_PASSWORD}
 *     server.ssl.key-store-type=PKCS12
 *     server.ssl.key-alias=bookstore
 *
 * HTTP Strict Transport Security (HSTS):
 *   Tells browsers to ONLY use HTTPS for this domain for the specified duration.
 *   Once a browser sees this header, it will refuse HTTP connections.
 *
 *     Strict-Transport-Security: max-age=31536000; includeSubDomains; preload
 *
 *   max-age=31536000 = 1 year
 *   includeSubDomains = applies to all subdomains
 *   preload = submit to browser HSTS preload lists (permanent — can't easily undo)
 *
 * HTTP → HTTPS redirect in Spring:
 *   Use a second connector on port 80 that redirects to 443.
 *   Or configure at the nginx/load balancer level.
 */

// application.properties for HTTPS (shown as a Java comment for walkthrough):
/*
# Production HTTPS via reverse proxy (preferred):
# No Spring SSL config — nginx handles TLS termination

# Dev/simple HTTPS directly in Spring Boot:
# server.port=8443
# server.ssl.enabled=true
# server.ssl.key-store=classpath:keystore.p12
# server.ssl.key-store-password=${KEYSTORE_PASSWORD}
# server.ssl.key-store-type=PKCS12

# Require HTTPS in Spring Security (after SSL is configured):
# See requiresSecure() in SecurityFilterChain below
*/


// =============================================================================
// SECTION 6: Token Management — Rotation and Blacklisting
// =============================================================================

/**
 * TOKEN MANAGEMENT PATTERNS
 * ==========================
 *
 * 1. REFRESH TOKEN ROTATION
 *    When issuing a new access token, also issue a NEW refresh token
 *    and invalidate the old one. This limits the damage if a refresh token
 *    is stolen — the attacker's copy stops working after first use.
 *
 *    POST /api/auth/refresh
 *      → validate refresh token
 *      → issue new access token (short-lived: 15 min)
 *      → issue new refresh token (long-lived: 30 days)
 *      → invalidate the old refresh token (mark as used in DB)
 *
 * 2. TOKEN BLACKLIST (for access token revocation)
 *    JWT access tokens can't be revoked without a blacklist.
 *    Store revoked JTI (JWT ID) in Redis with TTL = token's remaining lifetime.
 *
 *    JwtAuthenticationFilter checks:
 *      → extract JTI from token
 *      → if Redis.exists("blacklist:" + jti) → reject with 401
 *      → proceed normally
 *
 * 3. TOKEN FAMILY DETECTION (detect refresh token theft)
 *    If a refresh token from a previous rotation is used again,
 *    someone has stolen it. Invalidate ALL tokens for that user
 *    and force re-login.
 *
 * 4. SHORT ACCESS TOKEN LIFETIME
 *    The simpler alternative to blacklisting: keep access tokens so short
 *    (15 min) that stolen tokens expire quickly. Balance UX vs security.
 */

@Component
class TokenBlacklistService {

    // In production: inject RedisTemplate and store in Redis with TTL
    // Here: in-memory set for demonstration (doesn't survive restart)
    private final Set<String> blacklistedJtis = ConcurrentHashMap.newKeySet();

    /**
     * Add a JTI to the blacklist (e.g., on logout or when a compromise is detected).
     * In production: Redis SETEX blacklist:<jti> <remainingSeconds> "revoked"
     */
    public void blacklist(String jti) {
        blacklistedJtis.add(jti);
    }

    /**
     * Check if a JTI has been revoked.
     * In production: Redis EXISTS blacklist:<jti>
     */
    public boolean isBlacklisted(String jti) {
        return blacklistedJtis.contains(jti);
    }
}


// =============================================================================
// SECTION 7: Production-Ready SecurityFilterChain
// =============================================================================

/**
 * Full production-ready security configuration combining all the pieces:
 *   - Custom entry point and access denied handler
 *   - Security headers
 *   - CORS
 *   - Rate limiting
 *   - API key authentication
 *   - JWT authentication
 *   - URL-based authorization
 *   - HTTPS enforcement (HSTS)
 */
@Configuration
@EnableWebSecurity
class ProductionSecurityConfig {

    @Autowired
    private BookstoreAuthenticationEntryPoint authEntryPoint;

    @Autowired
    private BookstoreAccessDeniedHandler accessDeniedHandler;

    @Autowired
    private ApiKeyAuthFilter apiKeyAuthFilter;

    // Injected from Part 1: JwtAuthenticationFilter
    // @Autowired
    // private JwtAuthenticationFilter jwtAuthFilter;

    @Bean
    public SecurityFilterChain productionFilterChain(HttpSecurity http) throws Exception {
        http
            // ---------------------------------------------------------------
            // CSRF: disabled for stateless JWT API
            // ---------------------------------------------------------------
            .csrf(csrf -> csrf.disable())

            // ---------------------------------------------------------------
            // CORS: configure allowed origins (do not use * in production)
            // ---------------------------------------------------------------
            .cors(cors -> cors.configurationSource(request -> {
                var config = new org.springframework.web.cors.CorsConfiguration();
                config.setAllowedOrigins(List.of(
                    "https://bookstore.com",
                    "https://www.bookstore.com",
                    "https://admin.bookstore.com"
                ));
                config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
                config.setAllowedHeaders(List.of("Authorization", "Content-Type", "X-API-Key", "X-CSRF-TOKEN"));
                config.setAllowCredentials(true);
                config.setMaxAge(3600L); // 1 hour preflight cache
                return config;
            }))

            // ---------------------------------------------------------------
            // Sessions: fully stateless
            // ---------------------------------------------------------------
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )

            // ---------------------------------------------------------------
            // URL-based authorization
            // ---------------------------------------------------------------
            .authorizeHttpRequests(auth -> auth
                // Open to all
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/api/books/public/**").permitAll()
                .requestMatchers("/actuator/health", "/actuator/info").permitAll()
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll() // CORS preflight

                // API-key-authenticated services
                .requestMatchers("/api/webhooks/**").hasRole("SERVICE")

                // Role-restricted paths
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.POST, "/api/books/**").hasAnyRole("ADMIN", "LIBRARIAN")
                .requestMatchers(HttpMethod.PUT, "/api/books/**").hasAnyRole("ADMIN", "LIBRARIAN")

                // Everything else: must be authenticated
                .anyRequest().authenticated()
            )

            // ---------------------------------------------------------------
            // Custom error handlers
            // ---------------------------------------------------------------
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint(accessDeniedHandler ->
                    // This is actually authEntryPoint — handles 401
                    authEntryPoint.commence(null, null, null)
                )
                .accessDeniedHandler(accessDeniedHandler)
                // ↑ Cleaner way to wire them in:
            )

            // ---------------------------------------------------------------
            // Security headers for REST APIs
            // ---------------------------------------------------------------
            .headers(headers -> headers
                // X-Frame-Options: DENY — prevent clickjacking
                .frameOptions(frame -> frame.deny())

                // X-Content-Type-Options: nosniff — prevent MIME sniffing
                .contentTypeOptions(cto -> {}) // enabled by default

                // HTTP Strict Transport Security — force HTTPS
                .httpStrictTransportSecurity(hsts -> hsts
                    .includeSubDomains(true)
                    .maxAgeInSeconds(31536000)  // 1 year
                    .preload(true)
                )

                // Content Security Policy — restrict what can be loaded
                // For a pure REST API (no HTML served), this is less critical
                // but still good practice
                .contentSecurityPolicy(csp -> csp
                    .policyDirectives("default-src 'none'; frame-ancestors 'none'")
                )

                // X-XSS-Protection: 0 — modern browsers prefer CSP; this header is deprecated
                // but some older browsers still check it
                .xssProtection(xss -> xss.disable())
            )

            // ---------------------------------------------------------------
            // Disable form login and HTTP Basic — REST API only
            // ---------------------------------------------------------------
            .formLogin(form -> form.disable())
            .httpBasic(basic -> basic.disable())

            // ---------------------------------------------------------------
            // Filter chain order:
            //   RateLimitingFilter → ApiKeyAuthFilter → JwtAuthFilter → controller
            // ---------------------------------------------------------------
            .addFilterBefore(apiKeyAuthFilter, UsernamePasswordAuthenticationFilter.class);
            // .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
            // (from Part 1 — would be added here)

        // ---------------------------------------------------------------
        // Exception handler wiring (direct bean injection — cleaner)
        // ---------------------------------------------------------------
        http.exceptionHandling(ex -> ex
            .authenticationEntryPoint(authEntryPoint)   // 401
            .accessDeniedHandler(accessDeniedHandler)   // 403
        );

        return http.build();
    }
}


// =============================================================================
// SECTION 8: Security Best Practices for REST APIs — Summary Checklist
// =============================================================================

/*
 * REST API SECURITY BEST PRACTICES CHECKLIST
 * ============================================
 *
 * AUTHENTICATION
 *   [x] Use JWT for stateless authentication
 *   [x] Short access token lifetime (15 min – 1 hr)
 *   [x] Refresh tokens in HTTP-only cookies when possible
 *   [x] BCrypt for password storage (cost factor 12+)
 *   [x] Generic error messages — don't reveal if user exists
 *   [x] Account lockout after failed attempts
 *
 * AUTHORIZATION
 *   [x] @EnableMethodSecurity with @PreAuthorize on services
 *   [x] URL-based security as first line of defense
 *   [x] Deny by default — anyRequest().authenticated() as catch-all
 *   [x] hasRole() / hasAnyRole() for role checks
 *   [x] Ownership checks: #username == authentication.name
 *
 * TOKEN SECURITY
 *   [x] Never store secrets in JWT payload
 *   [x] Use environment variables for jwt.secret, never hardcode
 *   [x] JWT secret must be at least 256 bits (32 bytes) for HS256
 *   [x] Consider RS256 for microservices (private key signs, public verifies)
 *   [x] Implement token blacklist in Redis for logout revocation
 *   [x] Refresh token rotation — invalidate old on each refresh
 *
 * TRANSPORT SECURITY
 *   [x] HTTPS everywhere — TLS 1.2 minimum, TLS 1.3 preferred
 *   [x] HSTS header (max-age=31536000; includeSubDomains)
 *   [x] Disable HTTP; redirect to HTTPS
 *   [x] Valid certificates from trusted CA (Let's Encrypt for free certs)
 *
 * HEADERS
 *   [x] X-Frame-Options: DENY
 *   [x] X-Content-Type-Options: nosniff
 *   [x] Strict-Transport-Security
 *   [x] Content-Security-Policy (even for APIs)
 *   [x] CORS: restrict to known origins (not *)
 *   [x] Remove Server header — don't advertise your framework version
 *
 * INPUT / OUTPUT
 *   [x] Validate all input (Bean Validation / @Valid)
 *   [x] Parameterized queries — never string concatenation in SQL
 *   [x] Encode output — use libraries, not manual string manipulation
 *   [x] Reject unknown JSON fields (@JsonIgnoreProperties on DTOs)
 *
 * RATE LIMITING
 *   [x] Rate limit per user for authenticated endpoints
 *   [x] Rate limit per IP for public endpoints (/api/auth/login)
 *   [x] Return 429 with Retry-After header
 *   [x] Especially critical on login endpoint (brute force prevention)
 *
 * SECRETS MANAGEMENT
 *   [x] NEVER commit secrets to Git
 *   [x] Use environment variables or secrets manager (AWS Secrets Manager, Vault)
 *   [x] Rotate secrets on a schedule
 *   [x] Audit access to production secrets
 *
 * MONITORING
 *   [x] Log all authentication attempts (success + failure)
 *   [x] Log all 401 and 403 responses
 *   [x] Alert on unusual patterns (many 401s from one IP = brute force)
 *   [x] DO NOT log passwords, tokens, or sensitive data
 *   [x] Include correlation IDs in logs for request tracing
 */

/**
 * Placeholder controller demonstrating 429 handling in a controller context.
 * The actual rate limiting is done in the filter (RateLimitingFilter above),
 * but if business logic limits are needed, they can also be applied here.
 */
@RestController
@RequestMapping("/api/secure")
class SecureApiController {

    /**
     * This endpoint is protected by:
     *   1. JwtAuthenticationFilter (JWT validation)
     *   2. URL authorization (anyRequest().authenticated())
     *   3. RateLimitingFilter (100 req/min per user)
     *   4. @PreAuthorize at the service layer
     *
     * Multiple layers = defense in depth.
     */
    @GetMapping("/data")
    public ResponseEntity<?> getSecureData() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        return ResponseEntity.ok(Map.of(
                "message",   "Secure data",
                "user",      auth.getName(),
                "timestamp", Instant.now().toString()
        ));
    }
}

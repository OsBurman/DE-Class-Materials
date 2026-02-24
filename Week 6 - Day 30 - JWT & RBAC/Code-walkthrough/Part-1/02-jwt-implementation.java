package com.bookstore.security.jwt;

// =============================================================================
// JWT IMPLEMENTATION — Day 30: JWT & RBAC
// =============================================================================
// This file demonstrates:
//   1. JwtService — generating and validating JWT tokens using jjwt 0.12.x
//   2. JwtAuthenticationFilter — OncePerRequestFilter that intercepts every request
//   3. JwtSecurityConfig — SecurityFilterChain wired for stateless JWT
//   4. JwtAuthController — /api/auth/login endpoint that returns a token
//   5. LoginRequest / AuthResponse — request/response DTOs
//
// Maven dependencies needed (pom.xml):
//   io.jsonwebtoken:jjwt-api:0.12.3
//   io.jsonwebtoken:jjwt-impl:0.12.3 (runtime)
//   io.jsonwebtoken:jjwt-jackson:0.12.3 (runtime)
//
// application.properties:
//   jwt.secret=${JWT_SECRET:dev-only-secret-at-least-32-chars-long-replace-in-prod}
//   jwt.expiration-ms=3600000
// =============================================================================

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

// =============================================================================
// SECTION 1: JWT SERVICE — Token Generation and Validation
// =============================================================================

/**
 * JwtService is responsible for:
 *   - Generating a signed JWT for a given user
 *   - Extracting claims from an existing token
 *   - Validating a token (signature + expiry)
 *
 * It reads the secret key and expiration from application.properties.
 */
@Service
public class JwtService {

    // -------------------------------------------------------------------------
    // 1a. Configuration — inject from application.properties
    // -------------------------------------------------------------------------
    @Value("${jwt.secret}")
    private String secretKeyBase64;        // Base64-encoded 256-bit secret

    @Value("${jwt.expiration-ms:3600000}") // Default: 1 hour
    private long expirationMs;

    // -------------------------------------------------------------------------
    // 1b. Build the signing key
    // -------------------------------------------------------------------------
    /**
     * Converts the Base64-encoded secret string into a cryptographic SecretKey.
     * Keys.hmacShaKeyFor() enforces that the key is at least 256 bits for HS256.
     */
    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKeyBase64);
        return Keys.hmacShaKeyFor(keyBytes);
        // Keys.hmacShaKeyFor() throws WeakKeyException if key < 256 bits
        // This is a safety guard — weak keys make HMAC trivially breakable
    }

    // -------------------------------------------------------------------------
    // 1c. Generate a JWT token
    // -------------------------------------------------------------------------
    /**
     * Builds a signed JWT for the given UserDetails.
     *
     * Token structure produced:
     *   Header:  { "alg": "HS256", "typ": "JWT" }
     *   Payload: { "sub": "alice@bookstore.com",
     *              "roles": "ROLE_USER,ROLE_ADMIN",
     *              "iat": <now>,
     *              "exp": <now + expirationMs> }
     *   Signature: HMACSHA256(header + "." + payload, secretKey)
     */
    public String generateToken(UserDetails userDetails) {
        // Collect the user's roles into a comma-separated string
        String roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        // Custom claims we want embedded in the payload
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("roles", roles);

        long nowMs = System.currentTimeMillis();

        return Jwts.builder()
                // --- Standard registered claims ---
                .subject(userDetails.getUsername())           // "sub" — who this token is about
                .issuedAt(new Date(nowMs))                    // "iat" — when issued
                .expiration(new Date(nowMs + expirationMs))   // "exp" — when it expires
                .issuer("bookstore-api")                      // "iss" — who issued it
                // --- Custom claims ---
                .claims(extraClaims)                          // adds "roles" to the payload
                // --- Sign with our secret key ---
                .signWith(getSigningKey())                    // HS256 by default with SecretKey
                .compact();                                   // serialise to the 3-part string
    }

    // -------------------------------------------------------------------------
    // 1d. Generate token with additional custom claims (overload)
    // -------------------------------------------------------------------------
    /**
     * Overload that lets callers inject extra claims (e.g., userId, bookstoreId).
     * Useful when the token consumer needs application-specific data.
     */
    public String generateTokenWithExtraClaims(UserDetails userDetails,
                                               Map<String, Object> extraClaims) {
        String roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        extraClaims.put("roles", roles);
        long nowMs = System.currentTimeMillis();

        return Jwts.builder()
                .subject(userDetails.getUsername())
                .issuedAt(new Date(nowMs))
                .expiration(new Date(nowMs + expirationMs))
                .issuer("bookstore-api")
                .claims(extraClaims)
                .signWith(getSigningKey())
                .compact();
    }

    // -------------------------------------------------------------------------
    // 1e. Parse and extract all claims
    // -------------------------------------------------------------------------
    /**
     * Parses the token and returns all claims.
     * This is the core validation step — jjwt will throw if:
     *   - Signature doesn't match (someone tampered with the payload)
     *   - Token has expired (exp < now)
     *   - Token is malformed (not a valid JWT structure)
     */
    public Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())   // tell jjwt which key to verify against
                .build()
                .parseSignedClaims(token)      // throws JwtException subtypes on failure
                .getPayload();                 // returns the Claims object
    }

    // -------------------------------------------------------------------------
    // 1f. Convenience extractors
    // -------------------------------------------------------------------------
    /** Extract the "sub" claim — the username/email. */
    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    /** Extract the "exp" claim — the expiry date. */
    public Date extractExpiration(String token) {
        return extractAllClaims(token).getExpiration();
    }

    /** Extract the custom "roles" claim. */
    public String extractRoles(String token) {
        return (String) extractAllClaims(token).get("roles");
    }

    // -------------------------------------------------------------------------
    // 1g. Validation — the three things that can go wrong
    // -------------------------------------------------------------------------
    /**
     * Returns true if the token is valid for this UserDetails.
     * Checks:
     *   1. Username in token matches the UserDetails username
     *   2. Token has not expired
     *
     * Note: signature and structure are already verified in extractAllClaims().
     * If extractAllClaims() doesn't throw, the signature is valid.
     */
    public boolean isTokenValid(String token, UserDetails userDetails) {
        try {
            String tokenUsername = extractUsername(token);
            boolean usernameMatches = tokenUsername.equals(userDetails.getUsername());
            boolean notExpired = !isTokenExpired(token);
            return usernameMatches && notExpired;
        } catch (ExpiredJwtException e) {
            // Token expired — caught separately so we can return 401 with clear message
            throw e; // re-throw — the filter will handle it
        } catch (SignatureException e) {
            // Someone tampered with the token (or wrong secret key)
            throw e;
        } catch (MalformedJwtException e) {
            // Not a valid JWT structure
            throw e;
        } catch (JwtException e) {
            // Catch-all for any other JWT problem
            throw e;
        }
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }
}


// =============================================================================
// SECTION 2: REQUEST / RESPONSE DTOs
// =============================================================================

/**
 * LoginRequest — the JSON body the client sends to /api/auth/login.
 * Example: { "email": "alice@bookstore.com", "password": "secret123" }
 */
class LoginRequest {
    private String email;
    private String password;

    public String getEmail()    { return email; }
    public String getPassword() { return password; }
    public void setEmail(String e)    { this.email = e; }
    public void setPassword(String p) { this.password = p; }
}

/**
 * AuthResponse — what the server returns on successful login.
 * Example: { "token": "eyJhbGci...", "type": "Bearer", "expiresIn": 3600 }
 */
class AuthResponse {
    private String token;
    private String type = "Bearer";
    private long expiresIn; // seconds

    public AuthResponse(String token, long expiresIn) {
        this.token = token;
        this.expiresIn = expiresIn;
    }

    public String getToken()    { return token; }
    public String getType()     { return type; }
    public long   getExpiresIn(){ return expiresIn; }
}


// =============================================================================
// SECTION 3: JWT AUTHENTICATION FILTER — OncePerRequestFilter
// =============================================================================

/**
 * JwtAuthenticationFilter intercepts EVERY incoming HTTP request exactly once.
 * It extracts the JWT from the Authorization header, validates it, and if valid,
 * populates the SecurityContextHolder so Spring Security knows the user is authenticated.
 *
 * Extends OncePerRequestFilter — guaranteed one execution per request,
 * even in forward/include chains.
 *
 * Filter chain position:
 *   JwtAuthenticationFilter
 *     → UsernamePasswordAuthenticationFilter
 *     → ... (other filters)
 *     → DispatcherServlet → Controller
 */
@Component
class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserDetailsService userDetailsService;

    // -------------------------------------------------------------------------
    // 3a. Core filter logic — runs on every request
    // -------------------------------------------------------------------------
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        // Step 1: Extract the Authorization header
        final String authHeader = request.getHeader("Authorization");

        // Step 2: If no Bearer token, skip this filter — let the next filter run
        // (The request will fail later if the endpoint requires authentication)
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Step 3: Pull out the token (everything after "Bearer ")
        final String jwt = extractBearerToken(authHeader);

        // Step 4: Extract the username from the token's "sub" claim
        String username;
        try {
            username = jwtService.extractUsername(jwt);
        } catch (ExpiredJwtException e) {
            // Token is expired — send 401 immediately, don't continue
            sendUnauthorizedError(response, "Token expired — please log in again");
            return;
        } catch (SignatureException e) {
            sendUnauthorizedError(response, "Invalid token signature");
            return;
        } catch (MalformedJwtException e) {
            sendUnauthorizedError(response, "Malformed token");
            return;
        } catch (JwtException e) {
            sendUnauthorizedError(response, "Token validation failed");
            return;
        }

        // Step 5: Only proceed if we have a username AND the context isn't already set
        // (We don't want to override an already-authenticated request in the same chain)
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            // Step 6: Load the full UserDetails from the database
            // This verifies the user still exists and is still enabled/non-locked
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            // Step 7: Validate the token against the UserDetails
            if (jwtService.isTokenValid(jwt, userDetails)) {

                // Step 8: Create an Authentication object (no credentials needed — token IS the credential)
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,          // principal
                                null,                 // credentials (null — we don't store password)
                                userDetails.getAuthorities() // granted authorities (roles)
                        );

                // Optional: attach request details for audit logging
                authToken.setDetails(
                        new org.springframework.security.web.authentication.WebAuthenticationDetailsSource()
                                .buildDetails(request)
                );

                // Step 9: Store authentication in the SecurityContextHolder
                // This is what tells Spring Security "this request is authenticated"
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        // Step 10: Continue the filter chain — pass request to next filter / controller
        filterChain.doFilter(request, response);
    }

    // -------------------------------------------------------------------------
    // 3b. Helper — strip "Bearer " prefix
    // -------------------------------------------------------------------------
    private String extractBearerToken(String authHeader) {
        // "Bearer eyJhbGci..." → "eyJhbGci..."
        return authHeader.substring(7); // "Bearer " is 7 characters
    }

    // -------------------------------------------------------------------------
    // 3c. Helper — write a JSON 401 response
    // -------------------------------------------------------------------------
    private void sendUnauthorizedError(HttpServletResponse response, String message)
            throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401
        response.setContentType("application/json");
        response.getWriter().write(
            "{\"error\": \"Unauthorized\", \"message\": \"" + message + "\"}"
        );
    }
}


// =============================================================================
// SECTION 4: JWT SECURITY CONFIGURATION
// =============================================================================

/**
 * Configures Spring Security for a stateless JWT-based REST API.
 *
 * Key decisions made here:
 *   - Sessions are STATELESS — no HttpSession created or used
 *   - CSRF is DISABLED — safe because we don't use cookies for auth
 *   - Form login is DISABLED — we use /api/auth/login with JSON instead
 *   - JwtAuthenticationFilter runs BEFORE UsernamePasswordAuthenticationFilter
 */
@Configuration
@EnableWebSecurity
class JwtSecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthFilter;

    @Autowired
    private UserDetailsService userDetailsService;

    // -------------------------------------------------------------------------
    // 4a. Main security filter chain
    // -------------------------------------------------------------------------
    @Bean
    public SecurityFilterChain jwtFilterChain(HttpSecurity http) throws Exception {
        http
            // Disable CSRF — JWT tokens are not cookies, so CSRF is not a threat
            // The browser cannot automatically attach a JWT in the Authorization header
            .csrf(csrf -> csrf.disable())

            // URL-based authorization rules (also covered in depth in Part 2)
            .authorizeHttpRequests(auth -> auth
                // Public endpoints — no token needed
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/api/books/public/**").permitAll()
                .requestMatchers("/actuator/health").permitAll()

                // Role-based URL security
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                .requestMatchers("/api/books/manage/**").hasAnyRole("ADMIN", "LIBRARIAN")

                // Everything else requires authentication
                .anyRequest().authenticated()
            )

            // No sessions — we are fully stateless
            // Each request must carry its own JWT
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )

            // No form-based login page
            .formLogin(form -> form.disable())

            // No HTTP Basic authentication
            .httpBasic(basic -> basic.disable())

            // Register our JWT filter BEFORE the built-in username/password filter
            // This ensures JWTs are validated before Spring Security tries username/password auth
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // -------------------------------------------------------------------------
    // 4b. AuthenticationManager bean — needed by the login controller
    // -------------------------------------------------------------------------
    /**
     * Exposes the AuthenticationManager so the login controller can call
     * authManager.authenticate(new UsernamePasswordAuthenticationToken(email, password))
     * which triggers UserDetailsService.loadUserByUsername() + BCrypt comparison.
     */
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}


// =============================================================================
// SECTION 5: JWT AUTH CONTROLLER — /api/auth/login endpoint
// =============================================================================

/**
 * Handles the login request and issues a JWT on success.
 *
 * Flow:
 *   1. Client POSTs { email, password } to /api/auth/login
 *   2. authenticationManager.authenticate() calls UserDetailsService + BCrypt
 *   3. On success → generate JWT with JwtService
 *   4. Return { token, type, expiresIn }
 *   5. On failure → BadCredentialsException → 401
 */
@RestController
@RequestMapping("/api/auth")
class JwtAuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserDetailsService userDetailsService;

    @Value("${jwt.expiration-ms:3600000}")
    private long expirationMs;

    // -------------------------------------------------------------------------
    // 5a. POST /api/auth/login — authenticate and return JWT
    // -------------------------------------------------------------------------
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            // Step 1: Authenticate the credentials
            // This calls BookstoreUserDetailsService.loadUserByUsername()
            // and BCryptPasswordEncoder.matches() under the hood
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),    // username (we use email as username)
                            request.getPassword()  // raw password — BCrypt will hash + compare
                    )
            );

            // Step 2: Extract the authenticated UserDetails from the result
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();

            // Step 3: Generate the JWT token
            String token = jwtService.generateToken(userDetails);

            // Step 4: Return token to client
            return ResponseEntity.ok(new AuthResponse(token, expirationMs / 1000));

        } catch (AuthenticationException e) {
            // Bad credentials (wrong password or unknown user)
            // Return 401 — always use a generic message to prevent username enumeration
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid credentials"));
        }
    }

    // -------------------------------------------------------------------------
    // 5b. POST /api/auth/refresh — issue a new access token using a refresh token
    // -------------------------------------------------------------------------
    /**
     * In a production system, you would:
     *   1. Receive the refresh token (stored in HTTP-only cookie or header)
     *   2. Validate it against a database record (JTI matching, not expired)
     *   3. Issue a new short-lived access token
     *   4. Optionally rotate the refresh token (rotate-on-use strategy)
     *
     * Here we show the concept with a simple token re-validation.
     */
    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(
            @RequestHeader("X-Refresh-Token") String refreshToken) {
        try {
            // Extract username from the (still valid) refresh token
            String username = jwtService.extractUsername(refreshToken);
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            if (jwtService.isTokenValid(refreshToken, userDetails)) {
                // Issue a new access token
                String newAccessToken = jwtService.generateToken(userDetails);
                return ResponseEntity.ok(new AuthResponse(newAccessToken, expirationMs / 1000));
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Refresh token invalid"));
            }
        } catch (ExpiredJwtException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Refresh token expired — please log in again"));
        } catch (JwtException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid refresh token"));
        }
    }

    // -------------------------------------------------------------------------
    // 5c. POST /api/auth/logout — client-side token invalidation
    // -------------------------------------------------------------------------
    /**
     * True stateless JWT logout is client-side only — delete the token locally.
     * For server-side invalidation, maintain a token blacklist (Redis is ideal)
     * keyed by JTI with TTL matching the token's remaining lifetime.
     *
     * This endpoint demonstrates the concept with a comment-only stub.
     */
    @PostMapping("/logout")
    public ResponseEntity<?> logout(
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        // In a production system:
        //   1. Extract JTI from the token
        //   2. Add JTI to Redis blacklist with TTL = token remaining time
        //   3. JwtAuthenticationFilter checks blacklist before accepting token

        // For now, instruct client to delete the token locally
        return ResponseEntity.ok(Map.of(
                "message", "Logged out successfully. Delete your token on the client side."
        ));
    }
}


// =============================================================================
// SECTION 6: SAMPLE SECURED CONTROLLER — Demonstrates JWT in action
// =============================================================================

/**
 * Shows how JWT authentication flows through to a real controller.
 * Once JwtAuthenticationFilter validates the token and sets SecurityContextHolder,
 * controllers can inject the authenticated principal normally.
 */
@RestController
@RequestMapping("/api/books")
class BookController {

    // -------------------------------------------------------------------------
    // 6a. Public endpoint — no token required
    // -------------------------------------------------------------------------
    @GetMapping("/public/catalog")
    public ResponseEntity<?> getPublicCatalog() {
        // No JWT needed — requestMatchers("/api/books/public/**").permitAll()
        return ResponseEntity.ok(Map.of(
                "books", java.util.List.of(
                        Map.of("title", "Clean Code", "author", "Robert C. Martin"),
                        Map.of("title", "The Pragmatic Programmer", "author", "Hunt & Thomas")
                )
        ));
    }

    // -------------------------------------------------------------------------
    // 6b. Authenticated endpoint — valid JWT required
    // -------------------------------------------------------------------------
    @GetMapping("/my-library")
    public ResponseEntity<?> getMyLibrary() {
        // JwtAuthenticationFilter already set the SecurityContextHolder
        // We can extract the authenticated user from it
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName(); // extracts the "sub" claim via UserDetails.getUsername()

        return ResponseEntity.ok(Map.of(
                "user", username,
                "books", java.util.List.of("Clean Code", "Effective Java")
        ));
    }

    // -------------------------------------------------------------------------
    // 6c. Admin-only endpoint — ROLE_ADMIN required
    // -------------------------------------------------------------------------
    @GetMapping("/admin/all")
    public ResponseEntity<?> getAllBooksAdmin() {
        // requestMatchers("/api/admin/**").hasRole("ADMIN") handles this
        // But could also use @PreAuthorize — shown in Part 2
        return ResponseEntity.ok(Map.of("message", "All books (admin view)"));
    }

    // -------------------------------------------------------------------------
    // 6d. Inject principal with @AuthenticationPrincipal shortcut
    // -------------------------------------------------------------------------
    /**
     * @AuthenticationPrincipal injects the UserDetails directly from SecurityContextHolder.
     * Cleaner than calling SecurityContextHolder.getContext().getAuthentication() manually.
     */
    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(
            @org.springframework.security.core.annotation.AuthenticationPrincipal
            UserDetails currentUser) {
        // currentUser is the BookstoreUserDetails object set by JwtAuthenticationFilter
        return ResponseEntity.ok(Map.of(
                "username",    currentUser.getUsername(),
                "authorities", currentUser.getAuthorities()
        ));
    }
}

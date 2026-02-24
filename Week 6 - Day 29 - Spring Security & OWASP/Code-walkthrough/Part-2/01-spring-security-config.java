package com.bookstore.security;

// =============================================================================
// SPRING SECURITY — Overview, Architecture, and SecurityFilterChain
// =============================================================================
//
// Spring Security is a framework that provides authentication and authorization
// for Spring-based applications. It integrates tightly with Spring Boot and
// requires very little boilerplate to get a secured application running.
//
// ARCHITECTURE OVERVIEW:
//
//   HTTP Request
//       ↓
//   DelegatingFilterProxy  ← registered by Spring Boot in the Servlet container
//       ↓
//   FilterChainProxy        ← Spring Security's own filter chain manager
//       ↓
//   SecurityFilterChain     ← YOUR configuration: ordered list of security filters
//       ↓
//   [ UsernamePasswordAuthenticationFilter ]  ← handles /login POST
//   [ BasicAuthenticationFilter ]             ← handles Basic Auth header
//   [ BearerTokenAuthenticationFilter ]       ← handles Bearer/JWT tokens
//   [ CsrfFilter ]                            ← validates CSRF tokens
//   [ ExceptionTranslationFilter ]            ← converts security exceptions to HTTP responses
//   [ AuthorizationFilter ]                   ← checks authorization for the request
//       ↓
//   DispatcherServlet (your controllers)
//
// Key components:
//   SecurityFilterChain   — defines security rules (which URLs require auth, etc.)
//   AuthenticationManager — orchestrates authentication (delegates to providers)
//   AuthenticationProvider— verifies credentials (checks username/password)
//   UserDetailsService    — loads user data from a store (memory, database)
//   PasswordEncoder       — hashes and verifies passwords
//   SecurityContextHolder — stores the currently authenticated user for the request
//
// Maven dependency (Spring Boot Starter Web already pulls in web/MVC):
//   <dependency>
//       <groupId>org.springframework.boot</groupId>
//       <artifactId>spring-boot-starter-security</artifactId>
//   </dependency>
//
// When you add this dependency with NO other configuration, Spring Boot auto-configures:
//   - All endpoints require authentication
//   - A single in-memory user "user" with a random password (printed to console on startup)
//   - HTTP Basic authentication enabled
//   - CSRF protection enabled
//   - Session management enabled
// =============================================================================

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;


// =============================================================================
// SECTION 1: Authentication vs Authorization
// =============================================================================
// These terms are often confused. They answer different questions:
//
//   AUTHENTICATION: "Who are you?"
//     Verifying identity. Proving you are who you claim to be.
//     Mechanism: username+password, OAuth2 token, certificate, biometrics.
//     Result: the system knows your identity (or rejects you).
//     HTTP response on failure: 401 Unauthorized
//
//   AUTHORIZATION: "What are you allowed to do?"
//     Verifying permissions. Checking that your identity has the right to take an action.
//     Happens AFTER authentication.
//     Mechanism: roles (ADMIN, USER), permissions (READ_BOOKS, DELETE_BOOKS), ACLs.
//     Result: access granted or denied.
//     HTTP response on failure: 403 Forbidden
//
//   Example:
//     Authentication: "Alice logs in with username=alice, password=correct" → AUTHENTICATED
//     Authorization:  "Alice tries DELETE /admin/users → she has role USER, not ADMIN" → 403 FORBIDDEN
//
//   Memory trick: AuthentiCATION = verifying you're the CAT you claim to be.
//                 AuthoriZATION  = verifying you're in the right ZONE.
// =============================================================================


// =============================================================================
// SECTION 2: @EnableWebSecurity and SecurityFilterChain — Basic Configuration
// =============================================================================

@Configuration
@EnableWebSecurity   // activates Spring Security's web security support
                     // In Spring Boot, this is implied when you add spring-boot-starter-security
                     // BUT: explicit @EnableWebSecurity gives you full control and overrides auto-config
public class BasicSecurityConfig {

    // SecurityFilterChain is the core configuration bean.
    // It defines:
    //   1. Which URL patterns require authentication (and what level)
    //   2. What authentication mechanisms are supported (form login, basic, JWT...)
    //   3. Session management behavior
    //   4. CSRF protection settings
    //   5. Security headers
    //
    // Spring Security 6.x uses the lambda DSL — each feature is configured
    // via a lambda that receives a customizer object. This replaces the old
    // method-chaining style from Spring Security 5.x.

    @Bean
    public SecurityFilterChain basicFilterChain(HttpSecurity http) throws Exception {
        http
            // =================================================================
            // AUTHORIZATION RULES — requestMatchers
            // =================================================================
            // Rules are evaluated in ORDER. The first matching rule wins.
            // ALWAYS put more specific rules before more general ones.
            .authorizeHttpRequests(authz -> authz

                // PUBLIC: no authentication needed (landing page, static assets, login itself)
                .requestMatchers("/", "/home", "/login", "/register").permitAll()
                .requestMatchers("/css/**", "/js/**", "/images/**").permitAll()

                // PUBLIC API: anyone can browse the catalog
                .requestMatchers("/api/v1/books").permitAll()
                .requestMatchers("/api/v1/books/{id}").permitAll()

                // ADMIN ONLY: only users with ADMIN role can access admin endpoints
                .requestMatchers("/admin/**").hasRole("ADMIN")
                // hasRole("ADMIN") is equivalent to hasAuthority("ROLE_ADMIN")
                // Spring Security automatically prepends "ROLE_" for hasRole()

                // AUTHENTICATED USERS: any logged-in user can access these
                .requestMatchers("/api/v1/orders/**").authenticated()
                .requestMatchers("/profile/**").authenticated()

                // FALLBACK: everything else requires authentication
                // This is the "deny by default" rule — never remove it
                .anyRequest().authenticated()
            )

            // =================================================================
            // FORM LOGIN configuration
            // =================================================================
            .formLogin(form -> form
                .loginPage("/login")               // custom login page (GET)
                .loginProcessingUrl("/login")      // URL that Spring Security handles POST to
                .defaultSuccessUrl("/dashboard", true)  // redirect after successful login
                .failureUrl("/login?error=true")   // redirect after failed login
                .permitAll()                        // the login page itself is public
            )

            // =================================================================
            // LOGOUT configuration
            // =================================================================
            .logout(logout -> logout
                .logoutUrl("/logout")               // URL that triggers logout
                .logoutSuccessUrl("/login?logout")  // redirect after logout
                .invalidateHttpSession(true)        // destroy the session
                .deleteCookies("JSESSIONID")        // remove session cookie from browser
                .permitAll()
            )

            // =================================================================
            // SESSION MANAGEMENT
            // =================================================================
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                // SessionCreationPolicy options:
                //   IF_REQUIRED (default) — create session only when needed
                //   ALWAYS                — always create a session
                //   NEVER                 — never create, but use existing if present
                //   STATELESS             — never create or use sessions (for JWT APIs)
                .maximumSessions(1)    // prevent concurrent logins with same account
                .maxSessionsPreventsLogin(false)  // false = new login invalidates old session
                //                                  true  = reject new login if one exists
            );

        return http.build();
    }


    // =============================================================================
    // SECTION 3: In-Memory Authentication
    // =============================================================================
    // Good for: development/testing, internal tools, demos
    // NOT for: production applications with real users
    //
    // Users are defined in code, stored in JVM memory.
    // If the app restarts, changes are lost.
    // If you need to add a user, you must redeploy.
    // =============================================================================

    @Bean
    public UserDetailsService inMemoryUsers() {
        // User.withDefaultPasswordEncoder() is a convenience builder that
        // hashes the plain-text password. The "{bcrypt}" prefix in the stored
        // hash tells Spring Security which PasswordEncoder to use when verifying.
        //
        // ⚠️ withDefaultPasswordEncoder() is deprecated for production use —
        //    it's fine for demos and tests. For production, use a real encoder.

        UserDetails regularUser = User.withDefaultPasswordEncoder()
            .username("reader")
            .password("password123")
            .roles("USER")          // stored internally as "ROLE_USER"
            .build();

        UserDetails adminUser = User.withDefaultPasswordEncoder()
            .username("admin")
            .password("adminPass!")
            .roles("ADMIN", "USER") // admin also has USER role
            .build();

        UserDetails librarianUser = User.withDefaultPasswordEncoder()
            .username("librarian")
            .password("lib2024!")
            .roles("USER", "LIBRARIAN")
            .build();

        System.out.println("In-memory users configured: reader, admin, librarian");

        // InMemoryUserDetailsManager holds the users in a Map in memory
        return new InMemoryUserDetailsManager(regularUser, adminUser, librarianUser);
    }


    // =============================================================================
    // SECTION 4: Password Encoding with BCryptPasswordEncoder
    // =============================================================================
    // PasswordEncoder is used in TWO places:
    //   1. When creating a user — to hash the password before storing
    //   2. When authenticating — to verify the submitted password against the hash
    //
    // BCryptPasswordEncoder(strength):
    //   strength (cost factor) = 10 (default), 12 (recommended for production)
    //   Higher strength = more bcrypt rounds = longer hash time
    //   At strength=12: ~250ms per hash — slow enough to deter brute force
    //   At strength=10: ~65ms — slightly faster, still reasonable
    //
    // IMPORTANT: Only ONE PasswordEncoder bean should be registered.
    // Spring Security injects it wherever needed automatically.
    // =============================================================================

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12); // cost factor 12 for production

        // Example of what happens internally:
        // encoder.encode("mypassword")
        //   → generates random 22-char salt
        //   → runs bcrypt with 2^12 iterations
        //   → returns "$2a$12$<salt><hash>" (60-char string)
        //
        // encoder.matches("mypassword", storedHash)
        //   → extracts salt from storedHash
        //   → re-hashes the input with that same salt
        //   → compares in constant time
        //   → returns true if they match
        //
        // You NEVER decrypt a bcrypt hash — you can only re-hash and compare.
    }


    // =============================================================================
    // SECTION 5: CSRF Protection Configuration
    // =============================================================================

    @Bean
    public SecurityFilterChain csrfDemoFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authz -> authz
                .anyRequest().authenticated()
            )

            // -----------------------------------------------------------------
            // OPTION A: CSRF ENABLED (default) — for session-based web apps
            // -----------------------------------------------------------------
            // Spring Security enables CSRF by default.
            // The CsrfFilter validates the token on every non-safe request (POST/PUT/DELETE/PATCH).
            // Thymeleaf automatically injects _csrf hidden fields when you use th:action.
            //
            // Using CookieCsrfTokenRepository: stores the token in a cookie instead of session.
            // Useful for Single Page Applications (SPAs) that read the cookie and send the header.
            .csrf(csrf -> csrf
                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                // withHttpOnlyFalse() — JavaScript can read the cookie to send it as a header
                // Required for Angular, React apps that need to read XSRF-TOKEN cookie
            )

            .formLogin(form -> form
                .loginPage("/login")
                .permitAll()
            );

        return http.build();
    }


    // =============================================================================
    // SECTION 6: Stateless REST API Configuration (CSRF disabled + SessionCreationPolicy.STATELESS)
    // =============================================================================
    // For REST APIs that use JWT Bearer tokens:
    //   - Sessions are not used (no JSESSIONID cookie)
    //   - CSRF is not needed (Bearer token, not cookie, carries credentials)
    //   - HTTP Basic disabled for API endpoints
    //   - Every request must include a valid JWT (handled by a filter — see Day 30)
    // =============================================================================

    @Bean
    public SecurityFilterChain restApiFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authz -> authz
                .requestMatchers("/api/v1/auth/**").permitAll()  // login/register endpoints
                .requestMatchers("/api/v1/books").permitAll()     // public catalog
                .anyRequest().authenticated()                      // everything else: JWT required
            )

            // CSRF DISABLED — safe for stateless JWT APIs
            // Reason: attackers cannot forge the Authorization: Bearer <token> header
            // (browsers don't auto-attach headers the way they do with cookies)
            .csrf(csrf -> csrf.disable())

            // STATELESS: no sessions — every request is independent
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )

            // Disable form login for pure REST APIs
            .formLogin(form -> form.disable())
            .httpBasic(basic -> basic.disable());

            // JWT filter will be added here in Day 30:
            // .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)

        return http.build();
    }


    // =============================================================================
    // SECTION 7: Security Headers
    // =============================================================================
    // Spring Security adds a set of security headers by default.
    // You can customize them in the SecurityFilterChain.
    //
    // Default headers added automatically by Spring Security:
    //   X-Content-Type-Options: nosniff
    //   X-XSS-Protection: 0  (modern browsers use CSP instead)
    //   Cache-Control: no-cache, no-store, max-age=0
    //   X-Frame-Options: DENY   (prevents clickjacking)
    //   Strict-Transport-Security (HSTS): max-age=31536000 (HTTPS only!)
    // =============================================================================

    @Bean
    public SecurityFilterChain secureHeadersChain(HttpSecurity http) throws Exception {
        http
            .headers(headers -> headers
                // X-Frame-Options: DENY — prevents the page from being embedded in an iframe
                // Protects against clickjacking attacks
                .frameOptions(frame -> frame.deny())

                // HSTS: forces browser to use HTTPS for the next year
                // Only applies over HTTPS — Spring Security skips it if connection is HTTP
                .httpStrictTransportSecurity(hsts -> hsts
                    .includeSubDomains(true)
                    .maxAgeInSeconds(31536000)
                )

                // Content-Security-Policy: defines trusted content sources
                // default-src 'self' = only load resources from the same origin
                // This blocks inline scripts — powerful defense against XSS
                .contentSecurityPolicy(csp -> csp
                    .policyDirectives("default-src 'self'; script-src 'self'; style-src 'self' 'unsafe-inline'")
                )
            )
            .authorizeHttpRequests(authz -> authz.anyRequest().authenticated());

        return http.build();
    }


    // =============================================================================
    // SECTION 8: BCryptPasswordEncoder demo — standalone usage
    // =============================================================================
    public static void demonstrateBcrypt() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);

        String rawPassword = "mySecretPassword123!";

        // Encode (hash) the password — call this when storing a new user's password
        String hashed = encoder.encode(rawPassword);
        System.out.println("Raw:    " + rawPassword);
        System.out.println("Hashed: " + hashed);

        // Every call produces a different hash (different random salt)
        String hashed2 = encoder.encode(rawPassword);
        System.out.println("Hashed again (different!): " + hashed2);

        // Verify — call this during login to check submitted password against stored hash
        boolean match1 = encoder.matches(rawPassword, hashed);
        boolean match2 = encoder.matches("wrongPassword", hashed);
        System.out.println("\nmatches(correct): " + match1);  // true
        System.out.println("matches(wrong):   " + match2);  // false

        // The "{bcrypt}" prefix — Spring's DelegatingPasswordEncoder can handle multiple
        // algorithm types. The prefix in the stored hash identifies the algorithm.
        // When you call InMemoryUserDetailsManager with withDefaultPasswordEncoder(),
        // the stored password looks like: {bcrypt}$2a$10$...
        System.out.println("\nDelegatingPasswordEncoder prefix: {bcrypt}" + hashed);
    }

    public static void main(String[] args) {
        demonstrateBcrypt();
    }
}

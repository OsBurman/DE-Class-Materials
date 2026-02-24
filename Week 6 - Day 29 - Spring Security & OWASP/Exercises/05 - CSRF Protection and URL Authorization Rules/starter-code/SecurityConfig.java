package com.security.csrfauth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Exercise 05 – Two security filter chains in one application.
 *
 * Spring Security supports multiple SecurityFilterChain beans.
 * Each chain can be restricted to a subset of URL paths via
 * {@code http.securityMatcher(...)}.  The chain with the lowest
 * {@code @Order} value is evaluated first.
 *
 * ─────────────────────────────────────────────────────────────────
 * CHAIN 1 – REST API  (Order 1)
 * ─────────────────────────────────────────────────────────────────
 * Task 1: Complete {@code restSecurityFilterChain()}.
 *
 * Requirements:
 *   • Scope to paths: /books/**, /public/**
 *   • CSRF disabled (stateless REST – no session cookies)
 *   • HTTP Basic authentication enabled
 *   • URL rules (in order):
 *       GET  /public/**              → permitAll()
 *       POST /books                  → hasRole("ADMIN")
 *       PUT  /books/**               → hasRole("ADMIN")
 *       DELETE /books/**             → hasRole("ADMIN")
 *       anything else in this chain  → authenticated()
 *
 * Hint – in-memory users for REST chain:
 *   user  / password / role USER
 *   admin / admin123 / role ADMIN
 *
 * ─────────────────────────────────────────────────────────────────
 * CHAIN 2 – Form-based Web App  (Order 2)
 * ─────────────────────────────────────────────────────────────────
 * Task 2: Complete {@code formSecurityFilterChain()}.
 *
 * Requirements:
 *   • Scope to paths: /form/**
 *   • CSRF ENABLED (default – use csrf(Customizer.withDefaults()))
 *   • Form login enabled (use formLogin(Customizer.withDefaults()))
 *   • URL rules:
 *       POST /form/submit  → hasRole("EDITOR")
 *       GET  /form/**      → authenticated()
 *
 * In-memory users for form chain:
 *   editor / editor123 / role EDITOR
 *   viewer / viewer123 / role VIEWER
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // -------------------------------------------------------------------------
    // Shared infrastructure beans
    // -------------------------------------------------------------------------

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // -------------------------------------------------------------------------
    // Chain 1 – REST API (CSRF disabled, HTTP Basic)
    // -------------------------------------------------------------------------

    /**
     * TODO: Implement the REST security filter chain.
     *
     * Steps:
     *   1. Call http.securityMatcher("/books/**", "/public/**") to scope this chain.
     *   2. Disable CSRF with csrf(AbstractHttpConfigurer::disable).
     *   3. Disable form login with formLogin(AbstractHttpConfigurer::disable).
     *   4. Enable HTTP Basic with httpBasic(basic -> {}).
     *   5. Add requestMatchers() rules in the order listed above.
     *   6. Return http.build().
     */
    @Bean
    @Order(1)
    public SecurityFilterChain restSecurityFilterChain(HttpSecurity http) throws Exception {
        // TODO: implement Task 1
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /**
     * TODO: Provide in-memory users for the REST chain.
     *       Bean name must differ from the form chain's UserDetailsService.
     *
     * Hint:
     *   return new InMemoryUserDetailsManager(
     *       User.withUsername("user")
     *           .password(passwordEncoder().encode("password"))
     *           .roles("USER")
     *           .build(),
     *       User.withUsername("admin")
     *           .password(passwordEncoder().encode("admin123"))
     *           .roles("ADMIN")
     *           .build()
     *   );
     */
    @Bean
    public UserDetailsService restUserDetailsService() {
        // TODO: return InMemoryUserDetailsManager with user + admin
        throw new UnsupportedOperationException("Not yet implemented");
    }

    // -------------------------------------------------------------------------
    // Chain 2 – Form-based Web App (CSRF enabled, Form Login)
    // -------------------------------------------------------------------------

    /**
     * TODO: Implement the form security filter chain.
     *
     * Steps:
     *   1. Call http.securityMatcher("/form/**") to scope this chain.
     *   2. Enable CSRF with csrf(Customizer.withDefaults()) – this is the default,
     *      but declare it explicitly to make the intent clear.
     *   3. Enable form login with formLogin(Customizer.withDefaults()).
     *   4. Add requestMatchers() rules in the order listed above.
     *   5. Return http.build().
     */
    @Bean
    @Order(2)
    public SecurityFilterChain formSecurityFilterChain(HttpSecurity http) throws Exception {
        // TODO: implement Task 2
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /**
     * TODO: Provide in-memory users for the form chain.
     *
     * Hint:
     *   editor / editor123 / EDITOR
     *   viewer / viewer123 / VIEWER
     */
    @Bean
    public UserDetailsService formUserDetailsService() {
        // TODO: return InMemoryUserDetailsManager with editor + viewer
        throw new UnsupportedOperationException("Not yet implemented");
    }
}

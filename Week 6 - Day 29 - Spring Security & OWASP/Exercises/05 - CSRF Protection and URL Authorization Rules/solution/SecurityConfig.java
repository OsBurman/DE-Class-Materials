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
 * Exercise 05 solution – two SecurityFilterChain beans in a single application.
 *
 * Chain 1 (Order 1): REST API – CSRF disabled, HTTP Basic, /books/** and /public/**
 * Chain 2 (Order 2): Form App – CSRF enabled, form login, /form/**
 *
 * Key contrast:
 *   REST POST  without CSRF token → 201 Created  (CSRF disabled for this chain)
 *   Form POST  without CSRF token → 403 Forbidden (CSRF enabled for this chain)
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // -------------------------------------------------------------------------
    // Shared infrastructure
    // -------------------------------------------------------------------------

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // -------------------------------------------------------------------------
    // Chain 1 – REST API  (CSRF disabled, HTTP Basic)
    // -------------------------------------------------------------------------

    /**
     * Scoped to /books/** and /public/**.
     *
     * CSRF is disabled because:
     *   • This is a stateless REST API.
     *   • Clients authenticate per-request with HTTP Basic credentials.
     *   • There are no session cookies for an attacker to hijack.
     */
    @Bean
    @Order(1)
    public SecurityFilterChain restSecurityFilterChain(HttpSecurity http) throws Exception {
        http
            .securityMatcher("/books/**", "/public/**")
            .csrf(AbstractHttpConfigurer::disable)          // ← CSRF off for REST
            .formLogin(AbstractHttpConfigurer::disable)
            .httpBasic(Customizer.withDefaults())           // ← credentials per-request
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/public/**").permitAll()
                .requestMatchers(HttpMethod.POST,   "/books").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT,    "/books/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/books/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            );

        return http.build();
    }

    @Bean
    public UserDetailsService restUserDetailsService() {
        return new InMemoryUserDetailsManager(
            User.withUsername("user")
                .password(passwordEncoder().encode("password"))
                .roles("USER")
                .build(),
            User.withUsername("admin")
                .password(passwordEncoder().encode("admin123"))
                .roles("ADMIN")
                .build()
        );
    }

    // -------------------------------------------------------------------------
    // Chain 2 – Form-based Web App  (CSRF enabled, Form Login)
    // -------------------------------------------------------------------------

    /**
     * Scoped to /form/**.
     *
     * CSRF is ENABLED (default) because:
     *   • This is a session-based form app.
     *   • The browser stores a session cookie after login.
     *   • Without the CSRF token, a malicious site could forge form submissions.
     *
     * In Thymeleaf, the hidden input is:
     *   <input type="hidden" th:name="${_csrf.parameterName}" th:value="${_csrf.token}"/>
     * In MockMvc tests, csrf() injects the token automatically.
     */
    @Bean
    @Order(2)
    public SecurityFilterChain formSecurityFilterChain(HttpSecurity http) throws Exception {
        http
            .securityMatcher("/form/**")
            .csrf(Customizer.withDefaults())                // ← CSRF ON for form app
            .formLogin(Customizer.withDefaults())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(HttpMethod.POST, "/form/submit").hasRole("EDITOR")
                .requestMatchers("/form/**").authenticated()
            );

        return http.build();
    }

    @Bean
    public UserDetailsService formUserDetailsService() {
        return new InMemoryUserDetailsManager(
            User.withUsername("editor")
                .password(passwordEncoder().encode("editor123"))
                .roles("EDITOR")
                .build(),
            User.withUsername("viewer")
                .password(passwordEncoder().encode("viewer123"))
                .roles("VIEWER")
                .build()
        );
    }
}

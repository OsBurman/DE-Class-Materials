package com.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
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
 * Exercise 03 – Spring Security In-Memory Authentication
 *
 * TODO: Complete the three bean methods below.
 */
// TODO: Add @Configuration
// TODO: Add @EnableWebSecurity
public class SecurityConfig {

    /**
     * TODO: Define the SecurityFilterChain bean.
     *
     * Rules to configure:
     *  1. Disable CSRF (we are building a stateless REST API)
     *     http.csrf(AbstractHttpConfigurer::disable)
     *  2. Disable form login and HTTP basic (we use stateless bearer tokens later)
     *  3. authorizeHttpRequests:
     *     - /public/** → permitAll()
     *     - GET /books → authenticated()
     *     - POST /books → hasRole("ADMIN")
     *  4. Return http.build()
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // TODO: Configure http as described above
        return http.build();
    }

    /**
     * TODO: Define the UserDetailsService bean.
     *
     * Create two users with InMemoryUserDetailsManager:
     *  - username: "user",  password: passwordEncoder().encode("password"), role: USER
     *  - username: "admin", password: passwordEncoder().encode("admin123"), role: ADMIN
     *
     * Use User.withUsername(...).password(...).roles(...).build()
     */
    @Bean
    public UserDetailsService userDetailsService() {
        // TODO: build two UserDetails and return new InMemoryUserDetailsManager(user, admin)
        return null;
    }

    /**
     * TODO: Define the PasswordEncoder bean.
     * Return a new BCryptPasswordEncoder().
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        // TODO: return new BCryptPasswordEncoder();
        return null;
    }
}

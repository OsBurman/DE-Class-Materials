package com.security.dbauth.config;

import com.security.dbauth.service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Exercise 04 – Task 2: Wire database authentication into Spring Security.
 *
 * Unlike Exercise 03 (which used InMemoryUserDetailsManager), this config
 * uses a {@link DaoAuthenticationProvider} backed by {@link CustomUserDetailsService}
 * to load users from the H2 database.
 *
 * Three beans to implement:
 *
 * ── passwordEncoder() ──────────────────────────────────────────────────────
 * Return a {@code new BCryptPasswordEncoder()}.
 * This bean is also injected into DataInitializer to hash passwords at startup.
 *
 * ── daoAuthProvider() ──────────────────────────────────────────────────────
 * Create a {@code DaoAuthenticationProvider}, then:
 *   p.setUserDetailsService(userDetailsService);
 *   p.setPasswordEncoder(passwordEncoder());
 *
 * ── securityFilterChain() ──────────────────────────────────────────────────
 * Configure HttpSecurity:
 *   • Disable CSRF  (this is a stateless REST API)
 *   • Disable form login
 *   • Enable HTTP Basic so MockMvc tests can send credentials
 *   • Register the DaoAuthenticationProvider via http.authenticationProvider(...)
 *   • URL rules:
 *       /public/**  → permitAll()
 *       POST /books → hasRole("ADMIN")
 *       anything else → authenticated()
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private CustomUserDetailsService userDetailsService;

    // TODO: Declare a PasswordEncoder bean that returns BCryptPasswordEncoder.
    @Bean
    public PasswordEncoder passwordEncoder() {
        // TODO: return new BCryptPasswordEncoder();
        throw new UnsupportedOperationException("Not yet implemented");
    }

    // TODO: Declare a DaoAuthenticationProvider bean.
    @Bean
    public DaoAuthenticationProvider daoAuthProvider() {
        // TODO: Create DaoAuthenticationProvider, set userDetailsService and passwordEncoder.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    // TODO: Declare a SecurityFilterChain bean.
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // TODO: Configure CSRF, formLogin, httpBasic, authenticationProvider, and URL rules.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}

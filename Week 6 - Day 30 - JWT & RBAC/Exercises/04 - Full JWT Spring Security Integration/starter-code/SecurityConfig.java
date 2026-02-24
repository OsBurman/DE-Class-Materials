package com.jwt.integration.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Exercise 04 – Security configuration.
 *
 * TODO items:
 *   1. passwordEncoder() – return BCryptPasswordEncoder.
 *   2. userDetailsService() – InMemoryUserDetailsManager with:
 *        user  / password / USER
 *        admin / admin123 / ADMIN
 *   3. authenticationManager() – delegate to authenticationConfiguration.getAuthenticationManager().
 *   4. securityFilterChain():
 *        • CSRF disabled
 *        • STATELESS sessions
 *        • Permit POST /auth/login
 *        • GET /api/books → authenticated
 *        • POST /api/books → ADMIN only
 *        • addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    // TODO: Implement passwordEncoder() → return new BCryptPasswordEncoder()
    @Bean
    public PasswordEncoder passwordEncoder() {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    // TODO: Implement userDetailsService() with user + admin
    @Bean
    public UserDetailsService userDetailsService() {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    // TODO: Expose AuthenticationManager as a bean
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authenticationConfiguration) throws Exception {
        // TODO: return authenticationConfiguration.getAuthenticationManager();
        throw new UnsupportedOperationException("Not yet implemented");
    }

    // TODO: Configure the SecurityFilterChain
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}

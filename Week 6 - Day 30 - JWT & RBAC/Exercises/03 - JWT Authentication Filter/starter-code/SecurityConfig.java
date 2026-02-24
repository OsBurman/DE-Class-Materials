package com.jwt.filter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Exercise 03 – Security configuration for JWT filter exercise.
 *
 * This config:
 *   • Disables session creation (STATELESS) – JWTs are self-contained
 *   • Disables CSRF – stateless REST API
 *   • Registers the JwtAuthenticationFilter BEFORE Spring's default filter
 *   • Applies URL authorization rules
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/hello").authenticated()
                // TODO: add a rule: GET /api/admin requires ADMIN role
                .anyRequest().authenticated()
            )
            // TODO: Add the jwtAuthenticationFilter BEFORE UsernamePasswordAuthenticationFilter.
            //       Hint: http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
            ;

        return http.build();
    }
}

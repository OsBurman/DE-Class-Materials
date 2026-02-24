package com.jwt.rbac.config;

import com.jwt.rbac.security.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Exercise 05 – Security configuration.
 *
 * TODO:
 *   1. Add @EnableMethodSecurity to this class so that @PreAuthorize and @Secured work.
 *   2. Complete securityFilterChain():
 *        • CSRF disabled
 *        • STATELESS sessions
 *        • All /library/** requests require authentication (URL-level rule)
 *        • Register jwtAuthenticationFilter before UsernamePasswordAuthenticationFilter
 *
 * Note: The method-level annotations on LibraryService provide the ROLE-specific rules.
 *       The URL rule here just ensures unauthenticated requests are rejected at the web layer.
 */
@Configuration
@EnableWebSecurity
// TODO: Add @EnableMethodSecurity here
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // TODO: implement CSRF-disabled, STATELESS, all /library/** = authenticated,
        //       addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
        throw new UnsupportedOperationException("Not yet implemented");
    }
}

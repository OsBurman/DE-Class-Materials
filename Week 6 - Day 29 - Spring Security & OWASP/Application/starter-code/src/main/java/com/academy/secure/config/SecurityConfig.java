package com.academy.secure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * Spring Security configuration.
 *
 * TODO Task 2: Add class-level annotations:
 *   @Configuration
 *   @EnableWebSecurity
 *   @EnableMethodSecurity    (enables @PreAuthorize at method level)
 *
 * Then implement the filterChain bean.
 */
// TODO: add annotations
public class SecurityConfig {

    // TODO Task 2: Implement the SecurityFilterChain bean
    // @Bean
    // public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    //     return http
    //         .csrf(csrf -> csrf.disable())
    //         // EXPLAIN in a comment: why is CSRF disabled for REST APIs?
    //         .cors(cors -> cors.configurationSource(corsConfigurationSource()))
    //         .authorizeHttpRequests(auth -> auth
    //             .requestMatchers("/api/public/**").permitAll()
    //             .requestMatchers("/actuator/health").permitAll()
    //             .requestMatchers(HttpMethod.POST, "/api/tasks").hasRole("USER")
    //             .requestMatchers("/api/admin/**").hasRole("ADMIN")
    //             .anyRequest().authenticated()
    //         )
    //         .httpBasic(Customizer.withDefaults())
    //         .build();
    // }

    // TODO Task 4: Declare BCryptPasswordEncoder @Bean
    // @Bean
    // public PasswordEncoder passwordEncoder() {
    //     return new BCryptPasswordEncoder();
    // }

    // TODO Task 5: Implement CORS configuration
    // @Bean
    // public CorsConfigurationSource corsConfigurationSource() {
    //     CorsConfiguration config = new CorsConfiguration();
    //     config.setAllowedOrigins(List.of("http://localhost:3000"));
    //     config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE"));
    //     config.setAllowedHeaders(List.of("*"));
    //     UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    //     source.registerCorsConfiguration("/**", config);
    //     return source;
    // }
}

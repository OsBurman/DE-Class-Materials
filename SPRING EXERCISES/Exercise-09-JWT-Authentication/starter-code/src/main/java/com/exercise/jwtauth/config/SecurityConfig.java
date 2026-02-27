package com.exercise.jwtauth.config;

import com.exercise.jwtauth.jwt.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    // TODO 6: Configure SecurityFilterChain:
    //         - csrf.disable()
    //         - sessionManagement → STATELESS
    //         - authorizeHttpRequests:
    //             .requestMatchers("/api/auth/**").permitAll()
    //             .requestMatchers("/api/admin/**").hasRole("ADMIN")
    //             .anyRequest().authenticated()
    //         - Add jwtAuthFilter BEFORE UsernamePasswordAuthenticationFilter:
    //           .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
    //         - Return http.build()

    // TODO 7: Create AuthenticationManager bean:
    //         public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
    //             return config.getAuthenticationManager();
    //         }
    //         Spring uses this to authenticate username/password during login.

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

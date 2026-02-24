package com.academy.auth.config;

import com.academy.auth.filter.JwtAuthenticationFilter;
import com.academy.auth.model.Role;
import com.academy.auth.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.Map;

/**
 * JWT Security configuration.
 *
 * TODO Task 3: Implement the filterChain bean with STATELESS session management.
 * TODO Task 5: Add the 3 in-memory test users.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // TODO Task 5: In-memory users (admin, user, viewer)
    @Bean
    public UserDetailsService userDetailsService() {
        PasswordEncoder encoder = passwordEncoder();
        Map<String, User> users = Map.of(
            "admin",  new User("admin",  encoder.encode("admin123"),  Role.ROLE_ADMIN),
            "user",   new User("user",   encoder.encode("user123"),   Role.ROLE_USER),
            "viewer", new User("viewer", encoder.encode("viewer123"), Role.ROLE_VIEWER)
        );

        return username -> {
            User user = users.get(username);
            if (user == null) throw new UsernameNotFoundException("User not found: " + username);
            return org.springframework.security.core.userdetails.User.builder()
                    .username(user.getUsername())
                    .password(user.getEncodedPassword())
                    .authorities(user.getRole().name())
                    .build();
        };
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService());
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    // TODO Task 3: Implement SecurityFilterChain
    // @Bean
    // public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    //     return http
    //         .csrf(csrf -> csrf.disable())
    //         .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
    //         .authorizeHttpRequests(auth -> auth
    //             .requestMatchers("/auth/**").permitAll()
    //             .requestMatchers("/api/admin/**").hasAuthority("ROLE_ADMIN")
    //             .anyRequest().authenticated()
    //         )
    //         .authenticationProvider(authenticationProvider())
    //         .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
    //         .build();
    // }
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // Placeholder — replace with the TODO above
        return http.csrf(c -> c.disable()).authorizeHttpRequests(a -> a.anyRequest().permitAll()).build();
    }
}

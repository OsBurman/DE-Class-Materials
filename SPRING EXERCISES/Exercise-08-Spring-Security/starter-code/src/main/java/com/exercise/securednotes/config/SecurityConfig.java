package com.exercise.securednotes.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

// TODO 1: Add @Configuration and @EnableWebSecurity annotations to this class.
//         @Configuration tells Spring this class provides bean definitions.
//         @EnableWebSecurity activates Spring Security's web security support.
public class SecurityConfig {

    // TODO 2: Create a SecurityFilterChain bean.
    // Method signature: public SecurityFilterChain filterChain(HttpSecurity http)
    // throws Exception
    // Configure it to:
    // - Disable CSRF: .csrf(csrf -> csrf.disable())
    // (REST APIs don't use CSRF tokens because they're stateless)
    // - Set session policy to STATELESS:
    // .sessionManagement(sm ->
    // sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
    // - Authorize requests:
    // .authorizeHttpRequests(auth -> auth
    // .requestMatchers("/actuator/**").permitAll()
    // .requestMatchers("/api/admin/**").hasRole("ADMIN")
    // .anyRequest().authenticated()
    // )
    // - Enable HTTP Basic: .httpBasic(Customizer.withDefaults())
    // - Return http.build()
    //
    // HTTP Basic means: clients send "Authorization: Basic
    // base64(username:password)" header

    // TODO 3: Create a UserDetailsService bean (InMemoryUserDetailsManager).
    // This defines who can log in. Create two users:
    // User 1: username="user", password=passwordEncoder().encode("Password1!"),
    // roles="USER"
    // User 2: username="admin", password=passwordEncoder().encode("Admin123!"),
    // roles="ADMIN"
    // Use: User.withUsername("user").password(...).roles("USER").build()
    //
    // NOTE: Use passwordEncoder().encode() — never store plain-text passwords!

    // TODO 4: Create a PasswordEncoder bean.
    // Return new BCryptPasswordEncoder()
    // BCrypt is a secure, slow-by-design hashing algorithm for passwords.
    // Spring Security uses this bean automatically when authenticating.
    @Bean
    public PasswordEncoder passwordEncoder() {
        // TODO 4: Replace this with new BCryptPasswordEncoder()
        throw new UnsupportedOperationException("TODO 4: implement passwordEncoder()");
    }
}

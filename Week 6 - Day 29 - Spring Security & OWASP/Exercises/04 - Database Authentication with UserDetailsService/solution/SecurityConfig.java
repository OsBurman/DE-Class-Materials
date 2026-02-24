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
 * Spring Security configuration using database-backed authentication.
 *
 * Authentication flow:
 *   1. Client sends HTTP Basic credentials.
 *   2. Spring Security calls DaoAuthenticationProvider.
 *   3. DaoAuthenticationProvider delegates to CustomUserDetailsService
 *      to load the user from the database.
 *   4. BCryptPasswordEncoder compares the stored hash with the incoming
 *      plaintext password.
 *   5. If they match, the request proceeds; if not, 401 is returned.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private CustomUserDetailsService userDetailsService;

    /**
     * BCrypt password encoder – shared with DataInitializer so that the
     * hashes stored during seeding match what this encoder can verify.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Wires our custom UserDetailsService and the BCrypt encoder together
     * into a DaoAuthenticationProvider.
     *
     * Spring Security uses this provider to authenticate every request.
     */
    @Bean
    public DaoAuthenticationProvider daoAuthProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    /**
     * Configures the security filter chain:
     *   • CSRF disabled   – stateless REST API; no session cookies
     *   • Form login disabled – we use HTTP Basic for simplicity
     *   • HTTP Basic enabled – allows MockMvc tests to send credentials
     *   • URL rules:
     *       /public/**          → open to everyone
     *       POST /books         → ADMIN role only
     *       everything else     → any authenticated user
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .formLogin(AbstractHttpConfigurer::disable)
            .httpBasic(basic -> {})           // enable HTTP Basic
            .authenticationProvider(daoAuthProvider())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/public/**").permitAll()
                .requestMatchers(HttpMethod.POST, "/books").hasRole("ADMIN")
                .anyRequest().authenticated()
            );

        return http.build();
    }
}

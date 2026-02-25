package com.academy.secure.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Custom UserDetailsService implementation.
 * Simulates loading users from a database.
 *
 * TODO Task 3: Implement the loadUserByUsername method.
 * TODO Task 4: Use the PasswordEncoder to encode passwords (not plain text!).
 */
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final PasswordEncoder passwordEncoder;

    // In-memory user store (simulates a database)
    // TODO Task 3: Use this map to look up users
    private Map<String, UserRecord> users() {
        return Map.of(
                "admin", new UserRecord(passwordEncoder.encode("admin123"), "ADMIN"),
                "user", new UserRecord(passwordEncoder.encode("user123"), "USER"),
                "viewer", new UserRecord(passwordEncoder.encode("viewer123"), "VIEWER"));
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // TODO Task 3: Look up the user in the map
        // If not found, throw new UsernameNotFoundException("User not found: " +
        // username)
        // If found, return:
        // User.builder()
        // .username(username)
        // .password(record.encodedPassword())
        // .roles(record.role()) // roles() wraps with ROLE_ prefix automatically
        // .build();

        throw new UsernameNotFoundException("TODO: implement loadUserByUsername");
    }

    // Simple record to hold user data
    record UserRecord(String encodedPassword, String role) {
    }
}

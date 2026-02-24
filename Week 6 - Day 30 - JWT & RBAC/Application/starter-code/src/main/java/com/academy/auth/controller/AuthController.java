package com.academy.auth.controller;

import com.academy.auth.dto.LoginRequestDto;
import com.academy.auth.dto.LoginResponseDto;
import com.academy.auth.service.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Authentication endpoints.
 *
 * TODO Task 4: Implement login, refresh, and logout endpoints.
 */
@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;

    // Blacklist for invalidated tokens (in-memory, lost on restart)
    private final Set<String> tokenBlacklist = new HashSet<>();

    // TODO Task 4a: POST /auth/login
    // 1. Call authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password))
    //    — throws BadCredentialsException if invalid (Spring handles 401 automatically)
    // 2. Load UserDetails via userDetailsService.loadUserByUsername()
    // 3. Generate access and refresh tokens
    // 4. Return LoginResponseDto with both tokens
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody LoginRequestDto request) {
        // TODO
        return ResponseEntity.ok().build();
    }

    // TODO Task 4b: POST /auth/refresh
    // Body: { "refreshToken": "..." }
    // 1. Extract username from the refresh token using jwtService.extractUsername()
    // 2. Load UserDetails
    // 3. Validate that the token is valid
    // 4. Generate a new access token
    // 5. Return new access token in LoginResponseDto
    @PostMapping("/refresh")
    public ResponseEntity<LoginResponseDto> refresh(@RequestBody Map<String, String> body) {
        // TODO
        return ResponseEntity.ok().build();
    }

    // TODO Task 4c: POST /auth/logout
    // Add the token to the blacklist
    // Note: You'll also need to check the blacklist in JwtAuthenticationFilter
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestHeader("Authorization") String authHeader) {
        // TODO: extract token from header, add to tokenBlacklist
        return ResponseEntity.noContent().build();
    }
}

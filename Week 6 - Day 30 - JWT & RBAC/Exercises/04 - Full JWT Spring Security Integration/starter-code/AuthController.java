package com.jwt.integration.controller;

import com.jwt.integration.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Exercise 04 – Authentication controller.
 *
 * POST /auth/login validates credentials and, if valid, returns a JWT.
 */
@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * TODO: Implement the login endpoint.
     *
     * Steps:
     *   1. Call authenticationManager.authenticate(
     *          new UsernamePasswordAuthenticationToken(
     *              request.getUsername(), request.getPassword()))
     *      Wrap in a try-catch for BadCredentialsException.
     *
     *   2. On success, extract the username from authentication.getName()
     *      and the first role from authentication.getAuthorities().
     *      Strip the "ROLE_" prefix to get the plain role string
     *      (e.g., "ROLE_ADMIN" → "ADMIN").
     *
     *   3. Call jwtUtil.generateToken(username, role) and return:
     *      ResponseEntity.ok(Map.of("token", token))
     *
     *   4. On BadCredentialsException, return:
     *      ResponseEntity.status(401).build()
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        // TODO: implement
        throw new UnsupportedOperationException("Not yet implemented");
    }
}

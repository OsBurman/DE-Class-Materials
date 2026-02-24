package com.jwt.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * Exercise 02 – JWT Generation and Validation
 *
 * Implement the five methods below using the jjwt 0.12.x API.
 *
 * Key jjwt 0.12 classes:
 *   Jwts.builder()               – create a new JWT
 *   Jwts.parser()                – parse / validate a JWT
 *   Keys.hmacShaKeyFor(bytes)    – create a SecretKey from a byte array
 *   Claims                       – holds the decoded payload fields
 */
@Component
public class JwtUtil {

    // Secret must be at least 32 characters for HMAC-SHA256
    private static final String SECRET = "my-super-secret-jwt-signing-key-32chars!";

    // Expiry: 1 hour in milliseconds
    private static final long EXPIRATION_MS = 60 * 60 * 1000L;

    // TODO: Build the SecretKey from SECRET bytes.
    //       Assign to signingKey with: Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8))
    private final SecretKey signingKey = null; // replace null

    /**
     * TODO: Generate a signed JWT.
     *
     * Steps:
     *   1. Call Jwts.builder()
     *   2. .subject(username)
     *   3. .claim("role", role)
     *   4. .issuedAt(new Date())
     *   5. .expiration(new Date(System.currentTimeMillis() + EXPIRATION_MS))
     *   6. .signWith(signingKey)
     *   7. .compact()  ← returns the token string
     */
    public String generateToken(String username, String role) {
        // TODO: implement
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /**
     * TODO: Validate a token.
     *
     * Parse the token with Jwts.parser().verifyWith(signingKey).build().parseSignedClaims(token).
     * Return true if parsing succeeds, false if a JwtException is thrown.
     * Catch JwtException (and optionally IllegalArgumentException) to return false.
     */
    public boolean validateToken(String token) {
        // TODO: implement
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /**
     * TODO: Return the subject (username) from the token.
     *
     * Use parseClaims(token).getSubject().
     */
    public String extractUsername(String token) {
        // TODO: implement
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /**
     * TODO: Return the "role" custom claim from the token as a String.
     *
     * Use parseClaims(token).get("role", String.class).
     */
    public String extractRole(String token) {
        // TODO: implement
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /**
     * TODO: Return the expiry Date from the token.
     *
     * Use parseClaims(token).getExpiration().
     */
    public Date extractExpiry(String token) {
        // TODO: implement
        throw new UnsupportedOperationException("Not yet implemented");
    }

    // ── Private helper ────────────────────────────────────────────────────────

    /**
     * Helper: parse and return the Claims from a token.
     * TODO: implement using Jwts.parser().verifyWith(signingKey).build().parseSignedClaims(token).getPayload()
     */
    private Claims parseClaims(String token) {
        // TODO: implement
        throw new UnsupportedOperationException("Not yet implemented");
    }
}

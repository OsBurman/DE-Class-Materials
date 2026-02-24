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
 * Exercise 02 – JWT Generation and Validation  (SOLUTION)
 *
 * Uses jjwt 0.12.x fluent API.
 *
 * Key API changes from older versions:
 *   • Jwts.builder().subject() replaces .setSubject()
 *   • Jwts.parser().verifyWith() replaces .setSigningKey()
 *   • parseSignedClaims() replaces parseClaimsJws()
 */
@Component
public class JwtUtil {

    private static final String SECRET = "my-super-secret-jwt-signing-key-32chars!";
    private static final long EXPIRATION_MS = 60 * 60 * 1000L; // 1 hour

    // Build the signing key from the secret bytes.
    // Keys.hmacShaKeyFor validates that the key is long enough for HS256.
    private final SecretKey signingKey =
            Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));

    /** Generate a signed, expiring JWT containing the username and role. */
    public String generateToken(String username, String role) {
        return Jwts.builder()
                   .subject(username)               // "sub" claim
                   .claim("role", role)              // custom claim
                   .issuedAt(new Date())             // "iat" claim
                   .expiration(new Date(System.currentTimeMillis() + EXPIRATION_MS)) // "exp"
                   .signWith(signingKey)             // signs with HMAC-SHA256
                   .compact();                       // serialize to the three-part string
    }

    /** Return true if the token has a valid signature and has not expired. */
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token);   // throws JwtException if invalid
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    /** Return the subject (username) embedded in the token. */
    public String extractUsername(String token) {
        return parseClaims(token).getSubject();
    }

    /** Return the custom "role" claim as a String. */
    public String extractRole(String token) {
        return parseClaims(token).get("role", String.class);
    }

    /** Return the expiry Date embedded in the token. */
    public Date extractExpiry(String token) {
        return parseClaims(token).getExpiration();
    }

    // ── Private helper ────────────────────────────────────────────────────────

    /** Parse and return the full Claims payload. */
    private Claims parseClaims(String token) {
        return Jwts.parser()
                   .verifyWith(signingKey)
                   .build()
                   .parseSignedClaims(token)
                   .getPayload();            // getPayload() replaces getBody() in 0.12
    }
}

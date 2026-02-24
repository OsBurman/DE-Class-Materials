package com.jwt.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Exercise 02 – Unit tests for JwtUtil  (SOLUTION)
 */
class JwtUtilTest {

    private JwtUtil jwtUtil;
    private String token;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        token = jwtUtil.generateToken("alice", "ADMIN");
    }

    @Test
    void generatedToken_isNotBlank() {
        assertNotNull(token, "Token must not be null");
        assertFalse(token.isBlank(), "Token must not be blank");
    }

    @Test
    void validateToken_freshToken_returnsTrue() {
        assertTrue(jwtUtil.validateToken(token));
    }

    @Test
    void extractUsername_returnsAlice() {
        assertEquals("alice", jwtUtil.extractUsername(token));
    }

    @Test
    void extractRole_returnsAdmin() {
        assertEquals("ADMIN", jwtUtil.extractRole(token));
    }

    @Test
    void validateToken_tamperedToken_returnsFalse() {
        // Tamper with the signature (third part after the last ".")
        String[] parts = token.split("\\.");
        String sig = parts[2];
        // Flip the last character to break the HMAC signature
        char flipped = (sig.charAt(sig.length() - 1) == 'a') ? 'b' : 'a';
        String tampered = parts[0] + "." + parts[1] + "."
                        + sig.substring(0, sig.length() - 1) + flipped;

        assertFalse(jwtUtil.validateToken(tampered), "Tampered token should be invalid");
    }
}

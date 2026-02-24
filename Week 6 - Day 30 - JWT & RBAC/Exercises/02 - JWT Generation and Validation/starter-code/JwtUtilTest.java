package com.jwt.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Exercise 02 – Unit tests for JwtUtil.
 *
 * All five tests must pass once you complete JwtUtil.
 */
class JwtUtilTest {

    private JwtUtil jwtUtil;
    private String token;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        // TODO: Generate a token for username="alice" and role="ADMIN".
        //       Assign to the field 'token' so the other tests can use it.
        token = null; // replace null
    }

    /** The generated token must not be null or blank. */
    @Test
    void generatedToken_isNotBlank() {
        // TODO: Assert that token is not null and not blank.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /** A freshly generated token should be valid. */
    @Test
    void validateToken_freshToken_returnsTrue() {
        // TODO: Assert that jwtUtil.validateToken(token) returns true.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /** extractUsername should return the subject we passed in. */
    @Test
    void extractUsername_returnsAlice() {
        // TODO: Assert that jwtUtil.extractUsername(token) equals "alice".
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /** extractRole should return the role we passed in. */
    @Test
    void extractRole_returnsAdmin() {
        // TODO: Assert that jwtUtil.extractRole(token) equals "ADMIN".
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /**
     * A tampered token should fail validation.
     *
     * To tamper: split the token on ".", change one character in parts[2] (the signature),
     * then rejoin with ".".
     */
    @Test
    void validateToken_tamperedToken_returnsFalse() {
        // TODO: Create a tampered version of 'token' by flipping one character in the signature.
        //       Assert that jwtUtil.validateToken(tamperedToken) returns false.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}

package com.exercise.jwtauth.jwt;

import com.exercise.jwtauth.config.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtUtil {

    private final JwtProperties jwtProperties;

    public JwtUtil(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
    }

    // TODO 1: Generate a JWT token.
    // Use Jwts.builder() to create the token:
    // .subject(username)
    // .claim("role", role)
    // .issuedAt(new Date())
    // .expiration(new Date(System.currentTimeMillis() +
    // jwtProperties.getExpirationMs()))
    // .signWith(Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes()))
    // .compact()
    public String generateToken(String username, String role) {
        // TODO 1: implement this
        throw new UnsupportedOperationException("TODO 1: implement generateToken");
    }

    // TODO 2: Extract the username (subject) from a token.
    // Parse the token with the same signing key, get the Claims body,
    // and return claims.getSubject()
    //
    // Use:
    // Jwts.parser()
    // .verifyWith(Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes()))
    // .build()
    // .parseSignedClaims(token)
    // .getPayload()
    public String extractUsername(String token) {
        // TODO 2: implement this
        throw new UnsupportedOperationException("TODO 2: implement extractUsername");
    }

    // TODO 3: Validate a token — return true if it can be parsed without exception.
    // Wrap the parsing in a try/catch(Exception e) and return false on any
    // exception.
    // This handles: expired tokens, malformed tokens, wrong signature, etc.
    public boolean isTokenValid(String token) {
        // TODO 3: implement this
        throw new UnsupportedOperationException("TODO 3: implement isTokenValid");
    }
}

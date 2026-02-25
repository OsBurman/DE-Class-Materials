package com.academy.auth.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Service for JWT generation and validation.
 *
 * TODO Task 1: Implement all methods.
 * The JJWT library (jjwt-api, jjwt-impl, jjwt-jackson) is included in pom.xml.
 */
@Slf4j
@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration-ms}")
    private long expirationMs;

    @Value("${jwt.refresh-expiration-ms}")
    private long refreshExpirationMs;

    // TODO Task 1a: Generate access token (short-lived)
    // Include claim: "type" = "access"
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("type", "access");
        // TODO: add roles claim from userDetails.getAuthorities()
        return buildToken(claims, userDetails.getUsername(), expirationMs);
    }

    // TODO Task 1b: Generate refresh token (long-lived)
    // Include claim: "type" = "refresh"
    public String generateRefreshToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("type", "refresh");
        return buildToken(claims, userDetails.getUsername(), refreshExpirationMs);
    }

    // TODO Task 1c: Extract username (subject) from token
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // TODO Task 1d: Validate token — check username matches and token is not
    // expired
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    // ---- Private helpers ----

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        // TODO Task 1e: Parse the JWT and return claims
        // Hint:
        // return Jwts.parser()
        // .verifyWith(getSigningKey())
        // .build()
        // .parseSignedClaims(token)
        // .getPayload();
        throw new UnsupportedOperationException("TODO: implement extractAllClaims");
    }

    private String buildToken(Map<String, Object> extraClaims, String subject, long expiration) {
        // TODO Task 1f: Build and sign the JWT
        // Hint:
        // return Jwts.builder()
        // .claims(extraClaims)
        // .subject(subject)
        // .issuedAt(new Date())
        // .expiration(new Date(System.currentTimeMillis() + expiration))
        // .signWith(getSigningKey())
        // .compact();
        throw new UnsupportedOperationException("TODO: implement buildToken");
    }

    private SecretKey getSigningKey() {
        // TODO Task 1g: Decode the base64 secret and create an HMAC key
        // byte[] keyBytes = Decoders.BASE64.decode(secret);
        // return Keys.hmacShaKeyFor(keyBytes);
        throw new UnsupportedOperationException("TODO: implement getSigningKey");
    }
}

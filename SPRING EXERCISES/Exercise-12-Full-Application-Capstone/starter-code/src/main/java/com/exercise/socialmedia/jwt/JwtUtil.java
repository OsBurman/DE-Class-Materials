package com.exercise.socialmedia.jwt;

import com.exercise.socialmedia.config.JwtProperties;
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

    // TODO 1: Implement generateToken, extractUsername, isTokenValid
    //         (See Exercise 09 for the full implementation pattern)
    public String generateToken(String username, String role) {
        throw new UnsupportedOperationException("TODO 1: implement generateToken");
    }

    public String extractUsername(String token) {
        throw new UnsupportedOperationException("TODO 1: implement extractUsername");
    }

    public boolean isTokenValid(String token) {
        throw new UnsupportedOperationException("TODO 1: implement isTokenValid");
    }
}

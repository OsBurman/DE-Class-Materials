package com.jwt.rbac.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        // Step 1: Read the Authorization header
        String authHeader = request.getHeader("Authorization");

        // Step 2: If missing or not Bearer, skip JWT processing
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Step 3: Extract the raw token
        String token = authHeader.substring(7);

        // Step 4: Validate the token signature and expiry
        if (!jwtUtil.validateToken(token)) {
            filterChain.doFilter(request, response);
            return;
        }

        // Step 5: Extract claims
        String username = jwtUtil.extractUsername(token);
        String role     = jwtUtil.extractRole(token);

        // Step 6: Build a GrantedAuthority from the role claim (ROLE_ prefix required)
        var authority = new SimpleGrantedAuthority("ROLE_" + role);

        // Step 7: Build the authentication token (3-arg = already authenticated)
        var authToken = new UsernamePasswordAuthenticationToken(
                username, null, List.of(authority));

        // Step 8: Attach request details for audit / logging
        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        // Step 9: Store in SecurityContext so downstream filters & controllers can read it
        SecurityContextHolder.getContext().setAuthentication(authToken);

        filterChain.doFilter(request, response);
    }
}

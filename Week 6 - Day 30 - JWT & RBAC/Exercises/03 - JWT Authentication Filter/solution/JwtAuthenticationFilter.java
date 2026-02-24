package com.jwt.filter;

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

/**
 * Exercise 03 – JWT Authentication Filter  (SOLUTION)
 *
 * Intercepts every request and authenticates the user if a valid Bearer JWT
 * is present in the Authorization header.
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        // Step 1: Retrieve the Authorization header
        String authHeader = request.getHeader("Authorization");

        // Step 2: Only process if it looks like a Bearer token
        if (authHeader != null && authHeader.startsWith("Bearer ")) {

            // Step 3: Strip "Bearer " prefix (7 chars)
            String token = authHeader.substring(7);

            // Step 4: Validate the token – silently ignore invalid tokens;
            //         the authorization rules will return 401/403 as appropriate.
            if (jwtUtil.validateToken(token)) {

                // Step 5: Extract identity claims
                String username = jwtUtil.extractUsername(token);
                String role     = jwtUtil.extractRole(token);

                // Step 6: Build an already-authenticated token.
                //         The 3-arg constructor sets authenticated=true.
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                username,
                                null,    // credentials – not needed after validation
                                List.of(new SimpleGrantedAuthority("ROLE_" + role))
                        );

                // Step 7: Attach request details (IP address, session id, etc.)
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // Step 8: Register in the SecurityContext for this thread
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        // Step 9: Always continue the filter chain
        filterChain.doFilter(request, response);
    }
}

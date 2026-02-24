package com.jwt.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Set;

/**
 * Validates an API key passed in the X-API-Key request header.
 *
 * If the key is valid, a synthetic Authentication is injected into the
 * SecurityContextHolder so the request is treated as authenticated.
 * If the key is absent or invalid, no authentication is set and the
 * filter chain continues; Spring Security's AuthenticationEntryPoint
 * will produce a 401 if the endpoint requires authentication.
 */
@Component
public class ApiKeyFilter extends OncePerRequestFilter {

    static final Set<String> VALID_API_KEYS = Set.of(
            "secret-api-key-123",
            "another-valid-key-456"
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        // Step 1: Read the API key header
        String apiKey = request.getHeader("X-API-Key");

        // Step 2: Validate; inject Authentication only if key is recognized
        if (apiKey != null && VALID_API_KEYS.contains(apiKey)) {
            var authority  = new SimpleGrantedAuthority("ROLE_API_CLIENT");
            var authToken  = new UsernamePasswordAuthenticationToken(
                    "api-client", null, List.of(authority));
            SecurityContextHolder.getContext().setAuthentication(authToken);
        }

        // Step 3: Always continue the chain
        filterChain.doFilter(request, response);
    }
}

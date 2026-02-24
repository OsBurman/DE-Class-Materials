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
 * API keys are opaque tokens used for machine-to-machine (M2M) authentication.
 * Unlike JWTs they carry no claims – the server maps them to an identity/role.
 *
 * Flow:
 *  1. Read X-API-Key header
 *  2. If valid  → inject a synthetic Authentication into SecurityContextHolder
 *  3. Always call filterChain.doFilter() – do NOT short-circuit on failure;
 *     the missing Authentication will trigger CustomAuthEntryPoint automatically
 */
@Component
public class ApiKeyFilter extends OncePerRequestFilter {

    // In production these would be stored hashed in a database
    static final Set<String> VALID_API_KEYS = Set.of(
            "secret-api-key-123",
            "another-valid-key-456"
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        // TODO 1: Read the "X-API-Key" header from the request

        // TODO 2: Check if the key is present AND contained in VALID_API_KEYS

        // TODO 3: If valid, build and set an Authentication in SecurityContextHolder:
        //   a) Create a SimpleGrantedAuthority("ROLE_API_CLIENT")
        //   b) Create UsernamePasswordAuthenticationToken("api-client", null, List.of(authority))
        //   c) Call SecurityContextHolder.getContext().setAuthentication(authToken)

        // TODO 4: Always call filterChain.doFilter(request, response) to continue the chain
    }
}

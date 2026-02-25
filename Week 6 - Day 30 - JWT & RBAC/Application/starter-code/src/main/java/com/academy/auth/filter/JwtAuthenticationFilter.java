package com.academy.auth.filter;

import com.academy.auth.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JWT Authentication Filter — runs once per request.
 *
 * TODO Task 2: Implement doFilterInternal.
 * Follow the 6 steps in the instructions.md.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        // TODO Step 1: Get Authorization header
        final String authHeader = request.getHeader("Authorization");

        // TODO Step 2: Check for Bearer prefix — if not present, skip to next filter
        // if (authHeader == null || !authHeader.startsWith("Bearer ")) {
        // filterChain.doFilter(request, response);
        // return;
        // }

        // TODO Step 3: Extract JWT from header (remove "Bearer " prefix)
        // final String jwt = authHeader.substring(7);

        // TODO Step 4: Extract username from JWT
        // final String username = jwtService.extractUsername(jwt);

        // TODO Step 5: Load UserDetails and validate token
        // if (username != null &&
        // SecurityContextHolder.getContext().getAuthentication() == null) {
        // UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        // if (jwtService.isTokenValid(jwt, userDetails)) {
        // UsernamePasswordAuthenticationToken authToken =
        // new UsernamePasswordAuthenticationToken(
        // userDetails, null, userDetails.getAuthorities());
        // authToken.setDetails(new
        // WebAuthenticationDetailsSource().buildDetails(request));
        // SecurityContextHolder.getContext().setAuthentication(authToken);
        // }
        // }

        // TODO Step 6: Continue filter chain
        filterChain.doFilter(request, response);
    }
}

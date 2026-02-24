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
 * Exercise 03 – JWT Authentication Filter
 *
 * This filter runs once per request.  If the request carries a valid Bearer JWT
 * in the Authorization header, it authenticates the user in the SecurityContext.
 *
 * Authentication flow:
 *   1. Client sends:  Authorization: Bearer <token>
 *   2. This filter extracts and validates the token.
 *   3. If valid, it creates a UsernamePasswordAuthenticationToken and stores it
 *      in SecurityContextHolder so Spring Security can apply authorization rules.
 *   4. The request proceeds to the controller.
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * TODO: Implement the filter logic.
     *
     * Step 1: Get the "Authorization" header from the request.
     *         Hint: request.getHeader("Authorization")
     *
     * Step 2: Check if the header is not null AND starts with "Bearer ".
     *         If not, skip to filterChain.doFilter(...) at the bottom.
     *
     * Step 3: Extract the token by stripping the "Bearer " prefix (7 characters).
     *         String token = authHeader.substring(7);
     *
     * Step 4: Validate the token with jwtUtil.validateToken(token).
     *         If invalid, skip to filterChain.doFilter(...).
     *
     * Step 5: Extract username and role from the token.
     *
     * Step 6: Build a UsernamePasswordAuthenticationToken:
     *           new UsernamePasswordAuthenticationToken(
     *               username,
     *               null,   // credentials not needed after validation
     *               List.of(new SimpleGrantedAuthority("ROLE_" + role))
     *           )
     *
     * Step 7: Set request details:
     *           authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
     *
     * Step 8: Store in SecurityContext:
     *           SecurityContextHolder.getContext().setAuthentication(authToken);
     *
     * Step 9: ALWAYS call filterChain.doFilter(request, response) at the end.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        // TODO: implement steps 1–9 described above

        filterChain.doFilter(request, response); // always continue the chain
    }
}

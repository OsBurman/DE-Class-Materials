package com.exercise.jwtauth.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

// TODO 4: This class must extend OncePerRequestFilter.
//         OncePerRequestFilter guarantees the filter runs exactly once per HTTP request,
//         even in cases where the same request is forwarded internally.
@Component
public class JwtAuthenticationFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtUtil jwtUtil, UserDetailsService userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    // TODO 4: Make this class extend OncePerRequestFilter (above)
    //
    // TODO 5: Implement doFilterInternal(HttpServletRequest, HttpServletResponse,
    // FilterChain):
    // 1. Get header: String authHeader = request.getHeader("Authorization")
    // 2. If authHeader != null && authHeader.startsWith("Bearer "):
    // a. Extract token: String token = authHeader.substring(7)
    // b. String username = jwtUtil.extractUsername(token)
    // c. Check: username != null && jwtUtil.isTokenValid(token)
    // && SecurityContextHolder.getContext().getAuthentication() == null
    // d. Load UserDetails: UserDetails userDetails =
    // userDetailsService.loadUserByUsername(username)
    // e. Create auth token:
    // UsernamePasswordAuthenticationToken authToken =
    // new UsernamePasswordAuthenticationToken(userDetails, null,
    // userDetails.getAuthorities())
    // f. Set details: authToken.setDetails(new
    // WebAuthenticationDetailsSource().buildDetails(request))
    // g. Set on context:
    // SecurityContextHolder.getContext().setAuthentication(authToken)
    // 3. Always call: filterChain.doFilter(request, response)
}

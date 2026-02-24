package com.jwt.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Simple in-memory rate limiter keyed by client IP address.
 *
 * When a client exceeds MAX_REQUESTS_PER_WINDOW requests the filter
 * short-circuits and returns HTTP 429 with a JSON body – the downstream
 * filter chain is NOT invoked.
 */
@Component
public class RateLimitFilter extends OncePerRequestFilter {

    static final int MAX_REQUESTS_PER_WINDOW = 5;

    private final ConcurrentHashMap<String, AtomicInteger> requestCounts =
            new ConcurrentHashMap<>();

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        // Step 1: Identify client by IP
        String ip = request.getRemoteAddr();

        // Step 2: Atomically increment the counter for this IP
        int count = requestCounts
                .computeIfAbsent(ip, k -> new AtomicInteger())
                .incrementAndGet();

        // Step 3: Reject if over limit
        if (count > MAX_REQUESTS_PER_WINDOW) {
            response.setStatus(429);
            response.setContentType("application/json");
            response.getWriter().write(
                    "{\"error\":\"Too Many Requests\",\"message\":\"Rate limit exceeded\"}");
            return; // Do NOT call filterChain.doFilter
        }

        // Step 4: Under limit – continue normally
        filterChain.doFilter(request, response);
    }
}

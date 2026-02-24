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
 * Simple in-memory rate limiter per client IP address.
 *
 * For each incoming request:
 *  - Increment a counter for the client's IP
 *  - If the counter exceeds MAX_REQUESTS_PER_WINDOW, reject with HTTP 429
 *  - Otherwise continue the filter chain
 *
 * NOTE: This is a demonstration. A production rate limiter would:
 *   - Use a sliding window or token-bucket algorithm
 *   - Store counters in Redis for distributed environments
 *   - Reset counters on a schedule (@Scheduled)
 *   - Apply per-user limits, not just per-IP
 */
@Component
public class RateLimitFilter extends OncePerRequestFilter {

    // Keep low for testing; raise for production (e.g. 100 per minute)
    static final int MAX_REQUESTS_PER_WINDOW = 5;

    // Thread-safe: multiple requests can arrive concurrently
    private final ConcurrentHashMap<String, AtomicInteger> requestCounts =
            new ConcurrentHashMap<>();

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        // TODO 1: Get the client IP address using request.getRemoteAddr()

        // TODO 2: Atomically increment the counter for this IP:
        //   int count = requestCounts
        //       .computeIfAbsent(ip, k -> new AtomicInteger())
        //       .incrementAndGet();

        // TODO 3: If count > MAX_REQUESTS_PER_WINDOW:
        //   a) Set response status to 429
        //   b) Set Content-Type to "application/json"
        //   c) Write: {"error":"Too Many Requests","message":"Rate limit exceeded"}
        //   d) Return (do NOT call filterChain.doFilter)

        // TODO 4: Otherwise call filterChain.doFilter(request, response)
    }
}

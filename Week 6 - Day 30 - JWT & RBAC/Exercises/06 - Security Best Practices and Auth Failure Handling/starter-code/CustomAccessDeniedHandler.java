package com.jwt.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Called by Spring Security when an authenticated user attempts to access
 * a resource they are not authorized for (HTTP 403 Forbidden).
 *
 * This is the companion to {@link CustomAuthEntryPoint}:
 *   - 401 = "Who are you?"  → AuthenticationEntryPoint
 *   - 403 = "You can't do that." → AccessDeniedHandler
 */
@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException accessDeniedException)
            throws IOException {

        // TODO 1: Set the response Content-Type to "application/json"

        // TODO 2: Set the HTTP status code to 403 (use HttpServletResponse.SC_FORBIDDEN)

        // TODO 3: Write the following JSON string to the response writer:
        //         {"error":"Forbidden","message":"Access denied"}
    }
}

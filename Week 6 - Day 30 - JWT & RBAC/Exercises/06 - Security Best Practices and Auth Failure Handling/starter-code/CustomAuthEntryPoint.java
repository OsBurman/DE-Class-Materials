package com.jwt.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Called by Spring Security when a request reaches a protected endpoint
 * without valid credentials (HTTP 401 Unauthorized).
 *
 * Default Spring Security behavior returns an empty body or HTML page.
 * This implementation returns a structured JSON response so REST clients
 * can parse and handle the error programmatically.
 */
@Component
public class CustomAuthEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException)
            throws IOException {

        // TODO 1: Set the response Content-Type to "application/json"

        // TODO 2: Set the HTTP status code to 401 (use HttpServletResponse.SC_UNAUTHORIZED)

        // TODO 3: Write the following JSON string to the response writer:
        //         {"error":"Unauthorized","message":"Authentication required"}
        //         Hint: response.getWriter().write("...");
    }
}

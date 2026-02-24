# Exercise 06 – Security Best Practices and Auth Failure Handling

## Learning Objectives

By the end of this exercise you will be able to:

- Return structured JSON error responses for **401 Unauthorized** and **403 Forbidden** using Spring Security's `AuthenticationEntryPoint` and `AccessDeniedHandler`
- Implement a simple **API key filter** that validates an `X-API-Key` request header
- Build a basic **rate-limiting filter** using an in-memory counter per client IP
- Wire these components into a Spring Security configuration using `exceptionHandling()`

---

## Background

### Why Default Error Responses Are a Problem

By default Spring Security returns a plain HTML or empty body for 401/403 errors. In a REST API, clients expect **JSON** so they can parse the error programmatically. Spring Security provides two interfaces for this purpose:

| Interface | Triggers on | Use for |
|---|---|---|
| `AuthenticationEntryPoint` | Missing / invalid credentials (401) | "Who are you?" |
| `AccessDeniedHandler` | Authenticated but wrong role (403) | "You can't do that." |

### AuthenticationEntryPoint

```java
public class CustomAuthEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest req, HttpServletResponse res,
                         AuthenticationException ex) throws IOException {
        res.setContentType("application/json");
        res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        res.getWriter().write("{\"error\":\"Unauthorized\",\"message\":\"Authentication required\"}");
    }
}
```

### AccessDeniedHandler

```java
public class CustomAccessDeniedHandler implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest req, HttpServletResponse res,
                       AccessDeniedException ex) throws IOException {
        res.setContentType("application/json");
        res.setStatus(HttpServletResponse.SC_FORBIDDEN);
        res.getWriter().write("{\"error\":\"Forbidden\",\"message\":\"Access denied\"}");
    }
}
```

### API Key Authentication

API keys are opaque tokens passed in a custom header (e.g. `X-API-Key`). They are
useful for machine-to-machine (M2M) calls. An `OncePerRequestFilter` inspects the header
and injects a synthetic `Authentication` object if the key is valid.

```
curl -H "X-API-Key: secret-api-key-123" http://localhost:8080/api/data
```

### Rate Limiting

Rate limiting protects your service from abuse. A simple approach:
- Keep a `ConcurrentHashMap<String, AtomicInteger>` mapping **client IP → request count**
- On each request increment the counter; if it exceeds a threshold return **HTTP 429**
- Reset counters periodically (e.g. every minute) using a scheduled task

### Security Best Practices Checklist

| Practice | Why |
|---|---|
| Always use HTTPS in production | Prevents token interception |
| Short JWT expiry (15–60 min) | Limits exposure if a token is stolen |
| Store API keys hashed | Same principle as passwords |
| Return JSON, not HTML, for API errors | Clients can parse and handle errors |
| Never expose stack traces in responses | Avoids information leakage |
| Log auth failures (but not tokens) | Supports incident response |
| Use `Content-Type: application/json` on error responses | Prevents content-type sniffing |

---

## Project Setup

This exercise has a provided `pom.xml`. You do not need to change it.

The project structure is:

```
src/
 └─ main/java/com/jwt/security/
     ├─ CustomAuthEntryPoint.java
     ├─ CustomAccessDeniedHandler.java
     ├─ ApiKeyFilter.java
     ├─ RateLimitFilter.java
     ├─ SecureController.java
     └─ SecurityConfig.java
 └─ test/java/com/jwt/security/
     └─ SecurityBestPracticesTest.java
```

---

## Tasks

### Task 1 – Custom 401 JSON Response

Open `CustomAuthEntryPoint.java`. Complete the `commence()` method:

1. Set the response content type to `"application/json"`
2. Set the HTTP status to `401`
3. Write this JSON body to the response writer:
   ```json
   {"error":"Unauthorized","message":"Authentication required"}
   ```

### Task 2 – Custom 403 JSON Response

Open `CustomAccessDeniedHandler.java`. Complete the `handle()` method:

1. Set the response content type to `"application/json"`
2. Set the HTTP status to `403`
3. Write this JSON body:
   ```json
   {"error":"Forbidden","message":"Access denied"}
   ```

### Task 3 – API Key Filter

Open `ApiKeyFilter.java`. The filter should:

1. Read the `X-API-Key` header from the request
2. Check if it exists in `VALID_API_KEYS` (a `Set<String>`)
3. If **valid** – create a `UsernamePasswordAuthenticationToken` with principal `"api-client"`, no credentials, and authority `ROLE_API_CLIENT`, then set it in `SecurityContextHolder`
4. If **missing or invalid** – do NOT stop the chain; let downstream filters decide (the missing authentication will trigger the `AuthenticationEntryPoint`)
5. Always call `filterChain.doFilter()`

### Task 4 – Rate Limit Filter

Open `RateLimitFilter.java`. The filter should:

1. Get the client IP from `request.getRemoteAddr()`
2. Increment a counter for that IP using `requestCounts.computeIfAbsent(ip, k -> new AtomicInteger()).incrementAndGet()`
3. If the count exceeds `MAX_REQUESTS_PER_WINDOW` (set to 5 for testing):
   - Set status to `429`
   - Set content type to `"application/json"`
   - Write `{"error":"Too Many Requests","message":"Rate limit exceeded"}`
   - Return **without** calling `filterChain.doFilter()`
4. Otherwise call `filterChain.doFilter()`

### Task 5 – Wire Exception Handlers in SecurityConfig

Open `SecurityConfig.java`. Inside the `securityFilterChain` method, add:

```java
.exceptionHandling(ex -> ex
    .authenticationEntryPoint(authEntryPoint)
    .accessDeniedHandler(accessDeniedHandler))
```

Also add both custom filters before `UsernamePasswordAuthenticationFilter`.

---

## Running the Tests

```bash
mvn test
```

All 6 tests in `SecurityBestPracticesTest` should pass:

| Test | Expected |
|---|---|
| `noToken_returns401JsonBody` | 401 + JSON `{"error":"Unauthorized",...}` |
| `wrongRole_returns403JsonBody` | 403 + JSON `{"error":"Forbidden",...}` |
| `validApiKey_returns200` | 200 OK |
| `invalidApiKey_returns401` | 401 + JSON body |
| `rateLimitExceeded_returns429` | 429 + JSON body |
| `adminEndpoint_withAdminToken_returns200` | 200 OK |

---

## Key Classes Reference

| Class / Annotation | Package | Purpose |
|---|---|---|
| `AuthenticationEntryPoint` | `o.s.s.web` | Handles missing/invalid auth (401) |
| `AccessDeniedHandler` | `o.s.s.web.access` | Handles insufficient role (403) |
| `OncePerRequestFilter` | `o.s.w.filter` | Base class for custom filters |
| `HttpServletResponse.SC_UNAUTHORIZED` | `jakarta.servlet.http` | Constant `401` |
| `HttpServletResponse.SC_FORBIDDEN` | `jakarta.servlet.http` | Constant `403` |
| `ConcurrentHashMap` | `java.util.concurrent` | Thread-safe map |
| `AtomicInteger` | `java.util.concurrent.atomic` | Thread-safe counter |

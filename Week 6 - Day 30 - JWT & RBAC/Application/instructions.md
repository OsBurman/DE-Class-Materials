# Day 30 Application — JWT & RBAC: JWT Authentication System

## Overview

Add **JWT-based authentication** to your Spring Boot API with role-based access control, a custom authentication filter, and refresh token support.

---

## Learning Goals

- Understand JWT structure (header, payload, signature)
- Generate and validate JWTs using `jjwt`
- Build a custom `OncePerRequestFilter` for JWT auth
- Implement login → JWT issue → protected request flow
- Apply RBAC with `@PreAuthorize` and `@Secured`
- Handle token expiry and refresh

---

## Project Structure

```
src/main/java/com/academy/auth/
├── config/
│   └── SecurityConfig.java          ← TODO: stateless JWT config
├── controller/
│   └── AuthController.java          ← TODO: /auth/login, /auth/refresh
├── filter/
│   └── JwtAuthenticationFilter.java ← TODO: OncePerRequestFilter
├── model/
│   ├── User.java                    ← provided
│   └── Role.java (enum)             ← provided
├── service/
│   ├── UserDetailsServiceImpl.java  ← TODO
│   └── JwtService.java              ← TODO
└── dto/
    ├── LoginRequestDto.java
    └── LoginResponseDto.java        ← includes accessToken, refreshToken, expiresIn
```

---

## Part 1 — `JwtService`

**Task 1**  
Use `io.jsonwebtoken:jjwt-api` (included in pom.xml).
```java
@Service
public class JwtService {
    @Value("${jwt.secret}") private String secret;
    @Value("${jwt.expiration-ms}") private long expirationMs;

    public String generateToken(UserDetails userDetails) { ... }
    public String generateRefreshToken(UserDetails userDetails) { ... }
    public String extractUsername(String token) { ... }
    public boolean isTokenValid(String token, UserDetails userDetails) { ... }
    private boolean isTokenExpired(String token) { ... }
    private Claims extractAllClaims(String token) { ... }
}
```

---

## Part 2 — JWT Filter

**Task 2 — `JwtAuthenticationFilter`**  
Extends `OncePerRequestFilter`.
```java
@Override
protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) {
    // 1. Extract "Authorization" header
    // 2. Check for "Bearer " prefix
    // 3. Extract and validate JWT
    // 4. If valid, create UsernamePasswordAuthenticationToken
    // 5. Set in SecurityContextHolder
    // 6. chain.doFilter(request, response)
}
```

---

## Part 3 — Security Config (Stateless)

**Task 3**  
```java
http
    .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
    .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
    .authorizeHttpRequests(auth -> auth
        .requestMatchers("/auth/**").permitAll()
        .requestMatchers("/api/admin/**").hasRole("ADMIN")
        .anyRequest().authenticated()
    )
```

---

## Part 4 — Auth Controller

**Task 4**  
`POST /auth/login` — validate credentials, return `LoginResponseDto` with tokens.  
`POST /auth/refresh` — accept refreshToken in body, validate, return new accessToken.  
`POST /auth/logout` — invalidate token (add to a blacklist `Set<String>`).

---

## Part 5 — RBAC

**Task 5**  
Create 3 test users: `admin/admin123 (ROLE_ADMIN)`, `user/user123 (ROLE_USER)`, `viewer/viewer123 (ROLE_VIEWER)`.  
Apply: `@PreAuthorize("hasRole('ADMIN')")` on admin endpoints, `@Secured("ROLE_USER")` on user endpoints.

---

## Part 6 — JWT Tests

**Task 6**  
- Test: login returns 200 + valid JWT
- Test: protected endpoint with valid JWT returns 200
- Test: protected endpoint without JWT returns 401
- Test: admin endpoint with USER role returns 403
- Test: expired JWT returns 401

---

## Submission Checklist

- [ ] `JwtService` generates, validates, and extracts claims from JWTs
- [ ] `JwtAuthenticationFilter` intercepts all requests
- [ ] Security is stateless (no sessions)
- [ ] `/auth/login` returns tokens
- [ ] `/auth/refresh` issues new access token
- [ ] RBAC: ADMIN, USER, VIEWER roles enforced
- [ ] All 5 JWT tests passing

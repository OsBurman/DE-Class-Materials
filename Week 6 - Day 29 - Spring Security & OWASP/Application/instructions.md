# Day 29 Application — Spring Security & OWASP: Securing a REST API

## Overview

Add **Spring Security** to an existing REST API — implementing in-memory authentication, BCrypt password encoding, SecurityFilterChain configuration, CSRF handling, and OWASP-aware input validation.

---

## Learning Goals

- Understand the OWASP Top 10
- Configure Spring Security with `SecurityFilterChain`
- Implement in-memory and `UserDetailsService` authentication
- Encode passwords with `BCryptPasswordEncoder`
- Configure CORS and CSRF
- Use method-level security (`@PreAuthorize`)
- Write security integration tests

---

## Prerequisites

- Builds on the Day 25 Task Management API
- `mvn spring-boot:run` → `http://localhost:8080`

---

## Part 1 — OWASP Awareness

**Task 1 — `owasp-notes.md`**  
For each OWASP Top 10 item, write 1–2 sentences explaining:
1. What it is
2. How Spring Security helps mitigate it (or what other control is needed)

Minimum: cover A01 (Broken Access Control), A03 (Injection), A07 (Auth Failures).

---

## Part 2 — Security Configuration

**Task 2 — `SecurityConfig.java`**  
`@Configuration @EnableWebSecurity @EnableMethodSecurity`
```java
@Bean
public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    return http
        .csrf(csrf -> csrf.disable())  // explain why in a comment
        .cors(cors -> cors.configurationSource(corsConfigurationSource()))
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/api/public/**").permitAll()
            .requestMatchers("/actuator/health").permitAll()
            .requestMatchers(HttpMethod.POST, "/api/tasks").hasRole("USER")
            .requestMatchers("/api/admin/**").hasRole("ADMIN")
            .anyRequest().authenticated()
        )
        .httpBasic(Customizer.withDefaults())
        .build();
}
```

**Task 3 — `UserDetailsServiceImpl`**  
Implement `UserDetailsService`. Load users from a `Map<String, UserDto>` (simulate a DB). Return `User.builder().username(...).password(encoded).roles(...).build()`.

**Task 4 — `BCryptPasswordEncoder`**  
Declare as a `@Bean`. Use it to encode passwords in `UserDetailsServiceImpl`.

**Task 5 — CORS Configuration**  
`@Bean CorsConfigurationSource`: allow `http://localhost:3000`, methods GET/POST/PUT/DELETE, all headers.

---

## Part 3 — Method-Level Security

**Task 6 — `@PreAuthorize`**  
In `TaskController`:
- `@PreAuthorize("hasRole('ADMIN')")` on `deleteTask()`
- `@PreAuthorize("hasRole('USER') or hasRole('ADMIN')")` on `createTask()`

---

## Part 4 — Input Sanitization (OWASP A03)

**Task 7**  
Add a `@Component SanitizationUtil` that uses Apache Commons Text `StringEscapeUtils.escapeHtml4()` to sanitize all string fields before saving. Call it in the service layer.

---

## Part 5 — Security Tests

**Task 8**  
```java
@SpringBootTest
@AutoConfigureMockMvc
class SecurityTest {
    @Test void accessProtectedEndpoint_withoutAuth_shouldReturn401() { ... }
    @Test void accessProtectedEndpoint_withAuth_shouldReturn200() { ... }
    @Test void deleteTask_withUserRole_shouldReturn403() { ... }
    @Test void deleteTask_withAdminRole_shouldReturn204() { ... }
}
```
Use `mockMvc.perform(get("/api/tasks").with(httpBasic("user","password")))`.

---

## Submission Checklist

- [ ] `SecurityFilterChain` configured with correct access rules
- [ ] BCryptPasswordEncoder used (never plain text passwords)
- [ ] `UserDetailsService` implemented
- [ ] CORS configured for localhost:3000
- [ ] `@PreAuthorize` on at least 2 methods
- [ ] `owasp-notes.md` covers at least 3 OWASP items
- [ ] 4 security tests passing

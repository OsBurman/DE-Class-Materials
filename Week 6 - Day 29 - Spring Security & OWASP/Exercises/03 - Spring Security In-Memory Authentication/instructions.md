# Exercise 03: Spring Security In-Memory Authentication

## Objective

Configure Spring Security from scratch using `SecurityFilterChain`, in-memory users, and `BCryptPasswordEncoder` to protect a Spring Boot REST API.

## Background

Spring Security is the standard security framework for the Spring ecosystem. Its core model is a chain of servlet filters — `SecurityFilterChain` — that intercepts every HTTP request and applies authentication and authorization rules before the request reaches a controller. This exercise builds a minimal but complete security configuration using in-memory users (a good starting point before moving to a database in Ex 04).

## Requirements

1. The project contains `BookController` with three endpoints:
   - `GET /public/books` — publicly accessible (no authentication required)
   - `GET /books` — requires any authenticated user
   - `POST /books` — requires the `ADMIN` role
2. In `SecurityConfig.java` (starter-code):
   - Annotate the class with `@Configuration` and `@EnableWebSecurity`.
   - Define a `SecurityFilterChain` bean that:
     - Disables the default `formLogin` and `httpBasic` (use `.formLogin(AbstractHttpConfigurer::disable)` and `.httpBasic(AbstractHttpConfigurer::disable)`).
     - Permits all requests to `/public/**` without authentication.
     - Requires authentication for `GET /books`.
     - Requires the `ADMIN` role for `POST /books`.
     - Returns 401 for unauthenticated requests and 403 for unauthorised ones.
   - Define a `UserDetailsService` bean that creates two in-memory users:
     - `user` with password `password` and role `USER`
     - `admin` with password `admin123` and role `ADMIN`
   - Define a `PasswordEncoder` bean that returns a `BCryptPasswordEncoder`.
3. Run `SecurityConfigTest` to verify all three endpoint access rules work correctly.

## Hints

- Use `InMemoryUserDetailsManager` to build users with `User.withUsername(...).password(...).roles(...).build()`.
- Passwords stored in `InMemoryUserDetailsManager` must be encoded — pass them through `passwordEncoder().encode("raw")` when building.
- `requestMatchers(HttpMethod.POST, "/books").hasRole("ADMIN")` is the pattern for method-specific rules.
- Put the most specific rules first inside `authorizeHttpRequests`; `.anyRequest().authenticated()` should be last.

## Expected Output

When the test suite runs:

```
GET  /public/books → 200 OK  (no credentials)
GET  /books        → 200 OK  (user:password)
GET  /books        → 401     (no credentials)
POST /books        → 201     (admin:admin123)
POST /books        → 403     (user:password — no ADMIN role)
```

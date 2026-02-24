# Exercise 03: JWT Authentication Filter

## Objective
Build a custom Spring Security filter that intercepts every HTTP request, extracts a Bearer JWT from the `Authorization` header, validates it, and sets the authenticated user in the `SecurityContext`.

## Background
Spring Security processes requests through a chain of filters.
A `OncePerRequestFilter` is guaranteed to run exactly once per request.
Our `JwtAuthenticationFilter` sits in the chain *before* Spring's authorization rules are applied.
If a valid token is found, the filter creates an `Authentication` object and stores it in `SecurityContextHolder` so the rest of the security chain sees an authenticated user.

## Requirements

1. **`JwtAuthenticationFilter.java`** — extend `OncePerRequestFilter` and implement `doFilterInternal`:
   - Extract the `Authorization` header.
   - If the header is present and starts with `"Bearer "`, strip the prefix to get the raw token.
   - Call `jwtUtil.validateToken(token)`. If invalid, let the request continue without setting authentication (the authorization rules will then reject it).
   - If valid, extract the username and role via `JwtUtil`.
   - Build a `UsernamePasswordAuthenticationToken` with:
     - principal = username
     - credentials = `null`
     - authorities = `List.of(new SimpleGrantedAuthority("ROLE_" + role))`
   - Set the token's details: `authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request))`
   - Store it in `SecurityContextHolder.getContext().setAuthentication(authToken)`.
   - Always call `filterChain.doFilter(request, response)` to pass the request along.

2. **`JwtFilterTest.java`** — five MockMvc tests covering:
   - Request with no Authorization header → `401`
   - Request with an invalid token → `401`
   - Request with a valid USER token → `200` on `GET /api/hello`
   - Request with a valid ADMIN token → `200` on `GET /api/admin`
   - Request with a USER token → `403` on `GET /api/admin`

3. **`SecurityConfig.java`** (skeleton provided) — wire the `JwtAuthenticationFilter` before `UsernamePasswordAuthenticationFilter` using `http.addFilterBefore(...)`.

## Hints
- `UsernamePasswordAuthenticationToken(principal, credentials, authorities)` creates an already-authenticated token (the three-arg constructor sets `authenticated = true`).
- Use `@Autowired` or constructor injection to get `JwtUtil` into the filter.
- In `SecurityConfig`, use `.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)`.
- `SecurityMockMvcRequestPostProcessors.httpBasic()` is NOT used here — instead use `.header("Authorization", "Bearer " + token)` in tests.

## Expected Output

All 5 tests pass:
```
[INFO] Tests run: 5, Failures: 0, Errors: 0, Skipped: 0
```

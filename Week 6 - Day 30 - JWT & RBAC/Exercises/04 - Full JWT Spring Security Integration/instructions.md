# Exercise 04: Full JWT Spring Security Integration

## Objective
Build a complete JWT-secured Spring Boot REST API with a `/auth/login` endpoint that issues tokens and a filter that validates them on every subsequent request.

## Background
Exercises 02 and 03 built the individual pieces: `JwtUtil` for token management and `JwtAuthenticationFilter` for validation.
This exercise connects them into a full authentication flow:
1. Client POSTs credentials to `/auth/login` → receives a JWT.
2. Client includes `Authorization: Bearer <token>` on every protected request.
3. The filter validates the token and populates the `SecurityContext`.
4. Spring Security applies role-based authorization rules.

## Requirements

1. **`AuthController.java`** — implement `POST /auth/login`:
   - Accept a JSON body `{ "username": "...", "password": "..." }` via `LoginRequest`.
   - Validate credentials against an `InMemoryUserDetailsManager` (injected via `AuthenticationManager`).
   - If credentials are valid, generate a JWT (using `JwtUtil`) and return `{ "token": "..." }` in the response body.
   - If credentials are invalid, return `401 Unauthorized`.

2. **`SecurityConfig.java`** — complete the configuration:
   - Expose an `AuthenticationManager` bean.
   - Permit `POST /auth/login` without authentication.
   - Protect `GET /api/books` for any authenticated user.
   - Protect `POST /api/books` for `ADMIN` role only.
   - Register `JwtAuthenticationFilter` before `UsernamePasswordAuthenticationFilter`.
   - Session policy: `STATELESS`.

3. **`BookController.java`** — `GET /api/books` returns a list, `POST /api/books` adds a book (201).

4. **`JwtUtil.java`** — copy from Exercise 02 (already provided, no changes needed).

5. **`JwtAuthenticationFilter.java`** — copy from Exercise 03 (already provided, no changes needed).

6. **`IntegrationTest.java`** — seven tests must pass:
   - `POST /auth/login` with valid credentials → 200 with a `token` field in response body.
   - `POST /auth/login` with wrong password → 401.
   - `GET /api/books` without token → 401.
   - `GET /api/books` with USER token → 200.
   - `POST /api/books` with USER token → 403.
   - `POST /api/books` with ADMIN token → 201.
   - Token from `/auth/login` can be used to access `/api/books`.

## Hints
- `AuthenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password))` throws `BadCredentialsException` if credentials are wrong.
- Expose `AuthenticationManager` as a bean via `authenticationConfiguration.getAuthenticationManager()`.
- `UserDetails` from `InMemoryUserDetailsManager` uses `{noop}` prefix for plaintext passwords in tests, or `BCryptPasswordEncoder` for production.
- In tests, parse the response JSON to extract the `token` field: `response.andReturn().getResponse().getContentAsString()`.

## Expected Output

All 7 tests pass:
```
[INFO] Tests run: 7, Failures: 0, Errors: 0, Skipped: 0
```

Manual cURL flow:
```bash
# Step 1: Login
curl -X POST http://localhost:8080/auth/login \
     -H "Content-Type: application/json" \
     -d '{"username":"user","password":"password"}'
# → {"token":"eyJhbGciOiJIUzI1NiJ9..."}

# Step 2: Use token
curl http://localhost:8080/api/books \
     -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9..."
# → ["Clean Code","Effective Java"]
```

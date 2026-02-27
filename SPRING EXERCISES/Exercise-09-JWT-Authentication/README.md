# Exercise 09 — JWT Authentication

## 🎯 Learning Objectives
By the end of this exercise you will be able to:
- Generate and validate JWT (JSON Web Tokens) in Spring Boot
- Build a stateless authentication flow: `POST /auth/login` → JWT → protected endpoints
- Implement a `JwtAuthenticationFilter` extending `OncePerRequestFilter`
- Configure Spring Security to use JWT instead of Basic Auth
- Use `@PreAuthorize` for method-level security
- Understand JWT structure: header.payload.signature

---

## 📋 What You're Building
A **JWT-secured Task API** — users login to receive a token, then use it to access their tasks.

### Endpoints
| Method | Path | Auth | Description |
|--------|------|------|-------------|
| `POST` | `/api/auth/register` | Public | Register a new user |
| `POST` | `/api/auth/login` | Public | Login and receive JWT |
| `GET` | `/api/tasks` | Bearer JWT | Get current user's tasks |
| `POST` | `/api/tasks` | Bearer JWT | Create a task |
| `PUT` | `/api/tasks/{id}` | Bearer JWT | Update task |
| `DELETE` | `/api/tasks/{id}` | Bearer JWT | Delete task |
| `GET` | `/api/admin/tasks` | Bearer JWT (ADMIN) | Get all tasks |

### Auth Flow
```
1. POST /api/auth/login  {"username": "alice", "password": "Secret1!"}
   → 200 {"token": "eyJhbGciOiJIUzI1NiJ9..."}

2. GET /api/tasks
   Header: Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
   → 200 [{...tasks...}]
```

---

## 🏗️ Project Setup
```bash
cd Exercise-09-JWT-Authentication/starter-code
./mvnw spring-boot:run
```

---

## 📁 File Structure
```
src/main/java/com/exercise/jwtauth/
├── JwtAuthApplication.java
├── config/
│   ├── SecurityConfig.java             ← ⭐ JWT security chain
│   └── JwtProperties.java              ← JWT secret & expiration config
├── jwt/
│   ├── JwtUtil.java                    ← ⭐ Token generation & validation
│   └── JwtAuthenticationFilter.java   ← ⭐ OncePerRequestFilter
├── entity/
│   ├── User.java
│   └── Task.java
├── dto/
│   ├── RegisterRequest.java
│   ├── LoginRequest.java
│   ├── AuthResponse.java               ← Returns JWT token
│   ├── TaskRequest.java
│   └── TaskResponse.java
├── exception/
│   ├── ErrorResponse.java
│   ├── ResourceNotFoundException.java
│   └── GlobalExceptionHandler.java
├── repository/
│   ├── UserRepository.java
│   └── TaskRepository.java
├── service/
│   ├── UserDetailsServiceImpl.java     ← ⭐ Loads user from DB for Spring Security
│   ├── AuthService.java                ← ⭐ Register/login logic
│   └── TaskService.java
└── controller/
    ├── AuthController.java
    ├── TaskController.java
    └── AdminController.java
```

---

## ✅ TODOs

### `jwt/JwtUtil.java`
- [ ] **TODO 1**: Implement `generateToken(String username, String role)`:
  - Use `Jwts.builder()` with `subject`, `claim("role", role)`, `issuedAt(new Date())`, `expiration(...)`, and sign with `Keys.hmacShaKeyFor(secret.getBytes())`
- [ ] **TODO 2**: Implement `extractUsername(String token)`:
  - Parse the token, get `Claims`, return `claims.getSubject()`
- [ ] **TODO 3**: Implement `isTokenValid(String token)`:
  - Parse it; if it throws any exception (expired, malformed) return false; otherwise return true

### `jwt/JwtAuthenticationFilter.java`
- [ ] **TODO 4**: Extend `OncePerRequestFilter` (ensures filter runs once per request)
- [ ] **TODO 5**: Implement `doFilterInternal`:
  - Extract the `Authorization` header; if it starts with `"Bearer "`, get the token
  - Call `jwtUtil.extractUsername(token)` and `jwtUtil.isTokenValid(token)`
  - If valid and `SecurityContextHolder` has no auth yet:
    - Load the `UserDetails` via `userDetailsService.loadUserByUsername(username)`
    - Create `UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities())`
    - Set it on `SecurityContextHolder.getContext().setAuthentication(auth)`
  - Always call `filterChain.doFilter(request, response)` at the end

### `config/SecurityConfig.java`
- [ ] **TODO 6**: Configure `SecurityFilterChain`:
  - Disable CSRF, set STATELESS sessions
  - Permit `/api/auth/**` (public), restrict `/api/admin/**` to ADMIN, require auth for rest
  - Add `jwtAuthenticationFilter` BEFORE `UsernamePasswordAuthenticationFilter`
- [ ] **TODO 7**: Create an `AuthenticationManager` bean using `AuthenticationConfiguration`

### `service/AuthService.java`
- [ ] **TODO 8**: Implement `register(RegisterRequest request)`:
  - Check for duplicate username/email
  - Encode password with `passwordEncoder`
  - Save user, return `AuthResponse` with generated JWT
- [ ] **TODO 9**: Implement `login(LoginRequest request)`:
  - Call `authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password))`
  - If successful, generate JWT and return `AuthResponse`

---

## 💡 Key Concepts

### JWT Structure
```
eyJhbGciOiJIUzI1NiJ9      ← Header (algorithm)
.eyJzdWIiOiJ1c2VyIn0      ← Payload (claims: sub, exp, iat, custom)
.SflKxwRJSMeKKF2QT4fw     ← Signature (HMAC-SHA256 of header+payload)
```

### Why Stateless?
With JWT, the server stores NO session. Every request is self-contained:
```
Client → [request + JWT] → Server validates JWT → processes request
```
This scales horizontally — any server can validate any JWT.

### OncePerRequestFilter
```java
@Component
public class JwtFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws IOException, ServletException {
        // Your logic here
        chain.doFilter(request, response); // Must call this!
    }
}
```

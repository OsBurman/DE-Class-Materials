# Exercise 08 — Spring Security

## 🎯 Learning Objectives
By the end of this exercise you will be able to:
- Add `spring-boot-starter-security` to secure a Spring Boot application
- Configure a `SecurityFilterChain` using the modern lambda DSL
- Set up in-memory users with `InMemoryUserDetailsManager` and `BCryptPasswordEncoder`
- Use HTTP Basic authentication for API endpoints
- Restrict endpoints by role using `hasRole()` and `hasAnyRole()`
- Understand the Spring Security request processing pipeline
- Configure CORS and CSRF appropriately for REST APIs

---

## 📋 What You're Building
A **Secured Notes API** — notes that only authenticated users can access.

### Endpoints
| Method | Path | Required Role | Description |
|--------|------|--------------|-------------|
| `GET` | `/api/notes` | USER or ADMIN | List all notes for current user |
| `POST` | `/api/notes` | USER or ADMIN | Create a new note |
| `GET` | `/api/notes/{id}` | USER or ADMIN | Get a note by ID |
| `PUT` | `/api/notes/{id}` | USER or ADMIN | Update a note |
| `DELETE` | `/api/notes/{id}` | USER or ADMIN | Delete a note |
| `GET` | `/api/admin/users` | ADMIN only | List all users (admin only) |
| `GET` | `/actuator/health` | Public | Health check (no auth) |

### Test Users (in-memory)
| Username | Password | Role |
|----------|----------|------|
| `user` | `Password1!` | USER |
| `admin` | `Admin123!` | ADMIN |

---

## 🏗️ Project Setup
```bash
cd Exercise-08-Spring-Security/starter-code
./mvnw spring-boot:run
```
Test with Basic Auth:
```bash
curl -u user:Password1! http://localhost:8080/api/notes
```

---

## 📁 File Structure
```
src/main/java/com/exercise/securednotes/
├── SecuredNotesApplication.java
├── config/
│   └── SecurityConfig.java             ← ⭐ SecurityFilterChain configuration
├── entity/
│   └── Note.java
├── dto/
│   ├── NoteRequest.java
│   └── NoteResponse.java
├── exception/
│   ├── ErrorResponse.java
│   ├── ResourceNotFoundException.java
│   └── GlobalExceptionHandler.java
├── repository/
│   └── NoteRepository.java
├── service/
│   └── NoteService.java
└── controller/
    ├── NoteController.java
    └── AdminController.java
```

---

## ✅ TODOs

### `config/SecurityConfig.java`
- [ ] **TODO 1**: Annotate the class with `@Configuration` and `@EnableWebSecurity`
- [ ] **TODO 2**: Create a `SecurityFilterChain` bean:
  - Disable CSRF (`.csrf(csrf -> csrf.disable())`)
  - Set session management to `STATELESS`: `.sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))`
  - Configure authorization rules:
    - `/actuator/**` → `permitAll()`
    - `/api/admin/**` → `hasRole("ADMIN")`
    - Any other request → `authenticated()`
  - Enable HTTP Basic: `.httpBasic(Customizer.withDefaults())`
- [ ] **TODO 3**: Create a `UserDetailsService` bean using `InMemoryUserDetailsManager`:
  - Create a USER with username `"user"`, encoded password, role `"USER"`
  - Create an ADMIN with username `"admin"`, encoded password, role `"ADMIN"`
  - Use `User.withUsername(...).password(passwordEncoder().encode("Password1!")).roles("USER").build()`
- [ ] **TODO 4**: Create a `PasswordEncoder` bean: return `new BCryptPasswordEncoder()`

### `controller/NoteController.java`
- [ ] **TODO 5**: Inject `Principal` (from `java.security`) into methods that need the current user:
  - The `Principal` object represents the authenticated user; `principal.getName()` returns the username
  - Use it in `getNotes()` and `createNote()` to scope notes to the current user

### `controller/AdminController.java`
- [ ] **TODO 6**: Create an admin-only endpoint that returns all notes (using service)
  - Spring Security will enforce the ADMIN role based on the SecurityFilterChain config

---

## 💡 Key Concepts

### Spring Security Filter Chain
```
HTTP Request → Security Filters → DispatcherServlet → Controller
              (authentication,
               authorization,
               CSRF, CORS,
               session mgmt)
```

### SecurityFilterChain Configuration (Lambda DSL)
```java
@Bean
public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
        .csrf(csrf -> csrf.disable())
        .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/public/**").permitAll()
            .requestMatchers("/admin/**").hasRole("ADMIN")
            .anyRequest().authenticated()
        )
        .httpBasic(Customizer.withDefaults());
    return http.build();
}
```

### Roles vs Authorities
- `hasRole("ADMIN")` checks for granted authority `ROLE_ADMIN`
- `hasAuthority("ROLE_ADMIN")` — same check, explicit prefix
- Spring automatically prefixes roles with `ROLE_` when using `.roles("ADMIN")`

### Accessing Current User
```java
// Option 1: inject Principal
@GetMapping
public List<Note> getNotes(Principal principal) {
    String username = principal.getName();
    ...
}
// Option 2: use SecurityContextHolder
Authentication auth = SecurityContextHolder.getContext().getAuthentication();
String username = auth.getName();
```

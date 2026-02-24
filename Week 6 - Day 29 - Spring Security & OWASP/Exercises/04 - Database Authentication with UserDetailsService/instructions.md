# Exercise 04: Database Authentication with UserDetailsService

## Objective

Replace in-memory users with a database-backed authentication mechanism by implementing `UserDetailsService` and wiring it into Spring Security's `AuthenticationProvider`.

## Background

Production applications store users in a database, not in memory. Spring Security's `UserDetailsService` interface has a single method — `loadUserByUsername(String username)` — that Spring calls during authentication. You implement this interface to load user data from your repository, and Spring takes care of the rest (password comparison, authority mapping, session management).

## Requirements

1. The project provides `AppUser` (JPA entity), `AppUserRepository` (Spring Data JPA), and `BookController`. Do NOT modify these.
2. In `CustomUserDetailsService.java` (starter-code):
   - Implement the `UserDetailsService` interface.
   - In `loadUserByUsername(String username)`:
     - Call `userRepository.findByUsername(username)`.
     - If not found, throw `UsernameNotFoundException("User not found: " + username)`.
     - Build and return a Spring Security `User` object using `User.withUsername(...).password(...).roles(...).build()` from the `AppUser` fields.
3. In `SecurityConfig.java` (starter-code):
   - Annotate with `@Configuration` and `@EnableWebSecurity`.
   - Wire `CustomUserDetailsService` and `BCryptPasswordEncoder` into a `DaoAuthenticationProvider`.
   - Register the provider via `AuthenticationManagerBuilder` (or via `authenticationProvider(provider)` on `HttpSecurity`).
   - Keep the same URL rules as Ex 03: `/public/**` open, `GET /books` authenticated, `POST /books` ADMIN only.
4. In `DataInitializer.java` (starter-code), the `CommandLineRunner` bean seeds two users. Encode passwords with `BCryptPasswordEncoder` before saving.
5. Run `DatabaseAuthTest` and confirm all tests pass.

## Hints

- `UserDetailsService` has only one method: `UserDetails loadUserByUsername(String username) throws UsernameNotFoundException`.
- Build the `UserDetails` object with `User.withUsername(appUser.getUsername()).password(appUser.getPassword()).roles(appUser.getRole()).build()`.
- The password stored in `AppUser.password` must already be BCrypt-encoded (done in `DataInitializer`).
- Wire your custom service into Spring Security via `http.authenticationProvider(daoAuthProvider)`.

## Expected Output

```
GET  /books → 401  (no credentials)
GET  /books → 200  (user:password — USER loaded from DB)
POST /books → 201  (admin:admin123 — ADMIN loaded from DB)
POST /books → 403  (user:password)
```

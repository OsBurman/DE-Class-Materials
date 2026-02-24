# Exercise 05: RBAC with Method-Level Security

## Objective
Apply Role-Based Access Control (RBAC) using Spring Security's method-level annotations (`@PreAuthorize`, `@Secured`) and inspect the authenticated user's identity via `SecurityContextHolder`.

## Background
URL-based authorization rules (`requestMatchers()`) protect endpoints at the web layer.
Method-level security adds a second layer by protecting individual service methods regardless of how they are called.
This is especially useful when the same service method is invoked from multiple controllers, scheduled tasks, or message listeners.

## Requirements

1. **Enable method security** — add `@EnableMethodSecurity` to `SecurityConfig`.

2. **`LibraryService.java`** — implement four methods with appropriate security annotations:
   - `listAllBooks()` — any authenticated user may call this. Use `@PreAuthorize("isAuthenticated()")`.
   - `addBook(String title)` — only users with role `ADMIN` may call this. Use `@PreAuthorize("hasRole('ADMIN')")`.
   - `deleteBook(String title)` — only users with role `ADMIN` OR `LIBRARIAN`. Use `@Secured({"ROLE_ADMIN","ROLE_LIBRARIAN"})`.
   - `getCurrentUser()` — no annotation needed; implement the body to retrieve the currently authenticated username from `SecurityContextHolder.getContext().getAuthentication()`.

3. **`LibraryController.java`** — expose four endpoints that delegate to `LibraryService`:
   - `GET  /library/books`           → `listAllBooks()`
   - `POST /library/books`           → `addBook(title)`
   - `DELETE /library/books/{title}` → `deleteBook(title)`
   - `GET  /library/me`              → `getCurrentUser()`

4. **`RbacTest.java`** — eight tests must pass:
   - `listAllBooks` with USER → 200
   - `listAllBooks` without auth → 401
   - `addBook` with ADMIN → 201
   - `addBook` with USER → 403
   - `deleteBook` with LIBRARIAN → 200
   - `deleteBook` with USER → 403
   - `getCurrentUser` with USER token → 200 with body `"user"`
   - `getCurrentUser` without auth → 401

## Hints
- `@PreAuthorize` supports Spring Expression Language (SpEL): `hasRole('ADMIN')`, `hasAnyRole('ADMIN','LIBRARIAN')`, `#username == authentication.name`.
- `@Secured` takes `String[]` of full authority strings: `"ROLE_ADMIN"` (not just `"ADMIN"`).
- `SecurityContextHolder.getContext().getAuthentication().getName()` returns the currently authenticated principal's name.
- Spring Security throws `AccessDeniedException` when a method-level check fails; this results in a 403 response.

## Expected Output

All 8 tests pass:
```
[INFO] Tests run: 8, Failures: 0, Errors: 0, Skipped: 0
```

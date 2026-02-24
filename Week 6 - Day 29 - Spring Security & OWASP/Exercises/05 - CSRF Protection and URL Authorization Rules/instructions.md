# Exercise 05 – CSRF Protection and URL Authorization Rules

## Overview

This exercise explores two closely related Spring Security topics:

1. **CSRF (Cross-Site Request Forgery) protection** – when to enable it, when to disable it, and how it works.
2. **URL Authorization Rules** – using `requestMatchers()` to enforce fine-grained access control, and understanding the difference between *authentication* and *authorization*.

---

## Background

### Authentication vs Authorization

| Concept | Question answered | Spring Security mechanism |
|---|---|---|
| **Authentication** | *Who are you?* | `httpBasic()`, `formLogin()`, `UserDetailsService` |
| **Authorization** | *What are you allowed to do?* | `authorizeHttpRequests()`, `requestMatchers()`, `.hasRole()` |

Authentication always happens first.  Once Spring Security knows who the user is,
authorization rules decide whether they can access the requested resource.

### CSRF Protection

Cross-Site Request Forgery tricks an already-authenticated browser into making
an unwanted request to your server (e.g., a malicious site POSTing a form on
behalf of a logged-in user).

Spring Security defends against CSRF by requiring a secret **CSRF token** on every
state-changing request (POST, PUT, DELETE, PATCH).

**When to enable CSRF:**
- Server-side rendered HTML apps that use **session cookies** for authentication
  (e.g., Thymeleaf + form login).  The hidden `_csrf` field in the form ensures
  the request originated from your own page.

**When to disable CSRF:**
- **Stateless REST APIs** that authenticate with HTTP Basic or JWT.
  There is no session cookie, so there is nothing for an attacker to hijack.
  CSRF protection adds overhead with no security benefit.

### URL Authorization Rule Ordering

Rules are evaluated in **declaration order** – first match wins.  Always declare
the most specific rules first:

```java
.requestMatchers("/public/**").permitAll()          // most specific
.requestMatchers(HttpMethod.POST, "/books").hasRole("ADMIN")
.anyRequest().authenticated()                       // catch-all last
```

---

## Learning Objectives

- Configure Spring Security with CSRF **enabled** for a form-based application.
- Configure Spring Security with CSRF **disabled** for a stateless REST API.
- Chain multiple `requestMatchers()` rules correctly.
- Distinguish *authentication* (who) from *authorization* (what).
- Test CSRF behavior with `SecurityMockMvcRequestPostProcessors.csrf()`.

---

## Project Structure

```
05 - CSRF Protection and URL Authorization Rules/
├── instructions.md
├── starter-code/
│   ├── pom.xml
│   ├── BookController.java        – REST endpoints (already implemented)
│   ├── WebFormController.java     – HTML form endpoints (already implemented)
│   ├── SecurityConfig.java        – TODO: implement both REST and form configs
│   └── CsrfAndAuthorizationTest.java – tests to pass
└── solution/
    ├── pom.xml
    ├── BookController.java
    ├── WebFormController.java
    ├── SecurityConfig.java        – complete dual-config solution
    └── CsrfAndAuthorizationTest.java
```

---

## Tasks

### Task 1 – REST Security Config (CSRF disabled)

In `SecurityConfig.java`, complete the `restSecurityFilterChain()` method.

Requirements:
- CSRF **disabled** (stateless REST API – no session cookies).
- HTTP Basic authentication enabled.
- URL rules:
  - `GET /public/**` → `permitAll()`
  - `POST /books`, `PUT /books/**`, `DELETE /books/**` → `hasRole("ADMIN")`
  - All other requests → `authenticated()`
- Apply this chain only to paths matching `/books/**` and `/public/**`
  using `.securityMatcher(...)`.

### Task 2 – Form Security Config (CSRF enabled)

In `SecurityConfig.java`, complete the `formSecurityFilterChain()` method.

Requirements:
- CSRF **enabled** (default – `csrf(Customizer.withDefaults())`).
- Form login enabled.
- In-memory users:
  - `editor` / `editor123` / role `EDITOR`
  - `viewer` / `viewer123` / role `VIEWER`
- URL rules:
  - `GET /form/**` → `authenticated()` (any logged-in user)
  - `POST /form/submit` → `hasRole("EDITOR")` (write access)

### Task 3 – Study the tests

Read `CsrfAndAuthorizationTest.java` before running it.  Note how:
- `csrf()` post-processor injects a valid CSRF token into a MockMvc request.
- Omitting `csrf()` on a form POST results in a **403 Forbidden** response.
- REST POST **without** a CSRF token succeeds (CSRF is disabled for that chain).

Run the tests once you complete Tasks 1 and 2.

---

## Key Classes & Methods

| Class / Method | Purpose |
|---|---|
| `csrf(AbstractHttpConfigurer::disable)` | Disable CSRF (REST APIs) |
| `csrf(Customizer.withDefaults())` | Enable CSRF (form apps, default) |
| `SecurityMockMvcRequestPostProcessors.csrf()` | Inject CSRF token in MockMvc test |
| `requestMatchers(HttpMethod.POST, "/path")` | Match by method + path |
| `hasRole("ADMIN")` | Require ROLE_ADMIN authority |
| `hasAnyRole("ADMIN","USER")` | Require any of the listed roles |
| `authenticated()` | Any authenticated user |
| `permitAll()` | No authentication required |
| `http.securityMatcher(...)` | Restrict a filter chain to certain paths |

---

## Discussion Questions

1. Why does a REST API that uses JWT Bearer tokens **not** need CSRF protection?
2. What happens if you reverse the order of `requestMatchers` rules and put `anyRequest().authenticated()` first?
3. What is the difference between `.hasRole("ADMIN")` and `.hasAuthority("ROLE_ADMIN")`?
4. Could you have a single `SecurityFilterChain` that handles both REST and form-based auth? What trade-offs would there be?

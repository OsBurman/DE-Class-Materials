# Day 18b Application — Angular Routing & Forms: User Registration App

## Overview

You'll build a **User Registration & Profile App** — an Angular app with multiple routed views, a route guard protecting the profile page, lazy-loaded modules, and both template-driven and reactive form implementations.

---

## Learning Goals

- Configure Angular routing with multiple routes
- Use route parameters and query parameters
- Implement route guards (`CanActivate`)
- Set up lazy loading for a feature module
- Build template-driven forms
- Build reactive forms with `FormBuilder`
- Add built-in and custom validators

---

## Prerequisites

- `cd starter-code && npm install && npm run start`

---

## Project Structure

```
starter-code/
└── src/app/
    ├── app.module.ts
    ├── app-routing.module.ts      ← TODO: route config
    ├── app.component.ts/.html
    ├── guards/
    │   └── auth.guard.ts          ← TODO: CanActivate guard
    ├── services/
    │   └── auth.service.ts        ← provided (simple login state)
    └── pages/
        ├── home/                  ← provided
        ├── register/              ← TODO: reactive form
        ├── login/                 ← TODO: template-driven form
        └── profile/               ← TODO: guarded route
```

---

## Part 1 — Routing

**Task 1 — `app-routing.module.ts`**  
Define routes:
- `/` → `HomeComponent`
- `/login` → `LoginComponent`
- `/register` → `RegisterComponent`
- `/profile/:username` → `ProfileComponent` (guarded by `AuthGuard`)
- `**` → redirect to `/`

**Task 2 — Router outlet and navigation**  
Add `<router-outlet>` in `app.component.html`. Add `<nav>` with `routerLink` directives.

---

## Part 2 — Route Guard

**Task 3 — `AuthGuard`**  
Implement `CanActivate`. Inject `AuthService`. If `authService.isLoggedIn()` returns false, navigate to `/login` (pass `returnUrl` as query param) and return `false`.

---

## Part 3 — Template-Driven Form: `LoginComponent`

**Task 4**  
Build the login form using `NgModel` (template-driven):
- Email input with `required` and `email` validators
- Password input with `required` and `minlength="6"`
- Show inline error messages using `*ngIf` and template reference variables (`#emailField="ngModel"`)
- Disable submit button when form is invalid
- On submit: call `authService.login(email, password)`, navigate to profile

---

## Part 4 — Reactive Form: `RegisterComponent`

**Task 5 — FormBuilder**  
Inject `FormBuilder`. Build form:
```ts
this.form = this.fb.group({
  username: ['', [Validators.required, Validators.minLength(3)]],
  email:    ['', [Validators.required, Validators.email]],
  password: ['', [Validators.required, Validators.minLength(8)]],
  confirm:  ['', Validators.required],
}, { validators: this.passwordMatchValidator });
```

**Task 6 — Custom validator**  
`passwordMatchValidator(group: AbstractControl)`: return `{ passwordMismatch: true }` if `password !== confirm`, otherwise `null`.

**Task 7 — Error display**  
Show validation errors per field using `form.get('username')?.errors`.

---

## Part 5 — Profile Page

**Task 8 — Route parameter**  
Use `ActivatedRoute` to read `:username`. Display a profile card with the username. Add a Logout button that calls `authService.logout()` and navigates to `/`.

---

## Submission Checklist

- [ ] 4 routes configured with wildcard redirect
- [ ] `routerLink` used in navigation
- [ ] Route guard returns false and redirects to login
- [ ] Template-driven form with `NgModel` and inline errors
- [ ] Reactive form built with `FormBuilder`
- [ ] Custom cross-field validator implemented
- [ ] Route parameter read with `ActivatedRoute`
- [ ] Query parameter passed to login redirect

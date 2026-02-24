# Exercise 04 – CanActivate Route Guard

## Learning Objectives
- Create a **functional route guard** using `CanActivateFn`
- Use `inject()` inside a guard to access services and the `Router`
- Redirect unauthenticated users to a login page instead of blocking with `false`

## Background
Route guards run before Angular activates a route. `CanActivateFn` (introduced as the modern
functional API in Angular 14+) is a plain function — no class, no `implements` — that returns
`true`, `false`, or a `UrlTree` (redirect). Using a `UrlTree` is preferred over returning
`false` because it also updates the browser's URL to reflect the redirect.

## Exercise

You are protecting a `/dashboard` route. Users must be "logged in" (a flag on `AuthService`)
to view it; otherwise they are redirected to `/login`.

### Starter code TODOs

**`auth.service.ts`**
- TODO 1 – Add a public `isLoggedIn` boolean property (starts as `false`)
- TODO 2 – Add `login()` and `logout()` methods that toggle `isLoggedIn`

**`auth.guard.ts`**
- TODO 3 – Import `CanActivateFn`, `inject`, `Router` from `@angular/router` / `@angular/core`
- TODO 4 – Export a `const authGuard: CanActivateFn` that:
  - injects `AuthService` and `Router`
  - returns `true` if `authService.isLoggedIn`
  - otherwise returns `router.createUrlTree(['/login'])`

**`app.module.ts`**
- TODO 5 – Add `canActivate: [authGuard]` to the `/dashboard` route

## Files
```
starter-code/
  app.module.ts
  app.component.ts
  auth.service.ts
  auth.guard.ts
  dashboard.component.ts
  login.component.ts
solution/
  app.module.ts
  app.component.ts
  auth.service.ts
  auth.guard.ts
  dashboard.component.ts
  login.component.ts
```

## Expected Behaviour
1. Navigating to `/dashboard` without logging in redirects to `/login`.
2. Clicking "Log in" on the login page calls `authService.login()` and navigates to `/dashboard`.
3. Clicking "Log out" on the dashboard calls `authService.logout()`.
4. After logging out, navigating to `/dashboard` redirects back to `/login`.

# Exercise 01: Angular Router Basic Navigation

## Objective
Configure `RouterModule` with multiple routes, navigate between views using `routerLink`, and highlight the active link with `routerLinkActive`.

## Background
Angular's router maps URL paths to components, enabling single-page navigation without full page reloads. `RouterModule.forRoot()` registers routes at the application level, `<router-outlet>` is the placeholder where matched components render, and `routerLink` replaces plain `href` so navigation stays within the SPA.

## Requirements
1. In `app.module.ts`, import `RouterModule` and call `RouterModule.forRoot(routes)` with three routes:
   - `''` (empty path) → `HomeComponent`
   - `'about'` → `AboutComponent`
   - `'contact'` → `ContactComponent`
   - A wildcard `'**'` route → `NotFoundComponent`
2. In `app.component.html`, add a `<nav>` with three `[routerLink]` anchor elements: Home, About, Contact.
3. Apply `routerLinkActive="active"` to each link so the current route is visually distinguished.
4. Place `<router-outlet></router-outlet>` below the nav so matched components render there.
5. Each page component must render an `<h1>` with its page name (e.g. `<h1>Home</h1>`).
6. `NotFoundComponent` must render `<h1>404 - Page Not Found</h1>`.

## Hints
- `RouterModule.forRoot(routes)` takes an array of `{ path: string, component: ComponentClass }` objects.
- The wildcard route `{ path: '**', component: NotFoundComponent }` must be the **last** entry in the routes array.
- Use `routerLinkActive="active"` on the `<a>` tag (or its parent) — Angular toggles that CSS class when the route matches.
- Add `[routerLinkActiveOptions]="{ exact: true }"` on the Home link so `/about` doesn't also activate the `''` route.

## Expected Output

At `/` (Home):
```
[Home*] [About] [Contact]

Home
Welcome to the Home page!
```

At `/about`:
```
[Home] [About*] [Contact]

About
Learn more about us.
```

At `/unknown`:
```
[Home] [About] [Contact]

404 - Page Not Found
```

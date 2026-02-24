# Exercise 06 – Lazy Loading a Feature Module

## Learning Objectives
- Configure **lazy loading** with `loadChildren` and a dynamic `import()`
- Create a **feature `NgModule`** with its own `RouterModule.forChild()` route configuration
- Understand why lazy loading improves initial bundle size and load time

## Background
By default Angular eagerly loads all declared modules upfront. With **lazy loading** you tell
the router to fetch a feature module's bundle only when the user first navigates to that
route. The key syntax is:

```ts
{ path: 'reports', loadChildren: () => import('./reports/reports.module').then(m => m.ReportsModule) }
```

The feature module must **not** be imported in `AppModule`. It registers its own routes using
`RouterModule.forChild()` instead of `forRoot()`.

## Exercise

You are adding a "Reports" section to an app. The `ReportsModule` is lazy-loaded when the
user navigates to `/reports`.

### Starter code TODOs

**`app.module.ts`**
- TODO 1 – Add a lazy-loaded route: `{ path: 'reports', loadChildren: () => import('./reports/reports.module').then(m => m.ReportsModule) }`
- ⚠️  Do **not** import `ReportsModule` in `AppModule` — that would eager-load it

**`reports/reports-routing.module.ts`**
- TODO 2 – Import `ReportsHomeComponent`
- TODO 3 – Define a `routes` array: `{ path: '', component: ReportsHomeComponent }`
- TODO 4 – Export `RouterModule.forChild(routes)` in the `imports` array

**`reports/reports.module.ts`**
- TODO 5 – Import `ReportsRoutingModule` so the child routes are registered

## Files
```
starter-code/
  app.module.ts
  app.component.ts
  reports/
    reports.module.ts
    reports-routing.module.ts
    reports-home.component.ts
solution/
  app.module.ts
  app.component.ts
  reports/
    reports.module.ts
    reports-routing.module.ts
    reports-home.component.ts
```

## Expected Behaviour
1. App starts on the home page (fast — reports bundle is not loaded yet).
2. Clicking the "Reports" link triggers lazy loading; the browser fetches the reports chunk.
3. `/reports` renders `ReportsHomeComponent`.
4. In DevTools → Network you can observe the lazy chunk being fetched on first visit.

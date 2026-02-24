# Exercise 03 – Nested Routes

## Learning Objectives
- Configure **child routes** using the `children` property on a route definition
- Understand that a parent component must provide its own `<router-outlet>` to render child views
- Navigate to nested URLs like `/dashboard/analytics`

## Background
Nested routes let you embed a second level of routing inside a parent view (e.g. a side-nav
layout that switches its content area between Overview, Analytics, and Settings). The parent
component acts as a layout shell and contains a `<router-outlet>` into which the matched child
component is inserted.

## Exercise

You are building a simple admin dashboard with the following URL structure:

| URL | Renders |
|---|---|
| `/dashboard` | `DashboardLayoutComponent` → default child: `OverviewComponent` |
| `/dashboard/analytics` | `DashboardLayoutComponent` → `AnalyticsComponent` |
| `/dashboard/settings` | `DashboardLayoutComponent` → `SettingsComponent` |

### Starter code TODOs

**`app.module.ts`**
- TODO 1 – Import `RouterModule` and `Routes`
- TODO 2 – Define a `routes` array that redirects `''` to `/dashboard` and configures the nested dashboard routes using a `children` array
- TODO 3 – Add `RouterModule.forRoot(routes)` to `imports`

**`dashboard-layout.component.ts`**
- TODO 4 – Add a side-nav with `[routerLink]` links to the three child routes
- TODO 5 – Add a `<router-outlet>` in the template so child components are projected here

## Files
```
starter-code/
  app.module.ts
  app.component.ts
  dashboard-layout.component.ts
  overview.component.ts
  analytics.component.ts
  settings.component.ts
solution/
  app.module.ts
  app.component.ts
  dashboard-layout.component.ts
  overview.component.ts
  analytics.component.ts
  settings.component.ts
```

## Expected Behaviour
1. App redirects to `/dashboard` on load.
2. `DashboardLayoutComponent` is always visible with a sidebar.
3. Clicking Overview / Analytics / Settings swaps the content area without re-rendering the sidebar.

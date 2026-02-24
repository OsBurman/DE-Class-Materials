# Exercise 03: Nested Routes and Layouts

## Objective
Configure nested routes in React Router v6 using `<Outlet>` to share a persistent layout across child routes.

## Background
Most real apps have a consistent shell — a sidebar, a header, a breadcrumb bar — that stays on screen while inner content changes. React Router v6 enables this with nested `<Route>` elements and the `<Outlet>` component, which acts as a placeholder where child route content is rendered. In this exercise you will build a simple dashboard with a sidebar that stays visible across three sub-pages.

## Requirements
1. Create a `<DashboardLayout>` component that renders:
   - A left sidebar with three `<NavLink>` elements: "Overview" (`/dashboard`), "Analytics" (`/dashboard/analytics`), "Settings" (`/dashboard/settings`).
   - An `<Outlet />` to the right of the sidebar where child route content renders.
2. Define routes so that `/dashboard`, `/dashboard/analytics`, and `/dashboard/settings` all render inside `<DashboardLayout>`.
3. The `/dashboard` index route must render an `<OverviewPage>` component with a heading "Dashboard Overview".
4. `/dashboard/analytics` must render an `<AnalyticsPage>` with a heading "Analytics".
5. `/dashboard/settings` must render a `<SettingsPage>` with a heading "Settings".
6. The sidebar and layout must remain visible (not unmount) when navigating between the three child routes.
7. Apply the `active` class to the currently active sidebar link via `<NavLink>`'s `className` callback.

## Hints
- In React Router v6, nest `<Route>` elements inside a parent `<Route>` to create nested routing. The parent `<Route>` uses `element={<DashboardLayout />}`.
- Use `<Route index />` (no `path` attribute) for the default child rendered at the parent's exact path.
- `<Outlet />` is imported from `'react-router-dom'` and placed wherever you want child content to appear.
- Use CSS `display: flex` on the layout wrapper to place the sidebar and `<Outlet>` side by side.

## Expected Output

At `/dashboard`:
```
┌──────────────────┬──────────────────────┐
│ [Overview*]      │ Dashboard Overview   │
│ [Analytics]      │                      │
│ [Settings]       │ Welcome to the dash. │
└──────────────────┴──────────────────────┘
```

At `/dashboard/analytics`:
```
┌──────────────────┬──────────────────────┐
│ [Overview]       │ Analytics            │
│ [Analytics*]     │                      │
│ [Settings]       │ Charts go here.      │
└──────────────────┴──────────────────────┘
```
*(sidebar remains rendered — only right-side content changes)*

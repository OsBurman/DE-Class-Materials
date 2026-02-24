# Exercise 01: React Router Basic Navigation

## Objective
Set up React Router v6 with `BrowserRouter`, define multiple routes, and navigate between pages using `Link` and `NavLink`.

## Background
Single-page applications (SPAs) don't reload the page when you navigate — instead, React Router swaps out components while keeping the browser URL in sync. In this exercise you will wire up a small three-page app (Home, About, Contact) using React Router v6's declarative routing API.

## Requirements
1. Wrap your entire app in `<BrowserRouter>` inside `index.jsx`.
2. Define three routes inside `<Routes>` in `App.jsx`:
   - `/` → renders `<HomePage />`
   - `/about` → renders `<AboutPage />`
   - `/contact` → renders `<ContactPage />`
3. Add a `<Navbar>` component that renders three navigation links using `<NavLink>`:
   - "Home" → `/`
   - "About" → `/about`
   - "Contact" → `/contact`
4. Apply an `active` CSS class to the currently active `<NavLink>` (React Router v6 does this automatically when you provide a `className` callback or use the default `active` class).
5. Each page component must render a heading (`<h1>`) with its page name, e.g. `<h1>Home</h1>`.
6. Add a "404 - Page Not Found" route that catches any unmatched URL and renders a `<NotFoundPage>` component with the message "404 - Page Not Found".

## Hints
- In React Router v6, `<Routes>` replaces the old `<Switch>`. Every `<Route>` must be a direct child of `<Routes>`.
- `<NavLink>` accepts a `className` prop that can be a function: `className={({ isActive }) => isActive ? 'active' : ''}`.
- To catch all unmatched routes, use `path="*"` on your fallback `<Route>`.
- Import `{ BrowserRouter, Routes, Route, Link, NavLink }` from `'react-router-dom'`.

## Expected Output
When the app loads at `/`:
```
[Home] [About] [Contact]

Home
Welcome to the Home page!
```

When navigated to `/about`:
```
[Home] [About*] [Contact]

About
Learn more about us here.
```
*(* indicates the NavLink has the `active` class applied)*

When navigated to `/unknown-path`:
```
[Home] [About] [Contact]

404 - Page Not Found
```

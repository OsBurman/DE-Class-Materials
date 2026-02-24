# Exercise 04 — Code Splitting with React.lazy and Suspense

## Objective
Practice dynamic imports with `React.lazy` and `Suspense` to split a bundle into chunks that load on demand.

## Background
By default, a React app ships as a single JavaScript bundle. As the app grows, that bundle grows too — slowing the initial page load. Code splitting lets you tell the bundler to create separate chunks per route or feature, loading them only when needed. `React.lazy` and `Suspense` are the built-in React APIs that make this possible.

## Requirements
1. Create three "heavy" page components in separate files (`HomePage.tsx`, `AboutPage.tsx`, `DashboardPage.tsx`). Each should:
   - Render a simple `<h2>` heading and a short paragraph.
   - Export as a **default export** (required by `React.lazy`).
2. In `App.tsx`:
   - Import all three using **`React.lazy`** with a dynamic `import()`.
   - Build a simple navigation with three buttons that each set an `activePage` state to `'home'`, `'about'`, or `'dashboard'`.
   - Render the active page component inside a **`<Suspense fallback={<p>Loading...</p>}`**.
   - Show the correct page based on `activePage` using a conditional or object map.
3. Add a comment above each `React.lazy` call explaining what it does.
4. Demonstrate that **Suspense** shows the fallback by adding a simulated delay: add `await new Promise(r => setTimeout(r, 1500))` before the import in one of the lazy calls and note in a code comment which page has the delay.

## Hints
- `React.lazy` expects a function that returns a Promise of a module with a `default` export.
- Wrap lazy components in a single `<Suspense>` rather than one per component.
- The delay simulation must go inside the dynamic import arrow function, not in the component body.
- In a real app (Vite, CRA, Next.js), code splitting happens automatically — no extra config needed.

## Expected Output
```
[Home] [About] [Dashboard]

Welcome to the Home Page          ← loads instantly

After clicking Dashboard:
Loading...                        ← Suspense fallback visible for ~1.5 s

Dashboard Overview                ← then the component appears
```

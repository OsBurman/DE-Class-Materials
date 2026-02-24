# Exercise 02: Error Boundary Component

## Objective
Build a class-based React **Error Boundary** that catches rendering errors in its child tree and displays a friendly fallback UI instead of crashing the entire application.

## Background
JavaScript errors inside a component's render method or lifecycle hooks will crash the whole React tree unless an Error Boundary is in place. Error Boundaries are class components that implement `getDerivedStateFromError` (to update state so the fallback renders) and `componentDidCatch` (to log the error). Functional components cannot be Error Boundaries — this is one of the few cases where a class component is still required.

## Requirements
1. Create a class component called `ErrorBoundary` that:
   - Holds `hasError: boolean` and `errorMessage: string` in state.
   - Implements `static getDerivedStateFromError(error: Error)` — must return `{ hasError: true, errorMessage: error.message }`.
   - Implements `componentDidCatch(error, info)` — must `console.error` the error and the `info.componentStack`.
   - Renders its `children` when `hasError` is `false`.
   - Renders a fallback `<div className="error-boundary">` containing an `<h2>Something went wrong</h2>` and `<p>{errorMessage}</p>` when `hasError` is `true`.
2. Create a `BuggyCounter` component that:
   - Holds a `count` state starting at `0`.
   - Renders a button that increments the counter.
   - **Throws** `new Error('Counter exploded at 3!')` inside `render` (or the function body) when `count >= 3`.
3. In `App`, wrap `<BuggyCounter />` with `<ErrorBoundary>`.
4. Verify: clicking the button three times triggers the boundary and shows the fallback.

## Hints
- `getDerivedStateFromError` is a **static** method — it cannot access `this`.
- You must define a `children` prop type (e.g. `{ children: React.ReactNode }`).
- The throw should happen during rendering (not inside an event handler) — error boundaries only catch render-phase and lifecycle errors, not event-handler errors.
- To reset the boundary after it triggers, you could add a "Try again" button that calls `setState({ hasError: false })`.

## Expected Output
Clicks 1 and 2:
```
Count: 1   [Increment]
Count: 2   [Increment]
```

After click 3:
```
Something went wrong
Counter exploded at 3!
```

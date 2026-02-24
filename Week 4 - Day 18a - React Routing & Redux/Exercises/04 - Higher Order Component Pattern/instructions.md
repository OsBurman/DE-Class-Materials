# Exercise 04: Higher Order Component Pattern

## Objective
Implement a Higher Order Component (HOC) that adds shared cross-cutting behavior (loading state and access control) to any wrapped component.

## Background
A Higher Order Component is a function that takes a component and returns a new, enhanced component. HOCs were the primary pattern for reusing component logic before React Hooks. They are still common in legacy codebases and important to understand for reading existing code. In this exercise you will write two HOCs: one that wraps a component with a loading spinner, and one that gates access based on a permission flag.

## Requirements
1. Create a HOC named `withLoadingSpinner(WrappedComponent)` in `hocs/withLoadingSpinner.jsx`.
   - It must accept an `isLoading` prop.
   - When `isLoading` is `true`, render a `<p>Loading…</p>` instead of `<WrappedComponent />`.
   - When `isLoading` is `false`, render `<WrappedComponent />` and pass all remaining props through.
2. Create a HOC named `withAdminOnly(WrappedComponent)` in `hocs/withAdminOnly.jsx`.
   - It must accept an `isAdmin` prop.
   - When `isAdmin` is `false`, render `<p>Access Denied. Admins only.</p>`.
   - When `isAdmin` is `true`, render `<WrappedComponent />` and pass all remaining props through.
3. Create a `<UserCard>` component in `components/UserCard.jsx` that accepts `name` and `role` props and renders them in a styled card.
4. In `App.jsx`:
   - Wrap `<UserCard>` with `withLoadingSpinner` to produce `UserCardWithLoading`.
   - Wrap `<UserCard>` with `withAdminOnly` to produce `AdminUserCard`.
   - Render `<UserCardWithLoading isLoading={true} name="Alice" role="Developer" />` — should show spinner.
   - Render `<UserCardWithLoading isLoading={false} name="Bob" role="Designer" />` — should show the card.
   - Render `<AdminUserCard isAdmin={false} name="Charlie" role="Manager" />` — should show Access Denied.
   - Render `<AdminUserCard isAdmin={true} name="Dana" role="Admin" />` — should show the card.

## Hints
- A HOC is just a function: `function withSomething(WrappedComponent) { return function Enhanced(props) { ... }; }`.
- Use the spread operator `{...props}` to forward all props from the HOC to the wrapped component.
- Give your enhanced component a `displayName` for easier debugging: `Enhanced.displayName = \`withLoadingSpinner(${WrappedComponent.displayName || WrappedComponent.name})\``.
- The HOC itself does not use JSX — only the inner function it returns does.

## Expected Output
```
Loading…

┌─────────────────┐
│ Bob             │
│ Role: Designer  │
└─────────────────┘

Access Denied. Admins only.

┌─────────────────┐
│ Dana            │
│ Role: Admin     │
└─────────────────┘
```

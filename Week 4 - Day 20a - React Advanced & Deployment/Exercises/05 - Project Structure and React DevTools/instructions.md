# Exercise 05 — Project Structure and React DevTools Profiling

## Objective
Apply React best practices by reorganising a flat, poorly structured component file into a clean feature-based folder structure, then use React DevTools Profiler to identify and explain unnecessary re-renders.

## Background
Real-world React projects that start as a single file quickly become unmaintainable. Feature-based folder structure, barrel exports, and component isolation are industry-standard conventions. React DevTools Profiler is the primary tool for identifying which components re-render and why — a key skill for performance reviews.

## Requirements
### Part A — Refactor to Feature-Based Structure
You are given a single `App.tsx` that contains everything in one file. Refactor it into the following structure:
```
src/
├── components/
│   └── ui/
│       ├── Button.tsx
│       └── index.ts        ← barrel export
├── features/
│   └── counter/
│       ├── Counter.tsx
│       ├── useCounter.ts
│       └── index.ts        ← barrel export
├── App.tsx                 ← imports from features/counter and components/ui
└── main.tsx                ← entry point (provided, no changes needed)
```

1. Extract the `Button` component into `components/ui/Button.tsx` and re-export it from `components/ui/index.ts`.
2. Extract the `useCounter` hook into `features/counter/useCounter.ts`.
3. Extract the `Counter` component into `features/counter/Counter.tsx` (it imports `useCounter` and `Button` from their new paths).
4. Export both from `features/counter/index.ts`.
5. `App.tsx` must import `Counter` from `'./features/counter'` and render it.

### Part B — DevTools Profiling Task
6. In `Counter.tsx`, add a sibling component called `StaticLabel` that renders `<p>This never changes</p>`.
7. Render `<StaticLabel />` next to `<Counter />` in `App.tsx`.
8. Add `console.log('StaticLabel rendered')` inside `StaticLabel`.
9. Open the app in a browser with React DevTools installed. Open the **Profiler** tab, start recording, click Increment several times, then stop. In a comment at the top of `App.tsx`, answer: *"Does StaticLabel re-render when Counter's state changes? How would you prevent it?"*

## Hints
- Barrel files use `export { default } from './Button'` or `export * from './useCounter'`.
- `StaticLabel` is outside `Counter`, so whether it re-renders depends on where state lives.
- Wrapping `StaticLabel` with `React.memo` prevents it re-rendering when parent state changes.
- The DevTools Profiler flame graph shows each component's render time — grey bars = no re-render.

## Expected Output
```
Counter: 0
[Increment] [Decrement] [Reset]
This never changes

After clicking Increment:
Counter: 1
[Increment] [Decrement] [Reset]
This never changes              ← if memoised, console stays silent for StaticLabel
```

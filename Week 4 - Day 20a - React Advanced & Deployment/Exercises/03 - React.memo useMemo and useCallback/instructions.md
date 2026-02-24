# Exercise 03 — React.memo, useMemo and useCallback

## Objective
Practice the three React memoisation APIs to prevent unnecessary re-renders and avoid expensive recalculations.

## Background
React re-renders a child component every time its parent renders — even when the child's props haven't changed. `React.memo` solves this at the component level. `useMemo` caches an expensive computed value. `useCallback` caches a function reference so referential equality is preserved across renders. Together they are the primary tools shown in React DevTools Profiler for eliminating wasted renders.

## Requirements
1. Create a `SlowList` component that:
   - Accepts `items: string[]` and `highlight: string` as props.
   - Simulates expensive rendering by calling a `slowFilter(items, highlight)` function that loops 1 000 000 times before returning items that include the highlight string.
   - Renders a `<ul>` of the filtered items.
   - Is **wrapped with `React.memo`** so it only re-renders when `items` or `highlight` actually change.
2. Create an `App` component that:
   - Holds two pieces of state: `query` (string, controls what gets highlighted/filtered) and `count` (number, incremented by a button — unrelated to the list).
   - Derives the filtered result using **`useMemo`** so `slowFilter` is not re-run when only `count` changes.
   - Passes an `onSelect` callback to `SlowList` wrapped with **`useCallback`** so its reference stays stable across re-renders.
   - Renders:
     - An `<input>` bound to `query`.
     - A **Count: {count}** display and **Increment** button (clicking it must NOT re-render `SlowList`).
     - `<SlowList items={...} highlight={query} onSelect={onSelect} />`.
3. Add a `console.log('SlowList rendered')` inside `SlowList` so the optimisation is visible in the console.

## Hints
- Wrap `SlowList` in `React.memo(...)` — not inside the component, but at the export/declaration site.
- `useMemo` takes a factory function and a dependency array; only recompute when the deps change.
- `useCallback` wraps a function; its identity stays the same between renders unless dependencies change.
- Open the browser console and click **Increment** — if `SlowList rendered` does NOT print, your memoisation is working.

## Expected Output
```
[Input: ""]  Count: 0  [Increment]

• apple
• banana
• grape

Typing "ap" in input:          → SlowList renders (highlight changed)
Clicking Increment repeatedly: → SlowList does NOT re-render (only count changed)
```

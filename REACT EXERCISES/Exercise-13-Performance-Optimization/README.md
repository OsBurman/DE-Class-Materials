# Exercise 13 — Performance Optimization

## Overview

In this exercise you'll take a **Product Catalog** app that suffers from expensive unnecessary re-renders and optimize it step by step using `React.memo`, `useMemo`, and `useCallback`.

## Learning Objectives

By the end of this exercise you will be able to:

- Identify unnecessary re-renders using a render-count ref
- Wrap components in `React.memo` to skip re-renders when props haven't changed
- Use `useMemo` to memoize expensive derived values (filtered lists, aggregate totals)
- Use `useCallback` to stabilise callback props so memoised children don't re-render
- Understand _when_ memoisation helps and when it adds overhead for no gain

## Application Description

The app displays a catalog of **500 generated products**. A toolbar lets users:

- Filter by category
- Filter by a minimum price
- Search by product name
- Toggle products into a shopping cart

**Without optimisation**: every keystroke in the search box recalculates the filtered list, recomputes the cart total, and re-renders all 500 `ProductCard` components.

**Your task**: apply `React.memo`, `useMemo`, and `useCallback` so that:
- `ProductCard` only re-renders when its own data or cart membership changes
- The filtered list is only recomputed when the relevant filters change
- The cart total is only recomputed when the cart changes
- The `handleToggleCart` callback reference stays stable

## File Structure

```
Exercise-13-Performance-Optimization/
├── README.md
├── starter-code/
│   ├── package.json
│   ├── vite.config.js
│   ├── index.html
│   └── src/
│       ├── main.jsx
│       ├── index.css
│       ├── App.jsx
│       ├── App.css
│       ├── data/
│       │   └── products.js
│       └── components/
│           ├── Toolbar.jsx
│           ├── ProductCard.jsx
│           ├── RenderCount.jsx
│           └── CartSummary.jsx
└── solution/
    └── (same structure — fully optimised implementation)
```

## Key Concepts

| Tool | Where Applied | Why |
|---|---|---|
| `React.memo` | `ProductCard` | 500 cards; skip re-render when props unchanged |
| `useMemo` | `filteredProducts` | Expensive filter + sort over 500 items |
| `useMemo` | `cartTotal` | Sum over cart array |
| `useCallback` | `handleToggleCart` | Stable reference so memo'd cards don't re-render |
| `useRef` render count | `RenderCount` | Visualise when a component re-renders |

## TODOs (starter-code)

1. **TODO 1** (`ProductCard.jsx`) — wrap the export in `React.memo`
2. **TODO 2** (`App.jsx`) — wrap `filteredProducts` derivation in `useMemo`
3. **TODO 3** (`App.jsx`) — wrap `cartTotal` derivation in `useMemo`
4. **TODO 4** (`App.jsx`) — wrap `handleToggleCart` in `useCallback`
5. **TODO 5** (`RenderCount.jsx`) — use `useRef` to count renders without causing extra re-renders

## Running the App

```bash
cd starter-code   # or solution
npm install
npm run dev
```

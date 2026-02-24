# Day 20a Application — React Advanced & Deployment: Optimized Dashboard

## Overview

You'll build a **Performance-Optimized Analytics Dashboard** — a React app that demonstrates advanced patterns: memoization, lazy-loaded routes, Suspense boundaries, code splitting, and production build preparation.

---

## Learning Goals

- Apply `React.memo`, `useMemo`, `useCallback` for performance
- Implement code splitting with `React.lazy` and dynamic imports
- Use `Suspense` with fallback UIs
- Apply compound component and render props patterns
- Build and preview a production bundle

---

## Prerequisites

- `cd starter-code && npm install && npm run dev`
- Production build: `npm run build && npm run preview`

---

## Project Structure

```
starter-code/
├── package.json
├── vite.config.js
├── index.html
└── src/
    ├── main.jsx
    ├── App.jsx                   ← TODO: lazy routes + Suspense
    ├── App.css
    ├── components/
    │   ├── StatCard.jsx          ← TODO: React.memo
    │   ├── DataTable.jsx         ← TODO: useMemo for sorted/filtered data
    │   ├── ChartWidget.jsx       ← TODO: lazy loaded
    │   └── TabPanel.jsx          ← TODO: compound component pattern
    └── hooks/
        └── useDataFilter.js      ← TODO: useCallback + useMemo
```

---

## Part 1 — `React.memo`

**Task 1 — `StatCard`**  
Wrap `StatCard` in `React.memo()`. Add a `console.log("StatCard rendered")` inside.  
In `App`, demonstrate that `StatCard` does NOT re-render when unrelated state changes.  
Explain in a comment when `React.memo` is and isn't worth using.

---

## Part 2 — `useMemo` and `useCallback`

**Task 2 — `useDataFilter` hook**  
```js
export function useDataFilter(data, filters) {
  const filteredData = useMemo(() => {
    return data.filter(item => /* filter logic */);
  }, [data, filters]);  // only recalculates when data or filters change

  const handleFilterChange = useCallback((key, value) => {
    setFilters(prev => ({ ...prev, [key]: value }));
  }, []); // stable reference across renders

  return { filteredData, handleFilterChange };
}
```

**Task 3 — `DataTable`**  
Use `useDataFilter`. Also `useMemo` the sorted result: `useMemo(() => [...filteredData].sort(...), [filteredData, sortKey, sortDir])`.

---

## Part 3 — Code Splitting & Lazy Loading

**Task 4 — Lazy load `ChartWidget`**  
```jsx
const ChartWidget = React.lazy(() => import('./components/ChartWidget'));
```
Wrap in `<Suspense fallback={<div>Loading chart...</div>}>`.  
Add an artificial delay in `ChartWidget` (`new Promise(r => setTimeout(r, 1500))`) to see the fallback.

**Task 5 — Lazy-loaded routes in `App.jsx`**  
Lazy-load at least 2 page-level components (e.g., `SettingsPage`, `ReportsPage`).  
Wrap `<Routes>` in a single `<Suspense>` boundary.

---

## Part 4 — Compound Components: `TabPanel`

**Task 6**  
Build `<TabPanel>`, `<TabPanel.Tab>`, and `<TabPanel.Content>` as a compound component using `React.createContext` internally.  
Usage:
```jsx
<TabPanel>
  <TabPanel.Tab id="overview">Overview</TabPanel.Tab>
  <TabPanel.Content id="overview"><StatCard /></TabPanel.Content>
</TabPanel>
```

---

## Part 5 — Build & Deploy Prep

**Task 7 — Production build**  
Run `npm run build`. Open `dist/` and observe the output.  
In `vite.config.js`, enable manual chunking:
```js
rollupOptions: { output: { manualChunks: { react: ['react', 'react-dom'] } } }
```
Compare bundle sizes before and after.

---

## Submission Checklist

- [ ] `React.memo` applied and re-render prevention demonstrated
- [ ] `useMemo` used for expensive calculation
- [ ] `useCallback` used for stable function reference
- [ ] `React.lazy` + `Suspense` with fallback for at least 2 components
- [ ] Compound component pattern implemented
- [ ] Production build runs without errors (`npm run build`)
- [ ] Manual chunk splitting configured in Vite

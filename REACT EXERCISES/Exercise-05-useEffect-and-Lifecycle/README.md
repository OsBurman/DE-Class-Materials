# Exercise 05 — useEffect & Lifecycle

## Overview
Build a **Live Dashboard** that demonstrates `useEffect` in React. The dashboard features a live clock, a random quote fetcher, and a window-resize tracker — each powered by a different `useEffect` pattern.

## Learning Objectives
- Use `useEffect` with an **empty dependency array `[]`** (run once on mount)
- Use `useEffect` with a **dependency array `[dep]`** (run when a value changes)
- Write **cleanup functions** to prevent memory leaks from timers and event listeners
- Perform **data fetching** inside `useEffect`
- Update `document.title` as a side effect

## What You'll Build
A glassmorphism-styled dashboard with three widgets:
- **🕐 Clock Widget** — shows HH:MM:SS, updated every second via `setInterval`
- **💬 Quote Widget** — fetches a random quote on mount and on demand from a public API
- **📐 Window Widget** — displays live window dimensions, updated on resize

## Getting Started
```bash
cd starter-code
npm install
npm run dev
```

## File Structure
```
src/
├── main.jsx
├── App.jsx               ← All state and effects live here
├── App.css
└── components/
    ├── ClockWidget.jsx   ← Receives timeString prop
    ├── QuoteWidget.jsx   ← Receives quote, isLoading, onRefresh
    └── WindowWidget.jsx  ← Receives windowSize { width, height }
```

## TODO Checklist

All TODOs are in `App.jsx`.

- [ ] **TODO 1** — `useEffect` with `[]`: start a `setInterval` every 1000 ms that calls `setTime(new Date())`. Return a cleanup that calls `clearInterval`.
- [ ] **TODO 2** — `useEffect` with `[]`: add a `resize` event listener on `window` that updates `windowSize` state. Return a cleanup that removes the listener.
- [ ] **TODO 3** — Implement `fetchQuote()` async function: set loading, fetch from `https://dummyjson.com/quotes/random`, update `quote` state, clear loading.
- [ ] **TODO 4** — `useEffect` with `[]`: call `fetchQuote()` once on mount.
- [ ] **TODO 5** — `useEffect` with `[quote]`: update `document.title` whenever `quote` changes.
- [ ] **TODO 6** — In `QuoteWidget`: show a loading state when `isLoading` is `true`.
- [ ] **TODO 7** — In `QuoteWidget`: wire up the "New Quote" button to call `onRefresh`.
- [ ] **TODO 8** — Pass `formatTime(time)` as the `timeString` prop to `<ClockWidget />`.
- [ ] **TODO 9** — Pass `windowSize` to `<WindowWidget />`.

## Key Concepts

### The Three useEffect Patterns

| Pattern | When it runs | Common use |
|---------|-------------|------------|
| `useEffect(fn, [])` | Once after first render | Start timers, fetch initial data, add global listeners |
| `useEffect(fn, [dep])` | On mount + whenever `dep` changes | Sync a side effect with a piece of state |
| `useEffect(fn)` | After **every** render | Rarely used — easy to cause infinite loops |

### Cleanup Functions
Always return a cleanup when your effect creates a persistent resource:

```js
useEffect(() => {
  const interval = setInterval(fn, 1000)
  return () => clearInterval(interval) // ← runs on unmount (and before re-running the effect)
}, [])
```

### Async inside useEffect
The effect callback itself cannot be `async`. Define an inner async function and call it:

```js
useEffect(() => {
  async function load() {
    const res = await fetch(url)
    const data = await res.json()
    setState(data)
  }
  load()
}, [])
```

## API Used
- **GET** `https://dummyjson.com/quotes/random`
- Response shape: `{ id: number, quote: string, author: string }`
- No API key required — free public API

## Expected Behavior
1. Clock ticks every second automatically from the moment the page loads
2. A quote appears immediately on load (fetched on mount)
3. "New Quote" button fetches a fresh quote and shows a brief loading state
4. Document tab title updates to reflect the current quote
5. Window dimensions update live as the browser is resized
6. No console errors about memory leaks or missing cleanups

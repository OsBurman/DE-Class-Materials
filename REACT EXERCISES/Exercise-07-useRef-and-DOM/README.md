# Exercise 07 — useRef & DOM Access

## Learning Objectives
By the end of this exercise you will be able to:
- Use `useRef` to access and manipulate DOM elements directly
- Use `useRef` to store a mutable value that persists across renders without triggering re-renders
- Understand the difference between `useRef` and `useState`
- Cancel side effects (timers) stored in refs on component unmount

## Overview
Build a **Focus & Stopwatch App** with three independent sections:

1. **Auto-Focus Search** — a search input that gets focused automatically when the page loads, plus a "Clear & Focus" button that resets the input and returns focus.
2. **Stopwatch** — a precise stopwatch using `setInterval` stored in a ref so it can be started, stopped, and reset without stale-closure issues.
3. **Previous Value Tracker** — an input whose current and previous values are displayed side by side (tracking previous value with a ref).

## Part A — Auto-Focus Search (`SearchSection`)

### Tasks
1. Create a ref with `useRef(null)` and attach it to the `<input>` via `ref={inputRef}`.
2. Use a `useEffect` with an empty dependency array to call `inputRef.current.focus()` on mount.
3. Implement `handleClear` — set `inputRef.current.value = ''` then call `.focus()`.

## Part B — Stopwatch (`Stopwatch`)

### Tasks
1. Create `elapsedRef = useRef(0)` to store elapsed milliseconds and `intervalRef = useRef(null)` to hold the interval ID.
2. Create `display` state (string `'00:00.0'`) — the only piece of state, updated by the interval callback.
3. Implement `handleStart`: call `setInterval` every 100 ms, increment `elapsedRef.current`, format and `setDisplay`.
4. Implement `handleStop`: call `clearInterval(intervalRef.current)`.
5. Implement `handleReset`: stop the interval, reset `elapsedRef.current = 0`, set display back to `'00:00.0'`.
6. Clean up the interval in a `useEffect` cleanup function.

### Time Formatting
```
minutes = Math.floor(elapsed / 60000)
seconds = Math.floor((elapsed % 60000) / 1000)
tenths  = Math.floor((elapsed % 1000) / 100)
`${String(minutes).padStart(2,'0')}:${String(seconds).padStart(2,'0')}.${tenths}`
```

## Part C — Previous Value Tracker (`PrevValueTracker`)

### Tasks
1. Create `value` state and `prevValueRef = useRef('')`.
2. Use a `useEffect` that runs whenever `value` changes to update `prevValueRef.current = value`.
3. Display both the current value and `prevValueRef.current`.

> **Tip:** The ref update inside `useEffect` happens *after* the render, so `prevValueRef.current` always holds the *previous* render's value during the current render.

## Project Structure
```
Exercise-07-useRef-and-DOM/
├── README.md
├── starter-code/
│   ├── index.html
│   ├── package.json
│   ├── vite.config.js
│   └── src/
│       ├── main.jsx
│       ├── index.css
│       ├── App.jsx
│       ├── App.css
│       └── components/
│           ├── SearchSection.jsx
│           ├── Stopwatch.jsx
│           └── PrevValueTracker.jsx
└── solution/  (same structure)
```

## Getting Started
```bash
cd starter-code
npm install
npm run dev
```

## Key Concepts
| Concept | Usage |
|---------|-------|
| `useRef(null)` | Attach to DOM element via `ref={}` prop |
| `useRef(value)` | Store mutable value — changes don't cause re-render |
| `ref.current` | Read or write the stored value |
| Interval cleanup | `return () => clearInterval(intervalRef.current)` in useEffect |
| Prev-value pattern | Update ref in useEffect *after* render |

# Exercise 10 — Custom Hooks

## Learning Objectives
By the end of this exercise you will be able to:
- Extract reusable stateful logic into custom hooks
- Follow the naming convention `use*` for custom hooks
- Combine built-in hooks (`useState`, `useEffect`, `useRef`) inside a custom hook
- Consume third-party APIs inside a hook and expose loading/error state

## Overview
Build a **Developer Utilities Dashboard** that showcases four custom hooks — each powering a different widget.

| Hook | Widget | Description |
|------|--------|-------------|
| `useLocalStorage` | Notes Widget | Persist notes across page reloads |
| `useFetch` | User Profile Widget | Fetch a GitHub user with loading/error |
| `useDebounce` | Search Widget | Debounce input so search fires 500 ms after typing stops |
| `useToggle` | Toggle Widget | Simple boolean toggle |

## Hook Specifications

### `useLocalStorage(key, initialValue)`
- Returns `[value, setValue]` — same API as `useState`.
- Reads from `localStorage.getItem(key)` on init (parse JSON, fall back to `initialValue`).
- Writes to `localStorage.setItem(key, JSON.stringify(value))` inside a `useEffect` whenever `value` changes.

### `useFetch(url)`
- Returns `{ data, loading, error }`.
- Fetches `url` on mount (or when url changes).
- Sets `loading: true` while fetching, clears it after.
- Uses `AbortController` to cancel the request if the component unmounts or url changes.

### `useDebounce(value, delay)`
- Returns `debouncedValue`.
- Uses `useEffect` + `setTimeout` (clearing with the cleanup function) to delay propagating `value` until `delay` ms have passed without a change.

### `useToggle(initialValue = false)`
- Returns `[value, toggle]`.
- `toggle` is a stable function that flips the boolean.

## Project Structure
```
Exercise-10-Custom-Hooks/
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
│       ├── hooks/
│       │   ├── useLocalStorage.js
│       │   ├── useFetch.js
│       │   ├── useDebounce.js
│       │   └── useToggle.js
│       └── components/
│           ├── NotesWidget.jsx
│           ├── UserProfileWidget.jsx
│           ├── SearchWidget.jsx
│           └── ToggleWidget.jsx
└── solution/  (same structure)
```

## Getting Started
```bash
cd starter-code
npm install
npm run dev
```

# Exercise 06: Custom Hook — `useLocalStorage`

## Learning Objectives
- Understand what makes a function a "custom hook" (name starts with `use`, calls other hooks)
- Extract reusable stateful logic into a custom hook
- Combine `useState` and `useEffect` inside a custom hook to sync with `localStorage`
- Use a lazy initializer with `useState` to avoid reading `localStorage` on every render

---

## Background

A **custom hook** is just a function that:
1. Has a name starting with `use`
2. Calls one or more React hooks inside it

Custom hooks let you extract component logic into reusable functions — the same way you extract non-hook logic into helper functions.

```jsx
// Custom hook
function useLocalStorage(key, initialValue) {
  const [value, setValue] = useState(/* ... */);
  useEffect(() => { /* sync to localStorage */ }, [key, value]);
  return [value, setValue]; // same API as useState
}

// Usage — looks just like useState
const [name, setName] = useLocalStorage('username', '');
```

---

## Requirements

### The `useLocalStorage` Hook

Implement `useLocalStorage(key, initialValue)`:

1. **Lazy initializer** — Initialize state using a function passed to `useState`:
   ```jsx
   const [value, setValue] = useState(() => {
     const stored = localStorage.getItem(key);
     return stored !== null ? JSON.parse(stored) : initialValue;
   });
   ```
   This reads `localStorage` **once** on mount instead of on every render.

2. **Sync effect** — Add a `useEffect` that writes the current value to `localStorage` whenever `key` or `value` changes:
   ```jsx
   useEffect(() => {
     localStorage.setItem(key, JSON.stringify(value));
   }, [key, value]);
   ```

3. **Return** `[value, setValue]` — the same tuple shape as `useState`, so callers use it identically.

### Demo Components

Build two independent components that each use `useLocalStorage`:

#### `NamePersister`
- Uses `useLocalStorage('username', '')` to store a name.
- Shows a controlled text input bound to the stored value.
- Displays "Hello, {name}!" when the value is non-empty.
- **Persistence test**: type a name, refresh the page — the name should still appear.

#### `CounterPersister`
- Uses `useLocalStorage('persistCount', 0)` to store a number.
- Shows an Increment, Decrement, and Reset button.
- Displays the current count.
- **Persistence test**: increment several times, refresh the page — the count should survive.

---

## Tips

- `localStorage` only stores strings, so always use `JSON.parse` / `JSON.stringify` to handle numbers, booleans, and objects correctly.
- The lazy initializer (`useState(() => ...)`) is a performance optimization — avoid calling `localStorage.getItem` on every render.
- Your custom hook is consumed exactly like `useState`: `const [count, setCount] = useLocalStorage('key', 0)`.
- Open DevTools → Application → Local Storage to watch values persist in real time.

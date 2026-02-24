# Exercise 02: useEffect with Dependencies and Cleanup

## Objective
Use `useEffect` to run side effects after renders, control when effects re-run using a dependency array, and clean up effects (timers, intervals) when a component unmounts or a dependency changes.

## Background
`useEffect` is React's escape hatch for side effects: fetching data, subscribing to events, updating the document title, or starting a timer. The dependency array `[dep1, dep2]` controls when the effect re-runs — empty `[]` means run once on mount, omitting it means run after every render, and listing values means re-run whenever those values change. Returning a cleanup function from `useEffect` is critical to prevent memory leaks.

## Requirements

1. **Document title effect** — Create a `TitleUpdater` component with a `title` state (string, default `'React Hooks'`):
   - Use `useEffect` to set `document.title` to the current `title` value whenever `title` changes
   - Render a text input bound to `title` via `useState` + `onChange`
   - The dependency array must be `[title]`

2. **Interval timer effect** — Create a `Timer` component:
   - Has a `seconds` state (number, starts at `0`) and `running` state (boolean, starts at `false`)
   - When `running` is `true`, start a `setInterval` that increments `seconds` by 1 every 1000ms
   - When `running` goes back to `false`, **clear the interval** — return a cleanup function from `useEffect` that calls `clearInterval`
   - The dependency array must be `[running]`
   - Render: `"Elapsed: N seconds"` + a Start/Stop toggle button + a Reset button that sets `seconds` to 0 and `running` to false

3. **Fetch on mount** — Create a `UserFetcher` component:
   - Has `user` state (object or null, starts `null`) and `loading` state (boolean, starts `true`)
   - Use `useEffect` with an **empty dependency array `[]`** to fetch from: `https://jsonplaceholder.typicode.com/users/1`
   - When the fetch resolves, set `user` to the response JSON and `loading` to `false`
   - Render `"Loading..."` while `loading` is `true`, or the user's `name` and `email` once loaded

4. Render all three components from `App`.

## Hints
- The cleanup function returned by `useEffect` runs when the component unmounts OR just before the effect runs again (if a dependency changed)
- Always store the return value of `setInterval` so you can pass it to `clearInterval` in the cleanup
- For the fetch effect, use `async` inside `useEffect` by defining an inner async function and calling it immediately — do NOT make the `useEffect` callback itself `async`
- If you get a React StrictMode double-invocation in development, don't worry — the cleanup will fire and correct it

## Expected Output
```
[text input: "React Hooks"]  ← changing this updates the browser tab title

Elapsed: 7 seconds
[Stop]  [Reset]

Loading...   ← then:
Leanne Graham
Sincere@april.biz
```

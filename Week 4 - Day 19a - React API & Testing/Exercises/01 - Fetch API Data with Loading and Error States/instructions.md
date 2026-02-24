# Exercise 01: Fetch API Data with Loading and Error States

## Objective
Practice fetching data from a public REST API using `fetch`, managing loading and error states with `useState` and `useEffect`, and rendering the response.

## Background
Almost every real React application needs to fetch data from a server. The pattern involves three states: **loading** (the request is in flight), **data** (the response arrived), and **error** (something went wrong). Keeping all three in sync gives users clear feedback at every step.

## Requirements
1. Create a `UserList` component that fetches users from `https://jsonplaceholder.typicode.com/users` on mount.
2. While the request is in-flight, render a `<p>Loading...</p>` element.
3. If the request fails (or returns a non-ok status), render `<p className="error">Error: {message}</p>`.
4. On success, render a `<ul>` where each `<li>` shows the user's `name` and `email` separated by an em-dash (`—`).
5. Add a **"Reload"** button that re-triggers the fetch when clicked.
6. Install and use **axios** instead of `fetch` for the actual HTTP call (the component receives a prop `useAxios?: boolean`; when `false` use the native `fetch` API — this lets you compare both approaches).

## Hints
- Use a `useEffect` with a cleanup flag (`let cancelled = false`) to prevent setting state on an unmounted component.
- `axios.get()` returns a promise; the data lives in `response.data`.
- For `fetch`, check `response.ok` before calling `response.json()` — a 4xx/5xx does NOT throw automatically.
- Trigger a re-fetch by storing a `refreshKey` counter in state and including it in the `useEffect` dependency array.

## Expected Output
```
Loading...           ← shown briefly while request is in-flight

Leanne Graham — Sincere@april.biz
Ervin Howell — Shanna@melissa.tv
Clementine Bauch — Nathan@yesenia.net
...                  ← one <li> per user

[Reload]             ← button below the list
```

If the network is offline or the URL is wrong:
```
Error: Network Error
```

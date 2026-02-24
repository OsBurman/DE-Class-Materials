# Exercise 06: Environment Variables in React (.env Files)

## Objective
Configure a React app to read API base URLs and feature flags from `.env` files using the `REACT_APP_` prefix convention, and consume them safely inside components.

## Background
Hard-coding API URLs in source code makes it impossible to target different environments (development, staging, production) without changing code. React (via Create React App / Vite) supports `.env` files: any variable prefixed with `REACT_APP_` (CRA) or `VITE_` (Vite) is baked into the client bundle at build time and accessible via `process.env.REACT_APP_*` or `import.meta.env.VITE_*`. Secret keys must **never** go in `.env` files committed to version control — use a backend proxy instead.

## Requirements
1. Create a `.env` file in the project root with:
   ```
   REACT_APP_API_BASE_URL=https://jsonplaceholder.typicode.com
   REACT_APP_FEATURE_DARK_MODE=true
   ```
2. Create a `.env.example` file (safe to commit) that documents the required variables with placeholder values.
3. Add `.env` to `.gitignore` (include a comment explaining why).
4. Create a `config.ts` file that exports a typed `config` object:
   ```ts
   export const config = {
     apiBaseUrl: process.env.REACT_APP_API_BASE_URL ?? '',
     featureDarkMode: process.env.REACT_APP_FEATURE_DARK_MODE === 'true',
   };
   ```
5. Create a `WeatherWidget` component that:
   - Imports `config` from `config.ts`.
   - Fetches from `${config.apiBaseUrl}/todos/1` on mount (using it as a stand-in for a "real" API call).
   - Displays the fetched `title`.
   - Conditionally applies a `dark` CSS class to its wrapper `<div>` when `config.featureDarkMode` is `true`.
   - Renders `<p data-testid="api-url">Using: {config.apiBaseUrl}</p>` so the URL is visible for verification.

## Hints
- Variables missing from `.env` will be `undefined` at runtime — always use a fallback (e.g. `?? ''`).
- After editing `.env`, you must **restart the dev server** for changes to take effect.
- Never commit real secrets (API keys, passwords) in `.env` — use `.env.local` (also git-ignored by CRA/Vite by default).
- In tests, set `process.env.REACT_APP_API_BASE_URL = 'http://test'` in the test file or `jest.config.js`.

## Expected Output
```
Using: https://jsonplaceholder.typicode.com

delectus aut autem        ← title from /todos/1
```
(div has class "dark" because REACT_APP_FEATURE_DARK_MODE=true)

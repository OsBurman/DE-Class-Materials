# Exercise 12 — Data Fetching & APIs

## Overview

In this exercise you'll build a **GitHub User Explorer** — a React app that fetches real data from the public GitHub API, handles loading and error states, and renders a user profile alongside their most recent repositories.

## Learning Objectives

By the end of this exercise you will be able to:

- Fetch data inside `useEffect` using the `fetch` API
- Cancel in-flight requests with `AbortController` to avoid stale-state bugs
- Model loading, error, and success states with `useState`
- Trigger re-fetches when a dependency changes (controlled search input)
- Make multiple API calls (profile + repos) cleanly
- Build reusable presentational components (`UserProfile`, `RepoCard`, `LoadingSpinner`, `ErrorMessage`)

## Application Description

The app has a single search form. When the user types a GitHub username and submits, the app:

1. Shows a loading spinner
2. Fetches `https://api.github.com/users/{username}` and `https://api.github.com/users/{username}/repos?sort=updated&per_page=10`
3. Renders the user's avatar, name, bio, follower counts, and a list of their 10 most recently updated repos
4. Displays an error message if the username doesn't exist or the network fails

## File Structure

```
Exercise-12-Data-Fetching-and-APIs/
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
│       └── components/
│           ├── SearchForm.jsx
│           ├── UserProfile.jsx
│           ├── RepoList.jsx
│           ├── RepoCard.jsx
│           ├── LoadingSpinner.jsx
│           └── ErrorMessage.jsx
└── solution/
    └── (same structure — complete implementation)
```

## Key Concepts

| Concept | Where Used |
|---|---|
| `useEffect` + `fetch` | `App.jsx` — triggers on username change |
| `AbortController` | `App.jsx` — cleanup function cancels request |
| Loading/error state | `App.jsx` — drives conditional rendering |
| Parallel requests | `App.jsx` — `Promise.all([fetchUser, fetchRepos])` |
| Presentational components | `UserProfile`, `RepoCard`, `LoadingSpinner`, `ErrorMessage` |

## TODOs (starter-code)

1. **TODO 1** (`App.jsx`) — declare `user`, `repos`, `loading`, and `error` state
2. **TODO 2** (`App.jsx`) — write `useEffect` that runs when `submittedUsername` changes
3. **TODO 3** (`App.jsx`) — create an `AbortController` and fetch the user endpoint
4. **TODO 4** (`App.jsx`) — fetch the repos endpoint in parallel with `Promise.all`
5. **TODO 5** (`App.jsx`) — handle errors (non-OK response → throw; AbortError → ignore)
6. **TODO 6** (`App.jsx`) — return the cleanup function `() => controller.abort()`
7. **TODO 7** (`SearchForm.jsx`) — control the input with `value` / `onChange`
8. **TODO 8** (`SearchForm.jsx`) — call `onSearch(username)` on form submit
9. **TODO 9** (`UserProfile.jsx`) — render the user's avatar, name, login, bio, and stats
10. **TODO 10** (`RepoCard.jsx`) — render repo name, description, language, and stars

## Running the App

```bash
cd starter-code   # or solution
npm install
npm run dev
```

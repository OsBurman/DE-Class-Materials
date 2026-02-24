# Day 19a Application — React API & Testing: GitHub Profile Viewer

## Overview

You'll build a **GitHub Profile Viewer** — a React app that fetches real data from the GitHub public API, handles loading and error states, implements an error boundary, and has full test coverage with React Testing Library and Jest.

---

## Learning Goals

- Integrate a REST API using `fetch`/`axios`
- Handle loading, success, and error states
- Implement error boundaries for graceful failure
- Write unit tests with React Testing Library
- Test hooks and async state changes
- Mock API calls in tests
- Use environment variables

---

## Prerequisites

- `cd starter-code && npm install && npm run dev`
- GitHub public API (no key needed): `https://api.github.com/users/{username}`

---

## Project Structure

```
starter-code/
├── package.json          ← includes vitest + @testing-library/react
├── vite.config.js
├── index.html
└── src/
    ├── main.jsx
    ├── App.jsx              ← TODO
    ├── App.css
    ├── components/
    │   ├── ProfileCard.jsx   ← TODO
    │   ├── RepoList.jsx      ← TODO
    │   └── ErrorBoundary.jsx ← TODO (class component)
    ├── hooks/
    │   └── useGitHubProfile.js ← TODO: custom hook
    └── __tests__/
        ├── ProfileCard.test.jsx ← TODO
        └── useGitHubProfile.test.js ← TODO
```

---

## Part 1 — API Hook

**Task 1 — `useGitHubProfile(username)`**  
State: `{ profile: null, repos: [], loading: false, error: null }`  
`useEffect` that:
1. Sets `loading: true`
2. Fetches `https://api.github.com/users/{username}` and `/repos` in parallel with `Promise.all`
3. Sets profile and repos on success
4. Sets error on failure
5. Sets `loading: false` in finally

---

## Part 2 — Components

**Task 2 — `ProfileCard`**  
Receives: `profile` prop. Displays: avatar, name, bio, followers, following, public_repos.  
Show a placeholder if profile is null.

**Task 3 — `RepoList`**  
Receives `repos`. Renders a list of repo cards with: name (as link), description, stars, language. Show top 6 repos sorted by stars.

**Task 4 — `ErrorBoundary` (class component)**  
Implement `componentDidCatch` and `getDerivedStateFromError`. Render a fallback UI if `hasError` is true.

---

## Part 3 — `App.jsx`

**Task 5**  
Controlled input for username with a search button.  
Wrap the profile section in `<ErrorBoundary>`.  
Show a loading spinner during fetch. Show error message on failure.  
Read initial username from `import.meta.env.VITE_DEFAULT_USERNAME` (set in `.env`).

---

## Part 4 — Tests

**Task 6 — `ProfileCard.test.jsx`**  
```jsx
describe('ProfileCard', () => {
  it('renders profile name', () => {...});
  it('renders follower count', () => {...});
  it('renders placeholder when profile is null', () => {...});
});
```
Use `render`, `screen.getByText`, `screen.queryByText`.

**Task 7 — `useGitHubProfile.test.js`**  
Mock `fetch` using `vi.fn()` (Vitest). Test:
- Initial state has `loading: false` and `profile: null`
- Sets `loading: true` while fetching
- Sets profile data on success
- Sets error on failure (mock fetch rejection)

Use `renderHook` and `waitFor` from `@testing-library/react`.

---

## Stretch Goals

1. Add a debounce to the search input using `useRef` and `setTimeout`.
2. Test `RepoList` — verify repos are sorted by stars.
3. Add `msw` (Mock Service Worker) for more realistic API mocking.

---

## Submission Checklist

- [ ] API called with `fetch` using `Promise.all` for parallel requests
- [ ] Loading and error states handled in UI
- [ ] `useGitHubProfile` custom hook built
- [ ] `ErrorBoundary` class component implements `getDerivedStateFromError`
- [ ] `.env` file with `VITE_DEFAULT_USERNAME`
- [ ] At least 3 passing tests in `ProfileCard.test.jsx`
- [ ] `fetch` mocked in hook tests
- [ ] All tests pass with `npm run test`

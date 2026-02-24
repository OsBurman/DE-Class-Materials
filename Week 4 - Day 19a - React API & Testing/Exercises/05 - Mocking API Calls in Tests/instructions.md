# Exercise 05: Mocking API Calls in Tests

## Objective
Mock `fetch` (or a module) with Jest so your component tests run without making real network requests, and assert that components render correctly based on the mocked response.

## Background
Tests should be fast and deterministic — they cannot depend on a live server. Jest lets you replace `global.fetch` (or any module export) with a `jest.fn()` spy that returns whatever you specify. This lets you test the "success path", "loading path", and "error path" of a component that fetches data, all without touching the network.

## Requirements
1. A `PostList` component is provided that:
   - Fetches from `https://jsonplaceholder.typicode.com/posts?_limit=3` on mount.
   - Shows `Loading...` while in-flight.
   - Renders a `<ul>` with each post's `title` in an `<li>` on success.
   - Renders `<p data-testid="error">Error: {message}</p>` on failure.
2. In `PostList.test.tsx`, write three tests:
   - **"shows loading state initially"** — mock fetch to return a never-resolving promise; assert `Loading...` is visible immediately after render.
   - **"renders posts on successful fetch"** — mock fetch to resolve with 2 fake posts; assert both titles appear in the document.
   - **"shows error message when fetch fails"** — mock fetch to reject; assert the `[data-testid="error"]` element is visible with the correct message.
3. Restore `global.fetch` after each test with `afterEach`.

## Hints
- Assign `global.fetch = jest.fn(...)` before rendering the component.
- A successful mock: `jest.fn().mockResolvedValueOnce({ ok: true, json: () => Promise.resolve([...]) })`.
- A failing mock: `jest.fn().mockRejectedValueOnce(new Error('Network Error'))`.
- Use `waitFor` or `findBy*` queries (they wait for async DOM updates) rather than `getBy*` for assertions after the fetch completes.

## Expected Output
```
PASS  src/PostList.test.tsx
  PostList
    ✓ shows loading state initially
    ✓ renders posts on successful fetch
    ✓ shows error message when fetch fails

Tests: 3 passed, 3 total
```

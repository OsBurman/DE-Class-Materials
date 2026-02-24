# Day 19a — Part 1 Walkthrough Script
## React API Integration & Error Boundaries

**Duration:** ~90 minutes  
**Files:** `01-api-fetch-and-axios.jsx`, `02-error-boundaries.jsx`  
**Prerequisites:** React Hooks (useEffect, useState, useCallback), Promises/async-await

---

## Recap from Day 18a (5 min)

> "Yesterday we covered React Router and Redux — you can now navigate between pages and manage global state. Today we connect your apps to real data. Part 1 is all about making HTTP requests and handling what comes back — including when things go wrong."

> "Quick check: what are the two things `useState` gives you?" → current value and a setter function

> "And `useEffect` — when does it run?" → after render; when dependencies change

---

## Introduction (3 min)

> "Every real app talks to a server. When a user opens your course page, you need to fetch the list of courses. When they enroll, you need to POST that to an API. When the API is down, you need to not show a blank white screen.

> Today covers exactly that: the mechanics of fetching data, the patterns for handling loading and errors, and a special React mechanism called Error Boundaries for when things go wrong in the render phase itself.

> We have two files: the first covers Fetch and Axios, the second covers Error Boundaries. Let's start."

---

## File 1: `01-api-fetch-and-axios.jsx`

### Segment 1 — The Fetch API (Sections 1–2, ~15 min)

> "Open `01-api-fetch-and-axios.jsx`. At the very top, notice we import `useState`, `useEffect`, and `useCallback`. We're going to use all three today — they work together to manage async data."

---

#### Section 1 — Basic GET with fetch (~8 min)

> "Look at `CourseListWithFetch`. This is the most common pattern you'll write in React apps — fetch data on mount, handle loading and error states, display the result."

**Walk through the state variables:**
```jsx
const [courses, setCourses] = useState([]);
const [isLoading, setIsLoading] = useState(false);
const [error, setError] = useState(null);
```

> "Three pieces of state — and notice they start as: empty array, false, and null. Why an empty array for courses? Because we'll be mapping over it in the render — if it were `null` we'd crash on `.map()`."

**Walk through the useEffect body:**

> "Before the request — `setIsLoading(true)`. The user needs to see something is happening. Then `setError(null)` — clear any previous error so a retry shows a clean slate."

> "Now the critical part."

**Point to the `.ok` check:**
```js
if (!response.ok) {
  throw new Error(`HTTP error! Status: ${response.status}`);
}
```

> "Here's the number one gotcha with `fetch`. Who knows what `fetch` rejects on?" → pause for answers

> "Only NETWORK errors — you're offline, DNS fails, the server is completely unreachable. An HTTP 404 or 500 response still RESOLVES the promise. The response comes back, it just has `ok: false`. If you forget this check, your app silently ignores 500 errors and you render nothing."

> "After the check: `.then(res => res.json())` — this is a second Promise. fetch gives you the raw response first, and you parse the body separately."

**Walk through .catch:**
> "`.catch` receives any error thrown above — network errors AND our manually thrown error. We save the message to state."

**Walk through .finally:**
> "`.finally` runs regardless — success or failure — so we always clear the loading state. Without this, if an error is thrown, you'd be stuck showing a spinner forever."

**Ask class:** "What gets rendered if `isLoading` is true?" → `<div className="loading-spinner">Loading courses...</div>`

> "This pattern — conditional render based on state — keeps your JSX simple. The component renders once for loading, once for error, once for success."

---

#### Section 2 — POST with fetch (~5 min)

> "`CreateCourseForm` shows a POST request. Three differences from GET:"

Write on board:
```
1. method: 'POST'
2. headers: { 'Content-Type': 'application/json' }
3. body: JSON.stringify(data)   ← must be a string!
```

> "The body MUST be a string. `JSON.stringify` converts your object. The Content-Type header tells the server 'I'm sending JSON.' Forget either one and the server won't understand your request."

> "Notice we're using async/await here instead of .then chains. Both work — async/await is cleaner for multiple awaits. The try/catch/finally mirrors the .then/.catch/.finally pattern exactly."

---

### Segment 2 — Axios (Sections 3–5, ~15 min)

> "Now let's look at Axios. Same goal, better developer experience. `npm install axios` — not built into the browser."

---

#### Section 3 — Axios GET (~5 min)

> "Look at the differences in `CourseListWithAxios`."

**Point out each difference:**
- `axios.get(url, { params: { _limit: 10 } })` — pass query params as an object, not in the URL string
- `response.data` — Axios unwraps the JSON for you, no `.json()` step
- Axios throws on non-2xx status codes — the `if (!response.ok)` check is NOT needed

**Walk through the error handler:**
```js
if (err.response) {        // server responded, but 4xx/5xx
if (err.request) {         // request made, no response (network issue)
else {                     // setup error
```

> "Axios gives you a structured error object with three possible states. `err.response` has the server's actual response including status code and body. This makes for much more descriptive error messages."

---

#### Section 4 — Axios Instance (~5 min)

> "In a real app, you don't call `axios.get()` directly everywhere — you create an instance with shared configuration."

```js
export const apiClient = axios.create({
  baseURL: BASE_URL,
  timeout: 10000,
  headers: { 'Content-Type': 'application/json' },
});
```

> "Create this ONCE and export it. Every file that needs to call the API imports `apiClient`, not raw `axios`. Benefits: change the base URL in one place; add auth headers in one place; set a timeout once."

---

#### Section 5 — Interceptors (~5 min)

> "Interceptors are middleware for your HTTP calls. Two types: request and response."

**Request interceptor:**
> "Runs before every request leaves the browser. Here we read the auth token from localStorage and attach it. If we put this in every individual call, we'd repeat the same 3 lines in 50 places. One interceptor handles all of them."

> "The critical detail: `return config`. If you forget this, the request never fires — the interceptor swallows it."

**Response interceptor:**
> "Runs when every response comes back. Here we check for 401 — unauthorized — and redirect to login. Again: if we handled this in every component, that's 50 `if (err.response?.status === 401)` checks. One interceptor is the single source of truth."

**Q&A prompt:** "What's the difference between a 401 and a 403 status code?" → 401 = unauthenticated (not logged in); 403 = unauthorized (logged in but insufficient permissions)

---

### Segment 3 — Response Data Handling (Section 6, ~5 min)

> "Section 6 shows something you'll definitely encounter: APIs that wrap their data in container objects."

Show the example shape:
```json
{ "status": "success", "page": 1, "data": { "courses": [...] } }
```

> "If you do `res.data` with Axios, you get the outer object. Your courses are at `res.data.data.courses`. Always inspect what your API actually returns before writing the component — use the Network tab or Postman."

> "The `normalizeApiResponse` helper shows a defensive pattern: handle multiple possible shapes. Useful when you're consuming third-party APIs you don't control."

---

### Segment 4 — Cancelling Requests (Section 7, ~8 min)

> "This is important and often skipped. Open Section 7 — `SearchCourses`."

> "Scenario: user types 'Rea' in a search box. Three requests fire — one per keystroke. The first request (just 'R') might respond LAST because of network timing. Suddenly your state shows results for 'R' even though the user typed 'Rea'. This is a RACE CONDITION."

> "Solution: `AbortController`."

**Walk through:**
```js
const controller = new AbortController();
// ...
fetch(url, { signal: controller.signal })
// ...
return () => controller.abort();  // cleanup
```

> "Each time `query` changes, the useEffect cleanup runs — it calls `abort()` on the PREVIOUS request before starting the new one. The new request gets a fresh controller."

> "When we call abort(), the fetch Promise rejects with an `AbortError`. That's not a real error — we catch it and ignore it silently with the `err.name !== 'AbortError'` check."

**Ask class:** "What would happen if we didn't check for AbortError?" → we'd set the error state on every keystroke after the first, showing an error message to the user

---

### Segment 5 — Custom useFetch Hook (Section 8, ~8 min)

> "We've now written the loading/error/data pattern three times. Every time we need to fetch something, we copy the same useEffect structure. That's a signal to extract a custom hook."

> "Look at `useFetch`. It takes a URL and returns `{ data, isLoading, error, refetch }`."

**Walk through the implementation — key points:**
- `useCallback` wraps `fetchData` so its reference is stable — prevents infinite useEffect loops
- Cleanup with `AbortController` is inside the hook, not the component
- `refetch: fetchData` — the hook exposes a function the caller can invoke to re-run the request

**Show the consumer:**
```jsx
const { data: course, isLoading, error, refetch } = useFetch(
  `${BASE_URL}/posts/${courseId}`
);
```

> "The component is now three lines of state + conditional rendering. No useEffect visible. The component only cares about displaying data — the fetching logic is in the hook. This is exactly the separation of concerns that makes React apps maintainable."

---

### Segment 6 — Loading State Patterns (Section 9, ~3 min)

> "Quick section on loading state design. Boolean `isLoading` is the simplest. But notice the comment about impossible states."

> "With boolean loading + boolean error, you could technically have `isLoading: true` AND `error: 'some error'` at the same time — that's not a valid UI state. An enum status ('idle' | 'loading' | 'success' | 'error') prevents that — exactly ONE state is active at a time."

> "Skeleton loaders are worth mentioning — instead of a spinner, show grey placeholder shapes that match the content layout. Much better UX. Same loading boolean, different render."

---

### Segment 7 — Promise.all (Section 10, ~3 min)

> "Last pattern: `CourseDashboard` loads three things at once."

> "`Promise.all([...])` takes an array of Promises and returns a new Promise that resolves when ALL of them complete. The result is an array in the same order — you destructure it."

> "If ANY request fails, `Promise.all` rejects immediately — you lose all data. If that's not acceptable, use `Promise.allSettled` which gives you a result for each, success or failure, without short-circuiting."

---

## File 2: `02-error-boundaries.jsx`

### Segment 8 — Error Boundaries (Sections 1–7, ~15 min)

#### Section 1 — The problem (3 min)

> "Switch to `02-error-boundaries.jsx`. Read Section 1 out loud — specifically the bullets about what Error Boundaries catch and what they don't."

> "The key insight: if a component throws during rendering, React cascades the unmounting all the way up the tree until it finds an Error Boundary. Without any boundary, the WHOLE PAGE goes blank. A well-placed boundary limits the damage."

---

#### Section 2 — The Implementation (8 min)

> "Scroll down to the `ErrorBoundary` class. This is one of very few places where we MUST write a class component in modern React. There is no Hook equivalent — React hasn't provided one."

**`getDerivedStateFromError`:**
> "This runs during the render phase when a descendant throws. It's static — no `this`. Its only job is to return state updates. We set `hasError: true` and capture the error."

> "Why static? Because React may call this method multiple times before committing. No side effects allowed here — just pure state calculation."

**`componentDidCatch`:**
> "This runs after the DOM is updated — the commit phase. HERE is where you call Sentry, Datadog, your error API. Side effects are safe here. `errorInfo.componentStack` is a stack trace showing which components were rendering when the error occurred — invaluable for debugging."

**The render method:**
> "If `hasError` is true, render the fallback. Notice we check `this.props.fallback` first — this makes the boundary reusable. Different wrappers can pass different fallback UI for their context."

> "The 'Try Again' button calls `handleReset` which sets `hasError: false`. React will re-attempt rendering the children."

---

#### Sections 3–4 — Usage and Granularity (4 min)

> "Sections 3 and 4 show the key usage patterns. Scroll through quickly."

**Point out the granular boundary example:**
```jsx
<ErrorBoundary fallback={<p>Featured courses unavailable.</p>}>
  <FeaturedCourses />
</ErrorBoundary>

<ErrorBoundary fallback={<p>Your progress could not be loaded.</p>}>
  <StudentProgress />
</ErrorBoundary>
```

> "Each section of the dashboard has its own boundary with its own fallback message. If FeaturedCourses crashes, StudentProgress still loads. The user sees a meaningful message, not a blank screen."

> "The analogy: bulkheads on a ship. If one compartment floods, you seal it off. The rest of the ship stays afloat."

---

#### Section 7 — Demo component (2 min)

> "The `BrokenCourseCard` component throws intentionally when `course` is null. `SafeCourseSection` wraps it in a boundary. If you pass `null` as the course prop, the boundary catches the throw and shows 'This course card could not be displayed' instead of crashing the page."

> "Event handler errors are NOT caught. If the throw was inside an `onClick` handler instead of the render, the boundary would NOT catch it. You need a try/catch inside the handler for those."

---

## Part 1 Wrap-Up (5 min)

> "Let's anchor what we covered:"

**On the board:**
```
Fetch:   manual .ok check, manual .json() parse, .catch for both types of errors
Axios:   auto JSON, auto throws on 4xx/5xx, axios.create(), interceptors
Pattern: loading state → fetch → update state → render
Abort:   cleanup function → controller.abort() → prevent race conditions
Hook:    useFetch() → extract pattern → reuse across components
Error Boundaries: class component → getDerivedStateFromError → fallback UI
```

> "Part 2 switches gears entirely — we're going to TEST all of this. You'll write Jest tests and React Testing Library tests for components, hooks, and mock the API calls so tests don't need a real server running."

---

## Q&A Prompts

1. "Why does `fetch` not reject on a 404 response?"
   - `fetch` follows the spec: a response was received successfully. Whether the content was found is an application-level concern, not a network-level concern.

2. "When would you use `Promise.all` vs sequential awaits?"
   - `Promise.all` for parallel independent requests (faster); sequential awaits when request B depends on the result of request A.

3. "What's the difference between `getDerivedStateFromError` and `componentDidCatch`?"
   - `getDerivedStateFromError` → render phase, static, updates state for fallback UI; `componentDidCatch` → commit phase, for side effects like logging.

4. "Can you have an async function inside useEffect directly?"
   - No — useEffect's callback can't be async (it would return a Promise, not a cleanup function). Define an async function inside useEffect and call it immediately.

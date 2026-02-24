# Day 19a — Part 2 Walkthrough Script
## React Testing: Jest, React Testing Library, Mock APIs & Environment Variables

**Duration:** ~100 minutes  
**Files:** `01-jest-and-react-testing-library.test.jsx`, `02-mock-api-and-env-variables.test.jsx`  
**Prerequisites:** Part 1 complete (fetch, Axios, error boundaries), basic understanding of React components and hooks

---

## Transition from Part 1

> "In Part 1 we built the machinery for fetching data — fetch, Axios, loading states, error handling, error boundaries. Now we answer the question every professional developer has to answer: how do I know this code works correctly, and how do I make sure it keeps working after I change something?

> The answer is automated testing. Today's Part 2 covers the two tools you'll use to test React apps: Jest and React Testing Library. By the end of this session you'll know how to test components, test custom hooks, mock API calls so tests don't need a real server, and configure environment variables for different environments."

---

## Introduction: The Testing Philosophy (5 min)

> "Before we touch code, I want to establish the philosophy behind React Testing Library specifically — because it's different from how you might expect testing to work."

> "RTL was created by Kent C. Dodds with a core principle: **test your components the way users use them**. Not the internal implementation — not state variables, not method names — but what the user actually SEES and DOES."

**Write on board:**
```
The more your tests resemble the way your software is used,
the more confidence they can give you.
— Kent C. Dodds
```

> "What does this mean practically? We don't test that `setCount` was called. We test that the counter displays '1' after the user clicks Increment. If we refactor `setCount` to `useReducer`, the test still passes because the USER experience didn't change."

> "This is the biggest shift from older testing approaches like Enzyme. In Enzyme, you'd call methods on component instances directly. RTL says: simulate what a user does, verify what a user sees."

---

## File 1: `01-jest-and-react-testing-library.test.jsx`

### Segment 1 — File Overview & Setup (5 min)

> "Open `01-jest-and-react-testing-library.test.jsx`. At the top, notice the imports."

**Walk through imports:**
- `render, screen, waitFor, fireEvent, within` from `@testing-library/react`
- `userEvent` from `@testing-library/user-event`
- `renderHook, act` from `@testing-library/react`
- `@testing-library/jest-dom` — the import that adds DOM-specific matchers to Jest

> "The `jest-dom` import is side-effectful — it just extends Jest's `expect`. Without it, `toBeInTheDocument()` doesn't exist. CRA adds this import in `setupTests.js` automatically. For other setups you configure it in `jest.config.js`."

> "Section 1 defines the components we're testing. Normally these would be imported from their own files. Here they're defined locally so the file is fully self-contained for teaching."

---

### Segment 2 — Jest Basics: describe, test, expect (Section 2, ~5 min)

> "Section 2 is all comment — explaining the three building blocks of any Jest test."

**Write on board:**
```
describe('Group name', () => {
  test('should do X when Y', () => {
    // Arrange — set up
    // Act — do the thing
    // Assert — verify the result
  });
});
```

> "AAA: Arrange, Act, Assert. Every test follows this shape. The clarity of your test names matters — 'should render with the provided name' is descriptive. 'test 1' is useless in a bug report."

> "Note that `test` and `it` are identical — `it` reads more like English: `it('should render a heading')`. Use whichever you prefer; be consistent in a codebase."

---

### Segment 3 — render() and screen queries (Sections 3–4, ~12 min)

> "Let's look at `Greeting component` tests — our first actual test block."

#### render() (3 min)

```jsx
render(<Greeting name="Alice" />);
```

> "`render` mounts the component into a virtual DOM (JSDOM). The component runs just like in a browser — all hooks fire, all effects run. You don't need a browser to test this."

#### screen queries (5 min)

> "After rendering, we query the DOM with `screen`. Let me walk through the query types — this is the most important reference you'll need."

**Walk through the query type table from Section 3:**
- `getBy*` — asserts presence; throws if not found; use when the element MUST be there
- `queryBy*` — returns null if not found; use when testing the element is ABSENT
- `findBy*` — async; waits for element to appear; use after async state updates
- `getAllBy*`, `queryAllBy*`, `findAllBy*` — same but for multiple matches

> "The priority order matters. RTL authors say: prefer queries that match what a real user or assistive technology would perceive."

**Walk through priority list:**
1. `ByRole` — matches by ARIA role + accessible name. Most robust.
2. `ByLabelText` — form inputs with `<label>`.
3. `ByPlaceholderText`
4. `ByText` — visible text content
5. `ByDisplayValue` — input's current value
6. `ByAltText` — image alt text
7. `ByTestId` — `data-testid` attribute. Escape hatch — use last resort.

**Q&A prompt:** "Why is `ByRole` the most preferred?" → It tests accessibility. If a screen reader can find it, your test can too. It also means your app IS accessible.

#### The first test (4 min)

```jsx
const heading = screen.getByRole('heading', { name: /hello, alice!/i });
expect(heading).toBeInTheDocument();
```

> "Two things to notice: `{ name: /hello, alice!/i }` — this is the accessible name, meaning the text content the element exposes to assistive technology. The `/i` flag makes it case-insensitive — good habit."

> "`toBeInTheDocument()` — from jest-dom. Checks that the element is actually in the DOM. Compare to `expect(heading).not.toBeNull()` which would also work but is less readable."

---

### Segment 4 — Testing Props and Event Callbacks (Section 4, ~8 min)

> "Move to `CourseCard component`. This is the pattern for testing event callbacks — when a button is clicked, does it call the right function with the right arguments?"

#### jest.fn() (3 min)

```jsx
const mockOnEnroll = jest.fn();
```

> "`jest.fn()` creates a mock function. It does nothing by default but records every call. We pass it as the `onEnroll` prop — the component thinks it's a real function, but we can inspect what was called."

#### userEvent vs fireEvent (5 min)

```jsx
const user = userEvent.setup();
await user.click(screen.getByRole('button', { name: /enroll/i }));
```

> "Two ways to simulate events: `fireEvent` and `userEvent`. Which is better?"

**Write on board:**
```
fireEvent.click(button)   → dispatches ONE click event
user.click(button)        → dispatches pointer enter, pointer down, mouse down,
                            focus, pointer up, mouse up, click — the whole sequence
```

> "`userEvent` is more realistic. A real click generates many events — pointer, mouse, focus. If a button handler responds to `mousedown` rather than `click`, `fireEvent.click` misses it. `userEvent` catches it. Always prefer `userEvent` for user interactions."

> "Note `userEvent.setup()` — call this ONCE per test, before rendering. It creates a user interaction context."

**Walk through the assertions:**
```jsx
expect(mockOnEnroll).toHaveBeenCalledTimes(1);
expect(mockOnEnroll).toHaveBeenCalledWith(1);
```

> "`.toHaveBeenCalledTimes(1)` — not 0, not 2. Exactly once. `.toHaveBeenCalledWith(1)` — the course id was passed, not undefined or the entire course object."

---

### Segment 5 — State Changes & Async with waitFor (Sections 5–6, ~10 min)

> "The `Counter` tests are the simplest example of testing state changes. Look at the increment test."

```jsx
await user.click(screen.getByRole('button', { name: /increment/i }));
expect(screen.getByText('Count: 1')).toBeInTheDocument();
```

> "We click the button — React updates state — the DOM updates — we assert the new text content. All synchronous here, which is why we don't need `waitFor`. But `await user.click` is still awaited because userEvent returns a Promise."

#### waitFor (6 min)

> "Move to `EnrollButton`. This has async state — it simulates a network delay with setTimeout."

```jsx
await waitFor(() => {
  expect(screen.getByRole('button', { name: /enrolled ✓/i })).toBeInTheDocument();
});
```

> "`waitFor` accepts a callback and polls it until it passes — by default up to 1000ms with 50ms intervals. If the assertion never passes, it throws the last error."

> "When do you NEED `waitFor`? Any time the DOM change happens AFTER a Promise resolves, a timer fires, or an async operation completes. If you assert immediately after `user.click` on an async handler, the state hasn't updated yet."

**Watch-out:**
> "Don't wrap everything in `waitFor`. For synchronous updates, it's unnecessary overhead. Use it only when the DOM change is delayed."

**Walk through the `queryByText` in the last counter test:**
```jsx
expect(screen.queryByText(/loading/i)).not.toBeInTheDocument();
```

> "`queryBy` + `.not.toBeInTheDocument()` is the pattern for asserting something is ABSENT. `getBy` would throw immediately if the element doesn't exist — that's not what we want here. `queryBy` returns null, and `.not.toBeInTheDocument()` accepts null as 'not in document'."

---

### Segment 6 — Testing Forms with userEvent.type (Section 8, ~6 min)

> "The `SearchBox` tests show how to test forms."

```jsx
const input = screen.getByLabelText(/search courses/i);
await user.type(input, 'TypeScript');
await user.click(screen.getByRole('button', { name: /search/i }));
expect(mockOnSearch).toHaveBeenCalledWith('TypeScript');
```

> "Three important pieces:"

> "First: `getByLabelText` — we're targeting the input by its label text. This is accessible querying AND it forces you to write accessible HTML with proper `<label>` associations. If `htmlFor` doesn't match the input's `id`, this query fails — and that means your form is also inaccessible."

> "Second: `user.type` simulates real keystrokes. Each character fires keydown, keypress, input change, keyup. This is important for components that debounce on each keystroke."

> "Third: `toHaveValue('React')` — this jest-dom matcher checks the actual value of an input element. More semantic than `expect(input.value).toBe('React')`."

---

### Segment 7 — Testing Custom Hooks with renderHook (Sections 9–10, ~12 min)

> "Now we get into hook testing — one of the most useful capabilities of modern RTL."

> "The problem: hooks can only be called inside React components. Before `renderHook`, you'd have to create a dummy component just to test a hook. `renderHook` handles that wrapper for you."

#### Basic renderHook usage (5 min)

```jsx
const { result } = renderHook(() => useCounter(5));
expect(result.current.count).toBe(5);
```

> "`renderHook` returns an object. `result` is a ref — `result.current` is the LATEST return value of the hook. Think of it like a live view into the hook's state."

> "Why `result` instead of just `const counter = renderHook(...)`? Because the hook's return value changes every render — `result.current` always points to the newest value."

#### act() (4 min)

```jsx
act(() => {
  result.current.increment();
});
expect(result.current.count).toBe(1);
```

> "`act` wraps any code that causes React state updates. It tells React: 'flush all state updates, run all effects, THEN let me check the result.' Without `act`, React might not have processed the state update by the time you assert."

> "In practice, `userEvent` and `waitFor` call `act` internally — you rarely need it explicitly in component tests. But in hook tests, when you directly call functions that update state, you need `act`."

**Watch-out:**
> "You may see a React warning: 'An update to X inside a test was not wrapped in act(...)'. This means your test has async state updates that RTL doesn't know about — usually a Promise resolving after your test ends. Fix it by awaiting all async operations before the test completes."

#### useCart with useReducer (3 min)

> "The `useCart` tests show the same pattern for a reducer-based hook. The test doesn't care whether the hook uses `useState` or `useReducer` internally — it tests the BEHAVIOR (add, remove, clear) not the implementation."

> "Notice we can batch multiple actions in one `act`:"
```jsx
act(() => {
  result.current.addItem({ id: 1 });
  result.current.addItem({ id: 2 });
  result.current.clearCart();
});
```
> "All three dispatch calls happen, React batches the re-renders, and we get the final state."

---

### Segment 8 — Jest Matchers Reference (Section 11, ~3 min)

> "The last section in File 1 is a reference. I want to walk through the DOM-specific matchers quickly since those are the ones you'll use most."

**Highlight key matchers:**
- `toBeInTheDocument()` — is the element in the DOM?
- `toBeVisible()` — is it visible? (not `display:none`, not `visibility:hidden`)
- `toBeDisabled()` — is the form element disabled?
- `toHaveValue('text')` — what's the current input value?
- `toHaveClass('active')` — does the element have this CSS class?
- `toHaveTextContent(/regex/)` — does the text content match?
- `toHaveFocus()` — does the element currently have focus?

> "These read almost like English requirements documents. `expect(submitButton).toBeDisabled()` says exactly what the product requirement is."

---

## File 2: `02-mock-api-and-env-variables.test.jsx`

### Segment 9 — Why Mock APIs (Section 1, ~4 min)

> "Open File 2. Section 1 is four bullet points I want you to engrave in your brain."

**Read them out:**
- Fast — no real network
- Reliable — no dependency on a live server
- Isolated — no shared state between tests
- Controlled — we decide what the API returns

> "Imagine your test suite hits a real API. The API is down for maintenance at 2 AM when your CI pipeline runs. Your entire test suite fails. That's not a code bug — that's a flaky test due to external dependency. Mocking eliminates the dependency."

---

### Segment 10 — Mocking Axios (Sections 3–4, ~15 min)

> "The `CourseLoader` component calls `axios.get('/api/courses')` in a `useEffect`. Our tests need to intercept that call."

#### jest.spyOn approach (8 min)

```jsx
jest.spyOn(axios, 'get').mockResolvedValue({ data: mockCourses });
```

> "`jest.spyOn(object, 'method')` replaces that ONE method on the object with a mock while keeping everything else real. `.mockResolvedValue()` is shorthand for 'return `Promise.resolve(this value)`'."

**Walk through the happy path test:**

```jsx
test('renders courses after successful API response', async () => {
  const mockCourses = [
    { id: 1, title: 'React Fundamentals' },
    { id: 2, title: 'TypeScript Basics' },
  ];
  jest.spyOn(axios, 'get').mockResolvedValue({ data: mockCourses });

  render(<CourseLoader />);

  await waitFor(() => {
    expect(screen.getByText('React Fundamentals')).toBeInTheDocument();
  });

  expect(axios.get).toHaveBeenCalledWith('/api/courses');
});
```

> "Read the test from top to bottom: set up what the API returns, render the component, wait for the data to appear, verify the URL was correct. This covers the full user journey: component renders → API is called → data appears."

**Walk through the error test:**

```jsx
jest.spyOn(axios, 'get').mockRejectedValue(new Error('Network Error'));
```

> "`.mockRejectedValue` makes the Promise reject. The component's `.catch` handler sets the error message. We assert the error message appears. Test covers the failure path — equally important as the success path."

#### afterEach cleanup (3 min)

```jsx
afterEach(() => {
  jest.clearAllMocks();
});
```

> "This is CRITICAL. After each test, clear the mock state — the call count, the configured return value. If test 1 sets up a mock and test 2 doesn't, test 2 would accidentally use test 1's mock. Isolation."

> "`jest.clearAllMocks()` — resets call counts and instances  
> `jest.resetAllMocks()` — also resets return value implementations  
> `jest.restoreAllMocks()` — restores spied originals (use with `spyOn`)"

---

#### Mocking global.fetch (Section 4, ~4 min)

> "`CourseSearch` uses native `fetch` — not a module we can jest.mock. Instead we spy on the global."

```jsx
jest.spyOn(global, 'fetch').mockResolvedValue({
  ok: true,
  json: () => Promise.resolve(mockResponse),
});
```

> "Two things to mock: the fetch call itself AND the `.json()` method on the response — because our component calls `response.json()` as a second step."

> "Notice `afterEach(() => jest.restoreAllMocks())` — restores the real `fetch`. Don't skip this or subsequent tests will still be using the mock."

---

### Segment 11 — Mock Return Value Variants (Section 5, ~5 min)

> "Quick section — different ways to control what a mock returns."

**Walk through each:**

- `mockReturnValue(v)` — synchronous, returns this every time
- `mockResolvedValue(v)` — `Promise.resolve(v)` every time
- `mockRejectedValue(e)` — `Promise.reject(e)` every time
- `mockResolvedValueOnce(v)` — resolves with this value ONCE, then uses the default

> "The `Once` variants are powerful for testing retry scenarios:"

```js
mockFetch
  .mockRejectedValueOnce(new Error('Timeout'))    // first call fails
  .mockResolvedValueOnce({ data: [] });           // second call succeeds
```

> "With these, you can test: 'when the first request times out, does the user see a retry button, and when they retry, does the second request succeed?'"

---

### Segment 12 — MSW: Mock Service Worker (Section 6, ~8 min)

> "Now let's talk about the gold standard for mocking in React: Mock Service Worker. You don't need to implement it today — understand the concept and why it's better."

**Core idea:**
> "MSW intercepts HTTP requests at the network level using a Service Worker (in the browser) or a Node.js interceptor (in Jest). Your component uses real `axios.get` or real `fetch` — MSW catches the network request before it leaves the machine and returns your mocked response."

**Draw on board:**
```
Component → axios.get('/api/courses')
            ↓
            [MSW interceptor — 'I handle /api/courses']
            ↓
            Returns { data: [...] }  ← no network needed
```

> "The key benefit over jest.mock: your test and your browser dev environment use the SAME mock definitions. You set up handlers in `src/mocks/handlers.js` and use them in both Jest AND in the browser during development."

**Walk through the handler syntax:**
```js
http.get('/api/courses', () => {
  return HttpResponse.json([{ id: 1, title: 'React Fundamentals' }]);
});
```

> "Clean, readable URL matching. The handler returns what the server would return — no `mockResolvedValue` noise."

**Walk through `server.use()` for per-test overrides:**
```js
server.use(
  http.get('/api/courses', () => new HttpResponse(null, { status: 500 }))
);
```

> "Per-test overrides: replace a default handler for one test. `afterEach(() => server.resetHandlers())` restores the defaults after each test."

**Q&A prompt:** "When would you use `jest.spyOn` vs MSW?" → `spyOn` for simple quick tests, unit tests of specific axios calls; MSW for integration tests that more closely mirror production and for shared mocks across test and dev environments.

---

### Segment 13 — Environment Variables (Sections 7–9, ~10 min)

> "Switch context: environment variables. This is about configuration — not testing per se, but it's tested here because tests often need to control which environment they run in."

#### The concept (4 min)

> "Environment variables let you change app behavior between dev, test, and production WITHOUT changing source code. The URL for your API is different locally vs in production — you don't want to hardcode either one."

**Walk through .env file types:**
- `.env` — always loaded, committed to git (safe defaults)
- `.env.local` — always loaded, git-ignored (your personal secrets)
- `.env.development` — only in `npm start`
- `.env.production` — only in `npm run build`
- `.env.test` — only in `npm test`

> "The naming prefix is **mandatory**: CRA requires `REACT_APP_`, Vite requires `VITE_`. Variables without the prefix are intentionally hidden — they can't be accessed by the browser. This is a security guard."

#### The critical watch-out (3 min)

> "I need to stress this."

**Write on board in red:**
```
⚠️ .env variables are baked into your JS bundle at build time.
   They appear in plain text in the browser's Source tab.
   NEVER put API keys, passwords, or secrets here.
```

> "If you need to call an API with a secret key, build a backend endpoint that makes the call server-side. Your React app calls YOUR backend (no secret), YOUR backend calls the third-party API (with the secret, stored in a server environment variable). Never expose secrets to the browser."

#### Testing with process.env (3 min)

> "Look at Section 8. The pattern for modifying env vars in tests:"

```js
const ORIGINAL_ENV = process.env;
beforeEach(() => {
  process.env = { ...ORIGINAL_ENV };
});
afterEach(() => {
  process.env = ORIGINAL_ENV;
});
```

> "Make a copy before each test, restore the original after. This prevents env var changes from leaking between tests. Spread `...ORIGINAL_ENV` into the copy so you keep all existing variables and only override the ones you need."

> "Better option: use `.env.test` for test-wide defaults. Only use `process.env` manipulation in tests for specific overrides."

---

### Segment 14 — Integration Test: Component + API Mock + Env (Section 10, ~5 min)

> "Section 10 brings it all together: a component that reads an env var for its base URL and calls axios. The test sets the env var AND mocks axios AND verifies the URL includes the configured base URL."

```jsx
expect(axios.get).toHaveBeenCalledWith(
  'http://localhost:8080/api/courses'
);
```

> "This test ensures your service respects environment config. If someone hardcodes `https://prod-api.com` instead of using `process.env.REACT_APP_API_URL`, this test fails."

---

### Segment 15 — jest.fn() Patterns Reference (Section 11, ~3 min)

> "Last section — quick reference for inspecting mock calls."

```js
mockFn.mock.calls           // array of all calls: [[arg1, arg2], ...]
mockFn.mock.results         // array of return values: [{ type: 'return', value: ... }]
```

> "`.mock.calls` is useful when you need to inspect what was passed but `toHaveBeenCalledWith` isn't expressive enough — for example, checking that the first call's second argument was an object containing a specific key."

---

## Day 19a Full Wrap-Up (5 min)

> "Let's consolidate the day."

**Part 1 summary:**
```
Fetch:          two-step (response + .json()), manual .ok check
Axios:          auto-JSON, auto-throw, axios.create(), interceptors
Patterns:       loading state, error state, AbortController, useFetch hook
Error Boundaries: class component, getDerivedStateFromError, componentDidCatch
```

**Part 2 summary:**
```
Jest:           describe/test/expect, matchers, jest.fn(), spyOn
RTL:            render, screen queries (ByRole first), userEvent, waitFor
renderHook:     test hooks in isolation, act() for state updates
Mock APIs:      jest.spyOn().mockResolvedValue(), jest.spyOn(global, 'fetch')
MSW:            network-level interception, handlers, server.use() per-test
Env vars:       .env files, REACT_APP_ prefix, process.env in tests
```

> "These are professional-grade tools used in production React codebases. Tomorrow we'll continue with Angular HTTP & RxJS — the Angular ecosystem's equivalent of everything we covered today."

---

## Q&A Prompts

1. "What is the difference between `getByRole` and `queryByRole`?"
   - `getByRole` throws if the element is not found — use when you expect it to exist. `queryByRole` returns `null` — use when testing that an element is ABSENT.

2. "You have a test that works in isolation but fails when run with the full suite. What's a likely cause?"
   - Shared state leaking between tests — missing `jest.clearAllMocks()` or `afterEach` cleanup. A mock set in test A is still active in test B.

3. "Why can't you use an async function directly in `useEffect`?"
   - `useEffect` expects either nothing or a cleanup function as its return value. An async function returns a Promise — React doesn't know what to do with it. Define an async function inside and call it: `useEffect(() => { const load = async () => {...}; load(); }, [])`.

4. "What's the difference between `jest.mock('axios')` (at the module level) and `jest.spyOn(axios, 'get')`?"
   - `jest.mock('axios')` replaces the ENTIRE module with a mock — all exports become `jest.fn()`. `jest.spyOn` replaces ONE method and can restore the original. `spyOn` is preferred for surgical mocking; `jest.mock` is used when the whole module needs to be mocked.

5. "Why can environment variables be dangerous if misused?"
   - Variables prefixed `REACT_APP_` are embedded into the JavaScript bundle at build time and are visible to anyone who inspects the source in the browser. Secrets (API keys, tokens) must never be stored there — use a backend proxy instead.

---

## Take-Home Exercises

1. **Test the `CourseLoader` component** — write three tests: renders loading spinner on mount; renders course list from mock data; renders error message when API fails. Use `jest.spyOn(axios, 'get')`.

2. **Test the `useFetch` hook from Part 1** — use `renderHook` to verify: starts loading immediately; provides data after resolution; provides error message after rejection; `refetch` re-triggers the request.

3. **MSW setup** — install `msw` in a CRA or Vite project. Create `handlers.js` with a GET `/api/courses` handler. Set up `server.js`. Add `beforeAll/afterEach/afterAll` in a test. Write one test that uses the MSW handler without any `jest.spyOn`.

4. **Environment variable configuration** — create `.env.development`, `.env.production`, and `.env.test` files. Configure `REACT_APP_API_URL` differently in each. Write a test that verifies the production URL is used when `NODE_ENV=production`.

---

*Estimated total time: ~100 minutes (5 intro + 5 philosophy + 50 File 1 + 38 File 2 + 5 wrap-up)*

# Day 19a — Part 2: React Testing Library & Jest
## Lecture Script

**Delivery time:** ~60 minutes
**Pace:** ~165 words/minute
**Format:** Verbatim instructor script with timing markers

---

`[00:00–02:30]`

Welcome back. In Part 1 we learned how to get data into a React app — fetch, axios, the three-state pattern, Error Boundaries, environment variables. In Part 2 we answer the next question: how do you know any of that actually works?

The answer is automated tests. And I want to convince you of something before we write a single line of test code: tests are not a burden that slows you down. They're a tool that lets you move *faster* with *confidence*. Here's what I mean. Without tests, every time you change something, you manually click through the app to make sure you didn't break anything. That takes 10 minutes each time, and you still miss edge cases. With tests, you press one button, and in 30 seconds you get a report on whether all 50 scenarios you've defined still work correctly. The time you invest writing tests pays back every single time you refactor.

There's also this: tests are documentation. A well-named test — "shows an error message when the email field is empty" — tells the next developer exactly what the component is supposed to do. Better than a comment, because the test fails if the behavior changes.

Today's tools: Jest, which is the test runner and assertion library; React Testing Library, which renders components and queries the DOM; and `@testing-library/user-event`, which simulates realistic user interactions. Let's go.

---

`[02:30–09:00]`

**Slide 3 — Test setup in a Vite project.**

Let me first address something that confuses people: Vite projects use **Vitest**, not Jest. They look almost identical — same `describe`, `it`, `expect`, same `jest.fn()` syntax (though in Vitest you use `vi.fn()`). I'm going to say "Jest" throughout today because that's the term most documentation uses and because the API is identical. But if you're in a Vite project, you're technically using Vitest. The code translates directly — the only difference is `jest.fn()` becomes `vi.fn()` and `jest.mock()` becomes `vi.mock()`. Create React App projects use actual Jest.

Setup: `npm install --save-dev vitest jsdom @testing-library/react @testing-library/jest-dom @testing-library/user-event`. Then in `vite.config.js`, add a `test` block: `globals: true` so you don't need to import `describe` and `expect` in every file, `environment: 'jsdom'` so your components have a fake browser DOM to render into, and a `setupFiles` pointing to a setup file where you import `@testing-library/jest-dom`. That package adds a bunch of custom matchers — things like `toBeInTheDocument()`, `toHaveTextContent()`, `toBeVisible()`, `toBeDisabled()` — that make assertions on DOM nodes much more readable.

To run tests: `npm run test`. That gives you watch mode — tests re-run automatically when you save a file. `npm run test -- --run` for a single run, which is what you use in a CI pipeline. Let me take a moment to explain jsdom. Your component code expects a browser environment — `document`, `window`, DOM events. But our tests run in Node.js, not in a browser. jsdom is a JavaScript implementation of the browser DOM. It's not perfect — it can't do CSS layout, for example — but it handles everything we need for testing React components: rendering, querying, events.

---

`[09:00–18:00]`

**Slide 4 — Jest fundamentals.**

Before we touch React at all, let me show you the basic building blocks. Open any `.test.js` file and you'll see this structure.

`describe` groups related tests. It takes a string describing the thing you're testing — usually the function name or component name — and a callback with your tests inside. `describe('formatPrice', () => {...})`.

`it` or `test` defines an individual test. They're identical — just aliases. The string is the test name. Make it a complete sentence that reads like a specification. "formats a number as USD currency." "throws on negative price." The best test names read like user stories.

Inside `it`, you follow the Arrange-Act-Assert pattern. Arrange: set up whatever the test needs. Act: call the function or trigger the user interaction. Assert: verify the result with `expect`.

`expect` takes the value you want to inspect, and `.toBe()` or another matcher checks its condition. `toBe` uses strict equality — triple equals — so it's for primitives. For objects and arrays, use `toEqual`, which does deep equality. For DOM nodes, use `toBeInTheDocument`, `toHaveTextContent`, `toBeVisible`, `toBeDisabled`, and so on.

Other important matchers: `toBeTruthy`, `toBeFalsy`, `toBeNull`, `toBeUndefined`, `toContain` for arrays, `toHaveLength`, `toHaveBeenCalled` for mock functions, `toHaveBeenCalledWith` to verify the arguments, `toHaveBeenCalledTimes` to count how many times it was called.

The difference between `toBe` and `toEqual`: `expect([1,2,3]).toBe([1,2,3])` fails — two separate array objects, not the same reference. `expect([1,2,3]).toEqual([1,2,3])` passes — the contents are the same.

---

`[18:00–26:00]`

**Slide 5 — React Testing Library's core philosophy.**

Kent C. Dodds — the creator of React Testing Library — has a guiding principle: "The more your tests resemble the way your software is used, the more confidence they can give you."

What does that mean in practice? Your tests should interact with components the way a *user* interacts with them. A user sees text, clicks buttons, fills in form fields. A user does not look at your component's internal state. A user does not care whether you're using `useState` or Redux. A user does not care what CSS class a button has.

Before React Testing Library, teams used Enzyme, which let you directly inspect component internals — state variables, props passed to child components, what class names were applied. The problem: those tests break every time you refactor. You change from `useState` to a reducer, and suddenly 30 tests fail — not because the component does the wrong thing for the user, but because the internal implementation changed.

React Testing Library doesn't give you access to internals. You can only interact with what the user sees in the DOM. That's the constraint, and it's the feature — it forces you to write tests that survive refactoring.

So when you write RTL tests, ask yourself: "What would a user see? What would they click? What would change?" That's your test.

---

`[26:00–36:00]`

**Slide 6 — RTL query methods.**

The most important skill in React Testing Library is knowing how to *find* elements in the rendered DOM. There are three query families, and the difference between them is what happens when the element isn't found.

`getBy...` — throws an error if no match. Use it when the element *should* be there. If it's not, the test fails with a clear message.

`queryBy...` — returns `null` if no match. Use it when you're asserting that something is *absent*. `expect(screen.queryByText('Loading...')).not.toBeInTheDocument()`. If you used `getBy` for this, it would throw before you even got to the `expect`.

`findBy...` — returns a promise that resolves when the element appears. Use it for async content — things that appear after a fetch or a timer. It polls until it finds the element or times out after 1 second by default.

And for all three families, there are `AllBy` variants that match multiple elements — `getAllByRole`, `queryAllByText`, `findAllByRole`.

Now — what do you query *by*? React Testing Library gives you a priority order, and it's not arbitrary. The highest priority selectors are the most accessible ones. Role is first — `screen.getByRole('button', { name: /submit/i })`. This is how screen readers identify elements, so if you can query by role, your app is accessible. Label text is second — for form inputs: `getByLabelText('Email address')`. Text content is fourth — `getByText('Submit Order')`, or with a regex: `getByText(/submit/i)` for case-insensitive matching. And test ID is last resort — `getByTestId('product-card')` — because it's not visible to users and requires you to add `data-testid` attributes.

Why does this order matter? It's a gentle forcing function. If you can't query by role, that often means your component isn't accessible. RTL is teaching you accessibility as you write tests.

---

`[36:00–43:00]`

**Slide 7 — Testing a component — full example.**

Let's write a complete test file. Our component is `ProductCard` — it takes a `product` prop and an `onAddToCart` prop. It renders the name, the price, and a button. When the button is clicked, it calls `onAddToCart` with the product.

Three tests. First: "renders the product name and price." I call `render(<ProductCard product={mockProduct} onAddToCart={() => {}} />)`. Then I `getByText('Test Widget')` and `getByText('$9.99')` and assert they're in the document. Clean, simple.

Second: "calls onAddToCart with the product when button is clicked." This one uses `jest.fn()` — a mock function. Mock functions are functions that record every time they're called, what arguments they received, how many times. I create `const mockAddToCart = jest.fn()`, pass it as the prop, find the button with `getByRole('button', { name: /add to cart/i })`, and click it with `await userEvent.click(button)`. Then I assert: `expect(mockAddToCart).toHaveBeenCalledTimes(1)` and `expect(mockAddToCart).toHaveBeenCalledWith(mockProduct)`.

That second test is extremely powerful. You're not checking internal state. You're not checking what function was called internally. You're checking what the *user interaction* ultimately results in: the callback that was passed in gets called with the right data. That's the behavior the parent component cares about.

Third: just verifies the button is present. Notice `{ name: /add to cart/i }` uses a regex — the `/i` flag makes it case-insensitive, so "Add to Cart," "add to cart," "ADD TO CART" would all match.

**Slide 8 — Testing async components.**

Now let's test a component that fetches data. This is where things get interesting because we can't let it hit a real server — tests need to be fast, deterministic, and isolated. We mock fetch.

`beforeEach(() => { global.fetch = jest.fn(...) })`. You're replacing the global `fetch` function with a mock that immediately resolves with fake data. `jest.fn(() => Promise.resolve({...}))` — a function that returns a pre-resolved promise.

`afterEach(() => { jest.resetAllMocks() })` — clean up after each test so mocks don't leak between tests.

For the first test — "shows loading state initially" — I just render the component and immediately check for "Loading products...". At that instant, the fetch hasn't resolved yet, so the loading state should be showing. This works because the mock fetch is asynchronous — even though it resolves immediately, the resolution is queued as a microtask, so the render happens before the data arrives.

For the second test — "renders products after fetching" — I render, then use `await screen.findByText('Widget A')`. `findBy` returns a promise that keeps checking until "Widget A" appears in the DOM. Once the fetch resolves and the component re-renders with data, the element appears and `findBy` resolves. I don't need to set a specific wait time — RTL handles the polling.

`waitFor` is similar but more flexible. You can put any assertion inside `waitFor(() => {...})`, and it'll keep retrying until the assertion passes or times out. I use `waitFor` to assert that the loading spinner is *gone* — you can't use `findBy` for that because you're asserting absence.

---

`[43:00–50:00]`

**Slide 9 — Testing hooks with renderHook.**

Custom hooks are logic, not UI — you can't render them directly. React Testing Library provides `renderHook` for this. `const { result } = renderHook(() => useFetch('/api/products'))`. `result.current` gives you whatever the hook returned — `{ loading, data, error }`.

Let's test `useFetch`. First test: "starts with loading=true." Right after calling `renderHook`, before any promise resolves, `result.current.loading` should be `true`. `data` should be `null`, `error` should be `null`. Clean initial state.

Second test: "returns data when fetch succeeds." I mock fetch to resolve with data, call `renderHook`, then use `await waitFor(() => { expect(result.current.loading).toBe(false) })`. That waits until the loading state clears — meaning the fetch resolved — and then I can check `result.current.data`.

Third test: "returns error when fetch fails." I mock fetch to resolve with `{ ok: false, status: 500, statusText: 'Server Error' }`. Note that fetch itself doesn't throw here — it resolves with a bad-status response, just like the real fetch API does. The hook should check `response.ok`, throw manually, and set the error state. I wait for loading to be false, then check that `result.current.error` has the expected message.

Testing custom hooks this way is more efficient than testing through a component. You're testing the hook logic directly — no need to render any UI, no need to find DOM elements. It's faster to write and easier to read.

---

`[50:00–57:00]`

**Slide 10 — Testing Redux-connected components.**

If your component uses `useSelector` or `useDispatch`, it needs a Redux `<Provider>` wrapper in tests — just like in the real app. But you don't want to use your real store. You want to create a test store that starts with exactly the state you specify for each test.

Here's the `renderWithRedux` utility. It calls RTL's `render` with a `wrapper` option — a function that wraps the component in a `<Provider>` with a freshly created test store. You pass `preloadedState` to `configureStore` to set the initial state. And you return the store alongside RTL's render result so you can call `store.getState()` in assertions.

Now tests can preload specific state scenarios. "Cart has one item with qty 2, total $19.98" — you set that in `preloadedState` and render. No clicking, no user interactions needed to reach that state — you just start there. This makes tests deterministic and much faster to write.

The second pattern in that slide — testing a dispatch. After clicking "Clear Cart," I call `store.getState().cart.items` and assert it's empty. I'm looking at the Redux store directly to verify the action was dispatched and the reducer handled it. This is fine because the store is a JavaScript object — checking it doesn't violate RTL's "no internals" philosophy. The store IS the external interface; the component dispatches to it, and I'm verifying the result.

**Slide 11 — Mocking API calls with jest.mock.**

`jest.mock('../api/client')` — this replaces the entire module with auto-generated mock functions. Every export becomes a `jest.fn()` that returns `undefined` by default. Then in each test, you configure what that function returns: `api.get.mockResolvedValueOnce({ data: [...] })`. `ResolvedValueOnce` means it returns this value for the *next* call only — so each test gets clean mocks.

`mockRejectedValueOnce` for simulating errors: `api.get.mockRejectedValueOnce(new Error('Server unavailable'))`. This lets you test your error states without actually having a broken server.

Why is this better than mocking `global.fetch`? Because you're mocking at the right level. If you mock fetch, you're testing through the HTTP layer. If you mock your API client, you're testing "my component handled the data it received correctly." The API client is its own unit — test that separately. In component tests, just mock it out.

---

`[57:00–60:00]`

**Slide 16 — Best practices.**

Test user behavior, not implementation. Check what's rendered, not what state variable holds what value. Query by role and label before falling back to text content or test IDs. This keeps tests accessible and resilient.

One test per behavior. Not one test per component, not one massive test that checks everything. Each `it` should test exactly one thing. If a test name contains "and," consider splitting it.

Use `beforeEach` for setup, `afterEach` for cleanup, `jest.resetAllMocks()` after each test. Mocks that leak between tests create mysterious intermittent failures.

And test at multiple levels. Pure functions and slice reducers: unit tests, no rendering needed, just import and call. Custom hooks: `renderHook`, no UI. Components: RTL with mocked dependencies. Integration: `renderWithProviders` testing multiple components working together.

**Slide 17 — Day 19a Summary and looking ahead.**

Today you went from a React app that only had hardcoded data to one that fetches real APIs, handles all the things that go wrong, and has an automated test suite to prove it.

Coming up on Day 19b, the Angular track covers `HttpClient` and `RxJS` — the Angular equivalent of everything you did today. If you're on the Angular path, you'll see the same ideas — observables instead of promises, `catchError` instead of try/catch — but the fundamental mental model is identical.

And Day 20a dives into React advanced patterns: `React.memo`, `useMemo`, `useCallback` for performance, code splitting with `React.lazy` and `Suspense`, and deploying your app with Vite's production build. We're in the final stretch of the React track — great work today.

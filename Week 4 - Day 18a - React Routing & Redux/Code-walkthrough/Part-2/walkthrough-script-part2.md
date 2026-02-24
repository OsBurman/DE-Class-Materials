# Part 2 Walkthrough Script — Day 18a: React Routing & Redux
## Redux Fundamentals · Redux Toolkit · React-Redux · useSelector & useDispatch · DevTools · State Patterns
### Estimated time: ~100 minutes

---

## Before You Start

> "Welcome back from the break. We just covered React Router — client-side navigation, route parameters, nested layouts, HOCs. Now we're going to tackle **Redux** — the most well-known solution for global state management in React.
>
> I want to start with a question before we touch any code. Raise your hand if you've built a React app where two components on completely different parts of the page both needed to know about the same piece of data — like a shopping cart total, or whether the user is logged in. How did you handle that? Context? Props drilled through five layers? Yeah, those work — but they get messy fast. Redux is a systematic, industry-proven solution to that exact problem.
>
> We're going to do this in two passes. File 1 is vanilla Redux — no shortcuts, no magic. I want you to understand the fundamentals. File 2 is Redux Toolkit — the official modern way that removes all the boilerplate."

---

## File: `01-redux-fundamentals.js`

### [INTRO — 3 min]

> "Open `01-redux-fundamentals.js`. Notice this is a `.js` file, not `.jsx`. There's no React here at all. We can run this in Node.js directly — `node 01-redux-fundamentals.js`. This is intentional. I want you to see that Redux is NOT a React library. It's a state management pattern that happens to work beautifully with React."

---

### [SECTION 1 — The problem Redux solves — 5 min]

> "Read through Section 1 with me — the comment block describing the tree diagram."

Read it aloud, then:

> "Imagine you have a shopping cart count in the navbar and a cart summary in the sidebar. With React alone, you'd have to lift that state up to the `App` component and then pass it down as props through every intermediate component — that's prop drilling. If you later add a new page that also needs the cart, you repeat the process. It gets painful.
>
> Redux solves this by pulling that shared state OUT of the component tree entirely and into a **store** that any component can read from directly."

Draw on board:

```
WITHOUT Redux:     App (cart state here)
                   ├── NavBar ← needs cart
                   └── Main
                       └── Sidebar ← needs cart (drilled 3 levels down)

WITH Redux:        Store (cart state here)
                   ├── NavBar ← reads directly from Store
                   └── Main
                       └── Sidebar ← reads directly from Store
```

---

### [SECTION 2 — Actions — 8 min]

> "Scroll to Section 2. Actions are the most important concept in Redux. Read this definition with me: **An action is a plain JavaScript object with a `type` property.**"

> "The `type` is a string that describes WHAT happened. Think of it like a news headline — it doesn't DO anything, it just reports the event. `'courses/enroll'` means 'the user enrolled in a course'. The slash-separated prefix is a namespace convention — it makes the action type unique and scannable."

> "The `payload` property carries the data. For enrolling, the payload is the course ID. It's just called `payload` by convention — you could call it anything, but the ecosystem all uses `payload`, so stick with it."

> "**Ask the class:** Is an action a function or an object? An object. What about an action CREATOR? Right — a function that returns an action object. Why bother with creators? Look at the two ways to dispatch:"

```js
// Without creator:
dispatch({ type: 'courses/enroll', payload: 3 });

// With creator:
dispatch(enrollCourse(3));
```

> "The creator is shorter, readable, and if you ever change the type string, you change it in ONE place. Always use action creators."

---

### [SECTION 3 — Reducers — 12 min]

> "Now Section 3 — reducers. This is where the actual state changes happen. Read the definition: **A reducer is a pure function: `(currentState, action) => nextState`.**"

> "**Ask the class:** What does 'pure' mean in programming? Great — same inputs always produce the same outputs, no side effects. No API calls, no console.log, no random numbers inside a reducer. Reducers must be deterministic — given the same state and the same action, they ALWAYS return the same result. This is what makes Redux time-travel debugging possible."

> "**Immutability:** Look at the `enroll` case in `coursesReducer`. We return a NEW object using spread syntax:"

```js
return {
  ...state,
  enrolled: [...state.enrolled, action.payload],
};
```

> "We're not pushing to the existing array. We're creating a BRAND NEW array and a BRAND NEW state object. Why? Redux detects changes by comparing references. If I push to the existing array, the reference doesn't change — Redux sees old state === new state and doesn't trigger any re-renders. Your UI breaks in subtle, hard-to-debug ways."

> "**Watch out:** This is the #1 Redux bug I see from new developers — accidentally mutating state. `state.enrolled.push(id)` looks innocent but it breaks Redux. Always return new objects and arrays."

Point to the `default` case:

> "The `default` case is REQUIRED in every Redux reducer. When Redux initializes, it calls every reducer with an unknown action to get the initial state. If you don't return something in the default case, your initial state is `undefined`."

---

### [SECTION 4 — Combining reducers — 5 min]

> "As apps grow, one reducer handling ALL state becomes unmanageable. `combineReducers` lets us split the state into slices — each reducer owns one key in the state tree."

Show the resulting state shape:

```js
{
  courses: { enrolled: [] },
  cart:    { items: [] },
  auth:    { user: null, isLoggedIn: false },
}
```

> "When you dispatch `enrollCourse(3)`, Redux calls ALL three reducers. `coursesReducer` handles it and returns a new courses slice. `cartReducer` and `authReducer` see an unrecognised action type, hit the `default` case, and return their current state unchanged. `combineReducers` reassembles the three results into the new root state."

---

### [SECTION 5–6 — Store, getState, subscribe — 7 min]

> "Section 5: `createStore(rootReducer)`. The store object is what holds everything together. It has three methods we care about:"

> "- `getState()` — returns the current state snapshot  
> - `dispatch(action)` — sends an action through the reducers  
> - `subscribe(fn)` — registers a callback that fires after every dispatch  

> "Look at the subscriber code — it calls `store.getState()` inside and logs the new state. It also returns an `unsubscribe` function. In React, you'll NEVER call subscribe directly — `useSelector` does it for you. But knowing it exists helps you understand how useSelector works under the hood."

---

### [SECTION 7 — Dispatching actions — 5 min]

> "Scroll to Section 7. Let's read through the dispatch sequence together. First we log in, then enroll in courses, then add to the cart. Notice we dispatch `enrollCourse(101)` twice — the reducer's guard clause returns unchanged state the second time, so no duplicate."

> "**Ask the class:** Before I scroll down to the final state log — what do you think `finalState.courses.enrolled` contains? Right — just `[101, 102]`, because we unenrolled 102 later."

---

### [SECTION 8 — Data flow diagram — 3 min]

> "Section 8 — the Redux data flow. This diagram is the most important thing to commit to memory. Read it top to bottom:
>
> User interaction → dispatch(action) → Reducer → New state → Components re-render
>
> Everything flows in ONE direction. That's what makes Redux applications predictable. When something breaks, you ask: what action was dispatched? What did the reducer do with it? The answers are visible and traceable."

> "**Transition:** Alright — you now understand vanilla Redux from the ground up. The problem is that writing separate files for action types, action creators, and reducers for every feature is A LOT of boilerplate. That's exactly what Redux Toolkit solves. Let's open File 2."

---

---

## File: `02-redux-toolkit-and-react.jsx`

### [INTRO — 3 min]

> "Open `02-redux-toolkit-and-react.jsx`. This is the modern way to write Redux — the official recommendation from the Redux team. If you join a company and they use Redux, they're almost certainly using Redux Toolkit."

> "This file imports from TWO packages: `@reduxjs/toolkit` for Redux logic and `react-redux` for the React bindings. Let's go through each new concept."

---

### [SECTION 2 — createSlice — 15 min]

> "The star of Redux Toolkit is `createSlice`. Look at the `coursesSlice` definition. One function call creates everything you'd write by hand in vanilla Redux: the initial state, the action types, the action creators, AND the reducer."

Point to the structure:

> "Three properties matter here:
>
> - `name` — becomes the prefix for all action type strings. `'courses'` means the enroll action type is `'courses/enroll'`.
> - `initialState` — your starting state for this slice.
> - `reducers` — an object where each key becomes BOTH a case in the reducer AND an action creator."

> "**Ask the class:** Look at the `enroll` reducer. What's different from vanilla Redux? Right — we're calling `state.enrolledIds.push(courseId)`. That looks like mutation! In vanilla Redux that would break everything. What's different here?"

> "RTK uses a library called **Immer** internally. Immer wraps your reducer state in a Proxy. When it detects mutations, it creates a new immutable state automatically. You get to write clean, readable code that LOOKS mutable but is actually safe."

> "**Watch out:** Immer only works INSIDE createSlice reducers. You cannot write mutating code in regular reducers, useEffect, event handlers, etc. Only inside a createSlice `reducers` or `extraReducers` callback."

Point to the exports:

> "`coursesSlice.actions` is an object containing all the action creators, named exactly as you named the reducer functions. We destructure and export them: `export const { enroll, unenroll } = coursesSlice.actions`."

> "`coursesSlice.reducer` is the reducer function we pass to `configureStore`."

---

### [SECTION 5 — createAsyncThunk — 10 min]

> "Scroll to Section 5 — `createAsyncThunk`. This is how Redux handles async operations like API calls."

> "Read the comment: it automatically dispatches THREE action types — `pending`, `fulfilled`, and `rejected`. We handle each in the slice's `extraReducers` (look back at the `coursesSlice`, the `extraReducers` block)."

> "The flow:
> 1. Component calls `dispatch(fetchCourses())`.
> 2. Redux dispatches `'courses/fetchAll/pending'` — loading state turns on.
> 3. The async function runs (simulated API call).
> 4. If it resolves: `'courses/fetchAll/fulfilled'` fires — data lands in state.
> 5. If it throws: `'courses/fetchAll/rejected'` fires — error stored in state."

> "**Ask the class:** Why can't we just put the async API call directly in the reducer? Because reducers must be PURE — no side effects, no async operations. Thunks are the escape hatch for async logic."

---

### [SECTION 6 — configureStore — 5 min]

> "Section 6: `configureStore`. Compare it to vanilla Redux's `createStore`. Instead of manually calling `combineReducers` and wiring up DevTools and middleware, `configureStore` does all of that for you."

> "You just pass a `reducer` object — each key is a slice name, each value is the slice's reducer function. That's it. DevTools support is automatic. `redux-thunk` middleware (needed for async thunks) is included by default."

---

### [SECTION 7 — Selectors — 5 min]

> "Section 7 — selectors. These are functions that read from state. They follow the pattern `state => state.someSlice.someValue`."

> "Notice `selectEnrolledCourses` and `selectCartTotal` — these derive new data FROM the raw state. `selectEnrolledCourses` cross-references two pieces of state to return the full course objects for enrolled IDs. `selectCartTotal` reduces the cart items to a total price."

> "This is the **selector pattern** — an important architectural habit. Instead of scattering `state.courses.allCourses.filter(...)` logic across your components, you define it once as a named selector and import it everywhere. When the state shape changes, you update ONE selector."

---

### [SECTION 8 — useSelector and useDispatch — 15 min]

> "Now we get to the React integration. Look at `CourseCard`."

Point to `useSelector`:

> "`useSelector(selectEnrolledIds)` — this hook:
> 1. Calls your selector with the current state
> 2. Returns the result
> 3. Re-renders the component whenever the returned value CHANGES
>
> It's like `useState` but the state lives in Redux, not in the component."

> "**Watch out:** `useSelector` uses reference equality by default. If your selector returns a new array or object on every call even when the data hasn't changed, your component will re-render unnecessarily. This is why we filter and derive in selectors — and for expensive derivations, use `createSelector` from the `reselect` library to memoize results."

Point to `useDispatch`:

> "`useDispatch()` returns the store's `dispatch` function. From there it's just `dispatch(actionCreator(payload))`. The action flows through all reducers, state updates, and components re-render. Same flow we traced in File 1 — RTK just makes the code cleaner."

Walk through `handleEnroll`:

> "`dispatch(enroll(course.id))` — `enroll` is the action creator from `coursesSlice.actions`. It returns `{ type: 'courses/enroll', payload: course.id }`. Redux updates the store. `useSelector` in CourseCard and EnrolledList both see the change and re-render. The enrolled badge appears on the card AND the list updates — with zero prop drilling."

---

### [SECTION 9 — Provider — 5 min]

> "Scroll to the `ReduxApp` component at the bottom. Notice `<Provider store={store}>` wrapping everything. This uses React Context to make the store available to every `useSelector` and `useDispatch` hook in the tree."

> "**Watch out:** If you forget `<Provider>`, you'll get a runtime error: 'could not find react-redux context value'. Always wrap your root component — in a real project, this goes in `index.jsx` or `main.jsx`, not deep in the tree."

---

### [SECTION 10 — Redux DevTools — 8 min]

> "Read the DevToolsNote comment at the bottom. If you have the Redux DevTools browser extension installed, open your browser DevTools right now and click the Redux tab."

Walk through each DevTools feature live:

> "1. **Action log** — click 'Enroll' on a course. You'll see `courses/enroll` appear in the log. Click it to expand and see the action payload.
>
> 2. **Diff view** — switch to the Diff tab. It shows you EXACTLY what changed in the state tree after that action. Green = added, red = removed.
>
> 3. **State tree** — the State tab shows you the entire current Redux store. Expand `courses → enrolledIds` and watch it update as you enroll.
>
> 4. **Time travel** — click any past action in the log and use the 'Jump' button. The app rewinds to that exact point in time! This works because Redux state is immutable — every snapshot is preserved.
>
> 5. **Import/Export** — you can export the entire state history as a JSON file. Send it to a colleague, they import it, and they see your exact bug. This is one of the most powerful debugging tools in the React ecosystem."

> "**Ask the class:** Why is time-travel debugging possible in Redux but NOT with component-level useState? Because Redux state is immutable — every action produces a new snapshot. With useState, mutations happen in place and the history is lost."

---

### [SECTION 11 — State management patterns — 7 min]

> "Scroll to the patterns comment block at the bottom. Let me walk through the four patterns."

**Pattern 1: Normalised state:**
> "When you have a list of items, don't store them as an array — store them as a dictionary keyed by ID. Array lookups are O(n); dictionary lookups are O(1). RTK even has a helper called `createEntityAdapter` that does this automatically."

**Pattern 2: Slice ownership:**
> "Keep UI state (isModalOpen, activeTab) in separate slices from server data (courses, cart). They change for different reasons and have different lifecycles."

**Pattern 3: Selector colocation:**
> "Define selectors in the same file as the slice they read. Export and import them. When the state shape changes, there's ONE place to update."

**Pattern 4: What belongs in Redux vs local state:**
> "This is the most practical question. Rule of thumb: if multiple components need it, or if it needs to survive navigation, use Redux. If only ONE component cares, use `useState`."

Draw the mental model:

```
Global (Redux):         Server data, auth, cart, notifications, theme
Local (useState):       Form values, isOpen, hover/focus states
```

---

### [PART 2 WRAP-UP — 5 min]

> "Let's recap the whole day.
>
> **Part 1 — React Router:**
> - `BrowserRouter` → `Routes` → `Route` maps URLs to components
> - `Link` for navigation; `NavLink` for active-state nav links
> - `useParams` for path parameters; `useSearchParams` for query strings
> - `useNavigate` for programmatic navigation
> - Nested routes share layouts via `<Outlet>`
> - HOCs wrap components to add cross-cutting behaviour
>
> **Part 2 — Redux:**
> - The store is a single source of truth — one state tree for the whole app
> - Actions are plain objects describing WHAT happened
> - Reducers are pure functions computing the NEXT state
> - Redux Toolkit: `createSlice` removes boilerplate; Immer makes immutability safe
> - `configureStore` sets up DevTools and middleware automatically
> - `<Provider>` makes the store available to all React components
> - `useSelector` reads state; `useDispatch` sends actions
> - DevTools give you action logs, diffs, state tree, and time travel
> - Selectors are the right place to compute derived state
>
> Tomorrow (Day 19a) we're connecting React to real APIs — Fetch, Axios, error boundaries, and then React Testing Library."

---

## Q&A Prompts for Part 2

1. "What are the three things a Redux reducer must NEVER do?"
2. "What does `createSlice` generate? Name all three things."
3. "I dispatch an action. Walk me through exactly what happens."
4. "What's the difference between `useSelector` and `useDispatch`?"
5. "When should you keep state local in `useState` instead of in Redux?"

---

## Take-Home Exercises

1. **Add a `wishlist` slice** — create a `wishlistSlice` with `addToWishlist` and `removeFromWishlist` actions. Add a Wishlist panel component that shows all wishlisted courses.

2. **Add a filter slice** — create a `filtersSlice` that stores the currently selected category and level. Wire `CourseList` to only show courses matching the active filters.

3. **Persist the cart** — use the `subscribe` API to save `store.getState().cart` to `localStorage` on every change, and preload it as the cart slice's `initialState`.

4. **Combine routing + Redux** — wrap the `ReduxApp` in a `BrowserRouter`. Add a `/courses/:id` route that shows a detail page for a course, reading its data with `useParams` and `useSelector`.

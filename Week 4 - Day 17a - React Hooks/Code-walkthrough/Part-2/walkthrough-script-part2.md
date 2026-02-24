# Walkthrough Script — Part 2: React Hooks (Advanced)
## Day 17a | Week 4 | ~90 minutes

---

## Pre-Session Setup

- [ ] Open `Part-2/` folder in VS Code
- [ ] Browser open to `localhost:5173` (Vite dev server running)
- [ ] Browser DevTools open → Console tab visible
- [ ] Have localStorage cleared: DevTools → Application → Storage → Clear All
- [ ] Slides showing React Hooks Part 2 title slide

---

## Recap From Part 1 (5 min)

> "Let's take 2 minutes to anchor where we are before we go further."

Quick board recap — draw or point at the summary:

```
Part 1 covered:
  useState  — local state (primitives, objects, arrays)
  useEffect — side effects (mount, update, cleanup)
  Events    — onClick, onChange, onSubmit (synthetic events)
```

Ask the class:
- "What does the dependency array in useEffect control?"
- "When does a cleanup function run?"

Transition:
> "Part 1 covered the foundational hooks. Part 2 is where we apply them to real-world patterns — forms, references, sharing state across the tree, extracting reusable logic, and understanding the full component lifecycle."

---

## Segment 1 — Controlled vs Uncontrolled Forms & useRef (20 min)

**File:** `01-controlled-and-uncontrolled-forms.jsx`

---

### 1a. Open `ControlledInputTypes` (5 min)

> "A controlled input means React owns the value — it lives in state. Every keystroke fires an event, state updates, React re-renders, the input shows the new value. React is the single source of truth."

Walk through each input type:
- `text` / `email` → `value={state}` + `onChange={e => setState(e.target.value)}`
- `select` → same pattern — `value` controls which option is selected
- **Highlight the checkbox**: `e.target.checked` not `e.target.value`
  > "This trips up almost everyone the first time. Checkbox gives you `.checked`, a boolean."
- `radio` → `checked={level === 'intermediate'}` — compare state value to option value

---

### 1b. Open `RegistrationForm` (5 min)

> "Now a full form — watch the generic handleChange function."

Point out the computed property key trick:
```js
setForm(prev => ({ ...prev, [name]: value }))
```
> "The square brackets around `name` aren't an array — they're a computed key. `name` is the string from `e.target.name`, so if the user is typing in the email field, this sets `form.email`. One function handles every field."

Demo:
1. Submit with all fields empty → show all errors
2. Fill in fields → show errors clear per-field as you type

---

### 1c. Open `UncontrolledForm` (4 min)

> "Uncontrolled inputs: React doesn't manage the value — the DOM does. We access it via a ref when we need it."

Show:
```js
const nameRef = useRef(null);
// ...
<input ref={nameRef} defaultValue="" />
// ...
nameRef.current.value  // read on submit
```

> "Notice `defaultValue` instead of `value`. If you wrote `value` here with no onChange, React would warn you and the input would be read-only."

Ask: "When would you choose uncontrolled?" → File inputs, third-party widgets, performance-sensitive large forms.

---

### 1d. Open `RenderCounter` (4 min)

> "Here's something subtle about useRef — it's not just for DOM references. It's a mutable box that persists across renders but does NOT trigger a re-render when you change it."

Show the two side-by-side:
```js
const renderCount = useRef(0);   // mutable, persists, NO re-render
const [count, setCount] = useState(0);  // causes re-render when changed
```

> "We use the ref to count renders because if we used state for that, each state update would itself cause another render — infinite loop."

Demo the `prevCount` pattern — ref stores the previous value, updated in a useEffect after render.

---

## Segment 2 — Context API & useContext (20 min)

**File:** `02-context-api-and-useContext.jsx`

---

### 2a. Prop Drilling Problem (4 min)

Open `PropDrillingProblem`. Run it — it works.

> "This works. But look at Dashboard — it receives user and userName purely to pass them down to Sidebar, which also just passes them to UserBadge. Dashboard and Sidebar don't use these props at all. That's prop drilling."

Draw on board:
```
App (has user)
  └── Dashboard (receives user — doesn't use it)
        └── Sidebar (receives user — doesn't use it)
              └── UserBadge (actually uses user ← only this one)
```

> "For 2 levels this is annoying. For 5+ levels this becomes a maintenance nightmare."

---

### 2b. Context solution (8 min)

Open `ContextSolution`.

> "Context is a way to broadcast data to any component in a subtree, without passing props through every layer."

Walk through the 3 steps — draw on board or annotate in code:

**Step 1 — Create the context:**
```js
const UserContext = createContext({ user: null, setUser: () => {} });
```
> "The argument to createContext is the default value — used only when a component consumes context without a Provider above it. We'll always have a Provider, so this is mainly for TypeScript autocomplete."

**Step 2 — Provide the context:**
```jsx
<UserContext.Provider value={{ user, setUser }}>
  {children}
</UserContext.Provider>
```
> "Any component inside this Provider can read `user` and `setUser` directly. No prop passing needed."

**Step 3 — Consume it:**
```js
const { user, setUser } = useContext(UserContext);
```

Demo: click Login → UserBadge updates without any props being passed through Sidebar.

---

### 2c. Custom hook wrapping context (4 min)

Show the `useUser()` custom hook:
```js
function useUser() {
  const context = useContext(UserContext);
  if (!context) throw new Error('useUser must be used inside UserProvider');
  return context;
}
```
> "This is a nice pattern — instead of importing both the hook and the context everywhere, components just call `useUser()`. And we get an error message that tells us exactly what went wrong."

---

### 2d. Multiple contexts (4 min)

Show `MultipleContextsDemo` — nested providers:
```jsx
<ThemeContext.Provider value={themeValue}>
  <UserContext.Provider value={userValue}>
    <App />
  </UserContext.Provider>
</ThemeContext.Provider>
```

> "You can have as many contexts as you need. They nest like wrappers. `ThemedCard` consumes both — it doesn't care about the nesting order."

Ask: "Should you put everything in one global Context?" → No — split by concern, keep them small.

> "Context is NOT a replacement for all state management. For highly dynamic data that changes often (like a live search query), using context can cause unnecessary re-renders across the whole tree. For relatively stable data — auth user, theme, language — it's perfect."

---

## Segment 3 — Custom Hooks (25 min)

**File:** `03-custom-hooks.jsx`

---

### 3a. What is a custom hook? (3 min)

> "A custom hook is any function whose name starts with `use` and that calls at least one built-in hook. That's literally the entire definition."

Write on board:
```
Custom Hook = "use" prefix + calls at least one hook inside
```

> "Why build them? Extract logic from components. Reuse it across multiple components. Test it in isolation. Your component describes what to show; the hook handles the behavior."

---

### 3b. Rules of Hooks — open `RulesOfHooks` (4 min)

> "Before we write hooks, two rules that you must never break."

**Rule 1: Only call hooks at the top level**
```js
// ❌ WRONG
if (condition) {
  const [x, setX] = useState(0);  // breaks hook ordering
}

// ✅ CORRECT
const [x, setX] = useState(0);
if (condition) { /* use x here */ }
```
> "React tracks hooks by call ORDER, not by name. If you skip a hook call with a condition, the order changes, and React assigns state to the wrong hook. The app breaks in subtle ways."

**Rule 2: Only call hooks from React functions**
> "Not from plain JS utility functions. Not from class methods. Only from components or other custom hooks."

> "The good news: `eslint-plugin-react-hooks` is installed in Create React App and Vite by default. It will yell at you before you even run the code."

---

### 3c. useLocalStorage (5 min)

Open `ThemeWithPersistence`. Demo — toggle theme, refresh the page.

> "State is gone on refresh. localStorage persists. `useLocalStorage` gives us the best of both: state API with persistence."

Walk through the hook:
```js
const [storedValue, setStoredValue] = useState(() => {
  const item = localStorage.getItem(key);
  return item ? JSON.parse(item) : initialValue;
});
```
> "Lazy initializer — the function only runs ONCE on mount. We don't want to read localStorage on every render."

> "The `setValue` wrapper calls both `setStoredValue` AND `localStorage.setItem`. One call to `setTheme('dark')` updates React state and persists it."

Ask: "What would happen if we forgot JSON.parse?" → We'd get a string `"true"` instead of a boolean `true`.

---

### 3d. useFetch (6 min)

Open `CourseListWithFetch`. Show the component — 3 lines of JSX, no fetch logic visible.

> "Look at this component. It has no idea how fetching works. It just asks for data and gets loading/error/data back."

Show the hook:
```js
let cancelled = false;
const fetchData = async () => { … };
fetchData();
return () => { cancelled = true; };
```
> "We use a boolean flag `cancelled` to prevent setting state after the component unmounts. If we're mid-fetch and the user navigates away, the response comes back but `cancelled` is true — we do nothing. No memory leak, no state update on an unmounted component."

Ask: "What's the difference between this and the AbortController approach?" → AbortController actually cancels the HTTP request in-flight. The `cancelled` flag just ignores the response. Both patterns are valid — `cancelled` is simpler to teach.

Demo: change the URL to something invalid → error state displays.

---

### 3e. useDebounce (4 min)

Open `DebouncedSearch`. Type quickly — watch "Search executed" counter vs keystrokes.

> "If we called the search API on every keystroke, a user typing 'react' fires 5 requests. With debouncing, we wait 400ms after the LAST keystroke. One request."

Show the hook:
```js
useEffect(() => {
  const timer = setTimeout(() => setDebouncedValue(value), delay);
  return () => clearTimeout(timer);
}, [value, delay]);
```
> "Every time `value` changes, we set a timer. Before setting it, we cancel the previous one (cleanup). Only when `value` is stable for `delay` ms does the timer fire and update `debouncedValue`."

---

### 3f. useForm (3 min)

Open `ContactForm`. Demo — submit empty, fill fields, submit successfully.

> "Before this hook, how many components in an app have a form? Dozens. And they all had the same state shape, the same `handleChange` with computed keys, the same validation logic copy-pasted everywhere."

Show the call site:
```js
const { values, errors, handleChange, handleSubmit, reset } =
  useForm({ name: '', email: '', message: '' }, validate);
```
> "The component gives the hook its initial shape and a validation function. The hook gives back everything needed to render and submit the form. The component has NO form management logic — just JSX."

---

## Segment 4 — Component Lifecycle in Functional Components (20 min)

**File:** `04-component-lifecycle-in-functional-components.jsx`

---

### 4a. Open `LifecycleMappingTable` (5 min)

Render the mapping table in the browser. Talk through each row.

> "If you've worked with class components before, this is your Rosetta Stone. If you haven't, this table tells you which useEffect pattern to reach for based on WHEN you want something to happen."

Key rows to emphasize:

| Phase | Class | Hook |
|-------|-------|------|
| Mount | `componentDidMount` | `useEffect(fn, [])` |
| Update (specific) | `componentDidUpdate` with if-check | `useEffect(fn, [dep])` |
| Unmount | `componentWillUnmount` | `return () => cleanup` |

> "Notice that mount and unmount are handled by the SAME useEffect call. The effect sets things up, the cleanup tears them down. They're intentionally paired."

---

### 4b. Open `LifecycleClassComponent` and `LifecycleFunctionalComponent` side by side (5 min)

Split the screen or scroll between them.

> "The class component has one setup function (componentDidMount), one update function (componentDidUpdate), and one teardown (componentWillUnmount). The functional component has THREE small effects, each handling ONE concern."

Draw on board:
```
Class:                         Functional:
constructor                    useState(...)
componentDidMount              useEffect(() => { fetch(); return cleanup; }, [])
componentDidUpdate(check)      useEffect(() => { setTitle(); }, [count])
componentWillUnmount           return () => { cleanup }   (inside first effect)
```

> "The functional approach is more composable. If I want to extract 'document title syncing' to a custom hook, I take the second effect and move it. In a class component, that logic is tangled into componentDidUpdate alongside everything else."

---

### 4c. Live Demo — `MountUpdateUnmountDemo` (5 min)

Open the component in browser.

> "Let's actually see these events fire."

Step 1: Component mounts → green "MOUNTED" log appears.
Step 2: Click "Next Course" → "courseId changed" log appears.
Step 3: Click "Next Course" again → another update log.
Step 4: Click "Unmount" → red "UNMOUNTED" log appears.
Step 5: Mount again → green "MOUNTED" log again.

> "Notice the log panel is inside the component — when we unmount, the panel disappears. The log entry for UNMOUNTED appears in a new panel on next mount because we kept the parent's state. In a real app you'd see it in the console."

---

### 4d. Cleanup Deep Dive — `SubscriptionDemo` (5 min)

> "Here's the subtlety that catches people: cleanup doesn't ONLY run on unmount."

Open `SubscriptionDemo`. Open DevTools Console.

Demo:
1. Page loads → "✅ Subscribed to #general" in console
2. Click "#react" → "❌ Unsubscribed from #general" + "✅ Subscribed to #react"
3. Click "#jobs" → unsubscribe react, subscribe jobs

> "Before re-running the effect for the new channel, React runs the PREVIOUS effect's cleanup. This prevents you from being subscribed to two channels at once. With class components you had to remember to do this manually in componentDidUpdate."

> "This is arguably the most important thing to understand about useEffect cleanup. It's not just a destructor — it's a 'undo the previous effect' function."

---

## Recap & Q&A (5 min)

### Key Takeaways

1. **Controlled inputs** = React owns the value (state). **Uncontrolled** = DOM owns it (ref).
2. **`useRef`** has two jobs: hold a DOM reference OR hold a mutable value that doesn't trigger re-renders.
3. **Context** solves prop drilling — broadcast data to any component in a subtree without explicit prop passing.
4. **Custom hooks** = extract + reuse stateful logic. Any function starting with `use` that calls a hook.
5. **Rules of Hooks**: top-level only, React functions only. The linter enforces this.
6. **`useEffect` replaces all three lifecycle methods**: setup (mount), re-run (update), cleanup (unmount + pre-re-run).
7. **Cleanup runs before EACH re-run of the effect**, not just on unmount — prevents stale subscriptions.
8. A custom hook returns whatever the calling component needs — it can return values, functions, objects, arrays, or nothing.

---

### Q&A Questions

1. "When would you choose an uncontrolled input over a controlled one?"
   - *Good for: file inputs (always uncontrolled), integrating non-React libs, massive forms where every keystroke re-rendering is too costly*

2. "Can two components share the same Context value, or do they each get their own copy?"
   - *They share the same value — that's the whole point. Any component inside the Provider reads the same state.*

3. "If I write `useEffect(() => { setCount(count + 1); }, [])`, what happens?"
   - *Runs once on mount. count is always 0 in that closure (stale closure). count becomes 1, then never updates again. The ESLint hook rule would warn: count missing from dep array.*

4. "Why name custom hooks with `use`? What happens if you don't?"
   - *Convention that allows the linter to apply the Rules of Hooks to your custom hook. Without the prefix, ESLint won't warn if you call it conditionally.*

5. "What's the difference between putting `console.log` inside the component body vs inside a `useEffect`?"
   - *Component body runs during render (including StrictMode double renders). useEffect runs after the DOM is painted. For side effects, always useEffect.*

---

### Take-Home Exercises

1. **`useWindowSize` hook** — returns `{ width, height }` of the browser window, updates on resize. Hint: `window.addEventListener('resize', handler)` — don't forget cleanup.

2. **`useOnlineStatus` hook** — returns a boolean `isOnline`. Use `navigator.onLine` for initial value, then listen to `online` and `offline` events.

3. **Add persistence to `RegistrationForm`** — use `useLocalStorage` to save form progress. The form should restore its values after a page refresh.

4. **Context for a shopping cart** — Create `CartContext` with `items`, `addItem(product)`, `removeItem(id)`, and `total`. Wire it to a small product list and a cart summary component.

---

## Transition to Day 17b (if applicable)

> "We've now covered all the core React hooks and the patterns that make functional React so powerful. Tomorrow we move to the Angular side of the curriculum — Angular Fundamentals. You'll see many of the same concepts — components, routing, forms, services — expressed in a very different way."

---

*End of Day 17a — React Hooks*

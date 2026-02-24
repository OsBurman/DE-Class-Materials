# Day 17a — React Hooks | Part 1 Walkthrough Script
**Duration:** ~90 minutes  
**Folder:** `Code-walkthrough/Part-1/`  
**Files covered:**
- `01-useState-hook.jsx`
- `02-useEffect-and-event-handling.jsx`

---

## Pre-Class Setup Checklist
- [ ] CRA or Vite React project running (`npm run dev` or `npm start`)
- [ ] Both files open in VS Code
- [ ] Browser DevTools open (Console tab visible)
- [ ] React DevTools browser extension installed

---

## Opening Hook (3 min)

> [ACTION] Write on the board:
```
Class Component           Functional Component
this.state = {...}    →   useState()
componentDidMount()   →   useEffect(fn, [])
componentDidUpdate()  →   useEffect(fn, [deps])
componentWillUnmount() →  useEffect(() => { return cleanup }, [])
```

"Before React 16.8, if you needed state or lifecycle methods, you had to use class components. Hooks changed everything. Now functional components can do everything class components can — and they're simpler, more composable, and easier to test. Today we learn the two most important hooks: `useState` and `useEffect`."

> [ASK] "How many of you have written class components before? How many have only seen functional components? Either way, by the end of today you'll know why the industry moved to hooks."

---

## Segment 1 — useState: State Management (35 min)

### What IS State?

> [ACTION] Open `01-useState-hook.jsx`.

"State is data that belongs to a component and can change over time. When state changes, React re-renders the component and the UI updates automatically. That's the contract: you change state, React handles the DOM."

"Before hooks, you needed a class to have state:
```js
class Counter extends React.Component {
  state = { count: 0 };
  render() { return <p>{this.state.count}</p>; }
}
```
With hooks, it's one line."

### Section 1: Basic useState

> [ACTION] Scroll to the `Counter` component.

"Here's the hook call:"
```js
const [count, setCount] = useState(0);
```

"This is array destructuring. `useState` returns exactly two things — the current value and a setter function. We name them by convention: `thing` and `setThing`."

"The argument to `useState` — `0` here — is the **initial value**. React uses it only on the first render."

> [ASK] "What happens if I call `count++` instead of `setCount(count + 1)`?"

*Answer:* Nothing visible changes. React doesn't know about the mutation. The variable `count` is a constant within each render — you cannot mutate it. You MUST call the setter.

> [ACTION] Demo in browser — increment, decrement, reset.

"Notice how setting two pieces of state in `reset()` works — both `setCount(0)` and `setMessage(...)` are called. React batches them into a single re-render."

### Section 2: Object State

> [ACTION] Scroll to `UserProfile`.

"When state is an object, the pattern changes slightly. Watch."

Point to the `toggleActive` function:
```js
setProfile({ ...profile, isActive: !profile.isActive });
```

"We spread the existing object to copy all existing fields, then override only `isActive`. This creates a **brand new object** — React sees a different reference and schedules a re-render."

> ⚠️ **WATCH OUT:** The most common beginner mistake is this:
```js
profile.isActive = false;  // mutates directly — React never sees this!
setProfile(profile);        // same reference — React bails out, no re-render
```
"React uses `Object.is()` comparison on state. Same reference = no update."

> [ACTION] Demo double-click edit and toggle in browser.

### Section 3: Array State

> [ACTION] Scroll to `CourseList`.

"Arrays follow the same principle — create a new array instead of mutating. Let me show you the three operations."

Walk through each:
- **Add**: `[...courses, newCourse]` — spread + new item at end
- **Remove**: `courses.filter(c => c.id !== id)` — filter returns new array
- **Update**: `courses.map(c => c.id === id ? {...c, completed: !c.completed} : c)` — map returns new array, spread updates the changed item

> [ASK] "Why can't I just `courses.push(newCourse)` and `setCourses(courses)`?"

*Answer:* `push` mutates the existing array. React sees the same array reference and skips the re-render. The UI doesn't update.

> [ACTION] Demo adding, completing, and removing courses in browser.

### Section 4: Functional Updates

> [ACTION] Scroll to `BatchCounter`.

"This one is subtle but important. When your new state value depends on the previous state, always use the functional form."

> [ACTION] Click "Buggy +3" — show count goes up by 1, not 3.

"Why? React batches these three `setCount` calls. All three read the same `count` variable, which is still the stale value from the current render. So all three set it to 1."

> [ACTION] Click "Correct +3" — show count goes up by 3.

"The functional form `setCount(prev => prev + 1)` receives the **latest queued value**, not the stale render snapshot. Each call gets the result of the previous one."

> ⚠️ **WATCH OUT:** This matters most in async handlers, event batching, and `useEffect` calls where the state value might be stale. When in doubt, use the functional form.

### Section 5: Lazy Initializer

> [ACTION] Scroll to `ThemeToggle`. Open the Console tab in DevTools.

"If computing initial state is expensive — reading localStorage, parsing a large JSON object, running a search — you don't want that computation running on every render."

"Without lazy init:
```js
useState(loadSavedTheme())  // called on EVERY render — wasteful
```
With lazy init:
```js
useState(() => loadSavedTheme())  // called only on the FIRST render
```
Pass a function, not the result of calling the function."

> [ACTION] Toggle the theme a few times. Show that `loadSavedTheme()` only logs once in the console.

---

## Segment 2 — useEffect & Event Handling (47 min)

### What is a Side Effect?

> [ACTION] Open `02-useEffect-and-event-handling.jsx`. Write on board:

```
RENDER (pure):            SIDE EFFECTS (impure):
Read props/state    →     Fetch data
Compute JSX         →     Start a timer
Return JSX          →     Update document.title
                          Add event listeners
                          Log to console
```

"React renders components as **pure functions** — same input, same output, no side effects. But real apps need side effects. `useEffect` is the escape hatch that lets you run side effects AFTER React finishes rendering."

### The Three Dependency Array Patterns

> [ACTION] Draw on board:
```
useEffect(fn)          // runs after EVERY render
useEffect(fn, [])      // runs ONCE on mount
useEffect(fn, [a, b])  // runs when a or b changes
```

### Section 1: No Dependency Array

> [ACTION] Scroll to `DocumentTitleSync`.

"No array = runs after every render. This is the broadest setting. The effect synchronizes `document.title` with whatever `name` and `count` currently are."

> [ACTION] Type in the name input — show tab title updating.

"Every keystroke triggers a re-render (because `setName` is called), which triggers the effect. That's fine for cheap operations like setting `document.title`."

### Section 2: Empty Array — Mount Once

> [ACTION] Scroll to `CourseLoader`.

"Empty array means 'I have no dependencies — run once and never again.' This is the hook equivalent of `componentDidMount`."

Walk through the simulated fetch:
- State starts as `loading: true`, `courses: []`
- useEffect fires after first render
- `setTimeout` simulates async API call
- On completion: `setCourses(data)` and `setLoading(false)` trigger a re-render
- Cleanup returns `clearTimeout(timer)` in case the component unmounts early

> [ASK] "Why do we return `() => clearTimeout(timer)` even though this is a one-shot timer?"

*Answer:* If the user navigates away before the 1.2 seconds are up, the component unmounts. Without cleanup, the timer fires, tries to call `setCourses` on a destroyed component — React warns: "Can't perform a state update on an unmounted component."

### Section 3: Specific Dependencies

> [ACTION] Scroll to `CourseSearch`.

"Now we have `[query]` as the dependency. React re-runs the effect every time `query` changes — which happens on every keystroke. But we don't want to filter on every keystroke — we want to wait until the user pauses."

Walk through the debounce pattern:
1. User types → `query` changes → effect re-runs
2. Old effect's cleanup runs first → cancels the previous `clearTimeout`
3. New debounce timer starts
4. If user keeps typing, this repeats
5. If user pauses for 400ms → filter runs → results appear

> [ACTION] Type quickly, then pause. Show results only appear after the pause.

> ⚠️ **WATCH OUT:** The lint rule `react-hooks/exhaustive-deps` will warn you if you forget to add a variable to the dependency array. Don't suppress the warning — it's telling you your effect might use a stale value.

### Section 4: Cleanup

> [ACTION] Scroll to `LiveClock`.

"The cleanup function is returned from `useEffect`. React calls it in two situations: before re-running the effect (if deps changed) and when the component unmounts."

> [ACTION] In browser — mount the clock (it ticks). Then click "Unmount Clock." Show the console: `🛑 Clock stopped — interval cleared`.

"Without cleanup, that interval keeps running forever even after the component is gone. The callback tries to call `setTime` on a destroyed component. Memory leak."

> [ACTION] Mount it again — show `⏱ Clock started` in console.

### Section 5: Multiple Effects

> [ACTION] Scroll to `CourseDetail`.

"You can — and should — have multiple `useEffect` calls. One per concern. Don't cram unrelated setup into a single effect like you had to in class-based `componentDidMount`."

"Effect 1 fetches data. Effect 2 tracks views. Effect 3 resets the document title on unmount. Completely separate concerns, easy to understand individually."

> [ACTION] Click between courses — show both effects re-running when `courseId` changes.

### Section 6: Event Handling

> [ACTION] Scroll to `EventHandlingDemo`.

"React's event system uses **synthetic events** — JavaScript wrappers around native DOM events. The interface is identical, but React normalizes behavior across browsers."

Point out the key differences:
1. **camelCase**: `onClick` not `onclick`
2. **Pass a function**: `onClick={handleClick}` not `onClick="handleClick()"`
3. **Arrow function for arguments**: `onClick={() => addLog('enrolled')}` — the arrow function wraps the call so it only fires on click, not on render

> ⚠️ **WATCH OUT:** This is the most common event mistake:
```js
// ❌ This CALLS the function immediately on render and binds the return value
onClick={handleEnroll('course-1')}

// ✅ This passes a function that calls handleEnroll when clicked
onClick={() => handleEnroll('course-1')}
```

> [ACTION] Demo each event type in the browser — click, type, keydown, focus/blur, mouse move.

"For forms: ALWAYS call `e.preventDefault()` in your `onSubmit` handler. Without it, the browser does a full page reload."

---

## Recap & Q&A (5 min)

### Key Takeaways — Write on Board

1. `useState(initial)` returns `[value, setter]` — never mutate state directly
2. Object/array state: always create a **new** object/array with spread
3. When new state depends on old state → use **functional form**: `setState(prev => ...)`
4. **Lazy initializer**: `useState(() => expensiveComputation())` — only runs once
5. `useEffect` runs AFTER render; three patterns: no deps, `[]`, `[deps]`
6. **Always clean up**: return a function to cancel timers, subscriptions, listeners
7. **One effect per concern** — multiple `useEffect` calls are fine and preferred
8. Events are camelCase, pass functions not calls, always `e.preventDefault()` on forms

### Q&A Questions

1. "What's wrong with `setUsers(users.push(newUser))` — there are two bugs. Can you name both?"
2. "When would you use the functional setter form `setState(prev => prev + 1)` vs `setState(state + 1)`?"
3. "I have a `useEffect` that fetches data. Should I put the async function inside the effect or outside? Why?"
4. "What does the cleanup function in `useEffect` do? When exactly is it called?"
5. "I have `onClick={fetchData()}` — what's wrong and how do I fix it?"

---

## Take-Home Exercises

1. **Shopping cart**: Build a cart with `useState`. Items have name, price, quantity. Implement add, remove, and quantity increment/decrement. Show total price (computed from state, not stored).

2. **Auto-save**: Build a text editor that auto-saves to `localStorage` every time the content changes (use `useEffect` with `[content]` dep). On mount, load the saved content.

3. **Countdown timer**: Build a timer that counts down from a user-entered number to 0. Use `setInterval` in `useEffect`. When it hits 0, show "Time's up!" and stop. Cleanup the interval properly.

4. **Event challenge**: Build a keyboard shortcut helper — display which key the user pressed, with special handling for Ctrl+S ("saved!"), Escape ("dismissed"), and Enter ("confirmed"). Use `onKeyDown` on a container div.

---

→ **TRANSITION to Part 2:** "Now that we understand state and effects, Part 2 goes deeper into forms — controlled vs uncontrolled — plus two more powerful hooks: `useRef` and `useContext`. We'll also build our own custom hooks."

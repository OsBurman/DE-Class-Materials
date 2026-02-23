# Week 4 - Day 17a: React Hooks
## Part 1 Slide Descriptions — useState, useEffect & Event Handling

**Part 1 Topics:** State management with useState, Side effects with useEffect, Effect dependencies and cleanup, Event handling in React
**Slide count:** 17
**Estimated runtime:** 60 minutes

---

### Slide 1 — Title Slide

**Layout:** Centered title card, React blue gradient background (#61DAFB → #20232a)
**Content:**
- Main title: **React Hooks**
- Subtitle: **Part 1 — useState, useEffect & Events**
- Week 4 - Day 17a badge
- React atom logo (blue)
- Tag line: *"Functions that let you hook into React state and lifecycle"*

---

### Slide 2 — Where We Left Off: Day 16a Recap

**Layout:** Two-column: left = what we know, right = what's missing
**Content:**

**Left — What we built in Day 16a:**
- Function components with JSX
- Props for passing data parent → child
- Static component rendering
- Basic event wiring (onClick)

**Right — What's still missing:**
```jsx
function Counter() {
  // ❌ This doesn't work — plain variables reset on every render
  let count = 0;
  return <button onClick={() => count++}>{count}</button>;
}
```

**Bottom callout:** *"Components can receive data (props) but can't remember anything. Hooks solve this."*

**Speaker note:** Bridge from Day 16a — components, props, JSX are all done. Today we add memory (state) and side effects.

---

### Slide 3 — Why Hooks? A Brief History

**Layout:** Timeline/comparison split
**Content:**

**Before Hooks (Pre-2019):**
- State required class components
- `this.setState()`, lifecycle methods (`componentDidMount`, `componentDidUpdate`, `componentWillUnmount`)
- Logic was hard to reuse between components
- Wrapper hell: HOC + render props nesting

**React 16.8 (February 2019):**
```
Hooks released — function components can now:
✓ Have state (useState)
✓ Perform side effects (useEffect)
✓ Share stateful logic (custom hooks)
✓ Access context (useContext)
✓ Hold mutable values (useRef)
```

**Today:**
> *Class components still work and aren't going away, but hooks are the modern standard. New React code is written with function components + hooks.*

**Key rule box:**
```
Hooks must be called:
1. At the top level (not inside if/for/while)
2. Only from React function components or custom hooks
```

---

### Slide 4 — useState: The Basics

**Layout:** Full-width code walkthrough with annotations
**Content:**

```jsx
import { useState } from 'react';

function Counter() {
  //     ┌── current value    ┌── setter function
  const [count, setCount] = useState(0);
  //                                  └── initial value

  return (
    <div>
      <p>Count: {count}</p>
      <button onClick={() => setCount(count + 1)}>Increment</button>
      <button onClick={() => setCount(count - 1)}>Decrement</button>
      <button onClick={() => setCount(0)}>Reset</button>
    </div>
  );
}
```

**Annotation callouts:**
- `useState(0)` → "Returns an array of exactly two things: the current value and a function to update it"
- `[count, setCount]` → "Array destructuring — names are up to you, but convention is `value` / `setValue`"
- `setCount(count + 1)` → "Calling the setter triggers a re-render with the new value"

**Bottom box:**
> **React's contract:** When you call a setter, React re-renders the component. The function runs again. `count` gets its new value. JSX is returned with the updated number.

---

### Slide 5 — useState: Multiple State Variables

**Layout:** Two-column code comparison
**Content:**

**Option A — Multiple separate state variables (preferred):**
```jsx
function UserForm() {
  const [name, setName]         = useState('');
  const [email, setEmail]       = useState('');
  const [isActive, setIsActive] = useState(false);
  const [age, setAge]           = useState(null);

  // Each update only re-renders what changed
  return (
    <div>
      <input value={name} onChange={e => setName(e.target.value)} />
      <input value={email} onChange={e => setEmail(e.target.value)} />
    </div>
  );
}
```

**Option B — Single object (use when values always update together):**
```jsx
function UserForm() {
  const [user, setUser] = useState({ name: '', email: '' });

  // Must spread previous state — React does NOT auto-merge object state
  const updateName = (name) => setUser(prev => ({ ...prev, name }));
  const updateEmail = (email) => setUser(prev => ({ ...prev, email }));
}
```

**Rule callout (red border):**
> ⚠️ Unlike `this.setState()` in class components, `useState` setter **replaces** the entire value. With object state, always spread: `setUser(prev => ({ ...prev, key: value }))`

---

### Slide 6 — useState: Functional Updates

**Layout:** Code comparison + timing diagram
**Content:**

**The problem with stale closures:**
```jsx
// ❌ Stale closure — count may be outdated in rapid updates
const handleMultipleClicks = () => {
  setCount(count + 1);  // count = 0
  setCount(count + 1);  // count = 0 (same closure!)
  setCount(count + 1);  // count = 0 (still same!)
  // Result: count = 1, not 3
};
```

**The fix — functional updates:**
```jsx
// ✅ Functional update — always gets the latest value
const handleMultipleClicks = () => {
  setCount(prev => prev + 1);  // prev = 0, returns 1
  setCount(prev => prev + 1);  // prev = 1, returns 2
  setCount(prev => prev + 1);  // prev = 2, returns 3
  // Result: count = 3 ✓
};
```

**When to use functional updates:**
```jsx
// ✅ Always use when new state depends on old state
setCount(prev => prev + 1);
setItems(prev => [...prev, newItem]);
setIsOpen(prev => !prev);
```

**Rule box:**
> When the new state value depends on the previous state value, always use the `prev =>` functional form.

---

### Slide 7 — What NOT to Put in State

**Layout:** Two-column: correct vs incorrect
**Content:**

**❌ Don't store computed/derived values in state:**
```jsx
// ❌ Wrong — derivable from other state
const [items, setItems] = useState([...]);
const [itemCount, setItemCount] = useState(0); // derived!
const [totalPrice, setTotalPrice] = useState(0); // derived!

// Keeping these in sync manually is a bug factory
```

**✅ Derive during render instead:**
```jsx
// ✅ Correct — compute from state during render
const [items, setItems] = useState([...]);

const itemCount = items.length;                          // derived
const totalPrice = items.reduce((sum, i) => sum + i.price, 0);  // derived
const inStockItems = items.filter(i => i.inStock);       // derived
```

**What SHOULD go in state:**
| Goes in state ✅ | Don't put in state ❌ |
|---|---|
| User input values | Values derivable from state |
| UI toggle flags | Props (received, not owned) |
| Data from API | Constants |
| Selected item ID | Computed values |

> **Rule of thumb:** If a value can be computed from other state or props during render, it doesn't belong in state.

---

### Slide 8 — useEffect: What and Why

**Layout:** Left = concept explanation, right = code
**Content:**

**What is a "side effect"?**
> A side effect is anything that reaches outside the component's render cycle — fetching data, setting a document title, starting a timer, subscribing to events, writing to localStorage.

**Why effects need special handling:**
- React renders your component (possibly many times)
- Side effects in the render body run on every render — usually wrong
- `useEffect` lets you run side effects AFTER the DOM is updated, in a controlled way

**Basic useEffect:**
```jsx
import { useState, useEffect } from 'react';

function DocumentTitleUpdater({ title }) {
  useEffect(() => {
    document.title = title; // ← side effect
  });
  // Runs after EVERY render (we'll fix this next)

  return <h1>{title}</h1>;
}
```

**The pattern:**
```
Render JSX → React updates DOM → useEffect runs
```

**Analogy box:**
> Think of `useEffect` as telling React: *"After you've finished updating the screen, run this code."*

---

### Slide 9 — useEffect: The Dependency Array

**Layout:** Three distinct code examples with behavior labels
**Content:**

**Case 1 — No dependency array (runs after every render):**
```jsx
useEffect(() => {
  console.log('Runs after EVERY render');
  document.title = `Count: ${count}`;
}); // ← no array
```
*Use case:* Rare. Usually too expensive.

**Case 2 — Empty dependency array (runs once after mount):**
```jsx
useEffect(() => {
  console.log('Runs ONCE after mount');
  fetchInitialData();
}, []); // ← empty array
```
*Use case:* Initial data fetch, one-time subscriptions, setup code.

**Case 3 — With dependencies (runs when deps change):**
```jsx
useEffect(() => {
  console.log(`userId changed to: ${userId}`);
  fetchUserById(userId);
}, [userId]); // ← runs when userId changes
```
*Use case:* React to prop/state changes — re-fetch when an ID changes.

**Mental model box:**
```
No array    → "After every render"
[]          → "After the first render only (mount)"
[a, b]      → "After mount AND whenever a or b changes"
```

---

### Slide 10 — useEffect: Cleanup Functions

**Layout:** Full-width code with callouts
**Content:**

**Why cleanup matters:**
> Without cleanup: subscriptions accumulate, intervals keep firing, event listeners stack up. This causes memory leaks and bugs.

**The cleanup pattern:**
```jsx
useEffect(() => {
  // 1. Set up the effect
  const intervalId = setInterval(() => {
    setSeconds(prev => prev + 1);
  }, 1000);

  // 2. Return a cleanup function
  return () => {
    clearInterval(intervalId); // ← runs before the next effect or on unmount
  };
}, []); // mount only
```

**When does cleanup run?**
```
Mount:    Effect runs ──────────────────────────────────────┐
Re-render: Cleanup of old effect → New effect runs          │
Unmount:  Cleanup runs ← component removed from DOM ────────┘
```

**Real-world cleanup examples:**
```jsx
// Timer
return () => clearInterval(intervalId);

// WebSocket
return () => socket.close();

// Event listener
return () => window.removeEventListener('resize', handler);

// Subscription (Day 19b — RxJS)
return () => subscription.unsubscribe();
```

---

### Slide 11 — useEffect: Common Patterns

**Layout:** Grid of four use-case cards
**Content:**

**Pattern 1 — Document title:**
```jsx
useEffect(() => {
  document.title = `(${unread}) Messages`;
}, [unread]);
```

**Pattern 2 — LocalStorage sync:**
```jsx
useEffect(() => {
  localStorage.setItem('theme', theme);
}, [theme]);
```

**Pattern 3 — Timer:**
```jsx
useEffect(() => {
  const id = setInterval(tick, 1000);
  return () => clearInterval(id);
}, []);
```

**Pattern 4 — Window event listener:**
```jsx
useEffect(() => {
  const handleResize = () => setWidth(window.innerWidth);
  window.addEventListener('resize', handleResize);
  return () => window.removeEventListener('resize', handleResize);
}, []);
```

**Coming soon box (gray):**
> **Pattern 5 — Data fetching** is the most common use of `useEffect`. Full implementation with loading states, error handling, and abort controllers is in Day 19a (React API & Testing), once we have real APIs to hit.

---

### Slide 12 — The eslint-plugin-react-hooks Rule

**Layout:** Code examples with ESLint warning styling
**Content:**

**React enforces dependency rules with ESLint:**
```jsx
// ❌ ESLint warning: 'userId' is missing from dependency array
useEffect(() => {
  fetchUser(userId);
}, []);
```

```jsx
// ✅ Correct — all values used inside are listed as deps
useEffect(() => {
  fetchUser(userId);
}, [userId]);
```

**Why the rule exists:**
> If you use a value inside `useEffect` but don't list it in dependencies, the effect won't re-run when that value changes. Your effect sees a stale, old value. This is the "stale closure" bug.

**Functions as dependencies:**
```jsx
// ❌ Causes infinite re-render loop — new function on every render
useEffect(() => {
  fetchData();
}, [fetchData]); // fetchData is recreated each render!

// ✅ Define function inside the effect OR use useCallback (Day 20a)
useEffect(() => {
  const fetchData = async () => { /* ... */ };
  fetchData();
}, [userId]); // Only userId as dependency
```

**Rule:** Trust the ESLint plugin. If it says add a dependency, add it. If you think you need an exemption, you probably need to restructure.

---

### Slide 13 — Event Handling in React

**Layout:** Two-column: HTML comparison vs React
**Content:**

**HTML vs React event handling:**
```html
<!-- HTML: lowercase, string -->
<button onclick="handleClick()">Click me</button>
```
```jsx
// React: camelCase, function reference
<button onClick={handleClick}>Click me</button>
<button onClick={() => handleClick(id)}>Click me</button>
```

**Common events:**
```jsx
<button onClick={handleClick}>
<input onChange={handleChange} />
<form onSubmit={handleSubmit}>
<input onKeyDown={handleKeyDown} />
<input onFocus={handleFocus} onBlur={handleBlur} />
<div onMouseOver={handleHover} onMouseOut={handleOut} />
```

**Event handler patterns:**
```jsx
// Inline (simple)
<button onClick={() => setCount(count + 1)}>+</button>

// Method reference (no args)
<button onClick={handleClick}>Click</button>

// Method reference (with args)
<button onClick={() => handleDelete(item.id)}>Delete</button>

// Extracting from event object
const handleChange = (e) => setValue(e.target.value);
```

---

### Slide 14 — Synthetic Events

**Layout:** Code + explanation callouts
**Content:**

**React wraps native events in SyntheticEvent:**
```jsx
function SearchForm() {
  const [query, setQuery] = useState('');

  const handleSubmit = (e) => {
    e.preventDefault();       // ← prevent page reload
    console.log(e.type);      // 'submit'
    console.log(e.target);    // the form element
    performSearch(query);
  };

  const handleChange = (e) => {
    setQuery(e.target.value); // ← reading input value
  };

  return (
    <form onSubmit={handleSubmit}>
      <input value={query} onChange={handleChange} />
      <button type="submit">Search</button>
    </form>
  );
}
```

**SyntheticEvent properties you'll use:**
| Property/Method | Use |
|---|---|
| `e.target.value` | Current value of input |
| `e.target.checked` | Checkbox state |
| `e.preventDefault()` | Stop default behavior (form submit, link) |
| `e.stopPropagation()` | Stop event bubbling |
| `e.key` | Key pressed (keyboard events) |
| `e.type` | Event type string |

> **Note:** React's synthetic event system normalizes cross-browser differences. You get the same `e.target.value` in Chrome, Firefox, and Safari.

---

### Slide 15 — Putting It Together: Counter with History

**Layout:** Full-width complete component
**Content:**

```jsx
import { useState } from 'react';

function Counter() {
  const [count, setCount] = useState(0);
  const [history, setHistory] = useState([]);

  const increment = () => {
    const newCount = count + 1;
    setCount(newCount);
    setHistory(prev => [...prev, `+1 → ${newCount}`]);
  };

  const decrement = () => {
    const newCount = count - 1;
    setCount(newCount);
    setHistory(prev => [...prev, `-1 → ${newCount}`]);
  };

  const reset = () => {
    setCount(0);
    setHistory(prev => [...prev, `reset → 0`]);
  };

  return (
    <div>
      <h2>Count: {count}</h2>
      <button onClick={decrement}>-</button>
      <button onClick={reset}>Reset</button>
      <button onClick={increment}>+</button>

      <h3>History ({history.length} actions)</h3>
      <ul>
        {history.map((entry, i) => (
          <li key={i}>{entry}</li>
        ))}
      </ul>
    </div>
  );
}
```

**Callouts:**
- Two independent state variables — each with its own setter
- Functional update `prev => [...prev, entry]` for history array
- `e` not needed here — click events use no event data

---

### Slide 16 — Putting It Together: Auto-Saving Note

**Layout:** Full-width complete component combining useState + useEffect
**Content:**

```jsx
import { useState, useEffect } from 'react';

function AutoSaveNote() {
  const [note, setNote] = useState(() => {
    // Lazy initialization — only runs once on mount
    return localStorage.getItem('saved-note') || '';
  });
  const [saveStatus, setSaveStatus] = useState('All saved');

  useEffect(() => {
    setSaveStatus('Saving...');

    // Debounce — wait 1 second after typing stops
    const timer = setTimeout(() => {
      localStorage.setItem('saved-note', note);
      setSaveStatus('All saved');
    }, 1000);

    return () => clearTimeout(timer); // ← cleanup cancels pending save
  }, [note]); // ← runs whenever note changes

  return (
    <div>
      <div>Status: {saveStatus}</div>
      <textarea
        value={note}
        onChange={e => setNote(e.target.value)}
        placeholder="Start typing — auto-saves after 1 second"
        rows={10}
        cols={50}
      />
    </div>
  );
}
```

**Callouts:**
- Lazy initialization `useState(() => ...)` — reads localStorage once, not on every render
- `useEffect` with cleanup creates a debounce pattern
- Status message updates provide user feedback
- Combines `useState` + `useEffect` + cleanup in one real example

---

### Slide 17 — Part 1 Summary

**Layout:** Summary table + Part 2 preview, two-column
**Content:**

**Part 1 Covered:**

| Concept | Key Point |
|---|---|
| `useState(initialValue)` | Returns `[value, setter]`; calling setter triggers re-render |
| Multiple state | Prefer separate variables; object state needs spread on update |
| Functional updates | Use `prev =>` when new value depends on old |
| Don't derive in state | Compute derived values during render, not in state |
| `useEffect(fn, deps)` | Runs after render; deps control when |
| No array | Every render |
| `[]` | Mount only |
| `[a, b]` | Mount + when a or b changes |
| Cleanup function | Returned from effect; prevents memory leaks |
| Synthetic events | `e.target.value`, `e.preventDefault()`, camelCase handlers |

**Part 2 Preview:**
- Controlled forms — the full pattern for forms in React
- Uncontrolled components — when to reach for `useRef`
- `useRef` — mutable values + DOM access without re-renders
- `useContext` — sharing state without prop drilling
- Custom hooks — extracting and reusing stateful logic
- Lifecycle mapping — how React hooks map to class lifecycle methods

---

*[END OF PART 1 SLIDE DESCRIPTIONS]*

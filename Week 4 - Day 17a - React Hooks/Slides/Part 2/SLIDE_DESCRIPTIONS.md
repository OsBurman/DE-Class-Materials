# Week 4 - Day 17a: React Hooks
## Part 2 Slide Descriptions — Forms, useRef, useContext & Custom Hooks

**Part 2 Topics:** Controlled components, Uncontrolled components and refs, useRef hook, useContext and Context API, Custom hooks, Component lifecycle in functional components
**Slide count:** 17
**Estimated runtime:** 60 minutes

---

### Slide 1 — Part 2 Title Slide

**Layout:** Centered, React blue gradient background
**Content:**
- Main title: **React Hooks — Part 2**
- Subtitle: **Forms, Refs, Context & Custom Hooks**
- Tag line: *"Building reusable stateful logic"*
- Visual: React atom logo + hook icon

---

### Slide 2 — Controlled Components: The Core Idea

**Layout:** Diagram + code comparison
**Content:**

**The fundamental question:** Who owns the form value?

```
Uncontrolled:  DOM owns the value → React reads it when needed
Controlled:    React owns the value → DOM reflects React's state
```

**Uncontrolled (not yet controlled):**
```jsx
// DOM manages this — React doesn't know what's typed
<input type="text" defaultValue="Alice" />
```

**Controlled:**
```jsx
function NameInput() {
  const [name, setName] = useState('');

  return (
    // React owns this value — every keystroke goes through state
    <input
      value={name}                              // ← React controls display
      onChange={e => setName(e.target.value)}   // ← React updates on change
    />
  );
}
```

**Why controlled is the default choice:**
| Benefit | How |
|---|---|
| Instant validation | Check value on every keystroke |
| Conditional submit | `disabled={!isValid}` |
| Programmatic updates | `setName('Alice')` from code |
| Single source of truth | State IS the form data |

---

### Slide 3 — Controlled Inputs: All Types

**Layout:** Code grid covering all input types
**Content:**

```jsx
function AllInputTypes() {
  const [text, setText]         = useState('');
  const [number, setNumber]     = useState(0);
  const [email, setEmail]       = useState('');
  const [checked, setChecked]   = useState(false);
  const [selected, setSelected] = useState('cat');
  const [area, setArea]         = useState('');

  return (
    <form>
      {/* Text input */}
      <input type="text"
        value={text} onChange={e => setText(e.target.value)} />

      {/* Number input */}
      <input type="number"
        value={number} onChange={e => setNumber(Number(e.target.value))} />

      {/* Email input */}
      <input type="email"
        value={email} onChange={e => setEmail(e.target.value)} />

      {/* Checkbox — uses 'checked', not 'value' */}
      <input type="checkbox"
        checked={checked} onChange={e => setChecked(e.target.checked)} />

      {/* Select dropdown */}
      <select value={selected} onChange={e => setSelected(e.target.value)}>
        <option value="cat">Cat</option>
        <option value="dog">Dog</option>
      </select>

      {/* Textarea */}
      <textarea value={area} onChange={e => setArea(e.target.value)} />
    </form>
  );
}
```

**Key differences:**
- Text, number, email, textarea → `value` + `onChange` → `e.target.value`
- Checkbox → `checked` + `onChange` → `e.target.checked`
- Number input → parse string to number: `Number(e.target.value)`

---

### Slide 4 — Controlled Forms: Full Login Form

**Layout:** Full-width realistic form example
**Content:**

```jsx
function LoginForm() {
  const [formData, setFormData] = useState({
    email: '',
    password: '',
    rememberMe: false,
  });
  const [errors, setErrors] = useState({});
  const [isSubmitting, setIsSubmitting] = useState(false);

  // Single handler for all text inputs — uses input name attribute
  const handleChange = (e) => {
    const { name, value, type, checked } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: type === 'checkbox' ? checked : value
    }));
  };

  const validate = () => {
    const newErrors = {};
    if (!formData.email.includes('@')) newErrors.email = 'Invalid email';
    if (formData.password.length < 8) newErrors.password = 'Min 8 characters';
    return newErrors;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    const validationErrors = validate();
    if (Object.keys(validationErrors).length > 0) {
      setErrors(validationErrors);
      return;
    }
    setIsSubmitting(true);
    // await loginUser(formData) — Day 19a
    setIsSubmitting(false);
  };

  return (
    <form onSubmit={handleSubmit}>
      <input name="email" type="email"
        value={formData.email} onChange={handleChange} />
      {errors.email && <span className="error">{errors.email}</span>}

      <input name="password" type="password"
        value={formData.password} onChange={handleChange} />
      {errors.password && <span className="error">{errors.password}</span>}

      <input name="rememberMe" type="checkbox"
        checked={formData.rememberMe} onChange={handleChange} />

      <button type="submit" disabled={isSubmitting}>
        {isSubmitting ? 'Logging in...' : 'Log In'}
      </button>
    </form>
  );
}
```

---

### Slide 5 — Uncontrolled Components

**Layout:** Concept explanation + limited use case examples
**Content:**

**When uncontrolled makes sense:**
> Uncontrolled components let the DOM manage form state. You read the value only when needed — not on every keystroke.

```jsx
import { useRef } from 'react';

function SimpleSearch() {
  const inputRef = useRef(null);

  const handleSearch = () => {
    const term = inputRef.current.value; // read only when searching
    performSearch(term);
  };

  return (
    <div>
      <input ref={inputRef} type="text" defaultValue="" />
      <button onClick={handleSearch}>Search</button>
    </div>
  );
}
```

**`defaultValue` vs `value`:**
- `defaultValue` → sets initial value, then DOM takes over (uncontrolled)
- `value` → React always controls the value (controlled)
- Never mix both on the same input

**When to choose uncontrolled:**
| Scenario | Controlled | Uncontrolled |
|---|---|---|
| Live validation as user types | ✅ | ❌ |
| Instant submit button enable/disable | ✅ | ❌ |
| File input (`<input type="file">`) | ❌ impossible | ✅ only option |
| Third-party DOM library integration | ❌ conflicts | ✅ |
| Simple one-field quick search | Either | ✅ less code |

> **Rule of thumb:** Default to controlled components. Use uncontrolled only for file inputs or third-party DOM library integration.

---

### Slide 6 — useRef: Two Distinct Use Cases

**Layout:** Two-section split with clear separation
**Content:**

**`useRef` solves two completely different problems:**

```
Use Case 1: Access a DOM element directly
Use Case 2: Store a mutable value that survives re-renders without triggering them
```

**The shape of a ref:**
```jsx
const myRef = useRef(initialValue);
// Returns: { current: initialValue }
// myRef.current can be read and written freely
```

**Key behavior:**
```
useState:  changing value → triggers re-render
useRef:    changing .current → NO re-render
```

**Comparison:**
| | `useState` | `useRef` |
|---|---|---|
| Persists across renders | ✅ | ✅ |
| Triggers re-render | ✅ | ❌ |
| Stored in | State | `.current` |
| Use for | UI data | DOM refs, timers, previous values |

---

### Slide 7 — useRef: DOM Access

**Layout:** Code examples for common DOM ref patterns
**Content:**

```jsx
import { useRef, useEffect } from 'react';

function AutoFocusInput() {
  const inputRef = useRef(null);

  useEffect(() => {
    inputRef.current.focus(); // DOM access after mount
  }, []);

  return <input ref={inputRef} placeholder="Auto-focused on mount" />;
}
```

```jsx
function VideoPlayer({ src }) {
  const videoRef = useRef(null);

  const play  = () => videoRef.current.play();
  const pause = () => videoRef.current.pause();

  return (
    <div>
      <video ref={videoRef} src={src} />
      <button onClick={play}>Play</button>
      <button onClick={pause}>Pause</button>
    </div>
  );
}
```

```jsx
function ScrollToBottom() {
  const bottomRef = useRef(null);

  const scrollDown = () => {
    bottomRef.current.scrollIntoView({ behavior: 'smooth' });
  };

  return (
    <div>
      {/* ... long list ... */}
      <div ref={bottomRef} />
      <button onClick={scrollDown}>Scroll to Bottom</button>
    </div>
  );
}
```

**Rule:** Attach `ref={refName}` to the JSX element. After mount, `refName.current` is the DOM node.

---

### Slide 8 — useRef: Mutable Values Without Re-renders

**Layout:** Code comparing useState vs useRef for non-UI values
**Content:**

```jsx
import { useState, useEffect, useRef } from 'react';

function StopwatchWithRef() {
  const [displayTime, setDisplayTime] = useState(0); // ← triggers UI update
  const intervalRef = useRef(null);                  // ← no re-render needed

  const start = () => {
    intervalRef.current = setInterval(() => {
      setDisplayTime(prev => prev + 1);
    }, 1000);
  };

  const stop = () => {
    clearInterval(intervalRef.current); // read ref without re-render
  };

  useEffect(() => {
    return () => clearInterval(intervalRef.current); // cleanup
  }, []);

  return (
    <div>
      <p>Time: {displayTime}s</p>
      <button onClick={start}>Start</button>
      <button onClick={stop}>Stop</button>
    </div>
  );
}
```

**Why `useRef` for the interval ID:**
> If we stored `intervalId` in state, calling `setIntervalId()` would trigger a re-render. That's unnecessary — the UI doesn't show the interval ID. `useRef` stores it invisibly.

**Other common ref-as-mutable-value patterns:**
```jsx
const renderCount = useRef(0);        // count renders without triggering one
const previousValue = useRef(value);  // track previous prop/state value
const isMounted = useRef(true);       // prevent state update after unmount
```

---

### Slide 9 — useContext: The Prop Drilling Problem

**Layout:** Visual diagram of component tree with props passing down
**Content:**

**The problem — prop drilling:**
```
App (user: { name, role, theme })
 └── Layout (user)
      └── Sidebar (user)
           └── UserAvatar (user)  ← only this needs 'user'
```

```jsx
// Every intermediate component must accept and pass user
function Layout({ user }) {        // doesn't use user, just passes it
  return <Sidebar user={user} />;
}
function Sidebar({ user }) {       // doesn't use user, just passes it
  return <UserAvatar user={user} />;
}
function UserAvatar({ user }) {    // finally uses it
  return <img src={user.avatar} />;
}
```

**The solution — Context:**
```
App provides context ──────────────────────────────────┐
 └── Layout                                            │
      └── Sidebar                                      │
           └── UserAvatar (reads from context) ←───────┘
```

> Context lets a parent component provide data to any descendant, no matter how deep, without passing it through every level as props.

---

### Slide 10 — useContext: Creating and Providing Context

**Layout:** Full code — context setup
**Content:**

**Step 1 — Create the context (usually its own file):**
```jsx
// context/ThemeContext.js
import { createContext, useState } from 'react';

// createContext(defaultValue) — default used when no Provider above
export const ThemeContext = createContext('light');

// Provider component — wraps the tree that needs this context
export function ThemeProvider({ children }) {
  const [theme, setTheme] = useState('light');

  const toggleTheme = () => {
    setTheme(prev => prev === 'light' ? 'dark' : 'light');
  };

  return (
    <ThemeContext.Provider value={{ theme, toggleTheme }}>
      {children}
    </ThemeContext.Provider>
  );
}
```

**Step 2 — Wrap the app (or subtree) with the Provider:**
```jsx
// App.jsx
import { ThemeProvider } from './context/ThemeContext';

function App() {
  return (
    <ThemeProvider>
      <Layout />
    </ThemeProvider>
  );
}
```

**Key points:**
- `createContext()` creates the context object
- `Context.Provider` with `value={...}` makes data available to all descendants
- When the `value` changes, all consumers re-render automatically

---

### Slide 11 — useContext: Consuming Context

**Layout:** Code — consumption pattern + custom hook wrapper
**Content:**

**Step 3 — Consume with useContext:**
```jsx
import { useContext } from 'react';
import { ThemeContext } from '../context/ThemeContext';

function Header() {
  // Read from context — no props needed
  const { theme, toggleTheme } = useContext(ThemeContext);

  return (
    <header className={`header ${theme}`}>
      <h1>My App</h1>
      <button onClick={toggleTheme}>
        Switch to {theme === 'light' ? 'Dark' : 'Light'} Mode
      </button>
    </header>
  );
}

function UserAvatar() {
  const { theme } = useContext(ThemeContext);
  return <div className={`avatar ${theme}`}>...</div>;
}
```

**Best practice — wrap in a custom hook:**
```jsx
// In ThemeContext.js — add this export
export function useTheme() {
  const context = useContext(ThemeContext);
  if (!context) {
    throw new Error('useTheme must be used within a ThemeProvider');
  }
  return context;
}

// In components — cleaner consumption
const { theme, toggleTheme } = useTheme();
```

**When to use Context:**
- Theme (light/dark mode)
- Current authenticated user
- Language/locale preference
- Any data needed by many components at different levels

---

### Slide 12 — useContext vs Props: Decision Guide

**Layout:** Decision flowchart + comparison table
**Content:**

**Use props when:**
```jsx
// Parent → direct child, or 1-2 levels deep
function ProductCard({ product, onAddToCart }) {
  return (
    <div>
      <h3>{product.name}</h3>
      <button onClick={() => onAddToCart(product)}>Add</button>
    </div>
  );
}
```

**Use context when:**
```jsx
// Data needed by many components at various levels
// Examples: theme, auth user, language, cart state

// ❌ Prop drilling — bad
<App user={user}>
  <Layout user={user}>
    <Page user={user}>
      <Header user={user}>
        <Avatar user={user} />
```
```jsx
// ✅ Context — good
const { user } = useUser(); // anywhere in the tree
```

**Decision guide:**
| Question | If Yes → |
|---|---|
| Is it passed more than 2 levels deep? | Consider Context |
| Do many unrelated components need it? | Use Context |
| Is it local to a component subtree? | Props first |
| Does it change infrequently? | Good Context candidate |
| Does it change very frequently? | Caution — can cause performance issues |

**Important:** Context is not a state management replacement for complex global state like shopping carts or Redux-level data (covered Day 18a).

---

### Slide 13 — Custom Hooks: What and Why

**Layout:** Before/after code comparison
**Content:**

**The problem — duplicated logic:**
```jsx
// Component A
function UserProfile() {
  const [windowWidth, setWindowWidth] = useState(window.innerWidth);
  useEffect(() => {
    const handler = () => setWindowWidth(window.innerWidth);
    window.addEventListener('resize', handler);
    return () => window.removeEventListener('resize', handler);
  }, []);
  // ... now build the UI
}

// Component B — identical logic copy-pasted
function Sidebar() {
  const [windowWidth, setWindowWidth] = useState(window.innerWidth);
  useEffect(() => { /* same thing again */ }, []);
}
```

**The solution — custom hook:**
```jsx
// hooks/useWindowWidth.js
function useWindowWidth() {
  const [width, setWidth] = useState(window.innerWidth);
  useEffect(() => {
    const handler = () => setWidth(window.innerWidth);
    window.addEventListener('resize', handler);
    return () => window.removeEventListener('resize', handler);
  }, []);
  return width;
}

// Now in any component:
function UserProfile() {
  const width = useWindowWidth(); // one line!
}
function Sidebar() {
  const width = useWindowWidth(); // same logic, no duplication
}
```

**Custom hook rules:**
- Name MUST start with `use` (React convention — enables the linter rules)
- Can call other hooks inside
- Each component that calls a hook gets its own isolated state

---

### Slide 14 — Custom Hook: useLocalStorage

**Layout:** Full implementation with usage
**Content:**

```jsx
// hooks/useLocalStorage.js
import { useState } from 'react';

function useLocalStorage(key, initialValue) {
  const [storedValue, setStoredValue] = useState(() => {
    try {
      const item = window.localStorage.getItem(key);
      return item ? JSON.parse(item) : initialValue;
    } catch (error) {
      return initialValue;
    }
  });

  const setValue = (value) => {
    try {
      // Allow value to be a function (same API as useState)
      const valueToStore = value instanceof Function
        ? value(storedValue)
        : value;
      setStoredValue(valueToStore);
      window.localStorage.setItem(key, JSON.stringify(valueToStore));
    } catch (error) {
      console.error(error);
    }
  };

  return [storedValue, setValue];
}

export default useLocalStorage;
```

**Usage — identical API to useState:**
```jsx
function ThemeToggle() {
  // Persists to localStorage automatically
  const [theme, setTheme] = useLocalStorage('theme', 'light');
  return (
    <button onClick={() => setTheme(prev => prev === 'light' ? 'dark' : 'light')}>
      Current: {theme}
    </button>
  );
}
```

**What makes this a good custom hook:**
- Encapsulates related state + side effect logic
- API mirrors `useState` — easy to adopt
- Reusable for any localStorage key
- Handles errors internally

---

### Slide 15 — Custom Hook: useToggle and useCounter

**Layout:** Two smaller hooks showing the pattern at scale
**Content:**

**useToggle — simplify boolean state:**
```jsx
// hooks/useToggle.js
function useToggle(initialValue = false) {
  const [value, setValue] = useState(initialValue);
  const toggle = () => setValue(prev => !prev);
  const setTrue = () => setValue(true);
  const setFalse = () => setValue(false);
  return { value, toggle, setTrue, setFalse };
}

// Usage
function Modal() {
  const { value: isOpen, toggle, setFalse: close } = useToggle(false);
  return (
    <>
      <button onClick={toggle}>Open Modal</button>
      {isOpen && <div className="modal"><button onClick={close}>×</button></div>}
    </>
  );
}
```

**useCounter — reusable counter logic:**
```jsx
function useCounter(initialValue = 0, step = 1) {
  const [count, setCount] = useState(initialValue);
  const increment = () => setCount(prev => prev + step);
  const decrement = () => setCount(prev => prev - step);
  const reset = () => setCount(initialValue);
  return { count, increment, decrement, reset };
}

// Usage
function ProductQuantity() {
  const { count, increment, decrement } = useCounter(1, 1);
  return (
    <div>
      <button onClick={decrement}>-</button>
      <span>{count}</span>
      <button onClick={increment}>+</button>
    </div>
  );
}
```

---

### Slide 16 — Component Lifecycle: Hooks vs Class Methods

**Layout:** Three-column mapping table
**Content:**

**Mapping class lifecycle to hooks:**

| Class Lifecycle | Hook Equivalent | When It Runs |
|---|---|---|
| `constructor` | `useState(initialValue)` | Once, on first render |
| `componentDidMount` | `useEffect(() => {}, [])` | After first render |
| `componentDidUpdate` | `useEffect(() => {}, [deps])` | After render when deps change |
| `componentWillUnmount` | `return () => cleanup` inside useEffect | Just before component is removed |
| `shouldComponentUpdate` | `React.memo` (Day 20a) | Before re-render decision |
| `getDerivedStateFromProps` | Compute during render | Every render |

**Visual lifecycle timeline:**
```
Component created
      ↓
  Render (function body runs)
      ↓
  DOM updated
      ↓
  useEffect(fn, []) runs ← "componentDidMount"
      ↓
  [props or state changes]
      ↓
  Re-render (function body runs again)
      ↓
  DOM updated
      ↓
  useEffect(fn, [dep]) cleanup → then effect re-runs ← "componentDidUpdate"
      ↓
  Component removed from DOM
      ↓
  useEffect cleanup runs ← "componentWillUnmount"
```

**Bottom callout:**
> With hooks, the lifecycle is no longer a set of named methods — it's a natural consequence of when `useEffect` runs and cleans up. One mental model replaces four separate lifecycle methods.

---

### Slide 17 — Day 17a Summary

**Layout:** Summary table + Week 4 roadmap
**Content:**

**Day 17a Complete — All 5 Learning Objectives:**

| Learning Objective | How We Covered It |
|---|---|
| ✅ Manage component state with `useState` | Basics, multiple state, functional updates, anti-patterns |
| ✅ Handle side effects with `useEffect` | Deps array (no array / [] / [deps]), cleanup, common patterns |
| ✅ Create controlled forms | All input types, single-handler pattern, live validation |
| ✅ Share state using Context API | `createContext`, `Provider`, `useContext`, custom hook wrapper |
| ✅ Build custom hooks for reusable logic | `useWindowWidth`, `useLocalStorage`, `useToggle`, `useCounter` |

**Hooks Summary:**
| Hook | Primary Use |
|---|---|
| `useState` | Local component state |
| `useEffect` | Side effects, lifecycle |
| `useRef` | DOM access, mutable values |
| `useContext` | Consume shared context |
| Custom hooks | Encapsulate + reuse stateful logic |

**Week 4 React Track Remaining:**
- **Day 18a** — React Router (client-side navigation) + Redux (global state management) + `useSelector`/`useDispatch`
- **Day 19a** — API integration with fetch/axios, error boundaries, loading states, React Testing Library, Jest
- **Day 20a** — Advanced patterns: `React.memo`, `useMemo`, `useCallback`, code splitting, Suspense, deployment

---

*[END OF PART 2 SLIDE DESCRIPTIONS]*

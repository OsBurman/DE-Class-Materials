# Day 17a Review: React Hooks

**Week 4 — Tuesday (React Track)**
**Prerequisites:** Day 16a (React Fundamentals — components, JSX, props)
**Leads into:** Day 18a (React Router & Redux), Day 19a (React API & Testing)

---

## Learning Objectives Checklist

By the end of Day 17a, students can:
- [ ] Manage component state with `useState`
- [ ] Handle side effects with `useEffect`
- [ ] Create controlled forms in React
- [ ] Share state using the Context API
- [ ] Build custom hooks for reusable stateful logic

---

## Why Hooks?

React 16.8 (February 2019) introduced hooks — functions that let function components use state, side effects, and other React features previously only available in class components.

**Key rules (enforced by `eslint-plugin-react-hooks`):**
1. Call hooks at the **top level** — never inside `if`, `for`, or nested functions
2. Call hooks only from **React function components** or **custom hooks**

---

## useState

### Basic Syntax

```jsx
import { useState } from 'react';

function Counter() {
  const [count, setCount] = useState(0);
  //     ↑ current   ↑ setter    ↑ initial value

  return (
    <button onClick={() => setCount(count + 1)}>
      Count: {count}
    </button>
  );
}
```

- `useState(initialValue)` returns `[currentValue, setterFunction]`
- Calling the setter triggers a re-render with the new value
- Initial value is only used on the first render

### Multiple State Variables

```jsx
// ✅ Prefer separate variables for independent pieces of state
const [name, setName]     = useState('');
const [email, setEmail]   = useState('');
const [isOpen, setIsOpen] = useState(false);

// ✅ Object state — for values that always update together
const [form, setForm] = useState({ name: '', email: '' });

// ⚠️ With object state, ALWAYS spread — useState does NOT auto-merge
setForm(prev => ({ ...prev, name: 'Alice' }));   // ✅ keeps email
setForm({ name: 'Alice' });                       // ❌ loses email
```

### Functional Updates

```jsx
// Use prev => when new value depends on old value
setCount(prev => prev + 1);           // ✅
setItems(prev => [...prev, newItem]); // ✅
setIsOpen(prev => !prev);             // ✅

// ❌ Stale closure risk — avoid when batching updates
setCount(count + 1);  // count may be stale in async callbacks
```

### Lazy Initialization

```jsx
// Pass a function to avoid running expensive code on every render
const [data, setData] = useState(() => {
  return JSON.parse(localStorage.getItem('data')) || [];
});
```

### What NOT to Put in State

```jsx
// ❌ Derived values — they go out of sync
const [items, setItems] = useState([]);
const [count, setCount] = useState(0);    // derived from items.length!

// ✅ Compute during render — always in sync
const [items, setItems] = useState([]);
const count = items.length;
const total = items.reduce((sum, i) => sum + i.price, 0);
const filtered = items.filter(i => i.inStock);
```

**Rule:** If a value can be derived from state or props, compute it during render — not in state.

---

## useEffect

### Dependency Array: Three Configurations

```jsx
// No array → runs after EVERY render (rarely used)
useEffect(() => {
  console.log('Every render');
});

// Empty array → runs ONCE after mount (≈ componentDidMount)
useEffect(() => {
  fetchInitialData();
}, []);

// With deps → runs after mount AND when deps change
useEffect(() => {
  fetchUserById(userId);
}, [userId]);
```

### Cleanup Functions

```jsx
useEffect(() => {
  const intervalId = setInterval(() => setCount(c => c + 1), 1000);

  return () => clearInterval(intervalId); // ← runs before next effect or on unmount
}, []);
```

**When cleanup runs:**
- Just before the component is removed from the DOM (unmount)
- Just before the effect runs again when a dependency changed

### Common Patterns

```jsx
// Document title
useEffect(() => {
  document.title = `(${unread}) Messages`;
}, [unread]);

// LocalStorage sync
useEffect(() => {
  localStorage.setItem('theme', theme);
}, [theme]);

// Window event listener (needs cleanup)
useEffect(() => {
  const handler = () => setWidth(window.innerWidth);
  window.addEventListener('resize', handler);
  return () => window.removeEventListener('resize', handler);
}, []);

// Timer (needs cleanup)
useEffect(() => {
  const id = setInterval(tick, 1000);
  return () => clearInterval(id);
}, []);

// Data fetching shell (full pattern in Day 19a)
useEffect(() => {
  fetchUser(userId);
}, [userId]);
```

### The Dependency Rule

Every value used inside `useEffect` must be listed in the dependency array. Missing a dependency causes stale closure bugs — the effect sees an outdated value.

```jsx
// ❌ ESLint warning — userId used but not listed
useEffect(() => {
  fetchUser(userId);
}, []);  // userId never re-fetched when it changes!

// ✅ Correct
useEffect(() => {
  fetchUser(userId);
}, [userId]);
```

### Common useEffect Pitfalls

```jsx
// ❌ Infinite loop — effect changes its own dependency
useEffect(() => {
  setData(process(data));
}, [data]);

// ❌ Object dependency — new reference every render
useEffect(() => { ... }, [{ id: userId }]); // always re-runs!

// ✅ Use primitive values as dependencies
useEffect(() => { ... }, [userId]); // only re-runs when userId changes
```

---

## Event Handling

### Syntax Differences from HTML

```jsx
// HTML
<button onclick="handleClick()">Click</button>

// React — camelCase, function reference (not a call)
<button onClick={handleClick}>Click</button>

// ❌ Wrong — calls the function immediately during render
<button onClick={handleClick()}>Click</button>
```

### Handler Patterns

```jsx
// Inline (for simple one-liners)
<button onClick={() => setCount(c => c + 1)}>+</button>

// Method reference (no args)
<button onClick={handleSubmit}>Submit</button>

// Passing args
<button onClick={() => handleDelete(item.id)}>Delete</button>
```

### SyntheticEvent Properties

| Property / Method | Use |
|---|---|
| `e.target.value` | Current input value |
| `e.target.checked` | Checkbox state (boolean) |
| `e.preventDefault()` | Stop default behavior (form reload, link) |
| `e.stopPropagation()` | Stop event from bubbling |
| `e.key` | Key name for keyboard events (`'Enter'`, `'Escape'`) |
| `e.type` | Event type string (`'click'`, `'submit'`) |

---

## Controlled Components

### The Pattern

```jsx
// value prop + onChange handler = controlled input
const [name, setName] = useState('');
<input value={name} onChange={e => setName(e.target.value)} />
```

### All Input Types

```jsx
// Text / email / number / textarea → value + e.target.value
<input type="text"  value={text}   onChange={e => setText(e.target.value)} />
<input type="number" value={num}   onChange={e => setNum(Number(e.target.value))} />
<textarea          value={area}    onChange={e => setArea(e.target.value)} />

// Checkbox → checked + e.target.checked
<input type="checkbox" checked={agreed} onChange={e => setAgreed(e.target.checked)} />

// Select → value on <select> element
<select value={choice} onChange={e => setChoice(e.target.value)}>
  <option value="a">Option A</option>
  <option value="b">Option B</option>
</select>
```

### Single Handler for Multiple Fields

```jsx
const [form, setForm] = useState({ email: '', password: '', remember: false });

const handleChange = (e) => {
  const { name, value, type, checked } = e.target;
  setForm(prev => ({
    ...prev,
    [name]: type === 'checkbox' ? checked : value
  }));
};

// Each input needs a matching 'name' attribute
<input name="email"    value={form.email}    onChange={handleChange} />
<input name="password" value={form.password} onChange={handleChange} type="password" />
<input name="remember" checked={form.remember} onChange={handleChange} type="checkbox" />
```

### Form Submission

```jsx
const handleSubmit = (e) => {
  e.preventDefault();  // ← ALWAYS — prevents page reload
  // form data is in state — ready to use
  console.log(form);
};

<form onSubmit={handleSubmit}>
  {/* inputs */}
  <button type="submit">Submit</button>
</form>
```

---

## Uncontrolled Components

```jsx
// DOM manages value — React reads it when needed
const inputRef = useRef(null);
const handleSearch = () => performSearch(inputRef.current.value);

<input ref={inputRef} defaultValue="" />  // defaultValue (not value)
<button onClick={handleSearch}>Search</button>
```

**Use uncontrolled for:**
- `<input type="file">` — cannot be controlled (browser security)
- Third-party DOM libraries that manage their own DOM

**Default choice:** controlled components.

---

## useRef

### Two Use Cases

```
Use Case 1: Directly access a DOM element
Use Case 2: Store a mutable value that survives renders without triggering them
```

```jsx
const ref = useRef(initialValue);
// Returns: { current: initialValue }
// Reading/writing ref.current does NOT trigger re-render
```

### DOM Access

```jsx
function AutoFocusInput() {
  const inputRef = useRef(null);

  useEffect(() => {
    inputRef.current.focus(); // access DOM after mount
  }, []);

  return <input ref={inputRef} />;
}
```

```jsx
// Video control
const videoRef = useRef(null);
<video ref={videoRef} src={src} />
<button onClick={() => videoRef.current.play()}>Play</button>
<button onClick={() => videoRef.current.pause()}>Pause</button>
```

### Mutable Values (No Re-render)

```jsx
// Store interval ID without triggering re-render on assignment
const intervalRef = useRef(null);

const start = () => {
  intervalRef.current = setInterval(tick, 1000);
};
const stop = () => {
  clearInterval(intervalRef.current);
};
```

Other ref-as-mutable-value uses:
```jsx
const renderCount = useRef(0);     // count renders without re-rendering
const prevValue = useRef(value);   // track previous prop/state
```

### useState vs useRef

| | `useState` | `useRef` |
|---|---|---|
| Persists across renders | ✅ | ✅ |
| Triggers re-render on change | ✅ | ❌ |
| Access via | Direct variable | `.current` |
| Use for | UI state | DOM refs, timers, non-UI values |

---

## useContext

### Setup (Three Steps)

**Step 1 — Create context and provider:**
```jsx
// context/ThemeContext.js
import { createContext, useState } from 'react';

export const ThemeContext = createContext('light'); // default value

export function ThemeProvider({ children }) {
  const [theme, setTheme] = useState('light');
  const toggle = () => setTheme(p => p === 'light' ? 'dark' : 'light');

  return (
    <ThemeContext.Provider value={{ theme, toggle }}>
      {children}
    </ThemeContext.Provider>
  );
}

// Custom hook wrapper (best practice)
export function useTheme() {
  const ctx = useContext(ThemeContext);
  if (!ctx) throw new Error('useTheme must be inside ThemeProvider');
  return ctx;
}
```

**Step 2 — Wrap the tree:**
```jsx
// App.jsx
<ThemeProvider>
  <App />
</ThemeProvider>
```

**Step 3 — Consume anywhere in the tree:**
```jsx
import { useTheme } from '../context/ThemeContext';

function Header() {
  const { theme, toggle } = useTheme();
  return <button onClick={toggle}>Mode: {theme}</button>;
}
```

### When to Use Context vs Props

| Situation | Recommendation |
|---|---|
| Direct parent → child | Props |
| 1–2 levels deep | Props |
| Many components at different levels need it | Context |
| Passing through components that don't use it | Context |
| Frequently changing high-frequency state | Caution — can cause performance issues |
| Theme, auth user, language/locale | ✅ Good context candidates |
| Shopping cart, undo/redo, complex global state | Redux (Day 18a) |

---

## Custom Hooks

### Rules

- Name **must** start with `use`
- Can call other hooks inside
- Each component gets **its own isolated state** — hooks don't share state between callers

### Examples

**useWindowWidth:**
```jsx
function useWindowWidth() {
  const [width, setWidth] = useState(window.innerWidth);
  useEffect(() => {
    const handler = () => setWidth(window.innerWidth);
    window.addEventListener('resize', handler);
    return () => window.removeEventListener('resize', handler);
  }, []);
  return width;
}

const width = useWindowWidth(); // use in any component
```

**useLocalStorage:**
```jsx
function useLocalStorage(key, initialValue) {
  const [value, setValue] = useState(() => {
    const item = localStorage.getItem(key);
    return item ? JSON.parse(item) : initialValue;
  });

  const set = (newValue) => {
    const v = newValue instanceof Function ? newValue(value) : newValue;
    setValue(v);
    localStorage.setItem(key, JSON.stringify(v));
  };

  return [value, set]; // same API as useState
}

const [theme, setTheme] = useLocalStorage('theme', 'light');
```

**useToggle:**
```jsx
function useToggle(init = false) {
  const [value, setValue] = useState(init);
  return {
    value,
    toggle:   () => setValue(p => !p),
    setTrue:  () => setValue(true),
    setFalse: () => setValue(false),
  };
}

const { value: isOpen, toggle, setFalse: close } = useToggle();
```

**useCounter:**
```jsx
function useCounter(init = 0, step = 1) {
  const [count, setCount] = useState(init);
  return {
    count,
    increment: () => setCount(p => p + step),
    decrement: () => setCount(p => p - step),
    reset:     () => setCount(init),
  };
}

const { count, increment, decrement } = useCounter(1);
```

---

## Component Lifecycle: Hooks vs Class Methods

| Class Lifecycle | Hook Equivalent | When |
|---|---|---|
| `constructor` | `useState(init)` | Once, first render |
| `componentDidMount` | `useEffect(() => {}, [])` | After first render |
| `componentDidUpdate` | `useEffect(() => {}, [deps])` | After render when deps change |
| `componentWillUnmount` | `return () => cleanup` in effect | Before component removed |
| `getDerivedStateFromProps` | Compute during render | Every render |

```
Component renders
    ↓ DOM updated
useEffect(fn, []) runs              ← "componentDidMount"
    ↓
[State/props change]
    ↓ Re-render, DOM updated
Previous cleanup → effect re-runs  ← "componentDidUpdate"
    ↓
Component removed from DOM
    ↓
Cleanup runs                        ← "componentWillUnmount"
```

---

## Complete Example: Combining All Hooks

```jsx
import { useState, useEffect, useRef, useContext } from 'react';
import { useTheme } from '../context/ThemeContext';
import { useLocalStorage } from '../hooks/useLocalStorage';

function NotesApp() {
  const { theme }                     = useTheme();                   // Context
  const [note, setNote]               = useLocalStorage('note', ''); // Custom hook
  const [charCount, setCharCount]     = useState(0);                 // useState
  const [saveStatus, setSaveStatus]   = useState('Saved');           // useState
  const textareaRef                   = useRef(null);                 // useRef (DOM)

  // Derived value — NOT in state
  const isOverLimit = charCount > 500;

  // Side effect — update char count when note changes
  useEffect(() => {
    setCharCount(note.length);
    setSaveStatus('Saved');
  }, [note]);

  // Side effect — auto-focus on mount
  useEffect(() => {
    textareaRef.current?.focus();
  }, []);

  return (
    <div className={`app ${theme}`}>
      <p>{saveStatus} | {charCount}/500 chars</p>
      <textarea
        ref={textareaRef}
        value={note}
        onChange={e => setNote(e.target.value)}
        className={isOverLimit ? 'over-limit' : ''}
      />
      {isOverLimit && <p className="warning">Over 500 character limit!</p>}
    </div>
  );
}
```

---

## Common Mistakes and Fixes

| Mistake | Symptom | Fix |
|---|---|---|
| `onClick={handler()}` with parens | Handler fires immediately on render | Remove parens: `onClick={handler}` |
| Missing `e.preventDefault()` | Form reloads the page on submit | Add `e.preventDefault()` as first line |
| Derived value in state | State goes out of sync | Compute from state during render |
| Missing deps in `useEffect` | Stale data, effect doesn't re-run | Add all used values to deps array |
| Object/array in deps array | Infinite re-render loop | Use primitive values or `useMemo` (Day 20a) |
| Effect changes its own dependency | Infinite render loop | Restructure logic |
| Object state without spread | Other fields disappear on update | `setObj(prev => ({...prev, key: val}))` |
| `useState` in conditional | Hook order changes, React error | Move hook to top level unconditionally |
| No cleanup for setInterval | Interval fires after unmount | Return `() => clearInterval(id)` from effect |

---

## Quick Reference: All Hooks from Day 17a

```jsx
// State
const [value, setValue] = useState(initialValue);
const [value, setValue] = useState(() => expensiveInit()); // lazy

// Effects
useEffect(() => { /* side effect */ });              // every render
useEffect(() => { /* side effect */ }, []);          // mount only
useEffect(() => { /* side effect */ }, [a, b]);      // mount + when a/b change
useEffect(() => { return () => cleanup(); }, []);    // with cleanup

// DOM refs and mutable values
const ref = useRef(null);             // DOM: <div ref={ref}>
const valueRef = useRef(initialVal);  // Mutable: ref.current = newVal

// Context
const value = useContext(MyContext);  // reads nearest Provider value

// Custom hooks (must start with 'use')
const width = useWindowWidth();
const [stored, setStored] = useLocalStorage('key', default);
const { value, toggle } = useToggle(false);
const { count, increment } = useCounter(0);
```

---

## Looking Ahead: Day 18a (Tomorrow — React Track)

| Today Built | Tomorrow Adds |
|---|---|
| `useContext` for theme/user | Global state with Redux |
| Single-page component navigation | React Router — multiple pages/views |
| Data in component state | Redux store — centralized, predictable state |
| `useContext` consumption | `useSelector` + `useDispatch` (Redux hooks) |

Day 18a introduces React Router for client-side navigation and Redux for managing global application state at scale. The hooks foundation you built today — especially `useState` and the mental model of state — is exactly what prepares you to understand Redux's action → reducer → state cycle.

---

*Day 17a Complete — React Hooks*

# Week 4 - Day 17a: React Hooks
## Part 2 Lecture Script — Forms, useRef, useContext & Custom Hooks

**Total runtime:** 60 minutes
**Delivery pace:** ~165 words/minute
**Format:** Verbatim instructor script with [MM:SS–MM:SS] timing markers

---

## [00:00–02:00] Opening

Welcome back to Part 2. In Part 1 we covered the two foundational hooks — `useState` for state and `useEffect` for side effects — and we tied them together with React's event handling system. If you're solid on those three things, you can build most of what React applications actually do.

Part 2 goes in four directions. First, controlled forms — the full pattern for managing form input in React, which builds directly on `useState`. Second, `useRef` — a hook with two distinct use cases that look nothing alike. Third, `useContext` — how to share state across your component tree without passing props through every level. And fourth, custom hooks — the feature that makes all the other hooks composable and reusable.

We'll finish with the lifecycle mapping: how the mental model of `componentDidMount` and `componentWillUnmount` translates cleanly into hooks.

Let's start with forms.

---

## [02:00–08:00] Controlled Components

**[Slide 2 — Controlled Components: The Core Idea]**

A controlled component is a form input — text, checkbox, select, textarea — where React owns the current value. The component's state is the single source of truth, and the DOM just reflects it.

Compare two ways to write a text input.

Uncontrolled:
```jsx
<input type="text" defaultValue="Alice" />
```

This sets an initial value and then the DOM takes over. React doesn't know what's in the input after the user types. To read the value you'd have to reach into the DOM.

Controlled:
```jsx
const [name, setName] = useState('');
<input value={name} onChange={e => setName(e.target.value)} />
```

Every keystroke fires `onChange`. The handler calls `setName` with the new value. React re-renders. The `value` prop on the input is set to `name` — which now has the new value. The input displays what React says to display. React is always in charge.

Why does this matter? Because when React owns the value, you have instant access to it. You can validate as the user types. You can enable or disable the submit button based on whether the form is valid. You can programmatically set field values from code. You can reset the form by setting state back to empty strings.

The pattern is always the same: `value={stateVariable}` plus `onChange={e => setStateVariable(e.target.value)}`.

---

## [08:00–15:00] Controlled Inputs: All Types and Full Form

**[Slides 3 & 4 — All Input Types and Full Login Form]**

Let me walk through the controlled pattern for every input type you'll encounter.

Text inputs, number inputs, email inputs, and textareas all work the same way: `value={stateVar}` and `onChange={e => setter(e.target.value)}`. For number inputs, `e.target.value` is a string — parse it: `setter(Number(e.target.value))`.

Checkboxes are different. Instead of `value`, you use `checked`. Instead of `e.target.value`, you read `e.target.checked`:
```jsx
<input type="checkbox" checked={isAgreed} onChange={e => setIsAgreed(e.target.checked)} />
```

Select dropdowns use `value` on the `<select>` element itself — the `value` prop sets which `<option>` is currently selected:
```jsx
<select value={selected} onChange={e => setSelected(e.target.value)}>
  <option value="cat">Cat</option>
  <option value="dog">Dog</option>
</select>
```

Now let me show you the pattern for a full multi-field form, because writing a separate handler for every field gets tedious fast. The clean solution uses the input's `name` attribute:

```jsx
const [formData, setFormData] = useState({ email: '', password: '', rememberMe: false });

const handleChange = (e) => {
  const { name, value, type, checked } = e.target;
  setFormData(prev => ({
    ...prev,
    [name]: type === 'checkbox' ? checked : value
  }));
};
```

One handler covers every field. When the email input fires `onChange`, `name` is `"email"` and `value` is what the user typed. When the checkbox fires, `name` is `"rememberMe"` and we use `checked` instead of `value`. The computed property `[name]` updates only that key in the object, spreading the rest.

For form submission:
```jsx
const handleSubmit = (e) => {
  e.preventDefault();  // ← critical — stops page reload
  // Now formData has all the values
  console.log(formData);
};
```

`e.preventDefault()` is the most important line in any form handler. Without it, the browser's default form submission behavior fires — the page reloads, and your React state is gone. Always call `e.preventDefault()` in `onSubmit` handlers.

For validation: check your values before proceeding. Collect errors into an object, set them to state, and conditionally render error messages next to each field. The submit button can be conditionally disabled:
```jsx
<button type="submit" disabled={!formData.email || !formData.password}>
  Log In
</button>
```

Full validation libraries like Zod paired with React Hook Form are common in production. But the mental model is the same as what we're doing here — the library just automates the pattern.

---

## [15:00–20:00] Uncontrolled Components

**[Slide 5 — Uncontrolled Components]**

Uncontrolled components let the DOM manage the value. You read it when you need it rather than tracking every keystroke.

```jsx
import { useRef } from 'react';

function SimpleSearch() {
  const inputRef = useRef(null);

  const handleSearch = () => {
    const term = inputRef.current.value;
    performSearch(term);
  };

  return (
    <div>
      <input ref={inputRef} type="text" />
      <button onClick={handleSearch}>Search</button>
    </div>
  );
}
```

We haven't formally covered `useRef` yet — I'll explain it in the next section — but you can see the pattern: attach `ref={inputRef}` to the element, and `inputRef.current` is the DOM node. `.value` reads the current value at the moment you need it.

Use `defaultValue` instead of `value` for the initial value in an uncontrolled input. If you use `value` without `onChange`, React will warn you that you've created a read-only input.

The guidance I'll give you: default to controlled components. The explicit `value` plus `onChange` pattern gives you more control, more clarity, and fewer bugs. There are two situations where uncontrolled is the right choice.

First: file inputs. `<input type="file">` cannot be controlled — the browser won't let JavaScript set the value of a file input for security reasons. You must use `useRef` and read `inputRef.current.files` when you need it. This is the one unambiguous case where uncontrolled is not a choice, it's a requirement.

Second: third-party DOM libraries. If you're integrating a library like a rich text editor, a date picker, or a charting library that manages its own DOM and needs direct access to an element, uncontrolled refs are the integration mechanism. We'll see this in the `useRef` section.

---

## [20:00–27:30] useRef: Two Use Cases

**[Slides 6, 7 & 8 — useRef Basics, DOM Access, Mutable Values]**

`useRef` is a hook with two completely distinct use cases. I want to explain both clearly because people often only learn one and are confused when they see the other.

`useRef` returns an object: `{ current: initialValue }`. That object persists for the entire lifetime of the component. The key characteristic of `useRef` is that reading or writing `ref.current` does NOT trigger a re-render.

Compare this to `useState`: calling a state setter triggers a re-render. `useRef` changes are invisible to React's rendering system. This is the defining difference.

**Use case one: accessing a DOM element directly.**

```jsx
function AutoFocusInput() {
  const inputRef = useRef(null);

  useEffect(() => {
    inputRef.current.focus();
  }, []);

  return <input ref={inputRef} placeholder="Auto-focused on mount" />;
}
```

You attach the ref to a JSX element with the `ref` prop. After React mounts the element to the DOM, it sets `ref.current` to the actual DOM node. From `ngAfterViewInit` in Angular, you know this pattern — you can't access the DOM before it's rendered. `useEffect` with `[]` is exactly the right place.

Once you have the DOM node, you can call any native DOM method on it. `.focus()` to focus an input. `.play()` and `.pause()` on a video element. `.scrollIntoView()` to scroll to an element. `.getBoundingClientRect()` to measure an element's dimensions. Any DOM method that you'd use with `document.querySelector`, but in the React way.

```jsx
function VideoPlayer({ src }) {
  const videoRef = useRef(null);
  return (
    <div>
      <video ref={videoRef} src={src} />
      <button onClick={() => videoRef.current.play()}>Play</button>
      <button onClick={() => videoRef.current.pause()}>Pause</button>
    </div>
  );
}
```

**Use case two: a mutable value that survives re-renders without causing them.**

```jsx
function Stopwatch() {
  const [time, setTime] = useState(0);
  const intervalRef = useRef(null);  // ← stores the interval ID

  const start = () => {
    intervalRef.current = setInterval(() => setTime(t => t + 1), 1000);
  };

  const stop = () => {
    clearInterval(intervalRef.current);
  };

  useEffect(() => {
    return () => clearInterval(intervalRef.current);
  }, []);

  return (
    <div>
      <p>Time: {time}s</p>
      <button onClick={start}>Start</button>
      <button onClick={stop}>Stop</button>
    </div>
  );
}
```

Why `useRef` for the interval ID instead of `useState`? Because calling `setIntervalId()` would trigger a re-render. The UI doesn't need to display the interval ID — it's just a value we need to reference when stopping the timer. `useRef` stores it invisibly. No re-render. No performance cost. Just a persistent container.

Other patterns where this use case applies: tracking how many times a component has rendered, storing the previous value of a prop or state to compare with the current, and an `isMounted` flag to prevent calling `setState` after unmount. All of these need to persist across renders but don't need to cause re-renders.

---

## [27:30–36:00] useContext: Solving Prop Drilling

**[Slides 9, 10 & 11 — Context Problem, Creating Context, Consuming Context]**

Let's talk about `useContext` — React's solution to prop drilling.

Here's the scenario. You have user data at the top of your application — the currently logged-in user's name, avatar, and role. Somewhere deep in your component tree — maybe four or five levels down — a small avatar component needs to display the user's profile picture. With props alone, you have to pass `user` through every component between them. The Layout component doesn't need `user`, but it accepts it as a prop so it can pass it to Sidebar. Sidebar doesn't need it either, but passes it to UserNav. UserNav passes it to UserAvatar. Finally, UserAvatar uses it.

This is called prop drilling, and it's a genuine maintenance problem. When the structure of `user` changes, you potentially need to update every component in the chain. Components have props they don't care about and shouldn't need to know about.

Context solves this by allowing a parent to make data available to any descendant, at any depth, without threading it through every level.

Here's how you set it up. First, create the context — typically in its own file:

```jsx
// context/ThemeContext.js
import { createContext, useState } from 'react';

export const ThemeContext = createContext('light');

export function ThemeProvider({ children }) {
  const [theme, setTheme] = useState('light');

  const toggleTheme = () => setTheme(prev => prev === 'light' ? 'dark' : 'light');

  return (
    <ThemeContext.Provider value={{ theme, toggleTheme }}>
      {children}
    </ThemeContext.Provider>
  );
}
```

`createContext` creates the context object. The argument — `'light'` — is the default value, used only if a component calls `useContext` without a matching `Provider` anywhere above it in the tree. In practice, you always use a Provider.

`ThemeProvider` is a component you create. It holds the state and wraps `ThemeContext.Provider`. The `value` prop is what gets passed to all consumers. Any data you put in `value` is accessible anywhere inside `ThemeProvider`.

Second, wrap your application — or the relevant subtree — with the Provider:

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

Any component inside `ThemeProvider` can now read the theme context. No props needed.

Third, consume with `useContext`:

```jsx
import { useContext } from 'react';
import { ThemeContext } from '../context/ThemeContext';

function Header() {
  const { theme, toggleTheme } = useContext(ThemeContext);

  return (
    <header className={theme}>
      <button onClick={toggleTheme}>Toggle Theme</button>
    </header>
  );
}
```

`useContext(ThemeContext)` reads the current `value` from the nearest `ThemeContext.Provider` above this component in the tree. When the `value` changes — because `toggleTheme` was called and `theme` state updated — every component consuming this context automatically re-renders.

A best practice I strongly recommend: wrap `useContext` in a custom hook:

```jsx
// In ThemeContext.js — add this
export function useTheme() {
  const context = useContext(ThemeContext);
  if (!context) {
    throw new Error('useTheme must be called within a ThemeProvider');
  }
  return context;
}

// In components
const { theme, toggleTheme } = useTheme();
```

This has two benefits. Your components import `useTheme` instead of both `useContext` and `ThemeContext` — cleaner imports. And the error check catches cases where someone uses the hook outside the provider tree, giving them a clear error message instead of a confusing undefined value.

---

## [36:00–39:00] When to Use Context vs Props

**[Slide 12 — Context vs Props Decision Guide]**

Context isn't always the right tool. Let me give you the decision framework.

Use props when the data flows directly from a parent to a child or at most two levels deep. Props are explicit and easy to trace — you can follow the data flow through your code. For the vast majority of component communication in React, props are correct.

Use context when data is needed by many components at different levels of the tree, or when it would need to pass through multiple intermediate components that don't use it themselves. Classic context candidates: current user, theme, language/locale preference, notification state.

One caution: context is not a general replacement for state management. If you have complex, frequently-updating global state — a shopping cart that many components read and modify, undo/redo history, real-time collaborative state — context alone can cause performance issues because every consumer re-renders when context value changes, even if the specific data they use didn't change. That's where Redux or Zustand come in — covered in Day 18a.

But for the cases context is designed for — relatively stable shared configuration like theme or current user — it's the right tool.

---

## [39:00–48:00] Custom Hooks

**[Slides 13, 14 & 15 — Custom Hooks, useLocalStorage, useToggle]**

Custom hooks are my favorite React feature. They're the mechanism that makes everything else composable.

The problem custom hooks solve: imagine you've written the logic to track the window width with a `useEffect` and a `useState`. You need that same logic in three different components. Without custom hooks, you copy and paste the code into all three. Now if you find a bug or want to improve it, you have to fix it in three places.

Custom hooks extract stateful logic into reusable functions. The only rule: the function name must start with `use`. That's it. A custom hook is just a regular JavaScript function that happens to call React hooks inside it. React's hook rules apply because you're calling hooks, but from the outside it looks like a function call.

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
```

Now any component can write `const width = useWindowWidth()` and get a reactive value that updates whenever the window resizes. One implementation. Zero duplication.

Let me show you `useLocalStorage` — a hook that replaces `useState` for values you want to persist to the browser:

```jsx
function useLocalStorage(key, initialValue) {
  const [storedValue, setStoredValue] = useState(() => {
    try {
      const item = window.localStorage.getItem(key);
      return item ? JSON.parse(item) : initialValue;
    } catch {
      return initialValue;
    }
  });

  const setValue = (value) => {
    try {
      const valueToStore = value instanceof Function ? value(storedValue) : value;
      setStoredValue(valueToStore);
      window.localStorage.setItem(key, JSON.stringify(valueToStore));
    } catch (error) {
      console.error(error);
    }
  };

  return [storedValue, setValue];
}
```

The API is identical to `useState` — it returns a `[value, setter]` pair. The difference: reading initializes from localStorage, and writing persists to localStorage. Drop-in replacement for `useState` anywhere you want persistence.

Usage:
```jsx
const [theme, setTheme] = useLocalStorage('theme', 'light');
```

That's it. Theme is now persisted across page refreshes.

Let me show two simpler custom hooks that demonstrate how much they simplify your components.

`useToggle` — every boolean state in your app:
```jsx
function useToggle(initialValue = false) {
  const [value, setValue] = useState(initialValue);
  return {
    value,
    toggle: () => setValue(prev => !prev),
    setTrue: () => setValue(true),
    setFalse: () => setValue(false),
  };
}

// Usage
const { value: isOpen, toggle, setFalse: close } = useToggle(false);
```

`useCounter` — reusable counter logic:
```jsx
function useCounter(initialValue = 0, step = 1) {
  const [count, setCount] = useState(initialValue);
  return {
    count,
    increment: () => setCount(prev => prev + step),
    decrement: () => setCount(prev => prev - step),
    reset: () => setCount(initialValue),
  };
}

// Usage
const { count, increment, decrement } = useCounter(1);
```

Notice what's happening here: the component becomes a thin layer that calls hooks and renders. All the logic lives in the hook. This separation makes components dramatically easier to read and test.

Each component that calls a custom hook gets its own isolated state. Calling `useToggle` in `Modal` and `useToggle` in `Dropdown` gives each component its own separate toggle state. They don't share. Same hook, independent instances.

In Day 19a we'll build `useFetch` — a hook that handles data fetching, loading states, and error handling. That one is worth waiting for the HTTP module because it needs real API endpoints to demonstrate properly.

---

## [48:00–54:00] Component Lifecycle in Functional Components

**[Slide 16 — Lifecycle Mapping]**

Let me close with the lifecycle mapping, because you'll encounter class component code in real-world projects and need to translate it.

`componentDidMount` — runs once after the component is first rendered and the DOM is ready. Hook equivalent: `useEffect(() => { }, [])` — empty dependency array.

`componentDidUpdate` — runs after every update when props or state change. Hook equivalent: `useEffect(() => { }, [specificDeps])` — the effect runs after mount and after each render where the listed values changed.

`componentWillUnmount` — runs just before the component is removed from the DOM. Hook equivalent: the cleanup function returned from `useEffect`.

These three lifecycle methods collapse into one hook with different arguments:

```jsx
// componentDidMount equivalent
useEffect(() => {
  // setup
}, []);

// componentDidUpdate equivalent
useEffect(() => {
  // respond to prop or state change
}, [value]);

// componentWillUnmount equivalent
useEffect(() => {
  return () => {
    // cleanup
  };
}, []);

// All three combined — setup, respond to changes, and cleanup
useEffect(() => {
  const subscription = subscribe(userId);
  return () => subscription.unsubscribe();
}, [userId]);
```

That last example does all three: sets up a subscription when the component mounts or `userId` changes, and tears down the previous subscription before setting up a new one or when the component unmounts.

The visual timeline:

```
Render (function body)
  ↓
DOM updated
  ↓
useEffect with [] → "componentDidMount"
  ↓
[State/props change]
  ↓
Re-render (function body runs again)
  ↓
DOM updated
  ↓
Previous effect cleanup → new effect runs → "componentDidUpdate"
  ↓
Component removed from DOM
  ↓
Effect cleanup runs → "componentWillUnmount"
```

Class components had four separate lifecycle methods with different signatures to memorize. Hooks replace them all with one mechanism — `useEffect` — and two parameters: the effect function and the dependency array. Once you understand how those two parameters interact, you understand the entire React lifecycle.

---

## [54:00–58:00] useState and useEffect: Common Gotchas

**[Reference — Important gotchas to know]**

Before the final summary, a few gotchas that catch people regularly.

**Stale state in async code:** If you call `setCount` inside an async callback like a `setTimeout` or a promise `.then()`, the `count` that async callback sees is the value from when the callback was created — it can be stale. Use functional updates: `setCount(prev => prev + 1)` instead of `setCount(count + 1)`.

**Infinite render loops with useEffect:** This code causes an infinite loop:
```jsx
useEffect(() => {
  setData(processData(data));  // ❌ changes data → triggers effect → changes data...
}, [data]);
```
If your effect's side effect changes a value that's in the dependency array, you'll loop. Restructure the logic so the effect doesn't produce the value it depends on.

**Objects and arrays in dependency arrays:** React compares dependencies with `Object.is` — essentially `===`. A new object created during render — `{}` or `[]` — is a new reference every render even if the contents are identical. If you pass an object or array as a dependency, the effect runs every render. Solutions: move the object outside the component, memoize it (Day 20a), or include only the specific primitive values you need.

**Updating state after unmount:** If an async operation completes after a component unmounts and tries to call `setState`, you'll get a warning. Use `useRef` with an `isMounted` flag, or use an AbortController in your fetch (Day 19a).

---

## [58:00–60:00] Day 17a Summary

**[Slide 17 — Day 17a Summary]**

Let me close Day 17a.

All five learning objectives complete.

State management with `useState`: value and setter from destructuring, functional updates for previous-dependent changes, compute derived values during render not in state.

Side effects with `useEffect`: three configurations of the dependency array, cleanup functions for memory leak prevention.

Controlled forms: `value` plus `onChange` for every input type, single handler using `name` attribute, `e.preventDefault()` on submit.

Context API: `createContext`, Provider with `value`, `useContext` in consumers, custom hook wrapper for clean consumption.

Custom hooks: functions starting with `use` that call other hooks, each component gets isolated state, encapsulate and reuse any stateful logic.

Looking ahead in the React track: Day 18a is React Router — client-side navigation so your application has multiple pages — and Redux for global state management at scale. Day 19a is React API and Testing — the `useFetch` custom hook gets fully built there, plus `useEffect` for real HTTP calls, error boundaries, and React Testing Library. Day 20a is performance and advanced patterns.

Great work today. The hooks you learned — `useState`, `useEffect`, `useRef`, `useContext` — cover ninety percent of what you'll write in real React applications. The rest is patterns built on top of these.

---

*[END OF PART 2 SCRIPT]*

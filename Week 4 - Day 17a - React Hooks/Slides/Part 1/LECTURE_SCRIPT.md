# Week 4 - Day 17a: React Hooks
## Part 1 Lecture Script — useState, useEffect & Event Handling

**Total runtime:** 60 minutes
**Delivery pace:** ~165 words/minute
**Format:** Verbatim instructor script with [MM:SS–MM:SS] timing markers

---

## [00:00–02:30] Opening

Welcome to Day 17a — React Hooks. This is the session where React starts to feel genuinely powerful. On Day 16a we built components that could receive data through props and display it as JSX. They were functional, but they were static. Today we add the two capabilities that make real applications possible: memory and side effects.

Memory means a component can remember a value between renders — a counter, a form input, a selected item, whether a modal is open. Without state, every render starts fresh and you can't build anything interactive.

Side effects mean a component can do things beyond just returning JSX — fetching data from an API, updating the document title, starting a timer, writing to localStorage. These are things that reach outside the component's own render cycle, and React has a specific mechanism for managing them.

Both of these capabilities come from hooks — functions whose names start with `use`. They were introduced in React 16.8 in early 2019, and they fundamentally changed how React applications are written. Today we cover the two most important ones: `useState` and `useEffect`. We also cover event handling in depth, which ties the two together.

In Part 2 this afternoon we continue with forms, `useRef`, `useContext`, and building your own custom hooks. Together, today gives you the complete toolkit for the vast majority of real React development.

Let's start.

---

## [02:30–06:00] Where We Left Off + Why Hooks?

**[Slide 2 — Where We Left Off]**

Quick bridge from Day 16a. We know how to write function components that accept props and return JSX. But there's a fundamental problem with plain function components:

Every time React calls your component function, it starts over. Local variables are re-initialized. There's no persistence between calls. This code doesn't work:

```jsx
function Counter() {
  let count = 0;  // reset to 0 every single render
  return <button onClick={() => count++}>{count}</button>;
}
```

Clicking that button does nothing visible. `count` increments but the next render immediately resets it to zero. We need a way to preserve a value across renders — and when that value changes, we need React to re-render and display the new value.

**[Slide 3 — Why Hooks?]**

Before hooks, state and lifecycle methods required class components — JavaScript classes with `this.setState()`, `componentDidMount`, `componentDidUpdate`, `componentWillUnmount`. They worked, but they had real problems. Complex components had related logic scattered across three or four lifecycle methods. Logic was hard to share between components without creating awkward wrapper patterns.

React hooks, released in February 2019, solved this. Function components can now have state. They can perform side effects. They can share stateful logic through custom hooks. Class components still work and aren't going away, but every new React codebase is written with function components and hooks. Every library, every framework, every tutorial you'll read going forward uses hooks.

Two rules before we write a single hook:

First: hooks must be called at the top level of your component. Not inside `if` statements, not inside `for` loops, not inside nested functions. Always at the top.

Second: hooks can only be called from React function components or from custom hooks. Not from plain JavaScript functions.

React enforces these rules through its `eslint-plugin-react-hooks`. If you break them, you'll get a linting error. Trust the linter — it's protecting you from subtle bugs.

---

## [06:00–12:00] useState: The Basics

**[Slide 4 — useState Basics]**

Let's write our first hook.

```jsx
import { useState } from 'react';

function Counter() {
  const [count, setCount] = useState(0);

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

Let me break down every part of this.

`useState(0)` — you call `useState` with the initial value. In this case, `0`. It returns an array of exactly two elements.

`const [count, setCount]` — we're using array destructuring to name those two elements. The first element is the current value of the state — right now it's `0`. The second is a setter function — a function we call to update the state. The naming convention is `value` and `setValue`. Here: `count` and `setCount`.

When you click the Increment button, `setCount(count + 1)` is called. What happens? React stores the new value — `1` — and schedules a re-render. The component function runs again. This time, `useState(0)` returns `1` as the current value instead of `0`. React sees the new JSX — `<p>Count: 1</p>` — and updates the DOM. The button click produced visible feedback.

This is React's contract with you. You call a setter, React re-renders. That's it.

The initial value `useState(0)` is only used on the first render. Every subsequent render, React ignores the initial value argument and returns the current stored value. So if `count` is `5` and you re-render for any reason, `count` comes back as `5` — not reset to `0`.

---

## [12:00–17:30] useState: Multiple Variables and Functional Updates

**[Slide 5 — Multiple State Variables]**

In a real component, you'll almost always have multiple pieces of state. There are two approaches.

The first — and usually better — approach is separate state variables:

```jsx
const [name, setName]         = useState('');
const [email, setEmail]       = useState('');
const [isActive, setIsActive] = useState(false);
```

Each variable is independent. Updating `name` doesn't trigger re-renders based on `email`. They're conceptually separate.

The second approach is an object:

```jsx
const [user, setUser] = useState({ name: '', email: '' });
```

If you go this route, there's a critical difference from class-based `setState` that trips people up constantly. When you call `setUser` with a new object, React completely replaces the state with that new object. It doesn't merge. So if you write `setUser({ name: 'Alice' })`, you've lost `email` entirely.

The correct pattern with object state is always spread the previous state first:

```jsx
setUser(prev => ({ ...prev, name: 'Alice' }));
```

The spread `...prev` copies all existing properties, and then `name: 'Alice'` overwrites just that one. The rest of the object is preserved. This pattern is important enough to memorize.

When should you use an object vs separate variables? Use a single object when the values always change together and represent a single conceptual entity — like a form with many fields that are always submitted as a unit. Use separate variables when the values change independently — like an `isLoading` flag and a `selectedItem` that have nothing to do with each other.

**[Slide 6 — Functional Updates]**

There's a subtlety to `useState` setters that causes bugs in certain situations.

When you write `setCount(count + 1)` and the component is inside a closure — which all functions in JavaScript are — the `count` that expression refers to is the `count` from when the event handler was created. If multiple updates happen quickly before React re-renders, they might all see the same stale value.

The solution is functional updates:

```jsx
setCount(prev => prev + 1);
```

Instead of passing a value, you pass a function. React calls that function with the most current state as the argument. You compute and return the new value. React guarantees `prev` is always the latest state, even if multiple updates are batched.

The rule is simple: when the new state value depends on the old state value, use the functional form. `setCount(prev => prev + 1)`. `setItems(prev => [...prev, newItem])`. `setIsOpen(prev => !prev)`. Any time you're building the new value from the old value, use the function form.

---

## [17:30–22:00] What NOT to Put in State

**[Slide 7 — What NOT to Put in State]**

One of the most common mistakes beginners make with useState is putting derived values in state alongside the source data.

```jsx
// ❌ Wrong
const [items, setItems] = useState([]);
const [itemCount, setItemCount] = useState(0);       // derived from items
const [totalPrice, setTotalPrice] = useState(0);     // derived from items
```

Every time you add or remove an item, you have to remember to update `items`, `itemCount`, AND `totalPrice`. If you forget one, your UI is showing stale data. This is a bug factory.

```jsx
// ✅ Right — compute during render
const [items, setItems] = useState([]);

const itemCount = items.length;
const totalPrice = items.reduce((sum, item) => sum + item.price, 0);
```

`items.length` and `items.reduce(...)` are computed fresh on every render from the source of truth — the `items` array. They're never out of sync because they're always derived in the same moment. No synchronization bugs possible.

The rule is: if you can compute a value from other state or props, do it during render — not in state. State should hold the data you can't derive from anywhere else — the things you'd lose if you refreshed the page and started over.

---

## [22:00–28:30] useEffect: What and Why

**[Slide 8 — useEffect Basics]**

Now let's talk about side effects.

A side effect is anything your component does that reaches outside its own render cycle. Fetching data from an API. Setting the document title. Starting a timer. Adding an event listener to the window. Writing to localStorage. These are side effects.

The problem with side effects in a React component is that your component function runs every time React re-renders it. If you put a data fetch directly in your function body, it fires on every single render. If you put `document.title = title` in your function body, that's fine — document title is cheap to set. But an API call on every render would make thousands of network requests. You need control over when effects run.

`useEffect` gives you that control. The basic syntax:

```jsx
import { useEffect } from 'react';

function DocumentTitleUpdater({ title }) {
  useEffect(() => {
    document.title = title;
  });

  return <h1>{title}</h1>;
}
```

The function you pass to `useEffect` runs after React has finished updating the DOM. Not during render. After. The sequence is: render JSX, React updates the DOM, useEffect runs. Think of it as React saying: "I've finished drawing the screen — now you can do your side effects."

This example has no second argument to `useEffect`, so it runs after every single render. We can be more specific with the dependency array.

---

## [28:30–36:00] useEffect: The Dependency Array and Cleanup

**[Slides 9 & 10 — Dependency Array and Cleanup]**

The second argument to `useEffect` is the dependency array — a list of values that controls when the effect runs.

Three cases to know cold.

Case one: no dependency array at all. The effect runs after every render. This is rarely what you want — it's almost always too expensive. The only common use case is something genuinely cheap that should reflect every state change, like an analytics tracker.

Case two: empty dependency array — `[]`. The effect runs exactly once, after the first render. This is equivalent to `componentDidMount` from class components. Initial data fetch? One-time event listener setup? This is your pattern.

```jsx
useEffect(() => {
  fetchInitialData();
}, []);  // ← "Run once after mount"
```

Case three: with dependencies — `[userId]`, `[searchTerm, page]`, etc. The effect runs after mount, and again whenever any value in the array changes. When `userId` changes — because the user navigated to a different profile — the effect fires again and fetches the new user's data.

```jsx
useEffect(() => {
  fetchUserById(userId);
}, [userId]);  // ← "Run after mount AND when userId changes"
```

Now cleanup. Some effects need to undo what they did when the component is removed, or before the effect runs again. A setInterval keeps ticking even after the component that started it is gone from the page. An event listener on `window` keeps firing. These are memory leaks.

The cleanup pattern: return a function from your effect:

```jsx
useEffect(() => {
  const intervalId = setInterval(() => {
    setSeconds(prev => prev + 1);
  }, 1000);

  return () => {
    clearInterval(intervalId);  // ← cleanup
  };
}, []);
```

The returned function runs in two situations: just before the component is removed from the DOM, and just before the effect runs again if its dependencies changed. It always runs in that moment, cleaning up the previous effect before the next one starts.

Here's the sequence: component mounts → effect runs → component's deps change → cleanup of previous effect runs → effect runs again → component unmounts → cleanup runs one final time.

The cleanup is how you prevent memory leaks. If you start a timer, return a function that stops it. If you add an event listener, return a function that removes it. If you open a WebSocket, return a function that closes it.

---

## [36:00–40:00] useEffect: Patterns and the Dependency Rule

**[Slides 11 & 12 — Common Patterns and ESLint]**

Let me show you a few patterns you'll write constantly.

Document title update — one of the simplest effects:
```jsx
useEffect(() => {
  document.title = `(${unreadCount}) Messages`;
}, [unreadCount]);
```

Every time `unreadCount` changes, the browser tab title updates. No cleanup needed — setting a string is idempotent.

LocalStorage sync:
```jsx
useEffect(() => {
  localStorage.setItem('theme', theme);
}, [theme]);
```

Whenever `theme` changes, it's persisted to localStorage. Simple.

Window resize listener — needs cleanup:
```jsx
useEffect(() => {
  const handleResize = () => setWidth(window.innerWidth);
  window.addEventListener('resize', handleResize);
  return () => window.removeEventListener('resize', handleResize);
}, []);
```

Mount once, remove when component is destroyed.

Data fetching — I'll show you the shell pattern today. We'll add real API calls and error handling in Day 19a once we have something to fetch from.

Now, the dependency rule. React ships with an ESLint plugin called `eslint-plugin-react-hooks` that enforces a specific rule: every value you use inside an effect must be in the dependency array. If you use `userId` inside the effect but don't list it in the array, the ESLint plugin warns you.

Why does the rule exist? Because if you use a value inside an effect but don't declare it as a dependency, your effect never re-runs when that value changes. It sees the original value forever — a stale closure. This is one of the most common React bugs.

Trust the ESLint plugin. If it says add a dependency, add it. If you think you need to break the rule, you almost always need to restructure your code instead.

---

## [40:00–47:00] Event Handling in React

**[Slides 13 & 14 — Event Handling and Synthetic Events]**

Events are the bridge between state and user interaction. Let me go through the system completely.

First, the syntax difference from HTML. In plain HTML: `onclick="handleClick()"` — lowercase, a string. In React: `onClick={handleClick}` — camelCase, a function reference, no quotes.

Common events you'll use constantly:

`onClick` on buttons and any clickable element. `onChange` on input, select, textarea — fires every time the value changes. `onSubmit` on form elements. `onKeyDown`, `onKeyUp` on inputs for keyboard events. `onFocus` and `onBlur` for input focus tracking. `onMouseOver`, `onMouseOut` for hover effects.

For event handlers, you have three patterns:

Inline arrow functions for simple cases:
```jsx
<button onClick={() => setCount(count + 1)}>+</button>
```

Method reference for no-argument handlers:
```jsx
<button onClick={handleClick}>Click me</button>
// Notice: NOT onClick={handleClick()} — that would call it immediately
```

Arrow function that passes arguments:
```jsx
<button onClick={() => handleDelete(item.id)}>Delete</button>
```

The common mistake is writing `onClick={handleClick()}` with parentheses. Those parentheses call the function immediately during render and pass the return value to `onClick`. What you want is `onClick={handleClick}` — pass the function itself, and React calls it when the event fires.

Now, React's event system. React doesn't attach event listeners to each individual DOM element. It uses event delegation — a single listener at the root. When a click bubbles up, React identifies which component handler to call based on which element was clicked.

React wraps the native DOM event in a `SyntheticEvent` object. It has the same properties and methods as a native event — `target`, `currentTarget`, `type`, `preventDefault()`, `stopPropagation()` — but React normalizes them across browsers. Safari, Chrome, Firefox — you get the same API.

The properties you'll use most:
- `e.target.value` — the current value of an input
- `e.target.checked` — the boolean state of a checkbox
- `e.preventDefault()` — stop the browser's default behavior (form submission reloads the page without this)
- `e.key` — which key was pressed in keyboard events

---

## [47:00–53:00] Putting It Together: Complete Examples

**[Slides 15 & 16 — Counter with History and Auto-Saving Note]**

Let me show you two complete examples that combine what we've learned.

First, a counter that keeps a history of operations:

```jsx
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

      <h3>History</h3>
      <ul>
        {history.map((entry, i) => <li key={i}>{entry}</li>)}
      </ul>
    </div>
  );
}
```

Two independent state variables, functional updates on the history array, derived value `history.length` displayed in the heading — all the patterns from this session combined.

Second example — an auto-saving note editor that combines useState with useEffect and cleanup:

```jsx
function AutoSaveNote() {
  const [note, setNote] = useState(() => {
    return localStorage.getItem('saved-note') || '';
  });
  const [saveStatus, setSaveStatus] = useState('All saved');

  useEffect(() => {
    setSaveStatus('Saving...');

    const timer = setTimeout(() => {
      localStorage.setItem('saved-note', note);
      setSaveStatus('All saved');
    }, 1000);

    return () => clearTimeout(timer);
  }, [note]);

  return (
    <div>
      <div>{saveStatus}</div>
      <textarea
        value={note}
        onChange={e => setNote(e.target.value)}
        rows={10} cols={50}
        placeholder="Start typing — auto-saves after 1 second"
      />
    </div>
  );
}
```

A few things to notice. The `useState` initial value uses a function — `() => localStorage.getItem(...)`. When you pass a function to `useState`, React calls it once on the first render to get the initial value. This is called lazy initialization. Without it, `localStorage.getItem` would run on every render even though the result is only needed once.

The `useEffect` with `[note]` as the dependency fires every time `note` changes. It sets a one-second timeout — a debounce. The cleanup function cancels the pending timeout before the next effect runs. So rapid typing generates many effects, but the cleanup cancels each pending timer before it fires. The save only happens one second after the user stops typing. Debounce in six lines.

---

## [53:00–57:30] The Rules of Hooks

**[Reference — Rules Slide]**

Before we close Part 1, let me make the rules of hooks explicit because violating them causes strange, hard-to-debug behavior.

Rule one: only call hooks at the top level. Not inside `if` blocks, not inside loops, not inside nested functions. Always at the very top of your component, unconditionally.

Why? React keeps track of hooks by their call order. Every render must call the same hooks in the same order. If you conditionally call a hook, the order changes between renders and React loses track of which state belongs to which hook.

This means you can't write:
```jsx
if (userId) {
  const [user, setUser] = useState(null);  // ❌ conditional hook
}
```

You write:
```jsx
const [user, setUser] = useState(null);  // ✅ always called
// Then use the condition inside the effect or JSX
```

Rule two: only call hooks from React function components or custom hooks. Not from regular JavaScript functions, not from class components, not from outside a component.

These rules are enforced by `eslint-plugin-react-hooks`. If you're using Create React App, Vite with the React template, or Next.js — this ESLint plugin is already configured. Violations show up as errors in your editor. Pay attention to them.

---

## [57:30–60:00] Part 1 Summary

**[Slide 17 — Part 1 Summary]**

Let me close Part 1 with a quick review.

`useState` gives function components memory. `useState(initialValue)` returns `[currentValue, setter]`. Calling the setter with a new value triggers a re-render with that value. Use multiple state variables for independent pieces of state. With object state, always spread: `setObj(prev => ({ ...prev, key: value }))`. Use functional updates `prev =>` when the new value depends on the old. Don't store derived values in state — compute them during render.

`useEffect` manages side effects. The second argument — the dependency array — controls when the effect runs. No array means every render. Empty array means mount only. Array with values means mount plus when those values change. Return a cleanup function to prevent memory leaks. Trust the ESLint dependency rule.

Events in React use camelCase handlers — `onClick`, `onChange`, `onSubmit`. Pass function references, not function calls. `e.target.value` and `e.target.checked` read input values. `e.preventDefault()` prevents default browser behavior. Functional updates are your friend for event-driven state changes.

Take a short break and we'll come back for Part 2: controlled forms, `useRef`, `useContext`, and building your first custom hooks.

---

*[END OF PART 1 SCRIPT]*

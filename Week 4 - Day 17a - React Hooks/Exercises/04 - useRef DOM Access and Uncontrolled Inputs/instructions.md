# Exercise 04: useRef — DOM Access and Uncontrolled Inputs

## Learning Objectives
- Create a ref with `useRef` and attach it to a DOM element via the `ref` prop
- Read an uncontrolled input's value without React state
- Programmatically manipulate the DOM (focus, scroll) through a ref
- Use a ref as a persistent mutable container that **does not trigger re-renders**

---

## Background

`useRef` returns a mutable object `{ current: initialValue }` that persists across renders.

**Two primary use cases:**

| Use case | What `ref.current` holds |
|----------|--------------------------|
| DOM access | The actual DOM node (set automatically when `ref` prop is attached) |
| Persistent mutable value | Any value you want to keep across renders without causing a re-render |

```jsx
const inputRef = useRef(null);

// Access DOM node:
inputRef.current.focus();

// Read uncontrolled input value:
console.log(inputRef.current.value);
```

---

## Requirements

### Part A — `UncontrolledInput`

1. Create a text input that is **uncontrolled** (no `value` prop, no `onChange`).
2. Attach a ref to it with `const inputRef = useRef(null)` and `ref={inputRef}`.
3. Add a **"Read Value"** button. When clicked, it reads `inputRef.current.value` and stores it in a `useState` variable to display it below the input.

> Key insight: The input's value lives in the DOM, not in React state. React never re-renders when the user types.

### Part B — `FocusDemo`

1. Create a text input (can be uncontrolled).
2. Attach a ref to it.
3. Add a **"Focus Input"** button. When clicked, call `ref.current.focus()` to programmatically focus the input.

> Key insight: `useRef` lets you directly call DOM API methods like `.focus()`, `.scrollIntoView()`, `.select()`, etc.

### Part C — `RenderCounter`

1. Use a ref (not state) to count how many times the component has re-rendered.
2. Inside the component body (not inside `useEffect`), increment `renderCountRef.current` on every render.
3. Also add a piece of `useState` — a simple counter button — that causes deliberate re-renders.
4. Display the render count in the UI.

> Key insight: Updating `ref.current` does **not** trigger a re-render, making it ideal for tracking metadata like render counts, previous values, or timer IDs.

---

## Tips

- `useRef(null)` is the conventional initial value when the ref will point to a DOM element.
- You cannot call `ref.current.focus()` during render — it must be inside an event handler or `useEffect`.
- A ref used as a mutable container can be initialized with any value: `useRef(0)` for a counter.
- `ref.current++` works fine — modifying `.current` is safe and intentional.

# Exercise 02 — State & Events

## 🎯 Learning Objectives
By the end of this exercise you will be able to:
- Use the **`useState` hook** to add state to a functional component
- Write **event handler functions** and attach them to JSX elements
- Use the **prev-state updater pattern** `setState(prev => ...)` for safe state updates
- Manage **multiple state variables** in one component
- **Derive values** from state (compute instead of storing)
- Understand when to re-render and why

---

## 📋 What You're Building
An **Interactive Counter Dashboard** with multiple labelled counters. Each counter has its own increment, decrement, and reset controls. There's also a global "Reset All" button and a live total display. Plus a name input with a personalized greeting.

```
┌────────────────────────────────────────────────────────────┐
│  Hello, Alex! 👋                                           │
│  Your name: [Alex___________]                              │
│                                                            │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐                 │
│  │  ☕ Coffee │  │ 💧 Water  │  │ 🏃 Steps  │                 │
│  │    3     │  │    8     │  │    0     │                  │
│  │ [−][+][↺]│  │ [−][+][↺]│  │ [−][+][↺]│                 │
│  └──────────┘  └──────────┘  └──────────┘                 │
│                                                            │
│  Total across all counters: 11        [Reset All]          │
└────────────────────────────────────────────────────────────┘
```

---

## 🏗️ Project Setup
```bash
cd "Exercise-02-State-and-Events/starter-code"
npm install
npm run dev
```

---

## 📁 File Structure
```
src/
├── main.jsx
├── index.css
├── App.jsx                  ← holds ALL state; renders Counter components
├── App.css
└── components/
    ├── Counter.jsx          ← presentational: no own state, receives value + callbacks
    └── Counter.css
```

---

## ✅ TODOs

### `App.jsx`
- [ ] **TODO 1**: Import `useState` from `'react'`
- [ ] **TODO 2**: Declare a `counters` state — an array of objects: `[{ id, emoji, label, value }]`
  - Initial counters: ☕ Coffee (0), 💧 Water (0), 🏃 Steps (0)
- [ ] **TODO 3**: Declare a `name` state — a string, initialized to `'Student'`
- [ ] **TODO 4**: Implement `increment(id)` — increase the matching counter's value by 1
  - Use the **prev-state pattern**: `setCounters(prev => prev.map(...))`
- [ ] **TODO 5**: Implement `decrement(id)` — decrease by 1, **but don't go below 0**
- [ ] **TODO 6**: Implement `reset(id)` — set the matching counter's value back to 0
- [ ] **TODO 7**: Implement `resetAll()` — set ALL counters' values to 0
- [ ] **TODO 8**: Compute `total` from the counters array using `.reduce()` — **do NOT use a separate useState for this**
- [ ] **TODO 9**: Wire up the `name` input's `onChange` to update the name state
- [ ] **TODO 10**: Pass `onIncrement`, `onDecrement`, `onReset` callbacks to each `<Counter />`
- [ ] **TODO 11**: Display the computed `total` and the name greeting

### `components/Counter.jsx`
- [ ] **TODO 12**: Accept these props: `id`, `emoji`, `label`, `value`, `onIncrement`, `onDecrement`, `onReset`
- [ ] **TODO 13**: Apply CSS class `counter-value--zero` when `value === 0` (for visual styling)
- [ ] **TODO 14**: Wire up the three buttons: `onClick={() => onIncrement(id)}`, etc.

---

## 💡 Key Concepts

| Pattern | Example |
|---------|---------|
| useState | `const [count, setCount] = useState(0)` |
| Prev-state update | `setCount(prev => prev + 1)` |
| Updating array item | `arr.map(item => item.id === id ? { ...item, value: item.value + 1 } : item)` |
| Derived state | `const total = counters.reduce((sum, c) => sum + c.value, 0)` |
| Event handler | `<button onClick={handleClick}>Click</button>` |
| Callback with arg | `<button onClick={() => increment(id)}>+</button>` |

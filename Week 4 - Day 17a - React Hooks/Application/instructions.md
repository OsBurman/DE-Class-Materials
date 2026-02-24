# Day 17a Application — React Hooks: Expense Tracker

## Overview

You'll build an **Expense Tracker** — a React app where users can log expenses by category, see a running total, toggle a dark/light theme, and have their data persist across page refreshes via `localStorage`. Every hook from today is used.

---

## Learning Goals

- Manage state with `useState`
- Run side effects with `useEffect` and control dependencies
- Build controlled forms
- Use `useRef` to focus an input programmatically
- Share state across components with `useContext`
- Build a custom hook

---

## Prerequisites

- Node.js 18+ installed
- `cd starter-code && npm install && npm run dev`

---

## Project Structure

```
starter-code/
├── package.json
├── vite.config.js
├── index.html
└── src/
    ├── main.jsx
    ├── App.jsx              ← TODO
    ├── App.css              ← provided
    ├── context/
    │   └── ThemeContext.jsx  ← TODO: create context + provider
    ├── hooks/
    │   └── useLocalStorage.js ← TODO: custom hook
    └── components/
        ├── ExpenseForm.jsx   ← TODO
        ├── ExpenseList.jsx   ← TODO
        └── ExpenseSummary.jsx ← TODO
```

---

## Part 1 — Custom Hook: `useLocalStorage`

**Task 1**  
Build `useLocalStorage(key, initialValue)`:
- Use `useState` initialized from `localStorage.getItem(key)` (parse JSON, fall back to `initialValue`)
- Use `useEffect` to write to localStorage whenever the value changes
- Return `[value, setValue]`

---

## Part 2 — Theme Context: `ThemeContext.jsx`

**Task 2**  
- `createContext` with default `{ theme: 'light', toggleTheme: () => {} }`
- `ThemeProvider` component that uses `useState` for `theme` and provides `toggleTheme`
- Export `useTheme` custom hook: `() => useContext(ThemeContext)`

---

## Part 3 — `ExpenseForm` Component

**Task 3 — Controlled form**  
Fields: `description` (text), `amount` (number), `category` (select: Food, Transport, Entertainment, Other).  
Each field uses `useState` + controlled input (`value={...} onChange={...}`).

**Task 4 — `useRef` for focus**  
On mount (`useEffect` with `[]`), auto-focus the description input using `useRef`.  
After submitting, also focus back to the description input.

**Task 5 — Submit handler**  
Validate: description not empty, amount > 0. Call `onAddExpense(expense)` prop. Reset form.

---

## Part 4 — `ExpenseList` & `ExpenseSummary`

**Task 6 — `ExpenseList`**  
Receive `expenses` and `onDelete`. Render a list. If empty, show a message. Each item has a delete button calling `onDelete(id)`.

**Task 7 — `ExpenseSummary`**  
Receive `expenses`. Calculate and display:
- Total spent (sum of amounts)
- Breakdown by category (group + sum using `reduce`)
- Count of expenses

---

## Part 5 — `App.jsx`

**Task 8 — State + useLocalStorage**  
Use your `useLocalStorage` hook to persist the expenses array.

**Task 9 — useEffect for document title**  
`useEffect` that updates `document.title` to `"Expense Tracker — $[total]"` whenever the total changes.

**Task 10 — Theme toggle**  
Wrap app in `ThemeProvider`. Use `useTheme()` inside to read `theme`. Apply `className={theme}` to the root div. Add a button that calls `toggleTheme()`.

---

## Stretch Goals

1. Add a `useReducer` version of the expense state — replace `useState` with `useReducer`.
2. Add a `useMemo` to compute the category breakdown (preview for Day 20a).
3. Add an "undo last delete" feature using `useRef` to store the previous state.

---

## Submission Checklist

- [ ] `useState` used for all form fields
- [ ] `useEffect` used for localStorage sync and document title
- [ ] `useRef` used to auto-focus input
- [ ] `useContext` used via `useTheme` hook
- [ ] Custom `useLocalStorage` hook built and used
- [ ] Controlled form inputs (value + onChange)
- [ ] Data persists after page refresh
- [ ] Theme toggles between light and dark

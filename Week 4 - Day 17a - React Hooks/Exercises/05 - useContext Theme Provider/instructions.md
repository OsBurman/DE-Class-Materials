# Exercise 05: useContext — Theme Provider

## Learning Objectives
- Create a Context with `createContext`
- Provide context values to a subtree using a `<Context.Provider>`
- Consume context in any descendant component with `useContext`
- Understand why Context solves "prop drilling"

---

## Background

**Prop drilling** means passing data down through multiple component layers even when only a deeply nested component needs it. The Context API solves this by broadcasting a value to any descendant that opts in.

```jsx
// 1. Create the context (module level)
const ThemeContext = createContext('light');

// 2. Provide a value
<ThemeContext.Provider value="dark">
  <App />
</ThemeContext.Provider>

// 3. Consume anywhere in the tree
const theme = useContext(ThemeContext);
```

---

## Requirements

### Step 1 — Create the Context
At the **module level** (outside any component), create:
```jsx
const ThemeContext = createContext('light');  // 'light' is the default value
```

### Step 2 — `ThemeProvider` Component
Build a wrapper component:
1. Hold `theme` state (`'light'` or `'dark'`) using `useState`.
2. Provide a `toggleTheme` function that flips between the two values.
3. Return:
```jsx
<ThemeContext.Provider value={{ theme, toggleTheme }}>
  {children}
</ThemeContext.Provider>
```
> Pass **both** `theme` and `toggleTheme` in the context value object so consumers can read and change the theme.

### Step 3 — Consumer Components (no props needed!)
Build these three components. Each should call `useContext(ThemeContext)` to get `{ theme, toggleTheme }`.

| Component | Renders |
|-----------|---------|
| `ThemedButton` | A `<button>` that calls `toggleTheme` on click. Text should say "Switch to Dark Mode" or "Switch to Light Mode" based on current theme. |
| `ThemedCard` | A `<div>` with a heading "Themed Card" and a short paragraph. Apply inline styles for background/text color based on theme (light = white bg / dark text; dark = #333 bg / white text). |
| `ThemeDisplay` | A `<p>` that shows: `Current theme: light` or `Current theme: dark`. |

### Step 4 — `App` Component
Wrap everything in `<ThemeProvider>`:
```jsx
function App() {
  return (
    <ThemeProvider>
      <h1>Context Theme Demo</h1>
      <ThemeDisplay />
      <ThemedButton />
      <ThemedCard />
    </ThemeProvider>
  );
}
```

---

## Tips

- `createContext` and `useContext` must be destructured: `const { useState, useContext, createContext } = React;`
- The `children` prop is how React passes nested JSX to a wrapper component.
- Inline style in React uses camelCase: `style={{ backgroundColor: '#333', color: 'white' }}`.
- You do **not** need to pass any props to `ThemedButton`, `ThemedCard`, or `ThemeDisplay` — they read directly from context.

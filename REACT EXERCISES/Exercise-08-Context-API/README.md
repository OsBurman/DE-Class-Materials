# Exercise 08 — Context API

## Learning Objectives
By the end of this exercise you will be able to:
- Create a React Context with `createContext`
- Provide context values to a component tree using a `Provider`
- Consume context anywhere in the tree with `useContext`
- Manage multiple contexts in the same application
- Toggle themes using CSS custom properties driven by React state

## Overview
Build a **Themed Blog App** that uses two contexts:

1. **ThemeContext** — provides `theme` (`'light'` or `'dark'`) and a `toggleTheme` function. Drives CSS custom properties for the whole app.
2. **UserContext** — provides the current `user` object (name, avatar, role) and a `setUser` helper (simulates login/logout).

## Project Structure
```
Exercise-08-Context-API/
├── README.md
├── starter-code/
│   ├── index.html
│   ├── package.json
│   ├── vite.config.js
│   └── src/
│       ├── main.jsx
│       ├── index.css
│       ├── App.jsx
│       ├── App.css
│       ├── contexts/
│       │   ├── ThemeContext.jsx
│       │   └── UserContext.jsx
│       └── components/
│           ├── Header.jsx
│           ├── Sidebar.jsx
│           ├── BlogPost.jsx
│           └── BlogList.jsx
└── solution/  (same structure)
```

## Part A — ThemeContext

### Tasks
1. Create `ThemeContext` with `createContext`.
2. Build a `ThemeProvider` component that:
   - Holds `theme` state (`'light'` by default).
   - Provides `{ theme, toggleTheme }` to all children.
3. Export both the context and the provider.

### CSS variables approach
```css
[data-theme='light'] { --bg: #f8fafc; --surface: #ffffff; --text: #1e293b; --accent: #6366f1; --border: #e2e8f0; }
[data-theme='dark']  { --bg: #0f172a; --surface: #1e293b; --text: #f1f5f9; --accent: #818cf8; --border: #334155; }
```

Apply `data-theme={theme}` to the root `<div>` in App.

## Part B — UserContext

### Tasks
1. Create `UserContext` with `createContext`.
2. Build a `UserProvider` that holds `user` state (default: `null`).
3. Provide `{ user, setUser }` to children.
4. Export the context and provider.

## Part C — Consuming Context

### Header
- Use ThemeContext to display the toggle button and current theme name.
- Use UserContext to show "Log in" / "Log out" and the user's name.

### Sidebar
- Use UserContext to show/hide "Admin Panel" based on `user?.role === 'admin'`.
- Use ThemeContext to add an active class based on theme.

### BlogPost & BlogList
- BlogPost uses ThemeContext for the card's surface colour (all done via CSS variables).
- BlogList renders 3 sample posts passed as props.

## Getting Started
```bash
cd starter-code
npm install
npm run dev
```

## Key Concepts
| Concept | Usage |
|---------|-------|
| `createContext()` | Creates a context object |
| `<Context.Provider value={}>` | Wraps the tree that needs access |
| `useContext(MyContext)` | Reads the nearest Provider's value |
| CSS custom properties | Apply theme without prop-drilling |
| Multiple providers | Nest providers at the top of the tree |

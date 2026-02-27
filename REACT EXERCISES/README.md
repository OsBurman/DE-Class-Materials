# React Exercises — Full Curriculum

This folder contains **14 progressive React exercises** designed to take you from zero to production-ready React developer. Each exercise is a **self-contained React application** (using Vite) with starter code and a complete solution.

---

## 🗂️ Exercise Index

| # | Exercise | Key Concepts |
|---|----------|-------------|
| 01 | [JSX & Components](#) | JSX syntax, functional components, props, destructuring, composition |
| 02 | [State & Events](#) | `useState`, event handlers, prev-state pattern, multiple state variables |
| 03 | [Lists & Conditional Rendering](#) | `Array.map()`, `key` prop, `filter()`, ternary, `&&` operator, empty states |
| 04 | [Component Communication](#) | Lifting state up, callback props, sibling communication, prop drilling |
| 05 | [useEffect & Lifecycle](#) | `useEffect`, dependency array, cleanup functions, side effects |
| 06 | [Forms & Controlled Components](#) | Controlled inputs, form validation, select, radio, checkbox, textarea |
| 07 | [useRef & DOM Access](#) | `useRef`, DOM access, mutable refs, focus management, timers |
| 08 | [Context API](#) | `createContext`, `useContext`, `Provider`, avoiding prop drilling |
| 09 | [useReducer](#) | `useReducer`, action types, reducer functions, complex state |
| 10 | [Custom Hooks](#) | Writing custom hooks, `useLocalStorage`, `useFetch`, `useDebounce`, `useToggle` |
| 11 | [React Router](#) | `BrowserRouter`, `Routes`, `Route`, `Link`, `useParams`, `useNavigate`, protected routes |
| 12 | [Data Fetching & APIs](#) | `fetch`, async/await in `useEffect`, loading/error states, `AbortController` |
| 13 | [Performance Optimization](#) | `React.memo`, `useMemo`, `useCallback`, preventing unnecessary re-renders |
| 14 | [Full Application (Capstone)](#) | Task Manager combining all concepts: context, router, API, forms, hooks |

---

## 🚀 Getting Started

### Prerequisites
```bash
# Install Node.js (v18+) from https://nodejs.org
node --version   # should be 18+
npm --version
```

### How to Work on Each Exercise

1. **Navigate into the starter-code folder** for the exercise:
   ```bash
   cd "Exercise-01-JSX-and-Components/starter-code"
   ```

2. **Install dependencies:**
   ```bash
   npm install
   ```

3. **Start the development server:**
   ```bash
   npm run dev
   ```
   Open [http://localhost:5173](http://localhost:5173) in your browser.

4. **Read the `README.md`** inside the exercise folder — it lists every TODO you need to complete.

5. **Open the `src/` folder** and start completing the TODOs in order.

6. **Check your work against the solution:**
   ```bash
   cd ../solution
   npm install
   npm run dev
   ```

---

## 📚 Learning Path

```
JSX & Components → State & Events → Lists → Component Communication
       ↓
useEffect → Forms → useRef
       ↓
Context API → useReducer → Custom Hooks
       ↓
React Router → Data Fetching → Performance
       ↓
   Full Application (Capstone)
```

---

## 🛠️ Tech Stack Used
- **React 18** — UI library
- **Vite** — Build tool & dev server
- **React Router DOM v6** — Client-side routing (Exercises 11, 14)
- **CSS** — Plain CSS for styling (no frameworks, so you focus on React)

---

## 💡 Tips for Students
- Always read the **entire README** before writing any code
- Complete TODOs **in order** — later ones often depend on earlier ones
- Use **React DevTools** (browser extension) to inspect component trees and state
- If you're stuck, run the `solution` to see the expected result, then go back to starter code
- Every `// TODO` comment tells you exactly what to implement

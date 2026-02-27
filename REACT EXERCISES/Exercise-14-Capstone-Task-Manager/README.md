# Exercise 14 — Capstone: Full-Stack Task Manager

## Overview

This capstone exercise brings together **every major React concept** from Exercises 01–13 into a single production-quality application: a full-featured Task Manager SPA.

## Learning Objectives

This exercise consolidates:

| Concept | Source Exercise |
|---|---|
| JSX & component composition | Ex 01 |
| `useState` + events | Ex 02 |
| Lists, `.map()`, `.filter()`, conditional rendering | Ex 03 |
| Lifting state / callback props | Ex 04 |
| `useEffect` & lifecycle | Ex 05 |
| Forms & controlled inputs + validation | Ex 06 |
| `useRef` for focus management | Ex 07 |
| Context API (Auth + Theme) | Ex 08 |
| `useReducer` for complex state | Ex 09 |
| Custom hooks | Ex 10 |
| React Router v6 (nested routes, protected routes) | Ex 11 |
| Data fetching / async patterns | Ex 12 |
| `React.memo`, `useMemo`, `useCallback` | Ex 13 |

## Application Description

A fully-featured task management SPA with:

- **Authentication** — demo login via `AuthContext`; protected routes redirect to `/login`
- **Theme** — light/dark toggle via `ThemeContext`; persisted to `localStorage`
- **Dashboard** — statistics cards (total, by status, overdue)
- **Task List** — filterable/searchable table of tasks; status badges; priority indicators
- **Task Detail** — view a single task's full details
- **New Task / Edit Task** — validated form with title, description, priority, due date, status
- **Profile** — logged-in user info
- **404 page** — for unmatched routes

## File Structure

```
Exercise-14-Capstone-Task-Manager/
├── README.md
├── starter-code/
│   ├── package.json
│   ├── vite.config.js
│   ├── index.html
│   └── src/
│       ├── main.jsx
│       ├── index.css
│       ├── App.jsx
│       ├── App.css
│       ├── contexts/
│       │   ├── AuthContext.jsx
│       │   └── ThemeContext.jsx
│       ├── reducers/
│       │   └── taskReducer.js
│       ├── hooks/
│       │   └── useLocalStorage.js
│       ├── utils/
│       │   └── dateUtils.js
│       ├── data/
│       │   └── initialTasks.js
│       └── components/
│           ├── Layout.jsx
│           ├── Navbar.jsx
│           ├── ProtectedRoute.jsx
│           ├── TaskCard.jsx
│           ├── TaskForm.jsx
│           ├── FilterBar.jsx
│           └── LoadingSpinner.jsx
│       └── pages/
│           ├── DashboardPage.jsx
│           ├── TaskListPage.jsx
│           ├── TaskDetailPage.jsx
│           ├── NewTaskPage.jsx
│           ├── EditTaskPage.jsx
│           ├── LoginPage.jsx
│           ├── ProfilePage.jsx
│           └── NotFoundPage.jsx
└── solution/
    └── (same structure — complete implementation)
```

## Key Architecture

```
App
 └── AuthProvider
      └── ThemeProvider
           └── TaskProvider  (useReducer)
                └── BrowserRouter
                     └── Routes
                          └── Layout (Navbar + Outlet)
                               ├── / → DashboardPage
                               ├── /tasks → TaskListPage
                               ├── /tasks/new → NewTaskPage (protected)
                               ├── /tasks/:id → TaskDetailPage (protected)
                               ├── /tasks/:id/edit → EditTaskPage (protected)
                               ├── /login → LoginPage
                               ├── /profile → ProfilePage (protected)
                               └── * → NotFoundPage
```

## TODOs (starter-code)

1. **TODO 1** (`AuthContext.jsx`) — implement `login(name)` and `logout()` functions; store user in state
2. **TODO 2** (`ThemeContext.jsx`) — implement `toggleTheme()`; persist theme in `useLocalStorage`
3. **TODO 3** (`taskReducer.js`) — implement all 5 action cases: `ADD_TASK`, `UPDATE_TASK`, `DELETE_TASK`, `SET_STATUS`, `SET_FILTER`
4. **TODO 4** (`App.jsx`) — set up all routes, wrapping auth-required routes in `<ProtectedRoute>`
5. **TODO 5** (`TaskForm.jsx`) — implement controlled form with validation (title required, due date required)
6. **TODO 6** (`DashboardPage.jsx`) — compute and render stats using `useMemo`
7. **TODO 7** (`TaskListPage.jsx`) — filter tasks by status and search term using `useMemo`
8. **TODO 8** (`TaskDetailPage.jsx`) — use `useParams` to find and display a task
9. **TODO 9** (`EditTaskPage.jsx`) — use `useParams`, pre-populate form, dispatch `UPDATE_TASK`
10. **TODO 10** (`LoginPage.jsx`) — use `useNavigate` to redirect after login

## Running the App

```bash
cd starter-code   # or solution
npm install
npm run dev
```

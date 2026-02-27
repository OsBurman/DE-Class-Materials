# Exercise 11 — React Router

## Learning Objectives
By the end of this exercise you will be able to:
- Set up `BrowserRouter` and define routes with `<Routes>` and `<Route>`
- Navigate with `<Link>` and `<NavLink>` (active-link styling)
- Access URL parameters with `useParams`
- Navigate programmatically with `useNavigate`
- Create a protected route that redirects unauthenticated users
- Handle 404s with a wildcard route

## Overview
Build a **Bookstore SPA** with the following pages:

| Path | Page | Notes |
|------|------|-------|
| `/` | Home | Welcome / hero section |
| `/books` | Books | Grid of all books |
| `/books/:id` | BookDetail | Individual book page |
| `/cart` | Cart | Shopping cart |
| `/login` | Login | Fake login form |
| `/profile` | Profile | **Protected** — redirect to `/login` if not logged in |
| `*` | NotFound | 404 page |

## Data
A `data/books.js` file exports `BOOKS` — an array of 8 book objects:
```js
{ id, title, author, price, genre, description, cover (emoji) }
```

## Components
- **Layout** — wraps all pages with `<Navbar>` + `<main>`
- **Navbar** — uses `<NavLink>` for active styles, shows cart count badge, login/logout
- **ProtectedRoute** — wraps the profile route; redirects to `/login` if no user

## State
A simple `user` state in `App.jsx` (`null` or `{ name }`) and a `cart` state (array of book ids). Both are passed down via props.

## Tasks

### Part A — Router Setup
1. Wrap everything in `<BrowserRouter>` in `main.jsx`.
2. In `App.jsx` define all routes using `<Routes>` and `<Route>`.
3. Use `<Route element={<Layout .../>}>` as a parent route for all pages.

### Part B — Navbar
1. Use `<NavLink>` to add `className="active"` automatically on the active link.
2. Show a cart badge: `{cart.length > 0 && <span className="badge">{cart.length}</span>}`.
3. Show user name and a Logout button when logged in; a Login link when not.

### Part C — Books & BookDetail
1. `BooksPage` renders a grid of `<BookCard>` components.
2. `BookDetailPage` calls `useParams()` to get `id`, finds the book, and renders details.
3. Add an "Add to Cart" button on BookDetail that calls `onAddToCart(book.id)`.

### Part D — Login
1. A simple controlled form — on submit set a fake user object and navigate to `/profile`.

### Part E — ProtectedRoute
1. If `user` is null, `return <Navigate to="/login" replace />`.
2. Otherwise `return children`.

## Project Structure
```
Exercise-11-React-Router/
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
│       ├── data/
│       │   └── books.js
│       ├── components/
│       │   ├── Layout.jsx
│       │   ├── Navbar.jsx
│       │   └── ProtectedRoute.jsx
│       └── pages/
│           ├── HomePage.jsx
│           ├── BooksPage.jsx
│           ├── BookDetailPage.jsx
│           ├── CartPage.jsx
│           ├── LoginPage.jsx
│           ├── ProfilePage.jsx
│           └── NotFoundPage.jsx
└── solution/  (same structure)
```

## Getting Started
```bash
cd starter-code
npm install
npm run dev
```

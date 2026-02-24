# Week 4 - Day 18a: React Routing & Redux
## Part 1 Lecture Script

**Duration:** 60 minutes
**Pacing:** ~165 words/minute
**Topics:** React Router v6, navigation, route parameters, query strings, nested routes, HOCs

---

## [00:00–02:00] Opening

Good morning and welcome to Day 18a. We've spent the last two days building React components that know how to store state, react to side effects, and share data with hooks. But we've been building everything inside a single "page." Today that changes.

Part 1 is about React Router — how you build a real multi-page feel inside a React app without ever asking the browser to reload. Part 2 is Redux — how you manage global state at a scale that `useState` and `useContext` alone can't handle cleanly.

By the end of today, you'll be able to build a full navigation shell with multiple routes, protect certain pages behind authentication, and connect a shopping cart that any component in the app can read and update. Let's get started.

---

## [02:00–07:00] The Problem Router Solves — Bridge from Day 17a

Look at Slide 2. Think about where we left Day 17a. We have a component tree: App, Navbar, ProductList, ProductCard. We know how to share data with Context. We can display a list of products. But what happens when a user wants to go to `/products/42`? Right now, nothing. The URL doesn't mean anything to React. The browser would just try to make an HTTP request for that path and fail.

Here's the problem. Real web applications have URLs that reflect what the user is looking at. When you're on a product detail page, the URL says `/products/42`. If you copy that URL and paste it in a new tab, you expect to land on that product. If you hit the browser Back button, you expect to go to the products list. None of that works without routing.

React Router is the standard solution for client-side routing in React. It intercepts navigation events — link clicks, browser back and forward — and instead of letting the browser make an HTTP request, it tells React to render the right component for the current URL. The page never reloads. It's instant.

---

## [07:00–12:00] Client-Side vs Server-Side Routing

Slide 3 — let me explain why this is necessary, because it's not obvious.

In a traditional server-rendered website, every URL corresponds to a server endpoint. You click a link to `/products`, the browser makes a GET request to the server, the server sends back a complete HTML page, and the browser renders it. Full page reload every time. That was fine for 2005.

React is a Single Page Application — SPA. The browser loads one HTML file, one JavaScript bundle. After that initial load, JavaScript takes over. There's no server to send new HTML pages. So how do you have multiple "pages"?

React Router hooks into two browser APIs. The History API lets JavaScript update the URL bar without making a server request. And React Router intercepts anchor tag clicks to prevent the default browser navigation behavior. Put those together: user clicks a link, the URL changes, the browser history updates, and React Router renders a different component. Zero HTTP requests. Zero page reloads. Instant.

The user experience feels exactly like a traditional website. The browser Back button works. URLs are shareable. But under the hood it's all JavaScript swapping components in and out.

---

## [12:00–20:00] Setting Up React Router — BrowserRouter, Routes, Route

Slide 4. Install it: `npm install react-router-dom`. Note the `-dom` suffix — there's also `react-router-native` for React Native. We want the DOM version.

We're using v6. If you look at tutorials online, you'll find a lot of v5 material. The APIs changed significantly. The key differences to watch for: v5 uses `<Switch>`, v6 uses `<Routes>`. v5 uses `component={MyComponent}`, v6 uses `element={<MyComponent />}` with JSX. v5 uses `useHistory`, v6 uses `useNavigate`. When you're googling or asking an AI for help, always specify "React Router v6."

Slide 5 — the three core components. `BrowserRouter`, `Routes`, and `Route`.

`BrowserRouter` goes in `main.jsx` and wraps your entire app. It sets up the routing context — everything inside can use Router hooks. You only do this once.

Inside your app, wherever you want pages to render, you put a `<Routes>` block. `Routes` looks at the current URL and renders the first `<Route>` that matches. Think of it as a switch statement for URLs.

Each `<Route>` has two props: `path` (the URL pattern) and `element` (the JSX to render when matched). Look at the example: `<Route path="/products/:id" element={<ProductDetail />} />`. The colon in `:id` means "this segment is a variable." We'll extract that variable with a hook in a few minutes.

The `path="*"` at the bottom is the catch-all — it matches any URL that didn't match anything above it. This is how you implement a 404 page.

One important v6 change: exact matching is the default now. In v5, `/products` would match `/products/42` unless you added `exact`. In v6, `/products` only matches `/products` exactly. Much cleaner.

---

## [20:00–28:00] Link, NavLink, and useNavigate

Slide 6. Now that we have routes defined, we need ways to navigate between them.

`<Link to="/products">` renders as an anchor tag in the HTML but intercepts the click. Instead of following the href to a server, it tells React Router to match the URL `/products` and render the corresponding component. The page doesn't reload.

`<NavLink>` is `<Link>` plus automatic active state. It compares the `to` path against the current URL. When they match, it automatically applies the CSS class `active` to the element. You can also pass a function to `style` or `className` that receives `{ isActive }` — a boolean — and returns dynamic styles or class names. This is exactly what you want for navigation bars where the current page should be visually distinguished.

Never use a regular HTML `<a href="...">` inside a React Router app for internal navigation. That tells the browser to make a real HTTP request, which causes a full page reload and blows away all your React state. `<Link>` and `<NavLink>` for internal navigation; regular `<a>` is fine for external URLs.

Slide 7 — `useNavigate`. Sometimes you need to navigate in response to code rather than a user clicking a link. The most common example: after a form submits successfully, redirect the user to a confirmation page. Or after logout, redirect to the login page.

`const navigate = useNavigate()`. Then call `navigate('/some-path')`. That's it.

Two special behaviors: `navigate(-1)` goes back one page in browser history — equivalent to clicking the Back button. Very useful for "Back" buttons in UIs. And `navigate('/path', { replace: true })` replaces the current history entry instead of pushing a new one. Use this for login/logout flows. After logout, you want to redirect to `/login` and you don't want the user pressing Back to return to an authenticated page. Replace makes that impossible.

---

## [28:00–36:00] Route Parameters and Query Strings

Slide 8 — `useParams`. When you define `<Route path="/products/:id">`, the `:id` is a route parameter. It's a variable segment in the URL. `/products/42` matches, with `id` equal to `'42'`. `/products/laptop` also matches, with `id` equal to `'laptop'`.

In the component, call `useParams()`. It returns an object where the keys are your parameter names. So `const { id } = useParams()` gives you the string `'42'`.

Here's the most common gotcha: URL parameters are always strings. If your product IDs are numbers and you do `products.find(p => p.id === id)`, that's comparing a number to a string. In JavaScript, `42 === '42'` is false. The find will always return `undefined`. Add a `Number(id)` conversion immediately. Every time. Write it as a habit.

Slide 9 — `useSearchParams` for query strings. Route parameters are for resource identifiers — the specific thing you're looking at. Query strings are for filtering, sorting, pagination, search terms — optional parameters that modify how a resource is displayed.

`useSearchParams` returns exactly two things, just like `useState`: the current search params object, and a function to update it. To read: `searchParams.get('category')`. To write: `setSearchParams({ category: 'electronics', sort: 'price' })`. When you call `setSearchParams`, the URL updates, a new browser history entry is created, and the component re-renders with the new params.

This is a powerful pattern because your filters and sort state live in the URL. The user can bookmark a filtered view. They can copy the URL and share it. The browser Back button undoes filter changes. You get all that for free just by using `useSearchParams` instead of `useState` for filter values.

---

## [36:00–43:00] Nested Routes and Outlet

Slide 10 — nested routes. This is where React Router v6 really shines.

Look at the diagram. You have a `/dashboard` section of your app. Every page under `/dashboard` shares the same sidebar and top navigation. Without nested routes, you'd have to import and render that sidebar in every single dashboard page component. Dozens of components, all with the same boilerplate.

With nested routes, you define a parent route at `/dashboard` whose element is a `DashboardLayout` component. That layout contains the sidebar. Then inside the parent route, you nest child routes: `index` for the dashboard home, `profile`, `settings`. These children become nested `<Route>` elements inside the parent `<Route>`.

In the `DashboardLayout` component, you place an `<Outlet />`. That's a placeholder. When React Router matches `/dashboard/profile`, it renders `DashboardLayout` and inside the Outlet, it renders `Profile`. The layout stays mounted; only the Outlet's content changes.

This is the exact same concept as Angular's `<router-outlet>` from the Angular track. If students are following both tracks, point that out explicitly.

Slide 11 — index routes and 404. What renders when the user is at `/dashboard` exactly, not `/dashboard/profile`? Without an index route, the Outlet is empty. You'd have a sidebar with nothing in the main content area.

Add `<Route index element={<DashboardHome />} />` inside the parent route. The `index` prop makes it the default — it renders when the parent's URL matches exactly. No `path` prop needed on an index route.

For 404s, you can nest a `path="*"` inside a parent route to catch unrecognized sub-paths. So `/dashboard/something-weird` renders the not-found page inside the dashboard layout, keeping the sidebar. A top-level `path="*"` catches everything else.

---

## [43:00–52:00] Higher Order Components

Slide 12 — Higher Order Components. This is a pattern that predates hooks, but it's everywhere in existing codebases and some libraries.

The definition: an HOC is a function that takes a component and returns a new, enhanced component. The syntax is: function that receives a component as an argument, returns a function that renders the original component with some extra behavior.

Think of it as a wrapper. The classic use case is authentication: given any component, return a version of it that first checks if the user is logged in. If yes, render the original component. If no, redirect to the login page.

The code on the slide: `withAuth` is a function. It takes `WrappedComponent`. It returns an anonymous function that uses `useAuth`, checks for a user, and either redirects or renders the wrapped component. You apply it like this: `const ProtectedDashboard = withAuth(Dashboard)`. Now use `<ProtectedDashboard />` anywhere and it's automatically protected.

The `{...props}` spread is critical. It passes all props through to the wrapped component. Without this, any props you try to pass to `ProtectedDashboard` would never reach `Dashboard`. HOCs are transparent wrappers — the wrapped component should behave exactly as it would without the HOC, plus the added behavior.

Slide 13 — two more HOC examples. `withLogger` is a development tool — it logs every render with the component name and props. `withLoading` extracts the loading spinner concern. Instead of every component having `if (isLoading) return <Spinner />`, you wrap the component once.

The HOC vs hooks table is important context. Modern React code prefers hooks and wrapper components for most use cases. But HOCs are still valuable for: wrapping third-party components you don't control, applying behavior at the route level, and libraries like older versions of React Redux (which used `connect()`, an HOC).

---

## [52:00–58:00] Protected Routes — The Modern v6 Pattern

Slide 14 — the modern pattern for protected routes in React Router v6.

Instead of the `withAuth` HOC, we create a `ProtectedRoute` component. It uses `<Outlet />` — the same Outlet you use in layout routes. If `isAuthenticated` is true, it renders the Outlet (child routes pass through). If false, it renders `<Navigate to="/login" replace />`.

`<Navigate>` is a declarative redirect component — rendering it causes navigation. The `replace` prop means it replaces the history entry so the user can't press Back.

In `App.jsx`, you wrap groups of routes inside `<Route element={<ProtectedRoute isAuthenticated={!!user} />}>`. Notice: no `path` prop. This route has no URL of its own — it's just a behavior wrapper. Any child routes inside it are protected. You can protect twenty routes with one wrapper.

Slide 15 — `useLocation`. This is the finishing touch on the auth pattern. The `from` pattern: when a user tries to visit a protected route and gets redirected to login, save where they were trying to go. After they log in, send them there.

`useLocation()` returns the current URL as an object — `pathname`, `search`, `hash`, and `state`. In `ProtectedRoute`, you can pass the current location as navigation state to the redirect: `<Navigate to="/login" state={{ from: location }} replace />`. In the `Login` component, you read back `location.state?.from?.pathname` and after successful login, navigate there.

This "redirect to original destination after login" pattern is in almost every production React app. It's the difference between a frustrating login experience (always landing on the dashboard) and a smooth one (landing exactly where you were trying to go).

---

## [58:00–60:00] Part 1 Summary and Handoff

Slide 17 — the summary. Let's make sure we have everything.

React Router v6: `BrowserRouter` wraps the app, `Routes` and `Route` define your page map, `Link` and `NavLink` navigate without reloading, `useNavigate` handles programmatic redirects.

Route data: `useParams` for path segments (always convert from string), `useSearchParams` for query strings (bookmarkable filter state).

Layouts: nested routes with `<Outlet>` give you shared UI without repetition. Index routes fill the default child position. `<ProtectedRoute>` guards entire route subtrees.

HOCs: the pattern for wrapping components with cross-cutting behavior — auth, logging, loading states. Modern React uses custom hooks and wrapper components, but HOCs are everywhere in existing code.

Coming up in Part 2: our components can navigate between pages now, but they still don't have a good way to share state like a shopping cart across the whole app. That's where Redux comes in.

---

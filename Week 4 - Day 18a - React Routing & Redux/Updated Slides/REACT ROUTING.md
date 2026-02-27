# React Routing – One-Hour Lecture Script
### Topics: React Router & Navigation, Client-Side Routing, Route Parameters & Query Strings, Nested Routes & Layouts, Higher-Order Components (HOC)

---

## SLIDE 1: Title Slide
**Slide Content:** Title: "React Routing & Navigation" | Subtitle: "Client-Side Routing, Route Parameters, Nested Routes, and Higher-Order Components" | Your name and date.

**Script:**
"Welcome everyone. Today we're diving into one of the most essential skills for building real-world React applications — routing. By the end of this class you'll understand how React manages navigation between pages without ever reloading the browser, how to pass data through URLs, how to build nested page layouts, and how to use Higher-Order Components to protect and enhance your routes. Let's get into it."

---

## SLIDE 2: What is Client-Side Routing?
**Slide Content:** Two-column comparison — Traditional (Server-Side) Routing vs. Client-Side Routing. Key points: no full page reload, faster UX, single HTML file, JavaScript manages the URL.

**Script:**
"Before we talk about React Router specifically, let's understand the problem it solves. In a traditional multi-page website, every time you click a link the browser makes a full round-trip to the server — it requests a new HTML file, the page goes blank, then loads again. That's slow and jarring.

React apps are what we call Single-Page Applications, or SPAs. There is literally one HTML file — index.html — and React renders everything inside it dynamically. But users still expect to see the URL change when they navigate. They expect the back button to work. They expect to be able to share a link to a specific page.

Client-side routing is how we fake the experience of multiple pages inside a single HTML document. The JavaScript intercepts link clicks, updates the URL using the browser's History API, and re-renders the right components — all without touching the server. React Router is the library that does this for us."

---

## SLIDE 3: Installing and Setting Up React Router
**Slide Content:** Terminal command `npm install react-router-dom`. Code block showing `BrowserRouter` wrapping the `App` component in `main.jsx` or `index.jsx`.

```jsx
import { BrowserRouter } from 'react-router-dom';
import ReactDOM from 'react-dom/client';
import App from './App';

ReactDOM.createRoot(document.getElementById('root')).render(
  <BrowserRouter>
    <App />
  </BrowserRouter>
);
```

**Script:**
"To use React Router, we first install the package `react-router-dom` — notice the `-dom` suffix, that's the version made for web browsers as opposed to React Native. Run `npm install react-router-dom` in your project.

Once installed, the very first thing you do is wrap your entire application in a component called `BrowserRouter`. You'll typically do this in your `main.jsx` or `index.jsx` file, right where you render your root App component. This gives every component in your tree access to routing context. Think of BrowserRouter as the engine that listens to the URL and decides what to render. Everything else we do today lives inside this wrapper."

---

## SLIDE 4: Defining Routes with `<Routes>` and `<Route>`
**Slide Content:** Code block showing basic route definitions. Visual: URL bar examples next to component names.

```jsx
import { Routes, Route } from 'react-router-dom';
import Home from './pages/Home';
import About from './pages/About';
import Contact from './pages/Contact';

function App() {
  return (
    <Routes>
      <Route path="/" element={<Home />} />
      <Route path="/about" element={<About />} />
      <Route path="/contact" element={<Contact />} />
    </Routes>
  );
}
```

**Script:**
"Now let's define our routes. Inside your App component — or wherever you want routing to happen — you use two components: `Routes` and `Route`. Think of `Routes` as a switch statement — it looks at the current URL and renders only the first `Route` whose path matches. The `Route` component takes two key props: `path`, which is the URL segment to match, and `element`, which is the React component to render when that path is active.

So here, when someone visits the root URL `/`, they see the Home component. When they visit `/about`, they see the About component. React Router is checking the browser's URL bar against these paths and rendering accordingly — all without a page reload. This is the foundation of everything else we'll build today."

---

## SLIDE 5: Navigating with `<Link>` and `<NavLink>`
**Slide Content:** Code comparison between `<a href>` (bad) and `<Link to>` (good). Then `<NavLink>` with active styling example.

```jsx
// ❌ Don't do this — causes a full page reload
<a href="/about">About</a>

// ✅ Use Link for client-side navigation
import { Link, NavLink } from 'react-router-dom';

<Link to="/about">About</Link>

// NavLink adds an "active" class automatically
<NavLink to="/about" style={({ isActive }) => ({ color: isActive ? 'blue' : 'black' })}>
  About
</NavLink>
```

**Script:**
"Here's a critical rule: never use a plain HTML anchor tag — `<a href>` — to navigate between pages in a React app. Why? Because anchor tags trigger a full browser navigation, which blows away your entire React app state and defeats the whole purpose of client-side routing.

Instead, React Router gives us the `Link` component. It renders as an anchor tag in the DOM, so it's accessible and keyboard-navigable, but it intercepts the click and handles it with client-side routing instead of a server request. You swap `href` for `to`.

Then there's `NavLink`, which is a supercharged version of Link. NavLink knows whether its destination is the currently active route, and it automatically applies an `active` CSS class — or you can use a function like this to apply styles dynamically. This is perfect for navigation menus where you want to highlight the current page."

---

## SLIDE 6: Programmatic Navigation with `useNavigate`
**Slide Content:** Code block showing `useNavigate` hook usage. Use case: redirect after form submit or login.

```jsx
import { useNavigate } from 'react-router-dom';

function LoginForm() {
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    await loginUser();
    navigate('/dashboard'); // redirect after login
  };

  return <form onSubmit={handleSubmit}>...</form>;
}
```

**Script:**
"Sometimes you need to navigate programmatically — not because the user clicked a link, but because something happened in your code. The classic example is after a form submission or a successful login. You don't want a Link button for this; you want to trigger navigation inside a function.

The `useNavigate` hook gives you a `navigate` function you can call anywhere in your component logic. Pass it a path, and it navigates there. You can also pass `-1` to go back, like the browser's back button. This is essential for things like redirects after login, redirects after form submission, or navigating the user away from a page they shouldn't be on. We'll revisit this when we talk about protecting routes later."

---

## SLIDE 7: The 404 — Catch-All Routes
**Slide Content:** Code block with a wildcard `*` route. Visual of a 404 page.

```jsx
<Routes>
  <Route path="/" element={<Home />} />
  <Route path="/about" element={<About />} />
  <Route path="*" element={<NotFound />} /> {/* Catch-all */}
</Routes>
```

**Script:**
"What happens when someone visits a URL that doesn't match any of your routes? Without handling this, the user just sees a blank page — which is a terrible experience. The solution is a catch-all route using the wildcard path `*`. Place it last in your Routes list. React Router will only reach it if no other route matched. You render a friendly 404 or 'Not Found' component there. Always include this in real applications."

---

## SLIDE 8: Route Parameters
**Slide Content:** Diagram showing URL `/users/42` → `useParams()` → `{ id: "42" }`. Code block.

```jsx
// Route definition
<Route path="/users/:id" element={<UserProfile />} />

// Inside UserProfile component
import { useParams } from 'react-router-dom';

function UserProfile() {
  const { id } = useParams();
  // id will be "42" if URL is /users/42
  return <h1>User Profile: {id}</h1>;
}
```

**Script:**
"Now let's level up. Route parameters allow you to embed dynamic values directly in the URL path. You define a parameter in the route using a colon followed by the name — like `:id`. This is a placeholder that matches any value in that position. So the route `/users/:id` will match `/users/1`, `/users/42`, `/users/abc` — anything.

Inside the component that renders for that route, you use the `useParams` hook to read those values out. It returns an object where the keys match whatever you named after the colon. So `:id` becomes `{ id: '42' }`. Notice it comes back as a string — if you need a number, you'll need to convert it.

This is how virtually every real-world app works. Product pages, user profiles, blog posts, order details — they all use route parameters to know which specific item to display."

---

## SLIDE 9: Route Parameters in Practice — Fetching Data
**Slide Content:** Code block showing `useParams` + `useEffect` + `fetch` to load data based on the URL param.

```jsx
import { useParams } from 'react-router-dom';
import { useState, useEffect } from 'react';

function UserProfile() {
  const { id } = useParams();
  const [user, setUser] = useState(null);

  useEffect(() => {
    fetch(`https://api.example.com/users/${id}`)
      .then(res => res.json())
      .then(data => setUser(data));
  }, [id]); // re-fetch if id changes

  if (!user) return <p>Loading...</p>;
  return <h1>{user.name}</h1>;
}
```

**Script:**
"The most common use of route parameters is fetching data. As soon as the component mounts, you grab the `id` from the URL and use it in an API call. Notice the `useEffect` dependency array includes `id` — this is important! If the user navigates from `/users/1` to `/users/2`, the component stays mounted but the `id` changes. By including `id` in the dependency array, the effect re-runs and fetches the new user's data. If you forget this, you'll end up showing stale data from the previous user."

---

## SLIDE 10: Query Strings
**Slide Content:** Diagram showing URL `/products?category=shoes&sort=price` → `useSearchParams()` → values. Code block.

```jsx
import { useSearchParams } from 'react-router-dom';

function ProductList() {
  const [searchParams, setSearchParams] = useSearchParams();

  const category = searchParams.get('category'); // "shoes"
  const sort = searchParams.get('sort');          // "price"

  const updateFilter = (newCategory) => {
    setSearchParams({ category: newCategory, sort });
  };

  return <div>Showing: {category}, sorted by: {sort}</div>;
}
```

**Script:**
"Route parameters are great for identifying a specific resource, but sometimes you need to pass optional, configurable data through the URL — like filters, search terms, pagination, or sort options. That's what query strings are for. They appear after the `?` in the URL and look like key-value pairs separated by `&`.

React Router gives us the `useSearchParams` hook, which works similarly to `useState`. You get back `searchParams` — an object you can call `.get()` on to read values — and `setSearchParams`, a function to update them. When you call `setSearchParams`, it updates the URL query string without navigating away from the page.

Why put this in the URL instead of just in component state? Because it makes the state shareable. A user can copy the URL `/products?category=shoes&sort=price` and send it to a friend, and they'll see the exact same filtered view. It's also preserved through page refreshes. This is a key difference — component state disappears on refresh, URL state doesn't."

---

## SLIDE 11: Route Parameters vs. Query Strings — When to Use Which
**Slide Content:** Two-column comparison table. Route Params: required, identifies a resource. Query Strings: optional, filters/modifies the view.

| | Route Parameter | Query String |
|---|---|---|
| **Example** | `/users/42` | `/users?role=admin` |
| **Required?** | Yes | Optional |
| **Use for** | Identifying a specific resource | Filtering, sorting, searching |
| **Changes route?** | Yes | No |

**Script:**
"A quick rule of thumb to help you decide. Route parameters are for required pieces of data that identify a specific resource — a specific user, product, or post. Removing it would break the route. Query strings are for optional modifications to a view — filters, search terms, sort order, page number. The page still works without them, they just change what's shown. When in doubt: if the data identifies *what* you're looking at, use a route param. If it filters *how* you see it, use a query string."

---

## SLIDE 12: Nested Routes — The Concept
**Slide Content:** Visual tree diagram: App → Dashboard → (Overview, Settings, Reports). URL examples: `/dashboard`, `/dashboard/settings`.

**Script:**
"Now we get to one of the most powerful and widely-used features in React Router — nested routes. Most real applications have layouts that stay consistent while an inner area changes. Think of a dashboard: the sidebar and top navigation are always there, but the main content area shows different things — an overview, settings, reports, etc.

Nested routes let you model this in your route configuration directly. A parent route renders a layout component that includes permanent UI like a sidebar. Then child routes render their content inside that layout. The URL reflects this nesting: `/dashboard` shows the dashboard layout, `/dashboard/settings` shows the same layout with the Settings component in the content area. Let me show you how to build this."

---

## SLIDE 13: Nested Routes — Code
**Slide Content:** Full code example showing nested routes and `<Outlet />`.

```jsx
// Route config in App.jsx
<Routes>
  <Route path="/dashboard" element={<DashboardLayout />}>
    <Route index element={<Overview />} />
    <Route path="settings" element={<Settings />} />
    <Route path="reports" element={<Reports />} />
  </Route>
</Routes>

// DashboardLayout.jsx
import { Outlet, NavLink } from 'react-router-dom';

function DashboardLayout() {
  return (
    <div className="dashboard">
      <nav>
        <NavLink to="/dashboard">Overview</NavLink>
        <NavLink to="/dashboard/settings">Settings</NavLink>
        <NavLink to="/dashboard/reports">Reports</NavLink>
      </nav>
      <main>
        <Outlet /> {/* Child routes render here */}
      </main>
    </div>
  );
}
```

**Script:**
"Here's how it looks in code. In your route configuration, you nest `Route` components inside each other. The parent route has a path and renders the `DashboardLayout` component. The child routes have paths relative to the parent — so `settings` becomes `/dashboard/settings`. Notice the `index` route — that's what renders when you visit `/dashboard` with no sub-path.

The magic ingredient is the `Outlet` component inside `DashboardLayout`. This is a placeholder that says 'render whichever child route is currently active right here.' When the user is on `/dashboard/settings`, the DashboardLayout renders as usual, but the `Outlet` renders the Settings component. When they're on `/dashboard/reports`, the Outlet renders Reports instead. The layout stays, only the inner content swaps.

This pattern is incredibly common and incredibly useful. It's how you build consistent layouts — a persistent navigation bar, a sidebar, a header — with dynamic content areas."

---

## SLIDE 14: The `index` Route
**Slide Content:** Code snippet highlighting the `index` prop. Explanation of default child route.

```jsx
<Route path="/dashboard" element={<DashboardLayout />}>
  <Route index element={<Overview />} /> {/* Renders at /dashboard */}
  <Route path="settings" element={<Settings />} />
</Route>
```

**Script:**
"Let me take a moment to explain the `index` route specifically because it trips people up. When you visit `/dashboard` exactly — no sub-path — React Router needs to know what to put in the Outlet. That's what the `index` route is for. It has no `path` prop; instead it just has `index`. It says: 'When the parent route matches exactly, render me.' It's the default child. Without it, visiting `/dashboard` would show the layout with an empty Outlet."

---

## SLIDE 15: Higher-Order Components (HOC) — What Are They?
**Slide Content:** Definition. Visual: HOC as a wrapper function — Input component goes in, enhanced component comes out. Analogy: a decorator or wrapper.

```jsx
// HOC pattern
function withSomething(WrappedComponent) {
  return function EnhancedComponent(props) {
    // Add logic, data, or behavior
    return <WrappedComponent {...props} />;
  };
}
```

**Script:**
"Now let's talk about Higher-Order Components, or HOCs. This is a design pattern in React — not a built-in feature, but a convention that's been used widely in the React ecosystem. A Higher-Order Component is a function that takes a component as an argument and returns a new, enhanced component. The idea comes from functional programming — specifically higher-order functions, which are functions that take or return other functions.

The HOC wraps your component, adding new behavior, data, or logic around it. Your original component doesn't need to know it's being wrapped. This gives you a way to reuse logic across many components without repeating yourself. Let me show you the most important real-world use case for HOCs in routing."

---

## SLIDE 16: HOC Use Case — Protected Routes
**Slide Content:** Diagram: User tries to visit `/dashboard` → HOC checks auth → if logged in, show Dashboard; if not, redirect to `/login`. Code block.

```jsx
import { Navigate } from 'react-router-dom';

function withAuth(WrappedComponent) {
  return function ProtectedComponent(props) {
    const isAuthenticated = Boolean(localStorage.getItem('token'));

    if (!isAuthenticated) {
      return <Navigate to="/login" replace />;
    }

    return <WrappedComponent {...props} />;
  };
}

// Usage
const ProtectedDashboard = withAuth(Dashboard);

// In your routes:
<Route path="/dashboard" element={<ProtectedDashboard />} />
```

**Script:**
"The classic use case for HOCs in routing is protecting routes from unauthenticated users. You create a `withAuth` HOC. It takes any component, checks whether the user is logged in, and either renders the component or redirects to the login page using React Router's `Navigate` component.

Notice the `replace` prop on `Navigate` — this replaces the current entry in the browser history instead of pushing a new one. That way the user can't just hit the back button to get back to the protected page after being redirected.

You wrap any component you want to protect: `const ProtectedDashboard = withAuth(Dashboard)`. Now you use `ProtectedDashboard` in your routes instead of `Dashboard`. The route is protected. Any component can be protected this way by passing it through the HOC. One function, infinite reuse."

---

## SLIDE 17: HOC vs. Custom Hook — Knowing the Difference
**Slide Content:** Two-column comparison. HOC: wraps a component, returns a new component, used when behavior needs to wrap rendering. Custom Hook: used inside a component, returns values/functions, simpler for sharing logic.

**Script:**
"A common question at this stage is: when do I use a HOC versus a custom hook? Both let you reuse logic. Custom hooks, which you've learned about, are simpler and more modern. You use them inside a component to share stateful logic. HOCs are more powerful when you need to wrap the rendering itself — controlling whether a component renders at all, or injecting props from the outside.

For protected routes specifically, either pattern works. Some teams prefer a `ProtectedRoute` component approach, others prefer the HOC pattern. The HOC pattern is valuable to understand because you'll encounter it in older codebases and popular libraries. The concepts translate — understanding HOCs makes you a stronger React developer regardless of which pattern you personally favor."

---

## SLIDE 18: HOC — Passing Through Props Correctly
**Slide Content:** Code showing the spread operator `{...props}` to pass all props through. Common pitfall: forgetting to pass props.

```jsx
function withLogger(WrappedComponent) {
  return function LoggedComponent(props) {
    console.log('Rendering:', WrappedComponent.name, props);
    // ✅ Spread all props through so the wrapped component still works
    return <WrappedComponent {...props} />;
  };
}
```

**Script:**
"One critical rule when writing HOCs: always spread props through to the wrapped component. When someone uses your HOC-wrapped component and passes props to it, those props land on your outer function. If you forget to pass them down, the inner component never receives them and breaks silently.

The `{...props}` spread operator handles this. It takes everything the wrapper received and forwards it to the real component. This is a convention all HOCs should follow. Some HOC implementations also need to forward refs using `React.forwardRef`, but that's a deeper topic you'll encounter as you advance."

---

## SLIDE 19: Putting It All Together — A Full Example
**Slide Content:** Architecture diagram of a full app: BrowserRouter → App (Routes) → Public routes (Home, Login) → Protected routes (withAuth → Dashboard with nested routes → Outlet → Overview/Settings).

**Script:**
"Let's zoom out and see how all of today's concepts connect in a real application. At the root you have `BrowserRouter`. Your `App` component defines all the routes. Some routes are public — Home, Login — anyone can access them. For protected routes, you wrap the component with your `withAuth` HOC before passing it to the `element` prop. The protected component might itself contain a nested route structure, with a layout component and an `Outlet` for child routes. Route parameters flow through individual pages like user profiles. Query strings handle filters on list pages. Every piece has its role, and together they form a complete, navigable application."

---

## SLIDE 20: Common Gotchas and Best Practices
**Slide Content:** Bulleted list of gotchas and tips.

**Script:**
"Before we wrap up, let me quickly call out the most common mistakes I see.

First — don't use `<a href>` inside a React app for internal navigation. Always use `<Link>` or `<NavLink>`. This will bite you, usually in the form of mysterious state resets.

Second — remember that `useParams` returns strings. If you pass an id to an API that expects a number, convert it with `parseInt` or the unary `+` operator.

Third — include `id` or whatever param you're using in your `useEffect` dependency array. If you don't, your component will show stale data when the URL changes.

Fourth — always add a catch-all route with `path='*'` for 404 handling.

Fifth — don't nest routes unless you actually need a shared layout. Unnecessary nesting adds complexity without benefit.

And finally — when writing HOCs, always spread props through to the wrapped component, or you'll have mysterious bugs where props seem to disappear."

---

## SLIDE 21: Summary
**Slide Content:** Recap list of all topics covered with a one-line description of each.

- **Client-Side Routing** — React Router updates the URL without a page reload
- **BrowserRouter, Routes, Route** — The core setup components
- **Link & NavLink** — Navigation without full page reload
- **useNavigate** — Programmatic navigation
- **Route Parameters** — Dynamic URL segments accessed via `useParams()`
- **Query Strings** — Optional URL data accessed via `useSearchParams()`
- **Nested Routes & Outlet** — Shared layouts with dynamic content areas
- **Index Routes** — Default child route
- **Higher-Order Components** — Functions that wrap components to add behavior
- **Protected Routes with HOCs** — Auth-gating components using the HOC pattern

**Script:**
"Let's review what we covered today. We started with why client-side routing exists and how React Router provides it through `BrowserRouter`, `Routes`, and `Route`. We learned to navigate with `Link`, `NavLink`, and `useNavigate`. We covered route parameters for dynamic URLs and `useSearchParams` for query strings — and crucially, when to use each. We built nested routes with shared layouts using `Outlet`. And we finished with Higher-Order Components and their most important routing use case: protecting routes from unauthenticated users.

These are tools you will use in literally every non-trivial React application you build. In our next lessons we'll build on these foundations. For today, your assignment is to build a small multi-page app that uses at least two route parameters, a query string, one nested layout, and a protected route. See you next time."

---

## SLIDE 22: Q&A / Exercise Prompt
**Slide Content:** Exercise description and any discussion questions.

**Exercise:** Build a small app with the following routes:
- `/` — Home page (public)
- `/products` — Product list with `?category=` and `?sort=` query string filters
- `/products/:id` — Product detail page using route param
- `/dashboard` — Protected layout (redirect to `/login` if not authenticated) with nested routes: `/dashboard` (index), `/dashboard/settings`
- `/login` — Login page
- `*` — 404 page

---

*Total estimated delivery time: ~55–60 minutes including pauses and student questions.*
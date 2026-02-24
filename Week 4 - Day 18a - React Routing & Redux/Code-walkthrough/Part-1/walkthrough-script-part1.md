# Part 1 Walkthrough Script — Day 18a: React Routing & Redux
## React Router · Route Parameters · Nested Routes · Higher Order Components
### Estimated time: ~95 minutes

---

## Before You Start

**Recap from Day 17a (React Hooks):**

> "Yesterday we went deep on React Hooks — useState, useEffect, useContext, useReducer, and custom hooks. You now know how to manage local component state and share logic without class components.
>
> Today we're adding two big pieces on top of that: **React Router** — so our app can have multiple pages with real URLs — and then in Part 2, **Redux** — a battle-tested pattern for managing complex global state.
>
> Let's start with routing. Before I open the code, let me ask — who here has built a multi-page website before? How did you get from page to page? Right, anchor tags and full page reloads. React is a Single Page Application — there's only ONE HTML page. So how does it fake multiple pages? That's what React Router does."

---

## File: `01-react-router-and-navigation.jsx`

### [INTRO — ~3 min]

> "Open `01-react-router-and-navigation.jsx`. At the top of the file there's a big import block from `react-router-dom`. This is the library we install with `npm install react-router-dom`. Let's look at what each import does."

Walk through the imports slowly, naming each one:

> - **`BrowserRouter`** — "This wraps your whole app and plugs into the browser's History API. That's what lets the URL bar change without a page reload."
> - **`Routes`** — "A container for your route definitions. It looks at the current URL and renders the FIRST matching route."
> - **`Route`** — "Maps one URL path to one component. `path='/'` renders `HomePage`, `path='/courses'` renders `CourseListPage`."
> - **`Link`** — "Renders a plain `<a>` tag, but intercepts the click — no page reload. The React equivalent of `<a href>`."
> - **`NavLink`** — "Same as Link but adds an 'active' class or style when its `to` path matches the current URL. Perfect for navigation bars."
> - **`useNavigate`** — "A hook that returns a function you call to navigate programmatically — from inside event handlers or useEffect."
> - **`useParams`** — "A hook that reads the dynamic path segments — like `:id` in `/courses/:id`."
> - **`useSearchParams`** — "A hook for reading and writing URL query strings like `?category=frontend`."

---

### [SECTION 2 — NavBar with Link and NavLink — ~8 min]

> "Scroll to the `NavBar` function. This is how we build a navigation bar in React Router."

Point to the `navStyle` function:

> "NavLink's `className` or `style` prop accepts a function. React Router calls it with `{ isActive: true/false }` depending on whether that link's path matches the current URL. Here we make the text bold and blue when active. This is much cleaner than manually checking `window.location.pathname`."

Point to the `end` prop:

> "Watch out for this one. The Home link has `end` on it. Without `end`, the `/` route would ALWAYS be marked active — because EVERY path starts with '/'. The `end` prop says: only mark this active when the URL is EXACTLY '/' — nothing after it."

> "**Ask the class:** If I removed `end` from the Home NavLink, what would happen when I visit `/courses`? Right — both Home AND Courses would appear active. That's confusing for users."

Point to the plain `<Link>`:

> "For the Contact link I'm using plain `<Link>`. I don't need active styling there, so `Link` is fine. Use `NavLink` when active state matters, `Link` everywhere else."

---

### [SECTION 4 — Route parameters with useParams — ~10 min]

> "Now jump to `CourseDetailPage` — this is where we handle route parameters. The route for this page will be `/courses/:courseId`. The colon means 'this is a variable segment'. When someone visits `/courses/3`, React Router captures the `3` and makes it available via `useParams()`."

> "Look at this line: `const { courseId } = useParams();`  
> The key name `courseId` must match exactly what you wrote after the colon in the route definition. If your route says `:id`, you destructure `id`. If it says `:courseId`, you destructure `courseId`."

> "**Watch out:** Params are ALWAYS strings. We're looking up the course in an array using `===`, so we need `Number(courseId)` to convert the string to a number. Forgetting this is a super common bug — your find returns `undefined` because `'3' !== 3`."

Point to `useNavigate`:

> "We also use `useNavigate()` here. `navigate(-1)` is like clicking the browser's Back button — it goes one step back in the history stack. You can also pass a path string: `navigate('/courses')`. Or use the `replace: true` option to replace the current history entry instead of adding a new one — useful for login redirects so the user can't go 'back' to the login page."

---

### [SECTION 5 — Query strings with useSearchParams — ~12 min]

> "Now let's look at `CourseListPage`. This is where we tackle query strings — the `?category=frontend` part of a URL."

> "**Ask the class:** Why would you use query strings instead of just storing the filter in state? Think about it for a second."

> "Here are three good reasons:
> 1. Bookmarkable — the user can save or share `/courses?category=frontend` and come back to the same filtered view.
> 2. Refresh-safe — the filter survives a page reload.
> 3. Back-button works — they can hit Back and return to the previous filter."

Point to `useSearchParams`:

> "`useSearchParams()` returns a tuple just like `useState` — `[searchParams, setSearchParams]`. The `searchParams` object has a `.get(key)` method. If the param isn't in the URL, it returns `null` — we default to empty string."

Point to `handleFilter`:

> "When the user changes a filter, we don't just call `setSearchParams({ category: 'frontend' })` — that would WIPE the other params. Instead we copy the existing params into a new `URLSearchParams` object, then set or delete the one we want. This preserves all the other query params while updating just one."

> "**Watch out:** Calling `setSearchParams({ category: 'frontend' })` replaces ALL params. Always copy first: `const next = new URLSearchParams(searchParams)`."

---

### [SECTION 6 — Programmatic navigation — ~5 min]

> "Look at `LoginPage`. After a successful login we call `navigate('/', { replace: true })`. The `replace: true` option replaces the login page in the history stack. If we didn't use `replace`, the user could hit Back after logging in and land on the login page again. With `replace: true`, the login page is gone from history — which is exactly what you want."

---

### [SECTION 7 — 404 route — ~3 min]

> "The `NotFoundPage` is mapped to path `'*'`. The asterisk is a wildcard — it catches any URL that didn't match any earlier route. Always put it LAST in your `<Routes>`. React Router tries routes in order and renders the first match. If `'*'` came first, it would match EVERYTHING."

---

### [SECTION 8 — Route tree — ~5 min]

> "Scroll to the bottom — the `App` component. This is where we declare all the routes in one place. Read it like a map: URL on the left, component on the right. The `<BrowserRouter>` wraps everything. `<Routes>` tries each `<Route>` top to bottom and renders the first match.  
>
> Notice: `NavBar` is OUTSIDE `<Routes>`. That means it renders on every page — which is what we want for a persistent navigation bar."

> "**Transition:** Great — we can navigate around our app now. But what happens when we need routing to be more complex — like a dashboard with a sidebar that stays visible regardless of which sub-page you're on? That's nested routing. Let's look at File 2."

---

---

## File: `02-nested-routes-and-layouts.jsx`

### [INTRO — ~3 min]

> "Open `02-nested-routes-and-layouts.jsx`. We're building a dashboard — you know the type: sidebar on the left, content on the right, header at the top. The sidebar should stay visible whether you're on Overview, My Courses, or Settings. That's a shared layout — and nested routes are how we do it in React Router v6."

---

### [SECTION 1 — RootLayout and Outlet — ~10 min]

> "The `RootLayout` component has a header, a footer, and a `<main>` tag. Inside `<main>` there's one special element: `<Outlet />`."

> "**Ask the class:** What do you think `<Outlet>` does? Good guess — it's a placeholder. When React Router matches a child route, it renders that child's element RIGHT INSIDE `<Outlet>`. Without `<Outlet>`, your child routes would match in the URL bar but nothing would appear on screen. This is the #1 mistake with nested routes."

Draw on the board or narrate:

```
RootLayout
  ├── <header>   ← always visible
  ├── <Outlet>   ← child component renders here
  └── <footer>   ← always visible
```

---

### [SECTION 2 — DashboardLayout — ~8 min]

> "Now look at `DashboardLayout`. It has a sidebar with `NavLink`s and its OWN `<Outlet>`. So we have TWO levels of outlet. The route tree looks like this:"

```
/              → RootLayout (Outlet: main page)
  /            → HomePage (index)
  /dashboard   → DashboardLayout (Outlet: sub-page)
    /dashboard        → DashboardOverview (index)
    /dashboard/courses → DashboardCourses
    /dashboard/settings → DashboardSettings
```

> "When you visit `/dashboard/courses`:
> 1. React Router matches RootLayout — renders the header and footer, puts `DashboardLayout` in the RootLayout Outlet.
> 2. Then matches DashboardLayout — renders the sidebar, puts `DashboardCourses` in the DashboardLayout Outlet.
> 
> Two levels of Outlet, two levels of nesting. Clean."

Point to the NavLinks in the sidebar:

> "**Watch out:** The paths here are RELATIVE — no leading slash. `'courses'` resolves to `/dashboard/courses`. If you accidentally write `'/courses'` with a leading slash, it goes to the top-level `/courses` route, bypassing the dashboard entirely. Relative paths are a React Router v6 feature — always use them inside nested routes."

---

### [SECTION 3 — Index routes — ~5 min]

> "Look at `DashboardOverview`. In the route tree it uses the `index` prop instead of a `path`. An index route is the DEFAULT child — it renders when the parent path matches exactly with nothing extra. So `/dashboard` alone renders `DashboardOverview`, while `/dashboard/courses` renders `DashboardCourses`."

> "Think of it like `index.html` in a web server — when no specific file is requested, the server serves `index.html`. Same idea."

---

### [SECTION 4 — Deeply nested route — ~5 min]

> "We also have a route for `/dashboard/courses/:courseId`. This is THREE levels deep: RootLayout → DashboardLayout → EnrolledCourseDetail. The `useParams()` hook still works exactly the same regardless of nesting depth."

> "Notice the back link uses `'..'` — a relative path that goes UP one level, from `/dashboard/courses/2` to `/dashboard/courses`. Clean, maintainable, and doesn't hardcode the path."

> "**Transition:** Alright — we can route, we can nest, we can pass parameters. Last topic for Part 1: Higher Order Components. These have nothing to do with routing per se, but they're a powerful React pattern you'll see in the wild all the time."

---

---

## File: `03-higher-order-components.jsx`

### [INTRO — ~5 min]

> "Open `03-higher-order-components.jsx`. A Higher Order Component — HOC for short — is one of the oldest React patterns. It solves this problem: how do you share behaviour across many components without repeating code?"

> "Here's the definition in one sentence: A HOC is a function that takes a component and returns a NEW component with extra behaviour added."

Write on board:

```
const Enhanced = withSomeBehaviour(OriginalComponent);
```

> "The original component never changes. The HOC wraps it. Like adding a plugin."

---

### [SECTION 2 — withAuth HOC — ~12 min]

> "The most common HOC you'll encounter is `withAuth` — an authentication guard. Look at the code."

> "The function `withAuth` accepts `WrappedComponent` and an optional redirect path. Inside it defines a NEW function component called `WithAuth`. That component checks if there's a logged-in user. If not, it redirects to the login page. If yes, it renders `<WrappedComponent {...props} />`."

Point to `{...props}`:

> "This is critical. Every prop that was passed to `EnhancedDashboard` needs to reach `DashboardPage`. We spread ALL props through. If you forget `{...props}`, your wrapped component loses all its props — which breaks everything."

Point to `displayName`:

> "We also set `WithAuth.displayName`. Open React DevTools on any project using HOCs — without this, every HOC shows up as just 'WithAuth' or 'Component'. With displayName set to `WithAuth(Dashboard)`, you can immediately see which component is wrapped. Always do this."

> "**Ask the class:** Where should `ProtectedDashboard = withAuth(DashboardPage)` be defined — inside the component that uses it, or at the module level? Module level! If you define it inside a component's function body, React creates a NEW component type on every render, which tears down and remounts the DOM unnecessarily."

---

### [SECTION 4 — withLogging HOC — ~6 min]

> "The `withLogging` HOC is a classic example of a cross-cutting concern — behaviour you want across many components, like logging, analytics, or error tracking. Rather than adding console.log to every component, wrap it once."

> "This HOC uses `useEffect` to log on mount, unmount, and every render. Notice the pattern — we still spread `{...props}` through. The original component has no idea it's being logged."

---

### [SECTION 5 — withLoadingSpinner HOC — ~5 min]

> "The `withLoadingSpinner` HOC intercepts one specific prop — `isLoading` — and uses it to decide whether to show a spinner or the real component. The rest of the props are forwarded with `{...rest}`. This is the destructure-and-rest pattern: `{ isLoading, ...rest }` — pull out the prop the HOC needs, forward everything else."

---

### [SECTION 7 — Composing HOCs — ~5 min]

> "Look at `ProtectedAdminPage = withLogging(withRole(AdminPage, 'admin'))`. We're stacking two HOCs. Read it inside-out: first apply `withRole(AdminPage, 'admin')` — that creates an auth-checked version. Then wrap THAT with `withLogging`. The result gets both behaviours."

> "**Watch out:** Stacking too many HOCs creates what developers call 'wrapper hell' — your component tree in DevTools looks like: `WithLogging(WithRole(WithAuth(WithSpinner(AdminPage))))`. It becomes hard to trace which HOC is causing a bug. This is why modern React often prefers custom hooks for logic reuse."

---

### [SECTION 9 — HOC vs Custom Hooks — ~5 min]

> "Scroll to the table at the bottom. When should you use a HOC vs a custom hook?
>
> - Use a HOC when you need to **wrap with JSX** — like adding a spinner overlay, or redirecting before rendering. The HOC controls WHAT renders.
> - Use a custom hook when you need to share **logic and state** — like `useAuth()`, `useFetch()`, `useForm()`. The hook doesn't add DOM elements.
>
> In modern React codebases, custom hooks handle 80% of what HOCs used to do. But HOCs are still common in legacy codebases and certain libraries, so you need to understand both."

---

### [PART 1 WRAP-UP — ~3 min]

> "Alright — let's recap Part 1 before we break.
>
> - React Router gives us client-side routing: `BrowserRouter` wraps the app, `Routes` + `Route` map URLs to components.
> - `Link` prevents page reloads; `NavLink` adds active styling.
> - `useNavigate` for programmatic navigation; `useParams` for path params; `useSearchParams` for query strings.
> - Nested routes share layouts via `<Outlet>` — a slot where child routes render.
> - Index routes are the default child — use `index` prop instead of `path`.
> - HOCs are functions that wrap components to add behaviour. Always forward props and set displayName. Modern code prefers custom hooks for logic sharing.
>
> Take 10 minutes. When you come back we're doing Redux — global state management."

---

## Q&A Prompts for Part 1

1. "What's the difference between `<Link>` and `<NavLink>`?"
2. "If I define `ProtectedDashboard = withAuth(Dashboard)` inside a component's render, what goes wrong?"
3. "What does `<Outlet>` do and where do you put it?"
4. "Why would you store filter values in query params instead of useState?"
5. "What does `navigate(-1)` do?"

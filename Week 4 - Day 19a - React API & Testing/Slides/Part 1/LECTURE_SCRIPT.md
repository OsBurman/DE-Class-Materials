# Day 19a — Part 1: React API Integration
## Lecture Script

**Delivery time:** ~60 minutes
**Pace:** ~165 words/minute
**Format:** Verbatim instructor script with timing markers

---

`[00:00–02:30]`

Good morning everyone. So yesterday — Day 18a — you built a complete Redux-powered shopping cart. You had routing, you had a store, you had `useSelector` and `useDispatch` wired up. And if you looked at the cart after you refreshed the page, what happened? It was empty. Because all that state was hardcoded or typed in manually — none of it came from a server.

That's the gap we're closing today. Day 19a is all about making your React app actually talk to the outside world — fetching data from APIs, handling the fact that network calls take time and sometimes fail, and then in Part 2, making sure all of that works correctly with automated tests.

Here's our agenda for Part 1: we're starting with the two tools you'll use to make HTTP requests — the browser's built-in `fetch` and the popular library `axios`. Then we're going deep on what I call the three-state pattern — `loading`, `data`, and `error` — which is the backbone of every data-fetching component. After that, custom hooks for fetching, mutations like POST and PUT, the full `createAsyncThunk` implementation we previewed on Day 18a, error handling, loading UI patterns, Error Boundaries, and environment variables. Let's go.

---

`[02:30–08:00]`

**Slide 3 — The data flow problem.**

Before we write a line of fetch code, let me ask: where should data in a React app come from? You've seen two answers so far. One: you hardcode it. Two: you initialize it in a Redux slice with some fake data. Both of those are fine for learning, but in a real application, data lives on a server — in a database — and your React app is a client that requests that data over HTTP.

So the flow looks like this. Your component mounts. It triggers a fetch — an HTTP GET request. That request goes over the network to an API. The API queries a database, builds a JSON response, and sends it back. Your component receives that JSON, stores it in state, and renders it. Simple concept, but there are a lot of places it can go wrong.

Now, HTTP requests use different verbs depending on what you're doing. GET is for reading — it doesn't change anything on the server. POST is for creating — you're sending a new resource. PUT and PATCH are for updating — PUT replaces the entire resource, PATCH updates part of it. DELETE is for removing. You're going to use all of these today. GET on mount to load data, POST or PUT when a user submits a form, DELETE when a user removes an item.

The big idea I want you to keep in your head through all of this: network calls are *asynchronous*. You do not get data back immediately. The request goes out, time passes, the response comes back. Your component needs to handle all three moments: the waiting, the success, and the failure.

---

`[08:00–18:00]`

**Slide 4 — The Fetch API.**

The `fetch` function is built into every modern browser. You don't install it, you don't import it — it's just there. Let me walk through the most important things about it.

The basic usage: `const response = await fetch('/api/products')`. That gives you back a Response object. Then: `const data = await response.json()`. That gives you the parsed JSON. Two await calls, two steps. That's the minimum.

But there's something about `fetch` that trips up everyone the first time: fetch only throws — only rejects its promise — on network errors. Things like no internet connection, DNS failure, the server is completely unreachable. If the server responds with a 404 or a 500, `fetch` considers that a *successful* request — it got a response — and it does not throw. The promise resolves.

This is the opposite of what most people expect. So how do you handle a 404 or 500? You check `response.ok`. That property is `true` if the status code is 200 through 299, and `false` for everything else. Your job is to check it and throw manually if it's false. Like this: `if (!response.ok) throw new Error('HTTP ' + response.status)`. That's the idiom. Always check `response.ok` before calling `.json()`.

Now for POST — when you're sending data to the server, you need three things: the `method` option set to `'POST'`, a `headers` object with at least `'Content-Type': 'application/json'` so the server knows what format you're sending, and a `body` with `JSON.stringify(yourData)`. You're converting your JavaScript object into a JSON string to send over the wire. The server converts it back.

One more thing about fetch: if you need to add an auth token — which you will once you hit Spring Security in Week 6 — you add it as an `Authorization` header. `'Authorization': 'Bearer ' + token`. That token typically lives in localStorage after login.

The mental model for fetch: it's powerful, it's built-in, but it's low-level. You manage all the details yourself. That's fine for simple cases, but for large apps, most teams reach for axios instead.

---

`[18:00–26:00]`

**Slide 5 — Axios.**

Axios is a third-party library that wraps fetch with a friendlier API. `npm install axios`. Here are the three differences that matter most.

First: axios automatically parses JSON. When you do `const response = await axios.get('/api/products')`, your data is at `response.data`. No `.json()` call needed. Axios handles that for you.

Second: axios automatically throws on 4xx and 5xx responses. A 404 or 500 becomes a rejected promise. You don't need to check `.ok` — you just put everything in a try/catch and any HTTP error goes to the catch block. This is the more intuitive behavior and it's why most teams prefer axios.

Third: you can create an axios *instance* with default configuration. `axios.create({ baseURL: 'http://localhost:8080/api', timeout: 5000 })`. That instance remembers the base URL so every request just uses the path, like `/products`. And if the server doesn't respond in 5 seconds, axios throws a timeout error. This is something you'd have to implement manually with fetch using AbortController.

The most powerful feature of axios instances is interceptors. An interceptor is a function that runs before every request or after every response. The most common use: a request interceptor that automatically adds the Authorization header. You write it once in your API client, and every single axios call in your entire app gets that header without you thinking about it. The alternative is copying and pasting the header into every fetch call — which is both tedious and error-prone.

Here's a quick comparison so you can decide when to use which. Fetch: built-in, no install, verbose error handling, no automatic JSON parsing, no timeout, no interceptors. Axios: external dependency, concise error handling, auto JSON parsing, built-in timeout, interceptors. For small apps or when you want zero dependencies, fetch is fine. For anything production-scale, most teams use axios.

---

`[26:00–36:00]`

**Slide 6 — The three-state pattern.**

Here is the most important pattern in all of React API work. Every time you fetch data, you are in one of exactly three states: loading, you have data, or you have an error. You need to track all three in your component, and you need to handle all three in your JSX.

Here's what that looks like. Three `useState` calls: `loading` initialized to `true`, `data` initialized to `null`, `error` initialized to `null`. Then a `useEffect` that runs the fetch. Inside the effect, the try block calls fetch and sets `data`. The catch block sets `error`. The finally block sets `loading` to `false`. Finally runs whether the fetch succeeded or failed — it's the "we're done waiting" signal.

And there's one more piece: a `cancelled` flag. When a component unmounts — say the user navigates away before the fetch completes — the fetch is still in flight. When it eventually resolves, it will try to call `setData` on a component that no longer exists. React will give you a warning: "Can't perform a React state update on an unmounted component." The fix: create a flag variable inside the effect. If `cancelled` is true, skip the state updates. In the effect's cleanup function — the `return () => {}` — set `cancelled` to true. Now when the component unmounts, the cleanup function fires, sets `cancelled`, and any pending state updates get skipped.

Now let's look at the JSX side. Three branches: if `loading`, return a spinner or skeleton. If `error`, return an error message. Otherwise, render the data. And this is important — always render the loading state first, then error, then success. If you forget the loading state, you get a flash of empty content while the data is loading, which looks broken.

**Slide 7 — Custom useFetch hook.**

Now look at this pattern and notice something: it's completely reusable. The fetch URL changes, but the loading/data/error logic is identical for every API call. That's a textbook case for a custom hook.

The `useFetch` hook takes a URL, runs the same three-state pattern internally, and returns `{ loading, data, error }`. Any component that needs to fetch data calls `useFetch('/api/whatever')` and gets back those three pieces of state. The cancelled flag, the useEffect cleanup — all of that lives inside the hook once, and every consumer gets it for free.

This is the real power of custom hooks: they're not components, they're not plain functions — they're pieces of *stateful logic* that can be shared across components. We previewed this concept on Day 17a with `usePrevious`. `useFetch` is a much more practical example.

---

`[36:00–45:00]`

**Slide 8 — Mutations.**

So far we've been talking about GET requests — reading data on mount. But what about POST, PUT, and DELETE? Creating a product, editing a product, deleting a product? These are called *mutations* — they change data on the server.

Mutations are different from fetching in one key way: they happen in response to a user action, not on mount. When the user clicks "Submit," you trigger the POST. When they click "Delete," you trigger the DELETE. You don't run them inside `useEffect` on mount.

The pattern is still similar: a function that calls the API, wrapped in try/catch, updating loading/error state. I often extract this into a custom hook too, like `useCreateProduct`. The hook returns a `create` function and an `isLoading` state. The component calls `create(productData)` when the form is submitted. The hook handles the API call, then calls `useNavigate` to redirect the user after success.

One thing to handle: disabling the submit button while the request is in flight. If you don't, the user might click it multiple times and create duplicate records. Check `isLoading` and set the button's `disabled` attribute.

**Slides 9 and 10 — createAsyncThunk, the full picture.**

On Day 18a we previewed `createAsyncThunk` — I said "this is how Redux handles async operations, we'll build it out tomorrow." Tomorrow is here.

Here's the full pattern. `createAsyncThunk` takes two arguments: an action type string like `'products/fetchAll'`, and an async function called the *payload creator*. Inside the payload creator, you do your API call and return the data. If something goes wrong, you call `rejectWithValue(error.message)` and return it — this packages the error message as the rejected action's payload so you can display it.

Now the slice. You don't put thunks inside the `reducers` object — those are only for synchronous actions. Instead, you use `extraReducers`, which handles actions defined outside the slice. The builder pattern gives you three cases per thunk: `pending` (the request started — set loading to true), `fulfilled` (it succeeded — store the data), `rejected` (it failed — store the error message).

And in the component, you dispatch the thunk on mount: `useEffect(() => { dispatch(fetchProducts()); }, [dispatch])`. Redux Toolkit handles all the async lifecycle — firing the pending action automatically, firing fulfilled with the returned data, firing rejected if the payload creator threw or called `rejectWithValue`.

You can also dispatch thunks with parameters. `dispatch(fetchProductById(42))` — that `42` arrives as the first argument to the payload creator. `dispatch(createProduct({ name: 'Widget', price: 9.99 }))` — the object arrives as the first argument too.

The key insight: `createAsyncThunk` doesn't replace your custom hooks. It replaces them *inside Redux*. If data needs to live in the global store — shared across multiple pages, persisted across navigation — use `createAsyncThunk`. If data is local to one component, a custom hook is simpler. Use the right tool for the scope.

---

`[45:00–52:00]`

**Slide 11 — Error handling patterns.**

Let's talk about error handling more comprehensively, because there are actually three layers where errors can occur and each layer needs a different response.

Layer one: HTTP errors. These are errors from the network layer or the server. Fetch gives you `response.ok` to check. Axios throws automatically. In either case, you have the HTTP status code — 400 Bad Request (you sent bad data), 401 Unauthorized (not logged in), 403 Forbidden (logged in but not allowed), 404 Not Found, 409 Conflict (duplicate data), 500 Internal Server Error. The right UI response depends on the status. A 401 should redirect to login. A 404 should show "not found." A 500 should show a generic "something went wrong" and maybe log the error.

Layer two: application errors. These are cases where the HTTP request succeeded — status 200 — but the data you got back doesn't make sense. Maybe the API returned an empty array when you expected at least one item. Maybe a required field is missing. These aren't network errors, so they won't be caught by `response.ok`. You validate the data yourself and throw if it's invalid.

Layer three: render errors. These are JavaScript errors that happen during rendering — in JSX, in lifecycle methods, in constructors. These are caught by Error Boundaries, which we'll cover in two slides.

**Slide 12 — Loading state UI patterns.**

While you're in the loading state, you have several options for what to show the user. The simplest: a spinner — a CSS animation that says "working on it." Accessible spinners need an `aria-label` so screen readers announce them.

Skeleton UI is more sophisticated: you show placeholder shapes — gray boxes where text and images will be — that roughly match the layout of the content. This reduces perceived load time because the user's brain starts to understand the structure of the page before the data arrives. Most design systems include a Skeleton component.

Optimistic UI is the most advanced: you update the UI immediately on user action, before the server confirms success, and then roll back if the server returns an error. It makes the app feel instant. Amazon does this when you add something to your cart — the cart count updates immediately. Implementation: update local state first, then make the API call, and on error, revert to the previous state.

Disabled submit button: the simplest and most important. Set `disabled={isLoading}` on the submit button. This one line prevents double-submits and duplicate records.

---

`[52:00–59:00]`

**Slides 13, 14, 15 — Error Boundaries.**

Here's a scenario: you have a deeply nested component, and during rendering it throws a JavaScript error. Without any protection, that error propagates up to the root of your app and your entire application crashes. The user sees a blank screen. That's terrible.

Error Boundaries are React's solution. An Error Boundary is a component that wraps a subtree of your app. If anything inside that subtree throws during rendering, the Error Boundary catches it and renders a fallback UI instead of crashing the whole page.

What do Error Boundaries catch? Rendering errors — errors thrown in JSX evaluation. Errors in lifecycle methods. Errors in constructors of child components. What do they NOT catch? Errors in event handlers — those you catch with try/catch in the handler. Async errors — those you catch with try/catch in your fetch code. And an Error Boundary cannot catch its own errors — only its children's.

Implementation: Error Boundaries must be class components. There is no hook equivalent. You implement two things: the static method `getDerivedStateFromError`, which receives the error and returns new state — typically `{ hasError: true }`. And `componentDidCatch`, which receives the error and a context object. This is where you log the error to a monitoring service like Sentry. The `render` method checks `this.state.hasError` — if true, return the fallback UI; otherwise return `this.props.children`.

Where do you put them? Think of them like circuit breakers. Wrapping the entire app is a safety net, but it's too coarse — one error anywhere kills the whole page. Much better to wrap each major section: each route, each major feature area, each item in a list if items can independently fail. Now one broken product card doesn't take down the whole product list.

The `react-error-boundary` library simplifies this. It provides a `<ErrorBoundary>` component with a `fallbackRender` prop where you pass a function that receives the error and a `resetErrorBoundary` function — so you can show a "Try Again" button. And `onError` for logging. You don't have to write the class component yourself.

**Slide 16 — Environment variables.**

Last topic for Part 1 — and this is practical and important. You have different API base URLs for development, staging, and production. You never want to hardcode a production URL in your source code. Environment variables are how you manage this.

In a Vite project, environment variables go in `.env` files. `.env.local` for your local overrides — never commit this file. `.env.development` for values used in `npm run dev`. `.env.production` for values used in `npm run build`.

The critical rule: every environment variable exposed to your React code must start with `VITE_`. This is a security feature. Vite only injects variables with the `VITE_` prefix into your bundle. Anything else — database passwords, secret API keys — stays server-side and invisible to Vite.

In your code, you access them as `import.meta.env.VITE_API_URL`. Note the `import.meta.env` — that's Vite syntax. If you're using Create React App, the prefix is `REACT_APP_` and the access syntax is `process.env.REACT_APP_API_URL`.

And a critical reminder: *never* put secrets in frontend environment variables. A secret API key in `VITE_SECRET_KEY` is not secret — it's compiled into your JavaScript bundle and anyone who opens DevTools can read it. Secrets live on the server. The frontend only ever holds tokens that have limited scope and can be revoked.

---

`[59:00–60:00]`

**Slide 17 — Part 1 Summary.**

Here's what we covered: fetch for browser-native HTTP, axios for a cleaner API with auto-parsing and auto-throwing, the three-state pattern with `loading`/`data`/`error`, `useFetch` custom hook, mutations for user-triggered writes, `createAsyncThunk` full implementation, three layers of error handling, loading UI patterns, Error Boundaries for render crash protection, and environment variables.

After the break, we're flipping to the other side of this coin: testing. How do you write automated tests that verify all of this behavior — without hitting real servers? That's Part 2. Take 10 minutes and we'll pick back up.

# Week 4 - Day 18a: React Routing & Redux
## Part 2 Lecture Script

**Duration:** 60 minutes
**Pacing:** ~165 words/minute
**Topics:** Redux fundamentals, Redux Toolkit, configureStore, Provider, useSelector, useDispatch, Redux DevTools, state management patterns

---

## [00:00–02:00] Opening

Welcome back. You now have React Router working — components render based on the URL, navigation is smooth, protected routes work. There's one thing still missing from the picture.

In your current app, every piece of state lives in a component. The shopping cart lives in some component's `useState`. If you navigate away from that page, React unmounts the component, and the cart data is gone. When you navigate back, it starts fresh. And even if you keep the component mounted, how does your Navbar know how many items are in the cart? The Navbar and the ProductList have no parent-child relationship. Passing state between them means threading it through every component in between — the prop drilling problem from Day 17a.

You saw `useContext` as a solution for stable data like themes and the current user. But Context isn't designed for state that changes frequently or state whose update logic is complex. That's where Redux comes in. Redux is a predictable, centralized state container — one place where global state lives, one way to update it, and every component can subscribe to exactly the piece it needs. Let's build it from the ground up.

---

## [02:00–08:00] The State Management Spectrum

Slide 2. I want to start with the big picture before we write a single line of Redux, because the most important Redux skill is knowing when NOT to use it.

You have three tiers. Tier one: `useState`. If only one component cares about a piece of state, put it there. Form input values, whether a dropdown is open, whether a button is hovered — these are local to one component. `useState` is perfect.

Tier two: `useContext`. When a handful of related components need the same data, and that data doesn't change very often, Context is the right tool. The current theme — light mode or dark mode. The logged-in user object. The current language. These are stable. They might change a few times per session. Context is ideal.

But here's the limitation of Context that most people don't think about: when the Context value changes, every single component that consumes that Context re-renders. If you have a shopping cart in Context and every time a user types in a search box it updates a nearby context value, everything re-renders. For infrequent updates it doesn't matter. For frequent updates, it hurts.

Tier three: Redux. Use it when many unrelated components need the same data. When changes are frequent. When the update logic is complex — multiple fields updating together atomically. When you need to trace every change for debugging. Shopping carts, notifications, real-time feeds, application-wide loading states.

The caption on the slide captures it well: "Context is a broadcast channel. Redux is a database with an audit trail."

---

## [08:00–16:00] Redux Core Concepts — Store, Action, Reducer

Now let's understand Redux itself before we write any code. Slide 3.

Three concepts. Master these and the code will make sense.

The **store** is a single JavaScript object that holds all of your global state. One store. The whole app shares it. You can call `store.getState()` and get back something like `{ cart: { items: [], total: 0 }, user: null, notifications: [] }`. That's your entire app state in one place.

An **action** is a plain JavaScript object that describes what happened. It always has a `type` property — a string that names the event. It typically has a `payload` property — the data associated with the event. `{ type: 'cart/addItem', payload: { id: 42, name: 'Laptop', price: 999 } }`. The action is a description of the intention. "Something wants to add this item to the cart." The action doesn't do anything itself.

A **reducer** is a pure function that takes the current state and an action, and returns the new state. This is where the actual logic lives. The reducer says: "Given this action type, here's how the state should change." 

The key rules for reducers: never mutate state directly — always return a new object. No side effects — no API calls, no random numbers, nothing that would give different results for the same inputs. Same inputs must always produce the same output. These rules are what make Redux predictable and debuggable.

Slide 4 — the data flow. I want you to follow this loop. User clicks "Add to Cart." The component calls `dispatch` with an action: `{ type: 'cart/addItem', payload: product }`. Redux receives the action and passes it, along with the current state, to the cart reducer function. The reducer returns a new state object. Redux saves that new state in the store. All subscribed components re-render with the new data. The Navbar badge showing cart count updates. The ProductCard showing "In Cart" updates.

Every single state change goes through this loop. No exceptions. Every change is an explicit, named action. This is why Redux is so debuggable — you can literally log every action and replay them to reproduce any bug.

---

## [16:00–22:00] Redux Toolkit — Less Boilerplate, Same Power

Slide 5. Here's an honest admission about vanilla Redux: it has a lot of boilerplate. You define a constant for each action type. You write an action creator function. You write a reducer with a switch statement. You manually spread state to avoid mutation. And for just a shopping cart, that's forty lines of code.

Redux Toolkit — RTK — is the official, recommended way to write Redux today. Same underlying library, same DevTools, same mental model. But it eliminates the boilerplate. The right column on the slide shows what RTK replaces: one `createSlice` call does what five separate pieces of vanilla code did.

Install both packages: `npm install @reduxjs/toolkit react-redux`. The `@reduxjs/toolkit` package is the store setup and slice utilities. The `react-redux` package is the hooks — `useSelector` and `useDispatch` — that connect your components to the store.

I want to flag one thing that will confuse you when you first see it: inside RTK's `createSlice` reducers, you write `state.items.push(item)` — which looks like mutation. And in vanilla Redux, that would be wrong. But RTK uses a library called Immer under the hood. Immer intercepts those mutations and produces a new immutable state object behind the scenes. Your code looks like mutation; the result is immutable. This is intentional — it makes reducers vastly easier to write. Trust it.

---

## [22:00–30:00] createSlice — Building Your First Slice

Slide 6 — `createSlice`. This is the most important RTK API. Let's walk through every part.

`name: 'cart'` — this string becomes the prefix for all your action types. Every action this slice generates will have a type that starts with `'cart/'`. So the `addItem` reducer becomes `'cart/addItem'`. The `removeItem` reducer becomes `'cart/removeItem'`. This namespace convention prevents collision when you have many slices.

`initialState` — what the cart looks like before any actions are dispatched. Here it's `{ items: [], total: 0 }`. When the app first loads, this is the cart state.

`reducers` — an object where each key is a reducer name and each value is the function that handles that action. The function receives `state` — the current cart state — and `action`. The `action.payload` is whatever the component passed to the action creator.

Look at `addItem`. It calls `state.items.push(action.payload)` — looks like mutation, but RTK's Immer makes it safe. It also adds to `state.total`. Both fields update atomically in a single action. That's one of the advantages over multiple `useState` calls.

When you call `createSlice`, it gives you back an object. You destructure two things from it. `cartSlice.actions` gives you the action creators — `addItem`, `removeItem`, `clearCart`. These are functions. Calling `addItem(product)` builds the action object `{ type: 'cart/addItem', payload: product }`. You export these action creators for components to import and dispatch.

`cartSlice.reducer` gives you the reducer function. You export this as the default export to pass to `configureStore`.

---

## [30:00–36:00] configureStore and Provider

Slide 7 — `configureStore`. This is where you assemble all your slices into one store.

You call `configureStore` with an object containing a `reducer` key. The value is an object where each key becomes a top-level key in your state, and each value is a slice's reducer. Here: `cart: cartReducer, user: userReducer, notifications: notificationsReducer`. The resulting state shape matches: `store.getState()` returns `{ cart: {...}, user: {...}, notifications: [...] }`.

`configureStore` automatically does several things for you. It enables Redux DevTools — no extra setup needed. It adds the `redux-thunk` middleware for async action support. It adds development-only checks for non-serializable state (which catches common mistakes). All of that for free.

This file — `store/store.js` — is the only place you'll configure Redux. After this, everything is done through hooks.

Slide 8 — `Provider`. In `main.jsx`, you import the store and wrap your app:

```jsx
<Provider store={store}>
  <BrowserRouter>
    <App />
  </BrowserRouter>
</Provider>
```

The `Provider` from `react-redux` makes the Redux store available to any component in the tree via hooks. This is the exact same concept as React's Context Provider — you wrap the tree, every component inside can access it. The two providers — `Provider` and `BrowserRouter` — are independent. Neither is a child of the other's context. Convention is `Provider` outside `BrowserRouter`, but the order doesn't affect functionality.

From this point forward, you never touch the store object directly in components. You use hooks.

---

## [36:00–44:00] useSelector and useDispatch

Slide 9 — `useSelector`. This hook is how you read state from the Redux store.

`const cartItems = useSelector(state => state.cart.items)`. The function you pass is called a "selector." It receives the entire Redux state and you return the piece you want. Simple.

But here's the performance detail that matters: `useSelector` does a reference equality check after the selector runs. If the returned value is the same reference as last time, React skips the re-render. This is why you should select the minimum data you need. If you select `state => state.cart`, any change to `state.cart` — even a field you don't use — triggers a re-render. If you select `state => state.cart.items.length`, only the length value is compared.

Look at the examples on the slide. `CartBadge` selects just `items`. `CartTotal` selects just `total`. `ProductCard` selects a derived boolean — whether the specific product's ID exists in the cart items. Each component subscribes to exactly the data it needs. Changes to unrelated parts of the store don't affect them.

Slide 10 — `useDispatch`. This is how you send actions to the store.

`const dispatch = useDispatch()`. That's a one-time call — you get the dispatch function, and you keep it. Then whenever the user does something: `dispatch(addItem(product))`.

Let me walk through the line carefully. `addItem` is the action creator you exported from `cartSlice`. It's a function. `addItem(product)` calls it with the product as the argument, and it returns the action object: `{ type: 'cart/addItem', payload: product }`. `dispatch(...)` sends that action object to the Redux store, which runs it through the reducer.

The mistake to avoid: `dispatch(addItem)` — no parentheses. That dispatches the function itself, not an action object. Redux will complain or silently do nothing. Action creators are functions. You must call them with parentheses to get the action object.

The `ProductCard` example on the slide uses both hooks together. `useSelector` to know if the item is already in the cart. `useDispatch` to add or remove it. The `isInCart` boolean controls both the button text and the handler. Clean, declarative, connected to global state.

---

## [44:00–50:00] Multiple Slices and Redux DevTools

Slide 11 — multiple slices. Real apps have multiple feature domains. Cart, user, notifications, UI state. Each gets its own slice file. Each slice manages its own branch of the state tree.

The `userSlice` example on the slide handles login and logout. `login` sets all the user fields at once. `logout` clears them. Because this is a single action with Immer, all those fields update atomically — you'll never see a half-logged-out state where `name` is set but `isLoggedIn` is still true.

The `Navbar` component at the bottom of the slide shows a component using two slices simultaneously. Two separate `useSelector` calls — one for `state.user`, one for `state.cart.items.length`. One `useDispatch` call. The component re-renders only when its selected data changes. If the user profile changes but the cart doesn't, only the user-related selector triggers a re-render. If the cart changes but user doesn't, only the cart selector triggers.

This per-selector independence is the key performance advantage over Context. In Context, all consumers re-render together. In Redux, each component subscribes to exactly what it needs.

Slide 12 — Redux DevTools. This might be the most satisfying thing you'll show students all week.

Install the Redux DevTools Extension in your browser. Because we used `configureStore` from RTK, it's already connected — no extra setup.

Open your browser's developer tools, find the Redux tab. Every action you dispatch appears in the left panel with a timestamp. Click any action to see: the action object, the diff (what changed in state), and the full state at that moment. And the game-changer: you can click "Jump" next to any past action and your app's UI updates to show exactly what it looked like at that moment in time. You can step through your state history like a debugger stepping through code.

For finding bugs: reproduce the issue, look at the action log, find where state took the wrong turn. The audit trail that Redux enforces pays off immediately in debugging.

---

## [50:00–55:00] Async Actions Preview and State Decision Guide

Slide 13 — `createAsyncThunk`. I'm going to show this briefly because you'll need it in Day 19a when we integrate real APIs. Today, just understand the shape of the pattern.

`createAsyncThunk` wraps an async function. You give it a name and an async function. When you dispatch the thunk, Redux automatically dispatches three actions for you: `pending` when the async call starts, `fulfilled` when it succeeds, `rejected` if it fails. You handle each in `extraReducers`.

The `productsSlice` example shows the state shape: `{ items: [], loading: false, error: null }`. When `fetchProducts.pending` fires, `loading` becomes true. When `fulfilled` fires, `loading` becomes false and `items` gets the data. When `rejected` fires, `loading` becomes false and `error` gets the message. This is the standard pattern for loading states with Redux.

In a component, you `dispatch(fetchProducts())` inside a `useEffect`. The async call runs, and your state automatically transitions through loading → success or loading → error.

We'll build this out fully in Day 19a with actual `fetch` and `axios` calls. For now, the concept is: one `createAsyncThunk` generates three action types. Handle them in `extraReducers`. Your loading and error states come for free.

Slide 14 — state management decision guide. This is the practical output of everything we've covered. Print this out. Put it on your wall. It answers "should I use `useState`, `useContext`, or Redux?"

If only one component needs it — `useState`. If a few related components need stable, infrequently-changing data — `useContext`. If many unrelated components need it, or it changes frequently, or the update logic is complex, or you want an audit trail — Redux.

The table shows real examples. Form inputs: `useState`. Modal open/closed: `useState`. Current user: `useContext`. Theme: `useContext`. Shopping cart: Redux. Notifications: Redux. App-wide loading state: Redux.

The key distinction between Context and Redux in the notes: Context re-renders all consumers on every value change. `useSelector` in Redux only re-renders components whose selected data changed. For high-frequency updates — like a real-time notification feed — that difference is significant.

---

## [55:00–60:00] Complete Example and Day 18a Summary

Slide 15 — the complete cart example. I want to walk through this at a high level to show how everything connects.

The `cartSlice` has an `addItem` that handles duplicate items by incrementing `qty` rather than pushing duplicates — a more realistic implementation. Notice `state.items.find(...)` inside a reducer — because Immer, this is safe.

The `CartPage` component calls `useSelector` to get both `items` and `total`. It calls `useDispatch`. For each item it renders the name, quantity, price, and a remove button. The remove button dispatches `removeItem(item.id)`. A "Clear Cart" button dispatches `clearCart()`. No local state. No props. Just Redux.

The moment a product card in `ProductList` dispatches `addItem`, the store updates. The `CartPage` component re-renders with the new items. The `Navbar`'s `cartCount` updates. All of this happens through the Redux store — no prop drilling, no threading state through parents.

Slide 17 — the Day 18a summary. Everything we covered today.

React Router: `BrowserRouter` wraps the app, `Routes` and `Route` define the map, `Link` and `NavLink` for declarative navigation, `useNavigate` for programmatic redirects. `useParams` for path parameters, `useSearchParams` for query strings. Nested routes and Outlet for shared layouts. `ProtectedRoute` for authentication guards. HOCs for wrapping components with cross-cutting behavior.

Redux Toolkit: `createSlice` generates action creators and the reducer in one call. `configureStore` combines slices. `Provider` makes the store available. `useSelector` subscribes to state. `useDispatch` sends actions. `createAsyncThunk` handles async operations (preview for Day 19a).

Day 19a on Thursday covers API integration — fetch and Axios, error boundaries, loading states, and React Testing Library. The `createAsyncThunk` pattern we previewed today will be the bridge between Redux and real HTTP calls.

---

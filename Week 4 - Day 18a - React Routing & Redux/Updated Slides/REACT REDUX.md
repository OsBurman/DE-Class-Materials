SLIDE 1 — Title Slide
Slide content: "React Redux" as the title, subtitle "Managing Global State at Scale", your name, date.

SCRIPT:
"Good morning everyone. Today we're pulling together everything you've been introduced to and going deeper — we're going to talk about Redux as a complete system, how it connects to React, the modern way to write it with Redux Toolkit, and how to actually debug it when things go wrong. By the end of this class you'll have a mental model that covers the full Redux lifecycle from state to screen."
[~1 minute]

SLIDE 2 — Lesson Roadmap
Slide content: A simple numbered list of today's topics: Redux Fundamentals → Redux Toolkit → Connecting React to Redux → Hooks → DevTools → State Patterns.

SCRIPT:
"Here's our roadmap for the hour. We'll start by grounding ourselves in the core concepts — the store, actions, and reducers. Then we'll look at Redux Toolkit, which is the modern and recommended way to write Redux. After that we'll wire it all up to React using hooks. Then we'll cover DevTools so you can actually see what's happening inside your app. And we'll close with some real-world state management patterns you should know. Let's get into it."
[~1 minute]


PART 1 — Redux Fundamentals (12 minutes)

SLIDE 3 — What Problem Does Redux Solve?
Slide content: Two diagrams side by side. Left: "Prop Drilling" — a tree of components passing props down multiple levels with arrows showing the pain. Right: "Redux Global Store" — a central box with arrows pointing directly to any component that needs it.

SCRIPT:
"Before we talk about what Redux is, let's talk about why it exists. When you have a small React app, passing state around with props works fine. But imagine you have a deeply nested component — maybe five or six levels down — that needs access to the logged-in user's name. You'd have to pass that data through every single component in between, even the ones that don't care about it at all. This is called prop drilling and it becomes a real maintenance problem as your app grows.
Redux solves this by giving your entire application a single, centralized place to store state. Any component, at any level of your tree, can read from or write to that store directly. No more passing props through the middle."
[~2 minutes]

SLIDE 4 — The Three Core Principles of Redux
Slide content: Three bold statements: 1) Single source of truth. 2) State is read-only. 3) Changes are made with pure functions.

SCRIPT:
"Redux is built on three principles that everything else flows from. First: single source of truth. Your entire application's state lives in one JavaScript object inside one store. This makes your app predictable and easy to debug because there's one place to look.
Second: state is read-only. You never directly modify state. Instead, you describe what happened by dispatching an action. This keeps your data flow explicit and traceable.
Third: changes are made with pure functions. These functions are called reducers. A pure function means given the same input, it always returns the same output and has no side effects. It doesn't call an API, it doesn't mutate anything — it just takes the current state and an action and returns a new state.
These three principles are not just good ideas — they're what make Redux's debugging tools so powerful, which we'll see later."
[~3 minutes]

SLIDE 5 — The Store
Slide content: Code snippet showing a basic store created with configureStore from Redux Toolkit. Next to it, a simple diagram: "Store = { state, dispatch, getState }".

SCRIPT:
"The store is the heart of Redux. It holds your application state and gives you a few key abilities — you can get the current state, you can dispatch actions to trigger changes, and you can subscribe to updates.
Now, you'll notice I'm already using Redux Toolkit here with configureStore rather than the older createStore API. The Redux team officially recommends Toolkit for all new projects and we'll dive deep into why in part two. For now just understand that the store is the single object that everything revolves around."
[~2 minutes]

SLIDE 6 — Actions
Slide content: Code showing a plain action object { type: 'counter/increment', payload: 1 }. Below it, an action creator function. A callout box: "type is required. payload is convention."

SCRIPT:
"An action is a plain JavaScript object that describes something that happened in your application. Think of it as an event. The type field is required — it's a string that names what happened. By convention, you'll often see it namespaced like 'counter/increment' or 'auth/loginSuccess' so you can tell at a glance which feature of the app it belongs to.
The payload field is where you put any data that goes along with the event. If a user logs in, the payload might be their user object. If they add a quantity to a cart, the payload might be the number.
Action creators are just functions that return action objects. Rather than writing the raw object every time, you call the function. This cuts down on typos and keeps things consistent."
[~2.5 minutes]

SLIDE 7 — Reducers
Slide content: Code block showing a simple reducer function with a switch statement handling two action types and a default that returns state unchanged. Highlighted: "Always return state in the default case."

SCRIPT:
"A reducer is a pure function that takes two arguments — the current state and an action — and returns the next state. It decides how state should change in response to each action.
The classic pattern is a switch statement over action.type. For each type you handle, you return a new state object. Notice we're not modifying the existing state — we're returning a brand new object. This immutability is critical to Redux working correctly. If you mutate state in place, Redux can't detect that anything changed.
The default case is important — always return the current state for any action your reducer doesn't handle. Redux dispatches actions internally and other reducers in your app may also receive your actions, so you want to be a good citizen and not break things by returning undefined.
Now, writing immutable updates by hand can get verbose and error prone. This is one of the biggest pain points Redux Toolkit solves, and we'll see how in just a moment."
[~2.5 minutes]


PART 2 — Redux Toolkit (10 minutes)

SLIDE 8 — What is Redux Toolkit?
Slide content: Redux Toolkit logo. Bullet points: "The official, recommended way to write Redux. Eliminates boilerplate. Includes Immer for immutable updates. Includes Redux Thunk for async. Ships with Redux DevTools integration."

SCRIPT:
"For years, one of the biggest complaints about Redux was the amount of boilerplate code you had to write. Action type constants, action creators, verbose switch statements, manual immutable spread operators everywhere — it added up fast. Redux Toolkit, often called RTK, was created by the Redux team to solve this.
RTK wraps Redux with sensible defaults and utilities that dramatically reduce the amount of code you write. It includes Immer under the hood, which lets you write code that looks like it's mutating state but actually produces immutable updates safely. It includes Redux Thunk for handling async logic. And it sets up the Redux DevTools automatically. You get all the power of Redux with a fraction of the ceremony."
[~2 minutes]

SLIDE 9 — createSlice
Slide content: A full code example of createSlice with a name, initialState, and two reducers. Show the exported actions and reducer below it.

SCRIPT:
"The star of Redux Toolkit is createSlice. A 'slice' is just a piece of your Redux state — it could be the auth slice, the cart slice, the notifications slice. createSlice takes a name, an initial state, and an object of reducer functions, and automatically generates your action creators and action types for you.
Look at this example. We define our reducer logic, and RTK generates the action creators with matching type strings automatically. The name you give the slice becomes the prefix on your action types — so 'counter/increment' and 'counter/decrement' are created for us.
And notice something critical here — inside these reducer functions, we're writing state.value += 1. That looks like a mutation! But because RTK uses Immer under the hood, it's intercepting this and producing a brand new immutable state object. You get the readability of mutations without breaking Redux's rules. This is one of the best features of RTK."
[~3 minutes]

SLIDE 10 — configureStore with Slices
Slide content: Code showing configureStore with a reducer object combining two slices. A note: "Each key becomes a top-level property of your state tree."

SCRIPT:
"Once you have your slices, you bring them together in configureStore. Each slice's reducer gets assigned to a key, and that key becomes the path to that slice's state in the global store. So if you have a key called 'counter', you'll access that state at state.counter.
configureStore also handles combining your reducers internally — in plain Redux you'd need to call combineReducers yourself. RTK does that for you when you pass an object to the reducer option. It also sets up the Redux DevTools extension automatically in development, which we'll cover shortly."
[~2 minutes]

SLIDE 11 — Quick Comparison: Before & After RTK
Slide content: Side-by-side code. Left column labeled "Vanilla Redux" — showing action type constants, action creator function, and switch-statement reducer. Right column labeled "Redux Toolkit" — showing the equivalent createSlice. The RTK side should visually be much shorter.

SCRIPT:
"I want to take a moment to show you this side by side because it really drives home why RTK is the standard now. On the left, vanilla Redux — you're defining string constants, writing action creators by hand, and managing a switch statement. On the right, the exact same functionality in RTK's createSlice. Same behavior, a fraction of the code.
This isn't just about fewer lines — it's about fewer opportunities for bugs. Typos in action type strings were a common source of bugs in pre-RTK Redux. RTK eliminates that entire category of problem. Going forward in this class and in your projects, we'll be using RTK exclusively."
[~3 minutes]


PART 3 — Connecting React to Redux (10 minutes)

SLIDE 12 — Setting Up the Provider
Slide content: Code showing index.js or main.jsx with the <Provider store={store}> wrapping the root <App /> component. A callout: "Provider makes the store available to every component in the tree."

SCRIPT:
"Now let's actually wire Redux into a React application. The connection point between React and Redux is the Provider component, which comes from the react-redux library.
You wrap your entire application — at the root level — with this Provider and pass it your store. What this does under the hood is use React's Context API to make the store accessible to any component anywhere in the tree. You do this once, in your entry file, and then every component in your app has the ability to interact with Redux.
If you forget the Provider, you'll get an error telling you that you're trying to use Redux hooks outside of a Provider context. That's one of the most common setup mistakes, so remember — Provider goes at the very top."
[~2 minutes]

SLIDE 13 — useSelector
Slide content: Code showing a component using useSelector to pull a value from the store. Show the selector function taking state and returning a nested value. Highlight: "Re-renders only when this specific piece of state changes."

SCRIPT:
"With the Provider in place, you can now access state inside any component using the useSelector hook. You call useSelector and pass it a selector function — a function that receives the entire Redux state and returns just the piece you care about.
The key thing to understand about useSelector is that it subscribes your component to changes in the store. Whenever the store updates, useSelector runs your selector function again. If the returned value is different from what it was before, React re-renders the component. If it's the same value, nothing happens.
This is important for performance. Because of this, you want your selectors to be as specific as possible — return exactly what your component needs and nothing more. Don't select the entire state object if you only need one field, because then your component re-renders on every single state change, even ones completely unrelated to what it displays."
[~3 minutes]

SLIDE 14 — useDispatch
Slide content: Code showing a component using useDispatch to get the dispatch function, then calling it in an event handler with an action creator imported from a slice.

SCRIPT:
"If useSelector is how you read from the store, useDispatch is how you write to it. You call useDispatch with no arguments and it gives you the dispatch function for your store.
Then whenever something happens in your component — a button click, a form submit, whatever — you call dispatch with an action. Here we're importing the action creator from our slice and calling it, which produces the action object, and dispatch sends it to the store. The store runs it through the appropriate reducer, state updates, and any component subscribed to that part of state re-renders.
That is the complete Redux data flow in React: component dispatches action → reducer handles it → state updates → subscribed components re-render. You'll see this loop over and over again."
[~2.5 minutes]

SLIDE 15 — The Redux Data Flow Diagram
Slide content: A clear visual loop: UI → dispatches Action → Store runs Reducer → New State → useSelector updates → UI re-renders. Arrows connecting each step in a cycle.

SCRIPT:
"Let's look at this as a diagram because I want it locked in your head. The UI dispatches an action. The store receives it and runs your reducer with the current state and the action. The reducer returns new state. The store saves that new state. useSelector detects the change and your component re-renders with the new data. Around and around it goes.
This unidirectional data flow is what makes Redux so predictable and debuggable. Data only ever flows in one direction. There's no two-way binding, no hidden channels. If something is wrong with your UI state, you know exactly where to look."
[~2.5 minutes]


PART 4 — Redux DevTools (8 minutes)

SLIDE 16 — Installing Redux DevTools
Slide content: Screenshot of the Redux DevTools browser extension in the Chrome Web Store. Short note: "Install as a browser extension. Works automatically with configureStore."

SCRIPT:
"Redux DevTools is a browser extension available for Chrome and Firefox, and it is genuinely one of the best debugging tools in the JavaScript ecosystem. Go install it if you haven't — it should take about thirty seconds.
The good news is if you're using configureStore from Redux Toolkit, the DevTools integration is already set up for you automatically in development mode. There's nothing extra to configure. You open your app, open the browser DevTools panel, and you'll see a Redux tab."
[~1.5 minutes]

SLIDE 17 — Exploring the DevTools Interface
Slide content: Annotated screenshot of the Redux DevTools panel showing: the action log on the left, the state tree on the right, and the diff tab at the bottom. Label each section.

SCRIPT:
"When you open Redux DevTools, you'll see a few key areas. On the left is the action log — every single action that has been dispatched in your app shows up here in chronological order, with a timestamp. You can click any action to inspect it.
On the right, you can see the full state tree at any point in time. Switch between the State tab, which shows you what state looked like after the selected action ran, the Diff tab, which shows you exactly what changed between the previous state and the current one, and the Action tab, which shows you the raw action object that was dispatched.
The Diff view is especially useful. Instead of hunting through a large state object trying to figure out what changed, it highlights only the fields that were modified. For a complex app with lots of state, this saves enormous amounts of debugging time."
[~3 minutes]

SLIDE 18 — Time Travel Debugging
Slide content: Diagram or screenshot showing the "Jump" button in DevTools next to an action in the log. Bold text: "You can jump to any previous application state instantly."

SCRIPT:
"Now for the feature that makes Redux DevTools genuinely special — time travel debugging. Because every state change is recorded, and because reducers are pure functions, the DevTools can reconstruct your application's state at any point in history.
You can click 'Jump' next to any action in the log and your application will literally jump back to what it looked like at that moment in time. You can step forward and backward through your state history like scrubbing through a video. You can even skip specific actions and see how your app would have looked if they'd never happened.
This is incredibly powerful for reproducing bugs. A user reports a problem? If you can reproduce the sequence of actions, you can walk through them step by step and see exactly where the state went wrong. It turns debugging from guesswork into a deterministic process."
[~2.5 minutes]

SLIDE 19 — Using DevTools Effectively — Tips
Slide content: Short tip list: Name your actions descriptively. Keep state normalized. Use the filter bar to focus on specific action types. Export/import state snapshots.

SCRIPT:
"A few practical tips for getting the most out of DevTools. First, your action names are your breadcrumbs — if you name actions well, like 'cart/itemAdded' or 'auth/loginFailed', your action log reads like a story of what happened in the app. Vague action names make the log useless.
Second, use the filter bar in the DevTools panel to search for specific action types. In a large app with lots of activity, the log fills up fast. Being able to filter to just 'cart/' actions is a lifesaver.
Third, you can export your entire state as a JSON file and import it back later. This is great for sharing bug reproductions with teammates — you can literally send someone the exact state your app was in when the bug occurred."
[~1 minute]


PART 5 — State Management Patterns (12 minutes)

SLIDE 20 — What State Belongs in Redux?
Slide content: Two columns. "Put in Redux:" — shared state, server data, global UI state (modals, themes), auth/session. "Keep in local state:" — form input values, UI state local to one component, animation state.

SCRIPT:
"A question you'll face on every project is: what should go in Redux and what should stay in local React state? A lot of developers early on make the mistake of putting everything in Redux, and then their codebase becomes unnecessarily complex.
The rule of thumb is: if multiple unrelated components need access to the same piece of state, put it in Redux. If state is only ever used in one component, or in a component and its direct children, keep it local with useState.
Good candidates for Redux are things like the authenticated user, server data that multiple parts of the app display, application-wide UI state like whether a sidebar is open or what theme is active, and things like notification queues. Bad candidates for Redux are individual form field values, a boolean controlling whether a local dropdown is open, or the currently hovered item in a list."
[~3 minutes]

SLIDE 21 — Normalizing State
Slide content: Two code blocks. Left: "Nested Array" — array of objects with nested relationships. Right: "Normalized" — state as { ids: [], entities: {} }. A note: "Redux Toolkit's createEntityAdapter handles this pattern for you."

SCRIPT:
"When you're storing collections of data in Redux — like a list of posts, users, or products — you have a choice in how you structure it. The naive approach is to store them as an array of objects. This works for small datasets but becomes slow and awkward to work with as data grows, because to find one item you have to scan the whole array.
The normalized approach stores your data as a lookup table — an object keyed by ID — plus a separate array of IDs to maintain order. This gives you O(1) lookups, makes updates much simpler since you just update the one entry at its key, and avoids duplication when the same entity appears in multiple places.
RTK actually ships a utility called createEntityAdapter specifically for this pattern. It generates the CRUD reducer functions and selectors you need to work with normalized state, so you don't have to hand-write all of that logic. We'll likely come back to createEntityAdapter in a future lesson, but for now just understand the concept of why normalized state matters."
[~3 minutes]

SLIDE 22 — Selectors and Derived State
Slide content: Code showing a basic selector, then the same selector wrapped with createSelector from Reselect for memoization. Callout: "Memoized selectors only recompute when their inputs change."

SCRIPT:
"As your app grows, you'll often need to derive data from your Redux state — filtering a list, sorting items, computing a total. You could do this derivation inside the component, but a better pattern is to put it in a selector.
A selector is just a function that takes state and returns a computed value. The advantage of keeping selectors in your slice files is that you can reuse the same derived data logic across multiple components without duplicating it.
For expensive derivations, you'll want to memoize your selectors. Reselect is a library that RTK includes, and its createSelector function creates memoized selectors that only recompute their output when the relevant inputs change. This is a significant performance optimization — if a component is re-rendering frequently but the underlying data for a complex calculation hasn't changed, the memoized selector returns the cached result instead of recalculating."
[~3 minutes]

SLIDE 23 — Organizing Redux Code — Feature Folders
Slide content: A file tree diagram showing a feature-based folder structure: features/counter/counterSlice.js, features/auth/authSlice.js, features/cart/cartSlice.js. Note: "Co-locate slice, selectors, and related components."

SCRIPT:
"Last pattern — how to organize your Redux code in a real project. The RTK-recommended approach is feature folders. Instead of separating your code by type — all actions in one folder, all reducers in another — you group by feature. Everything related to authentication lives in the auth feature folder. Everything related to the shopping cart lives in the cart folder.
Inside each feature folder you'll typically have a slice file that contains your createSlice call, your initial state, your reducers, and your selectors, all in one place. This keeps related code together and makes it much easier to find what you're looking for. When you're working on the cart feature you don't need to jump between multiple folders — everything you need is in one place.
This is the pattern you'll see in most modern Redux codebases and it's what we'll use for our projects going forward."
[~3 minutes]


PART 6 — Putting It Together / Wrap-Up (7 minutes)

SLIDE 24 — Live Walkthrough Reference
Slide content: A simple app diagram showing: store.js (configureStore) → counterSlice.js (createSlice) → Counter.jsx (useSelector + useDispatch) → Provider in main.jsx. Arrows connecting each piece.

SCRIPT:
"Let me walk you through how all of this fits together as a complete application. Think of it in four files at minimum.
First you have your slice file — this is where you define your initial state and your reducers using createSlice. It exports your action creators and your reducer.
Second you have your store file — this calls configureStore and passes in your slice reducers. It exports the configured store.
Third, in your entry point file, you import the store, wrap your app in Provider, and pass the store in. This is done once.
Fourth, in any component that needs to interact with Redux, you use useSelector to read state and useDispatch to dispatch actions. That's it. That's the complete picture."
[~2.5 minutes]

SLIDE 25 — Common Mistakes to Avoid
Slide content: Short numbered list: 1) Mutating state directly outside Immer context. 2) Forgetting the Provider. 3) Selecting too much state causing excessive re-renders. 4) Putting everything in Redux — keep local state local. 5) Not using Redux Toolkit — don't write vanilla Redux from scratch.

SCRIPT:
"Before we close, let's quickly cover the mistakes I see most often. One: mutating state directly. Even with Immer, you can only write mutations inside createSlice reducer functions. Mutating state elsewhere breaks Redux entirely.
Two: forgetting the Provider. You'll get a clear error, but it wastes time.
Three: selecting too broadly. If you useSelector on a large object when you only need one field, your component re-renders too aggressively.
Four: Redux-ifying everything. Not all state needs to be global. If a component is the only one that cares about a value, useState is the right choice.
Five: writing Redux without RTK. There's really no good reason to skip RTK on a new project. It exists to solve real problems and the Redux team themselves tell you to use it."
[~2 minutes]

SLIDE 26 — Key Takeaways
Slide content: Five bold one-liners: "Redux gives your app a single, predictable source of state. Reducers are pure functions — no mutations, no side effects. Redux Toolkit eliminates boilerplate — use it. useSelector reads state. useDispatch triggers changes. Redux DevTools makes debugging a superpower."

SCRIPT:
"Let me leave you with these five things. Redux gives you a single predictable source of truth. Reducers are pure functions. RTK is not optional nice-to-have — it's the standard. useSelector and useDispatch are your two connection points between React and Redux. And DevTools turns what could be invisible state management into something you can literally watch happen in real time.
Coming up in our next session we'll go into async operations with Redux Thunk and RTK Query, which builds on everything we covered today. Make sure you're comfortable with the data flow we walked through today because async patterns layer on top of it.
Any questions before we wrap up?"
[~2.5 minutes]

SLIDE 27 — Resources
Slide content: Links to: Redux Toolkit docs (redux-toolkit.js.org), Redux Essentials tutorial (official), React-Redux docs, Redux DevTools GitHub page.

SCRIPT:
"The Redux Toolkit documentation is excellent — it's well written, has clear examples, and the Redux Essentials tutorial on the official Redux site walks through a real mini app from scratch. I'd highly recommend going through that tutorial tonight to reinforce what we covered today. Any questions?"
[~30 seconds]
INTRO (5 minutes)
[Slide: Title slide — "React Hooks Deep Dive: Forms, Context, and Custom Hooks" with today's date and your name]
"Good morning everyone. Today we're going to build on what you already know about hooks and go deeper. By the end of this lesson you'll know how to handle forms the React way, share data across your entire app without passing props through every layer, and write your own custom hooks so you can reuse logic like a professional developer.
This lesson is a big one, so I want you to stay focused. We're going to move through six major topics, and each one builds on the last. Let's start with something you're going to use in almost every real-world app you ever build — forms."

SECTION 1: Forms and Controlled Components (10 minutes)
[Slide: "Forms in React — Who's in Charge of the Data?" — show a simple HTML form on the left and a React controlled form on the right side by side]
"In plain HTML, when a user types into an input field, the browser owns that data. It lives in the DOM. React has a different philosophy — React wants to own all of your application's state. So when it comes to forms, React gives us something called a controlled component.
A controlled component is simply an input whose value is tied to a piece of React state. The input doesn't hold its own value — your state does. Every keystroke fires an onChange event, you update state, and React re-renders the input with the new value. You're always in control."
[Slide: Code example — a single controlled text input with useState, showing value={name} and onChange={(e) => setName(e.target.value)}]
"Here's the simplest possible example. We have a name input. Its value prop is set to our name state variable, and onChange updates that state on every keystroke. If you console log name, it updates in real time as the user types. That's the core idea.
Now let me show you a full login form so you can see how this scales."
[Slide: Code example — a controlled login form with email and password fields, a formData state object using useState, a handleChange function using event.target.name and event.target.value, and a handleSubmit that prevents default and logs formData]
"Notice a few things here. Instead of a separate state variable for every field, I'm using one state object called formData with keys for email and password. My handleChange function uses event.target.name to know which field changed — that's why each input needs a name attribute that matches the key in your state object. And handleSubmit calls event.preventDefault() so the browser doesn't reload the page — this is essential in React forms.
The beauty of this pattern is that at any moment, your formData state is an exact mirror of what the user has typed. You can validate it, transform it, send it to an API — all without touching the DOM."
[Slide: "Why Controlled Components?" — bullet points: instant validation, conditional disabling of submit button, synchronize multiple fields, easy to reset the form, you own the data]
"Why go through this effort? Because you get a lot of power. You can validate as the user types and show error messages instantly. You can disable the submit button until the form is valid. You can reset the form by just resetting your state. These things are hard or awkward in plain HTML but trivial in React."

SECTION 2: Uncontrolled Components and useRef (10 minutes)
[Slide: "Uncontrolled Components — Letting the DOM Do the Work" — show a simple uncontrolled input with a ref, contrast it visually with the controlled example from before]
"Now I want to show you the other side of the coin — uncontrolled components. In an uncontrolled component, you don't track the value in React state at all. Instead, you let the DOM hold the value and you reach into the DOM to read it when you need it, usually on form submission. To do this, we use the useRef hook."
[Slide: "useRef — A Hook With Two Jobs" — two columns: (1) Accessing DOM elements directly, (2) Storing mutable values that don't trigger re-renders. Simple bullet explanations under each.]
"useRef is one of the more interesting hooks because it does two pretty different things. First, it gives you a direct reference to a DOM element, which lets you read values, focus inputs, measure sizes, and so on. Second, it gives you a box to store a mutable value that persists across renders but does not cause a re-render when it changes. We'll look at both uses.
useRef returns an object with a single property called current. That's it. When you attach it to a DOM element using the ref prop, current becomes that DOM node."
[Slide: Code example — uncontrolled form with useRef. Show const emailRef = useRef(null), the input with ref={emailRef}, and the handleSubmit accessing emailRef.current.value]
"Here we create a ref with useRef, pass it to the input's ref prop, and when the form is submitted we read emailRef.current.value. We never tracked it in state. For simple forms where you only care about the value at submission time and don't need real-time validation, this works fine and is a bit less code.
But here's the important question students always ask — when should I use controlled versus uncontrolled?"
[Slide: "Controlled vs Uncontrolled — When to Use Each" — two-column table. Controlled: real-time validation, conditional logic, dynamic fields, most of the time. Uncontrolled: simple one-off reads, file inputs, integrating with third-party non-React code.]
"The honest answer is that controlled components are almost always the better choice in React. They're more predictable and easier to debug. Uncontrolled components have their place — file inputs are actually always uncontrolled because you can't set their value in React — but default to controlled unless you have a specific reason not to.
Now let me show you the second use of useRef — storing a mutable value."
[Slide: Code example — using useRef to track how many times a component has re-rendered, storing renderCount.current++ in a useEffect, and displaying it. Show that changing other state doesn't reset it and doesn't cause an extra render.]
"Here we're using a ref as a plain mutable container. renderCount.current is like a variable that lives inside the component but changing it doesn't trigger a re-render the way setState would. This is useful for things like tracking timers, storing previous values of props, or holding a reference to a setInterval ID so you can clear it later. You'll use this pattern more than you might expect."

SECTION 3: useContext and the Context API (12 minutes)
[Slide: "The Problem: Prop Drilling" — diagram showing a component tree three or four levels deep, with an arrow showing a value being passed as a prop down through components that don't actually need it, just to get it to a deeply nested child]
"Before I show you useContext, I want to make sure you understand the problem it solves. Imagine you have a logged-in user object at the top of your app. Now a button deep in your component tree needs to display that user's name. Without Context, you'd have to pass the user down as a prop through every single component in between — even the ones that don't care about the user at all. This is called prop drilling and it makes your code messy and brittle.
Context is React's built-in solution to this. It lets you broadcast a value from a parent component and have any descendant component subscribe to it directly, no matter how deep."
[Slide: "The Context API — Three Steps" — numbered list: (1) Create the context with createContext, (2) Wrap your component tree with a Provider and pass a value, (3) Consume it anywhere with useContext]
"There are three steps to using Context and I want you to memorize them. Create, Provide, Consume. Let's build a real example — a theme switcher that makes a dark/light mode toggle available across your whole app."
[Slide: Code example — Step 1 and 2 shown together. ThemeContext.js file showing: const ThemeContext = createContext(null), a ThemeProvider component that uses useState for theme, returns ThemeContext.Provider with value={{theme, toggleTheme}}, children inside. App.js wrapping everything in ThemeProvider.]
"First we create our context in its own file. createContext takes an optional default value — I'm passing null here because we'll always provide a real value through our Provider. Then we write a ThemeProvider component. This is just a regular React component that holds our theme state and wraps its children in ThemeContext.Provider. We pass our theme value and the toggleTheme function into the value prop. Everything inside ThemeProvider can now access these."
[Slide: Code example — Step 3: A deeply nested component calling const { theme, toggleTheme } = useContext(ThemeContext) and using it to apply a class or style and toggle on button click]
"Then anywhere in our app — doesn't matter how deep — we call useContext and pass it our context object. We destructure exactly what we need. No props, no passing anything through intermediate components. This component is now directly subscribed to our theme context.
There's one important thing to understand about re-renders here."
[Slide: "Context and Re-renders — What You Need to Know" — text explaining that every component consuming a context will re-render when the context value changes. Tip: keep context focused on related values, don't put everything in one context.]
"Every time the context value changes, every component that consumes that context will re-render. That's fine and expected — but it means you should be thoughtful about what you put in a single context. Don't create one giant app context with everything in it. Separate concerns. Have a ThemeContext, a UserContext, maybe an AuthContext. Keep each one focused."

SECTION 4: Custom Hooks (10 minutes)
[Slide: "Custom Hooks — Write Your Own Rules" — tagline: "If you find yourself copying and pasting stateful logic, it's time for a custom hook." Show the naming convention: always starts with 'use']
"Custom hooks are one of the things that makes React really elegant. Here's the idea — all those hooks we've been learning, useState, useEffect, useRef, useContext — they can be composed together inside a regular JavaScript function. If that function's name starts with 'use', React recognizes it as a hook and you can use all the hook rules inside it. That's genuinely all a custom hook is.
The reason to write them is code reuse. If two components need the same piece of stateful logic — fetching data, tracking window size, managing a form — you pull that logic into a custom hook and both components use it. No duplication."
[Slide: "Example — useFetch custom hook" — show the full code: useFetch(url) that internally uses useState for data, loading, error and useEffect to fetch the URL, returns {data, loading, error}]
"Let's build one. This is useFetch — a hook that any component can use to fetch data from a URL. Inside it's just useState and useEffect like you already know. It manages its own data, loading, and error state, does the fetch inside a useEffect, and returns those three values. Now any component that needs to fetch data just calls useFetch and gets back exactly what it needs."
[Slide: Code example — a component using useFetch, showing how clean it is: const { data, loading, error } = useFetch('https://api.example.com/users'). Contrast with what the component would look like if all that fetch logic lived inside it.]
"Look how clean this makes the component. One line to get all the data-fetching behavior. The component doesn't know or care about the implementation details. And if you need to fix a bug in your fetch logic, you fix it in one place and every component that uses useFetch gets the fix automatically.
Let me show you one more — a useLocalStorage hook — because this one comes up constantly in real projects."
[Slide: Code example — useLocalStorage(key, initialValue) hook that initializes state from localStorage if available, syncs state to localStorage on every change using useEffect, returns [value, setValue]]
"This hook wraps localStorage so you can use it exactly like useState but the value automatically persists across page refreshes. You'll use patterns like this all the time — wrapping browser APIs in hooks so they feel natural in React."
[Slide: "Custom Hook Rules" — bullet points: must start with 'use', can call other hooks inside, each call creates its own independent state, not for sharing state but for sharing logic, test them independently]
"A few rules to lock in. The name must start with use — this isn't just convention, it's how React's linter knows to apply the rules of hooks to your function. Each time a component calls your hook, it gets its own independent state — you're sharing logic, not state. If you want to share state, use Context."

SECTION 5: Component Lifecycle in Functional Components (8 minutes)
[Slide: "Functional Components Have a Lifecycle Too" — diagram showing Mount, Update, Unmount with arrows, and the corresponding useEffect patterns for each phase]
"You may have heard about lifecycle methods from class-based React — componentDidMount, componentDidUpdate, componentWillUnmount. Functional components don't have those methods, but they absolutely have a lifecycle, and useEffect is how we tap into it.
I want to map these out clearly because understanding this will save you from a lot of confusing bugs."
[Slide: Code example showing all three lifecycle phases with useEffect: (1) empty dependency array for mount only, (2) specific dependency for update, (3) return function inside useEffect for unmount cleanup]
"useEffect with an empty dependency array runs once after the component first mounts — use this for initial data fetching, setting up subscriptions, anything you want to happen once at the start. useEffect with values in the dependency array runs after mount and then again whenever any of those values change — this is your componentDidUpdate equivalent. And the function you return from useEffect is your cleanup, it runs when the component unmounts — use this to clear timers, cancel fetch requests, or remove event listeners."
[Slide: "The Rules of useEffect Dependencies" — bullet points: include every value from the component scope that the effect uses, don't lie about dependencies, if effect re-runs too often move logic inside the effect or use useCallback, if empty array feels wrong it probably is]
"The dependency array is where most students run into trouble. The rule is simple but important — if your effect uses a value from the component, that value goes in the dependency array. If you lie to React by leaving something out, you'll get bugs where your effect runs with stale data. React's linter plugin will warn you about this and you should listen to it."
[Slide: "A Real Lifecycle Example — Timer Component" — code showing a component that starts a setInterval on mount, updates a count state every second, and critically clears the interval in the cleanup function on unmount]
"Here's a practical example. We start an interval on mount, we clear it on unmount. If we didn't return that cleanup function, the interval would keep running even after the component is gone, causing memory leaks and errors. Cleanup functions are not optional when you're working with subscriptions, timers, or event listeners."

SECTION 6: Putting It All Together — Live Code Walkthrough (5 minutes)
[Slide: "Putting It All Together" — diagram of a small app: a UserContext at the top, a login form using controlled components, a custom useForm hook, and a profile component consuming context]
"I want to spend the last few minutes showing you how everything we talked about today connects in a real app structure.
We have a UserContext that holds the logged-in user and is provided at the app level. Our login form uses controlled components managed by a custom useForm hook that handles the onChange logic for us. When the form submits, we call a login function from UserContext. Our profile page consumes UserContext directly with useContext to show the user's name. And if we had refs needed anywhere, like auto-focusing the email input on page load, we'd add a useRef for that.
Each concept does its own job. State lives where it belongs. Logic is reusable. Components stay clean."

WRAP-UP AND Q&A (5 minutes)
[Slide: "Today's Key Takeaways" — six bullet points: (1) Controlled components tie input values to state, (2) useRef accesses DOM elements and stores mutable values without re-renders, (3) Context API solves prop drilling — create, provide, consume, (4) Custom hooks share logic not state — always start with 'use', (5) useEffect maps to mount/update/unmount — always clean up side effects, (6) Controlled forms + Context + custom hooks are the foundation of real React apps]
"Let me recap what we covered today. Controlled components put React in charge of your form data. useRef gives you a way to touch the DOM and store values outside of state. Context solves the prop drilling problem and lets you share state across your entire component tree. Custom hooks let you pull reusable stateful logic into its own function. And useEffect's dependency array and cleanup function are how functional components manage their full lifecycle.
These are not beginner topics — this is the real, production-level way React applications are built. If any of this felt confusing, that's normal. The best way to make it click is to build something with it.
Any questions before we wrap up?"

---

## INSTRUCTOR NOTES

**Missing:** `useReducer` — a significant hook for managing complex state that belongs in this session before students reach for Redux. It is the natural next step after `useState` for state with multiple sub-values or complex update logic, and its absence means students will jump straight to Redux when they hit state complexity.

**Unnecessary/Too Advanced:** Nothing to remove. The coverage of controlled forms, `useRef`, Context, and custom hooks is all immediately practical.

**Density:** This file covers six major topics (controlled forms, `useRef`, Context API, custom hooks, and component lifecycle mapping). The Context API section is the most conceptually new — the create/provide/consume pattern needs to be walked through slowly. Custom hooks are a highlight of this session and deserve the time they get.
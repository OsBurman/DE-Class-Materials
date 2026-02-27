SLIDE 1 — Title Slide
Content: "React Hooks: useState, useEffect, and Managing Your Component's World" | Your name | Date
Script:
"Alright everyone, let's get started. Today is one of those lessons where things are really going to click for you — or at least start to click, because this is foundational stuff. Everything we cover today is something you will use in virtually every React project you ever build. We're talking about state, side effects, and how your component interacts with the world outside itself.
By the end of this hour, you'll be able to manage data that changes inside a component, respond to things happening in the browser, and understand why React does some things that might seem a little weird at first. Let's dive in."

SLIDE 2 — What Are Hooks?
Content: Definition of Hooks, bullet points: "Functions that let you 'hook into' React features", "Always start with 'use'", "Only call at the top level of a component", "Only call inside React function components"
Script:
"Before we get into the specific hooks, let's just anchor on what a Hook actually is. A Hook is simply a function that lets you tap into React's built-in features — things like state and lifecycle — from inside a function component.
You've probably seen them already. They always start with the word 'use' — useState, useEffect, useRef, and so on. React enforces two hard rules about hooks that you need to burn into your memory right now.
First: only call hooks at the top level of your component. Not inside an if statement, not inside a for loop, not inside a nested function. Always at the top.
Second: only call hooks inside React function components or custom hooks. Not in regular JavaScript functions.
If you break these rules, React will throw an error and your app will break. There's actually a lint rule called eslint-plugin-react-hooks that will catch these mistakes for you, and I strongly recommend using it. But understanding why these rules exist will help too — React relies on the order in which hooks are called to keep track of state between renders. If you put a hook inside an if statement, that order can change, and React loses track of which state belongs to which hook."

SLIDE 3 — What Is State?
Content: Definition — "State is data that belongs to a component and can change over time", diagram showing: User Interaction → State Changes → Component Re-renders → Updated UI
Script:
"Let's talk about state. State is just data that lives inside your component and is allowed to change. When state changes, React re-renders the component — meaning it calls your function again and rebuilds the UI with the new data.
Think about a simple counter. The number on screen needs to go up when you click a button. That number is state. Or think about a form — the text someone is typing into an input field is state. A modal that's open or closed — that's state too, just a boolean.
The key insight here is that regular JavaScript variables don't work for this. If I write let count = 0 and then do count = count + 1 when a button is clicked, nothing happens on screen. React has no idea that variable changed. State is React's mechanism for saying 'this data matters, watch it, and when it changes, redraw the component.'"

SLIDE 4 — Introducing useState
Content: Syntax breakdown — const [value, setValue] = useState(initialValue), labeled diagram showing: "current state value" | "setter function" | "initial value"
Script:
"So let's look at useState. When you call it, you pass in an initial value — whatever you want the state to start as. It returns an array with exactly two things: the current value of that state, and a function to update it.
We use array destructuring to pull those two things out and give them names. By convention, if your state variable is called count, your setter is called setCount. If it's called isOpen, the setter is setIsOpen. You don't have to follow this convention, but you really should — every React developer in the world will understand your code instantly if you do.
Let me show you this live."
[Live Code — Basic Counter]
jsximport { useState } from 'react';

function Counter() {
  const [count, setCount] = useState(0);

  return (
    <div>
      <p>Count: {count}</p>
      <button onClick={() => setCount(count + 1)}>Increment</button>
    </div>
  );
}
"There it is. The initial value is 0. Every time we click the button, we call setCount with the new value, React re-renders the component, and the new count shows up on screen. Simple as that.
Now let me show you what happens if we try to do this with a regular variable."
[Live Code — Broken Version]
jsxfunction BrokenCounter() {
  let count = 0;
  return (
    <div>
      <p>Count: {count}</p>
      <button onClick={() => { count = count + 1; console.log(count); }}>Increment</button>
    </div>
  );
}
"You can see in the console that the value is incrementing — but the screen never updates. React doesn't know anything changed. This is why we need state."

SLIDE 5 — useState: Important Details
Content: Three key points: "State updates are asynchronous", "Use functional updates when new state depends on old state", "State can hold any type: number, string, boolean, array, object"
Script:
"There are a few nuances about useState that trip people up, so let's address them now.
First: state updates are asynchronous. When you call setCount, React doesn't update the value immediately in that same execution. It schedules the update and re-renders on its next pass. This means if you call setCount and then immediately try to read count, you'll still see the old value. Keep that in mind.
Second: when your new state depends on the previous state, use the functional form of the setter. Instead of setCount(count + 1), write setCount(prevCount => prevCount + 1). This is especially important if you're calling the setter multiple times in a row, or inside async code, because it guarantees you're working with the most current value."
[Live Code — Functional Update]
jsx// Instead of this:
setCount(count + 1);

// Do this when new state depends on old:
setCount(prevCount => prevCount + 1);
"Third: state can hold anything. Numbers, strings, booleans, objects, arrays — all fair game. Let me show you a quick example with a boolean for toggling something visible."
[Live Code — Boolean State]
jsxfunction Toggle() {
  const [isVisible, setIsVisible] = useState(false);

  return (
    <div>
      <button onClick={() => setIsVisible(prev => !prev)}>
        {isVisible ? 'Hide' : 'Show'}
      </button>
      {isVisible && <p>Now you see me!</p>}
    </div>
  );
}

SLIDE 6 — Event Handling in React
Content: Comparison table — HTML events vs React events: onclick vs onClick, onchange vs onChange, onsubmit vs onSubmit. Note: "React uses SyntheticEvents that wrap native browser events"
Script:
"Before we go further with state, let's talk about event handling because state and events go hand in hand — you almost always update state in response to an event.
React handles events similarly to regular HTML, but with a few differences. Event names are camelCase — onClick, onChange, onSubmit, onKeyDown — rather than the all-lowercase HTML versions. And instead of passing a string, you pass a JavaScript function.
React wraps browser events in something called a SyntheticEvent. This is just a wrapper that normalizes browser differences so you don't have to worry about IE doing things differently from Chrome. For most purposes you can treat it just like a native browser event.
Let me show you the most common patterns."
[Live Code — Event Handling]
jsxfunction EventDemo() {
  const [inputValue, setInputValue] = useState('');
  const [submitted, setSubmitted] = useState('');

  const handleChange = (event) => {
    setInputValue(event.target.value);
  };

  const handleSubmit = (event) => {
    event.preventDefault();
    setSubmitted(inputValue);
    setInputValue('');
  };

  return (
    <div>
      <form onSubmit={handleSubmit}>
        <input 
          type="text" 
          value={inputValue} 
          onChange={handleChange} 
          placeholder="Type something..."
        />
        <button type="submit">Submit</button>
      </form>
      {submitted && <p>You submitted: {submitted}</p>}
    </div>
  );
}
```

"A couple of important things here. Notice `event.preventDefault()` on the form submit — without that, the form would try to do a full page reload, which is almost never what you want in a React app.

Notice also that the input has `value={inputValue}` and `onChange={handleChange}`. This is called a controlled input. The input's value is entirely driven by React state. Every keystroke fires onChange, which updates state, which causes a re-render, which puts the new value back into the input. This gives you complete control over the input at all times."

---

## SLIDE 7 — Controlled vs Uncontrolled Inputs
**Content:** Side-by-side code comparison of controlled input (value + onChange) vs uncontrolled input (ref, no onChange). Recommendation: "Prefer controlled inputs in most cases"

**Script:**

"Just a quick note here because students often ask — what's an uncontrolled input? An uncontrolled input is one where the DOM manages its own value and you read it when you need it using a ref. We'll cover refs in a future lesson. For now, stick with controlled inputs. They're more predictable, easier to validate, easier to test, and more in line with the React philosophy of the UI being a reflection of state."

---

## SLIDE 8 — Introducing useEffect
**Content:** Definition — "useEffect lets you perform side effects in function components", list of side effects: fetching data, setting up subscriptions, manually updating the DOM, timers, logging

**Script:**

"Alright, let's move on to useEffect, which is the other big hook for today. This one is a little more conceptual, so let's take a moment to understand the problem it's solving before we look at the code.

React's job is to render UI. It takes your state and props, calls your function, and produces some JSX. That process should be pure — meaning the same inputs always produce the same outputs, and it doesn't cause any side effects.

But real applications need to do things beyond just rendering. They need to fetch data from an API. They need to set up a timer. They need to subscribe to a WebSocket. They need to update the document title. They might need to directly interact with a non-React library. These are all called side effects — things that reach outside the component's pure render cycle and interact with the outside world.

useEffect is React's way of saying: 'Okay, I understand you need to do this stuff. Do it here, after the render, where it won't mess up my rendering process.'"

---

## SLIDE 9 — useEffect Syntax
**Content:** Syntax breakdown:
```
useEffect(() => {
  // your side effect here
  return () => { /* cleanup */ }; // optional
}, [dependencies]);
Labeled: "effect function", "optional cleanup function", "dependency array"
Script:
"Here's the basic shape of useEffect. You call it with two arguments. The first is a function where you put your side effect code. The second is a dependency array, which we'll get to in just a moment.
Optionally, your effect function can return a cleanup function. This is a function React will call when it needs to clean up after that effect — more on that shortly too.
Let me start with the simplest possible example."
[Live Code — Document Title Effect]
jsxfunction PageTitle() {
  const [count, setCount] = useState(0);

  useEffect(() => {
    document.title = `You clicked ${count} times`;
  });

  return (
    <button onClick={() => setCount(count + 1)}>
      Click me ({count})
    </button>
  );
}
"This updates the browser tab title every time count changes. Simple, and it works. But there's an issue with this version that we need to talk about."

SLIDE 10 — The Dependency Array
Content: Three cases with code snippets:

No dependency array → runs after every render
Empty array [] → runs once on mount
Array with values [a, b] → runs when a or b changes

Script:
"The dependency array is the most important thing to understand about useEffect, and it's the thing that trips people up most often.
When you don't pass a dependency array at all, the effect runs after every single render. Every time anything in the component changes and it re-renders, your effect fires. That's often not what you want.
When you pass an empty array, the effect runs exactly once — after the component first appears on the screen. This is what you want for things like fetching initial data or setting up a subscription once.
When you pass an array with values in it, the effect runs on the initial render and then again any time one of those values changes. React compares the current values to the previous values after each render, and if any of them changed, it runs the effect again.
Let me fix our title example to only run when count changes."
[Live Code — Dependency Array]
jsxuseEffect(() => {
  document.title = `You clicked ${count} times`;
}, [count]); // Only re-run when count changes
"Now let me show you the 'run once' pattern, which you'll use constantly for data fetching."
[Live Code — Run Once / Mount]
jsxfunction UserProfile() {
  const [user, setUser] = useState(null);

  useEffect(() => {
    fetch('https://jsonplaceholder.typicode.com/users/1')
      .then(res => res.json())
      .then(data => setUser(data));
  }, []); // Empty array: runs once when component mounts

  if (!user) return <p>Loading...</p>;
  return <p>Hello, {user.name}</p>;
}
"Empty array, runs once, fetches the user, sets state, component re-renders with the data. This is the most common data-fetching pattern you'll write."

SLIDE 11 — Common Dependency Array Mistakes
Content: Warning callout: "Everything your effect uses from the component should be in the dependency array", example of missing dependency bug, note about eslint-plugin-react-hooks catching this
Script:
"Here's a rule that will save you a lot of debugging headaches: if your effect uses any variable from your component — state, props, anything defined in the component — that variable needs to be in your dependency array. If you leave something out, your effect might be working with stale, outdated values.
The eslint-plugin-react-hooks has a rule called exhaustive-deps that will warn you when you're missing a dependency. Take those warnings seriously. They're almost always pointing to a real bug.
The reason beginners often put empty arrays everywhere is that they're trying to make the effect run only once. But if your effect actually depends on changing values, you need to include them and let React handle when to re-run. If you genuinely want to run only once and you're still referencing component values, there are patterns for that — but that's an advanced topic for another day. For now: include your dependencies honestly."

SLIDE 12 — Cleanup Functions
Content: Why cleanup matters, diagram showing: Effect Runs → Component Updates or Unmounts → Cleanup Runs → (New Effect Runs if update). Examples of things that need cleanup: setInterval, event listeners, subscriptions, fetch abort controllers
Script:
"Let's talk about cleanup. Some side effects don't just fire and forget — they leave something running in the background. A timer, an event listener, a WebSocket connection. If you don't clean these up, you get memory leaks and bugs where effects from old renders are still running and trying to update state in a component that might not even be on the screen anymore.
The cleanup function is what you return from your effect. React calls it in two situations: right before the component is removed from the screen, and right before the effect runs again due to a dependency change.
Let me show you a timer example that absolutely needs cleanup."
[Live Code — Cleanup with setInterval]
jsxfunction Timer() {
  const [seconds, setSeconds] = useState(0);

  useEffect(() => {
    const intervalId = setInterval(() => {
      setSeconds(prev => prev + 1);
    }, 1000);

    // Cleanup: clear the interval when component unmounts
    // or before this effect runs again
    return () => {
      clearInterval(intervalId);
    };
  }, []); // Empty array: set up once

  return <p>Seconds elapsed: {seconds}</p>;
}
"Without that return function, if this component unmounts and remounts, you'd have two intervals running. Then three. Then four. The numbers would go crazy and you'd have a memory leak. The cleanup function prevents that by clearing the old interval before setting up a new one.
Let me also show you cleanup for a window event listener."
[Live Code — Cleanup with Event Listener]
jsxfunction WindowSize() {
  const [width, setWidth] = useState(window.innerWidth);

  useEffect(() => {
    const handleResize = () => setWidth(window.innerWidth);
    
    window.addEventListener('resize', handleResize);

    return () => {
      window.removeEventListener('resize', handleResize);
    };
  }, []);

  return <p>Window width: {width}px</p>;
}
"Same pattern — add the listener, return a function that removes it. Clean, simple, no leaks."

SLIDE 13 — Putting It All Together
Content: Diagram showing a component that uses both useState and useEffect together, showing the data flow: Initial Render → Effect Runs → State Updated → Re-render → Effect Runs Again (if deps changed)
Script:
"Let me now put everything together in one realistic component so you can see how useState and useEffect work in tandem. We'll build a small component that lets you search for a GitHub user by username."
[Live Code — Combined useState + useEffect]
jsxfunction GitHubUser() {
  const [username, setUsername] = useState('');
  const [userData, setUserData] = useState(null);
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState(null);

  const handleSearch = (event) => {
    event.preventDefault();
    setUsername(event.target.elements.username.value);
  };

  useEffect(() => {
    if (!username) return; // Don't fetch if empty

    setIsLoading(true);
    setError(null);

    fetch(`https://api.github.com/users/${username}`)
      .then(res => {
        if (!res.ok) throw new Error('User not found');
        return res.json();
      })
      .then(data => {
        setUserData(data);
        setIsLoading(false);
      })
      .catch(err => {
        setError(err.message);
        setIsLoading(false);
      });
  }, [username]); // Re-fetch whenever username changes

  return (
    <div>
      <form onSubmit={handleSearch}>
        <input name="username" placeholder="GitHub username" />
        <button type="submit">Search</button>
      </form>
      {isLoading && <p>Loading...</p>}
      {error && <p>Error: {error}</p>}
      {userData && <p>{userData.name} — {userData.public_repos} repos</p>}
    </div>
  );
}
"Look at everything happening here. We have multiple pieces of state — the username, the fetched data, a loading flag, and an error. We have an event handler on the form. And we have a useEffect that depends on username — so every time the user submits a new name, the effect runs and fetches fresh data.
Notice also that we're handling loading and error states, which is what real production code always does. You never just fetch and hope for the best."

SLIDE 14 — The React Component Lifecycle with Hooks
Content: Three phases mapped to useEffect patterns:

Mount (component appears) → useEffect(() => {}, [])
Update (state/props change) → useEffect(() => {}, [value])
Unmount (component removed) → cleanup function returned from useEffect

Script:
"If you've ever read about React class components, you may have heard terms like componentDidMount, componentDidUpdate, and componentWillUnmount. These were lifecycle methods. useEffect covers all three of those cases — it's a unified way to think about a component's lifetime.
When the component first appears on screen, that's mounting. useEffect with an empty array is your componentDidMount.
When state or props change and the component re-renders, that's updating. useEffect with dependencies is your componentDidUpdate.
When the component is removed from the screen, that's unmounting. The cleanup function is your componentWillUnmount.
You don't need to memorize the old class terms, but understanding these three phases will help you know which pattern to reach for."

SLIDE 15 — Common Pitfalls & Best Practices
Content: List of pitfalls with short fixes:

Missing dependencies → use the eslint rule
Infinite loops → don't set state unconditionally in an effect that depends on that state
Not cleaning up → always return cleanup for timers, listeners, subscriptions
Too much logic in one effect → split effects by concern
Overusing useEffect → many things don't need an effect at all

Script:
"Let me walk you through the most common mistakes and how to avoid them, because knowing what not to do is just as valuable as knowing what to do.
The infinite loop is probably the most common beginner mistake. It looks like this:"
[Live Code — Infinite Loop Demo]
jsx// DON'T DO THIS
useEffect(() => {
  setCount(count + 1); // sets state
}, [count]); // which triggers the effect again... forever
"You're setting state inside an effect that depends on that state. State changes, effect runs, state changes, effect runs — infinite loop. React will actually catch this and throw an error, but you'll see it. The fix is to rethink whether you even need an effect here — in many cases you don't. A lot of things beginners reach for useEffect to solve can actually be solved with event handlers or regular derived values.
Another pitfall: putting too much into one effect. If you have an effect doing three unrelated things, split it into three separate useEffect calls. Each effect should do one thing. This makes them easier to understand and easier to give the right dependencies.
And remember — not everything needs an effect. If you're computing a value from state, just compute it in the component body. If you're responding to a user action, use an event handler. useEffect is specifically for synchronizing with external systems."

SLIDE 16 — Quick Reference Summary
Content: Two-column reference card:
useState

const [val, setVal] = useState(initial)
Use for: any data that changes and drives UI
Functional update: setVal(prev => ...)

useEffect

useEffect(() => { ... return cleanup }, [deps])
No deps: runs every render
[]: runs once on mount
[a,b]: runs when a or b changes
Return a function to clean up

Script:
"Let me give you a moment to copy down this reference slide, because this is genuinely something you'll look at constantly when you're starting out. The mental model for useState is simple: data that changes, call the setter to change it. The mental model for useEffect is: code that needs to run in sync with the outside world, scoped to when specific things change.
These two hooks together will handle the majority of what you need to do in everyday React development."

SLIDE 17 — Q&A and Wrap-Up
Content: "What we covered today" recap list, teaser of what's coming next
Script:
"Alright, let's wrap up. Today you learned what hooks are and the two rules you must follow with them. You learned how to manage component state with useState — including the functional update pattern and using state with different data types. You learned how to handle events in React with onClick, onChange, and onSubmit. And you learned how to manage side effects with useEffect, including the dependency array, the three patterns it covers, and how to clean up after yourself.
The most important takeaway: state is data that belongs to your component and drives what the UI looks like. Side effects are anything your component does that reaches outside its render cycle. Those are two fundamentally different things and React gives you two different tools for them.
Before next class, I'd encourage you to build a small component from scratch — something like a todo list or a weather widget that fetches from a free API. The more you type these patterns yourself, the more naturally they'll come.
Any questions?"

---

## INSTRUCTOR NOTES

**Missing:** Nothing significant. The `useEffect` cleanup function and dependency array rules appear to be covered in the session — these are the two most critical things students must understand correctly. Confirm the common mistake of an infinite loop (missing or incorrect dependency array) is explicitly called out, as this is the #1 `useEffect` bug beginners encounter.

**Unnecessary/Too Advanced:** Nothing to remove.

**Density:** Well-paced. The controlled vs. uncontrolled input comparison is a helpful practical anchor. The `useEffect` section is inherently the most complex part — the three dependency array patterns (no array, empty array, array with values) need clear visual separation to avoid confusion.
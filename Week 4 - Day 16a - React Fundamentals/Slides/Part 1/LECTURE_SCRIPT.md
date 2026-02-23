# Week 4 - Day 16a: React Fundamentals
## Part 1 Lecture Script — Core Concepts, JSX & Components

**Total runtime:** 60 minutes
**Delivery pace:** ~165 words/minute
**Format:** Verbatim instructor script with [MM:SS–MM:SS] timing markers

---

## [00:00–02:00] Opening

Welcome to Week 4. If you just finished Week 3, you have covered a lot of ground — HTML and CSS, JavaScript fundamentals, ES6, OOP in JavaScript, asynchronous programming, and TypeScript. That was genuinely a deep foundation. And everything you've learned in those three weeks is going to be used constantly starting right now.

This week is the frontend frameworks week. And it splits into two tracks. If you're on the React track, today is Day 16a — and you're in the right room. If you're on the Angular track, that's Day 16b — same week, parallel curriculum. By the end of Week 4, both tracks arrive at the same Spring Boot backend, so don't worry — you're not missing out on anything in the other track; they solve similar problems in different ways.

Today — Day 16a — is React Fundamentals. Two hours, two parts. Part 1 this session covers the core philosophy, how React actually works in the browser, and the fundamentals of JSX and components. Part 2 covers props, lists, conditional rendering, and component composition.

Let me be direct about the scope: we are not doing state management today. No `useState`. No `useEffect`. I know some of you have seen React before and you're wondering when we get to the hooks — that's tomorrow, Day 17a. Today we're building the mental model first, because hooks only make sense once you understand what a component is.

Let's go.

---

## [02:00–05:30] What Is React?

**[Slide 2 — What Is React?]**

React is a JavaScript library for building user interfaces. That's the official description, and I want you to notice what it says and what it doesn't say.

It says "library" — not framework. React is not a full framework. It handles one thing: rendering UI. It does not include a router, an HTTP client, a form library, or a state management solution. You compose those from the ecosystem around React. That's a deliberate design decision, and it has trade-offs we'll talk about throughout this week.

React was created by Jordan Walke, an engineer at Facebook, and first deployed in Facebook's News Feed in 2011. It was open-sourced at JSConf US in 2013. It's now maintained by Meta and a massive open-source community, and it is the single most in-demand frontend skill on the job market right now. If you look at job postings — and I encourage you to look — "React" appears in more frontend job descriptions than any other technology by a wide margin.

React has three core philosophies, and understanding these will help you understand every decision React makes.

First: **declarative**. This is the word you'll see in React's documentation constantly. Declarative means you describe *what* the UI should look like for a given state — not *how* to update it. You say "the UI should look like this" and React figures out how to make the DOM match that description. The opposite of declarative is imperative — which is what you were doing in Week 3 when you called `document.getElementById` and set properties manually. You were telling the browser *how* to make a specific change step by step.

Second: **component-based**. You build encapsulated components that manage their own logic and can be composed together. A button is a component. A navigation bar is a component made of smaller components. An entire page is a component composed of sections, which are each composed of smaller components.

Third: **learn once, write anywhere**. React renders to the DOM for web apps. React Native renders to native iOS and Android. You can render React to PDFs, to email templates, to server-side HTML. The same mental model applies across all of them.

---

## [05:30–10:00] Single Page Applications

**[Slide 3 — Single Page Applications]**

Let me describe the problem that React is solving at the architectural level, because it's important context.

In the classic web — what we call Multi-Page Applications — every time you click a link, the browser sends a GET request to the server. The server renders HTML and sends back a complete HTML page. The browser tears down the existing page and loads the new one. You've seen this — the page flashes white, the scroll position resets, anything happening on the page stops. This works perfectly for simple sites. It breaks down when you're building something complex and interactive, like a social media feed or a dashboard application.

React applications are what we call Single Page Applications. The "single page" part is literal — there is one HTML file. If you look at the generated `index.html` in a React project, it's basically empty. There's a `<div id="root">` in the body and a script tag loading your JavaScript bundle. That's it. React mounts your entire application into that root div.

When a user navigates — clicks what looks like a link — JavaScript intercepts it, React swaps out the content on the page, and the URL updates using the browser's History API — no page reload. The transition is instant. From the user's perspective it feels like a native application.

Now, SPAs have trade-offs and I want to be honest about them because this is a real architectural decision you'll make in your career.

On the advantage side: fast, app-like transitions after the initial load. Reduced server load because you're only fetching data via API after that first page load, not re-rendering entire HTML pages. Rich client-side state — your application can maintain complex state across navigation without round-tripping to the server.

On the disadvantage side: you have to download a JavaScript bundle before anything renders, which makes the initial load slower than a simple HTML page. SEO can be challenging because search engine crawlers historically had trouble with JavaScript-rendered content. And if JavaScript fails — the user gets nothing.

For this reason, modern meta-frameworks like Next.js add Server-Side Rendering on top of React — you get both the rich client-side experience and a fast, SEO-friendly initial load. That's beyond this week's scope, but I mention it because you'll hear about Next.js in the industry, and it's built on top of exactly what you're learning today.

---

## [10:00–15:00] The Virtual DOM

**[Slide 4 — The Virtual DOM: Concept]**

Now I want to talk about *how* React works internally — the Virtual DOM — because once you understand this, React's behavior makes a lot more sense.

Direct DOM operations are expensive. When you call `document.createElement`, or set `innerHTML`, or call `appendChild`, you're triggering what the browser calls "reflow" and "repaint." The browser has to recalculate the layout of the page — what's where, what's affected, what changed — and then redraw the pixels. These operations are synchronous, they block the browser's main thread, and they're slow at any kind of scale.

React's solution is the Virtual DOM — a lightweight JavaScript object representation of the real DOM tree, maintained entirely in memory.

Let me break down the three-phase cycle.

Phase one: **Render.** When something in your application changes — a user clicks a button, data arrives from an API — React calls your component functions and builds a new virtual DOM tree. Not a real DOM tree — a JavaScript object tree. Fast. Cheap. Just function calls and object creation.

Phase two: **Diffing.** React takes the new virtual DOM tree and compares it against the previous virtual DOM tree. This is the reconciliation algorithm. React is smart about this — it uses heuristics to find the minimal set of actual DOM changes needed to go from the previous state to the new state.

Phase three: **Commit.** React applies only those minimal changes to the real DOM. It batches DOM mutations for efficiency. Multiple state changes in the same event handler get batched into a single commit phase.

Here's the key insight: your component functions run on every re-render. Writing JSX like `<div className="card">` is not directly touching the DOM. You're describing what you want, and React decides when and how to update the real DOM based on that description.

---

## [15:00–19:00] Reconciliation

**[Slide 5 — Reconciliation]**

Let me go a bit deeper on the reconciliation algorithm because it directly affects how you write components.

The naive approach to diffing two trees is O(n³) time complexity — for a tree with a thousand nodes, that's a billion operations. Obviously that doesn't work in practice. React uses heuristics to get this down to O(n).

Heuristic one: if the element type changes, React assumes the entire subtree is different and destroys it completely, mounting a fresh tree. If you had a `<div>` and it becomes a `<span>`, React doesn't try to convert it — it throws away the div tree and mounts a new span tree from scratch. Any component state inside that subtree is lost.

Heuristic two: for lists of elements, React compares by position unless you provide `key` props. This is the `key` prop that everyone hears React warn about. The key prop lets React track list items by identity rather than position. If item B was at position 1 and moves to position 3, React knows it's the same B — it doesn't destroy and remount it. We'll go deep on keys in Part 2.

Heuristic three: if the element type is the same, React updates the existing DOM node's props instead of replacing it. If you have `<div className="old" />` and it becomes `<div className="new" />`, React just updates the `className` attribute on the existing DOM node. Same node, updated prop.

There's one more concept worth knowing: React Fiber, which was the internal rewrite in React 16 that implemented what's called incremental rendering. The core idea is that the reconciliation work can be split into chunks and interrupted. If something more urgent comes along — like a user typing in an input — React can pause the current rendering work, handle the urgent update first, and then resume. This is what enables React 18's concurrent features. The details are deep and don't affect how you write code today, but I want you to know it exists so when you hear "React Fiber" or "concurrent mode" you have a mental model.

---

## [19:00–24:00] Project Setup

**[Slide 6 — Setting Up a React Project]**

Let's talk about creating an actual React project. If you've looked at any React tutorials from a few years ago, you've probably seen "Create React App" — `npx create-react-app my-app`. Do not use this. Create React App is deprecated. The React team officially removed it from their documentation and now recommends three options: Vite for single-page apps, and Next.js or Remix for full-stack apps.

We're using Vite today. Here's the command:

```bash
npm create vite@latest my-react-app -- --template react
cd my-react-app
npm install
npm run dev
```

Why Vite? It uses native ES modules in the browser during development and esbuild for bundling — the dev server starts in milliseconds instead of the 10–30 seconds CRA could take. In a real project, this adds up significantly.

Let me walk you through the generated file structure because understanding where things are matters.

At the root you have `index.html` — this is the single HTML file I mentioned earlier. Inside it you'll find `<div id="root"></div>` — that's where your entire React application gets mounted.

In `src/` you have `main.jsx` — this is the entry point. Let me walk through it line by line.

```jsx
import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import './index.css'
import App from './App.jsx'

createRoot(document.getElementById('root')).render(
  <StrictMode>
    <App />
  </StrictMode>,
)
```

`createRoot` takes the DOM element — the `<div id="root">` from your HTML — and tells React "this is where your application lives." Then `.render()` mounts the component tree into it.

`StrictMode` is a React development tool that activates extra warnings and intentionally runs certain things twice to help you catch bugs. It has no effect in production builds.

`App.jsx` is your root component — the component at the top of your component tree that everything else is nested inside.

If you're using TypeScript — and in your projects, you should be — the command is:
```bash
npm create vite@latest my-app -- --template react-ts
```
That gives you `.tsx` files instead of `.jsx`, with TypeScript fully configured.

---

## [24:00–28:00] What Is JSX?

**[Slide 7 — What Is JSX?]**

Now let's talk about JSX, because everything you write in React is JSX.

JSX is not HTML. I want to say that clearly because they look almost identical, and the differences are subtle but important. JSX is a syntax extension for JavaScript that allows you to write what looks like HTML markup inside your JavaScript code. But it compiles to plain JavaScript function calls.

When you write this:
```jsx
const element = <h1 className="title">Hello, World!</h1>;
```

Vite's toolchain — specifically Babel or TypeScript, depending on your setup — transforms it to this:
```js
import { jsx as _jsx } from 'react/jsx-runtime';
const element = _jsx("h1", { className: "title", children: "Hello, World!" });
```

That's what actually runs in the browser. Every JSX tag becomes a function call. The JSX is syntactic sugar to make that code readable and maintainable.

In older React — before React 17 — it compiled to `React.createElement()` calls, which is why every file with JSX had to start with `import React from 'react'`. React 17 introduced the "new JSX transform" which is what you see in the example — the automatic import from `'react/jsx-runtime'` happens behind the scenes. You no longer need the manual import. That's why modern React files don't start with `import React from 'react'` anymore. You'll still see it in older code, and it still works — it's just not required.

Could you write React without JSX? Yes. No one does, but you could. Instead of `<div className="app">Hello</div>` you'd write `React.createElement("div", { className: "app" }, "Hello")`. Once you see the compiled output, JSX makes complete sense as a productivity tool. It's a description language for your UI, and the toolchain handles the translation.

---

## [28:00–34:00] JSX Rules

**[Slide 8 — JSX Syntax Rules]**

Let me go through the rules of JSX — where it differs from HTML. These are the things that will trip you up when you first start writing React, so let's call them out explicitly.

**Rule one: You must return a single root element.** Every component can only return one top-level element. If you try to return two sibling elements with nothing wrapping them, you get a syntax error — JSX can't compile to a single `React.createElement` call if there are two roots.

Your options are: wrap in a `div` — which adds a real DOM node — or use a Fragment. A Fragment looks like empty angle brackets: `<>...</>`. It renders no DOM node — it's just a grouping mechanism for JSX. If you need to give the Fragment a key — for example when using it in a list — you can use the verbose form: `<React.Fragment key={id}>...</React.Fragment>`.

**Rule two: All tags must be closed.** In HTML, `<br>` and `<img>` don't need closing tags. In JSX, they do. Every single tag must close, either with a closing tag or self-closing with a slash: `<br />`, `<img />`, `<input />`. This will be a syntax error until you get used to it.

**Rule three: `className`, not `class`.** The HTML `class` attribute becomes `className` in JSX. Why? Because `class` is a reserved word in JavaScript. JSX is JavaScript — so you can't use `class` as an attribute name. Same with `for` on labels — it becomes `htmlFor`.

**Rule four: camelCase for most attributes.** `onclick` in HTML becomes `onClick` in JSX. `onchange` becomes `onChange`. `tabindex` becomes `tabIndex`. The general pattern is camelCase. There are a couple of exceptions — `aria-` and `data-` attributes stay hyphenated — but for HTML event handlers and most attributes, camelCase is the rule.

**Rule five: inline styles are objects, not strings.** In HTML you can write `style="color: red; font-size: 16px"`. In JSX, `style` takes a JavaScript object with camelCase property names: `style={{ color: 'red', fontSize: '16px' }}`. Those double curly braces look odd at first — the outer braces say "I'm evaluating a JavaScript expression" and the inner braces are the object literal. `font-size` in CSS becomes `fontSize` — camelCase.

---

## [34:00–39:00] JSX Expressions

**[Slide 9 — JSX Expressions]**

The other critical piece of JSX is embedding JavaScript inside it. You do this with curly braces `{}`. Anything inside curly braces is a JavaScript expression — it gets evaluated and the result gets rendered.

Variables, calculations, function calls, ternary expressions, method calls — all of these work. Let me show you:

```jsx
const name = "Alice";
const isLoggedIn = true;

function UserCard() {
  const currentTime = new Date().toLocaleTimeString();
  return (
    <div>
      <h2>Hello, {name}!</h2>
      <p>2 + 2 = {2 + 2}</p>
      <p>Uppercase: {name.toUpperCase()}</p>
      <p>Time: {currentTime}</p>
      <p>{isLoggedIn ? "Welcome back!" : "Please log in"}</p>
    </div>
  );
}
```

The comments in JSX have their own syntax — you need curly braces around block comments: `{/* this is a comment */}`. Regular JavaScript `//` and `/* */` don't work outside curly braces in JSX.

What can go in curly braces? **Expressions only.** An expression is anything that produces a value. Variables, function calls, ternaries, arithmetic, method calls — all expressions.

Statements cannot go in curly braces. `if`, `for`, `while` — these are statements. They don't produce a value, so they can't go directly inside JSX. You can use a ternary instead of if, and you can use `.map()` instead of for.

One very important behavior: `null`, `undefined`, `false`, and `true` render as nothing — they produce no output. This is intentional and you'll use it for conditional rendering. But `0` renders as the text "0" — that's a common gotcha we'll cover in detail in Part 2.

---

## [39:00–44:00] Functional Components

**[Slide 11 — Functional Components]**

Now let's talk about the thing that makes React *React*: components.

A React functional component is a JavaScript function. That's it. It accepts an argument — the props object — and it returns JSX. Two rules for the name: it must start with a capital letter, and by convention we use PascalCase — `UserCard`, not `userCard` or `user_card`.

The capital letter rule is not just convention — it's how React tells custom components apart from HTML elements. When React sees `<UserCard />` with a capital letter, it calls the `UserCard` function. When it sees `<div>` with a lowercase letter, it creates a native DOM element. If you accidentally write `<userCard />`, React will try to create an HTML element called `usercard` — not what you want.

Here's the basic anatomy:

```jsx
function Greeting({ name }) {
  return <h1>Hello, {name}!</h1>;
}
```

That's a complete, valid, functional component. Takes a `name` prop, returns an `h1` with the name embedded. You can use this in another component like `<Greeting name="Alice" />`.

You can also use arrow function syntax — both are equally valid:

```jsx
const Greeting = ({ name }) => <h1>Hello, {name}!</h1>;
```

For components with more logic, you put the logic before the return statement:

```jsx
function UserCard({ user }) {
  const initials = user.name
    .split(' ')
    .map(word => word[0])
    .join('');
  const joinDate = new Date(user.createdAt).toLocaleDateString();
  
  return (
    <div className="card">
      <div className="avatar">{initials}</div>
      <h2>{user.name}</h2>
      <p>Member since {joinDate}</p>
    </div>
  );
}
```

Notice that the logic — computing initials, formatting the date — happens in plain JavaScript before the return. React doesn't care what you do before the return as long as you return valid JSX.

---

## [44:00–49:30] Class Components (Historical Context)

**[Slide 12 — Class Components]**

Before we move on, I want to show you class components — not because you'll write them, but because you will encounter them in existing codebases, and they appear in interviews.

Before React 16.8 — released in February 2019 — if you wanted local state in a component or needed to respond to lifecycle events like "the component mounted" or "the component is about to unmount," you had to use a class component. Here's what one looks like:

```jsx
class Counter extends React.Component {
  constructor(props) {
    super(props);
    this.state = { count: 0 };
    this.handleClick = this.handleClick.bind(this);
  }
  
  componentDidMount() {
    document.title = `Count: ${this.state.count}`;
  }
  
  componentDidUpdate() {
    document.title = `Count: ${this.state.count}`;
  }
  
  handleClick() {
    this.setState({ count: this.state.count + 1 });
  }
  
  render() {
    return (
      <div>
        <p>Count: {this.state.count}</p>
        <button onClick={this.handleClick}>Increment</button>
      </div>
    );
  }
}
```

This is a lot of boilerplate. You have to call `super(props)` in the constructor — if you forget that, your component breaks. You have to manually bind event handlers in the constructor, otherwise `this` is undefined when the handler runs. You have separate lifecycle methods — `componentDidMount`, `componentDidUpdate`, `componentWillUnmount` — for different phases of the component's life.

React 16.8 introduced Hooks — `useState`, `useEffect`, and others — which let functional components do everything class components could do, with far less boilerplate. We cover Hooks tomorrow.

The same Counter component in modern React looks like:
```jsx
function Counter() {
  const [count, setCount] = useState(0);
  
  useEffect(() => {
    document.title = `Count: ${count}`;
  }, [count]);
  
  return (
    <div>
      <p>Count: {count}</p>
      <button onClick={() => setCount(count + 1)}>Increment</button>
    </div>
  );
}
```

Half the lines. No `this`. No binding. No constructor.

**Two reasons you still need to know class components:** One, legacy code. You will open a codebase and find class components — you need to be able to read them. Two, there is currently one thing class components can do that functional components cannot: Error Boundaries — the React mechanism for catching errors in the component tree. Error Boundaries currently require class components. We cover them on Day 19a.

New code you write: always functional components.

---

## [49:30–54:00] Component File Structure and Conventions

**[Slide 13 — Component File Structure]**

Let's talk about how to organize component files, because this affects how readable and maintainable your codebase is.

The standard convention: one component per file. Your file name matches your component name: `UserCard.jsx` contains the `UserCard` component. This makes finding components trivial — if you're looking for `ProductCard`, you open `ProductCard.jsx`.

For larger components, the folder-per-component pattern is common:
```
components/
├── UserCard/
│   ├── UserCard.jsx      ← the component
│   ├── UserCard.module.css  ← scoped styles
│   └── index.js          ← re-exports UserCard for clean imports
```

The `index.js` file typically just contains `export { default } from './UserCard'` — it lets you import as `import UserCard from './components/UserCard'` instead of `import UserCard from './components/UserCard/UserCard'`.

For exports, there are two patterns. Default exports — `export default function UserCard()` — are the most common for components. They allow the importer to name the import anything. Named exports — `export function UserCard()` — require the importer to use the same name but allow multiple exports from one file.

For components, default exports are more common. For utility functions and constants, named exports are common — especially when a file exports multiple things.

Inside a component file, there's a conventional order. Imports first — React imports, then library imports, then local imports, then styles. Then any constants or helper functions defined outside the component — keep these outside to avoid recreating them on every render. Then the component itself. Then the export at the bottom, or inline with the function declaration.

---

## [54:00–58:00] The Component Tree

**[Slide 15 — The Component Tree]**

I want to close Part 1 by zooming out to the big picture: the component tree.

A React application is a tree of components. Your `App` component is the root. It contains other components, which contain other components, which contain the individual elements that show up on screen.

```
App
├── Header
│   ├── Logo
│   └── Nav
│       ├── NavLink
│       ├── NavLink
│       └── NavLink
├── MainContent
│   └── ProductGrid
│       ├── ProductCard
│       ├── ProductCard
│       └── ProductCard
└── Footer
```

Data flows **downward** through this tree — a parent passes data to children via props. Events flow **upward** — a child notifies its parent by calling a callback function that the parent passed down as a prop. This is the unidirectional data flow I mentioned earlier, and we'll see it in concrete code in Part 2.

A few design principles worth knowing now, even if they take time to internalize.

A component should do one thing. If it's getting large — more than 100–150 lines — it's probably doing too much. Split it.

Reuse is a signal to extract. If you're writing the same JSX in two places, that's a component waiting to be created.

The depth and structure of your component tree is a design decision. There's no one correct answer — but your decisions directly affect how easy your code is to maintain, test, and reason about.

Before we break: install the React Developer Tools browser extension. It's published by Meta and available for Chrome, Firefox, and Edge. With it, you can inspect your component tree in the browser, see the props each component is receiving, and eventually inspect state values. I'll reference it during the exercises.

---

## [58:00–60:00] Part 1 Summary

**[Slide 17 — Part 1 Summary]**

Let me summarize Part 1 in under two minutes.

React is a declarative, component-based UI library. SPAs use one HTML file and JavaScript to handle all navigation and content updates. The Virtual DOM is a JavaScript representation of the DOM — React diffs two VDOMs and applies minimal real DOM changes in a commit phase.

JSX is syntactic sugar for `React.createElement`. The rules: single root element or Fragment, all tags closed, `className` not `class`, `htmlFor` not `for`, camelCase event handlers, inline styles as objects. Curly braces embed JavaScript expressions — not statements.

Functional components are functions that accept props and return JSX. Capital letter names are required — that's how React distinguishes components from HTML elements. Class components are legacy — you'll read them, but you write functional components.

The component tree is the architecture of a React application. Data flows down via props. Events flow up via callback props. A component should do one thing.

In Part 2, we make components actually talk to each other. Props, lists, conditional rendering, composition. Take a short break, and we'll pick back up in a few minutes.

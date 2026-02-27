[SLIDE 1: Title Slide]
"React Advanced Patterns & Deployment"
Subtitle: "Writing scalable, performant, production-ready React"
Include: your name, date, class number

SCRIPT:
"Good morning everyone — welcome back. Over the past few sessions we've built up a solid foundation in React. You know how components work, you understand hooks, and you've been writing real applications. Today we're going to take all of that and level it up significantly.
By the end of this hour, you will understand how to compose components in ways that make your code genuinely reusable and flexible. You'll know how to stop your app from doing unnecessary work. You'll understand how to split your code so users aren't downloading a massive JavaScript file all at once. And you'll know how to actually get your app onto the internet in a professional way.
This is the stuff that separates someone who can build a React app from someone who can build a React app well. Let's get into it."

SECTION 1: COMPONENT COMPOSITION PATTERNS (10 minutes)

[SLIDE 2: What Is Component Composition?]
Title: "Composition Over Inheritance"
Content:

React favors composing components rather than extending them
Small, focused components assembled into larger ones
The "children" prop is your foundation
Show a simple diagram: small blocks combining into a larger block


SCRIPT:
"The first thing we're covering today is component composition — and specifically, some patterns you'll use constantly in real codebases.
The core idea is this: instead of building one giant component that does everything, you build small focused pieces and compose them together. React was designed for this. The children prop is your most basic tool here — it lets a parent component render whatever you pass into it, which makes components incredibly flexible.
But there are two specific composition patterns I want to teach you today that go deeper than just passing children around."

[SLIDE 3: Render Props Pattern]
Title: "Render Props"
Content:

A component accepts a function as a prop
That function returns JSX
The component calls that function, passing it data
Code example:

jsx<DataFetcher url="/api/users" render={(users) => (
  <UserList users={users} />
)} />

Why: share stateful logic without HOCs or duplication


SCRIPT:
"The first pattern is called Render Props. The idea is simple but powerful: instead of a component deciding what to render, you pass it a function as a prop, and that function decides what to render. The component calls your function and passes it whatever data or state it controls.
Look at this example. DataFetcher handles all the fetching logic — loading states, errors, the actual data. But it doesn't decide what to do with that data. It calls your render function and passes the data to you. You decide what to render. This means you can reuse DataFetcher with any UI you want.
Where you'll see this in the wild: libraries like React Router and Formik have historically used this pattern. It's also useful any time you want to share behavior — like mouse tracking, scroll position, or data fetching — without coupling it to a specific UI."

[SLIDE 4: Compound Components]
Title: "Compound Components"
Content:

A set of components that work together, sharing implicit state
The parent manages state, children access it via Context
Familiar examples: HTML <select> and <option>, <table> and <tr>
Code example showing a custom Select or Tabs component:

jsx<Tabs>
  <Tabs.List>
    <Tabs.Tab>Overview</Tabs.Tab>
    <Tabs.Tab>Details</Tabs.Tab>
  </Tabs.List>
  <Tabs.Panel>Content A</Tabs.Panel>
  <Tabs.Panel>Content B</Tabs.Panel>
</Tabs>

Clean API, flexible, no prop drilling


SCRIPT:
"The second pattern is Compound Components, and this one is especially elegant. The idea is that a group of components work together as a family. The parent component manages all the shared state. The child components consume that state through React Context without you having to pass props manually through every level.
Think about how native HTML works — a select element and its option elements work together. The option doesn't need you to pass it a prop saying 'which option is currently selected' — it just knows, because it's part of the select. Compound components replicate that idea in React.
In this Tabs example, Tabs.List, Tabs.Tab, and Tabs.Panel are all separate components, but they all share the state of which tab is currently active through context. The consumer of this component gets a really clean, readable API. No messy prop drilling, no configuration objects, just components that read naturally.
You'll build these for things like Tabs, Accordions, Modals, Dropdowns — any UI pattern where multiple pieces need to coordinate."

SECTION 2: PERFORMANCE OPTIMIZATION (10 minutes)

[SLIDE 5: Why Performance Matters in React]
Title: "Understanding React Re-renders"
Content:

React re-renders a component whenever its state or props change
Parent re-renders cause all children to re-render by default
Most re-renders are fine — don't over-optimize
When it hurts: large lists, expensive calculations, frequent updates
Diagram: parent component triggering a cascade of child re-renders


SCRIPT:
"Before we talk about performance optimization tools, I want to make sure you understand the problem we're solving. By default, when a React component re-renders — because its state changed — all of its children re-render too. And their children re-render. It cascades down the tree.
Most of the time, this is totally fine. React is fast and re-renders are cheap. I want to say that clearly: do not reach for performance optimization tools prematurely. The first rule of optimization is to measure first, optimize second. But there are real scenarios — large lists, heavy computations, components that render dozens of times per second — where you need to be deliberate."

[SLIDE 6: React.memo]
Title: "React.memo — Skipping Unnecessary Re-renders"
Content:

Wraps a component — if props haven't changed, skip the re-render
Shallow comparison by default
Code:

jsxconst UserCard = React.memo(({ name, email }) => {
  return <div>{name} - {email}</div>;
});

When to use: components that render often but receive the same props
Warning: doesn't help if props are new object/array references each render


SCRIPT:
"React.memo is a higher-order component that wraps your component and tells React: only re-render this if the props actually changed. It does a shallow comparison — meaning it checks if each prop value is the same as the previous render.
There's an important gotcha here. If you pass an object or an array as a prop, even if the content is the same, if it's a new object created during the parent's render, React.memo will see it as a changed prop and re-render anyway. This is where our next two hooks come in."

[SLIDE 7: useMemo and useCallback]
Title: "useMemo & useCallback"
Content:

useMemo — memoizes a computed value. Recalculates only when dependencies change.
useCallback — memoizes a function reference. New function only when dependencies change.

jsx// useMemo: expensive calculation
const sortedList = useMemo(() => {
  return [...items].sort((a, b) => a.name.localeCompare(b.name));
}, [items]);

// useCallback: stable function reference for child components
const handleClick = useCallback((id) => {
  deleteItem(id);
}, [deleteItem]);

Both take a dependency array — same rules as useEffect
Don't use these everywhere — they have overhead too


SCRIPT:
"useMemo lets you memoize a computed value. The function only re-runs when the values in the dependency array change. This is perfect for expensive calculations — sorting a large list, filtering thousands of records, processing data — you don't want to redo that work on every single render.
useCallback does the same thing but for functions. It gives you a stable function reference across renders. Why does that matter? Because if you're passing a function as a prop to a memoized child component, and that function is recreated on every parent render, your React.memo is useless — the child sees a 'new' function and re-renders anyway. useCallback solves that.
The key point I want you to take away: these are tools, not defaults. Wrapping everything in useMemo and useCallback adds its own overhead and makes your code harder to read. Use them when you've identified a specific performance issue, not just in case."

SECTION 3: CODE SPLITTING, LAZY LOADING & SUSPENSE (10 minutes)

[SLIDE 8: The Bundle Problem]
Title: "Why Code Splitting Matters"
Content:

By default, your entire app ships as one JavaScript bundle
Users must download, parse, and execute ALL of it before seeing anything
A 2MB bundle on a slow connection = users leaving
Solution: only send the code the user actually needs right now
Diagram: single large bundle vs. multiple smaller chunks loaded on demand


SCRIPT:
"When you build a React app, by default everything gets bundled into a single JavaScript file. Every component, every library, every page — all of it. The user has to download all of that before your app becomes interactive. For a small app this is fine. For a large app with many pages and features, this creates a terrible first load experience.
Code splitting solves this by breaking your bundle into smaller chunks that load on demand. The user only downloads the code they need for the current page. When they navigate to another page, that chunk loads then. This dramatically improves initial load time."

[SLIDE 9: React.lazy and Suspense]
Title: "Dynamic Imports with React.lazy"
Content:

React.lazy() lets you import a component dynamically
Suspense displays a fallback while the component loads

jsximport { lazy, Suspense } from 'react';

const Dashboard = lazy(() => import('./pages/Dashboard'));
const Settings = lazy(() => import('./pages/Settings'));

function App() {
  return (
    <Suspense fallback={<LoadingSpinner />}>
      <Routes>
        <Route path="/dashboard" element={<Dashboard />} />
        <Route path="/settings" element={<Settings />} />
      </Routes>
    </Suspense>
  );
}

Each page becomes its own bundle chunk
Only loads when the route is visited


SCRIPT:
"React gives us two tools for this: React.lazy and Suspense. React.lazy lets you import a component dynamically — instead of importing it at the top of your file where it gets bundled in immediately, you import it as a function that returns a promise. The bundler sees this and creates a separate chunk for that component.
Suspense wraps those lazy components and shows a fallback UI — a spinner, a skeleton screen, whatever you want — while the chunk is being downloaded.
The most common use case is route-based splitting, which you can see here. Each page component becomes its own chunk. Your home page doesn't ship with the code for your admin dashboard. Your users loading the marketing page never download the code for the settings page. This is one of the highest-impact performance wins you can implement."

[SLIDE 10: Concurrent Features & Suspense for Data]
Title: "Concurrent React & Suspense"
Content:

React 18 introduced concurrent rendering
React can pause, interrupt, and resume rendering work
useTransition — mark a state update as non-urgent
useDeferredValue — defer re-rendering an expensive part of the UI

jsxconst [isPending, startTransition] = useTransition();

startTransition(() => {
  setSearchQuery(value); // non-urgent update
});
```
- *Keeps the UI responsive during heavy updates*
- *Suspense will eventually work for data fetching too (frameworks like Next.js use this today)*

---

**SCRIPT:**

"React 18 introduced concurrent features, and this is worth understanding at a conceptual level. Before React 18, rendering was synchronous — once React started rendering, it couldn't stop until it was done. If rendering took 100ms, your UI was frozen for 100ms.

Concurrent React can pause rendering, work on something more urgent, and come back to finish. Two hooks expose this to you. `useTransition` lets you mark a state update as non-urgent — like filtering a large list as a user types. The typing stays instant and snappy, while the list update can happen slightly delayed. `useDeferredValue` does something similar, letting you defer the re-render of an expensive part of the UI.

Suspense is also expanding beyond just lazy loading — frameworks like Next.js use Suspense for streaming data from the server, so parts of your page can render and display before all the data is ready. This is the direction React is heading, and it's worth being aware of."

---

## SECTION 4: REACT DEVTOOLS (5 minutes)

---

**[SLIDE 11: React DevTools]**
*Title: "React DevTools — Your Debugging Superpower"*
*Content:*
- *Browser extension for Chrome and Firefox*
- *Two main panels: Components and Profiler*
- *Components panel: inspect component tree, view/edit props and state in real time*
- *Profiler panel: record renders, see what re-rendered and why, measure timing*
- *Screenshot or diagram of the DevTools interface*

---

**SCRIPT:**

"Before we move to deployment, let's talk about React DevTools — this is a browser extension you should have installed right now if you don't already. It adds two tabs to your browser's developer tools.

The Components tab gives you a live view of your entire React component tree. You can click any component, see its props and state, and even edit them live in the browser. This is invaluable for debugging.

The Profiler tab is where performance debugging happens. You hit record, interact with your app, stop recording, and it shows you a flame graph of every render that occurred, how long each component took to render, and crucially — it tells you WHY each component re-rendered. Did it re-render because its own state changed? Because its parent re-rendered? Because a context value changed? This is how you find performance problems. You don't guess, you measure. Open DevTools, profile, find the actual bottleneck, then fix it."

---

## SECTION 5: BUILDING FOR PRODUCTION (5 minutes)

---

**[SLIDE 12: Development vs. Production Builds]**
*Title: "Building for Production"*
*Content:*
- *Development build: unminified, includes warnings, slower*
- *Production build: minified, optimized, warnings stripped*
- *`npm run build` creates a `/build` or `/dist` folder*
- *What happens during the build: minification, tree-shaking, code splitting, asset hashing*
- *Never deploy a development build*

---

**SCRIPT:**

"When you run your app with `npm start` you're running a development server. It's verbose, it includes helpful warnings, and it's not optimized at all. That is not what you deploy.

Running `npm run build` — whether you're using Vite, Create React App, or another tool — runs your code through a production build pipeline. It minifies your JavaScript, removing all the extra whitespace and renaming variables to single letters to reduce file size. It tree-shakes unused code — if you imported a library function and never used it, it gets removed. It applies all your code splitting. It also hashes your asset filenames, so that when you deploy a new version, users get the new files and not a cached old version.

The output is a folder — usually called `build` or `dist` — containing static files. HTML, CSS, and JavaScript. That's it. That's what you're deploying."

---

## SECTION 6: DEPLOYMENT STRATEGIES (8 minutes)

---

**[SLIDE 13: Where Do React Apps Live?]**
*Title: "Deployment Options"*
*Content:*
- *React builds are static files — they can be served from anywhere*
- *Static hosting (simplest): Netlify, Vercel, GitHub Pages*
- *CDN-backed object storage: AWS S3 + CloudFront, Google Cloud Storage*
- *Container-based: Docker + nginx*
- *Server-side rendering: Next.js on Vercel, Node servers*
- *Table comparing: complexity, cost, use case*

---

**SCRIPT:**

"Here's something that surprises a lot of students: a React build is just static files. There's no server running React. The server just hands the browser your HTML, CSS, and JS files, and the browser runs everything. This means you have a lot of deployment options.

For most projects, especially early in your career, static hosting platforms are the answer. Netlify and Vercel are the most popular. They're free for personal projects, they connect to your GitHub repository, and every time you push to your main branch, they automatically build and deploy your app. The entire process takes about two minutes once you've connected them.

For larger production applications, you'll often see S3 combined with CloudFront — Amazon's content delivery network. Your files sit in S3 storage and get served from servers physically close to your users around the world, making load times fast everywhere.

If your React app is part of a larger system with a backend, containerizing everything with Docker and putting it behind nginx is common. And if you're using Next.js for server-side rendering, Vercel is the natural home for that since Vercel built Next.js."

---

**[SLIDE 14: Deploying to Netlify/Vercel — Step by Step]**
*Title: "Deployment in Practice"*
*Content:*
- *Step 1: Push your project to GitHub*
- *Step 2: Connect your GitHub repo to Netlify or Vercel*
- *Step 3: Set build command (`npm run build`) and output directory (`build` or `dist`)*
- *Step 4: Set environment variables (API keys, etc.) in the dashboard — never hardcode*
- *Step 5: Deploy. Every push to main auto-deploys*
- *Bonus: Pull request previews — each PR gets its own preview URL*

---

**SCRIPT:**

"Let me walk you through the actual process because it's surprisingly simple. You push your code to GitHub. You go to Netlify or Vercel, connect your GitHub account, select your repository, and tell it your build command and output folder. For a Vite project that's `npm run build` and `dist`. For Create React App it's `npm run build` and `build`. You click deploy. Done.

The platform detects your framework automatically and often pre-fills those settings. After that first setup, every push to your main branch triggers an automatic deploy.

One critical thing: environment variables. If your app uses API keys, backend URLs, or any sensitive configuration, you do not put those in your code. You put them in the deployment platform's dashboard under environment variables. In your code you access them as `import.meta.env.VITE_API_KEY` or `process.env.REACT_APP_API_KEY`. The platform injects them at build time. Never commit secrets to version control."

---

## SECTION 7: PROJECT STRUCTURE & BEST PRACTICES (7 minutes)

---

**[SLIDE 15: Project Structure That Scales]**
*Title: "Organizing a React Project"*
*Content:*
```
src/
├── components/       # Reusable UI components
│   └── Button/
│       ├── Button.jsx
│       ├── Button.test.jsx
│       └── index.js
├── pages/            # Route-level components
├── hooks/            # Custom hooks
├── context/          # Context providers
├── services/         # API calls, external integrations
├── utils/            # Pure helper functions
├── assets/           # Images, fonts
└── App.jsx

Co-locate files that change together
Feature-based organization for large apps


SCRIPT:
"Structure is opinionated and teams disagree on specifics, but there are principles that hold up well. The structure on this slide is a common, battle-tested starting point.
The key idea is co-location — files that change together should live together. Keep your component's test file and styles right next to the component itself, not in a separate tests folder. When you delete a component, you delete one folder, not hunt down files scattered across the project.
Separate your concerns clearly. Pages are route-level components — they shouldn't contain a lot of logic. Components are reusable UI pieces. Hooks are where you extract and share stateful logic. Services are where your API calls live. Utils are pure functions with no React dependencies.
For larger applications, feature-based organization often makes more sense — instead of grouping by file type, you group by feature. Everything related to authentication lives in a features/auth folder. This scales better because you rarely touch multiple features at once."

[SLIDE 16: React Best Practices]
Title: "Best Practices to Live By"
Content:

Keep components small and focused — one responsibility
Lift state up only as far as needed
Prefer custom hooks for shared logic over HOCs
Keys in lists must be stable and unique — never use array index if list can reorder
Avoid anonymous functions as props when using React.memo
Clean up side effects in useEffect return
Don't derive state from props on initial render — compute it
Accessibility: semantic HTML, ARIA labels, keyboard navigation


SCRIPT:
"Let me give you a rapid-fire set of best practices that will save you debugging headaches.
Keep components small. If a component is doing more than one clear thing, split it. Small components are easier to test, easier to reason about, and easier to reuse.
For list keys — never use the array index as a key when your list can be filtered, sorted, or reordered. React uses keys to track which DOM elements correspond to which components. If you sort a list, the indices change but the keys don't change with them, and React gets confused. Use a stable, unique identifier from your data.
Clean up your useEffect side effects. If you set up an event listener or a subscription or a timer, return a cleanup function. Memory leaks and stale closures are common bugs that come from not doing this.
Accessibility is not optional. Use semantic HTML elements — button for buttons, nav for navigation, heading tags in order. Screen readers and keyboard users rely on this. It's also good for SEO. Get into the habit now."

CLOSING & RECAP (5 minutes)

[SLIDE 17: What We Covered Today]
Title: "Today's Key Takeaways"
Content:

Render Props: share behavior, let consumers control the UI
Compound Components: related components sharing state through context
React.memo, useMemo, useCallback: prevent unnecessary work — measure first
React.lazy + Suspense: code split by route for faster loads
Concurrent features: keep UI responsive with useTransition
React DevTools: profile before you optimize
Production build: npm run build — what actually gets deployed
Deployment: Netlify/Vercel for most projects, S3+CDN for scale
Project structure: co-locate, separate concerns, stay consistent


[SLIDE 18: What's Coming Next]
Title: "Looking Ahead"
Content:

[Fill in your next lesson topics here]
Recommended practice: take a previous project and deploy it today
Install React DevTools if you haven't
Try wrapping a page component in React.lazy


SCRIPT:
"Let's do a quick recap of everything we covered today, because that was a lot.
You now understand two powerful composition patterns: render props, where you pass a function as a prop to share behavior with flexible rendering, and compound components, where a family of components shares state through context.
You understand the three main performance tools — React.memo to skip re-renders when props haven't changed, useMemo to memoize expensive calculations, and useCallback to stabilize function references. And you know the most important rule: measure first with DevTools, optimize second.
You understand code splitting with React.lazy and Suspense — how to break your app into chunks that load on demand, which dramatically improves initial load time.
You know what npm run build actually produces and why it's different from development mode. And you know how to get those files onto the internet, whether through Netlify, Vercel, or other options.
Before next class, I want you to do two things. First, install React DevTools right now if it's not in your browser. Second, take a project you've already built and deploy it to Netlify or Vercel. The whole process should take you under ten minutes once you've done it once, and having actually done it is worth more than any amount of slides about it.
Any questions before we wrap up?"

---

## INSTRUCTOR NOTES

**Missing:** The React DevTools profiler is mentioned by name in the closing script ("measure first with DevTools") but does not appear to have a dedicated slide showing students how to actually use it to spot unnecessary re-renders. A brief demo slide or screenshot would make the performance optimization section more actionable. Error boundary behavior when a lazy-loaded chunk fails to load (network error on dynamic import) is also worth a callout.

**Unnecessary/Too Advanced:** `useTransition` and `useDeferredValue` concurrent features are advanced and not commonly needed in typical application development. These could be flagged as optional/advanced enrichment rather than required knowledge for intro students.

**Density:** Well-paced overall. The concurrent features section is the most complex part of the session — if time is limited, it is the most appropriate section to deprioritize or assign as optional reading.

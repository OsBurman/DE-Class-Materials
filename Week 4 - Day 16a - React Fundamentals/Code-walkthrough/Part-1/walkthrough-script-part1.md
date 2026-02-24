# Day 16a — React Fundamentals · Part 1 Walkthrough Script
## React Overview, JSX Syntax & Functional Components

**Duration:** ~90 minutes  
**Files:**
- `Part-1/01-react-overview-and-spa.jsx`
- `Part-1/02-jsx-syntax-and-rules.jsx`
- `Part-1/03-functional-components.jsx`

---

## ⚙️ Pre-Class Setup (3 min)

[ACTION] Before class, verify a React project is ready. If using Create React App:
```bash
npx create-react-app react-fundamentals
cd react-fundamentals
npm start
```
Or open https://codesandbox.io/s/new and select React template — zero install needed.

> "Good morning everyone. Today we start React — and I want to be upfront: this is one of the most popular JavaScript libraries in the world. Almost every job posting for a frontend or full-stack developer lists React. What we build over the next few days will directly apply to real projects."

---

## Part 1A — React Overview & Philosophy (20 min)
### File: `01-react-overview-and-spa.jsx`

---

### A1 — What is React? (5 min)

[ACTION] Open `01-react-overview-and-spa.jsx`. Read through Section 1 with the class.

> "React is a **library**, not a full framework. It has one job: build user interfaces. You pair it with other tools for routing, state management, and API calls."

[ACTION] Draw on the board:

```
React's 3 Philosophies:

1. DECLARATIVE        → describe WHAT, not HOW
2. COMPONENT-BASED    → build with reusable UI pieces
3. LEARN ONCE,        → web, mobile (React Native),
   WRITE ANYWHERE       desktop — same patterns
```

[ACTION] Show the imperative vs declarative comparison in the comments:

> "In vanilla JavaScript, you tell the browser step by step how to create an element, set its text, set its color, and add it to the page. In React, you just describe what the UI should look like. React figures out how to make it happen."

[ASK] "Can anyone think of another declarative system you've used?"
> _Expected answers: SQL (`SELECT * FROM users WHERE age > 18` — you declare what you want, not how to find it), CSS (you declare styles, the browser renders them), HTML itself._

→ TRANSITION: "Let's talk about the architecture behind how React efficiently updates the browser."

---

### A2 — Single Page Applications (7 min)

[ACTION] Stay in `01-react-overview-and-spa.jsx`, scroll to Section 2.

[ACTION] Draw the MPA vs SPA diagram on the board:

```
MULTI-PAGE APP (MPA):
  User clicks About →  GET /about → server returns full HTML page
  User clicks Contact → GET /contact → server returns full HTML page
  Every click = full reload = 🐌 flash/flicker

SINGLE PAGE APP (SPA):
  First load → GET / → server returns one index.html + JS bundle
  User clicks About → JavaScript swaps content in-place ← NO reload
  User clicks Contact → JavaScript swaps again ← NO reload
```

> "React apps are SPAs. The HTML file is basically empty — just `<div id='root'></div>`. React fills everything in with JavaScript."

[ASK] "What's the trade-off of an SPA? What's the downside?"
> _Answer: The first load can be slower because the browser has to download the whole JavaScript bundle. Also, search engines traditionally had trouble indexing SPAs (though this is largely solved now with Next.js and server-side rendering)._

⚠️ **WATCH OUT:** Students sometimes think React runs on the server. It runs in the **browser**. The server just serves static HTML/CSS/JS files. (SSR with Next.js is different — we cover that later.)

---

### A3 — Virtual DOM & Reconciliation (8 min)

[ACTION] Scroll to Section 3.

[ACTION] Draw the Virtual DOM flow:

```
State changes
     ↓
React builds NEW Virtual DOM (lightweight JS object tree)
     ↓
React DIFFS new V-DOM vs previous V-DOM
     ↓
React finds MINIMUM changes needed
     ↓
React PATCHES only those changes into the real DOM
```

> "Imagine you're editing a 500-page book. The dumb approach: reprint all 500 pages every time you change a comma. React's approach: mark only the sentences that changed and reprint only those."

[ASK] "What makes the real DOM expensive to update?"
> _Answer: Every time you touch the DOM, the browser potentially has to recalculate layout (reflow) and repaint. React batches and minimizes these operations._

> "The Virtual DOM is just plain JavaScript objects. Comparing two JS objects is extremely fast. Touching the real DOM is slow. React does the fast comparison first, then makes the minimum number of slow DOM operations."

[ACTION] Show the entry point comments at the bottom of the file:
```javascript
const root = ReactDOM.createRoot(document.getElementById('root'));
root.render(<App />);
```

> "This is where React boots up. It grabs the single `<div id='root'>` in your index.html and takes over everything inside it. One line to start the whole React app."

→ TRANSITION: "Now that we understand WHY React works the way it does, let's learn the syntax we use to write it — JSX."

---

## Part 1B — JSX Syntax & Rules (30 min)
### File: `02-jsx-syntax-and-rules.jsx`

---

### B1 — JSX Is Syntactic Sugar (5 min)

[ACTION] Open `02-jsx-syntax-and-rules.jsx`. Read Section 1.

> "This is the most important thing to understand about JSX: it is NOT HTML. It looks like HTML, but it compiles to JavaScript function calls."

[ACTION] Write on the board:
```
JSX:     <h1 className="title">Hello!</h1>
         ↓ Babel/Vite compiles to:
JS:      React.createElement('h1', { className: 'title' }, 'Hello!')
```

> "Babel (the transpiler) converts JSX to `React.createElement()` calls. We write JSX because it's much more readable than nested function calls. But this compilation step is why JSX has strict rules — it has to produce valid JavaScript."

[ASK] "Why do you think JSX uses `className` instead of `class`?"
> _Let them think... Then reveal: Because `class` is a reserved keyword in JavaScript — it's used for ES6 class definitions. If JSX used `class`, JavaScript would be confused._

---

### B2 — Rule 1: One Root Element & Fragments (7 min)

[ACTION] Scroll to Section 3 in the file.

> "JSX must return exactly ONE root element. This comes directly from `React.createElement()` — a function call can only return one thing."

[ACTION] Show the broken example:
```jsx
// ❌ INVALID — two siblings at the top level
const bad = (
  <h1>Title</h1>
  <p>Paragraph</p>
);
```

> "This would compile to two `React.createElement()` calls side by side with no parent — not valid JavaScript."

[ACTION] Show the Fragment shorthand:
```jsx
const withShortFragment = (
  <>
    <h1>Title</h1>
    <p>Paragraph</p>
  </>
);
```

> "Fragments are React's solution. The empty `<>` tags group children without adding an extra DOM node. Your DevTools inspector won't show a wrapper div — it just renders the two siblings directly."

⚠️ **WATCH OUT:** Students often reflexively wrap everything in `<div>`. This creates unnecessary DOM nesting. Use `<>` when you just need a grouping container for JSX rules.

---

### B3 — Rule 2: className, htmlFor, camelCase (5 min)

[ACTION] Scroll to Section 4.

> "Because JSX compiles to JavaScript, any JSX attribute that would conflict with a JavaScript keyword gets renamed."

[ACTION] Write on the board:
```
HTML attribute    →  JSX attribute
─────────────────────────────────
class             →  className
for               →  htmlFor
onclick           →  onClick     (camelCase)
tabindex          →  tabIndex    (camelCase)
```

⚠️ **WATCH OUT:** Using `class` instead of `className` in JSX will still render in the browser, but React will show a warning in the console and your linter will flag it. Build the habit from day one.

---

### B4 — Rule 3: Self-Closing Tags (3 min)

[ACTION] Scroll to Section 5.

> "In HTML5, you can write `<br>`, `<img>`, `<input>` without closing them. JSX does not allow this. Every tag must be explicitly closed."

[ACTION] Show the examples:
```jsx
<br />      <img src="..." alt="..." />
<input type="text" />    <hr />
```

> "Notice the space before `/>`. That's the JSX convention for self-closing tags."

---

### B5 — Rule 4: JavaScript Expressions with `{}` (7 min)

[ACTION] Scroll to Section 6.

> "The curly braces in JSX are portals into JavaScript. Inside `{}` you can put any expression — a variable, a function call, an arithmetic operation, a ternary — anything that produces a value."

[ACTION] Walk through each expression example:
```jsx
<h2>Welcome, {userName}!</h2>
<p>Total: ${(itemCount * price).toFixed(2)}</p>
<p>Today: {new Date().toLocaleDateString()}</p>
```

> "The key word is EXPRESSION. An expression produces a value. A statement is an instruction."

[ACTION] Draw on the board:
```
EXPRESSIONS (✅ in JSX {}):       STATEMENTS (❌ in JSX {}):
─────────────────────────         ──────────────────────────
userName                          if (x > 0) { ... }
itemCount * price                 for (let i = 0; ...) { }
isLoggedIn ? 'yes' : 'no'        while (true) { }
array.map(x => <li>{x}</li>)     const x = 5
new Date().getFullYear()
```

---

### B6 — Inline Styles & Dynamic Attributes (5 min)

[ACTION] Scroll to Sections 7-9.

> "Two tricky spots: the style attribute, and dynamic attributes."

[ACTION] Show the style example:
```jsx
// ❌ WRONG — JSX does not accept CSS strings:
<div style="background-color: blue">

// ✅ CORRECT — JSX style takes a JavaScript object:
<div style={{ backgroundColor: 'blue', fontSize: '16px' }}>
```

> "Two pairs of curly braces: the outer pair means 'I'm entering a JSX expression', the inner pair is the JavaScript object literal. Property names are camelCase because they map to JavaScript's `element.style.backgroundColor`, not CSS's `background-color`."

[ASK] "What's wrong with this: `<img src='{avatarUrl}' />`?"
> _Answer: The quotes make it a string literal — it would render the text `{avatarUrl}`, not the variable's value. For dynamic values, no quotes — just curly braces: `src={avatarUrl}`._

[ACTION] Walk through the `JsxShowcase` component at the bottom of the file — it uses all the rules together.

→ TRANSITION: "Now we know how to write JSX. Let's use it to build components — the building blocks of React."

---

## Part 1C — Functional Components (35 min)
### File: `03-functional-components.jsx`

---

### C1 — What Is a Component? (5 min)

[ACTION] Open `03-functional-components.jsx`. Read Section 1.

[ACTION] Draw the LEGO analogy on the board:

```
A web page in vanilla HTML:        A React app:
─────────────────────────          ──────────────────────────────
One giant HTML file                <App>
  All HTML mixed together            <NavBar />
  All JS mixed together              <main>
  Hard to reuse                        <CourseCard />
  Hard to maintain                     <CourseCard />
                                       <CourseCard />
                                     </main>
                                     <Footer />
                                   </App>
```

> "Each component is a self-contained piece of UI. The NavBar doesn't know about the Footer. CourseCard doesn't know about NavBar. They're independent LEGO bricks."

---

### C2 — Simplest Functional Components (7 min)

[ACTION] Scroll to Section 2. Walk through each example:

```jsx
function Hello() {
  return <h1>Hello, World!</h1>;
}
```

> "This is the simplest React component. A function named with a capital letter that returns JSX. That's it."

[ACTION] Show the arrow function variants:
```jsx
const Goodbye = () => {
  return <h1>Goodbye, World!</h1>;
};

const Logo = () => <img src="/logo.png" alt="App logo" />;
```

> "These three syntaxes all produce identical components. The arrow function with implicit return is common for simple components. Use whichever you find most readable."

⚠️ **WATCH OUT:** The most common beginner mistake: lowercase component name.
```jsx
function myComponent() { return <div />; }  // 'myComponent' is a DOM tag to React
<myComponent />  // React thinks you want a custom HTML element!

function MyComponent() { return <div />; }  // ✅ uppercase = React component
```

---

### C3 — Components with Logic (5 min)

[ACTION] Show `UserGreeting`:

> "Components can contain any JavaScript logic before the return statement. Here we calculate the time of day and use it in the JSX."

```jsx
function UserGreeting() {
  const currentHour = new Date().getHours();
  let timeOfDay;
  if (currentHour < 12) { timeOfDay = 'morning'; } ...
  return <div><h2>Good {timeOfDay}! 👋</h2></div>;
}
```

> "This is the pattern: all your logic at the top, then `return` the JSX at the bottom. Keep the JSX as clean as possible — compute values into variables first."

---

### C4 — Class Components: The Legacy Way (10 min)

[ACTION] Scroll to Section 3.

> "Before 2019, this was the only way to have state in React. You'll see this in older codebases. You need to recognize it even if you won't write it."

[ACTION] Walk through `ClassCounter`:

```jsx
class ClassCounter extends Component {
  constructor(props) {
    super(props);            // ← always first
    this.state = { count: 0 };
    this.handleClick = this.handleClick.bind(this); // ← binding `this`
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

> "Notice how verbose this is. You need a constructor, super(), bind the event handler manually. And the `this.` everywhere. This is why the React team invented hooks."

[ASK] "What happens if you forget to call `super(props)` in the constructor?"
> _Answer: You'll get a runtime error — `this` is not initialized until `super()` is called in a class that extends another class._

[ACTION] Now show the same component as a functional component (Section 4):

```jsx
function FunctionalCounter() {
  const [count, setCount] = useState(0);
  return (
    <div>
      <p>Count: {count}</p>
      <button onClick={() => setCount(count + 1)}>Increment</button>
    </div>
  );
}
```

> "Same behavior, a fraction of the code. No `this`. No binding. No constructor. Hooks give functional components everything class components had."

[ACTION] Show the comparison table in Section 4 comments. Read through each row with the class.

---

### C5 — Building a Real Page with Components (8 min)

[ACTION] Scroll to Section 5. Walk through `NavBar`, `CourseCard`, `Footer`, and `App`:

> "Each piece of the page is its own component. `NavBar` handles the navigation. `CourseCard` handles displaying course info. `Footer` handles the bottom of the page. `App` composes them all together."

[ACTION] Point at the `App` component:
```jsx
function App() {
  return (
    <div className="app">
      <NavBar />
      <main>
        <h1>Featured Course</h1>
        <CourseCard />
      </main>
      <Footer />
    </div>
  );
}
```

> "Using components in JSX is as simple as writing their name as an HTML-like tag. React calls that function, gets back JSX, and renders it in place."

[ASK] "If I wrote `<navbar />` instead of `<NavBar />`, what would happen?"
> _Answer: React would treat it as an unknown HTML element (like a custom DOM element), not a React component. Nothing would render. This is why the capital letter rule is so important._

---

### C6 — Component Best Practices (5 min)

[ACTION] Read through Section 6 best practices with the class.

> "Rule 3 is the most important for today: components should be **pure functions with respect to props**. Same input, same output, every time. No side effects in the render. We'll talk about side effects in Day 17a with useEffect."

---

## 🔁 Wrap-Up Q&A (5 min)

1. **"What is the Virtual DOM and why does React use it?"**
   > _A lightweight JavaScript copy of the DOM. React diffs it to find minimal changes, then updates only those in the real DOM — avoiding expensive full-DOM operations._

2. **"Name three differences between JSX and HTML."**
   > _`className` not `class`; `htmlFor` not `for`; all tags must be closed; style takes an object; `{}` for expressions; comments are `{/* */}`._

3. **"What's the rule for component names?"**
   > _Must start with a capital letter. Lowercase = React treats it as a DOM element._

4. **"What is a Fragment and when would you use one?"**
   > _`<>...</>` groups JSX children without adding a DOM node. Use when you need a root element for JSX rules but don't want an extra `<div>` in the DOM._

5. **"What's the difference between a functional and a class component?"**
   > _Functional = JavaScript function, uses hooks for state/lifecycle. Class = extends Component, uses this.state and lifecycle methods. Functional is preferred for all new code._

---

## 📚 Take-Home Exercises

### Exercise 1 — Profile Card Component
Build a `ProfileCard` component that displays:
- A user's name, job title, location, and bio
- Use hard-coded data inside the component (no props yet)
- Style with inline JSX styles (background, border-radius, padding)

### Exercise 2 — JSX Rules Practice
Find the 5 errors in this JSX and fix them:
```jsx
function BrokenComponent() {
  const name = "Sam";
  return (
    <div class="container">
      <h1>Hello {name}
      <p>Welcome to React!
      <img src="photo.jpg">
      <label for="age">Age:</label>
      <input type="number" id="age">
    </div>
  );
}
```

### Exercise 3 — Component Tree
Break this HTML into at least 4 React components. Identify which is the parent (root) component.
```html
<div class="app">
  <nav> ... </nav>
  <section class="hero"> <h1>...</h1> <p>...</p> <button>...</button> </section>
  <section class="features"> ... repeated cards ... </section>
  <footer> ... </footer>
</div>
```

---

*Part 2 covers: Props & data flow, Lists & keys, Conditional rendering, Component composition*

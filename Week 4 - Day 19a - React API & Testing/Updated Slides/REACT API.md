SLIDE 1 — Title Slide
Slide content: Title: "React API Integration & Error Handling" | Subtitle: "Fetch, Axios, Loading States, Error Boundaries" | Your name/date

Script:
"Good morning everyone. Today we're diving into one of the most practical skills you'll use as a React developer — connecting your applications to real data. Everything we've done so far has been static, hardcoded information. Today we make it live. By the end of this class you'll know how to fetch data from an API, handle what happens when things go wrong, and build components that are resilient to failure. Let's get into it."

SLIDE 2 — What We're Covering Today
Slide content: Four bullet points: "API Integration with Fetch & Axios" | "Loading States & Error Handling" | "Response Data Handling" | "Error Boundaries"

Script:
"Here's our roadmap for the hour. These four topics connect directly to each other — you really can't do one without the others. When you call an API, you need to handle loading, you need to handle errors, and you need to handle the data that comes back. Error boundaries are a React-specific safety net that wraps all of that together. We'll move through each one and by the end we'll have a working component that does all four."

SLIDE 3 — What Is an API and Why Do We Care?
Slide content: Simple diagram showing: Browser/React App → HTTP Request → API Server → HTTP Response → React App. Short definition: "API = a way for your app to communicate with a server to send or receive data."

Script:
"Before we write a single line of code, I want to make sure we're all on the same page about what's actually happening when we call an API. Your React app lives in the browser. The data — users, products, posts, whatever — lives on a server somewhere. An API is the agreed-upon way those two things talk to each other. You send an HTTP request, the server processes it, and it sends back a response, almost always in JSON format. React doesn't care how the API was built. It could be Node, Python, Go — it doesn't matter. All React cares about is sending that request and handling what comes back."

SLIDE 4 — The Two Main Tools: Fetch vs Axios
Slide content: Two-column comparison table. Fetch: "Built into the browser | No install needed | Verbose error handling | Returns Response object." Axios: "Third-party library | npm install axios | Cleaner syntax | Auto JSON parsing | Better error objects"

Script:
"You have two main choices for making API calls in React — Fetch, which is built directly into the browser, and Axios, which is a library you install. Both do the same job. The difference is convenience. Fetch is native, so nothing to install, but it requires extra steps — you have to manually call .json() to parse the response, and it won't throw an error on a 404 or 500 status code, which trips up a lot of beginners. Axios handles both of those things for you automatically. It parses JSON automatically and it throws errors on bad status codes. For production work most teams use Axios. We're going to learn both today so you understand what's happening under the hood."

SLIDE 5 — Using Fetch: Basic Syntax
Slide content: Code block showing a basic fetch call inside a useEffect with .then() chain and a console.log of the data.
jsuseEffect(() => {
  fetch('https://jsonplaceholder.typicode.com/posts')
    .then(response => response.json())
    .then(data => console.log(data))
    .catch(error => console.error(error));
}, []);

Script:
"Let's look at Fetch first. This is the most basic version. We're calling fetch inside a useEffect because we want this to run after the component mounts — we've covered useEffect before so this should look familiar. Fetch returns a Promise, so we chain .then() calls. The first .then() gives us the Response object — not the data yet, just the response. We call .response.json() to parse it, which itself returns another Promise, so we chain a second .then() to get the actual data. Finally, .catch() handles any network errors. Now here's the gotcha I mentioned — if the server returns a 404, Fetch will NOT hit the catch block. It considers the request successful because it got a response. I'll show you how to handle that in a moment."
[Live code this example, open DevTools Network tab, show the request firing and the data in the console.]

SLIDE 6 — Fetch: Handling HTTP Errors Properly
Slide content: Code block showing response.ok check before parsing.
jsfetch('https://api.example.com/data')
  .then(response => {
    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`);
    }
    return response.json();
  })
  .then(data => console.log(data))
  .catch(error => console.error(error));

Script:
"Here's how you fix that 404 problem with Fetch. Every Response object has an .ok property — it's a boolean that's true if the status code is between 200 and 299. If it's false, we manually throw an error, which kicks us into the catch block. This is boilerplate you'll write every single time you use Fetch. It's one of the reasons a lot of developers prefer Axios — it does this check for you automatically."

SLIDE 7 — Installing and Using Axios
Slide content: npm install axios command at top. Then code block showing the same request with Axios using async/await.
jsimport axios from 'axios';

useEffect(() => {
  axios.get('https://jsonplaceholder.typicode.com/posts')
    .then(response => console.log(response.data))
    .catch(error => console.error(error));
}, []);

Script:
"Now let's look at Axios. First, you install it — npm install axios. Then you import it at the top of your file. Notice with Axios the syntax is a little cleaner. You call axios.get() with your URL. In the .then() you get a response object, but the actual data is already parsed and waiting for you at response.data. No manual .json() call needed. And if the server returns a 404 or 500, Axios automatically throws an error, so your catch block fires. Much more intuitive."

SLIDE 8 — Async/Await Syntax (The Modern Way)
Slide content: Side-by-side: Promise chaining on the left vs async/await on the right. Both doing the same thing.

Script:
"You'll see both Promise chaining and async/await in the wild. Async/await is generally considered cleaner and easier to read, especially when you have multiple API calls. Let me rewrite that Axios example using async/await."
[Live code this:]
jsuseEffect(() => {
  const fetchPosts = async () => {
    try {
      const response = await axios.get('https://jsonplaceholder.typicode.com/posts');
      console.log(response.data);
    } catch (error) {
      console.error(error);
    }
  };
  fetchPosts();
}, []);
"Notice we define a separate async function inside the useEffect and then call it. That's because useEffect's callback can't be async directly — it's a quirk of how React works. You'll use this pattern constantly. Define the async function, call it. Get used to it."

SLIDE 9 — Loading States: Why They Matter
Slide content: Three-state diagram: "Loading → Success → Error" with a small UI mockup for each state. Loading: spinner. Success: data displayed. Error: error message shown.

Script:
"Now we need to talk about loading states, and this is where a lot of beginners skip a step and create bad user experiences. When you fire an API call, there's a gap in time before the data comes back. Could be 100 milliseconds, could be 3 seconds on a slow connection. During that time, your component has no data to display. If you don't handle that, the user sees a blank screen or a broken layout. Good apps have three states: loading, success, and error. You need to handle all three. Every single time."

SLIDE 10 — Implementing All Three States
Slide content: Full component code showing useState for data, loading, and error, plus the JSX rendering logic.

Script:
"Let me build this out properly. This is the full pattern you'll use in almost every component that fetches data."
[Live code this full example:]
jsximport { useState, useEffect } from 'react';
import axios from 'axios';

function PostList() {
  const [posts, setPosts] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    const fetchPosts = async () => {
      try {
        const response = await axios.get(
          'https://jsonplaceholder.typicode.com/posts'
        );
        setPosts(response.data);
      } catch (err) {
        setError(err.message);
      } finally {
        setLoading(false);
      }
    };
    fetchPosts();
  }, []);

  if (loading) return <p>Loading...</p>;
  if (error) return <p>Error: {error}</p>;

  return (
    <ul>
      {posts.map(post => (
        <li key={post.id}>{post.title}</li>
      ))}
    </ul>
  );
}
"Walk through this with me. We have three pieces of state — posts for our data, loading which starts as true, and error which starts as null. Inside our async function we have a try/catch/finally block. If the request succeeds, we set our posts. If it fails, we set the error message. The finally block runs no matter what and sets loading to false — this ensures we always exit the loading state. Then in our JSX we check loading first, error second, and render data third. This order matters."

SLIDE 11 — The finally Block: Don't Skip It
Slide content: Code showing what happens WITHOUT finally (loading stays true on error) vs WITH finally (loading always resolves).

Script:
"I want to pause on the finally block because students skip it all the time and it creates subtle bugs. If you only set setLoading(false) inside the try block, then when an error occurs, loading stays true forever. Your user just sees a spinner that never goes away. finally always runs, success or failure, so it's the right place to turn off loading. Small thing, big impact."

SLIDE 12 — Response Data Handling
Slide content: Three scenarios with code snippets: "Array response | Object response | Nested data"

Script:
"Let's talk about handling the data that actually comes back. APIs return data in different shapes and you need to know how to work with each one. The most common is an array — a list of posts, users, products. You handle that by mapping over it like we just did. Sometimes you get back a single object — a user profile, a single post. In that case you just access properties directly: response.data.name, response.data.email. The tricky one is nested data — a lot of real-world APIs return something like { data: { users: [...] } }. You have to drill down: response.data.data.users. Always console.log your response first when working with a new API so you know exactly what shape you're dealing with."
[Live code examples of each, showing the console.log first approach.]

SLIDE 13 — Practical Data Handling: Transforming API Responses
Slide content: Code showing mapping/transforming API data before storing in state.
jsconst users = response.data.map(user => ({
  id: user.id,
  fullName: `${user.firstName} ${user.lastName}`,
  email: user.email
}));
setUsers(users);

Script:
"A really useful technique is transforming the data before you put it in state. APIs don't always give you data in the exact shape your UI needs. Maybe you need a fullName field but the API gives you firstName and lastName separately. Maybe you need to filter out inactive records. Do that transformation right here after you get the response, before calling setState. This keeps your components clean — they just render what's in state without doing any data massaging in the JSX."

SLIDE 14 — What Are Error Boundaries?
Slide content: Diagram showing a component tree where one child crashes. Without Error Boundary: entire tree unmounts. With Error Boundary: only the crashed subtree is replaced with a fallback UI.

Script:
"Now we get to Error Boundaries, and this is a React-specific concept that's really important to understand. In JavaScript, if a component throws an error during rendering, React will unmount the entire component tree. Your whole app goes blank. Not great. Error Boundaries are special React components that catch errors from their child components and display a fallback UI instead of crashing everything. Think of them like a try/catch but for your component tree. If something inside the boundary blows up, the boundary catches it and shows a friendly error message instead of nuking the whole page."

SLIDE 15 — Error Boundary: The Code
Slide content: Full class component code for an ErrorBoundary.

Script:
"Here's the important thing to know upfront: Error Boundaries must be class components. This is one of the few places in modern React where you still need a class. There's no Hook equivalent yet. Let me write this out."
[Live code:]
jsximport { Component } from 'react';

class ErrorBoundary extends Component {
  constructor(props) {
    super(props);
    this.state = { hasError: false, error: null };
  }

  static getDerivedStateFromError(error) {
    return { hasError: true, error };
  }

  componentDidCatch(error, errorInfo) {
    console.error('Error caught by boundary:', error, errorInfo);
    // In production you'd send this to a logging service like Sentry
  }

  render() {
    if (this.state.hasError) {
      return (
        <div>
          <h2>Something went wrong.</h2>
          <p>{this.state.error?.message}</p>
        </div>
      );
    }
    return this.props.children;
  }
}

export default ErrorBoundary;
"Two lifecycle methods make this work. getDerivedStateFromError is called when a child throws — you return updated state here to trigger the fallback UI. componentDidCatch is where you do side effects like logging the error to a service. In production apps you'd send this to something like Sentry or Datadog. The render method checks hasError and either shows the fallback or renders this.props.children — which is whatever you wrapped inside the boundary."

SLIDE 16 — Using the Error Boundary
Slide content: Code showing ErrorBoundary wrapping components in App.jsx, at different levels of granularity.
jsx// Wrapping a whole page
<ErrorBoundary>
  <UserDashboard />
</ErrorBoundary>

// Wrapping individual components
<ErrorBoundary>
  <UserProfile />
</ErrorBoundary>
<ErrorBoundary>
  <RecentOrders />
</ErrorBoundary>

Script:
"Using it is simple — you just wrap your components with it like any other wrapper component. The placement matters a lot though. If you put one ErrorBoundary around your whole app, any error anywhere brings down the whole UI to that one fallback screen. Usually it's better to put ErrorBoundaries around sections. That way if the Orders widget crashes, the User Profile widget still works. Think about your app in terms of independent zones and put a boundary around each one. A good rule of thumb: any component that fetches its own data should probably be wrapped in an Error Boundary."

SLIDE 17 — What Error Boundaries Don't Catch
Slide content: Bullet list: "Event handlers | Async code (setTimeout, fetch callbacks) | Server-side rendering | Errors in the boundary itself"

Script:
"Error Boundaries have limits and you need to know them. They only catch errors that happen during rendering, in lifecycle methods, and in constructors of child components. They do NOT catch errors inside event handlers — for those you use regular try/catch. They do NOT catch errors in async code, which is why we still need our try/catch inside useEffect. They also won't catch an error thrown by the Error Boundary component itself. These aren't gotchas to worry you — just understand the tool you're using and use the right one for the right job."

SLIDE 18 — Putting It All Together: Full Example
Slide content: Architecture diagram showing: ErrorBoundary → PostList component → useEffect + Axios + loading/error/data states

Script:
"Let's put everything together into one complete example. I want you to see how all these pieces connect."
[Live code or walk through a complete example:]
jsx// App.jsx
import ErrorBoundary from './ErrorBoundary';
import PostList from './PostList';

function App() {
  return (
    <div>
      <h1>My Blog</h1>
      <ErrorBoundary>
        <PostList />
      </ErrorBoundary>
    </div>
  );
}
jsx// PostList.jsx
import { useState, useEffect } from 'react';
import axios from 'axios';

function PostList() {
  const [posts, setPosts] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    const fetchPosts = async () => {
      try {
        const response = await axios.get(
          'https://jsonplaceholder.typicode.com/posts?_limit=10'
        );
        setPosts(response.data);
      } catch (err) {
        setError(err.message);
      } finally {
        setLoading(false);
      }
    };
    fetchPosts();
  }, []);

  if (loading) return <div className="spinner">Loading posts...</div>;
  if (error) return <div className="error-msg">Failed to load posts: {error}</div>;

  return (
    <ul>
      {posts.map(post => (
        <li key={post.id}>
          <strong>{post.title}</strong>
          <p>{post.body}</p>
        </li>
      ))}
    </ul>
  );
}

export default PostList;
"Here's the full picture. App.jsx wraps PostList in an ErrorBoundary — that handles any unexpected render crashes. Inside PostList, we handle our three states — loading, error from the API call, and data. The try/catch handles async failures, the Error Boundary handles render failures. Together they cover all the bases."

SLIDE 19 — Common Mistakes to Avoid
Slide content: Four common mistakes with brief fix for each.

Script:
"Let me call out the mistakes I see most often so you don't make them too.
First — forgetting the dependency array on useEffect. Without it, your API call fires on every single render, spamming the server. Always add that empty array if you only want it to run once on mount.
Second — not initializing state correctly. If your API returns an array, initialize state as an empty array, not null. Then when you try to map over it before data loads, you won't crash.
Third — making useEffect async directly. Don't write useEffect(async () => {...}). React expects useEffect to either return a cleanup function or nothing. Async functions return Promises. Define your async function inside and call it.
Fourth — not handling the unmount case. If a component unmounts before an API call finishes, calling setState on an unmounted component causes a memory leak warning. You handle this with a cleanup function in useEffect that sets a flag to ignore the response if the component is gone."

SLIDE 20 — Quick Reference Cheat Sheet
Slide content: One-page visual summary: Fetch template | Axios template | Three-state pattern | Error Boundary shell

Script:
"I'll share this slide with you — it's a cheat sheet of all the patterns we covered today. The Fetch template with the .ok check, the Axios async/await template, the three-state loading pattern, and the Error Boundary class. These are the four things you'll write over and over again in your career. Get comfortable with them."

SLIDE 21 — Summary
Slide content: Four recap points matching the four topics covered.

Script:
"Let's recap quickly. Fetch is built-in but verbose — you need the .ok check manually. Axios is a library that's cleaner and more forgiving. Always handle three states: loading, error, and data. Use try/catch/finally and never skip that finally block. Handle your response data thoughtfully — know the shape before you render it. And wrap components that could crash in Error Boundaries, placed strategically at section level rather than just the top level. These patterns together give you robust, production-quality data fetching."

SLIDE 22 — What's Coming Next
Slide content: Preview of next lessons — text like "Coming up: Testing with React Testing Library | Mocking API calls in tests | Writing tests for loading and error states"

Script:
"In our next session we're going to take everything we built today and write tests for it — how do you test a component that makes an API call? How do you simulate a loading state or an error in a test? That's where React Testing Library and API mocking come in, and it builds directly on what you learned today. Make sure you're solid on these patterns before next class because we'll be testing them. Any questions before I let you go?"

---

## INSTRUCTOR NOTES

**Missing:** Cleanup of fetch requests inside `useEffect` using an `AbortController` — this prevents the common "Can't perform a React state update on an unmounted component" warning that students will encounter immediately in real projects. It is a one-slide addition that saves significant debugging confusion.

**Unnecessary/Too Advanced:** Nothing to remove. Content is well-scoped and practical.

**Density:** Well-paced. The three-state pattern (loading/error/data) is the most important takeaway and is clearly explained. Error Boundaries require a class component which feels anachronistic in a hooks-first course — a brief acknowledgment of why a class component is still required here (hooks cannot catch errors) would reduce student confusion.

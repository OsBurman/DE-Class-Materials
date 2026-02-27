[SLIDE 1: Title Slide]
"React API & Testing" — with subtitle "Writing Confident, Testable React Code"

Script:
"Good morning everyone. Today we're going to cover something that separates junior developers from developers who are actually production-ready — and that's testing. A lot of new developers skip this entirely, and then they ship bugs they didn't know they had. By the end of today, you'll know how to write real tests for your React components, test hooks and state changes, mock API calls so your tests don't hit a real server, and use environment variables to manage your app's configuration cleanly.
We have a lot to get through, so let's move fast but make sure everyone is following along. Raise your hand any time something isn't clicking."

SECTION 1: The Testing Ecosystem — Jest + React Testing Library (10 minutes)
[SLIDE 2: Why We Test]
Bullet points: Catch bugs early, Confidence when refactoring, Documents how components should behave, Required in professional environments

Script:
"Before we write a single line of test code, I want to explain the two tools we're going to use today, because students often confuse them.
Jest is the testing framework. Think of it as the engine. It runs your tests, gives you describe, it, and expect, and reports whether things passed or failed. It also handles mocking, timers, and code coverage. Jest was built by Facebook and ships with Create React App and Vite projects by default.
React Testing Library — often abbreviated RTL — is a completely different thing. It's a library that gives you utilities to render React components and interact with them in your tests. The philosophy behind RTL is that your tests should behave the way a real user would. You don't test internal implementation details. You test what the user actually sees and does.
Those two tools work together. Jest is the runner. RTL is the helper."

[SLIDE 3: Jest vs React Testing Library]
Two-column layout:

Left — Jest: Test runner, describe/it/test blocks, expect assertions, matchers (.toBe, .toEqual), mocking (jest.fn, jest.mock)
Right — RTL: render(), screen, fireEvent, userEvent, waitFor, queries (getBy, findBy, queryBy)


Script:
"Let me put this on the screen so you can see them side by side. On the left is everything Jest gives you. On the right is everything React Testing Library gives you. When you write a test, you'll be reaching into both columns at the same time.
You'll use describe and it from Jest to organize your test. You'll use render from RTL to mount your component. And then you'll use expect from Jest combined with RTL matchers like toBeInTheDocument to make your assertions.
Let's look at the simplest possible test to make this concrete."

[SLIDE 4: Your First Component Test]
jsx// Greeting.jsx
function Greeting({ name }) {
  return <h1>Hello, {name}!</h1>;
}

// Greeting.test.jsx
import { render, screen } from '@testing-library/react';
import Greeting from './Greeting';

describe('Greeting', () => {
  it('renders the user's name', () => {
    render(<Greeting name="Alex" />);
    expect(screen.getByText('Hello, Alex!')).toBeInTheDocument();
  });
});

Script:
"Here's a component that takes a name prop and renders it in an h1. The test renders it with the name 'Alex', then uses screen.getByText to find that text in the DOM, and checks that it's actually there with toBeInTheDocument.
Notice I'm not checking any state, any variables, any internal logic. I'm asking: if a user loaded this page, would they see 'Hello, Alex!'? That's the RTL philosophy in one example.
screen is your primary tool in RTL. It gives you access to the rendered DOM. getByText, getByRole, getByLabelText — these are all query methods on screen. We'll talk more about which queries to use in a moment."

SECTION 2: React Testing Library Basics — Queries & Interactions (10 minutes)
[SLIDE 5: The Three Query Families]
Table with three rows:

getBy — finds element, throws if not found, use when element MUST be there
queryBy — finds element, returns null if not found, use when element might NOT be there
findBy — returns a Promise, use for async elements that appear later


Script:
"RTL has three families of queries, and picking the right one matters. If you use getBy for something that doesn't exist yet, your test will throw an error immediately. If you use queryBy, it'll just return null — which is useful when you're testing that something is NOT on the screen.
And findBy is the async one. If your component loads data and then renders something, you need findBy because it waits for the element to appear. We'll use this when we get to API mocking.
Each family has the same set of suffixes: ByText, ByRole, ByLabelText, ByPlaceholderText, ByTestId. The RTL docs have a priority order for which query to prefer — and ByRole is at the top because it mirrors how assistive technologies and screen readers navigate the page."

[SLIDE 6: Preferred Query Priority]
Ordered list:

getByRole — buttons, headings, inputs, links
getByLabelText — form inputs with labels
getByPlaceholderText — inputs with placeholder
getByText — non-interactive elements
getByTestId — last resort, use data-testid attribute


Script:
"Try to use getByRole whenever possible. For a button, you'd write screen.getByRole('button', { name: /submit/i }). The name option matches the button's accessible name — either its text content or its aria-label.
getByTestId is the escape hatch. You put a data-testid attribute directly on the element and query it by that. It works, but it ties your test to implementation details, so use it sparingly."

[SLIDE 7: Firing Events]
jsximport { render, screen, fireEvent } from '@testing-library/react';

it('calls onClick when button is clicked', () => {
  const handleClick = jest.fn();
  render(<button onClick={handleClick}>Click me</button>);
  
  fireEvent.click(screen.getByRole('button', { name: /click me/i }));
  
  expect(handleClick).toHaveBeenCalledTimes(1);
});
Note on slide: For more realistic user interactions, prefer @testing-library/user-event over fireEvent

Script:
"When you need to simulate user interactions — clicking, typing, submitting — you have two options. fireEvent ships with RTL and fires raw DOM events. It works fine for simple cases. userEvent from the @testing-library/user-event package is more realistic because it simulates the full sequence of events a real browser would fire — pointerdown, mousedown, focus, click, and so on.
For this class, we'll use fireEvent to keep things straightforward. Just know that in production-grade test suites you'll often see userEvent instead.
In this example, jest.fn() creates a mock function — a fake function Jest can watch. After the click, we assert that it was called exactly once."

SECTION 3: Testing Hooks and State (10 minutes)
[SLIDE 8: Testing State Through the UI]
Heading: "Don't test state directly — test what the user sees"
jsxfunction Counter() {
  const [count, setCount] = useState(0);
  return (
    <div>
      <p>Count: {count}</p>
      <button onClick={() => setCount(count + 1)}>Increment</button>
    </div>
  );
}

Script:
"This is a concept students fight me on at first. If your component has a useState hook, you might want to somehow reach in and check that the state variable equals a certain number. But RTL makes that intentionally hard. Why? Because the user doesn't care what your state variable is called. They care what's on the screen.
So instead of checking state directly, you click the button and then check what the UI renders. Let me show you the test."

[SLIDE 9: Counter Test]
jsximport { render, screen, fireEvent } from '@testing-library/react';
import Counter from './Counter';

describe('Counter', () => {
  it('starts at zero', () => {
    render(<Counter />);
    expect(screen.getByText('Count: 0')).toBeInTheDocument();
  });

  it('increments when button is clicked', () => {
    render(<Counter />);
    fireEvent.click(screen.getByRole('button', { name: /increment/i }));
    expect(screen.getByText('Count: 1')).toBeInTheDocument();
  });

  it('increments multiple times', () => {
    render(<Counter />);
    const button = screen.getByRole('button', { name: /increment/i });
    fireEvent.click(button);
    fireEvent.click(button);
    fireEvent.click(button);
    expect(screen.getByText('Count: 3')).toBeInTheDocument();
  });
});

Script:
"Three tests. The first checks the initial render. The second clicks once and checks the result. The third clicks three times and checks the result. In all three cases, we're validating what a user would actually see on screen.
Now what about custom hooks? What if your logic lives in a hook rather than a component?"

[SLIDE 10: Testing Custom Hooks with renderHook]
jsx// useCounter.js
export function useCounter(initial = 0) {
  const [count, setCount] = useState(initial);
  const increment = () => setCount(c => c + 1);
  const reset = () => setCount(initial);
  return { count, increment, reset };
}

// useCounter.test.js
import { renderHook, act } from '@testing-library/react';
import { useCounter } from './useCounter';

it('increments the count', () => {
  const { result } = renderHook(() => useCounter());
  
  act(() => {
    result.current.increment();
  });
  
  expect(result.current.count).toBe(1);
});

Script:
"When you have a custom hook you want to test in isolation, RTL gives you renderHook. It does what it sounds like — it renders the hook in a minimal test environment so you can call it directly.
The act wrapper is important. Any time you call something that causes a state update — like increment() — you need to wrap it in act. This tells React to flush all state updates and effects before you make your assertions. If you forget act, React will warn you and your test might not reflect the final state.
After the act block, you read the hook's return values from result.current. In this case we check that count is now 1."

[SLIDE 11: Testing useEffect Side Effects]
Key points on slide: useEffect runs after render, use waitFor for async effects, verify DOM changes rather than the effect itself
jsxit('fetches and displays data on mount', async () => {
  render(<UserProfile userId="1" />);
  
  // Element appears after useEffect runs
  const username = await screen.findByText('John Doe');
  expect(username).toBeInTheDocument();
});
```

---

**Script:**

"When testing a component that runs a `useEffect` to fetch data on mount, you need the async query variants. `findByText` returns a Promise and will keep polling the DOM for up to 1000ms by default before timing out. This gives your effect time to run and your component time to re-render.

In a moment we're going to talk about mocking the API call inside that effect, because if your test actually hits a real API, it's slow, unreliable, and not a unit test anymore."

---

## SECTION 4: Environment Variables in React (8 minutes)

**[SLIDE 12: What Are Environment Variables?]**
*Definition: Key-value pairs set outside your code that configure your app's behavior*
*Use cases: API base URLs, feature flags, API keys (public only), environment-specific settings (dev vs staging vs prod)*

---

**Script:**

"Before we get to mocking APIs, let's talk about where API URLs and configuration values should live — and that's environment variables.

The problem environment variables solve is this: your app needs to talk to a different backend URL when you're developing locally versus when it's deployed to production. If you hardcode the URL in your component, you have to change it every time you deploy. That's fragile and error-prone.

Environment variables let you define those values once per environment and never touch your component code again."

---

**[SLIDE 13: .env Files in React (Vite & CRA)]**
*Two-column layout:*

*Create React App:*
```
REACT_APP_API_URL=https://api.example.com
REACT_APP_FEATURE_FLAGS=true
```
*Access: `process.env.REACT_APP_API_URL`*

*Vite:*
```
VITE_API_URL=https://api.example.com
VITE_FEATURE_FLAGS=true
Access: import.meta.env.VITE_API_URL

Script:
"There's an important rule here that trips people up. In Create React App, every environment variable you want accessible in your frontend code MUST start with REACT_APP_. Without that prefix, CRA will ignore it entirely. In Vite, the prefix is VITE_.
This prefix requirement exists for security. Without it, any variable in your environment — including server secrets that happen to be set — could accidentally get bundled into your frontend JavaScript and shipped to the browser.
You access these in CRA with process.env.REACT_APP_WHATEVER, and in Vite with import.meta.env.VITE_WHATEVER."

[SLIDE 14: .env File Hierarchy]
List:

.env — loaded always, base defaults
.env.local — loaded always, ignored by git, overrides .env
.env.development — loaded only in dev mode
.env.production — loaded only in production builds
.env.test — loaded only during testing

Bottom of slide in red: NEVER commit secrets to .env files that are checked into source control. Use .env.local for sensitive values.

Script:
"You can have multiple .env files and they have a priority order. .env.local always overrides .env, which is why it's the right place to put values specific to your machine, like a local API URL. .env.test is loaded automatically by Jest, which means you can give your tests their own API base URL without affecting your dev environment.
Always add .env.local to your .gitignore. That file is where your secrets go. Your .env file — which contains safe defaults and placeholder values — can be committed to source control and is useful documentation for your team."

[SLIDE 15: Using Env Variables in Components]
jsx// api.js
const BASE_URL = import.meta.env.VITE_API_URL || 'http://localhost:3000';

export async function fetchUser(id) {
  const res = await fetch(`${BASE_URL}/users/${id}`);
  if (!res.ok) throw new Error('Failed to fetch user');
  return res.json();
}
Note: Extract API logic into a separate file — easier to mock in tests

Script:
"Here's a pattern I strongly recommend: don't write your fetch calls directly inside components. Put them in a separate api.js file or a services folder. That file reads the environment variable for the base URL. Your components just call functions like fetchUser.
This separation makes your code cleaner, but more importantly for today, it makes mocking in tests much easier. When all your API calls live in one module, you can mock that entire module with a single line of Jest."

SECTION 5: Mocking API Calls in Tests (12 minutes)
[SLIDE 16: Why Mock APIs in Tests?]
Bullet points: Real API calls are slow, Network can fail — flaky tests, Test server may not be running, You should control what data your tests receive, Unit tests should be isolated

Script:
"If your component fetches data from an API, you have a problem in tests. The test environment has no server running. Even if it did, you don't want your test to depend on what data happens to be in a real database. Your tests need to be deterministic — the same code should produce the same result every time.
The solution is mocking. You intercept the API call and return fake data that you control. There are two main approaches I'll show you: mocking the fetch function directly with Jest, and using a library called MSW — Mock Service Worker — which is more powerful. Let's start with the simpler approach."

[SLIDE 17: Mocking fetch with jest.fn()]
jsx// Component
function UserProfile({ userId }) {
  const [user, setUser] = useState(null);
  
  useEffect(() => {
    fetch(`/api/users/${userId}`)
      .then(res => res.json())
      .then(data => setUser(data));
  }, [userId]);

  if (!user) return <p>Loading...</p>;
  return <h1>{user.name}</h1>;
}

// Test
it('displays user name after fetching', async () => {
  global.fetch = jest.fn().mockResolvedValue({
    json: jest.fn().mockResolvedValue({ name: 'Jane Doe' }),
  });

  render(<UserProfile userId="1" />);

  expect(screen.getByText('Loading...')).toBeInTheDocument();
  
  const name = await screen.findByText('Jane Doe');
  expect(name).toBeInTheDocument();
});

Script:
"Here we're replacing the global fetch function with jest.fn(). mockResolvedValue sets up what the mock will return when called — in this case a fake Response-like object whose .json() method also returns a resolved Promise with our fake user data.
Notice we first check that 'Loading...' is showing — that validates the initial state. Then we use findByText to wait for 'Jane Doe' to appear once the fetch resolves.
After this test runs, global.fetch is still mocked. You should clean that up. In Jest, you can use afterEach(() => jest.resetAllMocks()) in a setup file or at the top of your describe block."

[SLIDE 18: Mocking an API Module with jest.mock()]
jsx// api.js
export async function fetchUser(id) {
  const res = await fetch(`${BASE_URL}/users/${id}`);
  return res.json();
}

// UserProfile.test.jsx
import { fetchUser } from './api';
jest.mock('./api');  // <-- mocks the entire module

it('displays user name', async () => {
  fetchUser.mockResolvedValue({ name: 'Jane Doe' });

  render(<UserProfile userId="1" />);

  expect(await screen.findByText('Jane Doe')).toBeInTheDocument();
});

it('shows error on failure', async () => {
  fetchUser.mockRejectedValue(new Error('Network error'));
  
  render(<UserProfile userId="1" />);
  
  expect(await screen.findByText('Something went wrong')).toBeInTheDocument();
});

Script:
"This is the approach I recommend when you've properly separated your API calls into their own module. jest.mock('./api') tells Jest to replace the entire api.js module with auto-mocked versions of all its exports. Every exported function becomes a jest.fn() automatically.
Then in each test, you call .mockResolvedValue() to define what that mock function returns for that specific test. This is much cleaner than mocking global fetch.
Notice the second test — we test the error case by using .mockRejectedValue. This simulates a network failure. You should always test both the happy path and the error path."

[SLIDE 19: Introduction to MSW (Mock Service Worker)]
What it is: Intercepts actual network requests at the service worker level
Why it's better: Works the same in tests AND in the browser, No need to mock fetch or axios directly, More realistic — your real fetch code runs unchanged
jsx// handlers.js
import { http, HttpResponse } from 'msw';

export const handlers = [
  http.get('/api/users/:id', ({ params }) => {
    return HttpResponse.json({ id: params.id, name: 'Jane Doe' });
  }),
];

Script:
"MSW is worth knowing about even if you won't use it on day one. Instead of replacing your fetch function, MSW intercepts network requests at a lower level — the same way a service worker intercepts network requests in a browser. Your component's actual fetch code runs completely unchanged. MSW just intercepts the request before it hits the network and returns the response you defined.
This is powerful because your tests are testing code that's closer to what actually runs in production. You define handlers that describe what the fake API should return, then set up a test server that uses those handlers."

[SLIDE 20: MSW Setup for Tests]
jsx// setupTests.js
import { setupServer } from 'msw/node';
import { handlers } from './handlers';

const server = setupServer(...handlers);

beforeAll(() => server.listen());
afterEach(() => server.resetHandlers());
afterAll(() => server.close());

// In a specific test, override a handler:
it('handles server error', async () => {
  server.use(
    http.get('/api/users/:id', () => {
      return new HttpResponse(null, { status: 500 });
    })
  );
  
  render(<UserProfile userId="1" />);
  expect(await screen.findByText('Something went wrong')).toBeInTheDocument();
});

Script:
"You set up an MSW server in your test setup file. beforeAll starts it, afterEach resets any test-specific overrides, and afterAll closes it. In individual tests you can override handlers to simulate specific scenarios like a 500 error, a 404, or slow responses.
The beforeAll, afterEach, afterAll pattern is Jest lifecycle hooks — they run at the beginning of the suite, after each test, and at the end of the suite respectively. You'll use these whenever you have shared setup and teardown logic."

SECTION 6: Writing Unit Tests for React Components — Putting It All Together (8 minutes)
[SLIDE 21: Anatomy of a Good Component Test File]
Structure:

Imports — component, RTL utilities, mocked modules
Mock declarations — jest.mock() calls at top level
describe block — groups tests for one component
beforeEach/afterEach — shared setup and teardown
Individual it/test blocks — one behavior per test


Script:
"Let me show you what a complete, well-structured test file looks like. This brings everything together — component rendering, state, API mocking, environment variables, the works."

[SLIDE 22: Complete Test Example]
jsximport { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { fetchUser } from './api';
import UserDashboard from './UserDashboard';

jest.mock('./api');

describe('UserDashboard', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('shows loading state initially', () => {
    fetchUser.mockResolvedValue({ name: 'Jane', role: 'admin' });
    render(<UserDashboard userId="1" />);
    expect(screen.getByText(/loading/i)).toBeInTheDocument();
  });

  it('renders user data after fetch', async () => {
    fetchUser.mockResolvedValue({ name: 'Jane', role: 'admin' });
    render(<UserDashboard userId="1" />);
    expect(await screen.findByText('Jane')).toBeInTheDocument();
    expect(screen.getByText('admin')).toBeInTheDocument();
  });

  it('shows error message when fetch fails', async () => {
    fetchUser.mockRejectedValue(new Error('Network error'));
    render(<UserDashboard userId="1" />);
    expect(await screen.findByText(/something went wrong/i)).toBeInTheDocument();
  });

  it('refetches when refresh button is clicked', async () => {
    fetchUser.mockResolvedValue({ name: 'Jane', role: 'admin' });
    render(<UserDashboard userId="1" />);
    await screen.findByText('Jane');
    
    fireEvent.click(screen.getByRole('button', { name: /refresh/i }));
    
    await waitFor(() => {
      expect(fetchUser).toHaveBeenCalledTimes(2);
    });
  });
});

Script:
"Take a minute to read through this. This is a realistic test suite for a component that fetches user data. We have four tests: loading state, success, error, and re-fetch on button click.
beforeEach(() => jest.clearAllMocks()) ensures the mock's call history is cleared between tests so the call count starts fresh.
In the last test, waitFor is slightly different from findBy. waitFor takes a callback and retries it until it doesn't throw. It's useful when you're not waiting for a DOM element to appear — you're waiting for some assertion to become true, like the API being called twice.
This is the kind of test suite you should be writing for every significant component in a professional codebase."

[SLIDE 23: Common Mistakes to Avoid]
List:

Forgetting await on findBy queries — test passes before data loads
Not wrapping state updates in act — stale state in assertions
Using getBy for elements that don't exist yet — throws instead of failing gracefully
Asserting on implementation details (state values, function names) instead of DOM output
Not cleaning up mocks between tests — test order dependency
Hardcoding URLs instead of using environment variables


Script:
"These are the mistakes I see most often. The big one is forgetting await on findBy. If you write screen.findByText('Jane') without awaiting it, you get back a Promise that you never resolve, and the assertion might pass vacuously or your test might not actually test anything.
The other one worth emphasizing: don't test implementation details. If your component works but you refactor it to use a different state variable name, your tests should still pass. If they don't, you're testing the wrong things."

CLOSING (5 minutes)
[SLIDE 24: What We Covered Today]
Summary list: Jest framework basics, RTL queries and interactions, Testing state through the UI, renderHook for custom hooks, .env files and environment variable prefixes, Mocking fetch with jest.fn(), Mocking API modules with jest.mock(), MSW for realistic network interception, Writing complete component test suites

Script:
"Let's do a quick recap of what we covered. You now know how Jest and React Testing Library work together and what each one is responsible for. You know the three query families — getBy, queryBy, findBy — and when to use each. You know how to test state changes by checking the DOM instead of checking state directly, and how to test custom hooks with renderHook and act.
You know how to set up environment variables in both CRA and Vite, the prefix rules, and how the different .env files layer on top of each other.
And you know three ways to mock API calls — directly mocking global fetch, mocking an API module with jest.mock(), and using MSW for more realistic interception.
The best way to get comfortable with this is to write tests. After class, go back to a component you've already built and write a test file for it. Start with the happy path, then add an error case. That repetition is what makes it click."

[SLIDE 25: Key Takeaways]
Three large points:

Test what users see, not how your code works internally
Isolate your tests — mock APIs, control your data
Environment variables belong in .env files, never hardcoded

---

## INSTRUCTOR NOTES

**Missing:** `@testing-library/user-event` vs. `fireEvent` — the distinction between these two should be made more explicit. `userEvent` more accurately simulates real browser interactions (e.g., typing fires multiple events) and is the recommended approach, but students need to understand *why* `fireEvent` is inferior for user interactions to make an informed choice. `renderHook` for testing custom hooks should be confirmed as covered in this session.

**Unnecessary/Too Advanced:** Nothing to remove from what was reviewed.

**Density:** Well-structured overall. The MSW (Mock Service Worker) section may be dense for students encountering service workers for the first time — consider framing it as "MSW intercepts requests at the network layer" without going into service worker internals. The async testing section requires solid understanding of `findBy` queries and `waitFor` — ensure students are not rushed through this.
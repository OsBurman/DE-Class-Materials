// =============================================================
// DAY 19a — Part 2, File 2: Mock API Calls & Environment Variables
// =============================================================
// Topics: jest.mock() for fetch & axios, manual mocks,
//         testing components that make API calls,
//         MSW (Mock Service Worker) overview,
//         environment variables in React (.env files)
// =============================================================

import React, { useState, useEffect } from 'react';
import { render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import '@testing-library/jest-dom';
import axios from 'axios';

// =============================================================
// SECTION 1 — Why Mock API Calls in Tests?
// =============================================================
// Tests should be:
//   ✅ Fast      — no real network calls (network adds latency + flakiness)
//   ✅ Reliable  — no dependency on a live server being up
//   ✅ Isolated  — tests don't interfere with each other via shared server state
//   ✅ Controlled — we decide exactly what data the API returns each test
//
// Three strategies covered here:
//   1. jest.fn() — manual mock implementation
//   2. jest.mock('module') — mock entire module (axios, fetch)
//   3. MSW (Mock Service Worker) — most production-like approach
// =============================================================

// =============================================================
// SECTION 2 — Component Under Test
// =============================================================
// CourseLoader fetches courses from an API and renders them.
// We'll write tests that mock the fetch/axios calls it makes.
// =============================================================

function CourseLoader() {
  const [courses, setCourses] = useState([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    axios.get('/api/courses')
      .then(res => setCourses(res.data))
      .catch(err => setError('Failed to load courses'))
      .finally(() => setIsLoading(false));
  }, []);

  if (isLoading) return <p>Loading courses...</p>;
  if (error) return <p role="alert">{error}</p>;

  return (
    <ul>
      {courses.map(c => <li key={c.id}>{c.title}</li>)}
    </ul>
  );
}

function CourseSearch() {
  const [query, setQuery] = useState('');
  const [results, setResults] = useState([]);
  const [hasSearched, setHasSearched] = useState(false);

  const handleSearch = async () => {
    setHasSearched(true);
    try {
      const response = await fetch(`/api/courses/search?q=${query}`);
      const data = await response.json();
      setResults(data);
    } catch {
      setResults([]);
    }
  };

  return (
    <div>
      <input
        value={query}
        onChange={e => setQuery(e.target.value)}
        placeholder="Search"
        aria-label="Search courses"
      />
      <button onClick={handleSearch}>Search</button>
      {hasSearched && results.length === 0 && <p>No results found</p>}
      <ul>
        {results.map(r => <li key={r.id}>{r.title}</li>)}
      </ul>
    </div>
  );
}

// =============================================================
// SECTION 3 — Mocking Axios with jest.mock()
// =============================================================
// jest.mock('axios') replaces the real axios module with an
// auto-mocked version where every exported function is jest.fn().
// We then configure the mock's return value per test.
// =============================================================

// Mock the entire axios module at the TOP of the test file
// (in a real test file this would be at the top-level, outside describe blocks)
// jest.mock('axios');

// For teaching purposes, we show the pattern inline:

describe('CourseLoader — with axios mocked', () => {
  // Re-declare mock after each test to prevent state leakage
  afterEach(() => {
    jest.clearAllMocks();
  });

  test('shows loading spinner initially', () => {
    // Never resolves — keeps the component in loading state
    jest.spyOn(axios, 'get').mockImplementation(() => new Promise(() => {}));

    render(<CourseLoader />);
    expect(screen.getByText(/loading courses/i)).toBeInTheDocument();
  });

  test('renders courses after successful API response', async () => {
    // Arrange — configure what axios.get resolves with
    const mockCourses = [
      { id: 1, title: 'React Fundamentals' },
      { id: 2, title: 'TypeScript Basics' },
    ];

    jest.spyOn(axios, 'get').mockResolvedValue({
      data: mockCourses,
      // mockResolvedValue — shorthand for mockImplementation(() => Promise.resolve(...))
    });

    // Act — render the component (triggers useEffect → axios.get)
    render(<CourseLoader />);

    // Assert — wait for the async state updates to complete
    await waitFor(() => {
      expect(screen.getByText('React Fundamentals')).toBeInTheDocument();
      expect(screen.getByText('TypeScript Basics')).toBeInTheDocument();
    });

    // Verify the API was called with the right URL
    expect(axios.get).toHaveBeenCalledWith('/api/courses');
  });

  test('shows error message when API call fails', async () => {
    // Arrange — make axios.get reject
    jest.spyOn(axios, 'get').mockRejectedValue(new Error('Network Error'));
    // mockRejectedValue — shorthand for mockImplementation(() => Promise.reject(...))

    render(<CourseLoader />);

    // Wait for error state to render
    await waitFor(() => {
      expect(screen.getByRole('alert')).toHaveTextContent(/failed to load courses/i);
    });
  });

  test('does not show loading spinner after data loads', async () => {
    jest.spyOn(axios, 'get').mockResolvedValue({ data: [{ id: 1, title: 'Course A' }] });

    render(<CourseLoader />);

    // Initially shows loading
    expect(screen.getByText(/loading/i)).toBeInTheDocument();

    // After load, loading spinner is gone
    await waitFor(() => {
      expect(screen.queryByText(/loading/i)).not.toBeInTheDocument();
    });
  });
});

// =============================================================
// SECTION 4 — Mocking the Global fetch with jest.spyOn
// =============================================================
// global.fetch is a browser API — not a module. We spy on it directly.
// =============================================================

describe('CourseSearch — with fetch mocked', () => {
  afterEach(() => {
    jest.restoreAllMocks(); // restores spied originals
  });

  test('displays search results after a successful search', async () => {
    const user = userEvent.setup();

    // Mock fetch to return a resolved response
    const mockResponse = [
      { id: 1, title: 'Advanced React Patterns' },
      { id: 2, title: 'React Performance' },
    ];

    jest.spyOn(global, 'fetch').mockResolvedValue({
      ok: true,
      json: () => Promise.resolve(mockResponse),
      // mock the response object shape that our component expects
    });

    render(<CourseSearch />);

    await user.type(screen.getByLabelText(/search courses/i), 'React');
    await user.click(screen.getByRole('button', { name: /search/i }));

    await waitFor(() => {
      expect(screen.getByText('Advanced React Patterns')).toBeInTheDocument();
      expect(screen.getByText('React Performance')).toBeInTheDocument();
    });

    // Verify fetch was called with the right URL
    expect(global.fetch).toHaveBeenCalledWith('/api/courses/search?q=React');
  });

  test('shows "No results found" when API returns empty array', async () => {
    const user = userEvent.setup();

    jest.spyOn(global, 'fetch').mockResolvedValue({
      ok: true,
      json: () => Promise.resolve([]),
    });

    render(<CourseSearch />);

    await user.type(screen.getByLabelText(/search courses/i), 'xyz');
    await user.click(screen.getByRole('button', { name: /search/i }));

    await waitFor(() => {
      expect(screen.getByText(/no results found/i)).toBeInTheDocument();
    });
  });
});

// =============================================================
// SECTION 5 — Mock Return Value Variants
// =============================================================
// Different ways to configure what a mock function returns:

// mockReturnValue(value)       — always returns this synchronous value
// mockResolvedValue(value)     — returns Promise.resolve(value)
// mockRejectedValue(error)     — returns Promise.reject(error)

// mockReturnValueOnce(value)   — returns this value ONCE, then falls through
// mockResolvedValueOnce(value) — resolves with this value only the first time
// mockRejectedValueOnce(error) — rejects only the first time

// Example: first call fails (retry scenario), second call succeeds
// mockFetch
//   .mockRejectedValueOnce(new Error('Timeout'))    // first call
//   .mockResolvedValueOnce({ ok: true, json: () => Promise.resolve(data) }); // second call

// Example: different data on consecutive calls (pagination)
// mockAxiosGet
//   .mockResolvedValueOnce({ data: page1Results })
//   .mockResolvedValueOnce({ data: page2Results });

// =============================================================
// SECTION 6 — MSW: Mock Service Worker (Overview)
// =============================================================
// MSW intercepts requests at the NETWORK level (via a Service Worker
// in the browser, or an HTTP interceptor in Node/Jest).
//
// Advantages over jest.mock():
//   ✅ Tests work the same in browser AND Jest (same mocks)
//   ✅ Closer to reality — the component uses real fetch/axios
//   ✅ Request matching by URL + method — not by import
//   ✅ Easy to reuse mocks between tests
//   ✅ Can be used for manual testing in the browser (Storybook, etc.)
//
// npm install msw
// =============================================================

// Setup (in a separate file: src/mocks/handlers.js):
// import { http, HttpResponse } from 'msw';
//
// export const handlers = [
//   http.get('/api/courses', () => {
//     return HttpResponse.json([
//       { id: 1, title: 'React Fundamentals' },
//       { id: 2, title: 'TypeScript Basics' },
//     ]);
//   }),
//
//   http.post('/api/courses', async ({ request }) => {
//     const body = await request.json();
//     return HttpResponse.json({ id: 99, ...body }, { status: 201 });
//   }),
//
//   http.get('/api/courses/:id', ({ params }) => {
//     return HttpResponse.json({ id: params.id, title: 'Course Detail' });
//   }),
// ];

// Setup (in src/mocks/server.js — for Jest/Node):
// import { setupServer } from 'msw/node';
// import { handlers } from './handlers';
// export const server = setupServer(...handlers);

// In your test setup file (setupTests.js):
// import { server } from './mocks/server';
// beforeAll(() => server.listen());
// afterEach(() => server.resetHandlers());  // reset per-test overrides
// afterAll(() => server.close());

// Using MSW in a test:
// test('shows courses from the API', async () => {
//   render(<CourseLoader />);
//   // No mock setup needed — MSW intercepts the real axios.get call
//   await waitFor(() => {
//     expect(screen.getByText('React Fundamentals')).toBeInTheDocument();
//   });
// });
//
// // Override handler for one test
// test('shows error when server is down', async () => {
//   server.use(
//     http.get('/api/courses', () => {
//       return new HttpResponse(null, { status: 500 });
//     })
//   );
//   render(<CourseLoader />);
//   await waitFor(() => {
//     expect(screen.getByRole('alert')).toBeInTheDocument();
//   });
// });

// =============================================================
// SECTION 7 — Environment Variables in React (.env files)
// =============================================================
// React (CRA and Vite) supports .env files for configuration.
// Environment variables let you change behavior between dev/prod
// without modifying source code.
// =============================================================

// --- .env files (in the PROJECT ROOT, not src/) ---
//
// .env                  — loaded always (all environments)
// .env.local            — loaded always, git-ignored (secrets for your machine)
// .env.development      — loaded only in `npm start` / development mode
// .env.production       — loaded only in `npm run build` / production mode
// .env.test             — loaded during `npm test` / Jest
//
// --- Variable Naming Rules ---
//
// CRA:   MUST start with REACT_APP_
//   REACT_APP_API_URL=https://api.example.com
//   REACT_APP_FEATURE_FLAG=true
//
// Vite:  MUST start with VITE_
//   VITE_API_URL=https://api.example.com
//   VITE_FEATURE_FLAG=true
//
// Variables NOT prefixed are NOT exposed to the browser (security!)
//
// --- Accessing in Code ---
//
// CRA:
//   const apiUrl = process.env.REACT_APP_API_URL;
//   const isDev = process.env.NODE_ENV === 'development';
//
// Vite:
//   const apiUrl = import.meta.env.VITE_API_URL;
//   const isDev = import.meta.env.DEV;

// --- Example .env files ---

// .env (committed to git — non-secret defaults)
// REACT_APP_API_URL=https://jsonplaceholder.typicode.com
// REACT_APP_MAX_RESULTS=20
// REACT_APP_FEATURE_DARK_MODE=false

// .env.development (local development overrides)
// REACT_APP_API_URL=http://localhost:8080/api
// REACT_APP_MAX_RESULTS=5

// .env.production (production build values)
// REACT_APP_API_URL=https://api.myapp.com
// REACT_APP_MAX_RESULTS=50

// .env.local (gitignored — your personal overrides / secrets)
// REACT_APP_API_KEY=my-secret-key-never-commit-this

// --- Using env vars in a component ---
// const API_URL = process.env.REACT_APP_API_URL;
//
// function App() {
//   return (
//     <div>
//       <CourseList apiUrl={API_URL} />
//       {process.env.REACT_APP_FEATURE_DARK_MODE === 'true' && <DarkModeToggle />}
//     </div>
//   );
// }

// --- Using env vars in a service file ---
// const apiClient = axios.create({
//   baseURL: process.env.REACT_APP_API_URL,
//   timeout: 10000,
// });

// CRITICAL WATCH-OUTS:
// 1. NEVER put secrets (API keys, passwords) in .env files that are committed to git
//    → Use .env.local or a CI/CD secrets manager instead
// 2. Changes to .env require RESTARTING the dev server to take effect
// 3. Env variables are embedded at BUILD TIME — they are baked into the JS bundle
//    → They are visible in the browser's Source tab — not truly secret!
// 4. For truly secret values (OAuth secrets, DB credentials), use a backend — never put them in React

// =============================================================
// SECTION 8 — Mocking Environment Variables in Tests
// =============================================================
// In Jest, environment variables can be set per test via process.env
// =============================================================

describe('Environment variable usage', () => {
  const ORIGINAL_ENV = process.env;

  beforeEach(() => {
    // Make a copy of process.env so we can safely modify it
    jest.resetModules();
    process.env = { ...ORIGINAL_ENV };
  });

  afterEach(() => {
    // Restore the original environment after each test
    process.env = ORIGINAL_ENV;
  });

  test('uses REACT_APP_API_URL for API calls', () => {
    process.env.REACT_APP_API_URL = 'https://test-api.example.com';

    const url = process.env.REACT_APP_API_URL;
    expect(url).toBe('https://test-api.example.com');
    // In practice: test that your service/component uses this URL
    // by checking what it passes to axios.get / fetch
  });

  test('feature flag controls UI rendering', () => {
    process.env.REACT_APP_FEATURE_DARK_MODE = 'true';

    // Imagine a component that reads this flag:
    // function App() {
    //   return process.env.REACT_APP_FEATURE_DARK_MODE === 'true'
    //     ? <DarkApp />
    //     : <LightApp />;
    // }
    // render(<App />);
    // expect(screen.getByTestId('dark-mode')).toBeInTheDocument();

    expect(process.env.REACT_APP_FEATURE_DARK_MODE).toBe('true');
  });
});

// =============================================================
// SECTION 9 — .env.test: Test-Specific Variables
// =============================================================
// Create a .env.test file in the project root:
//
// REACT_APP_API_URL=http://localhost:3001  (test server / MSW)
// REACT_APP_FEATURE_FLAG=false             (disable experimental features in tests)
//
// Jest picks this up automatically — no manual process.env needed.
// =============================================================

// =============================================================
// SECTION 10 — Integration: Component + API Mock + Env Var
// =============================================================
// Complete test that uses mocked API + environment variable together

function CourseService() {
  // Reads the base URL from environment config
  const baseUrl = process.env.REACT_APP_API_URL || 'https://api.default.com';
  return axios.get(`${baseUrl}/courses`);
}

function SmartCourseList() {
  const [courses, setCourses] = useState([]);
  const [status, setStatus] = useState('loading');

  useEffect(() => {
    CourseService()
      .then(res => { setCourses(res.data); setStatus('success'); })
      .catch(() => setStatus('error'));
  }, []);

  if (status === 'loading') return <p>Loading...</p>;
  if (status === 'error') return <p role="alert">Failed to load</p>;
  return (
    <ul aria-label="course list">
      {courses.map(c => <li key={c.id}>{c.title}</li>)}
    </ul>
  );
}

describe('SmartCourseList — env var + API mock integration', () => {
  const originalEnv = process.env;

  beforeAll(() => {
    process.env = { ...originalEnv, REACT_APP_API_URL: 'http://localhost:8080/api' };
  });

  afterAll(() => {
    process.env = originalEnv;
    jest.restoreAllMocks();
  });

  test('fetches from the env-configured base URL', async () => {
    const mockCourses = [
      { id: 1, title: 'Spring Boot' },
      { id: 2, title: 'Docker Fundamentals' },
    ];

    jest.spyOn(axios, 'get').mockResolvedValue({ data: mockCourses });

    render(<SmartCourseList />);

    await waitFor(() => {
      expect(screen.getByText('Spring Boot')).toBeInTheDocument();
    });

    // Verify the URL includes the env-configured base URL
    expect(axios.get).toHaveBeenCalledWith(
      'http://localhost:8080/api/courses'
    );
  });
});

// =============================================================
// SECTION 11 — jest.fn() Spy Patterns: Call Inspection
// =============================================================

describe('jest.fn() patterns', () => {
  test('tracks function calls', () => {
    const mockFn = jest.fn();

    mockFn('React', 101);
    mockFn('Angular', 202);

    expect(mockFn).toHaveBeenCalledTimes(2);
    expect(mockFn).toHaveBeenNthCalledWith(1, 'React', 101);
    expect(mockFn).toHaveBeenNthCalledWith(2, 'Angular', 202);
    expect(mockFn).toHaveBeenLastCalledWith('Angular', 202);

    // Access raw call data
    console.log(mockFn.mock.calls);       // [['React', 101], ['Angular', 202]]
    console.log(mockFn.mock.results);     // results of each call
  });

  test('mockImplementation for dynamic behavior', () => {
    const mockGetUser = jest.fn().mockImplementation(id => ({
      id,
      name: `User ${id}`,
    }));

    expect(mockGetUser(1)).toEqual({ id: 1, name: 'User 1' });
    expect(mockGetUser(42)).toEqual({ id: 42, name: 'User 42' });
  });
});

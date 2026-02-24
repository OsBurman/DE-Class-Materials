// =============================================================
// DAY 19a — Part 1, File 1: API Integration with Fetch & Axios
// =============================================================
// Topics: fetch API, Axios, loading states, error handling,
//         response data handling, custom useFetch hook
// =============================================================

import React, { useState, useEffect, useCallback } from 'react';
// npm install axios
import axios from 'axios';

// Base URL — in a real app this would come from an environment variable
// (see Part 2 for .env files)
const BASE_URL = 'https://jsonplaceholder.typicode.com';

// =============================================================
// SECTION 1 — Fetch API: Basic GET Request
// =============================================================
// The browser's built-in fetch() returns a Promise.
// Two-step resolution: first .json() parses the body.
// =============================================================

export function CourseListWithFetch() {
  const [courses, setCourses] = useState([]);
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState(null);

  useEffect(() => {
    // Step 1: Set loading = true BEFORE the request starts
    setIsLoading(true);
    setError(null); // clear any previous error on retry

    fetch(`${BASE_URL}/posts?_limit=10`)  // using /posts to simulate courses
      .then(response => {
        // WATCH OUT: fetch only rejects on network errors (offline, DNS failure)
        // HTTP error codes like 404 or 500 still RESOLVE — you must check ok manually
        if (!response.ok) {
          throw new Error(`HTTP error! Status: ${response.status}`);
        }
        return response.json(); // returns another Promise — parses JSON body
      })
      .then(data => {
        setCourses(data);       // Step 2: store the parsed data in state
      })
      .catch(err => {
        setError(err.message);  // Step 3: catch both network errors AND our thrown errors
      })
      .finally(() => {
        setIsLoading(false);    // Step 4: always clear loading, success OR failure
      });
  }, []); // empty dependency array — run once on mount

  // --- Conditional rendering based on state ---
  if (isLoading) return <div className="loading-spinner">Loading courses...</div>;
  if (error) return <div className="error-banner">Error: {error}</div>;

  return (
    <ul>
      {courses.map(course => (
        <li key={course.id}>{course.title}</li>
      ))}
    </ul>
  );
}

// =============================================================
// SECTION 2 — Fetch API: POST Request (Creating a Resource)
// =============================================================
// POST requires: method, headers (Content-Type), body (JSON string)
// =============================================================

export function CreateCourseForm() {
  const [title, setTitle] = useState('');
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [successMessage, setSuccessMessage] = useState('');
  const [error, setError] = useState(null);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setIsSubmitting(true);
    setError(null);
    setSuccessMessage('');

    try {
      const response = await fetch(`${BASE_URL}/posts`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',  // tells the server what format we're sending
        },
        body: JSON.stringify({ title, userId: 1 }),  // body MUST be a string
      });

      if (!response.ok) {
        throw new Error(`Failed to create course: ${response.status}`);
      }

      const newCourse = await response.json();
      setSuccessMessage(`Course "${newCourse.title}" created with ID ${newCourse.id}`);
      setTitle('');
    } catch (err) {
      setError(err.message);
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <form onSubmit={handleSubmit}>
      <input
        value={title}
        onChange={e => setTitle(e.target.value)}
        placeholder="Course title"
        required
      />
      <button type="submit" disabled={isSubmitting}>
        {isSubmitting ? 'Creating...' : 'Create Course'}
      </button>
      {successMessage && <p className="success">{successMessage}</p>}
      {error && <p className="error">{error}</p>}
    </form>
  );
}

// =============================================================
// SECTION 3 — Axios: GET Request
// =============================================================
// Axios advantages over fetch:
//   ✅ Automatically parses JSON — no .json() step
//   ✅ Throws on HTTP error status codes — no need to check .ok
//   ✅ Request/response interceptors (great for auth headers)
//   ✅ Request cancellation with AbortController / CancelToken
//   ✅ Built-in timeout support
//   ✅ Works in Node.js AND the browser (fetch is browser-only)
// =============================================================

export function CourseListWithAxios() {
  const [courses, setCourses] = useState([]);
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState(null);

  useEffect(() => {
    setIsLoading(true);

    axios.get(`${BASE_URL}/posts`, { params: { _limit: 10 } })
      // params object → Axios serializes to query string: ?_limit=10
      .then(response => {
        // response.data — Axios unwraps the response body for you
        setCourses(response.data);
      })
      .catch(err => {
        // Axios error object has more detail than a plain Error
        if (err.response) {
          // Server responded with a status outside 2xx
          setError(`Server error: ${err.response.status} — ${err.response.data?.message}`);
        } else if (err.request) {
          // Request was made but no response received (network issue)
          setError('Network error — check your connection');
        } else {
          // Something happened setting up the request
          setError(err.message);
        }
      })
      .finally(() => setIsLoading(false));
  }, []);

  if (isLoading) return <p>Loading...</p>;
  if (error) return <p>Error: {error}</p>;

  return (
    <ul>
      {courses.map(c => <li key={c.id}>{c.title}</li>)}
    </ul>
  );
}

// =============================================================
// SECTION 4 — Axios Instance: Shared Configuration
// =============================================================
// In real apps, create ONE axios instance with shared base URL,
// headers, and timeout. Import it wherever you need to make calls.
// =============================================================

export const apiClient = axios.create({
  baseURL: BASE_URL,
  timeout: 10000,          // abort if no response in 10 seconds
  headers: {
    'Content-Type': 'application/json',
    // 'Authorization': `Bearer ${token}` — add auth token here or via interceptor
  },
});

// Using the instance:
// apiClient.get('/courses')       → GET  https://BASE_URL/courses
// apiClient.post('/courses', {})  → POST https://BASE_URL/courses
// apiClient.put('/courses/1', {}) → PUT  https://BASE_URL/courses/1
// apiClient.delete('/courses/1')  → DELETE https://BASE_URL/courses/1

// =============================================================
// SECTION 5 — Axios Interceptors
// =============================================================
// Interceptors run on EVERY request or response.
// Common uses: attach auth token, log requests, handle 401 globally.
// =============================================================

// Request interceptor — runs before every request is sent
apiClient.interceptors.request.use(
  config => {
    // Attach the auth token from localStorage (or a store)
    const token = localStorage.getItem('authToken');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    console.log(`[API] ${config.method?.toUpperCase()} ${config.url}`);
    return config;   // MUST return config or the request will not proceed
  },
  error => Promise.reject(error)
);

// Response interceptor — runs on every response (success or error)
apiClient.interceptors.response.use(
  response => response,  // pass through successful responses unchanged
  error => {
    if (error.response?.status === 401) {
      // Token expired or invalid — redirect to login
      console.warn('Unauthorized. Redirecting to login...');
      window.location.href = '/login';
    }
    return Promise.reject(error); // still reject so component .catch() runs
  }
);

// =============================================================
// SECTION 6 — Response Data Handling
// =============================================================
// Real APIs often wrap data in a container object.
// You must navigate to the right property.
// =============================================================

// Example real API response shape:
// {
//   "status": "success",
//   "page": 1,
//   "totalResults": 150,
//   "data": {
//     "courses": [ {...}, {...} ]
//   }
// }

// Correct handling — unwrap the nested data:
// apiClient.get('/courses').then(res => {
//   const courses = res.data.data.courses;  // ← navigate the shape
//   setCourses(courses);
// });

// Helper: normalize API responses to a consistent shape
function normalizeApiResponse(response) {
  // Handles both: { data: [...] } and plain [...] responses
  const raw = response.data;
  if (Array.isArray(raw)) return raw;
  if (raw.data && Array.isArray(raw.data)) return raw.data;
  if (raw.courses && Array.isArray(raw.courses)) return raw.courses;
  return [];
}

// =============================================================
// SECTION 7 — Cancelling Requests with AbortController
// =============================================================
// Problem: component unmounts before fetch completes → state update
// on an unmounted component → React warning / memory leak
// Solution: abort the request in the cleanup function of useEffect
// =============================================================

export function SearchCourses() {
  const [query, setQuery] = useState('');
  const [results, setResults] = useState([]);
  const [isLoading, setIsLoading] = useState(false);

  useEffect(() => {
    if (!query.trim()) {
      setResults([]);
      return;
    }

    const controller = new AbortController(); // create a new controller each time query changes
    setIsLoading(true);

    fetch(`${BASE_URL}/posts?q=${encodeURIComponent(query)}`, {
      signal: controller.signal,  // pass the signal to fetch
    })
      .then(res => {
        if (!res.ok) throw new Error('Search failed');
        return res.json();
      })
      .then(data => setResults(data))
      .catch(err => {
        // AbortError is NOT a real error — ignore it silently
        if (err.name !== 'AbortError') {
          console.error('Search error:', err);
        }
      })
      .finally(() => setIsLoading(false));

    // Cleanup: runs when `query` changes (before next effect) OR on unmount
    return () => controller.abort();
  }, [query]); // re-run every time query changes

  return (
    <div>
      <input
        value={query}
        onChange={e => setQuery(e.target.value)}
        placeholder="Search courses..."
      />
      {isLoading && <p>Searching...</p>}
      <ul>
        {results.map(r => <li key={r.id}>{r.title}</li>)}
      </ul>
    </div>
  );
}

// =============================================================
// SECTION 8 — Custom useFetch Hook
// =============================================================
// Extract the loading/error/data pattern into a reusable hook.
// Any component that needs to fetch data uses this instead of
// duplicating the same useEffect logic everywhere.
// =============================================================

export function useFetch(url) {
  const [data, setData] = useState(null);
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState(null);

  const fetchData = useCallback(async () => {
    if (!url) return;
    setIsLoading(true);
    setError(null);

    const controller = new AbortController();

    try {
      const response = await fetch(url, { signal: controller.signal });
      if (!response.ok) throw new Error(`HTTP ${response.status}`);
      const json = await response.json();
      setData(json);
    } catch (err) {
      if (err.name !== 'AbortError') {
        setError(err.message);
      }
    } finally {
      setIsLoading(false);
    }

    return () => controller.abort();
  }, [url]);

  useEffect(() => {
    fetchData();
  }, [fetchData]);

  return { data, isLoading, error, refetch: fetchData };
}

// Using the custom hook — component is now very clean:
export function CourseDetailWithHook({ courseId }) {
  const { data: course, isLoading, error, refetch } = useFetch(
    `${BASE_URL}/posts/${courseId}`
  );

  if (isLoading) return <p>Loading...</p>;
  if (error) return <p>Error: {error} <button onClick={refetch}>Retry</button></p>;
  if (!course) return null;

  return (
    <article>
      <h1>{course.title}</h1>
      <p>{course.body}</p>
      <button onClick={refetch}>Refresh</button>
    </article>
  );
}

// =============================================================
// SECTION 9 — Loading State Patterns
// =============================================================
// Different UX patterns for communicating loading state:
// =============================================================

// Pattern A: Boolean loading state (shown in all examples above)
// const [isLoading, setIsLoading] = useState(false);

// Pattern B: Enum status — more expressive, avoids impossible states
// const [status, setStatus] = useState('idle'); // 'idle' | 'loading' | 'success' | 'error'
//
// <div>
//   {status === 'idle' && <p>Enter a search term above</p>}
//   {status === 'loading' && <Spinner />}
//   {status === 'success' && <CourseList courses={courses} />}
//   {status === 'error' && <ErrorMessage message={error} />}
// </div>

// Pattern C: Skeleton loaders — show placeholder shapes while loading
// {isLoading
//   ? Array.from({ length: 5 }).map((_, i) => <SkeletonCard key={i} />)
//   : courses.map(c => <CourseCard key={c.id} course={c} />)
// }

// =============================================================
// SECTION 10 — Multiple Concurrent Requests with Promise.all
// =============================================================
// When you need data from multiple endpoints simultaneously,
// Promise.all fires all requests in parallel and waits for all.
// =============================================================

export function CourseDashboard() {
  const [dashboard, setDashboard] = useState(null);
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState(null);

  useEffect(() => {
    setIsLoading(true);

    Promise.all([
      fetch(`${BASE_URL}/posts?_limit=5`).then(r => r.json()),
      fetch(`${BASE_URL}/users?_limit=3`).then(r => r.json()),
      fetch(`${BASE_URL}/todos?_limit=5`).then(r => r.json()),
    ])
      .then(([courses, instructors, tasks]) => {
        // All three requests resolved — destructure by position
        setDashboard({ courses, instructors, tasks });
      })
      .catch(err => setError(err.message))
      .finally(() => setIsLoading(false));
  }, []);

  if (isLoading) return <p>Loading dashboard...</p>;
  if (error) return <p>Error: {error}</p>;
  if (!dashboard) return null;

  return (
    <div>
      <h2>Courses ({dashboard.courses.length})</h2>
      <h2>Instructors ({dashboard.instructors.length})</h2>
      <h2>Tasks ({dashboard.tasks.length})</h2>
    </div>
  );
}

// =============================================================================
// Day 18a — React Routing & Redux
// FILE: 02-redux-toolkit-and-react.jsx
//
// Topics covered:
//   1. Redux Toolkit (RTK): why it exists and what problems it solves
//   2. configureStore — replaces createStore
//   3. createSlice — combines action types, action creators, and reducer in one
//   4. Redux DevTools integration
//   5. Connecting React to Redux with <Provider>
//   6. useSelector — reading state from the store
//   7. useDispatch — dispatching actions from components
//   8. State management patterns (normalised state, selectors, async thunks)
// =============================================================================
//
// Install (for reference):
//   npm install @reduxjs/toolkit react-redux
// =============================================================================

import React, { useEffect } from 'react';
import {
  configureStore,     // RTK's replacement for createStore — adds DevTools etc.
  createSlice,        // Generates action types + action creators + reducer
  createAsyncThunk,   // Handles async operations (API calls) in Redux
} from '@reduxjs/toolkit';
import {
  Provider,           // Wraps the React tree — makes store available to all components
  useSelector,        // Hook: reads data from the Redux store
  useDispatch,        // Hook: returns the dispatch function
} from 'react-redux';

// =============================================================================
// SECTION 1 — Why Redux Toolkit?
// =============================================================================

/**
 * Problems with vanilla Redux (File 01):
 *
 *   1. BOILERPLATE — separate files for action types, action creators, reducers
 *   2. IMMUTABILITY — easy to accidentally mutate state and introduce bugs
 *   3. SETUP — middleware, DevTools, combineReducers all need manual wiring
 *
 * Redux Toolkit (RTK) is the OFFICIAL recommended way to write Redux.
 * It eliminates all of the above:
 *
 *   1. createSlice generates action types + creators + reducer together
 *   2. Uses Immer internally — you CAN write "mutating" syntax, RTK makes
 *      it immutable under the hood
 *   3. configureStore sets up DevTools and common middleware automatically
 *
 * Watch out: RTK doesn't change HOW Redux works — the store, actions,
 * reducers, dispatch pattern is identical. It just removes the boilerplate.
 */

// =============================================================================
// SECTION 2 — coursesSlice: createSlice in action
// =============================================================================

/**
 * createSlice takes:
 *   - name:         used as prefix for action type strings ('courses/enroll')
 *   - initialState: the starting value for this slice of state
 *   - reducers:     an object of "case reducer" functions
 *
 * It returns:
 *   - slice.reducer:  the reducer function to pass to configureStore
 *   - slice.actions:  auto-generated action creators (one per case reducer)
 *
 * Inside reducers, you CAN write "mutating" code like state.enrolled.push(id).
 * RTK uses the Immer library to intercept the mutation and produce a new
 * immutable state.  The mutation ONLY works inside createSlice reducers.
 */

const SAMPLE_COURSES = [
  { id: 1, title: 'React Fundamentals',   category: 'frontend', price: 49 },
  { id: 2, title: 'Redux & State',        category: 'frontend', price: 39 },
  { id: 3, title: 'Spring Boot',          category: 'backend',  price: 59 },
  { id: 4, title: 'Docker & Kubernetes',  category: 'devops',   price: 69 },
  { id: 5, title: 'TypeScript Deep Dive', category: 'frontend', price: 44 },
];

const coursesSlice = createSlice({
  name: 'courses',
  initialState: {
    allCourses: SAMPLE_COURSES, // in a real app this would be fetched
    enrolledIds: [],            // array of course IDs the user is enrolled in
    loading: false,
    error: null,
  },
  reducers: {
    /**
     * enroll — add a course ID to enrolledIds.
     * `action.payload` is whatever is passed to dispatch(enroll(someId)).
     *
     * We write state.enrolledIds.push(...) — looks like mutation!
     * But Immer intercepts this and produces a brand-new immutable state.
     */
    enroll(state, action) {
      const courseId = action.payload;
      if (!state.enrolledIds.includes(courseId)) {
        state.enrolledIds.push(courseId); // Immer-safe "mutation"
      }
    },

    unenroll(state, action) {
      const courseId = action.payload;
      state.enrolledIds = state.enrolledIds.filter(id => id !== courseId);
    },
  },
  // extraReducers handles actions generated OUTSIDE this slice (e.g. thunks)
  extraReducers: (builder) => {
    builder
      .addCase(fetchCourses.pending, (state) => {
        state.loading = true;
        state.error   = null;
      })
      .addCase(fetchCourses.fulfilled, (state, action) => {
        state.loading    = false;
        state.allCourses = action.payload; // replace with fetched data
      })
      .addCase(fetchCourses.rejected, (state, action) => {
        state.loading = false;
        state.error   = action.error.message;
      });
  },
});

// Named exports — import these where you dispatch actions
export const { enroll, unenroll } = coursesSlice.actions;

// =============================================================================
// SECTION 3 — cartSlice
// =============================================================================

const cartSlice = createSlice({
  name: 'cart',
  initialState: {
    items: [],   // [{ id, title, price }]
  },
  reducers: {
    addToCart(state, action) {
      const item = action.payload;
      const exists = state.items.some(i => i.id === item.id);
      if (!exists) {
        state.items.push(item); // Immer makes this safe
      }
    },

    removeFromCart(state, action) {
      state.items = state.items.filter(item => item.id !== action.payload);
    },

    clearCart(state) {
      state.items = []; // reassignment is also Immer-safe
    },
  },
});

export const { addToCart, removeFromCart, clearCart } = cartSlice.actions;

// =============================================================================
// SECTION 4 — authSlice
// =============================================================================

const authSlice = createSlice({
  name: 'auth',
  initialState: {
    user: null,
    isLoggedIn: false,
  },
  reducers: {
    login(state, action) {
      state.user       = action.payload;
      state.isLoggedIn = true;
    },
    logout(state) {
      state.user       = null;
      state.isLoggedIn = false;
    },
  },
});

export const { login, logout } = authSlice.actions;

// =============================================================================
// SECTION 5 — createAsyncThunk (async action creators)
// =============================================================================

/**
 * createAsyncThunk(typePrefix, payloadCreator)
 *
 * Handles async operations (API calls) cleanly in Redux.
 * Automatically dispatches three action types:
 *   - 'courses/fetchAll/pending'   — when the async call starts
 *   - 'courses/fetchAll/fulfilled' — when it succeeds (payload = return value)
 *   - 'courses/fetchAll/rejected'  — when it throws (error captured automatically)
 *
 * You then handle these in extraReducers inside the slice (see above).
 *
 * Watch out: the thunk itself is NOT in reducers — it's defined separately
 * because it's async (side effects are forbidden inside reducers).
 */
export const fetchCourses = createAsyncThunk(
  'courses/fetchAll',
  async () => {
    // Simulate an API call
    const response = await new Promise(resolve =>
      setTimeout(() => resolve(SAMPLE_COURSES), 800)
    );
    return response; // becomes action.payload in 'fulfilled'
  }
);

// =============================================================================
// SECTION 6 — configureStore: creating the Redux store with RTK
// =============================================================================

/**
 * configureStore automatically:
 *   - Calls combineReducers for you
 *   - Adds Redux DevTools Extension support
 *   - Adds redux-thunk middleware (for async thunks)
 *   - Adds serializable state checks in development
 *
 * Compare to vanilla:
 *   createStore(combineReducers({...}), applyMiddleware(thunk))
 *   + manual DevTools wiring
 */
const store = configureStore({
  reducer: {
    courses: coursesSlice.reducer,
    cart:    cartSlice.reducer,
    auth:    authSlice.reducer,
  },
  // Optional: add extra middleware or disable serializability checks
  // middleware: (getDefaultMiddleware) => getDefaultMiddleware().concat(myMiddleware),
});

// =============================================================================
// SECTION 7 — Selectors: reading state cleanly
// =============================================================================

/**
 * STATE MANAGEMENT PATTERN: Selectors
 *
 * Instead of accessing store.state.courses.enrolledIds directly everywhere,
 * define selector functions.  Benefits:
 *   1. Single place to change if the state shape changes
 *   2. Composable — combine selectors to derive data
 *   3. Memoizable — use createSelector (reselect) for expensive derivations
 *
 * Convention: name selector functions with the `select` prefix.
 */

// Simple selectors — directly read a slice of state
const selectEnrolledIds   = state => state.courses.enrolledIds;
const selectAllCourses    = state => state.courses.allCourses;
const selectCartItems     = state => state.cart.items;
const selectCurrentUser   = state => state.auth.user;
const selectIsLoggedIn    = state => state.auth.isLoggedIn;

// Derived selector — computes enrolled course objects from two pieces of state
const selectEnrolledCourses = state => {
  const { allCourses, enrolledIds } = state.courses;
  return allCourses.filter(course => enrolledIds.includes(course.id));
};

// Derived selector — total cart price
const selectCartTotal = state => {
  return state.cart.items.reduce((sum, item) => sum + item.price, 0);
};

// =============================================================================
// SECTION 8 — React components connected to Redux
// =============================================================================

/**
 * useSelector(selectorFn)
 *   - Subscribes the component to the Redux store.
 *   - Calls selectorFn(state) and returns the result.
 *   - Re-renders the component when the selected value CHANGES.
 *   - Uses reference equality by default — return primitives or stable
 *     references where possible.
 *
 * useDispatch()
 *   - Returns the store's dispatch function.
 *   - Call dispatch(actionCreator(payload)) to trigger a state change.
 */

// ---- CourseCard component ----

function CourseCard({ course }) {
  const dispatch     = useDispatch();
  const enrolledIds  = useSelector(selectEnrolledIds);
  const cartItems    = useSelector(selectCartItems);

  const isEnrolled = enrolledIds.includes(course.id);
  const isInCart   = cartItems.some(item => item.id === course.id);

  function handleEnroll() {
    dispatch(enroll(course.id));
  }

  function handleUnenroll() {
    dispatch(unenroll(course.id));
  }

  function handleAddToCart() {
    dispatch(addToCart({ id: course.id, title: course.title, price: course.price }));
  }

  function handleRemoveFromCart() {
    dispatch(removeFromCart(course.id));
  }

  return (
    <div style={{
      border: '1px solid #ddd',
      borderRadius: '8px',
      padding: '1rem',
      marginBottom: '0.75rem',
      background: isEnrolled ? '#f0fff0' : '#fff',
    }}>
      <h3 style={{ margin: 0 }}>{course.title}</h3>
      <p style={{ color: '#555', margin: '0.25rem 0' }}>
        {course.category} · ${course.price}
        {isEnrolled && <strong style={{ color: 'green', marginLeft: '0.5rem' }}>✓ Enrolled</strong>}
      </p>

      <div style={{ display: 'flex', gap: '0.5rem', flexWrap: 'wrap' }}>
        {!isEnrolled ? (
          <button onClick={handleEnroll}   style={btnStyle('#0070f3')}>Enroll</button>
        ) : (
          <button onClick={handleUnenroll} style={btnStyle('#e53e3e')}>Unenroll</button>
        )}

        {!isInCart ? (
          <button onClick={handleAddToCart}    style={btnStyle('#38a169')}>Add to Cart</button>
        ) : (
          <button onClick={handleRemoveFromCart} style={btnStyle('#718096')}>Remove from Cart</button>
        )}
      </div>
    </div>
  );
}

const btnStyle = (bg) => ({
  padding: '0.3rem 0.75rem',
  background: bg,
  color: '#fff',
  border: 'none',
  borderRadius: '4px',
  cursor: 'pointer',
  fontSize: '0.85rem',
});

// ---- CourseList component ----

function CourseList() {
  const dispatch    = useDispatch();
  const allCourses  = useSelector(selectAllCourses);
  const loading     = useSelector(state => state.courses.loading);
  const error       = useSelector(state => state.courses.error);

  // Dispatch the async thunk on mount
  useEffect(() => {
    dispatch(fetchCourses());
  }, [dispatch]);

  if (loading) return <p style={{ padding: '2rem' }}>Loading courses…</p>;
  if (error)   return <p style={{ padding: '2rem', color: 'red' }}>Error: {error}</p>;

  return (
    <div>
      <h2>All Courses</h2>
      {allCourses.map(course => (
        <CourseCard key={course.id} course={course} />
      ))}
    </div>
  );
}

// ---- CartSidebar component ----

function CartSidebar() {
  const dispatch  = useDispatch();
  const cartItems = useSelector(selectCartItems);
  const total     = useSelector(selectCartTotal);

  return (
    <aside style={{
      width: '260px',
      border: '1px solid #ddd',
      borderRadius: '8px',
      padding: '1rem',
      alignSelf: 'flex-start',
      background: '#fafafa',
    }}>
      <h2>🛒 Cart ({cartItems.length})</h2>

      {cartItems.length === 0 ? (
        <p style={{ color: '#888' }}>Your cart is empty.</p>
      ) : (
        <>
          <ul style={{ listStyle: 'none', padding: 0, margin: 0 }}>
            {cartItems.map(item => (
              <li key={item.id} style={{
                display: 'flex',
                justifyContent: 'space-between',
                alignItems: 'center',
                marginBottom: '0.5rem',
              }}>
                <span style={{ fontSize: '0.9rem' }}>{item.title}</span>
                <span>
                  ${item.price}
                  <button
                    onClick={() => dispatch(removeFromCart(item.id))}
                    style={{ marginLeft: '0.5rem', cursor: 'pointer', border: 'none', background: 'none', color: 'red' }}
                  >
                    ✕
                  </button>
                </span>
              </li>
            ))}
          </ul>

          <hr />
          <p style={{ fontWeight: 'bold', display: 'flex', justifyContent: 'space-between' }}>
            <span>Total:</span>
            <span>${total}</span>
          </p>

          <button
            onClick={() => dispatch(clearCart())}
            style={{ ...btnStyle('#e53e3e'), width: '100%', padding: '0.5rem' }}
          >
            Clear Cart
          </button>
        </>
      )}
    </aside>
  );
}

// ---- EnrolledList component ----

function EnrolledList() {
  const enrolledCourses = useSelector(selectEnrolledCourses);

  if (enrolledCourses.length === 0) {
    return (
      <div style={{ marginTop: '1.5rem' }}>
        <h2>My Enrolled Courses</h2>
        <p style={{ color: '#888' }}>Enroll in a course above to see it here.</p>
      </div>
    );
  }

  return (
    <div style={{ marginTop: '1.5rem' }}>
      <h2>My Enrolled Courses ({enrolledCourses.length})</h2>
      <ul>
        {enrolledCourses.map(c => (
          <li key={c.id}>{c.title}</li>
        ))}
      </ul>
    </div>
  );
}

// ---- AuthBar ----

function AuthBar() {
  const dispatch  = useDispatch();
  const user      = useSelector(selectCurrentUser);
  const isLoggedIn = useSelector(selectIsLoggedIn);

  function handleLogin() {
    dispatch(login({ username: 'alice', email: 'alice@example.com' }));
  }

  function handleLogout() {
    dispatch(logout());
  }

  return (
    <div style={{
      padding: '0.75rem 2rem',
      background: '#333',
      color: '#fff',
      display: 'flex',
      justifyContent: 'space-between',
      alignItems: 'center',
    }}>
      <span style={{ fontWeight: 'bold' }}>DevAcademy Store</span>

      {isLoggedIn ? (
        <span>
          👤 {user.username}
          <button
            onClick={handleLogout}
            style={{ marginLeft: '1rem', ...btnStyle('#718096') }}
          >
            Logout
          </button>
        </span>
      ) : (
        <button onClick={handleLogin} style={btnStyle('#0070f3')}>
          Login as Alice
        </button>
      )}
    </div>
  );
}

// =============================================================================
// SECTION 9 — Provider: connecting React to Redux
// =============================================================================

/**
 * <Provider store={store}> uses React Context to make the store available
 * to every component in the tree — without prop drilling.
 *
 * useSelector and useDispatch hooks only work inside a <Provider>.
 * Wrap your entire app at the root level (typically in index.jsx or App.jsx).
 *
 * Watch out: if you forget the Provider, you'll get an error:
 *   "could not find react-redux context value;
 *    make sure the component is wrapped in a <Provider>"
 */

export default function ReduxApp() {
  return (
    <Provider store={store}>
      <AuthBar />
      <div style={{ display: 'flex', gap: '1.5rem', padding: '1.5rem' }}>
        {/* Main content area */}
        <div style={{ flex: 1 }}>
          <CourseList />
          <EnrolledList />
        </div>

        {/* Sidebar */}
        <CartSidebar />
      </div>

      <DevToolsNote />
    </Provider>
  );
}

// =============================================================================
// SECTION 10 — Redux DevTools
// =============================================================================

/**
 * How to use Redux DevTools:
 *   1. Install the "Redux DevTools" browser extension (Chrome/Firefox)
 *   2. Run your app with configureStore — DevTools support is ON by default
 *   3. Open DevTools → Redux tab
 *
 * Features:
 *   - Action log: every dispatched action listed chronologically
 *   - Diff view: see exactly what changed in the state tree
 *   - State tree: browse the current store at any point in time
 *   - Time travel: jump back and forth through the history of state changes
 *   - Import/export state: share store snapshots for bug reproduction
 *
 * Watch out: configureStore only enables DevTools in development.
 * In a production build they are disabled automatically.
 */

function DevToolsNote() {
  return (
    <div style={{
      margin: '1rem 1.5rem',
      padding: '1rem',
      background: '#fffbe6',
      border: '1px solid #f6e05e',
      borderRadius: '8px',
      fontSize: '0.9rem',
    }}>
      <strong>🔧 Redux DevTools:</strong> Open your browser DevTools → Redux tab.
      Dispatch actions above and watch the action log, state diff, and time-travel
      controls update in real time.
      <br /><br />
      configureStore enables DevTools automatically in development mode.
      No extra setup required!
    </div>
  );
}

// =============================================================================
// SECTION 11 — State management patterns summary
// =============================================================================

/**
 * PATTERN 1: NORMALISED STATE
 * ───────────────────────────
 * Instead of storing an array of objects, store them in a dictionary keyed
 * by ID.  RTK's `createEntityAdapter` does this automatically.
 *
 *   // Denormalised (slow lookups):
 *   courses: [{ id: 1, title: '...' }, { id: 2, title: '...' }]
 *
 *   // Normalised (O(1) lookup by id):
 *   courses: {
 *     byId: { 1: { id: 1, title: '...' }, 2: { id: 2, title: '...' } },
 *     allIds: [1, 2]
 *   }
 *
 * Use normalised state when you have many items and need fast lookups.
 *
 *
 * PATTERN 2: SLICE OWNERSHIP
 * ──────────────────────────
 * Each feature owns its own slice.  The UI slice and data slice are separate.
 * Avoid putting UI state (isModalOpen) in the same slice as server data.
 *
 *
 * PATTERN 3: SELECTOR COLOCATION
 * ───────────────────────────────
 * Define selectors in the same file as the slice they read.
 * Export them and import where needed.  This keeps selector logic close to
 * the state shape it accesses.
 *
 *
 * PATTERN 4: WHAT BELONGS IN REDUX vs LOCAL STATE
 * ─────────────────────────────────────────────────
 *   Redux (global):
 *     - User authentication / profile
 *     - Data fetched from APIs (courses, cart, orders)
 *     - App-wide UI state (theme, language, notifications)
 *
 *   Local useState (component):
 *     - Form field values
 *     - isOpen (modal, dropdown)
 *     - Hover/focus state
 *     - Anything that only ONE component cares about
 *
 * Rule of thumb: if two or more components need the same piece of state,
 * or if the state needs to persist across page navigations, use Redux.
 * Otherwise, keep it local.
 */

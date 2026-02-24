// =============================================================================
// Day 18a — React Routing & Redux
// FILE: 01-redux-fundamentals.js
//
// Topics covered:
//   1. Why Redux? The problem with prop drilling and scattered state
//   2. The three core concepts: Store, Action, Reducer
//   3. Pure functions and immutability
//   4. Creating a store manually with createStore (vanilla Redux)
//   5. Dispatching actions
//   6. Reading state with getState()
//   7. Subscribing to store changes
//   8. Combining reducers
//   9. The Redux data flow (one-way)
// =============================================================================
//
// NOTE: This file uses VANILLA Redux (redux package, no React).
// It can be run in any Node.js environment — no browser or React needed.
// This isolates the Redux concepts from React so students can focus on the
// core ideas before we hook them up to React in the next file.
//
// Install (for reference): npm install redux
// =============================================================================

// We're importing the legacy createStore from Redux.
// Redux Toolkit (next file) replaces this — but you MUST understand the
// fundamentals before using the abstraction.
const { createStore, combineReducers } = require('redux');

// =============================================================================
// SECTION 1 — The problem Redux solves
// =============================================================================

/**
 * WITHOUT Redux:
 *   App
 *    ├─ NavBar  (needs: user, cartCount)
 *    ├─ CourseList
 *    │   └─ CourseCard (needs: enrolledIds to show "enrolled" badge)
 *    └─ Sidebar
 *        └─ EnrolledWidget (needs: enrolledIds, user)
 *
 * To share `enrolledIds` between CourseCard and EnrolledWidget, you'd have
 * to lift it all the way up to App, then drill it down through every
 * intermediate component.  That's "prop drilling" — messy and fragile.
 *
 * WITH Redux:
 *   - State lives in a central STORE outside the component tree.
 *   - Any component can read from the store via `useSelector`.
 *   - Any component can update the store via `useDispatch`.
 *   - No prop drilling needed.
 */

// =============================================================================
// SECTION 2 — Actions: plain objects that describe WHAT happened
// =============================================================================

/**
 * An action is a plain JavaScript object with:
 *   - type:    string describing the event (required)
 *   - payload: any additional data (optional, by convention)
 *
 * Think of actions like events: "the user enrolled in course 3"
 * The action doesn't DO anything — it just describes what happened.
 * The reducer decides WHAT to change in response.
 *
 * Convention: use SCREAMING_SNAKE_CASE for action type strings.
 * Many teams namespace them: 'courses/enroll', 'cart/addItem'.
 */

// Action type constants — never hard-code strings in action creators
const ENROLL_COURSE   = 'courses/enroll';
const UNENROLL_COURSE = 'courses/unenroll';
const ADD_TO_CART     = 'cart/addItem';
const REMOVE_FROM_CART = 'cart/removeItem';
const SET_CURRENT_USER = 'auth/setUser';
const LOGOUT           = 'auth/logout';

/**
 * Action creators — functions that return action objects.
 *
 * We could write action objects inline, but action creators:
 *   1. Centralize action construction (change type string in one place)
 *   2. Make dispatch calls readable: dispatch(enrollCourse(3))
 *      vs dispatch({ type: 'courses/enroll', payload: 3 })
 */
function enrollCourse(courseId) {
  return { type: ENROLL_COURSE, payload: courseId };
}

function unenrollCourse(courseId) {
  return { type: UNENROLL_COURSE, payload: courseId };
}

function addToCart(item) {
  return { type: ADD_TO_CART, payload: item };
}

function removeFromCart(itemId) {
  return { type: REMOVE_FROM_CART, payload: itemId };
}

function setCurrentUser(user) {
  return { type: SET_CURRENT_USER, payload: user };
}

function logout() {
  return { type: LOGOUT };
}

// =============================================================================
// SECTION 3 — Reducers: pure functions that compute the next state
// =============================================================================

/**
 * A reducer is a PURE FUNCTION:
 *   (currentState, action) => nextState
 *
 * Pure means:
 *   1. Same inputs → always same output (no randomness, no side effects)
 *   2. NEVER mutates the existing state — always returns a NEW object
 *   3. No API calls, no setTimeout, no console.log (side effects)
 *
 * Why immutability?
 *   Redux detects changes by reference equality: old state === new state?
 *   If you mutate (e.g. state.enrolled.push(id)), the reference doesn't
 *   change — Redux thinks nothing happened, and components don't re-render.
 *
 * Pattern: (state = initialState, action) => { switch ... }
 *   - The default parameter sets initial state on first call.
 *   - The switch handles each action type.
 *   - The default case returns the UNCHANGED state (required).
 */

// ---- Courses reducer (manages enrolled course IDs) ----

const coursesInitialState = {
  enrolled: [],   // array of course IDs the user has enrolled in
};

function coursesReducer(state = coursesInitialState, action) {
  switch (action.type) {

    case ENROLL_COURSE:
      // Already enrolled? Don't add a duplicate
      if (state.enrolled.includes(action.payload)) return state;
      // Return a NEW object with a NEW array (spread operator = shallow copy)
      return {
        ...state,
        enrolled: [...state.enrolled, action.payload],
      };

    case UNENROLL_COURSE:
      return {
        ...state,
        enrolled: state.enrolled.filter(id => id !== action.payload),
      };

    default:
      // MUST return current state for unrecognised actions
      return state;
  }
}

// ---- Cart reducer (manages items in a shopping cart) ----

const cartInitialState = {
  items: [],   // [{ id, title, price }]
};

function cartReducer(state = cartInitialState, action) {
  switch (action.type) {

    case ADD_TO_CART: {
      // Guard: don't add if already in cart
      const alreadyInCart = state.items.some(item => item.id === action.payload.id);
      if (alreadyInCart) return state;
      return {
        ...state,
        items: [...state.items, action.payload],
      };
    }

    case REMOVE_FROM_CART:
      return {
        ...state,
        items: state.items.filter(item => item.id !== action.payload),
      };

    default:
      return state;
  }
}

// ---- Auth reducer (manages logged-in user) ----

const authInitialState = {
  user: null,      // null = not logged in
  isLoggedIn: false,
};

function authReducer(state = authInitialState, action) {
  switch (action.type) {

    case SET_CURRENT_USER:
      return {
        ...state,
        user: action.payload,
        isLoggedIn: true,
      };

    case LOGOUT:
      return {
        ...state,
        user: null,
        isLoggedIn: false,
      };

    default:
      return state;
  }
}

// =============================================================================
// SECTION 4 — Combining reducers
// =============================================================================

/**
 * combineReducers — splits the state tree into slices.
 * Each reducer "owns" one slice of the global state.
 *
 * Resulting state shape:
 * {
 *   courses: { enrolled: [] },
 *   cart:    { items: [] },
 *   auth:    { user: null, isLoggedIn: false },
 * }
 *
 * When an action is dispatched, combineReducers calls EVERY reducer
 * and assembles the new root state from their individual return values.
 */
const rootReducer = combineReducers({
  courses: coursesReducer,
  cart:    cartReducer,
  auth:    authReducer,
});

// =============================================================================
// SECTION 5 — Creating the store
// =============================================================================

/**
 * createStore(rootReducer)
 *
 * The store is a single JavaScript object that holds:
 *   1. The current state tree
 *   2. A dispatch() method to send actions
 *   3. A getState() method to read current state
 *   4. A subscribe() method to listen for changes
 *
 * There is only ONE store in a Redux application.
 *
 * Watch out: createStore is technically deprecated in modern Redux — use
 * configureStore from Redux Toolkit instead (next file). We use it here
 * to understand the fundamentals without any magic.
 */
const store = createStore(rootReducer);

// =============================================================================
// SECTION 6 — Subscribing to state changes
// =============================================================================

/**
 * store.subscribe(callback) registers a function to run every time
 * an action is dispatched and the state may have changed.
 *
 * It returns an unsubscribe function — call it to stop listening.
 *
 * In React: you'll never call subscribe() directly — useSelector() does
 * it for you. But it's useful for understanding what's happening under the hood.
 */
const unsubscribe = store.subscribe(() => {
  // This runs after every dispatched action
  const state = store.getState();
  console.log('\n--- State after action ---');
  console.log('Enrolled courses:', state.courses.enrolled);
  console.log('Cart items:', state.cart.items.map(i => i.title));
  console.log('Logged in as:', state.auth.user?.username ?? 'not logged in');
});

// =============================================================================
// SECTION 7 — Dispatching actions and reading state
// =============================================================================

console.log('=== Redux Fundamentals Demo ===\n');

// Read initial state
console.log('Initial state:', store.getState());

// 1. Log in a user
store.dispatch(setCurrentUser({ username: 'alice', email: 'alice@example.com' }));

// 2. Enroll in some courses
store.dispatch(enrollCourse(101));
store.dispatch(enrollCourse(102));

// 3. Dispatch the same action twice — reducer guards against duplicates
store.dispatch(enrollCourse(101));  // no change — already enrolled

// 4. Add to cart
store.dispatch(addToCart({ id: 101, title: 'React Fundamentals', price: 49.99 }));
store.dispatch(addToCart({ id: 102, title: 'Redux & State',       price: 39.99 }));

// 5. Remove from cart
store.dispatch(removeFromCart(101));

// 6. Unenroll from a course
store.dispatch(unenrollCourse(102));

// 7. Read final state
const finalState = store.getState();
console.log('\n=== Final State ===');
console.log(JSON.stringify(finalState, null, 2));

// 8. Stop listening
unsubscribe();

// Dispatch after unsubscribe — subscriber won't fire, but state still updates
store.dispatch(logout());
console.log('\nAfter logout (unsubscribed, no log):', store.getState().auth);

// =============================================================================
// SECTION 8 — The Redux data flow (one-way)
// =============================================================================

/**
 * ┌────────────────────────────────────────────────────────────────────┐
 * │                    REDUX DATA FLOW (one-way)                       │
 * │                                                                    │
 * │  User interaction                                                  │
 * │       │                                                            │
 * │       ▼                                                            │
 * │  dispatch(action)  ←─── action creator returns { type, payload }  │
 * │       │                                                            │
 * │       ▼                                                            │
 * │  Root Reducer  (calls every slice reducer)                        │
 * │       │                                                            │
 * │       ▼                                                            │
 * │  New State Tree  (immutable — new objects/arrays)                 │
 * │       │                                                            │
 * │       ▼                                                            │
 * │  Store updated  →  subscribers notified  →  React re-renders      │
 * └────────────────────────────────────────────────────────────────────┘
 *
 * Key properties:
 *   - ONE store, ONE state tree
 *   - State is READ-ONLY — you can't change it directly
 *   - Changes happen ONLY via dispatched actions
 *   - Reducers are PURE — predictable, testable, time-travel-debuggable
 */

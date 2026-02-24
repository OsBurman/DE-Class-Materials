// ============================================================
// Day 16a — React Fundamentals
// File 1: React Overview, Philosophy & SPAs
// ============================================================
// Run these examples by pasting into a React project.
// Quick setup:
//   npx create-react-app react-fundamentals
//   cd react-fundamentals && npm start
// Or use the online sandbox: https://codesandbox.io
// ============================================================

// ─── SECTION 1: REACT OVERVIEW & PHILOSOPHY ─────────────────
//
// React is a JavaScript LIBRARY (not a full framework) created
// by Facebook in 2013. Its job: build user interfaces.
//
// React's three core philosophies:
//
//  1. DECLARATIVE — You describe *what* the UI should look like
//                   for a given state. React figures out *how*
//                   to update the DOM to match.
//
//     Imperative (vanilla JS):
//       const el = document.createElement('h1');
//       el.textContent = 'Hello';
//       el.style.color = 'blue';
//       document.body.appendChild(el);
//
//     Declarative (React):
//       <h1 style={{ color: 'blue' }}>Hello</h1>
//
//  2. COMPONENT-BASED — UIs are built from small, reusable,
//                       self-contained pieces called components.
//                       A component owns its structure, style,
//                       and behavior.
//
//  3. LEARN ONCE, WRITE ANYWHERE — Same React patterns work for
//     web (react-dom), mobile (React Native), desktop, VR (React 360).
//
// ─── SECTION 2: SINGLE PAGE APPLICATION (SPA) ───────────────
//
// Traditional Multi-Page App (MPA):
//   Browser requests /home  → server sends home.html
//   Browser requests /about → server sends about.html
//   Each navigation = full page reload = flicker + delay
//
//   Client                          Server
//   ──────                          ──────
//   GET /home          →            returns home.html
//   [user clicks About]
//   GET /about         →            returns about.html  ← full reload
//
//
// Single Page Application (SPA):
//   Browser requests /         → server sends ONE index.html + JS bundle
//   All subsequent navigation  = JavaScript swaps content in place
//                                NO full page reload
//
//   Client                          Server
//   ──────                          ──────
//   GET /             →            returns index.html + app.js  ← once
//   [user clicks About]
//   JS updates the DOM             (no server request needed)
//
// Benefits of SPAs:
//   ✅ Faster navigation after initial load
//   ✅ App-like feel (no page flicker)
//   ✅ Better separation: server serves data (API), client handles UI
//
// Trade-offs:
//   ⚠️ Larger initial bundle (first load can be slower)
//   ⚠️ SEO requires extra work (server-side rendering or pre-rendering)
//   ⚠️ History/back-button management must be handled in JS (React Router)
//

// ─── SECTION 3: VIRTUAL DOM & RECONCILIATION ────────────────
//
// The Problem with Direct DOM Manipulation:
//   The browser's DOM is slow to update. Every time you change a DOM
//   node, the browser may reflow (recalculate layout) and repaint.
//   Doing this many times per second in complex apps causes jank.
//
//
// React's Solution — The Virtual DOM:
//
//   Step 1: React keeps a lightweight JavaScript COPY of the DOM
//           called the Virtual DOM (V-DOM).
//
//   Step 2: When state changes, React builds a NEW Virtual DOM tree.
//
//   Step 3: React DIFFS the new V-DOM against the previous V-DOM
//           to find the minimum set of changes needed.
//           This process is called RECONCILIATION.
//
//   Step 4: React applies ONLY those changes to the real DOM
//           in a single batch — minimizing expensive DOM operations.
//
//
//   Your Code → Virtual DOM (JS objects) → Diff → Patch Real DOM
//
//   Think of it like editing a document:
//   Bad approach: reprint the entire book every time you change a word.
//   React's approach: mark only the changed sentences and reprint those.
//
//
// Reconciliation Key Rules:
//   - Elements of different TYPES are replaced entirely
//   - Elements of the same type: React updates only changed attributes
//   - Lists: React uses the `key` prop to identify which items changed
//             (covered in Part 2)
//

// ─── LIVE ILLUSTRATION: React is already in the page ─────────
//
// Every React app starts with a single root element in index.html:
//   <div id="root"></div>
//
// And a single entry point in index.js (or main.jsx in Vite):
//   import React from 'react';
//   import ReactDOM from 'react-dom/client';
//   import App from './App';
//
//   const root = ReactDOM.createRoot(document.getElementById('root'));
//   root.render(<App />);
//
// React takes over that <div id="root"> and manages everything inside it.
// The rest of the HTML file stays static — React only touches its root.
//

// ─── SUMMARY ────────────────────────────────────────────────
//
//  React  = declarative + component-based UI library
//  SPA    = one HTML load, JS handles all navigation
//  V-DOM  = JS copy of the DOM; React diffs + patches efficiently
//
// ─── NEXT FILE: 02-jsx-syntax-and-rules.jsx ─────────────────

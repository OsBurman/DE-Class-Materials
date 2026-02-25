// ── Day 16a Part 1 App — JSX, Functional Components, Virtual DOM ──────────────
//
// Run: npm install && npm run dev
//
// This app demonstrates:
//  1. JSX syntax — HTML-like syntax in JavaScript
//  2. Functional components — JavaScript functions that return JSX
//  3. Component tree — composing smaller components into a larger UI
//  4. How React re-renders (Virtual DOM concept)

import React from 'react'

// ── Styles (inline for simplicity) ───────────────────────────────────────────
const styles = {
  page:    { maxWidth: 900, margin: '0 auto', padding: '2rem 1rem' },
  header:  { background: '#1e3a5f', color: 'white', padding: '2rem', borderRadius: 8, marginBottom: '2rem' },
  section: { background: 'white', borderRadius: 8, padding: '1.5rem', marginBottom: '1.5rem',
             boxShadow: '0 2px 8px rgba(0,0,0,.08)' },
  h2:      { color: '#1e3a5f', marginBottom: '1rem', borderBottom: '2px solid #e06c1b', paddingBottom: '.4rem' },
  code:    { background: '#f0f0f0', padding: '2px 6px', borderRadius: 3, fontFamily: 'monospace', fontSize: '.9em' },
  pre:     { background: '#1e1e1e', color: '#d4e157', padding: '1rem', borderRadius: 6, fontFamily: 'monospace',
             fontSize: '.85rem', overflowX: 'auto', lineHeight: 1.6, marginBottom: '.5rem' },
}

// ─────────────────────────────────────────────────────────────────────────────
// 1. SIMPLE FUNCTIONAL COMPONENT — takes no props
// ─────────────────────────────────────────────────────────────────────────────
function Hero() {
  // JSX is transpiled to React.createElement calls by Babel
  // <div className="hero"> becomes React.createElement('div', {className:'hero'}, ...)
  return (
    <div style={styles.header}>
      <h1>⚛️  Day 16a Part 1 — React Fundamentals</h1>
      <p style={{ color: '#b0c8e8', marginTop: '.5rem' }}>
        npm install &amp;&amp; npm run dev | Open http://localhost:5173
      </p>
    </div>
  )
}

// ─────────────────────────────────────────────────────────────────────────────
// 2. JSX RULES DEMO
// ─────────────────────────────────────────────────────────────────────────────
function JsxRulesDemo() {
  const courseName = 'React Fundamentals'
  const isReact    = true
  const items      = ['Component', 'JSX', 'Props', 'State']

  return (
    <div style={styles.section}>
      <h2 style={styles.h2}>1. JSX Syntax &amp; Rules</h2>

      {/* Rule 1: Must return a single root element (or Fragment) */}
      {/* Rule 2: class → className, for → htmlFor */}
      {/* Rule 3: All tags must be closed (including self-closing) */}
      {/* Rule 4: JavaScript expressions go inside {} */}

      <p>JavaScript expression: <strong>{courseName}</strong></p>
      <p>Ternary: {isReact ? '✅ This IS React' : '❌ Not React'}</p>
      <p>Array map: {items.map((item, i) => <span key={i} style={styles.code}>{item} </span>)}</p>
      <p style={{ marginTop: '.8rem', fontStyle: 'italic', color: '#555' }}>
        use className (not class), htmlFor (not for), camelCase event handlers (onClick not onclick)
      </p>

      <pre style={styles.pre}>{`// JSX is sugar for React.createElement:
<h1 className="title">Hello</h1>
// compiles to →
React.createElement('h1', {className: 'title'}, 'Hello')`}</pre>
    </div>
  )
}

// ─────────────────────────────────────────────────────────────────────────────
// 3. FUNCTIONAL COMPONENT TYPES
// ─────────────────────────────────────────────────────────────────────────────
// Named function declaration
function GreetingCard() {
  return <div style={{ background:'#e8f0fe', padding:'.8rem', borderRadius:6, marginBottom:'.5rem' }}>📄 Function Declaration Component</div>
}

// Arrow function expression (most common modern style)
const InfoBadge = () => (
  <div style={{ background:'#e8f5e9', padding:'.8rem', borderRadius:6, marginBottom:'.5rem' }}>🏷️ Arrow Function Component</div>
)

function ComponentTypesDemo() {
  return (
    <div style={styles.section}>
      <h2 style={styles.h2}>2. Functional Components</h2>
      <p style={{ marginBottom:'1rem' }}>Two ways to write a component — both produce identical results:</p>
      <GreetingCard />
      <InfoBadge />
      <pre style={styles.pre}>{`// 1. Function Declaration
function MyComponent() {
  return <div>Hello!</div>
}

// 2. Arrow Function (modern standard)
const MyComponent = () => <div>Hello!</div>`}</pre>
    </div>
  )
}

// ─────────────────────────────────────────────────────────────────────────────
// 4. COMPONENT TREE / COMPOSITION
// ─────────────────────────────────────────────────────────────────────────────
function CourseIcon({ emoji }) {
  return <span style={{ fontSize: '2rem' }}>{emoji}</span>
}

function CourseTitle({ title, level }) {
  return (
    <div>
      <h3 style={{ margin: 0, color: '#1e3a5f' }}>{title}</h3>
      <small style={{ color: '#888' }}>{level}</small>
    </div>
  )
}

function CourseCard({ emoji, title, level, color }) {
  return (
    <div style={{
      display: 'flex', gap: '1rem', alignItems: 'center',
      background: color, padding: '.8rem 1rem', borderRadius: 8, marginBottom: '.5rem'
    }}>
      <CourseIcon emoji={emoji} />
      <CourseTitle title={title} level={level} />
    </div>
  )
}

function CompositionDemo() {
  return (
    <div style={styles.section}>
      <h2 style={styles.h2}>3. Component Composition (Component Tree)</h2>
      <p style={{ marginBottom:'1rem', color:'#555' }}>
        App → CourseCard → CourseIcon + CourseTitle (components inside components)
      </p>
      <CourseCard emoji="☕" title="Java Fundamentals" level="Week 1-2"  color="#fff9c4" />
      <CourseCard emoji="🌐" title="HTML & CSS"         level="Week 3"   color="#e8f5e9" />
      <CourseCard emoji="⚛️" title="React"             level="Week 4"   color="#e3f2fd" />
      <CourseCard emoji="🍃" title="Spring Boot"        level="Week 5-6" color="#fce4ec" />
    </div>
  )
}

// ─────────────────────────────────────────────────────────────────────────────
// 5. VIRTUAL DOM EXPLANATION
// ─────────────────────────────────────────────────────────────────────────────
function VirtualDomDemo() {
  return (
    <div style={styles.section}>
      <h2 style={styles.h2}>4. Virtual DOM &amp; React's Reconciliation</h2>
      <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr 1fr', gap: '1rem', textAlign: 'center' }}>
        <div style={{ background: '#fff9c4', padding: '1rem', borderRadius: 8 }}>
          <strong>1. State Changes</strong>
          <br /><br />
          <code style={styles.code}>setState(...)</code>
          <p style={{ fontSize: '.85rem', marginTop: '.5rem', color: '#555' }}>triggers re-render</p>
        </div>
        <div style={{ background: '#e8f5e9', padding: '1rem', borderRadius: 8 }}>
          <strong>2. Virtual DOM Diff</strong>
          <br /><br />
          <code style={styles.code}>reconcile()</code>
          <p style={{ fontSize: '.85rem', marginTop: '.5rem', color: '#555' }}>compare old vs new</p>
        </div>
        <div style={{ background: '#e3f2fd', padding: '1rem', borderRadius: 8 }}>
          <strong>3. Minimal DOM Update</strong>
          <br /><br />
          <code style={styles.code}>patch(DOM)</code>
          <p style={{ fontSize: '.85rem', marginTop: '.5rem', color: '#555' }}>only changed nodes</p>
        </div>
      </div>
      <p style={{ marginTop: '1rem', color: '#555', fontSize: '.9rem' }}>
        React never touches the real DOM directly. It maintains an in-memory Virtual DOM,
        diffs old vs new, then applies only the minimal set of real DOM changes — making UIs fast.
      </p>
    </div>
  )
}

// ─────────────────────────────────────────────────────────────────────────────
// ROOT APP COMPONENT
// ─────────────────────────────────────────────────────────────────────────────
export default function App() {
  return (
    <div style={styles.page}>
      <Hero />
      <JsxRulesDemo />
      <ComponentTypesDemo />
      <CompositionDemo />
      <VirtualDomDemo />
    </div>
  )
}

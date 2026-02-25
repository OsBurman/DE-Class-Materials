// Day 19a Part 2 — Components for testing (Counter + GreetingCard + TodoItem)
// These components are intentionally simple so their tests are easy to understand.
// Run tests: npm test       (vitest --watch)
// Run app:   npm run dev

import React, { useState } from 'react'

const primary = '#1e3a5f', accent = '#e06c1b'
const s = {
  page:   { maxWidth: 720, margin: '0 auto', padding: '2rem 1rem', fontFamily: 'sans-serif' },
  header: { background: primary, color: 'white', padding: '1.5rem 2rem', borderRadius: 8, marginBottom: '2rem' },
  card:   { background: 'white', borderRadius: 8, padding: '1.5rem', marginBottom: '1.5rem', boxShadow: '0 2px 8px rgba(0,0,0,.08)' },
  h2:     { color: primary, marginBottom: '1rem', borderBottom: `2px solid ${accent}`, paddingBottom: '.4rem' },
  btn:    (c=primary) => ({ background: c, color: 'white', border: 'none', padding: '.4rem .9rem', borderRadius: 4, cursor: 'pointer', margin: '.2rem' }),
}

// ─────────────────────────────────────────────────────────────────────────────
// Component 1: Counter (testable via role queries)
// ─────────────────────────────────────────────────────────────────────────────
export function Counter({ initialCount = 0, step = 1 }) {
  const [count, setCount] = useState(initialCount)
  return (
    <div>
      <p data-testid="count-display">{count}</p>
      <button aria-label="Decrement" onClick={() => setCount(c => c - step)}>−</button>
      <button aria-label="Increment" onClick={() => setCount(c => c + step)}>+</button>
      <button aria-label="Reset"     onClick={() => setCount(initialCount)}>Reset</button>
    </div>
  )
}

// ─────────────────────────────────────────────────────────────────────────────
// Component 2: GreetingCard (testable via text content)
// ─────────────────────────────────────────────────────────────────────────────
export function GreetingCard({ name, role = 'Student' }) {
  if (!name) return <p>Please provide a name</p>
  return (
    <div data-testid="greeting-card">
      <h3>Hello, {name}!</h3>
      <p>Role: {role}</p>
    </div>
  )
}

// ─────────────────────────────────────────────────────────────────────────────
// Component 3: TodoItem (testable via user interactions)
// ─────────────────────────────────────────────────────────────────────────────
export function TodoItem({ id, text, done = false, onToggle, onDelete }) {
  return (
    <li data-testid={`todo-${id}`} style={{ display:'flex', alignItems:'center', gap:'.5rem' }}>
      <input
        type="checkbox"
        checked={done}
        onChange={() => onToggle(id)}
        aria-label={`Toggle ${text}`}
      />
      <span style={{ textDecoration: done ? 'line-through' : 'none', color: done ? '#aaa' : '#222' }}>
        {text}
      </span>
      <button onClick={() => onDelete(id)} aria-label={`Delete ${text}`}>✕</button>
    </li>
  )
}

// ─────────────────────────────────────────────────────────────────────────────
// Component 4: LoginForm (testable via form submission)
// ─────────────────────────────────────────────────────────────────────────────
export function LoginForm({ onLogin }) {
  const [email,    setEmail]    = useState('')
  const [password, setPassword] = useState('')
  const [error,    setError]    = useState('')

  function handleSubmit(e) {
    e.preventDefault()
    if (!email.includes('@')) { setError('Invalid email'); return }
    if (password.length < 6)  { setError('Password too short'); return }
    setError('')
    onLogin({ email, password })
  }

  return (
    <form onSubmit={handleSubmit} aria-label="Login form">
      {error && <p role="alert" style={{ color:'red' }}>{error}</p>}
      <div>
        <label htmlFor="email">Email</label>
        <input id="email" type="email" value={email} onChange={e => setEmail(e.target.value)} />
      </div>
      <div>
        <label htmlFor="password">Password</label>
        <input id="password" type="password" value={password} onChange={e => setPassword(e.target.value)} />
      </div>
      <button type="submit">Login</button>
    </form>
  )
}

// ─────────────────────────────────────────────────────────────────────────────
// App — shows all components + links to test files
// ─────────────────────────────────────────────────────────────────────────────
export default function App() {
  const [todos, setTodos] = React.useState([
    { id: 1, text: 'Write tests',   done: false },
    { id: 2, text: 'Run npm test',  done: true  },
  ])
  const [loggedIn, setLoggedIn] = React.useState(null)

  return (
    <div style={s.page}>
      <div style={s.header}>
        <h1>🧪 Day 19a Part 2 — React Testing</h1>
        <p style={{ opacity:.85, marginTop:'.3rem' }}>Run: <code>npm test</code> (vitest)</p>
      </div>

      <div style={s.card}>
        <h2 style={s.h2}>Counter Component</h2>
        <Counter initialCount={0} step={1} />
        <p style={{ color:'#555', fontSize:'.85rem', marginTop:'.8rem' }}>
          Test file: <code>src/__tests__/Counter.test.jsx</code>
        </p>
      </div>

      <div style={s.card}>
        <h2 style={s.h2}>GreetingCard Component</h2>
        <GreetingCard name="Alice" role="Developer" />
        <GreetingCard name="" />
        <p style={{ color:'#555', fontSize:'.85rem', marginTop:'.8rem' }}>
          Test file: <code>src/__tests__/GreetingCard.test.jsx</code>
        </p>
      </div>

      <div style={s.card}>
        <h2 style={s.h2}>TodoItem Component</h2>
        <ul style={{ listStyle: 'none', padding: 0 }}>
          {todos.map(t => (
            <TodoItem key={t.id} {...t}
              onToggle={id => setTodos(ts => ts.map(t => t.id === id ? { ...t, done: !t.done } : t))}
              onDelete={id => setTodos(ts => ts.filter(t => t.id !== id))}
            />
          ))}
        </ul>
        <p style={{ color:'#555', fontSize:'.85rem', marginTop:'.8rem' }}>
          Test file: <code>src/__tests__/TodoItem.test.jsx</code>
        </p>
      </div>

      <div style={s.card}>
        <h2 style={s.h2}>LoginForm Component</h2>
        {loggedIn
          ? <p style={{ color:'green' }}>✅ Logged in as {loggedIn.email}</p>
          : <LoginForm onLogin={setLoggedIn} />
        }
        <p style={{ color:'#555', fontSize:'.85rem', marginTop:'.8rem' }}>
          Test file: <code>src/__tests__/LoginForm.test.jsx</code>
        </p>
      </div>
    </div>
  )
}

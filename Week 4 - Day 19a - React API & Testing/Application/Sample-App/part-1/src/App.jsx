// Day 19a Part 1 — API Integration: Fetch, Loading/Error States, Error Boundaries
// Run: npm install && npm run dev

import React, { useState, useEffect, Component, Suspense } from 'react'

const primary = '#1e3a5f', accent = '#e06c1b'
const s = {
  page:   { maxWidth: 960, margin: '0 auto', padding: '2rem 1rem' },
  header: { background: primary, color: 'white', padding: '1.5rem 2rem', borderRadius: 8, marginBottom: '2rem' },
  card:   { background: 'white', borderRadius: 8, padding: '1.5rem', marginBottom: '1.5rem', boxShadow: '0 2px 8px rgba(0,0,0,.08)' },
  h2:     { color: primary, marginBottom: '1rem', borderBottom: `2px solid ${accent}`, paddingBottom: '.4rem' },
  btn:    (c=primary) => ({ background: c, color: 'white', border: 'none', padding: '.4rem .9rem', borderRadius: 4, cursor: 'pointer', margin: '.2rem' }),
}

// ─────────────────────────────────────────────────────────────────────────────
// Custom hook — useFetch
// ─────────────────────────────────────────────────────────────────────────────
function useFetch(url, deps = []) {
  const [data,    setData]    = useState(null)
  const [loading, setLoading] = useState(false)
  const [error,   setError]   = useState(null)

  async function execute() {
    setLoading(true)
    setError(null)
    try {
      const res = await fetch(url)
      if (!res.ok) throw new Error(`HTTP ${res.status}: ${res.statusText}`)
      const json = await res.json()
      setData(json)
    } catch (err) {
      setError(err.message)
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => { execute() }, deps)

  return { data, loading, error, refetch: execute }
}

// ─────────────────────────────────────────────────────────────────────────────
// 1. Basic API Fetch — Users List
// ─────────────────────────────────────────────────────────────────────────────
function UsersList() {
  const { data: users, loading, error, refetch } = useFetch(
    'https://jsonplaceholder.typicode.com/users',
    []
  )

  if (loading) return <div style={{ textAlign:'center', padding:'2rem', color:'#888' }}>⏳ Loading users…</div>
  if (error)   return <div style={{ background:'#fce4ec', padding:'1rem', borderRadius:6, color:'#c62828' }}>❌ Error: {error} <button style={s.btn('#e74c3c')} onClick={refetch}>Retry</button></div>

  return (
    <div>
      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(2,1fr)', gap: '.8rem' }}>
        {users?.slice(0, 6).map(user => (
          <div key={user.id} style={{ background:'#f9f9f9', border:'1px solid #eee', borderRadius:6, padding:'.8rem' }}>
            <strong>{user.name}</strong><br />
            <small style={{ color:'#888' }}>📧 {user.email}</small><br />
            <small style={{ color:'#888' }}>🏢 {user.company.name}</small>
          </div>
        ))}
      </div>
      <button style={{ ...s.btn(), marginTop:'.8rem' }} onClick={refetch}>🔄 Refetch</button>
    </div>
  )
}

// ─────────────────────────────────────────────────────────────────────────────
// 2. POST / CRUD Simulation
// ─────────────────────────────────────────────────────────────────────────────
function PostCreator() {
  const [title,  setTitle]  = useState('')
  const [body,   setBody]   = useState('')
  const [status, setStatus] = useState(null)
  const [loading, setLoading] = useState(false)

  async function handleSubmit(e) {
    e.preventDefault()
    if (!title.trim() || !body.trim()) return
    setLoading(true)
    try {
      const res = await fetch('https://jsonplaceholder.typicode.com/posts', {
        method:  'POST',
        headers: { 'Content-Type': 'application/json' },
        body:    JSON.stringify({ title, body, userId: 1 }),
      })
      if (!res.ok) throw new Error(`HTTP ${res.status}`)
      const data = await res.json()
      setStatus({ ok: true, id: data.id, title: data.title })
      setTitle(''); setBody('')
    } catch (err) {
      setStatus({ ok: false, msg: err.message })
    } finally {
      setLoading(false)
    }
  }

  return (
    <form onSubmit={handleSubmit}>
      <div style={{ display: 'flex', flexDirection: 'column', gap: '.6rem' }}>
        <input value={title} onChange={e => setTitle(e.target.value)}
               style={{ padding:'.4rem', border:'1px solid #ccc', borderRadius:4 }}
               placeholder="Post title…" required />
        <textarea value={body} onChange={e => setBody(e.target.value)}
                  style={{ padding:'.4rem', border:'1px solid #ccc', borderRadius:4, height:80, resize:'vertical' }}
                  placeholder="Post body…" required />
        <button type="submit" style={s.btn()} disabled={loading}>
          {loading ? '⏳ Posting…' : '📤 POST to API'}
        </button>
      </div>
      {status && (
        <div style={{ marginTop:'.8rem', padding:'.8rem', borderRadius:6, background: status.ok ? '#e8f5e9' : '#fce4ec' }}>
          {status.ok ? `✅ Created post #${status.id}: "${status.title}"` : `❌ Error: ${status.msg}`}
        </div>
      )}
      <p style={{ marginTop:'.5rem', fontSize:'.8rem', color:'#888' }}>
        Note: jsonplaceholder.typicode.com simulates a POST — returns fake id 101
      </p>
    </form>
  )
}

// ─────────────────────────────────────────────────────────────────────────────
// 3. Error Boundary — class component (required for error boundaries)
// ─────────────────────────────────────────────────────────────────────────────
class ErrorBoundary extends Component {
  state = { hasError: false, error: null }

  static getDerivedStateFromError(error) {
    return { hasError: true, error }
  }

  componentDidCatch(error, errorInfo) {
    console.error('ErrorBoundary caught:', error, errorInfo)
  }

  render() {
    if (this.state.hasError) return (
      <div style={{ background:'#fce4ec', padding:'1rem', borderRadius:6, color:'#c62828' }}>
        <strong>🚨 Something went wrong:</strong> {this.state.error?.message}
        <br />
        <button style={{ ...s.btn('#e74c3c'), marginTop:'.5rem' }} onClick={() => this.setState({ hasError:false, error:null })}>
          Try Again
        </button>
      </div>
    )
    return this.props.children
  }
}

function BuggyComponent({ shouldThrow }) {
  if (shouldThrow) throw new Error('Simulated render error!')
  return <div style={{ background:'#e8f5e9', padding:'.8rem', borderRadius:6 }}>✅ Component rendered successfully</div>
}

function ErrorBoundaryDemo() {
  const [throwError, setThrowError] = useState(false)
  return (
    <div>
      <p style={{ color:'#555', marginBottom:'.8rem' }}>Error Boundaries catch render errors in child components without crashing the whole app.</p>
      <button style={s.btn(throwError ? '#27ae60' : '#e74c3c')} onClick={() => setThrowError(v => !v)}>
        {throwError ? '✅ Fix Component' : '💥 Throw Error'}
      </button>
      <div style={{ marginTop:'.8rem' }}>
        <ErrorBoundary>
          <BuggyComponent shouldThrow={throwError} />
        </ErrorBoundary>
      </div>
    </div>
  )
}

// ─────────────────────────────────────────────────────────────────────────────
// Root App
// ─────────────────────────────────────────────────────────────────────────────
export default function App() {
  return (
    <div style={s.page}>
      <div style={s.header}><h1>⚛️ Day 19a Part 1 — API Integration</h1></div>

      <div style={s.card}>
        <h2 style={s.h2}>1. Fetch + Loading/Error States (JSONPlaceholder API)</h2>
        <UsersList />
      </div>

      <div style={s.card}>
        <h2 style={s.h2}>2. POST Request — Creating Resources</h2>
        <PostCreator />
      </div>

      <div style={s.card}>
        <h2 style={s.h2}>3. Error Boundaries — Graceful Error Handling</h2>
        <ErrorBoundaryDemo />
      </div>
    </div>
  )
}

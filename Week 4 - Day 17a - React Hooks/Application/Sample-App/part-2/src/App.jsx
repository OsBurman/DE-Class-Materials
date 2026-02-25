// Day 17a Part 2 — Forms, useRef, useContext, Custom Hooks
// Run: npm install && npm run dev

import React, { useState, useEffect, useRef, useContext, createContext, useReducer } from 'react'

// ─────────────────────────────────────────────────────────────────────────────
// Context API — theme + user
// ─────────────────────────────────────────────────────────────────────────────
const ThemeContext = createContext({ primary: '#1e3a5f', accent: '#e06c1b' })
const UserContext  = createContext(null)

const s = {
  page:   { maxWidth: 960, margin: '0 auto', padding: '2rem 1rem' },
  header: (ctx) => ({ background: ctx.primary, color: 'white', padding: '1.5rem 2rem', borderRadius: 8, marginBottom: '2rem' }),
  card:   { background: 'white', borderRadius: 8, padding: '1.5rem', marginBottom: '1.5rem', boxShadow: '0 2px 8px rgba(0,0,0,.08)' },
  h2:     (ctx) => ({ color: ctx.primary, marginBottom: '1rem', borderBottom: `2px solid ${ctx.accent}`, paddingBottom: '.4rem' }),
  btn:    (color='#1e3a5f') => ({ background: color, color: 'white', border: 'none', padding: '.4rem .9rem', borderRadius: 4, cursor: 'pointer', margin: '.2rem' }),
  input:  { padding: '.35rem .6rem', border: '1px solid #ccc', borderRadius: 4, width: '100%', marginTop: '.2rem' },
}

// ─────────────────────────────────────────────────────────────────────────────
// Custom Hook — useLocalStorage
// ─────────────────────────────────────────────────────────────────────────────
function useLocalStorage(key, initialValue) {
  const [value, setValue] = useState(() => {
    try {
      const stored = localStorage.getItem(key)
      return stored ? JSON.parse(stored) : initialValue
    } catch { return initialValue }
  })

  function setStored(newValue) {
    setValue(newValue)
    localStorage.setItem(key, JSON.stringify(newValue))
  }

  return [value, setStored]
}

// Custom Hook — useFetch (data fetching)
function useFetch(url) {
  const [data,    setData]    = useState(null)
  const [loading, setLoading] = useState(true)
  const [error,   setError]   = useState(null)

  useEffect(() => {
    let cancelled = false
    setLoading(true)
    fetch(url)
      .then(r => r.json())
      .then(d => { if (!cancelled) { setData(d); setLoading(false) } })
      .catch(e => { if (!cancelled) { setError(e.message); setLoading(false) } })
    return () => { cancelled = true }  // cleanup prevents setting state after unmount
  }, [url])

  return { data, loading, error }
}

// ─────────────────────────────────────────────────────────────────────────────
// 1. Controlled Form
// ─────────────────────────────────────────────────────────────────────────────
function RegistrationForm() {
  const theme = useContext(ThemeContext)
  const [form, setForm] = useState({ name: '', email: '', track: '', agree: false })
  const [errors, setErrors] = useState({})
  const [submitted, setSubmitted] = useState(null)

  function handleChange(e) {
    const { name, value, type, checked } = e.target
    setForm(prev => ({ ...prev, [name]: type === 'checkbox' ? checked : value }))
    setErrors(prev => ({ ...prev, [name]: '' })) // clear error on change
  }

  function validate() {
    const e = {}
    if (!form.name.trim())    e.name  = 'Name is required'
    if (!form.email.includes('@')) e.email = 'Valid email required'
    if (!form.track)          e.track = 'Select a track'
    if (!form.agree)          e.agree = 'Must agree to terms'
    return e
  }

  function handleSubmit(e) {
    e.preventDefault()
    const errs = validate()
    if (Object.keys(errs).length > 0) { setErrors(errs); return }
    setSubmitted(form)
  }

  if (submitted) return (
    <div style={{ background: '#e8f5e9', padding: '1rem', borderRadius: 6 }}>
      <strong>✅ Registered!</strong> {submitted.name} ({submitted.email}) — {submitted.track}
      <button style={{ ...s.btn(), marginLeft: '1rem' }} onClick={() => setSubmitted(null)}>Try Again</button>
    </div>
  )

  return (
    <form onSubmit={handleSubmit}>
      {[
        { label: 'Full Name', name: 'name',  type: 'text',  placeholder: 'Alice Smith' },
        { label: 'Email',     name: 'email', type: 'email', placeholder: 'alice@example.com' },
      ].map(field => (
        <div key={field.name} style={{ marginBottom: '.8rem' }}>
          <label><strong>{field.label}</strong>
            <input style={{ ...s.input, borderColor: errors[field.name] ? '#e74c3c' : '#ccc' }}
                   type={field.type} name={field.name} value={form[field.name]}
                   onChange={handleChange} placeholder={field.placeholder} />
          </label>
          {errors[field.name] && <small style={{ color: '#e74c3c' }}>{errors[field.name]}</small>}
        </div>
      ))}
      <div style={{ marginBottom: '.8rem' }}>
        <label><strong>Track</strong>
          <select name="track" value={form.track} onChange={handleChange} style={{ ...s.input, borderColor: errors.track ? '#e74c3c' : '#ccc' }}>
            <option value="">— select —</option>
            <option value="React">React</option>
            <option value="Angular">Angular</option>
            <option value="Java">Java Full Stack</option>
          </select>
        </label>
        {errors.track && <small style={{ color: '#e74c3c' }}>{errors.track}</small>}
      </div>
      <label style={{ display: 'flex', gap: '.5rem', alignItems: 'center', marginBottom: '.8rem' }}>
        <input type="checkbox" name="agree" checked={form.agree} onChange={handleChange} />
        I agree to the terms
        {errors.agree && <small style={{ color: '#e74c3c' }}>{errors.agree}</small>}
      </label>
      <button type="submit" style={s.btn(theme.primary)}>Register</button>
    </form>
  )
}

// ─────────────────────────────────────────────────────────────────────────────
// 2. useRef
// ─────────────────────────────────────────────────────────────────────────────
function RefDemo() {
  const inputRef    = useRef(null)         // DOM ref
  const renderCount = useRef(0)            // mutable value — doesn't trigger re-render
  const [value, setValue] = useState('')

  renderCount.current++

  return (
    <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '1rem' }}>
      <div style={{ background: '#f0f4ff', padding: '1rem', borderRadius: 6 }}>
        <strong>DOM ref — focus input</strong><br /><br />
        <input ref={inputRef} style={s.input} value={value} onChange={e => setValue(e.target.value)} placeholder="Type here" />
        <button style={s.btn()} onClick={() => inputRef.current.focus()}>Focus Input</button>
        <button style={s.btn('#27ae60')} onClick={() => inputRef.current.select()}>Select All</button>
      </div>
      <div style={{ background: '#fff9c4', padding: '1rem', borderRadius: 6 }}>
        <strong>renderCount ref (persists across renders, no re-render)</strong>
        <div style={{ fontSize: '2rem', margin: '.5rem 0' }}>{renderCount.current}</div>
        <button style={s.btn()} onClick={() => setValue(v => v + 'x')}>Cause Re-render</button>
        <p style={{ fontSize: '.8rem', color: '#888', marginTop: '.5rem' }}>
          renderCount.current increments but doesn't trigger re-render itself
        </p>
      </div>
    </div>
  )
}

// ─────────────────────────────────────────────────────────────────────────────
// 3. Context API + useContext
// ─────────────────────────────────────────────────────────────────────────────
function ThemedButton({ children, onClick }) {
  const theme = useContext(ThemeContext)   // consume context without prop drilling
  const user  = useContext(UserContext)
  return (
    <div>
      <button style={s.btn(theme.primary)} onClick={onClick}>{children}</button>
      {user && <span style={{ marginLeft: '.5rem', color: '#888', fontSize: '.85rem' }}>Logged in as: {user.name}</span>}
    </div>
  )
}

function ContextDemo() {
  const theme = useContext(ThemeContext)
  const [isDark, setIsDark] = useState(false)
  const darkTheme = { primary: '#2d2d2d', accent: '#f39c12' }

  return (
    <ThemeContext.Provider value={isDark ? darkTheme : { primary: '#1e3a5f', accent: '#e06c1b' }}>
      <UserContext.Provider value={{ name: 'Alice', role: 'admin' }}>
        <div style={{ background: isDark ? '#333' : '#f9f9f9', padding: '1rem', borderRadius: 6, color: isDark ? 'white' : '#222' }}>
          <ThemedButton onClick={() => setIsDark(d => !d)}>
            Toggle Theme (currently: {isDark ? 'Dark 🌙' : 'Light ☀️'})
          </ThemedButton>
          <p style={{ marginTop: '.5rem', fontSize: '.85rem', color: isDark ? '#aaa' : '#555' }}>
            Context avoids "prop drilling" — ThemedButton accesses theme without receiving it as a prop from App
          </p>
        </div>
      </UserContext.Provider>
    </ThemeContext.Provider>
  )
}

// ─────────────────────────────────────────────────────────────────────────────
// 4. Custom Hook Demo
// ─────────────────────────────────────────────────────────────────────────────
function CustomHookDemo() {
  const [notes, setNotes] = useLocalStorage('day17-notes', '')
  const { data, loading, error } = useFetch('https://jsonplaceholder.typicode.com/users/1')

  return (
    <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '1rem' }}>
      <div style={{ background: '#e8f5e9', padding: '1rem', borderRadius: 6 }}>
        <strong>useLocalStorage custom hook</strong>
        <textarea value={notes} onChange={e => setNotes(e.target.value)}
          style={{ width:'100%', height:80, marginTop:'.5rem', padding:'.4rem', borderRadius:4, border:'1px solid #ccc' }}
          placeholder="Notes persist in localStorage..." />
        <small style={{ color:'#888' }}>Refresh the page — notes persist!</small>
      </div>
      <div style={{ background: '#f0f4ff', padding: '1rem', borderRadius: 6 }}>
        <strong>useFetch custom hook</strong><br />
        {loading && <span>Loading…</span>}
        {error   && <span style={{ color:'#e74c3c' }}>Error: {error}</span>}
        {data    && <div style={{ marginTop:'.5rem', fontSize:'.85rem' }}>
          <div>Name: {data.name}</div>
          <div>Email: {data.email}</div>
          <div>Company: {data.company?.name}</div>
        </div>}
        <small style={{ color:'#888' }}>Fetched from jsonplaceholder.typicode.com</small>
      </div>
    </div>
  )
}

// ─────────────────────────────────────────────────────────────────────────────
// ROOT APP
// ─────────────────────────────────────────────────────────────────────────────
export default function App() {
  const theme = useContext(ThemeContext)
  return (
    <div style={s.page}>
      <div style={s.header(theme)}><h1>⚛️ Day 17a Part 2 — Forms, useRef, useContext &amp; Custom Hooks</h1></div>

      <div style={s.card}>
        <h2 style={s.h2(theme)}>1. Controlled Form with Validation</h2>
        <RegistrationForm />
      </div>

      <div style={s.card}>
        <h2 style={s.h2(theme)}>2. useRef — DOM Access &amp; Mutable Values</h2>
        <RefDemo />
      </div>

      <div style={s.card}>
        <h2 style={s.h2(theme)}>3. Context API — useContext (avoid prop drilling)</h2>
        <ContextDemo />
      </div>

      <div style={s.card}>
        <h2 style={s.h2(theme)}>4. Custom Hooks — Reusable Logic</h2>
        <CustomHookDemo />
      </div>
    </div>
  )
}

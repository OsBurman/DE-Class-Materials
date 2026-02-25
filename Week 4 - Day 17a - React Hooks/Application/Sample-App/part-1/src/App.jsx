// Day 17a Part 1 — useState, useEffect, Event Handling
// Run: npm install && npm run dev

import React, { useState, useEffect } from 'react'

const s = {
  page:   { maxWidth: 960, margin: '0 auto', padding: '2rem 1rem' },
  header: { background: '#1e3a5f', color: 'white', padding: '1.5rem 2rem', borderRadius: 8, marginBottom: '2rem' },
  card:   { background: 'white', borderRadius: 8, padding: '1.5rem', marginBottom: '1.5rem', boxShadow: '0 2px 8px rgba(0,0,0,.08)' },
  h2:     { color: '#1e3a5f', marginBottom: '1rem', borderBottom: '2px solid #e06c1b', paddingBottom: '.4rem' },
  btn:    (color='#1e3a5f') => ({ background: color, color: 'white', border: 'none', padding: '.4rem .9rem', borderRadius: 4, cursor: 'pointer', margin: '.2rem' }),
  input:  { padding: '.35rem .6rem', border: '1px solid #ccc', borderRadius: 4, width: '220px' },
}

// ─────────────────────────────────────────────────────────────────────────────
// 1. useState — Counter
// ─────────────────────────────────────────────────────────────────────────────
function CounterDemo() {
  // useState returns [currentValue, setterFunction]
  const [count, setCount] = useState(0)
  const [step,  setStep]  = useState(1)

  // Each call to setCount triggers a re-render with the new value
  return (
    <div style={s.card}>
      <h2 style={s.h2}>1. useState — Counter</h2>
      <p style={{ marginBottom: '.8rem', color: '#555' }}>
        <code>const [count, setCount] = useState(0)</code>
      </p>
      <div style={{ fontSize: '3rem', textAlign: 'center', margin: '1rem 0' }}>{count}</div>
      <div style={{ textAlign: 'center' }}>
        <button style={s.btn('#e74c3c')} onClick={() => setCount(c => c - step)}>−{step}</button>
        <button style={s.btn()}         onClick={() => setCount(0)}>Reset</button>
        <button style={s.btn('#27ae60')} onClick={() => setCount(c => c + step)}>+{step}</button>
      </div>
      <div style={{ textAlign: 'center', marginTop: '.8rem' }}>
        Step:&nbsp;
        {[1, 5, 10].map(n => (
          <button key={n} style={s.btn(step === n ? '#e06c1b' : '#888')} onClick={() => setStep(n)}>{n}</button>
        ))}
      </div>
    </div>
  )
}

// ─────────────────────────────────────────────────────────────────────────────
// 2. useState — Object & Array state
// ─────────────────────────────────────────────────────────────────────────────
function TodoList() {
  const [todos, setTodos] = useState([
    { id: 1, text: 'Learn React hooks', done: true },
    { id: 2, text: 'Build a project',   done: false },
  ])
  const [input, setInput] = useState('')

  function addTodo() {
    if (!input.trim()) return
    // Always create a new array — never mutate state directly
    setTodos(prev => [...prev, { id: Date.now(), text: input.trim(), done: false }])
    setInput('')
  }

  function toggleTodo(id) {
    setTodos(prev => prev.map(t => t.id === id ? { ...t, done: !t.done } : t))
  }

  function deleteTodo(id) {
    setTodos(prev => prev.filter(t => t.id !== id))
  }

  const remaining = todos.filter(t => !t.done).length

  return (
    <div style={s.card}>
      <h2 style={s.h2}>2. useState — Array State (Todo List)</h2>
      <div style={{ display: 'flex', gap: '.5rem', marginBottom: '1rem' }}>
        <input style={s.input} value={input} onChange={e => setInput(e.target.value)}
               onKeyDown={e => e.key === 'Enter' && addTodo()} placeholder="New task…" />
        <button style={s.btn()} onClick={addTodo}>Add</button>
      </div>
      <ul style={{ listStyle: 'none', padding: 0 }}>
        {todos.map(todo => (
          <li key={todo.id} style={{ display:'flex', alignItems:'center', gap:'.5rem', padding:'.4rem', background:'#f9f9f9', borderRadius:4, marginBottom:'.3rem' }}>
            <input type="checkbox" checked={todo.done} onChange={() => toggleTodo(todo.id)} />
            <span style={{ flex:1, textDecoration: todo.done ? 'line-through' : 'none', color: todo.done ? '#aaa' : '#222' }}>{todo.text}</span>
            <button style={s.btn('#e74c3c')} onClick={() => deleteTodo(todo.id)}>✕</button>
          </li>
        ))}
      </ul>
      <p style={{ color: '#888', fontSize: '.9rem' }}>{remaining} of {todos.length} remaining</p>
    </div>
  )
}

// ─────────────────────────────────────────────────────────────────────────────
// 3. useEffect — Side Effects & Cleanup
// ─────────────────────────────────────────────────────────────────────────────
function EffectDemo() {
  const [count,  setCount]  = useState(0)
  const [width,  setWidth]  = useState(window.innerWidth)
  const [time,   setTime]   = useState(new Date().toLocaleTimeString())

  // Effect 1: run on every render (no dependency array)
  // useEffect(() => { console.log('runs every render') })

  // Effect 2: run once on mount (empty dependency array [])
  useEffect(() => {
    document.title = '⚛️ React Hooks Demo'
    return () => { document.title = 'React App' }  // cleanup on unmount
  }, [])

  // Effect 3: run when count changes (specific dependency)
  useEffect(() => {
    document.title = `Count: ${count}`
  }, [count])  // dependency array — effect re-runs when count changes

  // Effect 4: subscription / event listener with cleanup
  useEffect(() => {
    const handleResize = () => setWidth(window.innerWidth)
    window.addEventListener('resize', handleResize)
    return () => window.removeEventListener('resize', handleResize) // cleanup!
  }, []) // empty array = only runs once

  // Effect 5: interval with cleanup
  useEffect(() => {
    const id = setInterval(() => setTime(new Date().toLocaleTimeString()), 1000)
    return () => clearInterval(id) // cleanup — prevents memory leak
  }, [])

  return (
    <div style={s.card}>
      <h2 style={s.h2}>3. useEffect — Side Effects &amp; Cleanup</h2>
      <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '1rem' }}>
        <div style={{ background: '#f0f8f0', padding: '1rem', borderRadius: 6 }}>
          <strong>Clock (interval)</strong>
          <div style={{ fontSize: '1.4rem', marginTop: '.3rem' }}>{time}</div>
          <small style={{ color: '#888' }}>useEffect + setInterval + cleanup</small>
        </div>
        <div style={{ background: '#f0f4ff', padding: '1rem', borderRadius: 6 }}>
          <strong>Window Width</strong>
          <div style={{ fontSize: '1.4rem', marginTop: '.3rem' }}>{width}px</div>
          <small style={{ color: '#888' }}>useEffect + resize listener + cleanup</small>
        </div>
      </div>
      <div style={{ marginTop: '1rem' }}>
        <strong>Page Title updates with count:</strong>&nbsp;
        <button style={s.btn()} onClick={() => setCount(c => c + 1)}>Increment ({count})</button>
        <p style={{ fontSize: '.85rem', color: '#888', marginTop: '.3rem' }}>Check the browser tab title</p>
      </div>
      <div style={{ marginTop: '.8rem', background: '#fffacd', padding: '.8rem', borderRadius: 6, fontSize: '.85rem' }}>
        <strong>Rule:</strong> Always return a cleanup function when subscribing to events, timers, or subscriptions to prevent memory leaks.
      </div>
    </div>
  )
}

export default function App() {
  return (
    <div style={s.page}>
      <div style={s.header}><h1>⚛️ Day 17a Part 1 — useState &amp; useEffect</h1></div>
      <CounterDemo />
      <TodoList />
      <EffectDemo />
    </div>
  )
}

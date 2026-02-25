// Day 18a Part 2 — Redux Toolkit: Store, Slices, useSelector, useDispatch
// Run: npm install && npm run dev

import React from 'react'
import { configureStore, createSlice } from '@reduxjs/toolkit'
import { Provider, useSelector, useDispatch } from 'react-redux'

const primary = '#1e3a5f', accent = '#e06c1b'
const s = {
  page:   { maxWidth: 960, margin: '0 auto', padding: '2rem 1rem' },
  header: { background: primary, color: 'white', padding: '1.5rem 2rem', borderRadius: 8, marginBottom: '2rem' },
  card:   { background: 'white', borderRadius: 8, padding: '1.5rem', marginBottom: '1.5rem', boxShadow: '0 2px 8px rgba(0,0,0,.08)' },
  h2:     { color: primary, marginBottom: '1rem', borderBottom: `2px solid ${accent}`, paddingBottom: '.4rem' },
  btn:    (c=primary) => ({ background: c, color: 'white', border: 'none', padding: '.4rem .9rem', borderRadius: 4, cursor: 'pointer', margin: '.2rem' }),
  input:  { padding: '.35rem .6rem', border: '1px solid #ccc', borderRadius: 4, width: '220px' },
}

// ─────────────────────────────────────────────────────────────────────────────
// Redux Slices — createSlice auto-generates actions + reducers
// ─────────────────────────────────────────────────────────────────────────────

// Slice 1: Counter
const counterSlice = createSlice({
  name: 'counter',
  initialState: { value: 0, step: 1 },
  reducers: {
    increment:   state => { state.value += state.step },           // Immer allows "mutation" syntax
    decrement:   state => { state.value -= state.step },
    reset:       state => { state.value = 0 },
    setStep:    (state, action) => { state.step = action.payload },
    incrementBy:(state, action) => { state.value += action.payload },
  }
})

// Slice 2: Todo list
const todosSlice = createSlice({
  name: 'todos',
  initialState: {
    items: [
      { id: 1, text: 'Learn Redux Toolkit', done: true  },
      { id: 2, text: 'Build a React app',   done: false },
    ],
    filter: 'all'   // 'all' | 'active' | 'done'
  },
  reducers: {
    addTodo:    (state, action) => { state.items.push({ id: Date.now(), text: action.payload, done: false }) },
    toggleTodo: (state, action) => {
      const todo = state.items.find(t => t.id === action.payload)
      if (todo) todo.done = !todo.done
    },
    deleteTodo: (state, action) => { state.items = state.items.filter(t => t.id !== action.payload) },
    setFilter:  (state, action) => { state.filter = action.payload },
  }
})

// Export auto-generated action creators
export const { increment, decrement, reset, setStep, incrementBy } = counterSlice.actions
export const { addTodo, toggleTodo, deleteTodo, setFilter }        = todosSlice.actions

// ─────────────────────────────────────────────────────────────────────────────
// Configure Store — combine slices
// ─────────────────────────────────────────────────────────────────────────────
const store = configureStore({
  reducer: {
    counter: counterSlice.reducer,
    todos:   todosSlice.reducer,
  }
})

// ─────────────────────────────────────────────────────────────────────────────
// Components
// ─────────────────────────────────────────────────────────────────────────────
function CounterDisplay() {
  // useSelector — read from store (re-renders when selected state changes)
  const { value, step } = useSelector(state => state.counter)
  const dispatch = useDispatch()   // useDispatch — send actions to store

  return (
    <div style={s.card}>
      <h2 style={s.h2}>1. Counter Slice</h2>
      <p style={{ color: '#555', marginBottom: '1rem' }}>
        <code>const counter = useSelector(state =&gt; state.counter)</code><br />
        <code>dispatch(increment())</code>
      </p>
      <div style={{ fontSize: '3rem', textAlign: 'center', marginBottom: '1rem' }}>{value}</div>
      <div style={{ textAlign: 'center' }}>
        <button style={s.btn('#e74c3c')} onClick={() => dispatch(decrement())}>−{step}</button>
        <button style={s.btn('#888')}    onClick={() => dispatch(reset())}>Reset</button>
        <button style={s.btn('#27ae60')} onClick={() => dispatch(increment())}>+{step}</button>
        <button style={s.btn(accent)}    onClick={() => dispatch(incrementBy(10))}>+10</button>
      </div>
      <div style={{ textAlign: 'center', marginTop: '.8rem' }}>
        Step:&nbsp;{[1, 5, 10].map(n => (
          <button key={n} style={s.btn(step === n ? accent : '#888')} onClick={() => dispatch(setStep(n))}>{n}</button>
        ))}
      </div>
    </div>
  )
}

function TodoApp() {
  const { items, filter } = useSelector(state => state.todos)
  const dispatch = useDispatch()
  const [input, setInput] = React.useState('')

  const filtered = items.filter(t => {
    if (filter === 'active') return !t.done
    if (filter === 'done')   return  t.done
    return true
  })

  function handleAdd() {
    if (!input.trim()) return
    dispatch(addTodo(input.trim()))
    setInput('')
  }

  return (
    <div style={s.card}>
      <h2 style={s.h2}>2. Todos Slice</h2>
      <div style={{ display: 'flex', gap: '.5rem', marginBottom: '1rem' }}>
        <input style={s.input} value={input} onChange={e => setInput(e.target.value)}
               onKeyDown={e => e.key === 'Enter' && handleAdd()} placeholder="New task…" />
        <button style={s.btn()} onClick={handleAdd}>Add</button>
      </div>
      <div style={{ display: 'flex', gap: '.5rem', marginBottom: '1rem' }}>
        {['all', 'active', 'done'].map(f => (
          <button key={f} style={s.btn(filter === f ? accent : '#888')} onClick={() => dispatch(setFilter(f))}>
            {f}
          </button>
        ))}
        <span style={{ marginLeft: 'auto', color: '#888', fontSize: '.85rem', lineHeight: '2.2' }}>
          {items.filter(t => !t.done).length} remaining
        </span>
      </div>
      <ul style={{ listStyle: 'none', padding: 0 }}>
        {filtered.map(todo => (
          <li key={todo.id} style={{ display: 'flex', alignItems: 'center', gap: '.5rem', padding: '.4rem', background: '#f9f9f9', borderRadius: 4, marginBottom: '.3rem' }}>
            <input type="checkbox" checked={todo.done} onChange={() => dispatch(toggleTodo(todo.id))} />
            <span style={{ flex: 1, textDecoration: todo.done ? 'line-through' : 'none', color: todo.done ? '#aaa' : '#222' }}>{todo.text}</span>
            <button style={s.btn('#e74c3c')} onClick={() => dispatch(deleteTodo(todo.id))}>✕</button>
          </li>
        ))}
      </ul>
    </div>
  )
}

function StoreInspector() {
  const state = useSelector(state => state)
  return (
    <div style={s.card}>
      <h2 style={s.h2}>3. Redux Store State (live snapshot)</h2>
      <pre style={{ background:'#1e1e1e', color:'#d4e157', padding:'1rem', borderRadius:6, fontSize:'.8rem', overflow:'auto', maxHeight:200 }}>
        {JSON.stringify(state, null, 2)}
      </pre>
      <p style={{ color:'#555', fontSize:'.85rem', marginTop:'.5rem' }}>
        In development, install Redux DevTools browser extension for a live state tree + action history.
      </p>
    </div>
  )
}

// ─────────────────────────────────────────────────────────────────────────────
// Root — wrap with Provider
// ─────────────────────────────────────────────────────────────────────────────
export default function App() {
  return (
    <Provider store={store}>
      <div style={s.page}>
        <div style={s.header}><h1>⚛️ Day 18a Part 2 — Redux Toolkit</h1></div>
        <CounterDisplay />
        <TodoApp />
        <StoreInspector />
      </div>
    </Provider>
  )
}

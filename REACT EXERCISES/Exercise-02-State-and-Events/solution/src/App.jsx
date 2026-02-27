import { useState } from 'react'
import './App.css'
import Counter from './components/Counter'

const INITIAL_COUNTERS = [
  { id: 1, emoji: '☕', label: 'Coffee', value: 0 },
  { id: 2, emoji: '💧', label: 'Water', value: 0 },
  { id: 3, emoji: '🏃', label: 'Steps', value: 0 },
]

export default function App() {
  const [counters, setCounters] = useState(INITIAL_COUNTERS)
  const [name, setName] = useState('Student')

  // Increment a specific counter by id using the prev-state pattern
  function increment(id) {
    setCounters(prev =>
      prev.map(counter =>
        counter.id === id ? { ...counter, value: counter.value + 1 } : counter
      )
    )
  }

  // Decrement a specific counter — never go below 0
  function decrement(id) {
    setCounters(prev =>
      prev.map(counter =>
        counter.id === id
          ? { ...counter, value: Math.max(0, counter.value - 1) }
          : counter
      )
    )
  }

  // Reset a single counter to 0
  function reset(id) {
    setCounters(prev =>
      prev.map(counter =>
        counter.id === id ? { ...counter, value: 0 } : counter
      )
    )
  }

  // Reset ALL counters to 0
  function resetAll() {
    setCounters(prev => prev.map(counter => ({ ...counter, value: 0 })))
  }

  // Derived value — computed directly from state, no extra useState needed
  const total = counters.reduce((sum, c) => sum + c.value, 0)

  return (
    <div className="app">
      <div className="card">
        <div className="greeting-section">
          <h1>Hello, {name}! 👋</h1>
          <div className="name-input-group">
            <label htmlFor="name-input">Your name:</label>
            <input
              id="name-input"
              type="text"
              value={name}
              placeholder="Enter your name"
              onChange={(e) => setName(e.target.value)}
            />
          </div>
        </div>

        <div className="counters-grid">
          {counters.map(counter => (
            <Counter
              key={counter.id}
              id={counter.id}
              emoji={counter.emoji}
              label={counter.label}
              value={counter.value}
              onIncrement={increment}
              onDecrement={decrement}
              onReset={reset}
            />
          ))}
        </div>

        <div className="dashboard-footer">
          <span className="total-display">
            Total across all counters: <strong>{total}</strong>
          </span>
          <button className="btn btn-danger" onClick={resetAll}>
            Reset All
          </button>
        </div>
      </div>
    </div>
  )
}

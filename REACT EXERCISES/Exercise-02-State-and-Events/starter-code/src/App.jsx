// TODO 1: Import useState from 'react'
import './App.css'
import Counter from './components/Counter'

// ─────────────────────────────────────────────────────────────────────────────
// The initial counter data shape. Each counter has:
//   { id: number, emoji: string, label: string, value: number }
// ─────────────────────────────────────────────────────────────────────────────
const INITIAL_COUNTERS = [
  { id: 1, emoji: '☕', label: 'Coffee', value: 0 },
  { id: 2, emoji: '💧', label: 'Water', value: 0 },
  { id: 3, emoji: '🏃', label: 'Steps', value: 0 },
]

export default function App() {
  // TODO 2: Declare `counters` state using useState with INITIAL_COUNTERS as the default
  // const [counters, setCounters] = useState(INITIAL_COUNTERS)

  // TODO 3: Declare `name` state — a string, initialized to 'Student'
  // const [name, setName] = useState('Student')

  // ──────────────────────────────────────────────────────────────────────────
  // TODO 4: Implement increment(id)
  //   - Find the counter with the matching id and increase its value by 1
  //   - Use the prev-state updater pattern to safely update based on prev state:
  //     setCounters(prev => prev.map(counter =>
  //       counter.id === id ? { ...counter, value: counter.value + 1 } : counter
  //     ))
  // ──────────────────────────────────────────────────────────────────────────
  function increment(id) {
    // your code here
  }

  // TODO 5: Implement decrement(id)
  //   - Same as increment but subtract 1
  //   - IMPORTANT: don't let the value go below 0
  //     Use Math.max(0, counter.value - 1) to clamp the value
  function decrement(id) {
    // your code here
  }

  // TODO 6: Implement reset(id)
  //   - Set the matching counter's value back to 0
  function reset(id) {
    // your code here
  }

  // TODO 7: Implement resetAll()
  //   - Map over ALL counters and set every value to 0
  function resetAll() {
    // your code here
  }

  // TODO 8: Compute `total` — the sum of all counter values
  //   - Use .reduce() on the counters array
  //   - Do NOT declare a separate useState for this — derive it directly!
  //   const total = counters.reduce((sum, c) => sum + c.value, 0)
  const total = 0 // replace this line

  return (
    <div className="app">
      <div className="card">
        {/* Greeting section */}
        <div className="greeting-section">
          {/* TODO 11: Display "Hello, {name}! 👋" */}
          <h1>Hello, Student! 👋</h1>
          <div className="name-input-group">
            <label htmlFor="name-input">Your name:</label>
            <input
              id="name-input"
              type="text"
              placeholder="Enter your name"
              // TODO 9: Add value={name} and onChange to update the name state
              //         onChange={(e) => setName(e.target.value)}
            />
          </div>
        </div>

        {/* Counters grid */}
        <div className="counters-grid">
          {/* TODO 10: Map over `counters` and render a <Counter /> for each one.
                      Pass these props: key, id, emoji, label, value,
                      onIncrement, onDecrement, onReset */}
        </div>

        {/* Footer with total and reset all */}
        <div className="dashboard-footer">
          {/* TODO 11: Display the computed total */}
          <span className="total-display">Total across all counters: <strong>{total}</strong></span>
          <button className="btn btn-danger" onClick={resetAll}>
            Reset All
          </button>
        </div>
      </div>
    </div>
  )
}

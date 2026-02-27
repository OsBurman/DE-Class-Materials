import SearchSection from './components/SearchSection'
import Stopwatch from './components/Stopwatch'
import PrevValueTracker from './components/PrevValueTracker'
import './App.css'

function App() {
  return (
    <div className="app">
      <h1>🔬 useRef & DOM Access</h1>
      <p className="subtitle">Three independent demos showing different uses of <code>useRef</code>.</p>
      <div className="sections">
        <SearchSection />
        <Stopwatch />
        <PrevValueTracker />
      </div>
    </div>
  )
}

export default App

// TODO: Import useState and useEffect from 'react'
import './App.css'
import ClockWidget from './components/ClockWidget'
import QuoteWidget from './components/QuoteWidget'
import WindowWidget from './components/WindowWidget'

export default function App() {
  // ── CLOCK ──────────────────────────────────────────────────────────────────
  // TODO 1: Declare `time` state — initialize to new Date()
  //
  // TODO 2: Use useEffect with [] (runs once on mount) to start a setInterval:
  //   useEffect(() => {
  //     const id = setInterval(() => setTime(new Date()), 1000)
  //     return () => clearInterval(id)   ← IMPORTANT: cleanup prevents memory leaks!
  //   }, [])

  // ── WINDOW SIZE ────────────────────────────────────────────────────────────
  // TODO 3: Declare `windowSize` state — initialize to { width: window.innerWidth, height: window.innerHeight }
  //
  // TODO 4: Use useEffect with [] to add a resize event listener:
  //   useEffect(() => {
  //     const handleResize = () => setWindowSize({ width: window.innerWidth, height: window.innerHeight })
  //     window.addEventListener('resize', handleResize)
  //     return () => window.removeEventListener('resize', handleResize)  ← cleanup!
  //   }, [])

  // ── QUOTE ──────────────────────────────────────────────────────────────────
  // TODO 5: Declare `quote` state (null initially), `isLoading` state (false), `error` state (null)
  //
  // TODO 6: Write fetchQuote() async function:
  //   async function fetchQuote() {
  //     setIsLoading(true)
  //     setError(null)
  //     try {
  //       const res = await fetch('https://dummyjson.com/quotes/random')
  //       const data = await res.json()
  //       setQuote(data)
  //     } catch (err) {
  //       setError('Failed to fetch quote')
  //     } finally {
  //       setIsLoading(false)
  //     }
  //   }
  //
  // TODO 7: Use useEffect with [] to call fetchQuote() on mount
  //
  // TODO 8: Use useEffect with [quote] dependency to update document.title:
  //   useEffect(() => {
  //     if (quote) document.title = `"${quote.quote.slice(0, 40)}..." | React Dashboard`
  //   }, [quote])

  return (
    <div className="app">
      <h1>📊 Live Dashboard</h1>
      <div className="dashboard-grid">
        {/* TODO: Pass time prop as a formatted string, e.g. time.toLocaleTimeString() */}
        <ClockWidget time="00:00:00" />

        {/* TODO: Pass windowSize prop */}
        <WindowWidget windowSize={{ width: 0, height: 0 }} />

        {/* TODO: Pass quote, isLoading, onRefresh props */}
        <QuoteWidget quote={null} isLoading={false} onRefresh={() => {}} />
      </div>
    </div>
  )
}

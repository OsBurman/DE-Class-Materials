import { useState, useEffect } from 'react'
import './App.css'
import ClockWidget from './components/ClockWidget'
import QuoteWidget from './components/QuoteWidget'
import WindowWidget from './components/WindowWidget'

export default function App() {
  // ── Clock state ────────────────────────────────────────────────────────────
  const [time, setTime] = useState(new Date())

  // useEffect with [] runs once on mount — starts a 1-second interval
  // Returns a cleanup function that stops the interval when the component unmounts
  useEffect(() => {
    const id = setInterval(() => setTime(new Date()), 1000)
    return () => clearInterval(id)
  }, [])

  // ── Window size ────────────────────────────────────────────────────────────
  const [windowSize, setWindowSize] = useState({
    width: window.innerWidth,
    height: window.innerHeight,
  })

  // Listen for resize events — cleanup removes listener on unmount
  useEffect(() => {
    const handleResize = () =>
      setWindowSize({ width: window.innerWidth, height: window.innerHeight })
    window.addEventListener('resize', handleResize)
    return () => window.removeEventListener('resize', handleResize)
  }, [])

  // ── Quote ──────────────────────────────────────────────────────────────────
  const [quote, setQuote] = useState(null)
  const [isLoading, setIsLoading] = useState(false)
  const [error, setError] = useState(null)

  async function fetchQuote() {
    setIsLoading(true)
    setError(null)
    try {
      const res = await fetch('https://dummyjson.com/quotes/random')
      const data = await res.json()
      setQuote(data)
    } catch (err) {
      setError('Failed to fetch quote. Check your connection.')
    } finally {
      setIsLoading(false)
    }
  }

  // Fetch a quote on mount
  useEffect(() => {
    fetchQuote()
  }, [])

  // Update document title whenever the quote changes
  // This runs whenever `quote` updates (dependency array: [quote])
  useEffect(() => {
    if (quote) {
      document.title = `"${quote.quote.slice(0, 40)}..." | React Dashboard`
    }
  }, [quote])

  return (
    <div className="app">
      <h1>📊 Live Dashboard</h1>
      <div className="dashboard-grid">
        <ClockWidget time={time.toLocaleTimeString()} />
        <WindowWidget windowSize={windowSize} />
        <QuoteWidget quote={quote} isLoading={isLoading} onRefresh={fetchQuote} />
      </div>
      {error && <p style={{ color: '#ef4444', textAlign: 'center', marginTop: '1rem' }}>{error}</p>}
    </div>
  )
}

import { useState } from 'react'
import { useDebounce } from '../hooks/useDebounce'

const TECH_TERMS = [
  'React', 'Redux', 'TypeScript', 'JavaScript', 'Node.js', 'Express', 'PostgreSQL',
  'MongoDB', 'GraphQL', 'REST API', 'Docker', 'Kubernetes', 'AWS', 'CI/CD', 'Git',
  'Webpack', 'Vite', 'Jest', 'Testing Library', 'TailwindCSS',
]

// TODO 1: Add `query` state (string, '').
// TODO 2: Call useDebounce(query, 500) to get `debouncedQuery`.
// TODO 3: Derive `results`:
//   debouncedQuery.trim() === '' ? [] :
//   TECH_TERMS.filter(t => t.toLowerCase().includes(debouncedQuery.toLowerCase()))

function SearchWidget() {
  const [query, setQuery] = useState('')
  // TODO: add debouncedQuery and results

  const debouncedQuery = useDebounce(query, 500)
  const results = debouncedQuery.trim() === ''
    ? []
    : TECH_TERMS.filter(t => t.toLowerCase().includes(debouncedQuery.toLowerCase()))

  return (
    <div className="widget">
      <h2>🔍 Search Widget</h2>
      <span className="hook-name">useDebounce</span>
      {/* TODO 4: Bind value={query} and onChange */}
      <input type="search" placeholder="Search tech terms…" value={query} onChange={e => setQuery(e.target.value)} />
      <p className="debounce-note">Results update 500 ms after you stop typing.</p>
      {/* TODO 5: Render results list */}
      {results.length > 0 && (
        <ul className="search-results">
          {results.map(r => <li key={r}>{r}</li>)}
        </ul>
      )}
      {debouncedQuery && results.length === 0 && (
        <p style={{ color: '#94a3b8', fontSize: '0.85rem', marginTop: '0.5rem' }}>No results for "{debouncedQuery}"</p>
      )}
    </div>
  )
}

export default SearchWidget

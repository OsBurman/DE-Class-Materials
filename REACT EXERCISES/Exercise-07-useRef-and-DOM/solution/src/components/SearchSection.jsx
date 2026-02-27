import { useRef, useEffect } from 'react'

function SearchSection() {
  const inputRef = useRef(null)

  useEffect(() => {
    inputRef.current.focus()
  }, [])

  function handleClear() {
    inputRef.current.value = ''
    inputRef.current.focus()
  }

  return (
    <div className="section-card">
      <h2>🔍 Auto-Focus Search</h2>
      <p className="description">
        This input is focused automatically on load. The ref gives us direct DOM access
        without managing the value in state.
      </p>
      <input ref={inputRef} type="search" placeholder="Start typing to search…" />
      <div className="btn-group">
        <button className="btn-danger" onClick={handleClear}>✕ Clear & Focus</button>
      </div>
    </div>
  )
}

export default SearchSection

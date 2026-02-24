import { useState } from 'react'

/**
 * SearchBar — text input + button to trigger book search.
 *
 * TODO Task 6d: Complete this component.
 */
export default function SearchBar({ onSearch }) {
  const [value, setValue] = useState('')

  const handleSubmit = (e) => {
    e.preventDefault()
    onSearch(value)
  }

  const handleClear = () => {
    setValue('')
    onSearch('')
  }

  return (
    <form className="search-bar" onSubmit={handleSubmit}>
      <input
        type="text"
        placeholder="Search books by title…"
        value={value}
        onChange={e => setValue(e.target.value)}
      />
      <button type="submit">Search</button>
      {value && <button type="button" onClick={handleClear} style={{ background: '#6b7280' }}>Clear</button>}
    </form>
  )
}

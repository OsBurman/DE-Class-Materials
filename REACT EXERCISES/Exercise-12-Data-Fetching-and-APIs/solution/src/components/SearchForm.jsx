import { useState } from 'react'

function SearchForm({ onSearch }) {
  const [username, setUsername] = useState('')

  function handleSubmit(e) {
    e.preventDefault()
    const trimmed = username.trim()
    if (trimmed) {
      onSearch(trimmed)
    }
  }

  return (
    <form className="search-form" onSubmit={handleSubmit}>
      <input
        type="text"
        value={username}
        onChange={e => setUsername(e.target.value)}
        placeholder="Enter a GitHub username…"
      />
      <button type="submit">Search</button>
    </form>
  )
}

export default SearchForm

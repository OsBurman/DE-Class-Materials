import { useState } from 'react'

// TODO 7: Control the text input with `username` state and an onChange handler.
// TODO 8: On form submit, prevent the default, trim the value,
//         and call onSearch(username.trim()) if it is not empty.

function SearchForm({ onSearch }) {
  return (
    <form className="search-form">
      <input
        type="text"
        placeholder="Enter a GitHub username…"
      />
      <button type="submit">Search</button>
    </form>
  )
}

export default SearchForm

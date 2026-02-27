import { useRef, useEffect, useState } from 'react'

// TODO 1: Create a ref called `inputRef` using useRef(null).
//   This will be attached to the search input so we can control focus directly.

// TODO 2: Add a useEffect with an empty dependency array [] that calls
//   inputRef.current.focus() to auto-focus the input when the component mounts.

// TODO 3: Implement handleClear():
//   - Set inputRef.current.value = '' to clear the uncontrolled input.
//   - Call inputRef.current.focus() to return focus.
//   Note: We're intentionally using an UNCONTROLLED input here (no state) to
//   demonstrate direct DOM manipulation via refs.

function SearchSection() {
  // TODO: Add ref and useEffect here

  function handleClear() {
    // TODO: implement
  }

  return (
    <div className="section-card">
      <h2>🔍 Auto-Focus Search</h2>
      <p className="description">
        This input is focused automatically on load. The ref gives us direct DOM access
        without managing the value in state.
      </p>
      {/* TODO 4: Add ref={inputRef} to this input */}
      <input type="search" placeholder="Start typing to search…" />
      <div className="btn-group">
        <button className="btn-danger" onClick={handleClear}>✕ Clear & Focus</button>
      </div>
    </div>
  )
}

export default SearchSection

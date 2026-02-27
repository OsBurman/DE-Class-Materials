import { useState, useEffect } from 'react'
import './App.css'
import SearchForm from './components/SearchForm'
import UserProfile from './components/UserProfile'
import RepoList from './components/RepoList'
import LoadingSpinner from './components/LoadingSpinner'
import ErrorMessage from './components/ErrorMessage'

function App() {
  // TODO 1: Declare state for:
  //   - user (object | null)
  //   - repos (array)
  //   - loading (boolean, starts false)
  //   - error (string | null)
  //   - submittedUsername (string, starts '') — set when the form submits

  // TODO 2: Write a useEffect that runs whenever `submittedUsername` changes.
  //   - If `submittedUsername` is empty, clear user/repos/error and return early.
  //   - Otherwise set loading=true, error=null, then fetch.

  // TODO 3: Inside the effect, create an AbortController.
  //   Fetch `https://api.github.com/users/${submittedUsername}` passing
  //   `{ signal: controller.signal }`.
  //   If the response is not ok, throw a new Error('User not found').

  // TODO 4: In parallel, fetch
  //   `https://api.github.com/users/${submittedUsername}/repos?sort=updated&per_page=10`
  //   Hint: use Promise.all to run both fetches at the same time.

  // TODO 5: In the catch block, ignore AbortError but set `error` for all other errors.
  //   Always set loading=false in a finally block.

  // TODO 6: Return a cleanup function that calls `controller.abort()`.

  function handleSearch(username) {
    // Set submittedUsername here
  }

  return (
    <div className="app">
      <header className="app-header">
        <h1>🐙 GitHub User Explorer</h1>
        <p>Search any GitHub username to view their profile and recent repositories.</p>
        <SearchForm onSearch={handleSearch} />
      </header>

      <main className="app-main">
        {/* Render LoadingSpinner when loading */}
        {/* Render ErrorMessage when there is an error */}
        {/* Render UserProfile + RepoList when user data is available */}
      </main>
    </div>
  )
}

export default App

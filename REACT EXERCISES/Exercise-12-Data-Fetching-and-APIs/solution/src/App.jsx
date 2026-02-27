import { useState, useEffect } from 'react'
import './App.css'
import SearchForm from './components/SearchForm'
import UserProfile from './components/UserProfile'
import RepoList from './components/RepoList'
import LoadingSpinner from './components/LoadingSpinner'
import ErrorMessage from './components/ErrorMessage'

function App() {
  const [user, setUser] = useState(null)
  const [repos, setRepos] = useState([])
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState(null)
  const [submittedUsername, setSubmittedUsername] = useState('')

  useEffect(() => {
    if (!submittedUsername) {
      setUser(null)
      setRepos([])
      setError(null)
      return
    }

    const controller = new AbortController()
    const signal = controller.signal

    async function fetchData() {
      setLoading(true)
      setError(null)
      try {
        const [userRes, reposRes] = await Promise.all([
          fetch(`https://api.github.com/users/${submittedUsername}`, { signal }),
          fetch(`https://api.github.com/users/${submittedUsername}/repos?sort=updated&per_page=10`, { signal }),
        ])

        if (!userRes.ok) {
          throw new Error(userRes.status === 404 ? 'User not found.' : 'Failed to fetch user.')
        }

        const [userData, reposData] = await Promise.all([userRes.json(), reposRes.json()])
        setUser(userData)
        setRepos(Array.isArray(reposData) ? reposData : [])
      } catch (err) {
        if (err.name !== 'AbortError') {
          setError(err.message)
          setUser(null)
          setRepos([])
        }
      } finally {
        setLoading(false)
      }
    }

    fetchData()
    return () => controller.abort()
  }, [submittedUsername])

  function handleSearch(username) {
    setSubmittedUsername(username)
  }

  return (
    <div className="app">
      <header className="app-header">
        <h1>🐙 GitHub User Explorer</h1>
        <p>Search any GitHub username to view their profile and recent repositories.</p>
        <SearchForm onSearch={handleSearch} />
      </header>

      <main className="app-main">
        {loading && <LoadingSpinner />}
        {error && <ErrorMessage message={error} />}
        {!loading && !error && user && (
          <>
            <UserProfile user={user} />
            <RepoList repos={repos} />
          </>
        )}
      </main>
    </div>
  )
}

export default App

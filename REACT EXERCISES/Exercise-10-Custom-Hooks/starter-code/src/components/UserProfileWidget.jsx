import { useState } from 'react'
import { useFetch } from '../hooks/useFetch'

// TODO 1: When the user types a username and clicks "Search", build the URL:
//   `https://api.github.com/users/${username}`
//   and pass it to useFetch.
//
// TODO 2: useFetch returns { data, loading, error }.
//   - Show a loading message while loading.
//   - Show the error message if there is one.
//   - Show the user card (avatar, name, bio, public_repos) when data is available.

function UserProfileWidget() {
  const [input, setInput] = useState('')
  const [url, setUrl] = useState(null)

  // TODO 3: Call useFetch(url) — when url is null, fetch should not run
  //   (in your useFetch implementation, guard: if (!url) return early)
  const { data, loading, error } = useFetch(url)

  function handleSearch(e) {
    e.preventDefault()
    if (input.trim()) setUrl(`https://api.github.com/users/${input.trim()}`)
  }

  return (
    <div className="widget">
      <h2>👤 GitHub User Widget</h2>
      <span className="hook-name">useFetch</span>
      <form onSubmit={handleSearch} style={{ display: 'flex', gap: '0.5rem', marginBottom: '0.5rem' }}>
        <input type="text" placeholder="GitHub username…" value={input} onChange={e => setInput(e.target.value)} />
        <button type="submit" className="btn-primary">Search</button>
      </form>
      {/* TODO 4: Render loading / error / data states */}
      {loading && <p className="loading">Loading…</p>}
      {error && <p className="error">Error: {error}</p>}
      {data && (
        <div className="user-card">
          <img src={data.avatar_url} alt={data.login} />
          <div>
            <div className="name">{data.name || data.login}</div>
            <div className="bio">{data.bio || 'No bio'}</div>
            <div style={{ fontSize: '0.8rem', color: '#64748b' }}>📦 {data.public_repos} public repos</div>
          </div>
        </div>
      )}
    </div>
  )
}

export default UserProfileWidget

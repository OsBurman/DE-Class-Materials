import { useState } from 'react'
import { useFetch } from '../hooks/useFetch'

function UserProfileWidget() {
  const [input, setInput] = useState('')
  const [url, setUrl] = useState(null)
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

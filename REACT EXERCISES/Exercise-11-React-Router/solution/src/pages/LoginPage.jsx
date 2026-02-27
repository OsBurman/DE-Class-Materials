import { useState } from 'react'
import { useNavigate, Link } from 'react-router-dom'

function LoginPage({ onLogin }) {
  const [name, setName] = useState('')
  const navigate = useNavigate()

  function handleSubmit(e) {
    e.preventDefault()
    if (!name.trim()) return
    onLogin(name.trim())
    navigate('/profile')
  }

  return (
    <div className="page">
      <div className="login-form">
        <h1>🔐 Login</h1>
        <p style={{ color: '#64748b', marginBottom: '1.5rem', fontSize: '0.9rem' }}>
          Enter your name to sign in (demo login — no password required).
        </p>
        <form onSubmit={handleSubmit}>
          <div className="form-group">
            <label htmlFor="name">Your Name</label>
            <input
              id="name"
              type="text"
              value={name}
              onChange={e => setName(e.target.value)}
              placeholder="e.g. Alice"
              autoFocus
            />
          </div>
          <button type="submit" className="btn" style={{ width: '100%' }}>Sign In</button>
        </form>
        <p style={{ marginTop: '1rem', fontSize: '0.85rem', color: '#94a3b8', textAlign: 'center' }}>
          <Link to="/" style={{ color: '#6366f1' }}>← Back to Home</Link>
        </p>
      </div>
    </div>
  )
}

export default LoginPage

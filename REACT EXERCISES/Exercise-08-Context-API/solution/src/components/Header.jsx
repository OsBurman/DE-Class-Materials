import { useContext } from 'react'
import { ThemeContext } from '../contexts/ThemeContext'
import { UserContext } from '../contexts/UserContext'

const DEMO_USER = { name: 'Alice', role: 'admin', avatar: '👩‍💻' }

function Header() {
  const { theme, toggleTheme } = useContext(ThemeContext)
  const { user, setUser } = useContext(UserContext)

  return (
    <header className="header">
      <h1>📰 The Dev Blog</h1>
      <div className="header-right">
        <button className="theme-toggle" onClick={toggleTheme}>
          {theme === 'dark' ? '☀️ Light Mode' : '🌙 Dark Mode'}
        </button>
        {user ? (
          <>
            <span className="user-info">{user.avatar} {user.name}</span>
            <button className="logout-btn" onClick={() => setUser(null)}>Log out</button>
          </>
        ) : (
          <>
            <span className="user-info">Not logged in</span>
            <button className="login-btn" onClick={() => setUser(DEMO_USER)}>Log in</button>
          </>
        )}
      </div>
    </header>
  )
}

export default Header

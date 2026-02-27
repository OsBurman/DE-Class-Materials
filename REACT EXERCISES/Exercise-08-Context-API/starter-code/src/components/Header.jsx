import { useContext } from 'react'
import { ThemeContext } from '../contexts/ThemeContext'
import { UserContext } from '../contexts/UserContext'

const DEMO_USER = { name: 'Alice', role: 'admin', avatar: '👩‍💻' }

// TODO 1: Use useContext(ThemeContext) to get `theme` and `toggleTheme`.
// TODO 2: Use useContext(UserContext) to get `user` and `setUser`.

// TODO 3: Render a toggle button that calls toggleTheme and shows the current theme emoji:
//   theme === 'dark' ? '☀️ Light Mode' : '🌙 Dark Mode'

// TODO 4: Render user info:
//   - If user is logged in: show "{user.avatar} {user.name}" and a "Log out" button (setUser(null)).
//   - If not logged in: show a "Log in" button (setUser(DEMO_USER)).

function Header() {
  // TODO: implement with context

  return (
    <header className="header">
      <h1>📰 The Dev Blog</h1>
      <div className="header-right">
        {/* TODO: theme toggle button */}
        <button className="theme-toggle">🌙 Dark Mode</button>
        {/* TODO: user section */}
        <span className="user-info">Not logged in</span>
        <button className="login-btn">Log in</button>
      </div>
    </header>
  )
}

export default Header
